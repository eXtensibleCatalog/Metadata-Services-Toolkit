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

AddValidator : function()
         {
             try
             {
                   var returnvalue = false;
                   var rname = document.getElementById("repositoryName").value;
                   var rurl = document.getElementById("repositoryURL").value;

                   if(rname == "")
                       {

                           alert("Repository Name cannot be empty");
                           //return false;
                       }
                   else if(rurl == "")
                       {

                           alert("Repository URL cannot be empty");
                           //return false;
                       }
                   else
                       {
                           var arr = rurl.split("http://");

                           if(arr[1]==null)
                               {
                                   alert("Repository URL is invalid (Kindly ensure that protocol is specified eg:HTTP)");
                                   //return false;
                               }
                           else
                               {
                                   document.addRepositoryForm.submit();
                               }
                           //return true;
                       }
             }


             catch(err)
             {
                 alert(err);
             }

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