/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.record;

/**
 * Represents a record in the "Expression" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class Expression extends Record
{
	/**
	 * The type of indexed Object this is
	 */
	public static String indexedObjectType = "expression";

	/**
	 * Get's the indexed object type of this class.  This is used to differentiate between
	 * different types of objects stored in the index.
	 *
	 * @return The type of indexed object for this Object
	 */
	public String getType()
	{
		return Expression.indexedObjectType;
	} // end method getIndexedObjectType()

	/**
	 * Adds an up link to the passed work element
	 *
	 * @param work The work element to link to
	 */
	public void addLinkToWork(Work work)
	{
//		addUpLink(work);
	} // end method addLinkToWork(Work)

	/**
	 * Removes an up link to the passed work element
	 *
	 * @param work The work element to remove the link to
	 */
	public void removeLinkToWork(Work work)
	{
//		removeUpLink(work);
	} // end method removeLinkFromWork(Work)

	/**
	 * Builds a Expression with the same fields as the passed Record
	 *
	 * @param record The Record to copy
	 * @return A expression with the same fields as the passed Record
	 */
	public static Expression buildExpressionFromRecord(Record record)
	{
		Expression expression = new Expression();

		expression.setCreatedAt(record.getCreatedAt());
		expression.setDeleted(record.getDeleted());
		expression.setMessages(record.getMessages());
		expression.setFormat(record.getFormat());
		expression.setHarvest(record.getHarvest());
		expression.setId(record.getId());
		expression.setInputForServices(record.getInputForServices());
		expression.setOaiDatestamp(record.getOaiDatestamp());
		expression.setOaiHeader(record.getOaiHeader());
		expression.setOaiXml(record.getOaiXml());
		expression.setProcessedByServices(record.getProcessedByServices());
		expression.setProcessedFrom(record.getProcessedFrom());
		expression.setProvider(record.getProvider());
		expression.setService(record.getService());
		expression.setSets(record.getSets());
		expression.setTraits(record.getTraits());
		expression.setUpdatedAt(record.getUpdatedAt());
		expression.setUpLinks(record.getUpLinks());

		return expression;
	} // end method buildExpressionFromRecord(Record)
} // end class Expression
