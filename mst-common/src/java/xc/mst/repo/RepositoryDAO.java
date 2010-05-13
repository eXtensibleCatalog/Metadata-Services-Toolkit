package xc.mst.repo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import xc.mst.dao.BaseDAO;

public class RepositoryDAO extends BaseDAO {
	
	//private final static Logger LOG = Logger.getLogger(RepositoryDAO.class);
	private static Logger LOG = Logger.getLogger("harvestIn");
	
	protected final static String RECORDS_TABLE = "RECORDS";
	protected final static String RECORDS_XML_TABLE = "RECORDS_XML";
	protected final static String RECORDS_SETS_TABLE = "RECORDS_SETS";
	protected final static String RECORD_PREDECESSORS_TABLE = "RECORD_PREDECESSORS";
	
	protected final static String[] ALL_TABLES = new String[] {
		RECORDS_TABLE, RECORDS_XML_TABLE, RECORDS_SETS_TABLE, RECORD_PREDECESSORS_TABLE
	};
	
	protected String getTableName(String repoName, String tableName) {
		return " "+repoName+"_"+tableName+" ";
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
