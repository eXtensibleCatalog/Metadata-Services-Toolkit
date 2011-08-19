<!--
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mst" uri="mst-tags"%>

<c:import url="/st/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">


<html>
    <head>
        <title>List Services</title>
        <c:import url="/st/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
    <LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
    <LINK href="page-resources/css/bodylayout.css" rel="stylesheet" type="text/css">

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script>
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
                <c:import url="/st/inc/header.jsp"/>

                <!--  this is the header of the page -->
                <c:import url="/st/inc/menu.jsp"/>
                <jsp:include page="/st/inc/breadcrumb.jsp">

                    <jsp:param name="bread" value="Services | List Services" />

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
                        <span class="errorText">
                            <mst:fielderror error="${fieldErrors}">
                            </mst:fielderror>
                        </span>
                    </div>
                    </div>
                 </c:if>
                 <div id="error_div"></div>

                 <div class="clear">&nbsp;</div>
                 <c:choose>
                    <c:when test="${empty services}">
                         <div class="emptytablebar">
                             <div class="emptytable_innerdiv"> Choose <b>Services</b> <img class="emptytable_img" src="page-resources/img/bullet_go.gif"/> <b>Add Service</b> to add a new service </div>
                         </div>
                    </c:when>
                    <c:otherwise>
                        <div class="viewTable">
                            <table width="100%">

                                  <thead>
                                    <tr>
                                        <td class="sortcolumn" width="140">
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
                                        <td>Associated Repository URL </td>
                                        <td>Status</td>
                                        <td>View Log</td>
                                    </tr>
                                </thead>


                            <tbody>

                                          <c:forEach var="service" items="${services}" varStatus="serviceCount">
                                            <tr>
                                                    <c:set var="classColumn" value="plainColumn"/>
                                                    <c:if test="${columnSorted=='ServiceName'}">
                                                        <c:set var="classColumn" value="sortColumn"/>
                                                    </c:if>
                                                <c:if test="${!service.deleted}">
                                                  <td class="${classColumn}"><a href="viewEditService.action?serviceId=${service.id}"><c:out value="${service.name}"/></a></td>
                                                </c:if>
                                                <c:if test="${service.deleted}">
                                                  <td class="${classColumn}">${service.name} (Deleted)</td>
                                                </c:if>
                                                <c:set var="baseURL" value="${baseURL}"/>
                                                <c:set var = "url" value="${fn:replace(baseURL,'SERVICE_NAME',fn:replace(service.name, ' ', '-'))}" />
                                                <td><c:out value="${url}"/></td>
                                                <td>
                                                    <c:set var="service_status" value ="${service.status}"/>
                                                    <c:choose>
                                                        <c:when test="${service_status=='NOT_RUNNING'}">
                                                            Not running
                                                        </c:when>
                                                        <c:when test="${service_status=='RUNNING'}">
                                                            Running
                                                        </c:when>
                                                        <c:when test="${service_status=='ERROR'}">
                                                            Error
                                                        </c:when>
                                                        <c:when test="${service_status=='PAUSED'}">
                                                            Paused
                                                        </c:when>
                                                        <c:when test="${service_status=='CANCELED'}">
                                                            Aborted
                                                        </c:when>
                                                        <c:otherwise>
                                                            Not running
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <c:if test="${!service.deleted}">
                                                  <td width="175px">
                                                      <button style="width:70px" onclick="javascript:YAHOO.xc.mst.services.listServices.downloadFile('service','${service.id}');" class="xc_button" type="button" name="Service">Service</button> &nbsp;
                                                      <button style="width:85px" onclick="javascript:YAHOO.xc.mst.services.listServices.downloadFile('harvestout','${service.id}');" class="xc_button" type="button" name="HarvestOut">Harvest Out</button>
                                                  </td>
                                                </c:if>
                                                <c:if test="${service.deleted}">
                        <td width="175px">
                                                      <button style="width:70px" class="xc_button_disabled" disabled type="button" name="Service">Service</button> &nbsp;
                                                      <button style="width:85px" class="xc_button_disabled" disabled type="button" name="HarvestOut">Harvest Out</button>
                                                  </td>
                                                </c:if>
                                            </tr>
                                        </c:forEach>

                                </tbody>
                        </table>

                    </div>
                </c:otherwise>
            </c:choose>

            </div>

         <div class="clear">&nbsp;</div>
            <!--  this is the footer of the page -->
            <c:import url="/st/inc/footer.jsp"/>

        </div>
</body>
</html>
