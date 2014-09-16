package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.repo.Repository;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;
import xc.mst.services.marcaggregation.matcher.FieldMatcher;
import xc.mst.services.marcaggregation.matcher.MatchSet;
import xc.mst.services.marcaggregation.matchrules.MatchRuleIfc;

public class MatcherTest extends MASBaseTest {

    private static final Logger LOG = Logger.getLogger(MatchRulesTest.class);

    protected Map<String, FieldMatcher> matcherMap = null;
    protected Map<String, MatchRuleIfc> matchRuleMap = null;
    protected List<TreeSet<Long>> masMatchSetList = null;

    protected HashMap<String, Integer> expectedMatchRecords = new HashMap<String, Integer>();
    protected HashMap<String, Integer> expectedMatchRecordIds = new HashMap<String, Integer>();

    protected List<Set<Long>> expectedResults = new ArrayList<Set<Long>>();

    protected MarcAggregationServiceDAO masDao = null;

    protected int inputRecordCount = 0;

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
            m.load(true);
        }
        this.matchRuleMap = new HashMap<String, MatchRuleIfc>();
        List<String> mrStrs = getConfigFileValues("match.rules.value");
        for (String mrStr : mrStrs) {
            MatchRuleIfc mr = (MatchRuleIfc) applicationContext.getBean(mrStr + "MatchRule");
            matchRuleMap.put(mrStr, mr);
        }
        masMatchSetList = new ArrayList<TreeSet<Long>>();
        masDao = (MarcAggregationServiceDAO) applicationContext.getBean("MarcAggregationServiceDAO");
    }

    protected void setupMatcherExpectations() {
        //load expected number of records for each matcher.
        //TODO figure out how many of each till end of TODO
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

        expectedMatchRecordIds.put("x130aMatcher", 0);  // if we turn on fuzzy this will go to 11.
        expectedMatchRecords.put  ("x130aMatcher", 0);  //TODO this will need to be modified to 11.

        expectedMatchRecordIds.put("LccnMatcher", 56);
        expectedMatchRecords.put  ("LccnMatcher", 56);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 67);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 67);
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

            List<TreeSet<Long>> results = getRecordsAndAddToMem(providerRepo);
            checkNumberMatchedResults(results, expectedResults);

            //after parsing all the records, verify the counts are what is expected for our particular record set.
            //  the counts we are looking for and comparing are  number of matchpoints for each matcher and number of recordids for each matcher.
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                FieldMatcher matcher = me.getValue();
                LOG.info("for matcher " + matcher.getName() + " it has " + matcher.getNumRecordIdsInMatcher() + " recordIds and " + matcher.getNumMatchPointsInMatcher() + " match points.");
                if (expectedMatchRecordIds.get(matcher.getName()) != matcher.getNumRecordIdsInMatcher()) {
                    String result = "* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumRecordIdsInMatcher()+" records but expected: "+expectedMatchRecordIds.get(matcher.getName()) ;
                    reportFailure(result);
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumRecordIdsInMatcher()+" records expected: "+expectedMatchRecordIds.get(matcher.getName()) );
                }
                if (expectedMatchRecords.get(matcher.getName()) != matcher.getNumMatchPointsInMatcher()) {
                    String result = "* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints but expected: "+expectedMatchRecords.get(matcher.getName()) ;
                    reportFailure(result);
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints expected: "+expectedMatchRecords.get(matcher.getName()) );
                }
            }

            // the result is number of the 175 records that had 020 fields, result I got was 118, verify this is correct.
            // also note, this is really only testing the 1st matchrule and its matcher, perhaps unload that one, then run again. (2x), and so on, and so on.

            // TODO flush, then results should be empty  (this test occurs in MatchRulesTest.)

            // TODO load, then results should be 118  (maybe, or maybe you can't reload all into memory, TBD)

            // at this point, artificially add a record with known matches, verify you get them, flush, should be no matches, then load, should have the matches back.
            // , ideally harvest from a 2nd repo (that contains some matching records)?

            verifyCorrectNumberOutputRecords();

        } catch (Throwable t) {
            LOG.error("Exception occured when running MatcherTest!", t);
            getUtil().throwIt(t);
        }
    }

    protected void verifyCorrectNumberOutputRecords() {
        // output record count = input record count - extra matches, i.e if 1 match set of 4,
        //                       then output record count = input record count - 3.
        int actualOutRecordCnt =0;
        int expectedOutRecordCnt = inputRecordCount; //start with this number, decrement from it based on matches
        try {
            actualOutRecordCnt=getServiceRepository().getNumActiveRecords();
        } catch (Exception e) {
            LOG.error("Why!",e);
        }
        for (Set<Long> set: expectedResults) {
            expectedOutRecordCnt-= (set.size()-1);
        }

        if (expectedOutRecordCnt != actualOutRecordCnt) {
            reportFailure("wrong number of output records, expected: "+expectedOutRecordCnt+" got: "+actualOutRecordCnt);
        }
        else {
            LOG.info("correct number of output records, expected: "+expectedOutRecordCnt+" got: "+actualOutRecordCnt);
        }
    }

    // check whether we got the matches we expected for a record.  Note this test is order dependent, i.e. we expect the records
    // to come in in a certain order, we control this, so we will achieve it.  This is for test purposes only, to prove we get
    // the matches we expect for a certain record when this order is maintained.
    protected void checkNumberMatchedResults(List<TreeSet<Long>> results, List<Set<Long>> expectedResults) {
        try {
            for (TreeSet<Long> set: results) {
                if (!expectedResults.contains(set)) {
                    printExpectedVsActualMatchSets(results, expectedResults);
                    reportFailure("could not find given match set within expected match sets! "+set);
                }
            }

            printExpectedVsActualMatchSets(results, expectedResults);

            if (results.size() != expectedResults.size()) {
                String result = "* WRONG number match sets, expected"+ expectedResults.size()+" ! got "+results.size() ;
                reportFailure(result);
            }
            else {
                LOG.info("#match sets =" + results.size() + " goal="+expectedResults.size());
            }
        } catch (Exception e) {
            reportFailure(e);
        }
        List<TreeSet<Long>> sets = getCurrentMatchSetList();
        if (sets !=null) {
            LOG.info("** getCurrentMatchSetList, size="+sets.size());
            for(Set<Long> set: sets) {
                LOG.info("*** getCurrentMatchSetList, set size="+set.size());
            }
        }
    }

    protected void printExpectedVsActualMatchSets(List<TreeSet<Long>> results, List<Set<Long>> expectedResults) {
        for (Set<Long> set: results) {
            StringBuilder sb = new StringBuilder("***Actual Matchset: {");
            for (Long num: set) {
                sb.append(num+", ");
            }
            sb.append("}");
            LOG.info(sb.toString());
        }

        for (Set<Long> set: expectedResults) {
            StringBuilder sb = new StringBuilder("***Expected Matchset: {");
            for (Long num: set) {
                sb.append(num+", ");
            }
            sb.append("}");
            LOG.info(sb.toString());
        }
    }

    protected void reportFailure(String result) {
        throw new RuntimeException(result);
    }

    protected void reportFailure(Exception e) {
        LOG.info(e);
        throw new RuntimeException(e);
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
            matcher.load(true);
        }
    }



    protected List<TreeSet<Long>> getRecordsAndAddToMem(Repository repo) throws Throwable {
        List<Record> records = repo.getRecords(new Date(0), new Date(), 0l, getMarc21Format(), null);
        inputRecordCount = records.size();
        for (Record r : records) {
            Set<Long> set = process((InputRecord) r);
            if (set.size()>0) {
            }
        }
        return masMatchSetList;
    }

    public Set<Long> process(InputRecord r) {
        TreeSet<Long> matchedRecordIds = new TreeSet<Long>();
        try {

            LOG.debug("test:  process record+" + r.getId());

            if (r.getStatus() != Record.DELETED) {
                SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());
                smr.setRecordId(r.getId());

                MatchSet ms = new MatchSet(smr);
                for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                    String matchPointKey = me.getKey();
                    FieldMatcher matcher = me.getValue();
                    matcher.addRecordToMatcher(smr, r); // is this the place to do this? (was originally missing)
                    ms.addMatcher(matchPointKey, matcher);
                }

                Set<Long> previouslyMatchedRecordIds = null;

                for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
                    String matchRuleKey = me.getKey();
                    MatchRuleIfc matchRule = me.getValue();
                    matchRule.setDebug(true);
                    Set<Long> set = matchRule.determineMatches(ms);
                    if (set !=null && !set.isEmpty()) {
                        matchedRecordIds.addAll(set);
                    }
                }
                masMatchSetList = addToMatchSetList(matchedRecordIds, masMatchSetList);
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
            LOG.debug("recordId " +r.getId()+" has matches==>" + result+"<==");
        }
        return matchedRecordIds;
    }

    // this is just for what we have so far, not meant to always be up to date, i.e. it doesn't get
    // started off from looking at existing merged stuff in the database.  Based on the current record
    // that comes in, see what it matches, and go from there.
    public List<TreeSet<Long>> getCurrentMatchSetList() {
        return masMatchSetList;
    }

    // need to look to see if the given match set impacts existing sets.  i.e if this set  is {1,47,50}
    // and we have existing sets {1,3} and {4,47} then we need a superset: {1,3,4,47,50} and need to
    // remove the existing sets {1,3}, {4,47}
    //
    // disjoint-set data structure?
    //
    private List<TreeSet<Long>> addToMatchSetList(TreeSet<Long> matchset,  final List<TreeSet<Long>> origMasMatchSetList) {
        if (matchset==null) {
            return origMasMatchSetList;
        }
        if (matchset.size() < 1) {
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
    /*

package xc.mst.service.impl.test;

public abstract class MockHarvestTest extends StartToFinishTest {

    public void startToFinish() throws Exception {}
    // executes installService();  // is this enough?
*/

}

//
