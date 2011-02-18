<!--
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="mst" uri="mst-tags"%>

<!--  document type -->
<c:import url="/st/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">


<html>
    <head>
        <title>All Users</title>
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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/all_users.js"></SCRIPT>

    </head>

    <body class="yui-skin-sam">
        <%@ taglib prefix="s" uri="/struts-tags" %>
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">

		<!-- page header - this uses the yahoo page styling -->
		<div id="hd">

            <!--  this is the header of the page -->
            <c:import url="/st/inc/header.jsp"/>

            <!--  this is the header of the page -->
            <c:import url="/st/inc/menu.jsp"/>
            <jsp:include page="/st/inc/breadcrumb.jsp">

                    <jsp:param name="bread" value="Stats | Record Counts" />

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

             <div class="viewTable">
             	<span style="font-size: 120%; font-weight: bold;">Incoming Harvest Records</span>
                <table width="100%">
                	<c:forEach var="r" items="${incomingHarvestRecords}" varStatus="st">
                		<c:choose>
	                 		<c:when test="${st.first}">
	                 			<c:set var="style" value="font-weight: bold;" />
	                 		</c:when>
           					<c:otherwise>
           						<c:set var="style" value="font-weight: normal;" />
           					</c:otherwise>
                		</c:choose>
                		<tr>
                			<c:forEach var="c" items="${r}" varStatus="st2">
	                			<c:choose>
			                 		<c:when test="${st2.first}">
			                 			<c:set var="style2" value="" />
			                 		</c:when>
		           					<c:otherwise>
		           						<c:set var="style2" value="float: right;" />
		           					</c:otherwise>
		                		</c:choose>
                				<td>
                					<span style="${style}${style2}">${c}</span>
                				</td>
                			</c:forEach>
                		</tr>
                	</c:forEach>
                </table>
			</div>
			
			<div class="viewTable">
             	<span style="font-size: 120%; font-weight: bold;">Incoming Service Records</span>
                <table width="100%">
                	<c:forEach var="r" items="${incomingServiceRecords}" varStatus="st">
                		<c:choose>
                 		<c:when test="${st.first}">
                 			<c:set var="style" value="font-weight: bold;" />
                 		</c:when>
           					<c:otherwise>
           						<c:set var="style" value="font-weight: normal;" />
           					</c:otherwise>
                		</c:choose>
                		<tr>
                			<c:forEach var="c" items="${r}" varStatus="st2">
	                			<c:choose>
			                 		<c:when test="${st2.first}">
			                 			<c:set var="style2" value="" />
			                 		</c:when>
		           					<c:otherwise>
		           						<c:set var="style2" value="float: right;" />
		           					</c:otherwise>
		                		</c:choose>
                				<td>
                					<span style="${style}${style2}">${c}</span>
                				</td>
                			</c:forEach>
                		</tr>
                	</c:forEach>
                </table>
			</div>
			
			<div class="viewTable">
             	<span style="font-size: 120%; font-weight: bold;">Outgoing Service Records</span>
                <table width="100%">
                	<c:forEach var="r" items="${outgoingServiceRecords}" varStatus="st">
                		<c:choose>
                 		<c:when test="${st.first}">
                 			<c:set var="style" value="font-weight: bold;" />
                 		</c:when>
           					<c:otherwise>
           						<c:set var="style" value="font-weight: normal;" />
           					</c:otherwise>
                		</c:choose>
                		<tr>
                			<c:forEach var="c" items="${r}" varStatus="st2">
	                			<c:choose>
			                 		<c:when test="${st2.first}">
			                 			<c:set var="style2" value="" />
			                 		</c:when>
		           					<c:otherwise>
		           						<c:set var="style2" value="float: right;" />
		           					</c:otherwise>
		                		</c:choose>
                				<td>
                					<span style="${style}${style2}">${c}</span>
                				</td>
                			</c:forEach>
                		</tr>
                	</c:forEach>
                </table>
			</div>
        </div>
            <!--  this is the footer of the page -->
            <c:import url="/st/inc/footer.jsp"/>          
   </div>
</body>
</html>
