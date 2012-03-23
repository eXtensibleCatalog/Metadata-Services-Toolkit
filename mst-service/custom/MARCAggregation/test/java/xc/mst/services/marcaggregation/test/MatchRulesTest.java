/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import xc.mst.repo.Repository;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;
import xc.mst.services.marcaggregation.matcher.FieldMatcher;

public class MatchRulesTest extends MatcherTest {

    private static final Logger LOG = Logger.getLogger(MatchRulesTest.class);

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("demo_175_and_dupes");
        return fileStrs;
    }

    protected void setupMatcherExpectations() {
        //load expected number of records for each matcher.
        //TODO figure out how many of each FUZZY till end of TODO
        expectedMatchRecordIds.put("x028abMatcher", 0);
        expectedMatchRecords.put  ("x028abMatcher", 0);

        expectedMatchRecordIds.put("x245ahMatcher", 0);
        expectedMatchRecords.put  ("x245ahMatcher", 0);

        expectedMatchRecordIds.put("x240aMatcher", 0);
        expectedMatchRecords.put  ("x240aMatcher", 0);

        expectedMatchRecordIds.put("x260abcMatcher", 0);
        expectedMatchRecords.put  ("x260abcMatcher", 0);
        //TODO end of above TODO

        // 022$a
        expectedMatchRecordIds.put("ISSNMatcher", 15);
        expectedMatchRecords.put  ("ISSNMatcher", 14);

        // 020$a
        expectedMatchRecordIds.put("ISBNMatcher", 35);
        expectedMatchRecords.put  ("ISBNMatcher", 58);

        expectedMatchRecordIds.put("x024aMatcher", 19);
        expectedMatchRecords.put  ("x024aMatcher", 18);

        expectedMatchRecordIds.put("x130aMatcher", 0);  // if we turn on fuzzy this will go to 11.
        expectedMatchRecords.put  ("x130aMatcher", 0);  //TODO this will need to be modified to 11.

        // 010$a
        expectedMatchRecordIds.put("LccnMatcher", 61);
        expectedMatchRecords.put  ("LccnMatcher", 56);

        // 035$a
        expectedMatchRecordIds.put("SystemControlNumberMatcher", 75);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 74);

        expectedResults.add(getExpectedMatchSet(new long[]{176,178}));
        expectedResults.add(getExpectedMatchSet(new long[]{40,180}));
        expectedResults.add(getExpectedMatchSet(new long[]{44,181}));
        expectedResults.add(getExpectedMatchSet(new long[]{5,183}));
        expectedResults.add(getExpectedMatchSet(new long[]{2,184}));
    }

    protected int getNumberMatchedResultsGoal() {
        return expectedResults.size();
    }

    protected TreeSet<Long> getExpectedMatchSet(long[]nums) {
        TreeSet<Long> set = new TreeSet<Long>();
        for (long num: nums) {
            set.add(num);
        }
        return set;
    }

    protected String getTestName() {
        return "MatchRulesTest";
    }

    public void finalTest() {
        setup();
        try {
            // These first 2 steps are done in MockHarvestTest
            // - harvest records into MST and run them through norm service

            System.out.println("****START "+getTestName() +" *****");
            Repository providerRepo = getRepositoryService().getRepository(this.provider);

            List<TreeSet<Long>> results = getRecordsAndAddToMem(providerRepo);
            checkNumberMatchedResults(results, expectedResults);
            LOG.info("MatchRulesTest:ensureMatch results size =" + results.size());

            //after parsing all the records, verify the counts are what is expected for our particular record set.
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
                    String result ="* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints but expected: "+expectedMatchRecords.get(matcher.getName()) ;
                    reportFailure(result);
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints expected: "+expectedMatchRecords.get(matcher.getName()) );
                }
            }

            // the result is number of the 175 records that had 020 fields, result I got was 118, verify this is correct.
            // also note, this is really only testing the 1st matchrule and its matcher, perhaps unload that one, then run again. (2x), and so on, and so on.

            // flush, then results should be empty, definitely if force is true, maybe otherwise, depending on what I decide...
            flush(true);
            /*
            //
            // changed num reporting code in matchers to report totals from db, so flush affect.  for now, disable this test, it accomplished what it needed to.
            //
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                FieldMatcher matcher = me.getValue();
                if (matcher.getNumRecordIdsInMatcher() != 0 && matcher.getNumMatchPointsInMatcher() != 0) {
                    String result = "*FAIL,post-flush,matcher " + matcher.getName() + " it has " + matcher.getNumRecordIdsInMatcher() + " recordIds and " + matcher.getNumMatchPointsInMatcher() + " match points.";
                    reportFailure(result);
                }
                else {
                    LOG.info("*PASS:post-flush,matcher " + matcher.getName() + " it has " + matcher.getNumRecordIdsInMatcher() + " recordIds and " + matcher.getNumMatchPointsInMatcher() + " match points.");
                }
            }
            */

            testDAO();

            // load, then results should be back to original expectations
            load();
            //after parsing all the records, verify the counts are what is expected for our particular record set.
            // TODO I have not decided how this should work yet - would I really load all back into memory?  Probably not, comment out test till code supports.
            /*
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                FieldMatcher matcher = me.getValue();
                LOG.info("for matcher " + matcher.getName() + " it has " + matcher.getNumRecordIdsInMatcher() + " recordIds and " + matcher.getNumMatchPointsInMatcher() + " match points.");
                if (expectedMatchRecordIds.get(matcher.getName()) != matcher.getNumRecordIdsInMatcher()) {
                    String result = "* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumRecordIdsInMatcher()+" records but expected: "+expectedMatchRecordIds.get(matcher.getName()) ;
//                    reportFailure(result);
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumRecordIdsInMatcher()+" records expected: "+expectedMatchRecordIds.get(matcher.getName()) );
                }
                if (expectedMatchRecords.get(matcher.getName()) != matcher.getNumMatchPointsInMatcher()) {
                    String result = "* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints but expected: "+expectedMatchRecords.get(matcher.getName()) ;
//                  reportFailure(result);
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints expected: "+expectedMatchRecords.get(matcher.getName()) );
                }
            }
            */


            // at this point, artificially add a record with known matches, verify you get them, flush, should be no matches, then load, should have the matches back.
            // , ideally harvest from a 2nd repo (that contains some matching records)?

            verifyCorrectNumberOutputRecords();

        } catch (Throwable t) {
            LOG.error("Exception occured when running MatchRulesTest!", t);
            getUtil().throwIt(t);
        }
    }

    protected void testDAO() {
        //mysql -u root --password=root -D xc_marcaggregation -e 'select input_record_id  from matchpoints_035a where string_id = "24094664" '
        List<Long> records = masDao.getMatchingRecords(MarcAggregationServiceDAO.matchpoints_035a_table, MarcAggregationServiceDAO.input_record_id_field,MarcAggregationServiceDAO.string_id_field,"24094664");
        LOG.info("DAO, getMatching records for 24094664, numResults="+records.size());
        for (Long record: records) {
            LOG.info("** record id: "+record +" matches 24094664");
        }
    }
}
