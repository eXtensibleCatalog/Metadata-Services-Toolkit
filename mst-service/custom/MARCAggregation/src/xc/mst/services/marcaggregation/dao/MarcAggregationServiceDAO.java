/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation.dao;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;

import xc.mst.services.impl.dao.GenericMetadataServiceDAO;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;

/**
*
* @author John Brand
*
*/
public class MarcAggregationServiceDAO extends GenericMetadataServiceDAO {

    private final static Logger LOG = Logger.getLogger(MarcAggregationServiceDAO.class);

    protected final static String bibsProcessedLongId_table = "bibsProcessedLongId";
    protected final static String bibsProcessedStringId_table = "bibsProcessedStringId";
    protected final static String bibsYet2ArriveLongId_table = "bibsYet2ArriveLongId";
    protected final static String bibsYet2ArriveStringId_table = "bibsYet2ArriveStringId";
    protected final static String held_holdings_table = "held_holdings";

    protected final static String matchpoints_010a_table = "matchpoints_010a";
    protected final static String matchpoints_020a_table = "matchpoints_020a";
    protected final static String matchpoints_022a_table = "matchpoints_022a";
    protected final static String matchpoints_024a_table = "matchpoints_024a";
    protected final static String matchpoints_028a_table = "matchpoints_028a";
    protected final static String matchpoints_035a_table = "matchpoints_035a";
    protected final static String matchpoints_130a_table = "matchpoints_130a";
    protected final static String matchpoints_240a_table = "matchpoints_240a";
    protected final static String matchpoints_245a_table = "matchpoints_245a";
    protected final static String matchpoints_260abc_table = "matchpoints_260abc";



    @SuppressWarnings("unchecked")
    //perhaps will move this up to the generic layer - since 2 services will end up with identical code.
    public void persistBibMaps(
        ) {
        TimingLogger.start("MarcAggregationServiceDAO.persistBibMaps");
        TimingLogger.stop("MarcAggregationServiceDAO.persistBibMaps");
    }

    @SuppressWarnings("unchecked")
    public void persistScnMaps(Map<Long, List<String[]>> inputId2scnMap) {

        TimingLogger.start("MarcAggregationServiceDAO.persistScnMaps");

        for (Object keyObj : inputId2scnMap.keySet()) {
            Long id = (Long) keyObj;
            Object list = inputId2scnMap.get(id);

            try {
                if (list == null) {
                    continue;
                }
                final byte[] idBytes = String.valueOf(id).getBytes();

                String dbLoadFileStr = (MSTConfiguration.getUrlPath() + "/db_load.in").replace('\\', '/');
                final byte[] tabBytes = "\t".getBytes();
                final byte[] newLineBytes = "\n".getBytes();

                File dbLoadFile = new File(dbLoadFileStr);
                if (dbLoadFile.exists()) {
                    dbLoadFile.delete();
                }
                final OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
                final MutableInt j = new MutableInt(0);
                final String tableName = matchpoints_035a_table;
                TimingLogger.start(tableName + ".insert");
                TimingLogger.start(tableName + ".insert.create_infile");

                List<String[]> strList = (List<String[]>) list;
                LOG.debug("insert: " + tableName + ".size(): " + strList.size());
                if (strList != null && strList.size() > 0) {
                    for (String[] _s: strList) {
                        try {   // need to loop through all strings associated with id!
                                if (j.intValue() > 0) {
                                    os.write(newLineBytes);
                                } else {
                                    j.increment();
                                }
                                os.write(_s[1].getBytes());
                                os.write(tabBytes);
                                os.write(_s[0].getBytes());
                                os.write(tabBytes);
                                os.write(idBytes);
                        } catch (Exception e) {
                            LOG.error("problem with data - ",e);
                        }
                    }
                }
                os.close();
                TimingLogger.stop(tableName + ".insert.create_infile");
                TimingLogger.start(tableName + ".insert.load_infile");
                this.jdbcTemplate.execute(
                        "load data infile '" + dbLoadFileStr + "' REPLACE into table " +
                                tableName +
                                " character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
                        );
                TimingLogger.stop(tableName + ".insert.load_infile");
                TimingLogger.stop(tableName + ".insert");
            } catch (Throwable t) {
                getUtil().throwIt(t);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void persistMatchpointMaps(
        ) {
        TimingLogger.start("MarcAggregationServiceDAO.persistMatchpointMaps");
        //this.simpleJdbcTemplate.
        TimingLogger.stop("MarcAggregationServiceDAO.persistMatchpointMaps");
    }

    protected List<Map<String, Object>> getMaps(String tableName, int page) {
//        TimingLogger.start("getMaps");
        int recordsAtOnce = 250000;
        List<Map<String, Object>> rowList =null;//= this.jdbcTemplate.queryForList(
//                "select org_code, bib_001, record_id from " + tableName +
//                        " limit " + (page * recordsAtOnce) + "," + recordsAtOnce);
//        TimingLogger.stop("getMaps");
        return rowList;
    }

    @SuppressWarnings("unchecked")
    public void loadMaps(
        ) {
    }
}
