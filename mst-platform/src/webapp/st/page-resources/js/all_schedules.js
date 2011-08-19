 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
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
   getAllSchedules : function()
   {
    YAHOO.xc.mst.schedule.view.refreshScheduleTable(document.getElementById('is_ascending_order').value, document.getElementById('column_sorted').value);

   },

  /**
   * Refresh scheule table
   */
   refreshScheduleTable : function(isAscendingOrder, columnToSort)
   {

    var handleSuccess = function(o) {

      if(o.responseText != null && o.responseText.search("SchedulePage") < 0 )
      {
        window.location = 'viewLogin.action';
      } else {
        //get the response from adding a repository
        document.getElementById('scheduleTable').innerHTML = o.responseText;
      }


    };

      // handle form submission failure
    var handleFailure = function(o) {
    };

         // Wire up the success and failure handlers
      var callback = { success: handleSuccess,  failure: handleFailure };

      //delete the repository
      var cObj = YAHOO.util.Connect.asyncRequest('get',
      'getSchedulesTable.action' + '?isAscendingOrder=' + isAscendingOrder + '&columnSorted=' + columnToSort + '&bustcache='+new Date().getTime(), callback);


    window.setTimeout('YAHOO.xc.mst.schedule.view.getAllSchedules()',2000);
   },


  /**
   * initialize the page
   */
  init : function()
  {
      YAHOO.xc.mst.schedule.view.createDeleteScheduleDialog();
      YAHOO.xc.mst.schedule.view.refreshScheduleTable('true', 'ScheduleName');

  }

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.schedule.view.init);
