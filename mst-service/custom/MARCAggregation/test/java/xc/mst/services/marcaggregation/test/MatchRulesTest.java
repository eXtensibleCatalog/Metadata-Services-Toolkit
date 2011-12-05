/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.repo.Repository;
import xc.mst.services.marcaggregation.matcher.FieldMatcher;
import xc.mst.services.marcaggregation.matcher.MatchSet;
import xc.mst.services.marcaggregation.matchrules.MatchRuleIfc;


public class MatchRulesTest extends MockHarvestTest {

  private static final Logger LOG = Logger.getLogger(MatchRulesTest.class);

    protected Map<String, FieldMatcher> matcherMap = null;
    protected Map<String, MatchRuleIfc> matchRuleMap = null;

    public void setup() {
        this.matcherMap = new HashMap<String, FieldMatcher>();
        String[] mpStrs = new String[] {
                "Lccn",
                "ISBN",
                "SystemControlNumber",
                "x130a",
                "x240a",
                "x245ah"};
        for (String mp : mpStrs) {
            FieldMatcher m = (FieldMatcher) getConfig().getBean(mp + "Matcher");
            matcherMap.put(mp, m);
            m.load();
        }
        this.matchRuleMap = new HashMap<String, MatchRuleIfc>();
        String[] mrStrs = new String[] {
                "Step1a",
//              "Step2a",
                "Step3a",
        };
        for (String mrStr : mrStrs) {
            MatchRuleIfc mr = (MatchRuleIfc) getConfig().getBean(mrStr + "MatchRule");
            matchRuleMap.put(mrStr, mr);
        }
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("demo_175");
        return fileStrs;
    }


    public void finalTest() {
    	setup();
        try {
            // These first 2 steps are done in MockHarvestTest
            // - harvest records into MST and run them through norm service

            System.out.println("****START MatchRulesTest *****");
            Repository providerRepo = getRepositoryService().getRepository(this.provider);
            ensureMatch(providerRepo);

            Repository serviceRepo = getServiceRepository();
//            ensureAllRecordsMatchStatus(serviceRepo, Record.ACTIVE);

            // - ensure there are scheduled harvests
            HarvestSchedule hs = getHarvestScheduleDAO().getHarvestScheduleForProvider(this.provider.getId());
            assert hs != null : "there should be a harvestSchedule for the provider";

//            getProviderService().markProviderDeleted(this.provider);
//            waitUntilFinished();

            // - ensure there are no harvest schedules
//            hs = getHarvestScheduleDAO().getHarvestScheduleForProvider(this.provider.getId());
//            assert hs == null : "there should be a harvestSchedule for the provider";

            // - harvest from provider and norm service
            // - make sure all records are deleted
//            ensureAllRecordsMatchStatus(providerRepo, Record.DELETED);
//            ensureAllRecordsMatchStatus(serviceRepo, Record.DELETED);

            // - create a new harvest schedule
            // clear out previous harvest
//            getProvider().setLastOaiRequest(null);
//            getProviderService().updateProvider(getProvider());
//            createHarvestSchedule(); // you'll end up with active records again...must be from beginning
//            waitUntilFinished();

            // - harvest from provider and norm service
            // - make sure all records are active again
            // - make sure the same ids are used
//            ensureAllRecordsMatchStatus(providerRepo, Record.ACTIVE);
//            ensureAllRecordsMatchStatus(serviceRepo, Record.ACTIVE);

        } catch (Throwable t) {
            LOG.error("Exception occured when running MarkProviderDeletedTest!", t);
            getUtil().throwIt(t);
        }
    }

    protected void ensureMatch(Repository repo) throws Throwable {
        List<Record> records = repo.getRecords(new Date(0), new Date(), 0l, getMarc21Format(), null);
        for (Record r : records) {
//            if (r.getStatus() != status) {
//                throw new RuntimeException("For repo: " + repo.getName() + ",record with id: " + r.getId() + " has status:" + r.getStatus() + " while expecting:" + status);
//            }
        	process((InputRecord)r);
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
                    matchedRecordIds.addAll(matchRule.determineMatches(ms));
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
            getUtil().throwIt(t);
        }
        return null;
    }
}
