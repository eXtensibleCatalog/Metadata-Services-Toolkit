/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.Job;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordType;
import xc.mst.bo.service.Service;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;
import xc.mst.email.Emailer;
import xc.mst.manager.BaseService;
import xc.mst.manager.IndexException;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.ServiceUtil;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.index.Records;
import xc.mst.utils.index.SolrIndexManager;

/**
 * A copy of the MST is designed to interface with one or more Metadata Services depending on how it's configured.
 * There are several Metadata Services which may be used, each one of which extends the MetadataService
 * class.  The MetadataService class provides a common interface through which the MST can invoke functionality on
 * a Metadata Service.
 *
 * @author Eric Osisek
 */

public abstract class MetadataService extends BaseService
{
	/**
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);

	/**
	 * The service representing this service in the database
	 */
	protected Service service = null;

	/**
	 * The processing directives for this service
	 */
	private List<ProcessingDirective> processingDirectives = null;

	/**
	 * The number of warnings in running the current service
	 */
	private int warningCount = 0;

	/**
	 * The number of errors in running the current service
	 */
	private int errorCount = 0;
	
	/**
	 * The number of errors in running the current service per commit
	 */
	private int errorCountPerCommit = 0;

	/**
	 * Used to send email reports
	 */
	private Emailer mailer = new Emailer();

	/**
	 * A list of services to run after this service's processing completes
	 * The keys are the service IDs and the values are the IDs of the sets
	 * that service's records should get added to
	 */
	private HashMap<Integer, Integer> servicesToRun = new HashMap<Integer, Integer>();

	/**
	 * The flag for indicating cancel service operation.
	 */
	private boolean isCanceled;

	/**
	 * The flag for indicating service operation pause.
	 */
	private boolean isPaused;

	/**
	 * The number of records processed by the service so far
	 */
	protected int processedRecordCount = 0;
	
	/**
	 * The number of records the service needs to process
	 */
	private int totalRecordCount = 0;

	/**
	 * The number of input records 
	 */
	protected int inputRecordCount = 0;
	
	/**
	 * The output set id 
	 */
	protected Set outputSet;

	/** Stores the unprocessed record identifiers */
	protected ArrayList<String> unprocessedErrorRecordIdentifiers = new ArrayList<String>();
	
	private long startTime = new Date().getTime();
	private long endTime = 0;
	private long timeDiff = 0;

	/**
	 * 
	 * @param serviceId
	 * @param outputSetId
	 */
	public final void runService(int serviceId, int outputSetId) {
		
		try {
			
			// Set the service's ID and name
			service = getServiceDAO().getById(serviceId);
			
			// Load the service's configuration
			loadConfiguration(service.getServiceConfig());
	
			// Create the list of ProcessingDirectives which could be run on records processed from this service
			setProcessingDirectives(getProcessingDirectiveDAO().getBySourceServiceId(serviceId));
	
			if(log.isDebugEnabled())
				log.debug("Constructed the MetadataService Object, running its processRecords() method.");
	
			LogWriter.addInfo(service.getServicesLogFileName(), "Starting the " + service.getName() + " Service.");
	
			if(log.isDebugEnabled())
				log.debug("Validating the Metadata Service with ID " + serviceId + ".");
	
			ServiceUtil.getInstance().checkService(service,Constants.STATUS_SERVICE_RUNNING, true);
	
			if(log.isDebugEnabled())
				log.debug("Running the Metadata Service with ID " + serviceId + ".");
	
			setOutputSet(getSetDAO().getById(outputSetId));
			
			// Run the service's processRecords method
			boolean success = processRecords();
	
			LogWriter.addInfo(service.getServicesLogFileName(), "The " + service.getName() + " Service finished running.  " + processedRecordCount + " records processed." + (processedRecordCount - errorCount) + " records were processed successfully. " + errorCount + " were not processed due to error.");
	
			// Update database with status of service
			if(!isCanceled && success)
				setStatus(Constants.STATUS_SERVICE_NOT_RUNNING);
	
			sendReportEmail(null);
			
		} catch (DatabaseConfigException dce) {
				log.error("Exception occurred while invoking the service's processRecords method.", dce);

				// Update database with status of service
				service.setStatus(Constants.STATUS_SERVICE_ERROR);
				sendReportEmail("Exception occurred while invoking the service's processRecords method.");
				
				LogWriter.addError(service.getServicesLogFileName(), "An internal error occurred while trying to start the " + service.getName() + " Service.");

				// Load the provider again in case it was updated during the harvest
				try
				{
					service = getServiceDAO().getById(service.getId());
				}
				catch (DatabaseConfigException e1){
					
					log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				}

				// Increase the warning and error counts as appropriate, then update the provider
				service.setServicesErrors(service.getServicesErrors() + 1);

				try
				{
					getServiceDAO().update(service);
				}
				catch (DataException e2)
				{
					log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
				}

		}
	}
		
	/**
	 * This method gets called to give the service the service specific configuration
	 * which was defined for it in its configuration file.
	 *
	 * @param config The service specific configuration defined in the service's configuration file
	 */
	public abstract void loadConfiguration(String config);

	/**
	 * The MST calls this method to signal the Metadata Service to process the records.  Depending on the
	 * service, this method might look at all records in the database or it might just look at the
	 * unprocessed ones.  The type of processing that occurs will also be service specific.
	 *
	 * This method will process as many records as possible, creating a new list of records which contains
	 * the records which resulted from processing the existing ones.  Each record in the Lucene index will
	 * store a list of the record(s) it was processed from.  A record may be processed from multiple
	 * records, and more than one record may be processed from a single record.
	 *
	 * This method will return true if all processing worked perfectly and false if there were errors.  If
	 * it returns false, it will still have performed as much processing as possible.
	 *
	 * @param outputSetId The set to which processed records should be added, or -1 if they should not be added to an additional set
	 * @return true if all processing worked perfectly, false if there were errors.
	 */
	public boolean processRecords()
	{
		TimingLogger.start("processRecords");
		try
		{
			// Get the list of record inputs for this service
			List<Record> records = getRecordService().getInputForServiceToProcess(service.getId());
			totalRecordCount = records.size();
			log.info("Number of records to be processed by service = " + totalRecordCount);
			
			startTime = new Date().getTime();
			endTime = 0;
			timeDiff = 0;

			// Getting Record Types with the processing priority
			List<RecordType> recordTypes = recordTypeDAO.getAll();
			
			// If the record processing info is present in DB
			TimingLogger.start("processRecords.recordTypes.size() != 0");
			if(recordTypes.size() != 0){
			
				// Process the records which have record_type info
				for (int i = 0; i < recordTypes.size(); i++) {
					// First query for records type using record types
					RecordType recordType = recordTypes.get(i);
					TimingLogger.start("recordService.getByInputToServiceAndRecordType");
					Records inputRecords = getRecordService().getByInputToServiceAndRecordType(service.getId(), recordType.getName());
					TimingLogger.stop("recordService.getByInputToServiceAndRecordType");
					if (inputRecords != null && inputRecords.size() >0) {
						processRecordBatch(inputRecords);
						
						// Commit the records so that next record type can use that for processing 
						TimingLogger.start("331.commitIndex");
						SolrIndexManager.getInstance().commitIndex();
						TimingLogger.stop("331.commitIndex");
						updateServiceStatistics();
						
						// Updates the database with latest record id and OAI identifier used.
						// So that in case of server down, the service will resume correctly.  
						TimingLogger.start("updateOAIRecordIds");
						updateOAIRecordIds();
						TimingLogger.stop("updateOAIRecordIds");

					}
				}
			}
			TimingLogger.start("processRecords.recordTypes.size() != 0");

			// Now process the records with no record_type info
			TimingLogger.start("getInputForServiceToProcess");
			List<Record> inputRecords  = getRecordService().getInputForServiceToProcess(service.getId());
			TimingLogger.stop("getInputForServiceToProcess");
			processRecordBatch(inputRecords);

			// Reopen the reader so it can see the changes made by running the service
			TimingLogger.start("processRecords.352.commitIndex");
			SolrIndexManager.getInstance().commitIndex();
			TimingLogger.stop("processRecords.352.commitIndex");
			
			endTime = new Date().getTime();
			timeDiff = endTime - startTime;
			LogWriter.addInfo(service.getServicesLogFileName(), "Processed " + processedRecordCount + " records so far. Time taken = " + (timeDiff / (1000*60*60)) + "hrs  " + ((timeDiff % (1000*60*60)) / (1000*60)) + "mins  " + (((timeDiff % (1000*60*60)) % (1000*60)) / 1000) + "sec  " + (((timeDiff % (1000*60*60)) % (1000*60)) % 1000) + "ms  ");
			
			
			return true;
		} // end try(process the records)
		catch(Exception e)
		{
			log.error("An error occurred while running the service with ID " + service.getId() + ".", e);
			
			try {
				// Commit processed records to index
				SolrIndexManager.getInstance().commitIndex();
			} catch (IndexException ie) {
				log.error("Exception occured when commiting " + service.getName() + " records to index", e);
			}
			
			// Update database with status of service
			setStatus(Constants.STATUS_SERVICE_ERROR);
			
			return false;
		} // end catch(Exception)
		finally // Update the error and warning count for the service
		{
			TimingLogger.stop("processRecords");
			if (!updateServiceStatistics()) {
				return false;
			}
			
			// Updates the database with latest record id and OAI identifier used.
			TimingLogger.start("updateOAIRecordIds");
			updateOAIRecordIds();
			TimingLogger.stop("updateOAIRecordIds");
		} // end finally(write the next IDs to the database)
	} // end method processRecords(int)

	/*
	 * Updates the database with latest record id and OAI identifier used.
	 * So that in case of server down, the service will resume correctly.  
	 */
	private void updateOAIRecordIds() {
		// Update the next OAI ID for this service in the database
		getOaiIdentifierForServiceDAO().writeNextOaiId(service.getId());

		// Update the next XC ID for all elements in the database
		getXcIdentifierForFrbrElementDAO().writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_WORK);
		getXcIdentifierForFrbrElementDAO().writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_EXPRESSION);
		getXcIdentifierForFrbrElementDAO().writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_MANIFESTATION);
		getXcIdentifierForFrbrElementDAO().writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_HOLDINGS);
		getXcIdentifierForFrbrElementDAO().writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_ITEM);
		getXcIdentifierForFrbrElementDAO().writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_RECORD);
	}
	
	/**
	 * Update number of warnings, errors, records available, output & input records count
	 */
	protected boolean updateServiceStatistics() {
		// Load the provider again in case it was updated during the harvest
		Service service = null;
		try
		{
			service = getServiceDAO().getById(this.service.getId());
		}
		catch (DatabaseConfigException e1)
		{
			log.error("DatabaseConfig exception occured when getting service from database to update error, warning count.", e1);

			return false;
		}

		// Increase the warning and error counts as appropriate, then update the provider
		service.setServicesWarnings(service.getServicesWarnings() + warningCount);
		service.setServicesErrors(service.getServicesErrors() + errorCountPerCommit);
		service.setInputRecordCount(service.getInputRecordCount() + inputRecordCount);
		try {
			long harvestOutRecordsAvailable = getRecordService().getCount(null, null, null, -1, service.getId());
			service.setHarvestOutRecordsAvailable(harvestOutRecordsAvailable);
			service.setOutputRecordCount((int) harvestOutRecordsAvailable);

			/* In case of Normalization Service update, the deleted records are updated.
			 * So input record count will be zero, so count of output records is assigned to input count
			 * Since for normalization it is 1:1 i/p record to o/p.
			 */
			if (service.getInputRecordCount() == 0) {
				service.setInputRecordCount((int) harvestOutRecordsAvailable);
			}

		} catch (IndexException ie) {
			log.error("Index exception occured while querying Solr for number of output records in service " + service.getName() + ".", ie);
			return false;
		}
		
		try
		{
			getServiceDAO().update(service);
		}
		catch (DataException e)
		{
			log.error("Unable to update the service's warning and error counts due to a Data Exception.", e);
			return false;
		}
		
		warningCount = 0;
		errorCountPerCommit = 0;
		inputRecordCount = 0;

		return true;

	}

	/**
	 * Processes the records in the given set.
	 * @throws IndexException 
	 * @throws InterruptedException 
	 */
	protected void processRecordBatch(List<Record> records) throws IndexException, InterruptedException, Exception{
		
		// Iterate over the list of input records and process each.
		// Then run the processing directives on the results of each and add
		// the appropriate record inputs for services to be run on the records
		// resulting from the processing.  Also maintain a list of services to
		// be invoked after this service is finished.  Finally, add the records
		// resulting from this service.

		TimingLogger.start("processRecordBatch.iter");
		for(Record processMe : records)
		{
			TimingLogger.stop("processRecordBatch.iter");
			// If the service is not canceled and not paused then continue
			if(!isCanceled && !isPaused)
			{
				
				// Process the record
				processRecord(processMe);
				
				TimingLogger.start("processRecordBatch.iter");
				// Commit after 100k records
				if(processedRecordCount != 0 && processedRecordCount % 100000 == 0)
				{
					TimingLogger.start("491.commit");
					SolrIndexManager.getInstance().commitIndex();
					TimingLogger.stop("491.commit");
					
					TimingLogger.start("updateServiceStatistics");
					updateServiceStatistics();
					TimingLogger.stop("updateServiceStatistics");
					
					// Updates the database with latest record id and OAI identifier used.
					// So that in case of server down, the service will resume correctly.  
					TimingLogger.start("updateOAIRecordIds");
					updateOAIRecordIds();
					TimingLogger.stop("updateOAIRecordIds");
					
					endTime = new Date().getTime();
					timeDiff = endTime - startTime;
					
					LogWriter.addInfo(service.getServicesLogFileName(), 
							"Processed " + processedRecordCount + " records so far. Time taken = " 
							+ (timeDiff / (1000*60*60)) + "hrs  " + ((timeDiff % (1000*60*60)) / (1000*60)) + "mins  " 
							+ (((timeDiff % (1000*60*60)) % (1000*60)) / 1000) + "sec  " 
							+ (((timeDiff % (1000*60*60)) % (1000*60)) % 1000) + "ms  "
					);
					
					startTime = new Date().getTime();
					
					TimingLogger.reset(false);
				}
			}
			else {
					// If canceled the stop processing records
					if(isCanceled)
						{
							LogWriter.addInfo(service.getServicesLogFileName(), "Cancelled Service " + service.getName());
							LogWriter.addInfo(service.getServicesLogFileName(), "Processed " + processedRecordCount + " records so far.");
							// Update database with status of service
							setStatus(Constants.STATUS_SERVICE_CANCELED);
							break;
						}
					// If paused then wait
					else if(isPaused)
						{
							LogWriter.addInfo(service.getServicesLogFileName(), "Paused Service " + service.getName());
							// Update database with status of service
							setStatus(Constants.STATUS_SERVICE_PAUSED);

							while(isPaused && !isCanceled)
								{
									LogWriter.addInfo(service.getServicesLogFileName(), "Service Waiting to resume" );
									Thread.sleep(3000);
								}
							// If the service is canceled after it is paused, then exit
							if(isCanceled)
							{
								LogWriter.addInfo(service.getServicesLogFileName(), " Cancelled Service " + service.getName());
								// Update database with status of service
								setStatus(Constants.STATUS_SERVICE_CANCELED);
								break;

							}
							// If the service is resumed after it is paused, then continue
							else
							{
								LogWriter.addInfo(service.getServicesLogFileName(), "Resumed Service " + service.getName());
								// Update database with status of service
								setStatus(Constants.STATUS_SERVICE_RUNNING);

							}

						}
				}
		} // end loop over records to process
		TimingLogger.stop("processRecordBatch.iter");
	}
	
	/**
	 * Gets the cancel status of the service.
	 * @return true if service is canceled else false
	 */
	public boolean isCanceled() {
		return isCanceled;
	}

	/**
	 * Gets the pause status of the service.
	 * @return true if service is paused else false
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Sets the pause status of the service.
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * Sets the cancel status of the service.
	 * @param isCanceled Flag indicating the cancel status of the service
	 */
	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	/**
	 * Gets the name for this service
	 *
	 * @return This service's name
	 */
	public String getServiceName()
	{
		return service.getName();
	} // end method getServiceName()

	/**
	 * Gets the status of the service
	 * @return This service's status
	 */
	public String getServiceStatus(){

		if(isCanceled)
			return Constants.STATUS_SERVICE_CANCELED;
		else if(isPaused)
			return Constants.STATUS_SERVICE_PAUSED;
		else 
			return Constants.STATUS_SERVICE_RUNNING;
	}

	/**
	 * Gets the count of records processed
	 * @return the processedRecordCount
	 */
	public int getProcessedRecordCount() {
		return processedRecordCount;
	}

	/**
	 * Gets the count of total records
	 * @return the totalRecordCount
	 */
	public int getTotalRecordCount() {
		return totalRecordCount;
	}

	/**
	 * This method validates that the service is able to be run.
	 *
	 * @throws ServiceValidationException When the service is invalid
	 */
	protected abstract void validateService() throws ServiceValidationException;

	/**
	 * This method processes a single record.
	 *
	 * @param record The record to process
	 * @return A list of outgoing records that should be added, modified, or deleted
	 *         as a result of processing the incoming record
	 */
	protected abstract void processRecord(Record record) throws Exception ;

	/**
	 * Refreshes the index so all records are searchable.
	 */
	protected void refreshIndex()
	{
		try
		{
			SolrIndexManager.getInstance().waitForJobCompletion(5000);
			SolrIndexManager.getInstance().commitIndex();
		}
		catch (IndexException e)
		{
			log.error("An error occurred while commiting new records to the Solr index.", e);
		}
	}

	/**
	 * Updates a record in the index
	 * @param record The record to be updated.
	 */
	protected void updateRecord(Record record)
	{
		try
		{
			record.setUpdatedAt(new Date());
			getRecordService().update(record);
		}
		catch (IndexException e)
		{
			log.error("An error occurred while updating a record in the Solr index.", e);
		}
		catch (DataException e)
		{
			log.error("An error occurred while updating a record in the Solr index.", e);
		}
	}
	
	/**
	 * Gets the next OAI identifier for the service
	 *
	 * @return The next OAI identifier for the service
	 */
	public final String getNextOaiId()
	{
		return "oai:" + MSTConfiguration.getProperty(Constants.CONFIG_DOMAIN_NAME_IDENTIFIER) + ":" + 
				MSTConfiguration.getInstanceName() + "/" + service.getIdentifier().replace(" ", "_") + "/" + 
				getOaiIdentifierForServiceDAO().getNextOaiIdForService(service.getId());
	}
		
	/**
	 * Adds a new set to the database
	 *
	 * @param setSpec The setSpec of the new set
	 * @param setName The display name of the new set
	 * @param setDescription A description of the new set
	 * @throws DataException If an error occurred while adding the set
	 */
	protected Set addSet(String setSpec, String setName, String setDescription) throws DataException {
		Set set = new Set();
		set.setSetSpec(setSpec);
		set.setDescription(setDescription);
		set.setDisplayName(setName);
		set.setIsRecordSet(true);
		set.setIsProviderSet(false);
		getSetDAO().insert(set);
		return set;
	}

	/**
	 * Logs an info message in the service's log file
	 *
	 * @param message The message to log
	 */
	public final void logInfo(String message){
		
		LogWriter.addInfo(service.getServicesLogFileName(), message);
	}

	/**
	 * Logs a warning message in the service's log file
	 *
	 * @param message The message to log
	 */
	public final void logWarning(String message){ 
		
		LogWriter.addWarning(service.getServicesLogFileName(), message);
		warningCount++;
	}

	/**
	 * Logs an error message in the service's log file
	 *
	 * @param message The message to log
	 */
	public final void logError(String message){
		
		LogWriter.addError(service.getServicesLogFileName(), message);
		errorCount++;
		errorCountPerCommit++;
	}

	/**
	 * Inserts a record in the Lucene index and sets up RecordInput values
	 * for any processing directives the record matched so the appropriate
	 * services process the record
	 *
	 * @param record The record to insert
	 */
	protected void insertNewRecord(Record record) throws DataException, IndexException {
		try
		{
			record.setService(service);

			// Run the processing directives against the record we're inserting
			checkProcessingDirectives(record);

			if(!getRecordService().insert(record))
				log.error("Failed to insert the new record with the OAI Identifier " + record.getOaiIdentifier() + ".");
		} // end try(insert the record)
		catch (DataException e)
		{
			log.error("An exception occurred while inserting the record into the Lucene index.", e);
			throw e;
		} // end catch(DataException)
		catch (IndexException ie) {
			log.error("An exception occurred while inserting the record into the index.", ie);
			throw ie;
		}
	} // end method insertNewRecord(Record)

	/**
	 * Updates a record in the Lucene index and sets up RecordInput values
	 * for any processing directives the record matched so the appropriate
	 * services reprocess the record after the update
	 *
	 * @param newRecord The record as it should look after the update (the record ID is not set)
	 * @param oldRecord The record in the Lucene index which needs to be updated
	 */
	protected void updateExistingRecord(Record newRecord, Record oldRecord) throws DataException, IndexException {
		try
		{
			// Set the new record's ID to the old record's ID so when we call update()
			// on the new record it will update the correct record in the Lucene index
			newRecord.setId(oldRecord.getId());
			newRecord.setUpdatedAt(new Date());
			newRecord.setService(service);

			// Run the processing directives against the updated record
			checkProcessingDirectives(newRecord);

			// Update the record.  
			if(!getRecordService().update(newRecord)) {
				log.error("The update failed for the record with ID " + newRecord.getId() + ".");
			}
			
		} // end try(update the record)
		catch (DataException e)
		{
			log.error("An exception occurred while updating the record into the index.", e);
			throw e;
		} // end catch(DataException)
		catch (IndexException ie) {
			log.error("An exception occurred while updating the record into the index.", ie);
			throw ie;
		}
	} // end method updateExistingRecord(Record, Record)

	/**
	 * Logs the status of the service to the database
	 * @throws DataException
	 */
	public void setStatus(String status){

		// Load the provider again in case it was updated during the harvest
		Service service = null;
		try
		{
			service = getServiceDAO().getById(this.service.getId());
			LogWriter.addInfo(service.getServicesLogFileName(), "Setting the status of the service " +service.getName() +" as:" +status);
			service.setStatus(status);
			getServiceDAO().update(service);
		}
		catch (DatabaseConfigException e1)
		{
			log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

		} catch(DataException e)
		{
			log.error("An error occurred while updating service status to database for service with ID" + service.getId() + ".", e);
		}
	}

	/**
	 * Sets the list of processing directives for this service
	 *
	 * @param processingDirectives The list of processing directives which should be run on records processed by this service
	 */
	private void setProcessingDirectives(List<ProcessingDirective> processingDirectives)
	{
		this.processingDirectives = processingDirectives;
	} // end method setProcessingDirectives(List<ProcessingDirective>)

	/**
	 * Runs the processing directives for this service against the record.  For all matching
	 * processing directives, adds the appropriate recordInput objects to the Lucene index.
	 * Also adds the service ID for all matched processing directives to the list of services
	 * to run when this service finishes.
	 *
	 * @param record The record to match against the processing directives
	 */
	protected void checkProcessingDirectives(Record record)
	{
		// Don't check processing directives for subclasses of Record
		if(!record.getClass().getName().equals("xc.mst.bo.record.Record"))
			return;

		// Maintain a list of processing directives which were matched
		ArrayList<ProcessingDirective> matchedProcessingDirectives = new ArrayList<ProcessingDirective>();

		boolean matchedFormat = false;
		boolean matchedSet = false;

		// Loop over the processing directives and check if any of them match the record
		for(ProcessingDirective processingDirective : processingDirectives)
		{
			matchedFormat = false;
			matchedSet = false;
			
			// Check if the record matches any of the metadata formats for the current processing directive
			if(processingDirective.getTriggeringFormats().contains(record.getFormat())) {
				matchedFormat = true;
			}

			// check if the record is in any of the sets for the current processing directive
			if(processingDirective.getTriggeringSets() != null && processingDirective.getTriggeringSets().size() > 0)  {
				for(Set set : record.getSets())
				{
					if(processingDirective.getTriggeringSets().contains(set))
					{
						matchedSet = true;
						break;
					} 
				}
			} else {
				matchedSet = true;
			}
			
			if (matchedFormat && matchedSet) {
				matchedProcessingDirectives.add(processingDirective);
			}
		} // end loop over processing directives

		// Loop over the matched processing directives.  Add the appropriate record inputs and add the
		// correct services to the list of services to run after the harvest completes
		for(ProcessingDirective matchedProcessingDirective : matchedProcessingDirectives)
		{
			record.addInputForService(matchedProcessingDirective.getService());
			record.removeProcessedByService(matchedProcessingDirective.getService());

			Integer serviceId = new Integer(matchedProcessingDirective.getService().getId());

			if(!servicesToRun.containsKey(serviceId)) {
				
				int outputSetId = new Integer(matchedProcessingDirective.getOutputSet() == null ? 0 : matchedProcessingDirective.getOutputSet().getId());
				servicesToRun.put(serviceId, outputSetId);

				// Add jobs to database
				try {
					Job job = new Job(matchedProcessingDirective.getService(), outputSetId, Constants.THREAD_SERVICE);
					job.setOrder(getJobService().getMaxOrder() + 1); 
					getJobService().insertJob(job);
				} catch (DatabaseConfigException dce) {
					log.error("DatabaseConfig exception occured when ading jobs to database", dce);
				}

			}
		} // end loop over matched processing directives
	} // end method checkProcessingDirectives(Record)

	/**
	 * Reprocesses the passed record by all service that had processed it in the past
	 *
	 * @param record The record to reprocess
	 */
	protected void reprocessRecord(Record record)
	{
		for(Service processingService : record.getProcessedByServices())
		{
			record.addInputForService(processingService);

			Integer serviceId = processingService.getId();

			if(!servicesToRun.containsKey(serviceId)) {
				servicesToRun.put(serviceId, 0);
				
				// Add jobs to database
				try {
					Job job = new Job(processingService, 0, Constants.THREAD_SERVICE);
					job.setOrder(getJobService().getMaxOrder() + 1); 
					getJobService().insertJob(job);
				} catch (DatabaseConfigException dce) {
					log.error("DatabaseConfig exception occured when ading jobs to database", dce);
				}
			}

		}
	}

	/**
	 * Builds and sends an email report about the harvest to the schedule's notify email address.
	 *
	 * @param problem The problem which prevented the harvest from finishing, or null if the harvest was successful
	 */
	public boolean sendReportEmail(String problem)
	{
		try {
			
			if (mailer.isConfigured()) {
				
				// The email's subject
				InetAddress addr = null;
				addr = InetAddress.getLocalHost();

				String subject = "Results of processing " + getServiceName() +" by MST Server on " + addr.getHostName();
		
				// The email's body
				StringBuilder body = new StringBuilder();
		
				// First report any problems which prevented the harvest from finishing
				if(problem != null)
					body.append("The service failed for the following reason: ").append(problem).append("\n\n");

				// Report on the number of records inserted successfully and the number of failed inserts
				body.append("Total number of records to process = " + totalRecordCount);
				body.append("\nNumber of records processed successfully = " + (processedRecordCount - errorCount));
				
				if(errorCount > 0) {
					body.append("\nNumber of records not processed due to error = " + errorCount);
					body.append("\nPlease login into MST and goto Menu -> Logs -> Services to see the list of failed records and the reason for failure.");
				}
				
				// Send email to every admin user
				for (User user : userGroupUtilDAO.getUsersForGroup(groupDAO.getByName(Constants.ADMINSTRATOR_GROUP).getId())) {
					mailer.sendEmail(user.getEmail(), subject, body.toString());
				}
				
				return true;
			} 
			else {
				return false;
			}

		}
		catch (UnknownHostException exp) {
			log.error("Host name query failed. Error sending notification email.",exp);
			return false;
		}
		catch (DatabaseConfigException e) {
			log.error("Database connection exception. Error sending notification email.");
			return false;
		}
		catch (Exception e) {
			log.error("Error sending notification email.");
			return false;
		}
	} // end method sendReportEmail

	public ArrayList<String> getUnprocessedErrorRecordIdentifiers() {
		return unprocessedErrorRecordIdentifiers;
	}

	public void setUnprocessedErrorRecordIdentifiers(
			ArrayList<String> unprocessedErrorRecordIdentifiers) {
		this.unprocessedErrorRecordIdentifiers = unprocessedErrorRecordIdentifiers;
	}

	public Set getOutputSet() {
		return outputSet;
	}

	public void setOutputSet(Set outputSet) {
		this.outputSet = outputSet;
	}

	public abstract void setInputRecordCount(int inputRecordCount);
	
	

}