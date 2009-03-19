<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!--  document type -->
<c:import url="/inc/doctype-frag.jsp"/>

<html>
    <head>
        <title>Add Schedule</title>
        <c:import url="/inc/meta-frag.jsp"/>
        
        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">

        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
    	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>  
        
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script> 
        
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/add_schedule_set_format.js"></SCRIPT>
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

                    <jsp:param name="bread" value="Harvest , New Harvest:Step 2" />

            </jsp:include>
 		</div>
		<!--  end header -->
		
		<!-- body -->
		<div id="bd">
   			
   			
   			<form name="addScheduleForm" method="post">
   			
   			<input type="hidden" id="schedule_id" name="scheduleId" value="${schedule.id}"/>
   			
			<table class="basicTable">
			<tr>
				<td> <img src="${pageContext.request.contextPath}/page-resources/img/sel_repo_schedule.JPG"></td>
				<td> <img src="${pageContext.request.contextPath}/page-resources/img/sel_sets_formats.JPG"></td>
			</tr>
			<tr>
				<td colspan="2" class="label"> Contact Email </td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="text" name="notifyEmail" value="${schedule.notifyEmail}" size="40"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="label"> Choose Sets </td>
			</tr>
			<tr>
				<td colspan="2"> <input type="checkbox" onClick="javascript:YAHOO.xc.mst.schedule.add.selectAll(document.addScheduleForm.selectedSetIds, this);" value="true" name="select_all"/>&nbsp;&nbsp;&nbsp;Select All </td>
			</tr>
			
			<c:forEach var="set" items="${repository.sets}">
			<tr>
				<td colspan="2">
					<input type="checkbox" name="selectedSetIds" value="${set.id}"
					<c:forEach items="${schedule.sets}" var="scheduleSet">
						<c:if test="${scheduleSet.id == set.id}">
									checked
						</c:if>
					</c:forEach>
					/>&nbsp;&nbsp;&nbsp; ${set.displayName}
				</td>
			</tr>
			</c:forEach>
			<tr>
				<td colspan="2" class="label"> Choose Formats </td>
			</tr>

			<c:forEach var="format" items="${repository.formats}">
			<tr>
				<td colspan="2">
					<input type="checkbox" name="selectedFormatIds" value="${format.id}"
					<c:forEach items="${schedule.formats}" var="scheduleFormat">
						<c:if test="${scheduleFormat.id == format.id}">
									checked
						</c:if>
					</c:forEach>
					/>&nbsp;&nbsp;&nbsp; ${format.name}
				</td>
			</tr>
			</c:forEach>
			<tr>
				<td colspan="2" align="right">
					<button class="xc_button" name="previous" onclick="javascript:YAHOO.xc.mst.schedule.add.gotoPreviousStep();">Previous</button> 
					<button class="xc_button" name="next" onclick="javascript:YAHOO.xc.mst.schedule.add.saveAndExit();">Save & Exit</button>
				</td>
			</tr>						
		</table> 
		</form>
 		</div>
		<!--  end body -->		
            
        </div>
        <!-- end doc -->
    </body>
</html>

    
