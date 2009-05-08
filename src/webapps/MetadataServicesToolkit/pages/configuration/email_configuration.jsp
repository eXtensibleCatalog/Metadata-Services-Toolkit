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
        <title>Email Server Configuration</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/email_configuration.js"></SCRIPT>

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

                    <jsp:param name="bread" value="Configuration , Email Server" />

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
                        <c:if test="${errorType == 'error'}">
                        	<s:fielderror cssClass="errorMessage"/>
                       	</c:if>
                        <c:if test="${errorType == 'info'}">
                        	<div class="jsErrorMessage"> ${message}</div>
                        </c:if>            
                     </div>
                     </div>
                 </c:if>
                 <div id="error_div"></div>
                 <div class="clear">&nbsp;</div>

                <form action="changeEmailConfig.action" method="post" name="emailConfig">


                    <h4><b>SMTP</b></h4>

                    <table>

                        <tr>
                            <td>&nbsp;Outgoing Mail Server(SMTP):  &nbsp;&nbsp;</td>
                            <td><input type="text" id="emailServerAddress" name ="emailServerAddress" class="textBoxClass" value="${emailConfig.emailServerAddress}" maxlength="255"><br><br></td>
                        </tr>

                       <tr>
                           <td>From Address &nbsp;&nbsp;</td>
                           <td><input type="text" id="fromAddress" name ="fromAddress" class="textBoxClass" value="${emailConfig.fromAddress}" maxlength="255"><br><br></td>
                       </tr>

                       <tr>
                           <td>Password &nbsp;&nbsp;</td>
                           <td><input type="text" id="password" name ="password" class="textBoxClass" value="${emailConfig.password}" maxlength="255"><br><br></td>
                       </tr>

                       <tr>
                           <td> Port Number &nbsp;&nbsp;</td>
                           <td><input type="text" id="port" name ="port" class="textBoxClass" value="${emailConfig.portNumber}" maxlength="255"></td>
                       </tr>

                       <tr>
                           <td> Encrypted Connection &nbsp;&nbsp;</td>
                           <td>
                               <SELECT style="width:200px; height:20px;" ID="encryptedConnection" name="encryptedConnection" >
                                  <OPTION SELECTED value="none">None</OPTION>
                                  <OPTION value="ssl">SSL</OPTION>
                                  <OPTION value="tls">TLS</OPTION>
                                  <OPTION value="auto">AUTO</OPTION>
                                </SELECT>
                                <br><br>
                           </td>
                       </tr>

                        <tr>
                            <td> Timeout &nbsp;&nbsp;</td>
                            <td> <input type="text" id="timeout" name ="timeout" class="textBoxClass" value="${emailConfig.timeout}" maxlength="255"><br><br></td>
                        </tr>

                       

                    </table>
                    <br><br><br>

                    <div align="left">
                        <!-- <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.configuration.cancel();" name="cancel">Cancel</button> &nbsp;&nbsp;&nbsp; -->
                        <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.configuration.EmailServer.changeEmailConfig();" name="changeEmailConfig">Change Email Configuration</button>
                    </div>

                </form>
            </div>
        </div>

</body>
</html>