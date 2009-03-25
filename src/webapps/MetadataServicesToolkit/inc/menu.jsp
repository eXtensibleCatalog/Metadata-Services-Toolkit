<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mst" uri="mst-tags"%>

<c:if test="${user != null}">
    <div id="mainMenu" class="yuimenubar yuimenubarnav">
        <div class="bd">
            <ul class="first-of-type">

       			<mst:checkUserPermission permission="Repositories">
                   <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="<c:url value="/allRepository.action" />" ><span class="wrenchImg">&nbsp;</span>Repository</a>
                   <div id="repository" class="yuimenu">
                       <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/allRepository.action" />" >All Repositories</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddRepository.action" />" >Add Repository</a></li>
                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>

       			<mst:checkUserPermission permission="Harvest">
                   <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="<c:url value="/allSchedules.action"/>">Schedule</a>
                   <div id="schedule" class="yuimenu">
                       <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/allSchedules.action" />" >All Schedules</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddSchedule.action" />" >Add Schedule</a></li>
                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>

       			<mst:checkUserPermission permission="Services">
                    <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="<c:url value="/listServices.action"/>">Services</a>
                   <div id="Services" class="yuimenu">
                       <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/listServices.action" />" >List Services</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/listProcessingDirectives.action" />" >List Processing Directives</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddProcessingDirectives.action?refreshSession=yes" />" >Add Processing Directives</a></li>
                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>


       			<mst:checkUserPermission permission="Browse Records">
                    <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="<c:url value="/viewBrowseRecords.action"/>">Browse Records</a>
                    </li>
                 </mst:checkUserPermission>

       			<mst:checkUserPermission permission="Logs">
                    <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="">Logs</a>
                   <div id="logs" class="yuimenu">
                       <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="generalLog.action" >General</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="harvestInLog.action" >Harvest In</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="harvestOutLog.action" >Harvest Out</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="serviceLog.action" >Services</a></li>
                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>

       			 <mst:checkUserPermission permission="Users/Groups">
                    <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="<c:url value="/allUsers.action"/>">Users/Groups</a>
                   <div id="UsersGroups" class="yuimenu">
                       <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/allUsers.action" />" >Users</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddLocalUser.action" />" >Add Local User</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddLDAPUser.action" />" >Add LDAP User</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/allGroups.action" />" >Groups</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddGroup.action" />" >Add Group</a></li>
                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>

       			<mst:checkUserPermission permission="Configuration">
                    <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="">Configuration</a>
                   <div id="configuration" class="yuimenu">
                       <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewEmailConfig.action" />" >Email Configuration</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddLDAP.action" />" >LDAP Configuration</a></li>

                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>

	        </ul>
	     </div>
    </div>
</c:if>