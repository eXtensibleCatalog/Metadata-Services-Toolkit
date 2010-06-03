package xc.mst.services.impl.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import xc.mst.dao.BaseDAO;

public class GenericMetadataDAO extends BaseDAO {
	
	private static final Logger LOG = Logger.getLogger(GenericMetadataDAO.class);

	public void executeServiceDBScripts(String fileName) {
		List<String> allTables = this.jdbcTemplate.queryForList("show tables", String.class);
		LOG.debug("allTables: "+allTables);
		String createTablesContents = getUtil().slurp(fileName);
		if (createTablesContents != null) {
			String[] tokens = createTablesContents.split("\n\n");
			for (String sql : tokens) {
				if (StringUtils.isEmpty(StringUtils.trim(sql))) {
					continue;
				}
				this.jdbcTemplate.execute(sql);
			}
		} else {
			LOG.info("file is empty or doesn't exist: "+fileName);
		}
    }
}
