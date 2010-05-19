/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.repo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import xc.mst.bo.record.Record;
import xc.mst.dao.BaseDAO;
import xc.mst.utils.MSTConfiguration;

public class RepositoryDAO extends BaseDAO {
	
	private static Logger LOG = Logger.getLogger(RepositoryDAO.class);
	
	protected Lock oaiIdLock = new ReentrantLock();
	protected int nextId = -1;
	protected int nextIdInDB = -1;
	
	protected SimpleJdbcCall getNextOaiId = null;
	
	protected final static String RECORDS_TABLE = "RECORDS";
	protected final static String RECORD_UPDATES_TABLE = "RECORD_UPDATES";
	protected final static String RECORDS_XML_TABLE = "RECORDS_XML";
	protected final static String RECORDS_SETS_TABLE = "RECORD_SETS";
	protected final static String RECORD_PREDECESSORS_TABLE = "RECORD_PREDECESSORS";
	
	protected boolean inBatch = false;
	protected List<Record> recordsToAdd = null;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.getNextOaiId = new SimpleJdbcCall(jdbcTemplate).withFunctionName("get_next_oai_id");
	}
	
	protected final static String[] ALL_TABLES = new String[] {
		RECORDS_TABLE, RECORD_UPDATES_TABLE, RECORDS_XML_TABLE, RECORDS_SETS_TABLE, RECORD_PREDECESSORS_TABLE
	};
	
	protected String getTableName(String repoName, String tableName) {
		return " "+repoName+"_"+tableName+" ";
	}
	
	public void beginBatch() {
		inBatch = true;
	}
	
	public void endBatch(String name) {
		addRecords(name, null, true);
		inBatch = false;
	}
	
	public void injectId(Record r) {
		oaiIdLock.lock();
		if (nextId == nextIdInDB) {
			int idsAtOnce = 1000;
			nextId = this.getNextOaiId.executeObject(Integer.class, idsAtOnce);
			nextIdInDB = nextId + idsAtOnce;
		}
		r.setId(this.nextId++);
		oaiIdLock.unlock();
	}
	
	public void addRecords(String name, List<Record> records) {
		addRecords(name, records, false);
	}
	
	public void addRecords(String name, List<Record> records, boolean force) {
		if (inBatch) {
			if (recordsToAdd == null) {
				recordsToAdd = new ArrayList<Record>();
			}
			int batchSize = 50000;
			if (MSTConfiguration.getProperty("batchSize") != null) {
				batchSize = Integer.parseInt(MSTConfiguration.getProperty("batchSize"));
			}
			if (records != null) {
				recordsToAdd.addAll(records);
			}
			if (force || batchSize >= recordsToAdd.size()) {
				final Date d = new Date();
				String sql = 
        			"insert into "+getTableName(name, RECORDS_TABLE)+
        			" (record_id, oai_pmh_id_1, oai_pmh_id_2, oai_pmh_id_3, oai_pmh_id_4,"+
        			"  date_created, status, format_id ) "+
        			"values (?,?,?,?,?,?,?,?) "+
        			"on duplicate key update "+
        				"status=?, "+
        				"format_id=? "+
        			";";
		        int[] updateCounts = jdbcTemplate.batchUpdate(
		        		sql,
		                new BatchPreparedStatementSetter() {
		                    public void setValues(PreparedStatement ps, int j) throws SQLException {
		                    	int i=1;
		                    	Record r = recordsToAdd.get(j);
		                        ps.setLong(i++, r.getId());
		                        ps.setString(i++, r.getOaiIds()[0]);
		                        ps.setString(i++, r.getOaiIds()[1]);
		                        ps.setString(i++, r.getOaiIds()[2]);
		                        ps.setString(i++, r.getOaiIds()[3]);
		                        ps.setTimestamp(i++, new Timestamp(d.getTime()));
		                        for (int k=0; k<2; k++) {
			                        ps.setString(i++, String.valueOf(r.getStatus()));
			                        if (r.getFormat() != null) {
			                        	ps.setInt(i++, r.getFormat().getId());
			                        } else { 
			                        	ps.setObject(i++, null);
			                        }
		                        }
		                    }
	
		                    public int getBatchSize() {
		                        return recordsToAdd.size();
		                    }
		                } );
				sql = 
        			"insert into "+getTableName(name, RECORDS_XML_TABLE)+
        			" (record_id, xml) "+
        			"values (?,?) "+
        			"on duplicate key update "+
        				"xml=? "+
        			";";
		        updateCounts = jdbcTemplate.batchUpdate(
		        		sql,
		                new BatchPreparedStatementSetter() {
		                    public void setValues(PreparedStatement ps, int j) throws SQLException {
		                    	int i=1;
		                    	Record r = recordsToAdd.get(j);
		                        ps.setLong(i++, r.getId());
		                        ps.setString(i++, r.getOaiXml());
		                        ps.setString(i++, r.getOaiXml());
		                    }
	
		                    public int getBatchSize() {
		                        return recordsToAdd.size();
		                    }
		                } );
		        recordsToAdd = null;
			}
		} else {
			beginBatch();
			addRecords(name, records);
			endBatch(name);
		}
	}

	public boolean exists(String name) {
		try {
			this.jdbcTemplate.queryForInt("select count(*) from "+getTableName(name, RECORDS_TABLE));
			return true;
		} catch (Throwable t) {
			LOG.error("", t);
			return false;
		}
	}
	
	public void dropTables(String name) {
		for (String table : ALL_TABLES) {
			try {
				String sql = "drop table "+getTableName(name, table);
				this.jdbcTemplate.execute(sql);
			} catch (Throwable t) {
				LOG.error("", t);
			}
		}
	}
	
	public void createOaiIdTable() {
		
	}
	
	public void createTables(String name) {
		String createTablesContents = getUtil().slurp("xc/mst/repo/sql/create_tables.sql");
		createTablesContents = createTablesContents.replaceAll("REPO_NAME", name);
		createTablesContents = createTablesContents.replaceAll("repo_name", name);
		String[] tokens = createTablesContents.split(";");
		for (String sql : tokens) {
			if (StringUtils.isEmpty(StringUtils.trim(sql))) {
				continue;
			}
			// oai ids
			sql = sql + ";";
			LOG.info(sql);
			this.jdbcTemplate.execute(sql);
		}
	}
	
	public Record getRecord(String name, long id) {
		String sql = 
			"select r.record_id, "+
				"r.oai_pmh_id_1, "+
				"r.oai_pmh_id_2, "+
				"r.oai_pmh_id_3, "+
				"r.oai_pmh_id_4, "+
				"r.date_created, "+
				"r.status, "+
				"x.xml "+
			"from "+getTableName(name, RECORDS_TABLE)+" r, "+
				getTableName(name, RECORDS_XML_TABLE)+" x "+
			"where r.record_id=? "+
				"and r.record_id = x.record_id";
		Record r = null;
		try {
			r = this.jdbcTemplate.queryForObject(sql, new RecordMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			LOG.info("record not found for id: "+id);
		}
		return r;
	}
	
	private static final class RecordMapper implements RowMapper<Record> {

	    public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
	        Record r = new Record();
	        r.setId(rs.getLong("r.record_id"));
	        String[] oaiIds = new String[4];
	        oaiIds[0] = rs.getString("r.oai_pmh_id_1");
	        oaiIds[1] = rs.getString("r.oai_pmh_id_2");
	        oaiIds[2] = rs.getString("r.oai_pmh_id_3");
	        oaiIds[3] = rs.getString("r.oai_pmh_id_4");
	        r.setOaiIdentifier(oaiIds);
	        r.setCreatedAt(rs.getTimestamp("r.date_created"));
	        String status = rs.getString("r.status");
	        if (status != null && status.length() == 1) {
	        	r.setStatus(status.charAt(0));
	        }
	        r.setOaiXml(rs.getString("x.xml"));
	        return r;
	    }        
	}

}
