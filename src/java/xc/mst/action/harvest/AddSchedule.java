/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.harvest;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.harvester.Hexception;
import xc.mst.harvester.ValidateRepository;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.manager.repository.DefaultFormatService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.DefaultSetService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.ProviderService;
import xc.mst.manager.repository.SetService;

import com.opensymphony.xwork2.ActionSupport;



/**
 * Action to add/edit a schedule
 *
 * @author Sharmila Ranganathan
 */
public class AddSchedule extends ActionSupport implements ServletRequestAware 
{
    /** Eclipse generated id */
	private static final long serialVersionUID = 4317442764979283556L;

	/** A reference to the logger for this class */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/** Schedule to be added */
	private HarvestSchedule schedule = new HarvestSchedule();

	/** Schedule to be edited */
	private int scheduleId;

	/** All repositories */
	private List<Provider> repositories;

	/** Id of repository selected */
	private int repositoryId;

	/** Repository selected */
	private Provider repository;

	/** List of Ids of sets selected */
	private int[] selectedSetIds;

	/** List of Ids of formats selected */
	private int[] selectedFormatIds;

	/** Hour at which the schedule should run every day */
	private int dailyHour;

	/** Minute at which the schedule should run every day */
	private int dailyMinute;
	
	/** Email id to notify the schedule */
	private String notifyEmail;

	/** Name of schedule */
	private String scheduleName;

	/** Recurrence of the schedule */
	private String recurrence;

	/** Recurrence day of week to run schedule */
	private int dayOfWeek;

	/**  Hour at which the schedule should run every week */
	private int hour;

	/** Minutes at which the schedule should run every hour */
	private int minute;

	/** Start date for schedule to run */
	private String startDate;

	/** End date for schedule to stop */
	private Date endDate;

	/** End date for schedule  */
	private String endDateDisplayFormat;

	/** Start date for schedule */
	private String startDateDisplayFormat;

	/** Schedule service */
	private ScheduleService scheduleService = new DefaultScheduleService();

	/** Provider service */
	private ProviderService providerService = new DefaultProviderService();

	/** Set service */
	private SetService setService = new DefaultSetService();

	/** Format service */
	private FormatService formatService = new DefaultFormatService();

	/** Error type */
	private String errorType; 
	
    /** Request */
    private HttpServletRequest request;

	/**
     * Overriding default implementation to view add schedule.
     *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
    	if (log.isDebugEnabled()) {
    		log.debug("In Add schedule Execute()");
    	}
    	try {
	        repositories = providerService.getAllProviders();
	        
	        schedule = new HarvestSchedule();
	        request.getSession().setAttribute("schedule", schedule);
	        
	        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	    	startDateDisplayFormat = format.format(new  java.util.Date());
    	} catch (DatabaseConfigException dce) {
    		log.error(dce.getMessage(), dce);
    		errorType = "error";
    		addFieldError("dbError", "Error in displaying Add schedule page");
    		return INPUT;
    	}
    	
    	return SUCCESS;
    }

    /**
     * Add new schedule
     *
     * @return
     * @throws DataException
     */
    public String addScheduleAndProvider() throws DataException, ParseException {

    	if (log.isDebugEnabled()) {
    		log.debug("AddSchedule::addScheduleAndProvider():: scheduleName=" + scheduleName);
    	}

    	schedule = (HarvestSchedule) request.getSession().getAttribute("schedule");
    	
    	repository = providerService.getProviderById(repositoryId);

    	schedule.setProvider(repository);
    	if (scheduleName == null)
    	{
    		scheduleName = repository.getName() + " " + recurrence;
    	}
    	schedule.setScheduleName(scheduleName);
    	
    	schedule.setRecurrence(recurrence);

    	if (recurrence != null) {
			if (recurrence.equalsIgnoreCase("Daily")) {
				schedule.setHour(dailyHour);
				schedule.setMinute(dailyMinute);
				// Reset other values
				schedule.setDayOfWeek(0);
			}

			if (recurrence.equalsIgnoreCase("Hourly")) {
				schedule.setMinute(minute);

				// Reset other values
				schedule.setDayOfWeek(0);
				schedule.setHour(-1);

			}

			if (recurrence.equalsIgnoreCase("Weekly")) {
				schedule.setDayOfWeek(dayOfWeek);
				schedule.setHour(hour);

				//Reset other values
				schedule.setMinute(-1);
			}
    	}

    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    	schedule.setStartDate(new java.sql.Date(dateFormat.parse(startDate).getTime()));
    	schedule.setEndDate(endDate);

    	// Check if schedule exist for this provider
    	HarvestSchedule otherSchedule = null;
    	List<HarvestSchedule> schedules = scheduleService.getAllSchedules();
    	for(HarvestSchedule harvestSchedule:schedules) {
    		if (harvestSchedule.getProvider().getId() == repositoryId) {
    			otherSchedule = harvestSchedule;
    			break;
    		}
    	}
    	
    	if (otherSchedule == null || (otherSchedule.getId() == scheduleId)) {
	    	// Add schedule to session
    		request.getSession().setAttribute("schedule", schedule);

        	try {
    	    	ValidateRepository validateRepository = new ValidateRepository();
    	        validateRepository.validate(repositoryId);
        	} catch (Hexception he) {
        		log.error("Exception occured while validating the repository", he);
        	}

    	} else {
    		addFieldError("providerExist", "Harvest schedule for repository - " + repository.getName() + " already exists. There can only be one schedule for a repository. ");
    		errorType = "error";
    		repositories = providerService.getAllProviders();
    		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    		if (schedule.getEndDate() != null) {
    			endDateDisplayFormat = format.format(schedule.getEndDate());
    		}
        	startDateDisplayFormat = format.format(schedule.getStartDate());
    		return INPUT;
    	}
    	return SUCCESS;

    }

    /**
     * Update schedule
     *
     * @return
     * @throws DataException
     */
    public String updateSchedule() throws DataException, ParseException{
    	
    	if (log.isDebugEnabled()) {
    		log.debug("In update schedule updateSchedule()");
    	}
    	//schedule = scheduleService.getScheduleById(scheduleId);
    	schedule = (HarvestSchedule) request.getSession().getAttribute("schedule");


    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    	schedule.setStartDate(new java.sql.Date(dateFormat.parse(startDate).getTime()));
    	schedule.setEndDate(endDate);
		schedule.setRecurrence(recurrence);

     	if (recurrence != null) {
			if (recurrence.equalsIgnoreCase("Daily")) {
				schedule.setHour(dailyHour);
				schedule.setMinute(dailyMinute);
				// Reset other values
				schedule.setDayOfWeek(0);
			}

			if (recurrence.equalsIgnoreCase("Hourly")) {
				schedule.setMinute(minute);

				// Reset other values
				schedule.setDayOfWeek(0);
				schedule.setHour(-1);

			}

			if (recurrence.equalsIgnoreCase("Weekly")) {
				schedule.setDayOfWeek(dayOfWeek);
				schedule.setHour(hour);

				//Reset other values
				schedule.setMinute(-1);
			}
    	}

    	repository = providerService.getProviderById(repositoryId);
    	schedule.setProvider(repository);

    	// Check if schedule exist for this provider
    	HarvestSchedule otherSchedule = null;
    	List<HarvestSchedule> schedules = scheduleService.getAllSchedules();
    	for(HarvestSchedule harvestSchedule:schedules) {
    		if (harvestSchedule.getProvider().getId() == repositoryId) {
    			otherSchedule = harvestSchedule;
    			break;
    		}
    	}
    	
    	if (otherSchedule == null || (otherSchedule.getId() == scheduleId)) {
	    	// Add schedule to session
    		request.getSession().setAttribute("schedule", schedule);

    	} else {
    		addFieldError("providerExist", "Harvest schedule for repository - " + repository.getName() + " already exists. There can only be one schedule for a repository. ");
    		errorType = "error";
    		repositories = providerService.getAllProviders();
    		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    		if (schedule.getEndDate() != null) {
    			endDateDisplayFormat = format.format(schedule.getEndDate());
    		}        	startDateDisplayFormat = format.format(schedule.getStartDate());
    		return INPUT;
    	}

    	return SUCCESS;

    }

    /**
     * Add/update sets and formats for schedule
     *
     * @return
     * @throws DataException
     */
    public String addSchedule() throws DataException {

    	if (log.isDebugEnabled()) {
    		log.debug("AddSchedule::addSchedule():: scheduleId=" + scheduleId);
    	}

    	schedule = (HarvestSchedule) request.getSession().getAttribute("schedule");
    	
    	schedule.setNotifyEmail(notifyEmail);
    	schedule.setScheduleName(scheduleName);
    	schedule.removeAllFormats();
    	schedule.removeAllSets();

    	if (selectedSetIds != null) {
    		if (selectedSetIds.length == 1 && selectedSetIds[0] ==0) {
    			// Remove all set and keep it empty. Removing because if any single set was added previously
    			schedule.setSets(new ArrayList<Set>());
    		} else {
		    	for (int setId:selectedSetIds) {
		    		Set set = setService.getSetById(setId);
		    		schedule.addSet(set);
		    	}
    		}
    	}

    	if (selectedFormatIds != null) {
	    	for (int formatId:selectedFormatIds) {
	    		Format format = formatService.getFormatById(formatId);
	    		schedule.addFormat(format);
	    	}
    	}

    	
    	// CHeck if a schedule with same name exist
    	HarvestSchedule otherSchedule = scheduleService.getScheduleByName(scheduleName);
    	
    	if (otherSchedule == null || otherSchedule.getId() == schedule.getId()) {


        	if (schedule.getId() > 0) {
    	    	// Add schedule
    	    	scheduleService.updateSchedule(schedule);
        	} else {
        		scheduleService.insertSchedule(schedule);
        	}

    	} else {
    		addFieldError("scheduleNameExist", "Schedule name already exists - " + scheduleName);
    		repository = schedule.getProvider();
    		errorType = "error";
    		return INPUT;
    	}

    	
    	
    	return SUCCESS;
    }
    
    /**
     * Add/update sets and formats for schedule
     *
     * @return
     * @throws DataException
     */
    public String addSetFormatForSchedule() throws DataException {

    	if (log.isDebugEnabled()) {
    		log.debug("AddSchedule::addSetFormatForSchedule():: scheduleId=" + scheduleId);
    	}

    	//schedule = scheduleService.getScheduleById(scheduleId);
    	schedule = (HarvestSchedule) request.getSession().getAttribute("schedule");
    	
    	schedule.setNotifyEmail(notifyEmail);
    	schedule.setScheduleName(scheduleName);
    	schedule.removeAllFormats();
    	schedule.removeAllSets();

    	if (selectedSetIds != null) {
    		if (selectedSetIds.length == 1 && selectedSetIds[0] ==0) {
    			// Remove all set and keep it empty. Removing because if any single set was added previously
    			schedule.setSets(new ArrayList<Set>());
    		} else {
		    	for (int setId:selectedSetIds) {
		    		Set set = setService.getSetById(setId);
		    		schedule.addSet(set);
		    	}
    		}
    	}

    	if (selectedFormatIds != null) {
	    	for (int formatId:selectedFormatIds) {
	    		Format format = formatService.getFormatById(formatId);
	    		schedule.addFormat(format);
	    	}
    	}

    	
    	// CHeck if a schedule with same name exist
    	HarvestSchedule otherSchedule = scheduleService.getScheduleByName(scheduleName);
    	
    	if (otherSchedule == null || otherSchedule.getId() == schedule.getId()) {

    		request.getSession().setAttribute("schedule", schedule);
        	
    		// To display step 1 page
        	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        	if (schedule.getEndDate() != null) {
        		endDateDisplayFormat = format.format(schedule.getEndDate());
        	}
        	startDateDisplayFormat = format.format(schedule.getStartDate());

        	repositories = providerService.getAllProviders();


    	} else {
    		addFieldError("scheduleNameExist", "Schedule name already exists - " + scheduleName);
    		repository = schedule.getProvider();
    		errorType = "error";
    		return INPUT;
    	}

    	return SUCCESS;

    }

    /**
     * To view the edit page for add schedule
     *
     * @return
     */
    public String viewEdit() {

    	if (log.isDebugEnabled()) {
    		log.debug("AddSchedule::viewEdit():: scheduleId=" + scheduleId);
    	}
    	
    	try {
	    	schedule = scheduleService.getScheduleById(scheduleId);
	
	    	if (schedule != null) {
	    		request.getSession().setAttribute("schedule", schedule);
	    		
		    	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		    	if (schedule.getEndDate() != null) {
		    		endDateDisplayFormat = format.format(schedule.getEndDate());
		    	}
		    	startDateDisplayFormat = format.format(schedule.getStartDate());
	
		    	repositories = providerService.getAllProviders();
	    	} else {
	    		addFieldError("scheduleNotExist", "Schedule does not exist.");
	    		errorType = "error";
	    		return INPUT;
	    	}
    	} catch (DatabaseConfigException dce) {
    		log.error(dce.getMessage(), dce);
    		errorType = "error";
    		addFieldError("dbError", "Error in displaying Edit schedule page.");
    		return INPUT;
    	}
    	
    	return SUCCESS;
    }

    /**
     * Get schedule
     *
     * @return
     */
	public HarvestSchedule getSchedule() {
		return schedule;
	}

	/**
	 * Set schedule
	 *
	 * @param schedule
	 */
	public void setSchedule(HarvestSchedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * Get repositories
	 *
	 * @return
	 */
	public List<Provider> getRepositories() {
		return repositories;
	}

	/**
	 * Set repository id
	 *
	 * @param repositoryId
	 */
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	/**
	 * Get repository
	 *
	 * @return
	 */
	public Provider getRepository() {
		return repository;
	}

	/**
	 * Get format ids
	 *
	 * @return
	 */
	public int[] getSelectedFormatIds() {
		return selectedFormatIds;
	}

	/**
	 * Set format ids
	 *
	 * @param selectedFormatIds Format ids selected
	 */
	public void setSelectedFormatIds(int[] selectedFormatIds) {
		this.selectedFormatIds = selectedFormatIds;
	}

	/**
	 * Set set ids
	 *
	 * @param selectedSetIds
	 */
	public void setSelectedSetIds(int[] selectedSetIds) {
		this.selectedSetIds = selectedSetIds;
	}

	/**
	 * Get schedule id
	 *
	 * @return
	 */
	public int getScheduleId() {
		return scheduleId;
	}

	/**
	 * Set schedule id
	 *
	 * @param scheduleId
	 */
	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	/**
	 * Get daily hour
	 *
	 * @return
	 */
	public int getDailyHour() {
		return dailyHour;
	}

	/**
	 * Set daily hour
	 *
	 * @param dailyHour
	 */
	public void setDailyHour(int dailyHour) {
		this.dailyHour = dailyHour;
	}

	/**
	 * Set notify email
	 *
	 * @param notifyEmail
	 */
	public void setNotifyEmail(String notifyEmail) {
		this.notifyEmail = notifyEmail;
	}

	/**
	 * Set schedule name
	 *
	 * @param scheduleName
	 */
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	/**
	 * Set recurrence type
	 *
	 * @param recurrence
	 */
	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}

	/**
	 * Set day of week
	 *
	 * @param dayOfWeek
	 */
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * Set hour
	 *
	 * @param hour
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * Set minute
	 *
	 * @param minute
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}

	/**
	 * Set start date
	 *
	 * @param startDate
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * Set end date
	 *
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEndDateDisplayFormat() {
		return endDateDisplayFormat;
	}

	public String getStartDateDisplayFormat() {
		return startDateDisplayFormat;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * Set the servlet request.
	 *
	 * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public int getDailyMinute() {
		return dailyMinute;
	}

	public void setDailyMinute(int dailyMinute) {
		this.dailyMinute = dailyMinute;
	}


}
