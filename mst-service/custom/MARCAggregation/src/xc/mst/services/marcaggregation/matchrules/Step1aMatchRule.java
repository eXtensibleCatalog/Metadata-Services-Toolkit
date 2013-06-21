/**
  * Copyright (c) 2011 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation.matchrules;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.services.marcaggregation.matcher.MatchSet;

/**
 * compare the normalized 035a to all previously processed records' 035a's.
 * (This is done by doing a lookup on the matchpoints_035a data structure using the normalized 035a as a key).
 * If there are any records with equivalent 035a's, then those records are considered a match.

 * @author John Brand
 *
 */
public class Step1aMatchRule extends BaseMatchRule {

    private boolean debug = false;

    private static final Logger LOG = Logger.getLogger(Step1aMatchRule.class);

    public Set<Long> determineMatches(MatchSet ms) {
        Set<Long> matchedIds = new HashSet<Long>();
        matchedIds= ms.getMatches("SystemControlNumber");

        if (matchedIds.size() > 0) {
            for (Long result: matchedIds) {
                LOG.debug("step1a has matches==>" + result+"<==");
            }
        }

        return matchedIds;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}
