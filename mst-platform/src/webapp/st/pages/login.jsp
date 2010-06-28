<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="mst" uri="mst-tags"%>

<!--  document type -->
<c:import url="/st/inc/doctype-frag.jsp"/>

<html>
    <head>
        <title>XC Metadata Services Toolkit</title>
        <c:import url="/st/inc/meta-frag.jsp"/>
        
        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">

        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
    	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>     
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script> 

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/login.js"></SCRIPT>
    </head>
    
    <body class="yui-skin-sam">
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">  

		<!-- page header - this uses the yahoo page styling -->
		<div id="hd">
   
            <!--  this is the header of the page -->
            <c:import url="/st/inc/header.jsp"/>
              <div id="mainMenu" class="yuimenubar yuimenubarnav">
                <div class="bd">
                    <ul class="first-of-type">
                        <span class="wrenchImg">&nbsp;</span>
                    </ul>
                </div>
             </div>

            <!--  this is the header of the page -->
            <c:import url="/st/inc/menu.jsp"/>
            <div style="height:10px;">

            </div>
            <jsp:include page="/st/inc/breadcrumb.jsp">

                    <jsp:param name="bread" value="User Login" />

            </jsp:include>
 		</div>
		<!--  end header -->
		
		<!-- body -->
		<div id="bd">
   			<!-- Begin - This is required to identify that the response is a login screen -->
			<div style="display:none">LoginPage</div>
			<!-- End - This is required to identify that the response is a login screen -->
			 <!-- Display of error message -->
                 <c:if test="${errorType != null}">
                    <div id="server_error_div">
                    <div id="server_message_div" class="${errorType}">
                        <span><img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg"></span>
                        <!--s:fielderror cssClass="errorMessage"/-->
                        <span class="errorText">
                            <mst:fielderror error="${fieldErrors}">
                            </mst:fielderror>
                        </span>
                        
                    </div>
                    </div>
                 </c:if>

            <div id="error_div"></div>

                <div class="clear">&nbsp;</div>
			
			<form name="loginForm" action="login.action" method="post">
				<table class="basicTable" align="left">
					
					<tr>
						<td class="label"> Login server : </td>
						<td class="input"> 
							<select name="serverName" onChange="javascript:YAHOO.xc.mst.login.displayForgotPasswordURL(this);">
								<c:forEach var="server" items="${servers}">
									<c:if test="${server.name == serverName}">
										<option value="${server.name}" selected>${server.name}</option>
									</c:if>
									<c:if test="${server.name != serverName}">
										<option value="${server.name}">${server.name}</option>
									</c:if>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td class="label"> User name : </td>
						<td class="input"> <input type="text" name="userName" value="${userName}"/> </td>
					</tr>
					<tr>
						<td class="label"> Password : </td>
						<td class="input"> <input type="password" name="password" value=""/> </td>
					</tr>
					<c:if test="${!configurationError}">
						<tr>
							<td colspan="2" align="center">
							<button type="submit" class="xc_button" name="submit">&nbsp;&nbsp;Login&nbsp;&nbsp;</button>
							 </td>
						</tr>
						<tr>
							<td colspan="2" align="center"> <a href="viewUserRegisteration.action">New User Registeration</a>&nbsp;&nbsp;&nbsp;&nbsp;  
							<span id="forgot_password_url">
								&nbsp;
							</span>						
							<div id="forgot_password_local">
								   | 
								&nbsp;&nbsp;&nbsp;&nbsp;<a href="viewForgotPassword.action">Forgot Password</a>   
							</div>

							<div id="forgot_password_ldap">
								<c:forEach var="server" items="${servers}">
									<c:if test="${server.name != 'Local'}">
										<c:if test="${server.showForgotPasswordLink == 'true'}">
											| &nbsp;&nbsp;&nbsp;&nbsp;<a href="${server.forgotPasswordUrl}">Forgot Password</a>   
										</c:if>

									</c:if>
								</c:forEach>
							</div>


							</td>


						</tr>
					</c:if>
					<c:if test="${configurationError}">
						
						<tr>
							<td colspan="2" align="center">
							<button disabled type="button" class="xc_button_disabled" name="submit">&nbsp;&nbsp;Login&nbsp;&nbsp;</button>
							 </td>
						</tr>
						<tr>
							<td colspan="2" align="center" style="color:grey">New User Registeration&nbsp;&nbsp;&nbsp;&nbsp;  
								   | 
								&nbsp;&nbsp;&nbsp;&nbsp;Forgot Password


							</td>


						</tr>
						
					</c:if>

				</table>
			</form>
			

 		</div>
		<!--  end body -->		

            <!--  this is the footer of the page -->
            <c:import url="/st/inc/footer.jsp"/>           
        </div>
        <!-- end doc -->
    </body>
</html>

    
