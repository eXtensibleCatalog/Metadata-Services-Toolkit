<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  -->

<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css" >
<SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/service_status.js"></SCRIPT>
 <%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
 <%@ taglib prefix="mst" uri="mst-tags"%>

<c:if test="${user!=null}">
    <mst:checkUserPermission permission="Services">
        <div class="serviceStatus" id="serviceBar">

        </div>
    </mst:checkUserPermission>
</c:if>

<div id="header">
<ul style="list-style:none;">
    <li style="float:left;">
        <div><img src="page-resources/img/Logo.jpg"></div>
    </li>
    <li style="float:right;">
        <div style="margin-right:5px;margin-top:15px;">
           <ul style="list-style:none;">

               <c:if test="${user!=null}">
                   <li style="float:left;">

                       <div style="margin-top:10px;">
                           <x2>


                                 <ul style="list-style:none;float:right;">
                                     <li style="float:left;"><div><img src="page-resources/img/user_logo.jpg"></div></li>
                                     <li style="float:left;"><div> <B>Hi ${user.username}</B> &nbsp;&nbsp; | <a style="text-decoration:none;color:black;" href="viewMyAccount.action">My Account</a>   &nbsp;&nbsp;  |  &nbsp;&nbsp;<a style="text-decoration:none;color:black;" href="logout.action">Logout</a></div></li>
                                 </ul>


                            </x2>
                       </div>
                   </li>
               </c:if>
           </ul>
        </div>
    </li>
</ul>
</div>
