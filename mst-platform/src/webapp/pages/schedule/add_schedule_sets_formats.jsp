<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="mst" uri="mst-tags"%>

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
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
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

			    <jsp:param name="bread" value="Harvest | New Harvest:Step 2" />

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
		<div id="clear">&nbsp;</div>			   			
		<div class="stepsStructure">
			<span style="position: relative; top: 13px;"><img src="page-resources/img/schedule_step1_grey.gif"></span>
			<span style="position: relative; top: 13px;"><img src="page-resources/img/schedule_step2_hghlight.gif"></span>
		</div>
		
		<div class="greybody">
			<div style="margin-left:110px;padding-bottom:10px;">
			This schedule will harvest records from <b>${schedule.provider.name} </b>
			<c:if test="${schedule.recurrence == 'Hourly'}">
				Hourly at ${schedule.minute} minutes past the hour
			</c:if>
			<c:if test="${schedule.recurrence == 'Daily'}">
				Daily at ${schedule.hour}:<c:if test="${schedule.minute < 10}">0${schedule.minute}</c:if><c:if test="${schedule.minute > 9}">${schedule.minute}</c:if>
				
			</c:if>
			<c:if test="${schedule.recurrence == 'Weekly'}">
				Weekly on 
				<c:choose>
				<c:when test="${schedule.dayOfWeek == 1}">
				    Sunday
				</c:when>
				<c:when test="${schedule.dayOfWeek == 2}">
				    Monday
				</c:when>
				<c:when test="${schedule.dayOfWeek == 3}">
				    Tuesday
				</c:when>
				<c:when test="${schedule.dayOfWeek == 4}">
				    Wednesday
				</c:when>
				<c:when test="${schedule.dayOfWeek == 5}">
				    Thursday
				</c:when>
				<c:when test="${schedule.dayOfWeek == 6}">
				    Friday
				</c:when>
				<c:when test="${schedule.dayOfWeek == 7}">
				    Saturday
				</c:when>				
				</c:choose>
 				&nbsp;at ${schedule.hour}:00
			</c:if>		
			</div>
		</div>
		
		
   			<form name="addScheduleForm" method="post">
   			
   			<input type="hidden" id="schedule_id" name="scheduleId" value="${schedule.id}"/>
   			
			<table class="basicTable">
			<tr>
				<td colspan="2" class="label"> Which records from ${schedule.provider.name} should be harvested?</td>
			</tr>

			<tr>
				<td>
					<b>Formats</b><br>
					<select multiple name="selectedFormatIds" style="width:300px;" size="10">
						<c:forEach var="format" items="${repository.formats}">
							<option value="${format.id}"
							
							<c:forEach items="${schedule.formats}" var="scheduleFormat">
								<c:if test="${scheduleFormat.id == format.id}">
											selected
								</c:if>
							</c:forEach>
							>${format.name} </option>
						</c:forEach>
					</select>
					<br>
					(CTRL click to select multiple formats)
				</td>
				
				<td width="50%">
					Rename this harvest schedule(optional)<br>
					<input type="text" name="scheduleName" value="${schedule.scheduleName}" size="40" maxlength="265"/> 
					<br><br>
					
					Contact email:<br>
					<input type="text" name="notifyEmail" value="${schedule.notifyEmail}" size="40" maxlength="255"/><br>
					(Email will be sent when error occurs with the harvest.Separate multiple email address with comma.)
				
				</td>

			</tr>
			<tr>
				<td > 
					<b>Sets </b> 
					<br>
					<select name="selectedSetIds" multiple style="width:300px;" size="10"> 
						<option value="0" 
                                <c:if test="${schedule.sets == '[]' || schedule.sets == '[null]'}">
                                     selected
                                </c:if>
                            >All Sets</option>

                            <c:forEach var="set" items="${repository.sets}">
                                <option value="${set.id}"
                                <c:forEach items="${schedule.sets}" var="scheduleSet">
                                    <c:if test="${scheduleSet.id == set.id}">
                                                selected
                                    </c:if>
                                </c:forEach>
                                >${set.displayName}
                            </c:forEach>
                        </option>
                    </select>
					<br>
                        <c:if test="${!empty repository.sets}">
                             <div class="smallText">
                                (CTRL click to select multiple sets)
                            </div>
                        </c:if>
					
				</td>
			</tr>

			<tr>
				<td colspan="2" align="right">
				<hr size="1" style="color:#cfd2d4"><br>
					<button class="xc_button_small" style="vertical-align:bottom;" name="cancel" onclick="javascript:YAHOO.xc.mst.schedule.add.cancel();">Cancel</button>
                    <button style="width:140px;" class="xc_button" type="button" name="previous" onclick="javascript:YAHOO.xc.mst.schedule.add.gotoPreviousStep();"><img src="page-resources/img/bullet_go_left.gif"><span style="position:relative;top:-3px;">Back to Step 1</span></button>
					<button style="vertical-align:bottom;" class="xc_button" type="button" name="next" onclick="javascript:YAHOO.xc.mst.schedule.add.saveAndExit();">Finish</button>
				</td>
			</tr>						
		</table> 
		</form>
 		</div>
		<!--  end body -->		
            <!--  this is the footer of the page -->
            <c:import url="/inc/footer.jsp"/>              
        </div>
        <!-- end doc -->
    </body>
</html>

    
