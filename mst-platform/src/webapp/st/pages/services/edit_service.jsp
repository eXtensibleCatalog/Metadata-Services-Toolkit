<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mst" uri="mst-tags"%>

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

                    <jsp:param name="bread" value="Services | <a href='listServices.action'><U> All Services </U> </a> | Edit Service" />

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
		<form name="editService" method="post" action="editService.action">
                <table style="margin-left:10px" width="100%">
                <input type="hidden" name="serviceId" id="serviceId" value=${temporaryService.id}>
                    <br>
                    <tr>
                       <td colspan="2" >                          
                                Step 1: To edit a service, see <a style="text-decoration:underline;color:blue;"href="page-resources/docs/MST User Manual.docx">user manual</a> for configuration file information.<br>
                                (If you have made any changes to a service, you must come to update it)<br><br>
                       </td>
                    </tr>
                    <br>
                    <tr>
                      <td valign="top">Step 2: Select the Configuration File  : 
                          
                          <select id="selectedLocation" name ="selectedLocation">
                              <c:forEach var="serviceFile" items="${serviceFiles}" varStatus="a">
                                 
                                  <c:choose>
                                      <c:when test="${fn:contains(temporaryService.xccfgFileName,serviceFile.name)}">
                                          <option selected id="${serviceFile.name}" value="${serviceFile.path}">${serviceFile.name}
                                      </c:when>
                                      <c:otherwise>
		                                  <option id="${serviceFile.name}" value="${serviceFile.path}">${serviceFile.name}
                                      </c:otherwise>
                                  </c:choose>
                                  
                              </c:forEach>
                          </select>

                          <br><br>
                      </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="checkbox" id="reprocess_required" name="reprocessRecords" value="true" 
							<c:if test="${reprocessRecords}">
								checked
							</c:if>/> Check this box if records needs to be reprocessed
                        </td>
                    </tr>  
				    <tr>
				    <td colspan="2">
				       <font color="red">Note: On Clicking 'Save' button, the records processed by this service will be deleted and reprocessed again according to the new Configuration file changes.
				       If you donot wish to reprocess the records, then click 'Cancel'.</font>
				       <br>
				       <br>
				    </td>
                    </tr>
                    
                    
                    <tr>
                        <td>
                            <button class="xc_button_small" onclick="javascript:YAHOO.xc.mst.services.alterService.cancel();" type="button" name="cancel">Cancel</button>&nbsp;&nbsp;
                            <button class="xc_button" type="submit" name="editService">Save</button>
                        </td>
                    </tr>
                  
                </table>
              </form>

            </div>
            <!--  this is the footer of the page -->
            <c:import url="/inc/footer.jsp"/>              
        </div>
 </body>
</html>