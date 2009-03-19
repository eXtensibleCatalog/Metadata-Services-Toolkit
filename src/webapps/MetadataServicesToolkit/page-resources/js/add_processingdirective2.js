 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.services");

YAHOO.xc.mst.services = {

cancel : function()
 {
     try
     {
        document.addProcessingDirective2.action= 'addPDCancel.action';
		document.addProcessingDirective2.submit();
     }
     catch(err)
     {
         alert(err);
     }
 },
 goBack : function()
 {
     document.addProcessingDirective2.action= 'addPDGoBack.action';
     document.addProcessingDirective2.submit();
 },
 addDirective : function()
 {
     try
     {
          document.addProcessingDirective2.action= 'addProcessingDirectives2.action';
          document.addProcessingDirective2.submit();
            
       
     }
     catch(err)
     {
         alert(err);
     }
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
    

 }
 
 

}

