/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.constants.NormalizationServiceConstants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.utils.MarcXmlManagerForNormalizationService;
import xc.mst.utils.index.RecordList;

/**
 * A Metadata Service which for each unprocessed marcxml record creates a new
 * record which is a copy of the original record after cleaning up common problems.
 *
 * @author Eric Osisek
 */
public class NormalizationService extends MetadataService
{
	/**
	 * Builds the XML Document based on the record's OAI XML
	 */
	private SAXBuilder builder = new SAXBuilder();

	/**
	 * The namespace for MARCXML
	 */
	private Namespace marcNamespace = Namespace.getNamespace("marc", "http://www.loc.gov/MARC21/slim");

	/**
	 * The Properties file with information on which Normalization steps to run
	 */
	private Properties enabledSteps = null;

	/**
	 * The Properties file with the voyager location name mappings
	 */
    private Properties locationNameProperties = null;

    /**
     * The Properties file with the DCMI type information for the leader 06
     */
    private Properties dcmiType06Properties = null;

    /**
     * The Properties file with the MARC Vocabulary information for the leader 06
     */
    private Properties leader06MarcVocabProperties = null;

    /**
     * The Properties file with the vocab information for the leader 06
     */
    private Properties vocab06Properties = null;

    /**
     * The Properties file with the mode of issuance information in it
     */
    private Properties modeOfIssuanceProperties = null;

    /**
     * The Properties file with the DCMI type information for the 00 offset 07
     */
    private Properties dcmiType0007Properties = null;

    /**
     * The Properties file with the vocab information for the 007 offset 00
     */
    private Properties vocab007Properties = null;

    /**
     * The Properties file with the smd type information for the 007 offset 00
     */
    private Properties smdType007Properties = null;

    /**
     * The Properties file with the language term information
     */
    private Properties languageTermProperties = null;

    /**
     * The Properties file with the audience information for the 008 offset 22
     */
    private Properties audienceFrom008Properties = null;

    /**
	 * The output format (marcxml) for records processed from this service
	 */
	private Format marcxmlFormat = null;
	
	/**
	 * A list of errors to add to the record currently being processed
	 */
	private List<String> errors = new ArrayList<String>();

    /**
	 * Construct a NormalizationService Object
	 */
	public NormalizationService()
	{
		// Initialize the XC format
		try 
		{
			marcxmlFormat = getFormatByName("marcxml");
		} 
		catch (DatabaseConfigException e) 
		{
			log.error("Could not connect to the database with the parameters in the configuration file.", e);
		}
	}

	@Override
	protected List<Record> processRecord(Record record)
	{
		// If the record was deleted, don't process it
		if(record.getDeleted())
			return new ArrayList<Record>();
		
		// Empty the lists of errors because we're beginning to process a new record
		errors.clear();
		
		// The list of records resulting from processing the incoming record
		ArrayList<Record> results = new ArrayList<Record>();
		
		try
		{
			if(log.isInfoEnabled())
				log.info("Normalizing record with ID " + record.getId() + ".");

			// The XML after normalizing the record
			Document marcXml = null;

			// Parse the XML from the record
			try
			{
				if(log.isDebugEnabled())
					log.debug("Parsing the record's XML into a Document Object.");

				marcXml = builder.build(new InputSource(new StringReader(record.getOaiXml())));
			}
			catch(IOException e)
			{
				log.error("An error occurred while parsing the record's XML.", e);

				logWarning("An XML parse error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");
				
				errors.add(service.getId() + "-100: An XML parse error occurred while processing the record: " + e.getMessage());

				return results;
			}
			catch(JDOMException e)
			{
				log.error("An error occurred while parsing the record's XML.\n" + record.getOaiXml(), e);

				logWarning("An XML parse error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ".");
				
				errors.add(service.getId() + "-100: An XML parse error occurred while processing the record: " + e.getMessage());

				return results;
			}

			// Create a MarcXmlManagerForNormalizationService for the record
			MarcXmlManagerForNormalizationService normalizedXml = new MarcXmlManagerForNormalizationService(marcXml, getOrganizationCode());

			// Get the Leader 06.  This will allow us to determine the record's type, and we'll put it in the correct set for that type
			char leader06 = normalizedXml.getLeader().charAt(6);
			
			// Run these steps only if the record is a bibliographic record
			if("acdefgijkmoprt".contains(""+leader06))
			{
				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_REMOVE_OCOLC_003, "0").equals("1"))
					normalizedXml = removeOcolc003(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_DCMI_TYPE_06, "0").equals("1"))
					normalizedXml = dcmiType06(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_LEADER_06_VOCAB, "0").equals("1"))
					normalizedXml = leader06MarcVocab(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_007_VOCAB_06, "0").equals("1"))
					normalizedXml = vocab06(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_MODE_OF_ISSUANCE, "0").equals("1"))
					normalizedXml = modeOfIssuance(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_MOVE_MARC_ORG_CODE, "0").equals("1"))
					normalizedXml = moveMarcOrgCode(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_DCMI_TYPE_00_07, "0").equals("1"))
					normalizedXml = dcmiType0007(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_007_VOCAB, "0").equals("1"))
					normalizedXml = vocab007(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_007_SMD_TYPE, "0").equals("1"))
					normalizedXml = smdType007(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_FICTION_OR_NONFICTION, "0").equals("1"))
					normalizedXml = fictionOrNonfiction(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_008_DATE_RANGE, "0").equals("1"))
					normalizedXml = dateRange(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_LANGUAGE_SPLIT, "0").equals("1"))
					normalizedXml = languageSplit(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_LANGUAGE_TERM, "0").equals("1"))
					normalizedXml = languageTerm(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_008_AUDIENCE, "0").equals("1"))
					normalizedXml = audienceFrom008(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_008_THESIS, "0").equals("1"))
					normalizedXml = thesisFrom008(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_ISBN_CLEANUP, "0").equals("1"))
					normalizedXml = isbnCleanup(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_SUPPLY_MARC_ORG_CODE, "0").equals("1"))
					normalizedXml = supplyMARCOrgCode(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_VOYAGER_FIX_035, "0").equals("1"))
					normalizedXml = voyagerFix035(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_VOYAGER_DEDUP_035, "0").equals("1"))
					normalizedXml = voyagerDedup035(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_ROLE_AUTHOR, "0").equals("1"))
					normalizedXml = roleAuthor(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_ROLE_COMPOSER, "0").equals("1"))
					normalizedXml = roleComposer(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_UNIFORM_TITLE, "0").equals("1"))
					normalizedXml = uniformTitle(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_UR_GENRE, "0").equals("1"))
					normalizedXml = urGenre(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_TOPIC_SPLIT, "0").equals("1"))
					normalizedXml = topicSplit(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_CHRON_SPLIT, "0").equals("1"))
					normalizedXml = chronSplit(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_GEOG_SPLIT, "0").equals("1"))
					normalizedXml = geogSplit(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_GENRE_SPLIT, "0").equals("1"))
					normalizedXml = genreSplit(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_DEDUP_DCMI_TYPE, "0").equals("1"))
					normalizedXml = dedupDcmiType(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_DEDUP_007_VOCAB, "0").equals("1"))
					normalizedXml = dedup007Vocab(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_SEPERATE_NAME, "0").equals("1"))
					normalizedXml = seperateName(normalizedXml);

				if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_DEDUP_9XX, "0").equals("1"))
					normalizedXml = dedup9XX(normalizedXml);
			}

			if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_VOYAGER_LOCATION_NAME, "0").equals("1"))
				normalizedXml = voyagerLocationName(normalizedXml);

			if(enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_ENABLED_III_LOCATION_NAME, "0").equals("1"))
				normalizedXml = IIILocationName(normalizedXml);
			
			if(log.isDebugEnabled())
				log.debug("Adding errors to the record.");
			
			addErrorsToRecord(record, errors);
			
			if(log.isDebugEnabled())
				log.debug("Creating the normalized record.");

			// Get any records which were processed from the record we're processing
			// If there are any (there should be at most 1) we need to update them
			// instead of inserting a new Record
			RecordList existingRecords = getByProcessedFrom(record);

			// If there was already a processed record for the record we just processed, update it
			if(existingRecords.size() > 0)
			{
				if(log.isInfoEnabled())
					log.info("Updating the record which was processed from an older version of the record we just processed.");

				// Get the record which was processed from the record we just processed
				// (there should only be one)
				Record oldNormalizedRecord = existingRecords.get(0);

				// Set the XML to the new normalized XML
				oldNormalizedRecord.setOaiXml((new XMLOutputter()).outputString(normalizedXml.getModifiedMarcXml()));

				// Mark the record as not being deleted
				oldNormalizedRecord.setDeleted(false);
				
				// Add the normalized record after modifications were made to it to
				// the list of modified records.
				results.add(oldNormalizedRecord);

				return results;
			}
			// We need to create a new normalized record since we haven't normalized an older version of the original record
			// Do this only if the record we're processing is not deleted
			else
			{
				if(log.isInfoEnabled())
					log.info("Inserting the record since it was not processed from an older version of the record we just processed.");

				// Create the normalized record
				Record normalizedRecord = Record.copyRecord(record);
				normalizedRecord.setOaiXml((new XMLOutputter()).outputString(normalizedXml.getModifiedMarcXml()));
				normalizedRecord.setFormat(marcxmlFormat);
				normalizedRecord.setOaiIdentifier(getNextOaiId());

				// Set the datestamp, and header to null so they get computed when we insert the normalized record
				normalizedRecord.setOaiDatestamp(null);
				normalizedRecord.setOaiHeader(null);

				// Mark the record as not being deleted
				normalizedRecord.setDeleted(false);
				
				// Insert the normalized record

				// The setSpec and set Description of the "type" set we should add the normalized record to
				String setSpec = null;
				String setDescription = null;
				String setName = null;

				// Setup the setSpec and description based on the leader 06
				if("acdefgijkmoprt".contains(""+leader06))
				{
					setSpec = "MARCXMLbibliographic";
					setName = "MARCXML Bibliographic Records";
					setDescription = "A set of all MARCXML Bibliographic records in the repository.";
				}
				else if(leader06 == 'u' || leader06 == 'v' || leader06 == 'x' || leader06 == 'y')
				{
					setSpec = "MARCXMLholding";
					setName = "MARCXML Holding Records";
					setDescription = "A set of all MARCXML Holding records in the repository.";
				}
				else if(leader06 == 'z')
				{
					setSpec = "MARCXMLauthority";
					setName = "MARCXML Authority Records";
					setDescription = "A set of all MARCXML Authority records in the repository.";
				}

				if(setSpec != null)
				{
					// Get the set for the provider
					Set recordTypeSet = getSet(setSpec);
					
					// Add the set if it doesn't already exist
					if(recordTypeSet == null)
						recordTypeSet = addSet(setSpec, setName, setDescription);
					
					// Add the set to the record
					normalizedRecord.addSet(recordTypeSet);
				}

				// Add the record to the list of records resulting from processing the
				// incoming record
				results.add(normalizedRecord);

				if(log.isInfoEnabled())
					log.info("Created normalized record from unnormalized record with ID " + record.getId());

				return results;
			}
		}
		catch(Exception e)
		{
			log.error("An error occurred while normalizing the record with ID " + record.getId(), e);

			logError("An error occurred while processing the record with OAI Identifier " + record.getOaiIdentifier() + ": " + e.getMessage());
			
			if(log.isDebugEnabled())
				log.debug("Adding errors to the record.");
			
			addErrorsToRecord(record, errors);

			return results;
		}
	}

	@Override
	protected void finishProcessing()
	{
		// This service does not do any final processing
	}

	/**
	 * If the 003's value is "OCoLC", remove it.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService removeOcolc003(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering RemoveOCoLC003 normalization step.");

		// Check if the 003 is "OCoLC"
		String field003 = marcXml.getField003();
		if(field003 != null && field003.equals("OCoLC"))
			marcXml.removeControlField("003");

		return marcXml;
	}

	/**
	 * Creates a DCMI Type field based on the record's Leader 06 value.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService dcmiType06(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering DCMIType06 normalization step.");

		// The character at offset 6 of the leader field
		char leader06 = marcXml.getLeader().charAt(6);

		// Pull the DCMI type mapping from the configuration file based on the leader 06 value.
		String dcmiType = dcmiType06Properties.getProperty(""+leader06, null);

		// If there was no mapping for the provided leader 06, we can't create the field.  In this case return the unmodified MARCXML
		if(dcmiType == null)
		{
			if(log.isDebugEnabled())
				log.debug("Cannot find a DCMI Type mapping for the leader 06 value of " + leader06 + ", returning the unmodified MARCXML.");
		}
		else
		{
			if(log.isDebugEnabled())
				log.debug("Found the DCMI Type " + dcmiType + " for the leader 06 value of " + leader06 + ".");

			// Add a MARCXML field to store the DCMI Type
			marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_DCMI_TYPE, dcmiType);
		}

		String field006 = marcXml.getField006();
		if(field006 != null)
		{
			// The character at offset 6 of the leader field
			char field006_0 = field006.charAt(0);

			// Pull the DCMI type mapping from the configuration file based on the leader 06 value.
			String dcmiType006_0 = dcmiType06Properties.getProperty(""+field006_0, null);

			// If there was no mapping for the provided leader 06, we can't create the field.  In this case return the unmodified MARCXML
			if(dcmiType006_0 == null)
			{
				if(log.isDebugEnabled())
					log.debug("Cannot find a DCMI Type mapping for the 006 offset 0 value of " + dcmiType006_0 + ".");
			}

			else
			{
				if(log.isDebugEnabled())
					log.debug("Found the DCMI Type " + dcmiType006_0 + " for the 006 offset 0 value of " + field006_0 + ".");

				// Add a MARCXML field to store the DCMI Type
				marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_DCMI_TYPE, dcmiType006_0);
			}
		}

		// Return the modified MARCXML record
		return marcXml;
	}

	/**
	 * Creates a vocabulary field based on the record's Leader 06 value.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService leader06MarcVocab(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering Leader06Vocab normalization step.");

		// The character at offset 6 of the leader field
		char leader06 = marcXml.getLeader().charAt(6);

		// Pull the SMD Vocab mapping from the configuration file based on the leader 06 value.
		String marcVocab = leader06MarcVocabProperties.getProperty(""+leader06, null);

		// If there was no mapping for the provided leader 06, we can't create the field.  In this case return the unmodified MARCXML
		if(marcVocab == null)
		{
			if(log.isDebugEnabled())
				log.debug("Cannot find a MARC vocabulary mapping for the leader 06 value of " + leader06 + ".");
			
			if(leader06 != ' ')
				errors.add(service.getId() + "-102: Invalid leader 06 value: " + leader06);
		}
		else
		{
			if(log.isDebugEnabled())
				log.debug("Found the MARC vocabulary " + marcVocab + " for the leader 06 value of " + leader06 + ".");

			// Add a MARCXML field to store the SMD Vocab
			marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_007_MARC_VOCAB, marcVocab);
		}

		String field006 = marcXml.getField006();
		if(field006 != null)
		{
			// The character at offset 6 of the leader field
			char field006_0 = field006.charAt(0);

			// Pull the DCMI type mapping from the configuration file based on the leader 06 value.
			String marcVocab006_0 = leader06MarcVocabProperties.getProperty(""+field006_0, null);

			// If there was no mapping for the provided leader 06, we can't create the field.  In this case return the unmodified MARCXML
			if(marcVocab006_0 == null)
			{
				if(log.isDebugEnabled())
					log.debug("Cannot find a MARC vocabulary mapping for the 006 offset 0 value of " + marcVocab006_0 + ".");
			}

			else
			{
				if(log.isDebugEnabled())
					log.debug("Found the MARC vocabulary " + marcVocab006_0 + " for the 006 offset 0 value of " + marcVocab006_0 + ".");

				// Add a MARCXML field to store the SMD Vocab
				marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_007_MARC_VOCAB, marcVocab006_0);
			}
		}

		// Return the modified MARCXML record
		return marcXml;
	}

	/**
	 * Creates a vocabulary field based on the record's Leader 06 value.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService vocab06(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering 007Vocab06 normalization step.");

		// The character at offset 6 of the leader field
		char leader06 = marcXml.getLeader().charAt(6);

		// Pull the SMD Vocab mapping from the configuration file based on the leader 06 value.
		String marcVocab = vocab06Properties.getProperty(""+leader06, null);

		// If there was no mapping for the provided leader 06, we can't create the field.  In this case return the unmodified MARCXML
		if(marcVocab == null)
		{
			if(log.isDebugEnabled())
				log.debug("Cannot find a vocab mapping for the leader 06 value of " + leader06 + ", returning the unmodified MARCXML.");

			return marcXml;
		}

		if(log.isDebugEnabled())
			log.debug("Found the vocab " + marcVocab + " for the leader 06 value of " + leader06 + ".");

		// Add a MARCXML field to store the SMD Vocab
		marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_007_VOCAB, marcVocab);

		// Return the modified MARCXML record
		return marcXml;
	}

	/**
	 * Creates a mode of issuance field based upon single letter in Leader07
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService modeOfIssuance(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering ModeOfIssuance normalization step.");

		// The character at offset 7 of the leader field
		char leader07 = marcXml.getLeader().charAt(7);

		// Pull the mode of issuance mapping from the configuration file based on the leader 07 value.
		String modeOfIssuance = modeOfIssuanceProperties.getProperty(""+leader07, null);

		// If there was no mapping for the provided leader 07, we can't create the field.  In this case return the unmodified MARCXML
		if(modeOfIssuance == null)
		{
			if(log.isDebugEnabled())
				log.debug("Cannot find a mode of issuance mapping for the leader 07 value of " + leader07 + ", returning the unmodified MARCXML.");

			errors.add(service.getId() + "-103: Invalid leader 07 value: " + leader07);
			
			return marcXml;
		}

		if(log.isDebugEnabled())
			log.debug("Found the mode of issuance " + modeOfIssuance + " for the leader 07 value of " + leader07 + ".");

		// Add a MARCXML field to store the mode of issuance
		marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_MODE_OF_ISSUANCE, modeOfIssuance);

		return marcXml;
	}

	/**
	 * Creates a new 035 field on the record based on the existing 001 and 003 fields
	 * for example, if 001 = 12345 and 003 = NRU, the new 035 will be (NRU)12345.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService moveMarcOrgCode(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering MoveMarcOrgCode normalization step.");

		// Get the 001 and 003 control fields
		String control001 = marcXml.getField001();
		String control003 = marcXml.getField003();

		// If either control field didn't exist, we don't have to do anything
		if(control001 == null || control003 == null)
		{
			if(log.isDebugEnabled())
				log.debug("The record was missing either an 001 or an 003 control field, so we do not have to move the old marc organization code into a new 035 field.");

			return marcXml;
		}

		boolean moveAllOrgCodes = enabledSteps.getProperty(NormalizationServiceConstants.CONFIG_MOVE_ALL_MARC_ORG_CODES, "0").equals("1");

		// Create the new 035 field
		if(moveAllOrgCodes || control003.equals(getOrganizationCode()))
		{
			String new035 = "(" + control003 + ")" + control001;

			if(log.isDebugEnabled())
				log.debug("Moving the record's organization code to a new 035 field with value " + new035 + ".");

			// Add the new 035 field
			marcXml.addMarcXmlField("035", new035);
		}

		return marcXml;
	}

	/**
	 * Creates a DCMI Type field based on the record's 007 offset 00 value.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService dcmiType0007(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering DCMIType0007 normalization step.");

		// The value of field 007
		String field007 = marcXml.getField007();

		// The character at offset 00 of the 007 field
		char field007offset00 = (field007 != null ? field007.charAt(0) : ' ');

		// Pull the DCMI type mapping from the configuration file based on the leader 06 value.
		String dcmiType = dcmiType0007Properties.getProperty(""+field007offset00, null);

		// If there was no mapping for the provided 007 offset 00, we can't create the field.  In this case return the unmodified MARCXML
		if(dcmiType == null)
		{
			if(log.isDebugEnabled())
				log.debug("Cannot find a DCMI Type mapping for the 007 offset 00 value of " + field007offset00 + ", returning the unmodified MARCXML.");

			return marcXml;
		}

		if(log.isDebugEnabled())
			log.debug("Found the DCMI Type " + dcmiType + " for the 007 offset 00 value of " + field007offset00 + ".");

		// Add a MARCXML field to store the DCMI Type
		marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_DCMI_TYPE, dcmiType);

		// Return the modified MARCXML record
		return marcXml;
	}

	/**
	 * Creates an 007 vocabulary field based on the record's 007 offset 00 value.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService vocab007(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering 007Vocab normalization step.");

		// The value of field 007
		String field007 = marcXml.getField007();

		// The character at offset 00 of the 007 field
		char field007offset00 = (field007 != null ? field007.charAt(0) : ' ');

		// Pull the 007 Vocab mapping from the configuration file based on the leader 06 value.
		String smdVocab = vocab007Properties.getProperty(""+field007offset00, null);

		// If there was no mapping for the provided 007 offset 00, we can't create the field.  In this case return the unmodified MARCXML
		if(smdVocab == null)
		{
			if(log.isDebugEnabled())
				log.debug("Cannot find an 007 Vocab mapping for the 007 offset 00 value of " + field007offset00 + ", returning the unmodified MARCXML.");

			errors.add(service.getId() + "-104: Invalid value in Control Field 007 offset 00: " + field007offset00);
			
			return marcXml;
		}

		if(log.isDebugEnabled())
			log.debug("Found the 007 Vocab " + smdVocab + " for the 007 offset 00 value of " + field007offset00 + ".");

		// Add a MARCXML field to store the SMD Vocab
		marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_007_VOCAB, smdVocab);

		// Return the modified MARCXML record
		return marcXml;
	}

	/**
	 * Creates an SMD vocabulary field based on the record's 007 offset 00 and offset 01 values.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService smdType007(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering 007SMDVocab normalization step.");

		// The value of field 007
		String field007 = marcXml.getField007();

		// The character at offsets 00 and 01 of the 007 field
		String field007offset00and01 = (field007 != null ? field007.substring(0, 2) : "  ");

		// Pull the SMD type mapping from the configuration file based on the leader 06 value.
		String smdVocab = smdType007Properties.getProperty(field007offset00and01, null);

		// If there was no mapping for the provided 007 offset 00, we can't create the field.  In this case return the unmodified MARCXML
		if(smdVocab == null)
		{
			if(log.isDebugEnabled())
				log.debug("Cannot find a SMD Vocab mapping for the 007 offset 00 and 01 values of " + field007offset00and01 + ", returning the unmodified MARCXML.");

			return marcXml;
		}

		if(log.isDebugEnabled())
			log.debug("Found the SMD type " + smdVocab + " for the 007 offset 00 and 01 values of " + field007offset00and01 + ".");

		// Add a MARCXML field to store the SMD Vocab
		marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_SMD_VOCAB, smdVocab);

		// Return the modified MARCXML record
		return marcXml;
	}

	/**
	 * Creates a field with a value of "Fiction" if the Leader 06 value is 'a'
	 * and the 008 offset 33 value is '1', otherwise creates the field with a value
	 * of "Non-Fiction"
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService fictionOrNonfiction(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering FictionOrNonfiction normalization step.");

		// The character at offset 6 of the leader field
		char leader06 = marcXml.getLeader().charAt(6);

		// Return the unmodified MARCXML if the leader06 is neither 'a' nor 't'
		if(leader06 != 'a' && leader06 != 't')
			return marcXml;

		// The value of field 008
		String field008 = marcXml.getField008();

		// The character at offset 33 of the 008 field
		char field008offset33 = (field008 != null ? field008.charAt(33) : ' ');

		if(log.isDebugEnabled())
			log.debug("Leader 06 = " + leader06 + " and 008 offset 33 is " + field008offset33 + ".");

		// Add the fiction or nonfiction field
		if(field008offset33 == '1')
			marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_FICTION_OR_NONFICTION, "Fiction");
		else
			marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_FICTION_OR_NONFICTION, "Non-Fiction");

		// Return the modified MARCXML record
		return marcXml;
	}

	/**
	 * Creates a field with the date range specified in the 008 control field
	 * if 008 offset 06 is 'r'
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService dateRange(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering 008DateRange normalization step.");

		// The value of field 008
		String field008 = marcXml.getField008();

		// If 008 offset 06 is not 'c', 'd', or 'k' we don't need to do anything.
		if(field008 == null || (field008.charAt(6) != 'c' && field008.charAt(6) != 'd' && field008.charAt(6) != 'k'))
		{
			if(log.isDebugEnabled())
				log.debug("008 offset 6 was not 'c', 'd' or 'k' so we will not add a field with the date range.");

			return marcXml;
		}

		// If we got here, 008 offset 06 was 'r' and we need to add a field with the date range
		String dateRange = field008.substring(7, 11) + "-" + field008.substring(11, 15);

		// If either date is '9999', replace it with the empty string.  So "1983-9999" becomes "1983-    "
		dateRange = dateRange.replaceAll("9999", "    ");

		if(log.isDebugEnabled())
			log.debug("008 offset 6 was 'c', 'd' or 'k' so we will add a field with the date range " + dateRange + ".");

		// Add the date range field
		marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_DATE_RANGE, dateRange);

		return marcXml;
	}

	/**
	 * Creates a field for each language found in the original record's 008 offset 35-38
	 * and 041 $a and $d fields.  Only one field is added when the original record
	 * would produce duplicates of it.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService languageSplit(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering LanguageSplit normalization step.");

		// A list of language fields we're adding as fields.
		ArrayList<String> languages = new ArrayList<String>();

		// The value of field 008
		String field008 = marcXml.getField008();

		// If the 008 field non null, add the language it specified so the list of languages
		// provided that languages is valid
		if(field008 != null)
		{
			String langFrom008 = field008.substring(35, 38);

			if(isLanguageValid(langFrom008))
			{
				if(log.isDebugEnabled())
					log.debug("Found the valid language " + langFrom008 + " in the 008 field.");

				languages.add(langFrom008);
			}
		}

		// The 041 $a and $d fields
		ArrayList<String> fields041a = marcXml.getField041subfieldA();
		ArrayList<String> fields041d = marcXml.getField041subfieldD();

		// Add the languages in 041 $a to the list of languages assuming doing so wouldn't create duplicates
		for(String field041a : fields041a)
		{
			// Every group of three characters should be treated as a seperate language code.
			// So characters 1-3 are one language, 4-6 are another, etc.
			for(int counter = 0; counter + 3 <= field041a.length(); counter += 3)
			{
				String language = field041a.substring(counter, counter+3);

				if(!languages.contains(language))
				{
					if(isLanguageValid(language))
					{
						if(log.isDebugEnabled())
							log.debug("Found the valid language " + language + " in the 041 $a field.");

						languages.add(language);
					}
				}
			}
		}

		// Add the languages in 041 $d to the list of languages assuming doing so wouldn't create duplicates
		for(String field041d : fields041d)
		{
			// Every group of three characters should be treated as a seperate language code.
			// So characters 1-3 are one language, 4-6 are another, etc.
			for(int counter = 0; counter + 3 <= field041d.length(); counter += 3)
			{
				String language = field041d.substring(counter, counter+3);

				if(!languages.contains(language))
				{
					if(isLanguageValid(language))
					{
						if(log.isDebugEnabled())
							log.debug("Found the valid language " + language + " in the 041 $d field.");

						languages.add(language);
					}
				}
			}
		}

		// Add each language to the MARCXML in a new field
		for(String language : languages)
			marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_LANGUAGE_SPLIT, language);

		return marcXml;
	}

	/**
	 * Creates a field which contains the full name of the language based for
	 * each language code from the LanguageSplit step.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService languageTerm(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering LanguageTerm normalization step.");

		// A list of language code fields we've add.
		ArrayList<String> languageCodes = marcXml.getAddedLanguageCodes();

		// Add each language term to the MARCXML in a new field
		for(String languageCode : languageCodes)
		{
			// Pull the language term mapping from the configuration file based on the language code.
			String languageTerm = languageTermProperties.getProperty(languageCode, null);

			// If there was no mapping for the provided language code, we can't create the field.  In this case continue to the next language code
			if(languageTerm == null)
			{
				if(log.isDebugEnabled())
					log.debug("Cannot find a lanuage term mapping for the language code " + languageCode + ".");

				errors.add(service.getId() + "-106: Unrecognized language code: " + languageCode);
				
				continue;
			}

			if(log.isDebugEnabled())
				log.debug("Found the language term " + languageTerm + " for the language code " + languageCode + ".");

			// Add a MARCXML field to store the SMD Vocab

			marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_LANGUAGE_TERM, languageTerm);
		}

		return marcXml;
	}

	/**
	 * If leader 06 contains certain values, create a field with the intended audience from the
	 * 008 offset 22 value.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService audienceFrom008(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering 008Audience normalization step.");

		// The character at offset 6 of the leader field
		char leader06 = marcXml.getLeader().charAt(6);

		// If the leader 06 value is one of those which suggests this record needs an audience field,
		// add one based on the 008 offset 22 field.
		if(leader06 == 'a' || leader06 == 'c' || leader06 == 'd' || leader06 == 'g' || leader06 == 'k' || leader06 == 'm' || leader06 == 'o' || leader06 == 'r')
		{
			// The value of field 008
			String field008 = marcXml.getField008();

			// The character at offset 22 of the 008 field
			char field008offset22 = (field008 != null ? field008.charAt(22) : ' ');

			// Pull the audience mapping from the configuration file based on the 008 offset 22 value.
			String audience = audienceFrom008Properties.getProperty(""+field008offset22, null);

			// If there was no mapping for the provided 008 offset 22, we can't create the field.  In this case return the unmodified MARCXML
			if(audience == null)
			{
				if(log.isDebugEnabled())
					log.debug("Cannot find an audience mapping for the 008 offset 22 value of " + field008offset22 + ", returning the unmodified MARCXML.");

				return marcXml;
			}
			else if(field008offset22 != '|' && field008offset22 != '#')
				errors.add(service.getId() + "-105: Invalid value in Control Field 008 offset 22: " + field008offset22);

			if(log.isDebugEnabled())
				log.debug("Found the audience " + audience + " for the 008 offset 22 value of " + field008offset22 + ".");

			// Add a MARCXML field to store the audience
			marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_AUDIENCE, audience);
		}

		return marcXml;
	}

	/**
	 * If there is no 502 field, create one with the value "Thesis" if 08 offset 24, 25, 26 or 27 is 'm'.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService thesisFrom008(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering 008Thesis normalization step.");

		// If there is already a 502 field, don't make any changes
		if(marcXml.getField502() != null && marcXml.getField502().size() > 0)
		{
			if(log.isDebugEnabled())
				log.debug("502 field already exists, so we won't add another.");

			return marcXml;
		}

		// Get the leader 06
		char leader06 = marcXml.getLeader().charAt(6);

		// If the leader 06 is not 'a' return without doing anything
		if(leader06 != 'a')
		{
			if(log.isDebugEnabled())
				log.debug("The leader 06 is not 'a', so this cannot be a Thesis.");

			return marcXml;
		}

		// The value of field 008
		String field008 = marcXml.getField008();

		// If the 008 offset 24, 25, 26, or 27 is 'm', add a 502 tag with the value "Thesis."
		if(field008.substring(24, 28).contains("m"))
		{
			if(log.isDebugEnabled())
				log.debug("Adding 502 field with the value \"Thesis.\"");

			// Add a MARCXML 502 field with the value "Thesis"
			marcXml.addMarcXmlField("502", "Thesis.");
		}
		else if(log.isDebugEnabled())
			log.debug("Not adding 502 field with the value \"Thesis.\"");

		return marcXml;
	}

	/**
	 * Creates a field for each 020 field with the same $a value except that
	 * everything after the first left parenthesis is removed.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService isbnCleanup(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering ISBNCleanup normalization step.");

		// The 020 $a field
		ArrayList<String> fields020a = marcXml.getField020();

		// Return if there was no 020 $a
		if(fields020a.size() == 0)
		{
			if(log.isDebugEnabled())
				log.debug("The record did not have an 020 $a field so we don't have to normalize the ISBN number.");

			return marcXml;
		}

		// The index of the first left parenthesis in the 020 $a
		for(String field020a : fields020a)
		{
			int leftParenIndex = field020a.indexOf('(');
			int colonIndex = field020a.indexOf(':');
			int endIndex = (leftParenIndex < 0 ? colonIndex : (colonIndex < 0 || leftParenIndex < colonIndex ? leftParenIndex : colonIndex));

			// The cleaned up ISBN number.  This is the 020 $a with everything after the first left parenthesis removed
			String cleanIsbn = (endIndex >= 0 ? field020a.substring(0, endIndex) : field020a);

			if(endIndex >= 0)
				errors.add(service.getId() + "-109: Malformed ISBN in 020 $a: " + field020a + ".  This was fixed by the Normalization Service.");
			
			if(log.isDebugEnabled())
				log.debug("Adding the cleaned up ISBN number " + cleanIsbn + " to the normalized record.");

			// Add the cleaned up ISBN to the MARCXML in a new field
			marcXml.addMarcXmlField(NormalizationServiceConstants.FIELD_9XX_CLEAN_ISBN, cleanIsbn);
		}

		return marcXml;
	}

	/**
	 * Creates a new 035 field on the record based on the existing 001 field if
	 * there is no 003 field.  For example, if 001 = 12345 and the organization
	 * code in the configuration file is NRU, the new 035 will be (NRU)12345.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService supplyMARCOrgCode(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering SupplyMARCOrgCode normalization step.");

		// Get the 001 and 003 control fields
		String control001 = marcXml.getField001();
		String control003 = marcXml.getField003();

		// If either control field didn't exist, we don't have to do anything
		if(control003 != null || control001 == null)
		{
			if(log.isDebugEnabled())
				log.debug("The record was missing either an 001 or contained an 003 control field, so we do not have to supply a marc organization code into a new 035 field.");

			return marcXml;
		}

		// Create the new 035 field
		String new035 = "(" + getOrganizationCode() + ")" + control001;

		if(log.isDebugEnabled())
			log.debug("Supplying the record's organization code to a new 035 field with value " + new035 + ".");

		// Add the new 035 field
		marcXml.addMarcXmlField("035", new035);

		return marcXml;
	}

	/**
	 * Edits OCLC 035 records with common incorrect formats to take the format
	 * (OCoLC)%CONTROL_NUMBER%.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	@SuppressWarnings("unchecked")
	private MarcXmlManagerForNormalizationService voyagerFix035(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering VoyagerFix035 normalization step.");

		// Get the original list of 035 elements.  We know that any 035 we
		// supplied had the correct format, so all incorrect 035 records must
		// be contained in this list
		ArrayList<Element> field035Elements = marcXml.getOriginal035Fields();

		// Loop over the 035 elements
		for(Element field035 : field035Elements)
		{
			// The $a and $b subfields of
			Element aSubfield = null;
			Element bSubfield = null;
			Element subfield9 = null;

			// Get the control fields
			List<Element> subfields = field035.getChildren("subfield", marcNamespace);

			// Iterate over the subfields to find the $a and $b subfields
			for(Element subfield : subfields)
			{
				// Initialize the aSubfield if we found the $a
				if(subfield.getAttribute("code").getValue().equals("a"))
					aSubfield = subfield;
				else
					errors.add(service.getId() + "-107: Invalid 035 Data Field (035s cannot contain a $" + subfield.getAttribute("code").getValue() + " subfield)");

				// Initialize the bSubfield if we found the $b
				if(subfield.getAttribute("code").getValue().equals("b"))
					bSubfield = subfield;

				// Initialize the subfield9 if we found the $9
				if(subfield.getAttribute("code").getValue().equals("9"))
					subfield9 = subfield;
			} // end loop over 035 subfields

			// First case: $b = ocm or $b = ocn or $b = ocl, and $a contains only the control number
			if(bSubfield != null)
			{
				// Check if the $b subfield was "ocm", "ocn", or "ocl"
				if(bSubfield.getText().equals("ocm") || bSubfield.getText().equals("ocn") || bSubfield.getText().equals("ocl"))
				{
					// Try to parse out the control number from the $a subfield
					if(aSubfield != null)
					{
						try
						{
							String controlNumber = aSubfield.getText().trim();

							// Set $a to (OCoLC)%CONTROL_NUMBER%
							aSubfield.setText("(OCoLC)" + controlNumber);
						}
						catch(NumberFormatException e)
						{
						}
					}
				}

				// Remove the b subfield as we shouldn't ever have one
				field035.removeContent(bSubfield);
			}

			// Second case: $a = (OCoLC)ocm%CONTROL_NUMBER% or (OCoLC)ocn%CONTROL_NUMBER% or (OCoLC)ocl%CONTROL_NUMBER%
			if(aSubfield != null && (aSubfield.getText().startsWith("(OCoLC)ocm") || aSubfield.getText().startsWith("(OCoLC)ocn") || aSubfield.getText().startsWith("(OCoLC)ocl")))
				aSubfield.setText("(OCoLC)" + aSubfield.getText().substring(10));

			// Third case: $a = ocm%CONTROL_NUMBER% or ocn%CONTROL_NUMBER% or ocl%CONTROL_NUMBER%
			if(aSubfield != null && (aSubfield.getText().startsWith("ocm") || aSubfield.getText().startsWith("ocn") || aSubfield.getText().startsWith("ocl")))
				aSubfield.setText("(OCoLC)" + aSubfield.getText().substring(3));

			// Forth case: $9 = ocm%CONTROL_NUMBER% or ocn%CONTROL_NUMBER% or ocl%CONTROL_NUMBER%
			if(subfield9 != null && (subfield9.getText().startsWith("ocm") || subfield9.getText().startsWith("ocn") || subfield9.getText().startsWith("ocl")))
			{
				// Add an $a subfield if there wasn't one
				if(aSubfield == null)
				{
					aSubfield = new Element("subfield", marcNamespace);
					aSubfield.setAttribute("code", "a");
					field035.addContent("\t").addContent(aSubfield).addContent("\n");
				}

				aSubfield.setText("(OCoLC)" + subfield9.getText().substring(3));
				field035.removeContent(subfield9);
			}

			// If the $a has more than one prefix, only use the first one
			if(aSubfield != null)
			{
				String aSubfieldText = aSubfield.getText();
				if(aSubfieldText.contains("(") && aSubfieldText.contains(")"))
					aSubfield.setText(aSubfieldText.substring(0, aSubfieldText.indexOf(')') + 1) + aSubfieldText.substring(aSubfieldText.lastIndexOf(')') + 1));
			}
		}

		return marcXml;
	}

	/**
	 * Removes duplicate 035 fields.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService voyagerDedup035(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering VoyagerDedup035 normalization step.");

		marcXml.deduplicateMarcXmlField("035");

		return marcXml;
	}

	/**
	 * If 100 $4, 110 $4, or 111 $4 are empty and leader 06 is 'a',
	 * set the empty $4 subfields to "aut".
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService roleAuthor(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering RoleAuthor normalization step.");

		// If leader 06 is 'a', set the $4 subfields of 100, 110 and 111 to "aut" if they're not already set
		if(marcXml.getLeader().charAt(6) == 'a')
		{
			if(log.isDebugEnabled())
				log.debug("Setting 100, 110, and 111 $4 to \"aut\" if they're not set to something else already.");

			for(Element field100 : marcXml.getField100Element())
			{
				if(marcXml.getSubfieldsOfField(field100, '4').size() <= 0)
				{
					if(log.isDebugEnabled())
						log.debug("Adding $4 to 100 with value aut.");

					// Add the subfield to the field with the specified value
					Element newSubfield = new Element("subfield", marcNamespace);
					newSubfield.setAttribute("code", "4");
					newSubfield.setText("aut");
					field100.addContent("\t").addContent(newSubfield).addContent("\n\t");
				}
			}

			for(Element field110 : marcXml.getField110Element())
			{
				if(marcXml.getSubfieldsOfField(field110, '4').size() <= 0)
				{
					if(log.isDebugEnabled())
						log.debug("Adding $4 to 110 with value aut.");

					// Add the subfield to the field with the specified value
					Element newSubfield = new Element("subfield", marcNamespace);
					newSubfield.setAttribute("code", "4");
					newSubfield.setText("aut");
					field110.addContent("\t").addContent(newSubfield).addContent("\n\t");
				}
			}

			for(Element field111 : marcXml.getField111Element())
			{
				if(marcXml.getSubfieldsOfField(field111, '4').size() <= 0)
				{
					if(log.isDebugEnabled())
						log.debug("Adding $4 to 111 with value aut.");

					// Add the subfield to the field with the specified value
					Element newSubfield = new Element("subfield", marcNamespace);
					newSubfield.setAttribute("code", "4");
					newSubfield.setText("aut");
					field111.addContent("\t").addContent(newSubfield).addContent("\n\t");
				}
			}
		}

		return marcXml;
	}

	/**
	 * If 100 $4, 110 $4, or 111 $4 are empty and leader 06 is 'c',
	 * set the empty $4 subfields to "cmp".
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService roleComposer(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering RoleComposer normalization step.");

		// If leader 06 is 'c', set the $4 subfields of 100, 110 and 111 to "cmp" if they're not already set
		if(marcXml.getLeader().charAt(6) == 'c')
		{
			if(log.isDebugEnabled())
				log.debug("Setting 100, 110, and 111 $4 to \"cmp\" if they're not set to something else already.");

			for(Element field100 : marcXml.getField100Element())
			{
				if(marcXml.getSubfieldsOfField(field100, '4').size() <= 0)
				{
					if(log.isDebugEnabled())
						log.debug("Adding $4 to 100 with value cmp.");

					// Add the subfield to the field with the specified value
					Element newSubfield = new Element("subfield", marcNamespace);
					newSubfield.setAttribute("code", "4");
					newSubfield.setText("cmp");
					field100.addContent("\t").addContent(newSubfield).addContent("\n\t");
				}
			}

			for(Element field110 : marcXml.getField110Element())
			{
				if(marcXml.getSubfieldsOfField(field110, '4').size() <= 0)
				{
					if(log.isDebugEnabled())
						log.debug("Adding $4 to 110 with value cmp.");

					// Add the subfield to the field with the specified value
					Element newSubfield = new Element("subfield", marcNamespace);
					newSubfield.setAttribute("code", "4");
					newSubfield.setText("cmp");
					field110.addContent("\t").addContent(newSubfield).addContent("\n\t");
				}
			}

			for(Element field111 : marcXml.getField111Element())
			{
				if(marcXml.getSubfieldsOfField(field111, '4').size() <= 0)
				{
					if(log.isDebugEnabled())
						log.debug("Adding $4 to 111 with value cmp.");

					// Add the subfield to the field with the specified value
					Element newSubfield = new Element("subfield", marcNamespace);
					newSubfield.setAttribute("code", "4");
					newSubfield.setText("cmp");
					field111.addContent("\t").addContent(newSubfield).addContent("\n\t");
				}
			}
		}

		return marcXml;
	}

	/**
	 * If 130, 240, and 243 all don't exist and 245 does exist, copy the 245 into
	 * a new 243 field.  Only copies subfields afknp
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService uniformTitle(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering UniformTitle normalization step.");

		// If 130, 240, and 243 all don't exist and 245 does exist, copy the 245 into a new 240 field.
		// Only copy subfields afknp.
		if(marcXml.getField130() == null && marcXml.getField240() == null && marcXml.getField243() == null && marcXml.getField245() != null)
		{
			if(marcXml.getField100Element().size() > 0 || marcXml.getField110Element().size() > 0 || marcXml.getField111Element().size() > 0)
				marcXml.copyMarcXmlField("245", "240", "afknp", "0", "0", true);
			else
				marcXml.copyMarcXmlField("245", "130", "afknp", "0", " ", true);
		}

		return marcXml;
	}

	/**
	 * Changes 655 $2 subfield to "NRUgenre" and deletes the 655 $5 subfield
	 * for each 655 field with $2 = "local" and $5 = "NRU"
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	@SuppressWarnings("unchecked")
	private MarcXmlManagerForNormalizationService urGenre(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering URGenre normalization step.");

		// A list of all the 655 fields in the MARCXML record
		ArrayList<Element> field655elements = marcXml.getField655Elements();

		// Fix each 655 with $2 = "local" and $5 = "NRU"
		for(Element field655 : field655elements)
		{
			// The $2 and $5 subfields
			Element subfield2 = null;
			Element subfield5 = null;

			// Get the control fields
			List<Element> subfields = field655.getChildren("subfield", marcNamespace);

			// Iterate over the subfields, and append each one to the subject display if it
			// is in the list of key 655 subfields
			for(Element subfield : subfields)
			{
				// Save the current subfield if it's the $2 subfield
				if(subfield.getAttribute("code").getValue().equals("2"))
					subfield2 = subfield;

				// Save the current subfield if it's the $5 subfield
				if(subfield.getAttribute("code").getValue().equals("5"))
					subfield5 = subfield;
			}

			// If the $2 is "local" and the $5 is "NRU", delete the $5 and set
			// the $2 to "NRUgenre"
			if(subfield2 != null && subfield5 != null && subfield2.getText().equals("local") && subfield5.getText().equals("NRU"))
			{
				field655.removeContent(subfield5);
				subfield2.setText("NRUgenre");
			}
		}

		return marcXml;
	}

	/**
	 * Copies relevant fields from the 600, 610, 611, 630, and 650 datafields into
	 * 9xx fields.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService topicSplit(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering TopicSplit normalization step.");

		// A list of the tags to copy
		ArrayList<String> tagsToCopy = new ArrayList<String>();
		tagsToCopy.add("600");
		tagsToCopy.add("610");
		tagsToCopy.add("611");
		tagsToCopy.add("630");
		tagsToCopy.add("650");

		// Copy the fields
		marcXml.splitField(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_TOPIC_SPLIT, "vxyz");

		// Add the fields which we want to copy just the $x subfield from
		tagsToCopy.add("648");
		tagsToCopy.add("651");
		tagsToCopy.add("652");
		tagsToCopy.add("653");
		tagsToCopy.add("654");
		tagsToCopy.add("655");

		// Copy just the $x subfields of the fields
		marcXml.splitField(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_TOPIC_SPLIT, 'x');

		return marcXml;
	}

	/**
	 * Copies relevant fields from the 648 datafield into 9xx fields.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService chronSplit(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering ChronSplit normalization step.");

		// A list of the tags to copy
		ArrayList<String> tagsToCopy = new ArrayList<String>();
		tagsToCopy.add("648");

		// Copy the fields
		marcXml.splitField(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_CHRON_SPLIT, "vxyz");

		// Add the fields which we want to copy just the $y subfield from
		tagsToCopy.add("600");
		tagsToCopy.add("610");
		tagsToCopy.add("611");
		tagsToCopy.add("630");
		tagsToCopy.add("650");
		tagsToCopy.add("651");
		tagsToCopy.add("652");
		tagsToCopy.add("653");
		tagsToCopy.add("654");
		tagsToCopy.add("655");

		// Copy just the $y subfields of the fields
		marcXml.splitField(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_CHRON_SPLIT, 'y');

		return marcXml;
	}

	/**
	 * Copies relevant fields from the 651 datafield into 9xx fields.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService geogSplit(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering GeogSplit normalization step.");

		// A list of the tags to copy
		ArrayList<String> tagsToCopy = new ArrayList<String>();
		tagsToCopy.add("651");

		// Copy the fields
		marcXml.splitField(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_GEOG_SPLIT, "vxyz");

		// Add the fields which we want to copy just the $z subfield from
		tagsToCopy.add("600");
		tagsToCopy.add("610");
		tagsToCopy.add("611");
		tagsToCopy.add("630");
		tagsToCopy.add("650");
		tagsToCopy.add("648");
		tagsToCopy.add("652");
		tagsToCopy.add("653");
		tagsToCopy.add("654");
		tagsToCopy.add("655");

		// Copy just the $z subfields of the fields
		marcXml.splitField(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_GEOG_SPLIT, 'z');

		return marcXml;
	}

	/**
	 * Copies relevant fields from the 655 datafield into 9xx fields.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService genreSplit(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering GenreSplit normalization step.");

		// A list of the tags to copy
		ArrayList<String> tagsToCopy = new ArrayList<String>();
		tagsToCopy.add("655");

		// Copy the fields
		marcXml.splitField(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_GENRE_SPLIT, "vxyz");

		// Reset tags to copy to contain only those fields which we want to copy just the $v subfield from
		tagsToCopy.add("600");
		tagsToCopy.add("610");
		tagsToCopy.add("611");
		tagsToCopy.add("630");
		tagsToCopy.add("650");
		tagsToCopy.add("648");
		tagsToCopy.add("652");
		tagsToCopy.add("653");
		tagsToCopy.add("654");
		tagsToCopy.add("651");

		// Copy just the $v subfields of the fields
		marcXml.splitField(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_GENRE_SPLIT, 'v');

		return marcXml;
	}

	/**
	 * Removes duplicate DCMI type fields.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService dedupDcmiType(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering DedupDCMIType normalization step.");

		marcXml.deduplicateMarcXmlField(NormalizationServiceConstants.FIELD_9XX_DCMI_TYPE);

		return marcXml;
	}

	/**
	 * Removes duplicate 007 vocab fields.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService dedup007Vocab(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering Dedup007Vocab normalization step.");

		marcXml.deduplicateMarcXmlField(NormalizationServiceConstants.FIELD_9XX_007_VOCAB);

		return marcXml;
	}

	/**
	 * Replaces the location code in 852 $b with the name of the location it represents.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService voyagerLocationName(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering VoyagerLocationName normalization step.");

		// The 852 $l value
		ArrayList<String> field852subfieldBs = marcXml.getField852subfieldBs();

		// Pull the location mapping from the configuration file based on the 852 $l value.
	    for(String field852subfieldB : field852subfieldBs)
	    {
			String location = locationNameProperties.getProperty(field852subfieldB.replace(' ', '_'), null);

			// If there was no mapping for the provided 852 $b, we can't create the field.  In this case return the unmodified MARCXML
			if(location == null)
			{
				if(log.isDebugEnabled())
					log.debug("Cannot find a location mapping for the 852 $b value of " + field852subfieldB + ", returning the unmodified MARCXML.");

				return marcXml;
			}

			if(log.isDebugEnabled())
				log.debug("Found the location " + location + " for the 852 $b value of " + field852subfieldB + ".");

			// Set the 852 $l value to the location we found for the location code.
			marcXml.setMarcXmlSubfield("852", "b", location, field852subfieldB);
	    }

		return marcXml;
	}

	/**
	 * Replaces the location code in 945 $l with the name of the location it represents.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService IIILocationName(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering IIILocationName normalization step.");

		// The 945 $l value
		ArrayList<String> field945subfieldLs = marcXml.getField945subfieldLs();

		// Pull the location mapping from the configuration file based on the 852 $l value.
	    for(String field945subfieldL : field945subfieldLs)
	    {
			String location = locationNameProperties.getProperty(field945subfieldL.replace(' ', '_'), null);

			// If there was no mapping for the provided 945 $l, we can't create the field.  In this case return the unmodified MARCXML
			if(location == null)
			{
				if(log.isDebugEnabled())
					log.debug("Cannot find a location mapping for the 945 $l value of " + field945subfieldL + ", returning the unmodified MARCXML.");

				return marcXml;
			}

			if(log.isDebugEnabled())
				log.debug("Found the location " + location + " for the 945 $l value of " + field945subfieldL + ".");

			// Set the 945 $l value to the location we found for the location code.
			marcXml.setMarcXmlSubfield("945", "l", location, field945subfieldL);
	    }

		return marcXml;
	}
	
	/**
	 * Copies relevant fields from the 651 datafield into 9xx fields.
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService seperateName(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering SeperateName normalization step.");

		// A list of the tags to copy
		ArrayList<String> tagsToCopy = new ArrayList<String>();
		tagsToCopy.add("700");
		tagsToCopy.add("710");
		tagsToCopy.add("711");

		// Copy the fields, but only if they contain a $t subfield
		marcXml.seperateNames(tagsToCopy, NormalizationServiceConstants.FIELD_9XX_SEPERATE_NAME);

		return marcXml;
	}

	/**
	 * Removes duplicate 959, 963, 965, 967, and 969 fields
	 *
	 * @param marcXml The original MARCXML record
	 * @return The MARCXML record after performing this normalization step.
	 */
	private MarcXmlManagerForNormalizationService dedup9XX(MarcXmlManagerForNormalizationService marcXml)
	{
		if(log.isInfoEnabled())
			log.info("Entering Dedup9XX normalization step.");

		marcXml.deduplicateMarcXmlField(NormalizationServiceConstants.FIELD_9XX_CHRON_SPLIT);
		marcXml.deduplicateMarcXmlField(NormalizationServiceConstants.FIELD_9XX_TOPIC_SPLIT);
		marcXml.deduplicateMarcXmlField(NormalizationServiceConstants.FIELD_9XX_GEOG_SPLIT);
		marcXml.deduplicateMarcXmlField(NormalizationServiceConstants.FIELD_9XX_GENRE_SPLIT);
		marcXml.deduplicateMarcXml959Field();

		return marcXml;
	}

	/**
	 * Returns true if the passed language is valid and false otherwise.  The following languages are invalid:
	 * "mul", "N/A", "xxx", "und", "   ", and languages with more or less than three characters
	 *
	 *
	 * @param language The language code we're testing
	 * @return true if the passed language is valid and false otherwise.
	 */
	private boolean isLanguageValid(String language)
	{
		String languageLower = (language != null ? language.toLowerCase() : null);

		return (languageLower != null && languageLower.length() == 3 && !languageLower.contains("|")&& !languageLower.equals("mul") && !languageLower.equals("n/a") && !languageLower.equals("xxx") && !languageLower.equals("und"));
	}

	@Override
	public void loadConfiguration(String configuration)
	{	
		String[] configurationLines = configuration.split("\n");
	
		// The Properties file we're currently populating
		Properties current = null;
		
	    for(String line : configurationLines)
	    {
    		line = line.trim();
    		
	    	// Skip comments and blank lines
	    	if(line.startsWith("#") || line.length() == 0)
	    		continue;
	    	// If the line contains a property, add it to the current Properties Object
	    	else if(line.contains("="))
	    	{
	    		String key = line.substring(0, line.indexOf('=')).trim();
	    		String value = (line.contains("#") ? line.substring(line.indexOf('=')+1, line.indexOf('#')) : line.substring(line.indexOf('=')+1)).trim();
	    		current.setProperty(key, value);
	    	}
	    	// Otherwise check whether the line contains a valid properties heading
	    	else
	    	{
	    		if(line.equals("LOCATION CODE TO LOCATION"))
	    		{
	    			if(locationNameProperties == null)
	    				locationNameProperties = new Properties();
	    			current = locationNameProperties;
	    		}
	    		else if(line.equals("LEADER 06 TO DCMI TYPE"))
	    		{
	    			if(dcmiType06Properties == null)
	    				dcmiType06Properties = new Properties();
	    			current = dcmiType06Properties;
	    		}
	    		else if(line.equals("LEADER 06 TO MARC VOCAB"))
	    		{
	    			if(leader06MarcVocabProperties == null)
	    				leader06MarcVocabProperties = new Properties();
	    			current = leader06MarcVocabProperties;
	    		}
	    		else if(line.equals("LEADER 06 TO FULL TYPE"))
	    		{
	    			if(vocab06Properties == null)
	    				vocab06Properties = new Properties();
	    			current = vocab06Properties;
	    		}
	    		else if(line.equals("LEADER 07 TO MODE OF ISSUANCE"))
	    		{
	    			if(modeOfIssuanceProperties == null)
	    				modeOfIssuanceProperties = new Properties();
	    			current = modeOfIssuanceProperties;
	    		}
	    		else if(line.equals("FIELD 007 OFFSET 00 TO DCMI TYPE"))
	    		{
	    			if(dcmiType0007Properties == null)
	    				dcmiType0007Properties = new Properties();
	    			current = dcmiType0007Properties;
	    		}
	    		else if(line.equals("FIELD 007 OFFSET 00 TO FULL TYPE"))
	    		{
	    			if(vocab007Properties == null)
	    				vocab007Properties = new Properties();
	    			current = vocab007Properties;
	    		}
	    		else if(line.equals("FIELD 007 OFFSET 00 TO SMD TYPE"))
	    		{
	    			if(smdType007Properties == null)
	    				smdType007Properties = new Properties();
	    			current = smdType007Properties;
	    		}
	    		else if(line.equals("LANGUAGE CODE TO LANGUAGE"))
	    		{
	    			if(languageTermProperties == null)
	    				languageTermProperties = new Properties();
	    			current = languageTermProperties;
	    		}
	    		else if(line.equals("FIELD 008 OFFSET 22 TO AUDIENCE"))
	    		{
	    			if(audienceFrom008Properties == null)
	    				audienceFrom008Properties = new Properties();
	    			current = audienceFrom008Properties;
	    		}
	    		else if(line.equals("ENABLED STEPS"))
	    		{
	    			if(enabledSteps == null)
	    				enabledSteps = new Properties();
	    			current = enabledSteps;
	    		}
	    	}
	    }
	}
	
	@Override
	protected void validateService() throws ServiceValidationException 
	{
		if(locationNameProperties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: LOCATION CODE TO LOCATION");
		else if(dcmiType06Properties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: LEADER 06 TO DCMI TYPE");
		else if(leader06MarcVocabProperties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: LEADER 06 TO MARC VOCAB");
		else if(vocab06Properties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: LEADER 06 TO FULL TYPE");
		else if(modeOfIssuanceProperties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: LEADER 07 TO MODE OF ISSUANCE");
		else if(dcmiType0007Properties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: FIELD 007 OFFSET 00 TO DCMI TYPE");
		else if(vocab007Properties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: FIELD 007 OFFSET 00 TO FULL TYPE");
		else if(smdType007Properties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: FIELD 007 OFFSET 00 TO SMD TYPE");
		else if(languageTermProperties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: LANGUAGE CODE TO LANGUAGE");
		else if(audienceFrom008Properties == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: FIELD 008 OFFSET 22 TO AUDIENCE");
		else if(enabledSteps == null)
			throw new ServiceValidationException("Service configuration file is missing the required section: ENABLED STEPS");
	}
}
