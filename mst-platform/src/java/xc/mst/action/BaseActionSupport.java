/**
  * Copyright (c) 2009 University of Rochester
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
import xc.mst.utils.MSTConfiguration;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class BaseActionSupport extends ActionSupport {

	public EmailConfigService getEmailConfigService() {
		return (EmailConfigService)MSTConfiguration.getBean("EmailConfigService");
	}
	public ScheduleService getScheduleService() {
		return (ScheduleService)MSTConfiguration.getBean("ScheduleService");
	}
	public LogService getLogService() {
		return (LogService)MSTConfiguration.getBean("LogService");
	}
	public JobService getJobService() {
		return (JobService)MSTConfiguration.getBean("JobService");
	}
	public ProcessingDirectiveService getProcessingDirectiveService() {
		return (ProcessingDirectiveService)MSTConfiguration.getBean("ProcessingDirectiveService");
	}
	public ServicesService getServicesService() {
		return (ServicesService)MSTConfiguration.getBean("ServicesService");
	}
	public BrowseRecordService getBrowseRecordService() {
		return (BrowseRecordService)MSTConfiguration.getBean("BrowseRecordService");
	}
	public ExpressionService getExpressionService() {
		return (ExpressionService)MSTConfiguration.getBean("ExpressionService");
	}
	public HoldingsService getHoldingsService() {
		return (HoldingsService)MSTConfiguration.getBean("HoldingsService");
	}
	public ItemService getItemService() {
		return (ItemService)MSTConfiguration.getBean("ItemService");
	}
	public ManifestationService getManifestationService() {
		return (ManifestationService)MSTConfiguration.getBean("ManifestationService");
	}
	public WorkService getWorkService() {
		return (WorkService)MSTConfiguration.getBean("WorkService");
	}
	public FormatService getFormatService() {
		return (FormatService)MSTConfiguration.getBean("FormatService");
	}
	public ProviderService getProviderService() {
		return (ProviderService)MSTConfiguration.getBean("ProviderService");
	}
	public SetService getSetService() {
		return (SetService)MSTConfiguration.getBean("SetService");
	}
	public GroupService getGroupService() {
		return (GroupService)MSTConfiguration.getBean("GroupService");
	}
	public PermissionService getPermissionService() {
		return (PermissionService)MSTConfiguration.getBean("PermissionService");
	}
	public ServerService getServerService() {
		return (ServerService)MSTConfiguration.getBean("ServerService");
	}
	public UserGroupUtilService getUserGroupUtilService() {
		return (UserGroupUtilService)MSTConfiguration.getBean("UserGroupUtilService");
	}
	public UserService getUserService() {
		return (UserService)MSTConfiguration.getBean("UserService");
	}
	public RecordService getRecordService() {
		return (RecordService)MSTConfiguration.getBean("RecordService");
	}
	public EmailConfigDAO getEmailConfigDAO() {
		return (EmailConfigDAO)MSTConfiguration.getBean("EmailConfigDAO");
	}
	public HarvestDAO getHarvestDAO() {
		return (HarvestDAO)MSTConfiguration.getBean("HarvestDAO");
	}
	public HarvestRecordUtilDAO getHarvestRecordUtilDAO() {
		return (HarvestRecordUtilDAO)MSTConfiguration.getBean("HarvestRecordUtilDAO");
	}
	public HarvestScheduleDAO getHarvestScheduleDAO() {
		return (HarvestScheduleDAO)MSTConfiguration.getBean("HarvestScheduleDAO");
	}
	public HarvestScheduleStepDAO getHarvestScheduleStepDAO() {
		return (HarvestScheduleStepDAO)MSTConfiguration.getBean("HarvestScheduleStepDAO");
	}
	public LogDAO getLogDAO() {
		return (LogDAO)MSTConfiguration.getBean("LogDAO");
	}
	public JobDAO getJobDAO() {
		return (JobDAO)MSTConfiguration.getBean("JobDAO");
	}
	public ProcessingDirectiveDAO getProcessingDirectiveDAO() {
		return (ProcessingDirectiveDAO)MSTConfiguration.getBean("ProcessingDirectiveDAO");
	}
	public ProcessingDirectiveInputFormatUtilDAO getProcessingDirectiveInputFormatUtilDAO() {
		return (ProcessingDirectiveInputFormatUtilDAO)MSTConfiguration.getBean("ProcessingDirectiveInputFormatUtilDAO");
	}
	public ProcessingDirectiveInputSetUtilDAO getProcessingDirectiveInputSetUtilDAO() {
		return (ProcessingDirectiveInputSetUtilDAO)MSTConfiguration.getBean("ProcessingDirectiveInputSetUtilDAO");
	}
	public FormatDAO getFormatDAO() {
		return (FormatDAO)MSTConfiguration.getBean("FormatDAO");
	}
	public ProviderDAO getProviderDAO() {
		return (ProviderDAO)MSTConfiguration.getBean("ProviderDAO");
	}
	public ProviderFormatUtilDAO getProviderFormatUtilDAO() {
		return (ProviderFormatUtilDAO)MSTConfiguration.getBean("ProviderFormatUtilDAO");
	}
	public SetDAO getSetDAO() {
		return (SetDAO)MSTConfiguration.getBean("SetDAO");
	}
	public RecordTypeDAO getRecordTypeDAO() {
		return (RecordTypeDAO)MSTConfiguration.getBean("RecordTypeDAO");
	}
	public ResumptionTokenDAO getResumptionTokenDAO() {
		return (ResumptionTokenDAO)MSTConfiguration.getBean("ResumptionTokenDAO");
	}
	public XcIdentifierForFrbrElementDAO getXcIdentifierForFrbrElementDAO() {
		return (XcIdentifierForFrbrElementDAO)MSTConfiguration.getBean("XcIdentifierForFrbrElementDAO");
	}
	public ErrorCodeDAO getErrorCodeDAO() {
		return (ErrorCodeDAO)MSTConfiguration.getBean("ErrorCodeDAO");
	}
	public OaiIdentifierForServiceDAO getOaiIdentifierForServiceDAO() {
		return (OaiIdentifierForServiceDAO)MSTConfiguration.getBean("OaiIdentifierForServiceDAO");
	}
	public ServiceDAO getServiceDAO() {
		return (ServiceDAO)MSTConfiguration.getBean("ServiceDAO");
	}
	public ServiceInputFormatUtilDAO getServiceInputFormatUtilDAO() {
		return (ServiceInputFormatUtilDAO)MSTConfiguration.getBean("ServiceInputFormatUtilDAO");
	}
	public ServiceOutputFormatUtilDAO getServiceOutputFormatUtilDAO() {
		return (ServiceOutputFormatUtilDAO)MSTConfiguration.getBean("ServiceOutputFormatUtilDAO");
	}
	public ServiceOutputSetUtilDAO getServiceOutputSetUtilDAO() {
		return (ServiceOutputSetUtilDAO)MSTConfiguration.getBean("ServiceOutputSetUtilDAO");
	}
	public GroupDAO getGroupDAO() {
		return (GroupDAO)MSTConfiguration.getBean("GroupDAO");
	}
	public GroupPermissionUtilDAO getGroupPermissionUtilDAO() {
		return (GroupPermissionUtilDAO)MSTConfiguration.getBean("GroupPermissionUtilDAO");
	}
	public PermissionDAO getPermissionDAO() {
		return (PermissionDAO)MSTConfiguration.getBean("PermissionDAO");
	}
	public ServerDAO getServerDAO() {
		return (ServerDAO)MSTConfiguration.getBean("ServerDAO");
	}
	public UserDAO getUserDAO() {
		return (UserDAO)MSTConfiguration.getBean("UserDAO");
	}
	public UserGroupUtilDAO getUserGroupUtilDAO() {
		return (UserGroupUtilDAO)MSTConfiguration.getBean("UserGroupUtilDAO");
	}
	public RecordDAO getRecordDAO() {
		return (RecordDAO)MSTConfiguration.getBean("DBRecordDAO");
	}
}
