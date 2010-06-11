/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.harvester;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import xc.mst.bo.harvest.Harvest;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.bo.processing.Job;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.email.Emailer;
import xc.mst.manager.BaseManager;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DBRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.repo.Repository;
import xc.mst.scheduling.WorkDelegate;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;


public class HarvestManager extends BaseManager implements WorkDelegate {
	
	/**
	 * A reference to the logger which writes to the HarvestIn log file
	 */
	private static Logger log = Logger.getLogger("harvestIn");
	
	protected static final DateTimeFormatter UTC_FORMATTER = ISODateTimeFormat.dateTime();
	
	protected Status status = Status.STOPPED;

	protected Repository repo = null;
	
	protected Harvest currentHarvest = null;
	
	/**
	 * The granularity of the OAI repository we're harvesting (either GRAN_DAY or GRAN_SECOND)
	 */
	protected int granularity = -1;

	/**
	 * The policy for tracking deleted records that the OAI repository uses (either DELETED_RECORD_NO, DELETED_RECORD_TRANSIENT, or DELETED_RECORD_PERSISTENT)
	 */
	protected int deletedRecord = -1;
	
	protected HarvestSchedule schedule = null;
	protected Provider provider = null;
	protected Format format = null;
	

	// TODO: processingDirectives is going to be pulled out of here.  I'm leaving
	// it here for now so that I don't lose the code, but it definitely should be moved
	protected List<ProcessingDirective> processingDirectives = null;

	/**
	 * A list of services to run after this service's processing completes
	 * The keys are the service IDs and the values are the IDs of the sets
	 * that service's records should get added to
	 */
	protected HashMap<Integer, Integer> servicesToRun = new HashMap<Integer, Integer>();

	protected Emailer mailer = new Emailer();

	protected StringBuilder warnings = new StringBuilder();

	protected StringBuilder errors = new StringBuilder();

	protected boolean firstHarvest = false;
	protected int recordsFound = 0;
	protected int failedInserts = 0;
	protected int addedCount = 0;
	protected int updatedCount = 0;
	protected int warningCount = 0;
	protected int errorCount = 0;
	protected int processedRecordCount = 0;
	protected int totalRecordCount = 0;
	
	protected long totalPartTime = 0;
	protected long startPartTime = 0;
	protected long endPartTime = 0;
	
	protected long harvestStartTime = 0;
	protected long startTime = 0;
	protected long endTime = 0;
	protected long timeDiff = 0;

	protected void runHarvestStep(HarvestScheduleStep harvestScheduleStep) throws Exception {
		try {
			metadataPrefix = harvestScheduleStep.getFormat().getName();

			// If there was a set, set up the setSpec
			if(harvestScheduleStep.getSet() != null)
				setSpec = harvestScheduleStep.getSet().getSetSpec();

			// Set the from field to the time when we last harvested the provider
			if (harvestScheduleStep.getLastRan() != null) {
				from = new Date(harvestScheduleStep.getLastRan().getTime());
			}

			// The time when we started the harvest
			Date startTime = new Date();

			// Setup the harvest we're currently running
			currentHarvest = new Harvest();
			currentHarvest.setStartTime(startTime);
			currentHarvest.setProvider(provider);
			currentHarvest.setHarvestSchedule(harvestScheduleStep.getSchedule());
			getHarvestDAO().insert(currentHarvest);

			String timeout = config.getProperty(Constants.CONFIG_HARVESTER_TIMEOUT_URL);
			if(timeout != null)
			{
				try
				{
					timeOutMilliseconds = Integer.parseInt(timeout);
				}
				catch(NumberFormatException e)
				{
					log.warn("The HarvesterTimeout in the configuration file was not an integer.");
				}
			}
			
			
			// Run the harvest
			HarvestManager harvestManager = (HarvestManager)MSTConfiguration.getInstance().getBean("HarvestManager");
			harvestManager.harvest(
					 baseURL,
					 metadataPrefix,
					 setSpec,
					 harvestScheduleStep);

			// Set the request used to run the harvest
			currentHarvest = getHarvestDAO().getById(currentHarvest.getId());
			request = currentHarvest.getRequest();

			// Set the harvest schedule step's last run date to the time when we started the harvest.
			harvestScheduleStep.setLastRan(startTime);
			getHarvestScheduleStepDAO().update(harvestScheduleStep, harvestScheduleStep.getSchedule().getId());
		} // end try(run the harvest)
		catch (Exception e) {

			log.error("An error occurred while harvesting " + baseURL, e);
			throw e;

		}
	}
	
	/**
	 * Creates a Harvester that runs a given harvest schedule step and records the results
	 * using the passed <code>Harvest</code> Object.
	 *
	 * @param scheduleStep The <code>HarvestScheduleStep</code> with information on the harvest we're running
	 * @param currentHarvest The <code>Harvest</code> Object that will store the results of this harvest, such
	 *                       as the start and end times.  This should represent a Harvest that has already been
	 *                       written to the database.  The corrosponding harvest row will be updated with the
	 *                       results after the harvest finishes running.
	 * @throws DatabaseConfigException
	 */
	public void setup(HarvestScheduleStep scheduleStep, Harvest currentHarvest) throws DatabaseConfigException {
		this.currentHarvest = currentHarvest;
		this.schedule = scheduleStep.getSchedule();
		this.provider = schedule.getProvider();

		// Get the ProcessingDirectives which could match records harvested from the provider we're harvesting
		processingDirectives = getProcessingDirectiveDAO().getBySourceProviderId(provider.getId());
	} // end constructor Harvester(int, HarvestScheduleStep, Harvest)

	public void kill() {
		log.debug("HarvestManager.kill() called");
		status = Status.KILLED;
	}

	public void harvest(String baseURL, String metadataPrefix, String setSpec,
            HarvestScheduleStep scheduleStep, Harvest currentHarvest) throws DatabaseConfigException {

		LogWriter.addInfo(scheduleStep.getSchedule().getProvider().getLogFileName(), "Starting harvest of " + baseURL);

		try {
			// Create a Harvester and use it to run the harvest
			setup(scheduleStep, currentHarvest);

			// Update the Status of the harvest schedule
			persistStatus();
			
			String errorMsg = null;
			String request = null;
	
			try {
				firstHarvest = recordService.getCountByProviderId(schedule.getProvider().getId()) == 0;
				TimingLogger.log("firstHarvest: "+firstHarvest);
			} catch (IndexException e2) {
				log.error("An IndexExeption occurred while harvesting " , e2);
	
				errorMsg = "Harvest failed because the Solr index could not be accessed.  Check your configuration.";
	
				LogWriter.addError(schedule.getProvider().getLogFileName(), errorMsg);
				errorCount++;
	
				sendReportEmail(errorMsg);
				setStatus(Status.ERROR);
				persistStatus();
				throw new RuntimeException(errorMsg);
			}

			// Try to validate the repository.  An exception will be thrown and caught if validation fails.
			try {
				// Validate that the repository conforms to the OAI protocol
				TimingLogger.log("about to validate repo");
				ValidateRepository validator = (ValidateRepository)MSTConfiguration.getInstance().getBean("ValidateRepository");
				
				validator.validate(schedule.getProvider().getId());
	
				TimingLogger.log("validated repo");
				
				granularity = validator.getGranularity();
				deletedRecord = validator.getDeletedRecordSupport();
	
				// Get the provider from the repository so we know the formats and sets it
				// supports according to the validation we just performed
				schedule.setProvider(getProviderDAO().getById(schedule.getProvider().getId()));
				provider = schedule.getProvider();
            	repo = (Repository)config.getBean("Repository");
            	repo.setName(schedule.getProvider().getName());
	
				// Get the format we're to harvest
				format = getFormatDAO().getByName(metadataPrefix);
	
				// If the provider no longer supports the requested format we can't harvest it
				if(!schedule.getProvider().getFormats().contains(format)) {
					errorMsg = "The harvest could not be run because the MetadataFormat " + metadataPrefix + " is no longer supported by the OAI repository " + baseURL + ".";
	
					LogWriter.addError(schedule.getProvider().getLogFileName(), errorMsg);
					errorCount++;
	
					sendReportEmail(errorMsg);
	
					throw new RuntimeException(errorMsg);
				} // end if(format no longer supported)
	
				// If the provider no longer contains the requested set we can't harvest it
				if(setSpec != null && !schedule.getProvider().getSets().contains(getSetDAO().getBySetSpec(setSpec))) {
					errorMsg = "The harvest could not be run because the Set " + setSpec + " is no longer supported by the OAI repository " + baseURL + ".";
	
					LogWriter.addError(schedule.getProvider().getLogFileName(), errorMsg);
					errorCount++;
	
					sendReportEmail(errorMsg);
	
					throw new RuntimeException(errorMsg);
				} // end if(set no longer supported)
			} catch (Throwable e) {
				log.error(e.getMessage());
	
				LogWriter.addError(schedule.getProvider().getLogFileName(), e.getMessage());
				errorCount++;
	
				sendReportEmail(e.getMessage());
				setStatus(Status.ERROR);
				persistStatus();
				getUtil().throwIt(e);
			}
	
			try {
				setStatus(Status.RUNNING);
	
				String verb = "ListRecords";
				String resumption = null;
	
				// If metadataPrefix is not specified, log an error and abort
				if (metadataPrefix == null) {
					errorMsg = "The harvest could not be run because the MetadataFormat to harvest was not specified.";
					log.error(errorMsg);
	
					LogWriter.addError(schedule.getProvider().getLogFileName(), errorMsg);
					errorCount++;
	
					sendReportEmail(errorMsg);
	
					throw new RuntimeException(errorMsg);
				}
				
				Date startDate = new Date();
				startTime = new Date().getTime();
				harvestStartTime = startTime;
	
				// This loop places a ListRecords request, and then continues placing requests
				// until it receives a null resumption token
				do {
					// Abort the harvest if the harvester was killed
					checkSignal(baseURL);
	
					request = baseURL;
	
					// If this is the first request, setup a ListRecords request with the
					// correct metadataPrefix.  If we are supposed harvest a specific set
					// or use a known from or until parameter, set them here as well.
					if (resumption == null) {
						request += "?verb=" + verb;
						request += "&metadataPrefix=" + metadataPrefix;
	
						if (setSpec != null && setSpec.length() > 0)
							request += "&set=" + setSpec;
	
						//TODO check harvest schedule
						Date from = scheduleStep.getLastRan();
						if (from != null) {
							request += "&from=" + UTC_FORMATTER.print(from.getTime());						
						}
	
						request += "&until=" + UTC_FORMATTER.print(startDate.getTime());
					} else {
						// Try to encode the resumption token to include it in the URL.
						// Don't worry if encoding it failed because the OAI request may work anyway
						try {
							resumption = URLEncoder.encode(resumption, "utf-8");
						} catch (Exception e) {
							log.warn("An error occurred when encoding the resumption token for use in a URL.", e);
	
							LogWriter.addWarning(schedule.getProvider().getLogFileName(), "An error occurred when trying to encode the resumption token returned by the provider as UTF-8.  The resumption token was: " + resumption);
							warningCount++;
						}
	
						request += "?verb=" + verb + "&resumptionToken=" + resumption;
					}
					
					LogWriter.addInfo(schedule.getProvider().getLogFileName(), "The OAI request is " + request);
					
					currentHarvest.setRequest(request);
					getHarvestDAO().update(currentHarvest);
	
					if (log.isDebugEnabled()) {
						log.debug("Sending the OAI request: " + request);
					}
	
					// Perform the harvest
					TimingLogger.start("sendRequest");
				    Document doc = getHttpService().sendRequest(request);
				    TimingLogger.stop("sendRequest");
	
				    TimingLogger.start("extractRecords");
	                resumption = parseRecords(metadataPrefix, doc, baseURL);
	                TimingLogger.stop("extractRecords");
	                
	                if (MSTConfiguration.getInstance().isPerformanceTestingMode() &&
	                		processedRecordCount > 2000000) {
	                	resumption = null; // I only want to run the 5,000 for now
	                }
	    			if (recordService instanceof DBRecordService) {
	    				((DBRecordService)recordService).commit(0, false);
	    			} else if (processedRecordCount % 50000 == 0) {
	    				TimingLogger.reset(false);
	    				TimingLogger.stop("sinceLastCommit");
	    				TimingLogger.start("sinceLastCommit");
	    			}
				} while(resumption != null); // Repeat as long as we get a resumption token
				if (recordService instanceof DBRecordService) {
					((DBRecordService)recordService).commit(0, true);
				}
			} catch (Throwable e) {
				if(!isKilled())
					setStatus(Status.ERROR);
					persistStatus();
	
				log.error("An error occurred while harvesting " + baseURL, e);
	
				LogWriter.addError(schedule.getProvider().getLogFileName(), "An internal error occurred while executing the harvest: " + e.getMessage());
				errorCount++;
	
				// Throw the error so the calling code knows something went wrong
				throw new RuntimeException("Internal harvester error: " + e);
	
			} finally {
				if (isKilled()) {
					persistStatus();
				}
			}
			
			//END_OF_ORIG_doHarvest_METHOD
			
			log.info("Records harvested " + recordsFound + ", failed inserts " + failedInserts);
	
	
			LogWriter.addInfo(scheduleStep.getSchedule().getProvider().getLogFileName(), "Finished harvesting " + baseURL + ", " + recordsFound + " new records were returned by the OAI provider.");
	
			// Report the number of records which could not be added to the index due to an error
			if(failedInserts > 0)
			{
				LogWriter.addWarning(scheduleStep.getSchedule().getProvider().getLogFileName(), failedInserts + " records were not able to be added or updated in the index.");
				warningCount++;
			}
	
			// Send an Email report on the results of the harvest TODO
			sendReportEmail(null);
		} catch(DatabaseConfigException e) {
			log.error("Unable to connect to the database with the parameters defined in the configuration file.", e);
		}
		finally // Update the error and warning count for the provider
		{
			try
			{
				// Load the provider again in case it was updated during the harvest
				Provider provider = getProviderDAO().getById(scheduleStep.getSchedule().getProvider().getId());
	
				// Increase the warning and error counts as appropriate, then update the provider
				provider.setWarnings(provider.getWarnings() + warningCount);
				provider.setErrors(provider.getErrors() + errorCount);
				provider.setRecordsAdded(provider.getRecordsAdded() + addedCount);
				provider.setRecordsReplaced(provider.getRecordsReplaced() + updatedCount);
	
				getProviderDAO().update(provider);
			}
			catch (DataException e)
			{
				log.warn("Unable to update the provider's warning and error counts due to a Data Exception.", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected String parseRecords(String prefix, Document doc, String baseURL) {
		
		String resumption = null;
		Element root = doc.getRootElement();

		// Check whether or not the response contained an error
		// If it did, throw an exception describing the error
		Element errorEl = root.getChild("error");
		if (errorEl != null) {
			String errorCode = errorEl.getAttributeValue("code");
			throw new RuntimeException("errorCode: "+errorCode+" "+errorEl.getText());
		}

		Element listRecordsEl = null;
		// Get the verb (ListRecords) element.  Try to get it as though it were the child of
		// the root element.  If that doesn't work, assume that it is the root element itself
		try {
			listRecordsEl = root.getChild("ListRecords");
		} catch (Throwable e){
			listRecordsEl = root;
		}

		// Try to get the element containing the first record.  It should be a child of the
		// verb element.
		Element recordEl = null;
		try
		{
			recordEl = listRecordsEl.getChild("record");
		} catch (Throwable e) {
			// Check the response for the request URL
			Element requestUrlElement = listRecordsEl.getChild("requestURL");

			// If the response contained the URL, report the error "no records found"
			if (requestUrlElement != null) {
				LogWriter.addInfo(schedule.getProvider().getLogFileName(), "The OAI provider did not return any records");
				sendReportEmail("The OAI provider did not return any records");
				// Return null to show that there were no records returned
				return null;
			}

			// If we got here, the URL element wasn't found.  In this
			// case report the error as "invalid OAI response"
			LogWriter.addError(schedule.getProvider().getLogFileName(), "The OAI provider retured an invalid response to the ListRecords request.");
			errorCount++;
			sendReportEmail("The OAI provider retured an invalid response to the ListRecords request.");
			throw new RuntimeException("The data provider returned an invalid response to the ListRecords request: " + e.getMessage());
		}

		// Loop over all records in the OAI response
		List recordsEl = listRecordsEl.getChildren("record");
		
		for (Object recordElObj : recordsEl) {
			recordEl = (Element)recordElObj;
			TimingLogger.start("parseRecords loop");
			TimingLogger.start("erl - 1");
			// Check to see if the service was paused or canceled
			checkSignal(baseURL);

            try {
            	TimingLogger.start("getRecordService().parse(recordEl)");
            	Record record = getRecordService().parse(recordEl, schedule.getProvider());
            	TimingLogger.stop("getRecordService().parse(recordEl)");
				record.setHarvest(currentHarvest);
				
				// If the provider has been harvested before, check whether or not this
				// record already exists in the database
				// BDA: tell me why I care?
				//Record oldRecord = (firstHarvest ? null : recordService.getByOaiIdentifierAndProvider(oaiIdentifier, providerId));
				
				repo.addRecord(record);

				TimingLogger.stop("erl - 3");
				TimingLogger.stop("insert record");
				processedRecordCount++;
			} catch (Exception e) {
				failedInserts++;
				log.error("An error occurred in insertion ", e);
			}

            recordsFound++;
            if(recordsFound % 100000 == 0) {
            	
            	endTime = new Date().getTime();
    			timeDiff = endTime - startTime;
    		
            	LogWriter.addInfo(schedule.getProvider().getLogFileName(), "Indexed " + recordsFound + " records so far. Time taken for this 100k records = " + (timeDiff / (1000*60*60)) + "hrs  " + ((timeDiff % (1000*60*60)) / (1000*60)) + "mins  " + (((timeDiff % (1000*60*60)) % (1000*60)) / 1000) + "sec  " + (((timeDiff % (1000*60*60)) % (1000*60)) % 1000) + "ms  ");
            	startTime = new Date().getTime();
            }

            // If the record contained a resumption token, store that resumption token
            resumption = recordEl.getChildText("resumptionToken");
			if (!StringUtils.isEmpty(resumption)) {
				totalRecordCount = Integer.parseInt(recordEl.getAttributeValue("completeListSize"));
				
				log.debug("The resumption string is " + resumption);
				break;
			} else {
				resumption = null;
			}
			TimingLogger.stop("extractRecords loop");
			
		}

		return resumption;
	}


	/**
	 * Runs the processing directives for this harvest against the record.  For all matching
	 * processing directives, adds the appropriate recordInput objects to the Lucene index.
	 * Also adds the service ID for all matched processing directives to the list of services
	 * to run when the harvest completes.
	 *
	 * @param record The record to match against the processing directives
	 */
	private void checkProcessingDirectives(Record record)
	{
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
			if(processingDirective.getTriggeringSets() != null && processingDirective.getTriggeringSets().size() > 0) 
			{
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
					job.setOrder(jobService.getMaxOrder() + 1); 
					jobService.insertJob(job);
				} catch (DatabaseConfigException dce) {
					log.error("DatabaseConfig exception occured when ading jobs to database", dce);
				}
			}
		} // end loop over matched processing directives
	} // end method checkProcessingDirectives(Record)

	/**
	 * Builds and sends an email report about the harvest to the schedule's notify email address.
	 *
	 * @param problem The problem which prevented the harvest from finishing, or null if the harvest was successful
	 */
	private boolean sendReportEmail(String problem)
	{
		if (schedule.getNotifyEmail() != null && mailer.isConfigured()) {
			// The email's subject
			InetAddress addr = null;
			try {
				addr = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				log.error("Host name query failed.", e);
			}
			String subject = "Results of harvesting " + schedule.getProvider().getOaiProviderUrl() +" by MST Server on " + addr.getHostName();
	
			// The email's body
			StringBuilder body = new StringBuilder();
	
			// First report any problems which prevented the harvest from finishing
			if(problem != null)
				body.append("The harvest failed for the following reason: ").append(problem).append("\n\n");
	
			// Report on the number of records inserted successfully and the number of failed inserts
			if((processedRecordCount!=totalRecordCount) && totalRecordCount!=0)
				body.append("Error: Not all records from the OAI were harvested. \n");
			
			if(totalRecordCount!=0) {
				body.append("Total number of records available for harvest =").append(totalRecordCount).append(" \n");
				body.append("Number of records harvested =").append(processedRecordCount).append(" \n");
				
				body.append("Number of records added successfully to MST  = ").append(addedCount+updatedCount).append(" \n");
				if (failedInserts !=0) {
					body.append("Number of records failed =").append(failedInserts).append("\n\n");
				}
			} else if (processedRecordCount > 0) {
				body.append("Number of records harvested =").append(processedRecordCount).append(" \n");
				
				body.append("Number of records added successfully to MST  = ").append(addedCount+updatedCount).append(" \n");
				if (failedInserts !=0) {
					body.append("Number of records failed =").append(failedInserts).append("\n\n");
				}
				
			} 
			if (recordsFound == 0) {
				body.append("There are no records available for harvest. \n");
			}
			
	
			// Show the log information for warnings and errors
			if(errors.length() > 0)
				body.append("The following errors occurred:\n").append(errors.toString()).append(warnings.length() > 0 ? "\n" : "");
			if(warnings.length() > 0)
				body.append("The following warnings were generated:\n").append(warnings.toString());
	
			return mailer.sendEmail(schedule.getNotifyEmail(), subject, body.toString());
		} else {
			return false;
		}
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status Status) {
		this.status = Status;
	}

	protected void persistStatus() {
		log.info("Changing the Status to " + status);
		schedule.setStatus(getStatus().name());
		try {
			getHarvestScheduleDAO().update(schedule, false);
		} catch (DataException e) {
			log.error("Error during updating status of harvest_schedule to database.", e);
		}
	}

	/**
	 * Checks if the service is paused or canceled. If canceled,
	 * the processing of records is stopped or else if paused,
	 * then waits until it receives a resume or cancel signal
	 */
	protected void checkSignal(String baseURL) {
		if (isPaused()) {
			// Update the status of the harvest schedule
			persistStatus();

			while (isPaused() && !isKilled()) {
				try {
					log.info("Harvester Paused. Sleeping for 3 secs.");
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					log.info("Harvester Resumed.");
				}
			}
			if (!isKilled()) {
				// Harvester is resumed while paused
				LogWriter.addInfo(schedule.getProvider().getLogFileName(), "Harvest of " + baseURL + " has been resumed.");

				// Update the status of the harvest schedule
				persistStatus();
			}
		}
			
		if (isKilled()) {
			LogWriter.addInfo(schedule.getProvider().getLogFileName(), "Harvest of " + baseURL + " has been manually terminated.");
			sendReportEmail("Harvest of " + baseURL + " has been manually terminated.");
			throw new RuntimeException("Harvest received kill signal");
		}
	}

	/**
	 * Gets the provider of the harvest
	 */
	public Provider getProvider() {
		return provider;
	}
	
	public int getProcessedRecordCount() {
		return processedRecordCount;
	}

	public void resetProcessedRecordCount() {
		processedRecordCount = 0;
	}

	public int getTotalRecordCount() {
		return  totalRecordCount;
	}

	public void resetTotalRecordCount() {
		totalRecordCount = 0;
	}

	public boolean isKilled() {
		return Status.KILLED.equals(getStatus());
	}
	
	public boolean isPaused() {
		return Status.PAUSED.equals(getStatus());
	}
	
	public boolean isRunning() {
		return Status.RUNNING.equals(getStatus());
	}

	public RecordService getRecordService() {
		return recordService;
	}

	public void setRecordService(RecordService recordService) {
		this.recordService = recordService;
	}

}