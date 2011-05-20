 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.directives.delete");

YAHOO.xc.mst.directives.deleteDirective = {

	/**
	 * Delete Processing Directive
	 *
	 */
	deleteProcessingDirective : function(procDirId)
	{	
		document.getElementById('processingDirective_id').value= procDirId;
		YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog.showDialog();
	},
	
	/**
	 *  Dialog to confirm Processing Directive delete
	 */
	createDeleteProcessingDirectiveDialog : function()
	{
		// check to see if rule was deleted
		var handleSuccess = function(o) {

			//get the response from deleting a rule
			var response = eval("("+o.responseText+")");

			//if the rule was not deleted then show the error message.
			// received from the server
			if( response.processingRuleDeleted == "false" )
			{
				YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog.hide();
				var deleteProcessingRuleError = document.getElementById('deleteProcessingRuleError'); // list_processingdirectives.jsp
				deleteProcessingRuleError.innerHTML = '<p id="newDeleteProcessingRuleError">'
					+ response.message + '</p>';
				YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveNakDialog.showDialog();
			}
			else {
				// redirect
				window.location='listProcessingDirectives.action';
			}
		};

		var handleFailure = function(o) {
			alert('Delete processing rule failed ' + o.status);
		};

		// Wire up the success and failure handlers
		var callback = { success: handleSuccess,  failure: handleFailure };

	        // for Nak dialog	
		var handleYes = function() {
			YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveNakDialog.hide();
		};		

		// Define various event handlers for Dialog
		var handleSubmit = function() {
		    YAHOO.util.Connect.setForm('deleteProcessingDirective');
		    
		    //delete the PR
		    var cObj = YAHOO.util.Connect.asyncRequest('post',
			        'deleteProcessingDirective.action', callback);
		    YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog.hide();
		};
		
			
		// handle a cancel of deleting user dialog
		var handleCancel = function() {
		    YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog.hide();
		};

		
		// Instantiate the Dialog
		// make it modal - 
		// it should not start out as visible - it should not be shown until 
		// delete processing rule is clicked.
		YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog = new YAHOO.widget.Dialog('deleteProcessingDirectiveDialog',
		{ width : "400px",
			visible : false, 
			modal : true,
			buttons : [ { text:'Yes', handler:handleSubmit },
						{ text:'No', handler:handleCancel, isDefault:true } ]
		} );


		// Show the dialog
		YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog.showDialog = function()
		{
			YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog.show();
			YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog.center();
		};


		// new dialog
		YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveNakDialog = new YAHOO.widget.Dialog('deleteProcessingDirectiveNakDialog', 
		{ width : "400px",
			  visible : false, 
			  modal : true,
			  buttons : [ { text:'OK', handler:handleYes}]
		} );	

	       // Show the dialog with error message
	       YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveNakDialog.showDialog = function()
	       {
			YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveNakDialog.show();
			YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveNakDialog.center();
	       };


		// Render the Dialog
		YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveDialog.render();

		// Render the Dialog
		YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirectiveNakDialog.render();
	},

	/** 
	* initialize the page 
	*/ 
	init : function() 
	{
		YAHOO.xc.mst.directives.deleteDirective.createDeleteProcessingDirectiveDialog();
	}
};

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.directives.deleteDirective.init);
