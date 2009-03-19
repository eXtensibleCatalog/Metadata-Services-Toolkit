 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.password");



YAHOO.xc.mst.password = 
{
   /** 
     * clear out any form data messages or input
     * in the forgot password form
     */
    clearForgotPasswordForm : function()
    {
	    document.getElementById('forgotPasswordForm_email').value = "";
    },
    
    /**
     * Creates a YUI forgot password modal dialog 
     */
    createForgotPasswordDialog : function()
    {
    
	    // Define various event handlers for Dialog
	    var handleSubmit = function() {

	        YAHOO.util.Connect.setForm('forgotPasswordForm');
	    	    
	        if( YAHOO.xc.mst.password.forgotPasswordDialog.validate() )
	        {
                var cObj = YAHOO.util.Connect.asyncRequest('post',
                basePath + 'resetPassword.action', callback);
            }
	    };
	
		
	    // handle a cancel of the forgot password dialog
	    var handleCancel = function() 
	    {
	        YAHOO.xc.mst.password.clearForgotPasswordForm();
	        YAHOO.xc.mst.password.forgotPasswordDialog.hide();
	    };
	
	    var handleSuccess = function(o) 
	    {
	        //get the response from adding a password token
	        var response = o.responseText;
	        var forgotPasswordForm = document.getElementById('forgotPasswordDialogFields');
	    
	        // update the form fields with the response.  This updates
	        // the form, if there was an issue, update the form with
	        // the error messages.
	        forgotPasswordForm.innerHTML = o.responseText;
	    
	        // determine if the add/edit was a success
	        var success = document.getElementById("forgotPasswordForm_success").value;
	        var message = document.getElementById("forgotPasswordForm_message").value;
	    
	  
	        //if the password token not added then show the user the error message.
	        // received from the server
	        if( success == "false" )
	        {
	            var emailError = document.getElementById('emailError');
                emailError.innerHTML = '<p id="forgotPasswordForm_emailError">' + message + '</p>';
   
                YAHOO.xc.mst.password.forgotPasswordDialog.showDialog();
	        }
	        else
	        {
	            // we can clear the form if the password token was added
	            YAHOO.xc.mst.password.clearForgotPasswordForm();
	            YAHOO.xc.mst.password.forgotPasswordDialog.hide();
	        }
	    };
	
	    // handle form sbumission failure
	    var handleFailure = function(o)
	    {
	        alert('Email submission failed ' + o.status);
	    };

	    // Instantiate the Dialog
	    // make it modal - 
	    // it should not start out as visible - it should not be shown until 
	    // forgot password link is clicked.
	    YAHOO.xc.mst.password.forgotPasswordDialog = new YAHOO.widget.Dialog('forgotPasswordDialog', 
        { width : "500px",
		  visible : false, 
		  buttons : [ { text:'Submit', handler:handleSubmit, isDefault:true },
					  { text:'Cancel', handler:handleCancel } ]
		} );
		
		/**
		 *  show and center the dialog
		 */
		YAHOO.xc.mst.password.forgotPasswordDialog.showDialog = function()
		{
		    YAHOO.xc.mst.password.forgotPasswordDialog.center();
		    YAHOO.xc.mst.password.forgotPasswordDialog.show();
		}
   
 	    // Validate the email id
	    YAHOO.xc.mst.password.forgotPasswordDialog.validate = function() {
	        var email = document.getElementById('forgotPasswordForm_email').value;
		    if (email == "" || email == null) 
		    {
		        alert('Email Id must be entered');
			    return false;
		    } 
		    else 
		    {
			    if (!urUtil.emailcheck(email)) 
			    {
				    alert('Invalid email address');
				    return false;
			    }
			    return true;
		    }
	    };

	    // Wire up the success and failure handlers
	    var callback = { success: handleSuccess, failure: handleFailure };
			
	    // Render the Dialog
	    YAHOO.xc.mst.password.forgotPasswordDialog.render();
    },
    
    /**
     * Forgot the password
     */
    forgotPassword : function()
    {
	        YAHOO.xc.mst.password.forgotPasswordDialog.showDialog();
    },
    
    init : function()
    {
        YAHOO.xc.mst.password.createForgotPasswordDialog();
    }
}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady( YAHOO.xc.mst.password.init); 

