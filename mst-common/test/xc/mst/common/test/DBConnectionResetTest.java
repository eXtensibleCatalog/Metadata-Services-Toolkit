package xc.mst.common.test;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;

public class DBConnectionResetTest extends BaseTest {

    private static final Logger LOG = Logger.getLogger(DBConnectionResetTest.class);

    public void test1() {
        try {
            DataSource ds = (DataSource) getBean("DataSource");
            Connection conn = ds.getConnection();

            conn.close();
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            getUtil().throwIt(t);
        }
    }

    @Test
    public void test2() {
        try {
            Provider p = new Provider();
            p.setName("my provider");
            p.setId(100);
            p.setDescription("Repository used in TestNG tests");
            p.setOaiProviderUrl("http://128.151.244.135:8080/OAIToolkit/oai-request.do");
            p.setCreatedAt(new java.util.Date());
            p.setLogFileName("bogus");
            p.setNumberOfRecordsToHarvest(0);
            getProviderDAO().insert(p);
            p = getProviderDAO().loadBasicProvider(p.getId());
            LOG.debug("p.getId(): " + p.getId());

            Thread.sleep(6000);
            p = getProviderDAO().loadBasicProvider(p.getId());
            LOG.debug("p.getId(): " + p.getId());

            Thread.sleep(6000);
            Set s = getSetDAO().getBySetSpec("MARCXMLbibliographic");
            LOG.debug("s.getId(): " + s.getId());
            /*
            Thread.sleep(7000);
            p = getProviderDAO().loadBasicProvider(p.getId());
            LOG.debug("p.getId(): "+p.getId());
            */
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            getUtil().throwIt(t);
        }
    }

}
