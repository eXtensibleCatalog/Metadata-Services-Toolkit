package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This test is a bigger update/delete test.  Starts with records from the dedup directory, for this test called T0.xml.
 * Then updates and deletes are applied in file T1.xml.
 * Then 40 bibs and their 44 holds, new records, are applied in file T2.xml.
 * These T2 records are for testing match step2b.
 *
 * Right now, I have failure commented out - I need to analyze num matchpoints expected, and what matches are expected,
 * then will update the setupMatcherExpections method.
 *
 * @author John Brand
 *
 */
public class MST_494_Test extends MatchRulesTest {

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

        expectedMatchRecordIds.put("ISSNMatcher", 52);  //
        expectedMatchRecords.put  ("ISSNMatcher", 10);  //

        expectedMatchRecordIds.put("ISBNMatcher", 82);  //TODO confirm both of these
        expectedMatchRecords.put  ("ISBNMatcher", 24);

        expectedMatchRecordIds.put("x024aMatcher", 4); //confirmed
        expectedMatchRecords.put  ("x024aMatcher", 4); //confirmed

        expectedMatchRecordIds.put("LccnMatcher", 76); //TODO confirm both of these
        expectedMatchRecords.put  ("LccnMatcher", 16);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 110); //TODO confirm both of these
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 114);

        expectedResults.add(getExpectedMatchSet(new long[]{1, 25, 48, 94, 107, 167, 197, 234, 251, 316, 678}));
        expectedResults.add(getExpectedMatchSet(new long[]{3, 27, 50, 64, 96, 109, 162, 169, 203, 253, 318}));
        expectedResults.add(getExpectedMatchSet(new long[]{10, 29, 52, 66, 132, 171, 205, 255, 680}));
        expectedResults.add(getExpectedMatchSet(new long[]{12, 73, 98, 115, 177, 209, 264, 324}));
        expectedResults.add(getExpectedMatchSet(new long[]{14, 35, 54, 75, 100, 117, 179, 240, 267, 326, 673}));
        expectedResults.add(getExpectedMatchSet(new long[]{31, 68, 111, 134, 173, 238, 257, 671, 684}));
        expectedResults.add(getExpectedMatchSet(new long[]{33, 71, 113, 137, 175, 207, 262}));
        expectedResults.add(getExpectedMatchSet(new long[]{56, 58, 82, 121, 141, 164, 185, 211, 243, 275, 329}));
        expectedResults.add(getExpectedMatchSet(new long[]{62, 160, 201}));
        expectedResults.add(getExpectedMatchSet(new long[]{22, 60, 89, 104, 125, 154, 191, 222, 248, 308, 312, 333, 676, 682}));
        expectedResults.add(getExpectedMatchSet(new long[]{704, 710, 728, 771, 774}));
        expectedResults.add(getExpectedMatchSet(new long[]{706, 711, 730, 773, 776}));
        expectedResults.add(getExpectedMatchSet(new long[]{707, 721}));
        expectedResults.add(getExpectedMatchSet(new long[]{708, 722}));
        expectedResults.add(getExpectedMatchSet(new long[]{709, 716, 726}));
        expectedResults.add(getExpectedMatchSet(new long[]{705, 712, 772, 775}));
        expectedResults.add(getExpectedMatchSet(new long[]{713, 717, 785}));
        expectedResults.add(getExpectedMatchSet(new long[]{714, 731}));
        expectedResults.add(getExpectedMatchSet(new long[]{715, 725, 732}));
        expectedResults.add(getExpectedMatchSet(new long[]{718, 727, 734, 735}));
        expectedResults.add(getExpectedMatchSet(new long[]{719, 784}));
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("MST-494");
        return fileStrs;
    }

    protected String getTestName() {
        return "MST_494_Test";
    }


    protected void reportFailure(String result) {
        LOG.info(result);
//        throw new RuntimeException(result);
    }

    protected void reportFailure(Exception e) {
        LOG.info(e);
//        throw new RuntimeException(e);
    }


}


