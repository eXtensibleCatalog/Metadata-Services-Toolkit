package xc.mst.services.normalization.test;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.XmlHelper;

public class OneOffQueryTest extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(OneOffQueryTest.class);
	
	@Test
	public void XmlTest() {
		String str = util.slurp(new File("393842.xml"));
		
		XmlHelper x = new XmlHelper();
		
		for (int i=0; i<500; i++) {
			if (i == 200) {
				TimingLogger.reset();
			}
			int from = i%10;
			int to = (i%10)-1;
			if (to == -1) {
				to=9;
			}
			str = str.replaceAll(from+"", to+"");
			long t0 = System.currentTimeMillis();
			TimingLogger.start("x");
			Document d = x.getJDomDocument(str);
			TimingLogger.stop("x");
			long t1 = System.currentTimeMillis();
			System.out.println("2 time to execute: "+(t1-t0));
			System.out.println("d: "+d);
			
			t0 = System.currentTimeMillis();
				TimingLogger.start("y");
			str = x.getString(d.detachRootElement());
				TimingLogger.stop("y");
			t1 = System.currentTimeMillis();
			
			System.out.println("3 time to execute: "+(t1-t0));

		}
		TimingLogger.reset();	
	}
	
	public void OneOffQueryTest() {

		try {
			Connection conn = ((DataSource)getBean("DataSource")).getConnection();
			
			PreparedStatement ps = conn.prepareStatement("select record_id from marcnormalization.record_predecessors where pred_record_id = ?");
			
			long k=2000000;
			
			for (long i=0; i<1000; i++) {
				TimingLogger.start("ps query");
				long t0 = System.currentTimeMillis();
				ps.setLong(1, k+i);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					long rid = rs.getLong("record_id");
					long t1 = System.currentTimeMillis();
					System.out.println("time to execute: "+(t1-t0)+"     "+rid);
				} else {
					long t1 = System.currentTimeMillis();
					System.out.println("time to execute: "+(t1-t0)+"     no record found for "+k+i);
				}
				TimingLogger.stop("ps query");
			}
			TimingLogger.reset();
			ps.close();
			conn.close();
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}
}