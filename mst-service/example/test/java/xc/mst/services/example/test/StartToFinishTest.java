package xc.mst.services.example.test;

import java.util.HashSet;
import java.util.List;

import xc.mst.bo.provider.Format;
import xc.mst.bo.record.Record;

public class StartToFinishTest extends xc.mst.service.impl.test.StartToFinishTest {
	
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
	
	protected void finalTest() {
		Record r = getRepositoryService().getRecord(1999);
		assert r.getPredecessors().get(0).getId() == 999;
		
		r = getRepositoryService().getRecord(999);
		assert r.getSuccessors().get(0).getId() == 1999;
	}
}
