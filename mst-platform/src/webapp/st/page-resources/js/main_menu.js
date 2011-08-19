 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */


  // Initialize and render the menu bar when it is available in the DOM
  YAHOO.util.Event.onContentReady("mainMenu", function () {
      // Instantiate and render the menu bar
      var oMenuBar = new YAHOO.widget.MenuBar("mainMenu", { autosubmenudisplay: false, hidedelay: 750, lazyload: true });
      /*
          Call the "render" method with no arguments since the markup for
          this menu already exists in the DOM.
      */
      oMenuBar.render();
  });

