package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 *  Just 2 records that match on 035 to test this ability.
 */
public class Small035Test extends MatchRulesTest {

    private static final Logger LOG = Logger.getLogger(SmallTest.class);

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

        expectedMatchRecordIds.put("x130aMatcher", 0);
        expectedMatchRecords.put  ("x130aMatcher", 0);
        //TODO end of above TODO

        expectedMatchRecordIds.put("ISSNMatcher", 0);
        expectedMatchRecords.put  ("ISSNMatcher", 0);

        expectedMatchRecordIds.put("ISBNMatcher", 0);
        expectedMatchRecords.put  ("ISBNMatcher", 0);

        expectedMatchRecordIds.put("x024aMatcher", 0);
        expectedMatchRecords.put  ("x024aMatcher", 0);

        expectedMatchRecordIds.put("LccnMatcher", 0); 
        expectedMatchRecords.put  ("LccnMatcher", 0);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 2); 
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 1);


        expectedResults.add(getExpectedMatchSet(new long[]{1,2}));
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("small_035");
        return fileStrs;
    }

    protected String getTestName() {
        return "Small035Test";
    }
}
