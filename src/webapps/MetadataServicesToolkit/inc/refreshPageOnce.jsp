<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<c:set var="paramVal" value="${param.serviceStatus}"/>
<c:set var="abort" value="aborted"/>
<c:set var="pause" value="paused"/>
<c:set var="resume" value="resumed"/>

<c:if test="${paramVal eq abort}">
    MSTServiceAborted
</c:if>
<c:if test="${paramVal eq pause}">
    MSTServicePaused
</c:if>
<c:if test="${paramVal eq resume}">
    MSTServiceResumed
</c:if>

    