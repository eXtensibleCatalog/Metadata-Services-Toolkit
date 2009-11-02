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

import org.testng.annotations.Test;

import xc.mst.bo.service.Service;
import xc.mst.dao.DataException;
import xc.mst.helper.TestHelper;
import xc.mst.manager.processingDirective.ConfigFileException;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/**
 * Test class for testing services
 *
 * @author Tejaswi Haramurali
 */
@Test(groups = { "baseTests" }, enabled = true)
public class ServiceTest {

    public void serviceTest() throws DataException
    {
      	 // Initialize Solr, database, log before testing
      	 TestHelper helper = TestHelper.getInstance();
        try
        {
            ServicesService servicesService = new DefaultServicesService();
            Service service = new Service();
            service.setHarvestOutErrors(0);
            service.setHarvestOutLastLogReset(new Date());
            service.setHarvestOutLogFileName("harvestOutLogFileName");
            service.setHarvestOutRecordsAvailable(0);
            service.setNumberOfHarvests(0);
            service.setHarvestOutWarnings(0);
            service.setServicesErrors(0);
            service.setServicesLastLogReset(new Date());
            service.setServicesLogFileName("serviceLogFileName");
            service.setServicesWarnings(0);
            service.setXccfgFileName("MST-instances/MetadataServicesToolkit/services/Normalization/serviceConfig/DefaultNormalizationServiceConfig.xccfg");
            File configFile = new File("MST-instances/MetadataServicesToolkit/services/Normalization/serviceConfig/DefaultNormalizationServiceConfig.xccfg");
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

                // The version of the service, which must appear in the second line of the configuration file
        		String version = in.readLine();
        		version = (version.indexOf('#') >= 0 ? version.substring(0, version.indexOf('#')).trim() : version.trim());
        		
        		if(version == null || version.length() == 0)
        		{
        			throw new ConfigFileException("The second line of the service configuration file must be the service's version.");
        		}

                
                // The .jar file containing the service, which must appear in the third line of the configuration file
                String jar = in.readLine();
                jar = (jar.indexOf('#') >= 0 ? jar.substring(0, jar.indexOf('#')).trim() : jar.trim());
                if(jar == null || jar.length() == 0 || !jar.endsWith(".jar"))
                {

                    throw new ConfigFileException("The third line of the service configuration file must be the .jar file containing the service.");
                }

                // The name of the service's class, which must appear in the fourth line of the configuration file
                String className = in.readLine();
                className = (className.indexOf('#') >= 0 ? className.substring(0, className.indexOf('#')).trim() : className.trim());
                if(className == null || className.length() == 0)
                {

                    throw new ConfigFileException("The fourth line of the service configuration file must be the service's class name.");
                }

                // The port on which the service's OAI repository operates, which must appear in the fourth line of the configuration file
                String portString = in.readLine();
                portString = (portString.indexOf('#') >= 0 ? portString.substring(0, portString.indexOf('#')).trim() : portString.trim());
                if(portString == null || portString.length() == 0)
                {

                    throw new ConfigFileException("The fifth line of the service configuration file must be the service's OAI repository's port.");
                }
                service.setServiceJar(jar);
                service.setName(name);
                service.setClassName(className);
                service.setVersion(version);
            servicesService.insertService(service);

            Service newService = servicesService.getServiceById(service.getId());

            assert service.getHarvestOutErrors()==newService.getHarvestOutErrors() : "Harvest out errors are not equal";
            assert service.getHarvestOutLogFileName().equals(newService.getHarvestOutLogFileName()) : "Harvest out log file name are not equal";
            assert service.getHarvestOutRecordsAvailable()==service.getHarvestOutRecordsAvailable() : "Harvest out records avaialable are not equal";
            assert service.getNumberOfHarvests()==newService.getNumberOfHarvests() : "Harvest out records harvested are not equal";
            assert service.getHarvestOutWarnings()==newService.getHarvestOutWarnings() : "Harvest out warnings are not equal";
            assert service.getServicesErrors()==newService.getServicesErrors() : "Harvest out service error are not equal";
            assert service.getServicesWarnings()==newService.getServicesWarnings() : "Service warnings are not equal";
            assert service.getServicesLogFileName().equalsIgnoreCase(newService.getServicesLogFileName()) : "Service log file name are not equal";
            assert service.getVersion().equalsIgnoreCase(newService.getVersion()) : "Service version are not equal";

            servicesService.deleteService(service);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
