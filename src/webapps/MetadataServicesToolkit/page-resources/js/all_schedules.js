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
		var result;
		result = confirm('Are you sure you want to delete the Schedule - ' + scheduleName + ' ?');
		if (result) {
			document.getElementById('schedule_id').value= scheduleId;
			document.deleteScheduleForm.submit();
		}		
		    
	}
} 
