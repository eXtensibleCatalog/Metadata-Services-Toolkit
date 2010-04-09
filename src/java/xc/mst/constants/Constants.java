/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.constants;

/**
 * This class defines several constants as public static final variables
 * which may be used throughout the Metadata Hub
 *
 * @author Eric Osisek
 */
public class Constants
{
	//*******************************************************************
	// Logger Names
	//*******************************************************************

	/**
	 * Logger for the Harvest-in log file
	 */
	public static final String LOGGER_HARVEST_IN = "harvestIn";

	/**
	 * Logger for the Validation log file
	 */
	public static final String LOGGER_VALIDATION = "validation";

	/**
	 * Logger for the Processing log file
	 */
	public static final String LOGGER_PROCESSING = "processing";

	/**
	 * Logger for the Harvest-out log file
	 */
	public static final String LOGGER_HARVEST_OUT = "harvestOut";

	/**
	 * Logger for the General log file
	 */
	public static final String LOGGER_GENERAL = "general";

	//*******************************************************************
	// Service Names
	//*******************************************************************

	/**
	 * The name of the normalization service
	 */
	public static final String NORMALIZATION_SERVICE_NAME = "Normalization";

	/**
	 * The name of the transformation service
	 */
	public static final String TRANSFORMATION_SERVICE_NAME = "Transformation";

	/**
	 * The name of the aggregation service
	 */
	public static final String AGGREGATION_SERVICE_NAME = "Aggregation";
	
	//******************************************************************
	// General log file IDs
	//******************************************************************

	/**
	 * ID of the repository management log file
	 */
	public static final int LOG_ID_REPOSITORY_MANAGEMENT = 1;
	
	/**
	 * ID of the users management log file
	 */
	public static final int LOG_ID_USER_MANAGEMENT = 2;
	
	/**
	 * ID of the authentication server management log file
	 */
	public static final int LOG_ID_AUTHENTICATION_SERVER_MANAGEMENT = 3;
	
	/**
	 * ID of the MySQL log file
	 */
	public static final int LOG_ID_MYSQL = 4;
	
	/**
	 * ID of the Solr Index log file
	 */
	public static final int LOG_ID_SOLR_INDEX = 5;
	
	/**
	 * ID of the jobs management log file
	 */
	public static final int LOG_ID_JOBS_MANAGEMENT = 6;
	
	/**
	 * ID of the service management log file
	 */
	public static final int LOG_ID_SERVICE_MANAGEMENT = 7;
	
	/**
	 * ID of the MST Configuration log file
	 */
	public static final int LOG_ID_MST_CONFIGURATION = 8;
	//*******************************************************************
	// Configuration Parameters
	//*******************************************************************

	/**
	 * Parameter for looking up the location of the logger configuration file from the MST's configuration file
	 */
	public static final String CONFIG_LOGGER_CONFIG_FILE_LOCATION = "LoggerConfigFileLocation";

	/**
	 * Parameter for looking up the harvester's timeout from the configuration file
	 */
	public static final String CONFIG_HARVESTER_TIMEOUT_URL = "HarvesterTimeout";
	
	/**
	 * Parameter for looking up the location of the solr configuration
	 */
	public static final String CONFIG_SOLR_HOME = "SolrHome";
	
	/**
	 * Parameter for looking up the level at which Solr should be logging
	 */
	public static final String CONFIG_SOLR_LOG_LEVEL = "SolrLogLevel";
	
	/**
	 * Parameter for looking up the maximum size of the Lucene indexer's RAM buffer in MB from the configuration file
	 */
	public static final String CONFIG_LUCENE_INDEXER_RAM_BUFFER_MB = "LuceneIndexerRAMBufferMB";

	/**
	 * Parameter for looking up the code to represent the organization in the $5 subfield of Marc XML files from the configuration file
	 */
	public static final String CONFIG_ORGANIZATION_CODE = "OrganizationCode";

	/**
	 * Parameter for looking up the maximum number of concurrently running harvests from the configuration file
	 */
	public static final String CONFIG_MAX_RUNNING_HARVEST_JOBS = "MaxRunningHarvestJobs";

	/**
	 * Parameter for looking up the maximum number of concurrently running services from the configuration file
	 */
	public static final String CONFIG_MAX_RUNNING_SERVICE_JOBS = "MaxRunningServiceJobs";

	/**
	 * Parameter for looking up the directory where the Lucene index should be stored from the configuration file
	 */
	public static final String CONFIG_LUCENE_INDEX_DIRECTORY = "LuceneIndexDirectory";

	// Database parameters

	/**
	 * Parameter for looking up the URL of the database from the configuration file
	 */
	public static final String CONFIG_DATABASE_URL = "DatabaseUrl";

	/**
	 * Parameter for looking up the username to log into the database from the configuration file
	 */
	public static final String CONFIG_DATABASE_USERNAME = "DatabaseUsername";

	/**
	 * Parameter for looking up the password to log into the database from the configuration file
	 */
	public static final String CONFIG_DATABASE_PASSWORD = "DatabasePassword";

	// OAI Repository parameters

	/**
	 * Parameter for looking up the name of the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_NAME = "OaiRepoName";

	/**
	 * Parameter for looking up the OAI version of the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_PROTOCOL_VERSION = "OaiRepoProtocolVersion";

	/**
	 * Parameter for looking up the administrator email of the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_ADMIN_EMAIL = "OaiRepoAdminEmail";

	/**
	 * Parameter for looking up the deleted record handling of the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_DELETED_RECORD = "OaiRepoDeletedRecord";

	/**
	 * Parameter for looking up the granularity of the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_GRANULARITY = "OaiRepoGranularity";

	/**
	 * Parameter for looking up the supported compression types of the OAI repository from the configuration file.
	 * Supported compression types will be seperated by semi colons.
	 */
	public static final String CONFIG_OAI_REPO_COMPRESSION = "OaiRepoCompression";

	/**
	 * Parameter for looking up the XML for the description of the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_DESCRIPTION = "OaiRepoDescription";

	/**
	 * Parameter for looking up the delimiter used by the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_DELIMITER = "OaiRepoDelimiter";

	/**
	 * Parameter for looking up the scheme used by the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_SCHEME = "OaiRepoScheme";

	/**
	 * Parameter for looking up the domain name identifier for the OAI repository from the configuration file
	 */
	public static final String CONFIG_DOMAIN_NAME_IDENTIFIER = "DomainNameIdentifier";

	/**
	 * Parameter for looking up the maximum number of identifiers returned at a time by the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_MAX_IDENTIFIERS = "OaiRepoMaxIdentifiers";

	/**
	 * Parameter for looking up the maximum number of records returned at a time by the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_MAX_RECORDS = "OaiRepoMaxRecords";

	/**
	 * Parameter for looking up the maximum length in bytes of identifiers returned at a time by the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_MAX_IDENTIFIERS_LENGTH = "OaiRepoMaxIdentifiersLength";

	/**
	 * Parameter for looking up the maximum length in bytes of records returned at a time by the OAI repository from the configuration file
	 */
	public static final String CONFIG_OAI_REPO_MAX_RECORDS_LENGTH = "OaiRepoMaxRecordsLength";

	
	/**
	 * Parameter for looking up the threading model used for indexing SOLR
	 */
	public static final String CONFIG_SOLR_INDEXER = "SOLRIndexer";
	
	/**
	 * Parameter for looking up the threading model used for indexing SOLR
	 */
	public static final String CONFIG_SOLR_INDEXER_MULTI_THREAD_COUNT = "SOLRIndexerMultiThreadCount";
	
	/**
	 * Parameter constant value for Single Threaded indexing in SOLR
	 */
	public static final String SOLR_INDEXER_SINGLE_THREADED = "SingleThreaded";

	/**
	 * Parameter constant value for Single Threaded indexing in SOLR
	 */
	public static final String SOLR_INDEXER_MULTI_THREADED = "MultiThreaded";
	
	/**
	 * Parameter constant value for Instances folder name
	 */
	public static final String INSTANCES_FOLDER_NAME = "InstanceFolderName";
	
	public static final String INSTANCES_DIR = "InstanceDir";
	
	
	//*******************************************************************
	// Error Messages
	//*******************************************************************

	/**
	 * OAI error signifying bad arguments in the request
	 */
	public static final String ERROR_BAD_ARGUMENT = "The request includes illegal arguments or is missing required arguments.";

	/**
	 * OAI error signifying illegal arguments in the request
	 */
	public static final String ERROR_BAD_ARGUMENT_IDENTIFY = "The request includes illegal arguments.";

	/**
	 * OAI error signifying illegal arguments in the request
	 */
	public static final String ERROR_BAD_SET = "The request includes an invalid set.";

	/**
	 * OAI error signifying a bad verb in the request
	 */
	public static final String ERROR_BAD_VERB = "Illegal OAI verb.";

	/**
	 * OAI error signifying an invalid resumption token
	 */
	public static final String ERROR_BAD_RESUMPTION_TOKEN = "The value of the resumptionToken argument is invalid or expired.";

	/**
	 * OAI error signifying the metadata format is not supported
	 */
	public static final String ERROR_CANNOT_DISSEMINATE_FORMAT = "The value of the metadataPrefix argument is not supported by the item identified by the value of the identifier argument.";

	/**
	 * OAI error signifying the identifier specified in a request was unknown
	 */
	public static final String ERROR_ID_DOES_NOT_EXIST = "The value of the identifier argument is unknown or illegal in this repository.";

	/**
	 * OAI error signifying the specified item had no metadata formats
	 */
	public static final String ERROR_NO_METADATA_FORMATS = "There are no metadata formats available for the specified item.";

	/**
	 * OAI error signifying that no record could be found matching the request's credentials
	 */
	public static final String ERROR_NO_RECORDS_MATCH = "The combination of the values of the from, until, set, and metadataPrefix arguments results in an empty list.";

	/**
	 * OAI error signifying that the repository does not support sets
	 */
	public static final String ERROR_NO_SET_HIERARCHY = "The repository does not support sets.";

	//*******************************************************************
	// OAI Response format
	//*******************************************************************

	/**
	 * The header and opening tag for the OAI response element
	 */
	public static final String OAI_RESPONSE_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                                     "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"\n" +
                                                     "                 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                                                     "                 xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/\n" +
                                                     "                                      http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\n";

	/**
	 * The closing tag for the OAI response element
	 */
	public static final String OAI_RESPONSE_FOOTER = "</OAI-PMH>";
	
	
	//*******************************************************************
	// MST Service Status Constants
	//*******************************************************************

	public static final String STATUS_SERVICE_RUNNING = "RUNNING";
	
	public static final String STATUS_SERVICE_NOT_RUNNING = "NOT_RUNNING";
	
	public static final String STATUS_SERVICE_PAUSED = "PAUSED";
	
	public static final String STATUS_SERVICE_ERROR = "ERROR";
	
	public static final String STATUS_SERVICE_CANCELED = "CANCELED";

	
	//*******************************************************************
	// XC Element Name Constants
	//*******************************************************************
	
	public static final String ELEMENT_TITLE_OF_WORK = "titleOfWork";

	public static final String ELEMENT_CONTRIBUTOR = "contributor";

	public static final String ELEMENT_TITLE_OF_EXPRESSION = "titleOfExpression";

	public static final String ELEMENT_IDENTIFIER_FOR_THE_WORK = "identifierForTheWork";
	
	public static final String ELEMENT_CREATOR = "creator";
	
	public static final String ELEMENT_SUBJECT = "subject";
	
	public static final String ELEMENT_AUTHOR = "author";
	
	//*******************************************************************
	// MST Thread type  Constants
	//*******************************************************************

	public static final String THREAD_SERVICE = "SERVICE";
	
	public static final String THREAD_REPOSITORY = "REPOSITORY";
	
	public static final String THREAD_PROCESSING_DIRECTIVE = "PD";
	
	public static final String THREAD_SERVICE_REPROCESS = "REPROCESS";
	
	public static final String THREAD_DELETE_SERVICE = "DELETE_SERVICE";

	
	//*******************************************************************
	// Groups and Permissions Constants
	//*******************************************************************

	public static final String ADMINSTRATOR_GROUP = "Administrator";
	
}
