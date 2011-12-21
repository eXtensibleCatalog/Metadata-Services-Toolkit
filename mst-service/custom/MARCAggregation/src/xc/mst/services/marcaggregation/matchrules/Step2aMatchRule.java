package xc.mst.services.marcaggregation.matchrules;

import java.util.HashSet;
import java.util.Set;

import xc.mst.services.marcaggregation.matcher.MatchSet;

/**
 * look at 010$a (Lccn) and 020$a (ISBN) - both must match.
 * @author JohnB
 *
 */
public class Step2aMatchRule extends BaseMatchRule {

	@Override
    public Set<Long> determineMatches(MatchSet ms) {
        Set<Long> matchedIds = new HashSet<Long>();

        for (long matchedRecordId : ms.getMatches("ISBN")) {
            if (ms.getMatches("Lccn").contains(matchedRecordId)) {
                matchedIds.add(matchedRecordId);
            }
        }

        return matchedIds;
    }
}
