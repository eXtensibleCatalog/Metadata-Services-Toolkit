 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.services.listServices");

YAHOO.xc.mst.services.listServices = {

 downloadFile: function(type,id)
            {
                window.location = "pages/logs/downloadLogFile.action?logType="+type+"&id="+id;
            },

	/** 
	 * initialize the page 
	 */ 
	init : function() 
	{
	    //YAHOO.xc.mst.services.listServices.createDeleteServiceDialog();
	}

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.services.listServices.init);
