 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.group.removeGroup");

YAHOO.xc.mst.group.removeGroup = {

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
