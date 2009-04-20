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

    