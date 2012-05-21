package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * by adding a NEW record to the 'small' set (as an update) (see 'small' folder) that matches
 * with an existing match set {3,5}, test that code path.
 *
 * Result:  2 match sets, {2,6}, {3,5,13}, all others should map directly to an output.
 * Also, make it so record 13 becomes the record of source instead of 5.
 * Starting simple!
 *
 * @author John Brand
 *
 */
public class Update_1_RemergeAddTest extends MatchRulesTest {

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

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 4);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 3);


        expectedResults.add(getExpectedMatchSet(new long[]{3,5,13}));
        expectedResults.add(getExpectedMatchSet(new long[]{2,6}));
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("update_1_remerge_add");
        return fileStrs;
    }

    protected String getTestName() {
        return "Update_1_RemergeAddTest";
    }
}

