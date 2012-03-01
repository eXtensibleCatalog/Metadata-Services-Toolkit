/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.constants.Status;
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

import gnu.trove.TLongObjectHashMap;

/**
 * @author Benjamin D. Anderson
 * @author John Brand
 *
 */
public class MarcAggregationService extends GenericMetadataService {

    protected Map<String, FieldMatcher> matcherMap = null;
    protected Map<String, MatchRuleIfc> matchRuleMap = null;
    protected MarcAggregationServiceDAO masDAO = null;
    protected List<Set<Long>> masMatchSetList = null;
    protected TLongObjectHashMap<RecordOfSourceData> scores = null;

    private static final Logger LOG = Logger.getLogger(MarcAggregationService.class);
    private List<Character> leaderVals = null;
    private Repository inputRepo = null;
    private boolean leader_byte17_weighting_enabled;
    private boolean bigger_record_weighting_enabled;

    public void setup() {
        LOG.debug("MAS:  setup()");

        // determine record of source leader character priority, byte 17
        leaderVals = new ArrayList<Character>();
        List<String> _leaderVals = getConfigFileValues("leader.order");
        for (String val: _leaderVals) {
            LOG.info("Leader val==>"+val+"<== val length="+val.length());
            if (val.length() == 3) {
                leaderVals.add(val.charAt(1));  // char between quotes
            }
            else {
                leaderVals.add(val.charAt(0));
            }
        }
        //TODO process the rest of the new config file options for determining record of source
        leader_byte17_weighting_enabled= config.getPropertyAsBoolean("leader_byte17_weighting_enabled", false);
        bigger_record_weighting_enabled= config.getPropertyAsBoolean("bigger_record_weighting_enabled", false);

        try {
            validateService();
        } catch (ServiceValidationException e) {

            // Update database with status of service
            service.setStatus(Status.ERROR);
            LOG.error("Error validating service:", e);
            LogWriter.addInfo(service.getServicesLogFileName(), "** Error validating service - service will not run " + e.getMessage() + " **");
            sendReportEmail("Error validating service: " + e.getMessage());

            // in case the WorkerThread code addition causes issues, simply uncomment the below:
            // throw new RuntimeException(e);
        }

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
        this.matchRuleMap = new HashMap<String, MatchRuleIfc>();
        List<String> mrs = getConfigFileValues("match.rules.value");
        for (String mrStr : mrs) {
            MatchRuleIfc mr = (MatchRuleIfc) config.getBean(mrStr + "MatchRule");
            matchRuleMap.put(mrStr, mr);
        }
        if (this.masDAO == null) {  // this was really an initial unit test
            LOG.error("***  ERROR, DAO did not get initialized by Spring!");
        }
        masMatchSetList = new ArrayList<Set<Long>>();
        scores = new TLongObjectHashMap<RecordOfSourceData>();
    }

    @Override
    protected void validateService() throws ServiceValidationException {
        if (!leader_byte17_weighting_enabled && !bigger_record_weighting_enabled) {
            throw new ServiceValidationException("Service configuration file invalid: leader_byte17_weighting_enabled & bigger_record_weighting_enabled cannot both be disabled!");
        }
    }

    // http://stackoverflow.com/questions/367626/how-do-i-fix-the-expression-of-type-list-needs-unchecked-conversion
    // TODO move this UP to common utilities.
    public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
        List<T> r = new ArrayList<T>(c.size());
        for (Object o : c)
            r.add(clazz.cast(o));
        return r;
    }

    // this is just for what we have so far, not meant to always be up to date, i.e. it doesn't get
    // started off from looking at existing merged stuff in the database.  Based on the current record
    // that comes in, see what it matches, and go from there.
    public List<Set<Long>> getCurrentMatchSetList() {
        return masMatchSetList;
    }

    // need to look to see if the given match set impacts existing sets.  i.e if this set  is {1,47,50}
    // and we have existing sets {1,3} and {4,47} then we need a superset: {1,3,4,47,50} and need to
    // remove the existing sets {1,3}, {4,47}
    //
    // disjoint-set data structure?
    //
    private List<Set<Long>> addToMatchSetList(Set<Long> matchset,  final List<Set<Long>> origMasMatchSetList) {
        if (matchset==null) {
            return origMasMatchSetList;
        }
        if (matchset.size() < 1) {
            return origMasMatchSetList;
        }

        LOG.debug("** addToMatchSetList, matchset length="+matchset.size()+" TOTAL matchset size ="+origMasMatchSetList.size());
        List<Set<Long>> newMasMatchSetList = new ArrayList<Set<Long>>();
        newMasMatchSetList.addAll(origMasMatchSetList);

        boolean added = false;
        for (Set<Long> set: origMasMatchSetList) {
            for (Long number: matchset) {
                if (set.contains(number)) {
                    newMasMatchSetList.remove(set);
                    set.addAll(matchset);
                    newMasMatchSetList.add(set);
                    added = true;
                    break;   // get you out of THIS set, but still must check the others.
                }
            }
        }
        //
        // the list of sets has to start somewhere, and if you don't find a set including some part
        // of your set, you must add your set explicitly.
        //
        if (!added) {
            newMasMatchSetList.add(matchset);
        }
        LOG.debug("** addToMatchSetList, NEW TOTAL matchset size ="+newMasMatchSetList.size());
        return newMasMatchSetList;
    }

    private List<String> getConfigFileValues(String name) {
        try {
            // there is probably a more righteous way to grab the service name.
            final PropertiesConfiguration props = new PropertiesConfiguration(MSTConfiguration.getUrlPath() + "/services/" + getUtil().normalizeName("MARCAggregation") +
                    "/META-INF/classes/xc/mst/services/custom.properties");

            final List<String> values = castList(String.class, props.getList(name));
            return values;
        } catch (Exception e) {
            LOG.error("Error loading custom.properties for service: " + this.getServiceName(), e);
            return null;
        }
    }

    // for spring to inject.
    public void setMarcAggregationServiceDAO(MarcAggregationServiceDAO masDAO) {
        this.masDAO = masDAO;
    }

    public MarcAggregationServiceDAO getMarcAggregationServiceDAO() {
        return this.masDAO;
    }

    // wrap it.
    //  (to increase accessibility - classes like Matcher/MatchRules that aren't subclasses may need it.)
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


    public void processComplete() {
        //
        // for performance may want to do this:
        //        .createIndiciesIfNecessary(name);
        //
        // evaluate match sets here?
        LOG.info("** START processComplete!");
        List<Set<Long>> matches = getCurrentMatchSetList();
        if (matches != null) {
            //TODO maybe change this to 'debug' vs. 'info' at some point.
            LOG.info("** processComplete, matchset length="+matches.size());

            for (Set<Long> set: matches) {
                StringBuilder sb = new StringBuilder("*** Matchset: {");
                for (Long num: set) {
                    sb.append(num+", ");
                }
                sb.append("}");
                //TODO change this to 'debug' vs. 'info' at some point.
                LOG.info(sb.toString());
            }

            // TODO
            // important - this is not going to totally nail it for the long term
            // need to consider records received during THIS run of the service, and
            // there status, i.e. if if goes to deleted state and is part of a merge
            // set.  Future solution still in the works  - could be customProcessQueue
            // and if that is not enough save more to the current match set list?
            //
            merge(matches);
        }
    }

    private void merge(List<Set<Long>> matches) {
        for (Set<Long> set: matches) {
            Long recordOfSource = determineRecordOfSource(set);
            for (Long num: set) {
            }
        }
    }

    /**
     # Record of source criteria:
     #
     # 1) leader_byte17_weighting_enabled = true/false
     # 2) bigger_record_weighting_enabled = true/false
     #
     # And four cases:
     #
     # 1-true, 2-false
     # In this case we first compare Leader/byte17, pick the earliest (in String leader.order above),
     #   if they are the same, pick the record that is being processed.
     #
     # 1-true, 2-true
     # In this case we first compare Leader/byte17, pick the earliest (in String leader.order above),
     #   if they are the same, pick the record that is largest in bytes.
     #
     # 1-false, 2-true
     # Pick the record that is largest in bytes.
     #
     # 1-false, 2-false
     # This is a not-allowed state and the service will throw an error message.
     #
     * @param set
     * @return
     */
    private Long determineRecordOfSource(Set<Long> set) {
        //use leaderVals:
        // List<Character> leaderVals
        // leader_byte17_weighting_enabled;
        // bigger_record_weighting_enabled;

        Repository repo = getInputRepo();  //for date tie-breaker
        Long winner=null;  // to store/update winner as you go along and test records against each other.
        for (Long num: set) {
            if (winner == null) {
                winner = num;  // 1st record is temporarily the record of source till proven otherwise.
            }
            if (!scores.containsKey(num)) {
                RecordOfSourceData source = masDAO.getScoreData(num);
            }
            else {
                //get it out of memory.
            }
            // grab leader byte 17 val and size
            RecordOfSourceData source = masDAO.getScoreData(num);
            LOG.info("Source data for id: "+num+" char:"+source.leaderByte17+": "+" size="+source.size);

        }
        return null;
    }

    // note the 'well' named class Set collides with java.util.Set
    //
    // overriding this so you can save the repo/start over?
    public void process(Repository repo, Format inputFormat, xc.mst.bo.provider.Set inputSet, xc.mst.bo.provider.Set outputSet) {
        this.inputRepo = repo;
        LOG.info("MarcAggregationService, processing repo "+ repo.getName()+" started.");
        try {
            super.process(repo, inputFormat, inputSet, outputSet);
            processComplete();
        } catch (Exception e) {
            LOG.error("MarcAggregationService, processing repo "+ repo.getName()+" failed.", e);
        }
    }

    protected Repository getInputRepo() {
        return this.inputRepo;
    }

    public List<OutputRecord> process(InputRecord r) {
        try {
            LOG.debug("MAS:  process record+"+r.getId());

            if (r.getStatus() != Record.DELETED) {
                SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());
                smr.setRecordId(r.getId());

                final char leaderByte17 = smr.getLeader().charAt(17);
                final int rSize = r.getOaiXml().getBytes().length;
                scores.put(r.getId(), new RecordOfSourceData(leaderByte17, rSize));

                MatchSet ms = new MatchSet(smr);
                for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                    String matchPointKey = me.getKey();
                    FieldMatcher matcher = me.getValue();
                    matcher.addRecordToMatcher(smr, r);  // is this the place to do this?  (was originally missing)
                    // possibly need/want to add all match points 1st, then look for matches.
                    ms.addMatcher(matchPointKey, matcher);
                }

                // maybe this will come into play with rules that have parts that are alike...
                Set<Long> previouslyMatchedRecordIds = null;

                Set<Long> matchedRecordIds = new HashSet<Long>();
                for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
                    String matchRuleKey = me.getKey();
                    MatchRuleIfc matchRule = me.getValue();
                    Set<Long> set = matchRule.determineMatches(ms);
                    if (set !=null && !set.isEmpty()) {
                        matchedRecordIds.addAll(set);
                    }
                }
                masMatchSetList = addToMatchSetList(matchedRecordIds, masMatchSetList);

                // will we want to do this on a 2nd loop around all the records?
                //
                if (r.getSuccessors().size() == 0) {
                    // NEW-ACTIVE

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
                }
            } else {
                if (r.getSuccessors().size() == 0) {
                    // NEW-DELETED
                    //
                    // nothing to do?
                } else {
                    // UPDATE-DELETED
                    //
                    // TODO - unmerge ramifications, figure out merge set you may be in, remerge?
                }
            }

        } catch (Throwable t) {
            util.throwIt(t);
        }
        return null;
    }

    @Override
    protected boolean commitIfNecessary(boolean force, long processedRecordsCount) {
        try {
            LOG.debug("***FORCE: masDAO.commitIfNecessary");
            TimingLogger.start("masDAO.commitIfNecessary");

            // break down timing logger more later if necessary.
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                final FieldMatcher matcher = me.getValue();
                matcher.flush(force);
                LOG.debug("flush matcher: "+matcher.getName());
            }
            // this should not need to done in must do, must do frequently section.
            masDAO.persistScores(scores);
            TimingLogger.stop("masDAO.commitIfNecessary");
        } catch (Throwable t) {
            getUtil().throwIt(t);
        }
        if (!force) {
            TimingLogger.reset();
            return super.commitIfNecessary(force, 0);
        }
        try {
            TimingLogger.start("MarcAggregationServiceDAO.non-generic");
            super.commitIfNecessary(true, 0);
            TimingLogger.stop("MarcAggregationServiceDAO.non-generic");
            // as part of the flush call matcher must clear its memory data structures

            TimingLogger.stop("MarcAggregationServiceDAO.endBatch");

//            getRepository().setPersistentProperty("inputBibs", inputBibs);
//            getRepository().setPersistentProperty("inputHoldings", inputHoldings);
            TimingLogger.reset();
        } catch (Throwable t) {
            getUtil().throwIt(t);
        }
        return true;
    }
}
