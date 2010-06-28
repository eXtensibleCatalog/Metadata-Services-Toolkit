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
        <title>User Registration</title>
        <c:import url="/st/inc/meta-frag.jsp"/>
        
        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >  
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
    	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>          
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script>         
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/user_registration.js"></SCRIPT>
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

                    <jsp:param name="bread" value="Registeration Success" />

            </jsp:include>

            
	</div>
	<!--  end header -->

	<!-- body -->
	<div id="bd">

		<div class="clear">&nbsp;</div>
		Congratulations! You have successfully registered with the system. An email is sent to you for verification. <br>
		Also an email is sent to the System admin to assign appropriate permissions for you to access the system.

		<div class="clear">&nbsp;</div>
		<a href="home.action">Go to Home Page</a>

	</div>
	<!--  end body -->		
            <!--  this is the footer of the page -->
            <c:import url="/st/inc/footer.jsp"/>              
        </div>
        <!-- end doc -->
    </body>
</html>

    
