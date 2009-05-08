 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.user.removeGroup");

YAHOO.xc.mst.user.removeGroup = {

 deleteGroup : function(groupId)
 {

     try
     {
         var result;
         result = confirm('Are you sure you want to delete this group ?');
		 if (result)
         {
			document.getElementById('groupId').value = groupId;
			document.deleteGroup.submit();
		 }
     }
     catch(err)
     {
         alert(err);
     }
 }
}