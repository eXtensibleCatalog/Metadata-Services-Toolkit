/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.oai;

import xc.mst.constants.Constants;
import xc.mst.utils.XMLUtil;

/**
 * This class constructs OAI errors on behalf of one of the MST's OAI repositories.
 *
 * @author Eric Osisek
 */
public class ErrorBuilder
{
	/**
	 * Creates a badResumptionToken OAI error with no extra message
	 *
	 * @return A badResumptionToken OAI error
	 */
	public static String badResumptionTokenError()
	{
		return badResumptionTokenError(null);
	}

	/**
	 * Creates a badResumptionToken OAI error with the passed extra message
	 *
	 * @param message The error message
	 * @return A badResumptionToken OAI error with the passed extra message
	 */
	public static String badResumptionTokenError(String message)
	{
		return xmlError(Constants.ERROR_BAD_RESUMPTION_TOKEN, message, "badResumptionToken");
	}

	/**
	 * Creates a badArgument OAI error with no extra message
	 *
	 * @return A badArgument OAI error
	 */
	public static String badArgumentError()
	{
		return badArgumentError(null);
	}

	/**
	 * Creates a badArgument OAI error with the passed extra message
	 *
	 * @param message The error message
	 * @return A badArgument OAI error with the passed extra message
	 */
	public static String badArgumentError(String message)
	{
		return xmlError(Constants.ERROR_BAD_ARGUMENT, message, "badArgument");
	}

	/**
	 * Creates a badVerb OAI error with no extra message
	 *
	 * @return A badVerb OAI error
	 */
	public static String badVerbError()
	{
		return badVerbError(null);
	}

	/**
	 * Creates a badVerb OAI error with the passed extra message
	 *
	 * @param message The error message
	 * @return A badVerb OAI error with the passed extra message
	 */
	public static String badVerbError(String message)
	{
		return xmlError(Constants.ERROR_BAD_VERB, message, "badVerb");
	}

	/**
	 * Creates a cannotDisseminateFormat OAI error with no extra message
	 *
	 * @return A cannotDisseminateFormat OAI error
	 */
	public static String cannotDisseminateFormatError()
	{
		return cannotDisseminateFormatError(null);
	}

	/**
	 * Creates a cannotDisseminateFormat OAI error with the passed extra message
	 *
	 * @param message The error message
	 * @return A cannotDisseminateFormat OAI error with the passed extra message
	 */
	public static String cannotDisseminateFormatError(String message)
	{
		return xmlError(Constants.ERROR_ID_DOES_NOT_EXIST, message, "cannotDisseminateFormat");
	}

	/**
	 * Creates a idDoesNotExist OAI error with no extra message
	 *
	 * @return A idDoesNotExist OAI error
	 */
	public static String idDoesNotExistError()
	{
		return idDoesNotExistError(null);
	}

	/**
	 * Creates a idDoesNotExist OAI error with the passed extra message
	 *
	 * @param message The error message
	 * @return A idDoesNotExist OAI error with the passed extra message
	 */
	public static String idDoesNotExistError(String message)
	{
		return xmlError(Constants.ERROR_ID_DOES_NOT_EXIST, message, "idDoesNotExist");
	}

	/**
	 * Creates a noMetadataFormats OAI error with no extra message
	 *
	 * @return A noMetadataFormats OAI error
	 */
	public static String noMetadataFormatsError()
	{
		return noMetadataFormatsError(null);
	}

	/**
	 * Creates a noMetadataFormats OAI error with the passed extra message
	 *
	 * @param message The error message
	 * @return A noMetadataFormats OAI error with the passed extra message
	 */
	public static String noMetadataFormatsError(String message)
	{
		return xmlError(Constants.ERROR_NO_METADATA_FORMATS, message, "noMetadataFormats");
	}

	/**
	 * Creates a noSetHierarchy OAI error with no extra message
	 *
	 * @return A noSetHierarchy OAI error
	 */
	public static String noSetHierarchyError()
	{
		return noSetHierarchyError(null);
	}

	/**
	 * Creates a noSetHierarchy OAI error with the passed message
	 *
	 * @param message The error message
	 * @return A noSetHierarchy OAI error with the passed extra message
	 */
	public static String noSetHierarchyError(String message)
	{
		return xmlError(Constants.ERROR_NO_SET_HIERARCHY, message, "noSetHierarchy");
	}

	/**
	 * Creates and returns an OAI error
	 *
	 * @param base The base error message
	 * @param message A more detailed error message
	 * @param code The OAI error code
	 * @return The OAI error
	 */
	private static String xmlError(String base, String message, String code)
	{
		String msg = base;
		if(message != null && message.length() > 0)
			msg += " " + message;

		return XMLUtil.xmlTag("error", msg, new String[]{"code", code});
	}
}
