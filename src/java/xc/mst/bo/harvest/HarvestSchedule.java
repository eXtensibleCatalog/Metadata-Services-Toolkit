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
import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;

/**
 * Represents a schedule for running a harvest
 *
 * @author Eric Osisek
 */
public class HarvestSchedule
{
	/**
	 * The harvest schedule's ID
	 */
	private int id = -1;

	/**
	 * The harvest schedule's name
	 */
	private String scheduleName = null;

    /**
	 * The harvest schedule's recurrence pattern
	 */
	private String recurrence = null;

    /**
	 * The provider harvested when this schedule is run
	 */
	private Provider provider = null;

	/**
	 * The earliest date this harvest schedule should be run
	 */
	private Date startDate = null;

	/**
	 * The latest date this harvest schedule should be run
	 */
	private Date endDate = null;

	/**
	 * The number of minutes after the hour when this harvest schedule should be run
	 */
	private int minute = -1;

	/**
	 * The day of the week when this harvest schedule should be run
	 */
	private int dayOfWeek = -1;

	/**
	 * The hour (0 - 23) when this harvest schedule should be run
	 */
	private int hour = -1;

	/**
	 * The email address to be notified of the results of running the schedule
	 */
	private String notifyEmail = null;
	
	/**
	 * The current status of the harvest.
	 */
	private String status = null;
	

	/**
	 * A list of sets to be harvested by the schedule
	 */
	private List<Set> sets = new ArrayList<Set>();

	/**
	 * A list of formats to be harvested by the schedule
	 */
	private List<Format> formats = new ArrayList<Format>();

	/**
	 * A list of the schedule's steps
	 */
	private List<HarvestScheduleStep> steps = new ArrayList<HarvestScheduleStep>();

	/**
	 * Gets the harvest schedule's ID
	 *
	 * @return The harvest schedule's ID
	 */
	public int getId()
	{
		return id;
	} // end method getId()

	/**
	 * Sets the harvest schedule's ID
	 *
	 * @param id The harvest schedule's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the harvest schedule's name
	 *
	 * @return The harvest schedule's name
	 */
	public String getScheduleName()
	{
		return scheduleName;
	} // end method getScheduleName()

	/**
	 * Sets the harvest schedule's name
	 *
	 * @param scheduleName The harvest schedule's new name
	 */
	public void setScheduleName(String scheduleName)
	{
		this.scheduleName = scheduleName;
	} // end method setScheduleName(String)

	/**
	 * Gets the harvest schedule's recurrence
	 *
	 * @return The harvest schedule'srecurrence
	 */
	public String getRecurrence()
	{
		return recurrence;
	} // end method getRecurrence()

	/**
	 * Sets the harvest schedule's recurrence
	 *
	 * @param recurrence The harvest schedule's new recurrence
	 */
	public void setRecurrence(String recurrence)
	{
		this.recurrence = recurrence;
	} // end method setRecurrence(String)

	/**
	 * Gets the provider harvested by this schedule
	 *
	 * @return The provider harvested by this schedule
	 */
	public Provider getProvider()
	{
		return provider;
	} // end method getProvider()

	/**
	 * Sets the provider harvested by this schedule
	 *
	 * @param provider The provider harvested by this schedule
	 */
	public void setProvider(Provider provider)
	{
		this.provider = provider;
	} // end method setProvider(Provider)

	/**
	 * Gets the earliest date the schedule should be run
	 *
	 * @return The earliest date the schedule should be run
	 */
	public Date getStartDate()
	{
		return startDate;
	} // end method getStartDate()

	/**
	 * Sets the earliest date the schedule should be run
	 *
	 * @param startDate The earliest date the schedule should be run
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate ;
	} // end method setStartDate(Date)

	/**
	 * Gets the latest date the schedule should be run
	 *
	 * @return The the latest date the schedule should be run
	 */
	public Date getEndDate()
	{
		return endDate;
	} // end method getEndDate()

	/**
	 * Sets the the latest date the schedule should be run
	 *
	 * @param endDate The latest date the schedule should be run
	 */
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	} // end method setEndDate(Date)

	/**
	 * Gets the number of minutes after the hour when this harvest schedule should be run
	 *
	 * @return The number of minutes after the hour when this harvest schedule should be run
	 */
	public int getMinute()
	{
		return minute;
	} // end method getMinute()

	/**
	 * Sets the number of minutes after the hour when this harvest schedule should be run
	 *
	 * @param minute The number of minutes after the hour when this harvest schedule should be run
	 */
	public void setMinute(int minute)
	{
		this.minute = minute;
	} // end method setMinute(int)

	/**
	 * Gets the day of the week when this harvest schedule should be run (1 = Sunday, 2 = Monday, etc.)
	 *
	 * @return The day of the week when this harvest schedule should be run
	 */
	public int getDayOfWeek()
	{
		return dayOfWeek;
	} // end method getDayOfWeek()

	/**
	 * Sets the day of the week when this harvest schedule should be run (1 = Sunday, 2 = Monday, etc.)
	 *
	 * @param dayOfWeek The day of the week when this harvest schedule should be run
	 */
	public void setDayOfWeek(int dayOfWeek)
	{
		this.dayOfWeek = dayOfWeek;
	} // end method setDayOfWeek(int)

	/**
	 * Gets the hour (0 - 23) when this harvest schedule should be run
	 *
	 * @return The hour (0 - 23) when this harvest schedule should be run
	 */
	public int getHour()
	{
		return hour;
	} // end method getHour()

	/**
	 * Sets the hour (0 - 23) when this harvest schedule should be run
	 *
	 * @param hour The hour (0 - 23) when this harvest schedule should be run
	 */
	public void setHour(int hour)
	{
		this.hour = hour;
	} // end method setHour(int)

	/**
	 * Gets the email address to be notified of the results of running the schedule
	 *
	 * @return The email address to be notified of the results of running the schedule
	 */
	public String getNotifyEmail()
	{
		return notifyEmail;
	} // end method getNotifyEmail()

	/**
	 * Sets the email address to be notified of the results of running the schedule
	 *
	 * @param notifyEmail The email address to be notified of the results of running the schedule
	 */
	public void setNotifyEmail(String notifyEmail)
	{
		this.notifyEmail = notifyEmail;
	} // end method setNotifyEmail(String)

	/**
	 * Gets the current status of the harvest.
	 * @return The current status of the harvest.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the current status of the harvest.
	 * @param status The status of the harvest.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the sets to be harvested by the schedule
	 *
	 * @return The sets to be harvested by the schedule
	 */
	public List<Set> getSets()
	{
		return sets;
	} // end method getSets()

	/**
	 * Sets the sets to be harvested by the schedule
	 *
	 * @param sets A list of sets to be harvested by the schedule
	 */
	public void setSets(List<Set> sets)
	{
		this.sets = sets;
	} // end method setSets(List<Set>)

	/**
	 * Adds a set to the list of sets to be harvested by the schedule
	 *
	 * @param set The set to add
	 */
	public void addSet(Set set)
	{
		if(!sets.contains(set))
			sets.add(set);
	} // end method addSet(Set)

	/**
	 * Removes a set from the list of sets to be harvested by the schedule
	 *
	 * @param set The set to remove
	 */
	public void removeSet(Set set)
	{
		if(sets.contains(set))
			sets.remove(set);
	} // end method removeSet(Set)

	/**
	 * Removes all sets
	 */
	public void removeAllSets()
	{
		sets.clear();
	} // end method removeAllSets()

	/**
	 * Gets the formats to be harvested by the schedule
	 *
	 * @return The formats to be harvested by the schedule
	 */
	public List<Format> getFormats()
	{
		return formats;
	} // end method getFormats()

	/**
	 * Sets the formats to be harvested by the schedule
	 *
	 * @param formats A list of formats to be harvested by the schedule
	 */
	public void setFormats(List<Format> formats)
	{
		this.formats = formats;
	} // end method setFormats(List<Format>)

	/**
	 * Adds a format to the list of formats to be harvested by the schedule
	 *
	 * @param format The format to add
	 */
	public void addFormat(Format format)
	{
		if(!formats.contains(format))
			formats.add(format);
	} // end method addFormat(Format)

	/**
	 * Removes a format from the list of formats to be harvested by the schedule
	 *
	 * @param format The format to remove
	 */
	public void removeFormat(Format format)
	{
		if(formats.contains(format))
			formats.remove(format);
	} // end method removeFormat(Format)

	/**
	 * Removes all formats
	 */
	public void removeAllFormats()
	{
		formats.clear();
	} // end method removeAllFormats()

	/**
	 * Gets the schedule's steps
	 *
	 * @return The schedule's steps
	 */
	public List<HarvestScheduleStep> getSteps()
	{
		return steps;
	} // end method getSteps()

	/**
	 * Sets the schedule's steps
	 *
	 * @param steps The schedule's steps
	 */
	public void setSteps(List<HarvestScheduleStep> steps)
	{
		this.steps = steps;
	} // end method setSteps(List<HarvestScheduleStep>)

	/**
	 * Adds a step to the list of the schedule's steps
	 *
	 * @param step The step to add
	 */
	public void addStep(HarvestScheduleStep step)
	{
		if(!steps.contains(step))
			steps.add(step);
	} // end method addStep(HarvestScheduleStep)

	/**
	 * Removes a step from the list of the schedule's steps
	 *
	 * @param step The step to remove
	 */
	public void removeStep(HarvestScheduleStep step)
	{
		if(steps.contains(step))
			steps.remove(step);
	} // end method removeStep(HarvestScheduleStep)

	/**
	 * Removes all steps
	 */
	public void removeAllSteps()
	{
		steps.clear();
	} // end method removeAllSteps()
} // end class HarvestSchedule
