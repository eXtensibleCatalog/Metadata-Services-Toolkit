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
        <title>List Services</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/all_services.js"></SCRIPT>
     
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

                    <jsp:param name="bread" value="Services , List Services" />

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
                 
                    <div class="viewTable">
                        <table width="100%">
                            <thead>
                                <tr>
                                    <td class="sortcolumn">
                                        <div>
                                            <c:if test="${columnSorted!='ServiceName'}">
                                                     <c:url var="serviceSortUrl" value="listServices.action">
                                                       <c:param name="isAscendingOrder" value="true"/>
                                                       <c:param name="columnSorted" value="ServiceName"/>
                                                     </c:url>
                                                      <a href="${serviceSortUrl}">Service Name</a>
                                                 </c:if>

                                                 <c:if test="${columnSorted=='ServiceName'}">
                                                   <c:url var="serviceSortUrl" value="listServices.action">
                                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                                     <c:param name="columnSorted" value="ServiceName"/>
                                                   </c:url>

                                                   <a href="${serviceSortUrl}">Service Name</a>

                                                    <c:choose>
                                                        <c:when test="${isAscendingOrder==true}">
                                                            &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                                        </c:when>
                                                        <c:otherwise>
                                                            &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                                        </c:otherwise>
                                                    </c:choose>
                                                 </c:if>
                                            
                                        </div>

                                    </td>
                                    <td>
                                        <div>
                                            <c:if test="${columnSorted!='Port'}">
                                                     <c:url var="serviceSortUrl" value="listServices.action">
                                                       <c:param name="isAscendingOrder" value="true"/>
                                                       <c:param name="columnSorted" value="Port"/>
                                                     </c:url>
                                                      <a href="${serviceSortUrl}">Associated Repository URL</a>
                                                 </c:if>

                                                 <c:if test="${columnSorted=='Port'}">
                                                   <c:url var="serviceSortUrl" value="listServices.action">
                                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                                     <c:param name="columnSorted" value="Port"/>
                                                   </c:url>

                                                   <a href="${serviceSortUrl}">Associated Repository URL</a>

                                                    <c:choose>
                                                        <c:when test="${isAscendingOrder==true}">
                                                            &nbsp;<img src="page-resources/img/triangle_sort.jpg">
                                                        </c:when>
                                                        <c:otherwise>
                                                            &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                                        </c:otherwise>
                                                    </c:choose>
                                                 </c:if>
                                        </div>

                                    </td>
                                    <td>Status</td>
                                    <td>View Log</td>
                                    <td>Delete</td>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="n" items="${services}" varStatus="a">
                                    <tr>
                                            <c:set var="classColumn" value="plainColumn"/>
                                            <c:if test="${columnSorted=='ServiceName'}">
                                                <c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
                                        <td class="${classColumn}"><a href="viewEditService.action?serviceId=${n.id}"><U><c:out value="${n.name}"/></U></a></td>
                                        <c:set var="baseURL" value="${baseURL}"/>
                                        <c:set var = "url" value="${fn:replace(baseURL,'8080',n.port)}" />
                                            <c:set var="classColumn" value="plainColumn"/>
                                            <c:if test="${columnSorted=='Port'}">
                                                <c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
                                        <td class="${classColumn}"><c:out value="${url}"/></td>
                                        <td>${n.status}</td>
                                        <td>
                                            <button onclick="javascript:YAHOO.xc.mst.services.downloadFile('service','${n.id}');" class="xc_button" type="button" name="Service">Service</button> &nbsp;&nbsp;&nbsp;
                                            <button onclick="javascript:YAHOO.xc.mst.services.downloadFile('harvestout','${n.id}');" class="xc_button" type="button" name="HarvestOut">Harvest Out</button>
                                        </td>
                                        <td><button class="xc_button" onclick="javascript:YAHOO.xc.mst.services.deleteService(${n.id})" type="button" name="delete">Delete</button></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <form name="deleteService" method="post">
                            <input type="hidden" name="serviceId" id="serviceId">
                        </form>
                    </div>

            </div>
        </div>
</body>
</html>
