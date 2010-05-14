package xc.mst.repo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
	
	public RepositoryDAO() {
		super();
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
	
	public void endBatch() {
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
		if (inBatch) {
			if (recordsToAdd == null) {
				recordsToAdd = new ArrayList<Record>();
			}
			int batchSize = 50000;
			if (MSTConfiguration.getProperty("batchSize") != null) {
				batchSize = Integer.parseInt(MSTConfiguration.getProperty("batchSize"));
			}
			recordsToAdd.addAll(records);
			if (batchSize >= recordsToAdd.size()) {
        		String sql = 
        			"insert into "+getTableName(name, RECORDS_TABLE)+
        			" (record_id, oai_pmh_id_1, oai_pmh_id_2, oai_pmh_id_3, oai_pmh_id_4,"+
        			"  date_created, status, format_id ) "+
        			"values (?,?,?,?,?,?,?,?) "+
        			"on duplicate key update "+
        				"status=?, "+
        				"format_id=? "+
        			";";
        		final Date d = new Date();
		        int[] updateCounts = jdbcTemplate.batchUpdate(
		        		sql,
		                new BatchPreparedStatementSetter() {
		                    public void setValues(PreparedStatement ps, int j) throws SQLException {
		                    	int i=0;
		                    	Record r = recordsToAdd.get(j);
		                        ps.setLong(i++, r.getId());
		                        ps.setString(i++, r.getOaiIds()[0]);
		                        ps.setString(i++, r.getOaiIds()[1]);
		                        ps.setString(i++, r.getOaiIds()[2]);
		                        ps.setString(i++, r.getOaiIds()[3]);
		                        ps.setDate(i++, new java.sql.Date(d.getTime()));
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
		        recordsToAdd = null;
			}
		} else {
			LOG.error("unsupported");
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

}
