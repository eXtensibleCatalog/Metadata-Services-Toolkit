<%--
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<c:choose>
    <c:when test="${oaiRequest!=null}">${oaiRequest}</c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${noHarvestString==true}">
                 No harvest schedule has been set up for the specified repository.
            </c:when>
            <c:otherwise>
                 The repository has not yet been harvested so there are no OAI requests to display.
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>