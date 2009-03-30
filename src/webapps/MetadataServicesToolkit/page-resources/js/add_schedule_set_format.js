 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.schedule.add");

YAHOO.xc.mst.schedule.add = {
	
	gotoPreviousStep : function()
	{
		document.addScheduleForm.action= basePath + 'saveSetFormatForScheduleAndGoToPrevious.action';
		document.addScheduleForm.submit();
	
	},
	
	cancel : function()
	{
		document.addScheduleForm.action= basePath + 'allSchedules.action';
		document.addScheduleForm.submit();
	
	},	
	
	saveAndExit : function()
	{
	
		if (document.addScheduleForm.scheduleName.value=='') {
			createErrorDiv('error','Schedule Name is required.');
			return false;
		}

		if (document.addScheduleForm.selectedFormatIds.value=='') {
			createErrorDiv('error','Format is required.');
			return false;
		}
		
		document.addScheduleForm.action= basePath + 'saveSetFormatForScheduleAndExit.action';
		document.addScheduleForm.submit();
	
	},
 
	 selectAll : function(field, currentField)
	 {


	    if (currentField.checked) {

		 for (i=0;i<field.length;i++) 
		{
		    field[i].checked=true;
		}
	    } else {
		 for (i=0;i<field.length;i++) 
		{
		    field[i].checked=false;
		}
	    }


	 }
} 

