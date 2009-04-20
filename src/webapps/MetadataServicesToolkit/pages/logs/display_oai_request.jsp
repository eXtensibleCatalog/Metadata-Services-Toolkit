
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<c:choose>
    <c:when test="${requestString!=null}">
        ${requestString}
    </c:when>
    <c:otherwise>
        ERROR : Unable to display Request String
    </c:otherwise>
</c:choose>