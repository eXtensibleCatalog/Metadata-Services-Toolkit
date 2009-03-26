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


<!--  document type -->
<c:import url="/inc/doctype-frag.jsp"/>

<html>
    <head>
        <title>Browse Records</title>
        <c:import url="/inc/meta-frag.jsp"/>
        
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
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
    </head>
    
    <body class="yui-skin-sam">
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">  

		<!-- page header - this uses the yahoo page styling -->
		<div id="hd">
   
            <!--  this is the header of the page -->
            <c:import url="/inc/header.jsp"/>
            
            <c:import url="/inc/menu.jsp"/>

            <c:url var="viewResults" value="browseRecords.action">
		  		<c:param name="query" value="${query}"/>
			    <c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
		  	    <c:param name="selectedFacetValues" value="${selectedFacetValues}"/>
		  		<c:param name="rowStart" value="${rowStart}"/>
		  		<c:param name="startPageNumber" value="${startPageNumber}"/>
				<c:param name="currentPageNumber" value="${currentPageNumber}"/>
	    	</c:url>            
            <jsp:include page="/inc/breadcrumb.jsp"> 
	          <jsp:param name="bread" value="Browse Records,<a href='${viewResults}'>Search Results</a>, View Record" />
            </jsp:include>
            
 		</div>
		<!--  end header -->
		
		<!-- body -->
		<div id="bd">
   			<!-- <pre class="sourcecode">
   				<textarea rows="80" cols="115" style= "background: #C0C0C0;">${recordXML}</textarea> 
   			</pre>
   
			-->
			<h2> </h2>
			<textarea rows="80" cols="150" style= "background: #C0C0C0;">${recordXML}</textarea> 
			
 		</div>
		<!--  end body -->		
            
        </div>
        <!-- end doc -->
    </body>
</html>

    
