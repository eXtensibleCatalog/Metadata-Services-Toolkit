package xc.mst.services.marcaggregation;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.bo.record.marc.Field;
import xc.mst.manager.BaseService;
import xc.mst.repo.Repository;

/**
 * This is a collection of methods used by the MARC Aggregation Service, MAS, to manipulate
 * MARC21 xml files.
 *
 * Various methods are used to generate static and dynamic content for bibs and holdings.
 *
 * By extending BaseService, it is auto-created by Spring as a singleton bean
 * @author John Brand
 *
 */
public class MASMarcBuilder extends BaseService{

    private static final Logger LOG               = Logger.getLogger(MASMarcBuilder.class);


    /**
     * we KNOW what data fields we will be getting,
     * so specifically look for those in the desired order, and build the block of dynamic data.
     *
     * @param oaiXml
     * @param dynamic
     * @return
     */
    protected String updateDynamicRecordWithStaticContent(String oaiXml, Map<Integer, Set<MarcDatafieldHolder>> dynamic) {
        String dynData = getDynamicDataBlock(dynamic);
        //  LOG.info("DYNAMIC DATA:");
        //  LOG.info(dynData);

        oaiXml = insertTextAfter008(oaiXml, dynData);
        //LOG.info("DATA WITH DYNAMIC DATA:");
        //LOG.info(oaiXml);
        return oaiXml;
    }

    protected String update005(String xml, Transformer __005_Transformer) {
        xml = getXmlMinus005(xml, __005_Transformer);
        String _005 = getControlField005();
        xml = insertTextAfter008(xml, _005);
        return xml;
    }

    /**
     * transform the given xml by stripping 001's,003's, 010,020's,022's,024's & 035's
     * using an xsl to do the transformation.
     * The 035 fields are stripped then added back in because we only want well-formed 035's with prefix.
     * Contrast this to other keep fields that we may leave in the record of source.
     *
     * @param oaiXml
     * @param _static_Transformer
     * @return
     */
    protected String getStaticBase(String oaiXml, Transformer _staticTransformer) {
        return getTransformedXml(oaiXml, _staticTransformer);
    }

    /**
     * transform the given xml by stripping 004's & 014's
     * using an xsl to do the transformation.
     *
     *  This is in prep for adding in fields based on 004/014 later
     *  (so need to saved that info before now! )
     *
     * Turns out we DO NOT strip 004/014's.  Leaving this as a placeholder
     * in case it turns out later we do need to transform the holding record
     * in some way.
     *
     * @param oaiXml
     * @param _holding_Transformer
     * @return
     */
    protected String getHoldingBase(String oaiXml, Transformer _holdingTransformer) {
        return getTransformedXml(oaiXml, _holdingTransformer);
    }

    /**
     * original plan: got to figure out correctly what OAI ID currently represents the successor that this holding should link to:
     *
     * For every holdings record, whether or not its parent bibliographic record matches on and is merged with
     * another record, the Service generates one or more new 904 "XC Uplink" fields in each Output Holdings
     * record. This 904 field contains, in $a, the OAI ID for the Output parent record; that is, for the successor
     * to the record represented in the input Holdings record's 004 field.
     *
     * New plan - no 904's just pass through holdings.
     *
     * @param r
     * @param smr
     * @param repo
     * @return
     */
    protected StringBuilder add904toHolding(InputRecord r, SaxMarcXmlRecord smr, Repository repo) {
        String _004 = smr.getControlField(4);

        String dynamic = getDynamicHoldingContent(repo, r.getId());
        StringBuilder sb ;

        // now insert the dynamic block into the correct spot in oaiXml,
        // it goes before the end tag, </marc:record>
        // find:            </marc:record>
        //
        final String regex = "</marc:record>";
        sb = new StringBuilder(insertDynamicAtBegin(r.getOaiXml(), dynamic, regex));
        return sb;
    }

    /**
     * getDynamic => create 035 from 001/003, save existing 035's, save existing 010,020,022,024
     *   returns dynamic content
     *
     *  dynamic:
     *  record_id ->  {{035$a list}, {010 list}, etc.}
     *
     *  need to pass to a method that gets static content and dynamic content and builds a list of it.
        Grab the 5 fields below for all of the records that match,
        then dedup  fields with identical content.

        035 - just $a, and only when the field is properly formatted with a prefix in parens followed by the number
        010 - just $a  (1 and only 1 allowed, prefer the record of source version, else grab any other found in the match set)
        020 - just $a (which may contain more than just the ISBN - it may contain additional text, like " (v. 1)" or "paperback")
        022 - In addition to $a, also $ l [lowercase L], $m, and $z.
              The rationale here is that rather than lumping "invalid" and "incorrect" into the $z,
              in this case the "invalid" ones have their own subfield codes.
              These may be useful in some cases.  Also, a new kind of ISSN, called the "Linking ISSN" has just been defined in $l,
              and we may want to do something more with that someday so we need to keep it in the output record.
        024 - just $a.

        Copy all of this to the output record even when not from the record of source.

        By using TreeSet to hold the data, and with an appropriate 'Comparable' for each MarcDatafieldHolder 'subtype', can easily
        and effectively dedup the data.
     *
     * @param recordOfSource
     * @param repo
     * @param set
     * @return
     */
    protected Map<Integer, Set<MarcDatafieldHolder>> getDynamicContent(Long recordOfSource, Repository repo, Set<Long> set) {

        Map<Integer, Set<MarcDatafieldHolder>> dynamic = new HashMap<Integer, Set<MarcDatafieldHolder>>();
        TreeSet<MarcDatafieldHolder> fields35 = new TreeSet<MarcDatafieldHolder>();
        TreeSet<MarcDatafieldHolder> fields10 = new TreeSet<MarcDatafieldHolder>();
        TreeSet<MarcDatafieldHolder> fields20 = new TreeSet<MarcDatafieldHolder>();
        TreeSet<MarcDatafieldHolder> fields22 = new TreeSet<MarcDatafieldHolder>();
        TreeSet<MarcDatafieldHolder> fields24 = new TreeSet<MarcDatafieldHolder>();

        ArrayList<Character> charListA  = new ArrayList<Character>();
        ArrayList<Character> charList22 = new ArrayList<Character>();
        ArrayList<Character> charList24 = new ArrayList<Character>();

        // for most we just want the values from the $a subfield
        charListA.add(new Character('a'));

        // but for datafield 022 we want the values from more subfields:
        charList22.add(new Character('a'));
        charList22.add(new Character('l'));
        charList22.add(new Character('m'));
        charList22.add(new Character('z'));

        // & for datafield 024 we want the values from 1 more subfield:
        charList24.add(new Character('a'));
        charList24.add(new Character('2'));

        for (Long num: set) {
            String oai = repo.getRecord(num).getOaiXml();

            // make an 035 out of 001/003
            String _035 = create035(oai);
            if (_035 != null) {
                MarcSubfieldHolder subfield = new MarcSubfieldHolder('a', _035);
                List<MarcSubfieldHolder> subfields = new ArrayList<MarcSubfieldHolder>();
                subfields.add(subfield);
                fields35.add(new MarcDatafieldHolder("035",subfields," ", " "));
            }
            // add 035 data already in the documents, but it must be well-formed.
            // The getDynamicField method makes sure it is.
            fields35.addAll(getDynamicDatafield(oai, 35, "035", charListA));  // add 035$a data
        }
        dynamic.put(35, fields35);

        // can have 1 and only 1 010, and prefer it to be from record of source.
        String _oai = repo.getRecord(recordOfSource).getOaiXml();
        fields10.addAll(getDynamicDatafield(_oai,10, "010",charListA));
        if (fields10.size() < 1) {

            for (Long num: set) {
                // now get first 010$a you can find from all non-record of source in the match set
                if (num != recordOfSource) {
                    String oai = repo.getRecord(num).getOaiXml();
                    fields10.addAll(getDynamicDatafield(oai,10, "010",charListA));
                    if (fields10.size() > 0) {
                        continue;
                    }
                }
            }
        }
        dynamic.put(10, fields10);

        for (Long num: set) {
            // now get 020$a from all in the match set
            String oai = repo.getRecord(num).getOaiXml();
            fields20.addAll(getDynamicDatafield(oai,20, "020", charListA));
        }
        dynamic.put(20, fields20);

        for (Long num: set) {
            // now get 024$a2 from all in the match set
            String oai = repo.getRecord(num).getOaiXml();
            fields24.addAll(getDynamicDatafield(oai,24, "024", charList24));
        }
        dynamic.put(24, fields24);

        for (Long num: set) {
            // now get 022$almz from all in the match set
            String oai = repo.getRecord(num).getOaiXml();
            fields22.addAll(getDynamicDatafield(oai,22, "022", charList22));
        }
        dynamic.put(22, fields22);

        return dynamic;
    }

    /**
     * getDynamicHoldingContent => create 904 from 004/014, don't worry about existing 904,
     * we are not trimming anything from existing record.
     *   returns content to dynamically insert into holding records
     *
     * @param repo
     * @param num
     * @return
     */
    protected String getDynamicHoldingContent(Repository repo, Long num) {
        String oai = repo.getRecord(num).getOaiXml();
        String _904 = create904(oai);   // the block of xml

        return _904;
    }


    private String insertTextAfter008(String oaiXml, String dynData) {
        // now insert the dynamic block into the correct spot in oaiXml,
        // it goes after the 008!
        final String regex = "controlfield tag=\"008\".*/marc:controlfield>";
        oaiXml=insertDynamicAtEnd(oaiXml, dynData, regex);
        return oaiXml;
    }

    /**
     * Use a regular expression to search for a pattern.  Insert 'dynamic' right after the end
     * of the found match. If match not found insert at end.
     */
    private String insertDynamicAtEnd(String inputXml, String dynamic, String regex) {
        //
        // Create a Pattern instance
        //
        Pattern pattern = Pattern.compile(regex);

        //
        // Create matcher object
        //
        Matcher matcher = pattern.matcher(inputXml);
        StringBuffer sb = new StringBuffer(inputXml);

        //
        // Find where to place the text that match the pattern (at end of pattern matching text)
        //
        int end = -1;
        if (matcher.find()) {
            end = matcher.end();
            sb.insert(end, dynamic);
        }
        else {
            LOG.error("*** Could not find controlfield tag="+regex+".  Placed dynamic data at end of record!");
            sb.append(dynamic);
        }
        return sb.toString();
    }

    /**
     * Use a regular expression to search for a pattern.  Insert 'dynamic' right before the
     *  found match. If match not found insert at end.
     *  (at end would really would be an error, after end of </record>)
     */
    private String insertDynamicAtBegin(String inputXml, String dynamic, String regex) {
        //
        // Create a Pattern instance
        //
        Pattern pattern = Pattern.compile(regex);

        //
        // Create matcher object
        //
        Matcher matcher = pattern.matcher(inputXml);
        StringBuffer sb = new StringBuffer(inputXml);

        //
        // Find where to place the text that match the pattern (at end of pattern matching text)
        //
        int begin = -1;
        if (matcher.find()) {
            begin = matcher.start();
            sb.insert(begin, dynamic);
        }
        else {
            LOG.error("*** Could not find controlfield tag="+regex+".  Placed dynamic data at end of record!");
            sb.append(dynamic);
        }
        return sb.toString();
    }

    /**
     * we KNOW what data fields we will be getting, 10,20,22,24,35.
     * so specifically look for those in the desired order, and build the block of dynamic data.
     *
     */
    private String getDynamicDataBlock(Map<Integer, Set<MarcDatafieldHolder>> dynamic) {

        StringBuilder results = new StringBuilder(System.getProperty("line.separator"));

        results.append(getControlField005());
        results.append(getDynamicPiece(dynamic.get(10)));
        results.append(getDynamicPiece(dynamic.get(20)));
        results.append(getDynamicPiece(dynamic.get(22)));
        results.append(getDynamicPiece(dynamic.get(24)));
        results.append(getDynamicPiece(dynamic.get(35)));

        return results.toString();
    }

    /**
     *
     * @return newly generated 005
     */
    private String getControlField005() {
        String new005 = getUtil().printDateTimeISO8601(new Date());
        return "<marc:controlfield tag=\"005\">" + new005 + "</marc:controlfield>"+ System.getProperty("line.separator");
    }

    /**
     * iterate over the set of found fields.  For each field in the set, append the xml string
     * constructed to represent it.
     *
     * @param dynamic the set of encapsulated data field objects
     * @return
     */
    private String getDynamicPiece(Set<MarcDatafieldHolder> dynamic) {

        Iterator<MarcDatafieldHolder> _i = dynamic.iterator();
        StringBuilder results = new StringBuilder();

        while (_i.hasNext()) {
            results.append(_i.next().toString());
        }

        return results.toString();
    }

    /**
     * transform the given xml by stripping 005's using an xsl to do the transformation.
     *
     * @param oaiXml
     * @param __005_Transformer
     * @return
     */
    private String getXmlMinus005(String oaiXml, Transformer __005_Transformer) {
        return getTransformedXml(oaiXml, __005_Transformer);
    }

    /**
     * run a sax transformation
     * @param oaiXml
     * @param transformer
     * @return
     */
    private String getTransformedXml(String oaiXml, Transformer transformer) {
        try {
            // Use the parser as a SAX source for input
            MASSaxMarcXmlRecord record = new MASSaxMarcXmlRecord(oaiXml);
            InputSource inputSource = new InputSource(new StringReader(oaiXml));

            SAXSource source = new SAXSource(record.getXmlReader(), inputSource);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            transformer.transform(source, result);

            oaiXml = sw.getBuffer().toString();
            return  oaiXml;
        } catch (Throwable t) {
            LOG.error("", t);
        }
        return  oaiXml;
    }

    /**
     * package the found Marc datafields into a list of MarcDatafieldHolder, an encapsulating class that collects
     * ind1, ind2, datafield name, and subfields into one class.
     *
     * @param oaiXml
     * @param fieldNum used to retrieve the datafields from the oaiXml
     * @param fieldName string representation - need it to pass it to MarcDatafieldHolder
     * @param subfieldC - list of character subfields of interest
     * @return
     */
    private List<MarcDatafieldHolder> getDynamicDatafield(String oaiXml, int fieldNum, String fieldName, List<Character> subfieldC) {
        SaxMarcXmlRecord smr = new SaxMarcXmlRecord(oaiXml);
        List<Field> fields = smr.getDataFields(fieldNum);
        List<MarcDatafieldHolder> marcFields = new ArrayList<MarcDatafieldHolder>();

        for (Field field : fields) {
            ArrayList<MarcSubfieldHolder> _flds = new ArrayList<MarcSubfieldHolder>();
            char ind1 = field.getInd1();
            char ind2 = field.getInd2();
            for (Character c: subfieldC) {
                List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, c);
                for (String subfield : subfields) {
                    //
                    // ignore all 035's with no prefix.
                    //
                     if (subfield != null) {
                        if (fieldNum == 35) {
                            if (subfield.trim().contains("(")) {
                                _flds.add(new MarcSubfieldHolder(new Character(c),subfield));
                                LOG.debug("add 035:"+subfield+"<=");
                            }
                            else {
                                LOG.debug("do not add 035:"+subfield+"<=");
                            }
                        }
                        else {
                            _flds.add(new MarcSubfieldHolder(new Character(c),subfield));
                        }
                    }
                }
            }
            marcFields.add(new MarcDatafieldHolder(fieldName,_flds,
                    new Character(ind1).toString(),new Character(ind2).toString()));
        }
        return marcFields;
    }

    /**
     * The 001 and 003 from BOTH the Selected Record and the Non-Selected Record will be used to create separate,
     * new 035 fields in the Output Record,
     * with the value of the 003 (institution code) set as the prefix for the control number in $a, enclosed in parens.
     * The number from the 001 will follow the parens without a space.
     * @param oaiXml
     * @return can return null, so check for it!
     *         if it returns a string, it will be subfield contents of the form:
     *         (OCoLC)1788884
     */
    private String create035(String oaiXml) {
        SaxMarcXmlRecord smr = new SaxMarcXmlRecord(oaiXml);
        String org = smr.getOrgCode();
        if (org != null) {
            org = org.trim();
        }
        else {
            LOG.debug("no org code found in "+smr.recordId);
            return null;
        }
        String _001 = smr.getControlField(1);
        if (_001 != null) {
            _001 = _001.trim();
        }
        else {
            LOG.debug("no _001 code found in "+smr.recordId);
            return null;
        }
        StringBuilder sb = new StringBuilder("(").append(org).append(")").append(_001);
        return sb.toString();
    }

    /**
     *  Unused.  Leaving it in place for now in case we change our minds.
     *
        No matching or merging is done on MARC Holdings records. Therefore, the content of an Output
        holdings record will be identical to the content of the Input record, with the following exceptions:

        For every holdings record, whether or not its parent bibliographic record matches on and is merged with
        another record, the Service generates one or more new 904 "XC Uplink" fields in each Output Holdings
        record. This 904 field contains, in $a, the OAI ID for the Output parent record; that is, for the successor
        to the record represented in the input Holdings record's 004 field. In addition, the 904 field contains $1
        NyRoXCO to identify it as a field created by an XC service.

        The Service generates additional 904 fields to contain links to the successors to additional linked
        bibliographic records linked through 014 fields in the input records ("bound withs") if the following
        conditions are met:

        014 first indicator value=1
        If an 014 $b is present, the code matches that in the 003 of the incoming holdings record

        The Transformation Service will use these 904 fields as "uplinks" instead of using 004 or 014 fields
        in the MARC record to create the "uplinks" between XC Holdings and XC Manifestation records. These
        additional 904 fields will contain the same subfields ($a, $1) as 904 fields created from 004 fields.
     * @param oaiXml
     * @return can return null, so check for it!
     */
    private String create904(String oaiXml) {
        Marc904Generator _904generator = null;
        SaxMarcXmlRecord smr = new SaxMarcXmlRecord(oaiXml);
        String _003 = smr.getControlField(3);
        if (_003 != null) {
            _003 = _003.trim();
        }
        else {
            LOG.error("no _003 code found in "+smr.recordId);
            throw new RuntimeException("no _003 code found in "+smr.recordId);
        }
        String _004 = smr.getControlField(4);
        if (_004 != null) {
            _004 = _004.trim();
        }
        else {
            LOG.error("no _004 code found in "+smr.recordId);
        }
        List<Marc014Holder> marcFields = new ArrayList<Marc014Holder>();

        // Get dataField with tag=014
        List<Field> dataFields = smr.getDataFields(14);
        // Loop through the 014 - note, there can be >1

        for (Field field : dataFields) {
            char ind1 = field.getInd1();
            // 014$a and 014$b do not repeat.  if there is no 014$a, do not continue with parsing.
            List<String> subfields = SaxMarcXmlRecord.getSubfieldOfField(field, 'a');
            if (subfields.size()<1) {
                continue;
            }
            else {
                String subfield = subfields.get(0);   // only 1 subfield $a
                MarcSubfieldHolder subfieldA = new MarcSubfieldHolder(new Character('a'),subfield);
                MarcSubfieldHolder subfieldB = null;
                List<String> subfieldsB = SaxMarcXmlRecord.getSubfieldOfField(field, 'b');
                if (subfieldsB.size()>0) {
                    subfieldB = new MarcSubfieldHolder(new Character('b'),subfield);
                }
                marcFields.add(new Marc014Holder(subfieldA, subfieldB, new Character(ind1).toString()));
            }
        }
        if (_004 == null && marcFields.size() < 1) {
            throw new RuntimeException("no 004's or 014's found in "+smr.recordId);
        }
        else {
            _904generator = new Marc904Generator(_003, _004, marcFields);
        }
        // this will dedup and stringify
        List<String> _904s = _904generator.get904s();

        // create the xml
        Marc904Holder holder = new Marc904Holder(_904s);
        return holder.toString();
    }

}
