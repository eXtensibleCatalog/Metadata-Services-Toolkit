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
        <title>All Repositories</title>
        <c:import url="/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
		<LINK href="page-resources/css/bodylayout.css" rel="stylesheet" type="text/css">

        <script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/yahoo/yahoo-min.js" ></script>
        <script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/event/event-min.js" ></script>
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
	  <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/all_repositories.js"></SCRIPT>


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

                    <jsp:param name="bread" value="Repository , All Repositories" />

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
                        ${fieldErrors}
                        <span class="errorText">
                            <mst:fielderror error="${fieldErrors}">
                            </mst:fielderror>
                        </span>
                    </div>
                    </div>
                 </c:if>
                <div id="error_div"></div>

                <div class="clear">&nbsp;</div>

                <div class="viewTable">
                    <table width="100%">
                        <thead>
                            <tr>
                                <td>
                                    <div id="Div1">
                                                <c:if test="${columnSorted!='RepositoryName'}">
                                                     <c:url var="repositorySortUrl" value="allRepository.action">
                                                       <c:param name="isAscendingOrder" value="true"/>
                                                       <c:param name="columnSorted" value="RepositoryName"/>
                                                     </c:url>
                                                      <a href="${repositorySortUrl}">Name</a>
                                                 </c:if>

                                                 <c:if test="${columnSorted=='RepositoryName'}">
                                                   <c:url var="repositorySortUrl" value="allRepository.action">
                                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                                     <c:param name="columnSorted" value="RepositoryName"/>
                                                   </c:url>

                                                   <a href="${repositorySortUrl}">Name</a>

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
                                     <div id="Div2">
                                          <c:if test="${columnSorted!='RepositoryURL'}">
                                             <c:url var="repositorySortUrl" value="allRepository.action">
                                               <c:param name="isAscendingOrder" value="true"/>
                                               <c:param name="columnSorted" value="RepositoryURL"/>
                                             </c:url>
                                             <a href="${repositorySortUrl}">URL</a>
                                         </c:if>

                                        <c:if test="${columnSorted=='RepositoryURL'}">
                                           <c:url var="repositorySortUrl" value="allRepository.action">
                                             <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                             <c:param name="columnSorted" value="RepositoryURL"/>
                                           </c:url>

                                           <a href="${repositorySortUrl}">URL</a>
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
                                <td>
                                     <div id="Div3">
                                          <c:if test="${columnSorted!='LastHarvestEndTime'}">
                                             <c:url var="repositorySortUrl" value="allRepository.action">
                                               <c:param name="isAscendingOrder" value="true"/>
                                               <c:param name="columnSorted" value="LastHarvestEndTime"/>
                                             </c:url>
                                             <a href="${repositorySortUrl}">Last Harvested</a>
                                         </c:if>

                                        <c:if test="${columnSorted=='LastHarvestEndTime'}">
                                           <c:url var="repositorySortUrl" value="allRepository.action">
                                             <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                             <c:param name="columnSorted" value="LastHarvestEndTime"/>
                                           </c:url>

                                           <a href="${repositorySortUrl}">Last Harvested</a>
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
                            </tr>
                        </thead>
                        <tbody>
                             <c:forEach var="repository" items="${repositories}" varStatus="repositoryCount">
                                  <c:choose>
                                        <c:when test="${(repository.identify==true)&&(repository.listSets==true)&&(repository.service==true)&&(repository.listFormats==true)}">
                                             <tr>
                                                <c:set var="classColumn" value="plainColumn"/>
                                                <c:if test="${columnSorted=='RepositoryName'}">
                                                     <c:set var="classColumn" value="sortColumn"/>
                                                </c:if>
                                                <td class="${classColumn}">

                                                        <a href="/MetadataServicesToolkit/viewRepository.action?RepositoryId=<c:out value="${repository.id}" />"><c:out value="${repository.name}" /></a>

                                                </td>
                                                <c:set var="classColumn" value="plainColumn"/>
                                                <c:if test="${columnSorted=='RepositoryURL'}">
                                                     <c:set var="classColumn" value="sortColumn"/>
                                                </c:if>
                                                <td class="${classColumn}">
                                                     <c:out value="${repository.oaiProviderUrl}" />
                                                </td>
                                                <td>
                                                    <img src="page-resources/img/tick.png">
                                                    <span style="position:relative;top:-2px;">Success</span>

                                                </td>
                                                <c:choose>
                                                    <c:when test="${repository.lastHarvestEndTime==null}">
                                                         <c:set var="classColumn" value="plainColumn"/>
                                                           <c:if test="${columnSorted=='LastHarvestEndTime'}">
                                                                <c:set var="classColumn" value="sortColumn"/>
                                                           </c:if>
                                                        <td class="${classColumn}">
                                                               Never
                                                        </td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="classColumn" value="plainColumn"/>
                                                        <c:if test="${columnSorted=='LastHarvestEndTime'}">
                                                             <c:set var="classColumn" value="sortColumn"/>
                                                        </c:if>
                                                        <td class="${classColumn}">
                                                             ${repository.lastHarvestEndTime}
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>

                                             </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <tr class="errorColumn">
                                                <c:set var="classColumn" value="errorColumn"/>
                                                <c:if test="${columnSorted=='RepositoryName'}">
                                                     <c:set var="classColumn" value="sortColumn"/>
                                                </c:if>
                                                <td class ="${classColumn}">

                                                        <a href="/MetadataServicesToolkit/viewRepository.action?RepositoryId=<c:out value="${repository.id}" />"><c:out value="${repository.name}" /></a>

                                                </td>
                                                <c:set var="classColumn" value="errorColumn"/>
                                                <c:if test="${columnSorted=='RepositoryURL'}">
                                                     <c:set var="classColumn" value="sortColumn"/>
                                                </c:if>
                                                <td class="${classColumn}">
                                                     <c:out value="${repository.oaiProviderUrl}" />
                                                </td>
                                                <td>
								   <span style="position:relative;top:2px;">
                                                     <img src="page-resources/img/error_triangle.png">
								     <span style="position:relative;top:-3px;">Error</span>
                                                   </span>
                                                </td>
                                                <c:choose>
                                                    <c:when test="${repository.lastHarvestEndTime==null}">
                                                         <c:set var="classColumn" value="v"/>
                                                           <c:if test="${columnSorted=='LastHarvestEndTime'}">
                                                                <c:set var="classColumn" value="sortColumn"/>
                                                           </c:if>
                                                        <td class="${classColumn}">
                                                               Never
                                                        </td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="classColumn" value="errorColumn"/>
                                                        <c:if test="${columnSorted=='LastHarvestEndTime'}">
                                                             <c:set var="classColumn" value="sortColumn"/>
                                                        </c:if>
                                                        <td class="${classColumn}">
                                                             ${repository.lastHarvestEndTime}
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>
                                             </tr>
                                        </c:otherwise>
                                    </c:choose>
                            </c:forEach>
                        </tbody>
                    </table>
			</div>


 		</div>
		<!--  end body -->
            <!--  this is the footer of the page -->
            <c:import url="/inc/footer.jsp"/>  
        </div>
        <!-- end doc -->
    </body>
</html>
