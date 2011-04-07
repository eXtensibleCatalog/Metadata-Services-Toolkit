package xc.mst.services.normalization.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.record.Record;
import xc.mst.repo.Repository;

public class MarkProviderDeletedTest extends MockHarvestTest {
	
	public List<String> getFolders() {
		List<String> fileStrs = new ArrayList<String>();
		fileStrs.add("demo_175");
		return fileStrs;
	}
	
	public void finalTest() {
		try {
			// These first 2 steps are done in MockHarvestTest
			// - harvest records into MST and run them through norm service
			
			// - harvest from provider and norm service, making sure there are no deleteds
			Repository providerRepo = getRepositoryService().getRepository(this.provider);
			ensureAllRecordsMatchStatus(providerRepo, Record.ACTIVE);
			
			Repository serviceRepo = getServiceRepository(); 
			ensureAllRecordsMatchStatus(serviceRepo, Record.ACTIVE);
			
			// - ensure there are scheduled harvests
			HarvestSchedule hs = getHarvestScheduleDAO().getHarvestScheduleForProvider(this.provider.getId());
			assert hs != null : "there should be a harvestSchedule for the provider";

			// - delete the provider - John, this method doesn't exist.  It's what you need to create.
			//getProviderService().markProviderDeleted(this.provider);
			
			// - ensure there are no harvest schedules
			hs = getHarvestScheduleDAO().getHarvestScheduleForProvider(this.provider.getId());
			assert hs == null : "there should be a harvestSchedule for the provider";
			
			// - harvest from provider and norm service
			//   - make sure all records are deleted
			ensureAllRecordsMatchStatus(providerRepo, Record.DELETED);
			ensureAllRecordsMatchStatus(serviceRepo, Record.DELETED);

			// - create a new harvest schedule
			createHarvestSchedule();

			// - harvest from provider and norm service
			//   - make sure all records are active again
			//   - make sure the same ids are used
			ensureAllRecordsMatchStatus(providerRepo, Record.ACTIVE);
			ensureAllRecordsMatchStatus(serviceRepo, Record.ACTIVE);
			
		} catch (Throwable t) {
			getUtil().throwIt(t);
		}
	}
	
	protected void ensureAllRecordsMatchStatus(Repository repo, char status) throws Throwable {
		List<Record> records = repo.getRecords(new Date(0), new Date(), 0l, getMarc21Format(), null);
		for (Record r : records) {
			if (r.getStatus() != status) {
				throw new RuntimeException("record with id: "+r.getId()+" has status:"+r.getStatus()+" while expecting:"+status);
			}
		}
	}
	
}
