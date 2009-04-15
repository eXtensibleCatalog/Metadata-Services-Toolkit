/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.log.LogDAO;
import xc.mst.dao.processing.DefaultProcessingDirectiveDAO;
import xc.mst.dao.processing.ProcessingDirectiveDAO;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultSetDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.SetDAO;
import xc.mst.dao.record.DefaultXcIdentifierForFrbrElementDAO;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;
import xc.mst.dao.service.DefaultOaiIdentiferForServiceDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.OaiIdentifierForServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.scheduling.Scheduler;
import xc.mst.scheduling.ServiceWorkerThread;
import xc.mst.utils.LogWriter;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

/**
 * A copy of the MST is designed to interface with one or more Metadata Services depending on how it's configured.
 * There are several Metadata Services which may be used, each one of which extends the MetadataService
 * class.  The MetadataService class provides a common interface through which the MST can invoke functionality on
 * a Metadata Service.
 *
 * @author Eric Osisek
 */
public abstract class MetadataService
{
	/**
	 * Data access object for adding log statements
	 */
	private static LogDAO logDao = new DefaultLogDAO();

	/**
	 * Data access object for getting processing directives
	 */
	private static ProcessingDirectiveDAO processingDirectiveDao = new DefaultProcessingDirectiveDAO();

	/**
	 * Data access object for getting services
	 */
	private static ServiceDAO serviceDao = new DefaultServiceDAO();

	/**
	 * Data access object for getting sets
	 */
	private static SetDAO setDao = new DefaultSetDAO();

	/**
	 * Data access object for getting formats
	 */
	private static FormatDAO formatDao = new DefaultFormatDAO();

	/**
	 * Data access object for getting OAI IDs
	 */
	private static OaiIdentifierForServiceDAO oaiIdDao = new DefaultOaiIdentiferForServiceDAO();

	/**
	 * Data access object for getting FRBR level IDs
	 */
	private static XcIdentifierForFrbrElementDAO frbrLevelIdDao = new DefaultXcIdentifierForFrbrElementDAO();

	/**
	 * Manager for getting, inserting and updating records
	 */
	private static RecordService recordService = new DefaultRecordService();

	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration mstConfiguration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");

	/**
	 * The processing directives for this service
	 */
	protected List<ProcessingDirective> processingDirectives = null;

	/**
	 * A list of services to run after this service's processing completes
	 * The keys are the service IDs and the values are the IDs of the sets
	 * that service's records should get added to
	 */
	protected HashMap<Integer, Integer> servicesToRun = new HashMap<Integer, Integer>();

	/**
	 * The service representing this service in the database
	 */
	protected Service service = null;

	/**
	 * The name of this service
	 */
	protected String serviceName = null;

	/**
	 * The number of records processed by the service so far
	 */
	protected int numProcessed = 0;

	/**
	 * The number of warnings in running the current service
	 */
	protected int warningCount = 0;

	/**
	 * The number of errors in running the current service
	 */
	protected int errorCount = 0;

	/**
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);
	
	/**
	 * The flag for indicating cancel service operation.
	 */
	private boolean isCanceled;
	
	/**
	 * The flag for indicating service operation pause.
	 */
	private boolean isPaused;
	
	/**
	 * Reference of the service currently running 
	 */
	private static MetadataService runningService;
	
	/**
	 * Gets the reference of the service currently running 
	 * @return
	 */
	public static MetadataService getRunningService() {
		return runningService;
	}

	/**
	 * Gets the cancel status of the service.
	 */
	public boolean isCanceled() {
		return isCanceled;
	}

	/**
	 * Gets the pause status of the service.
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
	 * Sets the service ID for this service
	 *
	 * @param serviceId This service's ID
	 */
	protected void setServiceId(int serviceId)
	{
		this.service = serviceDao.getById(serviceId);
	} // end method setServiceId(int)

	/**
	 * Sets the name for this service
	 *
	 * @param serviceName This service's name
	 */
	protected void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	} // end method setServiceName(int)

	/**
	 * Gets the name for this service
	 *
	 * @return This service's name
	 */
	public String getServiceName()
	{
		return serviceName;
	} // end method getServiceName()

	/**
	 * Sets the list of processing directives for this service
	 *
	 * @param processingDirectives The list of processing directives which should be run on records processed by this service
	 */
	protected void setProcessingDirectives(List<ProcessingDirective> processingDirectives)
	{
		this.processingDirectives = processingDirectives;
	} // end method setProcessingDirectives(List<ProcessingDirective>)

	/**
	 * Runs the service with the passed ID
	 *
	 * @param serviceId The ID of the MetadataService to run
	 * @param outputSetId The ID of the set that records processed by this service should be added to
	 */
	public static boolean runService(int serviceId, int outputSetId)
	{
		if(log.isDebugEnabled())
			log.debug("Entering MetadataService.runService for the service with ID " + serviceId + ".");

		// Get the service
		Service service = serviceDao.getById(serviceId);

		// The name of the class for the service specified in the configuration file.
		String targetClassName = service.getClassName();

		// Get the class for the service specified in the configuration file
		try
		{
			if(log.isDebugEnabled())
				log.debug("Trying to get the MetadataService class named " + targetClassName);

			// Get the class specified in the configuration file
			// The class loader for the MetadataService class
    		ClassLoader serviceLoader = MetadataService.class.getClassLoader();
    		
			// Load the class from the .jar file
    		// TODO: Don't reload the class file each time.  Instead, load it into 
    		//       Tomcat once when the MST is started or the service is added/updated.
    		//       This requires more research into Tomcat's class loaders
			URLClassLoader loader = new URLClassLoader(new URL[] { new File(service.getServiceJar()).toURI().toURL() }, serviceLoader);
			Class<?> clazz = loader.loadClass(targetClassName);
			
			runningService = (MetadataService)clazz.newInstance();

			if(log.isDebugEnabled())
				log.debug("Found the MetadataService class named " + targetClassName + ", getting its constructor.");

			// Set the service's ID and name
			runningService.setServiceId(serviceId);
			runningService.setServiceName(service.getName());
			
			// Load the service's configuration
			runningService.loadConfiguration(service.getServiceConfig());

			// Create the list of ProcessingDirectives which could be run on records processed from this service
			runningService.setProcessingDirectives(processingDirectiveDao.getBySourceServiceId(serviceId));

			if(log.isDebugEnabled())
				log.debug("Constructed the MetadataService Object, running its processRecords() method.");

			LogWriter.addInfo(service.getServicesLogFileName(), "Starting the " + service.getName() + " Service.");
			
			// Update database with status of service
			runningService.persistStatus(Constants.STATUS_SERVICE_RUNNING);
			
			// Run the service's processRecords method
			boolean success = runningService.processRecords(outputSetId);

			LogWriter.addInfo(service.getServicesLogFileName(), "The " + service.getName() + " Service finished running.  " + runningService.numProcessed + " records were processed.");

			// Update database with status of service
			if(!runningService.isCanceled)
			runningService.persistStatus(Constants.STATUS_SERVICE_NOT_RUNNING);
			
			
			return success;
		} // end try(run the service through reflection)
		catch(ClassNotFoundException e)
		{
			log.error("Could not find class " + targetClassName, e);
			
			

			LogWriter.addError(service.getServicesLogFileName(), "Tried to start the " + service.getName() + " Service, but the java class " + targetClassName + " could not be found.");

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesErrors(service.getServicesErrors() + 1);

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}
			
			// Update database with status of service
			try {
				runningService.persistStatus(Constants.STATUS_SERVICE_ERROR);
			} catch (DataException e1) {
				e1.printStackTrace();
				log.error("An error occurred while updating service status to database for service with ID" + service.getId() + ".", e1);
			}
			
			// Return false if we did not recognize the service name
			return false;
		} // end if(service is not user defined)
		catch(NoClassDefFoundError e)
		{
			log.error("Could not find class " + targetClassName, e);

			LogWriter.addError(service.getServicesLogFileName(), "Tried to start the " + service.getName() + " Service, but the java class " + targetClassName + " could not be found.");

			// Load the provider again in case it was updated during the harvest
			service = serviceDao.getById(service.getId());

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesErrors(service.getServicesErrors() + 1);

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}
			
			// Update database with status of service
			try {
				runningService.persistStatus(Constants.STATUS_SERVICE_ERROR);
			} catch (DataException e1) {
				e1.printStackTrace();
				log.error("An error occurred while updating service status to database for service with ID" + service.getId() + ".", e1);
			}

			return false;
		} // end catch(NoClassDefFoundError)
		catch(IllegalAccessException e)
		{
			log.error("IllegalAccessException occurred while invoking the service's processRecords method.", e);

			LogWriter.addError(service.getServicesLogFileName(), "Tried to start the " + service.getName() + " Service, but the java class " + targetClassName + "'s processRecords method could not be accessed.");

			// Load the provider again in case it was updated during the harvest
			service = serviceDao.getById(service.getId());

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesErrors(service.getServicesErrors() + 1);

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}
			
			// Update database with status of service
			try {
				runningService.persistStatus(Constants.STATUS_SERVICE_ERROR);
			} catch (DataException e1) {
				e1.printStackTrace();
				log.error("An error occurred while updating service status to database for service with ID" + service.getId() + ".", e1);
			}


			return false;
		} // end catch(IllegalAccessException)
		catch(Exception e)
		{
			log.error("Exception occurred while invoking the service's processRecords method.", e);

			LogWriter.addError(service.getServicesLogFileName(), "An internal error occurred while trying to start the " + service.getName() + " Service.");

			// Load the provider again in case it was updated during the harvest
			service = serviceDao.getById(service.getId());

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesErrors(service.getServicesErrors() + 1);

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}
			
			// Update database with status of service
			try {
				runningService.persistStatus(Constants.STATUS_SERVICE_ERROR);
			} catch (DataException e1) {
				e1.printStackTrace();
				log.error("An error occurred while updating service status to database for service with ID" + service.getId() + ".", e1);
			}


			return false;
		} // end catch(Exception)
	} // end method runService(int, int)

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
	public boolean processRecords(int outputSetId)
	{
		try
		{
			// Get the list of record inputs for this service
			RecordList records = recordService.getInputForService(service.getId());
			
			//DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

			// Iterate over the list of input records and process each.
			// Then run the processing directives on the results of each and add
			// the appropriate record inputs for services to be run on the records
			// resulting from the processing.  Also maintain a list of services to
			// be invoked after this service is finished.  Finally, add the records
			// resulting from this service.
			for(Record processMe : records)
			{
				// If the service is not canceled and not paused then continue 
				if(!isCanceled && !isPaused)
				{
					// Get the results of processing the record
					List<Record> results = processRecord(processMe);
	
					for(Record outgoingRecord : results)
					{
						// Mark the record as not coming from a provider
						outgoingRecord.setProvider(null);
						
						if(outputSetId > 0)
							outgoingRecord.addSet(setDao.getById(outputSetId));
	
						// Check whether or not this record already exists in the database
						Record oldRecord = recordService.getByOaiIdentifierAndService(outgoingRecord.getOaiIdentifier(), service.getId());
	
						// If the current record is a new record, insert it
						if(oldRecord == null)
							insertNewRecord(outgoingRecord);
						// Otherwise we've seen the record before.  Update it as appropriate
						// If outgoingRecord's deleted flag is set to true, the record will
						// be deleted.
						else
							updateExistingRecord(outgoingRecord, oldRecord);
					} // end loop over processed records
	
					numProcessed++;
					if(numProcessed % 100000 == 0)
						LogWriter.addInfo(service.getServicesLogFileName(), "Processed " + numProcessed + " records so far.");
				}
				else 
					{
						// If canceled the stop processing records
						if(isCanceled)
							{
								LogWriter.addInfo(service.getServicesLogFileName(), "Cancelled Service " + serviceName);
								LogWriter.addInfo(service.getServicesLogFileName(), "Processed " + numProcessed + " records so far.");
								// Update database with status of service
								persistStatus(Constants.STATUS_SERVICE_CANCELED);
								break;
							}
						// If paused then wait
						else if(isPaused)
							{
								LogWriter.addInfo(service.getServicesLogFileName(), "Paused Service " + serviceName);
								// Update database with status of service
								persistStatus(Constants.STATUS_SERVICE_PAUSED);
								
								while(isPaused && !isCanceled)
									{
										LogWriter.addInfo(service.getServicesLogFileName(), "Service Waiting to resume" );
										Thread.sleep(3000);
									}
								// If the service is canceled after it is paused, then exit 
								if(isCanceled)
								{
									LogWriter.addInfo(service.getServicesLogFileName(), " Cancelled Service " + serviceName);
									// Update database with status of service
									persistStatus(Constants.STATUS_SERVICE_CANCELED);
									break;

								}
								// If the service is resumed after it is paused, then continue
								else
								{
									LogWriter.addInfo(service.getServicesLogFileName(), "Resumed Service " + serviceName);
									// Update database with status of service
									persistStatus(Constants.STATUS_SERVICE_RUNNING);
									
								}
									
							}
						
					}
				
				// Mark the record as having been processed by this service
				processMe.addProcessedByService(service);
				processMe.removeInputForService(service);
				recordService.update(processMe);
			} // end loop over records to process

			// Reopen the reader so it can see the changes made by running the service
			SolrIndexManager.getInstance().commitIndex();

			// Get the results of any final processing the service needs to perform
			finishProcessing();

			// Start the MetadataServices triggered by processing directives
			// matched on records resulting from the service we just finished running
			for(Integer serviceToRun : servicesToRun.keySet())
			{
				try
				{
					ServiceWorkerThread serviceThread = new ServiceWorkerThread();
					serviceThread.setServiceId(serviceToRun.intValue());
					serviceThread.setOutputSetId(servicesToRun.get(serviceToRun).intValue());
					Scheduler.scheduleThread(serviceThread);
				} // end try(start the service)
				catch(Exception e)
				{
					log.error("An error occurred while running the service with ID " + serviceToRun.intValue() + ".", e);
				} // end catch(Exception)
			} // end loop over services to run

			return true;
		} // end try(process the records)
		catch(Exception e)
		{
			log.error("An error occurred while running the service with ID " + service.getId() + ".", e);
			
			// Update database with status of service
			try {
				persistStatus(Constants.STATUS_SERVICE_ERROR);
			} catch (DataException e1) {
				log.error("An error occurred while updating service status to database for service with ID" + service.getId() + ".", e1);
			}
			
			return false;
		} // end catch(Exception)
		finally // Update the error and warning count for the service
		{
			// Load the provider again in case it was updated during the harvest
			Service service = serviceDao.getById(this.service.getId());

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesWarnings(service.getServicesWarnings() + warningCount);
			service.setServicesErrors(service.getServicesErrors() + errorCount);
			service.setHarvestOutRecordsAvailable(recordService.getCount(null, null, -1, -1, service.getId()));

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e);
			}

			// Update the next OAI ID for this service in the database
			oaiIdDao.writeNextOaiId(service.getId());

			// Update the next XC ID for all elements in the database
			frbrLevelIdDao.writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_WORK);
			frbrLevelIdDao.writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_EXPRESSION);
			frbrLevelIdDao.writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_MANIFESTATION);
			frbrLevelIdDao.writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_HOLDINGS);
			frbrLevelIdDao.writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_ITEM);
			frbrLevelIdDao.writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_RECORD);
		} // end finally(write the next IDs to the database)
	} // end method processRecords(int)

	/**
	 * Adds errors to the record and updates it in the index
	 * 
	 * @param record The record to add warnings and errors to
	 * @param errors A list of errors to add to the record
	 */
	protected void addErrorsToRecord(Record record, List<String> errors)
	{
		try
		{
			// Set the fields for warnings and errors on the record
			record.setErrors(errors);
			
			// Update the record.
			if(!recordService.update(record))
				log.error("The update failed for the record with ID " + record.getId() + ".");
		} // end try(update the record)
		catch (DataException e)
		{
			log.error("An exception occurred while updating the record into the index.", e);
		} // end catch(DataException)
	}
	
	/**
	 * Inserts a record in the Lucene index and sets up RecordInput values
	 * for any processing directives the record matched so the appropriate
	 * services process the record
	 *
	 * @param record The record to insert
	 */
	protected void insertNewRecord(Record record)
	{
		try
		{
			record.setService(service);

			// Run the processing directives against the record we're inserting
			checkProcessingDirectives(record);

			if(!recordService.insert(record))
				log.error("Failed to insert the new record with the OAI Identifier " + record.getOaiIdentifier() + ".");
		} // end try(insert the record)
		catch (DataException e)
		{
			log.error("An exception occurred while inserting the record into the Lucene index.", e);
		} // end catch(DataException)
	} // end method insertNewRecord(Record)

	/**
	 * Updates a record in the Lucene index and sets up RecordInput values
	 * for any processing directives the record matched so the appropriate
	 * services reprocess the record after the update
	 *
	 * @param newRecord The record as it should look after the update (the record ID is not set)
	 * @param oldRecord The record in the Lucene index which needs to be updated
	 */
	protected void updateExistingRecord(Record newRecord, Record oldRecord)
	{
		try
		{
			// Set the new record's ID to the old record's ID so when we call update()
			// on the new record it will update the correct record in the Lucene index
			newRecord.setId(oldRecord.getId());

			// Update the record.  If the update was successful,
			// run the processing directives against the updated record
			if(recordService.update(newRecord))
				checkProcessingDirectives(newRecord);
			else
				log.error("The update failed for the record with ID " + newRecord.getId() + ".");
		} // end try(update the record)
		catch (DataException e)
		{
			log.error("An exception occurred while updating the record into the index.", e);
		} // end catch(DataException)
	} // end method updateExistingRecord(Record, Record)

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

		// Loop over the processing directives and check if any of them match the record
		for(ProcessingDirective processingDirective : processingDirectives)
		{
			// Check if the record matches any of the metadata formats for the current processing directive
			if(processingDirective.getTriggeringFormats().contains(record.getFormat()))
				matchedProcessingDirectives.add(processingDirective);

			// If the metadata format didn't match, check if the record is in any of the sets for the current processing directive
			else
			{
				for(Set set : record.getSets())
				{
					if(processingDirective.getTriggeringSets().contains(set))
					{
						matchedProcessingDirectives.add(processingDirective);
						break;
					} // end if(the set matched the processing directive)
				} // end loop over the record's sets
			} // end else(the format did not trigger the processing directive)
		} // end loop over processing directives

		// Loop over the matched processing directives.  Add the appropriate record inputs and add the
		// correct services to the list of services to run after the harvest completes
		for(ProcessingDirective matchedProcessingDirective : matchedProcessingDirectives)
		{
			record.addInputForService(matchedProcessingDirective.getService());
			record.removeProcessedByService(matchedProcessingDirective.getService());

			Integer serviceId = new Integer(matchedProcessingDirective.getService().getId());

			if(!servicesToRun.containsKey(serviceId))
				servicesToRun.put(serviceId, (matchedProcessingDirective.getOutputSet() == null ? 0 : new Integer(matchedProcessingDirective.getOutputSet().getId())));
		} // end loop over matched processing directives
	} // end method checkProcessingDirectives(Record)

	/**
	 * Gets a Format by name.  Subclasses of MetadataService can call this method to
	 * get a Format from the database.
	 * 
	 * @param name The name of the target Format
	 * @return The Format with the passed name
	 */
	protected Format getFormatByName(String name)
	{
		return formatDao.getByName(name);
	}
	
	/**
	 * Gets all records that are successors of the record with the passed ID
	 * 
	 * @param recordId The record whose successors we're getting
	 * @return A list of records that are successors of the record with the passed ID
	 */
	protected RecordList getByProcessedFrom(long recordId)
	{
		return recordService.getByProcessedFrom(recordId);
	}
	
	/**
	 * Gets the next OAI identifier for the service
	 * 
	 * @return The next OAI identifier for the service
	 */
	protected long getNextOaiId()
	{
		return oaiIdDao.getNextOaiIdForService(service.getId());
	}
	
	/**
	 * Gets the set from the database with the passed setSpec
	 * 
	 * @param setSpec The setSpec of the target set
	 * @return The set with the passed setSpec
	 */
	protected Set getSet(String setSpec)
	{
		return setDao.getBySetSpec(setSpec);
	}
	
	/**
	 * Adds a new set to the database
	 * 
	 * @param setSpec The setSpec of the new set
	 * @param setName The display name of the new set
	 * @param setDescription A description of the new set
	 * @throws DataException If an error occurred while adding the set
	 */
	protected void addSet(String setSpec, String setName, String setDescription) throws DataException 
	{
		Set set = new Set();
		set.setSetSpec(setSpec);
		set.setDescription(setDescription);
		set.setDisplayName(setName);
		set.setIsRecordSet(true);
		set.setIsProviderSet(false);
		setDao.insert(set);
	}
	
	/**
	 * Gets the record for this service which has the passed OAI identifier
	 * 
	 * @param oaiId The OAI identifier of the target record
	 * @return The record with the passed OAI identifier, or null if no records matched the identifier
	 */
	protected Record getByOaiId(String oaiId)
	{
		return recordService.getByOaiIdentifierAndService(oaiId, service.getId());
	}
	
	/**
	 * This method processes a single record.
	 *
	 * @param record The record to process
	 * @return A list of outgoing records that should be added, modified, or deleted
	 *         as a result of processing the incoming record
	 */
	protected abstract List<Record> processRecord(Record record);

	/**
	 * This method gets called to give the service the service specific configuration
	 * which was defined for it in its configuration file.
	 *
	 * @param config The service specific configuration defined in the service's configuration file
	 */
	protected abstract void loadConfiguration(String config);
	
	/**
	 * This method gets called after all new records are processed.  If the service
	 * needs to do any additional processing after it processed all the input records,
	 * it should be done in this method.
	 */
	protected abstract void finishProcessing();
	
	/**
	 * Logs the status of the service to the database
	 * @throws DataException 
	 */
	protected void persistStatus(String status) throws DataException{
		
		service.setStatus(status);
		serviceDao.update(service);
	}
	
	/**
	 * Gets the status of the service to the database
	 */
	protected String getCurrentStatus(){
		return service.getStatus();
	}
	
	
}
