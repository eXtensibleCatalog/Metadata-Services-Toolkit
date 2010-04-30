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
			        document.getElementById('oai_request_div').innerHTML = o.responseText;
			
					// handle a No of deleting repository dialog
					var handleClose = function() {
					    YAHOO.xc.mst.logs.harvestIn.oaiRequestDialog.hide();
					};                
			                
					// Instantiate the Dialog
					// make it modal - 
					// it should not start out as visible - it should not be shown until 
					// new repository button is clicked.
					YAHOO.xc.mst.logs.harvestIn.oaiRequestDialog = new YAHOO.widget.Dialog('oaiRequestDialog', 
					{ width : "700px",
						  visible : false, 
						  modal : true,
						  buttons : [ { text:'Close', handler:handleClose }]
				
					} );
				
				
					// Show the dialog
				       YAHOO.xc.mst.logs.harvestIn.oaiRequestDialog.showDialog = function()
				       {
					   YAHOO.xc.mst.logs.harvestIn.oaiRequestDialog.show();
					   YAHOO.xc.mst.logs.harvestIn.oaiRequestDialog.center();
				       },
			
			
					// Render the Dialog
					YAHOO.xc.mst.logs.harvestIn.oaiRequestDialog.render();
			
			
					YAHOO.xc.mst.logs.harvestIn.oaiRequestDialog.showDialog();                
	                
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
