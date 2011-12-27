package xc.mst.services.marcaggregation.matchrules;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.services.marcaggregation.matcher.ISSNMatcher;
import xc.mst.services.marcaggregation.matcher.MatchSet;

/**
 * look at 010$a (Lccn) and 022$a (ISSN) - both must match.
 * @author JohnB
 *
 */
public class Step2bMatchRule extends BaseMatchRule {

    private static final Logger LOG = Logger.getLogger(Step2bMatchRule.class);

	@Override
    public Set<Long> determineMatches(MatchSet ms) {
        Set<Long> matchedIds = new HashSet<Long>();
        for (long matchedRecordId : ms.getMatches("ISSN")) {
            if (ms.getMatches("Lccn").contains(matchedRecordId)) {
                matchedIds.add(matchedRecordId);
            }
        }
        // for unit testing I changed the order of what was matched 1st to ISSN,
        //    since matches are required for both, this doesn't affect the final code.
        //    though it may be that more matches are found for ISSN, thus this order performs worse.
        /*
        for (long matchedRecordId : ms.getMatches("Lccn")) {
            if (ms.getMatches("ISSN").contains(matchedRecordId)) {
                matchedIds.add(matchedRecordId);
            }
        }
*/
        return matchedIds;
    }

}
