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
        <title>Add Group</title>
        <c:import url="/st/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
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
            <c:import url="/st/inc/header.jsp"/>

            <!--  this is the header of the page -->
            <c:import url="/st/inc/menu.jsp"/>
            <jsp:include page="/st/inc/breadcrumb.jsp">

                    <jsp:param name="bread" value="Users & Groups | Add Group" />

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

            <form action="addGroup.action" method="post" name="addGroup">
            <br><br>
                <table style="margin-left:10px">

                   <tr>
                       <td valign="top"><B>Group Name</B> <br> 
                       <input type="text" id="groupName" name ="groupName" class="textfield" value="${temporaryGroup.name}" maxlength="255" size="46"><br><br></td>
                   </tr>

                   <tr>
                       <td valign="top"><B>Description</B>  <br>
                       <textarea id="groupDescription" name ="groupDescription" cols="40" rows="5" maxlength="1023">${temporaryGroup.description}</textarea><br><br></td>
                   </tr>
                   
                   <tr>
                       <td>
                            <B>Permissions</B> <br>
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
                                                <Option SELECTED ID="${a.count}" value="${a.count}">${n.tabName}>
                                            </c:when>
                                            <c:otherwise>
                                                <Option ID="${a.count}" value="${a.count}">${n.tabName}
                                            </c:otherwise>
                                       </c:choose>
                                  </c:forEach>
                            </select>
                            <div class="smallText">
                                (CTRL click to select multiple permissions)
                            </div>
                       </td>
                   </tr>
                <tr><td>
               <br><br>
                <div align="left">
                      <button style="vertical-align:bottom;" class="xc_button_small" type="button" name="cancel" onclick="javascript:YAHOO.xc.mst.group.newGroup.cancel();">Cancel</button> &nbsp;&nbsp;&nbsp;
                      <button class="xc_button" type="button" name="addgroup" onclick="javascript:YAHOO.xc.mst.group.newGroup.addGroup();">Add Group</button>
                </div>
                </td></tr>
               </table>
          </form>

     </div>
            <!--  this is the footer of the page -->
            <c:import url="/st/inc/footer.jsp"/>       
 </div>
</body>
</html>