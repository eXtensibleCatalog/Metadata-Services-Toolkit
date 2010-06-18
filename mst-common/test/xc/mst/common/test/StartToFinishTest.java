package xc.mst.common.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.testng.annotations.Test;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.Service;
import xc.mst.constants.Status;
import xc.mst.repo.Repository;
import xc.mst.utils.MSTConfiguration;

public class StartToFinishTest extends BaseTest {
	
	protected Provider provider = null;
	protected String serviceName = "example";
	
	@Test
	public void startToFinish() throws Exception  {
		printClassPath();
		
		installProvider();
		installService();
		configureProcessingRules();
		createHarvestSchedule();
		waitUntilFinished();
		// wait until it is finished
		// walk through all records (put the implementation in a service and repository)
		//   - 
	    //   - each service
	    //   - each harvest schedule step
	    // for a record or small set of records (add the interface to MetadataService)
		//   - at first I thought this wouldn't actually test what we need it to since we have only
		//     one service, but actually it will.  If you select a record from the harvest, then we'll
		//     need to ask the example service if it has any successors for that record
	    //   - inject successor, predecessor
	}
	
	public void installProvider() throws Exception {
		System.setProperty("source.encoding", "UTF-8");
		   
		provider = new Provider();

		provider.setName("Test Repository");
		provider.setDescription("Repository used in TestNG tests");
		provider.setOaiProviderUrl("http://geolib.geo.auth.gr/digeo/index.php/index/oai");
		provider.setCreatedAt(new java.util.Date());
		providerService.insertProvider(provider);
		validateRepository.validate(provider.getId());
		
		repositoryDAO.createSchema(provider.getName(), true);
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
		servicesService.addNewService(serviceName);
	}

	public void configureProcessingRules() throws Exception {
		Service service = servicesService.getServiceByName(serviceName);
		ProcessingDirective pd = new ProcessingDirective();
		pd.setService(service);
		pd.setSourceProvider(provider);
		List<Format> formats = new ArrayList<Format>();
		formats.add(formatDAO.getByName("oai_dc"));
		pd.setTriggeringFormats(formats);
		List<Set> sets = new ArrayList<Set>();
		sets.add(setDAO.getBySetSpec("Test-Repository"));
		pd.setTriggeringSets(sets);
		//pd.setOutputSet(sets);
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
		while (true) {
			try {
				Thread.sleep(5000);
				if (Status.RUNNING != scheduler.getRunningJob().getJobStatus()) {
					break;
				}
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			
		}
	}

}
