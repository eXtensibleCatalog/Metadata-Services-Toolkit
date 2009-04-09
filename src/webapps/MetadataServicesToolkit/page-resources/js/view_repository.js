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
    
	createDeleteRepositoryDialog : function()
	{
	
	alert('createDeleteRepositoryDialog');
	    
		// Define various event handlers for Dialog
		var handleSubmit = function() {
		alert('handleSubmit');
		    YAHOO.util.Connect.setForm('deleteRepository');
		    
		    //delete the user
	        var cObj = YAHOO.util.Connect.asyncRequest('post',
	        'confirmDeleteRepository.action', callback);
		};
		
			
		// handle a cancel of deleting user dialog
		var handleCancel = function() {
		    YAHOO.xc.mst.repository.deleteRepositoryDialog.hide();
		};
		
		var handleSuccess = function(o) {
		alert('handleSuccess');
		alert(o.responseText);
		    //get the response from adding a user
		    var response = eval("("+o.responseText+")");
		    alert(response);
		    
		    //if the user was not deleted then show the user the error message.
		    // received from the server
		    if( response.repositoryDeleted == "false" )
		    {
		        var deleteRepositoryError = document.getElementById('form_deleteRepositoryError');
	            deleteRepositoryError.innerHTML = '<p id="newDeleteRepositoryError">' 
	            + response.message + '</p>';
	            YAHOO.xc.mst.repository.deleteRepositoryDialog.showDialog();
		    }
		    else
		    {
		        // we can clear the form if the users were deleted
//		        YAHOO.xc.mst.repository.clearDeleteRepositoryForm();
		        YAHOO.xc.mst.repository.deleteRepositoryDialog.hide();
		    }
		    // reload the table
//		    YAHOO.xc.mst.repository.getRepositorys(0,1,1,'lastName','asc');
		};
		
		// handle form submission failure
		var handleFailure = function(o) {
		    alert('user submission failed ' + o.status);
		};
	
		// Instantiate the Dialog
		// make it modal - 
		// it should not start out as visible - it should not be shown until 
		// new user button is clicked.
		YAHOO.xc.mst.repository.deleteRepositoryDialog = new YAHOO.widget.Dialog('deleteRepositoryDialog', 
	        { width : "400px",
			  visible : false, 
			  modal : true,
			  buttons : [ { text:'Yes', handler:handleSubmit, isDefault:true },
						  { text:'No', handler:handleCancel } ]
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
	},
    


	/** 
	 * initialize the page 
	 */ 
	init : function() 
	{
	alert('init');
	    YAHOO.xc.mst.repository.createDeleteRepositoryDialog();
	}

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.repository.init);
