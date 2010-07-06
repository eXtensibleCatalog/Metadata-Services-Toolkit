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
        <title>Change Password</title>
        <c:import url="/st/inc/meta-frag.jsp"/>
        
        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >       
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
    	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>          
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script>         
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/change_password.js"></SCRIPT>
    </head>
    
    <body class="yui-skin-sam">
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">  

	<!-- page header - this uses the yahoo page styling -->
	<div id="hd">
	     <c:if test="${user.groups == '[]'}">
		    <c:import url="/st/inc/header.jsp"/>
		      <div id="mainMenu" class="yuimenubar yuimenubarnav">
			<div class="bd">
			    <ul class="first-of-type">
				<span class="wrenchImg">&nbsp;</span>
			    </ul>
			</div>
		     </div>

		    <div style="height:10px;">
	            </div>
	     
	     </c:if>
	    
	    <c:if test="${user.groups != '[]'}">
	     
		    <!--  this is the header of the page -->
		    <c:import url="/st/inc/header.jsp"/>

		    <!--  this is the header of the page -->
		    <c:import url="/st/inc/menu.jsp"/>
	    </c:if>
            <jsp:include page="/st/inc/breadcrumb.jsp">
                    <jsp:param name="bread" value="Change Password" />
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
   			
   			<form name="changePasswordForm" method="post">
   			
				<table class="basicTable">
				<tr>
                    <td> <b>Old password</b> <br>
						<input type="password" id="old_password" name="oldPassword" value="${oldPassword}"/>
					</td>
				</tr>
				<tr>
                    <td> <b>New password</b> <br>
						<input type="password" id="new_password" name="newPassword" value="${newPassword}"/>
					</td>
				</tr>
				<tr>
                    <td> <b>Confirm new password</b> <br>
						<input type="password" id="new_password_confirm" name="newPasswordConfirmation"/>
					</td>
				</tr>
				<tr>
					<td>
						<button style="width:140px;" class="xc_button" type="button" name="save" onClick="Javascript:YAHOO.xc.mst.changePassword.save();">Change Password</button>
						<button style="width:140px;" class="xc_button" name="cancel" onClick="Javascript:YAHOO.xc.mst.changePassword.cancel();">Go To My Account</button>
					</td>
				</tr>						
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

    
