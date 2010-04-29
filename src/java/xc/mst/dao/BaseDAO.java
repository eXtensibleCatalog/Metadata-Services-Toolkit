package xc.mst.dao;

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
import xc.mst.dao.record.DBRecordDAO;
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

public class BaseDAO {
	public RecordService getRecordService() {
		return (RecordService)MSTConfiguration.getBean("RecordService");
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
	public DBRecordDAO getRecordDAO() {
		return (DBRecordDAO)MSTConfiguration.getBean("DBRecordDAO");
	}
}
