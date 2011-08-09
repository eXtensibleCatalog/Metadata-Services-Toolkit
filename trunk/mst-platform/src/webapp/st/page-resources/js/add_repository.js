 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.repository.addRepository");

YAHOO.xc.mst.repository.addRepository = {

addValidator : function()
         {
		  
                   var rname = document.getElementById("repositoryName").value;
                   var rurl = document.getElementById("repositoryURL").value;

                   if(rname == "")
                       {

                           createErrorDiv("error","Repository Name cannot be empty");
                       }
                   else if(rurl == "")
                       {

                           createErrorDiv("error","Repository URL cannot be empty");
                       }
                   else
                       {
                           var arr = rurl.split("http://");
                           var arr2 = rurl.split("file://");

                           if(arr[1]==null && arr2[1]==null)
                               {
                                   createErrorDiv("error","URL should begin with a protocol such as http://");
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
					YAHOO.xc.mst.repository.addRepository.processingDialog = new YAHOO.widget.Dialog('processingDialog',
					{ width : "400px",
						  visible : false, 
						  modal : true

						} );


					     // Show the dialog
				       YAHOO.xc.mst.repository.addRepository.processingDialog.showDialog = function()
				       {
					   YAHOO.xc.mst.repository.addRepository.processingDialog.show();
					   YAHOO.xc.mst.repository.addRepository.processingDialog.center();
				       },



					// Render the Dialog
					YAHOO.xc.mst.repository.addRepository.processingDialog.render();


					YAHOO.xc.mst.repository.addRepository.processingDialog.showDialog();
							
		
				       setTimeout("YAHOO.xc.mst.repository.addRepository.submitForm()", 2000);
				     
                               }
                           
                       }



        },
        
        submitForm : function() {
	    document.addRepositoryForm.submit();
	    return true;
        },
        
        cancel:function()
        {
            document.addRepositoryForm.action = 'allRepository.action';
            document.addRepositoryForm.submit();
        },
        removeErrorMessage : function()
        {
            window.location = "viewAddRepository.action";            
        }


}

