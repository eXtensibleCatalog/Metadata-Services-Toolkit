package xc.mst.service.impl.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import org.testng.annotations.Test;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.constants.Status;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkerThread;
import xc.mst.services.MetadataService;
import xc.mst.services.MetadataServiceManager;
import xc.mst.services.impl.service.SolrIndexService;
import xc.mst.utils.MSTConfiguration;

public abstract class StartToFinishTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	protected Provider provider = null;
	
	protected abstract String getServiceName();
	protected abstract String getRepoName();
	protected abstract String getProviderUrl();
	protected abstract Format getIncomingFormat() throws Exception;
	protected void testProvider() throws Exception {}
	protected abstract void finalTest() throws Exception;
	
	protected Repository getServiceRepository() throws Exception {
		return getServicesService().getServiceByName(getServiceName()).getMetadataService().getRepository();
	}
	
	protected Repository getHarvestRepository() throws Exception {
		Repository r = (Repository)MSTConfiguration.getInstance().getBean("Repository");
		r.setName(getRepoName());
		return r;
	}
	
	
	protected Format getDCFormat() throws Exception {
		return getFormat(new String[] {"oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/", "http://www.openarchives.org/OAI/2.0/oai_dc.xsd"});
	}
	
	protected Format getOaiMarcFormat() throws Exception {
		return getFormat(new String[] {"oai_marc", "http://www.openarchives.org/OAI/1.1/oai_marc", "http://www.openarchives.org/OAI/1.1/oai_marc.xsd"});
	}

	protected Format getMarcXmlFormat() throws Exception {
		return getFormat(new String[] {"marcxml", "http://www.loc.gov/MARC21/slim", "http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd"});
	}

	protected Format getRfc1807Format() throws Exception {
		return getFormat(new String[] {"rfc1807", "http://info.internet.isi.edu:80/in-notes/rfc/files/rfc1807.txt", "http://www.openarchives.org/OAI/1.1/rfc1807.xsd"});
	}

	protected Format getModsFormat() throws Exception {
		return getFormat(new String[] {"mods", "http://www.loc.gov/mods/v3", "http://www.loc.gov/standards/mods/v3/mods-3-0.xsd"});
	}
	
	protected Format getHtmlFormat() throws Exception {
		return getFormat(new String[] {"html", "http://www.w3.org/TR/REC-html40", "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd"});
	}

	protected Format getFormat(String[] arr) throws Exception {
		Format f = formatDAO.getByName(arr[0]);
		if (f == null) {
			f = new Format();
			f.setName(arr[0]);
			f.setNamespace(arr[1]);
			f.setSchemaLocation(arr[2]);
			formatDAO.insert(f);
			f = formatDAO.getByName(arr[0]);
		}
		return f;
	}

	@Test
	public void startToFinish() throws Exception  {
		printClassPath();
		
		dropOldSchemas();
		LOG.debug("after dropOldSchemas");
		installProvider();
		LOG.debug("after installProvider");
		installService();
		LOG.debug("after installService");

		configureProcessingRules();
		LOG.debug("after configureProcessingRules");
		createHarvestSchedule();
		LOG.debug("after createHarvestSchedule");

		waitUntilFinished();
		LOG.debug("after waitUntilFinished");
		
		finalTest();
		
		/*
		indexHarvestedRecords();
		LOG.debug("after indexHarvestedRecords");
		indexServicedRecords();
		LOG.debug("after indexServicedRecords");
		
		LOG.debug("after finalTest");
		
		Thread.sleep(60000);
		createHarvestSchedule();
		LOG.debug("after createHarvestSchedule");

		waitUntilFinished();
		LOG.debug("after waitUntilFinished");
		*/
	}
	
	public void dropOldSchemas() {
		try {
			repositoryDAO.deleteSchema(getRepoName());
			repositoryDAO.deleteSchema(getServiceName());
		} catch (Throwable t) {
			
		}
	}
	
	public void installProvider() throws Exception {
		System.setProperty("source.encoding", "UTF-8");
		   
		provider = new Provider();

		provider.setName(getRepoName());
		provider.setDescription("Repository used in TestNG tests");
		provider.setOaiProviderUrl(getProviderUrl());
		provider.setCreatedAt(new java.util.Date());
		providerService.insertProvider(provider);
		validateRepository.validate(provider.getId());
		
		repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
        repo.setName(provider.getName());
		
        testProvider();
	}
	
	public void installService() throws Exception {
		getServicesService().addNewService(getServiceName());
	}

	public void configureProcessingRules() throws Exception {
		Set s = new Set();
		s.setDisplayName(getRepoName());
		s.setSetSpec(getRepoName());
		s.setIsProviderSet(false);
		s.setIsRecordSet(true);
		setDAO.insert(s);
		
		s = new Set();
		s.setDisplayName(getServiceName()+"-out");
		s.setSetSpec(getServiceName()+"-out");
		s.setIsProviderSet(false);
		s.setIsRecordSet(true);
		setDAO.insert(s);
		
		Service service = getServicesService().getServiceByName(getServiceName());
		ProcessingDirective pd = new ProcessingDirective();
		pd.setService(service);
		pd.setSourceProvider(provider);
		List<Format> formats = new ArrayList<Format>();
		formats.add(getIncomingFormat());
		pd.setTriggeringFormats(formats);
		List<Set> sets = new ArrayList<Set>();
		sets.add(setDAO.getBySetSpec(getRepoName()));
		pd.setTriggeringSets(sets);
		pd.setOutputSet(s);
		processingDirectiveDAO.insert(pd);
	}
	
	public void createHarvestSchedule() throws Exception {
		HarvestSchedule schedule = new HarvestSchedule();
		Calendar nowCal = Calendar.getInstance();
        schedule.setScheduleName("Test Schedule Name");
        schedule.setDayOfWeek(nowCal.get(Calendar.DAY_OF_WEEK));

        schedule.addFormat(getIncomingFormat());
        
        schedule.setHour(nowCal.get(Calendar.HOUR_OF_DAY));
        schedule.setId(111);
        schedule.setMinute(nowCal.get(Calendar.MINUTE));
        schedule.setProvider(provider);
        schedule.setRecurrence("Daily");
        schedule.setStartDate(java.sql.Date.valueOf("2009-05-01"));

        scheduleService.insertSchedule(schedule);
	}
	
	public void updateHarvestSchedule() throws Exception {
		Calendar nowCal = Calendar.getInstance();
		HarvestSchedule schedule = scheduleService.getScheduleById(1);
		schedule.setMinute(nowCal.get(Calendar.MINUTE));
		scheduleService.updateSchedule(schedule);
	}
	
	public void waitUntilFinished() {
		int timesNotRunning = 0;
		while (true) {
			LOG.debug("checking to see if finished");
			try {
				Thread.sleep(1000);
				Date lastModified = getRepositoryService().getLastModified();
				LOG.debug("lastModified :"+lastModified);
				if (lastModified != null && lastModified.after(new Date())) {
					LOG.debug("Future dated!");
					continue;
				}
				if (scheduler.getRunningJob() == null || 
						Status.RUNNING != scheduler.getRunningJob().getJobStatus()) {
					timesNotRunning++;
				} else {
					timesNotRunning = 0;
				}
				if (timesNotRunning > 7) {
					break;
				}
				LOG.debug("timeNotRunning: "+timesNotRunning);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			
		}
	}
	
	public void indexHarvestedRecords() {
		try {
			List<Provider> providers = getProviderDAO().getAll();
			if (providers != null) {
				for (Provider p : providers) {
					Repository repo = getRepositoryService().getRepository(p);
					WorkerThread runningJob = new WorkerThread();
					MetadataServiceManager msm = new MetadataServiceManager();
					runningJob.setWorkDelegate(msm);
					MetadataService solrIndexService = (MetadataService)MSTConfiguration.getInstance().getBean("SolrIndexService");
					Service s = new Service();
					s.setName(p.getName()+"-solr-indexer");
					solrIndexService.setService(s);
					msm.setMetadataService(solrIndexService);
					msm.setIncomingRepository(repo);
					repo.setProvider(p);
					runningJob.run();
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	public void indexServicedRecords() {
		try {
			List<Service> services = getServicesService().getAllServices();
			if (services != null) {
				for (Service s : services) {
					Repository repo = s.getMetadataService().getRepository();
					WorkerThread runningJob = new WorkerThread();
					MetadataServiceManager msm = new MetadataServiceManager();
					runningJob.setWorkDelegate(msm);
					MetadataService solrIndexService = (MetadataService)MSTConfiguration.getInstance().getBean("SolrIndexService");
					Service s2 = new Service();
					s2.setName(s.getName()+"-solr-indexer");
					solrIndexService.setService(s2);
					msm.setMetadataService(solrIndexService);
					msm.setIncomingRepository(repo);
					repo.setService(s2);
					runningJob.run();
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
