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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        expectedMatchRecordIds.put("x130aMatcher", 11);
        expectedMatchRecords.put  ("x130aMatcher", 0);  //TODO this will need to be modified to 11.

        // 010$a
        expectedMatchRecordIds.put("LccnMatcher", 61);
        expectedMatchRecords.put  ("LccnMatcher", 56);

        // 035$a
        expectedMatchRecordIds.put("SystemControlNumberMatcher", 127);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 158);

        HashSet<Long> set =  new HashSet<Long>();
        set.add(176l);
        expectedResults.put(new Long(178), set);
        HashSet<Long> set2 =  new HashSet<Long>();
        set2.add(3l);
        expectedResults.put(new Long(179), set2);
        HashSet<Long> set3 =  new HashSet<Long>();
        set3.add(40l);
        expectedResults.put(new Long(180), set3);
        HashSet<Long> set4 =  new HashSet<Long>();
        set4.add(44l);
        expectedResults.put(new Long(181), set4);
        HashSet<Long> set5 =  new HashSet<Long>();
        set5.add(5l);
        expectedResults.put(new Long(183), set5);
        HashSet<Long> set6 =  new HashSet<Long>();
        set6.add(2l);
        expectedResults.put(new Long(184), set6);

        //other way too, with db in the picture
        // rule 1a
        HashSet<Long> set1 =  new HashSet<Long>();
        set1.add(178l);
        expectedResults.put(new Long(176), set1);
        HashSet<Long> set20 =  new HashSet<Long>();
        set20.add(179l);
        expectedResults.put(new Long(3), set20);

        //other way too, with db in the picture
        // rule 2
        HashSet<Long> set30 =  new HashSet<Long>();
        set30.add(180l);
        expectedResults.put(new Long(40), set30);
        HashSet<Long> set40 =  new HashSet<Long>();
        set40.add(181l);
        expectedResults.put(new Long(44), set40);
        HashSet<Long> set50 =  new HashSet<Long>();
        set50.add(183l);
        expectedResults.put(new Long(5), set50);
        HashSet<Long> set60 =  new HashSet<Long>();
        set60.add(184l);
        expectedResults.put(new Long(2), set60);
    }

    protected int getNumberMatchedResultsGoal() {
        return expectedResults.size();
    }

    public void finalTest() {
        setup();
        try {
            // These first 2 steps are done in MockHarvestTest
            // - harvest records into MST and run them through norm service

            System.out.println("****START MatchRulesTest *****");
            Repository providerRepo = getRepositoryService().getRepository(this.provider);

            Map<Long, Set<Long>> results = getRecordsAndAddToMem(providerRepo);
            checkNumberMatchedResults(results, getNumberMatchedResultsGoal());
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
