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
        <title> All Schedules</title>
        <c:import url="/inc/meta-frag.jsp"/>
        
        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >        
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
		<LINK href="page-resources/css/bodylayout.css" rel="stylesheet" type="text/css">

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
    	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>             
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script>           
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/all_schedules.js"></SCRIPT>
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

                    <jsp:param name="bread" value="Harvest , All Schedules" />

            </jsp:include>
 		</div>
		<!--  end header -->
		
		<!-- body -->
		<div id="bd">
   			
   			<c:if test="${errorType != null}"> 
   				<div class="${errorType}"> 
   					<img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg"> 
					<s:fielderror cssClass="errorMessage"/> 
				</div>
   			 </c:if> 
   			 <div id="error_div"></div>

             <div class="clear">&nbsp;</div>
             
			<div class="viewTable">
			<table width="100%">
				<thead>
					<tr>

                        <td>
                            <div>
                                <ul style="list-style:none;">
                                    <li style="float:left;"><div style="margin-top:-12px;"><a href="allSchedules.action?isAscendingOrder=${!isAscendingOrder}&columnSorted=schedule_name&columnNumber=1">Schedule Name</a></div></li>
                                     <c:if test="${columnNumber==1}">
                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                 <li style="float:left;"><div style="margin-top:-6px;margin-left:5px;"><img src="page-resources/img/triangle_sort.jpg"></div></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li style="float:left;"><div style="margin-top:-6px;margin-left:5px;"><img src="page-resources/img/triangle_sort_down.jpg"></div></li>
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </ul>
                            </div>
                        </td>
						<td>Repository Name</td>
                        <td>
                            <div>
                                <ul style="list-style:none;">
                                    <li style="float:left;"><div style="margin-top:-12px;"><a href="allSchedules.action?isAscendingOrder=${!isAscendingOrder}&columnSorted=recurrence&columnNumber=2">Recurrance</a></div></li>
                                     <c:if test="${columnNumber==2}">
                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                 <li style="float:left;"><div style="margin-top:-6px;margin-left:5px;"><img src="page-resources/img/triangle_sort.jpg"></div></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li style="float:left;"><div style="margin-top:-6px;margin-left:5px;"><img src="page-resources/img/triangle_sort_down.jpg"></div></li>
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </ul>
                            </div>

                        </td>
                        <td>Status</td>
						<td>Delete Schedule</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="schedule" items="${schedules}">
					<tr>
                            <c:set var="colorColumn" value="#ffffff"/>
                            <c:if test="${columnNumber==1}">
                                <c:set var="colorColumn" value="#edfaff"/>
                            </c:if>
						<td class="sortColumn" bgcolor="${colorColumn}"><a href="viewEditSchedule.action?scheduleId=${schedule.id}">${schedule.scheduleName}</a></td>
						<td> ${schedule.provider.name}</td>
                            <c:set var="colorColumn" value="#ffffff"/>
                            <c:if test="${columnNumber==2}">
                                <c:set var="colorColumn" value="#edfaff"/>
                            </c:if>
						<td bgcolor="${colorColumn}">
							<c:if test="${schedule.recurrence == 'Daily'}">
								${schedule.recurrence} Time ${schedule.hour+ 1}:00
							</c:if>
							<c:if test="${schedule.recurrence == 'Hourly'}">
								${schedule.recurrence} Every ${schedule.minute+ 1} minutes
							</c:if>
							<c:if test="${schedule.recurrence == 'Weekly'}">
								${schedule.recurrence} 
								<c:if test="${schedule.dayOfWeek == 1}">
									Sunday
								</c:if>
								<c:if test="${schedule.dayOfWeek == 2}">
									Monday
								</c:if>
								<c:if test="${schedule.dayOfWeek == 3}">
									Tuesday
								</c:if>
								<c:if test="${schedule.dayOfWeek == 4}">
									Wednesday
								</c:if>
								<c:if test="${schedule.dayOfWeek == 5}">
									Thursday
								</c:if>
								<c:if test="${schedule.dayOfWeek == 6}">
									Friday
								</c:if>
								<c:if test="${schedule.dayOfWeek == 7}">
									Saturday
								</c:if>
								
								Time ${schedule.hour + 1}:00
							</c:if>
						</td>
                        <td>Status should be here</td>
						<td> <button class="xc_button" id="showDeleteSchedule" type="button" name="delete" onClick="Javascript:YAHOO.mst.schedule.delete.deleteSchedule(${schedule.id}, '${schedule.scheduleName}');">Delete</button></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			</div>
 
 		</div>
		<!--  end body -->
		<form name="deleteScheduleForm" method="post" action= 'deleteSchedule.action'>
			<input type="hidden" id="schedule_id" name="scheduleId"/>
		</form>	      
        </div>
        <!-- end doc -->
    </body>
</html>

    
