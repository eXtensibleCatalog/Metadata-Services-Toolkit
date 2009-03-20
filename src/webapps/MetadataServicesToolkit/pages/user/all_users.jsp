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
        
             <div class="errorMessage"> <s:fielderror /> </div>

             <div class="viewTable">
                    <table width="100%">
                        <thead>
                            <tr>
                                <td><a href="allUsers.action?isAscendingOrder=${!isAscendingOrder}&columnSorted=username">User Name</a></td>
                                <td>Account type</td>
                                <td><a href="allUsers.action?isAscendingOrder=${!isAscendingOrder}&columnSorted=last_login">Last Login</a></td>
                                <td><a href="allUsers.action?isAscendingOrder=${!isAscendingOrder}&columnSorted=fullname">Full Name</a></td>
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
                                    <td class="sortcolumn"><a style="text-decoration:none;color:black;" href="${url}"><U>${n.username}</U></a></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${n.server.type==4}">
                                                Local
                                            </c:when>
                                            <c:otherwise>
                                                LDAP
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${n.accountCreated}</td>
                                    <td>${n.fullName}</td>
                                    <td>
                                        <c:set var="adminFlag" value="${false}"/>
                                         <c:forEach var="k" items="${n.groups}" varStatus="c">
                                             <c:if test="${k.id==1}">
                                                 <c:set var="adminFlag" value="${true}"/>
                                             </c:if>
                                         </c:forEach>
                                         <c:choose>
                                             <c:when test="${adminFlag==true}">
                                                 <button disabled class="xc_button" type="button" name="deleteUser" onclick="javascript:YAHOO.xc.mst.user.deleteUser(${n.id});">Delete</button>
                                             </c:when>
                                             <c:otherwise>
                                                 <button class="xc_button" type="button" name="deleteUser" onclick="javascript:YAHOO.xc.mst.user.deleteUser(${n.id});">Delete</button>
                                             </c:otherwise>
                                         </c:choose>

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
