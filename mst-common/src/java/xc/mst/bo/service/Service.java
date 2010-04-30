/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.constants.Constants;

/**
 * Represents a service in the MST
 *
 * @author Eric Osisek
 */
public class Service
{
	/**
	 * The service's ID
	 */
	private int id = -1;

	/**
	 * The service's name
	 */
	private String serviceName = null;

	/**
	 * The service's jar
	 */
	private String serviceJar = null;

	/**
	 * The service's identifier which will be used in constructing 'local-identifier' part in OAI identifier
	 */
	private String identifier = null;
	
	/**
	 * The service's service specific configuration parameters
	 */
	private String serviceConfig = null;

	/**
	 * The service's class name
	 */
	private String className = null;

	/**
	 * A list of formats which the service accepts as input
	 */
	private List<Format> inputFormats = new ArrayList<Format>();

	/**
	 * A list of sets which the service outputs
	 */
	private List<Set> outputSets = new ArrayList<Set>();
	
	/**
	 * A list of formats which the service outputs
	 */
	private List<Format> outputFormats = new ArrayList<Format>();

	/**
	 * The number of warnings the service has generated
	 */
	private int warnings = 0;

	/**
	 * The number of errors the service has generated
	 */
	private int errors = 0;

	/**
	 * The number of input records for the service
	 */
	private int inputRecordCount = 0;

	/**
	 * The number of output records for the service
	 */
	private int outputRecordCount = 0;

	/**
	 * The timestamp when the service's logs were last reset
	 */
	private Date lastLogReset = null;

	/**
	 * The name of the log file for this service
	 */
	private String logFileName = null;

	/**
	 * The number of warnings harvesting the service has generated
	 */
	private int harvestOutWarnings = 0;

	/**
	 * The number of errors the harvesting service has generated
	 */
	private int harvestOutErrors = 0;

	/**
	 * The number of records available for harvest from the service
	 */
	private long harvestOutRecordsAvailable = 0;

	/**
	 * The number of harvests from the service
	 */
	private long numberOfHarvests = 0;

	/**
	 * The timestamp when the service's harvest out logs were last reset
	 */
	private Date harvestOutLastLogReset = null;

	/**
	 * The name of the harvest out log file for this service
	 */
	private String harvestOutLogFileName = null;

	/**
	 * The current status of the service.
	 */
	private String status = Constants.STATUS_SERVICE_NOT_RUNNING;
	
	/**
	 * The name of the .xccfg file defining the service
	 */
	private String xccfgFileName = null;
	
	/**
	 * The version of the service
	 */
	private String version = null;
	
	/**
	 * Indicates whether service is deleted
	 */
	private boolean deleted = false;
	
	

	/**
	 * Gets the service's ID
	 *
	 * @return The service's ID
	 */
	public int getId()
	{
		return id;
	} // end method getId()

	/**
	 * Sets the service's ID
	 *
	 * @param id The service's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the service's name
	 *
	 * @return The service's name
	 */
	public String getName()
	{
		return serviceName;
	} // end method getName()

	/**
	 * Sets the service's name
	 *
	 * @param serviceName The service's new name
	 */
	public void setName(String serviceName)
	{
		this.serviceName = serviceName;
	} // end method setName(String)

	/**
	 * Gets the service's jar file
	 *
	 * @return The service's jar file
	 */
	public String getServiceJar()
	{
		return serviceJar;
	} // end method getServiceJar()

	/**
	 * Sets the service's jar file
	 *
	 * @param serviceJar The service's new jar file
	 */
	public void setServiceJar(String serviceJar)
	{
		this.serviceJar = serviceJar;
	} // end method setServiceJar(String)
	
	/**
	 * Gets the service's service specific configuration parameters
	 *
	 * @return The service's service specific configuration parameters
	 */
	public String getServiceConfig()
	{
		return serviceConfig;
	} // end method getServiceConfig()

	/**
	 * Sets the service's service specific configuration parameters
	 *
	 * @param serviceConfig The service's new service specific configuration parameters
	 */
	public void setServiceConfig(String serviceConfig)
	{
		this.serviceConfig = serviceConfig;
	} // end method setServiceConfig(String)

	/**
	 * Gets the service's class name
	 *
	 * @return The service's class name
	 */
	public String getClassName()
	{
		return className;
	} // end method getClassName()

	/**
	 * Sets the service's class name
	 *
	 * @param className The service's new class name
	 */
	public void setClassName(String className)
	{
		this.className = className;
	} // end method setClassName(String)

	/**
	 * Gets the formats the service accepts as input
	 *
	 * @return The provider's input formats
	 */
	public List<Format> getInputFormats()
	{
		return inputFormats;
	} // end method getInputFormats()

	/**
	 * Sets the formats the service accepts as input
	 *
	 * @param formats A list of formats the service accepts as input
	 */
	public void setInputFormats(List<Format> inputFormats)
	{
		this.inputFormats = inputFormats;
	} // end method setInputFormats(List<Format>)

	/**
	 * Adds a format to the list of formats the service accepts as input
	 *
	 * @param format The format to add to the list of formats the service accepts as input
	 */
	public void addInputFormat(Format format)
	{
		if(!inputFormats.contains(format))
			inputFormats.add(format);
	} // end method addInputFormat(Format)

	/**
	 * Removes a format from the list of formats the service accepts as input
	 *
	 * @param format The format to remove from the list of formats the service accepts as input
	 */
	public void removeInputFormat(Format format)
	{
		if(inputFormats.contains(format))
			inputFormats.remove(format);
	} // end method removeInputFormat(Format)

	/**
	 * Gets the sets the service outputs
	 *
	 * @return The provider's output sets
	 */
	public List<Set> getOutputSets()
	{
		return outputSets;
	} // end method getOutputSets()

	/**
	 * Sets the sets the service outputs
	 *
	 * @param sets A list of sets the service outputs
	 */
	public void setOutputSets(List<Set> outputSets)
	{
		this.outputSets = outputSets;
	} // end method setOutputSets(List<Set>)

	/**
	 * Adds a set to the list of sets the service outputs
	 *
	 * @param set The set to add to the list of sets the service outputs
	 */
	public void addOutputSet(Set set)
	{
		if(!outputSets.contains(set))
			outputSets.add(set);
	} // end method addOutputSet(Set)

	/**
	 * Removes a set from the list of sets the service outputs
	 *
	 * @param set The set to remove from the list of sets the service outputs
	 */
	public void removeOutputSet(Set set)
	{
		if(outputSets.contains(set))
			outputSets.remove(set);
	} // end method removeOutputSet(Set)
	
	/**
	 * Gets the formats the service outputs
	 *
	 * @return The formats the service outputs
	 */
	public List<Format> getOutputFormats()
	{
		return outputFormats;
	} // end method getOutputFormats()

	/**
	 * Sets the formats the service outputs
	 *
	 * @param outputFormats A list of formats the service outputs
	 */
	public void setOutputFormats(List<Format> outputFormats)
	{
		this.outputFormats = outputFormats;
	} // end method setOutputFormats(List<Format>)

	/**
	 * Adds a format to the list of formats the service outputs
	 *
	 * @param format The format to add to the list of formats the service outputs
	 */
	public void addOutputFormat(Format format)
	{
		if(!outputFormats.contains(format))
			outputFormats.add(format);
	} // end method addOutputFormat(Format)

	/**
	 * Removes a format from the list of formats the service outputs
	 *
	 * @param format The format to remove from the list of formats the service outputs
	 */
	public void removeOutputFormat(Format format)
	{
		if(outputFormats.contains(format))
			outputFormats.remove(format);
	} // end method removeOutputFormat(Format)

	/**
	 * Gets the number of warnings harvesting the service
	 *
	 * @return The number of warnings harvesting the service
	 */
	public int getServicesWarnings()
	{
		return warnings;
	} // end method getWarnings()

	/**
	 * Sets the number of warnings harvesting the service
	 *
	 * @param warnings The new number of warnings harvesting the service
	 */
	public void setServicesWarnings(int warnings)
	{
		this.warnings = warnings;
	} // end method setWarnings(int)

	/**
	 * Gets the number of errors harvesting the service
	 *
	 * @return The number of errors harvesting the service
	 */
	public int getServicesErrors()
	{
		return errors;
	} // end method getErrors()

	/**
	 * Sets the number of errors generated by the serivce
	 *
	 * @param errors The new number of errors generated by the serivce
	 */
	public void setServicesErrors(int errors)
	{
		this.errors = errors;
	} // end method setErrors(int)

	/**
	 * Gets the number of records the service can use as input
	 *
	 * @return The number of records the service can use as input
	 */
	public int getInputRecordCount()
	{
		return inputRecordCount;
	} // end method getInputRecordCount()

	/**
	 * Sets the number of records the service can use as input
	 *
	 * @param inputRecordCount The new number of records the service can use as input
	 */
	public void setInputRecordCount(int inputRecordCount)
	{
		this.inputRecordCount = inputRecordCount;
	} // end method setInputRecordCount(int)

	/**
	 * Gets the number of records the service has output
	 *
	 * @return The number of records the service has output
	 */
	public int getOutputRecordCount()
	{
		return outputRecordCount;
	} // end method getOutputRecordCount()

	/**
	 * Sets the number of records the service has output
	 *
	 * @param outputRecordCount The new number of records the service has output
	 */
	public void setOutputRecordCount(int outputRecordCount)
	{
		this.outputRecordCount = outputRecordCount;
	} // end method setOutputRecordCount(int)

	/**
	 * Gets the date when the service's logs were last reset
	 *
	 * @return The date when the service's logs were last reset
	 */
	public Date getServicesLastLogReset()
	{
		return lastLogReset;
	} // end method getLastLogReset()

	/**
	 * Sets the date when the service's logs were last reset
	 *
	 * @param lastLogReset The new date when the service's logs were last reset
	 */
	public void setServicesLastLogReset(java.util.Date lastLogReset)
	{
		this.lastLogReset = (lastLogReset == null ? null : new Date(lastLogReset.getTime()));
	} // end method setLastLogReset(Date)

	/**
	 * Gets the name of the log file for the service
	 *
	 * @return The name of the log file for the service
	 */
	public String getServicesLogFileName()
	{
		return logFileName;
	} // end method getLogFileName()

	/**
	 * Sets the name of the log file for the service
	 *
	 * @param logFileName The new name of the log file for the service
	 */
	public void setServicesLogFileName(String logFileName)
	{
		this.logFileName = logFileName;
	} // end method setLogFileName(String)

	/**
	 * Gets the number of warnings harvesting the service
	 *
	 * @return The number of warnings harvesting the service
	 */
	public int getHarvestOutWarnings()
	{
		return harvestOutWarnings;
	} // end method getHarvestOutWarnings()

	/**
	 * Sets the number of warnings harvesting the service
	 *
	 * @param harvestOutWarnings The new number of warnings harvesting the service
	 */
	public void setHarvestOutWarnings(int harvestOutWarnings)
	{
		this.harvestOutWarnings = harvestOutWarnings;
	} // end method setHarvestOutWarnings(int)

	/**
	 * Gets the number of errors harvesting the service
	 *
	 * @return The number of errors harvesting the service
	 */
	public int getHarvestOutErrors()
	{
		return harvestOutErrors;
	} // end method getHarvestOutErrors()

	/**
	 * Sets the number of errors harvesting the serivce
	 *
	 * @param harvestOutErrors The new number of errors harvesting the serivce
	 */
	public void setHarvestOutErrors(int harvestOutErrors)
	{
		this.harvestOutErrors = harvestOutErrors;
	} // end method setHarvestOutErrors(int)

	/**
	 * Gets the number of records the service exposed for harvest
	 *
	 * @return The number of records the service exposed for harvest
	 */
	public long getHarvestOutRecordsAvailable()
	{
		return harvestOutRecordsAvailable;
	} // end method getHarvestOutRecordsAvailable()

	/**
	 * Sets the number of records the service exposed for harvest
	 *
	 * @param harvestOutRecordsAvailable The new number of records the service exposed for harvest
	 */
	public void setHarvestOutRecordsAvailable(long harvestOutRecordsAvailable)
	{
		this.harvestOutRecordsAvailable = harvestOutRecordsAvailable;
	} // end method setHarvestOutRecordsAvailable(long)

	/**
	 * Gets the date when the service's harvest out logs were last reset
	 *
	 * @return The date when the service's harvest out logs were last reset
	 */
	public Date getHarvestOutLastLogReset()
	{
		return harvestOutLastLogReset;
	} // end method getHarvestOutLastLogReset()

	/**
	 * Sets the date when the service's harvest out logs were last reset
	 *
	 * @param lastLogReset The new date when the service's harvest out logs were last reset
	 */
	public void setHarvestOutLastLogReset(java.util.Date harvestOutLastLogReset)
	{
		this.harvestOutLastLogReset = (harvestOutLastLogReset == null ? null : new Date(harvestOutLastLogReset.getTime())); // NOTE : ERIC I made a change here changing harvestOutLastLogReset to LastLogReset
		                                                                                                                    // Eric's response: That was wrong, I changed it back
	} // end method setHarvestOutLastLogReset(Date)

	/**
	 * Gets the name of the harvest out log file for the service
	 *
	 * @return The name of the harvest out log file for the service
	 */
	public String getHarvestOutLogFileName()
	{
		return harvestOutLogFileName;
	} // end method getLogFileName()

	/**
	 * Sets the name of the harvest out log file for the service
	 *
	 * @param logFileName The new name of the harvest out log file for the service
	 */
	public void setHarvestOutLogFileName(String harvestOutLogFileName)
	{
		this.harvestOutLogFileName = harvestOutLogFileName;
	} // end method setHarvestOutLogFileName(String)

	/**
	 * Gets the current status of the service.
	 * @return The current status of the service.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the current status of the service.
	 * @param status The status of the service.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the name of the .xccfg file defining the service
	 * 
	 * @return The name of the .xccfg file defining the service
	 */
	public String getXccfgFileName() 
	{
		return xccfgFileName;
	}

	/**
	 * Sets the name of the .xccfg file defining the service
	 * 
	 * @param xccfgFileName The new name of the .xccfg file defining the service
	 */
	public void setXccfgFileName(String xccfgFileName) 
	{
		this.xccfgFileName = xccfgFileName;
	}
	
	/**
	 * Gets the version of the service
	 * 
	 * @return The version of the service
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version of the service
	 * @param version The version of the service
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof Service))
			return false;

		Service other = (Service)o;

		return other.serviceName.equals(this.serviceName);
	} // end method equals(Object)


	/**
	 * Gets the number of harvests from the service
	 *
	 * @return The  number of harvests 
	 */
	public long getNumberOfHarvests() {
		return numberOfHarvests;
	}

	/**
	 * Sets the number of harvests from the service
	 *
	 * @param numberOfHarvests The  number of harvests
	 */
	public void setNumberOfHarvests(long numberOfHarvests) {
		this.numberOfHarvests = numberOfHarvests;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[Id="+id);
		sb.append(" name = "+ serviceName +"]");
		
		return sb.toString();
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

} // end class Service
