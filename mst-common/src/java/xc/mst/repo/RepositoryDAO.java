package xc.mst.repo;

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
				this.jdbcTemplate.execute("drop table "+getTableName(name, table));
			} catch (Throwable t) {
				LOG.error("", t);
			}
		}
	}
	
	public void createTables(String name) {
		// oai ids
		String sql =
			"create table "+getTableName(name, RECORDS_TABLE)+"("+
				"\t\n record_id        int        NOT NULL    AUTO_INCREMENT,"+
				"\t\n oai_pmh_id_4     char(35)                             ,"+
				"\t\n oai_pmh_id_3     char(35)                             ,"+
				"\t\n oai_pmh_id_2     char(35)                             ,"+
				"\t\n oai_pmh_id_1     char(35)                             ,"+
				"\t\n date_created     datetime                             ,"+
				"\t\n status           char(1)                              ,"+
				"\t\n format_id        int                                  ,"+
			"\n PRIMARY KEY (record_id)                                     ,"+
			"\n KEY idx_"+RECORDS_TABLE+"_oai_pmh_id_4 (oai_pmh_id_4)       ,"+
			"\n KEY idx_"+RECORDS_TABLE+"_date_created (date_created)       ,"+
			"\n KEY idx_"+RECORDS_TABLE+"_status (status)                   ,"+
			"\n KEY idx_"+RECORDS_TABLE+"_format_id (format_id)              "+
			") ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;";
		System.out.println("sql: "+sql);
		LOG.error(sql);
		this.jdbcTemplate.execute(sql);
	}

}
