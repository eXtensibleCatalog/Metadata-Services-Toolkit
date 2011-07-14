 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.user.removeUser");

YAHOO.xc.mst.user.removeUser = {

 deleteUser : function(userId)
 {

     try
     {
         var result;
         result = confirm('Are you sure you want to delete the user ?');
		 if (result)
         {
			document.getElementById('userId').value = userId;
			document.deleteUser.submit();
		 }
     }
     catch(err)
     {
         alert(err);
     }
 },
 removeMember : function(userId,groupId)
 {

     try
     {
         var result;
         result = confirm('Are you sure you want to remove the member from the group ?');
		 if (result)
         {
			document.getElementById('userId').value = userId;
            document.getElementById('groupId').value = groupId;
			document.removeMember.submit();
		 }
     }
     catch(err)
     {
         alert(err);
     }
 }
}