/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

import xc.mst.constants.Constants;
import xc.mst.constants.NormalizationServiceConstants;


/**
 * This class is used to manage a MARC XML record.  When constructed,
 * it parses out fields likely to be needed by the Normalization service,
 * and contains methods to add, remove, modify, and deduplicate a field.
 *
 * @author Eric Osisek
 */
public class MarcXmlManagerForNormalizationService
{
	/**
	 * The logger object
	 */
	private static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);

	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration = ConfigurationManager.getConfiguration();


	/**
	 * The namespace for MARC XML
	 */
	private static Namespace marcNamespace = Namespace.getNamespace("marc", "http://www.loc.gov/MARC21/slim");

	/**
	 * The MARC XML Document we're managing
	 */
	private Document marcXml = null;

	/**
	 * A set of linking tags currently existing on the MARC XML record
	 */
	private HashSet<String> usedLinkingFields = new HashSet<String>();

	/**
	 * Tests whether or not the MARC XML record contains the passed linking field.
	 *
	 * @param linkingField The value of the linking field we're testing for duplicates.
	 * @return true iff the MARC XML record contained the passed linking field.
	 */
	public boolean isLinkingFieldUsed(String linkingField)
	{
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
	 * found in a $4 subfield.  If it is, the parent datafield should be
	 * ignored for certain operations.
	 *
	 * @param valueToTest The value of a $4 subfield
	 * @return true if valueToTest appearing in a $4 subfield means we
	 *         should ignore the parent datafield for operations which
	 *         ignore roles.
	 */
	public static boolean shouldIgnore4Subfield(String valueToTest)
	{
		return ignore4subfields.contains(valueToTest);
	}

	/**
	 * A map whose keys are MARCXML tags and whose values are a list of 880
	 * fields whose $6 equals that tag.
	 */
	private HashMap<String, List<Element>> tagTo880s = new HashMap<String, List<Element>>();

	// Initialize ignore4subfields
	static
	{
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
	 * The value of the next linking field we will use.  This value starts at 1 for each record we're
	 * normalizing, and increments every time a linking field is used in that record.
	 *
	 * We'll initialize it to 0 since we'll increment it before using it.
	 */
	private int nextLinkingField = 0;

	/**
	 * Calculates the next available linking field by incrementing
	 * nextLinkingField until it hits a value which does not exist
	 * on the current MARC XML record.  Since most MARC XML records
	 * will not use linking fields, this typically only increments
	 * nextLinkingField.
	 *
	 * @return The value of the next linking field to be used with this MARC XML record.
	 */
	public String getNextLinkingField()
	{
		// Increment nextLinkingField until it
		// hits a value which isn't used in the
		// current MARC XML record
		do
		{
			nextLinkingField++;
		}while(isLinkingFieldUsed(nextLinkingField+"\\c"));

		// Return the value of the next linking field
		return nextLinkingField+"\\c";
	}

	/**
	 * A list of the language codes we've added using this MarcXmlManagerForNormalizationService.  Because
	 * the Normalization Service may have to access this list after adding them, we'll
	 * save time by caching the values in this list.
	 */
	private ArrayList<String> addedLanguageCodes = new ArrayList<String>();

	/**
	 * Gets a list of the language codes we've added using this MarcXmlManagerForNormalizationService.
	 *
	 * @return A list of the language codes we've added using this MarcXmlManagerForNormalizationService.
	 */
	public ArrayList<String> getAddedLanguageCodes() { return addedLanguageCodes; }

	/**
	 * The organization code from the configuration file
	 */
	private String organizationCode = null;

	// The following variables are cached values for the commonly accessed MARC XML fields.
	// They are initialized in the constructor.  Each variable will be named either fieldNNN
	// to represent the $a subfield of field NNN, or fieldNNNsubfieldM to represent the M
	// subfield of field NNN.  There is also a leader variable, which contains the value of
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
	public String getLeader() { return leader; }

	/**
	 * The value of the 001 field
	 */
	private String field001 = null;

	/**
	 * Gets the 001 field
	 *
	 * @return the MARC XML's 001 field
	 */
	public String getField001() { return field001; }

	/**
	 * The value of the 003 field
	 */
	private String field003 = null;

	/**
	 * Gets the 003 field
	 *
	 * @return the MARC XML's 003 field
	 */
	public String getField003() { return field003; }

	/**
	 * The value of the 006 field
	 */
	private String field006 = null;

	/**
	 * Gets the 006 field
	 *
	 * @return the MARC XML's 006 field
	 */
	public String getField006() { return field006; }

	/**
	 * The value of the 007 field
	 */
	private String field007 = null;

	/**
	 * Gets the 007 field
	 *
	 * @return the MARC XML's 007 field
	 */
	public String getField007() { return field007; }

	/**
	 * The value of the 008 field
	 */
	private String field008 = null;

	/**
	 * Gets the 008 field
	 *
	 * @return the MARC XML's 008 field
	 */
	public String getField008() { return field008; }

	/**
	 * The value of the 020 field
	 */
	private ArrayList<String> field020 = new ArrayList<String>();

	/**
	 * Gets the 020 field
	 *
	 * @return the MARC XML's 020 field
	 */
	public ArrayList<String> getField020() { return field020; }

	/**
	 * The value of the 041 $a value
	 */
	private ArrayList<String> field041subfieldA = new ArrayList<String>();

	/**
	 * Gets the 041 $a value
	 *
	 * @return the MARC XML's 041 $a value
	 */
	public ArrayList<String> getField041subfieldA() { return field041subfieldA; }

	/**
	 * The value of the 041 $d value
	 */
	private ArrayList<String> field041subfieldD = new ArrayList<String>();

	/**
	 * Gets the 041 $d value
	 *
	 * @return the MARC XML's 041 $d value
	 */
	public ArrayList<String> getField041subfieldD() { return field041subfieldD; }

	/**
	 * The value of the 502 field
	 */
	private ArrayList<String> field502 = new ArrayList<String>();

	/**
	 * Gets the 502 field
	 *
	 * @return the MARC XML's 502 field
	 */
	public ArrayList<String> getField502() { return field502; }

	/**
	 * The 100 field's element
	 */
	private ArrayList<Element> field100element = new ArrayList<Element>();

	/**
	 * Gets the 100 field's element
	 *
	 * @return the MARC XML's 100 field's element
	 */
	public ArrayList<Element> getField100Element() { return field100element; }

	/**
	 * The 110 field's element
	 */
	private ArrayList<Element> field110element = new ArrayList<Element>();

	/**
	 * Gets the 110 field's element
	 *
	 * @return the MARC XML's 110 field's element
	 */
	public ArrayList<Element> getField110Element() { return field110element; }

	/**
	 * The 111 field's element
	 */
	private ArrayList<Element> field111element = new ArrayList<Element>();

	/**
	 * Gets the 111 field's element
	 *
	 * @return the MARC XML's 111 field's element
	 */
	public ArrayList<Element> getField111Element() { return field111element; }

	/**
	 * The value of the 100 $4 value
	 */
	private String field100subfield4 = null;

	/**
	 * Gets the 100 $4 value
	 *
	 * @return the MARC XML's 100 $4 value
	 */
	public String getField100subfield4() { return field100subfield4; }

	/**
	 * The value of the 110 $4 value
	 */
	private String field110subfield4 = null;

	/**
	 * Gets the 110 $4 value
	 *
	 * @return the MARC XML's 110 $4 value
	 */
	public String getField110subfield4() { return field110subfield4; }

	/**
	 * The value of the 111 $4 value
	 */
	private String field111subfield4 = null;

	/**
	 * Gets the 111 $4 value
	 *
	 * @return the MARC XML's 111 $4 value
	 */
	public String getField111subfield4() { return field111subfield4; }

	/**
	 * The value of the 130 field
	 */
	private String field130 = null;

	/**
	 * Gets the 130 field
	 *
	 * @return the MARC XML's 130 field
	 */
	public String getField130() { return field130; }

	/**
	 * The 240 field's element
	 */
	private ArrayList<Element> field240element = new ArrayList<Element>();

	/**
	 * Gets the 240 field's element
	 *
	 * @return the MARC XML's 240 field's element
	 */
	public ArrayList<Element> getField240Element() { return field240element; }

	/**
	 * The value of the 240 field
	 */
	private String field240 = null;

	/**
	 * Gets the 240 field
	 *
	 * @return the MARC XML's 240 field
	 */
	public String getField240() { return field240; }

	/**
	 * The value of the 243 field
	 */
	private String field243 = null;

	/**
	 * Gets the 243 field
	 *
	 * @return the MARC XML's 243 field
	 */
	public String getField243() { return field243; }

	/**
	 * The 243 field's element
	 */
	private ArrayList<Element> field243element = new ArrayList<Element>();

	/**
	 * Gets the 243 field's element
	 *
	 * @return the MARC XML's 243 field's element
	 */
	public ArrayList<Element> getField243Element() { return field243element; }

	/**
	 * The value of the 245 field
	 */
	private String field245 = null;

	/**
	 * Gets the 245 field
	 *
	 * @return the MARC XML's 245 field
	 */
	public String getField245() { return field245; }

	/**
	 * The 440 field's element
	 */
	private ArrayList<Element> field440elements = new ArrayList<Element>();

	/**
	 * Gets the 440 field's element
	 *
	 * @return the MARC XML's 440 field's element
	 */
	public ArrayList<Element> getField440Elements() { return field440elements; }

	/**
	 * The 600 field's element
	 */
	private ArrayList<Element> field600elements = new ArrayList<Element>();

	/**
	 * Gets the 600 field's element
	 *
	 * @return the MARC XML's 600 field's element
	 */
	public ArrayList<Element> getField600Elements() { return field600elements; }

	/**
	 * The 610 field's element
	 */
	private ArrayList<Element> field610elements = new ArrayList<Element>();

	/**
	 * Gets the 610 field's element
	 *
	 * @return the MARC XML's 610 field's element
	 */
	public ArrayList<Element> getField610Elements() { return field610elements; }

	/**
	 * The 611 field's element
	 */
	private ArrayList<Element> field611elements = new ArrayList<Element>();

	/**
	 * Gets the 611 field's element
	 *
	 * @return the MARC XML's 611 field's element
	 */
	public ArrayList<Element> getField611Elements() { return field611elements; }

	/**
	 * The 630 field's element
	 */
	private ArrayList<Element> field630elements = new ArrayList<Element>();

	/**
	 * Gets the 630 field's element
	 *
	 * @return the MARC XML's 630 field's element
	 */
	public ArrayList<Element> getField630Elements() { return field630elements; }

	/**
	 * The 650 field's element
	 */
	private ArrayList<Element> field650elements = new ArrayList<Element>();

	/**
	 * Gets the 650 field's element
	 *
	 * @return the MARC XML's 650 field's element
	 */
	public ArrayList<Element> getField650Elements() { return field650elements; }

	/**
	 * The 655 field's element
	 */
	private ArrayList<Element> field655elements = new ArrayList<Element>();

	/**
	 * Gets the 655 field's element
	 *
	 * @return the MARC XML's 655 field's element
	 */
	public ArrayList<Element> getField655Elements() { return field655elements; }

	/**
	 * The 700 field's element
	 */
	private ArrayList<Element> field700elements = new ArrayList<Element>();

	/**
	 * Gets the 700 field's element
	 *
	 * @return the MARC XML's 700 field's element
	 */
	public ArrayList<Element> getField700Elements() { return field700elements; }

	/**
	 * The 710 field's element
	 */
	private ArrayList<Element> field710elements = new ArrayList<Element>();

	/**
	 * Gets the 710 field's element
	 *
	 * @return the MARC XML's 710 field's element
	 */
	public ArrayList<Element> getField710Elements() { return field710elements; }

	/**
	 * The 711 field's element
	 */
	private ArrayList<Element> field711elements = new ArrayList<Element>();

	/**
	 * Gets the 711 field's element
	 *
	 * @return the MARC XML's 711 field's element
	 */
	public ArrayList<Element> getField711Elements() { return field711elements; }

	/**
	 * The 730 field's element
	 */
	private ArrayList<Element> field730elements = new ArrayList<Element>();

	/**
	 * Gets the 730 field's element
	 *
	 * @return the MARC XML's 730 field's element
	 */
	public ArrayList<Element> getField730Elements() { return field730elements; }

	/**
	 * The 800 field's element
	 */
	private ArrayList<Element> field800elements = new ArrayList<Element>();

	/**
	 * Gets the 800 field's element
	 *
	 * @return the MARC XML's 800 field's element
	 */
	public ArrayList<Element> getField800Elements() { return field800elements; }

	/**
	 * The 810 field's element
	 */
	private ArrayList<Element> field810elements = new ArrayList<Element>();

	/**
	 * Gets the 810 field's element
	 *
	 * @return the MARC XML's 810 field's element
	 */
	public ArrayList<Element> getField810Elements() { return field810elements; }

	/**
	 * The 811 field's element
	 */
	private ArrayList<Element> field811elements = new ArrayList<Element>();

	/**
	 * Gets the 811 field's element
	 *
	 * @return the MARC XML's 811 field's element
	 */
	public ArrayList<Element> getField811Elements() { return field811elements; }

	/**
	 * The value of the 852 $b value
	 */
	private ArrayList<String> field852subfieldBs = new ArrayList<String>();

	/**
	 * Gets the 852 $b value
	 *
	 * @return the MARC XML's 852 $b value
	 */
	public ArrayList<String> getField852subfieldBs() { return field852subfieldBs; }

	/**
	 * The value of the 945 $l value
	 */
	private ArrayList<String> field945subfieldLs = new ArrayList<String>();

	/**
	 * Gets the 945 $l value
	 *
	 * @return the MARC XML's 945 $l value
	 */
	public ArrayList<String> getField945subfieldLs() { return field945subfieldLs; }

	/**
	 * A list of the original 035 fields for the MARC XML
	 */
	private ArrayList<Element> original035fields = new ArrayList<Element>();

	/**
	 * Gets a list of the original 035 fields for the MARC XML.  Any changes
	 * made to the elements in this list will be reflected in the corrosponding
	 * 035 fields on the MARC XML records.
	 *
	 * @return A list of the original 035 fields for the MARC XML
	 */
	public ArrayList<Element> getOriginal035Fields() { return original035fields; }

	/**
	 * Constructs a MarcXmlManagerForNormalizationService based on a MARC XML record.
	 * This constructor will initialize all cached fields by iterating over
	 * the MARC XML record's fields exactly once.
	 *
	 * @param marcXml The MARC XML record we're managing
	 * @param organizationCode The organization code from the configuration file
	 */
	@SuppressWarnings("unchecked")
	public MarcXmlManagerForNormalizationService(Document marcXml, String organizationCode)
	{
		this.marcXml = marcXml;

		this.organizationCode = organizationCode;

		// Get the MARC XML's leader
		leader = this.marcXml.getRootElement().getChildText("leader", marcNamespace);

		if(log.isDebugEnabled())
			log.debug("Found the value of the leader to be " + leader + ".");

		// Initialize the MARC XML control fields
		initializeMarcControlFields();

		// Initialize the MARC XML data fields
		initializeMarcDataFields();

		// Initialize the used linking fields
		try
		{
			// Use XPATH to get a list of all linking fields currently
			// in the MARC XML records.  These are the values of the $8
			// subfield of any datafield
			XPath xpath = XPath.newInstance("//marc:subfield[@code='8']");
			xpath.addNamespace("marc", "http://www.loc.gov/MARC21/slim");
			List<Element> elements = xpath.selectNodes(marcXml);

			// Add all existing linking fields to the set of
			// initial linking fields
			for(Element element : elements)
				if(!usedLinkingFields.contains(element.getText()))
					usedLinkingFields.add(element.getText());
		}
		catch(JDOMException e)
		{
			log.error("An error occurred while getting the current linking fields on the MARC XML record.", e);
		}

	} // end constructor

	/**
	 * Gets the MARC XML resulting from the modifications which have been made through this MarcXmlManagerForNormalizationService
	 *
	 * @return The modified MARC XML record.
	 */
	public Document getModifiedMarcXml()
	{
		return marcXml;
	} // end method getModifiedMarcXml

	/**
	 * Returns true if the passed tag contains the passed subfield
	 *
	 * @param tag The tag we're checking
	 * @param subfield The subfield we're checking for
	 * @return true if the passed tag contains the passed subfield
	 */
	public boolean doesSubfieldExist(String tag, String subfield)
	{
		if(log.isDebugEnabled())
			log.debug("Checking if tag " + tag + " contains subfield $" + subfield + ".");

		// Check if the subfield exists for the passed tag
		try
		{
			// Use XPATH to get a list of all subfields of the passed tag with the passed subfield value.
			// Return true iff this list is not empty
			XPath xpath = XPath.newInstance("//marc:datafield[@tag='" + tag + "']/marc:subfield[@code='" + subfield + "']");
			xpath.addNamespace("marc", "http://www.loc.gov/MARC21/slim");
			return !xpath.selectNodes(marcXml).isEmpty();
		}
		catch(JDOMException e)
		{
			log.error("An error occurred while checking if tag " + tag + " contains subfield $" + subfield + ".", e);
			return false;
		}
	}

	/**
	 * Initializes the MARC XML control fields' cached values.
	 */
	@SuppressWarnings("unchecked")
	private void initializeMarcControlFields()
	{
		if(log.isDebugEnabled())
			log.debug("Initializing MARC XML control fields.");

		// Get the control fields
		List<Element> controlFields = marcXml.getRootElement().getChildren("controlfield", marcNamespace);

		// Iterate over the fields and find the 001, 003, 007, and 008 control fields.
		// Initialize their cached values as we find them.
		for(Element controlField : controlFields)
		{
			// Initialize the 001 field if we found it
			if(controlField.getAttribute("tag").getValue().equals("001"))
			{
				field001 = controlField.getText();

				if(log.isDebugEnabled())
					log.debug("Found the value of the control field 001 to be " + field001 + ".");
			} // end if (001 found)

			// Initialize the 003 field if we found it
			else if(controlField.getAttribute("tag").getValue().equals("003"))
			{
				field003 = controlField.getText();

				if(log.isDebugEnabled())
					log.debug("Found the value of the control field 003 to be " + field003 + ".");
			} // end if (003 found)

			// Initialize the 006 field if we found it
			else if(controlField.getAttribute("tag").getValue().equals("006"))
			{
				field006 = controlField.getText();

				if(log.isDebugEnabled())
					log.debug("Found the value of the control field 006 to be " + field006 + ".");
			} // end if (006 found)

			// Initialize the 007 field if we found it
			else if(controlField.getAttribute("tag").getValue().equals("007"))
			{
				field007 = controlField.getText();

				if(log.isDebugEnabled())
					log.debug("Found the value of the control field 007 to be " + field007 + ".");
			} // end if (007 found)

			// Initialize the 008 field if we found it
			else if(controlField.getAttribute("tag").getValue().equals("008"))
			{
				field008 = controlField.getText();

				if(log.isDebugEnabled())
					log.debug("Found the value of the control field 008 to be " + field008 + ".");
			} // end if (008 found)
		} // end loop over control fields
	} // end method initializeMarcControlFields

	/**
	 * Initializes the MARC XML data fields' cached values.
	 */
	@SuppressWarnings("unchecked")
	private void initializeMarcDataFields()
	{
		if(log.isDebugEnabled())
			log.debug("Initializing MARC XML data fields.");

		// Get the data fields
		List<Element> fields = marcXml.getRootElement().getChildren("datafield", marcNamespace);

		// Iterate over the fields and find the one with the correct tag
		for(Element field : fields)
		{
			String tag  = field.getAttributeValue("tag");

			// If the tag is "880", treat it like the tag from its $6 subfield
			if(tag.equals("880"))
			{
				List<String> realTags = getSubfieldOfField(field, '6');

				if(realTags.size() > 0)
					tag = realTags.get(0).substring(0, 3);

				for(String realTag : realTags)
				{
					String tagValue = realTag.substring(0, 3);

					if(!tagTo880s.containsKey(tagValue))
						tagTo880s.put(tagValue, new ArrayList<Element>());

					tagTo880s.get(tagValue).add(field);
				}
			}

			// If the current field is 020, get its subfields
			if(tag.equals("020"))
			{
				// Get the $a subfields
				List<String> subfields = getSubfieldValuesOfField(field, 'a');

				// Add the 020 $a subfields
				field020.addAll(subfields);
			} // end if (020 found)

			// If the current field is 035, get its subfields
			else if(tag.equals("035"))
				original035fields.add(field);

			// If the current field is 041, get its subfields
			else if(tag.equals("041"))
			{
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
			else if(tag.equals("100"))
			{
				// Get the $4 subfields
				List<String> subfields = getSubfieldValuesOfField(field, '4');

				// Set the 100 $4 value
				field100subfield4 = (subfields.size() > 0 ?subfields.get(0) : null);

				// Set the 100 field element
				field100element.add(field);
			} // end if (100 found)

			// If the current field is 110, get its subfields
			else if(tag.equals("110"))
			{
				// Get the $4 subfields
				List<String> subfields = getSubfieldValuesOfField(field, '4');

				// Set the 110 $4 value
				field110subfield4 = (subfields.size() > 0 ?subfields.get(0) : null);

				// Set the 110 field element
				field110element.add(field);
			} // end if (110 found)

			// If the current field is 111, get its subfields
			else if(tag.equals("111"))
			{
				// Get the $4 subfields
				List<String> subfields = getSubfieldValuesOfField(field, '4');

				// Set the 111 $4 value
				field111subfield4 = (subfields.size() > 0 ?subfields.get(0) : null);

				// Set the 111 field element
				field111element.add(field);
			} // end if (111 found)

			// If the current field is 130, get its subfields
			else if(tag.equals("130"))
			{
				// Get the $a subfields
				List<String> subfields = getSubfieldValuesOfField(field, 'a');

				// Set the 130 $a subfield
				field130 = (subfields.size() > 0 ?subfields.get(0) : null);
			} // end if (130 found)

			// If the current field is 240, get its subfields
			else if(tag.equals("240"))
			{
				// Get the $a subfields
				List<String> subfields = getSubfieldValuesOfField(field, 'a');

				// Set the 240 $a subfield
				field240 = (subfields.size() > 0 ?subfields.get(0) : null);
			} // end if (240 found)

			// If the current field is 243, get its subfields
			else if(tag.equals("243"))
			{
				// Get the $a subfields
				List<String> subfields = getSubfieldValuesOfField(field, 'a');

				// Set the 243 $a subfield
				field243 = (subfields.size() > 0 ?subfields.get(0) : null);
			} // end if (243 found)

			// If the current field is 245, get its subfields
			else if(tag.equals("245"))
			{
				// Get the $a subfields
				List<String> subfields = getSubfieldValuesOfField(field, 'a');

				// Set the 245 $a subfield
				field245 = (subfields.size() > 0 ?subfields.get(0) : null);
			} // end if (245 found)

			// If the current field is 502, get its subfields
			else if(tag.equals("502"))
			{
				// Get the $a subfields
				List<String> subfields = getSubfieldValuesOfField(field, 'a');

				// Add the 502 $a subfields
				field502.addAll(subfields);
			} // end if (502 found)

			// If the current field is 440, get its subfields
			else if(tag.equals("440"))
				field440elements.add(field);

			// If the current field is 600, get its subfields
			else if(tag.equals("600"))
				field600elements.add(field);

			// If the current field is 610, get its subfields
			else if(tag.equals("610"))
				field610elements.add(field);

			// If the current field is 611, get its subfields
			else if(tag.equals("611"))
				field611elements.add(field);

			// If the current field is 630, get its subfields
			else if(tag.equals("630"))
				field630elements.add(field);

			// If the current field is 650, get its subfields
			else if(tag.equals("650"))
				field650elements.add(field);

			// If the current field is 655, get its subfields
			else if(tag.equals("655"))
				field655elements.add(field);

			// If the current field is 700, get its subfields
			else if(tag.equals("700"))
				field700elements.add(field);

			// If the current field is 710, get its subfields
			else if(tag.equals("710"))
				field710elements.add(field);

			// If the current field is 711, get its subfields
			else if(tag.equals("711"))
				field711elements.add(field);

			// If the current field is 730, get its subfields
			else if(tag.equals("730"))
				field730elements.add(field);

			// If the current field is 800, get its subfields
			else if(tag.equals("800"))
				field800elements.add(field);

			// If the current field is 810, get its subfields
			else if(tag.equals("810"))
				field810elements.add(field);

			// If the current field is 811, get its subfields
			else if(tag.equals("811"))
				field811elements.add(field);

			// If the current field is 852, get its subfields
			else if(tag.equals("852"))
			{
				// Get the $b subfields
				List<String> subfields = getSubfieldValuesOfField(field, 'b');

				// Add the 852 $b subfields
				field852subfieldBs.addAll(subfields);
			} // end if (852 found)

			// If the current field is 945, get its subfields
			else if(tag.equals("945"))
			{
				// Get the $b subfields
				List<String> subfields = getSubfieldValuesOfField(field, 'l');

				// Add the 945 $l subfields
				field945subfieldLs.addAll(subfields);
			} // end if (945 found)
		} // end loop over data fields
	} // end method initializeMarcDataFields

	/**
	 * Adds a new datafield to the MARC XML record and returns the result.  The tag will have
	 * both of its indicaters empty and the $a subfield will be set to the specified value.
	 *
	 * @param tag The tag we're adding (i.e. 931)
	 * @param subfieldAValue The value of the $a subfield of the tag we're adding
	 */
	public void addMarcXmlField(String tag, String subfieldAValue)
	{
		addMarcXmlField(tag, subfieldAValue, null);
	}

	/**
	 * Adds a new datafield to the MARC XML record and returns the result.  The tag will have
	 * both of its indicaters empty and the $a subfield will be set to the specified value.
	 *
	 * @param tag The tag we're adding (i.e. 931)
	 * @param subfieldAValue The value of the $a subfield of the tag we're adding
	 * @param linkingField The value of the $8 subfield of the field we're adding, or null if we do not need a $8 subfield
	 */
	public void addMarcXmlField(String tag, String subfieldAValue, String linkingField)
	{
		if(log.isDebugEnabled())
			log.debug("Adding a new datafield to the MARC XML record with tag " + tag + " and value " + subfieldAValue + (linkingField == null ? "." : " with linking field " + linkingField + "."));

		// Add a MARC XML field with the specified tag
		// Both of its indicaters will be empty
		Element newFieldElement = new Element("datafield", marcNamespace);
		newFieldElement.setAttribute("tag", tag);
		newFieldElement.setAttribute("ind1", " ");
		newFieldElement.setAttribute("ind2", " ");

		// Add the $a subfield to the MARC XML field to the passed value
		Element newFieldASubfield = new Element("subfield", marcNamespace);
		newFieldASubfield.setAttribute("code", "a");
		newFieldASubfield.setText(subfieldAValue);

		// Add the $a subfield to the new datafield
		newFieldElement.addContent("\n\t").addContent(newFieldASubfield).addContent("\n");

		// Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
		if(tag.startsWith("9"))
		{
			// Add the $5 subfield to the new MARC XML field with the value of the user's organization code
			Element newField5Subfield = new Element("subfield", marcNamespace);
			newField5Subfield.setAttribute("code", "5");
			newField5Subfield.setText(organizationCode);

			// Add the $5 subfield to the new datafield
			newFieldElement.addContent("\t").addContent(newField5Subfield).addContent("\n");
		}

		// Add a $8 subfield with the specified linkingField  if it is not null
		if(linkingField != null)
		{
			// Add the $8 subfield to the new MARC XML field with the specified linking field
			Element newField8Subfield = new Element("subfield", marcNamespace);
			newField8Subfield.setAttribute("code", "8");
			newField8Subfield.setText(linkingField);

			// Add the $8 subfield to the new datafield
			newFieldElement.addContent("\t").addContent(newField8Subfield).addContent("\n");

			if(!usedLinkingFields.contains(linkingField))
				usedLinkingFields.add(linkingField);
		}

		// Add the new field to the end of the MARC XML if we didn't insert it already
		marcXml.getRootElement().addContent(newFieldElement).addContent("\n\n");

		// If we just added a language code, add it to the list of new language codes we're maintaining
		if(tag.equals(NormalizationServiceConstants.FIELD_9XX_LANGUAGE_SPLIT))
		{
			if(log.isDebugEnabled())
				log.debug("Added the language code " + subfieldAValue + ".");

			addedLanguageCodes.add(subfieldAValue.toLowerCase());
		} // end if (added language code)

		// If we just added a 243 element, cache the new element
		else if(tag.equals("243"))
			field243element.add(newFieldElement);
	} // end method addMarcXmlField

	/**
	 * Copies one marcXml tag into another marcXml tag
	 *
	 * @param copyFromTag The tag we're copying from
	 * @param copyToTag The tag we're copying into
	 */
	@SuppressWarnings("unchecked")
	public void copyMarcXmlField(String copyFromTag, String copyToTag, String subfieldsToCopy, String newInd1, String newInd2, boolean skipAPrefix)
	{
		if(log.isDebugEnabled())
			log.debug("Copying the MARC XML tag " + copyFromTag + " subfields " + subfieldsToCopy + " into the MARC XML tag " + copyToTag);

		// Get the data fields
		List<Element> fields = marcXml.getRootElement().getChildren("datafield", marcNamespace);

		// The copied field with the correct tag
		Element newField = null;

		// Iterate over the fields and find the one with the tag we're to copy from
		for(Element field : fields)
		{
			// If the current field is the correct tag, clone it into newField and change it's tag attribute to the correct value
			if(field.getAttribute("tag").getValue().equals(copyFromTag))
			{
				// Get the number of leading characters to skip from the 2nd indicator
				int skip = 0;
				try
				{
					skip = Integer.parseInt(field.getAttribute("ind2").getValue());
				}
				catch(NumberFormatException e)
				{

				}

				// Create the datafield for the copied tag, and set its attributes
				// The indicators should be the same as on the original field
				newField = new Element("datafield", marcNamespace);
				newField.setAttribute("tag", copyToTag);
				newField.setAttribute("ind1", newInd1);
				newField.setAttribute("ind2", newInd2);

				// Loop over the field's subfields and copy over the requested ones
				List<Element> subfields = field.getChildren("subfield", marcNamespace);
				for(Element subfield : subfields)
				{
					if(subfieldsToCopy.contains(subfield.getAttributeValue("code")))
					{
						newField.addContent("\n\t").addContent((Element)subfield.clone());

						if((skipAPrefix && skip > 0) && (subfield.getAttributeValue("code").equals("a")))
						{
							// Get the control fields
							List<Element> subfieldsOfNewfield = getSubfieldsOfField(newField, 'a');

							// Iterate over the subfields to find the target subfield
							for(Element subfieldOfNewfield : subfieldsOfNewfield)
							{
								// Get the current text of the subfield
								String currentText = subfieldOfNewfield.getText();

								subfieldOfNewfield.setText(currentText.substring(skip, skip+1).toUpperCase() + currentText.substring(skip+1));
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
		if(newField != null)
			marcXml.getRootElement().addContent(newField).addContent("\n\n");

		// If we created a new 240 tag as a result of the copy, save it
		if(copyToTag.equals("240") && field240element.size() == 0)
			field240element.add(newField);
	} // end method copyMarcXmlField

	/**
	 * Used to split the 4 categories of information contained in 6XX tags.
	 * If any of the required subfields exist in a target datafield, all
	 * subfields in that datafield are copied into the copy into field until the
	 * first occurrence of a required field.  In addition, the $2 is copied into the
	 * resulting field, even if it occurred after a stop field
	 *
	 * @param targetFields The fields which are to be split
	 * @param copyIntoField The field to copy the target fields into
	 * @param requiredSubfields At least one of these subfields must exist in a datafield for it to be split
	 *                          Nothing after or including these fields should be copied except for a $2
	 */
	@SuppressWarnings("unchecked")
	public void splitField(List<String> targetFields, String copyIntoField, String requiredSubfields)
	{
		if(log.isDebugEnabled())
			log.debug("Copying " + targetFields.size() + " MARC XML tags into the MARC XML tag " + copyIntoField + " using only subfields before " + requiredSubfields);

		// Get the data fields
		List<Element> fields = marcXml.getRootElement().getChildren("datafield", marcNamespace);

		// The copied field with the correct tag
		Element newField = null;

		// A list of the elements to add.  We can't add them in the loop or we'll get a ConcurrentModificationException
		ArrayList<Element> newFields = new ArrayList<Element>();

		// Iterate over the fields and find the one with the tag we're to copy from
		for(Element field : fields)
		{
			// If the current field is the correct tag, clone it into newField and change it's tag attribute to the correct value
			if(targetFields.contains(field.getAttribute("tag").getValue()))
			{
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
				for(Element subfield : subfields)
				{
					// If we hit a subfield we're supposed to split on, add a new field and then setup another one
					if(requiredSubfields.contains(subfield.getAttribute("code").getValue()))
					{
						// If the datafield contained a $2, copy that as well
						List<String> subfield2s = getSubfieldOfField(field, '2');

						if(subfield2s.size() > 0)
						{
							// Add the $5 subfield to the new MARC XML field with the value of the user's organization code
							Element newField2Subfield = new Element("subfield", marcNamespace);
							newField2Subfield.setAttribute("code", "2");
							newField2Subfield.setText(subfield2s.get(0));

							// Add the $2 subfield to the new datafield
							newField.addContent("\t").addContent(newField2Subfield).addContent("\n");
						}

						// Add the new field if we added any subfields to it
						if(newField.getChildren("subfield", marcNamespace).size() > 0)
						{
							// Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
							if(copyIntoField.startsWith("9"))
							{
								// Add the $5 subfield to the new MARC XML field with the value of the user's organization code
								Element newField5Subfield = new Element("subfield", marcNamespace);
								newField5Subfield.setAttribute("code", "5");
								newField5Subfield.setText(organizationCode);

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
		for(Element addMe : newFields)
			marcXml.getRootElement().addContent(addMe).addContent("\n\n");
	}

	/**
	 * Used to split the 4 categories of information contained in 6XX tags.
	 * Copies only the specified subfield into the copy into field, each into
	 * its own field.  In addition, the $2 is copied into the resulting fields
	 *
	 * @param targetFields The fields which are to be split
	 * @param copyIntoField The field to copy the target fields into
	 * @param copyOnlySubfield The subfield to copy
	 */
	@SuppressWarnings("unchecked")
	public void splitField(List<String> targetFields, String copyIntoField, char copyOnlySubfield)
	{
		if(log.isDebugEnabled())
			log.debug("Copying " + targetFields.size() + " MARC XML tags into the MARC XML tag " + copyIntoField + " using only subfield " + copyOnlySubfield);

		// Get the data fields
		List<Element> fields = marcXml.getRootElement().getChildren("datafield", marcNamespace);

		// The copied field with the correct tag
		Element newField = null;

		// A list of the elements to add.  We can't add them in the loop or we'll get a ConcurrentModificationException
		ArrayList<Element> newFields = new ArrayList<Element>();

		// Iterate over the fields and find the one with the tag we're to copy from
		for(Element field : fields)
		{
			// If the current field is the correct tag, clone it into newField and change it's tag attribute to the correct value
			if(targetFields.contains(field.getAttribute("tag").getValue()))
			{
				// Get the subfields
				List<String> subfields = getSubfieldOfField(field, copyOnlySubfield);

				// Get the $2 value, if there was one
				List<String> subfield2s = getSubfieldOfField(field, '2');
				String subfield2 = (subfield2s.size() > 0 ? subfield2s.get(0) : null);

				// Iterate over the subfields, and append each one to the subject display if it
				// is in the list of subfields to copy
				for(String subfield : subfields)
				{
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
					if(subfield2 != null && subfield2.length() > 0)
					{
						Element newField2Subfield = new Element("subfield", marcNamespace);
						newField2Subfield.setAttribute("code", "2");
						newField2Subfield.setText(subfield2);

						// Add the $2 subfield to the new datafield
						newField.addContent("\t").addContent(newField2Subfield).addContent("\n");
					}

					// Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
					if(copyIntoField.startsWith("9"))
					{
						// Add the $5 subfield to the new MARC XML field with the value of the user's organization code
						Element newField5Subfield = new Element("subfield", marcNamespace);
						newField5Subfield.setAttribute("code", "5");
						newField5Subfield.setText(organizationCode);

						// Add the $5 subfield to the new datafield
						newField.addContent("\t").addContent(newField5Subfield).addContent("\n");
					}

					newFields.add(newField);
				}
			} // end if (tag found)
		} // end loop over data fields

		// Add all the new elements
		for(Element addMe : newFields)
			marcXml.getRootElement().addContent(addMe).addContent("\n\n");
	}

	/**
	 * Used to pull name information out of 6XX tags. If the $t subfield exists in
	 * a target datafield, all subfields in that datafield are copied into the copy
	 * into field until the first occurrence of a $t field.  In addition, the $4
	 * is copied into the resulting field, even if it occurred after a $t
	 *
	 * @param targetFields The fields which are to be split
	 * @param copyIntoField The field to copy the target fields into
	 */
	@SuppressWarnings("unchecked")
	public void seperateNames(List<String> targetFields, String copyIntoField)
	{
		if(log.isDebugEnabled())
			log.debug("Copying " + targetFields.size() + " MARC XML tags into the MARC XML tag " + copyIntoField + " using only subfields before the $t.");

		// Get the data fields
		List<Element> fields = marcXml.getRootElement().getChildren("datafield", marcNamespace);

		// The copied field with the correct tag
		Element newField = null;

		// A list of the elements to add.  We can't add them in the loop or we'll get a ConcurrentModificationException
		ArrayList<Element> newFields = new ArrayList<Element>();

		// Iterate over the fields and find the one with the tag we're to copy from
		for(Element field : fields)
		{
			// If the current field is the correct tag, clone it into newField and change it's tag attribute to the correct value
			if(targetFields.contains(field.getAttribute("tag").getValue()) && field.getAttribute("ind2").getValue().equals("2"))
			{
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
				for(Element subfield : subfields)
				{
					// If the current subfield matches the required subfield, add the new field
					// and then setup the next one
					if(subfield.getAttributeValue("code").equals("t"))
					{
						// If the datafield contained a $4, copy that as well
						List<String> subfield4s = getSubfieldOfField(field, '4');

						if(subfield4s.size() > 0)
						{
							// Add the $5 subfield to the new MARC XML field with the value of the user's organization code
							Element newField4Subfield = new Element("subfield", marcNamespace);
							newField4Subfield.setAttribute("code", "4");
							newField4Subfield.setText(subfield4s.get(0));

							// Add the $2 subfield to the new datafield
							newField.addContent("\t").addContent(newField4Subfield).addContent("\n");
						}

						// Add the new field if we added any subfields to it
						if(newField.getChildren("subfield", marcNamespace).size() > 0)
						{
							// Add a $5 subfield with the user's organization code only if we're adding a 9XX datafield
							if(copyIntoField.startsWith("9"))
							{
								// Add the $5 subfield to the new MARC XML field with the value of the user's organization code
								Element newField5Subfield = new Element("subfield", marcNamespace);
								newField5Subfield.setAttribute("code", "5");
								newField5Subfield.setText(organizationCode);

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
						field.addContent("\t").addContent((Element)newField8Subfield.clone()).addContent("\n");

						if(!usedLinkingFields.contains(linkingField))
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
		for(Element addMe : newFields)
			marcXml.getRootElement().addContent(addMe).addContent("\n\n");
	}

	/**
	 * Removes duplicate values for MARC XML fields with a given tag.
	 * Two fields are considered duplicates if they have the same subfields
	 * in the same order with the same values.  If one or more duplicate
	 * fields are found with the specified tag, all but one of them are removed.
	 *
	 * @param tag The tag whose fields should be deduplicated.
	 */
	@SuppressWarnings("unchecked")
	public void deduplicateMarcXmlField(String tag)
	{
		// A list of values we've currently seen on target fields
		HashSet<String> currentValues = new HashSet<String>();

		// Get the data fields
		List<Element> fields = getDataFields(tag);

		// A list of elements to be removed as duplicates
		ArrayList<Element> toRemove = new ArrayList<Element>();

		// Iterate over the fields and find the one with the correct tag
		for(Element field : fields)
		{
			// A string which is unique for each distinct field
			StringBuilder value = new StringBuilder();

			// Get the subfields
			List<Element> subfields = field.getChildren("subfield", marcNamespace);

			// Get the 2nd indicator and append it to the field's value
			String ind2 = getIndicatorOfField(field, "2");
			value.append("ind2"+ind2);

			// Loop over each subfield and add the values of each to the datafield's value.
			for(Element subfield : subfields)
				value.append(subfield.getText().toLowerCase());

			// Remove an ending period since this should not effect whether or not fields are considered duplicates
			if(value.charAt(value.length()-1) == '.')
				value.setCharAt(value.length()-1, ' ');

			String valueStr = value.toString().trim();

			// If we've already seen the value of the current field, remove the
			// current field as it is a duplicate.  Otherwise, add its value to
			// the list of values we've seen.
			if(currentValues.contains(valueStr))
			{
				if(log.isDebugEnabled())
					log.debug("Removing duplicate " + tag + " field with value " + valueStr + ".");

				toRemove.add(field);
			}
			else
				currentValues.add(valueStr);
		} // end loop over data fields

		// Remove the fields we found to be duplicates
		for(Element removeMe : toRemove)
			marcXml.getRootElement().removeContent(removeMe);
	} // end method deduplicateMarcXmlField

	/**
	 * Removes duplicate values for MARC XML 959 fields.
	 * Two fields are considered duplicates if they have the same subfields
	 * in the same order with the same values.  If one or more duplicate
	 * fields are found with the specified tag, all but one of them are removed.
	 *
	 * 959 fields have a seperate dedup method since they have to handle linking fields
	 */
	@SuppressWarnings("unchecked")
	public void deduplicateMarcXml959Field()
	{
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
		for(Element field : fields)
		{
			// A string which is unique for each distinct field
			StringBuilder value = new StringBuilder();

			// Get the subfields
			List<Element> subfields = field.getChildren("subfield", marcNamespace);

			// Reset the values of the $8 subfields on the current field
			current8s.clear();

			// Get the 2nd indicator and append it to the field's value
			String ind2 = getIndicatorOfField(field, "2");
			value.append("ind2"+ind2);

			// Loop over each subfield and add the values of each to the datafield's value.
			// Don't include the $8 linking field.
			for(Element subfield : subfields)
			{
				if(!subfield.getAttribute("code").getValue().equals("8"))
					value.append(subfield.getText().toLowerCase());
				else
					current8s.add(subfield);
			}

			// Remove an ending period since this should not effect whether or not fields are considered duplicates
			if(value.charAt(value.length()-1) == '.')
				value.setCharAt(value.length()-1, ' ');

			String valueStr = value.toString().trim();

			// If we've already seen the value of the current field, remove the
			// current field as it is a duplicate.  Otherwise, add its value to
			// the list of values we've seen.
			if(currentValuesTo959.containsKey(valueStr))
			{
				if(log.isDebugEnabled())
					log.debug("Removing duplicate 959 field with value " + valueStr + ".");

				toRemove.add(field);

				Element duplicateOf = currentValuesTo959.get(valueStr);

				for(Element current8 : current8s)
					duplicateOf.addContent("\t").addContent(((Element)current8.clone())).addContent("\n");
			}
			else
				currentValuesTo959.put(valueStr, field);
		} // end loop over data fields

		// Remove the fields we found to be duplicates
		for(Element removeMe : toRemove)
			marcXml.getRootElement().removeContent(removeMe);
	} // end method deduplicateMarcXmlField

	/**
	 * Sets the value for a specified subfield of a specified field.  If the requested
	 * field does not exist this method will do nothing.  If there are more than one
	 * entry for the requested field, the subfield is set on the first one only.
	 *
	 * @param targetField The tag of the field to set the subfield for.
	 * @param targetSubfield The subfield to set.
	 * @param value The value to set the subfield to.
	 */
	public void setMarcXmlSubfield(String targetField, String targetSubfield, String value)
	{
		setMarcXmlSubfield(targetField, targetSubfield, value, null);
	}

	/**
	 * Sets the value for a specified subfield of a specified field.  If the requested
	 * field does not exist this method will do nothing.  If there are more than one
	 * entry for the requested field, the subfield is set on the first one only.
	 *
	 * @param targetField The tag of the field to set the subfield for.
	 * @param targetSubfield The subfield to set.
	 * @param value The value to set the subfield to.
	 * @param oldValue The old value of the field, or null if we don't care what the old value was
	 */
	@SuppressWarnings("unchecked")
	public void setMarcXmlSubfield(String targetField, String targetSubfield, String value, String oldValue)
	{
		if(log.isDebugEnabled())
			log.debug("Setting " + targetField + " $" + targetSubfield + " to " + value + (oldValue == null ? "." : " where the old value was " + oldValue + "."));

		// true only when we set the value
		boolean setValue = false;

		// Get the data fields
		List<Element> fields = marcXml.getRootElement().getChildren("datafield", marcNamespace);

		// The field we should add the subfield to
		Element addSubfieldToMe = null;

		// Iterate over the fields and find the one with the correct tag
		for(Element field : fields)
		{
			// If the current field is the target field, get its subfields
			if(field.getAttribute("tag").getValue().equals(targetField))
			{
				// Get the control fields
				List<Element> subfields = getSubfieldsOfField(field, targetSubfield.charAt(0));

				// Iterate over the subfields to find the target subfield
				for(Element subfield : subfields)
				{
					// Set the value of the subfield if it is the target subfield
					// If the oldValue was specified (not null,) only set the subfield if it's current value equals oldValue.
					if((oldValue == null || subfield.getText().equals(oldValue)))
					{
						if(log.isDebugEnabled())
							log.debug("Found " + targetField + " $" + targetSubfield + ", setting its value to " + value + ".");

						subfield.setText(value);

						setValue = true;
					} // end if (target subfield found)
				} // end loop over the target field's subfields

				// If we couldn't find the specified subfield, we need to add it to the current field.
				if(!setValue && oldValue == null)
				{
					addSubfieldToMe = field;
					break;
				}
			} // end if (the target field found)
		} // end loop over data fields

		// If the target subfield did not exist, add it to the target field
		if(addSubfieldToMe != null && !setValue)
		{
			if(log.isDebugEnabled())
				log.debug("Adding $" + targetSubfield + " to " + targetField + " with value " + value + ".");

			// Add the subfield to the field with the specified value
			Element newSubfield = new Element("subfield", marcNamespace);
			newSubfield.setAttribute("code", targetSubfield);
			newSubfield.setText(value);
			addSubfieldToMe.addContent("\t").addContent(newSubfield).addContent("\n\t");
		} // end if (the value wasn't set)
	} // end method setMarcXmlSubfield

	/**
	 * Sets the value for a specified subfield of a specified field.  If the requested
	 * field does not exist this method will do nothing.  If there are more than one
	 * entry for the requested field, the subfield is set on the nth one only.
	 *
	 * @param targetField The tag of the field to set the subfield for.
	 * @param targetSubfield The subfield to set.
	 * @param value The value to set the subfield to.
	 * @param n The index of the target subfield relative to the subfields matching the targetField tag
	 */
	@SuppressWarnings("unchecked")
	public void setMarcXmlSubfield(String targetField, String targetSubfield, String value, int n)
	{
		if(log.isDebugEnabled())
			log.debug("Setting " + targetField + " $" + targetSubfield + " to " + value + " where the index is " + n + ".");

		// true only when we set the value
		boolean setValue = false;

		// Get the data fields
		List<Element> fields = marcXml.getRootElement().getChildren("datafield", marcNamespace);

		// The field we should add the subfield to
		Element addSubfieldToMe = null;

		// The index of the next occurrence of the target field
		int occurrence = 0;

		// Iterate over the fields and find the one with the correct tag
		for(Element field : fields)
		{
			// If the current field is the target field, get its subfields
			if(field.getAttribute("tag").getValue().equals(targetField))
			{
				if(occurrence == n)
				{
					addSubfieldToMe = field;
					break;
				} // end if (the correct occurrence was found)
				else
					occurrence++;
			} // end if (the target field found)
		} // end loop over data fields

		// If the target subfield did not exist, add it to the target field
		if(addSubfieldToMe != null && !setValue)
		{
			if(log.isDebugEnabled())
				log.debug("Adding $" + targetSubfield + " to " + targetField + " with value " + value + ".");

			// Add the subfield to the field with the specified value
			Element newSubfield = new Element("subfield", marcNamespace);
			newSubfield.setAttribute("code", targetSubfield);
			newSubfield.setText(value);

			addSubfieldToMe.addContent("\t").addContent(newSubfield).addContent("\n\t");
		} // end if (the value wasn't set)
	} // end method setMarcXmlSubfield

	/**
	 * Given an Element containing a MARC XML datafield, return the value of the specified subfield of that Element
	 *
	 * @param datafield The Element we're getting the subfield of
	 * @param subfield The subfield to get
	 * @return The value of the requested subfield of the datafield
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getSubfieldsOfField(Element datafield, char subfield)
	{
		if(log.isDebugEnabled())
			log.debug("Getting the " + subfield + " of the passed datafield.");

		try
		{
			// An XPATH expression to get the requested subfields
			XPath xpath = XPath.newInstance("marc:subfield[@code='" + subfield + "']");
			xpath.addNamespace(marcNamespace);

			// Get the subfields.
			return xpath.selectNodes(datafield);
		}
		catch(JDOMException e)
		{
			log.error("An error occurred getting the $" + subfield + " subfields of the passed datafields.", e);
			return new ArrayList<Element>();
		}
	}

	/**
	 * Given an Element containing a MARC XML datafield, return the value of the specified subfield of that Element
	 *
	 * @param datafield The Element we're getting the subfield of
	 * @param subfield The subfield to get
	 * @return The value of the requested subfield of the datafield
	 */
	@SuppressWarnings("unchecked")
	public List<String> getSubfieldValuesOfField(Element datafield, char subfield)
	{
		if(log.isDebugEnabled())
			log.debug("Getting the " + subfield + " of the passed datafield.");

		// Holds the results
		ArrayList<String> results = new ArrayList<String>();

		try
		{
			// An XPATH expression to get the requested subfields
			XPath xpath = XPath.newInstance("marc:subfield[@code='" + subfield + "']");
			xpath.addNamespace(marcNamespace);

			// Get the subfields.
			List<Element> elements = xpath.selectNodes(datafield);

			// Return the empty list if there were no matching subfields
			if(elements.size() == 0)
			{
				if(log.isDebugEnabled())
					log.debug("The passed datafield did not have a $" + subfield + " subfield.");

				return results;
			}
			else
			{
				// Loop over the elements with the correct field and subfield, and add value of
				// each to the list of results
				for(Element element : elements)
				{
					// The value of the requested control field
					String value = element.getText();

					if(log.isDebugEnabled())
						log.debug("Found a $" + subfield + " subfield with a value of " + value + ".");

					results.add(value);
				}

				return results;
			}
		}
		catch(JDOMException e)
		{
			log.error("An error occurred getting the $" + subfield + " subfields of the passed datafields.", e);
			return results;
		}
	}

	/**
	 * Given an Element containing a MARC XML datafield, return the value of the specified indicator of that Element
	 *
	 * @param datafield The Element we're getting the indicator of
	 * @param indicator The indicator to get
	 * @return The value of the requested indicator of the datafield
	 */
	@SuppressWarnings("unchecked")
	public String getIndicatorOfField(Element datafield, String indicator)
	{
		if(log.isDebugEnabled())
			log.debug("Getting the ind" + indicator + " of the passed datafield.");

		try
		{
			// An XPATH expression to get the requested indicator
			XPath xpath = XPath.newInstance("@ind" + indicator);
			xpath.addNamespace(marcNamespace);

			// Get the subfields.
			List<Attribute> attributes = xpath.selectNodes(datafield);

			// Return the empty list if there were no matching subfields
			if(attributes.size() == 0)
			{
				if(log.isDebugEnabled())
					log.debug("The passed datafield did not have a ind" + indicator + ".");

				return null;
			}
			else
			{
				// The value of the requested control field
				String value = attributes.get(0).getValue();

				if(log.isDebugEnabled())
					log.debug("Found a ind" + indicator + " with a value of " + value + ".");

				return value;
			}
		}
		catch(JDOMException e)
		{
			log.error("An error occurred getting the ind" + indicator + " of the passed datafield.", e);
			return null;
		}
	}

	/**
	 * Gets all MARC XML data fields with a given tag
	 *
	 * @param targetField The tag of the data fields to retrieve (for example, "035")
	 * @return A list of all data fields with the requested tag
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getDataFields(String targetField)
	{
		if(log.isDebugEnabled())
			log.debug("Getting the " + targetField + " fields.");

		try
		{
			// An XPATH expression to get the requested control field
			XPath xpath = XPath.newInstance("//marc:datafield[@tag='" + targetField + "']");
			xpath.addNamespace(marcNamespace);

			// Get the data fields.  If the target field was not a 9xx field we can return the entire
			// list, otherwise we need to filter out those results with the wrong organization code.
			if(!targetField.startsWith("9"))
			{
				List<Element> results = xpath.selectNodes(marcXml);

				// Get the 880 fields that match the requested tag
				if(tagTo880s.containsKey(targetField))
					results.addAll(tagTo880s.get(targetField));

				return results;
			}
			else
			{
				List<Element> potentialResults = xpath.selectNodes(marcXml);
				ArrayList<Element> results = new ArrayList<Element>();

				// Get the 880 fields that match the requested tag
				if(tagTo880s.containsKey(targetField))
					potentialResults.addAll(tagTo880s.get(targetField));

				for(Element potentialResult : potentialResults)
					if(getSubfieldOfField(potentialResult, '5').contains(MSTConfiguration.getProperty(Constants.CONFIG_ORGANIZATION_CODE)))
						results.add(potentialResult);

				return results;
			}
		}
		catch(JDOMException e)
		{
			log.error("An error occurred getting the " + targetField + " fields.", e);
			return new ArrayList<Element>();
		}
	}

	/**
	 * Given an Element containing a MARC XML datafield, return the value of the specified subfield of that Element
	 *
	 * @param datafield The Element we're getting the subfield of
	 * @param subfield The subfield to get
	 * @return The value of the requested subfield of the datafield
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getSubfieldOfField(Element datafield, char subfield)
	{
		if(log.isDebugEnabled())
			log.debug("Getting the " + subfield + " of the passed datafield.");

		// Holds the results
		ArrayList<String> results = new ArrayList<String>();

		try
		{
			// An XPATH expression to get the requested subfields
			XPath xpath = XPath.newInstance("marc:subfield[@code='" + subfield + "']");
			xpath.addNamespace(marcNamespace);

			// Get the subfields.
			List<Element> elements = xpath.selectNodes(datafield);

			// Return the empty list if there were no matching subfields
			if(elements.size() == 0)
			{
				if(log.isDebugEnabled())
					log.debug("The passed datafield did not have a $" + subfield + " subfield.");

				return results;
			}
			else
			{
				// Loop over the elements with the correct field and subfield, and add value of
				// each to the list of results
				for(Element element : elements)
				{
					// The value of the requested control field
					String value = element.getText();

					if(log.isDebugEnabled())
						log.debug("Found a $" + subfield + " subfield with a value of " + value + ".");

					results.add(value);
				}

				return results;
			}
		}
		catch(JDOMException e)
		{
			log.error("An error occurred getting the $" + subfield + " subfields of the passed datafields.", e);
			return results;
		}
	}

	/**
	 * Gets all MARC XML datafields containing linking fields ($8 subfields for any tag)
	 *
	 * @return A list of all datafields containing linking fields in the MARCXML document
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getLinkingFieldsParents()
	{
		if(log.isDebugEnabled())
			log.debug("Getting the linking fields.");

		try
		{
			// An XPATH expression to get the linking field
			XPath xpath = XPath.newInstance("//marc:subfield[@code='8']/..");
			xpath.addNamespace(marcNamespace);

			// Get the linking fields.
			List<Element> results = xpath.selectNodes(marcXml);

			return results;
		}
		catch(JDOMException e)
		{
			log.error("An error occurred getting the linking fields.", e);
			return new ArrayList<Element>();
		}
	}

	/**
	 * Gets the value of a MARC XML control field
	 *
	 * @param targetField The control field to retrieve (for example, "008")
	 */
	@SuppressWarnings("unchecked")
	public void removeControlField(String targetField)
	{
		try
		{
			if(log.isDebugEnabled())
				log.debug("Removing the control field " + targetField);

			// An XPATH expression to get the requested control field
			XPath xpath = XPath.newInstance("//marc:controlfield[@tag='" + targetField + "']");
			xpath.addNamespace(marcNamespace);

			// Get the control field.  There should not be more than one Element in this list.
			List<Element> elements = xpath.selectNodes(marcXml);

			if(elements.size() == 0)
			{
				if(log.isDebugEnabled())
					log.debug("The " + targetField + " control field did not exist in the MARC XML record.");
			}
			else
			{
				// Remove the control field
				marcXml.getRootElement().removeContent(elements.get(0));

				if(targetField.equals("003"))
					field003 = null;
				else if(targetField.equals("001"))
					field001 = null;
				else if(targetField.equals("006"))
					field006 = null;
				else if(targetField.equals("007"))
					field007 = null;
				else if(targetField.equals("008"))
					field008 = null;

				if(log.isDebugEnabled())
					log.debug("Removed the " + targetField + " control field.");
			}
		}
		catch(JDOMException e)
		{
			log.error("An error occurred getting control field " + targetField);
		}
	}
} // end class MarcXmlManagerForNormalizationService
