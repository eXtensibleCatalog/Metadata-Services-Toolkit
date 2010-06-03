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
	
	protected final static String RECORDS_TABLE = "RECORDS";
	protected final static String RECORD_UPDATES_TABLE = "RECORD_UPDATES";
	protected final static String RECORDS_XML_TABLE = "RECORDS_XML";
	protected final static String RECORDS_SETS_TABLE = "RECORD_SETS";
	protected final static String RECORD_PREDECESSORS_TABLE = "RECORD_PREDECESSORS";
	protected final static String REPOS_TABLE = "REPOS";
	
	protected Lock oaiIdLock = new ReentrantLock();
	protected int nextId = -1;
	protected int nextIdInDB = -1;
	
	protected SimpleJdbcCall getNextOaiId = null;
	
	protected final static String RECORDS_TABLE_COLUMNS = 
			"r.record_id, \n"+
			"r.date_created, \n"+
			"r.status, \n";
	
	protected boolean inBatch = false;
	protected List<Record> recordsToAdd = null;
	
	public void init() {
		try {
			if (!tableExists(REPOS_TABLE)) {
				for (String file : new String[] {"xc/mst/repo/sql/create_repo_platform.sql", 
							"xc/mst/repo/sql/create_oai_id_seq.sql"}) {
					String createTablesContents = getUtil().slurp(file);
					String[] tokens = createTablesContents.split("\n\n");
					for (String sql : tokens) {
						if (StringUtils.isEmpty(StringUtils.trim(sql))) {
							continue;
						}
						jdbcTemplate.execute(sql);
					}
				}
			} else {
				// getversion and update if necessary
				// you should update all the tables here so that you can do it in a transaction 
			}
		} catch (Throwable t) {
			LOG.debug("", t);
			getUtil().throwIt(t);
		}
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.getNextOaiId = new SimpleJdbcCall(jdbcTemplate).withFunctionName("get_next_oai_id");
	}
	
	protected String getPrefix(String repoName) {
		return "repo_"+repoName;
	}
	
	protected String getTableName(String repoName, String tableName) {
		return " "+getPrefix(repoName)+"_"+tableName+" ";
	}
	
	public void beginBatch() {
		inBatch = true;
	}
	
	public void endBatch(String name) {
		addRecords(name, null, true);
		inBatch = false;
	}
	
	public long restIdSequence(long id) {
		oaiIdLock.lock();
		long retId = this.jdbcTemplate.queryForLong("select id from oai_id_sequence");
		this.jdbcTemplate.update("update oai_id_sequence set id=?", id);
		oaiIdLock.unlock();
		return retId;
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
        			" (record_id, date_created, status, format_id ) "+
        			"values (?,?,?,?) "+
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
		return tableExists(getTableName(name, RECORDS_TABLE));
	}
	
	public void dropTables(String name) {
		for (String table : getTablesWithPrefix(getPrefix(name))) {
			this.jdbcTemplate.execute("drop table "+table);
		}
	}
	
	public void createOaiIdTable() {
		
	}
	
	public void createRepo(String name) {
		this.jdbcTemplate.update(
				"insert into "+REPOS_TABLE+" (repo_name, service_id, provider_id) "+
					"values (?, ?, ?) ",
				name, null, null);
		String createTablesContents = getUtil().slurp("xc/mst/repo/sql/create_repo.sql");
		createTablesContents = createTablesContents.replaceAll("REPO_NAME", getPrefix(name));
		createTablesContents = createTablesContents.replaceAll("repo_name", getPrefix(name));
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
			"select "+RECORDS_TABLE_COLUMNS+
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
	
	public List<Record> getRecords(String name, Date from, Date until, Long startingId) {
		String sql = 
			"select "+RECORDS_TABLE_COLUMNS+
				"x.xml "+
			"from "+getTableName(name, RECORDS_TABLE)+" r, "+
				getTableName(name, RECORDS_XML_TABLE)+" x "+
			"where r.record_id = x.record_id "+
				"and r.date_created >= ? "+
				"and r.date_created < ? ";
		if (startingId != null) {
			sql += " and r.record_id > ? ";
		}
		sql += " order by r.record_id limit 3";
		List<Record> records = null;
		try {
			if (startingId != null) {
				records = this.jdbcTemplate.query(sql, new Object[]{from, until, startingId}, new RecordMapper());	
			} else {
				records = this.jdbcTemplate.query(sql, new Object[]{from, until}, new RecordMapper());
			}
		} catch (EmptyResultDataAccessException e) {
			LOG.info("no records found for from: "+from+" until: "+until+" startingId: "+startingId);
		}
		return records;
	}
	
	private static final class RecordMapper implements RowMapper<Record> {

	    public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
	        Record r = new Record();
	        r.setId(rs.getLong("r.record_id"));
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
