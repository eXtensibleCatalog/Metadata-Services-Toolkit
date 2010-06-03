package xc.mst.services.impl.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;

public class GenericMetadataDAO extends BaseDAO {
	
	private static final Logger LOG = Logger.getLogger(GenericMetadataDAO.class);

	protected void executeServiceDBScripts(String fileName) throws DataException {
		String createTablesContents = getUtil().slurp(fileName);
		String[] tokens = createTablesContents.split("\n\n");
		for (String sql : tokens) {
			if (StringUtils.isEmpty(StringUtils.trim(sql))) {
				continue;
			}
			this.jdbcTemplate.execute(sql);
		}
    }
}
