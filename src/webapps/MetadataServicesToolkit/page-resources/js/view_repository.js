YAHOO.namespace("xc.mst.repository");

YAHOO.xc.mst.repository = {

    delRepository : function(id)
    {
        
        var result;
        result = confirm('Are you sure you want to delete the repository ? ');
        if (result)
        {
            window.location = "deleteRepository.action?RepositoryId="+id;
        }

    },
    doneFunction:function()
    {
        window.location = "allRepository.action";
    },
    reValidateFunction : function(id)
    {
        window.location = "viewRepositoryValidate.action?RepositoryId="+id;
    },
    editFunction : function(id)
    {
        window.location = "viewEditRepository.action?RepositoryId="+id;
    },
    removeErrorMessage : function(id)
    {
        window.location = "viewRepository.action?RepositoryId="+id;
    },
    downloadFile: function(type,id)
    {
        window.location = "pages/logs/downloadLogFile.jsp?logType="+type+"&id="+id;
    },
    
	
	/**
	 *  Dialog to confirm repository delete
	 */
	createDeleteRepositoryDialog : function()
	{
	
	
		// Define various event handlers for Dialog
		var handleSubmit = function() {
		    YAHOO.util.Connect.setForm('deleteRepository');
		    
		    //delete the repository
		    var cObj = YAHOO.util.Connect.asyncRequest('post',
			        'deleteRepository.action', callback);
		};
		
			
		// handle a cancel of deleting repository dialog
		var handleCancel = function() {
		    YAHOO.xc.mst.repository.deleteRepositoryDialog.hide();
		};
		
		// handle a Yes of deleting repository dialog
		var handleYes = function() {
			document.deleteRepositoryRecords.submit();
		    YAHOO.xc.mst.repository.deleteRepositoryOkDialog.hide();
		};

		// handle a No of deleting repository dialog
		var handleNo = function() {
		    YAHOO.xc.mst.repository.deleteRepositoryOkDialog.hide();
		};		
		
		var handleSuccess = function(o) {
		
		    //get the response from adding a repository
		    var response = eval("("+o.responseText+")");
		    
		    
		    //if the repository was not deleted then show the repository the error message.
		    // received from the server
		    if( response.repositoryDeleted == "false" )
		    {
		    	YAHOO.xc.mst.repository.deleteRepositoryDialog.hide();
		        var deleteRepositoryError = document.getElementById('deleteRepositoryError');
			             deleteRepositoryError.innerHTML = '<p id="newDeleteRepositoryError">' 
 			            + response.message + '</p>';
 		            YAHOO.xc.mst.repository.deleteRepositoryOkDialog.showDialog();
 		            
		    }
		    else
		    {
		        // we can clear the form if the repositorys were deleted
		        YAHOO.xc.mst.repository.deleteRepositoryDialog.hide();
		        window.location = 'allRepository.action?isAscendingOrder=true&amp;columnSorted=RepositoryName';
		    }
		};
		
		// handle form submission failure
		var handleFailure = function(o) {
		    alert('repository submission failed ' + o.status);
		};
		
		
	
		// Instantiate the Dialog
		// make it modal - 
		// it should not start out as visible - it should not be shown until 
		// new repository button is clicked.
		YAHOO.xc.mst.repository.deleteRepositoryDialog = new YAHOO.widget.Dialog('deleteRepositoryDialog', 
	        { width : "400px",
			  visible : false, 
			  modal : true,
			  buttons : [ { text:'Yes', handler:handleSubmit },
						  { text:'No', handler:handleCancel, isDefault:true } ]
			} );
		
			
	       // Show the dialog
	       YAHOO.xc.mst.repository.deleteRepositoryDialog.showDialog = function()
	       {
	           YAHOO.xc.mst.repository.deleteRepositoryDialog.show();
	           YAHOO.xc.mst.repository.deleteRepositoryDialog.center();
	       }

		
	    	   
		// Wire up the success and failure handlers
		var callback = { success: handleSuccess,  failure: handleFailure };


		// Render the Dialog
		YAHOO.xc.mst.repository.deleteRepositoryDialog.render();

	       // listener for showing the dialog when clicked.
		YAHOO.util.Event.addListener("confirmDeleteRepository", "click", 
		    YAHOO.xc.mst.repository.deleteRepositoryDialog.showDialog, 
		    YAHOO.xc.mst.repository.deleteRepositoryDialog, true);

		
		YAHOO.xc.mst.repository.deleteRepositoryOkDialog = new YAHOO.widget.Dialog('deleteRepositoryOkDialog', 
		{ width : "400px",
			  visible : false, 
			  modal : true,
			  buttons : [ { text:'Yes', handler:handleYes},
			  				 { text:'No', handler:handleNo, isDefault:true }]
		} );	

	       // Show the dialog with error message
	       YAHOO.xc.mst.repository.deleteRepositoryOkDialog.showDialog = function()
	       {
	        	YAHOO.xc.mst.repository.deleteRepositoryOkDialog.show();
	        	YAHOO.xc.mst.repository.deleteRepositoryOkDialog.center();
	       }

		// Render the Dialog
		YAHOO.xc.mst.repository.deleteRepositoryOkDialog.render();

	},
    


	/** 
	 * initialize the page 
	 */ 
	init : function() 
	{
	    YAHOO.xc.mst.repository.createDeleteRepositoryDialog();
	}

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.repository.init);
