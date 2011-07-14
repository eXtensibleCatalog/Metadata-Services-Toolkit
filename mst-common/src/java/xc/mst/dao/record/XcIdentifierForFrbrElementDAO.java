/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.record;

import java.util.HashMap;

import org.apache.log4j.Logger;

import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.MySqlConnectionManager;

/**
 * Class to get, cache, and update the next unique XC identifiers for elements at
 * each FRBR level. A Metadata Service can use the methods on this class to maintain
 * the correct values for the next XC identifier for a FRBR level while minimizing the
 * number of SQL queries it makes.
 *
 * @author Eric Osisek
 */
public abstract class XcIdentifierForFrbrElementDAO extends BaseDAO
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
	 * A static map from the FRBR element ID to the next XC Identifier for that FRBR element
	 * The implementing class should use this to cache values read from the database.
	 * This will allow the calling service to get unique IDs for all records (of which there
	 * may be millions) without having to query the database more than twice for the same row
	 * (once to get the last value, once to write the new value when the element finishes.)
	 */
	protected static HashMap<Integer, Long> nextXcIdForFrbrElement = new HashMap<Integer, Long>();

	/**
	 * The name of the database table we're interacting with.
	 */
	protected final static String XC_ID_FOR_FRBR_ELEMENTS_TABLE_NAME = "xc_id_for_frbr_elements";

	/**
	 * The name of the xc identifier for element ID column
	 */
	protected final static String COL_XC_IDENTIFIER_FOR_SERVICE_ID = "xc_id_for_element_id";

	/**
	 * The name of the next xc ID column
	 */
	protected final static String COL_NEXT_XC_ID = "next_xc_id";

	/**
	 * The name of the element ID column
	 */
	protected final static String COL_ELEMENT_ID = "element_id";

	/**
	 * The element ID signifying a work element
	 */
	public final static int ELEMENT_ID_WORK = 1;

	/**
	 * The element ID signifying a expression element
	 */
	public final static int ELEMENT_ID_EXPRESSION = 2;

	/**
	 * The element ID signifying a manifestation element
	 */
	public final static int ELEMENT_ID_MANIFESTATION = 3;

	/**
	 * The element ID signifying a holdings element
	 */
	public final static int ELEMENT_ID_HOLDINGS = 4;

	/**
	 * The element ID signifying a item element
	 */
	public final static int ELEMENT_ID_ITEM = 5;

	/**
	 * The element ID signifying a record that doesn't belong to any FRBR level
	 */
	public final static int ELEMENT_ID_RECORD = 6;

	/**
	 * Gets the next unused XC identifier for the element with the passed FRBR element ID.
	 * The returned XC identifier is considered to be used after it is returned by this
	 * method.
	 *
	 * After all the necessary calls to this method are completed, the calling code is
	 * expected to call the writeNextXcId method passing the same element ID.  This method
	 * returns an ID but the database is not updated with the new value until writeNextXcId
	 * is called.  This is to improve performance by reducing the number of SQL queries.
	 * Without this improvement the Metadata Services had a run time of days instead of hours.
	 *
	 * @param elementId The ID of the element whose next XC identifier we're getting
	 * @return The next unused XC identifier for the element with the passed ID.  This
	 *         XC identifier is considered to be used after it is returned.
	 */
	public abstract long getNextXcIdForFrbrElement(int elementId);

	/**
	 * Updates the next ID for the FRBR element in the database based on the current known
	 * value for that element This method should be called by a element after that
	 * element finishes assigning XC identifiers to records.
	 *
	 * @param elementId The FRBR element ID whose next XC ID is to be updated
	 * @return true on success, false on failure
	 */
	public abstract boolean writeNextXcId(int elementId);
	
	/**
	 * Updates the next ID for the FRBR element in the database based on the given
	 * value for that element. This method should be called when server is restarted after 
	 * unexpected shut down when service was running.
	 *
	 * @param elementId The FRBR element ID whose next XC ID is to be updated
     * @param nextXcId next XC ID to be used
	 */
	public abstract void writeNextXcId(int elementId, long nextXcId);
} // end class XcIdentifierForFrbrElementDAO
