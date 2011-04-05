package xc.mst.services.normalization.test;

public class MarkProviderDeletedTest extends StartToFinishTest {
	
	@Override
	public void finalTest() {
		/*
		 * we need to start a list of ongoing tests.  I want this test to live on indefinitely, 
		 * so we should use a filed based approach to the harvest.
		 * 
		 * - harvest from provider and norm service
		 *   - make sure there are no deleteds
		 * - ensure there are scheduled harvests
		 * - delete a provider
		 * - ensure there are no harvest schedules
		 * - harvest from provider and norm service
		 *   - make sure all records are deleted
		 * - create a new harvest schedule
		 * - harvest from provider and norm service
		 *   - make sure all records are active again
		 *   - make sure the same ids are used
		 *    
		 */
		
		
	}
	
}
