<!--
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="mst" uri="mst-tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:import url="/st/inc/doctype-frag.jsp"/>

<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
<html>
<head>
 <title>View Repository</title>
<c:import url="/st/inc/meta-frag.jsp"/>

<LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
<LINK href="page-resources/yui/assets/skins/sam/skin.css"  rel="stylesheet" type="text/css" >
<LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
<LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >
<LINK HREF="page-resources/css/bodylayout.css" REL="stylesheet" TYPE="text/css">
<LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
<LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
<LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >

<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container_core-min.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>     
<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script> 
<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
 <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/view_repository.js"></SCRIPT>

</head>


<body class="yui-skin-sam">


        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">

		<!-- page header - this uses the yahoo page styling -->
		<div id="hd">

            <!--  this is the header of the page -->
            <c:import url="/st/inc/header.jsp"/>

            <!--  this is the header of the page -->
            <c:import url="/st/inc/menu.jsp"/>
            

		<c:if test="${fn:length(provider.name) > 60}">
		 	<jsp:include page="/st/inc/breadcrumb.jsp">
                    		<jsp:param name="bread" value="Repository | <a style='color:#292929;' href='allRepository.action'><U>All Repositories</U></a> | View Repository : <a style='text-decoration:none;color:black;' title='${provider.name}'> ${fn:substring(provider.name,0,60)}...</a>" />
                     	</jsp:include>
                </c:if>
                
                <c:if test="${fn:length(provider.name) <= 60}">
                	 <jsp:include page="/st/inc/breadcrumb.jsp">
                		<jsp:param name="bread" value="Repository | <a style='color:#292929;' href='allRepository.action'><U>All Repositories</U></a> | View Repository : ${provider.name}" />
                	 </jsp:include>
                 </c:if>

           

            <div id="error_div"></div>
            <!-- Display of error message -->
                <c:if test="${errorType != null}">
                    <div id="server_error_div">
                    <div id="server_message_div" class="${errorType}">
                        <img  src="${pageContext.request.contextPath}/page-resources/img/${errorType}.jpg">
                        <c:if test="${errorType == 'error'}">
                        	<span class="errorText">
                                <mst:fielderror error="${fieldErrors}">
                                </mst:fielderror>
                            </span>
                       	</c:if>
                        <c:if test="${errorType == 'info'}">
                        	<div class="jsErrorMessage"> ${message}</div>
                        </c:if>
                    </div>
                    </div>
                 </c:if> 
 		</div>
		<!--  end header -->

		<!-- body -->
		<div id="bd">

            <div class="clear">&nbsp;</div>


			        <table class="formTable">
			
			            
			            <tr>
			                <td style="font-size:13px;">
						<table>
						   <tr>
						       <td class="label" valign="top">
							   Repository name:
						       </td>
						       <td>
							   ${provider.name}
						       </td>
						   </tr>
					       </table>                                
                                    
                            		</td>
                            		
                            		
                            		<td rowspan="3">
                            		 <table width="220" border="1" style="color:#313131;">
					                           <c:choose>
					                               <c:when test="${provider.identify==true}">
					                                    <tr>
					                                        <td height="33" bgcolor="#edfaff">
					                                            
					                                            <ul style="list-style:none;">
					                                                <li style="float:left;"><div style="width:90px;" align="right"><B>&nbsp;Identify</B> :</div></li>
					                                                <li style="float:left;position:relative;top:-3px;"><div><img src="page-resources/img/bluetick.jpg"></div></li>
					                                                <li style="float:left;"><div>Success</div></li>
					                                            </ul>
					                                        </td>
					                                    </tr>
					                               </c:when>
					                               <c:otherwise>
					                                    <tr>
					                                        <td height="33" bgcolor="#ffeded">
					                                            <ul style="list-style:none;">
					                                                <li style="float:left;"><div style="width:90px;" align="right"><B>&nbsp;Identify</B> : </div></li>
					                                                <li style="float:left;position:relative;top:-3px;"><div><img src="page-resources/img/error_triangle.jpg"></div></li>
					                                                <li style="float:left;"><div>Error</div></li>
					                                            </ul>
					                                        </td>
					                                    </tr>
					                               </c:otherwise>
					                           </c:choose>
					                           <c:choose>
					                               <c:when test="${provider.listFormats==true}">
					                                    <tr>
					                                        <td height="33" bgcolor="#edfaff">
					                                            <ul style="list-style:none;">
					                                                <li style="float:left;"><div style="width:90px;" align="right"><B>&nbsp;List Formats</B> :</div></li>
					                                                <li style="float:left;position:relative;top:-3px;"><div><img src="page-resources/img/bluetick.jpg"></div></li>
					                                                <li style="float:left;"><div>Success</div></li>
					                                            </ul>
					                                        </td>
					                                    </tr>
					                               </c:when>
					                               <c:otherwise>
					                                    <tr>
					                                        <td height="33" bgcolor="#ffeded">
					                                            <ul style="list-style:none;">
					                                                <li style="float:left;"><div style="width:90px;" align="right"><B>&nbsp;List Formats</B> :</div></li>
					                                                <li style="float:left;position:relative;top:-3px;"><div><img src="page-resources/img/error_triangle.jpg"></div></li>
					                                                <li style="float:left;"><div>Error</div></li>
					                                            </ul>
					                                        </td>
					                                    </tr>
					                               </c:otherwise>
					                           </c:choose>
					                            <c:choose>
					                               <c:when test="${provider.listSets==true}">
					                                     <tr>
					                                        <td height="33" bgcolor="#edfaff">
					                                            <ul style="list-style:none;">
					                                                <li style="float:left;"><div style="width:90px;" align="right"><B>&nbsp;List Sets</B> : </div></li>
					                                                <li style="float:left;position:relative;top:-3px;"><div><img src="page-resources/img/bluetick.jpg"></div></li>
					                                                <li style="float:left;"><div>Success</div></li>
					                                            </ul>
					                                        </td>
					                                     </tr>
					                               </c:when>
					                               <c:otherwise>
					                                     <tr>
					                                        <td height="33" bgcolor="#ffeded">
					                                            <ul style="list-style:none;">
					                                                <li style="float:left;"><div style="width:90px;" align="right"><B>&nbsp;List Sets</B> : </div></li>
					                                                <li style="float:left;position:relative;top:-3px;"><div><img src="page-resources/img/error_triangle.jpg"></div></li>
					                                                <li style="float:left;"><div>Error</div></li>
					                                            </ul>
					                                        </td>
					                                     </tr>
					                               </c:otherwise>
					                           </c:choose>
					                           
					                           
                       </table>
                            		
                            		
                            		</td>
			            </tr>
			            <tr>
                            <td style="font-size:13px;">
                               <table>
                                   <tr>
                                       <td class="label" valign="top">
                                           URL:
                                       </td>
                                       <td>
                                           ${provider.oaiProviderUrl}
                                       </td>
                                   </tr>
                               </table>
                                    
                            </td>
			            </tr>
                        <tr>
                            <td class="label" style="margin-top:20px;font-size:14px;">
                                <br> Validation Results
                            </td>
                        </tr>
			            
			            <tr>
                            <td width="">
                                <ul style="list-style:none;">
                                    <li style="float:left;"><div align="right" style="width:140px;"><B>OAI Versions Supported </B></div> </li>
                                    <li style="float:left;"><div>&nbsp;:</div></li>
                                    <li style="float:left;"><div style="margin-left:15px;">${provider.protocolVersion} </div></li>
                                </ul>
                            </td>
			                
			            </tr>
			            <tr>
                            <td>
                                <ul style="list-style:none;">
                                    <li style="float:left;"><div align="right" style="width:140px;"><B>Last Validation Date</B> </div> </li>
                                    <li style="float:left;"><div>&nbsp;:</div></li>
                                    <li style="float:left;"><div style="margin-left:15px;">${provider.lastValidationDate}</div></li>
                                </ul>
                            </td>
			                
			            </tr>

			            <tr>
                            <td>
                                <ul style="list-style:none;">
                                    <li style="float:left;"><div align="right" style="width:140px;"><B>Creation Date</B> </div> </li>
                                    <li style="float:left;"><div>&nbsp;:</div></li>
                                    <li style="float:left;"><div style="margin-left:15px;">${provider.createdAt}</div></li>
                                </ul>
                            </td>
			               
			            </tr>
			            <tr>
                            <td>
                                <ul style="list-style:none;">
                                    <li style="float:left;"><div align="right" style="width:140px;"><B>Last Modified Date</B> </div> </li>
                                    <li style="float:left;"><div>&nbsp;:</div></li>
                                    <li style="float:left;"><div style="margin-left:15px;">${mst:simpleDateFormat(provider.updatedAt)} ${timeZone}</div></li>
                                </ul>
                            </td>
			                
			            </tr>
                        <tr>
                            <td colspan="2" style="margin-top:15px;">
                               <ul style="list-style:none;">
                                    <li style="float:left;"><div align="right" style="width:140px;"><B>Formats</B> </div> </li>
                                    <li style="float:left;"><div>&nbsp;:</div></li>
                                    <li style="float:left;">
                                        <div style="margin-left:15px;">
                                            <c:forEach var="format" items="${provider.formats}">
                                                ${format.name} <br>
                                            </c:forEach>
                                        </div>
                                    </li>
                                </ul>
                            </td>



			            </tr>
			            <tr>
                            <td  colspan="2" >
                                <ul style="list-style:none;">
                                    <li style="float:left;"><div align="right" style="width:140px;"><B>Sets Supported</B> </div> </li>
                                    <li style="float:left;"><div>&nbsp;:</div></li>
                                    <li style="float:left;">
                                        <div style="margin-left:15px;">
                                            <c:forEach var="set" items="${provider.sets}">
                                                ${set.displayName} (${set.setSpec}) <br>
                                            </c:forEach>
                                        </div>
                                    </li>
                                </ul>
                            </td>


			            </tr>
			       </table>
			           
	         
         	
                   
                      
                        
                          <br>
                          

            <hr size="1" style="color:#ced2d5;"><br>
            <form id="repositoryForm" name="repositoryForm" method="POST">
	    	<input type="hidden" name="repositoryId" value="${provider.id}"/>
	    </form>		  
              		<button style="width:140px;" name="edit" type="button" onclick="javascript:YAHOO.xc.mst.repository.viewRepository.editFunction();" class="xc_button">View Settings</button>
                        <button name="View Log" type="button" onclick="javascript:YAHOO.xc.mst.repository.viewRepository.downloadFile('HarvestIn',${provider.id});" class="xc_button">View Log</button>
                        <button name="Revalidate" type="button" id="revalidateRepository" class="xc_button">Revalidate</button>
                        <button style="width:140px;" name="delete" type="button" id="confirmDeleteRepository" class="xc_button">Delete Records</button>            
                        <button name="done" style="float:right;position:relative;top:-30px;" onclick="javascript:YAHOO.xc.mst.repository.viewRepository.done();" type="button" class="xc_button">Done</button>
             
        </div>


	      <div id="deleteRepositoryDialog" class="hidden">
	          <div class="hd">Delete Repository</div>
		      <div class="bd">
		          <form id="deleteRepository" name="deleteRepository" method="POST" 
		              action="deleteRepository.action">
		              
		              <input type="hidden" name="repositoryId" value="${provider.id}"/>
		              
			          <p>Are you sure you want to mark the repository records deleted?</p>
		          </form>
		      </div>
	      </div>

	      <div id="deleteRepositoryOkDialog" class="hidden">
	          <div class="hd">Delete Repository</div>
		      <div class="bd">
		          <form name="deleteRepositoryRecords" method="POST" 
		              action="deleteRepositoryRecords.action">
		      		  
		      		  <input type="hidden" name="repositoryId" value="${provider.id}"/>
			          <div id="deleteRepositoryError" cssClass="errorMessage"></div>
				Repository ${provider.name} contains ${provider.recordsAdded} harvested data records.  
				Continuing will result in the deletion of these records and in the deletion of related harvest schedules.  
				Are you sure?"


		      



		          </form>			          
		      </div>
	      </div>

	      <div id="processingDialog" class="hidden">
	          <div class="hd">Revalidate repository</div>
		      <div class="bd">
		      	
				 <strong>Validating the repository. Please wait.</strong>&nbsp;&nbsp;<img src="${pageContext.request.contextPath}/page-resources/img/loading_graphic_gray.gif">
				 <div class="clear">&nbsp;</div>
				 
		      </div>
	      </div>

	      <div id="deleteProcessingDialog" class="hidden">
	          <div class="hd">Deleting repository</div>
		      <div class="bd">
		      	
				 <strong>Deleting the repository. Please wait.</strong>&nbsp;&nbsp;<img src="${pageContext.request.contextPath}/page-resources/img/loading_graphic_gray.gif">
				 <div class="clear">&nbsp;</div>
		      </div>
	      </div>	      
            <!--  this is the footer of the page -->
            <c:import url="/st/inc/footer.jsp"/>  
       </div>
</body>
</html>
