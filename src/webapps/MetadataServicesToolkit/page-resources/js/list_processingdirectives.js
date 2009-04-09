 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.directives.delete");

YAHOO.xc.mst.directives.delete = {

	/**
	 * Delete Processing Directive
	 *
	 */
	deleteProcessingDirective : function(procDirId)
	{	
	
		document.getElementById('processingDirective_id').value= procDirId;
		YAHOO.xc.mst.directives.delete.deleteProcessingDirectiveDialog.showDialog();
	
	},
	
	/**
	 *  Dialog to confirm Processing Directive delete
	 */
	createDeleteProcessingDirectiveDialog : function()
	{
		// Define various event handlers for Dialog
		var handleSubmit = function() {
			document.deleteProcessingDirective.submit();
		    YAHOO.xc.mst.directives.delete.deleteProcessingDirectiveDialog.hide();
		};
		
			
		// handle a cancel of deleting user dialog
		var handleCancel = function() {
		    YAHOO.xc.mst.directives.delete.deleteProcessingDirectiveDialog.hide();
		};
		
		// Instantiate the Dialog
		// make it modal - 
		// it should not start out as visible - it should not be shown until 
		// new user button is clicked.
		YAHOO.xc.mst.directives.delete.deleteProcessingDirectiveDialog = new YAHOO.widget.Dialog('deleteProcessingDirectiveDialog', 
	        { width : "400px",
			  visible : false, 
			  modal : true,
			  buttons : [ { text:'Yes', handler:handleSubmit },
						  { text:'No', handler:handleCancel, isDefault:true } ]
			} );
		
			
       // Show the dialog
       YAHOO.xc.mst.directives.delete.deleteProcessingDirectiveDialog.showDialog = function()
       {
           YAHOO.xc.mst.directives.delete.deleteProcessingDirectiveDialog.show();
           YAHOO.xc.mst.directives.delete.deleteProcessingDirectiveDialog.center();
       }


		// Render the Dialog
		YAHOO.xc.mst.directives.delete.deleteProcessingDirectiveDialog.render();



	},

	/** 
	 * initialize the page 
	 */ 
	init : function() 
	{
	    YAHOO.xc.mst.directives.delete.createDeleteProcessingDirectiveDialog();
	}

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.directives.delete.init);
