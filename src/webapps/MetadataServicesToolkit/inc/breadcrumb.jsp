<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->



<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%@page import="java.util.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<LINK HREF="page-resources/css/breadcrumb.css" REL="stylesheet" TYPE="text/css">

<%
String breadcrumb = request.getParameter("bread");
StringTokenizer strtok = new StringTokenizer(breadcrumb,",");
%>
<html>
   
    <body>
       
        <div class="breadcrumb">
             <ul>
            <%
                while(strtok.hasMoreTokens())
                    {
                        String str = strtok.nextToken();
                        if(strtok.hasMoreTokens())
                            {
                                %>

                                            <li style="float:left;margin-top:-8px;margin-right:10px;"><div><%=str%></div></li>
                                            <li style="float:left;margin-top:-15px;margin-right:10px;"><div><img src="page-resources/img/breadcrumb.gif"></div></li>
                                <%
                            }
                        else
                            {
                                %>
                                            <li style="float:left;margin-top:-8px;"><div><%=str%></div></li>
                                <%
                            }

                    }
            %>
            </ul>

        </div>

    </body>
</html>
