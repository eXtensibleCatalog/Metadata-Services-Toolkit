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
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.repo.Repository;
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

        expectedMatchRecordIds.put("x024aMatcher", 19);
        expectedMatchRecords.put  ("x024aMatcher", 18);

        expectedMatchRecordIds.put("x130aMatcher", 11);
        expectedMatchRecords.put  ("x130aMatcher", 0);  //TODO this will need to be modified to 11.

        expectedMatchRecordIds.put("LccnMatcher", 58);
        expectedMatchRecords.put  ("LccnMatcher", 56);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 124);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 155);
    }

    public void finalTest() {
        setup();
        try {
            // These first 2 steps are done in MockHarvestTest
            // - harvest records into MST and run them through norm service

            System.out.println("****START MatchRulesTest *****");
            Repository providerRepo = getRepositoryService().getRepository(this.provider);

            Set<Long> results = getRecordsAndAddToMem(providerRepo);
            LOG.info("ensureMatch results size =" + results.size());
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

            // flush, then results should be empty, definitely if force is true, maybe otherwise, depending on what I decide...
            flush(true);
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                FieldMatcher matcher = me.getValue();
                if (matcher.getNumRecordIdsInMatcher() != 0 && matcher.getNumMatchPointsInMatcher() != 0) {
                    throw new RuntimeException("*FAIL,post-flush,matcher " + matcher.getName() + " it has " + matcher.getNumRecordIdsInMatcher() + " recordIds and " + matcher.getNumMatchPointsInMatcher() + " match points.");
                }
                else {
                    LOG.info("*PASS:post-flush,matcher " + matcher.getName() + " it has " + matcher.getNumRecordIdsInMatcher() + " recordIds and " + matcher.getNumMatchPointsInMatcher() + " match points.");
                }
            }
            // load, then results should be back to original expectations
            load();
            //after parsing all the records, verify the counts are what is expected for our particular record set.
            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                FieldMatcher matcher = me.getValue();
                LOG.info("for matcher " + matcher.getName() + " it has " + matcher.getNumRecordIdsInMatcher() + " recordIds and " + matcher.getNumMatchPointsInMatcher() + " match points.");
                if (expectedMatchRecordIds.get(matcher.getName()) != matcher.getNumRecordIdsInMatcher()) {
//                    throw new RuntimeException("* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumRecordIdsInMatcher()+" records but expected: "+expectedMatchRecordIds.get(matcher.getName()) );
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumRecordIdsInMatcher()+" records expected: "+expectedMatchRecordIds.get(matcher.getName()) );
                }
                if (expectedMatchRecords.get(matcher.getName()) != matcher.getNumMatchPointsInMatcher()) {
//                    throw new RuntimeException("* WRONG, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints but expected: "+expectedMatchRecords.get(matcher.getName()) );
                }
                else {
                    LOG.info("* PASS, for matcher: "+matcher.getName() +" got "+matcher.getNumMatchPointsInMatcher()+" matchpoints expected: "+expectedMatchRecords.get(matcher.getName()) );
                }
            }


            // at this point, artificially add a record with known matches, verify you get them, flush, should be no matches, then load, should have the matches back.
            // , ideally harvest from a 2nd repo (that contains some matching records)?

        } catch (Throwable t) {
            LOG.error("Exception occured when running MarkProviderDeletedTest!", t);
            getUtil().throwIt(t);
        }
    }
}
