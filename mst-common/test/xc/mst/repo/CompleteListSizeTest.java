package xc.mst.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.common.test.BaseTest;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;

public class CompleteListSizeTest extends BaseTest {

    private static final Logger LOG = Logger.getLogger(CompleteListSizeTest.class);

    protected void deleteAndCreateRepo(Repository repo) {
        try {
            getRepositoryDAO().deleteSchema(repo.getName());
        } catch (Throwable t) {
        }
        getRepositoryDAO().createRepo(repo);
        getRepositoryDAO().createTables(repo);
        getRepositoryDAO().createIndicesIfNecessary(repo.getName());
    }

    @Test
    public void doTest() {
        try {
            long millisInDay = 86400000l;

            Set bibSet = getSetService().getSetBySetSpec("MARCXMLbibliographic");
            Set holdSet = getSetService().getSetBySetSpec("MARCXMLholdings");

            Date d1 = new Date(0);
            Date d2 = new Date(millisInDay);
            Date d3 = new Date(millisInDay * 2);
            Date d4 = new Date(millisInDay * 3);

            String repoName = "complete_list_size";
            Repository repo = (Repository) getConfig().getBean("Repository");
            repo.setName(repoName);

            List<Record> records = new ArrayList<Record>();
            int numRecordsInserted = 10000;
            for (int i = 0; i < numRecordsInserted; i++) {
                Record r = new Record();
                r.setId(i);
                r.setUpdatedAt(d2);
                r.setFormat(getMarc21Format());
                r.addSet(bibSet);
                records.add(r);
            }
            deleteAndCreateRepo(repo);
            repo.addRecords(records);
            ((DefaultRepository) repo).commitIfNecessary(true);
            TimingLogger.reset();

            MSTConfiguration.getInstance().setProperty(
                    "harvestProvider.estimateCompleteListSizeThreshold", "5000");
            MSTConfiguration.getInstance().setProperty("harvestProvider.maxExplain", "1000");

            // get all
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            long count = repo.getRecordCount(d1, d3, getMarc21Format(), null);
            Assert.assertEquals(count, numRecordsInserted);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, 1);

            // same request, but check that it's a cache hit
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            count = repo.getRecordCount(d1, d3, getMarc21Format(), null);
            Assert.assertEquals(count, numRecordsInserted);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, -1);

            // get all using bibSet
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            count = repo.getRecordCount(d1, d3, getMarc21Format(), bibSet);
            Assert.assertEquals(count, numRecordsInserted);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, 1);

            // check wrong format
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            count = repo.getRecordCount(d1, d3, getXCFormat(), null);
            Assert.assertEquals(count, -1);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, -1);

            Thread.sleep(3000);
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            count = repo.getRecordCount(d1, d3, getXCFormat(), null);
            Assert.assertEquals(count, 0);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, -1);

            // check wrong date
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            count = repo.getRecordCount(d3, d4, getMarc21Format(), null);
            Assert.assertEquals(count, 0);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, 3);

            ((DefaultRepository) repo).getCompletListSizeMap().clear();
            MSTConfiguration.getInstance().setProperty(
                    "harvestProvider.estimateCompleteListSizeThreshold", "10000");
            MSTConfiguration.getInstance().setProperty("harvestProvider.maxExplain", "5000");
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            count = repo.getRecordCount(d3, d4, getMarc21Format(), null);
            Assert.assertEquals(count, 0);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, 3);

            // check wrong set
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            count = repo.getRecordCount(d1, d3, getMarc21Format(), holdSet);
            Assert.assertEquals(count, 0);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, 3);

            MSTConfiguration.getInstance().setProperty(
                    "harvestProvider.estimateCompleteListSizeThreshold", "1");
            MSTConfiguration.getInstance().setProperty("harvestProvider.maxExplain", "1");
            // get all should be an estimate
            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            count = repo.getRecordCount(d1, d3, getMarc21Format(), null);
            Assert.assertEquals(count, -1);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, -1);

            getRepositoryDAO().lastCompleteListSizeMethod = -1;
            Thread.sleep(3000);
            // check cache
            count = repo.getRecordCount(d1, d3, getMarc21Format(), null);
            Assert.assertEquals(count, numRecordsInserted);
            Assert.assertEquals(getRepositoryDAO().lastCompleteListSizeMethod, 3);
        } catch (Throwable t) {
            LOG.error("", t);
        }
    }

}
