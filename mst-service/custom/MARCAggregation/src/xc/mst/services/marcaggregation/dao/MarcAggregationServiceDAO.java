/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation.dao;

import GenericMetadataServiceDAO;

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
    public void persistMaps(
        TimingLogger.start("MarcAggregationServiceDAO.persistMaps");
        TimingLogger.stop("MarcAggregationServiceDAO.persistBibMaps");
    }

    protected List<Map<String, Object>> getMaps(String tableName, int page) {
        TimingLogger.start("getMaps");
        int recordsAtOnce = 250000;
        List<Map<String, Object>> rowList ;//= this.jdbcTemplate.queryForList(
//                "select org_code, bib_001, record_id from " + tableName +
//                        " limit " + (page * recordsAtOnce) + "," + recordsAtOnce);
        TimingLogger.stop("getMaps");
        return rowList;
    }

    @SuppressWarnings("unchecked")
    public void loadMaps(
    }
}
