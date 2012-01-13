package xc.mst.services.marcaggregation.matchrules;

import java.util.HashSet;
import java.util.Set;

import xc.mst.services.marcaggregation.matcher.MatchSet;

public class Step3aMatchRule extends BaseMatchRule {

    public Set<Long> determineMatches(MatchSet ms) {
        Set<Long> matchedIds = new HashSet<Long>();

        //TODO, ISBN could already have been checked once in rule 2a,
        //      figure how to see/save this result?
        for (long matchedRecordId : ms.getMatches("ISBN")) {
            if (ms.getMatches("x130a").contains(matchedRecordId) ||
                    (ms.getMatches("x240a").contains(matchedRecordId) ||
                    (ms.getMatches("x245ah").contains(matchedRecordId)))) {
                matchedIds.add(matchedRecordId);
            }
        }

        return matchedIds;
    }
}