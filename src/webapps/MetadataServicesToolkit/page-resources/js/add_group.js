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
    addGroup : function()
    {
        try
        {
            
            var groupName = document.getElementById("groupName").value;
            var groupDescription = document.getElementById("groupDescription").value;
           

            var permissionsSelected = document.getElementById("permissionsSelected");


            if((groupName=='')||(groupDescription==''))
                {
                    createErrorDiv("error","Kindly fill all the fields before submitting the form");
                }

            else
             {
                 var flag = 0;
                 for(i=0;i<permissionsSelected.options.length;i++)
                     {

                         if(permissionsSelected.options[i].selected==true)
                             {

                                 flag=1;
                             }
                     }
                 if(flag==1)
                     {
                         
                             document.addGroup.submit();
                        
                     }
                  else
                      {
                          createErrorDiv("error",'groups cannot be empty');
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
        document.addGroup.action = "allGroups.action";
        document.addGroup.submit();
    }
}
