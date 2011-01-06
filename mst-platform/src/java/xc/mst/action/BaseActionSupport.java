/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action;

import xc.mst.dao.emailconfig.EmailConfigDAO;
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
import xc.mst.manager.configuration.EmailConfigService;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.manager.logs.LogService;
import xc.mst.manager.processingDirective.JobService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.BrowseRecordService;
import xc.mst.manager.record.ExpressionService;
import xc.mst.manager.record.HoldingsService;
import xc.mst.manager.record.ItemService;
import xc.mst.manager.record.ManifestationService;
import xc.mst.manager.record.RecordService;
import xc.mst.manager.record.WorkService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.ProviderService;
import xc.mst.manager.repository.SetService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.PermissionService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserGroupUtilService;
import xc.mst.manager.user.UserService;
import xc.mst.repo.RepositoryDAO;
import xc.mst.repo.RepositoryService;
import xc.mst.scheduling.Scheduler;
import xc.mst.utils.MSTConfiguration;

import com.opensymphony.xwork2.ActionSupport;
 
@SuppressWarnings("serial")
public class BaseActionSupport extends ActionSupport { 

	public EmailConfigService getEmailConfigService() {
		return (EmailConfigService)MSTConfiguration.getInstance().getBean("EmailConfigService");
	}
	public ScheduleService getScheduleService() {
		return (ScheduleService)MSTConfiguration.getInstance().getBean("ScheduleService");
	}
	public LogService getLogService() {
		return (LogService)MSTConfiguration.getInstance().getBean("LogService");
	}
	public JobService getJobService() {
		return (JobService)MSTConfiguration.getInstance().getBean("JobService");
	}
	public ProcessingDirectiveService getProcessingDirectiveService() {
		return (ProcessingDirectiveService)MSTConfiguration.getInstance().getBean("ProcessingDirectiveService");
	}
	public ServicesService getServicesService() {
		return (ServicesService)MSTConfiguration.getInstance().getBean("ServicesService");
	}
	public BrowseRecordService getBrowseRecordService() {
		return (BrowseRecordService)MSTConfiguration.getInstance().getBean("BrowseRecordService");
	}
	public ExpressionService getExpressionService() {
		return (ExpressionService)MSTConfiguration.getInstance().getBean("ExpressionService");
	}
	public HoldingsService getHoldingsService() {
		return (HoldingsService)MSTConfiguration.getInstance().getBean("HoldingsService");
	}
	public ItemService getItemService() {
		return (ItemService)MSTConfiguration.getInstance().getBean("ItemService");
	}
	public ManifestationService getManifestationService() {
		return (ManifestationService)MSTConfiguration.getInstance().getBean("ManifestationService");
	}
	public WorkService getWorkService() {
		return (WorkService)MSTConfiguration.getInstance().getBean("WorkService");
	}
	public FormatService getFormatService() {
		return (FormatService)MSTConfiguration.getInstance().getBean("FormatService");
	}
	public ProviderService getProviderService() {
		return (ProviderService)MSTConfiguration.getInstance().getBean("ProviderService");
	}
	public SetService getSetService() {
		return (SetService)MSTConfiguration.getInstance().getBean("SetService");
	}
	public GroupService getGroupService() {
		return (GroupService)MSTConfiguration.getInstance().getBean("GroupService");
	}
	public PermissionService getPermissionService() {
		return (PermissionService)MSTConfiguration.getInstance().getBean("PermissionService");
	}
	public ServerService getServerService() {
		return (ServerService)MSTConfiguration.getInstance().getBean("ServerService");
	}
	public UserGroupUtilService getUserGroupUtilService() {
		return (UserGroupUtilService)MSTConfiguration.getInstance().getBean("UserGroupUtilService");
	}
	public UserService getUserService() {
		return (UserService)MSTConfiguration.getInstance().getBean("UserService");
	}
	public RecordService getRecordService() {
		return (RecordService)MSTConfiguration.getInstance().getBean("RecordService");
	}
	public EmailConfigDAO getEmailConfigDAO() {
		return (EmailConfigDAO)MSTConfiguration.getInstance().getBean("EmailConfigDAO");
	}
	public HarvestDAO getHarvestDAO() {
		return (HarvestDAO)MSTConfiguration.getInstance().getBean("HarvestDAO");
	}
	public HarvestRecordUtilDAO getHarvestRecordUtilDAO() {
		return (HarvestRecordUtilDAO)MSTConfiguration.getInstance().getBean("HarvestRecordUtilDAO");
	}
	public HarvestScheduleDAO getHarvestScheduleDAO() {
		return (HarvestScheduleDAO)MSTConfiguration.getInstance().getBean("HarvestScheduleDAO");
	}
	public HarvestScheduleStepDAO getHarvestScheduleStepDAO() {
		return (HarvestScheduleStepDAO)MSTConfiguration.getInstance().getBean("HarvestScheduleStepDAO");
	}
	public Scheduler getScheduler() {
		return (Scheduler)MSTConfiguration.getInstance().getBean("Scheduler");
	}
	public LogDAO getLogDAO() {
		return (LogDAO)MSTConfiguration.getInstance().getBean("LogDAO");
	}
	public JobDAO getJobDAO() {
		return (JobDAO)MSTConfiguration.getInstance().getBean("JobDAO");
	}
	public ProcessingDirectiveDAO getProcessingDirectiveDAO() {
		return (ProcessingDirectiveDAO)MSTConfiguration.getInstance().getBean("ProcessingDirectiveDAO");
	}
	public ProcessingDirectiveInputFormatUtilDAO getProcessingDirectiveInputFormatUtilDAO() {
		return (ProcessingDirectiveInputFormatUtilDAO)MSTConfiguration.getInstance().getBean("ProcessingDirectiveInputFormatUtilDAO");
	}
	public ProcessingDirectiveInputSetUtilDAO getProcessingDirectiveInputSetUtilDAO() {
		return (ProcessingDirectiveInputSetUtilDAO)MSTConfiguration.getInstance().getBean("ProcessingDirectiveInputSetUtilDAO");
	}
	public FormatDAO getFormatDAO() {
		return (FormatDAO)MSTConfiguration.getInstance().getBean("FormatDAO");
	}
	public ProviderDAO getProviderDAO() {
		return (ProviderDAO)MSTConfiguration.getInstance().getBean("ProviderDAO");
	}
	public ProviderFormatUtilDAO getProviderFormatUtilDAO() {
		return (ProviderFormatUtilDAO)MSTConfiguration.getInstance().getBean("ProviderFormatUtilDAO");
	}
	public SetDAO getSetDAO() {
		return (SetDAO)MSTConfiguration.getInstance().getBean("SetDAO");
	}
	public RecordTypeDAO getRecordTypeDAO() {
		return (RecordTypeDAO)MSTConfiguration.getInstance().getBean("RecordTypeDAO");
	}
	public ResumptionTokenDAO getResumptionTokenDAO() {
		return (ResumptionTokenDAO)MSTConfiguration.getInstance().getBean("ResumptionTokenDAO");
	}
	public XcIdentifierForFrbrElementDAO getXcIdentifierForFrbrElementDAO() {
		return (XcIdentifierForFrbrElementDAO)MSTConfiguration.getInstance().getBean("XcIdentifierForFrbrElementDAO");
	}
	public ErrorCodeDAO getErrorCodeDAO() {
		return (ErrorCodeDAO)MSTConfiguration.getInstance().getBean("ErrorCodeDAO");
	}
	public OaiIdentifierForServiceDAO getOaiIdentifierForServiceDAO() {
		return (OaiIdentifierForServiceDAO)MSTConfiguration.getInstance().getBean("OaiIdentifierForServiceDAO");
	}
	public ServiceDAO getServiceDAO() {
		return (ServiceDAO)MSTConfiguration.getInstance().getBean("ServiceDAO");
	}
	public ServiceInputFormatUtilDAO getServiceInputFormatUtilDAO() {
		return (ServiceInputFormatUtilDAO)MSTConfiguration.getInstance().getBean("ServiceInputFormatUtilDAO");
	}
	public ServiceOutputFormatUtilDAO getServiceOutputFormatUtilDAO() {
		return (ServiceOutputFormatUtilDAO)MSTConfiguration.getInstance().getBean("ServiceOutputFormatUtilDAO");
	}
	public ServiceOutputSetUtilDAO getServiceOutputSetUtilDAO() {
		return (ServiceOutputSetUtilDAO)MSTConfiguration.getInstance().getBean("ServiceOutputSetUtilDAO");
	}
	public GroupDAO getGroupDAO() {
		return (GroupDAO)MSTConfiguration.getInstance().getBean("GroupDAO");
	}
	public GroupPermissionUtilDAO getGroupPermissionUtilDAO() {
		return (GroupPermissionUtilDAO)MSTConfiguration.getInstance().getBean("GroupPermissionUtilDAO");
	}
	public PermissionDAO getPermissionDAO() {
		return (PermissionDAO)MSTConfiguration.getInstance().getBean("PermissionDAO");
	}
	public ServerDAO getServerDAO() {
		return (ServerDAO)MSTConfiguration.getInstance().getBean("ServerDAO");
	}
	public UserDAO getUserDAO() {
		return (UserDAO)MSTConfiguration.getInstance().getBean("UserDAO");
	}
	public UserGroupUtilDAO getUserGroupUtilDAO() {
		return (UserGroupUtilDAO)MSTConfiguration.getInstance().getBean("UserGroupUtilDAO");
	}
	public RecordDAO getRecordDAO() {
		return (RecordDAO)MSTConfiguration.getInstance().getBean("DBRecordDAO");
	}
	public RepositoryDAO getRepositoryDAO() {
		return (RepositoryDAO)MSTConfiguration.getInstance().getBean("RepositoryDAO");
	}
	public RepositoryService getRepositoryService() {
		return (RepositoryService)MSTConfiguration.getInstance().getBean("RepositoryService");
	}
}
