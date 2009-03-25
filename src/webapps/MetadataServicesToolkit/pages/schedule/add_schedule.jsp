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
        <title>Add Schedule</title>
        <c:import url="/inc/meta-frag.jsp"/>
        
        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/calendar/assets/skins/sam/calendar.css"  rel="stylesheet" type="text/css" > 
        
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
		<LINK href="page-resources/css/bodylayout.css" rel="stylesheet" type="text/css">

		<!--  Style for dialog boxes -->
	    <style>
	        	#containerDialog .bd {padding:0;}  
			#cal1Container {border:none;padding:1em} 
			#containerDialog .bd:after {content:".";display:block;clear:left;height:0;visibility:hidden;} 
			
		        #containerDialog2 .bd {padding:0;}  
			#cal2Container {border:none;padding:1em} 
			#containerDialog2 .bd:after {content:".";display:block;clear:left;height:0;visibility:hidden;} 
			

	    </style>

        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
    	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>  
    	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/calendar/calendar-min.js"></script>    
        
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script> 
        
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/add_schedule.js"></SCRIPT>
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
                    <jsp:param name="bread" value="Harvest , New Harvest:Step 1" />
            </jsp:include>
 		</div>
		<!--  end header -->
		
		<!-- body -->
		<div id="bd">
   			
   			<!-- Display of error message -->
   			<c:if test="${errorType != null}"> 
   				<div class="${errorType}"> 
   					<img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg"> 
					<s:fielderror cssClass="errorMessage"/> 
				</div>
   			 </c:if> 
   			 
   			  
   			
   			<c:if test="${schedule != null}">
	   			<form name="scheduleForm" method="post">
	   			
	   			<input type="hidden" id="schedule_id" name="scheduleId" value="${schedule.id}"/>
	   			
				<table class="basicTable">
				<tr>
					<td> <img src="${pageContext.request.contextPath}/page-resources/img/select_repo_schedule.JPG"></td>
					<td> <img src="${pageContext.request.contextPath}/page-resources/img/select_sets_formats.JPG"></td>
				</tr>
				<tr>
					<td colspan="2" class="label"> Schedule Name </td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="text" name="scheduleName" value="${schedule.scheduleName}"/>
					</td>
				</tr>
				
				<tr>
					<td colspan="2" class="label"> Select Repository</td>
				</tr>
				<tr>
					<td colspan="2">
						<select id="schedule_repository" name="repositoryId" />
					
							<option value = "0"></option>
							
							<c:forEach items="${repositories}" var="repository">
								<c:if test="${repository.identify}">
									<option value = "${repository.id}"
									<c:if test="${schedule.provider.id == repository.id}">
										selected
									</c:if>
									> ${repository.name}</option>
								</c:if>
							</c:forEach>
							<c:if test="${schedule.provider != null && !schedule.provider.identify}">
								<option value = "${schedule.provider.id}" selected>${schedule.provider.name}</option>
							</c:if>							
	
						</select>
					
					</td>
				</tr>
				<tr>
					<td colspan="2" class="label"> Schedule </td>
				</tr>
				<tr>
					<td colspan="2"><input type="radio" name="recurrence" value="Daily"
						<c:if test="${schedule.recurrence == 'Daily'}">
									checked
						</c:if>
						> Daily        
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Run&nbsp;&nbsp;&nbsp; 
						<select id="schedule_hour" name="dailyHour" />
					
							<option value = "-1"> Select </option>
							
							<c:forEach begin="1" end="24" step="1" var="time">
								<option value = "${time -1}"
								<c:if test="${schedule.recurrence == 'Daily' && schedule.hour == time-1}">
									selected
								</c:if>
								> ${time}:00</option>
							</c:forEach>
						</select>				
					&nbsp;&nbsp;&nbsp;each day </td>
				</tr>
				<tr>
					<td colspan="2"><input type="radio" name="recurrence" value="Hourly"
						<c:if test="${schedule.recurrence == 'Hourly'}">
									checked
						</c:if>
						> Hourly
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Run Every&nbsp;&nbsp;&nbsp;
						<select id="schedule_minute" name="minute" />
					
							<option value = "-1"> Select </option>
							
							<c:forEach begin="1" end="60" step="1" var="minute">
								<option value = "${minute -1}"
								<c:if test="${schedule.minute == minute-1}">
									selected
								</c:if>
								> ${minute}</option>
							</c:forEach>
						</select>				
					&nbsp;&nbsp;&nbsp;minutes each hour </td>
				</tr>
				<tr>
					<td colspan="2"><input type="radio" name="recurrence" value="Weekly"
						<c:if test="${schedule.recurrence == 'Weekly'}">
									checked
						</c:if>
						> Weekly        
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Run Every&nbsp;&nbsp;&nbsp;
						<select id="schedule_day" name="dayOfWeek" />
							<option value = "0"> Select </option>
							<option value = "1"
								<c:if test="${schedule.dayOfWeek == 1}">
									selected
								</c:if>
								> Sunday</option>
							<option value = "2"
								<c:if test="${schedule.dayOfWeek == 2}">
									selected
								</c:if>
								> Monday</option>
							<option value = "3"
								<c:if test="${schedule.dayOfWeek == 3}">
									selected
								</c:if>
								> Tuesday</option>
							<option value = "4"
								<c:if test="${schedule.dayOfWeek == 4}">
									selected
								</c:if>
								> Wednesday</option>
							<option value = "5"
								<c:if test="${schedule.dayOfWeek == 5}">
									selected
								</c:if>
								> Thursday</option>
							<option value = "6"
								<c:if test="${schedule.dayOfWeek == 6}">
									selected
								</c:if>
								> Friday</option>
							<option value = "7"
								<c:if test="${schedule.dayOfWeek == 7}">
									selected
								</c:if>
								> Saturday</option>
							
						</select>
					at					
						<select id="schedule_week_hour" name="hour" />
							<option value = "-1"> Select </option>
							<c:forEach begin="1" end="24" step="1" var="time">
								<option value = "${time-1}"
								<c:if test="${schedule.recurrence == 'Weekly' && schedule.hour == time-1}">
									selected
								</c:if>
								> ${time}:00</option>
							</c:forEach>
						</select>				
					</td>
				</tr>
				<tr>
					<td colspan="2"><Strong>Start Date  : </Strong>
			          		<input type="text" id="schedule_start_date" name="startDate"  
			          		value="${startDateDisplayFormat}" 
			          		size="10" maxlength ="10"/>
						
			    		<button type="button" id="show_start_date_calendar" title="Show Calendar"><img alt="Calendar" 
						                       src="${pageContext.request.contextPath}/page-resources/img/calendar.gif"/></button>
			    		
			    		<div id="containerDialog">
							<div cssClass="hd">Calendar</div>
			
							<div  cssClass="bd">
								 <div id="cal1Container"></div> 
							</div >
						</div >				
					</td>
				</tr>
				<tr>
					<td colspan="2" > <Strong>End Date  : </Strong>
			          	<input type="text" id="schedule_end_date" name="endDate"  
			          		value="${endDateDisplayFormat}" 
			          		size="10" maxlength ="10"/>
						
			    		<button type="button" id="show_end_date_calendar" title="Show Calendar"><img alt="Calendar" 
						                       src="${pageContext.request.contextPath}/page-resources/img/calendar.gif"/></button>
			    		
			    		<div id="containerDialog2">
							<div cssClass="hd">Calendar</div>
			
							<div  cssClass="bd">
								 <div id="cal2Container"></div> 
							</div >
						</div >				
					
					</td>
				</tr>
				<tr>
					<td colspan="2" align="right">
						<button class="xc_button" name="cancel" onClick="Javascript:YAHOO.xc.mst.schedule.allSchedules();">Cancel</button> 
						<button class="xc_button" type="button" name="next" onClick="Javascript:YAHOO.xc.mst.schedule.saveSchedule();">Save & Next</button>
					</td>
				</tr>						
			</table> 
			</form>
		</c:if>
 		</div>
		<!--  end body -->		
            
        </div>
        <!-- end doc -->
    </body>
</html>

    
