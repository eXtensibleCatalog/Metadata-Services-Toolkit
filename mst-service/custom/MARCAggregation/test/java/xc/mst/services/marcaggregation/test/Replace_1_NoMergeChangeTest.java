package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

/**
 * by deleting the 1st record in the 'small' set (see 'small' folder), test that
 * 'update-delete' method gets called, and matchpoint data for this record gets
 * deleted, note the 1st record is not part of a merge set.  starting simple!
 *
 * @author John Brand
 *
 */
public class Replace_1_NoMergeChangeTest extends SmallTest {

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("replace_1_no_affect");
        return fileStrs;
    }

    protected String getTestName() {
        return "Replace_1_NoMergeChangeTest";
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

        expectedMatchRecordIds.put("x130aMatcher", 0);
        expectedMatchRecords.put  ("x130aMatcher", 0);
        //TODO end of above TODO

        expectedMatchRecordIds.put("ISSNMatcher", 2);
        expectedMatchRecords.put  ("ISSNMatcher", 1);

        expectedMatchRecordIds.put("ISBNMatcher", 2);
        expectedMatchRecords.put  ("ISBNMatcher", 3);

        expectedMatchRecordIds.put("x024aMatcher", 0);
        expectedMatchRecords.put  ("x024aMatcher", 0);

        expectedMatchRecordIds.put("LccnMatcher", 5);  // remove one 010 from 'small' record set
        expectedMatchRecords.put  ("LccnMatcher", 3);  // but it was a duplicated 010, so this number stays same as 'small'

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 3);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 3);


        expectedResults.add(getExpectedMatchSet(new long[]{3,5}));
        expectedResults.add(getExpectedMatchSet(new long[]{2,6}));
    }

}
