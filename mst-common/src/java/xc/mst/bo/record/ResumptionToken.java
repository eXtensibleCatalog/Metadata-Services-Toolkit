/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.record;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.xwork.StringUtils;

import xc.mst.utils.Util;

/**
 * Represents a resumption token
 *
 * @author Eric Osisek
 */
public class ResumptionToken
{
	
	protected Util util = new Util();

	/**
	 * The resumption token's setSpec
	 */
	private String setSpec = null;

	/**
	 * The resumption token's metadata format
	 */
	private String metadataFormat = null;

	/**
	 * The resumption token's from parameter
	 */
	private Timestamp from = null;

	/**
	 * The resumption token's until parameter
	 */
	private Timestamp until = null;
	
	/**
	 * Starting record Id to query
	 */
	private long startingId;

	/**
	 * Gets the resumption token's setSpec
	 *
	 * @return The resumption token's setSpec
	 */
	public String getSetSpec()
	{
		return setSpec;
	} // end method getSetSpec()

	/**
	 * Sets the resumption token's setSpec
	 *
	 * @param setSpec The resumption token's new setSpec
	 */
	public void setSetSpec(String setSpec)
	{
		this.setSpec = setSpec;
	} // end method setSetSpec(String)

	/**
	 * Gets the resumption token's metadata format
	 *
	 * @return The resumption token's metadata format
	 */
	public String getMetadataFormat()
	{
		return metadataFormat;
	} // end method getMetadataFormat()

	/**
	 * Sets the resumption token's metadata format
	 *
	 * @param metadataFormat The resumption token's new metadata format
	 */
	public void setMetadataFormat(String metadataFormat)
	{
		this.metadataFormat = metadataFormat;
	} // end method setMetadataFormat(String)

	/**
	 * Gets the resumption token's from parameter
	 *
	 * @return The resumption token's from parameter
	 */
	public Timestamp getFrom()
	{
		return from;
	} // end method getFrom()

	/**
	 * Sets the resumption token's from parameter
	 *
	 * @param from The resumption token's new from parameter
	 */
	public void setFrom(Date from)
	{
		this.from = (from == null ? null : new Timestamp(from.getTime()));
	} // end setFrom(Date)

	/**
	 * Gets the resumption token's until parameter
	 *
	 * @return The resumption token's until parameter
	 */
	public Timestamp getUntil()
	{
		return until;
	} // end method getUntil()

	/**
	 * Sets the resumption token's until parameter
	 *
	 * @param until The resumption token's new until parameter
	 */
	public void setUntil(Date until)
	{
		this.until = (until == null ? null : new Timestamp(until.getTime()));
	} // end method setUntil(Date)

	public String getToken() {
		StringBuilder sb = new StringBuilder();
		if (this.from != null) {
			sb.append(util.printDateTime(new Date(this.from.getTime())));
		}
		sb.append("|");
		if (this.until != null) {
			sb.append(util.printDateTime(new Date(this.until.getTime())));
		}
		sb.append("|");
		if (!StringUtils.isEmpty(this.setSpec)) {
			sb.append(this.setSpec);
		}
		sb.append("|");
		sb.append(this.metadataFormat);
		sb.append("|");
		sb.append(this.startingId+"");
		return sb.toString();
	}

	public void setToken(String token) {
		//this.token = token;
	}

	/**
	 * Get starting id
	 * 
	 * @return
	 */
	public long getStartingId() {
		return startingId;
	}

	/**
	 * Set starting Id
	 * 
	 * @param startingId
	 */
	public void setStartingId(long startingId) {
		this.startingId = startingId;
	}
} // end class ResumptionToken
