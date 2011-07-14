/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.common.test;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

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
			System.out.println("beluga");
			LOG.error("beluga");
			
			//problem 
			//2750000,250000
			
			// this works
			//int offset = 2750000;
			//int incr = 25000;

			// this doesn't work
			//int offset = 2750000;
			//int incr = 250000;
			
			// this doesn't work
			int offset = 2750000;
			int incr = 250000;
			
			for (int i=0; (offset+(incr*i))<=3000000; i++) {
				String sql = "select record_id, oai_id from  xc_rochester_137.record_oai_ids limit "+(offset+(incr*i))+","+incr;
				LOG.error("sql: "+sql);
				List<Map<String, Object>> rowList = getJdbcTemplate().queryForList(sql);
				LOG.error("rowList.size(): "+rowList.size());
				int j=0;
				for (Map<String, Object> row : rowList) {
					if (j++ % 10000 == 0) {
						LOG.error("record_id: "+row.get("record_id"));
						LOG.error("oai_id: "+row.get("oai_id"));
					}
				}
				LOG.error("done.");
			}
			/*
			*/
			
			
			/*
			String[] rtSplit = "|||xc|13360453".split("\\\\|");
			
			for (int i=0; i<rtSplit.length; i++) {
				LOG.debug("i: "+i);
				LOG.debug("rtSplit[i]: "+rtSplit[i]);
			}
			*/
			
			/*
			int i=1;
			
			Repository r = (Repository)MSTConfiguration.getInstance().getBean("Repository");
			r.setName("135");
			long t0 = System.currentTimeMillis();
			for (; i<1000; i++) {
				r.getRecord(i);
			}
			long t1 = System.currentTimeMillis();
			System.out.println("first 1000: "+(t1-t0)/1000.);
			t0 = System.currentTimeMillis();
			for (; i<15000; i++) {
				r.getRecord(i);
			}
			t1 = System.currentTimeMillis();
			System.out.println("next 14000: "+(t1-t0)/14000.);
			*/
			
			/*
			URLClassLoader sysloader = (URLClassLoader) getClass().getClassLoader();
			Class sysclass = URLClassLoader.class;

			try {
				Method method = sysclass.getDeclaredMethod("addURL",  new Class[]{URL.class});
				method.setAccessible(true);
				method.invoke(sysloader, new Object[]{new URL("file://c")});
			} catch (Throwable t) {
				t.printStackTrace();
				throw new RuntimeException("Error, could not add URL to system classloader");
			}
			
			for (URL u2 : sysloader.getURLs()) {
				System.out.println("u: "+u2);
			}
			*/
			
			
			
			/*
			Record r = getRepositoryDAO().getRecord("test_repo", 1000);
			
			//String testXml = getUtil().slurp(new File(MSTConfiguration.getUrlPath()+"/test.xml"));
			String testXml = r.getOaiXml();
			r.setMode(Record.JDOM_MODE);
			Document doc = new XmlHelper().getJDomDocument(testXml);
			String outXml = new XmlHelper().getString(doc.getRootElement());
			System.out.println(outXml);
			*/
			/*
			InputStream istm = new ByteArrayInputStream("<yo>contains \\b</yo>".getBytes("UTF-8"));
			Document doc = xmlHelper.getJDomDocument(istm);
			
			String containsB = new XmlHelper().getString(doc.getRootElement());
			System.out.println("containsB: "+containsB);
			
			containsB = getJdbcTemplate().queryForObject("select prop_key from marctoxctransformation.properties", String.class);
			LOG.debug("containsB: "+containsB);
			System.out.println("containsB: "+containsB);
			containsB = "contains \\b";
			LOG.debug("containsB: "+containsB);
			System.out.println("containsB: "+containsB);
			*/
			/*
			String dbLoadFileStr = MSTConfiguration.getUrlPath()+"/db_load.in";
			LOG.debug("1 dbLoadFileStr: "+dbLoadFileStr);
			
			dbLoadFileStr = dbLoadFileStr.replace('\\', '/');
			LOG.debug("2 dbLoadFileStr: "+dbLoadFileStr);
			
			dbLoadFileStr = dbLoadFileStr.replaceAll("\\\\", "/");
			LOG.debug("3 dbLoadFileStr: "+dbLoadFileStr);
			
			dbLoadFileStr = dbLoadFileStr.replaceAll("\\", "/");
			LOG.debug("4 dbLoadFileStr: "+dbLoadFileStr);
			*/
			
			/*
			String str = getUtil().slurp(new File("docs/393842.xml"));
			
			XmlHelper x = new XmlHelper();
			for (int i=0; i<1000; i++) {
				TimingLogger.start("x");
				x.getJDomDocument(str);
				TimingLogger.stop("x");
			}
			TimingLogger.reset();
			*/
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
