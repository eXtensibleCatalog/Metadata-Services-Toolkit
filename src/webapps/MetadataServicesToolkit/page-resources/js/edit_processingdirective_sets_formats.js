 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.processingDirective");

YAHOO.xc.mst.processingDirective = {

 cancel : function()
 {
     try
     {
        document.editProcessingDirectiveSetsFormats.action= 'editProcessingDirectiveCancel.action';
		document.editProcessingDirectiveSetsFormats.submit();
     }
     catch(err)
     {
         alert(err);
     }
 },
 goBack : function()
 {
     document.editProcessingDirectiveSetsFormats.action= 'editProcessingDirectiveGoBack.action';
     document.editProcessingDirectiveSetsFormats.submit();
 },
 selectAll : function(field, currentField)
 {
    
   
    if (currentField.checked) {

   	 for (i=0;i<field.length;i++) 
        {
            field[i].checked=true;
        }
    } else {
    	 for (i=0;i<field.length;i++) 
        {
            field[i].checked=false;
        }
    }
    

 },
 editProcessingDirective : function(id)
 {
     document.editProcessingDirectiveSetsFormats.action= 'editProcessingDirectivesSetsFormats.action';
     document.getElementById("processingDirectiveId").value = id;
     document.editProcessingDirectiveSetsFormats.submit();
 }
}