<!--
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  -->
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="mst" uri="mst-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!--  document type -->
<c:import url="/st/inc/doctype-frag.jsp"/>

<html>
    <head>
        <title>Browse Records</title>
        <c:import url="/st/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
    <LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
    <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>
      <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script>
    </head>

    <body class="yui-skin-sam">
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">

    <!-- page header - this uses the yahoo page styling -->
    <div id="hd">

            <!--  this is the header of the page -->
            <c:import url="/st/inc/header.jsp"/>

            <c:import url="/st/inc/menu.jsp"/>

            <c:url var="viewResults" value="browseRecords.action">
          <c:param name="query" value="${query}"/>
          <c:param name="searchXML" value="${searchXML}"/>
          <c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
            <c:param name="selectedFacetValues" value="${selectedFacetValues}"/>
          <c:param name="rowStart" value="${rowStart}"/>
          <c:param name="startPageNumber" value="${startPageNumber}"/>
        <c:param name="currentPageNumber" value="${currentPageNumber}"/>
        </c:url>
            <jsp:include page="/st/inc/breadcrumb.jsp">
            <jsp:param name="bread" value="Browse Records | <a href='${viewResults}'>Search Results</a>| View Record
                 ${record.oaiIdentifier}" />
            </jsp:include>

     </div>
    <!--  end header -->

    <!-- body -->
    <div id="bd">

    <div class="record_metadata">
      Status:
      <c:choose>
        <c:when test="${record.status == 'A'}">Active</c:when>
        <c:when test="${record.status == 'H'}">Held</c:when>
        <c:when test="${record.status == 'D'}">Deleted</c:when>
      </c:choose>
      <br />
      <c:if test="${record.provider != null}">
        Repository:  ${record.provider.name}<br>
        Repository URL:
          <a href="${record.provider.oaiProviderUrl}?verb=GetRecord&identifier=${record.harvestedOaiIdentifier}&metadataPrefix=${record.format.name}">
            ${record.provider.oaiProviderUrl}
          </a>
        <br />
      </c:if>
      <c:if test="${record.service != null}">Service:  ${record.service.name}<br></c:if>
      <c:if test="${record.harvest != null}">Harvest:  ${record.harvestScheduleName}<br></c:if>
      Schema:  ${record.format.name}<br>
      Sets:
      <c:forEach var="set" items="${record.sets}" varStatus="status">
        <c:if test="${status.count > 1}">,&nbsp;</c:if>
        <c:choose>
          <c:when test="${fn:length(set.displayName) > 0}">${set.displayName}</c:when>
          <c:otherwise>${set.setSpec}</c:otherwise>
        </c:choose>
      </c:forEach>
      <br>
      <c:if test="${record.createdAt != null}">Created at: ${record.createdAt}<br></c:if>
      <c:if test="${record.updatedAt != null}">Updated at: ${record.updatedAt}<br></c:if>
      OAI datestamp: ${record.oaiDatestamp}<br>
      <c:choose>
        <c:when test="${record.harvestedOaiIdentifier != null}">
          OAI identifier: ${record.harvestedOaiIdentifier}<br>
        </c:when>
        <c:otherwise>
          OAI identifier: ${record.oaiIdentifier}<br>
        </c:otherwise>
      </c:choose>

      <%--
      OAI header: ${record.oaiHeader}<br>  --%>
      <c:if test="${record.messages != '[]'}">
        messages:
        <c:forEach var="m" items="${record.messages}" varStatus="status">
          <div class="redError">${m.message}
          <c:if test="${m.detail != null}">
            ${m.detail}
          </c:if>
          </div>
        </c:forEach>
      </c:if>
      <c:url var="viewPredecessorRecord" value="browseRecords.action">
          <c:param name="query" value=""/>
          <c:param name="addFacetName" value="successor"/>
          <c:param name="addFacetValue" value="${record.id}"/>
          <c:param name="searchXML" value="false"/>
         </c:url>
         <c:url var="viewSuccessorRecord" value="browseRecords.action">
            <c:param name="query" value=""/>
            <c:param name="addFacetName" value="processed_from"/>
            <c:param name="addFacetValue" value="${record.id}"/>
            <c:param name="searchXML" value="false"/>
         </c:url>
             <c:if test="${record.numberOfPredecessors > 0 && record.numberOfSuccessors > 0}">
        <a href="${viewPredecessorRecord}">${record.numberOfPredecessors}
        <c:if test="${record.numberOfPredecessors == 1}">
          Predecessor
        </c:if>
        <c:if test="${record.numberOfPredecessors > 1}">
          Predecessors
        </c:if></a>
        &nbsp;<img src="page-resources/img/white-book-both.jpg">&nbsp;
        <a href="${viewSuccessorRecord}">${record.numberOfSuccessors}
        <c:if test="${record.numberOfSuccessors == 1}">
          Successor
        </c:if>
        <c:if test="${record.numberOfSuccessors > 1}">
          Successors
        </c:if>
        </a>
          </c:if>
          <c:if test="${record.numberOfPredecessors > 0 && record.numberOfSuccessors < 1}">
          <a href="${viewPredecessorRecord}">${record.numberOfPredecessors}
          <c:if test="${record.numberOfPredecessors == 1}">
            Predecessor
          </c:if>
          <c:if test="${record.numberOfPredecessors > 1}">
            Predecessors
          </c:if>
          </a>
              &nbsp;<img src="page-resources/img/white-book-left.jpg">
          </c:if>
      <c:if test="${record.numberOfSuccessors > 0 && record.numberOfPredecessors < 1}">
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <img src="page-resources/img/white-book-right.jpg">
        &nbsp;<a href="${viewSuccessorRecord}">${record.numberOfSuccessors}
        <c:if test="${record.numberOfSuccessors == 1}">
          Successor
        </c:if>
        <c:if test="${record.numberOfSuccessors > 1}">
          Successors
        </c:if>
        </a>

          </c:if>
       </div>

      <div class="record_box">
        <pre class="preWrap">${recordXML}</pre>
      </div>
     </div>
    <!--  end body -->
            <!--  this is the footer of the page -->
            <c:import url="/st/inc/footer.jsp"/>
        </div>
        <!-- end doc -->
    </body>
</html>


