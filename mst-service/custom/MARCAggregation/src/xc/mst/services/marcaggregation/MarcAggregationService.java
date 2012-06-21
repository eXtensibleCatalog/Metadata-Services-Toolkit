/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation;

import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongObjectHashMap;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import xc.mst.bo.provider.Format;
import xc.mst.bo.record.*;
import xc.mst.bo.service.Service;
import xc.mst.constants.Status;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.repo.Repository;
import xc.mst.services.ServiceValidationException;
import xc.mst.services.impl.service.GenericMetadataService;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;
import xc.mst.services.marcaggregation.matcher.FieldMatcher;
import xc.mst.services.marcaggregation.matcher.MatchSet;
import xc.mst.services.marcaggregation.matchrules.MatchRuleIfc;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.Util;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Benjamin D. Anderson
 * @author John Brand
 *
 */
public class MarcAggregationService extends GenericMetadataService {

    /**
     * The output format (marcxml) for records processed from this service
     */
    protected Format                                 marc21 = null;

    protected Map<String, FieldMatcher>              matcherMap = null;
    protected Map<String, MatchRuleIfc>              matchRuleMap = null;
    protected MarcAggregationServiceDAO              masDAO = null;
    protected MASMarcBuilder                         masBld = null;
    protected RecordOfSourceManager                  masRsm = null;
    protected List<TreeSet<Long>>                    masMatchSetList = null;

    /**
     * to sort/figure record of source, place items we are interested in, that are used to determine
     * record of source, in this map, as we go.
     */
    protected TLongObjectHashMap<RecordOfSourceData> scores             = null;
    protected TLongObjectHashMap<RecordOfSourceData> scores_unpersisted = null;

    /**
     * map output records to corresponding input records map
     * not only tracked merged records, 1 to many, but track unmerged 1 to 1
     */
    protected Map<Long, TreeSet<Long>>               allBibRecordsO2Imap = null;

    /** map input records to corresponding output map,
     *  not only tracked merged records, many to 1, but track unmerged 1 to 1
     */
    protected TLongLongHashMap                       allBibRecordsI2Omap = null;
    protected TLongLongHashMap                       allBibRecordsI2Omap_unpersisted = null;

    /** track input records that have been merged (>1 input to an output),
     */
    protected List<Long>                             mergedInRecordsList = null;
    protected List<Long>                             mergedInRecordsList_unpersisted = null;

    /**
     * the repository feeding this service.  we need to hang on to this because of remerging, etc.
     */
    private Repository      inputRepo  = null;

    /**
     * transformer stuff used to strip/modify given xml
     */
    private Transformer staticTransformer;
    private Transformer holdingTransformer;
    private Transformer _005_Transformer;

    private static final String STATIC_TRANSFORM  = "createStatic.xsl";
    private static final String HOLDING_TRANSFORM = "stripHolding.xsl";
    private static final String _005_TRANSFORM    = "strip005.xsl";

    private static final Logger LOG               = Logger.getLogger(MarcAggregationService.class);


    /**
     * Setup record of source rules, setup transformers for modifying xml, validate the service, setup matchers and rules,
     * load known merged record details.
     */
    @Override
    public void setup() {
        LOG.debug("MAS:  setup()");

        try {
            marc21 = getFormatService().getFormatByName("marc21");
        } catch (DatabaseConfigException e) {
            LOG.error("Could not connect to the database with the parameters in the configuration file.", e);
        } catch (Exception e2) {
            LOG.error("Problem with init.", e2);
        }

        masRsm = (RecordOfSourceManager) config.getBean("RecordOfSourceManager");
        masRsm.setupRecordOfSource();
        masBld = (MASMarcBuilder) config.getBean("MASMarcBuilder");
        staticTransformer = setupTransformer(getTransformForStaticFilename());
        _005_Transformer  = setupTransformer(getTransformFor005Filename());

        final boolean transformHolding = false;
        if (transformHolding) {
            holdingTransformer= setupTransformer(getTransformForHoldingFilename());
        }
        try {
            validateService();
        } catch (ServiceValidationException e) {

            // error validating service:
            // Update database with status of service
            service.setStatus(Status.ERROR);
            LOG.error("Error validating service:", e);
            LogWriter.addInfo(service.getServicesLogFileName(), "** Error validating service - service will not run " + e.getMessage() + " **");
            sendReportEmail("Error validating service: " + e.getMessage());

            // in case the WorkerThread code addition causes issues, simply uncomment the below:
            // throw new RuntimeException(e);
        }
        setupMatchers();
        setupMatchRules();
        allBibRecordsI2Omap = loadMasBibIORecords();
        allBibRecordsO2Imap = createMergedRecordsO2Imap(allBibRecordsI2Omap);
        mergedInRecordsList = loadMasMergedInputRecords();

        allBibRecordsI2Omap_unpersisted = new TLongLongHashMap();
        mergedInRecordsList_unpersisted = new ArrayList<Long>();
    }

    /**
     *
     * map output records to corresponding input records map
     * there is probably a lot slicker way to do this.
     * @param i_to_o_map
     * @return
     */
    private Map<Long, TreeSet<Long>> createMergedRecordsO2Imap(TLongLongHashMap i_to_o_map) {
        TreeMap<Long,TreeSet<Long>> results = new TreeMap<Long, TreeSet<Long>>();
        for (Long out: i_to_o_map.getValues()) {
            if (!results.containsKey(out)) {
                List<Long> vals = masDAO.getInputRecordsMappedToOutputRecord(out);
                TreeSet<Long> set = new TreeSet<Long>();
                for (Long val: vals) {
                    set.add(val);
                }
                results.put(out, set);
            }
        }
        return results;
    }

    /**
     * load from the database
     * @return known merged records that were persisted, it returns all bibs i to o
     */
    private TLongLongHashMap loadMasBibIORecords() {
        return masDAO.getBibRecords();
    }


    /**
     * load from the database
     * @return known merged records that were persisted-input records that are part of a merge set (>1 corresponds to an output record)
     */
    private List<Long> loadMasMergedInputRecords() {
        return masDAO.getMergedInputRecords();
    }


    protected void setupMatchRules() {
        this.matchRuleMap = new HashMap<String, MatchRuleIfc>();
        List<String> mrs = getConfigFileValues("match.rules.value");
        for (String mrStr : mrs) {
            MatchRuleIfc mr = (MatchRuleIfc) config.getBean(mrStr + "MatchRule");
            matchRuleMap.put(mrStr, mr);
        }
        if (this.masDAO == null) {  // this was really an initial unit test
            LOG.error("***  ERROR, DAO did not get initialized by Spring!");
        }
        masMatchSetList = new ArrayList<TreeSet<Long>>();
        scores = new TLongObjectHashMap<RecordOfSourceData>();
        scores_unpersisted = new TLongObjectHashMap<RecordOfSourceData>();
    }

    protected void setupMatchers() {
        this.matcherMap = new HashMap<String, FieldMatcher>();
        List<String> mps = getConfigFileValues("matchers.value");
        for (String mp : mps) {
            final String n = mp + "Matcher";
            FieldMatcher m = (FieldMatcher) config.getBean(n);
            m.setName(n);
            m.setMarcAggregationService(this);
            matcherMap.put(mp, m);
            m.load();
        }
    }

    /**
     * open the xsl file, create a Transformer from it.
     * @param xslFileName a String that represents a File.
     * @return the Transformer created from the xslFileName.
     * @throws TransformerFactoryConfigurationError
     */
    protected Transformer setupTransformer(String xslFileName) throws TransformerFactoryConfigurationError {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        xslFileName = MSTConfiguration.getInstance().getServicePath() + service.getName() + "/xsl/" + xslFileName;
        try {
            return transformerFactory.newTransformer(new StreamSource(new FileInputStream(xslFileName)));
        } catch (Throwable t) {
            LOG.error("", t);
            throw new RuntimeException(t);
        }
    }

    protected String getTransformForStaticFilename() {
        return STATIC_TRANSFORM;
    }


    protected String getTransformFor005Filename() {
        return _005_TRANSFORM;
    }

    protected String getTransformForHoldingFilename() {
        return HOLDING_TRANSFORM;
    }

    @Override
    protected void validateService() throws ServiceValidationException {
        if (masRsm.isRecordOfSourceOptionsConfiguredIncorrectly()) {
            throw new ServiceValidationException("Service configuration file invalid: leader_byte17_weighting_enabled & bigger_record_weighting_enabled cannot both be disabled!");
        }
    }

    /**
     * this is just for what we have so far, not meant to always be up to date, i.e. it doesn't get
     * started off from looking at existing merged stuff in the database.  Based on the current record
     * that comes in, see what it matches, and go from there.
     *
     * @return
     */
    public List<TreeSet<Long>> getCurrentMatchSetList() {
        return masMatchSetList;
    }

    /**
     * load property from the service's config file.
     * @param name
     * @return
     */
    protected List<String> getConfigFileValues(String name) {
        try {
            // there is probably a more righteous way to grab the service name.
            final PropertiesConfiguration props = new PropertiesConfiguration(MSTConfiguration.getUrlPath() + "/services/" + getUtil().normalizeName("MARCAggregation") +
                    "/META-INF/classes/xc/mst/services/custom.properties");

            final List<String> values = Util.castList(String.class, props.getList(name));
            return values;
        } catch (Exception e) {
            LOG.error("Error loading custom.properties for service: " + this.getServiceName(), e);
            return null;
        }
    }
    /**
     * for injection.
     * @see xc.mst.spring.MSTBeanPostProcessor
     * @param masDAO
     */
    public void setMarcAggregationServiceDAO(MarcAggregationServiceDAO masDAO) {
        this.masDAO = masDAO;
    }

    /**
     * @see xc.mst.spring.MSTBeanPostProcessor
     * @return
     */
    public MarcAggregationServiceDAO getMarcAggregationServiceDAO() {
        return this.masDAO;
    }

    /**
     * wrap it.
     *  (to increase accessibility - classes like Matcher/MatchRules that aren't subclasses may need it.)
     */
    public void addMessage(InputRecord record, int code, char level) {
        try {
            // originally had to grab service as below but that was a workaround that didn't help ultimately.
            //
            //final Service service= getServicesService().getServiceByName("MARCAggregation");
            super.addMessage(record, code, level, null, getService());
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * at the end of the service, this is called
     * @param repo
     */
    public void processComplete(Repository repo) {
        //
        // for performance may want to do this:
        //        .createIndiciesIfNecessary(name);
        //
        // evaluate match sets here?
        LOG.info("** START processComplete!");

        // start to do the real work of the service.  Probably belongs
        // up higher than processCompleted method!!
        //
        List<TreeSet<Long>> matches = getCurrentMatchSetList();
        if (matches != null) {
            //TODO maybe change this to 'debug' vs. 'info' at some point.
            LOG.info("** processComplete, matchset length="+matches.size());

            for (Set<Long> set: matches) {
                StringBuilder sb = new StringBuilder("*** Matchset: {");
                for (Long num: set) {
                    sb.append(num+", ");
                }
                sb.append("}");
                //TODO decide whether this is useful to keep around!
                //LOG.info(sb.toString());
                logToServiceLog(sb.toString());
            }

            // TODO
            // important - this is not going to totally nail it for the long term
            // need to consider records received during THIS run of the service, and
            // their status, i.e. if if goes to deleted state and is part of a merge
            // set.  Future solution still in the works  - could be customProcessQueue
            // and if that is not enough save more to the current match set list?
            //
            // TODO
            // Do you need to build lists of records to create (part of merge set & not)
            // and records that will
            // not being created because they are being merged?
            //
            //mergeAll(matches, repo);
        }
        //end real work of the service (getting matches and merging)
    }

    private void logToServiceLog(String status) {

        Service service = null;
        try {
            service = getServiceDAO().getById(this.service.getId());
            if (service != null) {
                LogWriter.addInfo(service.getServicesLogFileName(), status);
            }
        } catch (DatabaseConfigException e1) {
            LOG.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);
        }
    }

    /**
     * note the 'well' named class Set collides with java.util.Set
     *
     * overriding this so you can save the repo/start over?
     */
    public void process(Repository repo, Format inputFormat, xc.mst.bo.provider.Set inputSet, xc.mst.bo.provider.Set outputSet) {
        this.inputRepo = repo;
        LOG.info("MarcAggregationService, processing repo "+ repo.getName()+" started.");
        try {
            super.process(repo, inputFormat, inputSet, outputSet);
            processComplete(repo);
        } catch (Exception e) {
            LOG.error("MarcAggregationService, processing repo "+ repo.getName()+" failed.", e);
        }
    }

    /**
     * each record run by the service,
     * gets process called at a particular time in the method
     * see parent method process(Repository repo, Format inputFormat, Set inputSet, Set outputSet)
     *
     * the existing paradigm is to do things record by record without considering the whole of the records
     */
    public List<OutputRecord> process(InputRecord r) {
        String type = null;
        List<OutputRecord> results = null;
        try {
            LOG.debug("MAS:  process record+"+r.getId());

            if (r.getStatus() != Record.DELETED) {
                SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());
                smr.setRecordId(r.getId());

                // Get the Leader 06. This will allow us to determine the record's type
                final char leader06 = smr.getLeader().charAt(6);

                // check if the record is a bibliographic record
                if ("abcdefghijkmnoprt".contains("" + leader06)) {

                    TimingLogger.start("bib steps");

                    // get record of source data for this bib
                    //  (only a bib would be a record of source)
                    final char leaderByte17 = smr.getLeader().charAt(17);
                    final int rSize = r.getOaiXml().getBytes().length;
                    scores.put(r.getId(), new RecordOfSourceData(leaderByte17, rSize));
                    scores_unpersisted.put(r.getId(), new RecordOfSourceData(leaderByte17, rSize));

                    type = "b";
                    //
                    // setting this here increments this type in the record counts when
                    // incremented in GenericMetadataService.process() -- else it then
                    // increments RecordCounts.OTHER
                    //
                    ((Record) r).setType(type);

                    results = processBib(r, smr, inputRepo);
                    TimingLogger.stop("bib steps");
                }
                // check if the record is a holding record
                else if ("uvxy".contains("" + leader06)) {
                    TimingLogger.start("hold steps");
                    type = "h";
                    //
                    // setting this here increments this type in the record counts when
                    // incremented in GenericMetadataService.process() -- else it then
                    // increments RecordCounts.OTHER
                    //
                    ((Record) r).setType(type);

                    results = processHolding(r, smr, inputRepo);
                    TimingLogger.stop("hold steps");
                }
                else if (leader06 == 'z') {
                    // authority
                    // just pass it on.
                    String oaiXml = inputRepo.getRecord(r.getId()).getOaiXml();
                    results = createNewRecord(r, "z", oaiXml);
                }
                else {
                    //LOG error, do the same as normalization.
                    logDebug("Record Id " + r.getId() + " with leader character " + leader06 + " not processed.");
                }
            } else {// Record.DELETED
                if (r.getSuccessors().size() == 0) {
                    // NEW-DELETED
                    //
                    // nothing to do?  should we still double-check datastructures and db?
                } else {
                    // UPDATE-DELETED
                    //
                    // ( mostly ) directly lifted from norm...
                    //
                    boolean isAbibWithSuccessors = false;
                    results = new ArrayList<OutputRecord>();
                    TimingLogger.start("processRecord.updateDeleted");
                    List<OutputRecord> successors = r.getSuccessors();

                    // If there are successors then the record exist and needs to be deleted. Since we are
                    // deleting the record, we need to decrement the count.
                    if (successors != null && successors.size() > 0) {
                        inputRecordCount--;

                        // and if the record exists, check if it is a bib
                        // if it is in mergedRecordsI2Omap, it is a bib, fastest way.  don't try to parse record, deleted could be incomplete
                        // and unparseable,
                        //
                        if (allBibRecordsI2Omap.containsKey(r.getId())) {
                            // is bib!  flag it for later...
                            isAbibWithSuccessors = true;
                        }

                        // Handle reprocessing of successors
                        for (OutputRecord successor : successors) {
                            successor.setStatus(Record.DELETED);
                            successor.setFormat(marc21);
                            results.add(successor);
                        }
                    }
                    if (isAbibWithSuccessors) {

                        TreeSet<Long> formerMatchSet = deleteAllMergeDetails(r);

                        // lastly must remerge the affected records, if this was part of a merge set.
                        if (formerMatchSet.size() > 1) {
                            formerMatchSet.remove(r.getId());       // remove the input that is gone.

                            results = remerge(formerMatchSet);
                        }
                    }
                    TimingLogger.stop("processRecord.updateDeleted");
                }
            }
            if (results != null && results.size() != 1) {
                // TODO increment records counts no output
                // (_IF_ database column added to record counts to help with reconciliation of counts)
                addMessage(r, 103, RecordMessage.ERROR);  // no output
            }
            return results;

        } catch (Throwable t) {
            LOG.error("error processing record with id:" + ((Record) r).getId(), t);
            // TODO increment records counts no output
            // (_IF_ database column added to record counts to help with reconciliation of counts)
            addMessage(r, 103, RecordMessage.ERROR);  // no output
        }
        return null;
    }

    protected Repository getInputRepo() {
        return this.inputRepo;
    }

    /**
     * if need to merge at end, after all records seen, (for better performance)
     *
     * @param matches
     * @param repo
     */
    private void mergeAll(List<TreeSet<Long>> matches, Repository repo) {
        // merge each match set, 1 winning record used to pull static content,
        // all in the set used to pull dynamic content

        for (TreeSet<Long> set: matches) {
            InputRecord record = masRsm.getRecordOfSourceRecord(set, repo, scores);
            String xml = mergeBibSet(record, set, repo);
            createNewBibRecord(record, xml, set);
        }
    }

    /**
     * will use these data structures as the basis to update DAO, should always be up to date.
     * @param outputRecordId
     * @param mergedInputRecordSet
     */
    private void addToMasMergedRecordsMemory(Long outputRecordId, TreeSet<Long> mergedInputRecordSet) {
        for (Long num: mergedInputRecordSet) {
            allBibRecordsI2Omap.put(num,outputRecordId);
            allBibRecordsI2Omap_unpersisted.put(num,outputRecordId);
        }
        allBibRecordsO2Imap.put(outputRecordId, mergedInputRecordSet);

        if (mergedInputRecordSet.size() > 1) {
            for (Long num: mergedInputRecordSet) {
                mergedInRecordsList.add(num);
                mergedInRecordsList_unpersisted.add(num);
            }
        }
    }


    /**
     * Note - createStatic => strip 001/003/035,  create 035, save 035 (as dynamic)
     *
     * @param set of record ids to merge
     * @param repo  seems as though we have frowned on this in the past, but with this
     *              service can we avoid looking up and using record content from the source?
     * @return returns static xml + saved dynamic content (included or not?)
     */
    private String mergeBibSet(InputRecord theSrcRecord, TreeSet<Long> set, Repository repo) {
        String oaiXml = theSrcRecord.getOaiXml();
        //SaxMarcXmlRecord smr = new SaxMarcXmlRecord(oaiXml);

        Map<Integer, Set<MarcDatafieldHolder>> dynamic = masBld.getDynamicContent(theSrcRecord.getId(), repo, set);

        oaiXml = masBld.getStaticBase(oaiXml, staticTransformer);
        // this would be a lot of data in the log.
        //
        //LOG.debug("STATIC-"+recordOfSource);
        //LOG.debug(oaiXml);

        oaiXml = masBld.updateDynamicRecordWithStaticContent(oaiXml, dynamic);
        //LOG.info("STATIC-"+recordOfSource);
        //LOG.info(oaiXml);

        return oaiXml;
    }

    private List<OutputRecord> createNewBibRecord(InputRecord theSrcRecord, String oaiXml, TreeSet<Long> set) {

        List<OutputRecord> list = createNewRecord(theSrcRecord, "b", oaiXml);

        // now that we have created a new record successfully, update the data structure to track the merged records.
        if (list.size() > 0) {
            // will get 1 agg. record back.
            addToMasMergedRecordsMemory(list.get(0).getId(), set);
        }
        return list;
    }


    /**
     * create a record of the specified type, set the xml to newXml
     * @param record -the record of source
     * @param type - b, h or z
     * @param newXml - the payload
     * @return the List of OutputRecord(s) (will just be 1)
     */
    private List<OutputRecord> createNewRecord(InputRecord record, String type, String newXml) {

        TimingLogger.start("new");

        // The list of records resulting from processing the incoming record
        //    for this service, need to somewhere account for the fact that
        //    we are collapsing, not expanding, so there <= output records for
        //    an input record
        //
        ArrayList<OutputRecord> results = new ArrayList<OutputRecord>();

        // Create the aggregated record
        OutputRecord aggRecord = getRecordService().createRecord();

        aggRecord.setMode(Record.STRING_MODE);
        aggRecord.setOaiXml(newXml); /* use the merged content */
        aggRecord.setFormat(marc21);

        // Insert the new (possibly) aggregated record

        // The setSpec and set Description of the "type" set we should add the record to
        String setSpec = null;
        String setDescription = null;
        String setName = null;

        if (type.equals("b")) {
            setSpec = "MARCXMLbibliographic";
            setName = "MARCXML Bibliographic Records";
            setDescription = "A set of all MARCXML Bibliographic records in the repository.";
            aggRecord.setType(type);
        } else if (type.equals("h")) {
            setSpec = "MARCXMLholding";
            setName = "MARCXML Holding Records";
            setDescription = "A set of all MARCXML Holding records in the repository.";
            aggRecord.setType(type);
        } else if (type.equals("z")) {
            setSpec = "MARCXMLauthority";
            setName = "MARCXML Authority Records";
            setDescription = "A set of all MARCXML Authority records in the repository.";
            // don't setType for this 'type'
        } else { // If leader 6th character is invalid, then log error and do not process that record.
                 // this code is identical to normalization service.
            logDebug("Record Id " + record.getId() + " with leader character " + type + " not processed.");
            return new ArrayList<OutputRecord>();
        }
        if (setSpec != null) {
            try {
                // Get the set for the provider
                TimingLogger.start("getSetBySetSpec");
                xc.mst.bo.provider.Set recordTypeSet = getSetService().getSetBySetSpec(setSpec);
                TimingLogger.stop("getSetBySetSpec");

                // Add the set if it doesn't already exist
                if (recordTypeSet == null) {
                    recordTypeSet = addSet(setSpec, setName, setDescription);
                }

                // Add the set to the record
                aggRecord.addSet(recordTypeSet);
            } catch (DatabaseConfigException e) {
                LOG.error("Could not connect to the database with the parameters in the configuration file.", e);
                e.printStackTrace();
            } catch (DataException e) {
                LOG.error("Error.", e);
            }
        }
        // Add the record to the list of records resulting from processing the
        // incoming record
        results.add(aggRecord);

        if (LOG.isDebugEnabled())
            LOG.debug("Created aggregated record from record with ID " + record.getId());

        TimingLogger.stop("new");
        return results;
    }

    /**
     * search to see if there are multiple in records for this given out record.
     * , in any event, add the predecessor to the output record.
     *
     */
    @Override
    protected void addPredecessor(Record in, Record out) {
        TreeSet<Long> set = allBibRecordsO2Imap.get(out.getId());
        if (set==null || set.isEmpty()) {
            // picks up the holding records
            LOG.debug("addPredecessor, empty or null set for record id: "+out.getId());
            out.addPredecessor(in);
        }
        else {
            LOG.debug("addPredecessor, will add, for record id: "+out.getId());
            for (Long in_rec: set) {
                Record r = inputRepo.getRecord(in_rec);
                if (r != null) {
                    out.addPredecessor(r);
                }
            }
        }
    }

    /**
     * Could call this if a record is updated (safest just to remerge all affected) or deleted (must remerge all affected).
     * for now , delete in a 1-off fashion.  And for both mem and db here and now.
     *
     * @param r - for this given input record, find its output record, and any
     *            input records that share the output record
     * @return - the complete set of input records that share an output record with the given input record
     */
    private TreeSet<Long> deleteAllMergeDetails(InputRecord r) {
        //
        //
        // 1st, delete from the database
        LOG.debug("&&& in deleteAllMergeDetails for "+r.getId());
        masDAO.deleteAllMASRecordDetails(r.getId());

        // and delete this records matchpoint data
        removeRecordsFromMatchers(r);

        // 2nd, get the related merged records:
        TreeSet<Long> formerMatchSet = getCurrentMatchSetForRecord(r);

        // 3rd, remove related records from memory structures in preparation for remerge.

        // since we are not attempting to delete an output record, don't pass in or expect back results.
        cleanupOldMergedOutputInfo(formerMatchSet, null, false); //TODO should we be trying to delete an output record???
/*
 // refactored the below code out, but since it represents a slightly different way to delete stuff than cleanupOldMergedOutputInfo,
 //  I am leaving it here for a bit.
 //
        allBibRecordsO2Imap.remove(getBibOutputId(r));
        for (Long member: formerMatchSet) {
            LOG.debug("&&& in deleteAllMergeDetails for "+r.getId()+" now delete from memory associate member="+member);
            allBibRecordsI2Omap.remove(member);
            mergedInRecordsList.remove(member);
            //sync with the database too
            masDAO.deleteMergeMemberDetails(member);
        }
*/

        return formerMatchSet;
    }

    private Long getBibOutputId(InputRecord r) {
        return getBibOutputId(r.getId());
    }

    private Long getBibOutputId(Long id) {
        return allBibRecordsI2Omap.get(id);
    }

    private TreeSet<Long> getCurrentMatchSetForRecord(InputRecord r) {
        Long outputId = getBibOutputId(r);
        TreeSet<Long> matchSet = allBibRecordsO2Imap.get(outputId);
        if (matchSet == null) {
            matchSet = new TreeSet<Long>();
        }
        return matchSet;
    }


    /**
     * basically, retrieve the records, create saxmarcxml records, find what matches what.
     * then merge / expand the sets, and create the output record(s).
     * Yes, the number of output records may increase.
     *
     * @param formerMatchSet
     * @return
     */
    private List<OutputRecord> remerge(TreeSet<Long> formerMatchSet) {
        List<TreeSet<Long>> listOfMatchSets = new ArrayList<TreeSet<Long>>();
        for (Long id: formerMatchSet) {

            Record r = getInputRepo().getRecord(id);
            SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());
            smr.setRecordId(r.getId());

            MatchSet ms = populateMatchSet(r, smr);
            TreeSet<Long> newMatchedRecordIds = populateMatchedRecordIds(ms);
            //
            // populateMatchedRecordIds does not return the record itself as part of the match set,
            // in this case I want it in the set.
            //
            newMatchedRecordIds.add(id);
            if (!listOfMatchSets.contains(newMatchedRecordIds)) {
                // come up with a bare bones set of new match sets, I am guessing most of the time it will be 1 set
                listOfMatchSets = addToMatchSetList(newMatchedRecordIds,  listOfMatchSets);
            }
        }
        List<OutputRecord> results = new ArrayList<OutputRecord>();
        for (TreeSet<Long> matchset: listOfMatchSets) {
            results = mergeOverlord(results, matchset, getInputRepo());
        }
        // will pred-succ relationships automatically be correct? --> it seems so.

        return results;
    }

    /*
     * if it is a record we have seen, update it, else create the new holding.  do nothing else.
     */
    protected List<OutputRecord> processHolding(InputRecord r, SaxMarcXmlRecord smr, Repository repo) {

        //
        // new plan - do not put a 904 into the holding, just pass the holding on.
        //
        //StringBuilder sb = masBld.add904toHolding(r, smr, repo);
        //
        //   LOG.info("** NEW HOLDING:");
        //   LOG.info(sb.toString());


        List<OutputRecord> list = null;
        String oaiXml = repo.getRecord(r.getId()).getOaiXml();
        // include an update to the 005.
        oaiXml = masBld.update005(r.getOaiXml(), _005_Transformer);


        // If there was already a processed record for the record we just processed, update it
        if (r.getSuccessors() != null && r.getSuccessors().size() > 0) {
            TimingLogger.start("update hold");

            if (LOG.isDebugEnabled())
                LOG.debug("Updating the record which was processed from an older version of the record we just processed.");

            // Get the record which was processed from the record we just processed
            // (there should only be one)
            OutputRecord oldHold = r.getSuccessors().get(0);

            oldHold.setMode(Record.STRING_MODE);
            oldHold.setFormat(marc21);
            oldHold.setStatus(Record.ACTIVE);

            // Set the XML to the updated XML
            oldHold.setOaiXml(oaiXml);

            // Add the updated record
            oldHold.setType("h");
            list = new ArrayList<OutputRecord>();
            list.add(oldHold);

            TimingLogger.stop("update hold");
            return list;
        }

        else {
            // originally I thought we were stripping 004/014 from holding.  We are not.
            //
            // oaiXml = masBld.getHoldingBase(oaiXml, holdingTransformer);
            TimingLogger.start("new hold");
            list = createNewRecord(r, "h", oaiXml);
            TimingLogger.stop("new hold");
            return list;
        }
    }

    /**
     *
     * @param r  the input record, a bib
     * @param smr, the SaxMarcXmlRecord representation of the bib
     * @param repo the input repository
     * @return
     */
    protected List<OutputRecord> processBib(InputRecord r, SaxMarcXmlRecord smr, Repository repo) {
        List<OutputRecord> results;
        if (r.getSuccessors().size() == 0) {
            // NEW-ACTIVE
            results = processBibNewActive(r, smr, repo);

        } else {
            // UPDATE-ACTIVE
                // unmerge
                /*
                for (inputBibId : inputBibIds) {
                    customProcessQueue.push(inputBibId)
                }
                for (inputHoldingId : inputHoldingIds) {
                    customProcessQueue.push(inputHoldingId)
                }
                */
            results = processBibUpdateActive(r, smr, repo);
        }

        return results;
    }

    private List<OutputRecord> processBibUpdateActive(InputRecord r, SaxMarcXmlRecord smr, Repository repo) {
        LOG.debug("*AM in processBibUpdateActive!");

        // before deleting anything, check if match set is the same?  if so, just update? (need to re-get record of source)
        //
        // Example to illustrate: For instance {1,2,3} may have used to match,
        // now with update this is the match situation: {1,2},{3} so now need TWO output records!
        //    could be the customProcessQueue
        List<OutputRecord> results = new ArrayList<OutputRecord>();

        TreeSet<Long> formerMatchSet = getCurrentMatchSetForRecord(r);

        removeRecordsFromMatchers(r);

        MatchSet ms = populateMatchSet(r, smr);
        TreeSet<Long> newMatchedRecordIds = populateMatchedRecordIds(ms);


        Long oldOutputId;
        OutputRecord oldOutput;

        boolean sameSet = areMatchSetsEquivalent(formerMatchSet, newMatchedRecordIds);
        if (!sameSet) {
            // must clear out ALL pertinent old record stuff, not just formerMatchSet, but stuff pertaining to newly matching records too!
            TreeSet<Long> union = new TreeSet<Long>();
            union.addAll(newMatchedRecordIds);
            union.addAll(formerMatchSet);
            // unmerge type step, we will undo what has been done then redo from scratch, easiest to assure proper results.
            oldOutput = r.getSuccessors().get(0);
            results = cleanupOldMergedOutputInfo(union, results, true);
            results.addAll(remerge(formerMatchSet));
        }
        else {   // same size merge set, must update.
            // this is the merge as you go along spot, and will be impacted if you change that paradigm.
            // does not seem like it is most efficient but if fits our paradigm of running through all records 1x.
            // TODO change to merge at end, looping a 2nd time through the records, if need be. (though I don't know
            //      how well that would work for updates/deletes/remerges!)

            // do not think you need to bother with this - you already verified the match set is the same, and it will have been added already.
            // if you are paranoid though you COULD run this - it is an idempotent method.
            //masMatchSetList = addToMatchSetList(newMatchedRecordIds, masMatchSetList);

            // note both sides of the if below should produce the same record.
            //   (as long as we continue to put ALL bibs into I2O map and O2I map, and not just merged records)
            if (allBibRecordsI2Omap.containsKey(newMatchedRecordIds.first())) {
                oldOutputId = getBibOutputId(newMatchedRecordIds.first());
                oldOutput = getRepository().getRecord(oldOutputId);
            }
            else {
                // Get the record which was processed from the record we just processed
                // (any of the matchset input records should map to the same output record, right?)
                oldOutput = r.getSuccessors().get(0);
            }
            // since same set, don't clean up any memory details, it all points to the right stuff still.

            List<OutputRecord> list = null;
            // may not have any matches!
            final boolean hasMatches = newMatchedRecordIds.size() > 0;
            String xml;
            if (hasMatches) {

                InputRecord record = masRsm.getRecordOfSourceRecord(newMatchedRecordIds, repo, scores);
                xml = mergeBibSet(record, newMatchedRecordIds, repo);

                LOG.debug("** create merged output record: "+list.get(0).getId()+" status="+list.get(0).getStatus());

                // now that we remerged, update the existing output record below!
            }
            else {
                // no matches, just update the output with the latest, include an update to the 005.
                xml = masBld.update005(r.getOaiXml(), _005_Transformer);
            }

            oldOutput.setMode(Record.STRING_MODE);
            oldOutput.setFormat(marc21);
            oldOutput.setStatus(Record.ACTIVE);

            // Set the XML to the updated XML - remerged and reconstituted the xml

            // Do NOT create a new record, update, the OLD record!
            // Set the XML to the updated XML - reconstituted the xml
            oldOutput.setOaiXml(xml);

            // Add the updated record
            oldOutput.setType("b");
            results.add(oldOutput);

            list = new ArrayList<OutputRecord>();
            list.add(oldOutput);

            LOG.debug("** create unmerged output record: "+list.get(0).getId()+" status="+list.get(0).getStatus());

            results.addAll(list);
        }
        return results;
    }

    private List<OutputRecord> processBibNewActive(InputRecord r, SaxMarcXmlRecord smr, Repository repo) {
        LOG.debug("*AM in processBibNewActive!");

        MatchSet ms = populateMatchSet(r, smr);

        TreeSet<Long> matchedRecordIds = populateMatchedRecordIds(ms);

        List<OutputRecord> results = new ArrayList<OutputRecord>();

        // un-merge type step, we will undo what has been done then re-do from scratch, easiest to assure proper results.
        // this could happen a lot in a merge as you go situation, i.e. each time the match set increases.
        // TODO for re-merge, this was already done right?  do it again? (idempotent/not run then?  / Test)
        // no need for record itself to be part of match set, yet, it is new, so won't be any old merge info.
        results = cleanupOldMergedOutputInfo(matchedRecordIds, results, true);

        // maybe this will come into play with rules that have parts that are alike...
        Set<Long> previouslyMatchedRecordIds = null;

        // this is the merge as you go along spot,
        // does not seem like it is most efficient but if fits our paradigm of running through all records 1x.
        // TODO change to merge at end, looping a 2nd time through the records, if need be.

        matchedRecordIds.add(r.getId());
        results = mergeOverlord(results, matchedRecordIds, repo);

        return results;
    }

    /**
     * do some of the housekeeping required with merging, really a premerge process, then hand off to mergeBibSet, if necessary.
     *
     * @param results - may already have some results from a prior merge or some deleted records from prior cleanup of unmerged records.
     * @param matchedRecordIds - the match set, will always have at least one element.
     * @param repo - the input records
     * @return - the prior passed in results + new results of OutputRecord resulting from the merge
     */
    private List<OutputRecord> mergeOverlord(List<OutputRecord> results, TreeSet<Long> matchedRecordIds, Repository repo) {

        if (LOG.isDebugEnabled()) {
            StringBuffer buf = new StringBuffer();
            for(Long num: matchedRecordIds){
                buf.append(num).append(" ");
            }
            LOG.debug("** MERGE overlord, matchset ="+buf.toString());
        }

        List<OutputRecord> list = null;
        // may not have any matches!
        final boolean hasMatches = matchedRecordIds.size() > 1;
        if (hasMatches) {
            masMatchSetList = addToMatchSetList(matchedRecordIds, masMatchSetList);

            InputRecord record = masRsm.getRecordOfSourceRecord(matchedRecordIds, repo, scores);
            String xml = mergeBibSet(record, matchedRecordIds, repo);
            list = createNewBibRecord(record, xml, matchedRecordIds);

            LOG.debug("** create merged output record: "+list.get(0).getId()+" status="+list.get(0).getStatus());

        }
        else {
            InputRecord r = repo.getRecord(matchedRecordIds.first());
            String xml = masBld.update005(r.getOaiXml(), _005_Transformer);

            list = createNewRecord(r, "b", xml);
            // even though it is not merged, must still track the I<->O relationships!
            if (list.size() > 0) {
                // will get 1 agg. record back.
                TreeSet<Long> littleSet = new TreeSet<Long>();
                littleSet.add(r.getId());
                addToMasMergedRecordsMemory(list.get(0).getId(), littleSet);
            }

            LOG.debug("** create unmerged output record: "+list.get(0).getId()+" status="+list.get(0).getStatus());
        }
        results.addAll(list);
        return results;
    }

    /**
     *
     * @param matchedRecordIds - a newly found set of matching records
     * @param results - possibly already has OutputRecord data in it, to be added, or to be deleted when all is said and done.
     * @return - the OutputRecord list, with any necessary OutputRecord deletions added to it.
     */
    private List<OutputRecord> cleanupOldMergedOutputInfo(TreeSet<Long> matchedRecordIds, List<OutputRecord> results, boolean deleteOutputRecord) {
        LOG.debug("*** IN cleanupOldMergedOutputInfo!");
        for (Long input: matchedRecordIds) {
            //delete from memory;
            if (allBibRecordsI2Omap.containsKey(input)) {
                Long outputRecordToBeDeletedNum = getBibOutputId(input);  // grabs it out of I2O
                allBibRecordsI2Omap.remove(input);   // at end of this will re-add with proper new relationship
                allBibRecordsI2Omap_unpersisted.remove(input);
                mergedInRecordsList.remove(input);
                mergedInRecordsList_unpersisted.remove(input);
                allBibRecordsO2Imap.remove(outputRecordToBeDeletedNum);
                if (deleteOutputRecord) {
                    LOG.debug("must delete output record! id="+outputRecordToBeDeletedNum);
                    results = deleteOutputRecord(results, outputRecordToBeDeletedNum);
                }
            }
            // this is for processing NEW records, but, what if they hit the database, during a commit, then an update to the merge set expanding it
            //  happened?  is there a way to check the db without crushing performance?  (here is where merging at the END of getting all records
            //             would really help)
            //  initially, did not have this line in, and passed my unit tests, but I'm pretty sure the above situation will happen and the db must be
            //      checked.
            masDAO.deleteMergeMemberDetails(input);
        }
        return results;
    }

    private List<OutputRecord> deleteOutputRecord(List<OutputRecord> results, Long outputRecordToBeDeletedNum) {
        Record outputRecordToBeDeleted = getOutputRecord(outputRecordToBeDeletedNum);

        // you may have already deleted it, because 1 output record can be mapped to multiple input records
        if (outputRecordToBeDeleted != null) {
            LOG.debug("found outputRecordToBeDeleted in repo, id="+outputRecordToBeDeletedNum+" mark it deleted!");
            outputRecordToBeDeleted.setStatus(Record.DELETED);
            // if the records did not get persisted, will get null record back, or you may have already
            //  deleted it if it is part of a merge set.
            LOG.debug("** just set status to D for record: "+outputRecordToBeDeletedNum);
            results.add(outputRecordToBeDeleted);
        }
        // dark side code because you are peering into the implementation of the DAO
        else if (getRepositoryDAO().haveUnpersistedRecord(outputRecordToBeDeletedNum)) {
            LOG.debug("DID NOT found outputRecordToBeDeleted in repo, id="+outputRecordToBeDeletedNum+" dark side time!");
            getRepositoryDAO().deleteUnpersistedRecord(outputRecordToBeDeletedNum);
        }

        LOG.debug("** remove output record: "+outputRecordToBeDeletedNum);
        // you may have already deleted it, because 1 output record can be mapped to multiple input records
        if (outputRecordToBeDeleted != null && outputRecordToBeDeleted.getSuccessors() != null) {
            for (OutputRecord or : outputRecordToBeDeleted.getSuccessors()) {
                or.setStatus(Record.DELETED);
                results.add(or);
                Record _r = getRepository().getRecord(or.getId());
                String type = getXCRecordService().getType(_r);
                or.setType(type);
            }
        }
        return results;
    }

    private Record getOutputRecord(Long outputRecordToBeDeletedNum) {
        return getRepository().getRecord(outputRecordToBeDeletedNum);
    }

    private TreeSet<Long> populateMatchedRecordIds(MatchSet ms) {
        TreeSet<Long> matchedRecordIds = new TreeSet<Long>();
        for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
            String matchRuleKey = me.getKey();
            MatchRuleIfc matchRule = me.getValue();
            Set<Long> set = matchRule.determineMatches(ms);
            if (set !=null && !set.isEmpty()) {
                matchedRecordIds.addAll(set);
            }
        }

        // make sure to get all the disjoint merge sets in the total set, i.e. if this given input record
        // does not match something that another record it did match did, it needs to be in the total.
        matchedRecordIds = expandMatchedRecords(matchedRecordIds,allBibRecordsI2Omap,allBibRecordsO2Imap);

        return matchedRecordIds;
    }

    /**
     * for updates and deletes.
     * @param r - the record whose matchpoints must be removed
     */
    private void removeRecordsFromMatchers(InputRecord r) {
        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            FieldMatcher matcher = me.getValue();
            matcher.removeRecordFromMatcher(r);
        }
    }

    private MatchSet populateMatchSet(InputRecord r, SaxMarcXmlRecord smr) {
        MatchSet ms = new MatchSet(smr);
        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            String matchPointKey = me.getKey();
            FieldMatcher matcher = me.getValue();
            matcher.addRecordToMatcher(smr, r);  // is this the place to do this?  (was originally missing)
            // possibly need/want to add all match points 1st, then look for matches.
            ms.addMatcher(matchPointKey, matcher);
        }
        return ms;
    }


    /**
     *
     * need to look to see if the given match set impacts existing sets.  i.e if this set  is {1,47,50}
     * and we have existing sets {1,3} and {4,47} then we need a superset: {1,3,4,47,50} and need to
     * remove the existing sets {1,3}, {4,47}
     *
     * disjoint-set data structure?
     *
     * @param matchset
     * @param origMasMatchSetList
     * @return
     */
    private List<TreeSet<Long>> addToMatchSetList(TreeSet<Long> matchset,  final List<TreeSet<Long>> origMasMatchSetList) {
        if (matchset==null) {
            return origMasMatchSetList;
        }
        if (matchset.size() < 1) {   ///TODO shouldn't this be <2 ?
            return origMasMatchSetList;
        }

        LOG.debug("** addToMatchSetList, matchset length="+matchset.size()+" TOTAL matchset size ="+origMasMatchSetList.size());
        List<TreeSet<Long>> newMasMatchSetList = new ArrayList<TreeSet<Long>>();
        newMasMatchSetList.addAll(origMasMatchSetList);

        boolean added = false;
        for (TreeSet<Long> set: origMasMatchSetList) {
            for (Long number: matchset) {
                if (set.contains(number)) {
                    if (!set.containsAll(matchset)) {
                        newMasMatchSetList.remove(set);
                        set.addAll(matchset);
                        newMasMatchSetList.add(set);
                        LOG.debug("addToMatchSetList, post-merge!  set.contains("+number+") merged newMasMatchSetList set="+set);
                    }
                    else {
                        LOG.debug("addToMatchSetList, will not add in: "+matchset);
                    }
                    added = true;  // this flag means that we don't want this set added to the big list below
                    break;   // get you out of THIS set, but still must check the others.
                }
            }
        }
        //
        // the list of sets has to start somewhere, and if you don't find a set including some part
        // of your set, you must add your set explicitly.
        //
        if (!added) {
            LOG.debug("must add in: "+matchset);
            newMasMatchSetList.add(matchset);
        }
        LOG.debug("** addToMatchSetList, NEW TOTAL matchset size ="+newMasMatchSetList.size());
        return newMasMatchSetList;
    }

    /**
     * assumptions:
     * 1) sets are not null
     * @param oldMatchSet
     * @param newMatchSet
     * @return true if they are equivalent (same record ids in each)
     */
    private boolean areMatchSetsEquivalent(TreeSet<Long> oldMatchSet, TreeSet<Long> newMatchSet) {
        if (oldMatchSet.size() != newMatchSet.size()) {
            return false;
        }
        for (Long id: oldMatchSet) {
            if (!newMatchSet.contains(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * given sets {62,160} and {160,201} where 160 individually matches the other 2, but the other 2 don't directly match
     * each other, indirectly they do, so the output record needs to combine {62,160,201}
     *
     * @param matchedRecordIds
     * @param _mergedRecordsI2Omap
     * @param _mergedRecordsO2Imap
     * @return merged sets (dedup'd)
     */
    private TreeSet<Long> expandMatchedRecords(TreeSet<Long> matchedRecordIds,TLongLongHashMap _mergedRecordsI2Omap, Map<Long, TreeSet<Long>> _mergedRecordsO2Imap) {
        TreeSet<Long> results = new TreeSet<Long>();
        results.addAll(matchedRecordIds);

        for (Long input: matchedRecordIds) {
            Long output = _mergedRecordsI2Omap.get(input);
            if (output != null) {
                TreeSet<Long> temp = _mergedRecordsO2Imap.get(output);
                if (temp != null) {
                    results.addAll(_mergedRecordsO2Imap.get(output));
                }
            }
        }
        return results;
    }

    @Override
    protected boolean commitIfNecessary(boolean force, long processedRecordsCount) {
        try {
            LOG.debug("***mas.commitIfNecessary force="+force);
            TimingLogger.start("mas.commitIfNecessary");

            // break down timing logger more later if necessary.
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                final FieldMatcher matcher = me.getValue();
                matcher.flush(force);
                LOG.debug("flush matcher: "+matcher.getName());
            }

        } catch (Throwable t) {
            TimingLogger.stop("mas.commitIfNecessary");
            getUtil().throwIt(t);
        }
        if (!force) {
            return super.commitIfNecessary(force, 0);
        }
        // force == true:
        try {
            TimingLogger.start("MarcAggregationService.non-generic");

            masDAO.persistScores(scores_unpersisted);
            masDAO.persistLongMatchpointMaps(allBibRecordsI2Omap_unpersisted,
                    MarcAggregationServiceDAO.bib_records_table, false);
            masDAO.persistLongOnly(mergedInRecordsList_unpersisted, MarcAggregationServiceDAO.merged_records_table);

            //flush from memory now that these have been persisted to database
            scores_unpersisted.clear();
            mergedInRecordsList_unpersisted.clear();
            allBibRecordsI2Omap_unpersisted.clear();


            super.commitIfNecessary(true, 0);
            TimingLogger.stop("MarcAggregationService.non-generic");

            //transformation service does this, not sure why, so this is a placeholder.
//            getRepository().setPersistentProperty("inputBibs", inputBibs);
//            getRepository().setPersistentProperty("inputHoldings", inputHoldings);
        } catch (Throwable t) {
            getUtil().throwIt(t);
        } finally {
            TimingLogger.stop("mas.commitIfNecessary");
            TimingLogger.reset();
        }
        return true;
    }

    /**
     * called by parent at the right place, end of service, then run rules.
     * I've not found a use for the passed in arg, instead, just go get the service's record counts,
     * as need to compare input to service counts to output to service counts.
     */
    @Override
    protected void applyRulesToRecordCounts(RecordCounts mostRecentIncomingRecordCounts) {
        /*
         * default.properties contains starting point for properties fetched here.
         * rule_checking_enabled=true
         */
        // need to get repository record counts (incoming are all that exist) and normalization outgoing record counts, and run rules.
        if (MSTConfiguration.getInstance().getPropertyAsBoolean("rule_checking_enabled", false)) {
            final Logger LOG2 = getRulesLogger();

            try {
                RecordCounts rcIn;
                RecordCounts rcOut;
                try {
                    Service s = service;
                    if (s == null) {
                        LOG2.error("*** can not calculate record counts, no service found");
                        return;
                    }
                    rcIn = getRecordCountsDAO().getTotalIncomingRecordCounts(s.getName());
                    if (rcIn == null) {
                        LOG2.error("*** can not calculate record counts null recordCounts returned for service: " + s.getName());
                        return;
                    }
                    rcOut = getRecordCountsDAO().getTotalOutgoingRecordCounts(s.getName());
                    if (rcOut == null) {
                        LOG2.error("*** can not calculate record counts null recordCounts returned for service: " + s.getName());
                        return;
                    }
                } catch (Exception e) {
                    LOG2.error("*** can not calculate record counts: ", e);
                    return;
                }
                Map<String, AtomicInteger> counts4typeIn_t = rcIn.getCounts().get(RecordCounts.TOTALS);
                Map<String, AtomicInteger> counts4typeIn_b = rcIn.getCounts().get("b");
                Map<String, AtomicInteger> counts4typeIn_h = rcIn.getCounts().get("h");
                Map<String, AtomicInteger> counts4typeOut_t = rcOut.getCounts().get(RecordCounts.TOTALS);
                Map<String, AtomicInteger> counts4typeOut_b = rcOut.getCounts().get("b");
                Map<String, AtomicInteger> counts4typeOut_h = rcOut.getCounts().get("h");

                // TODO this belongs in dynamic script so it can be modified easily - pass array of values to script.
                LOG2.info("%%%");

                LOG2.info(MSTConfiguration.getInstance().getProperty("message.ruleCheckingHeaderAggregation"));// = Rules for Aggregation:
                LOG2.info(MSTConfiguration.getInstance().getProperty("message.ruleAggregationMATIA_geq_MATOA"));// = MA Total In Active >= MA Total Out Active
                String result = "";
                try {
                    if (counts4typeIn_t.get(RecordCounts.NEW_ACTIVE).get() == counts4typeOut_t.get(RecordCounts.NEW_ACTIVE).get()) {
                        result = " ** PASS **";
                    } else {
                        result = " ** FAIL **";
                    }
                    LOG2.info("MATIA=" + counts4typeIn_t.get(RecordCounts.NEW_ACTIVE) + ", MATOA=" + counts4typeOut_t.get(RecordCounts.NEW_ACTIVE) + result);
                } catch (Exception e2) {
                    LOG2.info("Could not calculate previous rule, null data");
                }

                LOG2.info(MSTConfiguration.getInstance().getProperty("message.ruleAggregationMABIA_geq_MABOA"));// = MA Bibs In Active >= MA Bibs Out Active
                try {
                    if (counts4typeIn_b.get(RecordCounts.NEW_ACTIVE).get() == counts4typeOut_b.get(RecordCounts.NEW_ACTIVE).get()) {
                        result = " ** PASS **";
                    } else {
                        result = " ** FAIL **";
                    }
                    LOG2.info("MABIA=" + counts4typeIn_b.get(RecordCounts.NEW_ACTIVE) + ", MABOA=" + counts4typeOut_b.get(RecordCounts.NEW_ACTIVE) + result);
                } catch (Exception e1) {
                    LOG2.info("Could not calculate previous rule, null data");
                }

                LOG2.info(MSTConfiguration.getInstance().getProperty("message.ruleAggregationMAHIA_eq_MAHOA"));// = MA Holdings In Active = MA Holdings Out Active
                try {
                    if (counts4typeIn_h.get(RecordCounts.NEW_ACTIVE).get() == counts4typeOut_h.get(RecordCounts.NEW_ACTIVE).get()) {
                        result = " ** PASS **";
                    } else {
                        result = " ** FAIL **";
                    }
                    LOG2.info("MAHIA=" + counts4typeIn_h.get(RecordCounts.NEW_ACTIVE) + ", MAHOA=" + counts4typeOut_h.get(RecordCounts.NEW_ACTIVE) + result);
                } catch (Exception e) {
                    LOG2.info("Could not calculate previous rule, null data");
                }
                LOG2.info("%%%");
            } catch (Exception e) {
                LOG.error("", e);
                LOG2.error("", e);
            }
        }
    }
}
