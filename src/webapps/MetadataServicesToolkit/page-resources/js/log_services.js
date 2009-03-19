 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.log");

YAHOO.xc.mst.log = {

    downloadFile : function(serviceId)
           {
               window.location = "pages/logs/downloadLogFile.jsp?logType=Service&id="+serviceId;
           },

    resetFunction : function(serviceLogFileName,serviceId)
    {
        try
        {
            alert("Entered the reset method");
            document.getElementById("serviceLogFileName").value = serviceLogFileName;
            document.getElementById("serviceId").value = serviceId;
            document.serviceReset.submit();
        }
        catch(e)
        {
            alert(e);
        }
    }
}