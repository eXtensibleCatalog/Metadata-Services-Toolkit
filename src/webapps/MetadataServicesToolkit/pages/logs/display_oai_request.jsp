<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<c:choose>
    <c:when test="${oaiRequest!=null}">${oaiRequest}</c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${noHarvestString==true}">
                 No Harvest Schedule has been set up for the specified Provider
            </c:when>
            <c:otherwise>
                 The provider has not yet been harvested so there are no OAI requests to display
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>