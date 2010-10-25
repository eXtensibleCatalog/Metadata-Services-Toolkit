/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;
import xc.mst.bo.service.Service;
import xc.mst.bo.service.ServiceHarvest;
import xc.mst.constants.Constants;
import xc.mst.dao.BaseDAO;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

public abstract class ServiceDAO extends BaseDAO
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
	 * The name of the database table we're interacting with
	 */
	public final static String SERVICES_TABLE_NAME = "services";

	/**
	 * The name of the service ID column
	 */
	public final static String COL_SERVICE_ID = "service_id";

	/**
	 * The name of the service_name column
	 */
	public final static String COL_SERVICE_NAME = "service_name";

	/**
	 * The name of the class_name column
	 */
	public final static String COL_CLASS_NAME = "class_name";
	
	/**
	 * The name of the warnings column
	 */
	public final static String COL_WARNINGS = "warnings";

	/**
	 * The name of the errors column
	 */
	public final static String COL_ERRORS = "errors";

	/**
	 * The name of the input record count column
	 */
	public final static String COL_INPUT_RECORD_COUNT = "input_record_count";

	/**
	 * The name of the output record count column
	 */
	public final static String COL_OUTPUT_RECORD_COUNT = "output_record_count";

	/**
	 * The name of the last log reset column
	 */
	public final static String COL_LAST_LOG_RESET = "last_log_reset";

	/**
	 * The name of the log file name column
	 */
	public final static String COL_LOG_FILE_NAME = "log_file_name";

	/**
	 * The name of the warnings column
	 */
	public final static String COL_HARVEST_OUT_WARNINGS = "harvest_out_warnings";

	/**
	 * The name of the errors column
	 */
	public final static String COL_HARVEST_OUT_ERRORS = "harvest_out_errors";

	/**
	 * The name of the input record count column
	 */
	public final static String COL_HARVEST_OUT_RECORDS_AVAILABLE = "harvest_out_records_available";

	/**
	 * The name of the output record count column
	 */
	public final static String COL_HARVEST_OUT_RECORDS_HARVESTED = "harvest_out_records_harvested";

	/**
	 * The name of the last log reset column
	 */
	public final static String COL_HARVEST_OUT_LAST_LOG_RESET = "harvest_out_last_log_reset";

	/**
	 * The name of the log file name column
	 */
	public final static String COL_HARVEST_OUT_LOG_FILE_NAME = "harvest_out_log_file_name";
	
	/**
	 * The current status of the service.
	 */

	public final static String COL_STATUS = "status";
	
	/**
	 * The version of the service
	 */
	public final static String COL_VERSION = "version";
	
	/**
	 * Indicates service is deleted or not
	 */
	public final static String COL_DELETED = "is_deleted";
		
	
	/**
	 * A set of all columns which are valid for sorting
	 */
	protected static Set<String> sortableColumns = new HashSet<String>();
	
	// Initialize the list of sortable columns
	static
	{
		sortableColumns.add(COL_SERVICE_ID);
		sortableColumns.add(COL_SERVICE_NAME);
		sortableColumns.add(COL_CLASS_NAME);
		sortableColumns.add(COL_WARNINGS);
		sortableColumns.add(COL_ERRORS);
		sortableColumns.add(COL_INPUT_RECORD_COUNT);
		sortableColumns.add(COL_OUTPUT_RECORD_COUNT);
		sortableColumns.add(COL_LAST_LOG_RESET);
		sortableColumns.add(COL_LOG_FILE_NAME);
		sortableColumns.add(COL_HARVEST_OUT_WARNINGS);
		sortableColumns.add(COL_HARVEST_OUT_ERRORS);
		sortableColumns.add(COL_HARVEST_OUT_RECORDS_AVAILABLE);
		sortableColumns.add(COL_HARVEST_OUT_RECORDS_HARVESTED);
		sortableColumns.add(COL_HARVEST_OUT_LAST_LOG_RESET);
		sortableColumns.add(COL_HARVEST_OUT_LOG_FILE_NAME);
		sortableColumns.add(COL_STATUS);
		sortableColumns.add(COL_VERSION);
	} // end initialization of sortableColumns
	
	/**
	 * Gets all services from the database
	 *
	 * @return A list containing all services in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract ArrayList<Service> getAll() throws DatabaseConfigException;

	/**
     * Returns a sorted list of services
     * 
     * @param asc Determines whether the rows are sorted in ascending or descending order
     * @param columnSorted The coulmn on which rows are sorted
     * @return A sorted list of services
	 * @throws DatabaseConfigException if there was a problem connecting to the database
     */
	public abstract List<Service> getSorted(boolean asc,String columnSorted) throws DatabaseConfigException;
	
	/**
	 * Gets the service from the database with the passed service ID
	 *
	 * @param id The ID of the service to get
	 * @return The service with the passed service ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Service getById(int serviceId) throws DatabaseConfigException;

	/**
	 * Gets the service from the database with the passed service ID.
	 * This method does not set the input or output formats on the service.
	 *
	 * @param id The ID of the service to get
	 * @return The service with the passed service ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Service loadBasicService(int serviceId) throws DatabaseConfigException;

	/**
	 * Gets the service from the database with the passed name
	 *
	 * @param name The name of the service to get
	 * @return The service with the passed service Port
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract Service getByServiceName(String name) throws DatabaseConfigException;

	/**
	 * Inserts a service into the database
	 *
	 * @param service The service to insert
	 * @return True on success, false on failure
	 * @throws DataException if the passed Service was not valid for inserting
	 */
	public abstract boolean insert(Service service) throws DataException;

	/**
	 * Updates a service in the database
	 *
	 * @param service The service to update
	 * @return True on success, false on failure
	 * @throws DataException if the passed Service was not valid for inserting
	 */
	public abstract boolean update(Service service) throws DataException;

	/**
	 * Deletes a service from the database
	 *
	 * @param service The service to delete
	 * @return True on success, false on failure
	 * @throws DataException if the passed Service was not valid for inserting
	 */
	public abstract boolean delete(Service service) throws DataException;

	/**
	 * Validates the fields on the passed Service Object
	 *
	 * @param service The service to validate
	 * @param validateId true if the ID field should be validated
	 * @param validateNonId true if the non-ID fields should be validated
	 * @throws DataException If one or more of the fields on the passed service were invalid
	 */
	protected void validateFields(Service service, boolean validateId, boolean validateNonId) throws DataException
	{
		StringBuilder errorMessage = new StringBuilder();

		// Check the ID field if we're supposed to
		if(validateId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the ID");

			if(service.getId() < 0)
				errorMessage.append("The service id is invalid. ");
		} // end if(we should validate the ID field)

		// Check the non-ID fields if we're supposed to
		if(validateNonId)
		{
			if(log.isDebugEnabled())
				log.debug("Checking the non-ID fields");

			if(service.getName() == null || service.getName().length() <= 0 || service.getName().length() > 63)
				errorMessage.append("The service name is invalid. ");

			if(service.getClassName() == null || service.getClassName().length() <= 0 || service.getClassName().length() > 63)
				errorMessage.append("The class name is invalid. ");

			if(service.getServicesLogFileName() == null || service.getServicesLogFileName().length() <= 0 || service.getServicesLogFileName().length() > 255)
				errorMessage.append("The log file name is invalid. ");

			if(service.getHarvestOutLogFileName() == null || service.getHarvestOutLogFileName().length() <= 0 || service.getHarvestOutLogFileName().length() > 255)
				errorMessage.append("The harvest out log file name is invalid. ");

		} // end if(we should validate the non-ID fields)

		// Log the error and throw the exception if any fields are invalid
		if(errorMessage.length() > 0)
		{
			String errors = errorMessage.toString();
			log.error("The following errors occurred: " + errors);
			throw new DataException(errors);
		} // end if(we found an error)
	}
	
	public void persist(ServiceHarvest serviceHarvest) {
		log.debug("serviceHarvest.getId(): "+serviceHarvest.getId());
		if (serviceHarvest.getId() != null) {
			hibernateTemplate.update(serviceHarvest);	
		} else {
			hibernateTemplate.persist(serviceHarvest);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ServiceHarvest getServiceHarvest(Format format, xc.mst.bo.provider.Set set, String repoName, Service service) {
		List params = new ArrayList();
		
		StringBuilder sb = new StringBuilder(
				"from xc.mst.bo.service.ServiceHarvest as sh "+
				"where sh.service.id = ? "+
					"and sh.repoName = ? "
				);		
		params.add(service.getId());
		params.add(repoName);
		if (format != null) {
			sb.append("and sh.format.id = ? ");
			params.add(format.getId());
		} else {
			sb.append("and sh.format is null ");
		}
		if (set != null) {
			params.add(set.getId());
			sb.append("and sh.set.id = ? ");
		} else {
			sb.append("and sh.set is null ");
		}
		List<ServiceHarvest> shs = (List<ServiceHarvest>)this.hibernateTemplate.find(
				sb.toString(), params.toArray());
		if (shs == null || shs.size() == 0) {
			return null;
		} else if (shs.size() == 1) {
			return shs.get(0);
		} else {
			throw new RuntimeException("there shouldn't be more than one of these");
		}
	}
	
	public Service getService(int id) {
		return (Service)this.hibernateTemplate.load(Service.class, id);
	}
}
