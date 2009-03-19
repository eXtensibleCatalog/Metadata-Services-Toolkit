/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.record;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record in the "Work" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class Work extends Record
{
	/**
	 * The type of indexed Object this is
	 */
	public static String indexedObjectType = "work";

	/**
	 * Flag signifying whether or not a record was built from this work
	 */
	private boolean processed = false;

	/**
	 * The trait for the identifierOfTheWork element in the XC schema
	 */
	public final static String TRAIT_IDENTIFIER_FOR_THE_WORK = "identifierForTheWork";

	/**
	 * Get's the indexed object type of this class.  This is used to differentiate between
	 * different types of objects stored in the index.
	 *
	 * @return The type of indexed object for this Object
	 */
	public String getIndexedObjectType()
	{
		return Work.indexedObjectType;
	} // end method getIndexedObjectType()

	/**
	 * Sets the work's XC work ID
	 *
	 * @param newWorkId The work's new XC work ID
	 */
	public void setXcWorkId(long newWorkId)
	{
		frbrLevelId = newWorkId;
	} // end method setXcWorkId(long)

	/**
	 * Gets the work's XC work ID
	 *
	 * @return The work's XC work ID
	 */
	public long getXcWorkId()
	{
		return frbrLevelId;
	} // end method getXcWorkId()

	/**
	 * Gets whether or not the work has been processed
	 *
	 * @return True if the work has been processed, false otherwise
	 */
	public boolean getProcessed()
	{
		return processed;
	} // end method getProcessed()

	/**
	 * Sets whether or not the work has been processed
	 *
	 * @param processed True if the work has been processed, false otherwise
	 */
	public void setProcessed(boolean processed)
	{
		this.processed = processed;
	} // end method setProcessed(boolean)

	/**
	 * Adds an identifierForTheWork trait to the work
	 *
	 * @param type The type attribute of the identifierForTheWork element
	 * @param value The value of the identifierForTheWork element
	 */
	public void addIdentifierForTheWork(String type, String value)
	{
		addTrait(TRAIT_IDENTIFIER_FOR_THE_WORK + ":(" + type + ")" + value);
	} // end method addIdentifierForTheWork(String, String)

	/**
	 * Adds an identifierForTheWork trait to the work
	 *
	 * @param identifierForTheWork The identifierForTheWork element in the format (<type>)<value>
	 */
	public void addIdentifierForTheWork(String identifierForTheWork)
	{
		addTrait(TRAIT_IDENTIFIER_FOR_THE_WORK + ":" + identifierForTheWork);
	} // end method addIdentifierForTheWork(String)

	/**
	 * Removes an identifierForTheWork trait from the work
	 *
	 * @param type The type attribute of the identifierForTheWork element
	 * @param value The value of the identifierForTheWork element
	 */
	public void removeIdentifierForTheWork(String type, String value)
	{
		removeTrait(TRAIT_IDENTIFIER_FOR_THE_WORK + ":(" + type + ")" + value);
	} // end method removeIdentifierForTheWork(String, String)

	/**
	 * Removes an identifierForTheWork trait from the work
	 *
	 * @param identifierForTheWork The identifierForTheWork element in the format (<type>)<value>
	 */
	public void removeIdentifierForTheWork(String identifierForTheWork)
	{
		removeTrait(TRAIT_IDENTIFIER_FOR_THE_WORK + ":" + identifierForTheWork);
	} // end method removeIdentifierForTheWork(String)

	/**
	 * Gets a list of xc:identifierForTheWork traits on the work
	 *
	 * @returns A list of xc:identifierForTheWork elements for this work
	 */
	public List<String> getIdentifierForTheWorks()
	{
		List<String> results = new ArrayList<String>();

		for(String trait : getTraits())
			if(trait.startsWith(TRAIT_IDENTIFIER_FOR_THE_WORK))
				results.add(trait.substring(trait.indexOf(':')+1));

		return results;
	} // end method getIdentifierForTheWorks()

	/**
	 * Builds a Work with the same fields as the passed Record
	 *
	 * @param record The Record to copy
	 * @return A work with the same fields as the passed Record
	 */
	public static Work buildWorkFromRecord(Record record)
	{
		Work work = new Work();

		work.setCreatedAt(record.getCreatedAt());
		work.setDeleted(record.getDeleted());
		work.setErrors(record.getErrors());
		work.setFormat(record.getFormat());
		work.setFrbrLevelId(record.getFrbrLevelId());
		work.setHarvest(record.getHarvest());
		work.setId(record.getId());
		work.setInputForServices(record.getInputForServices());
		work.setOaiDatestamp(record.getOaiDatestamp());
		work.setOaiHeader(record.getOaiHeader());
		work.setOaiIdentifier(record.getOaiIdentifier());
		work.setOaiIdentifierBase(record.getOaiIdentifierBase());
		work.setOaiXml(record.getOaiXml());
		work.setProcessedByServices(record.getProcessedByServices());
		work.setProcessedFrom(record.getProcessedFrom());
		work.setProvider(record.getProvider());
		work.setService(record.getService());
		work.setSets(record.getSets());
		work.setTraits(record.getTraits());
		work.setUpdatedAt(record.getUpdatedAt());
		work.setUpLinks(record.getUpLinks());
		work.setWarnings(record.getWarnings());

		return work;
	} // end method buildWorkFromRecord(Record)
} // end class Work
