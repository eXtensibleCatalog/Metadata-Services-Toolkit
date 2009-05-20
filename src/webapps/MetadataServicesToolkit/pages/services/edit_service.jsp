<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:import url="/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">


<html>
    <head>
        <title>Edit Service</title>
        <c:import url="/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
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
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/edit_service.js"></SCRIPT>
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

                    <jsp:param name="bread" value="Services ,<a href='listServices.action'><U> All Services </U> </a> ,Edit Service" />

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
                            <mstFieldError maps=<s:fielderror/>
                            </mstFieldError>
                        </span>
                    </div>
                    </div>
                 </c:if>
                <div id="error_div"></div>

                 <div class="clear">&nbsp;</div>

                <table>

                    <tr>
                      <td valign="top">Configuration File  &nbsp;&nbsp;</td>
                      <td>
                          
                          <select id="fileLocation" name ="fileLocation">
                              <c:forEach var="n" items="${serviceFiles}" varStatus="a">
                                  <c:choose>
                                      <c:when test="${fn:contains(temporaryService.xccfgFileName,n)}">
                                          <option selected id="${n}" value="${n}">${n}
                                      </c:when>
                                      <c:otherwise>
                                          <option id="${n}" value="${n}">${n}
                                      </c:otherwise>
                                  </c:choose>
                                  
                              </c:forEach>
                          </select>

                          <br><br>
                      </td>
                    </tr>
                    <tr>
                        <td>
                            <button class="xc_button_small" onclick="javascript:YAHOO.xc.mst.services.alterService.cancel();" type="button" name="cancel">Cancel</button>&nbsp;&nbsp;
                            <button class="xc_button" type="button" onclick="javascript:YAHOO.xc.mst.services.alterService.editService(${temporaryService.id});" name="editService">Save</button>
                        </td>
                    </tr>
                </table>
                <form name="editService" method="post">
                    <input type="hidden" name="selectedLocation" id="selectedLocation">
                    <input type="hidden" name="serviceId" id="serviceId">
                </form>
            </div>
        </div>
 </body>
</html>