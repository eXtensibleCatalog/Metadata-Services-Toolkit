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
        <title>Configure LDAP Server</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/add_LDAP.js"></SCRIPT>

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

                    <jsp:param name="bread" value="Configuration , LDAP Server" />

                </jsp:include>
            </div>
            <!--  end header -->

            <!-- body -->
            <div id="bd">
                

                <!-- Display of error message -->
                <c:if test="${errorType != null}">
                    <div class="${errorType}">
                        <img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg">
                        <c:if test="${errorType == 'error'}">
                        	<s:fielderror cssClass="errorMessage"/>
                       	</c:if>
                        <c:if test="${errorType == 'info'}">
                        	<div class="jsErrorMessage"> ${message}</div>
                        </c:if>
                    </div>
                 </c:if> 
                 <div id="error_div"></div>
                 <div class="clear">&nbsp;</div>

                <form action="addLDAP.action" method="post" name="addLDAP">

                   <table>

                        <tr>
                            <td valign="top"> <B>Display Name</B>  &nbsp;&nbsp;</td>
                            <td><input type="text" id="displayName" name ="displayName" style="width:200px;height:25px;" value="${server.name}" maxlength="255"><br><br></td>
                        </tr>

                        <tr>
                            <td valign="top"><B>Server URL</B> &nbsp;&nbsp;</td>
                            <td><input type="text" id="serverURL" name ="serverURL" style="width:200px;height:25px;" value="${server.url}" maxlength="255"><br><br></td>
                        </tr>
                                      
                        <tr>
                            <td valign="top"> <B>Port</B> &nbsp;&nbsp;</td>
                            <td><input type="text" id="port" name ="port" style="width:200px;height:25px;" value="${server.port}" maxlength="255"><br><br></td>
                        </tr>

                        <tr>
                            <td valign="top"><B>Username Fieldname</B> &nbsp;&nbsp;</td>
                            <td><input type="text" id="userNameAttribute" name ="userNameAttribute" style="width:200px;height:25px;" value="${server.userNameAttribute}" maxlength="255"><br><br></td>
                        </tr>

                        <tr>
                            <td valign="top"> <B>LDAP start</B> &nbsp;&nbsp;</td>
                            <td><input type="text" id="startLocation" name ="startLocation" style="width:200px;height:25px;" value="${server.startLocation}" maxlength="255"></td>
                        </tr>


                   </table>
                   <br><br><br><br>
                       
                    <div align="left">
                        <!-- <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.configuration.MyObject.cancel();" name="cancel">Cancel</button> &nbsp;&nbsp;&nbsp; -->
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.configuration.MyObject.addLDAP();" name="addLDAP">Update LDAP Server</button>
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.configuration.MyObject.deleteLDAP();" name="deleteLDAP">Delete LDAP Server</button>
                    </div>

                </form>
            </div>
        </div>
    </body>
</html>

