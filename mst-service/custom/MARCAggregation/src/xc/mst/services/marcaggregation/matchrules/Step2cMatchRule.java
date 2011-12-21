package xc.mst.services.marcaggregation.matchrules;

import java.util.HashSet;
import java.util.Set;

import xc.mst.services.marcaggregation.matcher.MatchSet;

/**
 * look at 010$a (Lccn) and 024$a - both must match.
 * @author JohnB
 *
 */
public class Step2cMatchRule extends BaseMatchRule {

	@Override
    public Set<Long> determineMatches(MatchSet ms) {
        Set<Long> matchedIds = new HashSet<Long>();

        for (long matchedRecordId : ms.getMatches("Lccn")) {
            if (ms.getMatches("x024a").contains(matchedRecordId)) {
                matchedIds.add(matchedRecordId);
            }
        }

        return matchedIds;
    }

}
