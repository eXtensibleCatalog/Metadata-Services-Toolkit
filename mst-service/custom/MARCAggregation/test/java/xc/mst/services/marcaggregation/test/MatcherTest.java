package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.repo.Repository;
import xc.mst.services.marcaggregation.matcher.FieldMatcher;
import xc.mst.services.marcaggregation.matcher.MatchSet;
import xc.mst.services.marcaggregation.matchrules.MatchRuleIfc;

public class MatcherTest extends MASBaseTest {

    private static final Logger LOG = Logger.getLogger(MatchRulesTest.class);

    protected Map<String, FieldMatcher> matcherMap = null;
    protected Map<String, MatchRuleIfc> matchRuleMap = null;

    protected HashMap<String, Integer> expectedMatchRecords = new HashMap<String, Integer>();
    protected HashMap<String, Integer> expectedMatchRecordIds = new HashMap<String, Integer>();

    public void setup() {
        LOG.debug("MAS:  setup()");
        setupMatcherExpectations();

        this.matcherMap = new HashMap<String, FieldMatcher>();

        List<String> mpStrs = getConfigFileValues("matchers.value");
        for (String mp : mpStrs) {
            final String n = mp + "Matcher";
            FieldMatcher m = (FieldMatcher) applicationContext.getBean(n);
            m.setName(n);
            matcherMap.put(mp, m);
            m.load();
        }
        this.matchRuleMap = new HashMap<String, MatchRuleIfc>();
        List<String> mrStrs = getConfigFileValues("match.rules.value");
        for (String mrStr : mrStrs) {
            MatchRuleIfc mr = (MatchRuleIfc) applicationContext.getBean(mrStr + "MatchRule");
            matchRuleMap.put(mrStr, mr);
        }
    }

    protected void setupMatcherExpectations() {
        //load expected number of records for each matcher.
        //TODO figure out how menu of each till end of TODO
        expectedMatchRecordIds.put("x028abMatcher", 0);
        expectedMatchRecords.put  ("x028abMatcher", 0);

        expectedMatchRecordIds.put("x245ahMatcher", 0);
        expectedMatchRecords.put  ("x245ahMatcher", 0);

        expectedMatchRecordIds.put("x240aMatcher", 0);
        expectedMatchRecords.put  ("x240aMatcher", 0);

        expectedMatchRecordIds.put("x260abcMatcher", 0);
        expectedMatchRecords.put  ("x260abcMatcher", 0);
        //TODO end of above TODO

        expectedMatchRecordIds.put("ISSNMatcher", 14);
        expectedMatchRecords.put  ("ISSNMatcher", 14);

        expectedMatchRecordIds.put("ISBNMatcher", 34);
        expectedMatchRecords.put  ("ISBNMatcher", 57);

        expectedMatchRecordIds.put("x024aMatcher", 17);
        expectedMatchRecords.put  ("x024aMatcher", 18);

        expectedMatchRecordIds.put("x130aMatcher", 11);
        expectedMatchRecords.put  ("x130aMatcher", 0);  //TODO this will need to be modified to 11.

        expectedMatchRecordIds.put("LccnMatcher", 56);
        expectedMatchRecords.put  ("LccnMatcher", 56);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 118);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 151);
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("demo_175");
        return fileStrs;
    }

    protected int getNumberMatchedResultsGoal() {
        return 0;
    }

    public void finalTest() {
        setup();
        try {
            // These first 2 steps are done in MockHarvestTest
            // - harvest records into MST and run them through norm service

            System.out.println("****START MatcherTest *****");
            Repository providerRepo = getRepositoryService().getRepository(this.provider);

            Set<Long> results = getRecordsAndAddToMem(providerRepo);
            checkNumberMatchedResults(results, getNumberMatchedResultsGoal());
            // if (!results.isEmpty()) throw new RuntimeException("FAILURE - expected NO results to be returned.");

            // results = ensureMatch(providerRepo);
            // LOG.info("ensureMatch results size ="+results.size());
            // if (results.isEmpty()) throw new RuntimeException("FAILURE - expected some results to be returned.");

            //after parsing all the records, verify the counts are what is expected for our particular record set.
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                FieldMatcher matcher = me.getValue();
                LOG.info("for matcher " + matcher.getName() + " it has " + matcher.getNumRecordIdsInMatcher() + " recordIds and " + matcher.getNumMatchPointsInMatcher() + " match points.");
                if (expectedMatchRecordIds.get(matcher.getName()) != matcher.getNumRecordIdsInMatcher()) {
                    throw new RuntimeException("* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumRecordIdsInMatcher()+" records but expected: "+expectedMatchRecordIds.get(matcher.getName()) );
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumRecordIdsInMatcher()+" records expected: "+expectedMatchRecordIds.get(matcher.getName()) );
                }
                if (expectedMatchRecords.get(matcher.getName()) != matcher.getNumMatchPointsInMatcher()) {
                    throw new RuntimeException("* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints but expected: "+expectedMatchRecords.get(matcher.getName()) );
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints expected: "+expectedMatchRecords.get(matcher.getName()) );
                }
            }

            // the result is number of the 175 records that had 020 fields, result I got was 118, verify this is correct.
            // also note, this is really only testing the 1st matchrule and its matcher, perhaps unload that one, then run again. (2x), and so on, and so on.

            // TODO flush, then results should be empty

            // TODO load, then results should be 118

            // at this point, artificially add a record with known matches, verify you get them, flush, should be no matches, then load, should have the matches back.
            // , ideally harvest from a 2nd repo (that contains some matching records)?

        } catch (Throwable t) {
            LOG.error("Exception occured when running MarkProviderDeletedTest!", t);
            getUtil().throwIt(t);
        }
    }

    protected void checkNumberMatchedResults(Set<Long> results, int goal) {
        if (results.size() != goal) {
            throw new RuntimeException("* WRONG number matches, expected"+ goal+" ! got "+results.size() );
        }
        else {
            LOG.info("ensureMatch results size =" + results.size());
        }
    }

    protected void flush(boolean force) {
        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            FieldMatcher matcher = me.getValue();
            matcher.flush(force);
        }
    }

    protected void load() {
        for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
            FieldMatcher matcher = me.getValue();
            matcher.load();
        }
    }

    protected Set<Long> getRecordsAndAddToMem(Repository repo) throws Throwable {
        List<Record> records = repo.getRecords(new Date(0), new Date(), 0l, getMarc21Format(), null);
        Set<Long> overall = new HashSet<Long>();
        for (Record r : records) {
            overall.addAll(process((InputRecord) r));
        }
        LOG.info("* done *");
        return overall;
    }

    public Set<Long> process(InputRecord r) {
        Set<Long> matchedRecordIds = new HashSet<Long>();
        try {

            LOG.debug("test:  process record+" + r.getId());
            if (r.getStatus() != Record.DELETED) {
                SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());
                smr.setRecordId(r.getId());

                MatchSet ms = new MatchSet(smr);
                for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                    String matchPointKey = me.getKey();
                    FieldMatcher matcher = me.getValue();
                    matcher.addRecordToMatcher(smr); // is this the place to do this? (was originally missing)
                    ms.addMatcher(matchPointKey, matcher);
                }

                Set<Long> previouslyMatchedRecordIds = null;

                for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
                    String matchRuleKey = me.getKey();
                    MatchRuleIfc matchRule = me.getValue();
                    Set<Long> set = matchRule.determineMatches(ms);
                    if (set !=null && !set.isEmpty()) {
                        matchedRecordIds.addAll(set);
                    }
                }
/*
                for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
                    String matchRuleKey = me.getKey();
                    MatchRuleIfc matchRule = me.getValue();
                    matchedRecordIds.addAll(matchRule.determineMatches(ms));
                }
                */
            } /*
               * else {
               * if (r.getSuccessors().size() == 0) {
               * // NEW-DELETED
               * } else {
               * // UPDATE-DELETED
               * }
               * }
               */

        } catch (Throwable t) {
            getUtil().throwIt(t);
        }
        for (Long result: matchedRecordIds) {
            LOG.info("recordId " +r.getId()+" has matches==>" + result+"<==");
        }
        return matchedRecordIds;
    }

}
