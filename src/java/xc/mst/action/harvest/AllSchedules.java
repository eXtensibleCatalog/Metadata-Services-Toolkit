/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.harvest;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.ScheduleService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Action to view all schedules
 *
 * @author Sharmila Ranganathan
 */
public class AllSchedules extends ActionSupport
{
    /** Determines if rows are to be sorted in ascending or descending order */
    private boolean isAscendingOrder;
    
    /** Eclipse generated id */
	private static final long serialVersionUID = 4699309117473144076L;

	/** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** All schedules */
	private List<HarvestSchedule> schedules;

	/** Id of schedule to be deleted. */
	private int scheduleId;

	/** Schedule service */
	private ScheduleService scheduleService = new DefaultScheduleService();


	/**
     * Overriding default implementation to view all schedules.
     *
     * @return {@link #SUCCESS}
     */
    public String execute()
    {
    	log.debug("In All schedules Execute() with class");
    	schedules = scheduleService.getAllSchedules();

    	return SUCCESS;
    }

    /**
     * Delete a schedule
     *
     * @return
     */
    public String deleteSchedule() {
    	log.debug("AllSchedules::deleteSchedule() scheduleId = " + scheduleId);

    	HarvestSchedule schedule = scheduleService.getScheduleById(scheduleId);

		if (schedule != null ) {

	    	try {
	    		scheduleService.deleteSchedule(schedule);
	    		schedules = scheduleService.getAllSchedules();
	    	} catch (DataException e) {
	    		log.debug("Deleting the schedule failed" + e.getMessage());
	    		schedules = scheduleService.getAllSchedules();
	    		addFieldError("scheduleDeleteFailed", "Problems with deleting the schedule :" + schedule.getScheduleName());
	    		return INPUT;
	    	}
		}
    	return SUCCESS;
    }

    /**
     * Get all schedules
     *
     * @return
     */
	public List<HarvestSchedule> getSchedules() {
		return schedules;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

    /**
     * sets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public void setIsAscendingOrder(boolean isAscendingOrder)
    {
        this.isAscendingOrder = isAscendingOrder;
    }

    /**
     * sgets the boolean value which determines if the rows are to be sorted in ascending order
     *
     * @param isAscendingOrder
     */
    public boolean getIsAscendingOrder()
    {
        return this.isAscendingOrder;
    }

}
