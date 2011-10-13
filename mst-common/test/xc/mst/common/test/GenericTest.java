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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import antlr.NameSpace;

import xc.mst.utils.Util;
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

    Namespace xsiNS = null;
    Namespace oaiNS = null;

    public void putStuffIn(List l, Map<String, List<Element>> marcRecordElMap) throws Throwable {
        for (Object elObj : l) {
            Element recordEl = (Element)elObj;
            Element metadataEl = recordEl.getChild("metadata", oaiNS);
            Element marcRecordEl = (Element)metadataEl.getChildren().get(0);


            Element firstEl = (Element)((Element)marcRecordEl.getChildren().get(0)).getChildren().get(0);
            String md5input = xmlHelper.getString(firstEl);
            Element secondEl = null;
            try {
                secondEl = (Element)((Element)marcRecordEl.getChildren().get(0)).getChildren().get(1);
                md5input = md5input+xmlHelper.getString(secondEl);
            } catch (Throwable t) {
                System.out.println(xmlHelper.getString(marcRecordEl));
            }
            md5input = md5input.replaceAll("oai:mst.rochester.edu[^\"<]*", "");
            byte[] bytesOfMessage = md5input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<thedigest.length;i++) {
                hexString.append(Integer.toHexString(0xFF & thedigest[i]));
            }
            System.out.println(hexString+": "+md5input);
            marcRecordEl.addNamespaceDeclaration(xsiNS);
            List<Element> marcRecordList = marcRecordElMap.get(hexString.toString());
            if (marcRecordList == null) {
                marcRecordList = new ArrayList<Element>();
                marcRecordElMap.put(hexString.toString(), marcRecordList);
            }
            marcRecordList.add(marcRecordEl);
        }
    }

    public void getStuffOut(StringBuilder out, Map<String, List<Element>> marcRecordElMap) {
        for (Map.Entry<String, List<Element>> me : marcRecordElMap.entrySet()) {
            out.append("<md5>"+me.getKey()+"</md5>");
            for (Element e : me.getValue()) {
                out.append(xmlHelper.getString(e));
            }
        }
    }

    @Test
    public void goTest() {

        try {
            Util util = new Util();
            XmlHelper xmlHelper = new XmlHelper();

            xsiNS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

            String contents = util.slurp(new File("C:/dev/xc/mst/svn/trunk/mst-service/custom/MARCToXCTransformation/build/test/mock_harvest_actual_output/orig_186/001.xml"));
            Document doc = xmlHelper.getJDomDocument(contents);
            Map<String, List<Element>> marcRecordElMap = new TreeMap<String, List<Element>>();

            StringBuilder out = new StringBuilder();

            oaiNS = doc.getRootElement().getNamespace();

            out.append("<root>");
            List l = doc.getRootElement().getChild("ListRecords", oaiNS).getChildren("record", oaiNS);
            System.out.println("l.size(): "+l.size());
            putStuffIn(l, marcRecordElMap);
            getStuffOut(out, marcRecordElMap);

            marcRecordElMap.clear();
            out.append("</root>");
            util.spit("C:/dev/xc/mst/svn/trunk/new.xml", out.toString());

            out = new StringBuilder();
            out.append("<root>");

            File folder = new File("C:/dev/xc/mst/svn/trunk/mst-service/custom/MARCToXCTransformation/test/expected_output_records/orig-186");
            for (String fStr : folder.list()) {
                if (fStr != null && fStr.indexOf("svn") == -1) {
                    contents = util.slurp(new File("C:/dev/xc/mst/svn/trunk/mst-service/custom/MARCToXCTransformation/test/expected_output_records/orig-186/"+fStr));
                    doc = xmlHelper.getJDomDocument(contents);

                    l = doc.getRootElement().getChildren("record", oaiNS);
                    System.out.println("l.size(): "+l.size());
                    putStuffIn(l, marcRecordElMap);
                }
            }
            getStuffOut(out, marcRecordElMap);
            out.append("</root>");
            util.spit("C:/dev/xc/mst/svn/trunk/old.xml", out.toString());
            /*
            System.out.println("beluga");
            LOG.error("beluga");

            // problem
            // 2750000,250000

            // this works
            // int offset = 2750000;
            // int incr = 25000;

            // this doesn't work
            // int offset = 2750000;
            // int incr = 250000;

            // this doesn't work
            int offset = 2750000;
            int incr = 250000;

            for (int i = 0; (offset + (incr * i)) <= 3000000; i++) {
                String sql = "select record_id, oai_id from  xc_rochester_137.record_oai_ids limit " + (offset + (incr * i)) + "," + incr;
                LOG.error("sql: " + sql);
                List<Map<String, Object>> rowList = getJdbcTemplate().queryForList(sql);
                LOG.error("rowList.size(): " + rowList.size());
                int j = 0;
                for (Map<String, Object> row : rowList) {
                    if (j++ % 10000 == 0) {
                        LOG.error("record_id: " + row.get("record_id"));
                        LOG.error("oai_id: " + row.get("oai_id"));
                    }
                }
                LOG.error("done.");
            }
            */
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
