 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
 YAHOO.namespace("xc.mst.schedule.view");

YAHOO.xc.mst.schedule.view = {
	/**
	 * Delete schedule
	 *
	 */
	deleteSchedule : function(scheduleId, scheduleName)
	{	
		document.getElementById('schedule_id').value= scheduleId;
		YAHOO.xc.mst.schedule.view.deleteScheduleDialog.showDialog();
		    
	},
	
	/**
	 *  Dialog to confirm schedule delete
	 */
	createDeleteScheduleDialog : function()
	{
		// Define various event handlers for Dialog
		var handleSubmit = function() {
		    document.deleteSchedule.submit();
		    YAHOO.xc.mst.schedule.view.deleteScheduleDialog.hide();
		};
		
			
		// handle a cancel of deleting user dialog
		var handleCancel = function() {
		    YAHOO.xc.mst.schedule.view.deleteScheduleDialog.hide();
		};
		
		// Instantiate the Dialog
		// make it modal - 
		// it should not start out as visible - it should not be shown until 
		// new user button is clicked.
		YAHOO.xc.mst.schedule.view.deleteScheduleDialog = new YAHOO.widget.Dialog('deleteScheduleDialog', 
	        { width : "400px",
			  visible : false, 
			  modal : true,
			  buttons : [ { text:'Yes', handler:handleSubmit },
						  { text:'No', handler:handleCancel, isDefault:true } ]
			} );
		
			
	       // Show the dialog
	       YAHOO.xc.mst.schedule.view.deleteScheduleDialog.showDialog = function()
	       {
	           YAHOO.xc.mst.schedule.view.deleteScheduleDialog.show();
	           YAHOO.xc.mst.schedule.view.deleteScheduleDialog.center();
	       }


		// Render the Dialog
		YAHOO.xc.mst.schedule.view.deleteScheduleDialog.render();

	    

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

  			
		window.setTimeout('YAHOO.xc.mst.schedule.view.refreshScheduleTable()',2000);	
	 },
	 

	/** 
	 * initialize the page 
	 */ 
	init : function() 
	{
	    YAHOO.xc.mst.schedule.view.createDeleteScheduleDialog();
	    YAHOO.xc.mst.schedule.view.refreshScheduleTable();
	    
	}

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.schedule.view.init);
