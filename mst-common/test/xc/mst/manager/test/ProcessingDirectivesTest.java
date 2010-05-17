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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;
import xc.mst.helper.TestHelper;
import xc.mst.manager.processingDirective.ConfigFileException;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;


/**
 * Tests for Processing Directives
 *
 * @author Tejaswi Haramurali
 */
@Test(groups = { "baseTests" }, enabled = true)
public class ProcessingDirectivesTest
{
    /**
     * Method which tests all the functionality related to Processing Directives
     *
     */
    public void addProcDir() throws Exception 
    {
      	 // Initialize Solr, database, log before testing
      	 TestHelper helper = TestHelper.getInstance();

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
         
         service.setName(name);
         service.setClassName(className);
         service.setVersion(version);
         servicesService.insertService(service);
         
         ProviderService providerService = new DefaultProviderService();
         Provider provider = new Provider();
         provider.setName("repositoryname");
         provider.setDescription("description");
         provider.setOaiProviderUrl("http://oaitoolkit.com");
         provider.setCreatedAt(new Date(new java.util.Date().getTime()));
         provider.setErrors(0);
         provider.setWarnings(0);
         provider.setLastLogReset(new Date());
         provider.setLogFileName("logFileName");
         providerService.insertProvider(provider);

      	 
      	 
            ProcessingDirectiveService pdService = new DefaultProcessingDirectiveService();
            List setList = new ArrayList();
            List formatList = new ArrayList();



            ProcessingDirective processingDirective = new ProcessingDirective();
            processingDirective.setId(1001);
            processingDirective.setMaintainSourceSets(false);
            processingDirective.setOutputSet(null);

            processingDirective.setService(servicesService.getServiceByName("Normalization Service"));
            processingDirective.setSourceService(null);
            processingDirective.setSourceProvider(providerService.getProviderByName("repositoryname"));
            processingDirective.setTriggeringFormats(formatList);
            processingDirective.setTriggeringSets(setList);
            
            pdService.insertProcessingDirective(processingDirective);

            ProcessingDirective anotherDirective = pdService.getByProcessingDirectiveId(processingDirective.getId());
            assert (anotherDirective.getOutputSet()==null): "The output set is should be null";
            assert (anotherDirective.getService().equals(service)): "The service should exist";
            assert (anotherDirective.getSourceService() == null): "The Source Service should be null";
            assert (anotherDirective.getSourceProvider().equals(provider)): "The Source Provider should exist";

            pdService.deleteProcessingDirective(processingDirective);
            servicesService.deleteService(service);
            providerService.deleteProvider(provider);
            
    }


}
