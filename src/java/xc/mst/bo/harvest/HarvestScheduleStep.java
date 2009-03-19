/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.harvest;

import java.sql.Date;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;

/**
 * Represents a step in a schedule for running a harvest
 *
 * @author Eric Osisek
 */
public class HarvestScheduleStep
{
	/**
	 * The harvest schedule step's ID
	 */
	private int id = -1;

	/**
	 * The harvest schedule the step belongs to
	 */
	private HarvestSchedule schedule = null;

	/**
	 * The format to be harvested by this step
	 */
	private Format format = null;

	/**
	 * The set to be harvested by this step
	 */
	private Set set = null;

	/**
	 * The timestamp when this step was last ran
	 */
	private Date lastRan = null;

	/**
	 * Gets the harvest schedule step's ID
	 *
	 * @return The harvest schedule step's ID
	 */
	public int getId()
	{
		return id;
	} // end method getId()

	/**
	 * Sets the harvest schedule step's ID
	 *
	 * @param id The harvest schedule step's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the harvest schedule step's format
	 *
	 * @return The harvest schedule step's format
	 */
	public Format getFormat()
	{
		return format;
	} // end method getFormat()

	/**
	 * Sets the harvest schedule step's format
	 *
	 * @param formatId The harvest schedule step's new format
	 */
	public void setFormat(Format format)
	{
		this.format = format;
	} // end method setFormat(Format)

	/**
	 * Gets the harvest schedule step's set
	 *
	 * @return The harvest schedule step's set
	 */
	public Set getSet()
	{
		return set;
	} // end method getSet()

	/**
	 * Sets the harvest schedule step's set
	 *
	 * @param setId The harvest schedule step's new set
	 */
	public void setSet(Set set)
	{
		this.set = set;
	} // end method setSet(Set)

	/**
	 * Gets the harvest schedule step's schedule
	 *
	 * @return The harvest schedule step's schedule
	 */
	public HarvestSchedule getSchedule()
	{
		return schedule;
	} // end method getSchedule()

	/**
	 * Sets the harvest schedule step's schedule
	 *
	 * @param setId The harvest schedule step's new schedule
	 */
	public void setSchedule(HarvestSchedule schedule)
	{
		this.schedule = schedule;
	} // end method setSchedule(HarvestSchedule)

	/**
	 * Gets the timestamp when this step was last ran
	 *
	 * @return The timestamp when this step was last ran
	 */
	public Date getLastRan()
	{
		return lastRan;
	} // end method getLastRan()

	/**
	 * Sets the timestamp when this step was last ran
	 *
	 * @param lastRan The new timestamp when this step was last ran
	 */
	public void setLastRan(java.util.Date lastRan)
	{
		this.lastRan = (lastRan == null ? null : new Date(lastRan.getTime()));
	} // end method setNextListSetsListFormat(Date)
} // end class HarvestScheduleStep
