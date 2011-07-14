package xc.mst.harvester.test;

import java.util.Date;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.harvest.Harvest;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.provider.Provider;
import xc.mst.common.test.BaseTest;

public class HarvestDAOTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(HarvestDAOTest.class);
	
	@Test
	public void test() {
		try {
			Provider p = new Provider();
			p.setName("p1");
			p.setCreatedAt(new Date());
			p.setLogFileName("bogusFile");
			p.setOaiProviderUrl("http://www.bogus.com");
			getProviderDAO().insert(p);
			
			HarvestSchedule hs = new HarvestSchedule();
			hs.setScheduleName("hs1");
			hs.setProvider(p);
			getHarvestScheduleDAO().insert(hs);
			
			Date st1 = new Date(1000*60*60*5);  // DST nonsense
			long st2l = (new Date().getTime()/1000)*1000;
			LOG.debug("st2l: "+st2l);
			Date st2 = new Date(st2l);
			
			Harvest h1 = new Harvest();
			h1.setStartTime(st1);
			h1.setProvider(p);
			h1.setHarvestSchedule(hs);
			getHarvestDAO().insert(h1);
			
			Harvest h2 = new Harvest();
			h2.setStartTime(st2);
			h2.setProvider(p);
			h2.setHarvestSchedule(hs);
			getHarvestDAO().insert(h2);
			
			Harvest h = getHarvestDAO().getLatestHarvestForSchedule(1);
			LOG.debug("h.getStartTime(): "+h.getStartTime());
			LOG.debug("st2: "+st2);
			LOG.debug("h.getStartTime().getTime(): "+h.getStartTime().getTime());
			LOG.debug("st2.getTime(): "+st2.getTime());
			LOG.debug("h.getStartTime().getTime() == st2.getTime(): "+(h.getStartTime().getTime() == st2.getTime()));
			assert h.getStartTime().getTime() == st2.getTime();
			assert h.getHarvestSchedule().getScheduleName().equals("hs1");
			assert h.getProvider().getName().equals("p1");
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
