/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

import xc.mst.dao.harvest.HarvestDAO;
import xc.mst.dao.harvest.HarvestRecordUtilDAO;
import xc.mst.dao.harvest.HarvestScheduleDAO;
import xc.mst.dao.harvest.HarvestScheduleStepDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.dao.processing.JobDAO;
import xc.mst.dao.processing.ProcessingDirectiveDAO;
import xc.mst.dao.processing.ProcessingDirectiveInputFormatUtilDAO;
import xc.mst.dao.processing.ProcessingDirectiveInputSetUtilDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.provider.ProviderFormatUtilDAO;
import xc.mst.dao.provider.SetDAO;
import xc.mst.dao.record.MessageDAO;
import xc.mst.dao.record.RecordDAO;
import xc.mst.dao.record.RecordTypeDAO;
import xc.mst.dao.record.ResumptionTokenDAO;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;
import xc.mst.dao.service.ErrorCodeDAO;
import xc.mst.dao.service.OaiIdentifierForServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.dao.service.ServiceInputFormatUtilDAO;
import xc.mst.dao.service.ServiceOutputFormatUtilDAO;
import xc.mst.dao.service.ServiceOutputSetUtilDAO;
import xc.mst.dao.user.GroupDAO;
import xc.mst.dao.user.GroupPermissionUtilDAO;
import xc.mst.dao.user.PermissionDAO;
import xc.mst.dao.user.ServerDAO;
import xc.mst.dao.user.UserDAO;
import xc.mst.dao.user.UserGroupUtilDAO;
import xc.mst.manager.record.RecordService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class BaseDAO {
	
	private final static Logger LOG = Logger.getLogger(BaseDAO.class);
	
	protected DataSource dataSource = null;
	protected MSTConfiguration config;
	
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
	protected HibernateTemplate hibernateTemplate = null;
	protected JdbcTemplate jdbcTemplate = null;
	protected SimpleJdbcTemplate simpleJdbcTemplate = null;
	
	protected Util util = null;
	
	public void init() {}
	
	public void setConfig(MSTConfiguration config) {
		this.config = config;
	}

	
	public void createSchema(String name) {
		createSchema(name, false);
	}
	
	public void createSchema(String name, boolean dropIfExists) {
		name = getUtil().normalizeName(name);
		// this is potentially dangerous, but necessary for now
		if (dropIfExists && schemasExists(name)) {
			deleteSchema(name);
		}
		if (!schemasExists(name))
			this.jdbcTemplate.execute("create database "+name+" character set=utf8;");
	}
	
	public void deleteSchema(String name) {
		String sql = "drop database "+getUtil().normalizeName(name);
		LOG.debug("executing: "+sql);
		this.jdbcTemplate.execute(sql);
	}
	
	public List<String> getSchemas() {
		return this.jdbcTemplate.queryForList("show databases", String.class);
	}
	
	public boolean schemasExists(String name) {
		name = name.toUpperCase();
		List<String> dbs = getSchemas();
		for (String db : dbs) {
			if (name.equals(db.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean tableExists(String name) {
		List<String> allTables = this.jdbcTemplate.queryForList("show tables", String.class);
		List<String> allTablesUpper = new ArrayList<String>();
		for (String table : allTables) {
			allTablesUpper.add(table.toUpperCase());
		}
		boolean ret = allTablesUpper.contains(name.toUpperCase());
		LOG.debug("allTablesUpper: "+allTablesUpper);
		LOG.debug("name: "+name);
		LOG.debug(ret+":tableExists("+name+")");
		return ret;
	}
	
	public List<String> getTablesWithPrefix(String prefix) {
		List<String> tablesWithPrefix = new ArrayList<String>();
		try {
			prefix = prefix.toUpperCase();
			List<String> allTables = this.jdbcTemplate.queryForList("show tables", String.class);
			for (String table : allTables) {
				String upperTable = table.toUpperCase();
				if (upperTable.startsWith(prefix+"_")) {
					tablesWithPrefix.add(table);
				}
			}
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return tablesWithPrefix;
	}
	
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

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(jdbcTemplate);
	}
	
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
	
	public Util getUtil() {
		return util;
	}
	public void setUtil(Util util) {
		this.util = util;
	}
	
	public RecordService getRecordService() {
		return (RecordService)config.getBean("RecordService");
	}
	public HarvestDAO getHarvestDAO() {
		return (HarvestDAO)config.getBean("HarvestDAO");
	}
	public HarvestRecordUtilDAO getHarvestRecordUtilDAO() {
		return (HarvestRecordUtilDAO)config.getBean("HarvestRecordUtilDAO");
	}
	public HarvestScheduleDAO getHarvestScheduleDAO() {
		return (HarvestScheduleDAO)config.getBean("HarvestScheduleDAO");
	}
	public HarvestScheduleStepDAO getHarvestScheduleStepDAO() {
		return (HarvestScheduleStepDAO)config.getBean("HarvestScheduleStepDAO");
	}
	public LogDAO getLogDAO() {
		return (LogDAO)config.getBean("LogDAO");
	}
	public JobDAO getJobDAO() {
		return (JobDAO)config.getBean("JobDAO");
	}
	public ProcessingDirectiveDAO getProcessingDirectiveDAO() {
		return (ProcessingDirectiveDAO)config.getBean("ProcessingDirectiveDAO");
	}
	public ProcessingDirectiveInputFormatUtilDAO getProcessingDirectiveInputFormatUtilDAO() {
		return (ProcessingDirectiveInputFormatUtilDAO)config.getBean("ProcessingDirectiveInputFormatUtilDAO");
	}
	public ProcessingDirectiveInputSetUtilDAO getProcessingDirectiveInputSetUtilDAO() {
		return (ProcessingDirectiveInputSetUtilDAO)config.getBean("ProcessingDirectiveInputSetUtilDAO");
	}
	public FormatDAO getFormatDAO() {
		return (FormatDAO)config.getBean("FormatDAO");
	}
	public ProviderDAO getProviderDAO() {
		return (ProviderDAO)config.getBean("ProviderDAO");
	}
	public ProviderFormatUtilDAO getProviderFormatUtilDAO() {
		return (ProviderFormatUtilDAO)config.getBean("ProviderFormatUtilDAO");
	}
	public SetDAO getSetDAO() {
		return (SetDAO)config.getBean("SetDAO");
	}
	public RecordTypeDAO getRecordTypeDAO() {
		return (RecordTypeDAO)config.getBean("RecordTypeDAO");
	}
	public ResumptionTokenDAO getResumptionTokenDAO() {
		return (ResumptionTokenDAO)config.getBean("ResumptionTokenDAO");
	}
	public XcIdentifierForFrbrElementDAO getXcIdentifierForFrbrElementDAO() {
		return (XcIdentifierForFrbrElementDAO)config.getBean("XcIdentifierForFrbrElementDAO");
	}
	public ErrorCodeDAO getErrorCodeDAO() {
		return (ErrorCodeDAO)config.getBean("ErrorCodeDAO");
	}
	public OaiIdentifierForServiceDAO getOaiIdentifierForServiceDAO() {
		return (OaiIdentifierForServiceDAO)config.getBean("OaiIdentifierForServiceDAO");
	}
	public ServiceDAO getServiceDAO() {
		return (ServiceDAO)config.getBean("ServiceDAO");
	}
	public ServiceInputFormatUtilDAO getServiceInputFormatUtilDAO() {
		return (ServiceInputFormatUtilDAO)config.getBean("ServiceInputFormatUtilDAO");
	}
	public ServiceOutputFormatUtilDAO getServiceOutputFormatUtilDAO() {
		return (ServiceOutputFormatUtilDAO)config.getBean("ServiceOutputFormatUtilDAO");
	}
	public ServiceOutputSetUtilDAO getServiceOutputSetUtilDAO() {
		return (ServiceOutputSetUtilDAO)config.getBean("ServiceOutputSetUtilDAO");
	}
	public GroupDAO getGroupDAO() {
		return (GroupDAO)config.getBean("GroupDAO");
	}
	public GroupPermissionUtilDAO getGroupPermissionUtilDAO() {
		return (GroupPermissionUtilDAO)config.getBean("GroupPermissionUtilDAO");
	}
	public PermissionDAO getPermissionDAO() {
		return (PermissionDAO)config.getBean("PermissionDAO");
	}
	public ServerDAO getServerDAO() {
		return (ServerDAO)config.getBean("ServerDAO");
	}
	public UserDAO getUserDAO() {
		return (UserDAO)config.getBean("UserDAO");
	}
	public UserGroupUtilDAO getUserGroupUtilDAO() {
		return (UserGroupUtilDAO)config.getBean("UserGroupUtilDAO");
	}
	public RecordDAO getRecordDAO() {
		return (RecordDAO)config.getBean("DBRecordDAO");
	}
	public MessageDAO getMessageDAO() {
		return (MessageDAO)config.getBean("MessageDAO");
	}
}
