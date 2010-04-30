<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mst" uri="mst-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"   prefix="fn" %>

<!-- Begin - This is required to identify that the response is a schedule table and not login screen -->
<div style="display:none">SchedulePage</div>
<!-- End - This is required to identify that the response is a schedule table and not login screen -->

<input type="hidden" id="is_ascending_order" name="isAscendingOrder" value="${isAscendingOrder}"/>
<input type="hidden" id="column_sorted" name="columnSorted" value="${columnSorted}"/>

<c:choose>
    <c:when test="${empty schedules}">
         <div class="emptytablebar">
             <div class="emptytable_innerdiv"> Choose <b>Harvest</b> <img class="emptytable_img" src="page-resources/img/bullet_go.gif"/> <b>Add Scheduled Harvest</b> to add a new scheduled harvest </div>
         </div>
     </c:when>
     <c:otherwise>
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
                                      <a href="javascript:YAHOO.xc.mst.schedule.view.refreshScheduleTable('true', 'ScheduleName');">Schedule name</a>
                                 </c:if>

                                 <c:if test="${columnSorted=='ScheduleName'}">
                                   <c:url var="scheduleSortUrl" value="allSchedules.action">
                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                     <c:param name="columnSorted" value="ScheduleName"/>
                                   </c:url>

                                   <a href="javascript:YAHOO.xc.mst.schedule.view.refreshScheduleTable(${!isAscendingOrder}, 'ScheduleName');">Schedule Name</a>

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
                                            <a href="javascript:YAHOO.xc.mst.schedule.view.refreshScheduleTable('true', 'RepositoryName');">Repository name</a>
                                        </c:if>

                                        <c:if test="${columnSorted=='RepositoryName'}">
                                            <c:url var="scheduleSortUrl" value="allSchedules.action">
                                                <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                                <c:param name="columnSorted" value="RepositoryName"/>
                                        </c:url>

                                       <a href="javascript:YAHOO.xc.mst.schedule.view.refreshScheduleTable(${!isAscendingOrder}, 'RepositoryName');">Repository Name</a>

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
                                      <a href="javascript:YAHOO.xc.mst.schedule.view.refreshScheduleTable('true', 'Recurrence');">Recurrence</a>
                                 </c:if>

                                 <c:if test="${columnSorted=='Recurrence'}">
                                   <c:url var="scheduleSortUrl" value="allSchedules.action">
                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                     <c:param name="columnSorted" value="Recurrence"/>
                                   </c:url>

                                   <a href="javascript:YAHOO.xc.mst.schedule.view.refreshScheduleTable(${!isAscendingOrder}, 'Recurrence');">Recurrence</a>

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
                                      <a href="javascript:YAHOO.xc.mst.schedule.view.refreshScheduleTable('true', 'Status');">Status</a>
                                 </c:if>

                                 <c:if test="${columnSorted=='Status'}">
                                   <c:url var="scheduleSortUrl" value="allSchedules.action">
                                     <c:param name="isAscendingOrder" value="${!isAscendingOrder}"/>
                                     <c:param name="columnSorted" value="Status"/>
                                   </c:url>

                                   <a href="javascript:YAHOO.xc.mst.schedule.view.refreshScheduleTable(${!isAscendingOrder}, 'Status');">Status</a>

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
                                            <td class="${classColumn}"><a href="viewEditSchedule.action?scheduleId=${schedule.id}">${schedule.scheduleName}</a></td>
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
                                                    ${schedule.recurrence} at ${schedule.hour}:<c:if test="${schedule.minute < 10}">0${schedule.minute}</c:if><c:if test="${schedule.minute > 9}">${schedule.minute} </c:if> ${timeZone}
                                                </c:if>
                                                <c:if test="${schedule.recurrence == 'Hourly'}">
                                                    ${schedule.recurrence} ${schedule.minute} minutes past the hour
                                                </c:if>
                                                <c:if test="${schedule.recurrence == 'Weekly'}">
                                                    ${schedule.recurrence} on 
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

                                                    at ${schedule.hour}:00 ${timeZone}
                                                </c:if>
                                            </td>
                                            <c:set var="classColumn" value="plainColumn"/>
					    					<c:if test="${columnSorted=='Status'}">
													<c:set var="classColumn" value="sortColumn"/>
                                            </c:if>
					   						<td width="80" class="${classColumn}">
                                                <c:set var="schedule_status" value ="${schedule.status}"/>
                                                <c:choose>
                                                    <c:when test="${schedule_status=='NOT_RUNNING'}">
                                                         <c:set var="lastRun" value="${mst:lastHarvest(schedule)}"/>
                                                            <c:if test="${fn:startsWith(lastRun, 'Not')}">
                                                                ${mst:lastHarvest(schedule)}
                                                            </c:if>
                                                            <c:if test="${fn:startsWith(lastRun, 'last')}">
                                                                <img src="page-resources/img/tick.jpg"><span style="position:relative;top:-5px;">Success</span>
                                                            </c:if>
                                                    </c:when>
                                                    <c:when test="${schedule_status=='RUNNING'}">
                                                          In Progress
                                                    </c:when>
                                                    <c:when test="${schedule_status=='CANCELED'}">
                                                          Aborted
                                                    </c:when>
                                                    <c:when test="${schedule_status=='ERROR'}">
                                                          Error
                                                    </c:when>                                                    
                                                </c:choose>
                                               

                                            </td>
                                            <td>
                                                <c:set var="lastRun" value="${mst:lastHarvest(schedule)}"/>

												<c:choose>
						                            <c:when test="${schedule_status=='NOT_RUNNING'}">
														<c:if test="${fn:startsWith(lastRun, 'last')}">
														    ${mst:lastHarvest(schedule)} ${timeZone}
														</c:if>
                                                    </c:when>
                                                    
                                                    <c:when test="${schedule_status=='CANCELED'}">
                                                          Last scheduled harvest was aborted.
                                                    </c:when>
                                                </c:choose>


                                            </td>
                                            <td> <button class="xc_button" id="showDeleteSchedule" type="button" name="delete" onClick="javascript:YAHOO.xc.mst.schedule.view.deleteSchedule(${schedule.id}, '${schedule.scheduleName}');">Delete</button></td>
                                        </tr>
                                    </c:forEach>
                       
				</tbody>
			</table>
	  </div>
  </c:otherwise>
</c:choose>