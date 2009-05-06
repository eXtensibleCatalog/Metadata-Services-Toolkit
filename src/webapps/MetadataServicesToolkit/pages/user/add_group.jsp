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
        <title>Add Group</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/add_group.js"></SCRIPT>
             
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

                    <jsp:param name="bread" value="Users & Groups , Add Group" />

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

            <form action="addGroup.action" method="post" name="addGroup">
            <br><br>
                <table>

                   <tr>
                       <td valign="top">Group Name  &nbsp;&nbsp;</td>
                       <td><input type="text" id="groupName" name ="groupName" style="width:200px;height:25px;" value="${temporaryGroup.name}" maxlength="255"><br><br></td>
                   </tr>

                   <tr>
                       <td valign="top">Description  &nbsp;&nbsp;</td>
                       <td><textarea id="groupDescription" name ="groupDescription" cols="30" rows="5" value="${temporaryGroup.description}" maxlength="255"></textarea><br><br></td>
                   </tr>
                   
            
                </table>
               <br><br>

               <table>
                   <tr>
                       <td>
                            Permissions <br><br>
                            <select multiple size="8" id="permissionsSelected" name ="permissionsSelected" style="width:300px; height:125px;">
                             <c:forEach var="n" items="${tabNames}" varStatus="a">
                                      <c:set var="flag" value="${false}"/>
                                      <c:forEach var="m" items="${selectedPermissions}" varStatus="b">
                                          <c:if test="${a.count == m}">
                                              <c:set var="flag" value="${true}"/>
                                          </c:if>
                                      </c:forEach>
                                       <c:choose>
                                            <c:when test="${flag==true}">
                                                <Option SELECTED ID="<c:out value="${a.count}" />" value="<c:out value="${a.count}" />"><c:out value="${n.tabName}" />
                                            </c:when>
                                            <c:otherwise>
                                                <Option ID="<c:out value="${a.count}" />" value="<c:out value="${a.count}" />"><c:out value="${n.tabName}" />
                                            </c:otherwise>
                                       </c:choose>
                                  </c:forEach>
                            </select>
                       </td>
                   </tr>
               </table>
               <br><br>
                <div align="left">
                      <button style="vertical-align:bottom;" class="xc_button_small" type="button" name="cancel" onclick="javascript:YAHOO.xc.mst.user.cancel();">Cancel</button> &nbsp;&nbsp;&nbsp;
                      <button class="xc_button" type="button" name="addgroup" onclick="javascript:YAHOO.xc.mst.user.addGroup();">Add Group</button>
                </div>

          </form>
     </div>
 </div>
</body>
</html>