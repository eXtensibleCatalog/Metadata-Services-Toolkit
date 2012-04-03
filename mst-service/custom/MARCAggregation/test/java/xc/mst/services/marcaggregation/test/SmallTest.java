package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class SmallTest extends MatchRulesTest {

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

        expectedMatchRecordIds.put("ISSNMatcher", 2);
        expectedMatchRecords.put  ("ISSNMatcher", 1);

        expectedMatchRecordIds.put("ISBNMatcher", 2);
        expectedMatchRecords.put  ("ISBNMatcher", 3);

        expectedMatchRecordIds.put("x024aMatcher", 0);
        expectedMatchRecords.put  ("x024aMatcher", 0);

        expectedMatchRecordIds.put("LccnMatcher", 6); 
        expectedMatchRecords.put  ("LccnMatcher", 3);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 3); 
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 3);


        expectedResults.add(getExpectedMatchSet(new long[]{3,5}));
        expectedResults.add(getExpectedMatchSet(new long[]{2,6}));
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("small");
        return fileStrs;
    }

    protected String getTestName() {
        return "SmallTest";
    }
}
