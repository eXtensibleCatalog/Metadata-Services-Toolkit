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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<c:set var="breadcrumb" value="${param.bread}"/>

<html>
   
    <body>
       
        <div class="breadcrumb">
             <ul>
           
                                <c:forTokens var="str" delims="|" varStatus="a" items="${breadcrumb}">
                                    <c:choose>
                                        <c:when test = "${a.last}">
                                            <li><div>${str}</div></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li><div>${str}</div></li>
                                            <li><div><img src="page-resources/img/breadcrumb.gif"></div></li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forTokens>
                                            
                        
            </ul>

        </div>

    </body>
</html>
