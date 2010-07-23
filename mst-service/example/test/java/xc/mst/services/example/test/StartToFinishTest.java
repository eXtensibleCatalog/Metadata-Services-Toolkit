package xc.mst.services.example.test;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;

import xc.mst.bo.provider.Format;
import xc.mst.bo.record.Record;
import xc.mst.repo.DefaultRepository;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
	private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
	
	protected String getServiceName() {
		return "example";
	}
	
	protected String getRepoName() {
		return "test_repo";
	}
	
	protected String getProviderUrl() {
		return "http://geolib.geo.auth.gr/digeo/index.php/index/oai";
	}
	
	protected Format getIncomingFormat() throws Exception {
		return getDCFormat();
	}
	
	
	protected void testProvider() throws Exception {
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
	
	protected void finalTest() throws Exception {
		boolean useAsserts = true;
		Record r = getRepositoryService().getRecord(1999);
		if (useAsserts)
			assert r.getPredecessors().get(0).getId() == 999;
		
		r = getRepositoryService().getRecord(999);
		if (useAsserts) {
			assert r.getSuccessors().get(0).getId() == 1999;
			assert getHarvestRepository().getNumRecords() == 1000;
			assert getServiceRepository().getNumRecords() == 1000;
		}
		
		SolrQuery sq = new SolrQuery("*:*");
		SolrDocumentList sdl = getSolrIndexManager().getDocumentList(sq);
		LOG.debug("sdl.getNumFound(): "+sdl.getNumFound());
		if (useAsserts) {
			assert sdl.getNumFound() == 2000;
		}
		
		verifySets();

		getRepositoryDAO().setAllLastModifiedOais(getHarvestRepository().getName(), new Date(0));
		Date beforeGettingSameRecords = new Date();
		createHarvestSchedule();
		LOG.debug("160");
		waitUntilFinished();
		
		if (useAsserts) {
			assert beforeGettingSameRecords.before(getHarvestRepository().getLastModified()); 
			assert getHarvestRepository().getNumRecords() == 1000;
			assert getServiceRepository().getNumRecords() == 1000;
		}
		verifySets();
		LOG.debug("getHarvestRepository().getNumRecords(): "+getHarvestRepository().getNumRecords());
		LOG.debug("getServiceRepository().getNumRecords(): "+getServiceRepository().getNumRecords());
		sq = new SolrQuery("*:*");
		sdl = getSolrIndexManager().getDocumentList(sq);
		LOG.debug("sdl.getNumFound(): "+sdl.getNumFound());
		if (useAsserts)
			assert sdl.getNumFound() == 2000;
		
		LOG.debug("180");
		LOG.debug("200");
		updateHarvestSchedule();
		LOG.debug("220");
		waitUntilFinished();
		LOG.debug("240");
		
		LOG.debug("getHarvestRepository().getNumRecords(): "+getHarvestRepository().getNumRecords());
		LOG.debug("getServiceRepository().getNumRecords(): "+getServiceRepository().getNumRecords());
		if (useAsserts) {
			assert getHarvestRepository().getNumRecords() > 1000;
			assert getServiceRepository().getNumRecords() > 1000;
		}
		
		sq = new SolrQuery("*:*");
		sdl = getSolrIndexManager().getDocumentList(sq);
		LOG.debug("sdl.getNumFound(): "+sdl.getNumFound());
		if (useAsserts)
			assert sdl.getNumFound() > 2000;
			
	}
	
	public void verifySets() throws Exception {
		
		Date until = new Date();
		Long startingId = null;
		List<Record> records = ((DefaultRepository)getHarvestRepository()).getRecordsWSets(null, until, startingId);
		int setCount = 0;
		while (records != null && records.size() > 0) {
			for (Record r2 : records) {
				startingId = r2.getId();
				LOG.debug("r2.getSets(): "+r2.getSets());
				LOG.debug("r2.getId(): "+r2.getId());
				if (r2.getSets().size() > 0) {
					setCount++;
					LOG.debug("r2.getSets().size() > 0");
				} else {
					LOG.debug("r2.getSets().size() !> 0");
				}
			}
			records = ((DefaultRepository)getHarvestRepository()).getRecordsWSets(null, until, startingId);
		}
		LOG.debug("setCount: "+setCount);
		//assert setCount == 2000;

	}
}
