/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.services.impl.service.GenericMetadataService;

/**
 * @author Benjamin D. Anderson
 *
 */
public class MarcAggregationService extends GenericMetadataService {
	
	private static final Logger LOG = Logger.getLogger(MarcAggregationService.class);
	protected Map<String, Matcher> matcherMap = null;
	protected Map<String, MatchRule> matchRuleMap = null;
    	public void setup() {
		this.matcherMap = new HashMap<String, Matcher>();
		String[] mpStrs = new String {
			"LCCN", 
			"SystemControlNumber"};
		for (String mp : mpStrs) {
			Matcher m = (Matcher)config.getBean(mp+"Matcher");
			matcherMap.put(mp, m);
			m.loadFromDB();
		}	
		this.matchRuleMap = new HashMap<String, MatchRule>();
		String[] mrStrs = new String[] {
			"Step1a",
			"Step2a",
		};
		for (String mrStr : mrStrs) {
			MatchRule mr = (MatchRule)config.getBean(mrStr+"MatchRule");
			matcherMap.put(mrStr, mr);
		}	
	}

	public List<OutputRecord> process(InputRecord r) {
		
		SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());

		Set<Long> recordsWithCommonMatchPoints = new HashSet<Long>();
		Set<Long> matchedRecords = new HashSet<Long>();
		Map<String, String> recordMatchPoints = new HashMap<String, String>();
		try {
			for (Map.Entry<String, Matcher> me : this.matcherMap.entrySet()) {
				String matchPointKey = me.getKey();
				Matcher matcher = me.getValue();
				recordsWithCommonMatchPoints.addAll(
					matcher.getMatchingOutputIds(smr));
				recordMatchPoints.put(matchPointKey, matcher.getMatchPointValue(r.getId());
			}
			for (Long recordWithCommonMatchPoint : recordsWithCommonMatchPoints) {
				Map<String, String> otherRecordMatchPoints = new HashMap<String, String>();
				for (Map.Entry<String, Matcher> me : this.matcherMap) {
					String matchPointKey = me.getKey();
					Matcher matcher = me.getValue();
					otherRecordMatchPoints.put(matchPointKey, matcher.getMatchPointValue(recordWithCommonMatchPoint);
				}
				// applyRules
				for (Map.Entry<String, MatchRule> me : this.matchRuleMap.entrySet()) {
					String matchRuleKey = me.getKey();
					MatchRule matchRule = me.getValue();
					if (matchRule.isMatch(recordMatchPoints, otherRecordMatchPoints)) {
						LOG.info("matched according to: "+matchRuleKey);
						matchedRecords.add(recordWithCommonMatchPoint);
					} else {
						LOG.info("not matched according to: "+matchRuleKey);
					}
				}
			}
		} catch (Throwable t) {
			util.throwIt(t);
		}
		return records;
	}

}
