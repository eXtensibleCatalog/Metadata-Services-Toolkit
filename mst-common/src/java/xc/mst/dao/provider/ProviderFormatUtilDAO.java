/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.provider;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Utility class for manipulating the metadata formats exposed by a provider
 *
 * @author Eric Osisek
 */
public abstract class ProviderFormatUtilDAO extends BaseDAO
{
	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**
	 * The Object managing the database connection
	 */
	protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();

	/**
	 * The name of the formats to providers database table
	 */
	public final static String FORMATS_TO_PROVIDERS_TABLE_NAME = "formats_to_providers";

	/**
	 * The name of the provider ID column
	 */
	public final static String COL_PROVIDER_ID = "provider_id";

	/**
	 * The name of the format ID column
	 */
	public final static String COL_FORMAT_ID = "format_id";

	/**
	 * Inserts a row in the database assigning a provider to a format.
	 *
	 * @param providerId The provider to assign to the format
	 * @param formatId The format to assign the provider to
	 * @return True on success, false on failure
	 */
	public abstract boolean insert(int providerId, int formatId);

	/**
	 * Deletes the row in the database assigning the provider to the format.
	 *
	 * @param providerId The provider to remove from the format
	 * @param formatId The format to remove the provider from
	 * @return True on success, false on failure
	 */
	public abstract boolean delete(int providerId, int formatId);

	/**
	 * Gets all formats to which a provider belongs
	 *
	 * @param providerId The ID of the provider whose formats should be returned
	 * @return A list of format IDs for the formats the provider belongs to
	 */
	public abstract List<Integer> getFormatsForProvider(int providerId);

	/**
	 * Deletes all formats assignments for a provider
	 *
	 * @param providerId The ID of the provider whose formats should be removed
	 * @return True on success, false on failure
	 */
	public abstract boolean deleteFormatsForProvider(int providerId);
} // end class ProviderFormatUtilDAO
