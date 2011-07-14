package xc.mst.harvester.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.testng.annotations.Test;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.common.test.BaseTest;
import xc.mst.oai.Facade;
import xc.mst.oai.OaiRequestBean;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.XmlHelper;

public class HarvestableAlgorithm extends BaseTest {
	
	protected static final DateTimeFormatter UTC_PARSER = ISODateTimeFormat.dateTimeParser();
	protected static DateTimeFormatter UTC_FORMATTER = null;
	static {
		UTC_FORMATTER = ISODateTimeFormat.dateTime();
		UTC_FORMATTER = UTC_FORMATTER.withZone(DateTimeZone.UTC);
	}
	
	// see here: http://code.google.com/p/xcmetadataservicestoolkit/wiki/ResumptionToken
	
	public static final String SERVICE_NAME = "example";
	
	public static final String REPO_NAME = "xc_example";
	
	public static final String TEST_SET_1 = "TEST_SET_1";
	public static final String TEST_SET_2 = "TEST_SET_2";
	
	public static final String TEST_FORMAT_1 = "TEST_FORMAT_1";
	public static final String TEST_FORMAT_2 = "TEST_FORMAT_2";
	
	//(5 * 36000000) - account for utc
	public static long DATE_1 = (5 * 3600000) + 60000;                             // 1970-01-01 00:01:00
	public static long DATE_2 = (5 * 3600000) + 365 * 24 * 60 * 60 * 1000;         // 1971-01-01 00:00:00
	public static long DATE_3 = (5 * 3600000) + 2 * 365 * 24 * 60 * 60 * 1000;     // 1972-01-01 00:00:00
	
	public static int[][] dataSets = null;
	public static Object[][] requestParams = null;
	public static int[][] expectedResults = null;
	
	protected Service service = null;
	protected int factor = 1000;
	
	static {
		// using 1-based index to match the wiki page
		
		dataSets = new int[][] {
			null,
			new int[] {10,  0, 10,  0, 10,  0,  1}, // 1
			new int[] {10,  0,  0,  0, 10,  0,  1}, // 2
			new int[] {10,  0,  5,  5, 10,  0,  1}, // 3
			new int[] {10,  0,  0,  0,  5,  5,  1}, // 4
			new int[] { 5,  5,  0,  0, 10,  0,  1}, // 5
			new int[] { 5,  5,  5,  5,  5,  5,  3}, // 6
		};
		
		requestParams = new Object[][] {
			null,
			new Object[]{TEST_FORMAT_1,       null,   null}, // 1
			new Object[]{TEST_FORMAT_1,       null, DATE_1}, // 2
			new Object[]{TEST_FORMAT_1,       null, DATE_2}, // 3
			new Object[]{TEST_FORMAT_1,       null, DATE_3}, // 4
			new Object[]{TEST_FORMAT_1, TEST_SET_1,   null}, // 5
			new Object[]{TEST_FORMAT_1, TEST_SET_2,   null}, // 6
			new Object[]{TEST_FORMAT_2,       null,   null}, // 7
			new Object[]{TEST_FORMAT_1, TEST_SET_1, DATE_1}, // 8
		};
		
		expectedResults = new int[][] {
			null,
			new int[] { 1, 1, 10}, // 1
			new int[] { 1, 2, 10}, // 2
			new int[] { 1, 3,  0}, // 3
		};
	}
	
	protected Set getSet(String str) {
		try {
			Set set = getSetService().getSetBySetSpec(str); 
			if (set == null) {
				set = new Set();
				set.setDisplayName(str);
				set.setSetSpec(str);
				getSetService().insertSet(set);
			}
			return set;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	protected Format getFormat(String str) {
		try {
			Format fmt = getFormatService().getFormatByName(str); 
			if (fmt == null) {
				fmt = new Format();
				fmt.setName(str);
				fmt.setNamespace("urn:bogus");
				fmt.setSchemaLocation("http://www.bogus.com");
				getFormatService().insertFormat(fmt);
			}
			return fmt;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	protected void createDS(int numFmt1, int numFmt2, int numSet1, 
			int numSet2, int numDt1, int numDt2, int updatesPerRec) throws Throwable {
		//getRepositoryDAO().createSchema(REPO_NAME, true);
		getServicesService().addNewService(SERVICE_NAME);
		service = getServicesService().getServiceByName(SERVICE_NAME);
		List<Record> records = new ArrayList<Record>();
		for (int j=0; j<updatesPerRec; j++) {
			for (int i=0; i<factor*10; i++) {
				OutputRecord r =  new Record();
				((Record)r).setId(i);
				if (i < (numFmt1*factor)) {
					r.setFormat(getFormat(TEST_FORMAT_1));
				} else if (i < ((numFmt1 + numFmt2)*factor)) {
					r.setFormat(getFormat(TEST_FORMAT_2));
				}
				if (i < (numSet1*factor)) {
					r.addSet(getSet(TEST_SET_1));
				} else if (i < ((numSet1 + numSet2)*factor)) {
					r.addSet(getSet(TEST_SET_2));
				}
				if (i < (numDt1*factor)) {
					((Record)r).setUpdatedAt(new Date(DATE_1 + (j*24*60*60*1000))); // add 1 day per j
				} else if (i < ((numDt1 + numDt2)*factor)) {
					((Record)r).setUpdatedAt(new Date(DATE_2 + (j*24*60*60*1000))); // add 1 day per j
				}
				r.setMode(Record.STRING_MODE);
				r.setOaiXml("<foo />");
				records.add((Record)r);
			}
			getRepositoryDAO().addRecords(REPO_NAME, records, true);
			getRepositoryDAO().createIndiciesIfNecessary(REPO_NAME);
		}
	}
	
	@Test
	public void testAll() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			for (int i=1; i<expectedResults.length; i++) {
				int[] ds = dataSets[expectedResults[i][0]];
				createDS(ds[0], ds[1], ds[2], ds[3], ds[4], ds[5], ds[6]);
				
				Object[] rp = requestParams[expectedResults[i][1]];
				
				OaiRequestBean bean = new OaiRequestBean();
				bean.setVerb("ListRecords");
				bean.setMetadataPrefix((String)rp[0]);
				if (rp[1] != null) {
					bean.setSet((String)rp[1]);
				}
				if (rp[2] != null) {
					bean.setFrom(UTC_FORMATTER.print(new Date(((Long)rp[2])-5000l).getTime()));
					bean.setUntil(UTC_FORMATTER.print(new Date(((Long)rp[2])+5000l).getTime()));
				}

				bean.setServiceId(service.getId());
	
				Facade facade = (Facade) MSTConfiguration.getInstance().getBean("Facade");
				
				String respXml = facade.execute(bean);
				
				LOG.debug("respXml: "+respXml);
				Document respEl = new XmlHelper().getJDomDocument(respXml);
				
				Element oaiPmhEl = respEl.getRootElement();
				Element listRecordsEl = oaiPmhEl.getChild("ListRecords", oaiPmhEl.getNamespace());
				Element resumptionTokenEl = listRecordsEl.getChild("resumptionToken", oaiPmhEl.getNamespace());
				Integer completeListSize = null;
				try {
					String completeListSizeStr = resumptionTokenEl.getAttributeValue("completeListSize");
					completeListSize = Integer.parseInt(completeListSizeStr);
				} catch (Throwable t) {
					LOG.debug("completeListSize not found");
					LOG.debug("", t);
				}
				
				if (completeListSize == null || (expectedResults[i][2]*factor) != completeListSize) {
					LOG.error("i: "+i);
					LOG.error("completeListSize: "+completeListSize);
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
