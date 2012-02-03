package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

public class AggmatchTest extends MatchRulesTest {

    private static final Logger LOG = Logger.getLogger(AggmatchTest.class);



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
        expectedMatchRecords.put  ("ISSNMatcher", 3);

        expectedMatchRecordIds.put("ISBNMatcher", 18); //020
        expectedMatchRecords.put  ("ISBNMatcher", 18);

        expectedMatchRecordIds.put("x024aMatcher", 17);
        expectedMatchRecords.put  ("x024aMatcher", 18);

        expectedMatchRecordIds.put("x130aMatcher", 0);  // if we turn on fuzzy this will go to 14. (or whatever the cnt. is)
        expectedMatchRecords.put  ("x130aMatcher", 0);  //TODO this will need to be modified to 14.

        expectedMatchRecordIds.put("LccnMatcher", 14);
        expectedMatchRecords.put  ("LccnMatcher", 14);

        expectedMatchRecordIds.put("SystemControlNumberMatcher", 54);
        expectedMatchRecords.put  ("SystemControlNumberMatcher", 54);

        // 2a
        HashSet<Long> set =  new HashSet<Long>();
        set.add(1l);
        expectedResults.put(new Long(2), set);

        // 2b
        HashSet<Long> set2 =  new HashSet<Long>();
        set2.add(5l);
        expectedResults.put(new Long(6), set2);

        // 2c
        HashSet<Long> set3 =  new HashSet<Long>();
        set3.add(9l);
        expectedResults.put(new Long(10), set3);

        //other way too, with db in the picture
        // rule
        HashSet<Long> set1 =  new HashSet<Long>();
        set1.add(2l);
        expectedResults.put(new Long(1), set1);

        HashSet<Long> set20 =  new HashSet<Long>();
        set20.add(6l);
        expectedResults.put(new Long(5), set20);

        HashSet<Long> set30 =  new HashSet<Long>();
        set30.add(10l);
        expectedResults.put(new Long(9), set30);
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("aggmatch_norm");
        return fileStrs;
    }

    protected void reportFailure(String result) {
        //eventually we'll just throw a  runtime exception, and won't need to override this.
        LOG.info(result);
    }

    protected void reportFailure(Exception e) {
        //eventually we'll just throw a  runtime exception, and won't need to override this.
        LOG.info(e);
    }
    protected void testDAO() {}
}
