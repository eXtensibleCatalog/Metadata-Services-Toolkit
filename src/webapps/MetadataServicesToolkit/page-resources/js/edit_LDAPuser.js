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
            var username = document.getElementById("userName").value;
            var fullname = document.getElementById("fullName").value;
            var groupsSelected = document.getElementById("groupsSelected");
            
            if((email=='')||(fullname=='')||(username==''))
                {
                    alert("Kindly fill all the fields before submitting the form");
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
                            alert("Groups cannot be empty");
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
