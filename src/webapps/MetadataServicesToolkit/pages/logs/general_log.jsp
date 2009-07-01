<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="mst" uri="mst-tags"%>

<!--  document type -->
<c:import url="/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">


<html>
    <head>
        <title>General Logs</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/general_log.js"></SCRIPT>
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

                    <jsp:param name="bread" value="Logs , General" />

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

                <div class="viewTable" style="margin-top:10px;">
                    <div align="right" style="margin-bottom:10px;">
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.logs.general.resetAll();" name="next">Reset All *</button>
                    </div>

                    <table width="100%">
                        <thead>
                            <tr>
                                <td>
                                   <c:if test="${columnSorted!='LogFileName'}">
                                         <c:url var="logSortUrl" value="generalLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="LogFileName"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Log Type</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='LogFileName'}">
                                       <c:url var="logSortUrl" value="generalLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="LogFileName"/>
                                       </c:url>

                                       <a href="${logSortUrl}">Log Type</a>

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
                                         <c:url var="logSortUrl" value="generalLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="Warnings"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Warnings</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='Warnings'}">
                                       <c:url var="logSortUrl" value="generalLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="Warnings"/>
                                       </c:url>

                                       <a href="${logSortUrl}">Warnings</a>

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
                                         <c:url var="logSortUrl" value="generalLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="Errors"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Errors</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='Errors'}">
                                       <c:url var="logSortUrl" value="generalLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="Errors"/>
                                       </c:url>

                                       <a href="${logSortUrl}">Errors</a>

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
                                         <c:url var="logSortUrl" value="generalLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="LastLogReset"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Last Reset Date</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='LastLogReset'}">
                                       <c:url var="logSortUrl" value="generalLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="LastLogReset"/>
                                       </c:url>

                                       <a href="${logSortUrl}">Last Reset Date</a>

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
                              <c:forEach var="log" items="${logList}" varStatus="count">
                                  <tr>
                                           <c:set var="classColumn" value="plainColumn"/>
                                           <c:if test="${columnSorted=='LogFileName'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                           </c:if>
                                      <td class="${classColumn}">
                                          <a onclick="javascript:YAHOO.xc.mst.logs.general.downloadFile(${log.id});"><U>${log.logFileName}</U></a>
                                      </td>
                                            <c:set var="classColumn" value="plainColumn"/>
                                            <c:if test="${columnSorted=='Warnings'}">
                                                <c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
                                      <td class="${classColumn}">
                                          ${log.warnings}
                                      </td>
                                            <c:set var="classColumn" value="plainColumn"/>
                                            <c:if test="${columnSorted=='Errors'}">
                                                <c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
                                      <td class="${classColumn}">
                                          ${log.errors}
                                      </td>
                                      <td>
                                          <button onclick="javascript:YAHOO.xc.mst.logs.general.resetFunction('${log.id}');" class="xc_button" type="button" name="reset">Reset</button>
                                      </td>
                                            <c:set var="classColumn" value="plainColumn"/>
                                            <c:if test="${columnSorted=='LastLogReset'}">
                                                <c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
                                            <c:choose>
                                                <c:when test="${log.lastLogReset!=null}">
                                                    <td class="${classColumn}">
                                                        ${log.lastLogReset}
                                                    </td>
                                               </c:when>
                                               <c:otherwise>
                                                    <td class="${classColumn}">
                                                        Never
                                                    </td>
                                               </c:otherwise>
                                            </c:choose>
                                      
                                  </tr>
                              </c:forEach>
                        </tbody>
                    </table>
                    * Reset all will reset the statistic to 0 and move the log file to the archives directory
                     <div align="right" style="margin-bottom:10px;">
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.logs.general.resetAll();" name="next">Reset All*</button>
                    </div>
                     <form name="generalLogReset" method="post">
                        <input type="hidden" name="logId" id="logId">
                    </form>
			</div>


 		</div>
		<!--  end body -->
            <!--  this is the footer of the page -->
            <c:import url="/inc/footer.jsp"/>  
        </div>
        <!-- end doc -->
    </body>
</html>
