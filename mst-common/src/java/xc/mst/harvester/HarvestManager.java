/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.harvester;

import gnu.trove.TObjectLongHashMap;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTimeZone;
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
import xc.mst.scheduling.WorkerThread;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.XmlHelper;


public class HarvestManager extends BaseManager implements WorkDelegate {
	
	/**
	 * A reference to the logger which writes to the HarvestIn log file
	 */
	private static Logger log = Logger.getLogger("harvestIn");
	protected WorkerThread workerThread = null;
	
	protected static DateTimeFormatter UTC_FORMATTER = null;
	static {
		UTC_FORMATTER = ISODateTimeFormat.dateTime();
		UTC_FORMATTER = UTC_FORMATTER.withZone(DateTimeZone.UTC);
	}
	//        Map<MostSigToken, ListOfAllOaoIdsThatHaveToken<EntireOaiId, recordId>>
	protected TObjectLongHashMap harvestCache = new TObjectLongHashMap();
	
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
	protected int requestsSent4Step = 0;
	protected int numberOfNewRecords = 0;
	protected int numberOfUpdatedRecords = 0;
	
	protected ReentrantLock running = new ReentrantLock();
	
	public String printDate(Date d) {
		String s = UTC_FORMATTER.print(d.getTime());
		s = s.substring(0, s.length()-5)+"Z";
		return s;
	}
	
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
	protected int recordsProcessed = 0;
	protected int totalRecords = 0;

	public void cancel() {running.lock(); running.unlock();}
	public void finish() {repo.endBatch(); running.lock(); running.unlock();}
	public void pause()  {repo.endBatch(); running.lock(); running.unlock();}
	public void resume() {}
	
	public String getName() {
		return "harvest-"+harvestSchedule.getProvider().getName();
	}
	
	public WorkerThread getWorkerThread() {
		return workerThread;
	}

	public void setWorkerThread(WorkerThread workerThread) {
		this.workerThread = workerThread;
	}
	
	public String getDetailedStatus() {
		return "processed "+recordsProcessed+" of "+totalRecords;
	}
	
	public int getRecordsProcessed() {
		return recordsProcessed;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setHarvestSchedule(HarvestSchedule harvestSchedule) {
		this.harvestSchedule = harvestSchedule;
	}
	
	public void setup() {
		try {
			hssFirstTime = true;
			harvestCache.clear();
			// BDA - I added this check for 0 becuase the initialization of HarvestSchedule.steps creates a new
			// list of size zero.  The DAO which creates the harvestSchedule doesn't inject steps into it.  So
			// there's really no other way to tell. 
			if (harvestSchedule.getSteps() == null || harvestSchedule.getSteps().size() == 0) {
				harvestScheduleSteps = getHarvestScheduleStepDAO().getStepsForSchedule(harvestSchedule.getId());
			} else {
				harvestScheduleSteps = harvestSchedule.getSteps();
			}
			harvestScheduleStepIndex = 0;
			startDate = new Date();
			recordsProcessed = 0;
			totalRecords = 0;
			numErrorsTolerated = Integer.parseInt(config.getProperty("harvester.numErrorsToTolerate", "0"));
			repo = getRepositoryService().getRepository(harvestSchedule.getProvider());
			getRepositoryDAO().populateHarvestCache(repo.getName(), harvestCache);
			repo.beginBatch();
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
		running.lock();
		boolean retVal = true;
		/*
		 * BDA - I moved this to the bottom of this method
		String testHarvestMaxRequests = config.getProperty("test.harvest.maxRequests");
		recordsProcessed;
		if (testHarvestMaxRequests != null) {
			int maxRequests = Integer.parseInt(testHarvestMaxRequests);
			if (maxRequests > 0 && maxRequests == requestsSent4Step) {
				retVal = false;
			}
		}
		*/
		requestsSent4Step++;
		log.debug("harvestScheduleSteps.size(): "+harvestScheduleSteps.size());
		if (retVal && harvestScheduleStepIndex >= 0 && harvestScheduleStepIndex < harvestScheduleSteps.size()) {
			try {
				HarvestScheduleStep scheduleStep = harvestScheduleSteps.get(harvestScheduleStepIndex);
					
				String metadataPrefix = null;
				if (scheduleStep != null && scheduleStep.getFormat() != null) {
					metadataPrefix = scheduleStep.getFormat().getName();
				}
				
				String setSpec = null;
				
				// If there was a set, set up the setSpec
				if(scheduleStep.getSet() != null)
					setSpec = scheduleStep.getSet().getSetSpec();
					
				
				HarvestSchedule schedule = scheduleStep.getSchedule();
				String baseURL = null;
				
				if (hssFirstTime) {
					// Setup the harvest we're currently running
					currentHarvest = new Harvest();
					currentHarvest.setStartTime(startDate);
					currentHarvest.setProvider(scheduleStep.getSchedule().getProvider());
					currentHarvest.setHarvestSchedule(scheduleStep.getSchedule());
					baseURL = currentHarvest.getProvider().getOaiProviderUrl();	
					getHarvestDAO().insert(currentHarvest);
					log.debug("repo.installOrUpdateIfNecessary()");
					if (!baseURL.startsWith("file:")) 
						validate(scheduleStep);
					firstHarvest = repo.getSize() == 0;
					resumptionToken = null;
				}
				baseURL = currentHarvest.getProvider().getOaiProviderUrl();
				
				LogWriter.addInfo(scheduleStep.getSchedule().getProvider().getLogFileName(), "Starting harvest of " + baseURL);
				
                Provider provider = harvestSchedule.getProvider();
				String request = null;
				Document doc = null;
				if (baseURL.startsWith("file:")) {
					File pwd = new File(".");
					log.debug("pwd: "+pwd.getAbsolutePath());
					pwd = new File("file://.");
					log.debug("pwd: "+pwd.getAbsolutePath());
					//pwd = new File(new URI("file://."));
					//log.debug("pwd: "+pwd.getAbsolutePath());
					String folderStr = baseURL.substring("file://".length());
					log.debug("folderStr: "+folderStr);
					File folder = new File(folderStr);
					boolean nextOne = false;
					File file2harvest = null;
					log.debug("provider.getLastOaiRequest(): "+provider.getLastOaiRequest());
					log.debug("provider: "+provider);
					log.debug("provider.hashCode(): "+provider.hashCode());
					for (File file : folder.listFiles()) {
						log.debug("file.getName(): "+file.getName());
						if (!file.getName().endsWith(".xml")) {
							continue;
						} else if (nextOne || provider.getLastOaiRequest() == null) {
							file2harvest = file;
							break;
						} else {
							if (provider.getLastOaiRequest().equals(file.getName())) {
								nextOne = true;
							}
						}
					}
					log.debug("file2harvest: "+file2harvest);
					if (file2harvest == null) {
						return false;
					}
					provider.setLastOaiRequest(file2harvest.getName());
				    doc = new XmlHelper().getJDomDocument(getUtil().slurp(file2harvest));
				} else if (baseURL.startsWith("http:")) {
					String verb = "ListRecords";
					request = baseURL;
					
					String baseRequest = null;

					// If this is the first request, setup a ListRecords request with the
					// correct metadataPrefix.  If we are supposed harvest a specific set
					// or use a known from or until parameter, set them here as well.
					if (hssFirstTime) {
						request += "?verb=" + verb;
						request += "&metadataPrefix=" + metadataPrefix;

						if (setSpec != null && setSpec.length() > 0)
							request += "&set=" + setSpec;
						
						baseRequest = request;

						//Date from = scheduleStep.getLastRan();
						Date from = getRepositoryDAO().getLastModifiedOai(currentHarvest.getProvider().getName());
						//becuase from is inclusive
						//from = new Date(from.getTime()+1000);
						if (from != null && from.getTime() != 0) {
							request += "&from=" + printDate(from);
							// no need to set the until.  Some repos (eg IRPlus work better w/out an until)
							//+"&until=" + printDate(startDate);
						}
						
						harvestSchedule.setRequest(baseRequest);
						getHarvestScheduleDAO().update(harvestSchedule, false);
					} else {
						try {
							resumptionToken = URLEncoder.encode(resumptionToken, "utf-8");
						} catch (UnsupportedEncodingException uee) {
							log.error("couldn't encode resumption token: "+resumptionToken);
						}
						request += "?verb=" + verb + "&resumptionToken=" + resumptionToken;
					}
					
					LogWriter.addInfo(schedule.getProvider().getLogFileName(), "The OAI request is " + request);
					
					// TODO: BDA - I doubt we need this.
					//currentHarvest.setRequest(request);
					//getHarvestDAO().update(currentHarvest);

					if (log.isDebugEnabled()) {
						log.debug("Sending the OAI request: " + request);
					}

					// Perform the harvest
					TimingLogger.start("sendRequest");
					doc = getHttpService().sendRequest(request);
				    /*
				    log.debug("doc: ");
				    if (log.isDebugEnabled())
				    	log.debug(new XmlHelper().getString(doc.getRootElement()));
				    */
				    TimingLogger.stop("sendRequest");
				    
				    provider.setLastOaiRequest(request);
				}
				
				numberOfNewRecords=0;
				numberOfUpdatedRecords=0;
				TimingLogger.start("parseRecords");
				resumptionToken = parseRecords(metadataPrefix, doc, baseURL);
                log.debug("resumptionToken: "+resumptionToken);
                TimingLogger.stop("parseRecords");
                
                provider.setRecordsAdded(provider.getRecordsAdded() + numberOfNewRecords);
                provider.setRecordsReplaced(provider.getRecordsReplaced() + numberOfUpdatedRecords);
                provider.setLastHarvestEndTime(new Date());
                getProviderDAO().update(provider);
                
				LogWriter.addInfo(scheduleStep.getSchedule().getProvider().getLogFileName(), "Finished harvesting " + baseURL + ", " + recordsProcessed + " new records were returned by the OAI provider.");
		
			} catch(DataException de) {
				logError(de);
				retVal = false;
			} catch(HttpException he) {
				logError(he);
			} catch(Throwable t) {
				logError(t);
			}
			hssFirstTime = false;
			if (resumptionToken == null) {
				hssFirstTime = true;
				harvestScheduleStepIndex++;
				if (harvestScheduleStepIndex >= harvestScheduleSteps.size()) {
					retVal = false;
				}
			}
			if (requestsSent4Step % 10 == 0) {
				TimingLogger.reset();
			}
			retVal = true;
		} else {
			retVal = false;
			TimingLogger.reset();
		}
		if (harvestSchedule.getProvider().getNumberOfRecordsToHarvest() > 0 && 
				harvestSchedule.getProvider().getNumberOfRecordsToHarvest() <= recordsProcessed) {
			retVal = false;
		}
		running.unlock();
		return retVal;
	}

	@SuppressWarnings("unchecked")
	protected String parseRecords(String prefix, Document doc, String baseURL) {
		
		String resumption = null;
		Element root = doc.getRootElement();

		// Check whether or not the response contained an error
		// If it did, throw an exception describing the error
		Element errorEl = root.getChild("error", root.getNamespace());
		if (errorEl != null) {
			String errorCode = errorEl.getAttributeValue("code");
			throw new RuntimeException("errorCode: "+errorCode+" "+errorEl.getText());
		}

		Element listRecordsEl = null;
		// Get the verb (ListRecords) element.  Try to get it as though it were the child of
		// the root element.  If that doesn't work, assume that it is the root element itself
		try {
			listRecordsEl = root.getChild("ListRecords", root.getNamespace());
		} catch (Throwable e){
			listRecordsEl = root;
		}

		// Try to get the element containing the first record.  It should be a child of the
		// verb element.
		Element recordEl = null;
		try {
			recordEl = listRecordsEl.getChild("record", root.getNamespace());
		} catch (Throwable e) {
			// Check the response for the request URL
			Element requestUrlElement = listRecordsEl.getChild("requestURL", root.getNamespace());

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
		List recordsEl = listRecordsEl.getChildren("record", root.getNamespace());
		log.debug("recordsEl.size(): "+recordsEl.size());

		for (Object recordElObj : recordsEl) {
			recordEl = (Element)recordElObj;

            try {
            	HarvestScheduleStep scheduleStep = harvestScheduleSteps.get(harvestScheduleStepIndex);
            	TimingLogger.start("getRecordService().parse(recordEl)");
            	Record record = getRecordService().parse(recordEl, currentHarvest.getProvider());
            	TimingLogger.stop("getRecordService().parse(recordEl)");
            	record.setFormat(scheduleStep.getFormat());
				record.setHarvest(currentHarvest);
				record.setProvider(currentHarvest.getProvider());
				
				// If the provider has been harvested before, check whether or not this
				// record already exists in the database
				// BDA: tell me why I care? SR : To keep count of number of new records added and number of updated records.
				//Record oldRecord = (firstHarvest ? null : recordService.getByOaiIdentifierAndProvider(oaiIdentifier, providerId));
				String nonRedundantId = getUtil().getNonRedundantOaiId(record.getHarvestedOaiIdentifier());
				Long recordId = harvestCache.get(nonRedundantId);
				if (recordId == 0) {
					getRepositoryDAO().injectId(record);
					harvestCache.put(nonRedundantId, record.getId());
					numberOfNewRecords++;
				} else {
					record.setId(recordId);
					numberOfUpdatedRecords++;
				}				

				repo.addRecord(record);

			} catch (Exception e) {
				log.error("An error occurred in insertion ", e);
			}
            recordsProcessed++;
		}

        // If the record contained a resumption token, store that resumption token
        Element resumptionEl = listRecordsEl.getChild("resumptionToken", root.getNamespace());

        if (resumptionEl != null) {
        	resumption = resumptionEl.getText();
        }
        log.debug("resumption: "+resumption);
		if (!StringUtils.isEmpty(resumption)) {
			totalRecords = Integer.parseInt(resumptionEl.getAttributeValue("completeListSize"));
			log.debug("The resumption string is " + resumption);
		} else {
			resumption = null;
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
			
			if(totalRecords!=0) {
				body.append("Total number of records available for harvest =").append(totalRecords).append(" \n");
				body.append("Number of records harvested =").append(recordsProcessed).append(" \n");
			} 

			return mailer.sendEmail(harvestSchedule.getNotifyEmail(), subject, body.toString());
		} else {
			return false;
		}
	}
}