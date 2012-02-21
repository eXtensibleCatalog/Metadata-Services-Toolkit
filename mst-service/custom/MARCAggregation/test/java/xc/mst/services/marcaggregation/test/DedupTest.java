package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        expectedMatchRecordIds.put("x130aMatcher", 0);
        expectedMatchRecords.put  ("x130aMatcher", 0);
        //TODO end of above TODO

        expectedMatchRecordIds.put("ISSNMatcher", 14);  //confirmed
        expectedMatchRecords.put  ("ISSNMatcher", 2);   //confirmed

        expectedMatchRecordIds.put("ISBNMatcher", 78);  //TODO confirm both of these
        expectedMatchRecords.put  ("ISBNMatcher", 23);

        expectedMatchRecordIds.put("x024aMatcher", 3); //confirmed
        expectedMatchRecords.put  ("x024aMatcher", 3); //confirmed

        expectedMatchRecordIds.put("LccnMatcher", 49); //TODO confirm both of these
        expectedMatchRecords.put  ("LccnMatcher", 8);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 92); //TODO confirm both of these
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 28);


        // there are many matches, large match sets, so may need to come up with a different way to express this.
        /*
{5,14,35,54,75,100,117,179,240,267,326}
{22,60,89,104,125,154,191,222,248,308,312,333}
{56,58,82,121,141,164,185,211,243,275,329}
{12,73,98,115,177,209,264,324}
{31,68,111,134,173,238,257,320}
{3,27,50,64,96,109,162,169,203,236,253,318}
{1,25,48,94,107,167,197,234,251,316}
         */
    }

    // there are many matches, large match sets, so may need to come up with a different way to express this.
    protected void checkNumberMatchedResults(Map<Long,Set<Long>> results, int goal) {}

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
