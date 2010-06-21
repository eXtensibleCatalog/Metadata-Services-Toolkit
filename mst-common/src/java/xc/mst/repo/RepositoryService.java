package xc.mst.repo;

import java.util.HashMap;
import java.util.Map;

import xc.mst.manager.BaseService;

public class RepositoryService extends BaseService {
	
	protected Map<String, Repository> repos = new HashMap<String, Repository>();
	
	public Repository getRepository(String name) {
		Repository repo = repos.get(name);
		if (repo == null) {
			repo = (Repository)config.getBean("Repository");
			repo.setName(name);
			repos.put(name, repo);
		}
		return repo;
	}

}
