 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("mst.schedule.delete");

YAHOO.mst.schedule.delete = {


	/**
	 * Delete schedule
	 *
	 */
	deleteSchedule : function(scheduleId, scheduleName)
	{	
		document.getElementById('schedule_id').value= scheduleId;
		YAHOO.mst.schedule.delete.deleteScheduleDialog.showDialog();
		    
	},
	
	/**
	 *  Dialog to confirm schedule delete
	 */
	createDeleteScheduleDialog : function()
	{
		// Define various event handlers for Dialog
		var handleSubmit = function() {
			document.deleteSchedule.submit();
		    YAHOO.mst.schedule.delete.deleteScheduleDialog.hide();
		};
		
			
		// handle a cancel of deleting user dialog
		var handleCancel = function() {
		    YAHOO.mst.schedule.delete.deleteScheduleDialog.hide();
		};
		
		// Instantiate the Dialog
		// make it modal - 
		// it should not start out as visible - it should not be shown until 
		// new user button is clicked.
		YAHOO.mst.schedule.delete.deleteScheduleDialog = new YAHOO.widget.Dialog('deleteScheduleDialog', 
	        { width : "400px",
			  visible : false, 
			  modal : true,
			  buttons : [ { text:'Yes', handler:handleSubmit },
						  { text:'No', handler:handleCancel, isDefault:true } ]
			} );
		
			
	       // Show the dialog
	       YAHOO.mst.schedule.delete.deleteScheduleDialog.showDialog = function()
	       {
	           YAHOO.mst.schedule.delete.deleteScheduleDialog.show();
	           YAHOO.mst.schedule.delete.deleteScheduleDialog.center();
	       }


		// Render the Dialog
		YAHOO.mst.schedule.delete.deleteScheduleDialog.render();

	       // listener for showing the dialog when clicked.
		YAHOO.util.Event.addListener("confirmDeleteSchedule", "click", 
		    YAHOO.mst.schedule.delete.deleteScheduleDialog.showDialog, 
		    YAHOO.mst.schedule.delete.deleteScheduleDialog, true);

	},
	
	 	
	/**
	 * Refresh scheule table
	 */
	 refreshScheduleTable : function()
	 {

		var handleSuccess = function(o) {
			    //get the response from adding a repository
			    var response = o.responseText;
			    document.getElementById('scheduleTable').innerHTML = response;

		};

			// handle form submission failure
		var handleFailure = function(o) {
			    alert('All schedule failed ' );
		};

   	    // Wire up the success and failure handlers
	    var callback = { success: handleSuccess,  failure: handleFailure };	
		
	    //delete the repository
	    var cObj = YAHOO.util.Connect.asyncRequest('get',
			'getSchedulesTable.action', callback);

  			
		window.setTimeout('YAHOO.mst.schedule.delete.refreshScheduleTable()',2000);	
	 },
	 

	/** 
	 * initialize the page 
	 */ 
	init : function() 
	{
	    YAHOO.mst.schedule.delete.createDeleteScheduleDialog();
	    YAHOO.mst.schedule.delete.refreshScheduleTable();
	    
	}

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.mst.schedule.delete.init);
