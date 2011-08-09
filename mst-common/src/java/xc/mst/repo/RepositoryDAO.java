/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.repo;

import gnu.trove.TLongArrayList;
import gnu.trove.TLongByteHashMap;
import gnu.trove.TLongHashSet;
import gnu.trove.TLongIterator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordCounts;
import xc.mst.bo.record.RecordIfc;
import xc.mst.bo.record.RecordMessage;
import xc.mst.bo.service.Service;
import xc.mst.cache.DynKeyLongMap;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.record.MessageDAO;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;

public class RepositoryDAO extends BaseDAO {

	private static Logger LOG = Logger.getLogger(RepositoryDAO.class);

	public final static String RECORDS_TABLE = "records";
	public final static String RECORD_UPDATES_TABLE = "record_updates";
	public final static String RECORDS_XML_TABLE = "records_xml";
	public final static String RECORDS_SETS_TABLE = "record_sets";
	public final static String RECORD_PREDECESSORS_TABLE = "record_predecessors";
	public final static String RECORD_OAI_IDS = "record_oai_ids";
	public final static String REPOS_TABLE = "repos";
	public final static String RECORD_LINKS_TABLE = "record_links";
	public final static String PROPERTIES = "properties";
	public final static String PREV_INCOMING_RECORD_STATUSES = "prev_incoming_record_statuses";
	public int lastCompleteListSizeMethod = 0;

	protected Lock oaiIdLock = new ReentrantLock();
	protected int nextId = -1;
	protected int nextIdInDB = -1;

	protected SimpleJdbcCall getNextOaiId = null;

	protected final static String RECORDS_TABLE_COLUMNS =
			"r.record_id, "+
			"r.oai_datestamp, "+
			"r.format_id, "+
			"r.status, "+
			"r.type, "+
			"r.prev_status ";

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
				for (String file : new String[] {"sql/create_repo_platform.sql",
							"sql/create_oai_id_seq.sql"}) {
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
				"select r.repo_name, p.provider_id, p.name, p.oai_provider_url "+
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
			"select "+RECORDS_TABLE_COLUMNS+", x.xml "+
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
		return " "+getUtil().getDBSchema(repoName)+"."+tableName;
	}

	public int getSize(String name) {
		return this.jdbcTemplate.queryForInt("select count(*) from "+getTableName(name, RECORDS_TABLE));
	}

	public long resetIdSequence(long id) {
		this.nextId = -1;
		this.nextIdInDB = -1;
		oaiIdLock.lock();
		long retId = this.jdbcTemplate.queryForLong("select id from oai_id_sequence");
		this.jdbcTemplate.update("update oai_id_sequence set id=?", id);
		oaiIdLock.unlock();
		return retId;
	}

	public void injectId(Record r) {
		r.setId(getNextIdAndIncr());
	}

	public long getNextIdAndIncr() {
		oaiIdLock.lock();
		if (nextId == nextIdInDB) {
			int idsAtOnce = 1000;
			nextId = this.getNextOaiId.executeObject(Integer.class, idsAtOnce);
			nextIdInDB = nextId + idsAtOnce;
		}
		long id = this.nextId;
		this.nextId++;
		oaiIdLock.unlock();
		return id;
	}

	public long getNextId() {
		return this.nextId+1;
	}

	public void addRecords(String name, List<Record> records) {
		addRecords(name, records, false);
	}

	public void addRecord(String name, Record r) {
		if (recordsToAdd == null) {
			recordsToAdd = new ArrayList<Record>();
		}
		recordsToAdd.add(r);
	}

	public void addRecords(String name, List<Record> records, boolean force) {
		if (recordsToAdd == null) {
			LOG.debug("** recordsToAdd == null");
			recordsToAdd = new ArrayList<Record>();
		}
		if (records != null) {
			LOG.debug("** records != null");
			recordsToAdd.addAll(records);
		}
	}

	protected boolean commitIfNecessary(String name, boolean force, long processedRecordsCount) {
		//LOG.debug("commitIfNecessary:Inbatch : " + inBatch);
		int batchSize = MSTConfiguration.getInstance().getPropertyAsInt("db.insertsAtOnce", 10000);
		if (recordsToAdd != null) {
			//LOG.error("beluga highest id: "+recordsToAdd.get(recordsToAdd.size()-1).getId());
		}
		if (recordsToAdd != null && (force || batchSize <= recordsToAdd.size())) {
			//LOG.error("beluga commit!!!");
			TimingLogger.start("commit to db");
			final long startTime = System.currentTimeMillis();
			if (ready4harvest(name)) {
				String sql =
					"insert into "+getTableName(name, RECORDS_TABLE)+
					" (record_id, oai_datestamp, type, status, prev_status, format_id ) "+
					"values (?,?,?,?,?,?) "+
					"on duplicate key update "+
						"type=?, "+
						"status=?, "+
						"prev_status=?, "+
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
									if (r.getType() != null && r.getType().length() > 0) {
										ps.setString(i++, ""+r.getType().charAt(0));
									} else {
										ps.setString(i++, null);
									}
									ps.setString(i++, String.valueOf(r.getStatus()));
									ps.setString(i++, String.valueOf(r.getPreviousStatus()));
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
								if (r.getUpdatedAt() == null) {
									ps.setTimestamp(i++, new Timestamp(updateTime));
								} else {
									ps.setTimestamp(i++, new Timestamp(r.getUpdatedAt().getTime()));
								}
							}

							public int getBatchSize() {
								return recordsToAdd.size();
							}
						} );
				TimingLogger.stop("RECORD_UPDATES_TABLE.insert");
				LOG.debug(RECORD_UPDATES_TABLE+" committed: "+new Date());
				LOG.debug("updateTime: "+new Date(updateTime));

				LOG.debug("processedRecordsCount: "+processedRecordsCount);
				LOG.debug("db.numInserts2dropIndexes: "+MSTConfiguration.getInstance().getPropertyAsInt("db.numInserts2dropIndexes", 0));
				if (processedRecordsCount > MSTConfiguration.getInstance().getPropertyAsInt("db.numInserts2dropIndexes", 0)) {
					dropIndicies(name);
				}
			} else {
				try {
					LOG.debug("recordsToAdd.size(): "+recordsToAdd.size());
					String dbLoadFileStr = (MSTConfiguration.getUrlPath()+"/db_load.in").replace('\\', '/');
					LOG.debug("dbLoadFileStr: "+dbLoadFileStr);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					byte[] startTimeBytes = sdf.format(new Date(startTime)).getBytes();
					byte[] tabBytes = "\t".getBytes();
					byte[] newLineBytes = "\n".getBytes();
					byte[] nullBytes = "\u0000\n".getBytes();
					byte[] bellBytes = "\u0000\t".getBytes();

					File dbLoadFile = new File(dbLoadFileStr);
					if (dbLoadFile.exists()) {
						dbLoadFile.delete();
					}
					OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
					int i=0;
					TimingLogger.start("RECORDS_TABLE.insert");
					TimingLogger.start("RECORDS_TABLE.insert.create_infile");
					for (Record r : recordsToAdd) {
						if (i++ > 0) {
							os.write(newLineBytes);
						}
						os.write(String.valueOf(r.getId()).getBytes());
						os.write(tabBytes);
						if (r.getOaiDatestamp() == null) {
							os.write(startTimeBytes);
						} else {
							os.write(sdf.format(r.getOaiDatestamp()).getBytes());
						}
						os.write(tabBytes);
						if (r.getType() != null && r.getType().length() > 0) {
							os.write(r.getType().substring(0,1).getBytes());
						} else {
							os.write("\\N".getBytes());
						}
						os.write(tabBytes);
						os.write(String.valueOf(r.getStatus()).getBytes());
						os.write(tabBytes);
						os.write(String.valueOf(r.getPreviousStatus()).getBytes());
						os.write(tabBytes);
						if (r.getFormat() != null)
							os.write(String.valueOf(r.getFormat().getId()).getBytes());
					}
					os.close();
					TimingLogger.stop("RECORDS_TABLE.insert.create_infile");
					TimingLogger.start("RECORDS_TABLE.insert.load_infile");
					this.jdbcTemplate.execute(
							"load data infile '"+dbLoadFileStr+"' REPLACE into table "+
							getTableName(name, RECORDS_TABLE)+
							" character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
							);
					TimingLogger.stop("RECORDS_TABLE.insert.load_infile");
					TimingLogger.stop("RECORDS_TABLE.insert");
					final long endTime = System.currentTimeMillis();

					final List<Record> recordXmls2Add = new ArrayList<Record>();
					for (Record r : recordsToAdd) {
						r.setMode(Record.STRING_MODE);
						if (!Record.UNCHANGED.equals(r.getOaiXml())) {
							recordXmls2Add.add(r);
						}
					}

					if (dbLoadFile.exists()) {
						dbLoadFile.delete();
					}
					os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
					i=0;
					TimingLogger.start("RECORDS_XML_TABLE.insert");
					TimingLogger.start("RECORDS_XML_TABLE.insert.create_infile");
					for (Record r : recordsToAdd) {
						if (i++ > 0) {
							os.write(nullBytes);
						}
						os.write(String.valueOf(r.getId()).getBytes());
						os.write(bellBytes);
						r.setMode(Record.STRING_MODE);
						if (r.getOaiXml() != null)
							os.write(String.valueOf(r.getOaiXml()).getBytes("UTF-8"));
					}
					os.close();
					TimingLogger.stop("RECORDS_XML_TABLE.insert.create_infile");
					TimingLogger.start("RECORDS_XML_TABLE.insert.load_infile");
					this.jdbcTemplate.execute(
							"load data infile '"+dbLoadFileStr+"' REPLACE into table "+
							getTableName(name, RECORDS_XML_TABLE)+
							" character set utf8 fields terminated by '\\0\\t' escaped by '' lines terminated by '\\0\\n'"
							);
					TimingLogger.stop("RECORDS_XML_TABLE.insert.load_infile");
					TimingLogger.stop("RECORDS_XML_TABLE.insert");

					if (dbLoadFile.exists()) {
						dbLoadFile.delete();
					}
					os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
					i=0;
					TimingLogger.start("RECORDS_SETS_TABLE.insert");
					TimingLogger.start("RECORDS_SETS_TABLE.insert.create_infile");
					for (Record r : recordsToAdd) {
						if (r.getSets() != null) {
							for (Set s : r.getSets()) {
								if (i++ > 0) {
									os.write(newLineBytes);
								}
								os.write(String.valueOf(r.getId()).getBytes());
								os.write(tabBytes);
								os.write(String.valueOf(s.getId()).getBytes());
							}
						}
					}
					os.close();
					TimingLogger.stop("RECORDS_SETS_TABLE.insert.create_infile");
					TimingLogger.start("RECORDS_SETS_TABLE.insert.load_infile");
					this.jdbcTemplate.execute(
							"load data infile '"+dbLoadFileStr+"' REPLACE into table "+
							getTableName(name, RECORDS_SETS_TABLE)+
							" character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
							);
					TimingLogger.stop("RECORDS_SETS_TABLE.insert.load_infile");
					TimingLogger.stop("RECORDS_SETS_TABLE.insert");

					if (dbLoadFile.exists()) {
						dbLoadFile.delete();
					}
					os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
					i=0;
					TimingLogger.start("RECORD_PREDECESSORS_TABLE.insert");
					TimingLogger.start("RECORD_PREDECESSORS_TABLE.insert.create_infile");
					for (Record r : recordsToAdd) {
						if (r.getPredecessors() != null) {
							for (RecordIfc p : r.getPredecessors()) {
								if (i++ > 0) {
									os.write(newLineBytes);
								}
								os.write(String.valueOf(r.getId()).getBytes());
								os.write(tabBytes);
								os.write(String.valueOf(p.getId()).getBytes());
							}
						}
					}
					os.close();
					TimingLogger.stop("RECORD_PREDECESSORS_TABLE.insert.create_infile");
					TimingLogger.start("RECORDS_SETS_TABLE.insert.load_infile");
					this.jdbcTemplate.execute(
							"load data infile '"+dbLoadFileStr+"' REPLACE into table "+
							getTableName(name, RECORD_PREDECESSORS_TABLE)+
							" character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
							);
					TimingLogger.stop("RECORDS_SETS_TABLE.insert.load_infile");
					TimingLogger.stop("RECORD_PREDECESSORS_TABLE.insert");

					if (dbLoadFile.exists()) {
						dbLoadFile.delete();
					}
					boolean atLeastOne = false;
					os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
					i=0;
					TimingLogger.start("RECORD_OAI_IDS.insert");
					TimingLogger.start("RECORD_OAI_IDS.insert.create_infile");
					for (Record r : recordsToAdd) {
						if (r.getHarvestedOaiIdentifier() != null) {
							atLeastOne = true;
							if (i++ > 0) {
								os.write(newLineBytes);
							}
							os.write(String.valueOf(r.getId()).getBytes());
							os.write(tabBytes);
							os.write(String.valueOf(r.getHarvestedOaiIdentifier()).getBytes("UTF-8"));
						}
					}
					os.close();
					TimingLogger.stop("RECORD_OAI_IDS.insert.create_infile");
					TimingLogger.start("RECORDS_OAI_IDS.insert.load_infile");
					if (atLeastOne) {
						this.jdbcTemplate.execute(
								"load data infile '"+dbLoadFileStr+"' REPLACE into table "+
								getTableName(name, RECORD_OAI_IDS)+
								" character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
								);
					}
					TimingLogger.stop("RECORDS_OAI_IDS.insert.load_infile");
					TimingLogger.stop("RECORD_OAI_IDS.insert");

					if (dbLoadFile.exists()) {
						dbLoadFile.delete();
					}
					os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
					i=0;
					TimingLogger.start("RECORD_UPDATES_TABLE.insert");
					TimingLogger.start("RECORD_UPDATES_TABLE.insert.create_infile");
					// I'm slightly future dating the timestamp of the records so that a record will always
					// have been available from it's update_date forward.  If we don't do this, then it's
					// possible for harvests to miss records.
					final long updateTime = System.currentTimeMillis() + (endTime - startTime) + 3000;
					byte[] updateTimeBytes = sdf.format(updateTime).getBytes();
					for (Record r : recordsToAdd) {
						if (i++ > 0) {
							os.write(newLineBytes);
						}
						os.write(String.valueOf(r.getId()).getBytes());
						os.write(tabBytes);
						if (r.getUpdatedAt() == null) {
							os.write(updateTimeBytes);
						} else {
							os.write(sdf.format(r.getUpdatedAt()).getBytes());
						}
					}
					os.close();
					TimingLogger.stop("RECORD_UPDATES_TABLE.insert.create_infile");
					TimingLogger.start("RECORDS_UPDATES_TABLE.insert.load_infile");
					this.jdbcTemplate.execute(
							"load data infile '"+dbLoadFileStr+"' into table "+
							getTableName(name, RECORD_UPDATES_TABLE)+
							" character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
							);
					TimingLogger.stop("RECORDS_UPDATES_TABLE.insert.load_infile");
					TimingLogger.stop("RECORD_UPDATES_TABLE.insert");

				} catch (Throwable t) {
					getUtil().throwIt(t);
				}
			}

			recordsToAdd = null;
			TimingLogger.stop("commit to db");
			if (force) {
				inBatch = false;
			}
			return true;
		} else {
			return force;
		}
	}

	protected List<Map<String, Object>> getHarvestCache(String name, int page) {
		TimingLogger.start("getHarvestCache");
		int recordsAtOnce = 100000;
		String sql = "select record_id, oai_id from "+getTableName(name, RECORD_OAI_IDS)+
			" limit "+(page*recordsAtOnce)+","+recordsAtOnce;
		LOG.info(sql);
		List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(sql);
		TimingLogger.stop("getHarvestCache");
		return rowList;
	}

	public void populateHarvestCache(String name, DynKeyLongMap harvestCache) {
		TimingLogger.start("populateHarvestCache");
		int page = 0;
		List<Map<String, Object>> rowList = getHarvestCache(name, page);
		while (rowList != null && rowList.size() > 0) {
			for (Map<String, Object> row : rowList) {
				Long recordId = (Long)row.get("record_id");
				String oaiId = (String)row.get("oai_id");
				oaiId = getUtil().getNonRedundantOaiId(oaiId);
				harvestCache.put(oaiId, recordId);
			}
			rowList = getHarvestCache(name, ++page);
		}
		TimingLogger.stop("populateHarvestCache");
	}

	protected List<Map<String, Object>> getPreviousStatuses(String name, int page, boolean service) {
		String tableName = null;
		if (service) {
			tableName = PREV_INCOMING_RECORD_STATUSES;
		} else {
			tableName = RECORDS_TABLE;
		}
		TimingLogger.start("getPreviousStatuses");
		int recordsAtOnce = 100000;
		String sql = "select record_id, status from "+getTableName(name, tableName)+
			" limit "+(page*recordsAtOnce)+","+recordsAtOnce;
		LOG.info(sql);
		List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(sql);
		TimingLogger.stop("getPreviousStatuses");
		return rowList;
	}

	public void populatePreviousStatuses(String name, TLongByteHashMap previousStatuses, boolean service) {
		TimingLogger.start("populatePreviousStatuses");
		int page = 0;
		List<Map<String, Object>> rowList = getPreviousStatuses(name, page, service);
		while (rowList != null && rowList.size() > 0) {
			for (Map<String, Object> row : rowList) {
				char prevStatus = ((String)row.get("status")).charAt(0);
				previousStatuses.put(getUtil().getLongPrim(row.get("record_id")), (byte)prevStatus);
			}
			rowList = getPreviousStatuses(name, ++page, service);
		}
		TimingLogger.stop("populatePreviousStatuses");
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

	// first check that the table exists
	public boolean hasOnlyRecordsOfStatus(String name, char status) {
		try {
			List<Map<String, Object>> rows =
				this.jdbcTemplate.queryForList("select count(*), status from "+getTableName(name, RECORDS_TABLE)+" group by status");

			for (Map<String, Object> row : rows) {
				char stat = ((String)row.get("status")).charAt(0);
				if (stat != status) {
					LOG.debug("**** looking to see if have record type: "+status+ "FALSE now looking at: "+stat);
					return false;
				}
				else {
					LOG.debug("**** looking to see if have record type: "+status+ "TRUE now looking at: "+stat);
				}
			}
			return true;
		}
		catch (Throwable t) {
			LOG.error("unexpected exception in RepositoryDAO.hasOnlyRecordsOfStatus");
			return true;
		}
	}

	protected void createRepo(Repository repo) {
		String name = repo.getName();
		try {
			deleteSchema(name);
		} catch (Throwable t) {
			// do nothing
		}
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

	public void deleteRepo(String name) {
		this.jdbcTemplate.update(
				"delete from "+REPOS_TABLE+" where repo_name = ? ", getUtil().getDBSchema(name));
		deleteSchema(getUtil().getDBSchema(name));
	}

	public void createTables(Repository repo) {
		runSql(repo, "sql/create_repo.sql");
		if (repo.getProvider() != null) {
			runSql(repo, "sql/create_harvest_repo.sql");
		} else if (repo.getService() != null) {
			runSql(repo, "sql/create_service_repo.sql");
		}
	}

	protected void runSql(Repository repo, String sqlFile) {
		String name = repo.getName();
		String createTablesContents = getUtil().slurp(sqlFile);
		createTablesContents = createTablesContents.replaceAll("REPO_NAME", getUtil().getDBSchema(name));
		createTablesContents = createTablesContents.replaceAll("repo_name", getUtil().getDBSchema(name));
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
				", x.xml, "+ " max(u.date_updated) as date_updated " +
			"from "+getTableName(name, RECORDS_TABLE)+" r, "+
				getTableName(name, RECORDS_XML_TABLE)+" x, "+
				getTableName(name, RECORD_UPDATES_TABLE)+" u "+
			"where r.record_id=? "+
				"and r.record_id = x.record_id " +
				"and r.record_id = u.record_id " +
			"group by r.record_id";
		Record r = null;
		try {
			r = this.jdbcTemplate.queryForObject(sql,
					new RecordMapper(new String[]{RECORDS_TABLE, RECORDS_XML_TABLE, RECORD_UPDATES_TABLE}, this),
					id);
		} catch (EmptyResultDataAccessException e) {
			//LOG.info("record not found for id: "+id);
			//LOG.info("sql: "+sql);
		}
		return r;
	}

	public void injectHarvestInfo(String name, Record r) {
		String sql = "select oai_id from "+getTableName(name, RECORD_OAI_IDS)+" where record_id = ?";
		r.setHarvestedOaiIdentifier(this.jdbcTemplate.queryForObject(sql, String.class, (Long)r.getId()));
	}

	private void addStatusesInClause(StringBuilder sb, char[] statuses) {
		if (statuses != null) {
			if (statuses.length == 1) {
				sb.append(" where status = '")
					.append(statuses[0])
					.append("' ");
			} else {
				sb.append(" where status in (");
				for (char status : statuses) {
					sb.append("'");
					sb.append(status);
					sb.append("'");
					sb.append(',');
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append(")");
			}
		}
	}

	protected void addStatusesInWhereClause(StringBuilder sb, char[] statuses) {
		if (statuses != null) {
			if (statuses.length == 1) {
				sb.append(" and status = '")
					.append(statuses[0])
					.append("' ");
			} else {
				sb.append(" and status in (");
				for (char status : statuses) {
					sb.append("'");
					sb.append(status);
					sb.append("'");
					sb.append(',');
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append(")");
			}
		}
	}

	/**
	 * check if row returned, if so have records with given status
	 * @param name repo_name
	 * @param format_id
	 * @param set_id - allowed to be null
	 * @param statuses - the record statuses we are looking for
	 * @return false if no rows with statuses, return true if found a row with the status
	 */
	public boolean hasRecordsOfStatus(String name, int format_id, Integer set_id, char[] statuses) {
		StringBuilder sb = new StringBuilder("select 1");

		/*
		select 1
		from records r
		where status in ('A','H')
                and r.format_id = 1
		limit 1
		*/
		if (set_id == null) {
			sb.append(" from ").append(getTableName(name, RECORDS_TABLE)).append(" r ");
			addStatusesInClause(sb, statuses);
			sb.append(" and r.format_id = ").append(format_id);
			sb.append(" limit 1");
		}

		// if set_id != null, run this and see if a row is returned:
		/*
		select 1
		from records r,
		                record_sets rs
		where status in ('A','H')
		                and r.format_id = 1
		                and rs.record_id = r.record_id
		                and rs.set_id = 3
		limit 1
		*/
		else {
			sb.append(" from ").append(getTableName(name, RECORDS_TABLE)).append(" r , ").append(getTableName(name, RECORDS_SETS_TABLE)).append(" rs ");
			addStatusesInClause(sb, statuses);
			sb.append(" and r.format_id = ").append(format_id);
			sb.append(" and rs.record_id = r.record_id ");
			sb.append(" and rs.set_id = ").append(set_id.intValue());
			sb.append(" limit 1");
		}

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(sb.toString());
		return rows.size() > 0;
	}

	public List<Record> getRecords(String name, Date from, Date until,
			Long startingId, Format inputFormat, Set inputSet) {
		return getRecords(name, from, until, startingId, inputFormat, inputSet, new char[] {Record.ACTIVE, Record.DELETED});
	}
	@SuppressWarnings("unchecked")
	public List<Record> getRecords(String name, Date from, Date until,
			Long startingId, Format inputFormat, Set inputSet, char[] statuses) {
		long t0 = System.currentTimeMillis();
		List<Object> params = new ArrayList<Object>();
		if (until == null) {
			until = new Date();
		}
		if (startingId == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("select straight_join 1 ")
				.append(" from " ).append(getTableName(name, RECORD_UPDATES_TABLE)).append(" u force index (idx_record_updates_date_updated) , ")
				.append(getTableName(name, RECORDS_TABLE)).append(" r ")
				.append("where r.record_id = u.record_id  and (u.date_updated >= ? or ? is null)  and u.date_updated <= ? ");
			addStatusesInWhereClause(sb, statuses);
			sb.append(" limit 1");
			List atleastone = this.jdbcTemplate.queryForList(sb.toString(), from, from, until);
			if (atleastone == null || atleastone.size() == 0) {
				return new ArrayList<Record>();
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(
				" select straight_join "+RECORDS_TABLE_COLUMNS+
					" , x.xml, "+ " max(u.date_updated) as date_updated " +
				" from ");
		sb.append(getTableName(name, RECORD_UPDATES_TABLE)+" u force index (idx_record_updates_record_id)");
		sb.append(", ");
		sb.append(getTableName(name, RECORDS_TABLE)+" r ");
		if (inputFormat != null) {
			sb.append("IGNORE index (idx_records_format_id) " );
		}
		sb.append(", ");
		sb.append(getTableName(name, RECORDS_XML_TABLE)+" x ");

		if (inputSet != null) {
			sb.append(
				", "+getTableName(name, RECORDS_SETS_TABLE)+" rs ignore index (idx_"+RECORDS_SETS_TABLE+"_set_id) ");
		}
		sb.append(
				" where r.record_id = x.record_id " +
					" and (r.record_id > ? or ? is null) "+
					" and r.record_id = u.record_id " +
					" and (u.date_updated >= ? or ? is null) "+
					" and u.date_updated <= ?  "
					);
		addStatusesInWhereClause(sb, statuses);
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
				" limit " + MSTConfiguration.getInstance().getPropertyAsInt(Constants.CONFIG_OAI_REPO_MAX_RECORDS, 5000));

		Object obj[] = params.toArray();

		List<Record> records = null;
		try {
			/*
			LOG.error(sb.toString());
			LOG.error("startingId: "+startingId);
			LOG.error("from: "+from);
			LOG.error("until: "+until);
			LOG.error("inputSet: "+inputSet);
			LOG.error("inputFormat: "+inputFormat);
			*/
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
					" , max(u.date_updated) as u.date_updated  " +
				" from "+getTableName(name, RECORDS_TABLE)+" r, "+
					getTableName(name, RECORD_UPDATES_TABLE)+" u ");
		if (inputSet != null) {
			sb.append(
				", "+getTableName(name, RECORDS_SETS_TABLE)+" rs ");
		}
		sb.append(
				" where  (r.record_id > ? or ? is null) "+
					" and r.record_id = u.record_id " +
					" and (u.date_updated >= ? or ? is null) "+
					" and u.date_updated <= ?  " +
					" group by r.record_id "
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
				" order by u.record_id limit " + MSTConfiguration.getInstance().getPropertyAsInt(Constants.CONFIG_OAI_REPO_MAX_IDENTIFIERS, 5000));

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

	protected int getMaxExplain() {
		return config.getPropertyAsInt("harvestProvider.maxExplain", 1000);
	}

	protected int getEstimateCompleteListSizeThreshold() {
		return config.getPropertyAsInt("harvestProvider.estimateCompleteListSizeThreshold", 1000);
	}


	public boolean checkOutsideRange(String sql, AtomicInteger tally, AtomicInteger numMatching) {
		// could LIMIT accomplish the same thing as explain ?
		// no - I dont think so, because if the answer is zero - limit takes a looooong time

		List<Map<String, Object>> records = this.jdbcTemplate.queryForList("explain "+sql);

		BigInteger rows2examine = (BigInteger)records.get(0).get("rows");
		LOG.debug("rows: "+rows2examine);
		if (rows2examine == null) {
			return false;
		} else if (rows2examine.intValue() > getEstimateCompleteListSizeThreshold()) {
			if (tally != null) {
				tally.addAndGet(2);
			}
			if (numMatching != null) {
				numMatching.set(rows2examine.intValue());
			}
		} else {
			int exactCount = this.jdbcTemplate.queryForInt(sql);
			if (exactCount > 0) {
				if (tally != null) {
					tally.addAndGet(1);
				}
				if (numMatching != null) {
					numMatching.set(exactCount);
				}
			}
		}

		if (tally != null)
			LOG.debug("tally: "+tally.get());
		if (numMatching != null)
			LOG.debug("numMatching: "+numMatching.get());

		return true;
	}

	// You used to have a tally param (and startingId).  The purpose was to send it along in each resumptionToken.
	// This way, if you found the actual num usual step #3, you could add the already harvested
	// total to this.  I don't think it's necessary anymore because by the time the harvester will
	// have gotten to that point, the background thread will already have completed.
	public long getRecordCount(String name, Date from, Date until,
			Format inputFormat, Set inputSet, boolean force) {
		//return -1l;
		LOG.debug("from: "+from);
		LOG.debug("until: "+until);
		LOG.debug("inputFormat: "+inputFormat);
		LOG.debug("inputSet: "+inputSet);

		//http://code.google.com/p/xcmetadataservicestoolkit/wiki/ResumptionToken
		int countMethod2use = 0;

		int completeListSizeThreshold = config.getPropertyAsInt("harvestProvider.estimateCompleteListSizeThreshold", 1000000);

		// Check to see if all match
		if (!force) {
			AtomicInteger numNonMatches = new AtomicInteger();
			AtomicInteger numOutsideRange = new AtomicInteger();

			List<String> sqls = new ArrayList<String>();

			sqls.add("select count(*) from "+getTableName(name, RECORDS_TABLE)+" where status = '"+Record.HELD+"'");
			if (inputFormat != null) {
				sqls.add("select count(*) from "+getTableName(name, RECORDS_TABLE)+" where format_id <> "+inputFormat.getId());
			}
			if (inputSet != null) {
				sqls.add("select count(*) from "+getTableName(name, RECORDS_SETS_TABLE)+" where set_id <> "+inputSet.getId());
			}
			if (from != null || until != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("select count(*) "+
						"from "+getTableName(name, RECORD_UPDATES_TABLE)+" ");
				boolean whereInserted = false;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
				if (from != null) {
					whereInserted = true;
					sb.append("where date_updated <= '");
					sb.append(sdf.format(from));
					sb.append("'");
				}
				if (until != null) {
					if (!whereInserted) {
						sb.append("where ");
					} else {
						sb.append("or ");
					}
					sb.append("date_updated >= '");
					sb.append(sdf.format(until));
					sb.append("'");
				}
				sqls.add(sb.toString());
			}

			for (String sql : sqls) {
				if (numNonMatches.get() < 2) {
					if (!checkOutsideRange(sql, numNonMatches, numOutsideRange)) {
						return 0;
					}
				}
			}

			if (numNonMatches.get() < 2) {
				countMethod2use = 1;
				long allRecords = this.jdbcTemplate.queryForLong("select count(*) from "+getTableName(name, RECORDS_TABLE));
				lastCompleteListSizeMethod = 1;
				return allRecords - numOutsideRange.get();
			}
		// Check to see if none match
		} if (!force) {
			AtomicInteger numFound = new AtomicInteger();

			List<String> sqls = new ArrayList<String>();

			sqls.add("select count(*) from "+
					getTableName(name, RECORDS_TABLE)+" where status in ('"+Record.ACTIVE+"', '"+Record.DELETED+"')");
			if (inputFormat != null) {
				sqls.add("select count(*) from "+getTableName(name, RECORDS_TABLE)+" where format_id = "+inputFormat.getId());
			}
			if (inputSet != null) {
				sqls.add("select count(*) from "+getTableName(name, RECORDS_SETS_TABLE)+" where set_id = "+inputSet.getId());
			}
			if (from != null || until != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("select count(*) "+
						"from "+getTableName(name, RECORD_UPDATES_TABLE)+" ");
				boolean whereInserted = false;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
				if (from != null) {
					whereInserted = true;
					sb.append("where date_updated >= '");
					sb.append(sdf.format(from));
					sb.append("'");
				}
				if (until != null) {
					if (!whereInserted) {
						sb.append("where ");
					} else {
						sb.append("or ");
					}
					sb.append("date_updated <= '");
					sb.append(sdf.format(until));
					sb.append("'");
				}
				sqls.add(sb.toString());
			}
			for (String sql : sqls) {
				checkOutsideRange(sql, null, numFound);
				if (numFound.get() == 0) {
					lastCompleteListSizeMethod = 2;
					return 0;
				}
			}
		}

		{
			List<Object> params = new ArrayList<Object>();
			if (until == null) {
				until = new Date();
			}
			StringBuilder sb = new StringBuilder();
			sb.append(
					" select straight_join count(distinct u.record_id) " +
					" from "+getTableName(name, RECORD_UPDATES_TABLE)+" u  force index (idx_record_updates_date_updated), "+
						getTableName(name, RECORDS_TABLE)+" r IGNORE index (idx_records_format_id)");

			if (inputSet != null) {
				sb.append(", "+getTableName(name, RECORDS_SETS_TABLE)+" rs ignore index (idx_record_sets_set_id) ");
			}
			sb.append(
					" where r.status in ('" +Record.ACTIVE+"','"+Record.DELETED+"')"+
						" and r.record_id = u.record_id " +
						" and (u.date_updated >= ? or ? is null) "+
						" and (u.date_updated <= ? or ? is null) "
						);
			params.add(from);
			params.add(from);
			params.add(until);
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

			Object obj[] = params.toArray();

			long recordCount = 0;

			List<Map<String,Object>> records = null;
			BigInteger rows2examine = null;

			records = this.jdbcTemplate.queryForList("explain "+sb.toString(), obj);
			rows2examine = (BigInteger)records.get(0).get("rows");
			LOG.debug("rows: "+rows2examine);
			LOG.debug("force: "+force);
			if (rows2examine.intValue() < completeListSizeThreshold || force) {
				countMethod2use = 3;
				recordCount = this.jdbcTemplate.queryForLong(sb.toString(), obj);
				lastCompleteListSizeMethod = 3;
				return recordCount;
			}
		}

		if (countMethod2use == 0) {
			//TODO: take a guess
			return -1l;
		}

		return -1l;
	}

	public List<Set> getSets(String repoName, long recordId) {
		List<Set> sets = new ArrayList<Set>();
		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				" select s.set_spec, s.display_name "+
				" from sets s, "+getTableName(repoName, RECORDS_SETS_TABLE)+" rs"+
				" where s.set_id = rs.set_id "+
				" and rs.record_id = ?", recordId);
		for (Map<String, Object> row : rows) {
			Set s = new Set();
			s.setSetSpec((String)row.get("set_spec"));
			s.setDisplayName((String)row.get("display_name"));
			sets.add(s);
		}
		return sets;
	}

	public List<Record> getRecordsWSets(String name, Date from, Date until, Long startingId) {
		return getRecordsWSets(name, from, until, startingId, null, null, new char[] {Record.ACTIVE, Record.DELETED});
	}

	public List<Record> getRecordsWSets(String name, Date from, Date until,
			Long startingId, Format inputFormat, Set inputSet, char[] statuses) {
		List<Object> params = new ArrayList<Object>();
		if (until == null) {
			until = new Date();
		}

		List<Record> records = getRecords(name, from, until, startingId, inputFormat, inputSet, statuses);
		if (records != null && records.size() > 0) {
			Long highestId = records.get(records.size()-1).getId();
			startingId = records.get(0).getId();
			StringBuilder sb = new StringBuilder();
			sb.append(
					" select straight_join rs.record_id, "+
						"s.set_id, "+
						"s.set_spec, "+
						"s.display_name "+
					" from "+getTableName(name, RECORD_UPDATES_TABLE)+" u force index (idx_record_updates_record_id), "+
						getTableName(name, RECORDS_SETS_TABLE)+" rs, "+
						" sets s "+
					" where rs.record_id = u.record_id " +
						" and rs.set_id = s.set_id "+
						" and (rs.record_id >= ? or ? is null) "+
						" and rs.record_id <= ? "+
						" and (u.date_updated >= ? or ? is null) "+
						" and u.date_updated <= ? "+
						" group by u.record_id "+
						" order by u.record_id ");
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
				//LOG.error("records_w_sets_query");
				TimingLogger.start("records_w_sets_query");
				recordsWSets = this.jdbcTemplate.query(sb.toString(), obj,
						new RecordMapper(new String[]{RECORDS_SETS_TABLE}, this));
				LOG.debug("recordsWSets.size() "+recordsWSets.size());
				TimingLogger.stop("records_w_sets_query");

				int recIdx = 0;
				Record currentRecord = records.get(recIdx);
				/*
				for (Record rws : recordsWSets) {
					LOG.debug("rws.getId(): "+rws.getId());
				}
				for (Record r : records) {
					LOG.debug("r.getId(): "+r.getId());
				}
				*/
				for (Record rws : recordsWSets) {
					//LOG.debug("currentRecord.getId(): "+currentRecord.getId());
					//LOG.debug("rws.getId(): "+rws.getId());
					if (rws.getId() < currentRecord.getId()) {
						continue;
					}
					while (rws.getId() > currentRecord.getId()) {
						//LOG.debug("recIdx: "+recIdx);
						currentRecord = records.get(++recIdx);
					}
					currentRecord.addSet(rws.getSets().get(0));
				}
			} catch (EmptyResultDataAccessException e) {
				LOG.info("no recordsWSets found for from: "+from+" until: "+until+" startingId: "+startingId);
			}

			sb = new StringBuilder();
			sb.append(
					" select m.record_id, "+
						"m.rec_in_out, "+
						"m.msg_code, "+
						"m.msg_level, "+
						"m.service_id, "+
						"md.detail "+
					" from "+getTableName(name, RECORD_UPDATES_TABLE)+" u "+
							" inner join ("+MessageDAO.MESSAGES_TABLE+" m) on (m.record_id=u.record_id) "+
							" left outer join ("+MessageDAO.MESSAGE_DETAILS_TABLE+" md) on (m.record_message_id=md.record_message_id) "+
					" where (u.record_id >= ? or ? is null) "+
						" and u.record_id <= ? "+
						" and (u.date_updated >= ? or ? is null) "+
						" and u.date_updated <= ? "+
						" order by u.record_id ");
			LOG.debug("name: "+name+" startingId: "+startingId+" highestId: "+highestId+" from:"+from+" until:"+until);
			params = new ArrayList<Object>();
			params.add(startingId);
			params.add(startingId);
			params.add(highestId);
			params.add(from);
			params.add(from);
			params.add(until);

			Object obj2[] = params.toArray();

			List<Record> recordsWMessages = null;
			try {
				//LOG.error("records_w_sets_query");
				TimingLogger.start("records_w_messages_query");
				recordsWMessages = this.jdbcTemplate.query(sb.toString(), obj2,
						new RecordMapper(new String[]{MessageDAO.MESSAGES_TABLE, MessageDAO.MESSAGE_DETAILS_TABLE}, this));
				LOG.debug("recordsWMessages.size() "+recordsWMessages.size());
				TimingLogger.stop("records_w_messages_query");

				int recIdx = 0;
				Record currentRecord = records.get(recIdx);
				for (Record rws : recordsWMessages) {
					if (rws.getId() < currentRecord.getId()) {
						continue;
					}
					while (rws.getId() > currentRecord.getId()) {
						currentRecord = records.get(++recIdx);
					}
					currentRecord.addMessage(rws.getMessages().get(0));
				}
			} catch (EmptyResultDataAccessException e) {
				LOG.info("no recordsWMessages found for from: "+from+" until: "+until+" startingId: "+startingId);
			}
			LOG.debug("records.size(): "+records.size());
		}

		return records;
	}

	protected List<Map<String, Object>> getPredecessors(String name, int page) {
		TimingLogger.start("getPredecessors");
		int recordsAtOnce = 100000;
		List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
				" select record_id, pred_record_id "+
				" from "+getTableName(name, RECORD_PREDECESSORS_TABLE)+
				" limit "+(page*recordsAtOnce)+","+recordsAtOnce);
		TimingLogger.stop("getPredecessors");
		return rowList;
	}

	public void populatePredecessors(String name, TLongHashSet predecessors) {
		TimingLogger.outputMemory();
		TimingLogger.start("populatePredecessors");
		int page = 0;
		List<Map<String, Object>> rowList = getPredecessors(name, page);
		while (rowList != null && rowList.size() > 0) {
			for (Map<String, Object> row : rowList) {
				TimingLogger.add("pred_record", 0);
				//Long succId = (Long)row.get("record_id");
				Long predId = (Long)row.get("pred_record_id");
				predecessors.add(predId);
			}
			rowList = getPredecessors(name, ++page);
		}
		TimingLogger.stop("populatePredecessors");
		TimingLogger.reset();
	}


	public java.util.Set<Record> getSuccessorIds(String name, Long predId) {
		TimingLogger.start("RepositoryDAO.getSuccessorIds");
		java.util.Set<Record> succIds = new TreeSet<Record>();
		List<Map<String, Object>> rowList = this.jdbcTemplate.queryForList(
				" select r.record_id, status, type "+
				" from "+getTableName(name, RECORD_PREDECESSORS_TABLE)+" rp, "+
					getTableName(name, RECORDS_TABLE)+" r "+
				" where rp.pred_record_id=? "+
					" and rp.record_id = r.record_id ",
				predId);
		for (Map<String, Object> row : rowList) {
			LOG.debug("row: "+row);
			Integer succId = (Integer)row.get("record_id");
			LOG.debug("succId: "+succId);
			Record r = new Record();
			r.setId(succId);
			r.setType((String)row.get("type"));
			r.setStatus(((String)row.get("status")).charAt(0));
			succIds.add(r);
		}
		TimingLogger.stop("RepositoryDAO.getSuccessorIds");
		return succIds;
	}

	public List<Long> getLinkedRecordIds(String name, Long toRecordId) {
		List<Long> linkedRecordsIds = new ArrayList<Long>();
		String sql = "select from_record_id from "+getTableName(name, RECORD_LINKS_TABLE)+" where to_record_id = ?";
		List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql, toRecordId);
		if (results != null) {
			for (Map<String, Object> row : results) {
				linkedRecordsIds.add((Long)row.get("from_record_id"));
			}
		}
		return linkedRecordsIds;
	}

	public void persistLinkedRecordIds(String name, final List<long[]> links) {
		String sql = "insert into "+getTableName(name, RECORD_LINKS_TABLE)+" (from_record_id, to_record_id) values (?,?)";
		TimingLogger.start(RECORD_LINKS_TABLE+".insert");
		int[] updateCounts = jdbcTemplate.batchUpdate(
				sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int j) throws SQLException {
						long[] link = links.get(j);
						ps.setLong(1, link[0]);
						ps.setLong(2, link[1]);
					}

					public int getBatchSize() {
						return links.size();
					}
				} );
		TimingLogger.stop(RECORD_LINKS_TABLE+".insert");
	}

	public void persistPreviousStatuses(String repoName, TLongByteHashMap previousStatuses) {
		try {
			String dbLoadFileStr = (MSTConfiguration.getUrlPath()+"/db_load.in").replace('\\', '/');
			byte[] tabBytes = "\t".getBytes();
			byte[] newLineBytes = "\n".getBytes();

			File dbLoadFile = new File(dbLoadFileStr);
			if (dbLoadFile.exists()) {
				dbLoadFile.delete();
			}
			OutputStream os = new BufferedOutputStream(new FileOutputStream(dbLoadFileStr));
			int i=0;
			TimingLogger.start(PREV_INCOMING_RECORD_STATUSES+".insert");
			TimingLogger.start(PREV_INCOMING_RECORD_STATUSES+".insert.create_infile");
			for (long recordId : previousStatuses.keys()) {
				if (i++ > 0) {
					os.write(newLineBytes);
				}
				os.write((recordId+"").getBytes());
				os.write(tabBytes);
				os.write(previousStatuses.get(recordId));
			}
			os.close();
			TimingLogger.stop(PREV_INCOMING_RECORD_STATUSES+".insert.create_infile");
			TimingLogger.start(PREV_INCOMING_RECORD_STATUSES+".insert.load_infile");
			this.jdbcTemplate.execute(
					"load data infile '"+dbLoadFileStr+"' REPLACE into table "+
					getTableName(repoName, PREV_INCOMING_RECORD_STATUSES)+
					" character set utf8 fields terminated by '\\t' lines terminated by '\\n'"
					);
			TimingLogger.stop(PREV_INCOMING_RECORD_STATUSES+".insert.load_infile");
			TimingLogger.stop(PREV_INCOMING_RECORD_STATUSES+".insert");
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public void activateRecords(String name, final TLongHashSet recordIds) {
		if (recordIds.size() > 0) {
			long startTime = System.currentTimeMillis();
			TimingLogger.start("activateRecords");
			String sql = "update "+getTableName(name, RepositoryDAO.RECORDS_TABLE)+
				" set status='"+Record.ACTIVE+"'"+
				" where record_id = ?";
			final TLongIterator it = recordIds.iterator();
			int[] updateCount = jdbcTemplate.batchUpdate(
					sql,
					new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int j) throws SQLException {
							ps.setLong(1, it.next());
						}
						public int getBatchSize() {
							return recordIds.size();
						}
					});
			TimingLogger.stop("activateRecords");
			long endTime = System.currentTimeMillis();
			final long updateTime = System.currentTimeMillis() + (endTime - startTime) + 3000;
			final TLongIterator it2 = recordIds.iterator();
			TimingLogger.start("RECORD_UPDATES_TABLE.insert");
			sql =
				"insert into "+getTableName(name, RECORD_UPDATES_TABLE)+
				" (record_id, date_updated) "+
				"values (?,?) "+
				";";
			jdbcTemplate.batchUpdate(
					sql,
					new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int j) throws SQLException {
							int i=1;
							ps.setLong(i++, it2.next());
							ps.setTimestamp(i++, new Timestamp(updateTime));
						}

						public int getBatchSize() {
							return recordIds.size();
						}
					} );
			TimingLogger.stop("RECORD_UPDATES_TABLE.insert");
		} else {
			LOG.debug("linkedToIds is null or empty");
		}
	}

	public void activateLinkedRecords(String name, final TLongArrayList linkedToIds) {
		if (linkedToIds.size() > 0) {
			TimingLogger.start("activateHeldHoldings");
			StringBuilder sb = new StringBuilder("update "+getTableName(name, RepositoryDAO.RECORDS_TABLE)+
				" set status='"+Record.ACTIVE+"'"+
				" where record_id in (select from_record_id from links where to_record_id in (");
			for (int i=0; i<linkedToIds.size(); i++) {
				sb.append("?");
				if (i+1 < linkedToIds.size()) {
					sb.append(", ");
				}
			}
			sb.append("))");
			LOG.debug("sb.toString(): "+sb.toString());

			int updateCount = jdbcTemplate.update(
					sb.toString(), new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps) throws SQLException {
							for (int i=0; i<linkedToIds.size(); i++) {
								ps.setLong(i+1, linkedToIds.get(i));
							}
						}
					});
			TimingLogger.stop("activateHeldHoldings");
		} else {
			LOG.debug("linkedToIds is null or empty");
		}
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
				p.setOaiProviderUrl(rs.getString("p.oai_provider_url"));
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
				r.setType(rs.getString("type"));
				String status = rs.getString("r.status");
				String prevStatus = rs.getString("r.prev_status");
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
				if (prevStatus != null && prevStatus.length() == 1) {
					char ps = prevStatus.charAt(0);
					if (ps != 0)
						r.setPreviousStatus(ps);
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
				r.setId(rs.getLong("rs.record_id"));
				Set s = new Set();
				s.setId(rs.getInt("s.set_id"));
				s.setSetSpec(rs.getString("s.set_spec"));
				s.setDisplayName(rs.getString("s.display_name"));
				r.addSet(s);
			}
			if (tables.contains(RECORD_PREDECESSORS_TABLE)) {
			}
			RecordMessage rm = null;
			if (tables.contains(MessageDAO.MESSAGES_TABLE)) {
				r.setId(rs.getLong("m.record_id"));
				String inOut = rs.getString("m.rec_in_out");
				if (inOut != null) {
					rm = new RecordMessage();
					rm.setRecord(r);
					rm.setInputRecord("O".equals(rs.getString("m.rec_in_out")));
					rm.setCode(rs.getInt("m.msg_code"));
					rm.setLevel(rs.getString("m.msg_level").charAt(0));
					rm.setServiceId(rs.getInt("m.service_id"));
					r.addMessage(rm);
				}
			}
			if (rm != null && tables.contains(MessageDAO.MESSAGE_DETAILS_TABLE)) {
				rm.setDetail(rs.getString("md.detail"));
			}
			return r;
		}
	}

	public void dropIndicies(String name) {
		name = getUtil().getDBSchema(name);
		TimingLogger.start("dropIndicies."+name);
		String[] indicies2drop = new String[] {
				"idx_"+RECORDS_TABLE+"_date_created", RECORDS_TABLE,
				"idx_"+RECORDS_TABLE+"_status", RECORDS_TABLE,
				"idx_"+RECORDS_TABLE+"_format_id", RECORDS_TABLE,
				"idx_"+RECORD_UPDATES_TABLE+"_date_updated", RECORD_UPDATES_TABLE,
				"idx_"+RECORD_UPDATES_TABLE+"_record_id", RECORD_UPDATES_TABLE,
				//, You shouldn't drop these.  They might be needed (eg reprocessing)
				//"idx_"+RECORDS_SETS_TABLE+"_record_id", RECORDS_SETS_TABLE,
				//"idx_"+RECORDS_SETS_TABLE+"_set_id", RECORDS_SETS_TABLE,
				//"idx_"+RECORD_PREDECESSORS_TABLE+"_record_id", RECORD_PREDECESSORS_TABLE,
				//"idx_"+RECORD_PREDECESSORS_TABLE+"_pred_record_id", RECORD_PREDECESSORS_TABLE
		};
		for (int i=0; i<indicies2drop.length; i+=2) {
			try {
				this.jdbcTemplate.execute("drop index "+indicies2drop[i]+" on "+getTableName(name, indicies2drop[i+1]));
			} catch (Throwable t) {
				LOG.error("", t);
			}
		}
		java.util.Set<String> tables = new HashSet<String>();
		List<Map<String,Object>> rows = this.jdbcTemplate.queryForList("show tables in "+name);
		if (rows != null) {
			for (Map<String, Object> row : rows) {
				tables.add((String)row.values().iterator().next());
			}
		}
		boolean dropIndiciesOnRecordLinks = false;
		if (tables.contains(RECORD_LINKS_TABLE)) {
			dropIndiciesOnRecordLinks = true;
			rows = this.jdbcTemplate.queryForList("show indexes from "+getTableName(name, RECORD_LINKS_TABLE));
			if (rows != null) {
				for (Map<String, Object> row : rows) {
					String indexName = (String)row.get("Key_name");
					LOG.debug("indexName: "+indexName);
					if (("idx_to_record_id").equals(indexName)) {
						dropIndiciesOnRecordLinks = false;
						break;
					}
				}
			}
		}
		if (dropIndiciesOnRecordLinks) {
			indicies2drop = new String[] {
					"drop index idx_from_record_id on "+getTableName(name, RECORD_LINKS_TABLE),
					"drop index idx_to_record_id on "+getTableName(name, RECORD_LINKS_TABLE)
			};
			for (String index2drop : indicies2drop) {
				execute(index2drop);
			}
		}
		TimingLogger.stop("dropIndicies."+name);
	}

	public void createIndiciesIfNecessary(String name) {
		name = getUtil().getDBSchema(name);
		TimingLogger.start("createIndiciesIfNecessary."+name);
		List<Map<String,Object>> rows = this.jdbcTemplate.queryForList("show indexes from "+getTableName(name, RECORDS_TABLE));
		boolean genericRepoIndexExists = false;
		if (rows != null) {
			for (Map<String, Object> row : rows) {
				String indexName = (String)row.get("Key_name");
				LOG.debug("indexName: "+indexName);
				if (("idx_records_status").equals(indexName)) {
					genericRepoIndexExists = true;
					break;
				}
			}
		}
		java.util.Set<String> tables = new HashSet<String>();
		rows = this.jdbcTemplate.queryForList("show tables in "+name);
		if (rows != null) {
			for (Map<String, Object> row : rows) {
				tables.add((String)row.values().iterator().next());
			}
		}
		boolean createIndiciesOnRecordOaiIds = false;
		if (tables.contains(RECORD_OAI_IDS)) {
			createIndiciesOnRecordOaiIds = true;
			rows = this.jdbcTemplate.queryForList("show indexes from "+getTableName(name, RECORD_OAI_IDS));
			if (rows != null) {
				for (Map<String, Object> row : rows) {
					String indexName = (String)row.get("Key_name");
					LOG.debug("indexName: "+indexName);
					createIndiciesOnRecordOaiIds = false;
					break;
				}
			}
		}
		boolean createIndiciesOnRecordLinks = false;
		if (tables.contains(RECORD_LINKS_TABLE)) {
			createIndiciesOnRecordLinks = true;
			rows = this.jdbcTemplate.queryForList("show indexes from "+getTableName(name, RECORD_LINKS_TABLE));
			if (rows != null) {
				for (Map<String, Object> row : rows) {
					String indexName = (String)row.get("Key_name");
					LOG.debug("indexName: "+indexName);
					if (("idx_to_record_id").equals(indexName)) {
						createIndiciesOnRecordLinks = false;
						break;
					}
				}
			}
		}

		if (!genericRepoIndexExists) {
			String[] indicies2create = new String[] {
					//"alter table"+getTableName(name, RECORDS_TABLE)+" add primary key (record_id)",
					"create index idx_records_date_created on "+getTableName(name, RECORDS_TABLE)+" (oai_datestamp)",
					"create index idx_records_status on "+getTableName(name, RECORDS_TABLE)+" (status)",
					"create index idx_records_format_id on "+getTableName(name, RECORDS_TABLE)+" (format_id)",

					//"alter table "+getTableName(name, RECORD_UPDATES_TABLE)+" add primary key (id)",
					"create index idx_record_updates_date_updated on "+getTableName(name, RECORD_UPDATES_TABLE)+" (date_updated)",
					"create index idx_record_updates_record_id on "+getTableName(name, RECORD_UPDATES_TABLE)+" (record_id)",

					//"alter table"+getTableName(name, RECORDS_XML_TABLE)+" add primary key (record_id)",

					//"alter table"+getTableName(name, RECORDS_SETS_TABLE)+" add primary key (record_id, set_id)",
					"create index idx_"+RECORDS_SETS_TABLE+"_record_id on "+getTableName(name, RECORDS_SETS_TABLE)+" (record_id)",
					"create index idx_"+RECORDS_SETS_TABLE+"_set_id on "+getTableName(name, RECORDS_SETS_TABLE)+" (set_id)",

					//"alter table "+getTableName(name, RECORD_PREDECESSORS_TABLE)+" add primary key (id)",
					//"alter table"+getTableName(name, RECORD_PREDECESSORS_TABLE)+" add primary key (record_id, pred_record_id)",
					"create index idx_"+RECORD_PREDECESSORS_TABLE+"_record_id on "+getTableName(name, RECORD_PREDECESSORS_TABLE)+" (record_id)",
					"create index idx_"+RECORD_PREDECESSORS_TABLE+"_pred_record_id on "+getTableName(name, RECORD_PREDECESSORS_TABLE)+" (pred_record_id)",

			};
			for (String i2c : indicies2create) {
				TimingLogger.start(i2c.split(" ")[2]);
				try {
					this.jdbcTemplate.execute(i2c);
				} catch (Throwable t) {
					LOG.error("", t);
				}
				TimingLogger.stop(i2c.split(" ")[2]);
			}
		}
		if (createIndiciesOnRecordOaiIds) {
			// you might have to remove duplicates...
			// or for now, just put the primary key back
			/*
			String[] indicies2create = new String[] {
					"alter table"+getTableName(name, RECORD_OAI_IDS)+" add primary key (record_id)"
			};
			for (String i2c : indicies2create) {
				TimingLogger.start(i2c.split(" ")[2]);
				this.jdbcTemplate.execute(i2c);
				TimingLogger.stop(i2c.split(" ")[2]);
			}
			*/
		}
		if (createIndiciesOnRecordLinks) {
			// TODO: you might have to remove duplicates
			String[] indicies2create = new String[] {
					"create index idx_from_record_id on "+getTableName(name, RECORD_LINKS_TABLE)+" (from_record_id)",
					"create index idx_to_record_id on "+getTableName(name, RECORD_LINKS_TABLE)+" (to_record_id)"
			};
			for (String i2c : indicies2create) {
				TimingLogger.start(i2c.split(" ")[2]);
				this.jdbcTemplate.execute(i2c);
				TimingLogger.stop(i2c.split(" ")[2]);
			}
		}
		TimingLogger.stop("createIndiciesIfNecessary."+name);
		TimingLogger.reset();
	}

	public boolean ready4harvest(String name) {
		boolean genericRepoIndexExists = false;
		name = getUtil().getDBSchema(name);
		try {
			List<Map<String,Object>> rows = this.jdbcTemplate.queryForList("show indexes from "+getTableName(name, RECORDS_TABLE));
			if (rows != null) {
				for (Map<String, Object> row : rows) {
					String indexName = (String)row.get("Key_name");
					LOG.debug("indexName: "+indexName);
					if (("idx_records_status").equals(indexName)) {
						genericRepoIndexExists = true;
						break;
					}
				}
			}
		} catch (Throwable t) {
			//do nothing
		    LOG.debug("",t);
		}
		LOG.debug(name+" ready4harvest: "+genericRepoIndexExists);
		return genericRepoIndexExists;
	}

	public String getPersistentProperty(String name, String key) {
		try {
			return (String)this.jdbcTemplate.queryForObject(
					" select value "+
					" from "+getTableName(name, PROPERTIES)+
					" where prop_key = ?",
					String.class,
					key);
		} catch (EmptyResultDataAccessException t) {
			return null;
		}
	}

	public List<String[]> getAllPersistentProperties(String name) {
		try {
			List<String[]> props = new ArrayList<String[]>();
			List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
					"select prop_key, value from "+getTableName(name, PROPERTIES));
			if (rows != null) {
				for (Map<String, Object> row : rows) {
					props.add(new String[]{
							(String)row.get("prop_key"),
							(String)row.get("value")});
				}
			}
			return props;
		} catch (EmptyResultDataAccessException t) {
			return null;
		}
	}

	public void setPersistentProperty(String name, String key, String value) {
		this.jdbcTemplate.update("insert into "+getTableName(name, PROPERTIES)+
				" values (?, ?) on duplicate key update value=?",
				key, value, value);
	}

	public boolean isServiceRepo(String repoName) {
		return tableExists(getUtil().getDBSchema(repoName), RECORD_LINKS_TABLE);
	}

	public boolean isProviderRepo(String repoName) {
		return !tableExists(getUtil().getDBSchema(repoName), RECORD_LINKS_TABLE);
	}


	public String getRecordStatsByType(String name) {
		StringBuilder sb = new StringBuilder();

		List<Map<String, Object>> otherRows = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> totalsRows =
			this.jdbcTemplate.queryForList(
				"select status, count(*) c from "+
				getTableName(name, RECORDS_TABLE)+" group by status order by status");
		List<Map<String, Object>> rowsByType = null;

		if (isServiceRepo(name)) {
			rowsByType = this.jdbcTemplate.queryForList(
					"select type, status, count(*) c from "+
					getTableName(name, RECORDS_TABLE)+" group by type, status order by type, status");
		} else {
			rowsByType = this.jdbcTemplate.queryForList(
					"select rs.set_id type, r.status status, count(*) c "+
					"from "+
						getTableName(name, RECORDS_SETS_TABLE)+" rs, "+
						getTableName(name, RECORDS_TABLE)+" r "+
						" where r.record_id = rs.record_id group by rs.set_id, r.status");

			for (Map<String, Object> row : totalsRows) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("status", row.get("status"));
				m.put("c", row.get("c"));
				m.put("type", "other");
				otherRows.add(m);
			}
			List<Map<String, Object>> rows2remove = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> row : rowsByType) {
				LOG.debug("row.get(type): "+row.get("type"));
				if (row.get("type") != null)
					LOG.debug("row.get(type).getClass(): "+row.get("type").getClass());
				if (row.get("type") instanceof Integer) {
					try {
						String type = getSetDAO().getById((Integer)row.get("type")).getSetSpec();
						row.put("type", type);
						if (!type.contains(":")) {
							rows2remove.add(row);
							continue;
						}
					} catch (Throwable t) {
						getUtil().throwIt(t);
					}
				}
				String status = (String)row.get("status");
				Map<String, Object> m = null;
				for (Map<String, Object> otherRow : otherRows) {
					if (status.equals(otherRow.get("status"))) {
						m = otherRow;
						break;
					}
				}
				m.put("c", ((Long)m.get("c"))-((Long)row.get("c")));
			}
			rowsByType.removeAll(rows2remove);
		}
		for (List<Map<String, Object>> rows : new List[] {
				rowsByType,
				otherRows,
				totalsRows
		}) {
			int col=0;
			for (Map<String, Object> row : rows) {
				String type = "total";
				if (row.containsKey("type")) {
					if (StringUtils.isEmpty((String)row.get("type"))) {
						type = RecordCounts.OTHER;
					} else {
						type = (String)row.get("type");
					}
				}
				if (col == 0)
					sb.append("\n");
				sb.append(StringUtils.leftPad(type+"-"+
						Record.statusNames.get(((String)row.get("status")).charAt(0)),
						30));
				sb.append(":");
				DecimalFormat myFormatter = new DecimalFormat("###,###,###");
				sb.append(StringUtils.leftPad(myFormatter.format(getUtil().getLongPrim(row.get("c"))), 12));
				if (++col == 3) {
					col = 0;
				}
			}
		}
		return sb.toString();
	}

	public void updateOutgoingRecordCounts(String name) {
		Map<String, long[]> countsByType = new HashMap<String, long[]>();
		countsByType.put("total", new long[3]);

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select count(*) as count, type, status from "+getTableName(name, RECORDS_TABLE)+" group by type, status");

		if (rows != null) {
			for (Map<String, Object> row : rows) {
				Long count = (Long)row.get("count");
				String type = (String)row.get("type");
				char status = ((String)row.get("status")).charAt(0);

				long[] counts4type = countsByType.get(type);
				if (counts4type == null) {
					counts4type = new long[3];
					countsByType.put(type, counts4type);
				}
				if (status == Record.ACTIVE) {
					countsByType.get("total")[0] += count;
					counts4type[0] = count;
				} else if (status == Record.DELETED) {
					countsByType.get("total")[2] += count;
					counts4type[2] = count;
				}
			}
		}

		rows = this.jdbcTemplate.queryForList(
				" select count(*) as count, r.type as type "+
				" from "+
					getTableName(name, RECORD_UPDATES_TABLE)+" as u, "+
					getTableName(name, RECORDS_TABLE)+" as r "+
				" where u.record_id = r.record_id "+
				" group by r.type"
				);
		if (rows != null) {
			for (Map<String, Object> row : rows) {
				Long count = (Long)row.get("count");
				String type = (String)row.get("type");

				countsByType.get(type)[1] = count-countsByType.get(type)[0];
				countsByType.get("total")[1] += count;
			}
			countsByType.get("total")[1] = countsByType.get("total")[1] - countsByType.get("total")[0];
		}

		for (Map.Entry<String, long[]> counts4type : countsByType.entrySet()) {
			String key = "RecordsCount";
			if (counts4type.getKey() == null || counts4type.getKey().equals("")) {
				continue;
			}
			if (!counts4type.getKey().equals("total")) {
				key += "-"+counts4type.getKey();
			}
			setPersistentProperty(name, "outgoingActive"+key, ""+counts4type.getValue()[0]);
			setPersistentProperty(name, "outgoingUpdated"+key, ""+counts4type.getValue()[1]);
			setPersistentProperty(name, "outgoingDeleted"+key, ""+counts4type.getValue()[2]);
		}
	}

	public List<Integer> getSetIds(String name) {
		//List<Integer> sets = new ArrayList<Integer>();
		List<Integer> setIds = this.jdbcTemplate.queryForList(
				"select set_id from "+getTableName(name, RECORDS_SETS_TABLE)+" group by set_id", Integer.class);
		return setIds;
	}
}
