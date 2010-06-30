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
<%@ taglib prefix="mst" uri="mst-tags"%>
<c:import url="/st/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">


<html>
    <head>
        <title>Edit Repository</title>
        <c:import url="/st/inc/meta-frag.jsp"/>

        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >
        <LINK HREF="page-resources/css/bodylayout.css" REL="stylesheet" TYPE="text/css">
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >

        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>     

        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script> 
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/edit_repository.js"></SCRIPT>

     
</head>
 <body class="yui-skin-sam">
        <%@ taglib prefix="s" uri="/struts-tags" %>
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">

		<!-- page header - this uses the yahoo page styling -->
		<div id="hd">

            <!--  this is the header of the page -->
            <c:import url="/st/inc/header.jsp"/>

            <!--  this is the header of the page -->
            <c:import url="/st/inc/menu.jsp"/>
             <jsp:include page="/st/inc/breadcrumb.jsp">

                    <jsp:param name="bread" value="Repository | <a style='text-decoration:none;color:#292929;' href='allRepository.action'><U>All Repositories</U></a> | Edit Repository" />

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

        <form action="editRepository.action?RepositoryId=<s:property value="repositoryId"/>" name="editRepositoryForm" method="post">
              
        <div style="margin-left:10px;font-family: verdana,sans-serif;font-size: 12px;">

                OAI-PMH <B>Repository Name</B> <br>
                    <input type="text" id="repositoryName" name="repositoryName" value="<s:property value="repositoryName"/>" style="width:305px;" maxlength="255"><br><br>
                 OAI-PMH <B>Repository URL</B> <br>
                     <input type="text" id="repositoryURL" name="repositoryURL" value="<s:property value="repositoryURL"/>" style="width:470px;" maxlength="255">
                
                 <div style="margin-top:20px;margin-left:0px;vertical-align:bottom;">
                   <button style="vertical-align:bottom;" class="xc_button_small" type="button" onclick="javascript:YAHOO.xc.mst.repository.editRepository.cancel();" name="cancel">Cancel</button> &nbsp;&nbsp;&nbsp;
                   <button class="xc_button" type="button" name="edit" onclick="javascript:YAHOO.xc.mst.repository.editRepository.editValidator()">Save</button>

                 </div>
        </div>
        </form>
        <br><br>
        </div>
	      <div id="processingDialog" class="hidden">
	          <div class="hd">Editing Repository</div>
		      <div class="bd">
		      		
				 <strong>Updating and validating the repository. Please wait.</strong>&nbsp;&nbsp;<img src="${pageContext.request.contextPath}/page-resources/img/loading_graphic_gray.gif">
				 <div class="clear">&nbsp;</div>
		      </div>
	      </div>
	       <!--  this is the footer of the page -->
            <c:import url="/st/inc/footer.jsp"/>   
        </div>
</body>
