/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.record;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Represents a resumption token
 *
 * @author Eric Osisek
 */
public class ResumptionToken
{
	/**
	 * The resumption token's ID
	 */
	private long resumptionTokenId = -1;

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
	 * The resumption token's offset column
	 */
	private int offset = -1;
	
	/**
	 * The token issued to get more records.
	 */
	private String token;

	/**
	 * Gets the resumption token's ID
	 *
	 * @return The resumption token's ID
	 */
	public long getId()
	{
		return resumptionTokenId;
	} // end method getId()

	/**
	 * Sets the resumption token's ID
	 *
	 * @param resumptionTokenId The resumption token's new ID
	 */
	public void setId(long resumptionTokenId)
	{
		this.resumptionTokenId = resumptionTokenId;
	} // end method setId(long)

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

	/**
	 * Gets the resumption token's offset
	 *
	 * @return The resumption token's offset
	 */
	public int getOffset()
	{
		return offset;
	} // end method getOffset()

	/**
	 * Sets the resumption token's offset
	 *
	 * @param offset The resumption token's new offset
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	} // end method setOffset(int)

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
} // end class ResumptionToken
