package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class CarliStep2abTest extends MatchRulesTest {

    private static final Logger LOG = Logger.getLogger(DedupTest.class);

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

        expectedMatchRecordIds.put("ISSNMatcher", 38);  //must confirm
        expectedMatchRecords.put  ("ISSNMatcher", 19);   //must confirm

        expectedMatchRecordIds.put("ISBNMatcher", 36);  //TODO confirm both of these
        expectedMatchRecords.put  ("ISBNMatcher", 42);

        expectedMatchRecordIds.put("x024aMatcher", 0); //confirm
        expectedMatchRecords.put  ("x024aMatcher", 0); //confirm

        expectedMatchRecordIds.put("LccnMatcher", 64); //TODO confirm both of these
        expectedMatchRecords.put  ("LccnMatcher", 37);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 74); //TODO confirm both of these
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 121);


        expectedResults.add(getExpectedMatchSet(new long[]{1,12}));
        expectedResults.add(getExpectedMatchSet(new long[]{2,13}));
        expectedResults.add(getExpectedMatchSet(new long[]{3,14}));
        expectedResults.add(getExpectedMatchSet(new long[]{4,15}));
        expectedResults.add(getExpectedMatchSet(new long[]{5,16}));
        expectedResults.add(getExpectedMatchSet(new long[]{6,17}));
        expectedResults.add(getExpectedMatchSet(new long[]{7,18}));
        expectedResults.add(getExpectedMatchSet(new long[]{8,19}));
        expectedResults.add(getExpectedMatchSet(new long[]{9,20}));
        expectedResults.add(getExpectedMatchSet(new long[]{10,21}));
        expectedResults.add(getExpectedMatchSet(new long[]{11,22}));
        expectedResults.add(getExpectedMatchSet(new long[]{28,35}));
        expectedResults.add(getExpectedMatchSet(new long[]{37,52}));
        expectedResults.add(getExpectedMatchSet(new long[]{38,53}));
        expectedResults.add(getExpectedMatchSet(new long[]{39,54}));
        expectedResults.add(getExpectedMatchSet(new long[]{40,55}));
        expectedResults.add(getExpectedMatchSet(new long[]{41,56}));
        expectedResults.add(getExpectedMatchSet(new long[]{42,57}));
        expectedResults.add(getExpectedMatchSet(new long[]{43,58}));
        expectedResults.add(getExpectedMatchSet(new long[]{44,59}));
        expectedResults.add(getExpectedMatchSet(new long[]{45,60}));
        expectedResults.add(getExpectedMatchSet(new long[]{46,61}));
        expectedResults.add(getExpectedMatchSet(new long[]{47,62}));
        expectedResults.add(getExpectedMatchSet(new long[]{48,63}));
        expectedResults.add(getExpectedMatchSet(new long[]{49,64}));
        expectedResults.add(getExpectedMatchSet(new long[]{50,65}));
        expectedResults.add(getExpectedMatchSet(new long[]{51,66}));
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("carli_step_2ab");
        return fileStrs;
    }

    protected String getTestName() {
        return "CarliStep2abTest";
    }
}
