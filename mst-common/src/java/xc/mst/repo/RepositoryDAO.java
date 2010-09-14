/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.repo;

import gnu.trove.TLongHashSet;
import gnu.trove.TObjectLongHashMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordIfc;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;

public class RepositoryDAO extends BaseDAO {
	
	private static Logger LOG = Logger.getLogger(RepositoryDAO.class);
	
	protected final static String RECORDS_TABLE = "records";
	protected final static String RECORD_UPDATES_TABLE = "record_updates";
	protected final static String RECORDS_XML_TABLE = "records_xml";
	protected final static String RECORDS_SETS_TABLE = "record_sets";
	protected final static String RECORD_PREDECESSORS_TABLE = "record_predecessors";
	protected final static String RECORD_OAI_IDS = "record_oai_ids";
	protected final static String REPOS_TABLE = "repos";
	protected final static String RECORD_MESSAGES_TABLE = "RECORD_MESSAGES";
	
	protected Lock oaiIdLock = new ReentrantLock();
	protected int nextId = -1;
	protected int nextIdInDB = -1;
	
	protected SimpleJdbcCall getNextOaiId = null;
	
	protected final static String RECORDS_TABLE_COLUMNS = 
			"r.record_id, "+
			"r.oai_datestamp, "+
			"r.format_id, "+
			"r.status, ";
	
	protected final static String RECORD_MESSAGES_TABLE_COLUMNS = 
		"rm.record_message_id, "+
		"rm.rec_in_out, "+
		"rm.record_id, "+
		"rm.msg_code, "+
		"rm.msg_level, "+
		"rm.service_id, "+
		"rm.detail ";
	
	protected boolean inBatch = false;
	protected List<Record> recordsToAdd = null;
	
	public void init() {
		LOG.debug("RepositoryDAO.init()");
		try {
			if (!tableExists(REPOS_TABLE)) {
				for (String file : new String[] {"xc/mst/repo/sql/create_repo_platform.sql", 
							"xc/mst/repo/sql/create_oai_id_seq.sql"}) {
					executeServiceDBScripts(file);
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
	
	public Repository createRepository(Provider provider) {
		Repository r = (Repository)config.getBean("Repository");
		r.setProvider(provider);
		r.setName(provider.getName());
		createRepo(r);
		return r;
	}
	
	public void createRepository(Service service) {
		Repository r = (Repository)config.getBean("Repository");
		r.setService(service);
		r.setName(service.getName());
		createRepo(r);
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.getNextOaiId = new SimpleJdbcCall(jdbcTemplate).withFunctionName("get_next_oai_id");
	}
	
	public List<Repository> getAll() {
		List<Repository> repos = 
			this.jdbcTemplate.query(
				"select r.repo_name, p.provider_id, p.name "+
				"from repos r, providers p "+
				"where p.provider_id = r.provider_id", new RepoMapper());
		List<Repository> tempRepos =
			this.jdbcTemplate.query(
					"select r.repo_name, s.service_id, s.service_name "+
					"from repos r, services s "+
					"where s.service_id = r.service_id", new RepoMapper());
		if (tempRepos != null) {
			repos.addAll(tempRepos);
		}
		return repos;
	}
	
	public int getNumRecords(String name) {
		return this.jdbcTemplate.queryForInt("select count(*) from "+getTableName(name, RECORDS_TABLE));
	}
	
	public Date getLastModified(String name) {
		return (Date)this.jdbcTemplate.queryForObject("select max(date_updated) from "+getTableName(name, RECORD_UPDATES_TABLE), Date.class);
	}
	
	public Date getLastModifiedOai(String name) {
		return (Date)this.jdbcTemplate.queryForObject("select max(oai_datestamp) from "+getTableName(name, RECORDS_TABLE), Date.class);
	}
	
	public List<Record> getSuccessors(String name, long id) {
		String sql = 
			"select "+RECORDS_TABLE_COLUMNS+" x.xml "+
			"from "+getTableName(name, RECORDS_TABLE)+" r, "+
				getTableName(name, RECORDS_XML_TABLE)+" x, "+
				getTableName(name, RECORD_PREDECESSORS_TABLE)+" rp "+
			"where rp.pred_record_id = ? "+
				"and rp.record_id = r.record_id "+
				"and x.record_id = r.record_id";
		return this.jdbcTemplate.query(sql, new Object[] {id}, 
				new RecordMapper(new String[]{RECORDS_TABLE, RECORDS_XML_TABLE}, this));
	}
	
	public List<Long> getPredecessors(String name, long id) {
		String sql = 
			"select rp.pred_record_id "+
			"from "+getTableName(name, RECORD_PREDECESSORS_TABLE)+" rp "+
			"where rp.record_id = ? ";
		return this.jdbcTemplate.queryForList(sql, Long.class, id);
	}
	
	protected String getTableName(String repoName, String tableName) {
		return " "+getUtil().normalizeName(repoName)+"."+tableName;
	}
	
	public int getSize(String name) {
		return this.jdbcTemplate.queryForInt("select count(*) from "+getTableName(name, RECORDS_TABLE));
	}
	
	public void beginBatch() {
		inBatch = true;
	}
	
	public void endBatch(String name) {
		addRecords(name, null, true);
		inBatch = false;
	}
	
	public long resetIdSequence(long id) {
		oaiIdLock.lock();
		long retId = this.jdbcTemplate.queryForLong("select id from oai_id_sequence");
		this.jdbcTemplate.update("update oai_id_sequence set id=?", id);
		oaiIdLock.unlock();
		return retId;
	}
	
	public void injectId(Record r) {
		r.setId(getNextId());
	}
	
	public long getNextId() {
		oaiIdLock.lock();
		if (nextId == nextIdInDB) {
			int idsAtOnce = 1000;
			nextId = this.getNextOaiId.executeObject(Integer.class, idsAtOnce);
			nextIdInDB = nextId + idsAtOnce;
		}
		oaiIdLock.unlock();
		return this.nextId++;
	}
	
	public void addRecords(String name, List<Record> records) {
		addRecords(name, records, false);
	}
	
	public boolean addRecord(String name, Record r) {
		if (recordsToAdd == null) {
			recordsToAdd = new ArrayList<Record>();
		}

		recordsToAdd.add(r);
		return commitIfNecessary(name, false);
	}
	
	public void addRecords(String name, List<Record> records, boolean force) {
		if (inBatch) {
			LOG.debug("** inBatch True");
			if (recordsToAdd == null) {
				LOG.debug("** recordsToAdd == null");
				recordsToAdd = new ArrayList<Record>();
			}
			if (records != null) {
				LOG.debug("** records != null");
				recordsToAdd.addAll(records);
			}
			commitIfNecessary(name, force);
		} else {
			LOG.debug("** inBatch False");
			beginBatch();
			addRecords(name, records, force);
			endBatch(name);
		}
	}
	
	protected boolean commitIfNecessary(String name, boolean force) {
		LOG.debug("commitIfNecessary:Inbatch : " + inBatch);
		int batchSize = 5000;
		if (recordsToAdd != null) {
			//LOG.error("beluga highest id: "+recordsToAdd.get(recordsToAdd.size()-1).getId());
		}
		if (force || batchSize <= recordsToAdd.size()) {
			//LOG.error("beluga commit!!!");
			TimingLogger.start("commit to db");
			final long startTime = System.currentTimeMillis();
			String sql = 
    			"insert into "+getTableName(name, RECORDS_TABLE)+
    			" (record_id, oai_datestamp, status, format_id ) "+
    			"values (?,?,?,?) "+
    			"on duplicate key update "+
    				"status=?, "+
    				"format_id=?, "+
    				"oai_datestamp=? "+
    			";";
			TimingLogger.start("RECORDS_TABLE.insert");
	        int[] updateCounts = jdbcTemplate.batchUpdate(
	        		sql,
	                new BatchPreparedStatementSetter() {
	                    public void setValues(PreparedStatement ps, int j) throws SQLException {
	                    	int i=1;
	                    	Record r = recordsToAdd.get(j);
	                        ps.setLong(i++, r.getId());
	                        if (r.getOaiDatestamp() == null) {
	                        	ps.setTimestamp(i++, new Timestamp(startTime));	
	                        } else {
	                        	ps.setTimestamp(i++, new Timestamp(r.getOaiDatestamp().getTime()));
	                        }
	                        for (int k=0; k<2; k++) {
		                        ps.setString(i++, String.valueOf(r.getStatus()));
		                        if (r.getFormat() != null) {
		                        	ps.setInt(i++, r.getFormat().getId());
		                        } else { 
		                        	ps.setObject(i++, null);
		                        }
	                        }
	                        if (r.getOaiDatestamp() == null) {
	                        	ps.setTimestamp(i++, new Timestamp(startTime));	
	                        } else {
	                        	ps.setTimestamp(i++, new Timestamp(r.getOaiDatestamp().getTime()));
	                        }
	                    }

	                    public int getBatchSize() {
	                        return recordsToAdd.size();
	                    }
	                } );
	        TimingLogger.stop("RECORDS_TABLE.insert");
	        final long endTime = System.currentTimeMillis();
        	
	        final List<Record> recordXmls2Add = new ArrayList<Record>();
        	for (Record r : recordsToAdd) {
        		r.setMode(Record.STRING_MODE);
        		if (!Record.UNCHANGED.equals(r.getOaiXml())) {
            		recordXmls2Add.add(r);
            	}
        	}
        	
	        TimingLogger.start("RECORDS_XML_TABLE.insert");
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
	                    	Record r = recordXmls2Add.get(j);
	                    	r.setMode(Record.STRING_MODE);
	                        ps.setLong(i++, r.getId());
	                        ps.setString(i++, r.getOaiXml());
	                        ps.setString(i++, r.getOaiXml());
	                        if (r.getOaiXml() != null) {
	                        	TimingLogger.add("RECORDS_XML_LENGTH", r.getOaiXml().length());
	                        } else {
	                        	TimingLogger.add("RECORDS_XML_LENGTH", 0);
	                        }
	                    }

	                    public int getBatchSize() {
	                        return recordXmls2Add.size();
	                    }
	                } );
	        TimingLogger.stop("RECORDS_XML_TABLE.insert");
	        /*
	        TimingLogger.start("RECORDS_XML_TABLE.fs_insert");
	        try {
		        OutputStream os = new BufferedOutputStream(new FileOutputStream(
		        		MSTConfiguration.getUrlPath()+"/records/"+recordsToAdd.get(0).getId()+".xml"));
		        for (Record r : recordsToAdd) {
		        	r.setMode(Record.STRING_MODE);
		        	os.write(r.getOaiXml().getBytes("UTF-8"));
		        }
		        os.close();
	        } catch (Throwable t) {
	        	LOG.error("", t);
	        }
	        TimingLogger.stop("RECORDS_XML_TABLE.fs_insert");
	        */
	        TimingLogger.start("RECORDS_SETS_TABLE.insert");
			sql = 
    			"insert ignore into "+getTableName(name, RECORDS_SETS_TABLE)+
    			" (record_id, set_id) "+
    			"values (?,?) "+
    			";";
	        updateCounts = jdbcTemplate.batchUpdate(
	        		sql,
	                new BatchPreparedStatementSetter() {
	        			int recordSetInserts=0;
	                    public void setValues(PreparedStatement ps, int j) throws SQLException {
	                    	int k=0;
	                    	Record r = recordsToAdd.get(j);
	                    	if (r.getSets() != null && r.getSets().size() > 0) {
		                    	int totalSets = r.getSets().size();
		                    	for (Set s : r.getSets()) {
			                    	int i=1;
		                    		recordSetInserts++;
		                    		ps.setLong(i++, r.getId());
		                    		ps.setLong(i++, s.getId());
		                    		if (++k < totalSets) {
		                    			ps.addBatch();
		                    		}
		                    	}
	                    	} else {
	                    		ps.setObject(++k, null);
	                    		ps.setObject(++k, null);
	                    	}
	                    }
	                    public int getBatchSize() {
	                    	return recordsToAdd.size();
	                    }
	                } );
	        TimingLogger.stop("RECORDS_SETS_TABLE.insert");
	        /*
	        TimingLogger.start("RECORD_MESSAGES.insert");
			sql = 
    			"insert into "+getTableName(name, RECORD_MESSAGES_TABLE)+
    			" (record_id, rec_in_out, msg_code, msg_level, service_id, detail) "+
    			"values (?,?,?,?,?,?) "+
    			";";
	        List<Object[]> recordMsgs = new ArrayList<Object[]>();
	        for (Record r : recordsToAdd) {
	        	if (r.getMessages() != null) {
	        		LOG.debug("** r.getMessages():" + r.getMessages());
		        	for (RecordMessage m : r.getMessages()) {
		        		Object[] recMsgRow = new Object[6];
		        		recMsgRow[0] = r.getId();
		        		recMsgRow[1] = true;
		        		recMsgRow[2] = m.getMessageCode();
		        		recMsgRow[3] = m.getMessageLevel();
		        		recMsgRow[4] =  m.getServiceId();
		        		recMsgRow[5] = m.getDetailedMessage();
		        		recordMsgs.add(recMsgRow);
		        	}
	        	}
	        }
	        updateCounts = jdbcTemplate.batchUpdate(
	        		sql,
	                new RecMessageBatchPreparedStatementSetter(recordMsgs));
	        TimingLogger.stop("RECORD_MESSAGES.insert");
	        */
	        TimingLogger.start("RECORD_PREDECESSORS_TABLE.insert");
	        // TODO: Delete previous predecessors that are no longer there.
			sql = 
    			"insert ignore into "+getTableName(name, RECORD_PREDECESSORS_TABLE)+
    			" (record_id, pred_record_id) "+
    			"values (?,?) "+
    			";";

	        List<long[]> recordPreds = new ArrayList<long[]>();
	        for (Record r : recordsToAdd) {
	        	if (r.getPredecessors() != null) {
		        	for (RecordIfc p : r.getPredecessors()) {
		        		long[] recPredRow = new long[2];
		        		recPredRow[0] = r.getId();
		        		recPredRow[1] = p.getId();
		        		recordPreds.add(recPredRow);
		        	}
	        	}
	        }
	        updateCounts = jdbcTemplate.batchUpdate(
	        		sql,
	                new RecPredBatchPreparedStatementSetter(recordPreds));
	        TimingLogger.stop("RECORD_PREDECESSORS_TABLE.insert");
	        
			TimingLogger.start("RECORD_OAI_IDS.insert");
			sql = 
    			"insert ignore into "+getTableName(name, RECORD_OAI_IDS)+
    			" (record_id, oai_id) "+
    			"values (?,?) "+
    			";";
			updateCounts = this.jdbcTemplate.execute(sql, new PreparedStatementCallback<int[]>() {
				public int[] doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					for (Record r : recordsToAdd) {
						if (r.getHarvestedOaiIdentifier() != null) {
							ps.setLong(1, r.getId());
							ps.setString(2, r.getHarvestedOaiIdentifier());
							ps.addBatch();	
						}
					}
					return ps.executeBatch();
				}
			});
			TimingLogger.stop("RECORD_OAI_IDS.insert");
			
			// I slightly future dating the timestamp of the records so that a record will always
			// have been available from it's update_date forward.  If we don't do this, then it's 
			// possible for harvests to miss records.
			final long updateTime = System.currentTimeMillis() + (endTime - startTime) + 3000;
			TimingLogger.start("RECORD_UPDATES_TABLE.insert");
			sql = 
				"insert into "+getTableName(name, RECORD_UPDATES_TABLE)+
				" (record_id, date_updated) "+
				"values (?,?) "+
				";";
			updateCounts = jdbcTemplate.batchUpdate(
					sql,
	                new BatchPreparedStatementSetter() {
	                    public void setValues(PreparedStatement ps, int j) throws SQLException {
	                    	int i=1;
	                    	Record r = recordsToAdd.get(j);
	                        ps.setLong(i++, r.getId());
	                        ps.setTimestamp(i++, new Timestamp(updateTime));
	                    }

	                    public int getBatchSize() {
	                        return recordsToAdd.size();
	                    }
	                } );
	        TimingLogger.stop("RECORD_UPDATES_TABLE.insert");
	        LOG.debug(RECORD_UPDATES_TABLE+" committed: "+new Date());
	        LOG.debug("updateTime: "+new Date(updateTime));
	        
	        /*
	        Date updateDate = new Date(updateTime+1000);
	        try {
		        while (new Date().before(updateDate)) {
		        	Thread.sleep(500);
		        }
	        } catch (Throwable t) {
	        	LOG.error("", t);
	        }
	        */
	        
			recordsToAdd = null;
			TimingLogger.stop("commit to db");
			return true;
		} else {
			return false;
		}
	}
	
	public void populateHarvestCache(String name, TObjectLongHashMap harvestCache) {
		List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList("select record_id, oai_id from "+getTableName(name, RECORD_OAI_IDS));
		for (Map<String, Object> row : rowList) {
			Long recordId = (Long)row.get("record_id");
			String oaiId = (String)row.get("oai_id");
			oaiId = getUtil().getNonRedundantOaiId(oaiId);
			harvestCache.put(oaiId, recordId);
		}
	}
	
    private final static class RecPredBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    	protected List<long[]> recPreds = null;
		public RecPredBatchPreparedStatementSetter(List<long[]> recPreds) {
			this.recPreds = recPreds;
		}
        public void setValues(PreparedStatement ps, int j) throws SQLException {
        	int i=1;
        	//LOG.debug("this.recPreds.get("+j+")[0]: "+this.recPreds.get(j)[0]);
    		ps.setLong(i++, this.recPreds.get(j)[0]);
    		//LOG.debug("this.recPreds.get("+j+")[1]: "+this.recPreds.get(j)[1]);
    		ps.setLong(i++, this.recPreds.get(j)[1]);
        }
        public int getBatchSize() {
        	return this.recPreds.size();
        }
    }
    
    private final static class RecMessageBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    	protected List<Object[]> recMessages = null;
		public RecMessageBatchPreparedStatementSetter(List<Object[]> recMessages) {
			this.recMessages = recMessages;
		}
        public void setValues(PreparedStatement ps, int j) throws SQLException {
        	int i=1;
    		ps.setLong(i++, (Long)this.recMessages.get(j)[0]);
    		ps.setBoolean(i++, (Boolean)this.recMessages.get(j)[1]);
    		ps.setString(i++, (String)this.recMessages.get(j)[2]);
    		ps.setString(i++, (String)this.recMessages.get(j)[3]);
    		ps.setInt(i++, (Integer)this.recMessages.get(j)[4]);
    		ps.setString(i++, (String)this.recMessages.get(j)[5]);
        }
        public int getBatchSize() {
        	return this.recMessages.size();
        }
    }

	public boolean exists(String name) {
		try {
			this.jdbcTemplate.queryForInt("select count(*) from "+getTableName(name, RECORDS_TABLE));
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
	
	public void dropTables(String name) {
		for (String table : getTablesWithPrefix(getUtil().normalizeName(name))) {
			this.jdbcTemplate.execute("drop table "+table);
		}
	}
	
	protected void createRepo(Repository repo) {
		String name = repo.getName();
		createSchema(name);
		Integer serviceId = null;
		if (repo.getService() != null) {
			serviceId = repo.getService().getId();
		}
		Integer providerId = null;
		if (repo.getProvider() != null) {
			providerId = repo.getProvider().getId();
		}
		this.jdbcTemplate.update(
				"insert into "+REPOS_TABLE+" (repo_name, service_id, provider_id) "+
					"values (?, ?, ?) ",
				name, serviceId, providerId);
	}
	
	public void createTables(Repository repo) {
		runSql(repo, "xc/mst/repo/sql/create_repo.sql");
		if (repo.getProvider() != null) {
			runSql(repo, "xc/mst/repo/sql/create_harvest_repo.sql");	
		} else if (repo.getService() != null) {
			runSql(repo, "xc/mst/repo/sql/create_service_repo.sql");
		}
	}
	
	protected void runSql(Repository repo, String sqlFile) {
		String name = repo.getName();
		String createTablesContents = getUtil().slurp(sqlFile);
		createTablesContents = createTablesContents.replaceAll("REPO_NAME", getUtil().normalizeName(name));
		createTablesContents = createTablesContents.replaceAll("repo_name", getUtil().normalizeName(name));
		String[] tokens = createTablesContents.split(";");
		for (String sql : tokens) {
			if (StringUtils.isEmpty(StringUtils.trim(sql))) {
				continue;
			}
			sql = sql + ";";
			LOG.info(sql);
			this.jdbcTemplate.execute(sql);
		}
	}
	
	public Record getRecord(String name, long id) {
		String sql = 
			"select "+RECORDS_TABLE_COLUMNS+
				"x.xml, "+ " max(u.date_updated) as date_updated " +
			"from "+getTableName(name, RECORDS_TABLE)+" r, "+
				getTableName(name, RECORDS_XML_TABLE)+" x, "+
				getTableName(name, RECORD_UPDATES_TABLE)+" u "+
			"where r.record_id=? "+
				"and r.record_id = x.record_id " + 
				"and r.record_id = u.record_id " +
			"group by u.record_id";
		Record r = null;
		try {
			r = this.jdbcTemplate.queryForObject(sql, 
					new RecordMapper(new String[]{RECORDS_TABLE, RECORDS_XML_TABLE, RECORD_UPDATES_TABLE}, this),
					id);
		} catch (EmptyResultDataAccessException e) {
			LOG.info("record not found for id: "+id);
			LOG.info("sql: "+sql);
		}
		
		/*
		// If record not null then get error message information
		if (r != null) {
			String messageSql = 
				"select " + RECORD_MESSAGES_TABLE_COLUMNS +
				"from "+getTableName(name, RECORD_MESSAGES_TABLE)+" rm " +
				"where rm.record_id=? ";
	
			try {

				List<RecordMessage> messages = jdbcTemplate.query(messageSql,
						new Object[] {r.getId()},
						new RowMapper<RecordMessage>() {
							public RecordMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
								RecordMessage msg = new RecordMessage(rs.getInt(6), rs.getString(4), rs.getString(5), rs.getString(7));
								msg.setId(rs.getLong(1));
								ServicesService servicesService = (ServicesService) MSTConfiguration.getInstance().getBean("ServicesService");
								msg.setMessage(servicesService.getError(msg.getServiceId(), msg.getMessageCode()));
								return msg;
							}
				});
				r.setMessages(messages);
			} catch (EmptyResultDataAccessException e) {
				LOG.info("Messages not found for record id: "+id);
			}
		}
		*/
		return r;
	}
	
	public List<Record> getRecords(String name, Date from, Date until, Long startingId, Format inputFormat, Set inputSet) {
		long t0 = System.currentTimeMillis();
		List<Object> params = new ArrayList<Object>();
		if (until == null) {
			until = new Date();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(
				" select "+RECORDS_TABLE_COLUMNS+
				" x.xml, "+ " max(u.date_updated) as date_updated " +
				" from "+getTableName(name, RECORDS_TABLE)+" r, "+
					getTableName(name, RECORDS_XML_TABLE)+" x, " +
					getTableName(name, RECORD_UPDATES_TABLE)+" u ");
		if (inputSet != null) {
			sb.append(
				", "+getTableName(name, RECORDS_SETS_TABLE)+" rs ");
		}
		sb.append(
				" where r.record_id = x.record_id " +
					" and (r.record_id > ? or ? is null) "+
					" and r.record_id = u.record_id " +
					" and (u.date_updated > ? or ? is null) "+
					" and u.date_updated <= ?  "
					);
		/*
					+
			  		" and u.date_updated =" +
						" (select max(u.date_updated)" +
						" from " + getTableName(name, RECORD_UPDATES_TABLE)+" u " +
						" where u.record_id = r.record_id )" */
					
		if (inputFormat != null) {
			sb.append(
					" and r.format_id = ? ");
		}
		params.add(startingId);
		params.add(startingId);
		params.add(from);
		params.add(from);
		params.add(until);
		if (inputFormat != null) {
			params.add(inputFormat.getId());
		}
		
		if (inputSet != null) {
			sb.append(
					" and r.record_id = rs.record_id " +
					" and rs.set_id = ? ");
			params.add(inputSet.getId());
		}
		sb.append(
				" group by u.record_id "+
				" order by u.record_id "+
				" limit " + MSTConfiguration.getInstance().getPropertyAsInt(Constants.CONFIG_OAI_REPO_MAX_RECORDS, 1000));

		Object obj[] = params.toArray();
		
		List<Record> records = null;
		try {
			records = this.jdbcTemplate.query(sb.toString(), obj, 
					new RecordMapper(new String[]{RECORDS_TABLE, RECORDS_XML_TABLE, RECORD_UPDATES_TABLE}, this));
		} catch (EmptyResultDataAccessException e) {
			LOG.info("no records found for from: "+from+" until: "+until+" startingId: "+startingId + " format:" + inputFormat + " inputSet:" + inputSet);
		}
		LOG.debug("records.size(): "+records.size());
		if (records != null && records.size() > 0) {
			TimingLogger.add("GET_RECORDS_TIME", System.currentTimeMillis()-t0);
			TimingLogger.add("GET_RECORDS_NUM", records.size());
		}
		return records;
	}
	
	public List<Record> getRecordHeader(String name, Date from, Date until, Long startingId, Format inputFormat, Set inputSet) {
		List<Object> params = new ArrayList<Object>();
		if (until == null) {
			until = new Date();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(
				" select "+RECORDS_TABLE_COLUMNS+
				" max(u.date_updated) as u.date_updated  " +
				" from "+getTableName(name, RECORDS_TABLE)+" r, "+
					getTableName(name, RECORD_UPDATES_TABLE)+" u ");
		if (inputSet != null) {
			sb.append(
				", "+getTableName(name, RECORDS_SETS_TABLE)+" rs ");
		}
		sb.append(
				" where  (r.record_id > ? or ? is null) "+
					" and r.record_id = u.record_id " +
					" and (u.date_updated > ? or ? is null) "+
					" and u.date_updated <= ?  " +
					" group by u.record_id "
					);
					
		if (inputFormat != null) {
			sb.append(
					" and r.format_id = ? ");
		}
		params.add(startingId);
		params.add(startingId);
		params.add(from);
		params.add(from);
		params.add(until);
		if (inputFormat != null) {
			params.add(inputFormat.getId());
		}
		
		if (inputSet != null) {
			sb.append(
					" and r.record_id = rs.record_id " +
					" and rs.set_id = ? ");
			params.add(inputSet.getId());
		}
		sb.append(
				" order by u.record_id limit " + MSTConfiguration.getInstance().getPropertyAsInt(Constants.CONFIG_OAI_REPO_MAX_IDENTIFIERS, 1000));

		Object obj[] = params.toArray();
		
		List<Record> records = null;
		try {
			records = this.jdbcTemplate.query(sb.toString(), obj, 
					new RecordMapper(new String[]{RECORDS_TABLE, RECORD_UPDATES_TABLE}, this));
		} catch (EmptyResultDataAccessException e) {
			LOG.info("no records found for from: "+from+" until: "+until+" startingId: "+startingId + " format:" + inputFormat + " inputSet:" + inputSet);
		}
		LOG.debug("records.size(): "+records.size());
		return records;
	}
	
	public long getRecordCount(String name, Date from, Date until, Format inputFormat, Set inputSet) {
		List<Object> params = new ArrayList<Object>();
		if (until == null) {
			until = new Date();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(
				" select u.record_id " +
				" from "+getTableName(name, RECORDS_TABLE)+" r, "+
					getTableName(name, RECORD_UPDATES_TABLE)+" u ");
		if (inputSet != null) {
			sb.append(", "+getTableName(name, RECORDS_SETS_TABLE)+" rs ");
		}
		sb.append(
				" where  r.record_id = u.record_id " +
					" and (u.date_updated > ? or ? is null) "+
					" and u.date_updated <= ?  "
					);
		params.add(from);
		params.add(from);
		params.add(until);
		if (inputFormat != null) {
			sb.append(
					" and r.format_id = ? ");
			params.add(inputFormat.getId());
		}
		if (inputSet != null) {
			sb.append(
					" and r.record_id = rs.record_id " +
					" and rs.set_id = ? ");
			params.add(inputSet.getId());
		}
		sb.append(" group by u.record_id ");
		sb.append(" order by u.record_id ");

		Object obj[] = params.toArray();
		
		long recordCount = 0;
		try {
			LOG.debug("query for count: "+sb.toString());
			recordCount = this.jdbcTemplate.queryForList(sb.toString(), obj).size();
		} catch (EmptyResultDataAccessException e) {
			LOG.info("no records found for from: "+from+" until: "+until + " format:" + inputFormat + " inputSet:" + inputSet);
		}
		LOG.debug("records count: "+recordCount);
		return recordCount;
	}
	
	public List<Record> getRecordsWSets(String name, Date from, Date until, Long startingId) {
		List<Object> params = new ArrayList<Object>();
		if (until == null) {
			until = new Date();
		}
		List<Record> records = getRecords(name, from, until, startingId, null, null);
		if (records != null && records.size() > 0) {
			Long highestId = records.get(records.size()-1).getId();
			StringBuilder sb = new StringBuilder();
			sb.append(
					" select "+RECORDS_TABLE_COLUMNS+
						"s.set_id, "+
						"s.set_spec, "+
						"s.display_name "+
					" from "+getTableName(name, RECORDS_TABLE)+" r, "+
						getTableName(name, RECORD_UPDATES_TABLE)+" u, "+
						getTableName(name, RECORDS_SETS_TABLE)+" rs, "+
						" sets s"+
					" where r.record_id = u.record_id " +
						" and rs.record_id = r.record_id "+
						" and rs.set_id = s.set_id "+
						" and (r.record_id > ? or ? is null) "+
						" and r.record_id <= ? "+
						" and (u.date_updated > ? or ? is null) "+
						" and u.date_updated <= ? "+
						" group by u.record_id "+
						" order by u.record_id "+
						" limit " + MSTConfiguration.getInstance().getPropertyAsInt(Constants.CONFIG_OAI_REPO_MAX_RECORDS, 1000));
			LOG.debug("name: "+name+" startingId: "+startingId+" highestId: "+highestId+" from:"+from+" until:"+until);
			params.add(startingId);
			params.add(startingId);
			params.add(highestId);
			params.add(from);
			params.add(from);
			params.add(until);
			
			Object obj[] = params.toArray();
			
			List<Record> recordsWSets = null;
			try {
				recordsWSets = this.jdbcTemplate.query(sb.toString(), obj, 
						new RecordMapper(new String[]{RECORDS_TABLE, RECORDS_SETS_TABLE}, this));
				LOG.debug("recordsWSets.size() "+recordsWSets.size());
				
				StringBuilder sbMessages = new StringBuilder();
				sbMessages.append(
						" select "+RECORD_MESSAGES_TABLE_COLUMNS+
						" from "+getTableName(name, RECORD_MESSAGES_TABLE)+" rm, "+
							getTableName(name, RECORDS_TABLE)+" r "+
						" where r.record_id = rm.record_id " +
						" and r.record_id = ?");

				
				int recIdx = 0;
				Record currentRecord = records.get(recIdx);
				for (Record rws : recordsWSets) {
					while (currentRecord.getId() != rws.getId()) {
						currentRecord = records.get(++recIdx);
					}
					currentRecord.addSet(rws.getSets().get(0));
					
					/*
					List<RecordMessage> messages = jdbcTemplate.query(sbMessages.toString(),
							new Object[] {rws.getId()},
							new RowMapper() {
								public RecordMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
									RecordMessage msg = new RecordMessage(rs.getInt(6), rs.getString(4), rs.getString(5), rs.getString(7));
									msg.setId(rs.getLong(1));
									return msg;
								}
					});
					
					currentRecord.setMessages(messages);
					*/
					
			
				}
			} catch (EmptyResultDataAccessException e) {
				LOG.info("no recordsWSets found for from: "+from+" until: "+until+" startingId: "+startingId);
			}
			LOG.debug("records.size(): "+records.size());
		}

		return records;
	}
	
	public void populatePredecessors(String name, TLongHashSet predecessors) {
		List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList("select record_id, pred_record_id from "+getTableName(name, RECORD_PREDECESSORS_TABLE));
		for (Map<String, Object> row : rowList) {
			//Long succId = (Long)row.get("record_id");
			Long predId = (Long)row.get("pred_record_id");
			predecessors.add(predId);
		}
	}
	

	public java.util.Set<Long> getSuccessorIds(String name, Long predId) {
		java.util.Set<Long> succIds = new HashSet<Long>();
		List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
				"select record_id from "+getTableName(name, RECORD_PREDECESSORS_TABLE)+" where pred_record_id=?", predId);
		for (Map<String, Object> row : rowList) {
			Long succId = (Long)row.get("record_id");
			succIds.add(succId);
		}
		return succIds;
	}
	
	public void setAllLastModifiedOais(String name, Date d) {
		this.jdbcTemplate.update("update "+getTableName(name, RECORDS_TABLE)+ " set oai_datestamp=?", d);
	}
	
	public void deleteAllData(String name) {
		this.jdbcTemplate.update("delete from "+getTableName(name, RECORDS_TABLE));
		this.jdbcTemplate.update("delete from "+getTableName(name, RECORD_PREDECESSORS_TABLE));
		this.jdbcTemplate.update("delete from "+getTableName(name, RECORDS_SETS_TABLE));
		this.jdbcTemplate.update("delete from "+getTableName(name, RECORD_UPDATES_TABLE));
		this.jdbcTemplate.update("delete from "+getTableName(name, RECORDS_TABLE));
		this.jdbcTemplate.update("delete from "+getTableName(name, RECORDS_XML_TABLE));
	}
	
	private static final class RepoMapper implements RowMapper<Repository> {
	    public Repository mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	Repository r = new DefaultRepository();
	        r.setName(rs.getString("r.repo_name"));
	        try {
	        	Provider p = new Provider();
	        	p.setName(rs.getString("p.name"));
	        	p.setId(rs.getInt("p.provider_id"));
	        	r.setProvider(p);
	        } catch (SQLException t) {
	        	//LOG.debug("", t);
	        	Service s = new Service();
	        	s.setName(rs.getString("s.service_name"));
	        	s.setId(rs.getInt("s.service_id"));
	        	r.setService(s);
	        }
	        return r;
	    }        
	}
	
	private static final class RecordMapper implements RowMapper<Record> {
		protected List<String> tables = null;
		protected RepositoryDAO thisthis = null;
		
		public RecordMapper(String[] tables, RepositoryDAO thisthis) {
			this.tables = Arrays.asList(tables);
			this.thisthis = thisthis;
		}
	    public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
	        Record r = new Record();
	        if (tables.contains(RECORDS_TABLE)) {
	        	r.setId(rs.getLong("r.record_id"));
		        r.setCreatedAt(rs.getTimestamp("r.oai_datestamp"));
		        String status = rs.getString("r.status");
		        try {
			        Integer formatId = rs.getInt("r.format_id");
			        r.setFormat(thisthis.getFormatDAO().getById(formatId));
		        } catch (NullPointerException npe) {
		        	LOG.debug("no format for record: "+r.getId());
		        } catch (Throwable t) {
		        	LOG.debug("", t);
		        }
		        if (status != null && status.length() == 1) {
		        	r.setStatus(status.charAt(0));
		        }
	        }
	        if (tables.contains(RECORD_UPDATES_TABLE)) {
	        	r.setUpdatedAt(rs.getTimestamp("date_updated"));
	        }
	        if (tables.contains(RECORDS_XML_TABLE)) {
	        	r.setMode(Record.STRING_MODE);
		        r.setOaiXml(rs.getString("x.xml"));
	        }
	        
	        if (tables.contains(RECORDS_SETS_TABLE)) {
	        	r.setId(rs.getLong("r.record_id"));
	        	Set s = new Set();
	        	s.setId(rs.getInt("s.set_id"));
	        	s.setSetSpec(rs.getString("s.set_spec"));
	        	s.setDisplayName(rs.getString("s.display_name"));
	        	r.addSet(s);
	        }
	        if (tables.contains(RECORD_PREDECESSORS_TABLE)) {
	        }
	        return r;
	    }        
	}

}
