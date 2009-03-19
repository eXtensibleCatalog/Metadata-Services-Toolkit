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
    EditValidate : function()
    {
        try
        {
             
            var email = document.getElementById("email").value;
            var pass = document.getElementById("password").value;
            var confpass = document.getElementById("confpass").value;
            var fullname = document.getElementById("fullName").value;

            var groupsSelected = document.getElementById("groupsSelected");

			if (document.getElementById('password').value.length < 11) 
			{
				if (document.getElementById('password').value!=document.getElementById('confpass').value) {
					alert('Password does not match confirmation password.');
					return false;
				}
				
				if (document.getElementById('password').value.length < 6 || document.getElementById('password').value.length > 10) {
					alert('Password should be 6 to 10 characters long');
					return false;
				}
						
				var alphaExp = /^[0-9a-zA-Z]+$/;
				
				var validChar = '*,(,),_,#,@';
				var i = 0;
				for(i = 0; i < document.getElementById('password').value.length; i++) {
					if(document.getElementById('password').value[i].match(alphaExp)){
	
					}else{
						if  (validChar.indexOf(document.getElementById('password').value[i]) < 0 ) {
							alert('Invalid character in password. Only A-Z, a-z, 0-9 , *, @, (,), _,# are allowed.');
							return false;
						}
					}
				}
			}
			
            if((email=='')||(pass=='')||(confpass=='')||(fullname==''))
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
                         if(pass==confpass)
                         {
                            
                             document.editLocalUser.submit();
                         }
                         else
                         {
                             alert('passwords do not match');
                         }
                     }
                  else
                      {
                          alert('groups cannot be empty');
                      }
             }
            

        }
        catch(err)
        {
            alert(err);
        }
    },
    Editcancel : function()
    {
        document.editLocalUser.action = "allUsers.action";
        document.editLocalUser.submit();
    }
}
