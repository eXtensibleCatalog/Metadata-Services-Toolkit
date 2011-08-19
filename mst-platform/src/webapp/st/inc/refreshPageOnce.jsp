<%--
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<c:set var="paramVal" value="${param.serviceStatus}"/>
<c:set var="abort" value="aborted"/>
<c:set var="pause" value="paused"/>
<c:set var="resume" value="resumed"/>


<c:if test="${paramVal eq abort}">
    <c:if test="${displayText!=null}">
            Ended <br>
    </c:if>
    MSTServiceAborted
</c:if>
<c:if test="${paramVal eq pause}">
    <c:if test="${displayText!=null}">
            Ended <br>
    </c:if>
    MSTServicePaused
</c:if>
<c:if test="${paramVal eq resume}">
    <c:if test="${displayText!=null}">
            Ended <br>
    </c:if>
    MSTServiceResumed
</c:if>

