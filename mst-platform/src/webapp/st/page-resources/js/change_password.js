 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.changePassword");

YAHOO.xc.mst.changePassword = {

	save : function()
	{

		if (document.getElementById('old_password').value=='') {
			createErrorDiv("error",'Old password is required.');
			return false;
		}
		if (document.getElementById('new_password').value=='') {
			createErrorDiv("error",'New password is required.');
			return false;
		}
		if (document.getElementById('new_password_confirm').value=='') {
			createErrorDiv("error",'Re-enter new password.');
			return false;
		}	
		if (document.getElementById('new_password_confirm').value != document.getElementById('new_password').value) {
			createErrorDiv("error",'New password and password confirmation does not match.');
			return false;
		}

		
		if (document.getElementById('new_password').value.length < 6 || document.getElementById('new_password').value.length > 10) {
			createErrorDiv("error",'Password should be 6 to 10 characters long');
			return false;
		}
				
		var alphaExp = /^[0-9a-zA-Z]+$/;
		
		var validChar = '*,(,),_,#,@';
		var i = 0;
		for(i = 0; i < document.getElementById('new_password').value.length; i++) {
			if(document.getElementById('new_password').value.charAt(i).match(alphaExp)){

			}else{
				if  (validChar.indexOf(document.getElementById('new_password').value.charAt(i)) < 0 ) {
					alert('Invalid character in password. Only A-Z, a-z, 0-9 , *, @, (,), _,# are allowed.');
					return false;
				}
			}
		}			

		document.changePasswordForm.action=  'changePassword.action';
		document.changePasswordForm.submit();
	
	},

	cancel : function()
	{
		document.changePasswordForm.action=  'viewMyAccount.action';
		document.changePasswordForm.submit();
	
	}
	
} 

