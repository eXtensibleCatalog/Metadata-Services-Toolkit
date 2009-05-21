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
<c:import url="/inc/doctype-frag.jsp"/>

<html>
    <head>
        <title>Forgot Password</title>
        <c:import url="/inc/meta-frag.jsp"/>
        
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/forgot_password.js"></SCRIPT>
    </head>
    
    <body class="yui-skin-sam">
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">  

		<!-- page header - this uses the yahoo page styling -->
		<div id="hd">
            <!--  this is the header of the page -->
            <c:import url="/inc/header.jsp"/>
              <div id="mainMenu" class="yuimenubar yuimenubarnav">
                <div class="bd">
                    <ul class="first-of-type">
                        <span class="wrenchImg">&nbsp;</span>
                    </ul>
                </div>
             </div>

            <!--  this is the header of the page -->
            <c:import url="/inc/menu.jsp"/>
            <div style="height:10px;">

            </div>
            <jsp:include page="/inc/breadcrumb.jsp">
                   <jsp:param name="bread" value="Forgot Password" />
            </jsp:include>

 		</div>
		<!--  end header -->
		
		<!-- body -->
		<div id="bd">
   			
			<div class="clear"></div>

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

			<c:if test="${!resetSuccess}">
				<form name="forgotPasswordForm" action="resetPassword.action" method="post">
					<table class="basicTable" align="center">
	
					    <strong> Email address associated with the account: </strong>
					    
					    <input type="text" size="50" class="input" id="forgotPasswordForm_email" name="email" value="${email}"/>
							          
					    <div class="clear">&nbsp;</div>
					    
					    (Note: Password details will be sent to this E-mail ID)
					    
					    <div class="clear">&nbsp;</div>
					    
					    <button class="xc_button" name="submit" type="submit">Submit</button> 
					</table>
				</form>
			</c:if>		
			<c:if test="${resetSuccess}">
				<p> New password has been sent to your email address.  </p>
				
			</c:if>		
			<br>	<br>
			 <a href="home.action">Go To Home Page </a>

 		</div>
		<!--  end body -->		
            
        </div>
        <!-- end doc -->
    </body>
</html>


			
	   
