/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.oai;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.ResumptionToken;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseManager;
import xc.mst.manager.IndexException;
import xc.mst.services.MetadataService;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.XMLUtil;
import xc.mst.utils.XmlHelper;

/**
 * A facade class to create the response to the different OAI verb requests
 * 
 * @author Peter Kiraly -- Initial code
 * @author Eric Osisek -- Updated for Metadata Services Toolkit
 * @author Sharmila Ranganathan -- updated according to new MST architecture
 */
public class Facade extends BaseManager {
    /** The logger object */
    private static Logger LOG = Logger.getLogger(Facade.class);
    private static Logger log = Logger.getLogger(Constants.LOGGER_HARVEST_OUT);

    protected static final DateTimeFormatter UTC_PARSER = ISODateTimeFormat.dateTimeParser();
    protected static TransformerFactory transformerFactory = TransformerFactory.newInstance();

    protected Transformer transformer = null;
    protected XmlHelper xmlHelper = new XmlHelper();

    /** The number of warnings in executing the current request */
    private int warningCount = 0;

    /** The number of errors in executing the current request */
    private int errorCount = 0;

    /** The service being harvested */
    private Service service = null;

    /**
     * Returns response date element
     * 
     * @return response date element
     */
    public String getResponseDate() {
        // Add the element with the timestamp of the response
        try {
            Element responseDate = new Element("responseDate");
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            String formattedDate = fmt.print(new DateTime());
            responseDate.addContent(formattedDate);

            if (log.isDebugEnabled())
                log.debug("Setting the responseDate in the OAI response header to " + formattedDate);

            return XMLUtil.format.outputString(responseDate);
        } catch (Exception e) {
            log.error("An exception occurred setting the response date.", e);
            return null;
        }
    }

    /**
     * Returns the common header for all responses
     * 
     * @param url
     *            The requested URL
     * @param oaiRequest
     *            OAI request
     */
    public String getRequestElement(String url, OaiRequestBean oaiRequest) {
        if (log.isDebugEnabled())
            log.debug("Creating the response header for the OAI repsonse.");

        // Add the request element, which echoes the data from the request we're responding to
        // Each field which is not null is added as an attribute on the request element
        try {
            Element requestEl = new Element("request");
            requestEl.addContent(url.toString());
            if (oaiRequest.getVerb() != null) {
                if (log.isDebugEnabled())
                    log.debug("Setting the verb in the OAI response header to " + oaiRequest.getVerb());

                requestEl.setAttribute("verb", oaiRequest.getVerb());
            }
            if (oaiRequest.getFrom() != null) {
                if (log.isDebugEnabled())
                    log.debug("Setting the from field in the OAI response header to " + oaiRequest.getFrom());

                requestEl.setAttribute("from", oaiRequest.getFrom());
            }
            if (oaiRequest.getUntil() != null) {
                if (log.isDebugEnabled())
                    log.debug("Setting the until field in the OAI response header to " + oaiRequest.getUntil());

                requestEl.setAttribute("until", oaiRequest.getUntil());
            }
            if (oaiRequest.getMetadataPrefix() != null) {
                if (log.isDebugEnabled())
                    log.debug("Setting the metadataPrefix in the OAI response header to " + oaiRequest.getMetadataPrefix());

                requestEl.setAttribute("metadataPrefix", oaiRequest.getMetadataPrefix());
            }
            if (oaiRequest.getSet() != null) {
                if (log.isDebugEnabled())
                    log.debug("Setting the set in the OAI response header to " + oaiRequest.getSet());

                requestEl.setAttribute("set", oaiRequest.getSet());
            }
            if (oaiRequest.getIdentifier() != null) {
                if (log.isDebugEnabled())
                    log.debug("Setting the identifier in the OAI response header to " + oaiRequest.getIdentifier());

                requestEl.setAttribute("identifier", oaiRequest.getIdentifier());
            }
            if (oaiRequest.getResumptionToken() != null) {
                if (log.isDebugEnabled())
                    log.debug("Setting the resumptionToken in the OAI response header to " + oaiRequest.getResumptionToken());

                requestEl.setAttribute("resumptionToken", oaiRequest.getResumptionToken());
            }

            // Set the request header to the form's URL field
            return XMLUtil.format.outputString(requestEl);
        } catch (Exception e) {
            log.error("An exception occurred setting up the response's request element.", e);
            return null;
        }

    }

    /**
     * Executes the correct OAI function based on the verb
     * 
     * @param oaiRequest
     *            OAI request
     * @throws DatabaseConfigException
     *             Thrown when there is problem connecting to database
     */
    public String execute(OaiRequestBean oaiRequest) throws DatabaseConfigException {

        if (log.isDebugEnabled())
            log.debug("Executing request for verb " + oaiRequest.getVerb() + ".");

        // Get the service
        service = getServicesService().getServiceById(oaiRequest.getServiceId());
        String prefix = oaiRequest.getMetadataPrefix();
        MetadataService ms = service.getMetadataService();
        String xslFileName = ms.getConfig().getProperty("output.format." + prefix + ".xsl");
        if (xslFileName != null) {
            xslFileName = MSTConfiguration.getInstance().getServicePath() + service.getName() + "/xsl/" + xslFileName;
            try {
                transformer = transformerFactory.newTransformer(new StreamSource(new FileInputStream(xslFileName)));
            } catch (Throwable t) {
                log.error("", t);
            }
        }

        // If the verb was null, return a bad verb error
        // Otherwise execute the correct funtionality, and
        // return a bad verb error only when the verb is not
        // recognized
        try {

            String oaiVerbOutput = null;
            if (oaiRequest.getVerb() == null) {
                LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI request did not contain a verb.");
                warningCount++;
                oaiVerbOutput = ErrorBuilder.badVerbError();
            } else if (oaiRequest.getVerb().equalsIgnoreCase("Identify")) {
                oaiVerbOutput = doIdentify(oaiRequest);
            } else if (oaiRequest.getVerb().equalsIgnoreCase("ListSets")) {
                oaiVerbOutput = doListSets(oaiRequest);
            } else if (oaiRequest.getVerb().equalsIgnoreCase("ListMetadataFormats")) {
                oaiVerbOutput = doListMetadataFormats(oaiRequest);
            } else if (oaiRequest.getVerb().equalsIgnoreCase("ListIdentifiers")) {
                oaiVerbOutput = doListIdentifiers(oaiRequest);
            } else if (oaiRequest.getVerb().equalsIgnoreCase("ListRecords")) {
                oaiVerbOutput = doListRecords(oaiRequest);
            } else if (oaiRequest.getVerb().equalsIgnoreCase("GetRecord")) {
                oaiVerbOutput = doGetRecord(oaiRequest);
            } else {
                LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI request did not contain a valid verb: " + oaiRequest.getVerb());
                warningCount++;
                oaiVerbOutput = ErrorBuilder.badVerbError(oaiRequest.getVerb());
            }

            if (oaiVerbOutput != null) {
                // Build the OAI response
                StringBuilder oaiResponseElement = new StringBuilder();

                // Append the header
                oaiResponseElement.append(Constants.OAI_RESPONSE_HEADER);

                // Append the response date element
                oaiResponseElement.append(getResponseDate()).append("\n");

                if (oaiRequest.getRequest() != null) {
                    // Append the request element
                    oaiResponseElement.append(getRequestElement(oaiRequest.getRequest(), oaiRequest)).append("\n");
                }

                // Append the response itself
                oaiResponseElement.append(oaiVerbOutput).append("\n");

                // Append the footer
                oaiResponseElement.append(Constants.OAI_RESPONSE_FOOTER);

                String response = oaiResponseElement.toString();
                if (transformer != null) {
                    Document oaiDoc = xmlHelper.getJDomDocument(response);
                    Element oaiEl = oaiDoc.getRootElement();
                    Element listRecordsEl = oaiEl.getChild("ListRecords", oaiEl.getNamespace());
                    if (listRecordsEl != null) {
                        for (Object recordObj : listRecordsEl.getChildren("record", oaiEl.getNamespace())) {
                            Element recordEl = (Element) recordObj;
                            Element metadataEl = recordEl.getChild("metadata", oaiEl.getNamespace());
                            if (metadataEl != null) {
                                Element metadataContentEl = (Element) metadataEl.getChildren().get(0);
                                metadataEl.removeContent(metadataContentEl);
                                metadataEl.addContent(transformRecord(metadataContentEl));
                            }
                        }
                        response = xmlHelper.getString(oaiEl);
                    } else {
                    }
                }

                return response;
            } else {
                LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI request contained an invalid verb: " + oaiRequest.getVerb() + ".");
                warningCount++;

                return ErrorBuilder.badVerbError();
            }
        } catch (Exception e) {
            log.error("An exception occurred while executing the request.", e);

            LogWriter.addError(service.getHarvestOutLogFileName(), "An unexpected error occurred while executing the " + oaiRequest.getVerb() + " request.");
            errorCount++;
            return "";
        } finally // Update the error and warning count for the service
        {
            // Load the provider again in case it was updated during the harvest
            Service service = getServiceDAO().getById(this.service.getId());

            // Increase the warning and error counts as appropriate, then update the provider
            service.setHarvestOutWarnings(service.getHarvestOutWarnings() + warningCount);
            service.setHarvestOutErrors(service.getHarvestOutErrors() + errorCount);

            // Increase number of harvests if this is the initial request for harvest
            if (oaiRequest.getVerb() != null && (oaiRequest.getVerb().equalsIgnoreCase("ListRecords")) &&
                    (oaiRequest.getResumptionToken() == null || oaiRequest.getResumptionToken().trim().length() == 0) &&
                    (oaiRequest.getMetadataPrefix() != null && oaiRequest.getMetadataPrefix().trim().length() != 0)) {
                service.setNumberOfHarvests(service.getNumberOfHarvests() + 1);
            }

            try {
                getServiceDAO().update(service);
            } catch (DataException e) {
                log.warn("Unable to update the provider's warning and error counts due to a Data Exception.", e);
            }
        }
    }

    /**
     * Create response to the Identify verb. The parameters arrive in the
     * form (which is a FormBean). The XML response will be set to the value of
     * the form bean's xml field.
     * 
     * @throws DatabaseConfigException
     */
    public String doIdentify(OaiRequestBean oaiRequest) throws DatabaseConfigException {
        if (log.isDebugEnabled())
            log.debug("Entering doIdentify");

        // Build the Identify element which will contain information on the OAI repository.
        // Most of the information is pulled from the configuration file, but the earliestDatestamp
        // is read from the database as the lowest value for the OAI_datestamp column in the results table
        Element root = new Element("Identify");
        root.addContent(XMLUtil.xmlEl("repositoryName", MSTConfiguration.getInstance().getProperty(Constants.CONFIG_OAI_REPO_NAME)));
        root.addContent(XMLUtil.xmlEl("baseURL", oaiRequest.getOaiRepoBaseURL()));
        root.addContent(XMLUtil.xmlEl("protocolVersion", MSTConfiguration.getInstance().getProperty(Constants.CONFIG_OAI_REPO_PROTOCOL_VERSION)));
        root.addContent(XMLUtil.xmlEl("adminEmail", MSTConfiguration.getInstance().getProperty(Constants.CONFIG_OAI_REPO_ADMIN_EMAIL)));

        // Get the earliest record. If it's not null, set the earliestDatestamp to it's datestamp.
        // Otherwise, there were no records, and we will set it to the beginning of the epoch
        Record earliest = recordService.getEarliest(oaiRequest.getServiceId());
        root.addContent(XMLUtil.xmlEl("earliestDatestamp", (earliest != null ? new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format(earliest.getOaiDatestamp()) : "1970-01-01T12:00:00Z")));

        root.addContent(XMLUtil.xmlEl("deletedRecord", MSTConfiguration.getInstance().getProperty(Constants.CONFIG_OAI_REPO_DELETED_RECORD)));
        root.addContent(XMLUtil.xmlEl("granularity", MSTConfiguration.getInstance().getProperty(Constants.CONFIG_OAI_REPO_GRANULARITY)));

        String[] compressions = MSTConfiguration.getInstance().getProperty(Constants.CONFIG_OAI_REPO_COMPRESSION).split(";");
        for (String compression : compressions)
            root.addContent(XMLUtil.xmlEl("compression", compression));

        // Create the description's oai-identifier element
        Namespace mstNS = Namespace.getNamespace("mst", "http://www.extensiblecatalog.org/xsd/mst/1.0");
        Element oaiIdentifier = new Element("oai-identifier", mstNS);

        // Add child elements to the oaiIdentifier element with useful information
        oaiIdentifier.addContent(XMLUtil.xmlEl("scheme", MSTConfiguration.getInstance().getProperty(Constants.CONFIG_OAI_REPO_SCHEME), mstNS));
        oaiIdentifier.addContent(XMLUtil.xmlEl("repositoryIdentifier", MSTConfiguration.getInstance().getProperty(Constants.CONFIG_DOMAIN_NAME_IDENTIFIER), mstNS));
        oaiIdentifier.addContent(XMLUtil.xmlEl("delimiter", MSTConfiguration.getInstance().getProperty(Constants.CONFIG_OAI_REPO_DELIMITER), mstNS));
        oaiIdentifier.addContent(XMLUtil.xmlEl("sampleIdentifier",
                "oai:" + MSTConfiguration.getInstance().getProperty(Constants.CONFIG_DOMAIN_NAME_IDENTIFIER) + ":" +
                        MSTConfiguration.getInstanceName() + "/" + service.getName().replace(" ", "_") + "/1", mstNS));

        // Add a description element with the oai-identifier element we just created
        root.addContent(XMLUtil.xmlEl("description", null).addContent(oaiIdentifier));

        if (log.isDebugEnabled())
            log.debug("Setting the doIdentifiy response based on values in the configuration file.");

        return XMLUtil.format.outputString(root).replaceAll(" xmlns=\"\"", "");
    }

    /**
     * Create an XML response to the ListMetadataFormat verb.
     * 
     * @throws DatabaseConfigException
     */
    public String doListMetadataFormats(OaiRequestBean oaiRequest) throws DatabaseConfigException, IndexException {
        // Create the ListMetadataFormats element for the OAI response
        Element listMetadataFormats = new Element("ListMetadataFormats");

        // If the identifier was not null, it contains the record ID of the specific record whose format
        // should be returned. In this case the response will contain just the format of that record
        // (since we won't be able to convert it to different formats.) Otherwise it will contain all formats
        // supported by the service.
        if (oaiRequest.getIdentifier() != null && oaiRequest.getIdentifier().length() > 0) {
            // Get the record
            Record record = service.getMetadataService().getRepository().getRecord(oaiRequest.getIdentifier());

            // If the record didn't exist, the XML should be an error explaining this.
            if (record == null) {
                log.warn("The record with OAI identifier " + oaiRequest.getIdentifier() + " could not be found.");

                return XMLUtil.xmlTag("error", Constants.ERROR_ID_DOES_NOT_EXIST, new String[] { "code", "idDoesNotExist" });
            }

            // Get the record's format.
            Format format = record.getFormat();

            // Add the format to the response if it wasn't null. If it was null, the requested record did not exist
            if (format != null) {
                if (log.isDebugEnabled())
                    log.debug("Adding the format " + format.getName() + " as the format for the requested record with identifier " + oaiRequest.getIdentifier() + ".");

                for (Format f : getAllFormats(format)) {
                    // Add the metadata prefix, schema, and namespace information to the ListMetadataFormats element
                    listMetadataFormats.addContent(XMLUtil.xmlEl("metadataFormat", null).addContent(XMLUtil.xmlEl("metadataPrefix", f.getName()))
                                                                                        .addContent(XMLUtil.xmlEl("schema", f.getSchemaLocation()))
                                                                                        .addContent(XMLUtil.xmlEl("metadataNamespace", f.getNamespace())));
                }
            } else // The record didn't exist so the format could not be found. Log the problem and return an error
            {
                if (log.isDebugEnabled())
                    log.debug("The format for the requested record with identifier " + oaiRequest.getIdentifier() + " did not exist.  This suggests the record itself did not exist");

                LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI ListMetadataFormats request did not contain a metadataFormat.");
                warningCount++;

                return ErrorBuilder.idDoesNotExistError();

            }
        } else // The OAI Identifier was not specified, so just return all formats supported by the OAI repository
        {
            // Iterate over the formats and add each to the ListMetadataFormats element
            for (Format format : service.getOutputFormats()) {
                if (log.isDebugEnabled())
                    log.debug("Adding the format " + format.getName() + " to the list of returned formats.");

                LogWriter.addInfo(service.getHarvestOutLogFileName(), "Adding the format " + format.getName() + " to the list of returned formats.");

                listMetadataFormats.addContent(XMLUtil.xmlEl("metadataFormat", null).addContent(XMLUtil.xmlEl("metadataPrefix", format.getName()))
                                                                                    .addContent(XMLUtil.xmlEl("schema", format.getSchemaLocation()))
                                                                                    .addContent(XMLUtil.xmlEl("metadataNamespace", format.getNamespace())));
            }
        }

        // Set the ListMetadataFormat XML as the OAI response on the form
        return XMLUtil.format.outputString(listMetadataFormats);
    }

    /**
     * Create response to the ListSets verb. List the sets in XML format.
     * The parameters arrive in the form parameter (which is a FormBean). The XML
     * response will be the value of the form xml field.
     * 
     * @throws DatabaseConfigException
     */
    public String doListSets(OaiRequestBean oaiRequest) throws DatabaseConfigException {
        // The ListSets element for the OAI response
        Element listSets = new Element("ListSets");

        // If service sets are requested, then list only the sets for that service
        if (service != null) {
            if (service.getOutputSets() == null || service.getOutputSets().size() <= 0) {
                if (log.isDebugEnabled())
                    log.debug("There are no sets in the repository.");

                LogWriter.addInfo(service.getHarvestOutLogFileName(), "There are no sets in the OAI Repository, so sending a noSetHierarchy error in the ListSets response.");

                return ErrorBuilder.noSetHierarchyError();
            }

            for (Set set : service.getOutputSets()) {

                try {
                    listSets.addContent(XMLUtil.xmlEl("set", null).addContent(XMLUtil.xmlEl("setSpec", set.getSetSpec()))
                                            .addContent(XMLUtil.xmlEl("setName", set.getDisplayName())));
                } catch (Exception e) {
                    LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI ListIdentifiers request failed returning set spec or display name.");
                    warningCount++;
                }

            }
        }

        // Set the result to the form
        return XMLUtil.format.outputString(listSets);
    }

    /**
     * Create the response to the ListIdentifiers verb.
     * 
     * @throws DatabaseConfigException
     */
    public String doListIdentifiers(OaiRequestBean oaiRequest) throws DatabaseConfigException, IndexException {
        if (log.isDebugEnabled())
            log.debug("Entering doListIdentifiers");

        // Return an error if the metadataPrefix was null or empty
        if (oaiRequest.getMetadataPrefix() == null || oaiRequest.getMetadataPrefix().trim().length() == 0) {
            log.warn("The OAI request did not contain an metadataPrefix element.");

            LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI ListIdentifiers request did not contain a metadataFormat.");
            warningCount++;

            return ErrorBuilder.badArgumentError("Missing metadataPrefix parameter");

        }

        // Get the XML for the identifiers
        // The last parameter is true to query for just the identifiers and not the full records
        return handleRecordLists(oaiRequest.getFrom(), oaiRequest.getUntil(),
                getPersistedMetadataPrefix(oaiRequest.getMetadataPrefix()),
                oaiRequest.getSet(), oaiRequest.getResumptionToken(), false);

    }

    public String getPersistedMetadataPrefix(String mp) {
        MetadataService ms = service.getMetadataService();
        String origFormat = ms.getConfig().getProperty("output.format." + mp + ".orig-format");
        if (origFormat != null) {
            return origFormat;
        } else {
            return mp;
        }
    }

    public List<Format> getAllFormats(Format f) {
        List<Format> allFormats = new ArrayList<Format>();
        try {
            allFormats.add(f);
            MetadataService ms = service.getMetadataService();
            for (String fPref : ms.getConfig().getPropertyAsList("output.format.name")) {
                if (!fPref.equals(f.getName())) {
                    String origFormat = ms.getConfig().getProperty(
                            "output.format." + fPref + ".orig-format");
                    if (f.getName().equals(origFormat)) {
                        allFormats.add(getFormatDAO().getByName(fPref));
                    }
                }
            }
        } catch (Throwable t) {
            log.error("", t);
        }
        return allFormats;
    }

    /**
     * Create the response to the ListRecords verb.
     * 
     * @throws DatabaseConfigException
     */
    public String doListRecords(OaiRequestBean oaiRequest) throws DatabaseConfigException, IndexException {
        if (log.isDebugEnabled())
            log.debug("Entering doListRecords");

        // Return an error if the metadataPrefix was null or empty
        if ((oaiRequest.getResumptionToken() == null || oaiRequest.getResumptionToken().trim().length() == 0) && (oaiRequest.getMetadataPrefix() == null || oaiRequest.getMetadataPrefix().trim().length() == 0)) {
            log.warn("The OAI request did not contain an metadataPrefix element.");

            LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI ListRecords request did not contain a metadataFormat.");
            warningCount++;

            return ErrorBuilder.badArgumentError("Missing metadataPrefix parameter");

        }
        // Get the XML for the full records
        // The last parameter is true to query for the full records and not just the identifiers
        return handleRecordLists(oaiRequest.getFrom(), oaiRequest.getUntil(),
                getPersistedMetadataPrefix(oaiRequest.getMetadataPrefix()),
                oaiRequest.getSet(), oaiRequest.getResumptionToken(), true);

    }

    /**
     * Create response to the GetRecord verb
     * 
     * @throws DatabaseConfigException
     */
    public String doGetRecord(OaiRequestBean oaiRequest) throws DatabaseConfigException, IndexException {
        if (log.isDebugEnabled())
            log.debug("Entering doGetRecord");

        // Return an error if the identifier was null or empty
        if (oaiRequest.getIdentifier() == null || oaiRequest.getIdentifier().trim().length() == 0) {
            log.warn("The OAI request did not contain an identifier element.");

            LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI GetRecord request did not contain an identifier.");
            warningCount++;

            return ErrorBuilder.badArgumentError("Missing identifier parameter");

        }

        // Return an error if the metadataPrefix was null or empty
        if (oaiRequest.getMetadataPrefix() == null || oaiRequest.getMetadataPrefix().trim().length() == 0) {
            log.warn("The OAI request did not contain an metadataPrefix element.");

            LogWriter.addWarning(service.getHarvestOutLogFileName(), "The OAI GetRecord request did not contain a metadataFormat.");
            warningCount++;

            return ErrorBuilder.badArgumentError("Missing metadataPrefix parameter");
        }

        // Get the record
        Record record = service.getMetadataService().getRepository().getRecord(oaiRequest.getIdentifier());

        // If the record didn't exist, the XML should be an error explaining this.
        if (record == null) {
            log.warn("The record with OAI identifier " + oaiRequest.getIdentifier() + " could not be found.");

            LogWriter.addWarning(service.getHarvestOutLogFileName(), "The record with the OAI Identifier " + oaiRequest.getIdentifier() + " could not be found.");
            warningCount++;

            return XMLUtil.xmlTag("error", Constants.ERROR_ID_DOES_NOT_EXIST, new String[] { "code", "idDoesNotExist" });
        } else {
            // If the format didn't exist or didn't match the metadataPrefix,
            // the XML should be an error explaining this.
            // Otherwise it should be the XML for the OAI record
            boolean bogusMetadataFormat = true;
            if (record.getFormat() != null) {
                for (Format f : getAllFormats(record.getFormat())) {
                    if (f.getName().equals(oaiRequest.getMetadataPrefix())) {
                        bogusMetadataFormat = false;
                        break;
                    }
                }
            }

            if (bogusMetadataFormat) {
                log.warn("The record with OAI identifier " + oaiRequest.getIdentifier() + " did not match the metadataPrefix " + oaiRequest.getMetadataPrefix() + ".");

                LogWriter.addWarning(service.getHarvestOutLogFileName(), "The record with the OAI Identifier " + oaiRequest.getIdentifier() + " did not match the metadata format " + oaiRequest.getMetadataPrefix() + ".");
                warningCount++;

                return XMLUtil.xmlTag("error", Constants.ERROR_CANNOT_DISSEMINATE_FORMAT, new String[] { "code", "cannotDisseminateFormat" });
            } else {
                if (log.isDebugEnabled())
                    log.debug("Setting the record with OAI identifier " + oaiRequest.getIdentifier() + " on the XML parameter of the form.");

                LogWriter.addInfo(service.getHarvestOutLogFileName(), "Found the record with the OAI Identifier " + oaiRequest.getIdentifier() + ".");

                try {

	                record.setMode(Record.JDOM_MODE);
	                Element recordContentEl = record.getOaiXmlEl();
	                if (transformer != null) {
	                    recordContentEl = transformRecord(recordContentEl);
	                }
	                StringBuilder stringBuilder = new StringBuilder();
	                stringBuilder.append("<record>")
	                            .append(getHeader(record))
	                            .append("<metadata>")
	                            .append(xmlHelper.getStringRaw(recordContentEl))
	                            .append("</metadata>")
	                            .append("</record>");
	
	                return XMLUtil.xmlTag("GetRecord", stringBuilder.toString());

                }catch (Exception ex) {
                    if (log.isDebugEnabled())
                        log.debug("Unable to retrieve and process record: " + ex);
                    
                    return XMLUtil.xmlTag("error", Constants.ERROR_ID_DOES_NOT_EXIST, new String[] { "code", "idDoesNotExist" });

                }
            }
        }
    }

    /**
     * Returns the OAI XML for a list of records or identifiers.
     * 
     * @param from
     *            The earliest date for returned records or identifiers. If null or empty the earliest date out of all records will be used
     * @param until
     *            The latest date for returned records or identifiers. If null or empty the current date will be used
     * @param metadataPrefix
     *            The type of metadata which should be returned
     * @param set
     *            The set which is being queried
     * @param resumptionTokenId
     *            The resumption token's ID
     * @param getRecords
     *            true if we should return the full records, false if we should only return the headers
     * @return The XML containing a list of headers or record and header combinations as well as a resumption token.
     *         These should be included in the response's ListRecords or ListIdentifiers element.
     * @throws DatabaseConfigException
     */
    private String handleRecordLists(String from, String until, String metadataPrefix, String set, String resumptionToken, boolean getRecords)
            throws DatabaseConfigException, IndexException {
        if (log.isDebugEnabled())
            log.debug("Entering handleRecordLists");

        if (from != null && until != null && from.length() != until.length()) {
            return ErrorBuilder.badArgumentError("From and until have different levels of granularity.");
        }

        // The from and until dates. They will be null if the passed Strings could not be parsed
        Date fromDate = null;
        if (!StringUtils.isEmpty(from)) {
            try {
                fromDate = new Date(UTC_PARSER.parseDateTime(from).getMillis());
            } catch (IllegalArgumentException iae) {
                return ErrorBuilder.badArgumentError("from: " + from);
            }
        } else {
            fromDate = new Date(0);
        }
        Date untilDate = null;
        if (!StringUtils.isEmpty(until)) {
            try {
                untilDate = new Date(UTC_PARSER.parseDateTime(until).getMillis());
            } catch (IllegalArgumentException iae) {
                return ErrorBuilder.badArgumentError("until: " + until);
            }
        } else {
            untilDate = new Date();
        }

        // Starting record id
        long startingId = 0;

        // The Format and Set Objects associated with the OAI request
        Format format;
        Set setObject = null;

        // If there was a resumption token, get it from the database
        // ResumptionToken resToken = null;
        if (resumptionToken != null) {
            if (log.isDebugEnabled())
                log.debug("The request had a resumption token " + resumptionToken);

            // resToken = new ResumptionToken();
            String[] rtSplit = resumptionToken.split("\\|");
            // Set the values from the resumption token rather than keeping the ones parsed from the request
            fromDate = new Date(UTC_PARSER.parseDateTime(rtSplit[0]).getMillis());
            untilDate = new Date(UTC_PARSER.parseDateTime(rtSplit[1]).getMillis());
            set = rtSplit[2];
            metadataPrefix = rtSplit[3];
            startingId = Long.parseLong(rtSplit[4]);

        }

        // Get the Set Object for the requested set
        if (!StringUtils.isEmpty(set)) {
            setObject = getSetDAO().getBySetSpec(set);

            // If the set they asked for didn't exist, return an error
            if (setObject == null) {
                log.warn("The requested set could not be found.  set was " + set + ".");

                LogWriter.addWarning(service.getHarvestOutLogFileName(), "The requested set \"" + set + "\" could not be found.");
                warningCount++;

                return XMLUtil.xmlTag("error", Constants.ERROR_BAD_SET, new String[] { "code", "badArgument" });
            }
        }

        format = getFormatDAO().getByName(metadataPrefix);
        // Return an error if the format was null
        if (format == null) {
            log.warn("The requested metadataPrefix could not be found.  metadataPrefix was " + metadataPrefix + ".");

            LogWriter.addWarning(service.getHarvestOutLogFileName(), "The requested metadataPrefix \"" + metadataPrefix + "\" could not be found.");
            warningCount++;

            return XMLUtil.xmlTag("error", Constants.ERROR_NO_RECORDS_MATCH, new String[] { "code", "noRecordsMatch" });
        }

        List<Record> records = new ArrayList<Record>();

        // Total number of records satisfying the criteria. This is not the number of records loaded.
        // long totalRecords = service.getMetadataService().getRepository().getRecordCount(fromDate, untilDate, format, setObject);

        // BDA TODO: first do a count
        long totalCount = service.getMetadataService().getRepository().getRecordCount(fromDate, untilDate, format, setObject);
        // long totalCount = -1;
        log.debug("totalCount: " + totalCount);

        if (totalCount != 0) {
            records = service.getMetadataService().getRepository().getRecords(fromDate, untilDate, startingId, format, setObject);
        }

        // The XML for the OAI result
        StringBuffer xml = new StringBuffer();

        // If there were no records returned, set an error signifying that no records matched.
        // Otherwise, append data for each returned record to the result and insert a resumption token
        // to the database if needed
        if (records == null || records.size() == 0) {
            LogWriter.addInfo(service.getHarvestOutLogFileName(), "There were no records which matched the parameters provided in the " + (getRecords ? " ListRecords " : " ListIdentifiers") + " request.");
            return xml.append(XMLUtil.xmlTag("error", Constants.ERROR_NO_RECORDS_MATCH, new String[] { "code", "noRecordsMatch" })).toString();
        } else {
            // True if there are more results remaining than we can return at once
            boolean hasMore = records.size() == MSTConfiguration.getInstance().getPropertyAsInt(Constants.CONFIG_OAI_REPO_MAX_RECORDS, 5000);

            /*
            if(log.isDebugEnabled())
                log.debug("Returning results " + offset + " - " + offset + recordLimit + " " + (set == null ? "" : "of set " + set + " ") + " and format " + format.getId() + (from == null ? "" : "from " + from + " ") + (until == null ? "" : "until " + until) + ".");
                */

            // The number of records returned
            int returnedRecordsCount = 0;

            // Add whitespace to make the result more readable
            xml.append("\n");

            // Append XML for each record to the result
            for (Record record : records) {
                // If we're to get the records, append the record's OAI XML.
                // Otherwise, we're just supposed to get the identifiers, so
                // append the record's OAI header
                if (getRecords) {

                    // For deleted record, just append the header
                    if (Record.DELETED == record.getStatus()) {
                        String header = getHeader(record);
                        header = header.replaceAll("<header>", "<header status=\"deleted\">");
                        xml.append("<record>\n")
                                .append(header)
                                .append("\n</record>\n");
                    } else if (Record.ACTIVE == record.getStatus()) {
                        xml.append("<record>\n");

                        xml.append(getHeader(record));
                        if (getRecords && !record.getDeleted()) {
                            if (record.getOaiXml() == null) {
                                log.error("record has no content!!!!");
                                log.error("record.getStatus(): " + record.getStatus());
                                log.error("record.getId(): " + record.getId());
                            } else {
                                xml.append("\n<metadata>\n")
                                        .append(record.getOaiXml().replaceAll("<\\?xml.*\\?>", ""))
                                        .append("\n</metadata>\n");
                            }
                        }
                        xml.append("\n</record>\n");
                    }
                } else {
                    xml.append(getHeader(record).replaceAll("<\\?xml.*\\?>", "")).append("\n");
                }

                startingId = record.getId();

                // Increment the counter for the number of records returned
                returnedRecordsCount++;

                /* BDA - Is this feature really worthwhile?
                // If the length of the results exceeds the maximum allowed length,
                // break from the loop
                if(xml.length() >= maxLength)
                {
                    if(log.isDebugEnabled())
                        log.debug("Breaking from the loop because after adding " + returnedRecordsCount + " records, the length of the results is " + xml.length() + " which exceeds the maximum allowed length of " + maxLength);

                    hasMore = true;
                    break;
                }
                */
            }

            if (hasMore) {
                ResumptionToken newResToken = new ResumptionToken();
                // Set the fields on the resumption token
                newResToken.setFrom(fromDate);
                newResToken.setUntil(untilDate);
                newResToken.setSetSpec(set);
                newResToken.setMetadataFormat(metadataPrefix);
                newResToken.setStartingId(startingId);

                if (totalCount < -1) {
                    xml.append("<!-- completeListSize is an estimate -->");
                    totalCount = -1 * totalCount;
                }
                if (totalCount > 0) {
                    xml.append(XMLUtil.xmlTag("resumptionToken", "" + newResToken.getToken(), new String[] { "completeListSize", "" + totalCount }));
                } else {
                    xml.append(XMLUtil.xmlTag("resumptionToken", "" + newResToken.getToken()
                            ));
                    // ,new String[] { "cursor", "" + offset, "completeListSize", ""+totalRecords } ));

                }

                // LogWriter.addInfo(service.getHarvestOutLogFileName(), "Returning " + totalRecords + " records and the resumptionToken " + newResToken.getId() + " in response to the " + (getRecords ? " ListRecords " : " ListIdentifiers") + " request.");
            }
        }
        return XMLUtil.xmlTag((getRecords ? "ListRecords" : "ListIdentifiers"), xml.toString());
    }

    /*
     * Given a String containing a date in the yyyy-MM-dd'T'HH:mm:ssZ format, returns
     * a Date Object parsed from that String.  If the String could not be parsed, returns
     * null
     *
     * @param dateString A String with a date in the yyyy-MM-dd'T'HH:mm:ssZ format
     * @return A Date Object which was parsed from dateString, or null if it could not be parsed
     */
    private Date parseDate(String dateString) {
        if (log.isDebugEnabled())
            log.debug("Parsing a date from the String " + dateString);

        // If the passed string was null, return null
        if (dateString == null || dateString.length() <= 0)
            return null;

        // Try to parse a date from the String. If the parse fails, return null
        try {
            dateString = dateString.replace('T', ' ');
            dateString = dateString.replaceFirst("Z", "");
            dateString = dateString.replaceFirst("z", "");

            // Parse assuming granularity is to the nearest second
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
            try {
                // Granularity wasn't to the nearest second, try to the nearest day instead
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (ParseException e1) {
                log.warn("Could not parse a date from the String " + dateString, e1);

                return null;
            }
        }
    }

    /*
     * Builds the OAI header
     */
    private String getHeader(Record record) {

        StringBuilder header = new StringBuilder();
        header.append("<header>\n");

        // Inject service to get OAI identifier
        record.setService(service);

        header.append("\t<identifier>").append(record.getOaiIdentifier()).append("</identifier>\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));

        header.append("\t<datestamp>").append(sdf.format(record.getUpdatedAt())).append("</datestamp>\n");

        // Get each set from the list of set IDs this record belongs to. If the set is
        // not null, add its setSpec to the header.
        SortedSet<String> setSpecs = new TreeSet<String>();

        for (Set s : record.getSets())
            if (s != null)
                setSpecs.add(s.getSetSpec());

        for (String setSpec : setSpecs) {
            header.append("\t<setSpec>").append(setSpec).append("</setSpec>\n");
        }

        header.append("</header>");

        return header.toString();

    }

    public Element transformRecord(Element orig) {
        try {
            String metadataContentStr = xmlHelper.getString(orig);
            StringWriter sw = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(metadataContentStr)), new StreamResult(sw));
            metadataContentStr = sw.getBuffer().toString();
            return (Element) xmlHelper.getJDomDocument(metadataContentStr).getRootElement().detach();
        } catch (Throwable t) {
            log.error("", t);
            return orig;
        }
    }
}
