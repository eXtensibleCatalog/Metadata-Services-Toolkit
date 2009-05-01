/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import xc.mst.bo.service.Service;
import xc.mst.dao.DataException;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;
import org.testng.annotations.Test;
import xc.mst.manager.processingDirective.ConfigFileException;
import xc.mst.utils.LogWriter;

/**
 * Test class for testing services
 *
 * @author Tejaswi Haramurali
 */
@Test(groups = { "baseTests" }, enabled = true)
public class ServiceTest {

    public void serviceTest() throws DataException
    {
        try
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
            File configFile = new File("serviceConfig/DefaultNormalizationService.xccfg");
            BufferedReader in = new BufferedReader(new FileReader(configFile));

                // The name of the service, which must appear in the first line of the configuration file
                String name = in.readLine();
                name = (name.indexOf('#') >= 0 ? name.substring(0, name.indexOf('#')).trim() : name.trim());
                if(name == null || name.length() == 0)
                {

                    throw new ConfigFileException("The first line of the service configuration file must be the service's name.");
                }

                // Verify that the name is unique
                Service oldService = servicesService.getServiceByName(name);
                if(oldService != null)
                {

                    throw new ConfigFileException("Cannot add a service named " + name + " because a service with that name already exists.");
                }

                // The .jar file containing the service, which must appear in the secord line of the configuration file
                String jar = in.readLine();
                jar = (jar.indexOf('#') >= 0 ? jar.substring(0, jar.indexOf('#')).trim() : jar.trim());
                if(jar == null || jar.length() == 0 || !jar.endsWith(".jar"))
                {

                    throw new ConfigFileException("The secord line of the service configuration file must be the .jar file containing the service.");
                }

                // The name of the service's class, which must appear in the third line of the configuration file
                String className = in.readLine();
                className = (className.indexOf('#') >= 0 ? className.substring(0, className.indexOf('#')).trim() : className.trim());
                if(className == null || className.length() == 0)
                {

                    throw new ConfigFileException("The third line of the service configuration file must be the service's class name.");
                }

                // The port on which the service's OAI repository operates, which must appear in the fourth line of the configuration file
                String portString = in.readLine();
                portString = (portString.indexOf('#') >= 0 ? portString.substring(0, portString.indexOf('#')).trim() : portString.trim());
                if(portString == null || portString.length() == 0)
                {

                    throw new ConfigFileException("The fourth line of the service configuration file must be the service's OAI repository's port.");
                }
                service.setServiceJar(jar);
                service.setPort(Integer.parseInt(portString));
                service.setName(name);
                service.setClassName(className);
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
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
