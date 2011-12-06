/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation;

import java.util.*;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.services.impl.service.GenericMetadataService;
import xc.mst.services.marcaggregation.matcher.FieldMatcher;
import xc.mst.services.marcaggregation.matcher.MatchSet;
import xc.mst.services.marcaggregation.matchrules.MatchRuleIfc;

/**
 * @author Benjamin D. Anderson
 *
 */
public class MarcAggregationService extends GenericMetadataService {

    private static final Logger LOG = Logger.getLogger(MarcAggregationService.class);
    protected Map<String, FieldMatcher> matcherMap = null;
    protected Map<String, MatchRuleIfc> matchRuleMap = null;

    public void setup() {
        this.matcherMap = new HashMap<String, FieldMatcher>();
        //TODO refactor these strings to be returned from 1 place (I have this code 2 places now)
        String[] mpStrs = new String[] {
                "Lccn",
                "ISBN",
                "ISSN",
                "SystemControlNumber",
                "x024a",
                "x028ab",
                "x130a",
                "x240a",
                "x260abc",
                "x245ah"};
        for (String mp : mpStrs) {
            FieldMatcher m = (FieldMatcher) config.getBean(mp + "Matcher");
            matcherMap.put(mp, m);
            m.load();
        }
        this.matchRuleMap = new HashMap<String, MatchRuleIfc>();
        String[] mrStrs = new String[] {
                "Step1a",
//                "Step2a",
                "Step3a",
        };
        for (String mrStr : mrStrs) {
            MatchRuleIfc mr = (MatchRuleIfc) config.getBean(mrStr + "MatchRule");
            matchRuleMap.put(mrStr, mr);
        }
    }

    public List<OutputRecord> process(InputRecord r) {
        try {

            if (r.getStatus() != Record.DELETED) {
                SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());

                MatchSet ms = new MatchSet(smr);
                for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                    String matchPointKey = me.getKey();
                    FieldMatcher matcher = me.getValue();
                    ms.addMatcher(matchPointKey, matcher);
                }

                Set<Long> previouslyMatchedRecordIds = null;

                Set<Long> matchedRecordIds = new HashSet<Long>();
                for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
                    String matchRuleKey = me.getKey();
                    MatchRuleIfc matchRule = me.getValue();
                    Set<Long> set = matchRule.determineMatches(ms);
                    if (set !=null && !set.isEmpty()) {
                        matchedRecordIds.addAll(set);
                    }
                }

                if (r.getSuccessors().size() == 0) {
                    // NEW-ACTIVE

                } else {
                    // UPDATE-ACTIVE
                        // unmerge
                        /*
                        for (inputBibId : inputBibIds) {
                            customProcessQueue.push(inputBibId)
                        }
                        for (inputHoldingId : inputHoldingIds) {
                            customProcessQueue.push(inputHoldingId)
                        }
                        */
                }
            } else {
                if (r.getSuccessors().size() == 0) {
                    // NEW-DELETED
                } else {
                    // UPDATE-DELETED
                }
            }

        } catch (Throwable t) {
            util.throwIt(t);
        }
        return null;
    }

}
