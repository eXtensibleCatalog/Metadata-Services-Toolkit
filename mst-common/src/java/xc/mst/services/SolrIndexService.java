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
import java.util.Arrays;
import java.util.Collections;
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
        //  note, for solr to recognize as dynamic field I've add _key,
        //  see solr's schema.xml for all the dynamic fields you can choose from.
        registerId("Identifier",            "id_identifier_key");
        registerId(" - Record_ID",          "record_id_l");
        registerId(" - dcterms_ident_manifestation", "id_manDCTermsIdent_key");
        registerId(" - dcterms_ident_holdings", "id_holdDCTermsIdent_key");
        registerId(" - marc:controlfield 001", "id_001_key");  // can be alphanumeric
        registerId("Title",                 "id_title_key");
        registerId(" - rdvocab:titleOfWork", "rdvocab_titleOfWork_key");
        registerId(" - xc:titleOfExpression", "xc_titleOfExpression_key");
        registerId(" - dcterms:title", "dcterms_title_key");
        registerId(" - marc:datafield 245$a", "id_245a_key");
        registerId(" - marc:datafield 245$b", "id_245b_key");
        registerId("Call Number",           "id_callnum_key");
        registerId(" - xc:callNumber", "xc_callnum_key");
        registerId(" - marc:datafield 852$h", "id_852h_key");
        registerId(" - marc:datafield 852$i", "id_852i_key");
        registerId("Creator/Contributor",   "id_author_key");
        registerId(" - xc:contributor", "xc_contributor_key");
        registerId(" - xc:creator",   "xc_creator_key");
        registerId(" - dcterms:creator", "dcterms_creator_key");
        registerId(" - rdarole:compiler", "xc_rdarole_compiler_key");
        registerId(" - rdarole:composer", "xc_rdarole_composer_key");
        registerId(" - rdarole:speaker", "xc_rdarole_speaker_key");
        registerId(" - rdarole:director",   "xc_rdarole_director_key");
        registerId(" - rdarole:producer",   "xc_rdarole_producer_key");
        registerId(" - rdarole:editor",   "xc_rdarole_editor_key");
        registerId(" - rdarole:illustrator",   "xc_rdarole_illustrator_key");
        registerId(" - rdarole:performer",   "xc_rdarole_performer_key");
        registerId(" - rdarole:translator",   "xc_rdarole_translator_key");
        registerId(" - marc:datafield 100$a", "id_100a_key");
        registerId(" - marc:datafield 100$b", "id_100b_key");
        registerId(" - marc:datafield 100$c", "id_100c_key");
        registerId(" - marc:datafield 100$d", "id_100d_key");
        registerId(" - marc:datafield 110$a", "id_110a_key");
        registerId(" - marc:datafield 110$b", "id_110b_key");
        registerId(" - marc:datafield 110$c", "id_110c_key");
        registerId(" - marc:datafield 110$d", "id_110d_key");
        registerId(" - marc:datafield 111$a", "id_111a_key");
        registerId(" - marc:datafield 111$c", "id_111c_key");
        registerId(" - marc:datafield 111$d", "id_111d_key");
        registerId(" - marc:datafield 700$a", "id_700a_key");
        registerId(" - marc:datafield 700$b", "id_700b_key");
        registerId(" - marc:datafield 700$c", "id_700c_key");
        registerId(" - marc:datafield 700$d", "id_700d_key");
        registerId(" - marc:datafield 710$a", "id_710a_key");
        registerId(" - marc:datafield 710$b", "id_710b_key");
        registerId(" - marc:datafield 710$c", "id_710c_key");
        registerId(" - marc:datafield 710$d", "id_710d_key");
        registerId(" - marc:datafield 711$a", "id_711a_key");
        registerId(" - marc:datafield 711$c", "id_711c_key");
        registerId(" - marc:datafield 711$d", "id_711d_key");
        registerId("Uplink",                "id_uplink_key");
        registerId(" - xc:workExpressed", "id_xc_workExpressed_key");
        registerId(" - xc:expressionManifested", "id_xc_expressionManifested_key");
        registerId(" - xc:manifestationHeld", "id_xc_manifestationHeld_key");
        registerId(" - marc:controlfield 004", "id_004_key");
        registerId(" - marc:datafield 014$a",  "id_014_key");
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

        TimingLogger.start("SolrIndexService.process add static fields");

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

        /*
        if (incomingRepository != null) {
            if (incomingRepository.getProvider() !=null) {
                if (incomingRepository.getProvider().getName() != null ) {
                    LOG.info("*** SolrIndexService, process(ri), repo=" +this.incomingRepository.getProvider().getName());
                }
                else {
                    LOG.info("*** SolrIndexService, process(ri), provider (noname)=" +this.incomingRepository.getProvider());
                }
            }
            else {
                LOG.info("*** SolrIndexService, process(ri), repo, no provider=" +this.incomingRepository);
            }
        }
        else {
            LOG.info("*** SolrIndexService, process(ri), incomingRepo=null");
        }
        */

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
        TimingLogger.stop("SolrIndexService.process add static fields");

        // type does not have to be defined, but for our purposes what else can we use to differentiate record types/patterns?
        // TODO get the format names out of the config file.
        //
        // IF we do not know the format here, will just have to do the blind xpath search.   BUT if we do know the format here,
        // we can save time.
        try {
            final Format marc21 = getFormatService().getFormatByName("marc21");
            final Format xc = getFormatService().getFormatByName("xc");
            //final Format dc = getFormatService().getFormatByName("dc");
            final Format format = r.getFormat();
            final String type = r.getType();

            // if type is null, can getSets; records have info this injected into them.  repo data will have it too.
            // HMM: could look at set supported list but don't see how it helps me differentiate what to do with this record...yet.
            //if (r.getSets() != null) {
            //    for (Set set : r.getSets()) {
            //    }
            //}
            // note this one is special, it is always added, it is not parsed out of the record's xml, so do not have it come out of
            //   the config file?
            TimingLogger.start("SolrIndexService.process add dynamic fields");
            doc.addField("id_identifier_key", r.getId());
            doc.addField("record_id_l", r.getId());

            if (format.equals(xc)) {
                if (type != null) {
                    if (type.equals("w")) {
                        addFieldToIndex(ri, doc, "//rdvocab:titleOfWork",
                                Collections.unmodifiableList(Arrays.asList("id_title_key", "rdvocab_titleOfWork_key")),
                                false);
                        addFieldToIndex(ri, doc, "//xc:creator",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_creator_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:compiler",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_compiler_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:composer",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_composer_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:speaker",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_speaker_key")),
                                true);
                        addFieldToIndex(ri, doc, "//dcterms:creator",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "dcterms_creator_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:director",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_director_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:producer",
                                Collections.unmodifiableList(Arrays.asList("id_author_key","xc_rdarole_producer_key")),
                                true);
                    }
                    if (type.equals("e")) {
                        addFieldToIndex(ri, doc, "//xc:workExpressed",
                                Collections.unmodifiableList(Arrays.asList("id_uplink_key", "id_xc_workExpressed_key")),
                                true);
                        //TODO either add to stopwords or strip extraneous junk out of uplink,
                        // TODO i.e. oai:msg.rochester.edu:MetadataServicesToolkit/marctoxctransformation/
                        // TODO is all unneeded.
                        addFieldToIndex(ri, doc, "//xc:titleOfExpression",
                                Collections.unmodifiableList(Arrays.asList("id_title_key", "xc_titleOfExpression_key")),
                                false);
                        addFieldToIndex(ri, doc, "//xc:contributor",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_contributor_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:director",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_director_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:editor",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_editor_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:illustrator",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_illustrator_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:performer",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_performer_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:producer",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_producer_key")),
                                true);
                        addFieldToIndex(ri, doc, "//rdarole:translator",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "xc_rdarole_translator_key")),
                                true);
                    }
                    if (type.equals("m")) {
                        addFieldToIndex(ri, doc, "//xc:expressionManifested",
                                Collections.unmodifiableList(Arrays.asList("id_uplink_key", "id_xc_expressionManifested_key")),
                                true);
                        //TODO either add to stopwords or strip extraneous junk out of uplink,   (see Util class for a similar method)
                        // TODO i.e. oai:msg.rochester.edu:MetadataServicesToolkit/marctoxctransformation/
                        // TODO is all unneeded.
                        addFieldToIndex(ri, doc, "//dcterms:title",
                                Collections.unmodifiableList(Arrays.asList("id_title_key", "dcterms_title_key")),
                                false);
                        addFieldToIndex(ri, doc, "//dcterms:identifier",
                                Collections.unmodifiableList(Arrays.asList("id_identifier_key", "id_manDCTermsIdent_key")),
                                false);
                        registerId("Identifier",            "id_identifier_key");
                        registerId(" - dcterms_ident_holdings", "id_holdDCTermsIdent_key");
                    }
                    if (type.equals("h")) {
                        addFieldToIndex(ri, doc, "//xc:manifestationHeld",
                                Collections.unmodifiableList(Arrays.asList("id_uplink_key", "id_xc_manifestationHeld_key")),
                                true);
                        //TODO either add to stopwords or strip extraneous junk out of uplink,
                        // TODO i.e. oai:msg.rochester.edu:MetadataServicesToolkit/marctoxctransformation/
                        // TODO is all unneeded.
                        addFieldToIndex(ri, doc, "//xc:callNumber",
                                Collections.unmodifiableList(Arrays.asList("id_callnum_key","xc_callnum_key")),
                                false);
                        addFieldToIndex(ri, doc, "//dcterms:identifier",
                                Collections.unmodifiableList(Arrays.asList("id_identifier_key", "id_holdDCTermsIdent_key")),
                                false);
                    }
                }
            }
            //else if (format.equals(dc)) {
            //}
            else if (r.getFormat().equals(marc21)) {
                // get identifiers that could be present in ANY marc21 record
                addFieldToIndex(ri, doc, "//marc:controlfield[@tag='001']",
                        Collections.unmodifiableList(Arrays.asList("id_uplink_key", "id_001_key")),
                        false);

                if (type != null) {
                    // get identifiers that could be present only in a bib marc21 record
                    if (type.equals("b")) {
                        LOG.debug("*** SolrIndexService, found a Marc, type=" +type);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='245']/marc:subfield[@code='a']",
                                Collections.unmodifiableList(Arrays.asList("id_title_key", "id_245a_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='245']/marc:subfield[@code='b']",
                                Collections.unmodifiableList(Arrays.asList("id_title_key", "id_245b_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='a']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_100a_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='b']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_10b_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='c']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_100c_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='d']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_100d_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='a']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_110a_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='b']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_110b_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='c']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_110c_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='d']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_110d_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='a']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_111a_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='c']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_111c_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='d']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_111d_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='a']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_700a_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='b']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_700b_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='c']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_700c_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='d']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_700d_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='a']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_710a_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='b']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_710b_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='c']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_710c_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='d']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_710d_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='a']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_711a_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='c']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_711c_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='d']",
                                Collections.unmodifiableList(Arrays.asList("id_author_key", "id_711d_key")),
                                false);
                    }
                    // get identifiers that could be present only in a holdings marc21 record
                    else if (type.equals("h")) {
                        LOG.debug("*** SolrIndexService, found a Marc, type=" +type);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='014']/marc:subfield[@code='a']",
                                Collections.unmodifiableList(Arrays.asList("id_uplink_key","id_004_key")),
                                true);
                        addFieldToIndex(ri, doc, "//marc:controlfield[@tag='004']",
                                Collections.unmodifiableList(Arrays.asList("id_uplink_key","id_014_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='852']/marc:subfield[@code='h']",
                                Collections.unmodifiableList(Arrays.asList("id_callnum_key","id_852h_key")),
                                false);
                        addFieldToIndex(ri, doc, "//marc:datafield[@tag='852']/marc:subfield[@code='i']",
                                Collections.unmodifiableList(Arrays.asList("id_callnum_key","id_852i_key")),
                                false);
                    }
                }
                else {
                    LOG.info("***** DO I EVER GET HERE?, unknown / RAW format record to index...");
                           // just do it. the complete set of marc parsers

                            addFieldToIndex(ri, doc, "//marc:controlfield[@tag='001']",
                                   Collections.unmodifiableList(Arrays.asList("id_uplink_key", "id_001_key")),
                                   false);

                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='245']/marc:subfield[@code='a']",
                                   Collections.unmodifiableList(Arrays.asList("id_title_key", "id_245a_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='245']/marc:subfield[@code='b']",
                                   Collections.unmodifiableList(Arrays.asList("id_title_key", "id_245b_key")),
                                   false);

                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='a']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_100a_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='b']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_10b_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='c']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_100c_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='100']/marc:subfield[@code='d']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_100d_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='a']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_110a_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='b']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_110b_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='c']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_110c_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='110']/marc:subfield[@code='d']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_110d_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='a']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_111a_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='c']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_111c_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='111']/marc:subfield[@code='d']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_111d_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='a']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_700a_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='b']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_700b_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='c']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_700c_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='700']/marc:subfield[@code='d']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_700d_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='a']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_710a_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='b']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_710b_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='c']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_710c_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='710']/marc:subfield[@code='d']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_710d_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='a']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_711a_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='c']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_711c_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='711']/marc:subfield[@code='d']",
                                   Collections.unmodifiableList(Arrays.asList("id_author_key", "id_711d_key")),
                                   false);


                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='014']/marc:subfield[@code='a']",
                                   Collections.unmodifiableList(Arrays.asList("id_uplink_key","id_004_key")),
                                   true);
                           addFieldToIndex(ri, doc, "//marc:controlfield[@tag='004']",
                                   Collections.unmodifiableList(Arrays.asList("id_uplink_key","id_014_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='852']/marc:subfield[@code='h']",
                                   Collections.unmodifiableList(Arrays.asList("id_callnum_key","id_852h_key")),
                                   false);
                           addFieldToIndex(ri, doc, "//marc:datafield[@tag='852']/marc:subfield[@code='i']",
                                   Collections.unmodifiableList(Arrays.asList("id_callnum_key","id_852i_key")),
                                   false);
                }
            }
            else {
                // TODO unknown / future formats.
                //      , fed from config file. (future)
            }
        } catch (DatabaseConfigException e) {
            LOG.error("Could not connect to the database with the parameters in the configuration file.", e);
        }
        TimingLogger.stop("SolrIndexService.process add dynamic fields");

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
            TimingLogger.start("SolrIndexService.process add doc");
            getSolrIndexManager().addDoc(doc);
            TimingLogger.stop("SolrIndexService.process add doc");
        } catch (IndexException ie) {
            throw new RuntimeException(ie);
        }
        return null;
    }

    private void addFieldToIndex(InputRecord ri, SolrInputDocument doc, String xpStr, String key, boolean repeats) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(key);
        addFieldToIndex(ri, doc, xpStr, list, repeats);
    }
    private void addFieldToIndex(InputRecord ri, SolrInputDocument doc, String xpStr, List<String> keys, boolean repeats) {
        ri.setMode(Record.STRING_MODE);
        List<Element> elements = getFieldValue(xpStr, ri.getOaiXml());
        if (elements != null) {
            if (repeats) {
                for (Element element: elements) {
                    for (String key: keys) {
                        LOG.debug("*** SolrIndexService, adding ident: "+ key +" add data:"+element.getText());
                        doc.addField(key, element.getText());
                    }
                }
            }
            else {
                for (String key: keys) {
                    doc.addField(key, elements.get(0).getText());
                }
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
