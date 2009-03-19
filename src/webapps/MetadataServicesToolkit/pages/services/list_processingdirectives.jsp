<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:import url="/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">

<html>
    <head>
        <title>List Processing Directives</title>
        <c:import url="/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
		<LINK href="page-resources/css/bodylayout.css" rel="stylesheet" type="text/css">

        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/list_processingdirectives.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
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

                    <jsp:param name="bread" value="Services , List Processing Directives" />

                </jsp:include>
                <div class="errorMessage"> <s:fielderror /> </div>
            </div>
            <!--  end header -->

            <!-- body -->
            <div id="bd">

                <div class="viewTable">
                 <table width="100%">
                        <thead>
                            <tr>
                                <td>#ID</td>
                                <td>
                                   <div align="left">
                                        
                                            <ul>
                                                <li style="float:left;"><div>Output Records from</div></li>
                                                <li style="float:left;"><div><img src="page-resources/img/greenarrow.jpg"></div></li>
                                                <li style="float:left;"><div>To be Processed by</div></li>
                                            </ul>

                                     
                                   </div>

                                </td>
                                <td>Formats</td>
                                <td>Sets</td>
                                <td>Delete</td>
                            </tr>
                        </thead>

                        <tbody>


                            <c:forEach var="n" items="${processingDirectivesList}" varStatus="a">

                              <tr>
                                <c:set var="url" value='/MetadataServicesToolkit/viewEditProcessingDirectives.action?ProcessingDirectiveId=${n.id}' />

                                <c:set var="x1" value="${n.sourceProvider}" />
                                <c:set var="x2" value="${n.sourceService}" />

                               
                               

                                <c:if test='${x1!=null}'>
                                    <c:set var="bool" value="${true}" />

                                </c:if>
                                <c:if test='${x2!=null}'>
                                  <c:set var="bool" value="${false}" />

                                </c:if>

                                <td class="sortcolumn"><div style="margin-left:10px;margin-top:10px;height:17px;text-align:center"><c:out value="${n.id}" /></div></td>



                                    <c:if test="${bool=='true'}">
                                       
                                        <td>
                                            <div align="left">
                                                <a style="color:black;" href="<c:out value="${url}" />">
                                                    <ul>
                                                        <li style="float:left;"><div><U>${x1.name}</U></div></li>
                                                        <li style="float:left;"><div><img src="page-resources/img/greenarrow.jpg"></div></li>
                                                        <li style="float:left;"><div><U>${n.service.name}</U></div></li>
                                                    </ul>

                                                </a>
                                            </div>
                                        </td>
                                    </c:if>
                                    <c:if test="${bool=='false'}">
                                        <td>
                                            <div align="left">
                                                <a style="color:black;" href="<c:out value="${url}" />">
                                                    <ul>
                                                        <li style="float:left;"><div><U>${x2.name}</U></div></li>
                                                        <li style="float:left;"><div><img src="page-resources/img/greenarrow.jpg"></div></li>
                                                        <li style="float:left;"><div><U>${n.service.name}</U></div></li>
                                                    </ul>

                                                </a>
                                            </div>
                                        </td>
                                    </c:if>

                                <td>
                                    <c:set var="totalFormatsSize" value="${fn:length(n.triggeringFormats)}"/>
                                    <div style="text-align:left">
                                        <c:forEach var="m" items="${n.triggeringFormats}" varStatus="b">
                                            <c:choose>
                                                <c:when test="${b.count < totalFormatsSize}">
                                                    ${m.name},
                                                </c:when>
                                                <c:otherwise>
                                                    ${m.name}
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </div>
                                </td>
                                <td>
                                    <div style="text-align:left">
                                        <c:set var="totalSetsSize" value="${fn:length(n.triggeringSets)}"/>
                                        <c:forEach var="m" items="${n.triggeringSets}" varStatus="b">
                                            <c:choose>
                                                <c:when test="${b.count < totalSetsSize}">
                                                    ${m.setSpec},
                                                </c:when>
                                                <c:otherwise>
                                                    ${m.setSpec}
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </div>
                                </td>
                                <td><button class="xc_button" type="button" name="deleteService" onclick="javascript:YAHOO.xc.mst.services.deleteProcessingDirective(${n.id});">Delete</button></td>

                               </tr>
                           </c:forEach>
                      
                    </tbody>
                 </table>
                </div>
                <form name="deleteProcessingDirective" method="post" action= "deleteProcessingDirective.action">
                    <input type="hidden" id="processingDirectiveId" name="processingDirectiveId"/>
                </form>
           </div>
        </div>


</body>
