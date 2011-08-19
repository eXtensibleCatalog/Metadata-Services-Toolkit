<%--
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="varPause" value="pause"/>
<c:set var="varPausing" value="pausing"/>
<c:set var="varResume" value="resume"/>
<c:set var="varAbort" value="abort"/>

<li style="float:left;margin-top:8px;">
<c:if test="${currentProcess!=null}">
  <c:choose>
            <c:when test="${displayType!=null}">
                    <c:if test="${displayType eq varPause}">
                        <img src="page-resources/img/loading_graphic.gif">
                    </c:if>
                    <c:if test="${displayType eq varResume}">

                    </c:if>
                    <c:if test="${displayType eq varAbort}">
                          <img src="page-resources/img/loading_graphic.gif">
                    </c:if>

            </c:when>
            <c:otherwise>
               <img src="page-resources/img/loading_graphic.gif">
            </c:otherwise>
       </c:choose>
</c:if>
<!-- Begin - This is required to identify that the response is a status bar and not login screen -->
<div style="display:none">ServiceStatus</div>
<!-- End - This is required to identify that the response is a status bar and not login screen -->

<c:if test="${currentProcess!=null}">
        <c:set var="nameLength" value="${fn:length(currentProcess)}"/>
        <c:choose>
            <c:when test="${nameLength<65}">
                <xxx id="currentProcess">${currentProcess}</xxx>
            </c:when>
            <c:otherwise>
                <xxx id="currentProcess" title="${currentProcess}">${fn:substring(currentProcess,0,65)}...</xxx>
            </c:otherwise>
        </c:choose>
</c:if>
<c:if test="${currentProcess == null}">
    <xxx id="currentProcess">No process running</xxx>
</c:if>
</li>

 </li>
