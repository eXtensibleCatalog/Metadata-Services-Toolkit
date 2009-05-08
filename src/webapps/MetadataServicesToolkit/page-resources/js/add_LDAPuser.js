 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.user.addLDAP");

YAHOO.xc.mst.user.addLDAP = {
    addLDAPUser : function()
    {
        try
        {
           
            var email = document.getElementById("email").value;
            var username = document.getElementById("userName").value;
            var firstname = document.getElementById("firstName").value;
            var lastname = document.getElementById("lastName").value;
            var groupsSelected = document.getElementById("groupsSelected");
            if((email=='')||(firstname=='')||(lastname=='')||(username==''))
                {
                    if(email=='')
                        {
                            createErrorDiv("error","Email is a required Field");
                        }
                    else if(firstname=='')
                        {
                            createErrorDiv("error","First Name is a required Field");
                        }
                    else if(lastname=='')
                        {
                            createErrorDiv("error","Last Name is a required Field");
                        }
                    else
                        {
                            createErrorDiv("error","Username is a required Field");
                        }
                }
            else
             {
                     var flag = 0;
                     for(i=0;i<groupsSelected.options.length;i++)
                     {

                         if(groupsSelected.options[i].selected==true)
                             {
                                 flag=1;
                             }
                     }
                    
                    if(flag==1)
                        {
                            document.addLDAPUser.submit();
                        }
                    else
                        {
                            createErrorDiv("error","Groups cannot be empty");
                        }

             }

        }
        catch(err)
        {
            alert(err);
        }
    },
    cancel : function()
    {
        document.addLDAPUser.action = "allUsers.action";
        document.addLDAPUser.submit();
    }
}
