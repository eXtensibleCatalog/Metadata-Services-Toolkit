/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


package xc.mst.manager.test;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.helper.TestHelper;


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
    public void addProcDir()
    {
      	 // Initialize Solr, database, log before testing
      	 TestHelper helper = TestHelper.getInstance();
        try
        {
            ProcessingDirectiveService processingDirectiveService = new DefaultProcessingDirectiveService();
            ServicesService servicesService = new DefaultServicesService();
            List setList = new ArrayList();
            List formatList = new ArrayList();

            ProcessingDirective processingDirective = new ProcessingDirective();
            processingDirective.setId(10001);
            processingDirective.setMaintainSourceSets(false);
            processingDirective.setOutputSet(null);
            processingDirective.setService(servicesService.getServiceById(1));
            processingDirective.setSourceService(servicesService.getServiceById(1));
            processingDirective.setSourceProvider(null);
            processingDirective.setTriggeringFormats(formatList);
            processingDirective.setTriggeringSets(setList);
            processingDirectiveService.insertProcessingDirective(processingDirective);

            ProcessingDirective anotherDirective = processingDirectiveService.getByProcessingDirectiveId(processingDirective.getId());
            assert (anotherDirective.getOutputSet()==null): "The output set is should be null";
            assert (anotherDirective.getService().getId()==1): "The service ID should be 1";
            assert (anotherDirective.getSourceService().getId()==1): "The Source Service ID should be 1";
            assert (anotherDirective.getSourceProvider()==null): "The Source Provider is null";

            processingDirectiveService.deleteProcessingDirective(processingDirective);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


}
