/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation.matcher;

/**
 * @author Benjamin D. Anderson
 *
 * The lifetime of an instance of this class is meant to endure during the processing
 * of one record.
 */
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.apache.log4j.Logger;

import xc.mst.bo.record.SaxMarcXmlRecord;

public class MatchSet {
    boolean debug = true;
    private static final Logger LOG = Logger.getLogger(MatchSet.class);
    protected SaxMarcXmlRecord inProcessRecord = null;

    protected Map<String, FieldMatcher> matchers =
        new HashMap<String, FieldMatcher>();

    // caches the output of previous matches
    protected Map<FieldMatcher, Set<Long>> matches =
        new HashMap<FieldMatcher, Set<Long>>();

    public MatchSet(SaxMarcXmlRecord smr) {
        this.inProcessRecord = smr;
    }

    public void addMatcher(String s, FieldMatcher fm) {
        matchers.put(s, fm);
    }

    public Set<Long> getMatches(String matcherKey) {
        FieldMatcher fm = matchers.get(matcherKey);
        Set<Long> matchSet = matches.get(fm);
        if (matchSet == null) {
            matchSet = new HashSet<Long>();
            List<Long> list = fm.getMatchingOutputIds(this.inProcessRecord);
            if (list != null && !list.isEmpty()) {

                if (debug) {
                    LOG.info("for recordId:"+inProcessRecord.recordId+" matching ids:");
                    for (Long num: list) {
                        LOG.info("** for recordId:"+inProcessRecord.recordId+" matching id==>"+num);
                    }
                }
                matchSet.addAll(list);
            }
            // I don't think this is the right place to do this.  For one thing, won't get all matchpoints for a records if you do it here.
            // add to matcher, after we make the comparison, so we don't get the record back as a match for itself.
            //fm.addRecordToMatcher(this.inProcessRecord);  // is this the place to do this?  (was originally missing)
        }
        return matchSet;
    }

}
