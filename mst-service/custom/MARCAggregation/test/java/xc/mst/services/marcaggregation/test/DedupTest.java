package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DedupTest extends MatchRulesTest {

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
        //TODO end of above TODO

        expectedMatchRecordIds.put("ISSNMatcher", 14);
        expectedMatchRecords.put  ("ISSNMatcher", 14);

        expectedMatchRecordIds.put("ISBNMatcher", 34);
        expectedMatchRecords.put  ("ISBNMatcher", 57);

        expectedMatchRecordIds.put("x024aMatcher", 17);
        expectedMatchRecords.put  ("x024aMatcher", 18);

        expectedMatchRecordIds.put("x130aMatcher", 0);  // if we turn on fuzzy this will go to 11.
        expectedMatchRecords.put  ("x130aMatcher", 0);  //TODO this will need to be modified to 11.

        expectedMatchRecordIds.put("LccnMatcher", 56);
        expectedMatchRecords.put  ("LccnMatcher", 56);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 118);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 151);
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("dedup");
        return fileStrs;
    }

    //TODO need to figure out the right number!
    protected int getNumberMatchedResultsGoal() {
        return 0;
    }

    protected void reportFailure(String result) {
        //eventually we'll just throw a  runtime exception, and won't need to override this.
        LOG.info(result);
    }

    protected void reportFailure(Exception e) {
        //eventually we'll just throw a  runtime exception, and won't need to override this.
        LOG.info(e);
    }
}
