
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<c:choose>
    <c:when test="${requestString!=null}">
        ${requestString}
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${noHarvestString==true}">
                 ERROR : No Harvest Schedule has been set up for the specified Provider
            </c:when>
            <c:otherwise>
                 ERROR : Unable to display Request String (Note: If no harvest schedule is set up for the provider, the OAI request string will not be constructed)
            </c:otherwise>
        </c:choose>
       
    </c:otherwise>
</c:choose>