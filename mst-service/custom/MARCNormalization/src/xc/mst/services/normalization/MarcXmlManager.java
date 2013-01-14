/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.services.normalization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

import xc.mst.bo.record.InputRecord;
import xc.mst.constants.Constants;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.Util;

/**
 * This class is used to manage a MARC XML record. When constructed,
 * it parses out fields likely to be needed by the Normalization service,
 * and contains methods to add, remove, modify, and deduplicate a field.
 *
 * @author Eric Osisek
 */
public class MarcXmlManager {

    private static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);
    private static final Logger LOG = Logger.getLogger(MarcXmlManager.class);

    protected static Namespace marcNamespace = Namespace.getNamespace("marc", "http://www.loc.gov/MARC21/slim");

    protected Element marcXml = null;
    protected InputRecord inputRecord = null;

    protected static boolean useXpath = false;

    /**
     * A set of linking tags currently existing on the MARC XML record
     */
    protected HashSet<String> usedLinkingFields = new HashSet<String>();

    /**
     * Constructs a MarcXmlManager based on a MARC XML record.
     * This constructor will initialize all cached fields by iterating over
     * the MARC XML record's fields exactly once.
     *
     * @param marcXml
     *            The MARC XML record we're managing
     * @param organizationCode
     *            The organization code from the configuration file
     */
    @SuppressWarnings("unchecked")
    public MarcXmlManager(Element marcXml, String organizationCode) {
        TimingLogger.start("MarcXmlManager()");
        this.marcXml = marcXml;

        setOrganizationCode(organizationCode);

        // Get the MARC XML's leader
        leader = this.marcXml.getChildText("leader", marcNamespace);

        if (log.isDebugEnabled())
            log.debug("Found the value of the leader to be " + leader + ".");

        // Initialize the MARC XML control fields
        initializeMarcControlFields();

        // Initialize the MARC XML data fields
        initializeMarcDataFields();

        // Initialize the used linking fields
        try {
            // Use XPATH to get a list of all linking fields currently
            // in the MARC XML records. These are the values of the $8
            // subfield of any datafield
            List<Element> elements = null;
            TimingLogger.start("MarcXmlManager.xpath");
            TimingLogger.start("xpath");
            if (useXpath) {
                XPath xpath = XPath.newInstance(".//marc:subfield[@code='8']");
                xpath.addNamespace("marc", "http://www.loc.gov/MARC21/slim");
                elements = xpath.selectNodes(marcXml);
            } else {
                elements = new ArrayList<Element>();
                for (Object o : marcXml.getChildren("subfield", marcXml.getNamespace())) {
                    Element e = (Element) o;
                    if ("8".equals(e.getAttributeValue("code"))) {
                        elements.add(e);
                    }
                }
            }
            TimingLogger.stop("MarcXmlManager.xpath");
            TimingLogger.stop("xpath");

            // Add all existing linking fields to the set of
            // initial linking fields
            for (Element element : elements)
                if (!usedLinkingFields.contains(element.getText()))
                    usedLinkingFields.add(element.getText());
        } catch (Throwable e) {
            log.error("An error occurred while getting the current linking fields on the MARC XML record.", e);
        }
        TimingLogger.stop("MarcXmlManager()");

    } // end constructor

    /**
     * Tests whether or not the MARC XML record contains the passed linking field.
     *
     * @param linkingField
     *            The value of the linking field we're testing for duplicates.
     * @return true iff the MARC XML record contained the passed linking field.
     */
    public boolean isLinkingFieldUsed(String linkingField) {
        return usedLinkingFields.contains(linkingField);
    }

    /**
     * A set containing values for $4 subfields for which we should ignore
     * the parent datafield when performing operations only on datafields
     * which do not represent roles.
     */
    private static HashSet<String> ignore4subfields = new HashSet<String>();

    /**
     * Checks whether or not a String is a value indicating a role when
     * found in a $4 subfield. If it is, the parent datafield should be
     * ignored for certain operations.
     *
     * @param valueToTest
     *            The value of a $4 subfield
     * @return true if valueToTest appearing in a $4 subfield means we
     *         should ignore the parent datafield for operations which
     *         ignore roles.
     */
    public static boolean shouldIgnore4Subfield(String valueToTest) {
        return ignore4subfields.contains(valueToTest);
    }

    /**
     * A map whose keys are MARCXML tags and whose values are a list of 880
     * fields whose $6 equals that tag.
     */
    private HashMap<String, List<Element>> tagTo880s = new HashMap<String, List<Element>>();

    // Initialize ignore4subfields
    static {
        ignore4subfields.add("aut");
        ignore4subfields.add("lbt");
        ignore4subfields.add("lyr");
        ignore4subfields.add("cmp");
        ignore4subfields.add("com");
        ignore4subfields.add("art");
        ignore4subfields.add("ths");
        ignore4subfields.add("drt");
        ignore4subfields.add("edt");
        ignore4subfields.add("ill");
        ignore4subfields.add("prf");
        ignore4subfields.add("act");
        ignore4subfields.add("dnc");
        ignore4subfields.add("nrt");
        ignore4subfields.add("voc");
        ignore4subfields.add("itr");
        ignore4subfields.add("cnd");
        ignore4subfields.add("mod");
        ignore4subfields.add("pro");
        ignore4subfields.add("trl");
    }

    /**
     * The value of the next linking field we will use. This value starts at 1 for each record we're
     * normalizing, and increments every time a linking field is used in that record.
     *
     * We'll initialize it to 0 since we'll increment it before using it.
     */
    private int nextLinkingField = 0;

    /**
     * Calculates the next available linking field by incrementing
     * nextLinkingField until it hits a value which does not exist
     * on the current MARC XML record. Since most MARC XML records
     * will not use linking fields, this typically only increments
     * nextLinkingField.
     *
     * @return The value of the next linking field to be used with this MARC XML record.
     */
    public String getNextLinkingField() {
        // Increment nextLinkingField until it
        // hits a value which isn't used in the
        // current MARC XML record
        do {
            nextLinkingField++;
        } while (isLinkingFieldUsed(nextLinkingField + "\\c"));

        // Return the value of the next linking field
        return nextLinkingField + "\\c";
    }

    /**
     * A list of the language codes we've added using this MarcXmlManager. Because
     * the Normalization Service may have to access this list after adding them, we'll
     * save time by caching the values in this list.
     */
    private ArrayList<String> addedLanguageCodes = new ArrayList<String>();

    /**
     * Gets a list of the language codes we've added using this MarcXmlManager.
     *
     * @return A list of the language codes we've added using this MarcXmlManager.
     */
    public ArrayList<String> getAddedLanguageCodes() {
        return addedLanguageCodes;
    }

    /**
     * The organization code from the configuration file
     */
    private String organizationCode = null;
    
    private String sourceOfOrganizationCode = null;

    // The following variables are cached values for the commonly accessed MARC XML fields.
    // They are initialized in the constructor. Each variable will be named either fieldNNN
    // to represent the $a subfield of field NNN, or fieldNNNsubfieldM to represent the M
    // subfield of field NNN. There is also a leader variable, which contains the value of
    // the MARC XML leader.

    /**
     * The value of the leader field
     */
    private String leader = null;

    /**
     * Gets the leader field
     *
     * @return the MARC XML's leader
     */
    public String getLeader() {
        return leader;
    }

    public InputRecord getInputRecord() {
        return inputRecord;
    }

    public void setInputRecord(InputRecord inputRecord) {
        this.inputRecord = inputRecord;
    }

    /*
     * It is possible for the organization code to change post-initialization time.
     */
    public void setOrganizationCode(String org) {
        this.organizationCode = org;
    }
    
    public void setSourceOfOrganizationCode(String soc) {
    	this.sourceOfOrganizationCode = soc;
    }

    /**
     * The value of the 001 field
     */
    private String field001 = null;

    /**
     * Gets the 001 field
     *
     * @return the MARC XML's 001 field
     */
    public String getField001() {
        return field001;
    }
    
    public void setField001(String _001) {
    	field001 = _001;
    }

    /**
     * The value of the 003 field
     */
    private String field003 = null;

    /**
     * Gets the 003 field
     *
     * @return the MARC XML's 003 field
     */
    public String getField003() {
        return field003;
    }
    
    public void setField003(String _003) {
    	field003 = _003;
    }

    /**
     * The value of the 006 field
     */
    private ArrayList<String> field006 = new ArrayList<String>();

    /**
     * Gets the 006 field
     *
     * @return the MARC XML's 006 field
     */
    public ArrayList<String> getField006() {
        return field006;
    }

    /**
     * The value of the 007 field
     */
    private String field007 = null;

    /**
     * Gets the 007 field
     *
     * @return the MARC XML's 007 field
     */
    public String getField007() {
        return field007;
    }

    /**
     * The value of the 008 field
     */
    private String field008 = null;

    /**
     * Gets the 008 field
     *
     * @return the MARC XML's 008 field
     */
    public String getField008() {
        return field008;
    }

    /**
     * The value of the 014 $b field
     */
    private ArrayList<String> field014subfieldB = new ArrayList<String>();

    /**
     * Gets the 014 data field subfield b
     *
     * @return the MARC XML's 014 $b field
     */
    public ArrayList<String> getField014subfieldB() {
        return field014subfieldB;
    }

    /**
     * The value of the 020 field
     */
    private ArrayList<String> field020 = new ArrayList<String>();

    /**
     * Gets the 020 field
     *
     * @return the MARC XML's 020 field
     */
    public ArrayList<String> getField020() {
        return field020;
    }

    /**
     * The value of the 041 $a value
     */
    private ArrayList<String> field041subfieldA = new ArrayList<String>();

    /**
     * Gets the 041 $a value
     *
     * @return the MARC XML's 041 $a value
     */
    public ArrayList<String> getField041subfieldA() {
        return field041subfieldA;
    }

    /**
     * The value of the 041 $d value
     */
    private ArrayList<String> field041subfieldD = new ArrayList<String>();

    /**
     * Gets the 041 $d value
     *
     * @return the MARC XML's 041 $d value
     */
    public ArrayList<String> getField041subfieldD() {
        return field041subfieldD;
    }

    /**
     * The value of the 502 field
     */
    private ArrayList<String> field502 = new ArrayList<String>();

    /**
     * Gets the 502 field
     *
     * @return the MARC XML's 502 field
     */
    public ArrayList<String> getField502() {
        return field502;
    }

    /**
     * The 100 field's element
     */
    private ArrayList<Element> field100element = new ArrayList<Element>();

    /**
     * Gets the 100 field's element
     *
     * @return the MARC XML's 100 field's element
     */
    public ArrayList<Element> getField100Element() {
        return field100element;
    }

    /**
     * The 110 field's element
     */
    private ArrayList<Element> field110element = new ArrayList<Element>();

    /**
     * Gets the 110 field's element
     *
     * @return the MARC XML's 110 field's element
     */
    public ArrayList<Element> getField110Element() {
        return field110element;
    }

    /**
     * The 111 field's element
     */
    private ArrayList<Element> field111element = new ArrayList<Element>();

    /**
     * Gets the 111 field's element
     *
     * @return the MARC XML's 111 field's element
     */
    public ArrayList<Element> getField111Element() {
        return field111element;
    }

    /**
     * The value of the 100 $4 value
     */
    private String field100subfield4 = null;

    /**
     * Gets the 100 $4 value
     *
     * @return the MARC XML's 100 $4 value
     */
    public String getField100subfield4() {
        return field100subfield4;
    }

    /**
     * The value of the 110 $4 value
     */
    private String field110subfield4 = null;

    /**
     * Gets the 110 $4 value
     *
     * @return the MARC XML's 110 $4 value
     */
    public String getField110subfield4() {
        return field110subfield4;
    }

    /**
     * The value of the 111 $4 value
     */
    private String field111subfield4 = null;

    /**
     * Gets the 111 $4 value
     *
     * @return the MARC XML's 111 $4 value
     */
    public String getField111subfield4() {
        return field111subfield4;
    }

    /**
     * The value of the 130 field
     */
    private String field130 = null;

    /**
     * Gets the 130 field
     *
     * @return the MARC XML's 130 field
     */
    public String getField130() {
        return field130;
    }

    /**
     * The 240 field's element
     */
    private ArrayList<Element> field240element = new ArrayList<Element>();

    /**
     * Gets the 240 field's element
     *
     * @return the MARC XML's 240 field's element
     */
    public ArrayList<Element> getField240Element() {
        return field240element;
    }

    /**
     * The value of the 240 field
     */
    private String field240 = null;

    /**
     * Gets the 240 field
     *
     * @return the MARC XML's 240 field
     */
    public String getField240() {
        return field240;
    }

    /**
     * The value of the 243 field
     */
    private String field243 = null;

    /**
     * Gets the 243 field
     *
     * @return the MARC XML's 243 field
     */
    public String getField243() {
        return field243;
    }

    /**
     * The 243 field's element
     */
    private ArrayList<Element> field243element = new ArrayList<Element>();

    /**
     * Gets the 243 field's element
     *
     * @return the MARC XML's 243 field's element
     */
    public ArrayList<Element> getField243Element() {
        return field243element;
    }

    /**
     * The value of the 245 field
     */
    private String field245 = null;

    /**
     * Gets the 245 field
     *
     * @return the MARC XML's 245 field
     */
    public String getField245() {
        return field245;
    }

    /**
     * The value of the 999 field
     */
    private String field999 = null;

    /**
     * Gets the 999 field
     *
     * @return the MARC XML's 999 field
     */
    public String getField999() {
        return field999;
    }

    /**
     * The 440 field's element
     */
    private ArrayList<Element> field440elements = new ArrayList<Element>();

    /**
     * Gets the 440 field's element
     *
     * @return the MARC XML's 440 field's element
     */
    public ArrayList<Element> getField440Elements() {
        return field440elements;
    }

    /**
     * The 600 field's element
     */
    private ArrayList<Element> field600elements = new ArrayList<Element>();

    /**
     * Gets the 600 field's element
     *
     * @return the MARC XML's 600 field's element
     */
    public ArrayList<Element> getField600Elements() {
        return field600elements;
    }

    /**
     * The 610 field's element
     */
    private ArrayList<Element> field610elements = new ArrayList<Element>();

    /**
     * Gets the 610 field's element
     *
     * @return the MARC XML's 610 field's element
     */
    public ArrayList<Element> getField610Elements() {
        return field610elements;
    }

    /**
     * The 611 field's element
     */
    private ArrayList<Element> field611elements = new ArrayList<Element>();

    /**
     * Gets the 611 field's element
     *
     * @return the MARC XML's 611 field's element
     */
    public ArrayList<Element> getField611Elements() {
        return field611elements;
    }

    /**
     * The 630 field's element
     */
    private ArrayList<Element> field630elements = new ArrayList<Element>();

    /**
     * Gets the 630 field's element
     *
     * @return the MARC XML's 630 field's element
     */
    public ArrayList<Element> getField630Elements() {
        return field630elements;
    }

    /**
     * The 650 field's element
     */
    private ArrayList<Element> field650elements = new ArrayList<Element>();

    /**
     * Gets the 650 field's element
     *
     * @return the MARC XML's 650 field's element
     */
    public ArrayList<Element> getField650Elements() {
        return field650elements;
    }

    /**
     * The 655 field's element
     */
    private ArrayList<Element> field655elements = new ArrayList<Element>();

    /**
     * Gets the 655 field's element
     *
     * @return the MARC XML's 655 field's element
     */
    public ArrayList<Element> getField655Elements() {
        return field655elements;
    }

    /**
     * The 700 field's element
     */
    private ArrayList<Element> field700elements = new ArrayList<Element>();

    /**
     * Gets the 700 field's element
     *
     * @return the MARC XML's 700 field's element
     */
    public ArrayList<Element> getField700Elements() {
        return field700elements;
    }

    /**
     * The 710 field's element
     */
    private ArrayList<Element> field710elements = new ArrayList<Element>();

    /**
     * Gets the 710 field's element
     *
     * @return the MARC XML's 710 field's element
     */
    public ArrayList<Element> getField710Elements() {
        return field710elements;
    }

    /**
     * The 711 field's element
     */
    private ArrayList<Element> field711elements = new ArrayList<Element>();

    /**
     * Gets the 711 field's element
     *
     * @return the MARC XML's 711 field's element
     */
    public ArrayList<Element> getField711Elements() {
        return field711elements;
    }

    /**
     * The 730 field's element
     */
    private ArrayList<Element> field730elements = new ArrayList<Element>();

    /**
     * Gets the 730 field's element
     *
     * @return the MARC XML's 730 field's element
     */
    public ArrayList<Element> getField730Elements() {
        return field730elements;
    }

    /**
     * The 800 field's element
     */
    private ArrayList<Element> field800elements = new ArrayList<Element>();

    /**
     * Gets the 800 field's element
     *
     * @return the MARC XML's 800 field's element
     */
    public ArrayList<Element> getField800Elements() {
        return field800elements;
    }

    /**
     * The 810 field's element
     */
    private ArrayList<Element> field810elements = new ArrayList<Element>();

    /**
     * Gets the 810 field's element
     *
     * @return the MARC XML's 810 field's element
     */
    public ArrayList<Element> getField810Elements() {
        return field810elements;
    }

    /**
     * The 811 field's element
     */
    private ArrayList<Element> field811elements = new ArrayList<Element>();

    /**
     * Gets the 811 field's element
     *
     * @return the MARC XML's 811 field's element
     */
    public ArrayList<Element> getField811Elements() {
        return field811elements;
    }

    /**
     * The value of the 852 $b value
     */
    private ArrayList<String> field852subfieldBs = new ArrayList<String>();

    /**
     * Gets the 852 $b value
     *
     * @return the MARC XML's 852 $b value
     */
    public ArrayList<String> getField852subfieldBs() {
        return field852subfieldBs;
    }

    /**
     * The value of the 945 $l value
     */
    private ArrayList<String> field945subfieldLs = new ArrayList<String>();

    /**
     * Gets the 945 $l value
     *
     * @return the MARC XML's 945 $l value
     */
    public ArrayList<String> getField945subfieldLs() {
        return field945subfieldLs;
    }

    /**
     * A list of the original 035 fields for the MARC XML
     */
    private ArrayList<Element> original035fields = new ArrayList<Element>();

    /**
     * Gets a list of the original 035 fields for the MARC XML. Any changes
     * made to the elements in this list will be reflected in the corrosponding
     * 035 fields on the MARC XML records.
     *
     * @return A list of the original 035 fields for the MARC XML
     */
    public ArrayList<Element> getOriginal035Fields() {
        return original035fields;
    }

    /**
     * Gets the MARC XML resulting from the modifications which have been made through this MarcXmlManager
     *
     * @return The modified MARC XML record.
     */
    public Element getModifiedMarcXml() {
        return marcXml;
    } // end method getModifiedMarcXml

    /**
     * Returns true if the passed tag contains the passed subfield
     *
     * @param tag
     *            The tag we're checking
     * @param subfield
     *            The subfield we're checking for
     * @return true if the passed tag contains the passed subfield
     */
    public boolean doesSubfieldExist(String tag, String subfield) {
        TimingLogger.start("doesSubFieldExist:709");
        if (log.isDebugEnabled())
            log.debug("Checking if tag " + tag + " contains subfield $" + subfield + ".");

        // Check if the subfield exists for the passed tag
        try {
            boolean ret = false;
            if (useXpath) {
                TimingLogger.start("doesSubFieldExist.xpath");
                TimingLogger.start("xpath");
                // Use XPATH to get a list of all subfields of the passed tag with the passed subfield value.
                // Return true iff this list is not empty
                XPath xpath = XPath.newInstance(".//marc:datafield[@tag='" + tag + "']/marc:subfield[@code='" + subfield + "']");
                xpath.addNamespace("marc", "http://www.loc.gov/MARC21/slim");
                List nodes = xpath.selectNodes(marcXml);
                ret = !nodes.isEmpty();
                TimingLogger.stop("doesSubFieldExist.xpath");
                TimingLogger.stop("xpath");
            } else {
                TimingLogger.start("doesSubFieldExist.noxpath");
                TimingLogger.start("noxpath");
                for (Object o : marcXml.getChildren("datafield", marcXml.getNamespace())) {
                    Element e = (Element) o;
                    if (tag.equals(e.getAttributeValue("tag"))) {
                        for (Object o2 : e.getChildren("subfield", marcXml.getNamespace())) {
                            Element e2 = (Element) o2;
                            if (subfield.equals(e2.getAttributeValue("code"))) {
                                ret = true;
                                break;
                            }
                        }
                        if (ret) {
                            break;
                        }
                    }
                }
                TimingLogger.stop("doesSubFieldExist.noxpath");
                TimingLogger.stop("noxpath");
            }
            TimingLogger.stop("doesSubFieldExist:709");
            return ret;
        } catch (Throwable e) {
            log.error("An error occurred while checking if tag " + tag + " contains subfield $" + subfield + ".", e);
            TimingLogger.stop("doesSubFieldExist:709");
            return false;
        }
    }

    /**
     * Initializes the MARC XML control fields' cached values.
     */
    @SuppressWarnings("unchecked")
    private void initializeMarcControlFields() {
        TimingLogger.start("initializeMarcControlFields");
        if (log.isDebugEnabled())
            log.debug("Initializing MARC XML control fields.");
        
        // Get the control fields
        List<Element> controlFields = marcXml.getChildren("controlfield", marcNamespace);
        // Iterate over the fields and find the 001, 003, 007, and 008 control fields.
        // Initialize their cached values as we find them.
        for (Element controlField : controlFields) {
            TimingLogger.start("initializeMarcControlFields.for");
            // Initialize the 001 field if we found it
            if (controlField.getAttribute("tag").getValue().equals("001")) {
                field001 = controlField.getText();

                if (log.isDebugEnabled())
                    log.debug("Found the value of the control field 001 to be " + field001 + ".");
            } // end if (001 found)

            // Initialize the 003 field if we found it
            else if (controlField.getAttribute("tag").getValue().equals("003")) {
                field003 = controlField.getText();

                if (log.isDebugEnabled())
                    log.debug("Found the value of the control field 003 to be " + field003 + ".");
            } // end if (003 found)

            // Initialize the 006 field if we found it
            else if (controlField.getAttribute("tag").getValue().equals("006")) {
                field006.add(controlField.getText());

                if (log.isDebugEnabled())
                    log.debug("Found the value of the control field 006 to be " + field006 + ".");
            } // end if (006 found)

            // Initialize the 007 field if we found it
            else if (controlField.getAttribute("tag").getValue().equals("007")) {
                field007 = controlField.getText();

                if (log.isDebugEnabled())
                    log.debug("Found the value of the control field 007 to be " + field007 + ".");
            } // end if (007 found)

            // Initialize the 008 field if we found it
            else if (controlField.getAttribute("tag").getValue().equals("008")) {
                field008 = controlField.getText();

                if (log.isDebugEnabled())
                    log.debug("Found the value of the control field 008 to be " + field008 + ".");
            } // end if (008 found)
            TimingLogger.stop("initializeMarcControlFields.for");
        } // end loop over control fields
        TimingLogger.stop("initializeMarcControlFields");
    } // end method initializeMarcControlFields

            
    /**
     * Initializes the MARC XML data fields' cached values.
     */
    @SuppressWarnings("unchecked")
    private void initializeMarcDataFields() {
        TimingLogger.start("initializeMarcDataFields");
        if (log.isDebugEnabled())
            log.debug("Initializing MARC XML data fields.");        
        
        // Get the data fields
        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);

        // Iterate over the fields and find the one with the correct tag
        for (Element field : fields) {
            TimingLogger.start("initializeMarcDataFields.for");
            String tag = field.getAttributeValue("tag");

            // If the tag is "880", treat it like the tag from its $6 subfield
            if (tag.equals("880")) {
                List<String> realTags = getSubfieldOfField(field, '6');

                if (realTags.size() > 0)
                    tag = realTags.get(0).substring(0, 3);

                for (String realTag : realTags) {
                    String tagValue = realTag.substring(0, 3);

                    if (!tagTo880s.containsKey(tagValue))
                        tagTo880s.put(tagValue, new ArrayList<Element>());

                    tagTo880s.get(tagValue).add(field);
                }
            }

            // If the current field is 020, get its subfields
            if (tag.equals("020")) {
                // Get the $a subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'a');

                // Add the 020 $a subfields
                field020.addAll(subfields);
            } // end if (020 found)

            else if (tag.equals("014")) {
                // Get the $b subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'b');
                field014subfieldB.addAll(subfields);
            }

            // If the current field is 035, get its subfields
            else if (tag.equals("035"))
                original035fields.add(field);

            // If the current field is 041, get its subfields
            else if (tag.equals("041")) {
                // Get the $a subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'a');

                // Add the 041 $d values
                field041subfieldA.addAll(subfields);

                // Get the $d subfields
                subfields = getSubfieldValuesOfField(field, 'd');

                // Add the 041 $d subfields
                field041subfieldD.addAll(subfields);
            } // end if (041 found)

            // If the current field is 100, get its subfields
            else if (tag.equals("100")) {
                // Get the $4 subfields
                List<String> subfields = getSubfieldValuesOfField(field, '4');

                // Set the 100 $4 value
                field100subfield4 = (subfields.size() > 0 ? subfields.get(0) : null);

                // Set the 100 field element
                field100element.add(field);
            } // end if (100 found)

            // If the current field is 110, get its subfields
            else if (tag.equals("110")) {
                // Get the $4 subfields
                List<String> subfields = getSubfieldValuesOfField(field, '4');

                // Set the 110 $4 value
                field110subfield4 = (subfields.size() > 0 ? subfields.get(0) : null);

                // Set the 110 field element
                field110element.add(field);
            } // end if (110 found)

            // If the current field is 111, get its subfields
            else if (tag.equals("111")) {
                // Get the $4 subfields
                List<String> subfields = getSubfieldValuesOfField(field, '4');

                // Set the 111 $4 value
                field111subfield4 = (subfields.size() > 0 ? subfields.get(0) : null);

                // Set the 111 field element
                field111element.add(field);
            } // end if (111 found)

            // If the current field is 130, get its subfields
            else if (tag.equals("130")) {
                // Get the $a subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'a');

                // Set the 130 $a subfield
                field130 = (subfields.size() > 0 ? subfields.get(0) : null);
            } // end if (130 found)

            // If the current field is 240, get its subfields
            else if (tag.equals("240")) {
                // Get the $a subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'a');

                // Set the 240 $a subfield
                field240 = (subfields.size() > 0 ? subfields.get(0) : null);
            } // end if (240 found)

            // If the current field is 243, get its subfields
            else if (tag.equals("243")) {
                // Get the $a subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'a');

                // Set the 243 $a subfield
                field243 = (subfields.size() > 0 ? subfields.get(0) : null);
            } // end if (243 found)

            // If the current field is 245, get its subfields
            else if (tag.equals("245")) {
                // Get the $a subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'a');

                // Set the 245 $a subfield
                field245 = (subfields.size() > 0 ? subfields.get(0) : null);
            } // end if (245 found)

            // If the current field is 999, get its subfields
            else if (tag.equals("999")) {
                // Get the $a subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'a');

                // Set the 999 $a subfield
                field999 = (subfields.size() > 0 ? subfields.get(0) : null);
            } // end if (999 found)

            // If the current field is 502, get its subfields
            else if (tag.equals("502")) {
                // Get the $a subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'a');

                // Add the 502 $a subfields
                field502.addAll(subfields);
            } // end if (502 found)

            // If the current field is 440, get its subfields
            else if (tag.equals("440"))
                field440elements.add(field);

            // If the current field is 600, get its subfields
            else if (tag.equals("600"))
                field600elements.add(field);

            // If the current field is 610, get its subfields
            else if (tag.equals("610"))
                field610elements.add(field);

            // If the current field is 611, get its subfields
            else if (tag.equals("611"))
                field611elements.add(field);

            // If the current field is 630, get its subfields
            else if (tag.equals("630"))
                field630elements.add(field);

            // If the current field is 650, get its subfields
            else if (tag.equals("650"))
                field650elements.add(field);

            // If the current field is 655, get its subfields
            else if (tag.equals("655"))
                field655elements.add(field);

            // If the current field is 700, get its subfields
            else if (tag.equals("700"))
                field700elements.add(field);

            // If the current field is 710, get its subfields
            else if (tag.equals("710"))
                field710elements.add(field);

            // If the current field is 711, get its subfields
            else if (tag.equals("711"))
                field711elements.add(field);

            // If the current field is 730, get its subfields
            else if (tag.equals("730"))
                field730elements.add(field);

            // If the current field is 800, get its subfields
            else if (tag.equals("800"))
                field800elements.add(field);

            // If the current field is 810, get its subfields
            else if (tag.equals("810"))
                field810elements.add(field);

            // If the current field is 811, get its subfields
            else if (tag.equals("811"))
                field811elements.add(field);

            // If the current field is 852, get its subfields
            else if (tag.equals("852")) {
                // Get the $b subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'b');

                // Add the 852 $b subfields
                field852subfieldBs.addAll(subfields);
            } // end if (852 found)

            // If the current field is 945, get its subfields
            else if (tag.equals("945")) {
                // Get the $b subfields
                List<String> subfields = getSubfieldValuesOfField(field, 'l');

                // Add the 945 $l subfields
                field945subfieldLs.addAll(subfields);
            } // end if (945 found)
            TimingLogger.stop("initializeMarcDataFields.for");
        } // end loop over data fields
        TimingLogger.stop("initializeMarcDataFields");
    } // end method initializeMarcDataFields
    
    
    /**
     * Sets the control 005 to current timestamp
     */
    @SuppressWarnings("unchecked")
    public void set005() {
        TimingLogger.start("set005");
        if (log.isDebugEnabled())
            log.debug("Setting 005 to current timestamp.");

        boolean found = false;
        Util util = Util.getUtil();
        String new005 = util.printDateTimeISO8601(new Date());
        List<Element> controlFields = marcXml.getChildren("controlfield", marcNamespace);
        for (Element controlField : controlFields) {
            if (controlField.getAttribute("tag").getValue().equals("005")) {
                if (log.isDebugEnabled())
                    log.debug("Re-setting control field 005 to current timestamp.");
                controlField.setText(new005);
                found = true;
                break;
            }
        }
        
        if (! found) {
           if (log.isDebugEnabled())
                log.debug("Setting new 005 control field to current timestamp.");
           addMarcXmlControlField("005", new005);
        }
    }

    /**
     * Remove unwanted subfields
     *    
     *    @param fieldTag
     *    			the field we are editing 
     *    @param keepSubfields
     *    			keep subfield if subfield is included in keepSubfields string 
     *    @param removeSubfields
     *    			remove subfield if subfield is included in keepSubfields string
     */
    @SuppressWarnings("unchecked")
	public void stripSubfields(String fieldTag, String keepSubfields, String removeSubfields) {
        if (log.isDebugEnabled())
            log.debug("In stripSubfields...");

        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);
    	ArrayList<Element> badFlds = new ArrayList<Element>();

        for (Element field : fields) {
            String tag = field.getAttributeValue("tag");
            //String ind1 = field.getAttributeValue("ind1");

            if (tag.equals(fieldTag)) {
            	ArrayList<Element> badEls = new ArrayList<Element>();
               	int totCnt = 0; int badCnt = 0;
            	for (Object o : field.getChildren("subfield", field.getNamespace())) {
                    Element e = (Element) o;
                    totCnt++;
                    CharSequence fc = e.getAttributeValue("code").subSequence(0, 1);
                    if ((keepSubfields != null && !keepSubfields.contains(fc))
                    		|| (removeSubfields != null && removeSubfields.contains(fc))) {
                        		badEls.add(e);
                        		badCnt++;
                    }
                }
                for (Element badEl : badEls) {
                	field.removeContent(badEl);
                }   
                if (totCnt == badCnt) {
                	badFlds.add(field);
                }
            }
        }
        for (Element badFld : badFlds) {
        	marcXml.removeContent(badFld);
        }   

        
    }
    
    
    /**
     * Remove invalid 014$a 
     *    if indicator1 is "1" and
     *    first character is either NOT among validFirstChars or first character IS among invalidFirstChars
     *    
     *    @param validFirstChars
     *    			remove subfield if first character is NOT contained in this list of characters 
     *    @param invalidFirstChars
     *    			remove subfield if first character IS contained in this list of characters
     */
    @SuppressWarnings("unchecked")
	public void removeInvalid014s(String validFirstChars, String invalidFirstChars) {
        if (log.isDebugEnabled())
            log.debug("In removeInvalid014s...");
        
        boolean allInvalid = false;
        boolean allValid = false;
        
        // special settings: [all] [none]
        if (validFirstChars.equals("[all]")) allValid = true;
        if (validFirstChars.equals("[none]")) allInvalid = true;
        if (invalidFirstChars.equals("[none]")) allValid = true;
        if (invalidFirstChars.equals("[all]")) allInvalid = true;
        
        if (allValid && allInvalid) {
        	log.error("removeInvalid014s: Configuration error (all 014s considered valid and invalid at the same time?!)");
        	return;
        }
        
        if (allValid) return; // nothing to do

        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);
    	ArrayList<Element> badFlds = new ArrayList<Element>();

        for (Element field : fields) {
            String tag = field.getAttributeValue("tag");
            String ind1 = field.getAttributeValue("ind1");

            if (tag.equals("014") && ind1.equals("1")) {
            	ArrayList<Element> badEls = new ArrayList<Element>();
               	int totCnt = 0; int badCnt = 0;
            	for (Object o : field.getChildren("subfield", field.getNamespace())) {
                    Element e = (Element) o;
                    totCnt++;
                    if (("a").equals(e.getAttributeValue("code"))) {
                    	if (e.getText().length() > 0) {
                    		CharSequence fc = e.getText().subSequence(0, 1);
                        	if (allInvalid || (validFirstChars.length() > 0 && !validFirstChars.contains(fc)) || 
                        		 (invalidFirstChars.length() > 0 && invalidFirstChars.contains(fc)) ) {
                        		badEls.add(e);
                        		badCnt++;
                        	}
                    	}
                    }
                }
                for (Element badEl : badEls) {
                	field.removeContent(badEl);
                }   
                if (totCnt == badCnt) {
                	badFlds.add(field);
                }
            }
        }
        for (Element badFld : badFlds) {
        	marcXml.removeContent(badFld);
        }   

        
    }
    
    /**
     * Removes the first forward slash and any characters afterwards from the 010
     */
    @SuppressWarnings("unchecked")
	public void lccnCleanup() {
        if (log.isDebugEnabled())
            log.debug("In lccnCleanup...");
        
        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);
        //boolean dirty010 = false;
        for (Element field : fields) {
            String tag = field.getAttributeValue("tag");

            if (tag.equals("010")) {
            	for (Object o : field.getChildren("subfield", field.getNamespace())) {
                    Element e = (Element) o;
                    
                    String lccn = e.getText();
                    int inx = lccn.indexOf('/');
                    if (inx > 0) {
	                    //dirty010 = true;
	                    e.setText(lccn.substring(0, inx));
                    }
            	}
            }
        }
        
        //if (dirty010) {
        	// re-initialize field data if necessary 
        //}
    }
    
    
    /**
     * Moves any ISBN-13s that were input into 024 fields (OCLC interim practice in ca. 2006) to 020 
     *    so that they can be used in Aggregation Service matching 
     *    if indicator1 is "3"
     *    and 1st 3 digits= 978 OR 1st 4 digits= 9791, 9792, 9793, 9794, 9495, 9796, 9797, 9798, or 9799 (but not 9790)
     *    and Number itself=13 digits
     */
    @SuppressWarnings("unchecked")
	public void isbnMove024() {
        if (log.isDebugEnabled())
            log.debug("In isbnMove024...");

    	ArrayList<Element> badFlds = new ArrayList<Element>();
    	ArrayList<Element> new020s = new ArrayList<Element>();
    	HashSet<String> current020s = new HashSet<String>();
    	Element the020 = null;

        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);

        boolean deduped020 = false;
        
    	// First pass: remove valid ISBN-13s from 024 $a (we'll add them to 020 later)
    	// and keep track of current 020 $a (and dedup)
        for (Element field : fields) {
            String tag = field.getAttributeValue("tag");
            String ind1 = field.getAttributeValue("ind1");

            if (tag.equals("024") && ind1.equals("3")) {
            	ArrayList<Element> badEls = new ArrayList<Element>(); // mark for deletion
               	int totCnt = 0; int badCnt = 0;
            	for (Object o : field.getChildren("subfield", field.getNamespace())) {
                    Element e = (Element) o;
                    totCnt++;
                    if (("a").equals(e.getAttributeValue("code"))) {
                    	String isbn = e.getText();
                    	if (isbn.length() == 13 && isbn.startsWith("979")) {
                    		switch(isbn.charAt(3)) {
	                    		case '1':
	                    		case '2':
	                    		case '3':
	                    		case '4':
	                    		case '5':
	                    		case '6':
	                    		case '7':
	                    		case '8':
	                    		case '9':
	                    			badEls.add(e); // mark subfield for deletion
	                    			new020s.add(e); // keep track of "found" new 020
	                        		badCnt++;
                    		}
                    	}
                    }
               
                }
            	// delete marked subfields
                for (Element badEl : badEls) {
                	field.removeContent(badEl);
                }   
                // if field is completely empty, mark for deletion
                if (totCnt == badCnt) {
                	badFlds.add(field);
                }
            }
            
            if (tag.equals("020")) {
            	the020 = field; // save the 020 tag so we can add new Els later
            	ArrayList<Element> badEls = new ArrayList<Element>();
            	for (Object o : field.getChildren("subfield", field.getNamespace())) {
                    Element e = (Element) o;
                    if (("a").equals(e.getAttributeValue("code"))) {
                    	String isbn = e.getText();
                    	// dedup while we're at it
                    	if (current020s.contains(isbn)) {
                    		badEls.add(e); // mark subfield for deletion
                    		deduped020 = true;
                    	} else {
                    		current020s.add(isbn);
                    	}
                    }
            	}
            	// delete marked subfields
                for (Element badEl : badEls) {
                	field.removeContent(badEl);
                }   

            }
            
        } // end of First Pass

        // remove any marked fields (since they are empty)
        for (Element badFld : badFlds) {
        	marcXml.removeContent(badFld);
        }   

        // in case we don't have a 020 already
        boolean wasEmpty = false;
        if (the020 == null) {
        	the020 = new Element("datafield", marcNamespace);
            the020.setAttribute("tag", "020");
            the020.setAttribute("ind1", " ");
            the020.setAttribute("ind2", " ");
            wasEmpty = true;
        }
        
        // Only add new 020 values (no duplicates)
        int cnt = 0;
        for (Element n : new020s) {
        	if (! current020s.contains(n.getText())) {
        		the020.addContent(n);
        		current020s.add(n.getText());
        		cnt++;
        	}
        }
        
        if (wasEmpty && cnt > 0)
            marcXml.addContent("\n\t").addContent(the020).addContent("\n");

        // Since we've modified the 020, we need to re-initialize the field data
        if (deduped020 || wasEmpty || cnt > 0) {
	        field020 = new ArrayList<String>();
	        field020.addAll(current020s);
        }

    }

    
    /**
     * Turn multiple 004 fields into a single 004 and one or more valid 014$a fields
     * @param fixMultiple004s
     *             on - create 014s for additional 004s
     *             off - do nothing
     *             protect - generate error if multiple 004s found
     * @return false if encountering multiple 004s while "protect" is set             
     */
    @SuppressWarnings("unchecked")
	public boolean fixMultiple004s(String fixMultiple004s) {
        if (log.isDebugEnabled())
            log.debug("In fixMultiple004s...");
        
        if (fixMultiple004s.equals("off")) return true;
        
        List<Element> fields = marcXml.getChildren("controlfield", marcNamespace);
    	ArrayList<Element> new014s = new ArrayList<Element>();
    	ArrayList<Element> old004s = new ArrayList<Element>();
    	
    	int cnt004 = 0;
        for (Element field : fields) {
            String tag = field.getAttributeValue("tag");
            
            if (tag.equals("004")) {
            	if (++cnt004 > 1) {            		
            		new014s.add(field);
            		old004s.add(field);
            	}
            }
        }
        
    	if (cnt004 > 1 && fixMultiple004s.equals("protect")) return false;
        
        for (Element old004 : old004s) {
        	marcXml.removeContent(old004);
        } 
    	
    	fields = marcXml.getChildren("datafield", marcNamespace);
    	
    	ArrayList<Element> removeFlds = new ArrayList<Element>();

        for (Element field : fields) {
            String tag = field.getAttributeValue("tag");
            String ind1 = field.getAttributeValue("ind1");

            if (tag.equals("014") && ind1.equals("1")) {
            	ArrayList<Element> removeEls = new ArrayList<Element>();
               	int totCnt = 0; int removeCnt = 0;
            	for (Object o : field.getChildren("subfield", field.getNamespace())) {
                    Element e = (Element) o;
                    totCnt++;
                    if (("a").equals(e.getAttributeValue("code"))) {
                		removeEls.add(e);
                		removeCnt++;
                    }
                }
                for (Element removeEl : removeEls) {
                	field.removeContent(removeEl);
                }   
                if (totCnt == removeCnt) {
                	removeFlds.add(field);
                }
            }
        }
        for (Element removeFld : removeFlds) {
        	marcXml.removeContent(removeFld);
        }   
        
        for (Element new014 : new014s) {
        	addMarcXmlField("014", new014.getText(), null, "1", null, null);
        }

        return true;
        
    }    
        
    /**
     * Adds a new datafield to the MARC XML record and returns the result. The tag will have
     * both of its indicators empty and the $a subfield will be set to the specified value.
     *
     * @param tag
     *            The tag we're adding (i.e. 931)
     * @param subfieldAValue
     *            The value of the $a subfield of the tag we're adding
     */
    public void addMarcXmlField(String tag, String subfieldAValue) {
        addMarcXmlField(tag, subfieldAValue, null, null, null, null);
    }
    
    /**
     * Helper function to create 9XX organization code subfield - this can be based on
     * either the organization code of the record
     * or the organization code of the XC organization code
     */
    public Element createOrgCodeSubfield() {
	    // Add the $5 subfield to the new MARC XML field with the value of the user's organization code
	    Element newField5Subfield = new Element("subfield", marcNamespace);
	    if (sourceOfOrganizationCode != null) {
		    newField5Subfield.setAttribute("code", "1");
		    newField5Subfield.setText(sourceOfOrganizationCode);	    	
	    } else {
		    newField5Subfield.setAttribute("code", "5");
		    newField5Subfield.setText(organizationCode);
	    }
	    return newField5Subfield;
    }
        
    /**
     * Adds a new datafield to the MARC XML record and returns the result. The tag will have
     * both of its indicators empty and the $a subfield will be set to the specified value.
     *
     * @param tag
     *            The tag we're adding (i.e. 931)
     * @param subfieldAValue
     *            The value of the $a subfield of the tag we're adding
     * @param linkingField
     *            The value of the $8 subfield of the field we're adding, or null if we do not need a $8 subfield
     * @param ind1Value
     * 	          Allow user to set ind1
     * @param ind2Value
     * 	          Allow user to set ind2
     * @param subfieldCode
     * 	          Allow user to set subfield other than 'a'
     */
    public void addMarcXmlField(String tag, String subfieldAValue, String linkingField, String ind1Value, String ind2Value, String subfieldCode) {
        TimingLogger.start("addMarcXmlField");
        if (log.isDebugEnabled())
            log.debug("Adding a new datafield to the MARC XML record with tag " + tag + " and value " + subfieldAValue + (linkingField == null ? "." : " with linking field " + linkingField + "."));

        // Add a MARC XML field with the specified tag
        // Both of its indicators will be empty
        Element newFieldElement = new Element("datafield", marcNamespace);
        newFieldElement.setAttribute("tag", tag);
        if (ind1Value != null) {
        	newFieldElement.setAttribute("ind1", ind1Value);
        } else {
        	newFieldElement.setAttribute("ind1", " ");
        }
        if (ind2Value != null) {
            newFieldElement.setAttribute("ind2", ind2Value);
        } else {
            newFieldElement.setAttribute("ind2", " ");        	
        }

        // Add the $a subfield to the MARC XML field to the passed value
        Element newFieldASubfield = new Element("subfield", marcNamespace);
        if (subfieldCode != null)
        	newFieldASubfield.setAttribute("code", subfieldCode);
        else
        	newFieldASubfield.setAttribute("code", "a");
        newFieldASubfield.setText(subfieldAValue);

        // Add the $a subfield to the new datafield
        newFieldElement.addContent("\n\t").addContent(newFieldASubfield).addContent("\n");

        // Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
        if (tag.startsWith("9")) {
            // Add the $5 subfield to the new MARC XML field with the value of the user's organization code
            Element newField5Subfield = createOrgCodeSubfield();

            // Add the $5 subfield to the new datafield
            newFieldElement.addContent("\t").addContent(newField5Subfield).addContent("\n");
        }

        // Add a $8 subfield with the specified linkingField if it is not null
        if (linkingField != null) {
            // Add the $8 subfield to the new MARC XML field with the specified linking field
            Element newField8Subfield = new Element("subfield", marcNamespace);
            newField8Subfield.setAttribute("code", "8");
            newField8Subfield.setText(linkingField);

            // Add the $8 subfield to the new datafield
            newFieldElement.addContent("\t").addContent(newField8Subfield).addContent("\n");

            if (!usedLinkingFields.contains(linkingField))
                usedLinkingFields.add(linkingField);
        }

        // Add the new field to the end of the MARC XML if we didn't insert it already
        marcXml.addContent(newFieldElement).addContent("\n\n");

        // If we just added a language code, add it to the list of new language codes we're maintaining
        if (tag.equals(NormalizationServiceConstants.FIELD_9XX_LANGUAGE_SPLIT)) {
            if (log.isDebugEnabled())
                log.debug("Added the language code " + subfieldAValue + ".");

            addedLanguageCodes.add(subfieldAValue.toLowerCase());
        } // end if (added language code)

        // If we just added a 243 element, cache the new element
        else if (tag.equals("243"))
            field243element.add(newFieldElement);
        
        TimingLogger.stop("addMarcXmlField");
    } // end method addMarcXmlField

    /**
     * Adds a new datafield to the MARC XML record and returns the result. The tag will have
     * both of its indicators empty and the $a subfield will be set to the specified value.
     *
     * @param tag
     *            The tag we're adding (i.e. 931)
     * @param ind1Value
     * 	          Allow user to set ind1
     * @param ind2Value
     * 	          Allow user to set ind2
     * @param subfieldValues
     * 	          Allow user to set subfields using a TreeMap
     */
    public void addMarcXmlField(String tag,  String ind1Value, String ind2Value, TreeMap<String,String> subfieldValues) {
        TimingLogger.start("addMarcXmlField");
        if (log.isDebugEnabled())
            log.debug("Adding a new datafield to the MARC XML record with tag " + tag + " with multiple subfield values.");

        // Add a MARC XML field with the specified tag
        // Both of its indicators will be empty
        Element newFieldElement = new Element("datafield", marcNamespace);
        newFieldElement.setAttribute("tag", tag);
        if (ind1Value != null) {
        	newFieldElement.setAttribute("ind1", ind1Value);
        } else {
        	newFieldElement.setAttribute("ind1", " ");
        }
        if (ind2Value != null) {
            newFieldElement.setAttribute("ind2", ind2Value);
        } else {
            newFieldElement.setAttribute("ind2", " ");        	
        }

        // Add the $a subfield to the MARC XML field to the passed value
        for(Entry<String, String> entry : subfieldValues.entrySet()) {
        	String subfield = entry.getKey();
        	String subfieldValue = entry.getValue();
        	        	
            Element newFieldSubfield = new Element("subfield", marcNamespace);
            newFieldSubfield.setAttribute("code", subfield);
            newFieldSubfield.setText(subfieldValue);

            // Add the $a subfield to the new datafield
            newFieldElement.addContent("\n\t").addContent(newFieldSubfield).addContent("\n");
        	
            // If we just added a language code, add it to the list of new language codes we're maintaining
            if (tag.equals(NormalizationServiceConstants.FIELD_9XX_LANGUAGE_SPLIT)) {
                if (log.isDebugEnabled())
                    log.debug("Added the language code " + subfieldValue + ".");

                addedLanguageCodes.add(subfieldValue.toLowerCase());
            } // end if (added language code)
        }
        
        // Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
        if (tag.startsWith("9")) {
            // Add the $5 subfield to the new MARC XML field with the value of the user's organization code
            Element newField5Subfield = createOrgCodeSubfield();

            // Add the $5 subfield to the new datafield
            newFieldElement.addContent("\t").addContent(newField5Subfield).addContent("\n");
        }

        // If we just added a 243 element, cache the new element
        else if (tag.equals("243"))
            field243element.add(newFieldElement);
        
        // Add the new field to the end of the MARC XML if we didn't insert it already
        marcXml.addContent(newFieldElement).addContent("\n\n");
        
        TimingLogger.stop("addMarcXmlField");
    } // end method addMarcXmlField
    
    
    /**
     * Sets a controlfield to the MARC XML record and returns the result.
     *
     * @param tag
     *            The tag we're setting (i.e. 931)
     * @param value
     *            The value of the tag we're setting
     */
    public void setMarcXmlControlField(String tag, String value) {

        if (log.isDebugEnabled())
            log.debug("Setting a  controlfield to the MARC XML record with tag " + tag + " and value " + value + ".");
     
        for (Object o : marcXml.getChildren("controlfield", marcXml.getNamespace())) {
            Element e = (Element) o;
            if (tag.equals(e.getAttributeValue("tag"))) {
                e.setText(value);
            }
        }
    }

    /**
     * Adds a new controlfield to the MARC XML record and returns the result.
     *
     * @param tag
     *            The tag we're adding (i.e. 931)
     * @param value
     *            The value of the tag we're adding
     */
    public void addMarcXmlControlField(String tag, String value) {

        if (log.isDebugEnabled())
            log.debug("Adding a new controlfield to the MARC XML record with tag " + tag + " and value " + value + ".");

        // Add a MARC XML field with the specified tag
        // Both of its indicators will be empty
        Element newFieldElement = new Element("controlfield", marcNamespace);
        newFieldElement.setAttribute("tag", tag);
        newFieldElement.setText(value);

        // Add the new field to the end of the MARC XML if we didn't insert it already
        marcXml.addContent(4, newFieldElement);
        
    }

    /**
     * Copies one marcXml tag into another marcXml tag
     *
     * @param copyFromTag
     *            The tag we're copying from
     * @param copyToTag
     *            The tag we're copying into
     */
    @SuppressWarnings("unchecked")
    public void copyMarcXmlField(String copyFromTag, String copyToTag, String subfieldsToCopy, String newInd1, String newInd2, boolean skipAPrefix) {
        TimingLogger.start("copyMarcXmlField");

        if (log.isDebugEnabled())
            log.debug("Copying the MARC XML tag " + copyFromTag + " subfields " + subfieldsToCopy + " into the MARC XML tag " + copyToTag);

        // Get the data fields
        TimingLogger.start("copyMarcXmlField.getChildren");
        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);
        TimingLogger.stop("copyMarcXmlField.getChildren");

        // The copied field with the correct tag
        Element newField = null;

        // Iterate over the fields and find the one with the tag we're to copy from
        for (Element field : fields) {
            // If the current field is the correct tag, clone it into newField and change it's tag attribute to the correct value
            if (field.getAttribute("tag").getValue().equals(copyFromTag)) {
                // Get the number of leading characters to skip from the 2nd indicator
                int skip = 0;
                try {
                    skip = Integer.parseInt(field.getAttribute("ind2").getValue());
                } catch (NumberFormatException e) {

                }

                // Create the datafield for the copied tag, and set its attributes
                // The indicators should be the same as on the original field
                newField = new Element("datafield", marcNamespace);
                newField.setAttribute("tag", copyToTag);
                newField.setAttribute("ind1", newInd1);
                newField.setAttribute("ind2", newInd2);

                // Loop over the field's subfields and copy over the requested ones
                List<Element> subfields = field.getChildren("subfield", marcNamespace);
                for (Element subfield : subfields) {
                    if (subfieldsToCopy.contains(subfield.getAttributeValue("code"))) {
                        newField.addContent("\n\t").addContent((Element) subfield.clone());

                        if ((skipAPrefix && skip > 0) && (subfield.getAttributeValue("code").equals("a"))) {
                            // Get the control fields
                            List<Element> subfieldsOfNewfield = getSubfieldsOfField(newField, 'a');

                            // Iterate over the subfields to find the target subfield
                            for (Element subfieldOfNewfield : subfieldsOfNewfield) {
                                // Get the current text of the subfield
                                String currentText = subfieldOfNewfield.getText();

                                // This check is required for bad records (like the 245 of the UR voyager record #1493362
                                if (currentText.length() > skip + 1) {
                                    subfieldOfNewfield.setText(currentText.substring(skip, skip + 1).toUpperCase() + currentText.substring(skip + 1));
                                }
                            } // end loop over the target field's subfields
                        }
                    }
                }

                break;
            } // end if (tag found)
        } // end loop over data fields

        // Format the new field for human readability
        newField.addContent("\n");

        // Add the new field to the end of the MARC XML if we found the field to copy from
        if (newField != null)
            marcXml.addContent(newField).addContent("\n\n");

        // If we created a new 240 tag as a result of the copy, save it
        if (copyToTag.equals("240") && field240element.size() == 0)
            field240element.add(newField);

        TimingLogger.stop("copyMarcXmlField");
    } // end method copyMarcXmlField

    /**
     * Used to split the 4 categories of information contained in 6XX tags.
     * If any of the required subfields exist in a target datafield, all
     * subfields in that datafield are copied into the copy into field until the
     * first occurrence of a required field. In addition, the $2 is copied into the
     * resulting field, even if it occurred after a stop field
     *
     * @param targetFields
     *            The fields which are to be split
     * @param copyIntoField
     *            The field to copy the target fields into
     * @param requiredSubfields
     *            At least one of these subfields must exist in a datafield for it to be split
     *            Nothing after or including these fields should be copied except for a $2
     */
    @SuppressWarnings("unchecked")
    public void splitField(List<String> targetFields, String copyIntoField, String requiredSubfields) {
        TimingLogger.start("splitField");
        if (log.isDebugEnabled())
            log.debug("Copying " + targetFields.size() + " MARC XML tags into the MARC XML tag " + copyIntoField + " using only subfields before " + requiredSubfields);

        // Get the data fields
        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);

        // The copied field with the correct tag
        Element newField = null;

        // A list of the elements to add. We can't add them in the loop or we'll get a ConcurrentModificationException
        ArrayList<Element> newFields = new ArrayList<Element>();

        // Iterate over the fields and find the one with the tag we're to copy from
        for (Element field : fields) {
            // If the current field is the correct tag, clone it into newField and change it's tag attribute to the correct value
            if (targetFields.contains(field.getAttribute("tag").getValue())) {
                // Create the new field
                newField = new Element("datafield", marcNamespace);
                newField.setAttribute("tag", copyIntoField);
                newField.setAttribute("ind1", field.getAttribute("ind1").getValue());
                newField.setAttribute("ind2", field.getAttribute("ind2").getValue());
                newField.addContent("\n");

                // Get the subfields
                List<Element> subfields = field.getChildren("subfield", marcNamespace);

                // Iterate over the subfields, and append each one to the subject display if it
                // is in the list of subfields to copy
                for (Element subfield : subfields) {
                    // If we hit a subfield we're supposed to split on, add a new field and then setup another one
                    if (requiredSubfields.contains(subfield.getAttribute("code").getValue())) {
                        // If the datafield contained a $2, copy that as well
                        List<String> subfield2s = getSubfieldOfField(field, '2');

                        if (subfield2s.size() > 0) {
                            // Add the $5 subfield to the new MARC XML field with the value of the user's organization code
                            Element newField2Subfield = new Element("subfield", marcNamespace);
                            newField2Subfield.setAttribute("code", "2");
                            newField2Subfield.setText(subfield2s.get(0));

                            // Add the $2 subfield to the new datafield
                            newField.addContent("\t").addContent(newField2Subfield).addContent("\n");
                        }

                        // Add the new field if we added any subfields to it
                        if (newField.getChildren("subfield", marcNamespace).size() > 0) {
                            // Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
                            if (copyIntoField.startsWith("9")) {
                                // Add the $5 subfield to the new MARC XML field with the value of the user's organization code
                                Element newField5Subfield = createOrgCodeSubfield();

                                // Add the $5 subfield to the new datafield
                                newField.addContent("\t").addContent(newField5Subfield).addContent("\n");
                            }

                            newFields.add(newField);
                        }

                        // Stop iterating over subfields since we hit a stop field
                        break;

                    }

                    // We hit a subfield we're to copy, so copy it into the new field
                    newField.addContent("\t").addContent((new Element("subfield", marcNamespace)).setAttribute("code", subfield.getAttribute("code").getValue()).addContent(subfield.getText())).addContent("\n");
                }

                // Reset the new field
                newField = new Element("datafield", marcNamespace);
                newField.setAttribute("tag", copyIntoField);
                newField.setAttribute("ind1", field.getAttribute("ind1").getValue());
                newField.setAttribute("ind2", field.getAttribute("ind2").getValue());
                newField.addContent("\n");
            } // end if (tag found)
        } // end loop over data fields

        // Add all the new elements
        for (Element addMe : newFields)
            marcXml.addContent(addMe).addContent("\n\n");

        TimingLogger.stop("splitField");
    }

    /**
     * Used to split the 4 categories of information contained in 6XX tags.
     * Copies only the specified subfield into the copy into field, each into
     * its own field. In addition, the $2 is copied into the resulting fields
     *
     * @param targetFields
     *            The fields which are to be split
     * @param copyIntoField
     *            The field to copy the target fields into
     * @param copyOnlySubfield
     *            The subfield to copy
     */
    @SuppressWarnings("unchecked")
    public void splitField(List<String> targetFields, String copyIntoField, char copyOnlySubfield) {
        TimingLogger.start("splitField");

        if (log.isDebugEnabled())
            log.debug("Copying " + targetFields.size() + " MARC XML tags into the MARC XML tag " + copyIntoField + " using only subfield " + copyOnlySubfield);

        // Get the data fields
        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);

        // The copied field with the correct tag
        Element newField = null;

        // A list of the elements to add. We can't add them in the loop or we'll get a ConcurrentModificationException
        ArrayList<Element> newFields = new ArrayList<Element>();

        // Iterate over the fields and find the one with the tag we're to copy from
        for (Element field : fields) {
            // If the current field is the correct tag, clone it into newField and change it's tag attribute to the correct value
            if (targetFields.contains(field.getAttribute("tag").getValue())) {
                // Get the subfields
                List<String> subfields = getSubfieldOfField(field, copyOnlySubfield);

                // Get the $2 value, if there was one
                List<String> subfield2s = getSubfieldOfField(field, '2');
                String subfield2 = (subfield2s.size() > 0 ? subfield2s.get(0) : null);

                // Iterate over the subfields, and append each one to the subject display if it
                // is in the list of subfields to copy
                for (String subfield : subfields) {
                    // Reset the new field
                    newField = new Element("datafield", marcNamespace);
                    newField.setAttribute("tag", copyIntoField);
                    newField.setAttribute("ind1", field.getAttribute("ind1").getValue());
                    newField.setAttribute("ind2", field.getAttribute("ind2").getValue());
                    newField.addContent("\n");

                    // Add the target subfield to the new MARC XML field with the value of the user's organization code
                    Element newFieldSubfield = new Element("subfield", marcNamespace);
                    newFieldSubfield.setAttribute("code", copyOnlySubfield + "");
                    newFieldSubfield.setText(subfield);

                    // Add the $2 subfield to the new datafield
                    newField.addContent("\t").addContent(newFieldSubfield).addContent("\n");

                    // Add the $2 subfield to the new MARC XML field with the value of the user's organization code
                    if (subfield2 != null && subfield2.length() > 0) {
                        Element newField2Subfield = new Element("subfield", marcNamespace);
                        newField2Subfield.setAttribute("code", "2");
                        newField2Subfield.setText(subfield2);

                        // Add the $2 subfield to the new datafield
                        newField.addContent("\t").addContent(newField2Subfield).addContent("\n");
                    }

                    // Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
                    if (copyIntoField.startsWith("9")) {
                        // Add the $5 subfield to the new MARC XML field with the value of the user's organization code
                        Element newField5Subfield = createOrgCodeSubfield();

                        // Add the $5 subfield to the new datafield
                        newField.addContent("\t").addContent(newField5Subfield).addContent("\n");
                    }

                    newFields.add(newField);
                }
            } // end if (tag found)
        } // end loop over data fields

        // Add all the new elements
        for (Element addMe : newFields)
            marcXml.addContent(addMe).addContent("\n\n");

        TimingLogger.stop("splitField");
    }

    /**
     * Used to pull name information out of 6XX tags. If the $t subfield exists in
     * a target datafield, all subfields in that datafield are copied into the copy
     * into field until the first occurrence of a $t field. In addition, the $4
     * is copied into the resulting field, even if it occurred after a $t
     *
     * @param targetFields
     *            The fields which are to be split
     * @param copyIntoField
     *            The field to copy the target fields into
     */
    @SuppressWarnings("unchecked")
    public void separateNames(List<String> targetFields, String copyIntoField) {
        TimingLogger.start("separateNames");
        if (log.isDebugEnabled())
            log.debug("Copying " + targetFields.size() + " MARC XML tags into the MARC XML tag " + copyIntoField + " using only subfields before the $t.");

        // Get the data fields
        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);

        // The copied field with the correct tag
        Element newField = null;

        // A list of the elements to add. We can't add them in the loop or we'll get a ConcurrentModificationException
        ArrayList<Element> newFields = new ArrayList<Element>();

        // Iterate over the fields and find the one with the tag we're to copy from
        for (Element field : fields) {
            // If the current field is the correct tag, clone it into newField and change it's tag attribute to the correct value
            if (targetFields.contains(field.getAttribute("tag").getValue()) && field.getAttribute("ind2").getValue().equals("2")) {
                // Create the new field
                newField = new Element("datafield", marcNamespace);
                newField.setAttribute("tag", copyIntoField);
                newField.setAttribute("ind1", field.getAttribute("ind1").getValue());
                newField.setAttribute("ind2", "2");
                newField.addContent("\n");

                // Get the subfields
                List<Element> subfields = field.getChildren("subfield", marcNamespace);

                // Iterate over the subfields, and append each one to the subject display if it
                // is in the list of subfields to copy
                for (Element subfield : subfields) {
                    // If the current subfield matches the required subfield, add the new field
                    // and then setup the next one
                    if (subfield.getAttributeValue("code").equals("t")) {
                        // If the datafield contained a $4, copy that as well
                        List<String> subfield4s = getSubfieldOfField(field, '4');

                        if (subfield4s.size() > 0) {
                            // Add the $5 subfield to the new MARC XML field with the value of the user's organization code
                            Element newField4Subfield = new Element("subfield", marcNamespace);
                            newField4Subfield.setAttribute("code", "4");
                            newField4Subfield.setText(subfield4s.get(0));

                            // Add the $2 subfield to the new datafield
                            newField.addContent("\t").addContent(newField4Subfield).addContent("\n");
                        }

                        // Add the new field if we added any subfields to it
                        if (newField.getChildren("subfield", marcNamespace).size() > 0) {
                            // Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
                            if (copyIntoField.startsWith("9")) {
                                // Add the $5 subfield to the new MARC XML field with the value of the user's organization code
                                Element newField5Subfield = createOrgCodeSubfield();

                                // Add the $5 subfield to the new datafield
                                newField.addContent("\t").addContent(newField5Subfield).addContent("\n");
                            }

                            newFields.add(newField);
                        }

                        // Get the next linking field
                        String linkingField = getNextLinkingField();

                        // Add the $8 subfield to the new MARC XML field with the specified linking field
                        Element newField8Subfield = new Element("subfield", marcNamespace);
                        newField8Subfield.setAttribute("code", "8");
                        newField8Subfield.setText(linkingField);

                        // Add the $8 subfield to link the original datafield and the copy we just made
                        // Clone the subfield when we add it to the original field because we can't
                        // add the same Element to the root jdom Document twice
                        newField.addContent("\t").addContent(newField8Subfield).addContent("\n");
                        field.addContent("\t").addContent((Element) newField8Subfield.clone()).addContent("\n");

                        if (!usedLinkingFields.contains(linkingField))
                            usedLinkingFields.add(linkingField);

                        // Stop iterating over subfields since we hit a stop field
                        break;

                    }

                    // We hit a subfield we're to copy, so copy it into the new field
                    newField.addContent("\t").addContent((new Element("subfield", marcNamespace)).setAttribute("code", subfield.getAttribute("code").getValue()).addContent(subfield.getText())).addContent("\n");
                }

                // Reset the new field
                newField = new Element("datafield", marcNamespace);
                newField.setAttribute("tag", copyIntoField);
                newField.setAttribute("ind1", field.getAttribute("ind1").getValue());
                newField.setAttribute("ind2", field.getAttribute("ind2").getValue());
                newField.addContent("\n");
            } // end if (tag found)
        } // end loop over data fields

        // Add all the new elements
        for (Element addMe : newFields)
            marcXml.addContent(addMe).addContent("\n\n");

        TimingLogger.stop("separateNames");
    }

    /**
     * Removes duplicate values for MARC XML fields with a given tag.
     * Two fields are considered duplicates if they have the same subfields
     * in the same order with the same values. If one or more duplicate
     * fields are found with the specified tag, all but one of them are removed.
     *
     * @param tag
     *            The tag whose fields should be deduplicated.
     */
    @SuppressWarnings("unchecked")
    public void deduplicateMarcXmlField(String tag) {
        TimingLogger.start("deduplicateMarcXmlField");
        LOG.debug("deduping tag: " + tag);
        // A list of values we've currently seen on target fields
        HashSet<String> currentValues = new HashSet<String>();

        // Get the data fields
        List<Element> fields = getDataFields(tag);

        // A list of elements to be removed as duplicates
        ArrayList<Element> toRemove = new ArrayList<Element>();

        // Iterate over the fields and find the one with the correct tag
        for (Element field : fields) {
            LOG.debug("field: " + field);
            // A string which is unique for each distinct field
            StringBuilder value = new StringBuilder();

            // Get the subfields
            List<Element> subfields = field.getChildren("subfield", marcNamespace);

            // Get the 2nd indicator and append it to the field's value
            String ind2 = getIndicatorOfField(field, "2");
            value.append("ind2" + ind2);

            // Loop over each subfield and add the values of each to the datafield's value.
            for (Element subfield : subfields)
                value.append(subfield.getText().toLowerCase());

            // Remove an ending period since this should not effect whether or not fields are considered duplicates
            if (value.charAt(value.length() - 1) == '.')
                value.setCharAt(value.length() - 1, ' ');

            String valueStr = value.toString().trim();

            // If we've already seen the value of the current field, remove the
            // current field as it is a duplicate. Otherwise, add its value to
            // the list of values we've seen.
            LOG.debug("valueStr: " + valueStr);
            if (currentValues.contains(valueStr)) {
                if (log.isDebugEnabled())
                    log.debug("Removing duplicate " + tag + " field with value " + valueStr + ".");

                toRemove.add(field);
            } else
                currentValues.add(valueStr);
        } // end loop over data fields

        // Remove the fields we found to be duplicates
        for (Element removeMe : toRemove)
            marcXml.removeContent(removeMe);

        TimingLogger.stop("deduplicateMarcXmlField");
    } // end method deduplicateMarcXmlField

    /**
     * Removes duplicate values for MARC XML 959 fields.
     * Two fields are considered duplicates if they have the same subfields
     * in the same order with the same values. If one or more duplicate
     * fields are found with the specified tag, all but one of them are removed.
     *
     * 959 fields have a separate dedup method since they have to handle linking fields
     */
    @SuppressWarnings("unchecked")
    public void deduplicateMarcXml959Field() {
        TimingLogger.start("deduplicateMarcXml959Field");

        // A Map from the values we've currently seen on target fields
        // to the 959 Element which contained them.
        HashMap<String, Element> currentValuesTo959 = new HashMap<String, Element>();

        // Get the data fields
        List<Element> fields = getDataFields("959");

        // A list of elements to be removed as duplicates
        ArrayList<Element> toRemove = new ArrayList<Element>();

        // The values of the $8 subfields on the current field
        ArrayList<Element> current8s = new ArrayList<Element>();

        // Iterate over the fields and find the one with the correct tag
        for (Element field : fields) {
            // A string which is unique for each distinct field
            StringBuilder value = new StringBuilder();

            // Get the subfields
            List<Element> subfields = field.getChildren("subfield", marcNamespace);

            // Reset the values of the $8 subfields on the current field
            current8s.clear();

            // Get the 2nd indicator and append it to the field's value
            String ind2 = getIndicatorOfField(field, "2");
            value.append("ind2" + ind2);

            // Loop over each subfield and add the values of each to the datafield's value.
            // Don't include the $8 linking field.
            for (Element subfield : subfields) {
                if (!subfield.getAttribute("code").getValue().equals("8"))
                    value.append(subfield.getText().toLowerCase());
                else
                    current8s.add(subfield);
            }

            // Remove an ending period since this should not effect whether or not fields are considered duplicates
            if (value.charAt(value.length() - 1) == '.')
                value.setCharAt(value.length() - 1, ' ');

            String valueStr = value.toString().trim();

            // If we've already seen the value of the current field, remove the
            // current field as it is a duplicate. Otherwise, add its value to
            // the list of values we've seen.
            if (currentValuesTo959.containsKey(valueStr)) {
                if (log.isDebugEnabled())
                    log.debug("Removing duplicate 959 field with value " + valueStr + ".");

                toRemove.add(field);

                Element duplicateOf = currentValuesTo959.get(valueStr);

                for (Element current8 : current8s)
                    duplicateOf.addContent("\t").addContent(((Element) current8.clone())).addContent("\n");
            } else
                currentValuesTo959.put(valueStr, field);
        } // end loop over data fields

        // Remove the fields we found to be duplicates
        for (Element removeMe : toRemove)
            marcXml.removeContent(removeMe);

        TimingLogger.stop("deduplicateMarcXml959Field");
    } // end method deduplicateMarcXmlField

    /**
     * Sets the value for a specified subfield of a specified field. If the requested
     * field does not exist this method will do nothing. If there are more than one
     * entry for the requested field, the subfield is set on the first one only.
     *
     * @param targetField
     *            The tag of the field to set the subfield for.
     * @param targetSubfield
     *            The subfield to set.
     * @param value
     *            The value to set the subfield to.
     */
    public void setMarcXmlSubfield(String targetField, String targetSubfield, String value) {
        setMarcXmlSubfield(targetField, targetSubfield, value, null);
    }

    /**
     * Sets the value for a specified subfield of a specified field. If the requested
     * field does not exist this method will do nothing. If there are more than one
     * entry for the requested field, the subfield is set on the first one only.
     *
     * @param targetField
     *            The tag of the field to set the subfield for.
     * @param targetSubfield
     *            The subfield to set.
     * @param value
     *            The value to set the subfield to.
     * @param oldValue
     *            The old value of the field, or null if we don't care what the old value was
     */
    @SuppressWarnings("unchecked")
    public void setMarcXmlSubfield(String targetField, String targetSubfield, String value, String oldValue) {
        TimingLogger.start("setMarcXmlSubfield");

        if (log.isDebugEnabled())
            log.debug("Setting " + targetField + " $" + targetSubfield + " to " + value + (oldValue == null ? "." : " where the old value was " + oldValue + "."));

        // true only when we set the value
        boolean setValue = false;

        // Get the data fields
        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);

        // The field we should add the subfield to
        Element addSubfieldToMe = null;

        // Iterate over the fields and find the one with the correct tag
        for (Element field : fields) {
            // If the current field is the target field, get its subfields
            if (field.getAttribute("tag").getValue().equals(targetField)) {
                // Get the control fields
                List<Element> subfields = getSubfieldsOfField(field, targetSubfield.charAt(0));

                // Iterate over the subfields to find the target subfield
                for (Element subfield : subfields) {
                    // Set the value of the subfield if it is the target subfield
                    // If the oldValue was specified (not null,) only set the subfield if it's current value equals oldValue.
                    if ((oldValue == null || subfield.getText().equals(oldValue))) {
                        if (log.isDebugEnabled())
                            log.debug("Found " + targetField + " $" + targetSubfield + ", setting its value to " + value + ".");

                        subfield.setText(value);

                        setValue = true;
                    } // end if (target subfield found)
                } // end loop over the target field's subfields

                // If we couldn't find the specified subfield, we need to add it to the current field.
                if (!setValue && oldValue == null) {
                    addSubfieldToMe = field;
                    break;
                }
            } // end if (the target field found)
        } // end loop over data fields

        // If the target subfield did not exist, add it to the target field
        if (addSubfieldToMe != null && !setValue) {
            if (log.isDebugEnabled())
                log.debug("Adding $" + targetSubfield + " to " + targetField + " with value " + value + ".");

            // Add the subfield to the field with the specified value
            Element newSubfield = new Element("subfield", marcNamespace);
            newSubfield.setAttribute("code", targetSubfield);
            newSubfield.setText(value);
            addSubfieldToMe.addContent("\t").addContent(newSubfield).addContent("\n\t");
        } // end if (the value wasn't set)

        TimingLogger.stop("setMarcXmlSubfield");
    } // end method setMarcXmlSubfield

    /**
     * Sets the value for a specified subfield of a specified field. If the requested
     * field does not exist this method will do nothing. If there are more than one
     * entry for the requested field, the subfield is set on the nth one only.
     *
     * @param targetField
     *            The tag of the field to set the subfield for.
     * @param targetSubfield
     *            The subfield to set.
     * @param value
     *            The value to set the subfield to.
     * @param n
     *            The index of the target subfield relative to the subfields matching the targetField tag
     */
    @SuppressWarnings("unchecked")
    public void setMarcXmlSubfield(String targetField, String targetSubfield, String value, int n) {
        TimingLogger.start("setMarcXmlSubfield2");

        if (log.isDebugEnabled())
            log.debug("Setting " + targetField + " $" + targetSubfield + " to " + value + " where the index is " + n + ".");

        // true only when we set the value
        boolean setValue = false;

        // Get the data fields
        List<Element> fields = marcXml.getChildren("datafield", marcNamespace);

        // The field we should add the subfield to
        Element addSubfieldToMe = null;

        // The index of the next occurrence of the target field
        int occurrence = 0;

        // Iterate over the fields and find the one with the correct tag
        for (Element field : fields) {
            // If the current field is the target field, get its subfields
            if (field.getAttribute("tag").getValue().equals(targetField)) {
                if (occurrence == n) {
                    addSubfieldToMe = field;
                    break;
                } // end if (the correct occurrence was found)
                else
                    occurrence++;
            } // end if (the target field found)
        } // end loop over data fields

        // If the target subfield did not exist, add it to the target field
        if (addSubfieldToMe != null && !setValue) {
            if (log.isDebugEnabled())
                log.debug("Adding $" + targetSubfield + " to " + targetField + " with value " + value + ".");

            // Add the subfield to the field with the specified value
            Element newSubfield = new Element("subfield", marcNamespace);
            newSubfield.setAttribute("code", targetSubfield);
            newSubfield.setText(value);

            addSubfieldToMe.addContent("\t").addContent(newSubfield).addContent("\n\t");
        } // end if (the value wasn't set)

        TimingLogger.stop("setMarcXmlSubfield2");
    } // end method setMarcXmlSubfield

    /**
     * Given an Element containing a MARC XML datafield, return the value of the specified subfield of that Element
     *
     * @param datafield
     *            The Element we're getting the subfield of
     * @param subfield
     *            The subfield to get
     * @return The value of the requested subfield of the datafield
     */
    @SuppressWarnings("unchecked")
    public List<Element> getSubfieldsOfField(Element datafield, char subfield) {
        TimingLogger.start("getSubfieldsOfField");
        if (log.isDebugEnabled())
            log.debug("Getting the " + subfield + " of the passed datafield.");

        try {
            List nodes = null;
            TimingLogger.start("xpath");
            TimingLogger.start("getSubfieldsOfField.xpath");
            if (useXpath) {
                // An XPATH expression to get the requested subfields
                XPath xpath = XPath.newInstance("marc:subfield[@code='" + subfield + "']");
                xpath.addNamespace(marcNamespace);
                // Get the subfields.
                nodes = xpath.selectNodes(datafield);
            } else {
                nodes = new ArrayList<Element>();
                for (Object o : datafield.getChildren("subfield", datafield.getNamespace())) {
                    Element e = (Element) o;
                    if ((subfield + "").equals(e.getAttributeValue("code"))) {
                        nodes.add(e);
                    }
                }
            }
            TimingLogger.stop("xpath");
            TimingLogger.stop("getSubfieldsOfField.xpath");
            TimingLogger.stop("getSubfieldsOfField");
            return nodes;
        } catch (Throwable e) {
            log.error("An error occurred getting the $" + subfield + " subfields of the passed datafields.", e);
            TimingLogger.stop("getSubfieldsOfField");
            return new ArrayList<Element>();
        }

    }

    /**
     * Given an Element containing a MARC XML datafield, return the value of the specified subfield of that Element
     *
     * @param datafield
     *            The Element we're getting the subfield of
     * @param subfield
     *            The subfield to get
     * @return The value of the requested subfield of the datafield
     */
    @SuppressWarnings("unchecked")
    public List<String> getSubfieldValuesOfField(Element datafield, char subfield) {
        TimingLogger.start("getSubfieldValuesOfField");
        if (log.isDebugEnabled())
            log.debug("Getting the " + subfield + " of the passed datafield.");

        // Holds the results
        ArrayList<String> results = new ArrayList<String>();

        try {
            List<Element> elements = null;

            TimingLogger.start("getSubfieldValuesOfField.xpath");
            TimingLogger.start("xpath");
            if (useXpath) {
                // An XPATH expression to get the requested subfields
                XPath xpath = XPath.newInstance("marc:subfield[@code='" + subfield + "']");
                xpath.addNamespace(marcNamespace);

                // Get the subfields.
                elements = xpath.selectNodes(datafield);
            } else {
                elements = new ArrayList<Element>();
                List children = datafield.getChildren("subfield", datafield.getNamespace());
                for (Object o : children) {
                    Element e = (Element) o;
                    if ((subfield + "").equals(e.getAttributeValue("code"))) {
                        elements.add(e);
                    }
                }
            }
            TimingLogger.stop("getSubfieldValuesOfField.xpath");
            TimingLogger.stop("xpath");

            // Return the empty list if there were no matching subfields
            if (elements.size() == 0) {
                if (log.isDebugEnabled())
                    log.debug("The passed datafield did not have a $" + subfield + " subfield.");

                TimingLogger.stop("getSubfieldValuesOfField");
                return results;
            } else {
                // Loop over the elements with the correct field and subfield, and add value of
                // each to the list of results
                for (Element element : elements) {
                    // The value of the requested control field
                    String value = element.getText();

                    if (log.isDebugEnabled())
                        log.debug("Found a $" + subfield + " subfield with a value of " + value + ".");

                    results.add(value);
                }

                TimingLogger.stop("getSubfieldValuesOfField");
                return results;
            }
        } catch (Throwable e) {
            log.error("An error occurred getting the $" + subfield + " subfields of the passed datafields.", e);
            TimingLogger.stop("getSubfieldValuesOfField");
            return results;
        }
    }

    /**
     * Given an Element containing a MARC XML datafield, return the value of the specified indicator of that Element
     *
     * @param datafield
     *            The Element we're getting the indicator of
     * @param indicator
     *            The indicator to get
     * @return The value of the requested indicator of the datafield
     */
    @SuppressWarnings("unchecked")
    public String getIndicatorOfField(Element datafield, String indicator) {
        TimingLogger.start("getIndicatorOfField");
        if (log.isDebugEnabled())
            log.debug("Getting the ind" + indicator + " of the passed datafield.");

        try {
            Attribute ind = null;
            TimingLogger.start("getIndicatorOfField.xpath");
            TimingLogger.start("xpath");
            if (useXpath) {
                // An XPATH expression to get the requested indicator
                XPath xpath = XPath.newInstance("@ind" + indicator);
                xpath.addNamespace(marcNamespace);

                // Get the subfields.
                List<Attribute> attributes = xpath.selectNodes(datafield);

                if (attributes != null && attributes.size() > 0) {
                    ind = attributes.get(0);
                }
            } else {
                ind = datafield.getAttribute("ind" + indicator);
            }
            TimingLogger.stop("getIndicatorOfField.xpath");
            TimingLogger.stop("xpath");

            // Return the empty list if there were no matching subfields
            if (ind == null) {
                if (log.isDebugEnabled())
                    log.debug("The passed datafield did not have a ind" + indicator + ".");

                TimingLogger.stop("getIndicatorOfField");
                return null;
            } else {
                // The value of the requested control field
                String value = ind.getValue();

                if (log.isDebugEnabled())
                    log.debug("Found a ind" + indicator + " with a value of " + value + ".");

                TimingLogger.stop("getIndicatorOfField");
                return value;
            }
        } catch (Throwable e) {
            log.error("An error occurred getting the ind" + indicator + " of the passed datafield.", e);
            TimingLogger.stop("getIndicatorOfField");
            return null;
        }
    }

    /**
     * Gets all MARC XML data fields with a given tag
     *
     * @param targetField
     *            The tag of the data fields to retrieve (for example, "035")
     * @return A list of all data fields with the requested tag
     */
    @SuppressWarnings("unchecked")
    public List<Element> getDataFields(String targetField) {
        TimingLogger.start("getDataFields");
        if (log.isDebugEnabled())
            log.debug("Getting the " + targetField + " fields.");

        try {
            List<Element> potentialResults = null;
            TimingLogger.start("getDataFields.xpath");
            TimingLogger.start("xpath");
            if (useXpath) {
                // An XPATH expression to get the requested control field
                XPath xpath = XPath.newInstance(".//marc:datafield[@tag='" + targetField + "']");
                xpath.addNamespace(marcNamespace);

                potentialResults = xpath.selectNodes(marcXml);
            } else {
                potentialResults = new ArrayList<Element>();
                for (Object o : marcXml.getChildren("datafield", marcXml.getNamespace())) {
                    Element e = (Element) o;
                    if (targetField.equals(e.getAttributeValue("tag"))) {
                        potentialResults.add(e);
                    }
                }
            }
            TimingLogger.stop("getDataFields.xpath");
            TimingLogger.stop("xpath");

            // Get the data fields. If the target field was not a 9xx field we can return the entire
            // list, otherwise we need to filter out those results with the wrong organization code.
            if (!targetField.startsWith("9")) {
                List<Element> results = potentialResults;

                // Get the 880 fields that match the requested tag
                if (tagTo880s.containsKey(targetField))
                    results.addAll(tagTo880s.get(targetField));

                TimingLogger.stop("getDataFields");
                return results;
            } else {
                ArrayList<Element> results = new ArrayList<Element>();

                // Get the 880 fields that match the requested tag
                if (tagTo880s.containsKey(targetField))
                    potentialResults.addAll(tagTo880s.get(targetField));

                for (Element potentialResult : potentialResults)
                    if (getSubfieldOfField(potentialResult, '5').contains(this.organizationCode))
                        results.add(potentialResult);

                TimingLogger.stop("getDataFields");
                return results;
            }
        } catch (Throwable e) {
            log.error("An error occurred getting the " + targetField + " fields.", e);
            TimingLogger.stop("getDataFields");
            return new ArrayList<Element>();
        }
    }

    /**
     * Given an Element containing a MARC XML datafield, return the value of the specified subfield of that Element
     *
     * @param datafield
     *            The Element we're getting the subfield of
     * @param subfield
     *            The subfield to get
     * @return The value of the requested subfield of the datafield
     */
    @SuppressWarnings("unchecked")
    public static List<String> getSubfieldOfField(Element datafield, char subfield) {
        TimingLogger.start("getSubfieldOfField");
        if (log.isDebugEnabled())
            log.debug("Getting the " + subfield + " of the passed datafield.");

        // Holds the results
        ArrayList<String> results = new ArrayList<String>();

        try {
            List<Element> elements = null;
            TimingLogger.start("getSubfieldOfField.xpath");
            TimingLogger.start("xpath");
            if (useXpath) {
                // An XPATH expression to get the requested subfields
                XPath xpath = XPath.newInstance("marc:subfield[@code='" + subfield + "']");
                xpath.addNamespace(marcNamespace);

                // Get the subfields.
                elements = xpath.selectNodes(datafield);
            } else {
                elements = new ArrayList<Element>();
                for (Object o : datafield.getChildren("subfield", datafield.getNamespace())) {
                    Element e = (Element) o;
                    if ((subfield + "").equals(e.getAttributeValue("code"))) {
                        elements.add(e);
                    }
                }
            }
            TimingLogger.stop("getSubfieldOfField.xpath");
            TimingLogger.stop("xpath");

            // Return the empty list if there were no matching subfields
            if (elements.size() == 0) {
                if (log.isDebugEnabled())
                    log.debug("The passed datafield did not have a $" + subfield + " subfield.");

                TimingLogger.stop("getSubfieldOfField");
                return results;
            } else {
                // Loop over the elements with the correct field and subfield, and add value of
                // each to the list of results
                for (Element element : elements) {
                    // The value of the requested control field
                    String value = element.getText();

                    if (log.isDebugEnabled())
                        log.debug("Found a $" + subfield + " subfield with a value of " + value + ".");

                    results.add(value);
                }

                TimingLogger.stop("getSubfieldOfField");
                return results;
            }
        } catch (Throwable e) {
            log.error("An error occurred getting the $" + subfield + " subfields of the passed datafields.", e);
            TimingLogger.stop("getSubfieldOfField");
            return results;
        }
    }

    /**
     * Gets all MARC XML datafields containing linking fields ($8 subfields for any tag)
     *
     * @return A list of all datafields containing linking fields in the MARCXML document
     */
    @SuppressWarnings("unchecked")
    public List<Element> getLinkingFieldsParents() {
        TimingLogger.start("getLinkingFieldsParents");
        if (log.isDebugEnabled())
            log.debug("Getting the linking fields.");

        try {
            TimingLogger.start("getLinkingFieldsParents.xpath");
            TimingLogger.start("xpath");
            List<Element> results = null;
            if (useXpath) {
                // An XPATH expression to get the linking field
                TimingLogger.start("getLinkingFieldsParents.XPath.newInstance");
                XPath xpath = XPath.newInstance(".//marc:subfield[@code='8']/..");
                xpath.addNamespace(marcNamespace);
                TimingLogger.stop("getLinkingFieldsParents.XPath.newInstance");

                // Get the linking fields.
                TimingLogger.start("getLinkingFieldsParents.selectNodes");
                results = xpath.selectNodes(marcXml);
                TimingLogger.stop("getLinkingFieldsParents.selectNodes");
            } else {
                results = new ArrayList<Element>();
                for (Object o : marcXml.getChildren("datafield", marcXml.getNamespace())) {
                    Element e = (Element) o;
                    for (Object o2 : e.getChildren("subfield", e.getNamespace())) {
                        Element e2 = (Element) o2;
                        if ("8".equals(e2.getAttributeValue("code"))) {
                            results.add(e);
                            break;
                        }
                    }
                }
            }
            TimingLogger.stop("getLinkingFieldsParents.xpath");
            TimingLogger.stop("xpath");
            TimingLogger.stop("getLinkingFieldsParents");
            return results;
        } catch (Throwable e) {
            log.error("An error occurred getting the linking fields.", e);
            TimingLogger.stop("getLinkingFieldsParents");
            return new ArrayList<Element>();
        }
    }

    /**
     * Gets the value of a MARC XML control field
     *
     * @param targetField
     *            The control field to retrieve (for example, "008")
     */
    @SuppressWarnings("unchecked")
    public void removeControlField(String targetField) {
        TimingLogger.start("removeControlField");
        try {
            if (log.isDebugEnabled())
                log.debug("Removing the control field " + targetField);

            TimingLogger.start("removeControlField.xpath");
            TimingLogger.start("xpath");
            List<Element> elements = null;
            if (useXpath) {
                // An XPATH expression to get the requested control field
                XPath xpath = XPath.newInstance(".//marc:controlfield[@tag='" + targetField + "']");
                xpath.addNamespace(marcNamespace);

                // Get the control field. There should not be more than one Element in this list.
                elements = xpath.selectNodes(marcXml);
            } else {
                elements = new ArrayList<Element>();
                for (Object o : marcXml.getChildren("controlfield", marcXml.getNamespace())) {
                    Element e = (Element) o;
                    if (targetField.equals(e.getAttributeValue("tag"))) {
                        elements.add(e);
                    }
                }
            }
            TimingLogger.stop("removeControlField.xpath");
            TimingLogger.stop("xpath");

            if (elements.size() == 0) {
                if (log.isDebugEnabled())
                    log.debug("The " + targetField + " control field did not exist in the MARC XML record.");
            } else {
                // Remove the control field
                marcXml.removeContent(elements.get(0));

                if (targetField.equals("003"))
                    field003 = null;
                else if (targetField.equals("001"))
                    field001 = null;
                else if (targetField.equals("006"))
                    field006 = new ArrayList<String>();
                else if (targetField.equals("007"))
                    field007 = null;
                else if (targetField.equals("008"))
                    field008 = null;

                if (log.isDebugEnabled())
                    log.debug("Removed the " + targetField + " control field.");
            }
        } catch (Throwable e) {
            log.error("An error occurred getting control field " + targetField);
        }
        TimingLogger.stop("removeControlField");
    }

    /**
     * Gets data field 945. Separate method is used for 945 because it needs to return
     * field 945 irrespective of $5 subfield value being organization code.
     *
     * @return A list of all data fields with the requested tag
     */
    @SuppressWarnings("unchecked")
    public List<Element> getField945() {
        TimingLogger.start("getField945");
        if (log.isDebugEnabled())
            log.debug("Getting the " + 945 + " fields.");

        try {
            TimingLogger.start("getField945.xpath");
            List<Element> elements = null;
            if (useXpath) {
                // An XPATH expression to get the requested control field
                XPath xpath = XPath.newInstance(".//marc:datafield[@tag='" + 945 + "']");
                xpath.addNamespace(marcNamespace);

                elements = xpath.selectNodes(marcXml);
            } else {
                elements = new ArrayList<Element>();
                for (Object o : marcXml.getChildren("datafield", marcXml.getNamespace())) {
                    Element e = (Element) o;
                    if ("945".equals(e.getAttributeValue("tag"))) {
                        elements.add(e);
                    }
                }
            }
            TimingLogger.stop("getField945.xpath");
            TimingLogger.stop("getField945");
            return elements;
        } catch (Throwable e) {
            log.error("An error occurred getting the " + 945 + " fields.", e);
            TimingLogger.stop("getField945");
            return new ArrayList<Element>();
        }
    }

    public void remove945(Element element) {

        // List<Element> children = marcXml.getRootElement().getChildren();
        // Element entityElement = ((Element)(children.get(0)));
        // entityElement.getChildren().remove(element);

        TimingLogger.start("remove945");
        // Remove the control field
        marcXml.removeContent(element);
        TimingLogger.stop("remove945");

    }

}
