package xc.mst.services.transformation.test;

import java.util.Date;

import org.testng.annotations.Test;

public class ResumePerfTest extends xc.mst.service.impl.test.ResumePerfTest {

    @Override
    protected String getInputRepoName() {
        return "marcnormalization";
    }

    @Override
    @Test
    public void resumePerfTest() {
        // getJdbcTemplate().update("delete from MetadataServicesToolkit.service_harvests");
        /*
        getJdbcTemplate().update("insert into MetadataServicesToolkit.service_harvests "+
                "(service_id, repo_name, from_date, until_date, highest_id) values (?, ?, ?, ?, ?)",
                getMetadataService().getService().getId(),
                getInputRepoName(),
                new Date(0),
                new Date(),
                104991);
                */
        try {
            Thread.currentThread().sleep(15000);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        waitUntilFinished();
    }

}
