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

alterStatus : function(changedStatus)
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
              var response = http_request.responseText;
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
                        document.getElementById("resumeButton").style.display = "none";
                        document.getElementById("abortButton").style.display = "inline";
                        document.getElementById("pauseButton").disabled = true;
                        document.getElementById("abortButton").disabled = true;
                        
                  }
            }

        }
        if(changedStatus=='pause')
            {
                http_request.open("GET","servicePause.action",true);
            }
        else if(changedStatus=='resume')
            {
                http_request.open("GET","serviceResume.action",true);
            }
        else
            {
                http_request.open("GET","serviceAbort.action",true)
            }

        http_request.send(null);


    },
refreshServiceBar : function()
    {
        try
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
                    if(document.getElementById("serviceBar").innerHTML.toString() == http_request.responseText.toString())
                        {
                            alert("HTML content is the same");
                        }
                    else
                        {
                            document.getElementById("serviceBar").innerHTML = http_request.responseText;
                        }
                }
                else
                {
                    document.getElementById("serviceBar").innerHTML = "ERROR : Please try later";
                }
            }
            
            http_request.open("GET","refreshServiceBar.action",true);           
            http_request.send(null);
            window.setTimeout('YAHOO.xc.mst.serviceStatusBar.refreshServiceBar()',300000);
        }
        catch(e)
        {
            alert(e.description);
        }

    },
	init : function()
	{
	    YAHOO.xc.mst.serviceStatusBar.refreshServiceBar();
	}

 }


// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.serviceStatusBar.init);