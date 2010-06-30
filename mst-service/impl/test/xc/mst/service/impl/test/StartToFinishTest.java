package xc.mst.service.impl.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.constants.Status;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkerThread;
import xc.mst.services.MetadataService;
import xc.mst.services.MetadataServiceManager;
import xc.mst.services.impl.service.SolrIndexService;
import xc.mst.utils.MSTConfiguration;

public class StartToFinishTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	protected Provider provider = null;
	protected String serviceName = "example";
	protected String repoName = "Test Repository";
	
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
		
		indexHarvestedRecords();
		LOG.debug("after indexHarvestedRecords");
		indexServicedRecords();
		LOG.debug("after indexServicedRecords");
		
		Record r = getRepositoryService().getRecord(1999);
		assert r.getPredecessors().get(0).getId() == 999;
		
		r = getRepositoryService().getRecord(999);
		assert r.getSuccessors().get(0).getId() == 1999;
	}
	
	public void dropOldSchemas() {
		try {
			repositoryDAO.deleteSchema(repoName);
			repositoryDAO.deleteSchema(serviceName);
		} catch (Throwable t) {
			
		}
	}
	
	public void installProvider() throws Exception {
		System.setProperty("source.encoding", "UTF-8");
		   
		provider = new Provider();

		provider.setName(repoName);
		provider.setDescription("Repository used in TestNG tests");
		provider.setOaiProviderUrl("http://geolib.geo.auth.gr/digeo/index.php/index/oai");
		provider.setCreatedAt(new java.util.Date());
		providerService.insertProvider(provider);
		validateRepository.validate(provider.getId());
		
		Repository repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
        repo.setName(provider.getName());
		
		// Make sure we got the correct sets for the repository
		assert setDAO.getSetsForProvider(provider.getId()).size() == 5 : "Expected 5 sets, but found " + setDAO.getSetsForProvider(provider.getId()).size() + " sets.";

		// TODO:  Make the following test for sets work without encoding problems.
		
		//List<Set> sets = new DefaultSetDAO().getSetsForProvider(provider.getId());
		//java.util.Set<String> setNames = new HashSet<String>();
		//for(Set set : sets)
			//setNames.add(set.getDisplayName());
		
		//assert setNames.contains("\u00ce\u0178\u00cf\ufffd\u00cf\u2026\u00ce\u00ba\u00cf\u201e\u00cf\u0152\u00cf\u201a \u00ce\u00a0\u00ce\u00bb\u00ce\u00bf\u00cf\ufffd\u00cf\u201e\u00ce\u00bf\u00cf\u201a") : "The set \u00ce\u0178\u00cf\ufffd\u00cf\u2026\u00ce\u00ba\u00cf\u201e\u00cf\u0152\u00cf\u201a \u00ce\u00a0\u00ce\u00bb\u00ce\u00bf\u00cf\ufffd\u00cf\u201e\u00ce\u00bf\u00cf\u201a was expected but not found.";
		//assert setNames.contains("\u00ce\u2020\u00cf\ufffd\u00ce\u00b8\u00cf\ufffd\u00ce\u00b1") : "The set \u00ce\u2020\u00cf\ufffd\u00ce\u00b8\u00cf\ufffd\u00ce\u00b1 was expected but not found.";
		//assert setNames.contains("\u00ce\u201d\u00ce\u00b5\u00ce\u00bb\u00cf\u201e\u00ce\u00af\u00ce\u00bf\u00ce\u00bd \u00cf\u201e\u00ce\u00b7\u00cf\u201a \u00ce\u2022\u00ce\u00bb\u00ce\u00bb\u00ce\u00b7\u00ce\u00bd\u00ce\u00b9\u00ce\u00ba\u00ce\u00ae\u00cf\u201a \u00ce\u201c\u00ce\u00b5\u00cf\u2030\u00ce\u00bb\u00ce\u00bf\u00ce\u00b3\u00ce\u00b9\u00ce\u00ba\u00ce\u00ae\u00cf\u201a \u00ce\u2022\u00cf\u201e\u00ce\u00b1\u00ce\u00b9\u00cf\ufffd\u00ce\u00af\u00ce\u00b1\u00cf\u201a") : "The set \u00ce\u201d\u00ce\u00b5\u00ce\u00bb\u00cf\u201e\u00ce\u00af\u00ce\u00bf\u00ce\u00bd \u00cf\u201e\u00ce\u00b7\u00cf\u201a \u00ce\u2022\u00ce\u00bb\u00ce\u00bb\u00ce\u00b7\u00ce\u00bd\u00ce\u00b9\u00ce\u00ba\u00ce\u00ae\u00cf\u201a \u00ce\u201c\u00ce\u00b5\u00cf\u2030\u00ce\u00bb\u00ce\u00bf\u00ce\u00b3\u00ce\u00b9\u00ce\u00ba\u00ce\u00ae\u00cf\u201a \u00ce\u2022\u00cf\u201e\u00ce\u00b1\u00ce\u00b9\u00cf\ufffd\u00ce\u00af\u00ce\u00b1\u00cf\u201a was expected but not found.";
		//assert setNames.contains("\u00ce\u2020\u00cf\ufffd\u00ce\u00b8\u00cf\ufffd\u00ce\u00b1") : "The set \u00ce\u2020\u00cf\ufffd\u00ce\u00b8\u00cf\ufffd\u00ce\u00b1 was expected but not found.";
		//assert setNames.contains("\u00ce\u2022\u00cf\u2026\u00cf\ufffd\u00ce\u00b5\u00cf\u201e\u00ce\u00ae\u00cf\ufffd\u00ce\u00b9\u00ce\u00b1") : "The set \u00ce\u2022\u00cf\u2026\u00cf\ufffd\u00ce\u00b5\u00cf\u201e\u00ce\u00ae\u00cf\ufffd\u00ce\u00b9\u00ce\u00b1 was expected but not found.";
		
		// Make sure we got the correct formats for the repository
		List<Format> formats = formatDAO.getFormatsForProvider(provider.getId());
		java.util.Set<String> formatNames = new HashSet<String>();
		for(Format format : formats)
			formatNames.add(format.getName());
		
		assert formatNames.contains("oai_dc") : "The format oai_dc was expected but not found.";
		assert formatNames.contains("oai_marc") : "The format oai_marc was expected but not found.";
		assert formatNames.contains("marcxml") : "The format marcxml was expected but not found.";
		assert formatNames.contains("rfc1807") : "The format rfc1807 was expected but not found.";
	}
	
	public void installService() throws Exception {
		getServicesService().addNewService(serviceName);
	}

	public void configureProcessingRules() throws Exception {
		Format f = new Format();
		f.setName("oai_dc");
		f.setNamespace("http://www.openarchives.org/OAI/2.0/oai_dc/");
		f.setSchemaLocation("http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
		formatDAO.insert(f);
		
		Set s = new Set();
		s.setDisplayName("Test-Repository");
		s.setSetSpec("Test-Repository");
		s.setIsProviderSet(false);
		s.setIsRecordSet(true);
		setDAO.insert(s);
		
		s = new Set();
		s.setDisplayName("example-out");
		s.setSetSpec("example-out");
		s.setIsProviderSet(false);
		s.setIsRecordSet(true);
		setDAO.insert(s);
		
		Service service = getServicesService().getServiceByName(serviceName);
		ProcessingDirective pd = new ProcessingDirective();
		pd.setService(service);
		pd.setSourceProvider(provider);
		List<Format> formats = new ArrayList<Format>();
		formats.add(formatDAO.getByName("oai_dc"));
		pd.setTriggeringFormats(formats);
		List<Set> sets = new ArrayList<Set>();
		sets.add(setDAO.getBySetSpec("Test-Repository"));
		pd.setTriggeringSets(sets);
		pd.setOutputSet(s);
		processingDirectiveDAO.insert(pd);
	}
	
	public void createHarvestSchedule() throws Exception {
		HarvestSchedule schedule = new HarvestSchedule();
		Calendar nowCal = Calendar.getInstance();
        schedule.setScheduleName("Test Schedule Name");
        schedule.setDayOfWeek(nowCal.get(Calendar.DAY_OF_WEEK));

        schedule.addFormat(formatDAO.getByName("oai_dc"));
        
        schedule.setHour(nowCal.get(Calendar.HOUR_OF_DAY));
        schedule.setId(111);
        schedule.setMinute(nowCal.get(Calendar.MINUTE));
        schedule.setProvider(provider);
        schedule.setRecurrence("Daily");
        schedule.setStartDate(java.sql.Date.valueOf("2009-05-01"));

        scheduleService.insertSchedule(schedule);
	}
	
	public void waitUntilFinished() {
		int timesNotRunning = 0;
		while (true) {
			try {
				Thread.sleep(1000);
				if (scheduler.getRunningJob() == null || Status.RUNNING != scheduler.getRunningJob().getJobStatus()) {
					timesNotRunning++;
				} else {
					timesNotRunning = 0;
				}
				if (timesNotRunning > 5) {
					break;
				}
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
					((SolrIndexService)solrIndexService).setProvider2index(p);
					((SolrIndexService)solrIndexService).setService2index(null);
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
					((SolrIndexService)solrIndexService).setProvider2index(null);
					((SolrIndexService)solrIndexService).setService2index(s);
					runningJob.run();
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
