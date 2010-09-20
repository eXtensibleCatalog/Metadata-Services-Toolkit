package xc.mst.common.test.dao.service;

import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.bo.service.ServiceHarvest;
import xc.mst.common.test.BaseTest;

public class ServiceDAOTest extends BaseTest {

	@Test
	public void getServiceHarvest() {
		deleteRecords();
		int serviceId = 101;
		getJdbcTemplate().update(
				"insert into service_harvests ("+
					"service_id, "+
					"format_id, "+
					"repo_name, "+
					"set_id, "+
					"from_date, "+
					"until_date, "+
					"highest_id) "+
				"values (" +
					serviceId+", "+
					"null, "+
					"'"+this.getClass().getName()+"', "+
					"null, "+
					"null, "+
					"null, "+
					"null )");
		
		Service s = new Service();
		s.setId(serviceId);
		ServiceHarvest sh = getServiceDAO().getServiceHarvest(
				null, null, this.getClass().getName(), s);
		deleteRecords();
		assert sh != null;
	}
	
	protected void deleteRecords() {
		getJdbcTemplate().update(
				"delete from service_harvests "+
				"where repo_name = '"+this.getClass().getName()+"'");
	}
}
