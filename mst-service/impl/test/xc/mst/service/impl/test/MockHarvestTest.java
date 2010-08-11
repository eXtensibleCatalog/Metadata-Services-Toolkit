package xc.mst.service.impl.test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.testng.annotations.Test;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;
import xc.mst.harvester.HarvestManager;
import xc.mst.oai.Facade;
import xc.mst.oai.OaiRequestBean;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkerThread;
import xc.mst.services.impl.GenericMetadataService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;
import xc.mst.utils.XmlHelper;


public abstract class MockHarvestTest extends StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(MockHarvestTest.class);
	
	protected HarvestManager harvestManager = null;
	protected Date harvestOutFrom = null;
	protected Date harvestOutUntil = null;
	protected XmlHelper xmlHelper = new XmlHelper();

	@Test
	public void startToFinish() throws Exception  {
		try {
			dropOldSchemas();
			LOG.debug("after dropOldSchemas");
			installProvider();
			LOG.debug("after installProvider");
			installService();
			LOG.debug("after installService");

			createHarvestSchedule();
			LOG.debug("after createHarvestSchedule");
			
			Date previousLastModified = null;
			while (true) {
				WorkerThread wt = new WorkerThread();
				wt.setWorkDelegate(harvestManager);
				wt.run();
				LOG.debug("previousFileLastModified: "+previousLastModified);
				LOG.debug("repo.getLastModified(): "+repo.getLastModified());
				
				if (previousLastModified != null && previousLastModified.equals(repo.getLastModified())) {
					break;
				} else {
					previousLastModified = repo.getLastModified();
				}
				
				Service s = getServicesService().getServiceByName(getServiceName());
				GenericMetadataService ms = (GenericMetadataService)s.getMetadataService();
				
				if (repo instanceof DefaultRepository) {
					((DefaultRepository)repo).sleepUntilReady();
				}
				
				ms.process(repo, null, null, null);
				
				if (ms.getRepository() instanceof DefaultRepository) {
					((DefaultRepository)ms.getRepository()).sleepUntilReady();
				}
				
				harvestOutRecordsFromMST();
			}
				
			finalTest();
			
		} catch (Throwable t) {
			util.throwIt(t);
		}
	}
	
	public abstract String getFolder();
	
	public void installProvider() throws Exception {
		provider = new Provider();

		provider.setName(getRepoName());
		provider.setDescription("Repository used in TestNG tests");
		provider.setOaiProviderUrl(getProviderUrl());
		provider.setCreatedAt(new java.util.Date());
		providerService.insertProvider(provider);
		
		repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
        repo.setName(provider.getName());
		repo.setProvider(provider);
	}
	
	public void createHarvestSchedule() throws Exception {
		HarvestSchedule schedule = new HarvestSchedule();
		Calendar nowCal = Calendar.getInstance();
        schedule.setScheduleName("Test Schedule Name");
        schedule.setDayOfWeek(nowCal.get(Calendar.DAY_OF_WEEK));

        schedule.addFormat(null);
        schedule.setHour(nowCal.get(Calendar.HOUR_OF_DAY));
        schedule.setId(111);
        schedule.setMinute(nowCal.get(Calendar.MINUTE));
        schedule.setProvider(provider);
        schedule.setRecurrence("Daily");
        schedule.setStartDate(java.sql.Date.valueOf("2009-05-01"));
		
		harvestManager = (HarvestManager)MSTConfiguration.getInstance().getBean("HarvestManager");
		HarvestScheduleStep hss = new HarvestScheduleStep();
		hss.setFormat(null);
		hss.setSet(null);
		hss.setSchedule(schedule);
		schedule.addStep(hss);
		harvestManager.setHarvestSchedule(schedule);
	}
	
	public void harvestOutRecordsFromMST() throws Exception {
		if (harvestOutUntil != null) {
			harvestOutFrom = harvestOutUntil;
		} else {
			harvestOutFrom = new Date();
			harvestOutFrom.setYear(60);
		}
		harvestOutUntil = new Date();
		
		LOG.debug("harvestOutFrom: "+harvestOutFrom);
		LOG.debug("harvestOutUntil: "+harvestOutUntil);

		OaiRequestBean bean = new OaiRequestBean();

		// Set parameters on the bean based on the OAI request's parameters
		bean.setVerb("ListRecords");
		bean.setMetadataPrefix(getHarvestOutFormat().getName());
		bean.setFrom(harvestManager.printDate(harvestOutFrom));
		bean.setUntil(harvestManager.printDate(harvestOutUntil));
		
		Service service = getServicesService().getServiceByName(getServiceName());
		bean.setServiceId(service.getId());

		Facade facade = (Facade) MSTConfiguration.getInstance().getBean("Facade");
		
		StringBuilder stringBuilder = new StringBuilder();
		
		LOG.debug("provider.getLastOaiRequest(): "+provider.getLastOaiRequest());
		stringBuilder.append(facade.execute(bean));
		
		harvestOutResponse = stringBuilder.toString();
		Document doc = xmlHelper.getJDomDocument(harvestOutResponse);
		harvestOutResponse = xmlHelper.getStringPretty(doc.getRootElement());
		
		LOG.debug("XML response");
		LOG.debug(harvestOutResponse);
		File outputFolder = new File("test/mock_harvest_actual_output_records/"+getFolder());
		if (!outputFolder.exists()) {
			outputFolder.mkdir();
		}
		Util.getUtil().spit("test/mock_harvest_actual_output_records/"+getFolder()+"/"+provider.getLastOaiRequest(), harvestOutResponse);
	}

}
