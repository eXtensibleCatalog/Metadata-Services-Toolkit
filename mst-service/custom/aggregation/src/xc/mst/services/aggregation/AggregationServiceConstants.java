/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services.aggregation;

/**
 * This class defines several constants as public static final variables
 * which may be used throughout the AggregationService
 *
 * @author Eric Osisek
 */
public class AggregationServiceConstants
{
	//*******************************************************************
	// Merge fields
	//*******************************************************************

	/**
	 * Parameter for looking up the whether or not to merge works with matching identifierOfTheWork elements
	 */
	public static final String CONFIG_MERGE_IDENTIFIER_FOR_THE_WORK = "merge_identifierForTheWork";

	/**
	 * Parameter for looking up the whether or not to merge manifestations with matching OCoLC record ID elements
	 */
	public static final String CONFIG_MERGE_OCOLC = "merge_OCoLCRecordID";

	/**
	 * Parameter for looking up the whether or not to merge manifestations with matching LCCN record ID elements
	 */
	public static final String CONFIG_MERGE_LCCN = "merge_LCCNRecordID";

	/**
	 * Parameter for looking up the whether or not to merge manifestations with matching ISBN record ID elements
	 */
	public static final String CONFIG_MERGE_ISBN = "merge_ISBNRecordID";

	/**
	 * Parameter for looking up the whether or not to merge manifestations with matching ISSN record ID elements
	 */
	public static final String CONFIG_MERGE_ISSN = "merge_ISSNRecordID";

	/**
	 * Parameter for looking up the whether or not to merge manifestations with matching record ID elements of unknown types
	 */
	public static final String CONFIG_MERGE_RECORD_ID = "merge_recordID";
	
	/**
	 * Parameter for looking up the whether or not to choose merge base record based on number of elements
	 */
	public static final String CONFIG_NUMBER_OF_ELEMENTS = "Number_of_elements";
	
	/**
	 * Parameter for looking up the whether or not to choose merge base record based on record length
	 */
	public static final String CONFIG_RECORD_LENGTH = "Record_Length";
	
	/**
	 * Parameter for looking up the whether or or not to choose merge base record based on institution priority
	 */
	public static final String CONFIG_PROVENANCE = "Provenance";
	
	/**
	 * Parameter for looking up the priority of institution codes
	 */
	public static final String CONFIG_PROVENANCE_PRIORITY = "Provenance_Priority";

	//*******************************************************************
	// Properties files
	//*******************************************************************

	/**
	 * Properties file with fields to merge on for work elements
	 */
	public static final String PROPERTIES_MERGE_WORK = "workMergeFields.properties";

	/**
	 * Properties file with fields to merge on for manifestations
	 */
	public static final String PROPERTIES_MERGE_MANIFESTATION = "manifestationMergeFields.properties";

	//*******************************************************************
	// Other
	//*******************************************************************

	/**
	 * The location of the configuration directory.
	 */
	public static final String CONFIG_DIRECTORY = "config";
}
