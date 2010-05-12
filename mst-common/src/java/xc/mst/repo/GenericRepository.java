package xc.mst.repo;

import xc.mst.manager.BaseManager;
import xc.mst.utils.MSTConfiguration;

public class GenericRepository extends BaseManager {
	
	protected String name = null;
	
	public void installOrUpdateIfNecessary() {
		if ("0.3.0".equals(MSTConfiguration.getProperty("version"))) {
			if (getRepositoryDAO().exists()) {
				
			}
		}
	}
	
	protected boolean exists() {
		return false;
	}

}
