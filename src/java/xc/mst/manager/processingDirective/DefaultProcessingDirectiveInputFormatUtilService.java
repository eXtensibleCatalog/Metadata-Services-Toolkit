/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.processingDirective;

import java.util.List;

import xc.mst.dao.processing.DefaultProcessingDirectiveInputFormatUtilDAO;
import xc.mst.dao.processing.ProcessingDirectiveInputFormatUtilDAO;

/**
 *
 * @author tejaswih
 */
public class DefaultProcessingDirectiveInputFormatUtilService implements ProcessingDirectiveInputFormatUtilService
{
    ProcessingDirectiveInputFormatUtilDAO pdifUtilDao;

    public DefaultProcessingDirectiveInputFormatUtilService()
    {
        pdifUtilDao = new DefaultProcessingDirectiveInputFormatUtilDAO();
    }

	public void insertPDIF(int processingDirectiveId, int formatId)
    {
        pdifUtilDao.insert(processingDirectiveId, formatId);
    }

	public void deletePDIF(int processingDirectiveId, int formatId)
    {
        pdifUtilDao.delete(processingDirectiveId, formatId);
    }

	public  List<Integer> getInputFormatsForProcessingDirective(int processingDirectiveId)
    {
        return pdifUtilDao.getInputFormatsForProcessingDirective(processingDirectiveId);
    }

	public void deleteInputFormatsForProcessingDirective(int processingDirectiveId)
    {
        pdifUtilDao.deleteInputFormatsForProcessingDirective(processingDirectiveId);
    }
}
