 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
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
		document.addScheduleForm.action=  'saveSetFormatForScheduleAndGoToPrevious.action';
		document.addScheduleForm.submit();
	
	},
	
	gotoPreviousUpdateStep : function()
	{
		document.addScheduleForm.action=  'saveSetFormatForScheduleAndGoToPreviousUpdate.action';
		document.addScheduleForm.submit();
	
	},
	
	cancel : function()
	{
		document.addScheduleForm.action=  'allSchedules.action';
		document.addScheduleForm.submit();
	
	},	
	
	saveAndExit : function()
	{
	
		if (document.addScheduleForm.scheduleName.value=='') {
			createErrorDiv('error','Schedule Name is required.');
			return false;
		}


		if (document.addScheduleForm.selectedFormatIds.value=='') {
			createErrorDiv('error','Select a format.');
			return false;
		}
		
		if (document.addScheduleForm.selectedSetIds.value=='') {
			createErrorDiv('error','Select a set.');
			return false;
		}
				
		document.addScheduleForm.action=  'saveSetFormatForScheduleAndExit.action';
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

