/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.provider;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import xc.mst.utils.MSTConfiguration;

/**
 * Represents an OAI provider
 *
 * @author Eric Osisek
 */
public class Provider {
	
	// http://www.openarchives.org/OAI/openarchivesprotocol.html#Identify
	public static final String DAY_GRANULARITY = "YYYY-MM-DD";
	public static final String SECOND_GRANULARITY = "YYYY-MM-DDThh:mm:ssZ";
	
	/** The provider's id */
	private int id = -1;

	/** The date and time the provider was created  */
	private Date createdAt = null;

	/** The date and time the provider was updated	 */
	private Timestamp updatedAt = null;

	/** The provider's name 	 */
	private String name = null;

	/** The provider's url	 */
	private String url = null;

	/** The provider's title	 */
	private String title = null;

	/** The provider's creator	 */
	private String creator = null;

	/** The provider's subject	 */
	private String subject = null;

	/** The provider's description	 */
	private String description = null;

	/** The provider's publisher	 */
	private String publisher = null;

	/** The provider's contributors	 */
	private String contributors = null;

	/** The provider's date	 */
	private Date date = null;

	/** The provider's type	 */
	private String type = null;

	/** The provider's format	 */
	private String format = null;

	/** The provider's identifier	 */
	private int identifier = -1;

	/** The provider's language	 */
	private String language = null;

	/** The provider's relation	 */
	private String relation = null;

	/** The provider's coverage	 */
	private String coverage = null;

	/** The provider's rights	 */
	private String rights = null;

	/** True iff the provider is in service	 */
	private boolean service = false;

	/** The time when the listSets and listMetadataFormats responses should be validated for the providers	 */
	private Date nextListSetsListFormats = null;

	/** The version of the OAI protocol the provider supports	 */
	private String protocolVersion = null;

	/** The time when the provider was last validated	 */
	private Date lastValidationDate = null;

	/** Whether or not the provider's identify response was valid	 */
	private boolean identify = false;

	/** Whether or not the provider's listMetadataFormats response was valid	 */
	private boolean listformats = false;

	/** Whether or not the provider's listSets response was valid	 */
	private boolean listsets = false;
	
	protected String granularity = null;

	/** A list of sets the provider supports	 */
	private List<Set> sets = new ArrayList<Set>();

	/** A list of formats the provider supports	 */
	private List<Format> formats = new ArrayList<Format>();

	/** A counter tracking the number of warnings from harvesting the provider	 */
	private int warnings = 0;

	/** A counter tracking the number of errors from harvesting the provider	 */
	private int errors = 0;

	/** A counter tracking the number of new records harvested from the provider	 */
	private int recordsAdded = 0;

	/** A counter tracking the number of updated records harvested from the provider	 */
	private int recordsReplaced = 0;

	/** The last OAI request run on the provider	 */
	private String lastOaiRequest = null;

	/** The timestamp when the last harvest of this provider completed	 */
	private Date lastHarvestEndTime = null;

	/** The timestamp when the provider's logs were last reset	 */
	private Date lastLogReset = null;

	/** The name of the log file for this provider 	 */
	private String logFileName = null;
	
	/** Number of records to harvest	 */
	private long numberOfRecordsToHarvest;	

	/** List of sets the records harvested from this repository belong to. 
	 * After harvest the records belong to different sets than the ones supported by the repository. 
	 */
	private List<Set> harvestedRecordSets = new ArrayList<Set>();
	
	/**
	 * Gets provider's ID
	 *
	 * @return The provider's ID
	 */
	public int getId()
	{
		return id;
	} // end method getId()

	/**
	 * Sets the provider's ID
	 *
	 * @param id The provider's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the date the provider was created
	 *
	 * @return The date when the provider was created
	 */
	public Date getCreatedAt()
	{
		return createdAt;
	} // end method getCreatedAt()

	/**
	 * Sets the date the provider was created
	 *
	 * @param createdAt The new date the provider was created
	 */
	public void setCreatedAt(java.util.Date createdAt)
	{
		this.createdAt = (createdAt == null ? null : new Date(createdAt.getTime()));
	} // end method setCreatedAt(Date)

	/**
	 * Gets the date the provider was last updated
	 *
	 * @return The date the provider was last updated
	 */
	public Timestamp getUpdatedAt()
	{
		return updatedAt;
	} // end getUpdatedAt()

	/**
	 * Sets the date the provider was last updated
	 *
	 * @param updatedAt The new date the provider was last updated
	 */
	public void setUpdatedAt(Timestamp updatedAt)
	{
		this.updatedAt = updatedAt;
	} // end method setUpdatedAt(Timestamp)

	/**
	 * Gets the provider's name
	 *
	 * @return The provider's name
	 */
	public String getName()
	{
		return name;
	} // end method getName()

	/**
	 * Sets the provider's name
	 *
	 * @param name The provider's new name
	 */
	public void setName (String name)
	{
		this.name = name;
	} // end method setName(String)

	/**
	 * Gets the provider's URL
	 *
	 * @return The provider's URL
	 */
	public String getOaiProviderUrl()
	{
		return url;
	} // end method getOaiProviderUrl()

	/**
	 * Sets the provider's URL
	 *
	 * @param url The provider's new URL
	 */
	public void setOaiProviderUrl (String url)
	{
		this.url = url;
	} // end method setOaiProviderUrl(String)

	/**
	 * Gets the provider's title
	 *
	 * @return The provider's title
	 */
	public String getTitle()
	{
		return title;
	} // end method getTitle()

	/**
	 * Sets the provider's title
	 *
	 * @param title The provider's new title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	} // end method setTitle(String)

	/**
	 * Gets the provider's creator
	 *
	 * @return The provider's creator
	 */
	public String getCreator()
	{
		return creator;
	} // end method getCreator()

	/**
	 * Sets the provider's creator
	 *
	 * @param creator The provider's new creator
	 */
	public void setCreator(String creator)
	{
		this.creator = creator;
	} // end method setCreator(String)

	/**
	 * Gets the provider's subject
	 *
	 * @return The provider's subject
	 */
	public String getSubject()
	{
		return subject;
	} // end method getSubject()

	/**
	 * Sets the provider's subject
	 *
	 * @param subject The provider's new subject
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	} // end method setSubject(String)

	/**
	 * Gets the provider's description
	 *
	 * @return The provider's description
	 */
	public String getDescription()
	{
		return description;
	} // end method getDescription()

	/**
	 * Sets the provider's description
	 *
	 * @param description The provider's new description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	} // end method setDescription(String)

	/**
	 * Gets the provider's publisher
	 *
	 * @return The provider's publisher
	 */
	public String getPublisher()
	{
		return publisher;
	} // end method getPublisher()

	/**
	 * Sets the provider's publisher
	 *
	 * @param publisher The provider's new publisher
	 */
	public void setPublisher(String publisher)
	{
		this.publisher = publisher;
	} // end method setPublisher(String)

	/**
	 * Gets the provider's contributors
	 *
	 * @return The provider's contributors
	 */
	public String getContributors()
	{
		return contributors;
	} // end method getContributors()

	/**
	 * Sets the provider's contributors
	 *
	 * @param contributors The provider's new contributors
	 */
	public void setContributors(String contributors)
	{
		this.contributors = contributors;
	} // end method setContributors(String)

	/**
	 * Gets the provider's date
	 *
	 * @return The provider's date
	 */
	public Date getDate()
	{
		return date;
	} // end method getDate()

	/**
	 * Sets the provider's date
	 *
	 * @param date The provider's date
	 */
	public void setDate(java.util.Date date)
	{
		this.date = (date == null ? null : new Date(date.getTime()));
	} // end method setDate(Date)

	/**
	 * Gets the provider's type
	 *
	 * @return The provider's type
	 */
	public String getType()
	{
		return type;
	} // end method getType()

	/**
	 * Sets the provider's type
	 *
	 * @param type The provider's new type
	 */
	public void setType(String type)
	{
		this.type = type;
	} // end method setType(String)

	/**
	 * Gets the provider's format
	 *
	 * @return The provider's format
	 */
	public String getFormat()
	{
		return format;
	} // end method getFormat()

	/**
	 * Sets the provider's format
	 *
	 * @param format The provider's format
	 */
	public void setFormat(String format)
	{
		this.format = format;
	} // end method setFormat(String)

	/**
	 * Gets the provider's identifier
	 *
	 * @return The provider's identifier
	 */
	public int getIdentifier()
	{
		return identifier;
	} // end method getIdentifier()

	/**
	 * Sets the provider's identifer
	 *
	 * @param identifier The provider's identifier
	 */
	public void setIdentifier(int identifier)
	{
		this.identifier = identifier;
	} // end method setIdentifier(int)

	/**
	 * Gets the provider's language
	 *
	 * @return The provider's language
	 */
	public String getLanguage()
	{
		return language;
	} // end method getLanguage()

	/**
	 * Sets the provider's language
	 *
	 * @param language The provider's language
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	} // end method setLanguage(String)

	/**
	 * Gets the provider's relation
	 *
	 * @return The provider's relation
	 */
	public String getRelation()
	{
		return relation;
	} // end method getRelation()

	/**
	 * Sets the provider's relation
	 *
	 * @param relation The provider's new relation
	 */
	public void setRelation(String relation)
	{
		this.relation = relation;
	} // end method setRelation(String)

	/**
	 * Gets the provider's coverage
	 *
	 * @return The provider's coverage
	 */
	public String getCoverage()
	{
		return coverage;
	} // end method getCoverage()

	/**
	 * Sets the provider's coverage
	 *
	 * @param coverage The provider's new coverage
	 */
	public void setCoverage(String coverage)
	{
		this.coverage = coverage;
	} // end setCoverage(String)

	/**
	 * Gets the provider's rights
	 *
	 * @return The provider's rights
	 */
	public String getRights()
	{
		return rights;
	} // end method getRights()

	/**
	 * Sets the provider's rights
	 *
	 * @param rights The provider's new rights
	 */
	public void setRights(String rights)
	{
		this.rights = rights;
	} // end method setRights(String)

	/**
	 * Gets whether the provider is valid for harvesting
	 *
	 * @return True if the provider is valid for harvesting, false otherwise
	 */
	public boolean getService()
	{
		return service;
	} // end method getService()

	/**
	 * Sets whether the provider is valid for harvesting
	 *
	 * @param service True if the provider is valid for harvesting, false otherwise
	 */
	public void setService(boolean service)
	{
		this.service = service;
	} // end method setService(boolean)

	/**
	 * Gets the date when the provider's listSets and listMetadataFormats should be validated
	 *
	 * @return The date when the provider's listSets and listMetadataFormats should be validated
	 */
	public Date getNextListSetsListFormats()
	{
		return nextListSetsListFormats;
	} // end method getNextListSetsListFormats()

	/**
	 * Sets the date when the provider's listSets and listMetadataFormats should be validated
	 *
	 * @param nextListSetsListFormats The new date when the provider's listSets and listMetadataFormats should be validated
	 */
	public void setNextListSetsListFormats(java.util.Date nextListSetsListFormats)
	{
		this.nextListSetsListFormats = (nextListSetsListFormats == null ? null : new Date(nextListSetsListFormats.getTime()));
	} // end method setNextListSetsListFormat(Date)

	/**
	 * Gets provider's protocol version
	 *
	 * @return The provider's protocol version
	 */
	public String getProtocolVersion()
	{
		return protocolVersion;
	} // end method getProtocolVersion()

	/**
	 * Sets the provider's protocol version
	 *
	 * @param protocolVersion The provider's new protocol version
	 */
	public void setProtocolVersion(String protocolVersion)
	{
		this.protocolVersion = protocolVersion;
	} // end method setProtocolVersion(String

	/**
	 * Gets the date when the provider was last validated
	 *
	 * @return The date when the provider was last validated
	 */
	public Date getLastValidationDate()
	{
		return lastValidationDate;
	} // end method getLastValidationDate()

	/**
	 * Sets the date when the provider was last validated
	 *
	 * @param lastValidationDate The new date when the provider was last validated
	 */
	public void setLastValidationDate(java.util.Date lastValidationDate)
	{
		this.lastValidationDate = (lastValidationDate == null ? null : new Date(lastValidationDate.getTime()));
	} // end method setLastValidationDate(Date)

	/**
	 * Gets whether the provider passed validation for the identify OAI request
	 *
	 * @return True if the provider passed validation for the identify OAI request, false otherwise
	 */
	public boolean getIdentify()
	{
		return identify;
	} // end method getIdentify()

	/**
	 * Sets whether the provider passed validation for the identify OAI request
	 *
	 * @param identify True if the provider passed validation for the identify OAI request, false otherwise
	 */
	public void setIdentify(boolean identify)
	{
		this.identify = identify;
	} // end method setIdentify(boolean)

	/**
	 * Gets whether the provider passed validation for the listMetadataFormats OAI request
	 *
	 * @return True if the provider passed validation for the listMetadataFormats OAI request, false otherwise
	 */
	public boolean getListFormats()
	{
		return listformats;
	} // end method getListFormats()

	/**
	 * Sets whether the provider passed validation for the listMetadataFormats OAI request
	 *
	 * @param listformats True if the provider passed validation for the listMetadataFormats OAI request, false otherwise
	 */
	public void setListFormats(boolean listformats)
	{
		this.listformats = listformats;
	} // end method setListFormats(boolean)

	/**
	 * Gets whether or not the provider passed validation for the listSets OAI request
	 *
	 * @return True if the provider passed validation for the listSets OAI request, false otherwise
	 */
	public boolean getListSets()
	{
		return listsets;
	} // end method getListSets()

	/**
	 * Sets whether or not the provider passed validation for the listSets OAI request
	 *
	 * @param listsets True if the provider passed validation for the listSets OAI request, false otherwise
	 */
	public void setListSets(boolean listsets)
	{
		this.listsets = listsets;
	} // end method setListSets(boolean)

	public String getGranularity() {
		return granularity;
	}

	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}
	
	/**
	 * Gets the sets belonging to the provider
	 *
	 * @return The provider's sets
	 */
	public List<Set> getSets()
	{
		return sets;
	} // end method getSets()

	/**
	 * Sets the sets belonging to the provider
	 *
	 * @param sets A list of sets for the provider
	 */
	public void setSets(List<Set> sets)
	{
		this.sets = sets;
	} // end method setSets(List<Set>)

	/**
	 * Adds a set to the list of sets belonging to the provider
	 *
	 * @param set The set to add
	 */
	public void addSet(Set set)
	{
		if(!sets.contains(set))
			sets.add(set);
	} // end method addSet(Set)

	/**
	 * Removes a set from the list of sets belonging to the provider
	 *
	 * @param set The set to remove
	 */
	public void removeSet(Set set)
	{
		if(sets.contains(set))
			sets.remove(set);
	} // end method removeSet(Set)

	/**
	 * Gets the formats the provider supports
	 *
	 * @return The provider's formats
	 */
	public List<Format> getFormats()
	{
		return formats;
	} // end method getFormats()

	/**
	 * Sets the formats the provider supports
	 *
	 * @param formats A list of formats for the provider
	 */
	public void setFormats(List<Format> formats)
	{
		this.formats = formats;
	} // end method setFormats(List<Format>)

	/**
	 * Adds a format to the list of formats the provider supports
	 *
	 * @param format The format to add
	 */
	public void addFormat(Format format)
	{
		if(!formats.contains(format))
			formats.add(format);
	} // end method addFormat(Format)

	/**
	 * Removes a format from the list of formats belonging to the provider
	 *
	 * @param format The format to remove
	 */
	public void removeFormat(Format format)
	{
		if(formats.contains(format))
			formats.remove(format);
	} // end method removeFormat(Format)

    /**
     * Deletes all the formats from a Provider
     */
    public void removeAllFormats()
    {
        formats.clear();
    } // end method removeAllFormats()

    /**
     * Deletes all the sets from a Provider
     */
    public void removeAllSets()
    {
        sets.clear();
    } // end method removeAllSets()

    /**
	 * Gets the number of warnings harvesting the provider
	 *
	 * @return The number of warnings harvesting the provider
	 */
	public int getWarnings()
	{
		return warnings;
	} // end method getWarnings()

	/**
	 * Sets the number of warnings harvesting the provider
	 *
	 * @param warnings The new number of warnings harvesting the provider
	 */
	public void setWarnings(int warnings)
	{
		this.warnings = warnings;
	} // end method setWarnings(int)

	/**
	 * Gets the number of errors harvesting the provider
	 *
	 * @return The number of errors harvesting the provider
	 */
	public int getErrors()
	{
		return errors;
	} // end method getErrors()

	/**
	 * Sets the number of errors harvesting the provider
	 *
	 * @param recordsReplaced The new number of errors harvesting the provider
	 */
	public void setErrors(int errors)
	{
		this.errors = errors;
	} // end method setErrors(int)

    /**
	 * Gets the number of records added by harvesting the provider
	 *
	 * @return The number of records added by harvesting the provider
	 */
	public int getRecordsAdded()
	{
		return recordsAdded;
	} // end method getRecordsAdded()

	/**
	 * Sets the number of records added by harvesting the provider
	 *
	 * @param recordsAdded The new number of records added by harvesting the provider
	 */
	public void setRecordsAdded(int recordsAdded)
	{
		this.recordsAdded = recordsAdded;
	} // end method setRecordsAdded(int)

	/**
	 * Gets the number of records replaced by harvesting the provider
	 *
	 * @return The number of records replaced by harvesting the provider
	 */
	public int getRecordsReplaced()
	{
		return recordsReplaced;
	} // end method getRecordsReplaced()

	/**
	 * Sets the number of records replaced by harvesting the provider
	 *
	 * @param recordsReplaced The new number of records replaced by harvesting the provider
	 */
	public void setRecordsReplaced(int recordsReplaced)
	{
		this.recordsReplaced = recordsReplaced;
	} // end method setRecordsReplaced(int)

	/**
	 * Gets the last OAI request placed on the provider
	 *
	 * @return The last OAI request placed on the provider
	 */
	public String getLastOaiRequest()
	{
		return lastOaiRequest;
	} // end method getLastOaiRequest()

	/**
	 * Sets the last OAI request placed on the provider
	 *
	 * @param lastOaiRequest The new last OAI request placed on the provider
	 */
	public void setLastOaiRequest(String lastOaiRequest)
	{
		this.lastOaiRequest = lastOaiRequest;
	} // end method setLastOaiRequest(String)

	/**
	 * Gets the date when the provider was last harvested
	 *
	 * @return The date when the provider was last harvested
	 */
	public Date getLastHarvestEndTime()
	{
		return lastHarvestEndTime;
	} // end method getLastHarvestEndTime()

	/**
	 * Sets the date when the provider was last harvested
	 *
	 * @param lastHarvestEndTime The new date when the provider was last harvested
	 */
	public void setLastHarvestEndTime(java.util.Date lastHarvestEndTime)
	{
		this.lastHarvestEndTime = (lastHarvestEndTime == null ? null : new Date(lastHarvestEndTime.getTime()));
	} // end method setLastHarvestEndTime(Date)

	/**
	 * Gets the date when the provider's logs were last reset
	 *
	 * @return The date when the provider's logs were last reset
	 */
	public Date getLastLogReset()
	{
		return lastLogReset;
	} // end method getLastLogReset()

	/**
	 * Sets the date when the provider's logs were last reset
	 *
	 * @param lastLogReset The new date when the provider's logs were last reset
	 */
	public void setLastLogReset(java.util.Date lastLogReset)
	{
		this.lastLogReset = (lastLogReset == null ? null : new Date(lastLogReset.getTime()));
	} // end method setLastLogReset(Date)

	/**
	 * Gets the name of the log file for the provider
	 *
	 * @return The name of the log file for the provider
	 */
	public String getLogFileName() {
		return getLogFileName(false);
	}
	public String getLogFileName(boolean abs) {
		if (abs) {
			return MSTConfiguration.getUrlPath()+"/"+logFileName;
		} else {
			return logFileName;
		}
	} // end method getLogFileName()

	/**
	 * Sets the name of the log file for the provider
	 *
	 * @param logFileName The new name of the log file for the provider
	 */
	public void setLogFileName(String logFileName)
	{
		this.logFileName = logFileName;
	} // end method setLogFileName(String)

	@Override
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof Provider))
			return false;

		Provider other = (Provider)o;

		return other.url.equals(this.url);
	} // end method equals(Object)

	public List<Set> getHarvestedRecordSets() {
		return harvestedRecordSets;
	}

	public void setHarvestedRecordSets(List<Set> harvestedRecordSets) {
		this.harvestedRecordSets = harvestedRecordSets;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[Id="+id);
		sb.append(" name= "+ name);
		sb.append(" URL= "+ url+"]");
		
		return sb.toString();
	}

	/**
	 * Get number of records to harvest for testing
	 * 
	 * @return number of records to harvest
	 */
	public long getNumberOfRecordsToHarvest() {
		return numberOfRecordsToHarvest;
	}

	/**
	 * Set number of records to harvest for testing
	 * 
	 * @param numberOfRecordsToHarvest  number of records to harvest
	 */
	public void setNumberOfRecordsToHarvest(long numberOfRecordsToHarvest) {
		this.numberOfRecordsToHarvest = numberOfRecordsToHarvest;
	}

} // end class Provider
