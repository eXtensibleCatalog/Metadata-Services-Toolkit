/*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.logs.general");

YAHOO.xc.mst.logs.general = {


    downloadFile: function(logId)
            {
                window.location = "downloadLogFile.action?logType=General&id="+logId;
            },

    resetFunction : function(logId)
            {
                try
                {


                    document.getElementById("logId").value = logId;
                    document.generalLogReset.action = "generalLogReset.action";
                    document.generalLogReset.submit();
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
            document.generalLogReset.action = "generalLogResetAll.action";
            document.generalLogReset.submit();
        }
        catch(e)
        {
            alert(e);
        }
    }
}
