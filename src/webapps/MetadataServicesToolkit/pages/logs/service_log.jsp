<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<!--  document type -->
<c:import url="/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">


<html>
    <head>
        <title>Service Logs</title>
        <c:import url="/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
		<LINK href="page-resources/css/bodylayout.css" rel="stylesheet" type="text/css">


        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/log_services.js"></SCRIPT>

       
    </head>

    <body class="yui-skin-sam">
        <%@ taglib prefix="s" uri="/struts-tags" %>
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">

		<!-- page header - this uses the yahoo page styling -->
		<div id="hd">

            <!--  this is the header of the page -->
            <c:import url="/inc/header.jsp"/>

            <!--  this is the header of the page -->
            <c:import url="/inc/menu.jsp"/>
             <jsp:include page="/inc/breadcrumb.jsp">

                    <jsp:param name="bread" value="Logs,Services" />

             </jsp:include>
 		</div>
		<!--  end header -->

		<!-- body -->
		<div id="bd">

             
             <!-- Display of error message -->
                <c:if test="${errorType != null}">
                    <div class="${errorType}">
                        <img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg">
                        <s:fielderror cssClass="errorMessage"/>
                    </div>
                 </c:if>
                 <div id="error_div"></div>

                <div class="clear">&nbsp;</div>

                <div align="right" style="margin-bottom:10px;">
                    <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.log.resetAll();" name="next">Reset All *</button>
                </div>
                <div class="viewTable">
                    <table width="100%">
                        <thead>
                            <tr>
                               <td>
                                    <c:if test="${columnSorted!='ServiceName'}">
                                         <c:url var="logSortUrl" value="serviceLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="ServiceName"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Service Name</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='ServiceName'}">
                                       <c:url var="logSortUrl" value="serviceLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="ServiceName"/>
                                       </c:url>

                                       <a href="${logSortUrl}">Service Name</a>

                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </td>
                                <td>
                                    <c:if test="${columnSorted!='InputRecords'}">
                                         <c:url var="logSortUrl" value="serviceLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="InputRecords"/>
                                         </c:url>
                                          <a href="${logSortUrl}">#Input Records</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='InputRecords'}">
                                       <c:url var="logSortUrl" value="serviceLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="InputRecords"/>
                                       </c:url>

                                       <a href="${logSortUrl}">#Input Records</a>

                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </td>
                                <td>
                                    <c:if test="${columnSorted!='OutputRecords'}">
                                         <c:url var="logSortUrl" value="serviceLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="OutputRecords"/>
                                         </c:url>
                                          <a href="${logSortUrl}">#Output Records</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='OutputRecords'}">
                                       <c:url var="logSortUrl" value="serviceLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="OutputRecords"/>
                                       </c:url>

                                       <a href="${logSortUrl}">#Output Records</a>

                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </td>
                                <td>
                                    <c:if test="${columnSorted!='Warnings'}">
                                         <c:url var="logSortUrl" value="serviceLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="Warnings"/>
                                         </c:url>
                                          <a href="${logSortUrl}">#Warnings</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='Warnings'}">
                                       <c:url var="logSortUrl" value="serviceLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="Warnings"/>
                                       </c:url>

                                       <a href="${logSortUrl}">#Warnings</a>

                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </td>
                                <td>
                                    <c:if test="${columnSorted!='Errors'}">
                                         <c:url var="logSortUrl" value="serviceLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="Errors"/>
                                         </c:url>
                                          <a href="${logSortUrl}">#Errors</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='Errors'}">
                                       <c:url var="logSortUrl" value="serviceLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="Errors"/>
                                       </c:url>

                                       <a href="${logSortUrl}">#Errors</a>

                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </td>

                                <td>Reset</td>

                                <td>
                                   <c:if test="${columnSorted!='LastLogReset'}">
                                         <c:url var="logSortUrl" value="serviceLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="LastLogReset"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Last Reset Date</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='LastLogReset'}">
                                       <c:url var="logSortUrl" value="serviceLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="LastLogReset"/>
                                       </c:url>

                                       <a href="${logSortUrl}">#Last Reset Date</a>

                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </td>
                            </tr>
                        </thead>
                        <tbody>
                             <c:forEach var="n" items="${serviceList}" varStatus="a">
                                  <tr>
                                            <c:set var="classColumn" value="plainColumn"/>
                                            <c:if test="${columnSorted=='ServiceName'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
                                      <td class="${classColumn}">
                                          <a style="cursor:pointer;" onclick ="javascript:YAHOO.xc.mst.log.downloadFile('${n.id}')"><U>${n.name}</U></a>
                                      </td>
                                             <c:set var="classColumn" value="plainColumn"/>
                                             <c:if test="${columnSorted=='InputRecords'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                             </c:if>
                                      <td class="${classColumn}">
                                          ${n.inputRecordCount}
                                      </td>
                                             <c:set var="classColumn" value="plainColumn"/>
                                             <c:if test="${columnSorted=='OutputRecords'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                             </c:if>
                                      <td class="${classColumn}">
                                          ${n.outputRecordCount}
                                      </td>
                                             <c:set var="classColumn" value="plainColumn"/>
                                             <c:if test="${columnSorted=='Warnings'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                             </c:if>
                                      <td class="${classColumn}">
                                          ${n.servicesWarnings}
                                      </td>
                                             <c:set var="classColumn" value="plainColumn"/>
                                             <c:if test="${columnSorted=='Errors'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                             </c:if>
                                      <td class="${classColumn}">
                                          ${n.servicesErrors}
                                      </td>
                                      <td><button class="xc_button" type="button" name="reset" onclick="javascript:YAHOO.xc.mst.log.resetFunction('${n.harvestOutLogFileName}','${n.id}')">Reset</button></td>
                                             <c:set var="classColumn" value="plainColumn"/>
                                             <c:if test="${columnSorted=='LastLogReset'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                             </c:if>
                                      <td class="${classColumn}">
                                          ${n.servicesLastLogReset}
                                      </td>
                                  </tr>
                              </c:forEach>
                        </tbody>
                    </table>
                    * Reset will reset the statistic to 0 and move the log file to the archives directory
                     <div align="right" style="margin-top:10px;margin-bottom:10px;">
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.log.resetAll();" name="next">Reset All</button>
                    </div>
                    
                    <form name="serviceReset" method="post">
                        <input type="hidden" name="serviceLogFileName" id="serviceLogFileName">
                        <input type="hidden" name="serviceId" id="serviceId">
                    </form>
			</div>


 		</div>
		<!--  end body -->

        </div>
        <!-- end doc -->
    </body>
</html>
