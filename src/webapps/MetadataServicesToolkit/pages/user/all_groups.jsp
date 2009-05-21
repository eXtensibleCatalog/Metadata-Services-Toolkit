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
        <title>All Groups</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/all_groups.js"></SCRIPT>

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

                    <jsp:param name="bread" value="Users & Groups , All Groups" />

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

              <div class="viewTable">
                    <table width="100%">
                        <thead>
                            <tr>
                                <td>
                                    
                                        <c:if test="${columnSorted!='GroupName'}">
                                             <c:url var="groupSortUrl" value="allGroups.action">
                                               <c:param name="isAscendingOrder" value="true"/>
                                               <c:param name="columnSorted" value="GroupName"/>
                                             </c:url>
                                              <a href="${groupSortUrl}">Group Name</a>
                                         </c:if>

                                         <c:if test="${columnSorted=='GroupName'}">
                                               <c:url var="groupSortUrl" value="allGroups.action">
                                                 <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                                 <c:param name="columnSorted" value="GroupName"/>
                                               </c:url>

                                               <a href="${groupSortUrl}">Group Name</a>

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
                                <td>Members</td>
                                <td>
                                        <c:if test="${columnSorted!='GroupDescription'}">
                                             <c:url var="groupSortUrl" value="allGroups.action">
                                               <c:param name="isAscendingOrder" value="true"/>
                                               <c:param name="columnSorted" value="GroupDescription"/>
                                             </c:url>
                                              <a href="${groupSortUrl}">Group Description</a>
                                         </c:if>

                                         <c:if test="${columnSorted=='GroupDescription'}">
                                               <c:url var="groupSortUrl" value="allGroups.action">
                                                 <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                                 <c:param name="columnSorted" value="GroupDescription"/>
                                               </c:url>

                                               <a href="${groupSortUrl}">Group Decription</a>

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
                                <td>Delete</td>
                            </tr>
                        </thead>
                        <tbody>
                             <c:forEach var="group" items="${groupList}" varStatus="groupCount">
                                 <tr>
                                            <c:set var="classColumn" value="plainColumn"/>
                                            <c:if test="${columnSorted=='GroupName'}">
                                                <c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
                                    <td class="${classColumn}"><a href="/MetadataServicesToolkit/viewEditGroup.action?groupId=<c:out value="${group.id}"/>"><U>${group.name}</U></a></td>
                                    <td><a href="showGroupMembers.action?groupId=${group.id}&isAscendingOrder=true&columnSorted=UserName"><U>${group.memberCount}</U></a></td>
                                            <c:set var="classColumn" value="plainColumn"/>
                                            <c:if test="${columnSorted=='GroupDescription'}">
                                                <c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
                                    <td class="${classColumn}">${group.description}</td>
                                    <c:choose>
                                        <c:when test="${group.id==1}">
                                            <td><button disabled class="xc_button_disabled" type="button" name="deleteGroup" onclick="javascript:YAHOO.xc.mst.group.removeGroup.deleteGroup(${group.id});">Delete</button></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><button class="xc_button" type="button" name="deleteGroup" onclick="javascript:YAHOO.xc.mst.group.removeGroup.deleteGroup(${group.id});">Delete</button></td>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                 </tr>
                            </c:forEach>
                        </tbody>
                    </table>
			</div>
            <form action="deleteGroup.action" method="post" name="deleteGroup">
                <input type="hidden" name="groupId" id="groupId">
            </form>
        </div>
   </div>

</body>
</html>
