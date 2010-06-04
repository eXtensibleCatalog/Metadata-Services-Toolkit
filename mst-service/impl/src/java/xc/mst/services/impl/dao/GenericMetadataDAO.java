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
			StringBuilder replacedContents = new StringBuilder();
			String[] tokens = createTablesContents.split("\n");
			String currentDelimiter = ";";
			for (String line : tokens) {
				if (line.matches("^[dD][eE][lL][iI][mM][iI][tT][eE][rR].*")) {
					LOG.debug("matches");
					currentDelimiter = StringUtils.trim(line.split(" ")[1]);
					if (currentDelimiter.equals(";")) {
						replacedContents.append("\nEND_OF_STMT");
					}
					continue;
				}
				String replaceWith = ";\nEND_OF_STMT";
				if (!currentDelimiter.equals(";")) {
					replaceWith = ";";
				}
				replacedContents.append(line.replaceAll(currentDelimiter, replaceWith));
				replacedContents.append("\n");
			}
			LOG.debug("replacedContents: "+replacedContents);
			String[] stmts = replacedContents.toString().split("END_OF_STMT");
			for (String sql : stmts) {
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
