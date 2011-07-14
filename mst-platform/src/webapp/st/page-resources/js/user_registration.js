 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.registeration");



YAHOO.xc.mst.registeration = {

	register : function()
	{

		if (document.getElementById('user_first_name').value=='') {
			createErrorDiv("error",'User first Name is required.');
			return false;
		}

		if (document.getElementById('user_last_name').value=='') {
			createErrorDiv("error",'User last Name is required.');
			return false;
		}		
		if (document.getElementById('login_server').value== '0') {
		    createErrorDiv("error",'Please select login sever type.');
			return false;
		}						

		if (document.getElementById('user_name').value=='') {
		    createErrorDiv("error",'User name is required.');
			return false;
		}
		
		

		if (document.getElementById('login_server').value == 'local') {
			if (document.getElementById('user_password').value=='') {
			    createErrorDiv("error",'Password is required.');
				return false;
			}
			if (document.getElementById('user_password').value!=document.getElementById('user_password_confirmation').value) {
			    createErrorDiv("error",'Password does not match confirmation password.');
				return false;
			}
			
			if (document.getElementById('user_password').value.length < 6 || document.getElementById('user_password').value.length > 20) {
			    createErrorDiv("error",'Password should be 6 to 20 characters long');
				return false;
			}
					
			var alphaExp = /^[0-9a-zA-Z]+$/;
			
			var validChar = '*,(,),_,#,@';
			var i = 0;
			for(i = 0; i < document.getElementById('user_password').value.length; i++) {
				if(document.getElementById('user_password').value.charAt(i).match(alphaExp)){

				}else{
					if  (validChar.indexOf(document.getElementById('user_password').value.charAt(i)) < 0 ) {
						createErrorDiv("error",'Invalid character in password. Only A-Z, a-z, 0-9 , *, @, (,), _,# are allowed.');
						return false;
					}
				}
			}

		}

		if (document.getElementById('login_server').value != 'local') {
		
			if (document.getElementById('user_password').value=='') {
			    createErrorDiv("error",'Password is required.');
				return false;
			}
			if (document.getElementById('user_password').value!=document.getElementById('user_password_confirmation').value) {
			
			    createErrorDiv("error",'Password does not match confirmation password.');
				return false;
			}

		}

		if (document.getElementById('user_email').value=='') {
			alert('Email is required.');
			return false;
		} else {
			if (!YAHOO.xc.mst.registeration.emailCheck(document.getElementById('user_email').value)) {
				alert("Invalid E-mail ID");
				return false;
			}

		}

		document.registrationForm.action= 'registerUser.action';
		document.registrationForm.submit();
	
	},

	cancel : function()
	{
		document.registrationForm.action= 'home.action';
		document.registrationForm.submit();
	
	},
	
	emailCheck : function(str) 
	{
			var at="@"
			var dot="."
			var lat=str.indexOf(at)
			var lstr=str.length
			var ldot=str.indexOf(dot)
			if (str.indexOf(at)==-1){
			   return false
			}
	
			if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
			   return false
			}
	
			if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
			    return false
			}
	
			 if (str.indexOf(at,(lat+1))!=-1){
			    return false
			 }
	
			 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
			    return false
			 }
	
			 if (str.indexOf(dot,(lat+2))==-1){
			    return false
			 }
			
			 if (str.indexOf(" ")!=-1){
			    return false
			 }
	
	 		 return true					
	}
}


