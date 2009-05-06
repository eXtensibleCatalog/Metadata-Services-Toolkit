 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.user");

YAHOO.xc.mst.user = {
    editLDAPUser : function()
    {
        try
        {
           
            var email = document.getElementById("email").value;
            var firstname = document.getElementById("firstName").value;
            var lastname = document.getElementById("lastName").value;
            var groupsSelected = document.getElementById("groupsSelected");
            
            if((email=='')||(firstname=='')||(lastname==''))
                {
                    if(email=='')
                        {
                            createErrorDiv("error","Email is a required field");
                        }
                    else if(firstname=='')
                        {
                            createErrorDiv("error","First name is a required field");
                        }
                    else
                        {
                            createErrorDiv("error","Last name is a required field");
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
                            document.editLDAPUser.submit();
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
        document.editLDAPUser.action = "allUsers.action";
        document.editLDAPUser.submit();
    }
}
