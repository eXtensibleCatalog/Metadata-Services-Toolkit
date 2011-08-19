/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.normalization.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class ResumePerfTest extends xc.mst.service.impl.test.ResumePerfTest {

    private static final Logger LOG = Logger.getLogger(ResumePerfTest.class);

    @Override
    protected String getInputRepoName() {
        return "135_5m";
    }

    @Override
    @Test
    public void resumePerfTest() {
        getJdbcTemplate().update("delete from MetadataServicesToolkit.service_harvests");
        waitUntilFinished();
        /*
        LOG.info("getInputRepoName(): "+getInputRepoName());
        getJdbcTemplate().update("delete from MetadataServicesToolkit.service_harvests");
        getJdbcTemplate().update("insert into MetadataServicesToolkit.service_harvests "+
                "(service_id, repo_name, from_date, until_date, highest_id) values (?, ?, ?, ?, ?)",
                getMetadataService().getService().getId(),
                getInputRepoName(),
                new Date(0),
                new Date(),
                1540000);
        super.resumePerfTest();
        */
    }

}
