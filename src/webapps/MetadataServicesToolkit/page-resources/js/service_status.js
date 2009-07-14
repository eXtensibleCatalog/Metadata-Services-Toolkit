 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.serviceStatusBar");

YAHOO.xc.mst.serviceStatusBar = {

yuiAlterStatus : function(changedStatus)
{
    var url = "";
    
    if(changedStatus=='pause')
            {
                url = "servicePause.action";
            }
        else if(changedStatus=='resume')
            {
                url = "serviceResume.action";
            }
        else
            {
                url = "serviceAbort.action";
            }
    YAHOO.util.Connect.asyncRequest('GET', url, {
            success: function (o)
            {
                  
                  var response = o.responseText;
                  var findResume = response.search("MSTServiceResumed");
                  var findPause = response.search("MSTServicePaused");
                  var findAbort = response.search("MSTServiceAborted");
                  var findEnded = response.search("Ended");
                  if(findResume!=-1)
                      {
                            if(findEnded==-1)
                                {
                                    document.getElementById("pauseButton").style.display = "inline";
                                    document.getElementById("resumeButton").style.display = "none";
                                    document.getElementById("abortButton").style.display = "inline";
                                }
                            else
                                {
                                    document.getElementById("pauseButton").style.display = "inline";
                                    document.getElementById("pauseButton").disabled = true;
                                    document.getElementById("resumeButton").style.display = "none";
                                    document.getElementById("abortButton").style.display = "inline";
                                    document.getElementById("abortButton").disabled = true;
                                    document.getElementById("currentProcess").innerHTML = "Process has already ended";
                                }

                      }
                  if(findPause!=-1)
                      {
                            if(findEnded==-1)
                                {
                                    document.getElementById("pauseButton").style.display = "none";
                                    document.getElementById("resumeButton").style.display = "inline";
                                    document.getElementById("abortButton").style.display = "inline";
                                }
                            else
                                {
                                    document.getElementById("pauseButton").style.display = "inline";
                                    document.getElementById("pauseButton").disabled = true;
                                    document.getElementById("resumeButton").style.display = "none";
                                    document.getElementById("abortButton").style.display = "inline";
                                    document.getElementById("abortButton").disabled = true;
                                    document.getElementById("currentProcess").innerHTML = "Process has already ended";
                                }

                      }
                  if(findAbort!=-1)
                      {
                            if(findEnded!=-1)
                                {
                                    document.getElementById("currentProcess").innerHTML = "Process has already ended";
                                }
                            else
                                {
                                    document.getElementById("currentProcess").innerHTML = "";
                                }
                            document.getElementById("pauseButton").style.display = "inline";
                            document.getElementById("pauseButton").className = "xc_button_disabled";
                            document.getElementById("resumeButton").style.display = "none";
                            document.getElementById("abortButton").style.display = "inline";
                            document.getElementById("abortButton").className = "xc_button_disabled";
                            document.getElementById("pauseButton").disabled = true;
                            document.getElementById("abortButton").disabled = true;

                      }
            }
        });

},

refreshServiceBar : function()
    {
        try
        {
           
            var url = "refreshServiceBar.action";
            YAHOO.util.Connect.asyncRequest('GET', url, {
            success: function (o)
            {
                
                     if(o.responseText != null && o.responseText.search("ServiceStatus") < 0 )
                        {
                            window.location = 'viewLogin.action';
                        } else {
                    	    document.getElementById("serviceBar").innerHTML = o.responseText;
                    	}
                    	
            }
            });

            window.setTimeout('YAHOO.xc.mst.serviceStatusBar.refreshServiceBar()',1000);
        }
        catch(e)
        {
            alert(e.description);
        }

    },
	init : function()
	{
         var showServiceStatus = document.getElementById("userHasPermission").value;

         if(showServiceStatus)
             {
                 YAHOO.xc.mst.serviceStatusBar.refreshServiceBar();
             }
         
	}

 }


// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.serviceStatusBar.init);