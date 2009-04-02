<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:import url="/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">


<html>
    <head>
        <title>New Service</title>
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
            </div>
            <!--  end header -->

            <!-- body -->
            <div id="bd">

                 <!-- Display of error message -->
                 <c:if test="${errorType != null}">
                    <div id="server_error_div">
                    <div id="server_message_div" class="${errorType}">
                        <img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg">
                        <s:fielderror cssClass="errorMessage"/>
                    </div>
                    </div>
                 </c:if>
                <div id="error_div"></div>

                 <div class="clear">&nbsp;</div>
                 
                <table>

                    <tr>
                      <td valign="top">Service Name &nbsp;&nbsp;</td>
                      <td><input type="text" id="serviceName" name ="serviceName" style="width:200px;height:25px;" maxlength="255"><br><br></td>
                    </tr>

                    <tr>
                      <td valign="top">Configuration File  &nbsp;&nbsp;</td>
                      <td>
                          <input type="text" id="configurationFile" name ="configurationFile" style="width:200px;height:25px;" maxlength="255"> &nbsp;&nbsp;&nbsp;&nbsp;
                          <input class="xc_button" type="file" name="browse">
                          <br><br>
                      </td>
                    </tr>
                    <tr>
                        <td>
                            <button class="xc_button_small" type="button" name="cancel">Cancel</button>&nbsp;&nbsp;
                            <button class="xc_button" type="button" name="addService">Save</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
 </body>
</html>