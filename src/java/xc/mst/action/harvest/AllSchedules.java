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
    /** determines the column by which the rows are to be sorted */
    private String columnSorted;
    
    /** Determines if rows are to be sorted in ascending or descending order */
    private boolean isAscendingOrder = true;
    
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
	
	/** Error type */
	private String errorType; 


	/**
     * Overriding default implementation to view all schedules.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            log.debug("In All schedules Execute() with class");
            if(columnSorted==null)
            {
                columnSorted = "schedule_name";
            }
            schedules = scheduleService.getAllSchedulesSorted(isAscendingOrder, columnSorted);
            return SUCCESS;
        }
        catch(Exception e)
        {
            schedules = scheduleService.getAllSchedulesSorted(isAscendingOrder, columnSorted);
            return SUCCESS;
        }
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
	    		schedules = scheduleService.getAllSchedulesSorted(isAscendingOrder, columnSorted);
	    	} catch (DataException e) {
	    		log.debug("Deleting the schedule failed" + e.getMessage());
	    		schedules = scheduleService.getAllSchedules();
	    		addFieldError("scheduleDeleteFailed", "Problems with deleting the schedule :" + schedule.getScheduleName());
	    		errorType = "error";
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

    /**
     * sets the name of the column on which the sorting should be performed
     * @param columnSorted name of the column
     */
    public void setColumnSorted(String columnSorted)
    {
        System.out.println("Setting column sorted as "+columnSorted);
        this.columnSorted = columnSorted;
    }

    /**
     * returns the name of the column on which sorting should be performed
     * @return column name
     */
    public String getColumnSorted()
    {
        return this.columnSorted;
    }

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
