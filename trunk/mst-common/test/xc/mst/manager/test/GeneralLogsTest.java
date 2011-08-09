/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.test;

import java.util.Date;

import org.testng.annotations.Test;

import xc.mst.bo.log.Log;
import xc.mst.common.test.BaseTest;
import xc.mst.dao.DataException;
import xc.mst.helper.TestHelper;
import xc.mst.manager.logs.LogService;

/**
 * Tests for General Logs
 *
 * @author Tejaswi Haramurali
 */
@Test(groups = { "baseTests" }, enabled = true)
public class GeneralLogsTest extends BaseTest {

    public void generalLogsTest() throws DataException
    {
      	 // Initialize Solr, database, log before testing
      	 TestHelper helper = TestHelper.getInstance();
        try
        {
            LogService logService = (LogService)getBean("LogService");
            Log log = new Log();
            log.setErrors(0);
            log.setLastLogReset(new Date());
            log.setLogFileLocation("Location");
            log.setLogFileName("LogFileName");
            log.setWarnings(0);
            logService.insert(log);

            Log newLog = logService.getById(log.getId());
            assert (newLog.getErrors()==log.getErrors());
            assert (newLog.getWarnings()==log.getWarnings());
            //assert (newLog.getLastLogReset()==log.getLastLogReset());
            assert (newLog.getLogFileLocation().equalsIgnoreCase(log.getLogFileLocation()));
            assert (newLog.getLogFileName().equalsIgnoreCase(log.getLogFileName()));
            logService.delete(log);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

}
