 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.login");

YAHOO.xc.mst.login = {

    displayForgotPasswordURL : function(server)
    {

      if (server.value == 'Local') {
          YAHOO.xc.mst.login.showLocal();
        }
  if (server.value != 'Local') {
    YAHOO.xc.mst.login.showLDAP();
        }

    },

    showLocal : function()
    {
      document.getElementById('forgot_password_url').innerHTML = document.getElementById('forgot_password_local').innerHTML;
    },

    showLDAP : function()
    {
         document.getElementById('forgot_password_url').innerHTML = document.getElementById('forgot_password_ldap').innerHTML;
    },

  /**
   * initialize the page
   */
  init : function()
  {
      YAHOO.xc.mst.login.showLocal();
      document.getElementById('forgot_password_local').style.visibility = "hidden";
      document.getElementById('forgot_password_ldap').style.visibility = "hidden";
  }

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.login.init);
