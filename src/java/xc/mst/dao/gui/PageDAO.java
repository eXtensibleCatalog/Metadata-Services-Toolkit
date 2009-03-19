/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.gui;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.gui.Page;
import xc.mst.bo.gui.SubTab;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;

public abstract class PageDAO
{
	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The connection to the database
	 */
	protected final static Connection dbConnection = MySqlConnectionManager.getDbConnection();

	/**
	 * The name of the database table we're interacting with
	 */
	public final static String PAGES_TABLE_NAME = "pages";

	/**
	 * The name of the page ID column
	 */
	public final static String COL_PAGE_ID = "page_id";

	/**
	 * The name of the page number column
	 */
	public final static String COL_PAGE_NUMBER = "page_number";

	/**
	 * The name of the name column
	 */
	public final static String COL_NAME = "name";

	/**
	 * The name of the title column
	 */
	public final static String COL_TITLE = "title";

	/**
	 * The name of the sub_tab_id column
	 */
	public final static String COL_SUB_TAB_ID = "sub_tab_id";

	/**
	 * Gets all pages from the database
	 *
	 * @return A List of all the pages in the database
	 */
	public abstract List<Page> getAll();

	/**
	 * Gets the page from the database with the passed ID
	 *
	 * @param pageId The ID of the page to get
	 * @return The page with the passed ID
	 */
	public abstract Page getById(int pageId);

	/**
	 * Gets the page from the database with the passed Page Number
	 *
	 * @param id The page number of the page to get
	 * @return The page with the passed page number
	 */
	public abstract List<Page> getByPageNumber(int pageNumber);

	/**
	 * Gets all pages from the database for the passed subtab
	 *
	 * @param subtab The subTab whose pages should be returned
	 * @return A list of all pages belonging to the passed subtab
	 */
	public abstract List<Page> getPagesForSubtab(SubTab subtab);

    /**
	 * Gets the page from the database with the passed ID
	 *
	 * @param name The name of the page to get
	 * @return The page with the passed ID
	 */
	public abstract Page getByPageName(String name);

	/**
	 * Inserts a page into the database
	 *
	 * @param page The page to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed page was not valid for inserting
	 */
	public abstract boolean insert(Page page);

	/**
	 * Updates a page on the database
	 *
	 * @param page The page to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed page was not valid for updating
	 */
	public abstract boolean update(Page page);

	/**
	 * Deletes a page from the database
	 *
	 * @param page The page to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed page was not valid for deleting
	 */
	public abstract boolean delete(Page page);

	/**
	 * Validates the fields on the passed Page Object
	 *
	 * @param schedule The page to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed page were invalid
	 */
	protected void validateFields(Page page, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(page.getId() < 0)
				errorMessage.append("The page ID is invalid. ");
		} // end if(we should validate the ID)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(page.getName() == null || page.getName().length() <= 0 || page.getName().length() > 67)
				errorMessage.append("The name is invalid. ");

			if(page.getTitle() != null && page.getTitle().length() > 63)
				errorMessage.append("The title is invalid. ");

			if(page.getPageNumber() < 0)
				errorMessage.append("The page_number is invalid. ");
		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(error found)
	} // end method ValidateFields(Page, boolean, boolean)
} // end class PageDAO
