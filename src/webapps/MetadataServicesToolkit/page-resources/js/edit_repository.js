 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.repository");

YAHOO.xc.mst.repository = {

EditValidator : function()
         {
                   var returnvalue = false;
                   var rname = document.getElementById("repositoryName").value;
                   var rurl = document.getElementById("repositoryURL").value;

                   if(rname == "")
                       {
                           createErrorDiv("error","Repository Name cannot be empty");
                       }
                   else if(rurl == "")
                       {
                           createErrorDiv("error","Repository Name cannot be empty");
                       }
                   else
                       {
                           var arr = rurl.split("http://");
                          
                           if(arr[1]==null)
                               {
                                   createErrorDiv("error","Repository Name cannot be empty");
                               }
                           else
                               {
                                   
					/**
					 *  Dialog to show processing
					 */
				     
					// Instantiate the Dialog
					// make it modal - 
					// it should not start out as visible - it should not be shown until 
					// new repository button is clicked.
					YAHOO.xc.mst.repository.processingDialog = new YAHOO.widget.Dialog('processingDialog', 
					{ width : "400px",
						  visible : false, 
						  modal : true,

						} );


					     // Show the dialog
				       YAHOO.xc.mst.repository.processingDialog.showDialog = function()
				       {
					   YAHOO.xc.mst.repository.processingDialog.show();
					   YAHOO.xc.mst.repository.processingDialog.center();
				       },



					// Render the Dialog
					YAHOO.xc.mst.repository.processingDialog.render();


					YAHOO.xc.mst.repository.processingDialog.showDialog();
                                   	setTimeout("YAHOO.xc.mst.repository.submitForm()", 2000);
                                   
                               }
                           
                       }

        },
        
        submitForm : function() {
	    document.editRepositoryForm.submit();
	    return true;
        },
        
        cancel:function()
        {
            try
            {
                document.editRepositoryForm.action = 'allRepository.action';
                document.editRepositoryForm.submit();
            }
            catch(err)
            {
                alert(err);
            }
        },
        removeErrorMessage : function(repositoryId)
        {
            window.location = "viewEditRepository.action?RepositoryId="+repositoryId;
        }
        
}