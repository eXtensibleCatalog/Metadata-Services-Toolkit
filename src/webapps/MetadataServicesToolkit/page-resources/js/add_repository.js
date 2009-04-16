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

addValidator : function()
         {

             try
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

                           createErrorDiv("error","Repository URL cannot be empty");
                       }
                   else
                       {
                           var arr = rurl.split("http://");

                           if(arr[1]==null)
                               {
                                   createErrorDiv("error","Repository URL is invalid (Kindly ensure that protocol is specified eg:HTTP)");
                               }
                           else
                               {
                                   
				     document.getElementById('processing_bar').style.visibility = "visible";
				     setTimeout("YAHOO.xc.mst.repository.submitForm()", 3000);
				     
                               }
                           
                       }

             }


             catch(err)
             {
                 alert(err);
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
        },

	/** 
	 * initialize the page 
	 */ 
	init : function() 
	{
	    document.getElementById('processing_bar').style.visibility = "hidden";
	}

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.repository.init);
