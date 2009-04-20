 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.logs");

YAHOO.xc.mst.logs = {

    displayOAIRequest : function(providerId)
    {
         var http_request = false;
            if (window.XMLHttpRequest)
                  { // Mozilla, Safari,...
                     http_request = new XMLHttpRequest();
                     if (http_request.overrideMimeType) {
                        // set type accordingly to anticipated content type
                        //http_request.overrideMimeType('text/xml');
                        http_request.overrideMimeType('text/html');
                     }
                  }
                  else if (window.ActiveXObject)
                  { // IE
                     try
                     {
                        http_request = new ActiveXObject("Msxml2.XMLHTTP");
                     }
                     catch (e)
                     {
                        try
                        {
                           http_request = new ActiveXObject("Microsoft.XMLHTTP");
                        }
                        catch (e)
                        {

                        }
                     }
                  }
                  if (!http_request)
                  {
                     alert('Cannot create XMLHTTP instance');
                  }


            http_request.onreadystatechange=function()
            {
                if(http_request.readyState==4)
                {
                    alert(http_request.responseText);
                }
                else
                {
                    
                }
            }

            http_request.open("GET","displayOAIRequest.action?providerId="+providerId,true);
            http_request.send(null);
        },

    downloadFile: function(providerId)
            {
                window.location = "pages/logs/downloadLogFile.jsp?logType=HarvestIn&id="+providerId;
            },

    resetFunction : function(harvestInLogFileName,providerId)
            {
                try
                {
                    
                    document.getElementById("harvestInLogFileName").value = harvestInLogFileName;
                    document.getElementById("providerId").value = providerId;
                    document.harvestInReset.action = "harvestInReset.action";
                    document.harvestInReset.submit();
                }
                catch(e)
                {
                    alert(e);
                }
            },
    resetAll: function()
    {
        try
        {
            document.harvestInReset.action = "harvestInResetAll.action";
            document.harvestInReset.submit();
        }
        catch(e)
        {
            alert(e);
        }
    }
}
