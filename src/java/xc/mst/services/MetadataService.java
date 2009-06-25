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
import xc.mst.dao.DatabaseConfigException;
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
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.scheduling.Scheduler;
import xc.mst.scheduling.ServiceWorkerThread;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
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
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);

	/**
	 * The service representing this service in the database
	 */
	protected Service service = null;

	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	private static final Configuration mstConfiguration = ConfigurationManager
			.getConfiguration();

	/**
	 * The name of this service
	 */
	private String serviceName = null;

	/**
	 * The number of warnings in running the current service
	 */
	private int warningCount = 0;

	/**
	 * The number of errors in running the current service
	 */
	private int errorCount = 0;

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
	 * The processing directives for this service
	 */
	private List<ProcessingDirective> processingDirectives = null;

	/**
	 * A list of services to run after this service's processing completes
	 * The keys are the service IDs and the values are the IDs of the sets
	 * that service's records should get added to
	 */
	private HashMap<Integer, Integer> servicesToRun = new HashMap<Integer, Integer>();

	/**
	 * The number of records processed by the service so far
	 */
	private int numProcessed = 0;

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
		Service service;
		try
		{
			service = serviceDao.getById(serviceId);
		}
		catch (DatabaseConfigException e1)
		{
			log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

			return false;
		}

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

			if(log.isDebugEnabled())
				log.debug("Validating the Metadata Service with ID " + serviceId + ".");

			runningService.checkService(Constants.STATUS_SERVICE_RUNNING, true);

			if(log.isDebugEnabled())
				log.debug("Running the Metadata Service with ID " + serviceId + ".");

			// Run the service's processRecords method
			boolean success = runningService.processRecords(outputSetId);

			LogWriter.addInfo(service.getServicesLogFileName(), "The " + service.getName() + " Service finished running.  " + runningService.numProcessed + " records were processed.");

			// Update database with status of service
			if(!runningService.isCanceled)
				runningService.setStatus(Constants.STATUS_SERVICE_NOT_RUNNING);

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
			runningService.setStatus(Constants.STATUS_SERVICE_ERROR);

			// Return false if we did not recognize the service name
			return false;
		} // end if(service is not user defined)
		catch(NoClassDefFoundError e)
		{
			log.error("Could not find class " + targetClassName, e);

			LogWriter.addError(service.getServicesLogFileName(), "Tried to start the " + service.getName() + " Service, but the java class " + targetClassName + " could not be found.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				service = serviceDao.getById(service.getId());
			}
			catch (DatabaseConfigException e1)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				return false;
			}

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
			runningService.setStatus(Constants.STATUS_SERVICE_ERROR);

			return false;
		} // end catch(NoClassDefFoundError)
		catch(IllegalAccessException e)
		{
			log.error("IllegalAccessException occurred while invoking the service's processRecords method.", e);

			LogWriter.addError(service.getServicesLogFileName(), "Tried to start the " + service.getName() + " Service, but the java class " + targetClassName + "'s processRecords method could not be accessed.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				service = serviceDao.getById(service.getId());
			}
			catch (DatabaseConfigException e1)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				return false;
			}

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
			runningService.setStatus(Constants.STATUS_SERVICE_ERROR);

			return false;
		} // end catch(IllegalAccessException)
		catch(Exception e)
		{
			log.error("Exception occurred while invoking the service's processRecords method.", e);

			LogWriter.addError(service.getServicesLogFileName(), "An internal error occurred while trying to start the " + service.getName() + " Service.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				service = serviceDao.getById(service.getId());
			}
			catch (DatabaseConfigException e1)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				return false;
			}

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
			runningService.setStatus(Constants.STATUS_SERVICE_ERROR);

			return false;
		} // end catch(Exception)
	} // end method runService(int, int)

	/**
	 * This method gets called to give the service the service specific configuration
	 * which was defined for it in its configuration file.
	 *
	 * @param config The service specific configuration defined in the service's configuration file
	 */
	public abstract void loadConfiguration(String config);

	/**
	 * Validates the service with the passed ID
	 *
	 * @param serviceId The ID of the MetadataService to run
	 * @param successStatus The status of the MetadataService is the validation was successful
	 * @param testSolr True to test the connection to the index, false otherwise
	 */
	public static void checkService(int serviceId, String successStatus, boolean testSolr)
	{
		if(log.isDebugEnabled())
			log.debug("Entering MetadataService.checkService for the service with ID " + serviceId + ".");

		// Get the service
		Service serviceObj = null;
		try
		{
			serviceObj = serviceDao.getById(serviceId);
		}
		catch (DatabaseConfigException e3)
		{
			log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e3);

			return; // We can't connect to the database so we can't write the status
		}

		MetadataService serviceToTest = null;

		// The name of the class for the service specified in the configuration file.
		String targetClassName = serviceObj.getClassName();

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
			URLClassLoader loader = new URLClassLoader(new URL[] { new File(serviceObj.getServiceJar()).toURI().toURL() }, serviceLoader);
			Class<?> clazz = loader.loadClass(targetClassName);

			serviceToTest = (MetadataService)clazz.newInstance();

			if(log.isDebugEnabled())
				log.debug("Found the MetadataService class named " + targetClassName + ", getting its constructor.");

			serviceToTest.service = serviceObj;

			// Set the service's ID and name
			serviceToTest.setServiceId(serviceId);
			serviceToTest.setServiceName(serviceObj.getName());

			// Load the service's configuration
			serviceToTest.loadConfiguration(serviceObj.getServiceConfig());

			// Create the list of ProcessingDirectives which could be run on records processed from this service
			serviceToTest.setProcessingDirectives(processingDirectiveDao.getBySourceServiceId(serviceId));

			if(log.isDebugEnabled())
				log.debug("Constructed the MetadataService Object, running its processRecords() method.");

			LogWriter.addInfo(serviceObj.getServicesLogFileName(), "Starting the " + serviceObj.getName() + " Service.");

			if(log.isDebugEnabled())
				log.debug("Validating the Metadata Service with ID " + serviceId + ".");

			serviceToTest.checkService(successStatus, testSolr);
		} // end try(run the service through reflection)
		catch(ClassNotFoundException e)
		{
			log.error("Could not find class " + targetClassName, e);

			LogWriter.addError(serviceObj.getServicesLogFileName(), "Tried to validate the " + serviceObj.getName() + " Service, but the java class " + targetClassName + " could not be found.");

			// Increase the warning and error counts as appropriate, then update the provider
			serviceObj.setServicesErrors(serviceObj.getServicesErrors() + 1);

			try
			{
				serviceDao.update(serviceObj);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}

			// Update database with status of service
			try
			{
				serviceObj.setStatus(Constants.STATUS_SERVICE_ERROR);
				serviceDao.update(serviceObj);
			}
			catch(DataException e1)
			{
				log.error("An error occurred while updating service status to database for service with ID" + serviceObj.getId() + ".", e1);
			}
		} // end if(service is not user defined)
		catch(NoClassDefFoundError e)
		{
			log.error("Could not find class " + targetClassName, e);

			LogWriter.addError(serviceObj.getServicesLogFileName(), "Tried to validate the " + serviceObj.getName() + " Service, but the java class " + targetClassName + " could not be found.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				serviceObj = serviceDao.getById(serviceObj.getId());
			}
			catch (DatabaseConfigException e3)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e3);

				return;
			}

			// Increase the warning and error counts as appropriate, then update the provider
			serviceObj.setServicesErrors(serviceObj.getServicesErrors() + 1);

			try
			{
				serviceDao.update(serviceObj);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}

			// Update database with status of service
			try
			{
				serviceObj.setStatus(Constants.STATUS_SERVICE_ERROR);
				serviceDao.update(serviceObj);
			}
			catch(DataException e1)
			{
				log.error("An error occurred while updating service status to database for service with ID" + serviceObj.getId() + ".", e1);
			}
		} // end catch(NoClassDefFoundError)
		catch(IllegalAccessException e)
		{
			log.error("IllegalAccessException occurred while invoking the service's checkRecords method.", e);

			LogWriter.addError(serviceObj.getServicesLogFileName(), "Tried to validate the " + serviceObj.getName() + " Service, but the java class " + targetClassName + "'s processRecords method could not be accessed.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				serviceObj = serviceDao.getById(serviceObj.getId());
			}
			catch (DatabaseConfigException e3)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e3);
			}

			// Increase the warning and error counts as appropriate, then update the provider
			serviceObj.setServicesErrors(serviceObj.getServicesErrors() + 1);

			try
			{
				serviceDao.update(serviceObj);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}

			// Update database with status of service
			try
			{
				serviceObj.setStatus(Constants.STATUS_SERVICE_ERROR);
				serviceDao.update(serviceObj);
			}
			catch(DataException e1)
			{
				log.error("An error occurred while updating service status to database for service with ID" + serviceObj.getId() + ".", e1);
			}
		} // end catch(IllegalAccessException)
		catch(Exception e)
		{
			log.error("Exception occurred while invoking the service's checkRecords method.", e);

			LogWriter.addError(serviceObj.getServicesLogFileName(), "An internal error occurred while trying to validate the " + serviceObj.getName() + " Service.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				serviceObj = serviceDao.getById(serviceObj.getId());
			}
			catch (DatabaseConfigException e3)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e3);
			}

			// Increase the warning and error counts as appropriate, then update the provider
			serviceObj.setServicesErrors(serviceObj.getServicesErrors() + 1);

			try
			{
				serviceDao.update(serviceObj);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}

			// Update database with status of service
			try
			{
				serviceObj.setStatus(Constants.STATUS_SERVICE_ERROR);
				serviceDao.update(serviceObj);
			}
			catch(DataException e1)
			{
				log.error("An error occurred while updating service status to database for service with ID" + serviceObj.getId() + ".", e1);
			}
		} // end catch(Exception)
	} // end method checkService(int)

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
					// If the record was deleted, delete and reprocess all records that were processed from it
					if(processMe.getDeleted())
					{
						List<Record> successors = getByProcessedFrom(processMe);

						for(Record successor : successors)
						{
							successor.setDeleted(true);
							reprocessRecord(successor);
						}
					}

					// Get the results of processing the record
					List<Record> results = processRecord(processMe);

					for(Record outgoingRecord : results)
					{
						// Mark the output record as a successor of the input record
						processMe.addSuccessor(outgoingRecord);

						// Mark the input record as a predecessor of the output record
						outgoingRecord.addProcessedFrom(processMe);
						
						// Mark the record as not coming from a provider
						outgoingRecord.setProvider(null);

						// Add all sets the outgoing record belongs to to the service's list of output sets
						for(Set outputSet : outgoingRecord.getSets())
							service.addOutputSet(outputSet);

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

					// Mark the record as having been processed by this service
					processMe.addProcessedByService(service);
					processMe.removeInputForService(service);
					recordService.update(processMe);

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
								setStatus(Constants.STATUS_SERVICE_CANCELED);
								break;
							}
						// If paused then wait
						else if(isPaused)
							{
								LogWriter.addInfo(service.getServicesLogFileName(), "Paused Service " + serviceName);
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
									LogWriter.addInfo(service.getServicesLogFileName(), " Cancelled Service " + serviceName);
									// Update database with status of service
									setStatus(Constants.STATUS_SERVICE_CANCELED);
									break;

								}
								// If the service is resumed after it is paused, then continue
								else
								{
									LogWriter.addInfo(service.getServicesLogFileName(), "Resumed Service " + serviceName);
									// Update database with status of service
									setStatus(Constants.STATUS_SERVICE_RUNNING);

								}

							}

					}
			} // end loop over records to process

			// Reopen the reader so it can see the changes made by running the service
			SolrIndexManager.getInstance().commitIndex();

			// Get the results of any final processing the service needs to perform
			finishProcessing();

			// Reopen the reader so it can see the changes made by running the service
			SolrIndexManager.getInstance().commitIndex();

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
			setStatus(Constants.STATUS_SERVICE_ERROR);

			return false;
		} // end catch(Exception)
		finally // Update the error and warning count for the service
		{
			// Load the provider again in case it was updated during the harvest
			Service service = null;
			try
			{
				service = serviceDao.getById(this.service.getId());
			}
			catch (DatabaseConfigException e1)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				return false;
			}

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesWarnings(service.getServicesWarnings() + warningCount);
			service.setServicesErrors(service.getServicesErrors() + errorCount);

			try {
				service.setHarvestOutRecordsAvailable(recordService.getCount(null, null, -1, -1, service.getId()));
			} catch (IndexException ie) {
				log.error("Index exception occured.", ie);
			}

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
	 * Gets the name for this service
	 *
	 * @return This service's name
	 */
	public String getServiceName()
	{
		return serviceName;
	} // end method getServiceName()

	/**
	 * Gets the status of the job
	 */
	public String getServiceStatus(){

		if(isCanceled)
			return Constants.STATUS_SERVICE_CANCELED;
		else if(isPaused)
			return Constants.STATUS_SERVICE_PAUSED;
		else if(runningService!= null)
			return Constants.STATUS_SERVICE_RUNNING;
		else
			return Constants.STATUS_SERVICE_NOT_RUNNING;
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
	protected abstract List<Record> processRecord(Record record);

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
		catch (IndexException ie) {
			log.error("An exception occurred while updating the record into the index.", ie);
		}
	}

	/**
	 * This method gets called after all new records are processed.  If the service
	 * needs to do any additional processing after it processed all the input records,
	 * it should be done in this method.
	 */
	protected abstract void finishProcessing();

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
	 */
	protected void updateRecord(Record record)
	{
		try
		{
			recordService.update(record);
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
	public String getNextOaiId()
	{
		return "oai:" + MSTConfiguration.getProperty(Constants.CONFIG_OAI_REPO_IDENTIFIER) + ":" + serviceName + "/" + oaiIdDao.getNextOaiIdForService(service.getId());
	}

	/**
	 * Gets a Format by name.  Subclasses of MetadataService can call this method to
	 * get a Format from the database.
	 *
	 * @param name The name of the target Format
	 * @return The Format with the passed name
	 * @throws DatabaseConfigException
	 */
	protected Format getFormatByName(String name) throws DatabaseConfigException
	{
		return formatDao.getByName(name);
	}

	/**
	 * Gets all records that are successors of the record with the passed ID
	 *
	 * @param record The record whose successors we're getting
	 * @return A list of records that are successors of the record with the passed ID
	 * @throws IndexException
	 */
	protected RecordList getByProcessedFrom(Record record) throws IndexException
	{
		return recordService.getByProcessedFrom(record.getId());
	}

	/**
	 * Gets all records that contain the passed trait
	 *
	 * @param trait The trait of the records we're getting
	 * @return A list of records that have the passed trait
	 * @throws IndexException
	 */
	protected RecordList getByTrait(String trait) throws IndexException
	{
		return recordService.getByTrait(trait);
	}
	
	/**
	 * Gets the output record for the service with the passed OAI identifier
	 * 
	 * @param oaiId The OAI identifier of the record to get
	 * @return The output record for the service with the passed OAI identifier
	 * @throws IndexException 
	 * @throws DatabaseConfigException 
	 */
	protected Record getOutputByOaiId(String oaiId) throws IndexException, DatabaseConfigException
	{
		return recordService.getByOaiIdentifierAndService(oaiId, service.getId());
	}
	
	/**
	 * Gets the input record for the service with the passed OAI identifier
	 * 
	 * @param oaiId The OAI identifier of the record to get
	 * @return The input record for the service with the passed OAI identifier
	 * @throws IndexException 
	 * @throws DatabaseConfigException 
	 */
	protected Record getInputByOaiId(String oaiId) throws IndexException, DatabaseConfigException
	{
		return recordService.getInputForServiceByOaiIdentifier(oaiId, service.getId());
	}
	
	/**
	 * Adds a new set to the database
	 *
	 * @param setSpec The setSpec of the new set
	 * @param setName The display name of the new set
	 * @param setDescription A description of the new set
	 * @throws DataException If an error occurred while adding the set
	 */
	protected Set addSet(String setSpec, String setName, String setDescription) throws DataException
	{
		Set set = new Set();
		set.setSetSpec(setSpec);
		set.setDescription(setDescription);
		set.setDisplayName(setName);
		set.setIsRecordSet(true);
		set.setIsProviderSet(false);
		setDao.insert(set);
		return set;
	}

	/**
	 * Gets the set from the database with the passed setSpec
	 *
	 * @param setSpec The setSpec of the target set
	 * @return The set with the passed setSpec
	 * @throws DatabaseConfigException
	 */
	protected Set getSet(String setSpec) throws DatabaseConfigException
	{
		return setDao.getBySetSpec(setSpec);
	}

	/**
	 * Logs an info message in the service's log file
	 *
	 * @param message The message to log
	 */
	protected void logInfo(String message)
	{
		LogWriter.addInfo(service.getServicesLogFileName(), message);
	}

	/**
	 * Logs a warning message in the service's log file
	 *
	 * @param message The message to log
	 */
	protected void logWarning(String message)
	{
		LogWriter.addWarning(service.getServicesLogFileName(), message);
		warningCount++;
	}

	/**
	 * Logs an error message in the service's log file
	 *
	 * @param message The message to log
	 */
	protected void logError(String message)
	{
		LogWriter.addError(service.getServicesLogFileName(), message);
	}

	protected String getOrganizationCode()
	{
		return mstConfiguration.getProperty(Constants.CONFIG_ORGANIZATION_CODE);
	}
	
	/**
	 * Inserts a record in the Lucene index and sets up RecordInput values
	 * for any processing directives the record matched so the appropriate
	 * services process the record
	 *
	 * @param record The record to insert
	 */
	private void insertNewRecord(Record record)
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
		catch (IndexException ie) {
			log.error("An exception occurred while inserting the record into the index.", ie);
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
	private void updateExistingRecord(Record newRecord, Record oldRecord)
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
		catch (IndexException ie) {
			log.error("An exception occurred while updating the record into the index.", ie);
		}
	} // end method updateExistingRecord(Record, Record)

	/**
	 * Logs the status of the service to the database
	 * @throws DataException
	 */
	private void setStatus(String status)
	{
		try
		{
			service.setStatus(status);
			serviceDao.update(service);
		}
		catch(DataException e)
		{
			log.error("An error occurred while updating service status to database for service with ID" + service.getId() + ".", e);
		}
	}

	/**
	 * Sets the service ID for this service
	 *
	 * @param serviceId This service's ID
	 * @throws DatabaseConfigException
	 */
	private void setServiceId(int serviceId) throws DatabaseConfigException
	{
		this.service = serviceDao.getById(serviceId);
	} // end method setServiceId(int)

	/**
	 * Sets the name for this service
	 *
	 * @param serviceName This service's name
	 */
	private void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	} // end method setServiceName(int)

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
	private void checkProcessingDirectives(Record record)
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
	 * Reprocesses the passed record by all records that had processed it in the past
	 *
	 * @param record The record to reprocess
	 */
	private void reprocessRecord(Record record)
	{
		for(Service processingService : record.getProcessedByServices())
		{
			record.addInputForService(processingService);

			Integer serviceId = processingService.getId();

			if(!servicesToRun.containsKey(serviceId))
				servicesToRun.put(serviceId, 0);
		}
	}

	/**
	 * Checks whether or not the service is able to be run.  If the service is
	 * not runnable, logs the reason as an error in the service's log file and
	 * sets the service's status to "error".  Otherwise sets the service's status to
	 * the passed status.
	 *
	 * @param statusForSuccess The status of the service if it is runnable
	 * @param testSolr True to verify access to the Solr index, false otherwise
	 * @return True iff the service is runnable
	 */
	private boolean checkService(String statusForSuccess, boolean testSolr)
	{
		if(testSolr)
		{
			// Check that we can access the Solr index
			try
			{
				RecordList test = recordService.getInputForService(service.getId());
				if(test == null)
				{
					LogWriter.addError(service.getServicesLogFileName(), "Cannot run the service because we cannot access the Solr index.");
					service.setServicesErrors(service.getServicesErrors()+1);

					try
					{
						serviceDao.update(service);
					}
					catch(DataException e)
					{
						log.error("An error occurred while updating the service's error count.", e);
					}

					setStatus(Constants.STATUS_SERVICE_ERROR);

					return false;
				}
			}
			catch(Exception e)
			{
				LogWriter.addError(service.getServicesLogFileName(), "Cannot run the service because we cannot access the Solr index.");
				service.setServicesErrors(service.getServicesErrors()+1);

				try
				{
					serviceDao.update(service);
				}
				catch(DataException e1)
				{
					log.error("An error occurred while updating the service's error count.", e1);
				}

				setStatus(Constants.STATUS_SERVICE_ERROR);

				return false;
			}
		}

		try
		{
			validateService();
		}
		catch(ServiceValidationException e)
		{
			LogWriter.addError(service.getServicesLogFileName(), "Cannot run the service for the following reason: " + e.getMessage() + ".");
			service.setServicesErrors(service.getServicesErrors()+1);

			try
			{
				serviceDao.update(service);
			}
			catch(DataException e1)
			{
				log.error("An error occurred while updating the service's error count.", e1);
			}

			setStatus(Constants.STATUS_SERVICE_ERROR);

			return false;
		}

		setStatus(statusForSuccess);

		return true;
	}
}
