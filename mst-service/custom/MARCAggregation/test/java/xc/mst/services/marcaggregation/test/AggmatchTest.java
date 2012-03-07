package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AggmatchTest extends MatchRulesTest {

    private static final Logger LOG = Logger.getLogger(AggmatchTest.class);

    protected String getTestName() {
        return "AggmatchTest";
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

        expectedMatchRecordIds.put("ISSNMatcher", 6); //022a
        expectedMatchRecords.put  ("ISSNMatcher", 3); // confirmed both

        // verified the records and match points, P.S., this is a good test, has lots of corner cases in the data.
        expectedMatchRecordIds.put("ISBNMatcher", 12); //020
        expectedMatchRecords.put  ("ISBNMatcher", 13);

        expectedMatchRecordIds.put("x024aMatcher", 11); //verified, also a good test.
        expectedMatchRecords.put  ("x024aMatcher", 8);

        expectedMatchRecordIds.put("x130aMatcher", 0);  // if we turn on fuzzy this will go to 14. (or whatever the cnt. is)
        expectedMatchRecords.put  ("x130aMatcher", 0);  //TODO this will need to be modified to 14.

        expectedMatchRecordIds.put("LccnMatcher", 14);  //verified
        expectedMatchRecords.put  ("LccnMatcher", 9);

        // confirmed, note in this file the 035's are frequently near the end of the record.
        expectedMatchRecordIds.put("SystemControlNumberMatcher", 28);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 49);

        // 2a
        expectedResults.add(getExpectedMatchSet(new long[]{1,2}));

        // 2b
        expectedResults.add(getExpectedMatchSet(new long[]{5,6}));

        // 2c
        expectedResults.add(getExpectedMatchSet(new long[]{9,10}));

        // 2a
        expectedResults.add(getExpectedMatchSet(new long[]{13,20}));

        // rule 1
        expectedResults.add(getExpectedMatchSet(new long[]{18,19}));
        expectedResults.add(getExpectedMatchSet(new long[]{25,26}));

    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("aggmatch_norm");
        return fileStrs;
    }
    protected void testDAO() {}
}
