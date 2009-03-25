 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.schedule");

YAHOO.xc.mst.schedule = {

	saveSchedule : function()
	{

		if (document.scheduleForm.scheduleName.value=='') {
			createErrorDiv('error','Schedule Name is required.');
			return false;
		}
		if (document.scheduleForm.repositoryId.value=='0') {
						
			createErrorDiv('error','Please select a Repository');		
			return false;
		}

		var selectedRecurrence = '';
		
		for (var i=0; i<document.scheduleForm.recurrence.length; i++)  {
			if (document.scheduleForm.recurrence[i].checked)  {

			selectedRecurrence = document.scheduleForm.recurrence[i].value

			}
		} 	

		if (selectedRecurrence == '') {
			createErrorDiv('error','Recurrence for Schedule is required.');
			return false;
		} else {
			if (selectedRecurrence=='Daily') {
				if (document.scheduleForm.dailyHour.value=='-1') {
					createErrorDiv('error','Time to harvest the repository is required.');
					return false;
				}
			}
			if (selectedRecurrence=='Hourly') {
				if (document.scheduleForm.minute.value=='-1') {
					createErrorDiv('error','Minute to harvest the repository every hour is required.');
					return false;
				}
			}
			if (selectedRecurrence=='Weekly') {
				if (document.scheduleForm.dayOfWeek.value=='0') {
					createErrorDiv('error','Select a Day of week to havest the repository.');
					return false;
				}
				if (document.scheduleForm.hour.value=='-1') {
					createErrorDiv('error','Time to harvest the repository is required.');
					return false;
				}				
			}			
			
		
		}
		
		var startDate = document.scheduleForm.startDate.value;
		var endDate = document.scheduleForm.endDate.value;
		if (startDate=='') {
			createErrorDiv('error','Start date is required.');
			return false;
		}
		if (endDate=='') {
			createErrorDiv('error','End date is required.');
			return false;
		}
		
		
		var eDate = new Date(endDate);
		var sDate = new Date(startDate);
		
		
		if (sDate > eDate) {
			createErrorDiv('error','End Date should be greater than Start date.');
			return false;
		}
		
		if (document.getElementById('schedule_id').value == -1) {
			document.scheduleForm.action= basePath + 'addScheduleAndNext.action';
		} else {
			document.scheduleForm.action= basePath + 'updateScheduleAndNext.action';
		}
		document.scheduleForm.submit();
	
	},

	allSchedules : function()
	{
		document.scheduleForm.action= basePath + 'allSchedules.action';
		document.scheduleForm.submit();
	
	},
	
	/*
	 * Show the calender
	 */
	showStartDateCalendar : function()
	{
	
		YAHOO.xc.mst.schedule.cal1 = new YAHOO.widget.Calendar("cal1Container",{
	            iframe:false,          // Turn iframe off, since container has iframe support.
	            hide_blank_weeks:true  // Enable, to demonstrate how we handle changing height, using changeContent
	        });
		 
		var handleSelect = function(type,args,obj) {
		
			var dates = args[0];
			var date = dates[0];
			var year = date[0], month = date[1], day = date[2];
	
			document.getElementById("schedule_start_date").value = month + '/' + day + '/' + year;
		}
	
		var dialog = new YAHOO.widget.Dialog("containerDialog", {
	            context:["show", "tl", "bl"],
	           
	            width:"250px",  // Sam Skin dialog needs to have a width defined (7*2em + 2*1em = 16em).
	           
	            draggable:false,
	            close:true
	        });
	        
	   	
		YAHOO.xc.mst.schedule.cal1.render();
		
		dialog.render();
		
		// Using dialog.hide() instead of visible:false is a workaround for an IE6/7 container known issue with border-collapse:collapse.
	    dialog.hide();
	
		YAHOO.xc.mst.schedule.cal1.selectEvent.subscribe(handleSelect, YAHOO.xc.mst.schedule.cal1, true);
		
		YAHOO.util.Event.on("show_start_date_calendar", "click", dialog.show, dialog, true);
	
	},
	
	showEndDateCalendar : function()
	{
	
		YAHOO.xc.mst.schedule.cal2 = new YAHOO.widget.Calendar("cal2Container",{
	            iframe:false,          // Turn iframe off, since container has iframe support.
	            hide_blank_weeks:true  // Enable, to demonstrate how we handle changing height, using changeContent
	        });
		 
		var handleSelect = function(type,args,obj) {
		
			var dates = args[0];
			var date = dates[0];
			var year = date[0], month = date[1], day = date[2];
	
			document.getElementById("schedule_end_date").value = month + '/' + day + '/' + year;
		}
	
		var dialog = new YAHOO.widget.Dialog("containerDialog2", {
	            context:["show", "tl", "bl"],
	           
	            width:"250px",  // Sam Skin dialog needs to have a width defined (7*2em + 2*1em = 16em).
	           
	            draggable:false,
	            close:true
	        });
	        
	   	
		YAHOO.xc.mst.schedule.cal2.render();
		
		dialog.render();
		
		// Using dialog.hide() instead of visible:false is a workaround for an IE6/7 container known issue with border-collapse:collapse.
	    dialog.hide();
	
		YAHOO.xc.mst.schedule.cal2.selectEvent.subscribe(handleSelect, YAHOO.xc.mst.schedule.cal2, true);
		
		YAHOO.util.Event.on("show_end_date_calendar", "click", dialog.show, dialog, true);
	
	},	

	// initialize the page
	// this is called once the dom has
	// been created
	init : function() 
	{
	   YAHOO.xc.mst.schedule.showStartDateCalendar();
	   YAHOO.xc.mst.schedule.showEndDateCalendar();
	    
	
	}
} 

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.schedule.init);	