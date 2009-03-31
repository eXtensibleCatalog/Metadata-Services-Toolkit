/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.harvester;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SimpleTimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import xc.mst.bo.harvest.Harvest;
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.harvest.HarvestScheduleStep;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.harvest.DefaultHarvestDAO;
import xc.mst.dao.harvest.HarvestDAO;
import xc.mst.dao.processing.DefaultProcessingDirectiveDAO;
import xc.mst.dao.processing.ProcessingDirectiveDAO;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.DefaultSetDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.provider.SetDAO;
import xc.mst.dao.record.DefaultXcIdentifierForFrbrElementDAO;
import xc.mst.dao.record.XcIdentifierForFrbrElementDAO;
import xc.mst.email.Emailer;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.scheduling.Scheduler;
import xc.mst.scheduling.ServiceWorkerThread;
import xc.mst.utils.LogWriter;
import xc.mst.utils.index.SolrIndexManager;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 *  Harvests metadata from an <a href="http://www.openarchives.org/">OAI</a> data provider, saving the results
 *  to the Lucene index used by the MST. Supports data providers that use resumption tokens for
 *  <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#FlowControl"> flow control</a>,
 *  selective harvesting by <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SelectiveHarvestingandDatestamps">
 *  date</a> or <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SelectiveHarvestingandSets">
 *  set</a> , gzip <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ResponseCompression">
 *  response compression</a> and other protocol features. Supports OAI protocol versions 1.1 and <a
 *  href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm">2.0</a> . <p>
 *
 *  To perform a harvest, use one of the following methods:
 *  <ul>
 *    <li> The static <b>harvest</b> method (for general use): <code>{@link #harvest}}</code><br/>
 *    <br/>
 *    </li>
 *    <li> The non-static <b>doHarvest</b> method (provides a few additional options): {@link #doHarvest}.
 *    <br/>
 *    <br/>
 *    </li>
 *  </ul>
 *  Use of this API assumes familiarity with the <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm">
 *  OAI protocol</a> .<p>
 *
 * @author     Steve Sullivan, John Weatherley
 * @version    $Id: Harvester.java,v 1.49 2007/01/17 16:18:24 jweather Exp $
 */
public class Harvester implements ErrorHandler
{
	/**
	 * Data access object for getting and updating providers
	 */
	private static ProviderDAO providerDao = new DefaultProviderDAO();

	/**
	 * Data access object for getting processing directives
	 */
	private static ProcessingDirectiveDAO processingDirectiveDao = new DefaultProcessingDirectiveDAO();

	/**
	 * Data access object for getting formats
	 */
	private static FormatDAO formatDao = new DefaultFormatDAO();

	/**
	 * Data access object for getting sets
	 */
	private static SetDAO setDao = new DefaultSetDAO();

	/**
	 * Data access object for updating harvests
	 */
	private static HarvestDAO harvestDao = new DefaultHarvestDAO();

	/**
	 * Manager for getting, inserting and updating records
	 */
	private static RecordService recordService = new DefaultRecordService();

	/**
	 * Data access object for getting FRBR level IDs
	 */
	private static XcIdentifierForFrbrElementDAO frbrLevelIdDao = new DefaultXcIdentifierForFrbrElementDAO();

	/**
	 * The number of records inserted for the current harvest
	 */
	private int recordsFound = 0;

	/**
	 * The total amount of time we've spent waiting for the OAI Toolkit (used for debugging)
	 */
	private long oaiRepositoryTime = 0;

	/**
	 * The total amount of time we've spent parsing and inserting records from an OAI response
	 */
	private long insertRecordsTime = 0;

	/**
	 * Timestamp before an OAI request is placed.  This is used to calculate the amount of time spent waiting for a response from the server.
	 */
	private long startOaiRequest = 0;

	/**
	 * Timestamp after an OAI response is received.  This is used to calculate the amount of time spent waiting for a response from the server.
	 */
	private long finishOaiRequest = 0;

	/**
	 * Timestamp before processing of the OAI response is begun.  This is used to calculate the amount of time spent processing the response
	 */
    private long resTokStartTime = 0;

	/**
	 * Timestamp after processing of the OAI response is finished.  This is used to calculate the amount of time spent processing the response
	 */
    private long resTokEndTime = 0;

	/**
	 * The granularity of the OAI repository we're harvesting (either GRAN_DAY or GRAN_SECOND)
	 */
	private int granularity = -1;

	/**
	 * The policy for tracking deleted records that the OAI repository uses (either DELETED_RECORD_NO, DELETED_RECORD_TRANSIENT, or DELETED_RECORD_PERSISTENT)
	 */
	private int deletedRecord = -1;

	/**
	 * A list of XML errors in the OAI response
	 */
	private String xmlerrors;

	/**
	 * A list of XML warnings in the OAI response
	 */
	private String xmlwarnings;

	/**
	 * True if this Harvester is currently running or has already run a harvest
	 */
	private boolean hasDoneHarvest = false;

	/**
	 * True if this Harvester has received the kill command
	 */
	private boolean killed = false;

	/**
	 * How long the Harvester should wait for a response from the OAI server before giving up and throwing an error
	 */
	private int timeOutMilliseconds = 180000;

	/**
     * The harvest schedule being run
     */
	private HarvestSchedule schedule = null;

	/**
     * The harvest being run
     */
	private Harvest currentHarvest = null;

	/**
	 * The provider being harvested
	 */
	private Provider provider = null;

	/**
	 * The ID of the Format entry with the metadataPrefix we're harvesting
	 */
	private Format format = null;

	/**
	 * True once we've logged a HException
	 */
	private boolean loggedHException = false;

	/**
	 * The number of records that could not be inserted correctly
	 */
	private int failedInserts = 0;

	/**
	 * The processing directives for the repository we're harvesting
	 */
	private List<ProcessingDirective> processingDirectives = null;

	/**
	 * A list of services to run after this service's processing completes
	 * The keys are the service IDs and the values are the IDs of the sets
	 * that service's records should get added to
	 */
	private HashMap<Integer, Integer> servicesToRun = new HashMap<Integer, Integer>();

	/**
	 * Used to send email reports
	 */
	private Emailer mailer = new Emailer();

	/**
	 * A list of warnings that occurred during the harvest
	 */
	private StringBuilder warnings = new StringBuilder();

	/**
	 * A list of errors that occurred during the harvest
	 */
	private StringBuilder errors = new StringBuilder();

	/**
	 * A reference to the logger which writes to the HarvestIn log file
	 */
	private static Logger log = Logger.getLogger("harvestIn");

	/**
	 * The number of records added by the current harvest
	 */
	private int addedCount = 0;

	/**
	 * The number of records updated by the current harvest
	 */
	private int updatedCount = 0;

	/**
	 * The number of warnings in the current harvest
	 */
	private int warningCount = 0;

	/**
	 * The number of errors in the current harvest
	 */
	private int errorCount = 0;

	/**
	 * HttpClient used for making OAI requests
	 */
	private HttpClient client = null;
	
	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration = ConfigurationManager.getConfiguration("MetadataServicesToolkit");

	/**
	 * Harvest the given provider, saving the resulting metadata to the Lucene index.
	 *
	 * @param baseURL The baseURL of the data provider.
	 * @param metadataPrefix The format to harvest.  This is passed to the provider in the OAI request as the metadataFormat parameter
	 * @param setSpec The set to harvest.  This is passed to the provider in the OAI request as the setSpec parameter
	 * @param from from parameter in the OAI request we're sending to the provider. May be null.
	 * @param until until parameter in the OAI request we're sending to the provider. May be null.
	 * @param harvestAll True to delete previous harvested records and harvest all records again from scratch
	 * @param harvestAllIfNoDeletedRecord True to harvest all records from scratch if deleted records are not supported
	 * @param timeOutMilliseconds Number of milliseconds the harvester will wait for a response from the data provider before timing out
	 * @param scheduleStep The <code>HarvestScheduleStep</code> with information on the harvest we're running
	 * @param currentHarvest The <code>Harvest</code> Object that will store the results of this harvest, such
	 *                       as the start and end times.  This should represent a Harvest that has already been
	 *                       written to the database.  The corrosponding harvest row will be updated with the
	 *                       results after the harvest finishes running.
	 * @exception Hexception If serious error
	 * @exception OAIErrorException If OAI error
	 */
	public static void harvest(String baseURL, String metadataPrefix, String setSpec, Date from, Date until, boolean harvestAll,
			                   boolean harvestAllIfNoDeletedRecord, int timeOutMilliseconds,
			                   HarvestScheduleStep scheduleStep, Harvest currentHarvest) throws Hexception, OAIErrorException
	{
		LogWriter.addInfo(scheduleStep.getSchedule().getProvider().getLogFileName(), "Starting harvest of " + baseURL);

		// Create a Harvester and use it to run the harvest
		Harvester hvst = new Harvester(timeOutMilliseconds, scheduleStep, currentHarvest);

		try
		{
			hvst.doHarvest(baseURL, metadataPrefix, setSpec, from, until, harvestAll, harvestAllIfNoDeletedRecord);

			log.info("Records harvested " + hvst.recordsFound + ", failed inserts " + hvst.failedInserts);

			LogWriter.addInfo(scheduleStep.getSchedule().getProvider().getLogFileName(), "Finished harvesting " + baseURL + ", " + hvst.recordsFound + " new records were returned by the OAI provider.");

			// Report the number of records which could not be added to the index due to an error
			if(hvst.failedInserts > 0)
			{
				LogWriter.addWarning(scheduleStep.getSchedule().getProvider().getLogFileName(), hvst.failedInserts + " records were not able to be added or updated in the index.");
				hvst.warningCount++;
			}

			// Send an Email report on the results of the harvest
			hvst.sendReportEmail(null);
		}
		finally // Update the error and warning count for the provider
		{
			// Load the provider again in case it was updated during the harvest
			Provider provider = providerDao.getById(scheduleStep.getSchedule().getProvider().getId());

			// Increase the warning and error counts as appropriate, then update the provider
			provider.setWarnings(provider.getWarnings() + hvst.warningCount);
			provider.setErrors(provider.getErrors() + hvst.errorCount);
			provider.setRecordsAdded(provider.getRecordsAdded() + hvst.addedCount);
			provider.setRecordsReplaced(provider.getRecordsReplaced() + hvst.updatedCount);

			try
			{
				providerDao.update(provider);
			}
			catch (DataException e)
			{
				log.warn("Unable to update the provider's warning and error counts due to a Data Exception.", e);
			}
		}
	} // end method harvest(String, String, String, Date, Date, boolean, boolean, int, HarvestScheduleStep, Harvest)

	/**
	 * Creates a Harvester that runs a given harvest schedule step and records the results
	 * using the passed <code>Harvest</code> Object.
	 *
	 * @param timeOutMilliseconds Number of milliseconds the harvester will wait for a response
	 *                            from the data provider before timing out
	 * @param scheduleStep The <code>HarvestScheduleStep</code> with information on the harvest we're running
	 * @param currentHarvest The <code>Harvest</code> Object that will store the results of this harvest, such
	 *                       as the start and end times.  This should represent a Harvest that has already been
	 *                       written to the database.  The corrosponding harvest row will be updated with the
	 *                       results after the harvest finishes running.
	 */
	public Harvester(int timeOutMilliseconds, HarvestScheduleStep scheduleStep, Harvest currentHarvest)
	{
		this.timeOutMilliseconds = timeOutMilliseconds;
		HttpClientParams params = new HttpClientParams();
		params.setSoTimeout(timeOutMilliseconds);
		client = new HttpClient(params, new MultiThreadedHttpConnectionManager());
		this.currentHarvest = currentHarvest;
		this.schedule = scheduleStep.getSchedule();
		this.provider = schedule.getProvider();

		// Get the ProcessingDirectives which could match records harvested from the provider we're harvesting
		processingDirectives = processingDirectiveDao.getBySourceProviderId(provider.getId());
	} // end constructor Harvester(int, HarvestScheduleStep, Harvest)

	/**
	 * Gracefully kills the harvest after the current record is finished being harvested.
	 */
	public void kill()
	{
		log.debug("Harvester kill() ");
		killed = true;
	} // end method kill()

	/**
	 *  Performs the harvest. Note that his method is not safe for multiple harvests - a separate Harvester
	 *  instance should be created for each havest performed.<P>
	 *
	 * @param baseURL The baseURL of the data provider.
	 * @param metadataPrefix The format to harvest.  This is passed to the provider in the OAI request as the metadataFormat parameter
	 * @param setSpec The setSpec of the set to harvest.  This is passed to the provider in the OAI request as the setSpec parameter
	 * @param from from parameter in the OAI request we're sending to the provider. May be null.
	 * @param until until parameter in the OAI request we're sending to the provider. May be null.
	 * @param harvestAll True to delete previous harvested records and harvest all records
	 *                   again from scratch
	 * @param harvestAllIfNoDeletedRecord True to harvest all records from scratch if
	 *                                     deleted records are not supported
	 * @exception Hexception If a serious error occurs.
	 * @exception OAIErrorException If OAI error was returned by the data provider.
	 */
	public void doHarvest(String baseURL, String metadataPrefix, String setSpec, Date from, Date until,
	                      boolean harvestAll, boolean harvestAllIfNoDeletedRecord) throws Hexception, OAIErrorException
    {
		String errorMsg = null;
		String request = null;

		// Report an error if the URL is invalid
		if (!baseURL.startsWith("http://"))
		{
			errorMsg = "baseURL does not start with http://";
			log.error(errorMsg);

			LogWriter.addError(schedule.getProvider().getLogFileName(), "Harvest of " + baseURL + ", failed because the OAI provider's URL must begin with \"http://\".");
			errorCount++;

			loggedHException = true;

			sendReportEmail(errorMsg);

			throw new Hexception(errorMsg);
		} // end if(URL invalid)

		// Try to validate the repository.  An exception will be thrown and caught if validation fails.
		try
		{
			// Validate that the repository conforms to the OAI protocol
			ValidateRepository validator = new ValidateRepository();
			validator.validate(schedule.getProvider().getId(), currentHarvest.getId());

			granularity = validator.getGranularity();

			// Get the provider from the repository so we know the formats and sets it
			// supports according to the validation we just performed
			schedule.setProvider(providerDao.getById(schedule.getProvider().getId()));
			provider = schedule.getProvider();

			// Get the format we're to harvest
			format = formatDao.getByName(metadataPrefix);

			// If the provider no longer supports the requested format we can't harvest it
			if(!schedule.getProvider().getFormats().contains(format))
			{
				errorMsg = "The harvest could not be run because the MetadataFormat " + metadataPrefix + " is no longer supported by the OAI repository " + baseURL + ".";

				LogWriter.addError(schedule.getProvider().getLogFileName(), errorMsg);
				errorCount++;

				loggedHException = true;

				sendReportEmail(errorMsg);
				
				throw new Hexception(errorMsg);
			} // end if(format no longer supported)

			// If the provider no longer contains the requested set we can't harvest it
			if(setSpec != null && !schedule.getProvider().getSets().contains(setDao.getBySetSpec(setSpec)))
			{
				errorMsg = "The harvest could not be run because the Set " + setSpec + " is no longer supported by the OAI repository " + baseURL + ".";

				LogWriter.addError(schedule.getProvider().getLogFileName(), errorMsg);
				errorCount++;

				loggedHException = true;

				sendReportEmail(errorMsg);
				
				throw new Hexception(errorMsg);
			} // end if(set no longer supported)
		} // end try(validate the repository)
		catch (Hexception e) // This will happen if validation of the repository fails
		{
			log.error(e.getMessage());

			LogWriter.addError(schedule.getProvider().getLogFileName(), e.getMessage());
			errorCount++;

			loggedHException = true;

			sendReportEmail(e.getMessage());

			throw e;
		} // end catch(Hexception)

		if(log.isDebugEnabled())
		{
			if (from == null)
				log.debug("doHarvest() from: null");
			else
				log.debug("doHarvest() from: " + from);

			log.debug("harvestAll is " + harvestAll);
		} // end if(debug level logging enabled)

		// Make sure this harvester has not run another harvest, and throw an exception if it has
		synchronized (this)
		{
			if (hasDoneHarvest)
				throw new Hexception("This harvester has already performed a harvest. Please use a new Harvester instance.");

			hasDoneHarvest = true;
		} // end synchronized

		try
		{
			killed = false;

			String verb = "ListRecords";
			String resumption = null;

			if (harvestAllIfNoDeletedRecord && deletedRecord == ValidateRepository.DELETED_RECORD_NO)
				harvestAll = true;

			if (harvestAll)
			{
				// Make sure from and until dates are not used for a full harvest:
				from = null;
				until = null;
			} // end if(we should harvest all records)

			// If metadataPrefix is not specified, log an error and abort
			if (metadataPrefix == null)
			{
				errorMsg = "The harvest could not be run because the MetadataFormat to harvest was not specified.";
				log.error(errorMsg);

				LogWriter.addError(schedule.getProvider().getLogFileName(), errorMsg);
				errorCount++;

				loggedHException = true;

				sendReportEmail(errorMsg);

				throw new Hexception(errorMsg);
			} // end if(metadataPrefix not specified)

			// This loop places a ListRecords request, and then continues placing requests
			// until it receives a null resumption token
			do
			{
				// Abort the harvest if the harvester was killed
				if (killed)
				{
					LogWriter.addInfo(schedule.getProvider().getLogFileName(), "Harvest of " + baseURL + " has been manually terminated.");

					loggedHException = true;

					sendReportEmail("Harvest of " + baseURL + " has been manually terminated.");

					throw new Hexception("Harvest received kill signal");
				} // end if(harvester was killed)

				request = baseURL;
				String reqMessage;

				// If this is the first request, setup a ListRecords request with the
				// correct metadataPrefix.  If we are supposed harvest a specific set
				// or use a known from or until parameter, set them here as well.
				if (resumption == null)
				{
					request += "?verb=" + verb;
					request += "&metadataPrefix=" + metadataPrefix;

					if (setSpec != null && setSpec.length() > 0)
						request += "&set=" + setSpec;

					if (from != null)
						request += "&from=" + formatDate(granularity, from);

					if (until != null)
						request += "&until=" + formatDate(granularity, until);

					LogWriter.addInfo(schedule.getProvider().getLogFileName(), "The OAI request is " + request);

					currentHarvest.setRequest(request);
					harvestDao.update(currentHarvest);

					reqMessage = "A request for ListRecords has been made. Establishing connection with the data provider...";
				} // end if(this is the first request)
				// This is not the first request for the harvest, so setup a ListRecords
				// request with the correct resumptionToken.
				else
				{
					reqMessage = "A request for ListRecords with resumptionToken " + resumption + " has been made. Establishing connection with the data provider...";

					// Try to encode the resumption token to include it in the URL.
					// Don't worry if encoding it failed because the OAI request may work anyway
					try
					{
						resumption = URLEncoder.encode(resumption, "utf-8");
					} // end try(encode the resumption token)
					catch (Exception e)
					{
						log.warn("An error occurred when encoding the resumption token for use in a URL.", e);
						
						LogWriter.addWarning(schedule.getProvider().getLogFileName(), "An error occurred when trying to encode the resumption token returned by the provider as UTF-8.  The resumption token was: " + resumption);
						warningCount++;
					} // end catch(Exception)

					request += "?verb=" + verb + "&resumptionToken=" + resumption;
				} // end else(this is not the first request)

				if (log.isDebugEnabled())
					log.debug(reqMessage);

				LogWriter.addInfo(schedule.getProvider().getLogFileName(), "Sending the OAI request: " + request);

				// Perform the harvest
			    Document doc = getDoc(request);

                resumption = extractRecords(metadataPrefix, doc, baseURL);
			} while(resumption != null); // Repeat as long as we get a resumption token
		} // end try(run the harvest)
		catch (Hexception he)
		{
			log.error("A Hexeption occurred while harvesting " + baseURL, he);

			// Log the error for the user and send them a report email if we didn't already
			if(!loggedHException)
			{
				LogWriter.addError(schedule.getProvider().getLogFileName(), "An internal error occurred while executing the harvest: " + he.getMessage());
				errorCount++;

				sendReportEmail("An internal error occurred while executing the harvest.");
			} // end if(we didn't already log the error)

			// Throw the Exception so the calling code knows something went wrong
			throw new Hexception(he.getMessage() + "\n, request was " + request);
		} // end catch(Hexception)
		catch (OAIErrorException oaie)
		{
			log.error("An OAIErrorExeption occurred while harvesting " + baseURL, oaie);

			// Log the error for the user and send them a report email
			LogWriter.addError(schedule.getProvider().getLogFileName(), "The OAI provider returned the following error: " + oaie.getOAIErrorCode() + "," + oaie.getOAIErrorMessage());
			errorCount++;

			sendReportEmail("The OAI provider returned the following error: " + oaie.getOAIErrorCode() + "," + oaie.getOAIErrorMessage());

			// Throw the Exception so the calling code knows something went wrong
			throw new OAIErrorException(oaie.getOAIErrorCode(), oaie.getOAIErrorMessage());
		} // end catch(OAIErrorException)
		catch (Throwable e)
		{
			log.error("An error occurred while harvesting " + baseURL, e);

			LogWriter.addError(schedule.getProvider().getLogFileName(), "An internal error occurred while executing the harvest: " + e.getMessage());
			errorCount++;

			// Throw the error so the calling code knows something went wrong
			throw
            	new Hexception("Internal harvester error: " + e);
		} // end catch(Throwable)
		finally
		{
			// Process the records we harvested
			try
			{
				// Write the next XC ID for raw Records
				frbrLevelIdDao.writeNextXcId(XcIdentifierForFrbrElementDAO.ELEMENT_ID_RECORD);

				// Reopen the reader so it can see the record inputs we inserted for this harvest
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
					} // end try(schedule the service to be run)
					catch(Exception e)
					{
						log.error("An error occurred while running the service with ID " + serviceToRun.intValue() + ".", e);
					} // end catch(Exception)
				} // end loop over services to be run
			} // end try(schedule the services that the records triggered)
			catch(DataException e)
			{
				log.error("An error occurred while managing the Index", e);

				LogWriter.addError(schedule.getProvider().getLogFileName(), "An internal error occurred while managing the Index.");
				errorCount++;
			} // end catch(IOException)
		} // end finally(reopen the index and schedule the triggered services)
	} // end method doHarvest(String, String, String, Date, Date, boolean, boolean)

	/**
	 * Extracts records from an XML document and inserts them into the Lucene Index.
	 * Also runs processing directives against the records and marks them to be processed
	 * by the appropriate services after the harvest completes
	 *
	 * @param prefix The metadata prefix.
	 * @param doc The XML document in OAI format.
	 * @param outdir The output directory.
	 * @param baseURL The base URL
	 * @return Returns a resumption token.
	 * @exception Hexception If serious error.
	 * @exception OAIErrorException If OAI error.
	 */
	private String extractRecords(String prefix, Document doc, String baseURL) throws Hexception, OAIErrorException
    {
		String resumption = null;
		Element root = doc.getDocumentElement();

		// Check whether or not the response contained an error
		// If it did, throw an exception describing the error
		Element errorElement = findChild(root, "error");
		if (errorElement != null)
		{
			String oaiErrorCode = errorElement.getAttribute("code");
			String errorMsg = getContent(errorElement);

			if (errorMsg == null)
				errorMsg = "";

			throw new OAIErrorException(oaiErrorCode, getContent(errorElement));
		} // end if(an error occurred)

		Element verbElement = null;

		// Get the verb (ListRecords) element.  Try to get it as though it were the child of
		// the root element.  If that doesn't work, assume that it is the root element itself
		try
		{
			verbElement = mustFindChild(root, "ListRecords");
		} // try to get the ListRecords child element of the root element
		catch (Hexception e)
		{
			// Try protocol version 1.1 format:
			verbElement = root;
		} // end catch(Hexception)

		// Try to get the element containing the first record.  It should be a child of the
		// verb element.
		Element recordElement = null;
		try
		{
			recordElement = mustFindChild(verbElement, "record");
		} // end try(get the record element as a child of the verb element)
		catch (Hexception e)
		{
			// Check the response for the request URL
			Element requestUrlElement = findChild(verbElement, "requestURL");

			// If the response contained the URL, report the error "no records found"
			if (requestUrlElement != null)
			{
				LogWriter.addInfo(schedule.getProvider().getLogFileName(), "The OAI provider did not return any records");

				loggedHException = true;

				sendReportEmail("The OAI provider did not return any records");

				// Return null to show that there were no records returned
				return null;
			} // end if(the response contained the URL)

			// If we got here, the URL element wasn't found.  In this
			// case report the error as "invalid OAI response"
			LogWriter.addError(schedule.getProvider().getLogFileName(), "The OAI provider retured an invalid response to the ListRecords request.");
			errorCount++;

			loggedHException = true;

			sendReportEmail("The OAI provider retured an invalid response to the ListRecords request.");

			throw new Hexception("The data provider returned an invalid response to the ListRecords request: " + e.getMessage());
		} // end catch(Hexception)

		resTokStartTime = System.currentTimeMillis();
		log.info("Time taken between placing the request and begining to process the reply " + (resTokStartTime-finishOaiRequest));

		// Loop over all records in the OAI response
		while (recordElement != null)
		{
			if (killed)
				throw new Hexception("Harvest received kill signal");

            Node metadataNode = null;

            // If the schedule is null set the provider_id to 1,
            // otherwise set it to the provider_id associated with
            // the provider.
            int providerId = (schedule == null ? 1 : schedule.getProvider().getId());
            String oaiIdentifier="";
            String oaiDatestamp = "";
            String oaiHeader = "";
            String oaiXml = "";

            // Get the identifier and the datestamp from the header
			Element headerElement = mustFindChild(recordElement, "header");
			Element identifierElement = mustFindChild(headerElement, "identifier");
			Element datestampElement = mustFindChild(headerElement, "datestamp");
			oaiIdentifier = getContent(identifierElement);
			oaiDatestamp = getContent(datestampElement);

			// Try to insert the record
			try
			{
				// Get the XML from the record
				metadataNode = findChild(recordElement, "metadata");

				// If we found the metadata Node, get the OAI XML from it
				if(metadataNode != null)
				{
					Node oaiXmlNode = metadataNode.getFirstChild();

					// This will prevent us from losing the record if the metadata Element's
					// first child is a text node
					while(oaiXmlNode != null && oaiXmlNode.getNodeType() == Node.TEXT_NODE)
						oaiXmlNode = oaiXmlNode.getNextSibling();

					// Serialize the contents of the OAI XML node into a String
					oaiXml = serialize(oaiXmlNode);
				} // end if(we found the metadata node)

				// Get the status of the record
				String status = headerElement.getAttribute("status");

				// Check whether or not the record is deleted
				boolean deleted = false;
				if (status != null && status.equalsIgnoreCase("deleted"))
					deleted = true;

				// Serialize the contents of the OAI header
				oaiHeader = serialize((Node)headerElement);

				Record record = new Record();
				record.setProvider(provider);
				record.setFormat(format);
				record.setOaiIdentifier(oaiIdentifier);
				record.setOaiDatestamp(oaiDatestamp);
				record.setOaiXml(oaiXml);
				record.setOaiHeader(oaiHeader);
				record.setDeleted(deleted);

				// Get the sets in which the record appears
				Element setSpecElement = findSibling(datestampElement, "setSpec");

				// Loop over the record's sets and add each to the record BO
				while (setSpecElement != null)
				{
					String setSpec = provider.getName().replace(' ', '-') + ":" + getContent(setSpecElement);

					// Split the set into its components
					String[] setSpecLevels = setSpec.split(":");

					// This will build the setSpecs to which the record belongs
					StringBuilder setSpecAtLevel = new StringBuilder();

					// Loop over all levels in the set spec
					for(String setSpecLevel : setSpecLevels)
					{
						// Append the set at the current level to the setSpec at the previous level to
						// get the setSpec for the current level. Append colons as needed
						setSpecAtLevel.append(setSpecAtLevel.length() <= 0 ? setSpecLevel : ":" + setSpecLevel);

						String currentSetSpec = setSpecAtLevel.toString();

						// If the set's already in the index, get it
						Set set = setDao.getBySetSpec(currentSetSpec);

						// Add the set if there wasn't already one in the database
						if(set == null)
						{
							set = new Set();
							set.setSetSpec(currentSetSpec);
							set.setDisplayName(currentSetSpec);
							set.setIsProviderSet(false);
							set.setIsRecordSet(true);
							setDao.insert(set);
						} // end if(the set wasn't found)

						// Add the set's ID to the list of sets to which the record belongs
						record.addSet(set);
					} // end loop over the set levels

					// Get the next setSpec from the OAI response
					setSpecElement = findSibling(setSpecElement, "setSpec");
				} // end loop over setSpecs for the record

				// Check whether or not this record already exists in the database
				Record oldRecord = recordService.getByOaiIdentifierAndProvider(oaiIdentifier, providerId);

				// If the current record is a new record, insert it
				if(oldRecord == null)
					insertNewRecord(record);
				// Otherwise we've seen the record before.  Update or delete it as appropriate
				else
				{
					if(deleted)
						deleteExistingRecord(oldRecord);
					else
						updateExistingRecord(record, oldRecord);
				} // end else(the record already existed it the index)
			} // end try(insert the record)
			catch (Exception e)
			{
				failedInserts++;
				log.error("An error occurred in insertion ", e);
			} // end catch(Exception)

			recordElement = findSibling(recordElement, "record", "resumptionToken");

            recordsFound++;
            if(recordsFound % 100000 == 0)
            	LogWriter.addInfo(schedule.getProvider().getLogFileName(), "Inserted " + recordsFound + " records so far.");

            // If the record contained a resumption token, store that resumption token
			if (recordElement != null && recordElement.getNodeName().equals("resumptionToken"))
			{
				resumption = getContent(recordElement);

				log.info("The resumption string is " + resumption);

				if (resumption.length() == 0)
					resumption = null;

				break;
			} // end if(record contained a resumptionToken)
		} // end loop over record elements

		resTokEndTime = System.currentTimeMillis();

		log.info("Time to clear the resumption token " + (resTokEndTime - resTokStartTime));
		insertRecordsTime += (resTokEndTime - resTokStartTime);
		log.info("Total time to clear resumption tokens " + insertRecordsTime);

		return resumption;
	} // end method extractRecords(String, Document, String)

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
			// Run the processing directives against the record we're inserting
			checkProcessingDirectives(record);

			if(!recordService.insert(record))
				log.error("Failed to insert the new record with the OAI Identifier " + record.getOaiIdentifier() + ".");
			else
				addedCount++;
		} // end try(insert the record)
		catch (DataException e)
		{
			failedInserts++;
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
	private void updateExistingRecord(Record newRecord, Record oldRecord)
	{
		try
		{
			// Set the new record's ID to the old record's ID so when we call update()
			// on the new record it will update the correct record in the Lucene index
			newRecord.setId(oldRecord.getId());
			newRecord.setCreatedAt(oldRecord.getCreatedAt());
			newRecord.setUpdatedAt(new Date());

			// Run the processing directives against the record we're updating
			checkProcessingDirectives(newRecord);

			if(!recordService.update(newRecord))
				log.error("The update failed for the record with ID " + newRecord.getId() + ".");
			else
				updatedCount++;
		} // end try(update the record)
		catch (DataException e)
		{
			failedInserts++;
			log.error("An exception occurred while updating the record into the Lucene index.", e);
		} // end catch(DataException)
	} // end method updateExistingRecord(Record, Record)

	/**
	 * Deletes a record in the Lucene index and sets up RecordInput values
	 * for any processing directives the record matched so the appropriate
	 * services reprocess the record after the delete
	 *
	 * @param deleteMe The record to delete
	 */
	private void deleteExistingRecord(Record deleteMe)
	{
		try
		{
			deleteMe.setDeleted(true);

			// Run the processing directives against the record we're deleting
			checkProcessingDirectives(deleteMe);

			if(!recordService.update(deleteMe))
				log.error("Failed to delete the new record with the ID " + deleteMe.getId() + ".");
			else
				updatedCount++;
		} // end try(delete the record)
		catch (DataException e)
		{
			failedInserts++;
			log.error("An exception occurred while deleting the record from the Lucene index.", e);
		} // end catch(DataException)
	} // end method deleteExistingRecord(Record)

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
					} // end if(set triggers processing directive)
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
				servicesToRun.put(serviceId, new Integer(matchedProcessingDirective.getOutputSet() == null ? 0 : matchedProcessingDirective.getOutputSet().getId()));
		} // end loop over matched processing directives
	} // end method checkProcessingDirectives(Record)

	/**
	 * Builds and sends an email report about the harvest to the schedule's notify email address.
	 *
	 * @param problem The problem which prevented the harvest from finishing, or null if the harvest was successful
	 */
	private boolean sendReportEmail(String problem)
	{
		// The email's subject
		String subject = "Results of harvesting " + schedule.getProvider().getOaiProviderUrl();

		// The email's body
		StringBuilder body = new StringBuilder();

		// First report any problems which prevented the harvest from finishing
		if(problem != null)
			body.append("The harvest failed for the following reason: ").append(problem).append("\n\n");

		// Report on the number of records inserted successfully and the number of failed inserts
		body.append(recordsFound).append(" Records were successfully harvested.\n");
		body.append(failedInserts).append(" Records were not able to harvested.\n\n");

		// Show the log information for warnings and errors
		if(errors.length() > 0)
			body.append("The following errors occurred:\n").append(errors.toString()).append(warnings.length() > 0 ? "\n" : "");
		if(warnings.length() > 0)
			body.append("The following warnings were generated:\n").append(warnings.toString());

		return mailer.sendEmail(schedule.getNotifyEmail(), subject, body.toString());
	} // end method sendReportEmail

	/**
	 * Retrieves an OAI XML document via http and parses the XML.
	 *
	 * @param request The http request, e.g., "http://www.x.com/..."
	 * @return The doc value
	 * @exception Hexception If an error occurs
	 */
	private Document getDoc(String request)throws Hexception
	{
		if(log.isDebugEnabled())
			log.debug("Sending the OAI request: " + request);

		Document doc = null;
		
		GetMethod getOaiResponse = null;
		
		try
		{
			int statusCode = 0; // The status code in the HTTP response
			
			startOaiRequest = System.currentTimeMillis();
			
			synchronized(client)
			{
				// Instantiate a GET HTTP method to get the Voyager "first" page
				getOaiResponse = new GetMethod(request);
				
				// Execute the get method to get the Voyager "first" page
				statusCode = client.executeMethod(getOaiResponse);
			}
	        
	        // If the get was successful (200 is the status code for success)
	        if(statusCode == 200)
	        {	        
	        	InputStream istm = getOaiResponse.getResponseBodyAsStream();
	        	finishOaiRequest = System.currentTimeMillis();

	            log.info("Time taken to get a response from the server " + (finishOaiRequest-startOaiRequest));
	            oaiRepositoryTime += (finishOaiRequest-startOaiRequest);
	            log.info("Total time taken to get a response from the server " + oaiRepositoryTime);
	
				DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
				docfactory.setCoalescing(true);
				docfactory.setExpandEntityReferences(true);
				docfactory.setIgnoringComments(true);
				docfactory.setNamespaceAware(true);
	
				// We must set validation false since jdk1.4 parser
				// doesn't know about schemas.
				docfactory.setValidating(false);
	
				// Ignore whitespace doesn't work unless setValidating(true),
				// according to javadocs.
				docfactory.setIgnoringElementContentWhitespace(false);
	
				DocumentBuilder docbuilder = docfactory.newDocumentBuilder();
	
				xmlerrors = "";
				xmlwarnings = "";
				docbuilder.setErrorHandler(this);
				doc = docbuilder.parse(istm);
				istm.close();
	
				if (xmlerrors.length() > 0 || xmlwarnings.length() > 0)
				{
					String msg = "XML validation failed.\n";
	
					if (xmlerrors.length() > 0)
					{
						msg += "Errors:\n" + xmlerrors;
						LogWriter.addError(schedule.getProvider().getLogFileName(), "The OAI provider's response had the following XML errors:\n" + xmlerrors);
						errorCount++;
					} // end if(error parsing the response)
					if (xmlwarnings.length() > 0)
					{
						msg += "Warnings:\n" + xmlwarnings;
						LogWriter.addWarning(schedule.getProvider().getLogFileName(), "The OAI provider's response had the following XML warnings:\n" + xmlwarnings);
						warningCount++;
					} // end if(warning parsing the response)
	
					throw new Hexception(msg);
				} // end if(problem parsing the responses)
	        } // end if(status code indicates success)
	        else
	        {
	        	String msg = "Error getting the HTML document, the HTTP status code was " + statusCode;
	        	
	        	log.error(msg);
	        	
	        	LogWriter.addError(schedule.getProvider().getLogFileName(), msg);
				errorCount++;
				
				loggedHException = true;

				sendReportEmail(msg);

				throw new Hexception(msg);
	        }
		} // end try(place the HTTP request)
		catch (Exception exc)
		{
			log.error("Error getting the HTML document.", exc);

			String msg = "";

			if (exc.getMessage().matches(".*respcode.*"))
				msg = "The request for data resulted in an invalid response from the provider. The baseURL indicated may be incorrect or the service may be unavailable. HTTP response: " + exc.getMessage();
			else
				msg = "The request for data resulted in an invalid response from the provider. Error: " + exc.getMessage();

			LogWriter.addError(schedule.getProvider().getLogFileName(), msg);
			errorCount++;

			loggedHException = true;

			sendReportEmail(msg);

			throw new Hexception(msg);
		} // end catch(Exception)
		return doc;
	} // end method getDoc(String)

	/**
	 * Finds the first immediate child of the specified Element having the specified tag; throws Hexception if
	 * none found.
	 *
	 * @param element The element to get the child of
	 * @param tag The child we're looking for
	 * @return The requested child of the passed element
	 * @exception Hexception If the child could not be found
	 */
	private Element mustFindChild(Element element, String tag) throws Hexception
	{
		Element result = findChild(element, tag);

		// If the element was not found, throw an exception
		if (result == null)
		{
			LogWriter.addError(schedule.getProvider().getLogFileName(), "A required element \"" + tag + "\" was missing from the OAI response");
			errorCount++;

			throw new Hexception("Required element not found: \"" + tag + "\"");
		} // end if(tag not found)

		return result;
	} // end method mustFindChild(Element, String)

	/**
	 * Finds the first immediate child of the specified Element having the specified tag; returns null if none
	 * found.
	 *
	 * @param element The element to get the child of
	 * @param tag The child we're looking for
	 * @return The requested child of the passed element, or null if there was no child of the
	 *         passed element with the requested tag.
	 */
	private Element findChild(Element element, String tag)
	{
		Element result = null;
		Node node = element.getFirstChild();

		// Loop over all child nodes of the element until we find the requested tag
		while (node != null)
		{
			// If we found the correct node, break from the loop and return it
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(tag))
			{
				result = (Element) node;
				break;
			} // end if(correct node found)

			// Check the sibling of the current node in the next iteration of the loop
			node = node.getNextSibling();
		} // end loop over the element's children

		return result;
	} // end method findChild(Element, String)

	/**
	 * Finds the first following sibling of the specified Element having the specified tag; returns null if none
	 * found.
	 *
	 * @param element The element to get the sibling of
	 * @param tag The sibling we're looking for
	 * @return The requested sibling of the passed element, or null if there was no sibling of the
	 *         passed element with the requested tag.
	 */
	private Element findSibling(Element element, String tag)
	{
		Element result = null;
		Node node = element.getNextSibling();

		// Loop over the siblings of the passed node until we find the requested tag
		while (node != null)
		{
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(tag))
			{
				result = (Element) node;
				break;
			} // end if(current element matched the tag)

			node = node.getNextSibling();
		} // end loop over siblings

		return result;
	} // end method findSibling(Element, String)

	/**
	 *  Finds the first following sibling of the specified Element having either of the specified tags; returns
	 *  null if none found.
	 *
	 * @param element The element to get the sibling of
	 * @param taga The first sibling we're looking for
	 * @param tagb The second sibling we're looking for
	 * @return The first sibling of the passed element which matched either requested tag,
	 *         or null if there was no sibling of the passed element with either requested tag.
	 */
	private Element findSibling(Element element, String taga, String tagb)
	{
		Element result = null;
		Node node = element.getNextSibling();

		// Loop over the siblings of the passed node until we find one of the the requested tags
		while (node != null)
		{
			if (node.getNodeType() == Node.ELEMENT_NODE && (node.getNodeName().equals(taga) || node.getNodeName().equals(tagb)))
			{
				result = (Element) node;
				break;
			} // end if(current element matched either tag)

			node = node.getNextSibling();
		} // end loop over siblings

		return result;
	} // end method findSibling(Element, String, String)

	/**
	 * Returns the concatenation of all text node content under the specified node.
	 *
	 * @param node The node to get the content for
	 * @return The concatenation of all text node content under the specified node.
	 */
	private String getContent(Node node)
	{
		StringBuffer result = new StringBuffer();
		getContentSub(node, result);
		return result.toString();
	} // end method getContent(Node)

	/**
	 *  Appends to the result StringBuffer the concatenation of all text node content under the specified node.
	 *
	 * @param node The node to get the content for
	 * @param resultBuffer The buffer to hold the results
	 */
	private void getContentSub(Node node, StringBuffer resultBuffer)
	{
		switch (node.getNodeType())
		{
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				resultBuffer.append(node.getNodeValue().trim());
				break;
			case Node.ELEMENT_NODE:
				// recurse on children
				Node childNode = node.getFirstChild();
				while (childNode != null)
				{
					getContentSub(childNode, resultBuffer);
					childNode = childNode.getNextSibling();
				} // end loop over children
				break;
			default: // ignore all else
		} // end switch on node type
	} // end method getContentSub(Node, StringBuffer)

	/**
	 *  Formats a date as specified in section 3.3 of http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm.
	 *  <p>
	 *
	 *  If granularity is GRAN_DAY, the format is "yyyy-MM-dd". If granularity is GRAN_SECOND, the format is
	 *  "yyyy-MM-ddTHH:mm:ssZ".
	 *
	 * @param granularity GRAN_DAY or GRAN_SECOND
	 * @param dt The Date
	 * @return An OAI datestamp
	 */
	private String formatDate(int granularity, Date dt)
	{
		SimpleDateFormat sdf = null;

		if (granularity == ValidateRepository.GRAN_SECOND)
		{
			sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		} // end if(second granularity is used)
		else
			sdf = new SimpleDateFormat("yyyy-MM-dd");

		String res = sdf.format(dt);

		return res;
	} // end method formatDate(int, Date)

	/**
	 * Creates a String from an XML node
	 *
	 * @param node The node to serialize
	 * @return The node in String form
	 */
	private String serialize(Node node)
	{
		try
		{
			OutputFormat format = new OutputFormat();
			StringWriter result = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(result, format);
	        switch (node.getNodeType())
	        {
	        	case Node.DOCUMENT_NODE:
	               serializer.serialize((Document) node);
	               break;
	            case Node.ELEMENT_NODE:
	               serializer.serialize((Element) node);
	               break;
	            case Node.DOCUMENT_FRAGMENT_NODE:
	               serializer.serialize((DocumentFragment) node);
	               break;
	         } // end switch on node type

	        return result.toString();
	    } // end try(serialize the record)
	    catch (Exception e)
	    {
	        log.error("Error: ", e);
	        return null;
	    } // end catch(Exception)
	} // end method serialize(Node)

	/**
	 * Handles fatal errors. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void fatalError(SAXParseException exc)
	{
		xmlerrors += exc;
	} // end method fatalError(SAXParseException)

	/**
	 * Handles errors. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void error(SAXParseException exc)
	{
		xmlerrors += exc;
	} // end method error(SAXParseException)

	/**
	 * Handles warnings. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void warning(SAXParseException exc)
	{
		xmlwarnings += exc;
	} // end method warning(SAXParseException)
} // end class Harvester