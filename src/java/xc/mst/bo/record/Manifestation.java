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
 * Represents a record in the "Manifestation" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class Manifestation extends Record
{
	/**
	 * The type of indexed Object this is
	 */
	public static String indexedObjectType = "manifestation";

	/**
	 * The trait for the recordId element in the XC schema
	 */
	public final static String TRAIT_RECORD_ID = "M_recordId";

	/**
	 * Get's the indexed object type of this class.  This is used to differentiate between
	 * different types of objects stored in the index.
	 *
	 * @return The type of indexed object for this Object
	 */
	public String getIndexedObjectType()
	{
		return Manifestation.indexedObjectType;
	} // end method getIndexedObjectType()

	/**
	 * Sets the manifestation's XC manifestation ID
	 *
	 * @param newManifestationId The manifestation's new XC manifestation ID
	 */
	public void setXcManifestationId(long newManifestationId)
	{
		frbrLevelId = newManifestationId;
	} // end method setXcManifestationId(long)

	/**
	 * Gets the manifestation's XC manifestation ID
	 *
	 * @return The manifestation's XC manifestation ID
	 */
	public long getXcManifestationId()
	{
		return frbrLevelId;
	} // end method getXcManifestationId()

	/**
	 * Adds a recordID trait to the manifestation
	 *
	 * @param type The type of the recordID to add
	 * @param value The value of the recordID to add
	 */
	public void addXcRecordId(String type, String value)
	{
		addTrait(TRAIT_RECORD_ID + ":(" + type + ")" + value);
	} // end method addXcRecordId(String, String)

	/**
	 * Adds a recordID trait to the manifestation
	 *
	 * @param recordId The recordID to add in the format (<type>)<value>
	 */
	public void addXcRecordId(String recordId)
	{
		addTrait(TRAIT_RECORD_ID + ":" + recordId);
	} // end method addXcRecordId(String)

	/**
	 * Removes a recordID trait from the manifestation
	 *
	 * @param type The type of the recordID to remove
	 * @param value The value of the recordID to remove
	 */
	public void removeXcRecordId(String type, String value)
	{
		removeTrait(TRAIT_RECORD_ID + ":(" + type + ")" + value);
	} // end method removeXcRecordId(String, String)

	/**
	 * Removes a recordID trait from the manifestation
	 *
	 * @param recordId The recordID to remove in the format (<type>)<value>
	 */
	public void removeXcRecordId(String recordId)
	{
		removeTrait(TRAIT_RECORD_ID + ":" + recordId);
	} // end method removeXcRecordId(String)

	/**
	 * Gets a list of xc:recordID traits on this manifestation
	 *
	 * @returns A list of xc:recordID elements for this manifestation
	 */
	public List<String> getXcRecordIds()
	{
		List<String> results = new ArrayList<String>();

		for(String trait : getTraits())
			if(trait.startsWith(TRAIT_RECORD_ID))
				results.add(trait.substring(trait.indexOf(':')+1));

		return results;
	} // end method getXcRecordIds()

	/**
	 * Adds an up link to the passed expression element
	 *
	 * @param expression The expression element to link to
	 */
	public void addLinkToExpression(Expression expression)
	{
		addUpLink(expression);
	} // end method addLinkToExpression(Expression)

	/**
	 * Removes an up link to the passed expression element
	 *
	 * @param expression The expression element to remove the link to
	 */
	public void removeLinkToExpression(Expression expression)
	{
		removeUpLink(expression);
	} // end method removeLinkToExpression(Expression)

	/**
	 * Builds a Manifestation with the same fields as the passed Record
	 *
	 * @param record The Record to copy
	 * @return A manifestation with the same fields as the passed Record
	 */
	public static Manifestation buildManifestationFromRecord(Record record)
	{
		Manifestation manifestation = new Manifestation();

		manifestation.setCreatedAt(record.getCreatedAt());
		manifestation.setDeleted(record.getDeleted());
		manifestation.setErrors(record.getErrors());
		manifestation.setFormat(record.getFormat());
		manifestation.setFrbrLevelId(record.getFrbrLevelId());
		manifestation.setHarvest(record.getHarvest());
		manifestation.setId(record.getId());
		manifestation.setInputForServices(record.getInputForServices());
		manifestation.setOaiDatestamp(record.getOaiDatestamp());
		manifestation.setOaiHeader(record.getOaiHeader());
		manifestation.setOaiIdentifier(record.getOaiIdentifier());
		manifestation.setOaiIdentifierBase(record.getOaiIdentifierBase());
		manifestation.setOaiXml(record.getOaiXml());
		manifestation.setProcessedByServices(record.getProcessedByServices());
		manifestation.setProcessedFrom(record.getProcessedFrom());
		manifestation.setProvider(record.getProvider());
		manifestation.setService(record.getService());
		manifestation.setSets(record.getSets());
		manifestation.setTraits(record.getTraits());
		manifestation.setUpdatedAt(record.getUpdatedAt());
		manifestation.setUpLinks(record.getUpLinks());

		return manifestation;
	} // end method buildManifestationFromRecord(Record)
} // end class Manifestation
