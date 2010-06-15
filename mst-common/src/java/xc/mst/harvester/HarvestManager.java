/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.harvester;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import xc.mst.bo.harvest.Harvest;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.email.Emailer;
import xc.mst.manager.BaseManager;
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
	
	protected HarvestSchedule harvestSchedule = null;
	protected List<HarvestScheduleStep> harvestScheduleSteps = null;
	protected boolean hssFirstTime = true;
	protected int harvestScheduleStepIndex = 0;
	protected Repository repo = null;
	protected Harvest currentHarvest = null;
	protected Date startDate = null;
	protected String resumptionToken = null;
	protected int numErrorsToTolerate = 0;
	protected int numErrorsTolerated = 0;
	
	/**
	 * The granularity of the OAI repository we're harvesting (either GRAN_DAY or GRAN_SECOND)
	 */
	protected int granularity = -1;

	/**
	 * The policy for tracking deleted records that the OAI repository uses (either DELETED_RECORD_NO, DELETED_RECORD_TRANSIENT, or DELETED_RECORD_PERSISTENT)
	 */
	protected int deletedRecord = -1;

	protected Emailer mailer = new Emailer();

	protected boolean firstHarvest = false;
	protected int recordsFound = 0;
	protected int totalRecordCount = 0;
	
	public void cancel() {
	}
	
	public void finish() {
	}
	
	public String getName() {
		return "harvest-"+harvestSchedule.getProvider().getName();
	}
	
	public String getDetailedStatus() {
		return "processed "+recordsFound+" of "+totalRecordCount;
	}

	public void setHarvestSchedule(HarvestSchedule harvestSchedule) {
		this.harvestSchedule = harvestSchedule;
	}
	
	public void setup() {
		try {
			harvestScheduleSteps = getHarvestScheduleStepDAO().getStepsForSchedule(harvestSchedule.getId());
			harvestScheduleStepIndex = 0;
			startDate = new Date();
			recordsFound = 0;
			totalRecordCount = 0;
			numErrorsTolerated = Integer.parseInt(config.getProperty("harvester.numErrorsToTolerate", "0"));
			repo = (Repository)config.getBean("Repository");
	    	repo.setName(harvestSchedule.getProvider().getName());
		} catch (DatabaseConfigException e) {
			getUtil().throwIt(e);
		}
	}
	
	public void logError(Throwable t) {
		try {
			log.error("", t);
			Provider provider = currentHarvest.getProvider();
			provider.setErrors(provider.getErrors()+1);
			getProviderDAO().update(provider);
		} catch (DataException de) {
			throw new RuntimeException(de);
		}
		numErrorsTolerated++;
		if (numErrorsTolerated > numErrorsToTolerate) {
			throw new RuntimeException("numErrorsToTolerate exceeded", t);
		}
	}
	
	public void validate(HarvestScheduleStep scheduleStep) throws DataException {
		Provider provider = harvestSchedule.getProvider();
		// Try to validate the repository.  An exception will be thrown and caught if validation fails.
		// Validate that the repository conforms to the OAI protocol
		TimingLogger.log("about to validate repo");
		ValidateRepository validator = (ValidateRepository)MSTConfiguration.getInstance().getBean("ValidateRepository");
		
		validator.validate(harvestSchedule.getProvider().getId());

		TimingLogger.log("validated repo");
		
		granularity = validator.getGranularity();
		deletedRecord = validator.getDeletedRecordSupport();

		// Get the provider from the repository so we know the formats and sets it
		// supports according to the validation we just performed
		harvestSchedule.setProvider(getProviderDAO().getById(harvestSchedule.getProvider().getId()));
		provider = harvestSchedule.getProvider();
    	
    	String metadataPrefix = scheduleStep.getFormat().getName();

		// Get the format we're to harvest
		Format format = getFormatDAO().getByName(metadataPrefix);

		// If the provider no longer supports the requested format we can't harvest it
		if(!harvestSchedule.getProvider().getFormats().contains(format)) {
			String errorMsg = "The harvest could not be run because the MetadataFormat " + metadataPrefix + 
				" is no longer supported by the OAI repository " + provider.getOaiProviderUrl() + ".";

			LogWriter.addError(harvestSchedule.getProvider().getLogFileName(), errorMsg);
			sendReportEmail(errorMsg);
			throw new RuntimeException(errorMsg);
		} // end if(format no longer supported)

		String setSpec = null;
		
		// If there was a set, set up the setSpec
		if(scheduleStep.getSet() != null)
			setSpec = scheduleStep.getSet().getSetSpec();
		
		// If the provider no longer contains the requested set we can't harvest it
		if(setSpec != null && !harvestSchedule.getProvider().getSets().contains(getSetDAO().getBySetSpec(setSpec))) {
			String errorMsg = "The harvest could not be run because the Set " + setSpec + 
				" is no longer supported by the OAI repository " + provider.getOaiProviderUrl() + ".";

			LogWriter.addError(harvestSchedule.getProvider().getLogFileName(), errorMsg);
			sendReportEmail(errorMsg);
			throw new RuntimeException(errorMsg);
		}
	}
	
	public boolean doSomeWork() {
		if (harvestScheduleStepIndex >= 0 && harvestScheduleStepIndex < harvestScheduleSteps.size()) {
			String resumption = null;
			try {
				HarvestScheduleStep scheduleStep = harvestScheduleSteps.get(harvestScheduleStepIndex);
					
				String metadataPrefix = scheduleStep.getFormat().getName();
				String setSpec = null;
				
				// If there was a set, set up the setSpec
				if(scheduleStep.getSet() != null)
					setSpec = scheduleStep.getSet().getSetSpec();
					
				if (hssFirstTime) {
					// Setup the harvest we're currently running
					currentHarvest = new Harvest();
					currentHarvest.setStartTime(startDate);
					currentHarvest.setProvider(currentHarvest.getProvider());
					currentHarvest.setHarvestSchedule(scheduleStep.getSchedule());
					getHarvestDAO().insert(currentHarvest);
					
					validate(scheduleStep);
					firstHarvest = repo.getSize() == 0;
					resumptionToken = null;
				}
				
				HarvestSchedule schedule = scheduleStep.getSchedule();
				String baseURL = currentHarvest.getProvider().getOaiProviderUrl();
				LogWriter.addInfo(scheduleStep.getSchedule().getProvider().getLogFileName(), "Starting harvest of " + baseURL);

				String request = null;
				
				TimingLogger.log("firstHarvest: "+firstHarvest);

				String verb = "ListRecords";
				request = baseURL;

				// If this is the first request, setup a ListRecords request with the
				// correct metadataPrefix.  If we are supposed harvest a specific set
				// or use a known from or until parameter, set them here as well.
				if (hssFirstTime) {
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
					try {
						resumption = URLEncoder.encode(resumption, "utf-8");
					} catch (UnsupportedEncodingException uee) {
						log.error("couldn't encode resumption token: "+resumption);
					}
					request += "?verb=" + verb + "&resumptionToken=" + resumption;
				}
				
				LogWriter.addInfo(schedule.getProvider().getLogFileName(), "The OAI request is " + request);
				
				// TODO: BDA - I doubt we need this.
				//currentHarvest.setRequest(request);
				//getHarvestDAO().update(currentHarvest);

				if (log.isDebugEnabled()) {
					log.debug("Sending the OAI request: " + request);
				}

				repo.beginBatch();
				// Perform the harvest
				TimingLogger.start("sendRequest");
			    Document doc = getHttpService().sendRequest(request);
			    TimingLogger.stop("sendRequest");

			    TimingLogger.start("extractRecords");
                resumption = parseRecords(metadataPrefix, doc, baseURL);
                TimingLogger.stop("extractRecords");

                repo.endBatch();
				LogWriter.addInfo(scheduleStep.getSchedule().getProvider().getLogFileName(), "Finished harvesting " + baseURL + ", " + recordsFound + " new records were returned by the OAI provider.");
		
			} catch(DataException de) {
				logError(de);
				return false;
			} catch(HttpException he) {
				logError(he);
			} catch(Throwable t) {
				logError(t);
			}
			if (resumption == null) {
				harvestScheduleStepIndex++;
				if (harvestScheduleStepIndex >= harvestScheduleSteps.size()) {
					return false;
				}
			}
			return true;
		} else {
			return false;
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
		try {
			recordEl = listRecordsEl.getChild("record");
		} catch (Throwable e) {
			// Check the response for the request URL
			Element requestUrlElement = listRecordsEl.getChild("requestURL");

			// If the response contained the URL, report the error "no records found"
			if (requestUrlElement != null) {
				LogWriter.addInfo(currentHarvest.getProvider().getLogFileName(), "The OAI provider did not return any records");
				sendReportEmail("The OAI provider did not return any records");
				// Return null to show that there were no records returned
				return null;
			}

			// If we got here, the URL element wasn't found.  In this
			// case report the error as "invalid OAI response"
			LogWriter.addError(currentHarvest.getProvider().getLogFileName(), "The OAI provider retured an invalid response to the ListRecords request.");
			sendReportEmail("The OAI provider retured an invalid response to the ListRecords request.");
			throw new RuntimeException("The data provider returned an invalid response to the ListRecords request: " + e.getMessage());
		}

		// Loop over all records in the OAI response
		List recordsEl = listRecordsEl.getChildren("record");
		
		for (Object recordElObj : recordsEl) {
			recordEl = (Element)recordElObj;
			TimingLogger.start("parseRecords loop");
			TimingLogger.start("erl - 1");

            try {
            	TimingLogger.start("getRecordService().parse(recordEl)");
            	Record record = getRecordService().parse(recordEl, currentHarvest.getProvider());
            	TimingLogger.stop("getRecordService().parse(recordEl)");
				record.setHarvest(currentHarvest);
				
				// If the provider has been harvested before, check whether or not this
				// record already exists in the database
				// BDA: tell me why I care?
				//Record oldRecord = (firstHarvest ? null : recordService.getByOaiIdentifierAndProvider(oaiIdentifier, providerId));
				
				repo.addRecord(record);

				TimingLogger.stop("erl - 3");
				TimingLogger.stop("insert record");
			} catch (Exception e) {
				log.error("An error occurred in insertion ", e);
			}

            recordsFound++;

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
	 * Builds and sends an email report about the harvest to the schedule's notify email address.
	 *
	 * @param problem The problem which prevented the harvest from finishing, or null if the harvest was successful
	 */
	protected boolean sendReportEmail(String problem) {
		if (harvestSchedule.getNotifyEmail() != null && mailer.isConfigured()) {
			// The email's subject
			InetAddress addr = null;
			try {
				addr = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				log.error("Host name query failed.", e);
			}
			String subject = "Results of harvesting " + harvestSchedule.getProvider().getOaiProviderUrl() +" by MST Server on " + addr.getHostName();
	
			// The email's body
			StringBuilder body = new StringBuilder();
	
			// First report any problems which prevented the harvest from finishing
			if(problem != null)
				body.append("The harvest failed for the following reason: ").append(problem).append("\n\n");
			
			if(totalRecordCount!=0) {
				body.append("Total number of records available for harvest =").append(totalRecordCount).append(" \n");
				body.append("Number of records harvested =").append(recordsFound).append(" \n");
			} 

			return mailer.sendEmail(harvestSchedule.getNotifyEmail(), subject, body.toString());
		} else {
			return false;
		}
	}
}