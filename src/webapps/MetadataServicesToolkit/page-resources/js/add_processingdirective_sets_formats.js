 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */

YAHOO.namespace("xc.mst.processingDirective.addDirectiveSetsFormats");

YAHOO.xc.mst.processingDirective.addDirectiveSetsFormats = {

cancel : function()
 {
     try
     {
        document.addProcessingDirectiveSetsFormats.action= 'addProcessingDirectiveCancel.action';
		document.addProcessingDirectiveSetsFormats.submit();
     }
     catch(err)
     {
         alert(err);
     }
 },

goBack : function()
 {
     document.addProcessingDirectiveSetsFormats.action= 'addProcessingDirectiveGoBack.action';
     document.addProcessingDirectiveSetsFormats.submit();
 },

 addDirective : function()
 {
     try
     {
         var choice = true;
         if((document.getElementById("setsSelected")==null)||(document.getElementById("formatsSelected")==null))
             {
                    if((document.getElementById("setsSelected")==null)&&(document.getElementById("formatsSelected")==null))
                        {
                            createErrorDiv('error','Processing Rule cannot be created as neither sets or formats are available');
                            choice = false;
                        }
                    else if(document.getElementById("setsSelected")==null)
                        {
                            if (document.addProcessingDirectiveSetsFormats.formatsSelected.value=='') {
                                createErrorDiv('error','Select a format.');
                                choice = false;
                            }

                        }
                    else if(document.getElementById("formatsSelected")==null)
                        {
                             if (document.addProcessingDirectiveSetsFormats.setsSelected.value=='') {
                                createErrorDiv('error','Select a set.');
                                choice = false;
                            }
                        }
                                       
             }
         else
             {
                 if((document.addProcessingDirectiveSetsFormats.setsSelected.value=='')&&(document.addProcessingDirectiveSetsFormats.formatsSelected.value==''))
                     {

                          createErrorDiv('error','Both sets and formats cannot be left unselected. Either sets or formats have to selected');
                          choice = false;

                     }
                   
             }

         if(choice==true)
             {
                  document.addProcessingDirectiveSetsFormats.action= 'addProcessingDirectivesSetsFormats.action';
                  document.addProcessingDirectiveSetsFormats.submit();
             }

            
       
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



