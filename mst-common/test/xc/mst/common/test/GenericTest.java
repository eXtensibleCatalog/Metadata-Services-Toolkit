/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.common.test;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.bo.service.ServiceHarvest;
import xc.mst.repo.DefaultRepository;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.XmlHelper;

public class GenericTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(GenericTest.class);
	
	@BeforeSuite
	@Override
	public void startup() {
		super.startup();
	}
	
	@AfterSuite
	@Override
	public void shutdown() {
		super.shutdown();
	}
	
	@Test
	public void goTest() {
		
		try {
			
			String str = util.slurp(new File("docs/393842.xml"));
			
			XmlHelper x = new XmlHelper();
			for (int i=0; i<1000; i++) {
				TimingLogger.start("x");
				x.getJDomDocument(str);
				TimingLogger.stop("x");
			}
			TimingLogger.reset();
			
			/*
			JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource)getBean("DataSource"));
			
			for (int i=0; i<1000; i++) {
				long t0 = System.currentTimeMillis();
				jdbcTemplate.queryForInt("select 1");
				long t1 = System.currentTimeMillis();
				System.out.println("time to execute: "+(t1-t0));
			}
			*/
			
			/*
			String repoName = "test_repo";
			DefaultRepository repo = (DefaultRepository)MSTConfiguration.getInstance().getBean("Repository");
			repo.setName(repoName);
			Date from = new Date();
			from.setHours(14);
			from.setMinutes(34);
			from.setSeconds(58);
			
			Date to = new Date();
			to.setHours(14);
			to.setMinutes(35);
			to.setSeconds(01);
			
			List<Record> records = 
				((DefaultRepository)repo).getRecordsWSets(from, to, null);
			
			LOG.debug("records.size(): "+records.size());
			
			
			String serviceName = "example";
			try {
				getRepositoryDAO().deleteSchema(serviceName);
			} catch (Throwable t) {}
			
			ServiceHarvest sh = new ServiceHarvest();
			//Service s = getServicesService().getServiceByName(serviceName);
			Service s = new Service();
			s.setId(45);
			
			LOG.debug("s.getName(): "+s.getName());
			LOG.debug("s.getId(): "+s.getId());

			sh.setService(s);
			sh.setFrom(new Date());
			sh.setUntil(new Date());
			sh.setRepoName(serviceName);
			getServiceDAO().persist(sh);

			sh = getServiceDAO().getServiceHarvest(null, null, serviceName, s);
			LOG.debug("sh: "+sh);
			LOG.debug("sh.getId(): "+sh.getId());

			String serviceName = "example";
			try {
				getRepositoryDAO().deleteSchema(serviceName);
			} catch (Throwable t) {}
			getServicesService().addNewService(serviceName);
			
			ServiceHarvest sh = new ServiceHarvest();
			//Service s = getServicesService().getServiceByName(serviceName);
			Service s = new Service();
			s.setId(45);
			
			LOG.debug("s.getName(): "+s.getName());
			LOG.debug("s.getId(): "+s.getId());
			
			Set set = getSetService().getSetById(1);
			sh.setSet(set);
			sh.setService(s);
			sh.setFrom(new Date());
			sh.setUntil(new Date());
			sh.setRepoName(serviceName);
			getServiceDAO().persist(sh);
			*/
			
			/*
			RecordService rs = (RecordService)getBean("RecordService");
			System.out.println("rs.getRecordDAO(): "+rs.getRecordDAO());
			
			List<URL> urls = new ArrayList<URL>();

			System.out.println("hello");
			File f = new File(".");
			URL url = f.toURI().toURL();
			System.out.println("url: "+url);
			//url = new URL(url.toString()+"/");
			//System.out.println("url: "+url);
			urls.add(url);
			
			URL[] urlsArr = urls.toArray(new URL[]{});
    		URLClassLoader loader = new URLClassLoader(urlsArr, getClass().getClassLoader());
    		
    		BufferedReader br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("build.properties")));
    		StringBuilder sb = new StringBuilder();
    		String line = null;
    		while ((line = br.readLine()) != null) {
    		sb.append(line + "\n");
    		}
    		br.close();
    		System.out.println(sb.toString());
    		*/
		} catch (Throwable t) {
			t.printStackTrace(System.out);
			throw new RuntimeException(t);
		}
	}

}
