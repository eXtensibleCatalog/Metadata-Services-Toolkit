 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.configuration.email");

YAHOO.xc.mst.configuration.email = {

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
             if(emailServerAddress=='')
                 {
                     createErrorDiv("error","Email Server Address is a required field");
                 }
             else if(fromAddress=='')
                 {
                     createErrorDiv("error","From Address is a required field");
                 }
             else if(password=='')
                 {
                     createErrorDiv("error","Password is a required field");
                 }
             else if(encryptedConnection=='')
                 {
                     createErrorDiv("error","Encrypted Connection is a required field");
                 }
             else
                 {
                     createErrorDiv("error","Port is a required field");
                 }

         }
         else
         {
            document.emailConfig.submit();
         }
     }
     catch(err)
     {
         alert(err.description);
     }
 },
  cancel : function()
    {
        document.emailConfig.action = "viewEmailConfig.action";
        document.emailConfig.submit();
    }
}
