 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.processingDirective.editDirective");

YAHOO.xc.mst.processingDirective.editDirective = {

 cancel : function()
 {

     try
     {
        document.editProcessingDirective.action = 'listProcessingDirectives.action';
		document.editProcessingDirective.submit();
     }
     catch(err)
     {
         alert(err);
     }
 },
 editProcessingDirective : function()
 {

    try
     {
         var sourceFlag = false;
         var serviceFlag = false;
         var serviceArraySize = 1;
         var sourceArraySize = 1;
         if(typeof(document.editProcessingDirective.source.length)!='undefined')
             {
                 sourceArraySize = document.editProcessingDirective.source.length;
                 for(i=0;i<sourceArraySize;i++)
                 {
                     if(document.editProcessingDirective.source[i].checked)
                         {
                             sourceFlag = true;
                         }
                 }
             }
         else
             {
                if(document.editProcessingDirective.source.checked)
                    {
                        sourceFlag = true;
                    }
             }
         if(typeof(document.editProcessingDirective.service.length)!='undefined')
             {
                 serviceArraySize = document.editProcessingDirective.service.length;
                 for(i=0;i<serviceArraySize;i++)
                 {
                     if(document.editProcessingDirective.service[i].checked)
                         {
                             serviceFlag = true;
                         }
                 }
             }
         else
             {
                 if(document.editProcessingDirective.service.checked)
                     {
                         serviceFlag = true;
                     }
             }

            if((sourceFlag==true)&&(serviceFlag==true))
                {
                    document.editProcessingDirective.submit();
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