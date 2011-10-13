<!--
  * Copyright (c) 2009 eXtensible Catalog Organization
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
      <input type="hidden" id="userHasPermission" name="userHasPermission" value="true">
        <div class="serviceStatus" id="serviceBar">

        </div>
    </mst:checkUserPermission>
</c:if>

<div id="header">


  <div class="header_logo">
                        <img  src="page-resources/img/Logo.jpg">
                          <c:if test="${user!=null}">

    <span class="headerMessage"> <B><img style="position:relative;top:-4px;"  src="page-resources/img/user_logo.jpg" > Hi ${user.username}</B> &nbsp;&nbsp; | <a href="viewMyAccount.action">My Account</a>   &nbsp;&nbsp;  |  &nbsp;&nbsp;<a href="logout.action">Logout</a></span>

      </c:if>
        </div>

</div>
