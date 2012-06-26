/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation.dao;


import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongLongProcedure;
import gnu.trove.TLongObjectHashMap;
import gnu.trove.TLongObjectProcedure;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import xc.mst.services.impl.dao.GenericMetadataServiceDAO;
import xc.mst.services.marcaggregation.RecordOfSourceData;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;
/**
*
* @author John Brand
*
*/
public class MarcAggregationServiceDAO extends GenericMetadataServiceDAO {

    private final static Logger LOG = Logger.getLogger(MarcAggregationServiceDAO.class);

    // not yet used starts
    public final static String matchpoints_028a_table   = "matchpoints_028a";
    public final static String matchpoints_130a_table   = "matchpoints_130a";
    public final static String matchpoints_240a_table   = "matchpoints_240a";
    public final static String matchpoints_245a_table   = "matchpoints_245a";
    public final static String matchpoints_260abc_table = "matchpoints_260abc";
    // not yet used stops

    public final static String matchpoints_010a_table   = "matchpoints_010a";
    public final static String matchpoints_020a_table   = "matchpoints_020a";
    public final static String matchpoints_022a_table   = "matchpoints_022a";
    public final static String matchpoints_024a_table   = "matchpoints_024a";
    public final static String matchpoints_035a_table   = "matchpoints_035a";

    public final static String merge_scores_table       = "merge_scores";
    public final static String merged_records_table     = "merged_records";
    public final static String bib_records_table        = "bib_records";

    public final static String input_record_id_field    = "input_record_id";
    public final static String string_id_field          = "string_id";
    public final static String numeric_id_field         = "numeric_id";
    public final static String leaderByte17_field       = "leaderByte17";
    public final static String size_field               = "size";


    @SuppressWarnings("unchecked")
    public void persist2StrMatchpointMaps(Map<Long, List<String[]>> inputId2matcherMap, String tableName) {

        TimingLogger.start("MarcAggregationServiceDAO.persist2StrMaps");

        TimingLogger.start("prepare to write");
        String dbLoadFileStr = getDbLoadFileStr();

        final byte[] tabBytes = getTabBytes();
        final byte[] newLineBytes = getNewLineBytes();

        try {
            final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));

            final MutableInt j = new MutableInt(0);
            for (Object keyObj : inputId2matcherMap.keySet()) {
                Long id = (Long) keyObj;
                final byte[] idBytes = String.valueOf(id).getBytes();
                Object list = inputId2matcherMap.get(id);

                try {
                    if (list == null) {
                        continue;
                    }
                    if (j.intValue() > 0) {
                        os.write(newLineBytes);
                    } else {
                        j.increment();
                    }

                    final MutableInt j2 = new MutableInt(0);
                    List<String[]> strList = (List<String[]>) list;
                    LOG.debug("insert: " + tableName + ".size(): " + strList.size());
                    if (strList != null && strList.size() > 0) {
                        for (String[] _s: strList) {
                            try {   // need to loop through all strings associated with id!
                                    if (j2.intValue() > 0) {
                                        os.write(newLineBytes);
                                    } else {
                                        j2.increment();
                                    }
                                    os.write(getBytes(_s[1]));
                                    os.write(tabBytes);
                                    os.write(getBytes(_s[0]));
                                    os.write(tabBytes);
                                    os.write(idBytes);
                            } catch (Exception e) {
                                LOG.error("problem with data - id="+id,e);
                                getUtil().throwIt(e);
                            }
                        }
                    }
                } catch (Throwable t) {
                    LOG.error("*** problem with data readying id="+id,t);
                    getUtil().throwIt(t);
                }
            }
            os.close();
            TimingLogger.stop("prepare to write");

            TimingLogger.start("will replace");
            replaceIntoTable(tableName, dbLoadFileStr);
            TimingLogger.stop("will replace");

        } catch (Exception e4) {
            LOG.error("*** problem with replaceIntoTable data",e4);
            getUtil().throwIt(e4);
        } finally {
            TimingLogger.stop("MarcAggregationServiceDAO.persist2StrMaps");
        }
    }

    @SuppressWarnings("unchecked")
    public void persist1StrMatchpointMaps(Map<Long, List<String>> inputId2matcherMap, String tableName) {
        TimingLogger.start("MarcAggregationServiceDAO.persist1StrMatchpointMaps");
        TimingLogger.start("prepare to write");

        String dbLoadFileStr = getDbLoadFileStr();
        final byte[] tabBytes = getTabBytes();
        final byte[] newLineBytes = getNewLineBytes();

        try {
            final MutableInt j = new MutableInt(0);
            final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
            for (Object keyObj : inputId2matcherMap.keySet()) {
                Long id = (Long) keyObj;
                Object list = inputId2matcherMap.get(id);

                try {
                    if (list == null) {
                        continue;
                    }
                    if (j.intValue() > 0) {
                        os.write(newLineBytes);
                    } else {
                        j.increment();
                    }

                    final byte[] idBytes = String.valueOf(id).getBytes();
                    final MutableInt _j = new MutableInt(0);

                    List<String> strList = (List<String>) list;
                    LOG.debug("insert: " + tableName + ".size(): " + strList.size());
                    if (strList != null && strList.size() > 0) {
                        for (String _s: strList) {
                            if (StringUtils.isEmpty(_s)) {
                                continue;
                            }
                            try {
                                // need to loop through all strings associated with id!
                                //
                                // write the newline after we have written a line, but not at the end of the last line
                                if (_j.intValue() > 0) {
                                    os.write(newLineBytes);
                                } else {
                                    _j.increment();
                                }
                                os.write(getBytes(_s));
                                os.write(tabBytes);
                                os.write(idBytes);
                            } catch (Exception e) {
                                LOG.error("problem with data - id="+id,e);
                                getUtil().throwIt(e);
                            }
                        }
                    }
                } catch (Throwable t) {
                    LOG.error("problem with replaceIntoTable data - id="+id,t);
                    getUtil().throwIt(t);
                }
            }
            os.close();
            TimingLogger.stop("prepare to write");

            TimingLogger.start("will replace");
            replaceIntoTable(tableName, dbLoadFileStr);
            TimingLogger.stop("will replace");

        } catch (Throwable t4) {
            LOG.error("*** problem with replaceIntoTable data",t4);
            getUtil().throwIt(t4);
        } finally {
            TimingLogger.stop("MarcAggregationServiceDAO.persist1StrMatchpointMaps");
        }
    }

    public void persistLongOnly(List<Long> values, String tableName)  {

        TimingLogger.start("MarcAggregationServiceDAO.persistLongOnly");
        try {

            String dbLoadFileStr = getDbLoadFileStr();
            final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
            final MutableInt j = new MutableInt(0);

            final byte[] newLineBytes = getNewLineBytes();
            for (Long value: values) {
                try {
                    if (j.intValue() > 0) {
                        LOG.debug("line break!!! j:" + j.intValue());
                        os.write(newLineBytes);
                    } else {
                        j.increment();
                    }
                    os.write(String.valueOf(value).getBytes());
                } catch (Throwable t) {
                    getUtil().throwIt(t);
                }
            }
            os.close();
            replaceIntoTable(tableName, dbLoadFileStr);
        } catch (Throwable t) {
            LOG.error("problem with replaceIntoTable data ",t);
            getUtil().throwIt(t);
        } finally {
            TimingLogger.stop("MarcAggregationServiceDAO.persistLongOnly");
        }
    }

    /**
     * this one if for persisting those that do not repeat (1 set of entries per record id) and has a TLongLong only for each record id
     * ,also using it to persist bib_records, input_record->output_record
     *
     * @param inputId2numMap
     * @param tableName
     * @param swap - if true, then write the key / value as value / key into the db
     */
    public void persistLongMatchpointMaps(TLongLongHashMap inputId2numMap, String tableName, final boolean swap) {

        TimingLogger.start("MarcAggregationServiceDAO.persistLongMaps");
        try {

            String dbLoadFileStr = getDbLoadFileStr();
            final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
            final MutableInt j = new MutableInt(0);

            final byte[] tabBytes = getTabBytes();
            final byte[] newLineBytes = getNewLineBytes();

            if (inputId2numMap instanceof TLongLongHashMap) {
                LOG.debug("insert: " + tableName + ".size(): " + inputId2numMap.size());
                if (inputId2numMap != null && inputId2numMap.size() > 0) {
                    inputId2numMap.forEachEntry(new TLongLongProcedure() {
                        public boolean execute(long id, long num) {
                            try {
                                if (j.intValue() > 0) {
                                    LOG.debug("line break!!! j:" + j.intValue());
                                    os.write(newLineBytes);
                                } else {
                                    j.increment();
                                }
                                if (swap) {        // write value then key
                                    os.write(String.valueOf(num).getBytes());
                                    os.write(tabBytes);
                                    os.write(String.valueOf(id).getBytes());
                                }
                                else {             // write key then value
                                    os.write(String.valueOf(id).getBytes());
                                    os.write(tabBytes);
                                    os.write(String.valueOf(num).getBytes());
                                }
                            } catch (Throwable t) {
                                LOG.error("problem with data - id="+id,t);
                                getUtil().throwIt(t);
                            }
                            return true;
                        }
                    });
                }
            }
            os.close();
            replaceIntoTable(tableName, dbLoadFileStr);
        } catch (Throwable t) {
            LOG.error("problem with replaceIntoTable data - ",t);
            getUtil().throwIt(t);
        } finally {
            TimingLogger.stop("MarcAggregationServiceDAO.persistLongMaps");
        }
    }

    public void persistScores(TLongObjectHashMap<xc.mst.services.marcaggregation.RecordOfSourceData> scores) {

        final String tableName = merge_scores_table;
        TimingLogger.start("MarcAggregationServiceDAO.persistScores");
        try {
            String dbLoadFileStr = getDbLoadFileStr();

            final byte[] tabBytes = getTabBytes();
            final byte[] newLineBytes = getNewLineBytes();

            final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
            final MutableInt j = new MutableInt(0);

            if (scores instanceof TLongObjectHashMap) {
                LOG.debug("insert: " + tableName + ".size(): " + scores.size());
                if (scores != null && scores.size() > 0) {
                    scores.forEachEntry(new TLongObjectProcedure<xc.mst.services.marcaggregation.RecordOfSourceData>() {
                        public boolean execute(long id, xc.mst.services.marcaggregation.RecordOfSourceData source) {
                            try {
                                if (j.intValue() > 0) {
                                    LOG.debug("line break!!! j:" + j.intValue());
                                    os.write(newLineBytes);
                                } else {
                                    j.increment();
                                }
                                os.write(String.valueOf(id).getBytes());
                                os.write(tabBytes);
                                os.write(String.valueOf(source.leaderByte17).getBytes());
                                os.write(tabBytes);
                                os.write(String.valueOf(source.size).getBytes());
                            } catch (Throwable t) {
                                getUtil().throwIt(t);
                            }
                            return true;
                        }
                    });
                }
            }
            os.close();
            replaceIntoTable(tableName, dbLoadFileStr);
        } catch (Throwable t) {
            getUtil().throwIt(t);
        } finally {
            TimingLogger.stop("MarcAggregationServiceDAO.persistScores");
        }
    }

    /**
     * quote the string as otherwise mysql insert fails when inserting '123344\'
     * @param s
     * @return
     */
    protected static byte[] getBytes(String s) {
        final String s3 = getQuoted(s);
        return s3.getBytes();
    }

    protected static byte[] getTabBytes() {
        return "\t".getBytes();
    }

    protected static byte[] getNewLineBytes() {
        return "\n".getBytes();
    }

    protected static String getQuoted(String s) {
        final String s3 = "'"+ s + "'" ;
        return s3;
    }

    // not only does it create the string but it has a side effect - it creates a file from the string,
    // checks for its existence and deletes it if it finds it.
    protected String getDbLoadFileStr() {
        String dbLoadFileStr =
        (MSTConfiguration.getUrlPath() + "/db_load.in").replace('\\', '/');

        File dbLoadFile = new File(dbLoadFileStr);
        if (dbLoadFile.exists()) {
            dbLoadFile.delete();
        }
        return dbLoadFileStr;
    }

    protected void replaceIntoTable(String tableName, String dbLoadFileStr) {
        TimingLogger.start(tableName + ".insert.create_infile");
        TimingLogger.start(tableName + ".insert.load_infile");
        this.jdbcTemplate.execute(
                "load data infile '" + dbLoadFileStr + "' REPLACE into table " +
                        tableName +
                        " character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
                );
        TimingLogger.stop(tableName + ".insert.load_infile");
        TimingLogger.stop(tableName + ".insert.create_infile");
    }

    /**
     *   bib_records
     *   purpose: provides a mapping of input records to output records. This allows for 2 paths:
     *
     *     -------------------------------------------------------------------
     *     | given               | can be determined                           |
     *     |-------------------------------------------------------------------|
     *     | an output_record_id | all the input_records that have been merged |
     *     |                     | together to create this output_record       |
     *     |-------------------------------------------------------------------|
     *     | an input_record_id  | all the other input_records that have been  |
     *     |                     | merged with this input_record and the       |
     *     |                     | corresponding output_record                 |
     *      -------------------------------------------------------------------
     *
     * @param output_record_id
     * @return all the input_records that have been merged together to create this output_record, possibly on 1 record.
     */
    public List<Long> getInputRecordsMappedToOutputRecord(Long output_record_id) {
        TimingLogger.start("MarcAggregationServiceDAO.getInputRecordsMappedToOutputRecord");
        String sql = "select input_record_id from " + bib_records_table +
                            " where output_record_id = ? ";
        final List<Long> results =this.jdbcTemplate.queryForList(sql, Long.class, output_record_id);
        TimingLogger.stop("MarcAggregationServiceDAO.getInputRecordsMappedToOutputRecord");
        return results;
    }

    /**
     * what output record corresponds to this input record?
     * @param input_record_id
     * @return there will only be 1 record number returned.
     */
    public List<Long> getOutputRecordForInputRecord(Long input_record_id) {
        TimingLogger.start("MarcAggregationServiceDAO.getOutputRecordForInputRecord");
        String sql = "select output_record_id from " + bib_records_table +
                            " where input_record_id = ? ";
        final List<Long> results =this.jdbcTemplate.queryForList(sql, Long.class, input_record_id);
        TimingLogger.stop("MarcAggregationServiceDAO.getOutputRecordForInputRecord");
        return results;
    }

    public TLongLongHashMap getBibRecords(/*int page*/) {
        TimingLogger.start("MarcAggregationServiceDAO.getBibRecords");

//        int recordsAtOnce = 100000;
        String sql = "select input_record_id, output_record_id from " + bib_records_table;// +
//                " limit " + (page * recordsAtOnce) + "," + recordsAtOnce;

        List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(sql);
        TLongLongHashMap results = new TLongLongHashMap();
        for (Map<String, Object> row : rowList) {
            Long in_id = (Long) row.get("input_record_id");
            Long out_id = (Long) row.get("output_record_id");
            results.put(in_id, out_id);
        }
        TimingLogger.stop("MarcAggregationServiceDAO.getBibRecords");
        return results;
    }

    /**
     * input records that are part of a merge set (>1 corresponds to an output record)
     * @return
     */
    public List<Long> getMergedInputRecords(/*int page*/) {
        TimingLogger.start("MarcAggregationServiceDAO.getMergedInputRecords");

//      int recordsAtOnce = 100000;
      String sql = "select input_record_id from " + merged_records_table;// +
//              " limit " + (page * recordsAtOnce) + "," + recordsAtOnce;
      List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(sql);
      List<Long> results = new ArrayList<Long>();
      if (rows != null) {
          for (Map<String, Object> row : rows) {
              results.add((Long) row.values().iterator().next());
          }
      }
      TimingLogger.stop("MarcAggregationServiceDAO.getMergedInputRecords");
      return results;
    }

    public RecordOfSourceData getScoreData(Long num) {
        TimingLogger.start("MarcAggregationServiceDAO.getMatchingRecords");

        final String tableName = merge_scores_table;

        String sql = "select "+ leaderByte17_field +", "+ size_field +
                " from " + tableName+ " where "+ input_record_id_field +" = ?";

        List<RecordOfSourceData> rowList = this.jdbcTemplate.query(sql, new Object[] {num}, new RecordOfSourceDataMapper());

        final int size = rowList.size();
        if (size == 0) {
            LOG.error("No rows returned for merge_scores for "+num);
            return null;
        }
        else if (size>1) {
            // enforce through schema?
            LOG.error("multiple rows returned for merge_scores for "+num);
        }
        TimingLogger.stop("MarcAggregationServiceDAO.getMatchingRecords");

        return rowList.get(0);
    }

    /**
     * about RowMapper:
     * An interface used by JdbcTemplate for mapping rows of a ResultSet on a per-row basis.
     * @see RowMapper
     * @author John Brand
     *
     */
    private static final class RecordOfSourceDataMapper implements RowMapper<RecordOfSourceData> {

        public RecordOfSourceData mapRow(ResultSet rs, int rowNum) throws SQLException {
            RecordOfSourceData source;
            char encoding;

            // the ' ' is not getting into the db. Is it a big deal, or is this
            // hack good enough?
            //
            if (StringUtils.isNotEmpty(rs.getString("leaderByte17"))) {
                encoding = rs.getString("leaderByte17").charAt(0);
            }
            else encoding=' ';

            source = new RecordOfSourceData(encoding,rs.getInt("size"));
            return source;
        }
    }

    /**
     * given a string_id in String form to match on. (currently used by ISSN, ISBN, SCCN, x024 matchers)
     *
     *  for instance:
     * mysql -u root --password=root -D xc_marcaggregation -e 'select input_record_id  from matchpoints_035a where string_id = "24094664" '
     *
     * @param tableName
     * @param record_id_field
     * @param string_id_field
     * @param itemToMatch
     * @return
     */
    public List<Long> getMatchingRecords(String tableName, String record_id_field, String string_id_field, String itemToMatch) {
        TimingLogger.start("MarcAggregationServiceDAO.getMatchingRecords");

        String sql = "select "+ record_id_field + " from " + tableName+ " where "+ getQuoted(string_id_field)+ " = ?";

        List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(sql, new Object[] {itemToMatch});

        List<Long> results = new ArrayList<Long>();
        for (Map<String, Object> row : rowList) {
            Long id = (Long) row.get("input_record_id");
            results.add(id);
        }
        TimingLogger.stop("MarcAggregationServiceDAO.getMatchingRecords");
        return results;
    }

    /**
     * given a numeric_id in Long form to match on. (currently used by LCCN matcher)
     *
     * for instance:
     * mysql -u root --password=root -D xc_marcaggregation -e 'select input_record_id  from matchpoints_035a where string_id = "24094664" '
     *
     * @param tableName
     * @param record_id_field for query
     * @param numeric_id_field for query
     * @param itemToMatch
     * @return
     */
    public List<Long> getMatchingRecords(String tableName, String record_id_field, String numeric_id_field, Long itemToMatch) {
        TimingLogger.start("MarcAggregationServiceDAO.getMatchingRecords");

        String sql = "select "+ record_id_field + " from " + tableName+ " where "+ numeric_id_field +" = ?";

        List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(sql, new Object[] {itemToMatch});

        List<Long> results = new ArrayList<Long>();
        for (Map<String, Object> row : rowList) {
            Long id = (Long) row.get("input_record_id");
            results.add(id);
        }
        TimingLogger.stop("MarcAggregationServiceDAO.getMatchingRecords");
        return results;
    }

    public int getNumRecords(String tableName) {
        return this.jdbcTemplate.queryForInt("select count(*) from " + tableName);
    }

    public int getNumUniqueStringIds(String tableName) {
        return this.jdbcTemplate.queryForInt("select count(distinct string_id) from " + tableName);
    }

    public int getNumUniqueNumericIds(String tableName) {
        return this.jdbcTemplate.queryForInt("select count(distinct numeric_id) from " + tableName);
    }

    public int getNumUniqueRecordIds(String tableName) {
        return this.jdbcTemplate.queryForInt("select count(distinct input_record_id) from " + tableName);
    }

    /***
     * Generically, given a input_record_id, delete that row (or rows) from given table.
     * All the tables we will delete from with this method have 'input_record_id' column.
     * @param name
     */
    public void deleteMergeRow(String table, Long input_record_id) {
        this.jdbcTemplate.update(
                "delete from " + table + " where "+input_record_id_field+" = ? ", input_record_id);
    }

    /**
     * call this one if a record is deleted or perhaps updated
     * @param input_record_id
     */
    public void deleteAllMASRecordDetails(Long input_record_id) {

        TimingLogger.start("MarcAggregationServiceDAO.deleteAllMASRecordDetails");

        deleteAllMatchpointDetails(input_record_id);
        deleteAllMergeDetails(     input_record_id);

        TimingLogger.stop("MarcAggregationServiceDAO.deleteAllMASRecordDetails");
    }

    /**
     * call this one if a merge set has to be reformed because
     * a different member record was deleted or perhaps updated
     * @param input_record_id
     */
    public void deleteAllMergeDetails(Long input_record_id) {

        TimingLogger.start("MarcAggregationServiceDAO.deleteAllMergeDetails");

        deleteMergeRow(merge_scores_table,     input_record_id);
        deleteMergeRow(merged_records_table,   input_record_id);
        deleteMergeRow(bib_records_table,      input_record_id);

        TimingLogger.stop("MarcAggregationServiceDAO.deleteAllMergeDetails");
    }

    /**
     * call this one if a merge set has to be reformed because
     * a different member record was deleted or perhaps updated
     * don't want to delete the merge_scores because the record_id
     * still exists in the system, just want to break its bonds
     * to the old merge.
     *
     * @param input_record_id
     */
    public void deleteMergeMemberDetails(Long input_record_id) {

        TimingLogger.start("MarcAggregationServiceDAO.deleteMergeMemberDetails");

        deleteMergeRow(merged_records_table,   input_record_id);
        deleteMergeRow(bib_records_table,      input_record_id);

        TimingLogger.stop("MarcAggregationServiceDAO.deleteMergeMemberDetails");
    }

    /**
     * leave it private till we see if we need it on the outside.
     * @param input_record_id
     */
    private void deleteAllMatchpointDetails(Long input_record_id) {
        deleteMergeRow(matchpoints_010a_table, input_record_id);
        deleteMergeRow(matchpoints_020a_table, input_record_id);
        deleteMergeRow(matchpoints_022a_table, input_record_id);
        deleteMergeRow(matchpoints_024a_table, input_record_id);
        deleteMergeRow(matchpoints_035a_table, input_record_id);
    }



    /*
     * a convoluted query to find merged records (>1 input_record_id for a given output_record_id)
     * (there is probably a VERY simple query to do this!)
     *
    mysql -u root --password=root -D xc_marcaggregation -e
    'select a.* from merged_records as a left join merged_records as b using(output_record_id) where  a.input_record_id != b.input_record_id'

     * also, not 100% clear how fast this will be, so for now I am adding another table instead, that just contains the merged_input_id's (or should
     * it be merged output_id's?
     */

    /**
     * reserved, in case there is ability and need to do it
     */
    public void loadMaps(
        ) {
    }
}
