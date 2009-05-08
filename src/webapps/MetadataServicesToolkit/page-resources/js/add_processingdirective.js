 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.processingDirective.addDirective");

YAHOO.xc.mst.processingDirective.addDirective = {

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
     try
     {
         var sourceFlag = false;
         var serviceFlag = false;
         var serviceArraySize = 1;
         var sourceArraySize = 1;
         if(typeof(document.addProcessingDirective.source.length)!='undefined')
             {
                 sourceArraySize = document.addProcessingDirective.source.length;
                 for(i=0;i<sourceArraySize;i++)
                 {
                     if(document.addProcessingDirective.source[i].checked)
                         {
                             sourceFlag = true;
                         }
                 }
             }
         else
             {
                if(document.addProcessingDirective.source.checked)
                    {
                        sourceFlag = true;
                    }
             }
         if(typeof(document.addProcessingDirective.service.length)!='undefined')
             {
                 serviceArraySize = document.addProcessingDirective.service.length;
                 for(i=0;i<serviceArraySize;i++)
                 {
                     if(document.addProcessingDirective.service[i].checked)
                         {
                             serviceFlag = true;
                         }
                 }
             }
         else
             {
                 if(document.addProcessingDirective.service.checked)
                     {
                         serviceFlag = true;
                     }
             }
         
         


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