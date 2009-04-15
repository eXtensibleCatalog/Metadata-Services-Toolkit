<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:import url="/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">

<html>
    <head>
        <title>Add Processing Directive</title>
        <c:import url="/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
		<LINK href="page-resources/css/bodylayout.css" rel="stylesheet" type="text/css">

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/add_processingdirective.js"></SCRIPT>

    </head>


 <body class="yui-skin-sam">

        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">

            <!-- page header - this uses the yahoo page styling -->
            <div id="hd">

                <!--  this is the header of the page -->
                <c:import url="/inc/header.jsp"/>

                <!--  this is the header of the page -->
                <c:import url="/inc/menu.jsp"/>
                 <jsp:include page="/inc/breadcrumb.jsp">

                    <jsp:param name="bread" value="Services , Add Processing Directives (Step 1)" />

                </jsp:include>
                
                
                 
            </div>
            <!--  end header -->

            <!-- body -->
            <div id="bd">
           
                 <!-- Display of error message -->
                <c:if test="${errorType != null}">
                    <div id="server_error_div">
                    <div id="server_message_div" class="${errorType}">
                        <img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg">
                        <s:fielderror cssClass="errorMessage"/>
                    </div>
                    </div>
                 </c:if>
                 <div id="error_div"></div>

                 <div class="clear">&nbsp;</div>
                 
                <div class="stepsStructure">
                    <ul style="list-style:none;">
                        <li style="float:left;"><div><img src="page-resources/img/3.4_step1_highlight.gif"></div></li>
                        <li style="margin-left:5px;float:left;"><div><img src="page-resources/img/3.4_step2_grey.gif"></div></li>
                    </ul>
                </div>
                <div align="right" style="margin-bottom:10px;">
                        <button style="vertical-align:bottom;" class="xc_button_small" type="button" onclick="javascript:YAHOO.xc.mst.processingDirective.cancel();" name="cancel">Cancel</button> &nbsp;&nbsp;&nbsp;
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.ProcessingDirective.addProcessingDirective();" name="next">Continue to Step 2</button>
                </div>
                    <form action="/MetadataServicesToolkit/addProcessingDirectives.action" method="post" name="addProcessingDirective">
                    <div class="greybody">
                    <table align="center" cellpadding="0" cellspacing="0" border="0" width="60%">

                        <tr>
                            <td align="right">
                                <div style="margin-right:25px;">
                                    <c:choose>
                                        <c:when test="${temporaryProcessingDirective.sourceProvider!=null}">
                                            <c:set var="source" value="${temporaryProcessingDirective.sourceProvider.name}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="source" value="${temporaryProcessingDirective.sourceService.name}"/>
                                        </c:otherwise>
                                    </c:choose>
                                        <B>Select Source for records to be Processed</B> <br><br>
                                        <B>External Repositories</B> <br><br>
                                            <c:forEach var="n" items="${providerList}" varStatus="a">
                                                <c:choose>
                                                    <c:when test="${source==n.name}">
                                                        <c:out value="${n.name}"/>&nbsp;&nbsp;<input checked type="radio" name="source" value="${n.name}"><br><br>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${n.name}"/>&nbsp;&nbsp;<input type="radio" name="source" value="${n.name}"><br><br>
                                                    </c:otherwise>
                                                </c:choose>

                                            </c:forEach>
                                            <br><br><br>
                                        <B> Input Records to Services </B><br><br>
                                            <c:forEach var="n" items="${serviceList}" varStatus="a">
                                                <c:choose>
                                                    <c:when test="${source==n.name}">
                                                        <c:out value="${n.name}"/>&nbsp;&nbsp;<input checked type="radio" name="source" value="${n.name}"><br><br>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${n.name}"/>&nbsp;&nbsp;<input type="radio" name="source" value="${n.name}"><br><br>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                </div>
                            </td>
                            <td width="5%" valign="top">
                                <div align="center">
                                    <img src="page-resources/img/greenarrow_greybgrd.jpg">
                                </div>
                            </td>
                            <td valign="top" align="left">

                                <div style="margin-left:25px;">
                                    <B> Output Records from Services </B><br><br>
                                    <c:forEach var="n" items="${serviceList}" varStatus="a">
                                        <c:choose>
                                            <c:when test="${n.id==temporaryProcessingDirective.service.id}">
                                                <input checked type="radio" name="service" value="${n.id}">&nbsp;&nbsp;<c:out value="${n.name}"/>&nbsp;&nbsp;<br><br>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="radio" name="service" value="${n.id}">&nbsp;&nbsp;<c:out value="${n.name}"/>&nbsp;&nbsp;<br><br>
                                            </c:otherwise>
                                        </c:choose>

                                    </c:forEach>
                                </div>


                            </td>
                        </tr>
                       
                        
                    </table>
                    </div>

                    <div align="right" style="margin-top:10px;">
                        <button style="vertical-align:bottom;" class="xc_button_small" type="button" onclick="YAHOO.xc.mst.processingDirective.cancel();" name="cancel">Cancel</button> &nbsp;&nbsp;&nbsp;
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.processingDirective.addProcessingDirective();" name="next">Continue to Step 2</button>
                    </div>
                    </form>
             </div>
           </div>
       </body>
     </html>