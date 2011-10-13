 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.serviceStatusBar");

YAHOO.xc.mst.serviceStatusBar = {

  refreshServiceBar : function() {
    try {
      var url = "refreshServiceBar.action";
      YAHOO.util.Connect.asyncRequest('GET', url, {
        timeout: 20000,
        success: function (o) {
          if (o.responseText != null && o.responseText.search("ServiceStatus") < 0 ) {
            if(o.responseText != null && o.responseText.search("LoginPage") > 0 ) {
              window.location = 'viewLogin.action';
            }
          } else {
            document.getElementById("serviceBar").innerHTML = o.responseText;
          }
          window.setTimeout('YAHOO.xc.mst.serviceStatusBar.refreshServiceBar()',5000);
        }, failure: function (o) {
          document.getElementById("serviceBar").innerHTML = 'status unknown';
          window.setTimeout('YAHOO.xc.mst.serviceStatusBar.refreshServiceBar()',20000);
        }
      });
    } catch(e) {
      alert(e.description);
    }

  },
  init : function() {
    var showServiceStatus = document.getElementById("userHasPermission").value;
    if(showServiceStatus) {
      YAHOO.xc.mst.serviceStatusBar.refreshServiceBar();
    }
  }
}


// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.serviceStatusBar.init);
