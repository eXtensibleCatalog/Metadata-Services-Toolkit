 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.logs.harvestIn");

YAHOO.xc.mst.logs.harvestIn = {

    displayOAIRequest : function(providerId)
    {

            var url = "displayOAIRequest.action?providerId="+providerId;
            YAHOO.util.Connect.asyncRequest('GET', url, {
            success: function (o)
            {
                alert(o.responseText);
            }
            });

     },

    downloadFile: function(providerId)
            {
                window.location = "downloadLogFile.action?logType=HarvestIn&id="+providerId;
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
