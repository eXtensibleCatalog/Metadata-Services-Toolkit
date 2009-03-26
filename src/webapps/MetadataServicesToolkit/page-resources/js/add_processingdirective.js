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
        document.addProcessingDirective.action = 'listProcessingDirectives.action';
		document.addProcessingDirective.submit();
     }
     catch(err)
     {
         alert(err);
     }
 },
 addProcessingDirective : function()
 {
       
     var sourceFlag = false;
     var serviceFlag = false;
     for(i=0;i<document.addProcessingDirective.source.length;i++)
         {
             if(document.addProcessingDirective.source[i].checked)
                 {
                     sourceFlag = true;
                 }
         }
     for(i=0;i<document.addProcessingDirective.service.length;i++)
         {
             if(document.addProcessingDirective.service[i].checked)
                 {
                     serviceFlag = true;
                 }
         }
    
     try
     {
        if((sourceFlag==true)&&(serviceFlag==true))
            {
                document.addProcessingDirective.submit();
            }
        else
            {
                if(sourceFlag==false)
                    {
                        createErrorDiv("error","Atleast one source has to be selected");
                    }
                else
                    {
                        createErrorDiv("error","Atleast one service has to be selected");
                    }
            }
     }
     catch(err)
     {
         alert(err);
     }
 }
}