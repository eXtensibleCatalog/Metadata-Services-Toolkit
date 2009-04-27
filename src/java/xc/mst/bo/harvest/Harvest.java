/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.harvest;

import java.sql.Timestamp;

import xc.mst.bo.provider.Provider;

/**
 * Represents a harvest
 *
 * @author Eric Osisek
 */
public class Harvest
{
	/**
	 * The harvest's ID
	 */
	private int id = -1;

	/**
	 * The harvest's start time
	 */
	private Timestamp startTime = null;

	/**
	 * The harvest's end time
	 */
	private Timestamp endTime = null;

	/**
	 * The OAI request placed when the harvest was run
	 */
	private String request = null;

	/**
	 * The result of the harvest
	 */
	private String result = null;

	/**
	 * The name of the harvest schedule that ran the harvest
	 */
	private String harvestScheduleName = null;
	
	/**
	 * The provider that was harvested
	 */
	private Provider provider = null;

	/**
	 * Gets the harvest's ID
	 *
	 * @return The harvest's ID
	 */
	public int getId()
	{
		return id;
	} // end method getHarvestId()

	/**
	 * Sets the harvest's ID
	 *
	 * @param id The harvest's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the harvest's start time
	 *
	 * @return The harvest's start time
	 */
	public Timestamp getStartTime()
	{
		return startTime;
	} // end method getStartTime()

	/**
	 * Sets the harvest's start time
	 *
	 * @param startTime The harvest's new start time
	 */
	public void setStartTime(java.util.Date startTime)
	{
		this.startTime = (startTime == null ? null : new Timestamp(startTime.getTime()));
	} // end method setStartTime(Date)

	/**
	 * Gets the harvest's end time
	 *
	 * @return The harvest's end time
	 */
	public Timestamp getEndTime()
	{
		return endTime;
	} // end method getEndTime()

	/**
	 * Sets the harvest's end time
	 *
	 * @param endTime The harvest's new end time
	 */
	public void setEndTime(java.util.Date endTime)
	{
		this.endTime = (endTime == null ? null : new Timestamp(endTime.getTime()));
	} // end method setEndTime(Date)

	/**
	 * Gets the harvest's request
	 *
	 * @return The harvest's request
	 */
	public String getRequest()
	{
		return request;
	} // end method getRequest()

	/**
	 * Sets the harvest's request
	 *
	 * @param request The harvest's new request
	 */
	public void setRequest(String request)
	{
		this.request = request;
	} // end method setRequest(String)

	/**
	 * Gets the harvest's result
	 *
	 * @return The harvest's result
	 */
	public String getResult()
	{
		return result;
	} // end method getResult()

	/**
	 * Sets the harvest's result
	 *
	 * @param result The harvest's new result
	 */
	public void setResult(String result)
	{
		this.result = result;
	} // end method setResult(String)

	/**
	 * Gets the name of the harvest schedule that ran the harvest
	 *
	 * @return The name of the harvest schedule that ran the harvest
	 */
	public String getHarvestScheduleName()
	{
		return harvestScheduleName;
	} // end method getHarvestScheduleName()

	/**
	 * Sets the name of the harvest schedule that ran the harvest
	 *
	 * @param harvestScheduleName The name of the harvest schedule that ran the harvest
	 */
	public void setHarvestScheduleName(String harvestScheduleName)
	{
		this.harvestScheduleName = harvestScheduleName;
	} // end method setHarvestScheduleName(String)
	
	/**
	 * Gets the provider that was harvested
	 *
	 * @return The provider that was harvested
	 */
	public Provider getProvider()
	{
		return provider;
	} // end method getProvider()

	/**
	 * Sets the provider that was harvested
	 *
	 * @param provider The new provider that was harvested
	 */
	public void setProvider(Provider provider)
	{
		this.provider = provider;
	} // end method setProvider(Provider)
} // end class Harvest
