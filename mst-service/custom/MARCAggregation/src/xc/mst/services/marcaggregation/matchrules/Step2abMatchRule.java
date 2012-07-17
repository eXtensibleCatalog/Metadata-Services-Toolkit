package xc.mst.services.marcaggregation.matchrules;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.services.marcaggregation.matcher.MatchSet;

/**
 * look at 010$a (Lccn) and 1 of 2 other possibilities, originally  had
 * this as 3 separate matchers, but then Lccn was getting searched mult.
 * times.  These are related, so check for Lccn match 1st, then check the
 * other 2 possibilities!
 *
 * @author John Brand
 *
 */
public class Step2abMatchRule extends BaseMatchRule {

    private static final Logger LOG = Logger.getLogger(Step2abMatchRule.class);

    boolean debug = false;

    @Override
    public Set<Long> determineMatches(MatchSet ms) {
        Set<Long> matchedIds = new HashSet<Long>();

        for (long matchedRecordId : ms.getMatches("Lccn")) {

            // rule 2a
            // look at 010$a (Lccn) and 020$a (ISBN) - both must match.
            if (ms.getMatches("ISBN").contains(matchedRecordId)) {
                matchedIds.add(matchedRecordId);
                if (debug) {
                    LOG.info("Step 2a MatchRule, matched.");
                }
            }

            // rule 2b
            // look at 010$a (Lccn) and 022$a (ISSN) - both must match.
            else if (ms.getMatches("ISSN").contains(matchedRecordId)) {
                matchedIds.add(matchedRecordId);
                if (debug) {
                    LOG.info("Step 2b MatchRule, matched.");
                }
            }

            else {
                if (debug) {
                    LOG.info("Step 2ab MatchRule, matched on Lccn but not ISBN or ISSN.");
                }
            }
        }

        if (debug) {
            if (matchedIds.size() > 0) {
                for (Long result: matchedIds) {
                    LOG.info("step2 has matches==>" + result+"<==");
                }
            }
        }

        return matchedIds;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
