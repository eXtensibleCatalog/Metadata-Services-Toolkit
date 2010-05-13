/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.manager;

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
import xc.mst.dao.record.RecordDAO;
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
import xc.mst.manager.record.MSTSolrServer;
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
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class BaseService {

	protected Util util = null;
	protected EmailConfigDAO emailConfigDAO = null;
	protected HarvestDAO harvestDAO = null;
	protected HarvestRecordUtilDAO harvestRecordUtilDAO = null;
	protected HarvestScheduleDAO harvestScheduleDAO = null;
	protected HarvestScheduleStepDAO harvestScheduleStepDAO = null;
	protected LogDAO logDAO = null;
	protected JobDAO jobDAO = null;
	protected ProcessingDirectiveDAO processingDirectiveDAO = null;
	protected ProcessingDirectiveInputFormatUtilDAO processingDirectiveInputFormatUtilDAO = null;
	protected ProcessingDirectiveInputSetUtilDAO processingDirectiveInputSetUtilDAO = null;
	protected FormatDAO formatDAO = null;
	protected ProviderDAO providerDAO = null;
	protected ProviderFormatUtilDAO providerFormatUtilDAO = null;
	protected SetDAO setDAO = null;
	protected RecordTypeDAO recordTypeDAO = null;
	protected ResumptionTokenDAO resumptionTokenDAO = null;
	protected XcIdentifierForFrbrElementDAO xcIdentifierForFrbrElementDAO = null;
	protected ErrorCodeDAO errorCodeDAO = null;
	protected OaiIdentifierForServiceDAO oaiIdentifierForServiceDAO = null;
	protected ServiceDAO serviceDAO = null;
	protected ServiceInputFormatUtilDAO serviceInputFormatUtilDAO = null;
	protected ServiceOutputFormatUtilDAO serviceOutputFormatUtilDAO = null;
	protected ServiceOutputSetUtilDAO serviceOutputSetUtilDAO = null;
	protected GroupDAO groupDAO = null;
	protected GroupPermissionUtilDAO groupPermissionUtilDAO = null;
	protected PermissionDAO permissionDAO = null;
	protected ServerDAO serverDAO = null;
	protected UserDAO userDAO = null;
	protected UserGroupUtilDAO userGroupUtilDAO = null;
	protected RecordDAO recordDAO = null;
	protected RepositoryDAO repositoryDAO = null;
	
	public Util getUtil() {
		return util;
	}
	public void setUtil(Util util) {
		this.util = util;
	}

	public EmailConfigDAO getEmailConfigDAO() {
		return emailConfigDAO;
	}

	public void setEmailConfigDAO(EmailConfigDAO emailConfigDAO) {
		this.emailConfigDAO = emailConfigDAO;
	}

	public HarvestDAO getHarvestDAO() {
		return harvestDAO;
	}

	public void setHarvestDAO(HarvestDAO harvestDAO) {
		this.harvestDAO = harvestDAO;
	}

	public HarvestRecordUtilDAO getHarvestRecordUtilDAO() {
		return harvestRecordUtilDAO;
	}

	public void setHarvestRecordUtilDAO(HarvestRecordUtilDAO harvestRecordUtilDAO) {
		this.harvestRecordUtilDAO = harvestRecordUtilDAO;
	}

	public HarvestScheduleDAO getHarvestScheduleDAO() {
		return harvestScheduleDAO;
	}

	public void setHarvestScheduleDAO(HarvestScheduleDAO harvestScheduleDAO) {
		this.harvestScheduleDAO = harvestScheduleDAO;
	}

	public HarvestScheduleStepDAO getHarvestScheduleStepDAO() {
		return harvestScheduleStepDAO;
	}

	public void setHarvestScheduleStepDAO(
			HarvestScheduleStepDAO harvestScheduleStepDAO) {
		this.harvestScheduleStepDAO = harvestScheduleStepDAO;
	}

	public LogDAO getLogDAO() {
		return logDAO;
	}

	public void setLogDAO(LogDAO logDAO) {
		this.logDAO = logDAO;
	}

	public JobDAO getJobDAO() {
		return jobDAO;
	}

	public void setJobDAO(JobDAO jobDAO) {
		this.jobDAO = jobDAO;
	}

	public ProcessingDirectiveDAO getProcessingDirectiveDAO() {
		return processingDirectiveDAO;
	}

	public void setProcessingDirectiveDAO(
			ProcessingDirectiveDAO processingDirectiveDAO) {
		this.processingDirectiveDAO = processingDirectiveDAO;
	}

	public ProcessingDirectiveInputFormatUtilDAO getProcessingDirectiveInputFormatUtilDAO() {
		return processingDirectiveInputFormatUtilDAO;
	}

	public void setProcessingDirectiveInputFormatUtilDAO(
			ProcessingDirectiveInputFormatUtilDAO processingDirectiveInputFormatUtilDAO) {
		this.processingDirectiveInputFormatUtilDAO = processingDirectiveInputFormatUtilDAO;
	}

	public ProcessingDirectiveInputSetUtilDAO getProcessingDirectiveInputSetUtilDAO() {
		return processingDirectiveInputSetUtilDAO;
	}

	public void setProcessingDirectiveInputSetUtilDAO(
			ProcessingDirectiveInputSetUtilDAO processingDirectiveInputSetUtilDAO) {
		this.processingDirectiveInputSetUtilDAO = processingDirectiveInputSetUtilDAO;
	}

	public FormatDAO getFormatDAO() {
		return formatDAO;
	}

	public void setFormatDAO(FormatDAO formatDAO) {
		this.formatDAO = formatDAO;
	}

	public ProviderDAO getProviderDAO() {
		return providerDAO;
	}

	public void setProviderDAO(ProviderDAO providerDAO) {
		this.providerDAO = providerDAO;
	}

	public ProviderFormatUtilDAO getProviderFormatUtilDAO() {
		return providerFormatUtilDAO;
	}

	public void setProviderFormatUtilDAO(ProviderFormatUtilDAO providerFormatUtilDAO) {
		this.providerFormatUtilDAO = providerFormatUtilDAO;
	}

	public SetDAO getSetDAO() {
		return setDAO;
	}

	public void setSetDAO(SetDAO setDAO) {
		this.setDAO = setDAO;
	}

	public RecordTypeDAO getRecordTypeDAO() {
		return recordTypeDAO;
	}

	public void setRecordTypeDAO(RecordTypeDAO recordTypeDAO) {
		this.recordTypeDAO = recordTypeDAO;
	}

	public ResumptionTokenDAO getResumptionTokenDAO() {
		return resumptionTokenDAO;
	}

	public void setResumptionTokenDAO(ResumptionTokenDAO resumptionTokenDAO) {
		this.resumptionTokenDAO = resumptionTokenDAO;
	}

	public XcIdentifierForFrbrElementDAO getXcIdentifierForFrbrElementDAO() {
		return xcIdentifierForFrbrElementDAO;
	}

	public void setXcIdentifierForFrbrElementDAO(
			XcIdentifierForFrbrElementDAO xcIdentifierForFrbrElementDAO) {
		this.xcIdentifierForFrbrElementDAO = xcIdentifierForFrbrElementDAO;
	}

	public ErrorCodeDAO getErrorCodeDAO() {
		return errorCodeDAO;
	}

	public void setErrorCodeDAO(ErrorCodeDAO errorCodeDAO) {
		this.errorCodeDAO = errorCodeDAO;
	}

	public OaiIdentifierForServiceDAO getOaiIdentifierForServiceDAO() {
		return oaiIdentifierForServiceDAO;
	}

	public void setOaiIdentifierForServiceDAO(
			OaiIdentifierForServiceDAO oaiIdentifierForServiceDAO) {
		this.oaiIdentifierForServiceDAO = oaiIdentifierForServiceDAO;
	}

	public ServiceDAO getServiceDAO() {
		return serviceDAO;
	}

	public void setServiceDAO(ServiceDAO serviceDAO) {
		this.serviceDAO = serviceDAO;
	}

	public ServiceInputFormatUtilDAO getServiceInputFormatUtilDAO() {
		return serviceInputFormatUtilDAO;
	}

	public void setServiceInputFormatUtilDAO(
			ServiceInputFormatUtilDAO serviceInputFormatUtilDAO) {
		this.serviceInputFormatUtilDAO = serviceInputFormatUtilDAO;
	}

	public ServiceOutputFormatUtilDAO getServiceOutputFormatUtilDAO() {
		return serviceOutputFormatUtilDAO;
	}

	public void setServiceOutputFormatUtilDAO(
			ServiceOutputFormatUtilDAO serviceOutputFormatUtilDAO) {
		this.serviceOutputFormatUtilDAO = serviceOutputFormatUtilDAO;
	}

	public ServiceOutputSetUtilDAO getServiceOutputSetUtilDAO() {
		return serviceOutputSetUtilDAO;
	}

	public void setServiceOutputSetUtilDAO(
			ServiceOutputSetUtilDAO serviceOutputSetUtilDAO) {
		this.serviceOutputSetUtilDAO = serviceOutputSetUtilDAO;
	}

	public GroupDAO getGroupDAO() {
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}

	public GroupPermissionUtilDAO getGroupPermissionUtilDAO() {
		return groupPermissionUtilDAO;
	}

	public void setGroupPermissionUtilDAO(
			GroupPermissionUtilDAO groupPermissionUtilDAO) {
		this.groupPermissionUtilDAO = groupPermissionUtilDAO;
	}

	public PermissionDAO getPermissionDAO() {
		return permissionDAO;
	}

	public void setPermissionDAO(PermissionDAO permissionDAO) {
		this.permissionDAO = permissionDAO;
	}

	public ServerDAO getServerDAO() {
		return serverDAO;
	}

	public void setServerDAO(ServerDAO serverDAO) {
		this.serverDAO = serverDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public UserGroupUtilDAO getUserGroupUtilDAO() {
		return userGroupUtilDAO;
	}

	public void setUserGroupUtilDAO(UserGroupUtilDAO userGroupUtilDAO) {
		this.userGroupUtilDAO = userGroupUtilDAO;
	}

	public RecordDAO getRecordDAO() {
		return recordDAO;
	}

	public void setRecordDAO(RecordDAO recordDAO) {
		this.recordDAO = recordDAO;
	}
	
	public RepositoryDAO getRepositoryDAO() {
		return repositoryDAO;
	}
	public void setRepositoryDAO(RepositoryDAO repositoryDAO) {
		this.repositoryDAO = repositoryDAO;
	}

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
	public MSTSolrServer getMstSolrServer() {
		return (MSTSolrServer)MSTConfiguration.getBean("MSTSolrServer");
	}
}
