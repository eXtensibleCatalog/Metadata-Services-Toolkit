 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.configuration.LDAPServer");

YAHOO.xc.mst.configuration.LDAPServer.MyObject = {

  converter :function(c)
    {
        
        // restrict input to a single character
        c = c . charAt (0);

        // loop through all possible ASCII values
        var i;
        for (i = 0; i < 256; ++ i)
        {
            // convert i into a 2-digit hex string
            var h = i . toString (16);
            if (h . length == 1)
                h = "0" + h;

            // insert a % character into the string
            h = "%" + h;

            // determine the character represented by the escape code
            h = unescape(h);

            // if the characters match, we've found the ASCII value
            if (h == c)
                break;
        }
        return i;
    },
    
     addLDAP : function()
     {

         try
         {
             var displayName = document.getElementById("displayName").value;
             var serverURL = document.getElementById("serverURL").value;
             var userNameAttribute = document.getElementById("userNameAttribute").value;
             var startLocation = document.getElementById("startLocation").value;
             var port = document.getElementById("port").value;
             if((displayName=='')||(serverURL=='')||(userNameAttribute=='')||(startLocation=='')||(port==''))
             {
                if(displayName=='')
                    {
                         createErrorDiv("error","Display name is a required field");
                    }
                else if(serverURL=='')
                    {
                        createErrorDiv("error","Server URL is a required field");
                    }
                else if(userNameAttribute=='')
                    {
                        createErrorDiv("error","UserName Attribute is a required field");
                    }
                else if(startLocation=='')
                    {
                        createErrorDiv("error","Start Location is a required field");
                    }
                else
                    {
                        createErrorDiv("error","Port is a required field");
                    }
               
             }
             else
             {
             
                if (document.getElementById("showForgotPasswordLink").checked) {
                	 if (document.getElementById("forgotPasswordUrl").value == '') {
                	 	createErrorDiv("error","Forgot password URL is required.");
                	 	return false;
                	 }
                }
                 var arr = serverURL.split("://");
                

                 if(arr[1]==null)
                     {
                         createErrorDiv("error","Repository URL is invalid (Kindly ensure that protocol is specified eg:HTTP)");
                     }
                 else
                     {
                        var flag = true;
                       
                        for(var i=0;i<port.length;i++)
                            {
                               

                                var j;
                                j = YAHOO.xc.mst.configuration.MyObject.converter(port[i]);
                               
                                if((j<48)||(j>57))
                                    {
                                        flag = false;
                                        break;
                                    }
                            }
                        if(flag==true)
                            {
                               
                                document.addLDAP.submit();
                            }
                        else
                            {
                               
                                createErrorDiv("error","Port Number should be an integer value");
                            }
                     }
             }
         }
         catch(err)
         {
             alert(err);
         }
     },

    cancel: function()
    {
        document.addLDAP.action = "allLDAP.action";
        document.addLDAP.submit();
    },
    deleteLDAP : function()
     {

         try
         {
             var result;
             result = confirm('Are you sure you want to delete the LDAP Server ?');
             if(result)
                 {
                    window.location = "deleteLDAP.action";
                 }
         }
         catch(err)
         {
             alert(err);
         }
     }
}

