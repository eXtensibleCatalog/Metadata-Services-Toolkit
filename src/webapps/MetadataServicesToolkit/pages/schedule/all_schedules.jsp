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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"   prefix="fn" %>


<!--  document type -->
<c:import url="/inc/doctype-frag.jsp"/>

<html>
    <head>
        <title> All Schedules</title>
        <c:import url="/inc/meta-frag.jsp"/>
        
        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css"  rel="stylesheet" type="text/css" >
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
			<table width="100%">
				<thead>
					<tr>

                        <td width="125">
                            <div>
                                <c:if test="${columnSorted!='ScheduleName'}">
                                     <c:url var="scheduleSortUrl" value="allSchedules.action">
                                       <c:param name="isAscendingOrder" value="true"/>
                                       <c:param name="columnSorted" value="ScheduleName"/>
                                     </c:url>
                                      <a href="${scheduleSortUrl}">Schedule name</a>
                                 </c:if>

                                 <c:if test="${columnSorted=='ScheduleName'}">
                                   <c:url var="scheduleSortUrl" value="allSchedules.action">
                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                     <c:param name="columnSorted" value="ScheduleName"/>
                                   </c:url>

                                   <a href="${scheduleSortUrl}">Schedule Name</a>

                                    <c:choose>
                                        <c:when test="${isAscendingOrder==true}">
                                            &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                        </c:when>
                                        <c:otherwise>
                                            &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                        </c:otherwise>
                                    </c:choose>
                                 </c:if>
                               
                            </div>
                        </td>
						<td width="170">
                                <div>
                                        <c:if test="${columnSorted!='RepositoryName'}">
                                            <c:url var="scheduleSortUrl" value="allSchedules.action">
                                                <c:param name="isAscendingOrder" value="true"/>
                                                <c:param name="columnSorted" value="RepositoryName"/>
                                            </c:url>
                                            <a href="${scheduleSortUrl}">Repository name</a>
                                        </c:if>

                                        <c:if test="${columnSorted=='RepositoryName'}">
                                            <c:url var="scheduleSortUrl" value="allSchedules.action">
                                                <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                                <c:param name="columnSorted" value="RepositoryName"/>
                                        </c:url>

                                       <a href="${scheduleSortUrl}">Repository Name</a>

                                        <c:choose>
                                            <c:when test="${isAscendingOrder==true}">
                                                &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                            </c:otherwise>
                                        </c:choose>
                                     </c:if>
                                </div>

                        </td>
                        <td>
                            <div>
                                 <c:if test="${columnSorted!='Recurrence'}">
                                     <c:url var="scheduleSortUrl" value="allSchedules.action">
                                       <c:param name="isAscendingOrder" value="true"/>
                                       <c:param name="columnSorted" value="Recurrence"/>
                                     </c:url>
                                      <a href="${scheduleSortUrl}">Recurrence</a>
                                 </c:if>

                                 <c:if test="${columnSorted=='Recurrence'}">
                                   <c:url var="scheduleSortUrl" value="allSchedules.action">
                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                     <c:param name="columnSorted" value="Recurrence"/>
                                   </c:url>

                                   <a href="${scheduleSortUrl}">Recurrence</a>

                                    <c:choose>
                                        <c:when test="${isAscendingOrder==true}">
                                            &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                        </c:when>
                                        <c:otherwise>
                                            &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                        </c:otherwise>
                                    </c:choose>
                                 </c:if>
                            </div>

                        </td>
                        <td>
                                <c:if test="${columnSorted!='Status'}">
                                     <c:url var="scheduleSortUrl" value="allSchedules.action">
                                       <c:param name="isAscendingOrder" value="true"/>
                                       <c:param name="columnSorted" value="Status"/>
                                     </c:url>
                                      <a href="${scheduleSortUrl}">Status</a>
                                 </c:if>

                                 <c:if test="${columnSorted=='Status'}">
                                   <c:url var="scheduleSortUrl" value="allSchedules.action">
                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                     <c:param name="columnSorted" value="Status"/>
                                   </c:url>

                                   <a href="${scheduleSortUrl}">Status</a>

                                    <c:choose>
                                        <c:when test="${isAscendingOrder==true}">
                                            &nbsp;<img src="page-resources/img/triangle_sort.jpg">

                                        </c:when>
                                        <c:otherwise>
                                            &nbsp;<img src="page-resources/img/triangle_sort_down.jpg">
                                        </c:otherwise>
                                    </c:choose>
                                 </c:if>
                               
                        </td>
                        <td>
                            Message
                        </td>
						<td>Delete Schedule</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="schedule" items="${schedules}">
					<tr>
                            <c:set var="classColumn" value="plainColumn"/>
                            <c:if test="${columnSorted=='ScheduleName'}">
                                <c:set var="classColumn" value="sortColumn"/>
                            </c:if>
						<td class="${classColumn}"><a href="viewEditSchedule.action?scheduleId=${schedule.id}"><U>${schedule.scheduleName}</U></a></td>
                            <c:set var="classColumn" value="plainColumn"/>
                            <c:if test="${columnSorted=='RepositoryName'}">
                                <c:set var="classColumn" value="sortColumn"/>
                            </c:if>
						<td class="${classColumn}"> ${schedule.provider.name}</td>
                            <c:set var="classColumn" value="plainColumn"/>
                            <c:if test="${columnSorted=='Recurrence'}">
                                <c:set var="classColumn" value="sortColumn"/>
                            </c:if>
						<td class="${classColumn}">
							<c:if test="${schedule.recurrence == 'Daily'}">
								${schedule.recurrence} Time ${schedule.hour}:<c:if test="${schedule.minute < 10}">0${schedule.minute}</c:if><c:if test="${schedule.minute > 9}">${schedule.minute} hrs {timeZone}</c:if>
							</c:if>
							<c:if test="${schedule.recurrence == 'Hourly'}">
								${schedule.recurrence} at ${schedule.minute} minutes past the hour
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
								
								Time ${schedule.hour}:00 hrs ${timeZone}
							</c:if>
						</td>
                        <td width="80">
                        	<c:set var="lastRun" value="${mst:lastHarvest(schedule)}"/>
                        	<c:if test="${fn:startsWith(lastRun, 'Not')}">
                        		${mst:lastHarvest(schedule)}
                        	</c:if>
                        	<c:if test="${fn:startsWith(lastRun, 'last')}">
                                <img src="page-resources/img/tick.jpg"><span style="position:relative;top:-5px;">Success</span>
                        	</c:if>
                        	
                        </td>
                        <td>
                            ${mst:lastHarvest(schedule)} hrs ${timeZone}
                        </td>
						<td> <button class="xc_button" id="showDeleteSchedule" type="button" name="delete" onClick="Javascript:YAHOO.mst.schedule.delete.deleteSchedule(${schedule.id}, '${schedule.scheduleName}');">Delete</button></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			</div>
 
 		</div>
		<!--  end body -->

	      <div id="deleteScheduleDialog" class="hidden">
	          <div class="hd">Delete Schedule</div>
		      <div class="bd">
		          <form id="deleteSchedule" name="deleteSchedule" method="POST" 
		              action="deleteSchedule.action">
		              
		              <input type="hidden" id="schedule_id" name="scheduleId"/>
		              
			          <p>Are you sure you want to delete the Schedule?</p>
		          </form>
		      </div>
	      </div>

        </div>
        <!-- end doc -->
    </body>
</html>

    
