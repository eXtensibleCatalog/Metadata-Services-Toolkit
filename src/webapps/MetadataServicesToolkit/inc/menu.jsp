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
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/allRepository.action" />" >List Repositories</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddRepository.action" />" >Add Repository</a></li>
                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>

       			<mst:checkUserPermission permission="Schedule">
                   <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="<c:url value="/allSchedules.action"/>">Harvest</a>
                   <div id="schedule" class="yuimenu">
                       <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/allSchedules.action" />" >List Scheduled Harvests</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddSchedule.action" />" >Add Scheduled Harvests</a></li>
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
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddService.action" />" >Add Service</a></li>
                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>

                 <mst:checkUserPermission permission="Processing Rules">
                    <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="<c:url value="/listProcessingDirectives.action"/>">Processing Rules</a>
                   <div id="Processing Rules" class="yuimenu">
                       <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/listProcessingDirectives.action" />" >List Processing Rules</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddProcessingDirectives.action?refreshSession=yes" />" >Add Processing Rules</a></li>
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
                    <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="generalLog.action">Logs</a>
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
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/allUsers.action" />" >List Users</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddLocalUser.action" />" >Add Local User</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/allGroups.action" />" >List Groups</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="<c:url value="/viewAddGroup.action" />" >Add Group</a></li>
                           </ul>
                        </div>
                    </div>
                    </li>
                 </mst:checkUserPermission>

       			<mst:checkUserPermission permission="Configuration">
                    <li class="yuimenubaritemleft"><a class="yuimenubaritemlabel" href="viewEmailConfig.action">Configuration</a>
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
