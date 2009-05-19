/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.repository;

import java.util.List;

import xc.mst.bo.provider.Format;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.FormatDAO;

/**
 * Service to access the formats
 *
 * @author Sharmila Ranganathan
 *
 */
public class DefaultFormatService implements FormatService {

	private FormatDAO formatDAO = new DefaultFormatDAO();

	/**
	 * Get format having the specified format id
	 *
	 * @param formatId Id of the format
	 * @return Format if exist else null
	 * @throws DatabaseConfigException 
	 */
	public Format getFormatById(int formatId) throws DatabaseConfigException {
		return formatDAO.getById(formatId);
	}

	/**
	 * Delete Format
	 *
	 * @param format format to be deleted
	 * @throws DataException Thrown when problem in deleting the format
	 */
	public void deleteFormat(Format format) throws DataException {
		formatDAO.delete(format);
	}

    /**
     * Add a format
     *
     * @param format format to inserted into the database
     */
    public void insertFormat(Format format) throws DataException {
    	formatDAO.insert(format);
    }

    /**
     * Get all formats
     *
     * @return all formats
     * @throws DatabaseConfigException 
     */
    public List<Format> getAllFormats() throws DatabaseConfigException {
    	return formatDAO.getAll();
    }

}
