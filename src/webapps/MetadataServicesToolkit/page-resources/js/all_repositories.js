 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */
  
YAHOO.namespace("xc.mst.repository");

YAHOO.xc.mst.repository = {


        removeErrorMessage : function()
        {
            window.location = "allRepository.action";
        },

       
        funcHandler : function()
        {
            try
            {
                var isAscendingOrder = document.getElementById("isAscendingOrder").value;
                var columnNumber = document.getElementById("columnNumber").value;
                var id = "";
                var i=0;
                for(i=1;i<3;i++)
                    {
                        if(i==columnNumber)
                            {
                                id = "Column" + i;
                                document.getElementById(id).style.background = "#edfaff";
                                if(isAscendingOrder==true)
                                    {
                                        id = "triangle_sort" + i;
                                        document.getElementById(id).style.display = "block";
                                        id = "triangle_sort_down" + i;
                                        document.getElementById(id).style.display = "none";
                                    }
                                else
                                    {
                                        id = "triangle_sort_down" + i;
                                        document.getElementById(id).style.display = "block";
                                        id = "triangle_sort" + i;
                                        document.getElementById(id).style.display = "none";
                                    }

                            }
                        else
                            {
                                id = "Column" + i;
                                document.getElementById(id).style.background = "#ffffff";
                                id = "triangle_sort" + i;
                                document.getElementById(id).style.display = "none";
                                id = "triangle_sort_down" + i;
                                document.getElementById(id).style.display = "none";
                            }
                    }
            }
            catch(err)
            {
                alert(err);
            }
        }
 }

 