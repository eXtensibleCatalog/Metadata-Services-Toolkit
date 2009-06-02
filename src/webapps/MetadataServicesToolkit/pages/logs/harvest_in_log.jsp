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
        <title>Harvest In Logs</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/harvest_in_log.js"></SCRIPT>
        
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

                    <jsp:param name="bread" value="Logs,Harvest-In" />

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
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.logs.harvestIn.resetAll();" name="next">Reset All *</button>
                    </div>
                    <table width="100%">
                        <thead>
                            <tr>
                                <td></td>
                                <td></td>
                                <td>
                                    <c:if test="${columnSorted!='RepositoryName'}">
                                         <c:url var="logSortUrl" value="harvestInLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="RepositoryName"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Repositories</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='RepositoryName'}">
                                       <c:url var="logSortUrl" value="harvestInLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="RepositoryName"/>
                                       </c:url>

                                       <a href="${logSortUrl}">Repositories</a>

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
                                    <c:if test="${columnSorted!='LastHarvestEndTime'}">
                                         <c:url var="logSortUrl" value="harvestInLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="LastHarvestEndTime"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Last Harvest Date</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='LastHarvestEndTime'}">
                                       <c:url var="logSortUrl" value="harvestInLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="LastHarvestEndTime"/>
                                       </c:url>

                                       <a href="${logSortUrl}">Last Harvest Date</a>

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
                                    <c:if test="${columnSorted!='RecordsAdded'}">
                                         <c:url var="logSortUrl" value="harvestInLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="RecordsAdded"/>
                                         </c:url>
                                          <a href="${logSortUrl}">#Records Added</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='RecordsAdded'}">
                                       <c:url var="logSortUrl" value="harvestInLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="RecordsAdded"/>
                                       </c:url>

                                       <a href="${logSortUrl}">#Records Added</a>

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
                                    <c:if test="${columnSorted!='RecordsReplaced'}">
                                         <c:url var="logSortUrl" value="harvestInLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="RecordsReplaced"/>
                                         </c:url>
                                          <a href="${logSortUrl}">#Records Replaced</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='RecordsReplaced'}">
                                       <c:url var="logSortUrl" value="harvestInLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="RecordsReplaced"/>
                                       </c:url>

                                       <a href="${logSortUrl}">#Records Replaced</a>

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
                                         <c:url var="logSortUrl" value="harvestInLog.action">
                                           <c:param name="isAscendingOrder" value="true"/>
                                           <c:param name="columnSorted" value="LastLogReset"/>
                                         </c:url>
                                          <a href="${logSortUrl}">Last Reset</a>
                                     </c:if>

                                     <c:if test="${columnSorted=='LastLogReset'}">
                                       <c:url var="logSortUrl" value="harvestInLog.action">
                                         <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                         <c:param name="columnSorted" value="LastLogReset"/>
                                       </c:url>

                                       <a href="${logSortUrl}">Last Reset</a>

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
                              <c:forEach var="log" items="${providerList}" varStatus="count">
								 <c:url var="browseRecordsAction" value="browseRecords.action">
									  <c:param name="query" value="${log.name}"/>
								 </c:url>                              	
                                  <tr>
                                      <td><a style="cursor:pointer;"onclick="javascript:YAHOO.xc.mst.logs.harvestIn.displayOAIRequest(${log.id});"><U>OAI Request</U></a></td>
                                      <td><a href="${browseRecordsAction}"><U>Browse Records</U></a></td>
                                           <c:set var="classColumn" value="plainColumn"/>
                                           <c:if test="${columnSorted=='RepositoryName'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                           </c:if>
                                      <td class="${classColumn}">
                                          <a style="cursor:pointer;" onclick="javascript:YAHOO.xc.mst.logs.harvestIn.downloadFile(${log.id});"><U>${log.name}</U></a>
                                      </td>
                                           <c:set var="classColumn" value="plainColumn"/>
                                           <c:if test="${columnSorted=='LastHarvestEndTime'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                           </c:if>
                                      <td class="${classColumn}">
                                          ${log.lastHarvestEndTime}
                                      </td>
                                           <c:set var="classColumn" value="plainColumn"/>
                                           <c:if test="${columnSorted=='RecordsAdded'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                           </c:if>
                                      <td class="classColumn">
                                          ${log.recordsAdded}
                                      </td>
                                           <c:set var="classColumn" value="plainColumn"/>
                                           <c:if test="${columnSorted=='RecordsReplaced'}">
                                               <c:set var="classColumn" value="sortColumn"/>
                                           </c:if>
                                      <td class="${classColumn}">
                                          ${log.recordsReplaced}
                                      </td>
                                      <td>
                                          <button class="xc_button" type="button" name="reset" onclick="javascript:YAHOO.xc.mst.logs.harvestIn.resetFunction('${log.logFileName}','${log.id}')">Reset</button>
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
                    * Reset will reset the statistic to 0 and move the log file to the archives directory
                     <div align="right" style="margin-bottom:10px;">
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.logs.harvestIn.resetAll();" name="next">Reset All</button>
                    </div>
                    <form name="harvestInReset" method="post">
                        <input type="hidden" name="harvestInLogFileName" id="harvestInLogFileName">
                        <input type="hidden" name="providerId" id="providerId">
                    </form>
			</div>


 		</div>
		<!--  end body -->

        </div>
        <!-- end doc -->
    </body>
</html>
