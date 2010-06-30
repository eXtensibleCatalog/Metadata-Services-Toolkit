package xc.mst.services.example.dao;

import xc.mst.dao.BaseDAO;

public class FooDAO extends BaseDAO {
	
	public void insertFoo(String name) {
		this.jdbcTemplate.update("insert into foos (foo) values (?)", name);
	}

}
