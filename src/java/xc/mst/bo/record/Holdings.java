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
 * Represents a record in the "Holdings" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class Holdings extends Record
{
	/**
	 * The type of indexed Object this is
	 */
	public static String indexedObjectType = "holdings";

	/**
	 * The trait for the recordId element in the XC schema
	 */
	public final static String TRAIT_RECORD_ID = "H_recordId";

	/**
	 * The trait for the manifestationHeld element in the XC schema
	 */
	public final static String TRAIT_MANIFESTATION_HELD = "manifestationHeld";

	/**
	 * Get's the indexed object type of this class.  This is used to differentiate between
	 * different types of objects stored in the index.
	 *
	 * @return The type of indexed object for this Object
	 */
	public String getIndexedObjectType()
	{
		return Holdings.indexedObjectType;
	} // end method getIndexedObjectType()

	/**
	 * Sets the holdings's XC holdings ID
	 *
	 * @param newHoldingsId The holdings's new XC holdings ID
	 */
	public void setXcHoldingsId(long newHoldingsId)
	{
		frbrLevelId = newHoldingsId;
	} // end method setXcHoldingsId(long)

	/**
	 * Gets the holdings's XC holdings ID
	 *
	 * @return The holdings's XC holdings ID
	 */
	public long getXcHoldingsId()
	{
		return frbrLevelId;
	} // end method getXcHoldingsId()

	/**
	 * Adds a recordId trait to the holdings
	 *
	 * @param type The type attribute of the recordId element
	 * @param value The value of the recordId element
	 */
	public void addXcRecordId(String type, String value)
	{
		addTrait(TRAIT_RECORD_ID + ":(" + type + ")" + value);
	} // end method addRecordId(String, String)

	/**
	 * Adds a recordId trait to the holdings
	 *
	 * @param recordId The recordId element in the format (<type>)<value>
	 */
	public void addXcRecordId(String recordId)
	{
		addTrait(TRAIT_RECORD_ID + ":" + recordId);
	} // end method addRecordId(String)

	/**
	 * Removes a recordId trait from the holdings
	 *
	 * @param type The type attribute of the recordId element
	 * @param value The value of the recordId element
	 */
	public void removeXcRecordId(String type, String value)
	{
		removeTrait(TRAIT_RECORD_ID + ":(" + type + ")" + value);
	} // end method removeRecordId(String, String)

	/**
	 * Removes a recordId trait from the holdings
	 *
	 * @param recordId The recordId element in the format (<type>)<value>
	 */
	public void removeXcRecordId(String recordId)
	{
		removeTrait(TRAIT_RECORD_ID + ":" + recordId);
	} // end method removeRecordId(String)

	/**
	 * Gets a list of recordId traits on the work
	 *
	 * @returns A list of xc:recordId elements for this work
	 */
	public List<String> getXcRecordIds()
	{
		List<String> results = new ArrayList<String>();

		for(String trait : getTraits())
			if(trait.startsWith(TRAIT_RECORD_ID))
				results.add(trait.substring(trait.indexOf(':')+1));

		return results;
	} // end method getRecordIds()

	/**
	 * Adds a manifestationHeld trait to the holdings
	 *
	 * @param type The type attribute of the manifestationHeld element
	 * @param value The value of the manifestationHeld element
	 */
	public void addManifestationHeld(String type, String value)
	{
		addTrait(TRAIT_MANIFESTATION_HELD + ":(" + type + ")" + value);
	} // end method addManifestationHeld(String, String)

	/**
	 * Adds a manifestationHeld trait to the holdings
	 *
	 * @param manifestationHeld The manifestationHeld element in the format (<type>)<value>
	 */
	public void addManifestationHeld(String manifestationHeld)
	{
		addTrait(TRAIT_MANIFESTATION_HELD + ":" + manifestationHeld);
	} // end method addManifestationHeld(String)

	/**
	 * Removes a manifestationHeld trait from the holdings
	 *
	 * @param type The type attribute of the manifestationHeld element
	 * @param value The value of the manifestationHeld element
	 */
	public void removeManifestationHeld(String type, String value)
	{
		removeTrait(TRAIT_MANIFESTATION_HELD + ":(" + type + ")" + value);
	} // end method removeManifestationHeld(String, String)

	/**
	 * Removes a manifestationHeld trait from the holdings
	 *
	 * @param manifestationHeld The manifestationHeld element in the format (<type>)<value>
	 */
	public void removeManifestationHeld(String manifestationHeld)
	{
		removeTrait(TRAIT_MANIFESTATION_HELD + ":" + manifestationHeld);
	} // end method removeManifestationHeld(String)

	/**
	 * Gets a list of xc:manifestationHeld traits on the work
	 *
	 * @returns A list of xc:manifestationHeld elements for this work
	 */
	public List<String> getManifestationsHeld()
	{
		List<String> results = new ArrayList<String>();

		for(String trait : getTraits())
			if(trait.startsWith(TRAIT_MANIFESTATION_HELD))
				results.add(trait.substring(trait.indexOf(':')+1));

		return results;
	} // end method getManifestationsHeld()

	/**
	 * Adds an up link to the passed manifestation element
	 *
	 * @param manifestation The manifestation element to link to
	 */
	public void addLinkToManifestation(Manifestation manifestation)
	{
		addUpLink(manifestation);
	} // end method addLinkToManifestation(Manifestation)

	/**
	 * Removes an up link to the passed manifestation element
	 *
	 * @param expression The manifestation element to remove the link to
	 */
	public void removeLinkToManifestation(Manifestation manifestation)
	{
		removeUpLink(manifestation);
	} // end method removeLinkToManifestation(Manifestation)

	/**
	 * Builds a Holdings with the same fields as the passed Record
	 *
	 * @param record The Record to copy
	 * @return A holdings with the same fields as the passed Record
	 */
	public static Holdings buildHoldingsFromRecord(Record record)
	{
		Holdings holdings = new Holdings();

		holdings.setCreatedAt(record.getCreatedAt());
		holdings.setDeleted(record.getDeleted());
		holdings.setErrors(record.getErrors());
		holdings.setFormat(record.getFormat());
		holdings.setFrbrLevelId(record.getFrbrLevelId());
		holdings.setHarvest(record.getHarvest());
		holdings.setId(record.getId());
		holdings.setInputForServices(record.getInputForServices());
		holdings.setOaiDatestamp(record.getOaiDatestamp());
		holdings.setOaiHeader(record.getOaiHeader());
		holdings.setOaiIdentifier(record.getOaiIdentifier());
		holdings.setOaiXml(record.getOaiXml());
		holdings.setProcessedByServices(record.getProcessedByServices());
		holdings.setProcessedFrom(record.getProcessedFrom());
		holdings.setProvider(record.getProvider());
		holdings.setService(record.getService());
		holdings.setSets(record.getSets());
		holdings.setTraits(record.getTraits());
		holdings.setUpdatedAt(record.getUpdatedAt());
		holdings.setUpLinks(record.getUpLinks());

		return holdings;
	} // end method buildHoldingsFromRecord(Record)
} // end class Holdings
