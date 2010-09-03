 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.user.editLocal");

YAHOO.xc.mst.user.editLocal = {
    editValidate : function()
    {
   
            var email = document.getElementById("email").value;
            var pass = document.getElementById("password").value;
            var confpass = document.getElementById("confpass").value;
            var firstname = document.getElementById("firstName").value;
            var lastname = document.getElementById("lastName").value;

            var groupsSelected = document.getElementById("groupsSelected");

			if (document.getElementById('password').value.length > 0) 
			{
				if (document.getElementById('password').value!=document.getElementById('confpass').value) {
					createErrorDiv("error",'Password does not match confirmation password.');
					return false;
				}
				
				if (document.getElementById('password').value.length < 6 || document.getElementById('password').value.length > 20) {
				    createErrorDiv("error",'Password should be 6 to 20 characters long');
					return false;
				}
						
				var alphaExp = /^[0-9a-zA-Z]+$/;
				
				var validChar = '*,(,),_,#,@';
				var i = 0;
				for(i = 0; i < document.getElementById('password').value.length; i++) {
			
					if(document.getElementById('password').value.charAt(i).match(alphaExp)){

					}else{
						if  (validChar.indexOf(document.getElementById('password').value.charAt(i)) < 0 ) {
						    createErrorDiv("error",'Invalid character in password. Only A-Z, a-z, 0-9 , *, @, (,), _,# are allowed.');
							return false;
						}
					}
				}
			}
			
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
                         if(pass==confpass)
                         {
                            
                             document.editLocalUser.submit();
                         }
                         else
                         {
                             createErrorDiv("error",'Passwords do not match');
                         }
                     }
                  else
                      {
                          createErrorDiv("error",'Group is required.');
                      }
             }
            
},    
    editcancel : function()
    {
        document.editLocalUser.action = "allUsers.action";
        document.editLocalUser.submit();
    }
}
