/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.test;

import java.util.Date;
import xc.mst.bo.service.Service;
import xc.mst.dao.DataException;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;
import org.testng.annotations.Test;

/**
 * Test class for testing services
 *
 * @author Tejaswi Haramurali
 */
@Test(groups = { "baseTests" }, enabled = true)
public class ServiceTest {

    public void serviceTest() throws DataException
    {
        ServicesService servicesService = new DefaultServicesService();
        Service service = new Service();
        service.setHarvestOutErrors(0);
        service.setHarvestOutLastLogReset(new Date());
        service.setHarvestOutLogFileName("harvestOutLogFileName");
        service.setHarvestOutRecordsAvailable(0);
        service.setHarvestOutRecordsHarvested(0);
        service.setHarvestOutWarnings(0);
        service.setServicesErrors(0);
        service.setServicesLastLogReset(new Date());
        service.setServicesLogFileName("serviceLogFileName");
        service.setServicesWarnings(0);
        servicesService.insertService(service);

        Service newService = servicesService.getServiceById(service.getId());

        assert service.getHarvestOutErrors()==newService.getHarvestOutErrors();
        assert service.getHarvestOutLastLogReset()==newService.getHarvestOutLastLogReset();
        assert service.getHarvestOutLogFileName().equals(newService.getHarvestOutLogFileName());
        assert service.getHarvestOutRecordsAvailable()==service.getHarvestOutRecordsAvailable();
        assert service.getHarvestOutRecordsHarvested()==newService.getHarvestOutRecordsHarvested();
        assert service.getHarvestOutWarnings()==newService.getHarvestOutWarnings();
        assert service.getServicesErrors()==newService.getServicesErrors();
        assert service.getServicesWarnings()==newService.getServicesWarnings();
        assert service.getServicesLastLogReset()==newService.getServicesLastLogReset();
        assert service.getServicesLogFileName().equalsIgnoreCase(newService.getServicesLogFileName());

        servicesService.deleteService(service);
    }

}
