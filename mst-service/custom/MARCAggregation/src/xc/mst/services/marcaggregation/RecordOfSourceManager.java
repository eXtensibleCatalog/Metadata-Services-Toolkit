package xc.mst.services.marcaggregation;

import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongObjectHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;
import xc.mst.manager.BaseService;
import xc.mst.repo.Repository;
import xc.mst.services.marcaggregation.dao.MarcAggregationServiceDAO;
import xc.mst.utils.TimingLogger;

/**
 * This is a collection of methods used by the MARC Aggregation Service, MAS, to determine
 * record of source. There are multiple ways to configure this feature.  See the comments
 * for method determineRecordOfSource below for more on that.
 *
 * By extending BaseService, it is auto-created by Spring as a singleton bean
 * @author John Brand
 *
 */
public class RecordOfSourceManager extends BaseService {


    private static final Logger LOG               = Logger.getLogger(RecordOfSourceManager.class);

    /**
     * record-of-source-related class variables
     */
    private List<Character> leaderVals = null;
    private boolean leader_byte17_weighting_enabled;
    private boolean bigger_record_weighting_enabled;

    /**
     * currently, either leader_byte17_weighting_enabled or bigger_record_weighting_enabled or both must
     * be enabled.  (not neither)
     * @return true if the configuration is bad.
     */
    protected boolean isRecordOfSourceOptionsConfiguredIncorrectly() {
        return (!leader_byte17_weighting_enabled && !bigger_record_weighting_enabled);
    }

    /**
     * read the config file to determine how to determine record of source for this service instance.
     */
    protected void setupRecordOfSource() {
        // determine record of source leader character priority, byte 17
        leaderVals = new ArrayList<Character>();
        MarcAggregationService mas = (MarcAggregationService) config.getBean("MarcAggregationService");
        List<String> _leaderVals = mas.getConfigFileValues("leader.order");
        for (String val: _leaderVals) {
            LOG.debug("Leader val==>"+val+"<== val length="+val.length());
            if (val.length() == 3) {
                leaderVals.add(val.charAt(1));  // char between quotes
            }
            else {
                leaderVals.add(val.charAt(0));
            }
        }
        leader_byte17_weighting_enabled= config.getPropertyAsBoolean("leader_byte17_weighting_enabled", false);
        bigger_record_weighting_enabled= config.getPropertyAsBoolean("bigger_record_weighting_enabled", false);
    }

    protected InputRecord getRecordOfSourceRecord(HashSet<Long> set, Repository repo, TLongObjectHashMap<RecordOfSourceData> scores) {
    	return getRecordOfSourceRecord(set, repo, scores, null);
    }
    
    protected InputRecord getRecordOfSourceRecord(HashSet<Long> set, Repository repo, TLongObjectHashMap<RecordOfSourceData> scores, TLongLongHashMap rosMap) {
        final Long recordOfSource = determineRecordOfSource(set, repo, scores, rosMap);
        LOG.debug("**** Record of Source == "+recordOfSource);  // obviously produces much writing to log
        //TODO should we be hanging on to who we chose as record of source?  (for the update case?)

        final Record theSrcRecord = repo.getRecord(recordOfSource);
        return theSrcRecord;
    }

    /**
     # Record of source criteria:
     #
     # 1) leader_byte17_weighting_enabled = true/false
     # 2) bigger_record_weighting_enabled = true/false
     #
     # And four cases:
     #
     # 1-true, 2-false
     # In this case we first compare Leader/byte17, pick the earliest (in String leader.order above),
     #   if they are the same, pick the record that is being processed.
     #
     # 1-true, 2-true
     # In this case we first compare Leader/byte17, pick the earliest (in String leader.order above),
     #   if they are the same, pick the record that is largest in bytes.
     #
     # 1-false, 2-true
     # Pick the record that is largest in bytes.
     #
     # 1-false, 2-false
     # This is a not-allowed state and the service will throw an error message.
     #
     * @param set
     * @param repo //for date tie-breaker
     * @return
     */
    protected Long determineRecordOfSource(Set<Long> set, Repository repo, TLongObjectHashMap<RecordOfSourceData> _scores, TLongLongHashMap rosMap) {
        TimingLogger.start("RecordOfSourceManager.determineRecordOfSource");

        TreeMap<SortableRecordOfSourceData, RecordOfSourceData> sortedMap = new TreeMap<SortableRecordOfSourceData, RecordOfSourceData>();
        for (Long num: set) {

            // grab leader byte 17 value and size
            RecordOfSourceData source;
            if (!_scores.containsKey(num)) {
                MarcAggregationServiceDAO masDAO = (MarcAggregationServiceDAO) config.getBean("MarcAggregationServiceDAO");

                source = masDAO.getScoreData(num);
            }
            else {
                //use the data already in memory.
                source = _scores.get(num);
            }
            LOG.debug("Source data for id: "+num+" char:"+source.leaderByte17+": "+" size="+source.size);

            // use leaderVals:
            // List<Character> leaderVals
            // leader_byte17_weighting_enabled;
            // bigger_record_weighting_enabled;
            sortedMap.put(new SortableRecordOfSourceData(repo,leaderVals,num,source, leader_byte17_weighting_enabled ,bigger_record_weighting_enabled ), source);
        }
        final Long RoS = sortedMap.firstKey().recordId;
        
        // In case caller wants to keep track of the Record of Source for the matchset (each member of the set will be mapped to the RoS)
        if (rosMap != null) {
            for (Long num: set) {
            	rosMap.put(num, RoS);
            }
        }
        TimingLogger.stop("RecordOfSourceManager.determineRecordOfSource");
        return RoS;
    }

}
