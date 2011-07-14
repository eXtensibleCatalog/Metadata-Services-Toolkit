 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  


var bootstrap = {
	init: function(){
		// define our loader
		bootstrap.loader = new YAHOO.util.YUILoader({
			// which components we need
			require: ["container", "button", "fonts", "selector"],
			// load optional components
			loadOptional: true,
			onSuccess: function() {
				// we add the yui-skin-sam class to our body for skinning purposes
				var bodyTag = YAHOO.util.Selector.query('body');
				YAHOO.util.Dom.addClass(bodyTag, 'yui-skin-sam');
				// initialize our object
				ui.init();
				// replace the alert function by ours
				window.alert = function(text){
					ui.dialogInfo.setBody(text);
					ui.dialogInfo.show();
				};
			}
		});
		// finally we insert the loader
		bootstrap.loader.insert();
	}
}

var ui = {	
	init: function(){		
		// our dialog for info, to show messages to the users
		ui.dialogInfo = new YAHOO.widget.SimpleDialog("simpledialog1", 
														{ 	width: "300px",
															fixedcenter: true,
															visible: false,
															draggable: false,
															zIndex: 9999,
															close: true,
															modal: true,
															effect:{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.25},
															constraintoviewport: true,
															buttons: [ { text:"close", handler: function(){this.hide();}, isDefault:true }]
														});
		ui.dialogInfo.setHeader("information");
		// Render the Dialog
		ui.dialogInfo.render(document.body);
	},
	
	/**
	*	Show the info dialog
	*	@param text String the text we want to show
	**/
	showDialogInfo: function(text){
		ui.dialogInfo.setBody(text);
		ui.dialogInfo.show();
	}
};

// get the whole thing going
bootstrap.init();

