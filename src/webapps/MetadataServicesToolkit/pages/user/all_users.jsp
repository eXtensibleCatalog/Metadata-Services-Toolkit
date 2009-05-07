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
        <title>All Users</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/all_users.js"></SCRIPT>

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

                    <jsp:param name="bread" value="Users & Groups , All Users" />

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
                                <td>
                                        <c:if test="${columnSorted!='UserName'}">
                                             <c:url var="userSortUrl" value="allUsers.action">
                                               <c:param name="isAscendingOrder" value="true"/>
                                               <c:param name="columnSorted" value="UserName"/>
                                             </c:url>
                                              <a href="${userSortUrl}">User Name</a>
                                         </c:if>

                                         <c:if test="${columnSorted=='UserName'}">
                                           <c:url var="userSortUrl" value="allUsers.action">
                                             <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                             <c:param name="columnSorted" value="UserName"/>
                                           </c:url>

                                           <a href="${userSortUrl}">User Name</a>

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
                                <td>Account type</td>
                                <td>
                                        <c:if test="${columnSorted!='LastLogin'}">
                                             <c:url var="userSortUrl" value="allUsers.action">
                                               <c:param name="isAscendingOrder" value="true"/>
                                               <c:param name="columnSorted" value="LastLogin"/>
                                             </c:url>
                                              <a href="${userSortUrl}">Last Login</a>
                                         </c:if>

                                         <c:if test="${columnSorted=='LastLogin'}">
                                           <c:url var="userSortUrl" value="allUsers.action">
                                             <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                             <c:param name="columnSorted" value="LastLogin"/>
                                           </c:url>

                                           <a href="${userSortUrl}">Last Login</a>

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
                                        <c:if test="${columnSorted!='FirstName'}">
                                             <c:url var="userSortUrl" value="allUsers.action">
                                               <c:param name="isAscendingOrder" value="true"/>
                                               <c:param name="columnSorted" value="FirstName"/>
                                             </c:url>
                                              <a href="${userSortUrl}">First Name</a>
                                         </c:if>

                                         <c:if test="${columnSorted=='FirstName'}">
                                           <c:url var="userSortUrl" value="allUsers.action">
                                             <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                             <c:param name="columnSorted" value="FirstName"/>
                                           </c:url>

                                           <a href="${userSortUrl}">First Name</a>

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
                                        <c:if test="${columnSorted!='LastName'}">
                                             <c:url var="userSortUrl" value="allUsers.action">
                                               <c:param name="isAscendingOrder" value="true"/>
                                               <c:param name="columnSorted" value="LastName"/>
                                             </c:url>
                                              <a href="${userSortUrl}">Last Name</a>
                                         </c:if>

                                         <c:if test="${columnSorted=='LastName'}">
                                           <c:url var="userSortUrl" value="allUsers.action">
                                             <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                             <c:param name="columnSorted" value="LastName"/>
                                           </c:url>

                                           <a href="${userSortUrl}">Last Name</a>

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
                             <c:forEach var="n" items="${userList}" varStatus="a">

                                 <tr>
                                     <c:choose>
                                         <c:when test="${n.server.name=='Local'}">
                                             <c:set var="url" value="/MetadataServicesToolkit/viewEditLocalUser.action?userId=${n.id}"/>
                                         </c:when>
                                         <c:otherwise>
                                              <c:set var="url" value="/MetadataServicesToolkit/viewEditLDAPUser.action?userId=${n.id}"/>
                                         </c:otherwise>
                                    </c:choose>

                                                <c:set var="classColumn" value="plainColumn"/>
                                                <c:if test="${columnSorted=='UserName'}">
                                                    <c:set var="classColumn" value="sortColumn"/>
                                                </c:if>
                                    <td class="${classColumn}"><a href="${url}"><U>${n.username}</U></a></td>
                                    <td>
                                        ${n.server.type}
                                    </td>
                                                <c:set var="classColumn" value="plainColumn"/>
                                                <c:if test="${columnSorted=='LastLogin'}">
                                                    <c:set var="classColumn" value="sortColumn"/>
                                                </c:if>
                                    <td class="${classColumn}">${n.accountCreated}</td>
                                                <c:set var="classColumn" value="plainColumn"/>
                                                <c:if test="${columnSorted=='FirstName'}">
                                                    <c:set var="classColumn" value="sortColumn"/>
                                                </c:if>
                                    <td class="${classColumn}">${n.firstName}</td>
                                                <c:set var="classColumn" value="plainColumn"/>
                                                <c:if test="${columnSorted=='LastName'}">
                                                    <c:set var="classColumn" value="sortColumn"/>
                                                </c:if>
                                    <td class="${classColumn}">${n.lastName}</td>
                                    <td>
                                        <c:if test="${n.username == 'admin'}">
                                        	<button class="xc_button_disabled" disabled="true" type="submit" name="deleteUser" onclick="javascript:YAHOO.xc.mst.user.deleteUser(${n.id});">Delete</button>
                                        </c:if>
                                        <c:if test="${n.username != 'admin'}">
                                                 <button class="xc_button" type="button" name="deleteUser" onclick="javascript:YAHOO.xc.mst.user.deleteUser(${n.id});">Delete</button>
                                        </c:if>
                                            
                                    </td>
                                 </tr>
                            </c:forEach>
                        </tbody>
                    </table>
			</div>
             <form name="deleteUser" method="post" action= "deleteUser.action">
                <input type="hidden" id="userId" name="userId"/>
            </form>
        </div>
   </div>
</body>
</html>
