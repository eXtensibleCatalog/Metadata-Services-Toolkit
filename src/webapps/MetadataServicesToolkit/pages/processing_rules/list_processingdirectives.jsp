<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mst" uri="mst-tags"%>

<c:import url="/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">

<html>
    <head>
        <title>List Processing Directives</title>
        <c:import url="/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
		<LINK href="page-resources/css/bodylayout.css" rel="stylesheet" type="text/css">

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script>           
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/list_processingdirectives.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
       
    </head>


 <body class="yui-skin-sam">

        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">

            <!-- page header - this uses the yahoo page styling -->
            <div id="hd">

                <!--  this is the header of the page -->
                <c:import url="/inc/header.jsp"/>

                <!--  this is the header of the page -->
                <c:import url="/inc/menu.jsp"/>
                <jsp:include page="/inc/breadcrumb.jsp">

                    <jsp:param name="bread" value="Processing Rules , List Processing Rules" />

                </jsp:include>
                
            </div>
            <!--  end header -->

            <!-- body -->
            <div id="bd">
                
                 <!-- Display of error message -->
                 <c:if test="${errorType != null}">
                    <div id="server_error_div">
                    <div id="server_message_div" class="${errorType}">
                        <img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg">
                        <span class="errorText">
                            <mst:fielderror error="${fieldErrors}">
                            </mst:fielderror>
                        </span>
                    </div>
                    </div>
                 </c:if>
                 <div id="error_div"></div>

                 <div class="clear">&nbsp;</div>
                 
                <div class="viewTable">
                 <table width="100%">
                        <thead>
                            <tr>
                                <td>#ID</td>
                                <td>
                                   <div align="left">

                                           <span>Output Records from</span>
                                           <span style="margin-left:10px;margin-right:10px;"><img src="page-resources/img/greenarrow.jpg"/></span>
                                           <span>To be Processed by</span>

                                    </div>

                                </td>
                                <td>Formats</td>
                                <td>Sets</td>
                                <td>Delete</td>
                            </tr>
                        </thead>

                        <tbody>


                            <c:forEach var="directive" items="${processingDirectives}" varStatus="directiveCount">

                                <c:set var="totalFormatsSize" value="${fn:length(directive.triggeringFormats)}"/>
                                
                                <c:set var="formatList" value=""/>
                                    <c:forEach var="triggerFormat" items="${directive.triggeringFormats}" varStatus="triggerFormatCount">
                                        <c:choose>
                                            <c:when test="${triggerFormatCount.count==1}">
                                                <c:set var="formatList" value="${triggerFormat.name}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="formatList" value="${formatList},${triggerFormat.name}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                <c:set var="totalSetsSize" value="${fn:length(directive.triggeringSets)}"/>
                                 
                                 <c:set var="setList" value=""/>
                                    <c:forEach var="triggerSet" items="${directive.triggeringSets}" varStatus="triggerSetCount">
                                        <c:choose>
                                            <c:when test="${triggerSetCount.count==1}">
                                                <c:set var="setList" value="${triggerSet.setSpec}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="setList" value="${setList},${triggerSet.setSpec}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>


                              <tr>
                                <c:set var="url" value='/MetadataServicesToolkit/viewEditProcessingDirectives.action?ProcessingDirectiveId=${directive.id}' />

                                <c:set var="x1" value="${directive.sourceProvider}" />
                                <c:set var="x2" value="${directive.sourceService}" />

                               
                               

                                <c:if test='${x1!=null}'>
                                    <c:set var="bool" value="${true}" />

                                </c:if>
                                <c:if test='${x2!=null}'>
                                  <c:set var="bool" value="${false}" />

                                </c:if>

                                <td class="sortcolumn" align="left"><div style="margin-left:10px;margin-top:10px;height:17px;text-align:left;"><c:out value="${directive.id}" /></div></td>



                                    <c:if test="${bool=='true'}">
                                       
                                        <td>
                                            <div align="left" style="margin-left:5px;">
                                                <a style="color:black;" href="<c:out value="${url}" />">
                                                   <U> ${x1.name} >>> ${directive.service.name} </U>
                                                </a>
                                            </div>
                                        </td>
                                    </c:if>
                                    <c:if test="${bool=='false'}">
                                        <td>

                                               <div align="left" style="margin-left:5px;">
                                                    <a style="color:black;" href="<c:out value="${url}" />">
                                                        <U> ${x2.name} >>> ${directive.service.name} </U>
                                                    </a>
                                               </div>
                                        </td>
                                    </c:if>

                                <td title="${formatList}">
                                    <c:set var="totalFormatsSize" value="${fn:length(directive.triggeringFormats)}"/>
                                    <div style="text-align:left" id="formatsList">
                                        <c:forEach var="triggerFormat" items="${directive.triggeringFormats}" varStatus="triggerFormatCount">
                                            <c:if test="${triggerFormatCount.count==1}">
                                                ${triggerFormat.name}.....
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </td>
                                <td title="${setList}">
                                    <div style="text-align:left" id="setsList">
                                        <c:set var="totalSetsSize" value="${fn:length(directive.triggeringSets)}"/>
                                        <c:forEach var="triggerSet" items="${directive.triggeringSets}" varStatus="triggerSetCount">
                                            <c:if test="${triggerSetCount.count==1}">
                                                ${triggerSet.setSpec}.....
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </td>
                                <td><button class="xc_button" type="button" name="deleteService" onclick="javascript:YAHOO.xc.mst.directives.deleteDirective.deleteProcessingDirective(${directive.id});">Delete</button></td>

                               </tr>
                           </c:forEach>
                      
                    </tbody>
                 </table>
                </div>


		      <div id="deleteProcessingDirectiveDialog" class="hidden">
		          <div class="hd">Delete Processing Directive</div>
			      <div class="bd">
			          <form id="deleteProcessingDirective" name="deleteProcessingDirective" method="POST" 
			              action="deleteProcessingDirective.action">
			              
			              <input type="hidden" id="processingDirective_id" name="processingDirectiveId"/>
			              
				          <p>Are you sure you want to delete the Processing directive?</p>
			          </form>
			      </div>
		      </div>

           </div>
        </div>


</body>
