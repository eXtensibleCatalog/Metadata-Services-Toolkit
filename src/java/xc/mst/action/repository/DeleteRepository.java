
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.repository;

import com.opensymphony.xwork2.ActionSupport;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Provider;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveInputFormatUtilService;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveInputSetUtilService;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.ProcessingDirectiveInputFormatUtilService;
import xc.mst.manager.processingDirective.ProcessingDirectiveInputSetUtilService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.ProviderService;

/**
 *This class is used to delete a repository from the database
 *
 * @author Tejaswi Haramurali
 */
public class DeleteRepository extends ActionSupport
{
        
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** The ID of the repository to be deleted */
    private int repositoryId;

    /**
     * set the ID of the repository to be deleted
     * @param repoId The ID of the repository to be deleted
     */
    public void setRepositoryId(String repoId)
    {
        repositoryId = Integer.parseInt(repoId);
    }

    /**
     * gets the ID of the repository to be deleted
     * @return The ID of the repository to be deleted
     */
    public int getRepositoryId()
    {
        return repositoryId;
    }

    /**
     * Overrides default implementation to delete a repository.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            ProviderService providerService = new DefaultProviderService();
            ProcessingDirectiveService PDService = new DefaultProcessingDirectiveService();
            ProcessingDirectiveInputFormatUtilService PDIFService = new DefaultProcessingDirectiveInputFormatUtilService();
            ProcessingDirectiveInputSetUtilService PDISService = new DefaultProcessingDirectiveInputSetUtilService();
            Provider provider = providerService.getProviderById(repositoryId);
            List<ProcessingDirective> processingDirectivesList = PDService.getBySourceProviderId(repositoryId);
            Iterator iter = processingDirectivesList.iterator();
            while(iter.hasNext())
            {
                ProcessingDirective processingDirective = (ProcessingDirective)iter.next();
                PDIFService.deleteInputFormatsForProcessingDirective(processingDirective.getId());
                PDISService.deleteInputSetsForProcessingDirective(processingDirective.getId());
                PDService.deleteProcessingDirective(processingDirective);
            }
            providerService.deleteProvider(provider);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            this.addFieldError("viewRepositoryError", "Repository cannot be deleted");
            return INPUT;
        }
    }

}
