/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.bo.service.Service;
import xc.mst.bo.service.ServiceHarvest;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.RecordService;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.XmlHelper;

public class SolrIndexService extends GenericMetadataService {

    private static final Logger LOG = Logger.getLogger(SolrIndexService.class);
    protected int loops = 1;

    Repository incomingRepository = null;
    protected int recordsProcessedSinceCommit;

    protected String name4progressBar = null;

//    private List<RegisteredData> m_recordRegisteredData = new ArrayList<RegisteredData>();

    public void init() {
        //TODO get these out of config file!
//        registerId("marc:controlfield 001", "id_001_key");   //register your 'important' identifiers for this service
//        registerId("marc:controlfield 004", "id_004_key");
//        registerId("marc:datafield 014$a",  "id_014_key");
        registerId("Title",                 "id_title_key"); //  note, for solr to recognize as dynamic field I've add _key
        registerId("Call Number",           "id_callnum_key");
        registerId("Creator/Contributor",   "id_author_key");
        registerId("Uplink",                "id_uplink_key");
    }

    public String getName4progressBar() {
        return name4progressBar;
    }

    @Override
    protected List<Record> getRecords(Repository repo, ServiceHarvest sh, Format inputFormat, Set inputSet) {
            TimingLogger.start("getRecordsWSets");
            List<Record> rs =
                ((DefaultRepository)repo).getRecordsWSets(sh.getFrom(), sh.getUntil(), sh.getHighestId(), null);
            TimingLogger.stop("getRecordsWSets");
            /*
            if (loops++ % 1000 == 0) {
                loops = 1;
                TimingLogger.reset();
            }
            */
            return rs;
    }

    @Override
    protected boolean commitIfNecessary(boolean force, long processedRecords) {
        if (force || this.recordsProcessedSinceCommit >=
                MSTConfiguration.getInstance().getPropertyAsInt("solr.records2commitAtOnce", 10000)) {
            try {
                TimingLogger.start("commitIndex");
                getSolrIndexManager().commitIndex();
                TimingLogger.stop("commitIndex");
                recordsProcessedSinceCommit=0;
                return true;
            } catch (Throwable t) {
                getUtil().throwIt(t);
            }
        }
        return false;
    }

    public void process(Repository repo, Format inputFormat, Set inputSet, Set outputSet) {
        this.incomingRepository = repo;
        this.preserveStatuses = false;
        super.process(repo, inputFormat, inputSet, outputSet);
    }

    public List<OutputRecord> process(InputRecord ri) {
        recordsProcessedSinceCommit++;
        this.name4progressBar = "indexing "+incomingRepository.getName();
        /*
        if (ri.getStatus() != Record.ACTIVE) {
            TimingLogger.start("deleteByQuery");
            getSolrIndexManager().deleteByQuery("record_id:"+ri.getId());
            TimingLogger.stop("deleteByQuery");
        } else {
        */
        TimingLogger.add(incomingRepository.getName(), 0);
        Record r = (Record)ri;
        LOG.debug("indexing record.getId(): "+r.getId());
        if (r.getId() % 1000 == 0) {
            LOG.debug("indexing record.getId(): "+r.getId());
        }
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(RecordService.FIELD_RECORD_ID, r.getId());
        r.setMode(Record.STRING_MODE);
        //doc.addField(RecordService.FIELD_ALL, r.getId()+" "+r.getOaiXml());
        if (r.getFormat() != null) {
            doc.addField(RecordService.FIELD_FORMAT_ID, r.getFormat().getId());
            doc.addField("format_name", r.getFormat().getName());
        } else {
            doc.addField(RecordService.FIELD_FORMAT_ID, 0);
        }

        if (r.getSets() != null) {
            for (Set s : r.getSets()) {
                doc.addField(RecordService.FIELD_SET_SPEC, s.getSetSpec());
                doc.addField(RecordService.FIELD_SET_NAME, s.getDisplayName());
            }
        }

        if (this.incomingRepository.getProvider() != null) {
            doc.addField(RecordService.FIELD_PROVIDER_ID, this.incomingRepository.getProvider().getId());
            doc.addField("provider_name", this.incomingRepository.getProvider().getName());
        } else {
            doc.addField(RecordService.FIELD_PROVIDER_ID, 0);
        }

        Service s = this.incomingRepository.getService();
        if (s != null) {
            doc.addField(RecordService.FIELD_SERVICE_ID, this.incomingRepository.getService().getId());
            doc.addField("service_name", this.incomingRepository.getService().getName());

            //TODO this is the place to ask for the name / value pairs, if you want an individual service to provide it (could be faster)
            // but, for now, this service will get the data itself.
        } else {

            doc.addField(RecordService.FIELD_SERVICE_ID, 0);
        }
        if (r.getStatus() == Record.ACTIVE) {
            doc.addField("status", "active");
        } else if (r.getStatus() == Record.HELD) {
            doc.addField("status", "held");
        } else if (r.getStatus() == Record.DELETED) {
            doc.addField("status", "deleted");
        }

        // type does not have to be defined, but for our purposes what else can we use to differentiate record types/patterns?
        // TODO get the format names out of the config file.
        //
        // IF we do not know the format here, will just have to do the blind xpath search.   BUT if we do know the format here,
        // we can save time.
        try {
            final Format marc21 = getFormatService().getFormatByName("marc21");
            final Format xc = getFormatService().getFormatByName("xc");
            final Format format = r.getFormat();
            final String type = r.getType();

            // if type is null, can getSets; records have info this injected into them.  repo data will have it too.
            // HMM: could look at set supported list but don't see how it helps me differentiate what to do with this record...yet.
            //if (r.getSets() != null) {
            //    for (Set set : r.getSets()) {
            //    }
            //}
            if (format.equals(xc)) {
                if (type != null) {
                    if (type.equals("w")) {
                        addFieldToIndex(ri, doc, "//rdvocab:titleOfWork", "id_title_key", false);
                        addFieldToIndex(ri, doc, "//xc:creator", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:compiler", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:composer", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:speaker", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//dcterms:creator", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:director", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:producer", "id_author_key", true);
                    }
                    if (type.equals("e")) {
                        addFieldToIndex(ri, doc, "//xc:workExpressed", "id_uplink_key", true);
                        //TODO either add to stopwords or strip extraneous junk out of uplink,
                        // TODO i.e. oai:msg.rochester.edu:MetadataServicesToolkit/marctoxctransformation/
                        // TODO is all unneeded.
                        addFieldToIndex(ri, doc, "//xc:titleOfExpression", "id_title_key", false);
                        addFieldToIndex(ri, doc, "//xc:contributor", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:director", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:editor", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:illustrator", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:performer", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:producer", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//rdarole:translator", "id_author_key", true);
                    }
                    if (type.equals("m")) {
                        addFieldToIndex(ri, doc, "//xc:expressionManifested", "id_uplink_key", true);
                        //TODO either add to stopwords or strip extraneous junk out of uplink,
                        // TODO i.e. oai:msg.rochester.edu:MetadataServicesToolkit/marctoxctransformation/
                        // TODO is all unneeded.
                        addFieldToIndex(ri, doc, "//dcterms:title", "id_title_key", false);
                    }
                    if (type.equals("h")) {
                        addFieldToIndex(ri, doc, "//xc:manifestationHeld", "id_uplink_key", true);
                        //TODO either add to stopwords or strip extraneous junk out of uplink,
                        // TODO i.e. oai:msg.rochester.edu:MetadataServicesToolkit/marctoxctransformation/
                        // TODO is all unneeded.
                        addFieldToIndex(ri, doc, "//xc:callNumber", "id_callnum_key", false);
                    }
                }
            }
            else if (r.getFormat().equals(marc21)) {
                // get identifiers that could be present in ANY marc21 record
                addFieldToIndex(ri, doc, "//marc:controlfield[@tag='001']", "id_uplink_key", false);

                if (type != null) {
                    // get identifiers that could be present only in a bib marc21 record
                    if (type.equals("b")) {
                        LOG.debug("*** SolrIndexService, found a Marc, type=" +type);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='245']/marc:subfield[@code='a']", "id_title_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='245']/marc:subfield[@code='b']", "id_title_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='a']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='b']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='c']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='d']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='a']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='b']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='c']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='d']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='a']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='c']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='d']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='a']", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='b']", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='c']", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='d']", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='a']", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='b']", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='c']", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='d']", "id_author_key", true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='a']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='c']", "id_author_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='d']", "id_author_key", false);
                    }
                    // get identifiers that could be present only in a holdings marc21 record
                    else if (type.equals("h")) {
                        LOG.debug("*** SolrIndexService, found a Marc, type=" +type);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='014']/marc:subfield[@code='a']", "id_uplink_key", true);
                        addFieldToIndex(ri, doc, "//marc:controlfield[@tag='004']", "id_uplink_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='852']/marc:subfield[@code='h']", "id_callnum_key", false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='852']/marc:subfield[@code='i']", "id_callnum_key", false);
                    }
                }
            }
            else {
                // just do it. for now it == the complete set of marc parsers but in future it could encompass new/unknown formats,
                // fed from config file.
                addFieldToIndex(ri, doc, "//marc:controlfield[@tag='001']", "id_uplink_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='245']/marc:subfield[@code='a']", "id_title_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='245']/marc:subfield[@code='b']", "id_title_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='a']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='b']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='c']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='d']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='a']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='b']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='c']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='d']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='a']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='c']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='d']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='a']", "id_author_key", true);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='b']", "id_author_key", true);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='c']", "id_author_key", true);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='d']", "id_author_key", true);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='a']", "id_author_key", true);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='b']", "id_author_key", true);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='c']", "id_author_key", true);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='d']", "id_author_key", true);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='a']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='c']", "id_author_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='d']", "id_author_key", false);

                addFieldToIndex(ri, doc, "//marc:datafield[@tag='014']/marc:subfield[@code='a']", "id_uplink_key", true);
                addFieldToIndex(ri, doc, "//marc:controlfield[@tag='004']", "id_uplink_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='852']/marc:subfield[@code='h']", "id_callnum_key", false);
                addFieldToIndex(ri, doc, "//marc:datafield[@tag='852']/marc:subfield[@code='i']", "id_callnum_key", false);
            }
        } catch (DatabaseConfigException e) {
            LOG.error("Could not connect to the database with the parameters in the configuration file.", e);
        }

        if (r.getMessages() != null) {
            for (RecordMessage m : r.getMessages()) {
                try {
                    LOG.debug("m: "+m);
                    // TODO not necessarily type ERROR anymore
                    doc.addField(RecordService.FIELD_ERROR,
                            m.getMessage());  // used to display id here prepended to msg text.
                } catch (Throwable t) {
                    LOG.error("continuing, but logging", t);
                }
            }
        }

        try {
            getSolrIndexManager().addDoc(doc);
        } catch (IndexException ie) {
            throw new RuntimeException(ie);
        }
        return null;
    }

    private void addFieldToIndex(InputRecord ri, SolrInputDocument doc, String xpStr, String key, boolean repeats) {
        ri.setMode(Record.STRING_MODE);
        List<Element> elements = getFieldValue(xpStr, ri.getOaiXml());
        if (elements != null) {
            if (repeats) {
                for (Element element: elements) {
                    LOG.debug("*** SolrIndexService, adding ident: "+ key +" add data:"+element.getText());
                    doc.addField(key, element.getText());
                }
            }
            else {
                doc.addField(key, elements.get(0).getText());
            }
        }
        // m_recordRegisteredData.add(new RegisteredData("id_title_key", elements.get(0).getText()));
    }

    /**
     * Gets the value of a MARC XML field
     *
     * @param targetField
     *            The field to retrieve (for example, "008")
     * @return The value of the requested field
     */
    @SuppressWarnings("unchecked")
    private List<Element> getFieldValue(String xpathStr, String xml) {
        try {
            //TODO change this over to sax, when I do method signature probably will change to return list of Strings vs. Elements
            Namespace marcNamespace = Namespace.getNamespace("marc", "http://www.loc.gov/MARC21/slim");
            List<Element> elements = null;
            XPath xpath = XPath.newInstance(xpathStr);
            xpath.addNamespace(marcNamespace);

            // Get the control field. There should not be more than one Element in this list.
            // start with dom, move to sax.
            org.jdom.Document doc = new XmlHelper().getJDomDocument(xml);

            elements = xpath.selectNodes(doc);

            if (elements.size() == 0) {
                if (LOG.isDebugEnabled())
                    LOG.debug("The " + xpathStr + " field did not exist in the MARC XML record.");
                return null;
            } else {
                // The value of the requested field
                String value = elements.get(0).getText();

                if (LOG.isDebugEnabled())
                    LOG.debug("The " + xpathStr + " field had a value of " + value + ".");
                return elements;
            }
        } catch (JDOMException e) {
            LOG.error("An error occurred getting field " + xpathStr);
            return null;
        }
    }


    public void runTests() {
        //do nothing
    }
    protected void processStatusDisplay(Repository repo, Format inputFormat, Set inputSet, Set outputSet) {}
}
