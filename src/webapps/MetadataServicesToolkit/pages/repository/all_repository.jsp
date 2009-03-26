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

        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>    
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/all_repositories.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
      
        
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
                    <div class="${errorType}">
                        <img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg">
                        <s:fielderror cssClass="errorMessage"/>
                    </div>
                 </c:if>
                <div id="error_div"></div>
                
                <div class="viewTable">
                    <table width="100%">
                        <thead>
                            <tr>
                                <td><a href="allRepository.action?isAscendingOrder=${!isAscendingOrder}&columnSorted=name">Name</a></td>
                                <td><a href="allRepository.action?isAscendingOrder=${!isAscendingOrder}&columnSorted=oai_provider_url">URL</a></td>
                                <td>Status</td>
                                <td><a href="allRepository.action?isAscendingOrder=${!isAscendingOrder}&columnSorted=last_harvest_end_time">Last Harvested</a></td>
                            </tr>
                        </thead>
                        <tbody>
                             <c:forEach var="n" items="${repositories}" varStatus="a">
                                  <c:choose>
                                        <c:when test="${(n.identify==true)&&(n.listSets==true)&&(n.service==true)&&(n.listFormats==true)}">
                                             <tr>
                                                <td class="sortcolumn"><a style="color:black;text-decoration:none;" href="/MetadataServicesToolkit/viewRepository.action?RepositoryId=<c:out value="${n.id}" />"><U><c:out value="${n.name}" /></U></a></td>
                                                <td><c:out value="${n.oaiProviderUrl}" /></td>
                                                <td>
                                                    <ul style="list-style:none;">
                                                        <li style="float:left;margin-top:-10px;"><div><img src="page-resources/img/tick.jpg"></div></li>
                                                        <li style="float:left;margin-top:-8px;"><div>Success</div></li>
                                                    </ul>
                                                </td>
                                                <c:choose>
                                                    <c:when test="${n.lastHarvestEndTime==null}">
                                                        <td>Never</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td>${n.lastHarvestEndTime}</td>
                                                    </c:otherwise>
                                                </c:choose>

                                             </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <tr bgcolor="#ffe7e3">
                                                <td class="sortcolumn"><a style="color:black;text-decoration:none;" href="/MetadataServicesToolkit/viewRepository.action?RepositoryId=<c:out value="${n.id}" />"><U><c:out value="${n.name}" /></U></a></td>
                                                <td><c:out value="${n.oaiProviderUrl}" /></td>
                                                <td>
                                                    <ul style="list-style:none;">
                                                        <li style="float:left;margin-top:-10px;"><div><img src="page-resources/img/error_triangle.jpg"></div></li>
                                                        <li style="float:left;margin-top:-8px;"><div>Error</div></li>
                                                    </ul>
                                                </td>
                                                <c:choose>
                                                    <c:when test="${n.lastHarvestEndTime==null}">
                                                        <td>Never</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td>${n.lastHarvestEndTime}</td>
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

        </div>
        <!-- end doc -->
    </body>
</html>
