 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.services");

YAHOO.xc.mst.services = {

 downloadFile: function(type,id)
            {
                window.location = "pages/logs/downloadLogFile.jsp?logType="+type+"&id="+id;
            },
 deleteService : function(serviceId)
            {
                var result;
                result = confirm('Are you sure you want to delete the Service ?');
                if (result)
                {
                    document.getElementById("serviceId").value = serviceId;
                    document.deleteService.action = "deleteService.action";
                    document.deleteService.submit();
                }
                
            }
}