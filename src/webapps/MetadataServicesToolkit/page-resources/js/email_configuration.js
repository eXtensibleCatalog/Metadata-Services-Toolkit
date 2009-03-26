 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.configuration");

YAHOO.xc.mst.configuration = {

 changeEmailConfig : function()
 {

     try
     {
         var emailServerAddress = document.getElementById("emailServerAddress").value;
         var fromAddress = document.getElementById("fromAddress").value;
         var password = document.getElementById("password").value;
         var encryptedConnection = document.getElementById("encryptedConnection").value;
         var port = document.getElementById("port").value;
         if((emailServerAddress=='')||(fromAddress=='')||(password=='')||(encryptedConnection=='')||(port==''))
         {
            createErrorDiv("error","Kindly fill in all the details before submitting the form");
         }
         else
         {
            document.emailConfig.submit();
         }
     }
     catch(err)
     {
         alert(err);
     }
 },
  cancel : function()
    {
        document.emailConfig.action = "viewEmailConfig.action";
        document.emailConfig.submit();
    }
}