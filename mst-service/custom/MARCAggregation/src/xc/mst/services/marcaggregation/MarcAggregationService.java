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
    protected List<HashSet<Long>>                    masMatchSetList = null;

    /**
     * to sort/figure record of source, place items we are interested in, that are used to determine
     * record of source, in this map, as we go.
     */
    // TODO: I could be wrong, but it appears that since scores does not get loaded into memory at the start, we
    // probably don't need to worry about keeping a scores_unpersisted variable around. If this is true, then
    // we could remove the scores_unpersisted variable and replace it with the scores variable. This needs to be
    // verified with regression testing first, though...
    protected TLongObjectHashMap<RecordOfSourceData> scores             = null;
    protected TLongObjectHashMap<RecordOfSourceData> scores_unpersisted = null;

    /**
     * map output records to corresponding input records map
     * not only tracked merged records, 1 to many, but track unmerged 1 to 1
     */
    protected Map<Long, HashSet<Long>>               allBibRecordsO2Imap = null;

    /** map input records to corresponding output map,
     *  not only tracked merged records, many to 1, but track unmerged 1 to 1
     */
    protected TLongLongHashMap                       allBibRecordsI2Omap = null;
    protected TLongLongHashMap                       allBibRecordsI2Omap_unpersisted = null;
    
    protected TLongLongHashMap                       currentMatchSets = null;
    protected HashMap<Long, OutputRecord>				 currentMatchSetRecords = null;
    
    /**
     * We can save a lot of time if we keep track of changed matchpoints during updates.
     * If the matchpoints have changed, then we need to worry about changing matchets; 
     * otherwise, we just update the record and leave the matchset alone.
     */
    protected TLongLongHashMap changedMatchpoints = null;
    
    /** track input records that have been merged (>1 input to an output),
     */
    /**** WHY, OH WHY, DO WE NEED TO KEEP TRACK OF THESE when we already have allBibRecordsI2Omap and allBibRecordsO2Imap ?????
     * PLUS, IT'S SLOW TO MANIPULATE HUGE ArrayLists!
    protected List<Long>                             mergedInRecordsList = null;
    protected List<Long>                             mergedInRecordsList_unpersisted = null;
    ****/
    
    /** it's informative (debugging) to track a matchset's record of source
     */
    protected TLongLongHashMap						recordOfSourceMap = null;
    
    /** 
     * SaxMarcXmlRecord processing occurs often, perhaps on the same records multiple times.
     * Let's cache these as we preProcess.
     */
    protected TLongObjectHashMap<SaxMarcXmlRecord> SMRs             = null;
    private boolean cacheSMRs = true;

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
    private Transformer _001_Transformer;

    private static final String STATIC_TRANSFORM  = "createStatic.xsl";
    private static final String HOLDING_TRANSFORM = "stripHolding.xsl";
    private static final String _005_TRANSFORM    = "strip005.xsl";
    private static final String _001_TRANSFORM = "new001.xsl";
    
    /**
     * are we in DEBUG mode?
     */
    private boolean debugMode = false;
    
    private boolean insert001;
    private String insert001_prefix;
    private boolean insert003;
    private String insert003_value;

    /**
     * when commitIfNecessary is called, do we persist every n records (true), or do we wait until force == true? (false) at the end of processing)
     */
    public static boolean hasIntermediatePersistence = false; // for now, we are only flushing records at the very end, therefore do not waste any RAM or processing on intermediate objects
    
    /**
     * Is this the first time running through this service?
     */
    boolean firstTime = false;
    
    boolean isSetUp = false;

    private static final Logger LOG               = Logger.getLogger(MarcAggregationService.class);
private long flushTimer = System.currentTimeMillis();;
    private void clear_objects() {
        matcherMap = null;
        matchRuleMap = null;
        masBld = null;
        masRsm = null;
        masMatchSetList = null;

        scores             = null;
        scores_unpersisted = null;

        allBibRecordsO2Imap = null;

        allBibRecordsI2Omap = null;
        allBibRecordsI2Omap_unpersisted = null;

        /****mergedInRecordsList = null;
        mergedInRecordsList_unpersisted = null;****/
        
        recordOfSourceMap = null;
        
        currentMatchSets = null;
        
        isSetUp = false;
flushTimer = System.currentTimeMillis();;        
        System.gc();
    }

    /**
     * Setup record of source rules, setup transformers for modifying xml, validate the service, setup matchers and rules,
     * load known merged record details.
     */
    @Override
    public void setup() {
        try {
            marc21 = getFormatService().getFormatByName("marc21");
        } catch (DatabaseConfigException e) {
            LOG.error("Could not connect to the database with the parameters in the configuration file.", e);
        } catch (Exception e2) {
            LOG.error("Problem with init.", e2);
        }
        
        debugMode = config.getPropertyAsBoolean("debug_mode", false);
        cacheSMRs = config.getPropertyAsBoolean("cache_smr", true);
        
        // 001/003 manipulation
        insert001 = config.getPropertyAsBoolean("insert_001", false);
        insert001_prefix = config.getProperty("insert_001.prefix", "");
        insert003 = config.getPropertyAsBoolean("insert_003", false);
        insert003_value = config.getProperty("insert_003.value", "");
        
        // This service NO LONGER needs to retrieve records during the first run, therefore this index NO LONGER needs to get created initially!
        // However, we do still want to know if this is an initial run.
        firstTime = !getRepositoryDAO().ready4harvest(getRepository().getName());
        
        isSetUp = false; // save intensive setup for later
        
        doPreProcess = true;
    }

    // Too time-consuming to do on "real" setup(); fire this off only if we need to process records! (totalRecordCount > 0)
    private void doSetup() {
    	if (isSetUp) return;
    	
    	SMRs = new TLongObjectHashMap<SaxMarcXmlRecord>();
    	
        recordOfSourceMap = new TLongLongHashMap();

        masRsm = (RecordOfSourceManager) config.getBean("RecordOfSourceManager");
        masRsm.setupRecordOfSource();
        masBld = (MASMarcBuilder) config.getBean("MASMarcBuilder");
        staticTransformer = setupTransformer(getTransformForStaticFilename());
        _005_Transformer  = setupTransformer(getTransformFor005Filename());
        _001_Transformer  = setupTransformer(getTransformFor001Filename());

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
        
        TimingLogger.start("MAS.doSetup.setupMatchers");
        setupMatchers();
        TimingLogger.stop("MAS.doSetup.setupMatchers");

        TimingLogger.start("MAS.doSetup.setupMatchRules");
        setupMatchRules();
        TimingLogger.stop("MAS.doSetup.setupMatchRules");
        
        TimingLogger.start("MAS.doSetup.loadMasBibIORecords");
        allBibRecordsI2Omap = loadMasBibIORecords();
        TimingLogger.stop("MAS.doSetup.loadMasBibIORecords");
        LOG.info("allBibRecordsI2Omap size: "+allBibRecordsI2Omap.size());

        TimingLogger.start("MAS.doSetup.createMergedRecordsO2Imap");
        allBibRecordsO2Imap = createMergedRecordsO2Imap(allBibRecordsI2Omap);
        TimingLogger.stop("MAS.doSetup.createMergedRecordsO2Imap");

        /****TimingLogger.start("MAS.doSetup.loadMasMergedInputRecords");
        mergedInRecordsList = loadMasMergedInputRecords();
        TimingLogger.stop("MAS.doSetup.loadMasMergedInputRecords");
        LOG.info("mergedInRecordsList.size: "+ mergedInRecordsList.size());****/

        if (hasIntermediatePersistence) {
            allBibRecordsI2Omap_unpersisted = new TLongLongHashMap();
            /****mergedInRecordsList_unpersisted = new ArrayList<Long>();****/
        }
        
        currentMatchSets = new TLongLongHashMap();
        //currentMatchSetRecords = new HashMap<Long, OutputRecord>();

        changedMatchpoints = new TLongLongHashMap();

        isSetUp = true;
    }
    
    private void doSetup2() {
    	//make sure Setup() has been called first
    	doSetup();
    	
        //
        // Since matcher data is complete (after running through preProcess), we should flush to db
        //
        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            final FieldMatcher matcher = me.getValue();
            matcher.flush(true);
            LOG.debug("flush matcher: "+matcher.getName());
        }
        
        if (hasIntermediatePersistence) {
            masDAO.persistScores(scores_unpersisted);
            //flush from memory now that these have been persisted to database
            scores_unpersisted.clear();
        }
        else {
            masDAO.persistScores(scores);	
        }
    }

    /**
     *
     * map output records to corresponding input records map
     *
     * @param i_to_o_map
     * @return
     */
    private Map<Long, HashSet<Long>> createMergedRecordsO2Imap(TLongLongHashMap i_to_o_map) {
        LOG.info("start createMergedRecordsO2Imap");
        TreeMap<Long,HashSet<Long>> results = new TreeMap<Long, HashSet<Long>>();
        // obviously there can be multiple input records corresponding to one output record.
        for (Long in: i_to_o_map.keys()) {
            Long out = i_to_o_map.get(in);
            if (!results.containsKey(out)) {
                HashSet<Long> set = new HashSet<Long>();
                set.add(in);
                results.put(out, set);
            }
            else {
                // this output record already had at least one input record associated with it.
                HashSet<Long> _set = results.get(out);
                _set.add(in);
                results.put(out, _set);
            }
        }
        LOG.info("done createMergedRecordsO2Imap");
        return results;
    }

    /**
     * load from the database
     * @return known merged records that were persisted, it returns all bibs i to o
     */
    private TLongLongHashMap loadMasBibIORecords() {
        return masDAO.getBibRecordsCache();
    }


    /**
     * load from the database
     * @return known merged records that were persisted-input records that are part of a merge set (>1 corresponds to an output record)
     */
    private List<Long> loadMasMergedInputRecords() {
        return masDAO.getMergedInputRecordsCache();
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
        masMatchSetList = new ArrayList<HashSet<Long>>();
        scores = new TLongObjectHashMap<RecordOfSourceData>();   /// TODO load what you have in the db!

        if (hasIntermediatePersistence) {
            scores_unpersisted = new TLongObjectHashMap<RecordOfSourceData>();
        }
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
            m.load(firstTime);
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

    protected String getTransformFor001Filename() {
        return _001_TRANSFORM;
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
    public List<HashSet<Long>> getCurrentMatchSetList() {
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
        // evaluate match sets here?
        LOG.info("** START processComplete!");

        masDAO.createIndicesIfNecessary();
        
        clear_objects();
    }

/***
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
***/
    
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
    
    // turn on the two-phase "pre process" hook
    public boolean doPreProcess() { return true; }
    
    public void preProcess(InputRecord r) { 
        TimingLogger.start("preProcess");
        
        // we do the "real" setup here, since we now know that we will need to process at least 1 record!
    	doSetup();

		String inputType = r.getType();
		boolean inputDeleted = r.getDeleted();
    	SaxMarcXmlRecord smr = null;
    	
    	// special case for deleted recs
    	if (inputDeleted) {
    		if (inputType.equals("b")) {
    			// Save a lot of processing time by not having to worry about update info/scoring
    			if (! firstTime) removeRecordsFromMatchers(r);    
    		}
    		TimingLogger.stop("preProcess");
            return;
    	}
    	
    	try {    	    	       
    		smr = new SaxMarcXmlRecord(r.getOaiXml());
    		smr.setRecordId(r.getId());
    		if(cacheSMRs && inputType.equals("b")) SMRs.put(r.getId(), smr);
        } catch (Throwable t) {
            LOG.error("error pre-processing record :" + r + " type: " + inputType + " isDeleted? " + inputDeleted, t);
            TimingLogger.stop("preProcess");
            return;
        }
    	
        // Get the Leader 06. This will allow us to determine the record's type
        final char leader06 = smr.getLeader().charAt(6);

        // check if the record is a bibliographic record
        if ("abcdefghijkmnoprt".contains("" + leader06)) {
        	
        	// is it a new record? it is, if it doesn't exist in our map
        	if (! allBibRecordsI2Omap.containsKey(r.getId())) {
        		LOG.info("MAS: preProcess() found a new record (i.e., with changed matchpoints for record): " + r.getId());
        		changedMatchpoints.put(r.getId(), 1L);        		
        	} else {        	
	        	if (matchpointsHaveChanged(r, smr)) {
	        		LOG.info("MAS: preProcess() found an updated record with changed matchpoints for record: " + r.getId());
	        		changedMatchpoints.put(r.getId(), 1L);
	        	} else {
	        		LOG.info("MAS: preProcess() found an updated record with NO changed matchpoints (good! we can optimize!) for record: " + r.getId());        		
	        	}
        	}
        	
        	TimingLogger.start("preProcess.removeFromMatchers");
        	// Save a lot of processing time by not having to worry about update info/scoring
        	if (! firstTime) removeRecordsFromMatchers(r);
        	TimingLogger.stop("preProcess.removeFromMatchers");
        	
        	if (r.getStatus() == Record.DELETED) {
            	// we don't need to do anything with these        		
        	} else {            	
        		addAndPersistScores(r, smr);
        	}
        }
        TimingLogger.stop("preProcess");

    }
    
    private SaxMarcXmlRecord getSMR(InputRecord r) {
    	if (cacheSMRs && SMRs.contains(r.getId())) {
    		//LOG.info("MAS: getSMR() returned a cached record: " + r.getId());
    		return SMRs.get(r.getId());
    	}
    	SaxMarcXmlRecord smr = null;
    	try {    	    	       
    		smr = new SaxMarcXmlRecord(r.getOaiXml());
    		smr.setRecordId(r.getId());
    		if (cacheSMRs) SMRs.put(r.getId(), smr);
    		return smr;
        } catch (Throwable t) {
            LOG.error("Couldn't create SaxMarcXmlRecord for id="+r.getId()+" error="+t);
            return null;
        }    	
    }
    

    private void addAndPersistScores (InputRecord r, SaxMarcXmlRecord smr) {
    	TimingLogger.start("addAndPersistScores");
        // get record of source data for this bib
        //  (only a bib would be a record of source)
        final char leaderByte17 = smr.getLeader().charAt(17);
        final int rSize = r.getOaiXml().getBytes().length;
        
    	TimingLogger.start("addAndPersistScores.addScores");
        scores.put(r.getId(), new RecordOfSourceData(leaderByte17, rSize));
        if (hasIntermediatePersistence) {
            scores_unpersisted.put(r.getId(), new RecordOfSourceData(leaderByte17, rSize));
        }
    	TimingLogger.stop("addAndPersistScores.addScores");
        
    	TimingLogger.start("addAndPersistScores.addToMatchers");
        addRecordToMatchers(r, smr);
    	TimingLogger.stop("addAndPersistScores.addToMatchers");
    	
    	TimingLogger.stop("addAndPersistScores");
    }
    
    public void preProcessCompleted() {
        doSetup2();
        
        if (firstTime) masDAO.createIndicesIfNecessary();
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
        			
long tnow = System.currentTimeMillis();	
if (tnow - flushTimer >= 3600000) {
	flushTimer = tnow;
	TimingLogger.reset();
}

			String inputType = r.getType();
			boolean inputDeleted = r.getDeleted();
			LOG.info("MAS:  process record: "+r.getId()+", type:"+inputType+", getDeleted:"+inputDeleted);
			
			// special case for deleted recs
			if (inputDeleted) {
				if (inputType.equals("b")) {
            		results = processBibDelete(r);
				} else if (inputType == "h")  {
            		results = processHoldDelete(r);
            	}
				return results;
			}

            SaxMarcXmlRecord smr = null;
            if (inputType.equals("b")) {
            	smr = getSMR(r);
            } else {
            	smr = new SaxMarcXmlRecord(r.getOaiXml());
            	smr.setRecordId(r.getId());
            }
            
            // Get the Leader 06. This will allow us to determine the record's type
            final char leader06 = smr.getLeader().charAt(6);
            
            // determine type
            if ("abcdefghijkmnoprt".contains("" + leader06)) {
                type = "b";
            } else if ("uvxy".contains("" + leader06)) {
                type = "h";
            } else if (leader06 == 'z') {
            	type = "z";
            }
                       
            if (r.getStatus() != Record.DELETED) {

                // check if the record is a bibliographic record
            	if (type == "b") {
                    TimingLogger.start("bib steps");
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
            	else if (type == "h") {
                    TimingLogger.start("hold steps");
                    //
                    // setting this here increments this type in the record counts when
                    // incremented in GenericMetadataService.process() -- else it then
                    // increments RecordCounts.OTHER
                    //
                    ((Record) r).setType(type);

                    results = processHolding(r, smr, inputRepo);
                    TimingLogger.stop("hold steps");
                }
            	else if (type == "z") {
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
            	// These *should* have been handled earlier...
            	LOG.error("We probably should have processed this record earlier (based on incoming type); record: " + r);
				if (type == "b") {
            		results = processBibDelete(r);
				} else if (type == "h")  {
            		results = processHoldDelete(r);
            	}

            }
            
/*            
            if (results != null && results.size() != 1) {
                // TODO increment records counts no output
                //     (_IF_ database column added to record counts to help with reconciliation of counts)
//////////////////
                // BUG!, the above if statement is WRONG, TODO need to figure out what constitutes a no output error?
                // addMessage(r, 103, RecordMessage.ERROR);  // no output
            }
*/
            
            return results;

        } catch (Throwable t) {
            LOG.error("error processing record with id:" + ((Record) r).getId(), t);
            // TODO increment records counts no output
            //        (_IF_ database column added to record counts to help with reconciliation of counts)
            addMessage(r, 103, RecordMessage.ERROR);  // no output
        }
        return null;
    }

    protected Repository getInputRepo() {
        return this.inputRepo;
    }


    /**
     * will use these data structures as the basis to update DAO, should always be up to date.
     * @param outputRecordId
     * @param mergedInputRecordSet
     */
    private void addToMasMergedRecordsMemory(Long outputRecordId, HashSet<Long> mergedInputRecordSet) {
        TimingLogger.start("addToMasMergedRecordsMemory");

        for (Long num: mergedInputRecordSet) {
            allBibRecordsI2Omap.put(num,outputRecordId);
            if (hasIntermediatePersistence) {
                allBibRecordsI2Omap_unpersisted.put(num,outputRecordId);
            }
        }
        allBibRecordsO2Imap.put(outputRecordId, mergedInputRecordSet);

        /****if (mergedInputRecordSet.size() > 1) {
            for (Long num: mergedInputRecordSet) {
                mergedInRecordsList.add(num);
                if (hasIntermediatePersistence) {
                    mergedInRecordsList_unpersisted.add(num);
                }
            }
        }****/
        TimingLogger.stop("addToMasMergedRecordsMemory");
    }


    /**
     * Note - createStatic => strip 001/003/035,  create 035, save 035 (as dynamic)
     *
     * @param set of record ids to merge
     * @param repo  seems as though we have frowned on this in the past, but with this
     *              service can we avoid looking up and using record content from the source?
     * @return returns static xml + saved dynamic content (included or not?)
     */
    private String mergeBibSet(InputRecord theSrcRecord, HashSet<Long> set, Repository repo) {
        TimingLogger.start("mergeBibSet");

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
        TimingLogger.stop("mergeBibSet");
        return oaiXml;
    }
    
    private String injectNew001(long id, String xml) {
    	String new001 = "";
    	String new003 = "";
    	if (insert001) {
    		new001 = insert001_prefix + id;
    	}
    	if (insert003) {
    		new003 = insert003_value;
    	}
    	return masBld.getXmlNew001(xml, _001_Transformer, new001, new003);
    }

    private List<OutputRecord> createNewBibRecord(InputRecord theSrcRecord, String oaiXml, HashSet<Long> set) {
        TimingLogger.start("createNewBibRecord");

        List<OutputRecord> list = createNewRecord(theSrcRecord, "b", oaiXml);

        // now that we have created a new record successfully, update the data structure to track the merged records.
        if (list.size() > 0) {
            // will get 1 agg. record back.
            addToMasMergedRecordsMemory(list.get(0).getId(), set);
        }
        TimingLogger.stop("createNewBibRecord");
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
        
        newXml = injectNew001(aggRecord.getId(), newXml);

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
        HashSet<Long> set = allBibRecordsO2Imap.get(out.getId());
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
    private HashSet<Long> deleteAllMergeDetails(InputRecord r) {
    	TimingLogger.start("deleteAllMergeDetails");

        //
        //
        // 1st, delete from the database
        LOG.debug("&&& in deleteAllMergeDetails for "+r.getId());
        masDAO.deleteAllMASRecordDetails(r.getId());

        // and delete this records matchpoint data
        removeRecordsFromMatchers(r);

        // 2nd, get the related merged records:
        HashSet<Long> formerMatchSet = getCurrentMatchSetForRecord(r);
        LOG.info("MAS:  deleteAllMergeDetails formerMatchSet [" + formerMatchSet.size() + "] =  getCurrentMatchSetForRecord(" + r.getId() + ": "+formerMatchSet);    	                        

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
    	TimingLogger.stop("deleteAllMergeDetails");

        return formerMatchSet;
    }

    private Long getBibOutputId(InputRecord r) {
        return getBibOutputId(r.getId());
    }

    private Long getBibOutputId(Long id) {
    	if (allBibRecordsI2Omap.containsKey(id)) return allBibRecordsI2Omap.get(id);
    	LOG.info("MAS: getBibOutput(" + id + ") came up empty.");
    	return null;
    }

    private HashSet<Long> getCurrentMatchSetForRecord(InputRecord r) {
        Long outputId = getBibOutputId(r);
        if (outputId != null) return getCurrentMatchSetForRecordId(outputId);
        return new HashSet<Long>();
    }
    	
    private HashSet<Long> getCurrentMatchSetForRecordId(Long id) {
        HashSet<Long> matchSet = allBibRecordsO2Imap.get(id);
        if (matchSet == null) {
            matchSet = new HashSet<Long>();
        }
        return matchSet;
    }



    private List<HashSet<Long>> findMatchSets(HashSet<Long> formerMatchSet) {
	    
        TimingLogger.start("findMatchSets");

        List<HashSet<Long>> listOfMatchSets = new ArrayList<HashSet<Long>>();
                
    	for (Long id: formerMatchSet) {

    		TimingLogger.start("findMatchSets.populateMatchedRecords");
            Record r = getInputRepo().getRecord(id);
            
            // do not process deletes (faster to ignore, plus it's possible the XML is empty, which will cause issues below)            
            if (r.getDeleted()) {
            	LOG.info("MAS:  findMatchSets, this record is marked for deletion, therefore ignore it, id: "+r.getId());            	
            	continue;
            }
            
            SaxMarcXmlRecord smr = getSMR(r);
            if (smr == null) {
            	LOG.error("MAS:  findMatchSets, couldn't create SaxMarcXmlRecord: "+r.getId()+ ", XML: "+r.getOaiXml());
            	continue;
            }

            MatchSet ms = populateMatchSet(r, smr);
            HashSet<Long> newMatchedRecordIds = populateMatchedRecordIds(ms);
            //
            // populateMatchedRecordIds does not return the record itself as part of the match set,
            // in this case I want it in the set.
            //
            newMatchedRecordIds.add(id);
    		TimingLogger.stop("findMatchSets.populateMatchedRecords");
            
            // We need to account for associativity,
    		TimingLogger.start("findMatchSets.expandMatchedRecords");
            newMatchedRecordIds = expandMatchedRecords(newMatchedRecordIds);
    		TimingLogger.stop("findMatchSets.expandMatchedRecords");
            
    		TimingLogger.start("findMatchSets.buildListOfMatchsets");
            if (!listOfMatchSets.contains(newMatchedRecordIds)) {
                // come up with a bare bones set of new match sets, I am guessing most of the time it will be 1 set
                listOfMatchSets = addToMatchSetList(newMatchedRecordIds,  listOfMatchSets);
            }
    		TimingLogger.stop("findMatchSets.buildListOfMatchsets");
        }
        TimingLogger.stop("findMatchSets");

    	return listOfMatchSets;
    }
    
    private List<OutputRecord> remerge(List<OutputRecord> results, HashSet<Long> matchset) {
		TimingLogger.start("remerge");
	    // clean up any previous merge data for all elements in this new aggregated matchset
	    List<OutputRecord> deletes = null;
		if (! firstTime) {
		    deletes = new ArrayList<OutputRecord>();
		    deletes = cleanupOldMergedOutputInfo(matchset, deletes, true);
		}
	
	    // create the aggregated record (which may or may not be a set of matched records)
	    List<OutputRecord> matchsetResults = new ArrayList<OutputRecord>();
	    matchsetResults = mergeOverlord(matchsetResults, matchset);
	    
	    // let's return the same output record for any and all future elements of this newly aggregated matchset
	    if (matchset.size() > 1) {
	        OutputRecord outputRecord = matchsetResults.get(0);
	        long outputRecordId = outputRecord.getId();
	        for (long id : matchset) {
	        	currentMatchSets.put(id, outputRecordId);
	        	((Record)outputRecord).addPredecessor(getInputRepo().getRecord(id));
	        }
	        ////currentMatchSetRecords.put(results.get(0).getId(), results.get(0));
	    }
	    
	    if (!firstTime && deletes != null && deletes.size() > 0) matchsetResults.addAll(deletes);
	    
	    if (matchsetResults.size() > 0) results.addAll(matchsetResults);
        
        TimingLogger.stop("remerge");

        return results;

    }
    
/****    
    private List<OutputRecord> OLDremerge(HashSet<Long> formerMatchSet) {
        TimingLogger.start("remerge");

        List<HashSet<Long>> listOfMatchSets = new ArrayList<HashSet<Long>>();
        
        // First go through the matchset and look for any deleted records we haven't yet
        // encountered, then delete the merge data so that we do not include them in the remerge.
        //
        // FYI: It's possible that a record in this set was deleted in a prior service (e.g., Normalization),
        // but we haven't encountered it yet here in Aggregation (we will eventually),
        // however it is still present in our matchset data. We must account for this (because certain
        // assumptions will not be true, such as r.getOaiXmml(), because the record is deleted!).
        // It should be safe to delete the matchset data (formally) now. Later, when the deleted record
        // is encountered, it should be safe, right?
        List<Long> removeThese = new ArrayList<Long>();
        for (Long id: formerMatchSet) {
            Record r = getInputRepo().getRecord(id);
            if (r.getDeleted()) {
            	deleteAllMergeDetails(r);
            	removeThese.add(id);
            }
        }
        if (removeThese.size() > 0) formerMatchSet.removeAll(removeThese);
        
    	for (Long id: formerMatchSet) {

            Record r = getInputRepo().getRecord(id);
            
            SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());
            smr.setRecordId(r.getId());

            MatchSet ms = populateMatchSet(r, smr);
            HashSet<Long> newMatchedRecordIds = populateMatchedRecordIds(ms);
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
        for (HashSet<Long> matchset: listOfMatchSets) {
            results = mergeOverlord(results, matchset);
        }
        // will pred-succ relationships automatically be correct? --> it seems so.
        
        TimingLogger.stop("remerge");

        return results;
    }
***/
    
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
        int numSuccessors = r.getSuccessors().size();
        
        boolean processNew = true;
        if (numSuccessors > 0) {
        	// if all successors of this incoming record are deletes, then this service has completely "forgotten" about it;
        	// treat it as if it were a brand new record.
        	for (OutputRecord succ : r.getSuccessors()) {
        		if (! succ.getDeleted()) {
        			processNew = false;
        			break;
        		}
        	}
        }
        
        if (processNew) {
            // NEW-ACTIVE
            TimingLogger.start("processBib.processBibNewActive");
            results = processBibNewActive(r, smr, repo);
            TimingLogger.stop("processBib.processBibNewActive");

        } else {
            // UPDATE-ACTIVE
            TimingLogger.start("processBib.processBibUpdateActive");
            results = processBibUpdateActive(r, smr, repo);
            TimingLogger.stop("processBib.processBibUpdateActive");
        }

        return results;
    }
    
    // A safe way to retrieve a record, whether it has already been persisted or not.
    protected Record getRecord(Long recordId) {
        // We may need to access in-memory (not yet persisted) records.
        // MST doesn't provide a safe framework for manipulating in-memory objects; therefore, we will persist all records first!
        if (getRepositoryDAO().haveUnpersistedRecord(recordId)) {
            super.commitIfNecessary(true, 0);
        }
        Record r = getRepository().getRecord(recordId);
        return r;
    }


    private List<OutputRecord> processBibUpdateActive(InputRecord r, SaxMarcXmlRecord smr, Repository repo) {
    	LOG.info("MAS:  processBibUpdateActive: "+r.getId());
    	List<OutputRecord> results = new ArrayList<OutputRecord>();
    	
    	// If the match points are the same, then we do not need to worry about the match set changing; just update the record payload
    	if (! changedMatchpoints.contains(r.getId())) {
    		LOG.info("MAS:  processBibUpdateActive: matchpoints have NOT changed; going to re-use the current matchset.");
    		OutputRecord oldOutput;
        	String xml;

    		HashSet<Long> formerMatchSet = getCurrentMatchSetForRecord(r);
    		if (formerMatchSet.size() > 0) {
    			Long oldOutputId = getBibOutputId(formerMatchSet.iterator().next());
                oldOutput = getRecord(oldOutputId);
                
                InputRecord record = masRsm.getRecordOfSourceRecord(formerMatchSet, repo, scores);
                xml = mergeBibSet(record, formerMatchSet, repo);
    		} else {
				// Get the record which was processed from the record we just processed
	            // (any of the matchset input records should map to the same output record, right?)
	            oldOutput = r.getSuccessors().get(0);
	            
	            xml = masBld.update005(r.getOaiXml(), _005_Transformer);
    		}
    		
    		oldOutput.setMode(Record.STRING_MODE);
            oldOutput.setFormat(marc21);
            oldOutput.setStatus(Record.ACTIVE);

            // Set the XML to the updated XML - remerged and reconstituted the xml

            // Do NOT create a new record, update, the OLD record!
            // Set the XML to the updated XML - reconstituted the xml
            oldOutput.setOaiXml(xml);
         
            // we need the clear out the old updatedAt value
            // so that the MST will correctly set it later (when repo is persisted)
            // issue: mst-549
            ((Record) oldOutput).setUpdatedAt( null );
            
            // Add the updated record
            oldOutput.setType("b");
            results.add(oldOutput);    		
    		
    	// If the match points change at all, we must re-match/merge all records in the set
    	} else {
    		LOG.info("MAS:  processBibUpdateActive: matchpoints HAVE changed; need to re-match/merge, i.e., delete-then-re-add.");

	    	results = processBibDelete(r);
	
	    	// processBibDelete nukes all the record's score data; must re-add it
	    	addAndPersistScores(r, smr);
	    	
	    	results.addAll(processBibNewActive(r, smr, repo));
    	}
    	
    	return results;
    }

    
    private List<OutputRecord> processHoldDelete(InputRecord recordIn) {
    	// lifted from MarcNormalization
        List<OutputRecord> results = new ArrayList<OutputRecord>();
        TimingLogger.start("processRecord.getDeleted");
        List<OutputRecord> successors = recordIn.getSuccessors();

        // If there are successors then the record exist and needs to be deleted. Since we are
        // deleting the record, we need to decrement the count.
        if (successors != null && successors.size() > 0) {
            inputRecordCount--;
        }

        // Handle reprocessing of successors
        for (OutputRecord successor : successors) {
            successor.setStatus(Record.DELETED);
            successor.setFormat(marc21);
            results.add(successor);
        }
        
        return results;
    }
    
    private List<OutputRecord> processBibDelete(InputRecord r) {
    	LOG.info("MAS:  processBibDelete: "+r.getId());    	        
    	List<OutputRecord> results = new ArrayList<OutputRecord>();

        if (r.getSuccessors().size() == 0) {
            // NEW-DELETED
            //
            // nothing to do?  should we still double-check datastructures and db?
        } else {
            TimingLogger.start("processRecord.updateDeleted");
            // UPDATE-DELETED
            //
            // ( mostly ) directly lifted from norm...
            //
            boolean isAbibWithSuccessors = false;
            results = new ArrayList<OutputRecord>();
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
                HashSet<Long> formerMatchSet = deleteAllMergeDetails(r);
                LOG.info("MAS:  processBibDelete formerMatchSet [" + formerMatchSet.size() + "] = deleteAllMergeDetails: "+formerMatchSet);    	                
                for (long formerId: formerMatchSet) {
                	currentMatchSets.remove(formerId);                	
                	recordOfSourceMap.remove(formerId);
                }
                currentMatchSets.remove(r.getId());
            	recordOfSourceMap.remove(r.getId());

            	formerMatchSet.remove(r.getId());
                
                if (formerMatchSet.size() > 0) {
                	List<HashSet<Long>> listOfMatchSets = findMatchSets(formerMatchSet);
                	for (HashSet<Long> matchset: listOfMatchSets) {
                		LOG.info("MAS:  processBibDelete listOfMatchSets [" + matchset.size() + "]  = findMatchSets: "+matchset);    	                                	
                		results = remerge(results, matchset);
                	}
                }
                
            }
            TimingLogger.stop("processRecord.updateDeleted");
        }
        return results;
    }
    
    
    private List<OutputRecord> processBibNewActive(InputRecord r, SaxMarcXmlRecord smr, Repository repo) {
    	LOG.info("MAS:  processBibNewActive: "+r.getId());    	
        List<OutputRecord> results = new ArrayList<OutputRecord>();

        if (currentMatchSets.contains(r.getId())) {
        	LOG.info("MAS:  processBibNewActive currentMatchSets already processed this bib!: "+r.getId());    	                                	
            // we already processed this record; it was included in a matchset (aggregated record)
        	//////results.add(currentMatchSetRecords.get(currentMatchSets.get(r.getId())));
        	return results;
        }
        
        MatchSet ms = getMatchSet(smr);
        HashSet<Long> matchedRecordIds = populateMatchedRecordIds(ms);
        LOG.info("MAS:  processBibNewActive matchedRecordIds [" + matchedRecordIds.size() + "] =  populateMatchedRecordIds: "+matchedRecordIds);    	                                	        
        matchedRecordIds.add(r.getId());
        
        // We need to account for associativity,
		TimingLogger.start("findMatchSets.expandMatchedRecords");
		matchedRecordIds = expandMatchedRecords(matchedRecordIds);
		LOG.info("MAS:  processBibNewActive matchedRecordIds [" + matchedRecordIds.size() + "] =  expandMatchedRecords: "+matchedRecordIds);    	                                	        		
		TimingLogger.stop("findMatchSets.expandMatchedRecords");
        
        return remerge(results, matchedRecordIds);
    }

    /**
     * do some of the housekeeping required with merging, really a premerge process, then hand off to mergeBibSet, if necessary.
     *
     * @param results - may already have some results from a prior merge or some deleted records from prior cleanup of unmerged records.
     * @param matchedRecordIds - the match set, will always have at least one element.
     * @param repo - the input records
     * @return - the prior passed in results + new results of OutputRecord resulting from the merge
     */
    private List<OutputRecord> mergeOverlord(List<OutputRecord> results, HashSet<Long> matchedRecordIds) {
        TimingLogger.start("mergeOverlord");

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
            //masMatchSetList = addToMatchSetList(matchedRecordIds, masMatchSetList);

            InputRecord record = masRsm.getRecordOfSourceRecord(matchedRecordIds, getInputRepo(), scores);
            String xml = mergeBibSet(record, matchedRecordIds, getInputRepo());
            list = createNewBibRecord(record, xml, matchedRecordIds); // this method calls addToMasMergedRecordsMemory
            recordOfSourceMap.put(list.get(0).getId(), record.getId());

            LOG.debug("** create merged output record: "+list.get(0).getId()+" status="+list.get(0).getStatus());

        }
        else {
            InputRecord r = getInputRepo().getRecord(matchedRecordIds.iterator().next());
            String xml = masBld.update005(r.getOaiXml(), _005_Transformer);

            list = createNewRecord(r, "b", xml);
            // even though it is not merged, must still track the I<->O relationships!
            if (list.size() > 0) {
                // will get 1 agg. record back.
                HashSet<Long> littleSet = new HashSet<Long>();
                littleSet.add(r.getId());
                addToMasMergedRecordsMemory(list.get(0).getId(), littleSet);
            }

            LOG.debug("** create unmerged output record: "+list.get(0).getId()+" status="+list.get(0).getStatus());
        }
        results.addAll(list);
        TimingLogger.stop("mergeOverlord");
        return results;
    }

    // expects a set of matched record ids, i.e., > 1
    private HashSet<Long> expandMatchedRecords(HashSet<Long> matchedRecordIds) {
        
        // matchedRecordIds contains all matches for incoming record. However, we also need to check the matched
        // records themselves (not including the incoming record we already checked), because we need to
        // account for associativity,
        if (matchedRecordIds.size() > 1) {
        	HashSet<Long> results = new HashSet<Long>();
        	for (Long match: matchedRecordIds) {
	        	Record r = getInputRepo().getRecord(match);
	        	SaxMarcXmlRecord smr = getSMR(r);
	            
	            MatchSet ms = getMatchSet(smr);
	            HashSet<Long> matchedRecordIdsAssoc = populateMatchedRecordIds(ms);
	           
	            if (matchedRecordIdsAssoc.size() > 0) results.addAll(matchedRecordIdsAssoc);
	        }
        	if (results.size() > 0) matchedRecordIds.addAll(results);

        }
        return matchedRecordIds;
    }
    
    /**
     *
     * @param matchedRecordIds - a newly found set of matching records
     * @param results - possibly already has OutputRecord data in it, to be added, or to be deleted when all is said and done.
     * @return - the OutputRecord list, with any necessary OutputRecord deletions added to it.
     */
    private List<OutputRecord> cleanupOldMergedOutputInfo(HashSet<Long> matchedRecordIds, List<OutputRecord> results, boolean deleteOutputRecord) {
        LOG.debug("*** IN cleanupOldMergedOutputInfo!");
        for (Long input: matchedRecordIds) {
            //delete from memory;
            if (allBibRecordsI2Omap.containsKey(input)) {
                Long outputRecordToBeDeletedNum = getBibOutputId(input);  // grabs it out of I2O
                allBibRecordsI2Omap.remove(input);   // at end of this will re-add with proper new relationship
                if (hasIntermediatePersistence) {
                    allBibRecordsI2Omap_unpersisted.remove(input);
                }
                /****mergedInRecordsList.remove(input);
                if (hasIntermediatePersistence) {
                    mergedInRecordsList_unpersisted.remove(input);
                }****/
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
        Record outputRecordToBeDeleted = getRecord(outputRecordToBeDeletedNum);

        // you may have already deleted it, because 1 output record can be mapped to multiple input records
        if (outputRecordToBeDeleted != null) {
            LOG.debug("found outputRecordToBeDeleted in repo, id="+outputRecordToBeDeletedNum+" mark it deleted!");
            outputRecordToBeDeleted.setStatus(Record.DELETED);
            
            // MST-550: Solr facets incorrect for deleted records.
            // This is because we weren't updating the timestamp.
            // We do this by clearing out the old (incorrect) value:
            outputRecordToBeDeleted.setUpdatedAt( null );
            
            // if the records did not get persisted, will get null record back, or you may have already
            //  deleted it if it is part of a merge set.
            LOG.debug("** just set status to D for record: "+outputRecordToBeDeletedNum);
            results.add(outputRecordToBeDeleted);
        }

        LOG.debug("** remove output record: "+outputRecordToBeDeletedNum);
        // you may have already deleted it, because 1 output record can be mapped to multiple input records
        if (outputRecordToBeDeleted != null && outputRecordToBeDeleted.getSuccessors() != null) {
            for (OutputRecord or : outputRecordToBeDeleted.getSuccessors()) {
                or.setStatus(Record.DELETED);
                ((Record) or).setUpdatedAt(null);
                results.add(or);
                Record _r = getRecord(or.getId());
                String type = getXCRecordService().getType(_r);
                or.setType(type);
            }
        }
        return results;
    }

    private HashSet<Long> populateMatchedRecordIds(MatchSet ms) {
    	TimingLogger.start("populateMatchedRecordIds");
        HashSet<Long> matchedRecordIds = new HashSet<Long>();
        for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
            String matchRuleKey = me.getKey();
            
            TimingLogger.start("populateMatchedRecordIds.determineMatches." + matchRuleKey);
            MatchRuleIfc matchRule = me.getValue();
            Set<Long> set = matchRule.determineMatches(ms);
            TimingLogger.stop("populateMatchedRecordIds.determineMatches." + matchRuleKey);
            
            if (set !=null && !set.isEmpty()) {
                matchedRecordIds.addAll(set);
            }
        }
    	TimingLogger.stop("populateMatchedRecordIds");


/***
 * We are now using a 2-pass system where in pass 1 we apply scores to all incoming records, and in pass 2 we
 * are then able to find ALL matching records at once, by calling this method, populateMatchedRecordIds, for each
 * element in the set. 
 * 
        // make sure to get all the disjoint merge sets in the total set, i.e. if this given input record
        // does not match something that another record it did match did, it needs to be in the total.
        matchedRecordIds = expandMatchedRecords(matchedRecordIds,allBibRecordsI2Omap,allBibRecordsO2Imap);
    	TimingLogger.stop("populateMatchedRecordIds.populateMatchedRecordIds");
***/
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
    
    private void addRecordToMatchers(InputRecord r, SaxMarcXmlRecord smr) {
        TimingLogger.start("addRecordToMatchers");

        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            FieldMatcher matcher = me.getValue();
            matcher.addRecordToMatcher(smr, r); 
        }
        TimingLogger.stop("addRecordToMatchers");
    }

    private boolean matchpointsHaveChanged(InputRecord r, SaxMarcXmlRecord smr) {
        TimingLogger.start("matchpointsHaveChanged");

        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            FieldMatcher matcher = me.getValue();
            if (matcher.matchpointsHaveChanged(smr, r)) {
                TimingLogger.stop("matchpointsHaveChanged");
                LOG.info("Matcher: " + matcher.getName() + " noticed that matchpoints have changed.");
            	return true; 
            }
        }
        TimingLogger.stop("matchpointsHaveChanged");
       return false;
    }
    
    private MatchSet getMatchSet(SaxMarcXmlRecord smr) {
        TimingLogger.start("getMatchSet");

        MatchSet ms = new MatchSet(smr);
        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            String matchPointKey = me.getKey();
            FieldMatcher matcher = me.getValue();
            ms.addMatcher(matchPointKey, matcher);
        }
        TimingLogger.stop("getMatchSet");
        
        return ms;
    }


    private MatchSet populateMatchSet(InputRecord r, SaxMarcXmlRecord smr) {
        TimingLogger.start("populateMatchSet");

        MatchSet ms = new MatchSet(smr);
        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            String matchPointKey = me.getKey();
            FieldMatcher matcher = me.getValue();
            matcher.addRecordToMatcher(smr, r);  // is this the place to do this?  (was originally missing)
            // possibly need/want to add all match points 1st, then look for matches.
            ms.addMatcher(matchPointKey, matcher);
        }
        TimingLogger.stop("populateMatchSet");
        
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

    private List<HashSet<Long>> addToMatchSetList(HashSet<Long> matchset,  final List<HashSet<Long>> origMasMatchSetList) {
        TimingLogger.start("addToMatchSetList");

        if (matchset==null) {
            TimingLogger.stop("addToMatchSetList");
            return origMasMatchSetList;
        }
        if (matchset.size() < 1) {   ///TODO shouldn't this be <2 ?
            TimingLogger.stop("addToMatchSetList");
            return origMasMatchSetList;
        }

        LOG.debug("** addToMatchSetList, matchset length="+matchset.size()+" TOTAL matchset size ="+origMasMatchSetList.size());
        List<HashSet<Long>> newMasMatchSetList = new ArrayList<HashSet<Long>>();
        
        TimingLogger.start("addToMatchSetList.addAll()");
        newMasMatchSetList.addAll(origMasMatchSetList);
        TimingLogger.stop("addToMatchSetList.addAll()");

        TimingLogger.start("addToMatchSetList.set.processing");
        boolean added = false;
        for (HashSet<Long> set: origMasMatchSetList) {
            for (Long number: matchset) {
                TimingLogger.start("addToMatchSetList.set.contains");
                if (set.contains(number)) {
                    TimingLogger.stop("addToMatchSetList.set.contains");
                    TimingLogger.start("addToMatchSetList.set.notcontainsall");
                    if (!set.containsAll(matchset)) {
                        TimingLogger.stop("addToMatchSetList.set.notcontainsall");
                        TimingLogger.start("newMasMatchSetList.remove()");
                        newMasMatchSetList.remove(set);
                        TimingLogger.stop("newMasMatchSetList.remove()");
                        TimingLogger.start("set.addAll()");
                        set.addAll(matchset);
                        TimingLogger.stop("set.addAll()");
                        TimingLogger.start("newMasMatchSetList.add()");
                        newMasMatchSetList.add(set);
                        TimingLogger.stop("newMasMatchSetList.add()");
                        LOG.debug("addToMatchSetList, post-merge!  set.contains("+number+") merged newMasMatchSetList set="+set);
                    }
                    else {
                        TimingLogger.stop("addToMatchSetList.set.notcontainsall");
                        LOG.debug("addToMatchSetList, will not add in: "+matchset);
                    }
                    added = true;  // this flag means that we don't want this set added to the big list below
                    break;   // get you out of THIS set, but still must check the others.
                } else {
                	TimingLogger.stop("addToMatchSetList.set.contains");
                }

            }
        }
        TimingLogger.stop("addToMatchSetList.set.processing");

        //
        // the list of sets has to start somewhere, and if you don't find a set including some part
        // of your set, you must add your set explicitly.
        //
        if (!added) {
            LOG.debug("must add in: "+matchset);
            newMasMatchSetList.add(matchset);
        }
        LOG.debug("** addToMatchSetList, NEW TOTAL matchset size ="+newMasMatchSetList.size());
        TimingLogger.stop("addToMatchSetList");
        return newMasMatchSetList;
    }


    /**
     * assumptions:
     * 1) sets are not null
     * @param oldMatchSet
     * @param newMatchSet
     * @return true if they are equivalent (same record ids in each)
     */
    private boolean areMatchSetsEquivalent(HashSet<Long> oldMatchSet, HashSet<Long> newMatchSet) {
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
    private HashSet<Long> expandMatchedRecords(HashSet<Long> matchedRecordIds,TLongLongHashMap _mergedRecordsI2Omap, Map<Long, HashSet<Long>> _mergedRecordsO2Imap) {
        HashSet<Long> results = new HashSet<Long>();
        results.addAll(matchedRecordIds);

        for (Long input: matchedRecordIds) {
            Long output = _mergedRecordsI2Omap.get(input);
            if (output != null) {
                HashSet<Long> temp = _mergedRecordsO2Imap.get(output);
                if (temp != null) {
                    results.addAll(_mergedRecordsO2Imap.get(output));
                }
            }
        }
        return results;
    }

    @Override
    protected boolean commitIfNecessary(boolean force, long processedRecordsCount) {
        if (!force) {
            return super.commitIfNecessary(force, 0);
        }
        // force == true, only happens at the end of processing!
        try {
            TimingLogger.start("MarcAggregationService.non-generic");

            persistFromMASmemory();

            // During PROCESSING(as well as PRE-PROCESSING) of record updates, we delete this data, then add it back
            // (since it could be different, i.e., it's an update), therefore we must again persist to db
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                final FieldMatcher matcher = me.getValue();
                matcher.flush(true);
                LOG.debug("flush matcher: "+matcher.getName());
            }
            
            if (hasIntermediatePersistence) {
                masDAO.persistScores(scores_unpersisted);
                //flush from memory now that these have been persisted to database
                scores_unpersisted.clear();
            }
            else {
                masDAO.persistScores(scores);	
            }


            super.commitIfNecessary(true, 0);
            TimingLogger.stop("MarcAggregationService.non-generic");

            //transformation service does this, not sure why, so this is a placeholder.
//            getRepository().setPersistentProperty("inputBibs", inputBibs);
//            getRepository().setPersistentProperty("inputHoldings", inputHoldings);
        } catch (Throwable t) {
            getUtil().throwIt(t);
        } finally {
            TimingLogger.reset();
        }
        return true;
    }

    protected void persistFromMASmemory() {
        if (hasIntermediatePersistence) {
            masDAO.persistLongMatchpointMaps(allBibRecordsI2Omap_unpersisted,
                    MarcAggregationServiceDAO.bib_records_table, false);
            /****masDAO.persistLongOnly(mergedInRecordsList_unpersisted, MarcAggregationServiceDAO.merged_records_table);****/

            //flush from memory now that these have been persisted to database
            /****mergedInRecordsList_unpersisted.clear();****/
            allBibRecordsI2Omap_unpersisted.clear();
        }
        else {
            masDAO.persistLongMatchpointMaps(allBibRecordsI2Omap,
                    MarcAggregationServiceDAO.bib_records_table, false);
            /****masDAO.persistLongOnly(mergedInRecordsList, MarcAggregationServiceDAO.merged_records_table);****/
        }
        
        masDAO.persistLongMatchpointMaps(recordOfSourceMap,
                MarcAggregationServiceDAO.record_of_source_table, false);
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
                    if (counts4typeIn_t.get(RecordCounts.NEW_ACTIVE).get() >= counts4typeOut_t.get(RecordCounts.NEW_ACTIVE).get()) {
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
                    if (counts4typeIn_b.get(RecordCounts.NEW_ACTIVE).get() >= counts4typeOut_b.get(RecordCounts.NEW_ACTIVE).get()) {
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
