<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
 <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mst" uri="mst-tags"%>

<!--  document type -->
<c:import url="/inc/doctype-frag.jsp"/>

<html>
    <head>
        <title>Browse Records</title>
        <c:import url="/inc/meta-frag.jsp"/>
        
        <LINK href="page-resources/yui/reset-fonts-grids/reset-fonts-grids.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/assets/skins/sam/skin.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/base-mst.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/yui/menu/assets/skins/sam/menu.css"  rel="stylesheet" type="text/css" >       
        <LINK href="page-resources/css/global.css" rel="stylesheet" type="text/css" >
        <LINK href="page-resources/css/tables.css" rel="stylesheet" type="text/css" >
		<LINK href="page-resources/css/header.css" rel="stylesheet" type="text/css">
		<LINK href="page-resources/css/main_menu.css" rel="stylesheet" type="text/css" >

        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/utilities.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/yahoo-dom-event/yahoo-dom-event.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/connection/connection-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" src="page-resources/yui/container/container-min.js"></SCRIPT>    
    	<SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/element/element-beta-min.js"></script>          
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/menu/menu-min.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/js/main_menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="page-resources/yui/button/button-min.js"></script> 
        
        <SCRIPT LANGUAGE="JavaScript" SRC="pages/js/base_path.js"></SCRIPT>
    </head>
    
    <body class="yui-skin-sam">
        <!--  yahoo doc 2 template creates a page 950 pixles wide -->
        <div id="doc2">  

	<!-- page header - this uses the yahoo page styling -->
	<div id="hd">
   
            <!--  this is the header of the page -->
            <c:import url="/inc/header.jsp"/>
            
            <c:import url="/inc/menu.jsp"/>
	 	    <c:if test="${!initialLoad}">
               	<jsp:include page="/inc/breadcrumb.jsp"> 
	                    <jsp:param name="bread" value="Browse Records, Search Results" />
		         </jsp:include>
            </c:if>            
             <c:if test="${initialLoad}">
 	            <jsp:include page="/inc/breadcrumb.jsp"> 
	                    <jsp:param name="bread" value="Browse Records" />
	            </jsp:include>
            </c:if>    
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
                 
				<div class="facet_search_results">
					
					 	<div class="facetContainer">
					 		<c:forEach var="facet" items="${result.facets}">
						       <div class="facetTitle">
							   	<p><strong>
							   	<c:if test="${facet.name == 'format_name'}">
							   		Schema 
							   	</c:if>
								<c:if test="${facet.name == 'set_name'}">
							   		Set 
							   	</c:if>							   	
							   	<c:if test="${facet.name == 'provider_name'}">
							   		Repository 
							   	</c:if>
							   	<c:if test="${facet.name == 'service_name'}">
							   		Service 
							   	</c:if>
								<c:if test="${facet.name == 'harvest_start_time'}">
							   		Harvest 
							   	</c:if>
							   	<c:if test="${facet.name == 'error'}">
							   		Error 
							   	</c:if>
							   	
							   	
							   	</strong>
							   		<c:forEach var="filter" items="${result.facetFilters}">
								   		<c:if test="${facet.name == filter.name}">
										  	 <c:url var="removeFacet" value="browseRecords.action">
												  <c:param name="query" value="${query}"/>
												  <c:param name="searchXML" value="${searchXML}"/>
												  <c:param name="removeFacetName" value="${filter.name}"/>
												  <c:param name="removeFacetValue" value="${filter.value}"/>
												  <c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
												  <c:param name="selectedFacetValues" value="${selectedFacetValues}"/>
												  
											  </c:url>
										  	: ${filter.value} (<a href="${removeFacet}">Remove</a>)
								  		</c:if>
								  	</c:forEach>
							   	</p>
						       </div>

						       <div class="facetContent">
								<p>
								<c:forEach var="fcount" items="${facet.values}">
									<c:set var="facetExist" value="false"/>
									<c:forEach var="filter" items="${result.facetFilters}">
										<c:if test="${fcount.name == filter.value}">
											<c:set var="facetExist" value="true"/>
										</c:if>
									</c:forEach>
									
									<c:if test="${facetExist == false}">
											<c:url var="facetFilter" value="browseRecords.action">
												  <c:param name="query" value="${query}"/>
												  <c:param name="searchXML" value="${searchXML}"/>
												  <c:param name="addFacetName" value="${facet.name}"/>
												  <c:param name="addFacetValue" value="${fcount.name}"/>
												  <c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
												  <c:param name="selectedFacetValues" value="${selectedFacetValues}"/>
												  
											 </c:url>

											<a href="${facetFilter}">${fcount.name} (${fcount.count})</a>
											<c:if test="${facet.name == 'error'}">
												<c:url var="viewError" value="viewErrorDescription.action">
													  <c:param name="error" value="${fcount.name}"/>
													  <c:param name="query" value="${query}"/>
													  <c:param name="searchXML" value="${searchXML}"/>
													  <c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
													  <c:param name="selectedFacetValues" value="${selectedFacetValues}"/>
													  <c:param name="rowStart" value="${rowStart}"/>
													  <c:param name="startPageNumber" value="${startPageNumber}"/>
													  <c:param name="currentPageNumber" value="${currentPageNumber}"/>
												  </c:url>
										   		&nbsp;&nbsp;&nbsp;<a href="${viewError}">[info]</a>
										   	</c:if>											
											
											<br/>
									</c:if>
								</c:forEach> 
								</p>
								<br>

						       </div>
						        </c:forEach>
					   	</div>	
					
					</div>
					<!-- facet_search_results  end -->

					<div >	
						<!-- Display of filters -->
						<c:if test="${(query != '' || result.facetFilters != '[]') && (predecessorRecord == null && successorRecord == null)}">
							<p class="searched_for">You Searched for : <c:if test="${query != ''}">"${query}"</c:if><c:if test="${result.facetFilters != '[]' && query != ''}">, </c:if>
							<c:forEach var="filter" items="${result.facetFilters}"  varStatus="status"><c:if test="${status.count > 1}">, </c:if><c:if test="${filter.name == 'format_name'}">Schema</c:if><c:if test="${filter.name == 'set_name'}">Set</c:if><c:if test="${filter.name == 'provider_name'}">Repository</c:if><c:if test="${filter.name == 'service_name'}">Service</c:if><c:if test="${filter.name == 'harvest_start_time'}">Harvest</c:if><c:if test="${filter.name == 'error'}">Error</c:if>:${filter.value}</c:forEach>							
							</p>
						</c:if>
						
						<!-- Display of filters In case of predecessor  - begin-->
						<c:if test="${predecessorRecord != null}">

							   <c:url var="viewPredecessorRecord" value="browseRecords.action">
									  <c:param name="query" value=""/>
									  <c:param name="addFacetName" value="successor"/>
									  <c:param name="addFacetValue" value="${predecessorRecord.id}"/>
									  <c:param name="searchXML" value="false"/>
							   </c:url>
	
							   
							<p class="searched_for">You Searched for :<strong>All Successor of:</strong><br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<c:if test="${predecessorRecord.provider != null}">${predecessorRecord.provider.name}</c:if> 
								<c:if test="${predecessorRecord.service != null}">${predecessorRecord.service.name}</c:if> ${predecessorRecord.id}
									<br>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									Schema: ${predecessorRecord.format.name}
									
									<c:if test="${predecessorRecord.provider != null}">
										<br>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										Repository:${predecessorRecord.provider.name}
									</c:if>	
							       <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							       <c:if test="${predecessorRecord.numberOfPredecessors > 0 && predecessorRecord.numberOfSuccessors > 0}">
										<a href="${viewPredecessorRecord}">${predecessorRecord.numberOfPredecessors}
										<c:if test="${predecessorRecord.numberOfPredecessors == 1}">
											Predecessor
										</c:if>
										<c:if test="${predecessorRecord.numberOfPredecessors > 1}">
											Predecessors
										</c:if></a> 
										&nbsp;<img src="page-resources/img/white-book-both.jpg">&nbsp;
										${predecessorRecord.numberOfSuccessors} 
										<c:if test="${predecessorRecord.numberOfSuccessors == 1}">
											Successor
										</c:if>
										<c:if test="${predecessorRecord.numberOfSuccessors > 1}">
											Successors
										</c:if> 
										
								    </c:if>
								    <c:if test="${predecessorRecord.numberOfPredecessors > 0 && predecessorRecord.numberOfSuccessors < 1}">
										<a href="${viewPredecessorRecord}">${predecessorRecord.numberOfPredecessors} 
										<c:if test="${predecessorRecord.numberOfPredecessors == 1}">
											Predecessor
										</c:if>
										<c:if test="${predecessorRecord.numberOfPredecessors > 1}">
											Predecessors
										</c:if> 
										</a> 
									      &nbsp;<img src="page-resources/img/white-book-left.jpg">
								    </c:if>
									<c:if test="${predecessorRecord.numberOfSuccessors > 0 && predecessorRecord.numberOfPredecessors < 1}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<img src="page-resources/img/white-book-right.jpg">
										&nbsp;${predecessorRecord.numberOfSuccessors} 
										<c:if test="${predecessorRecord.numberOfSuccessors == 1}">
											Successor
										</c:if>
										<c:if test="${predecessorRecord.numberOfSuccessors > 1}">
											Successors
										</c:if> 
										
										
								    </c:if>                                    
							<br>								
							<c:forEach var="filter" items="${result.facetFilters}"  varStatus="status"><c:if test="${status.count > 2}">, </c:if><c:if test="${filter.name == 'format_name'}">Schema:${filter.value}</c:if><c:if test="${filter.name == 'set_name'}">Set:${filter.value}</c:if><c:if test="${filter.name == 'provider_name'}">Repository:${filter.value}</c:if><c:if test="${filter.name == 'service_name'}">Service:${filter.value}</c:if><c:if test="${filter.name == 'harvest_start_time'}">Harvest:${filter.value}</c:if><c:if test="${filter.name == 'error'}">Error:${filter.value}</c:if></c:forEach>							
							</p>
						</c:if>
						<!-- Display of filters In case of predecessor - end -->

						<!-- Display of filters In case of successor - begin -->
						<c:if test="${successorRecord != null}">
							   <c:url var="viewSuccessorRecord" value="browseRecords.action">
									  <c:param name="query" value=""/>
									  <c:param name="addFacetName" value="processed_from"/>
									  <c:param name="addFacetValue" value="${successorRecord.id}"/>
									  <c:param name="searchXML" value="false"/>
							   </c:url>						
							<p class="searched_for">You Searched for : <strong>All Precedessors of:</strong><br>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<c:if test="${successorRecord.provider != null}">${successorRecord.provider.name}</c:if> 
								<c:if test="${successorRecord.service != null}">
									${successorRecord.service.name}</c:if> ${successorRecord.id}
									<br>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									Schema: ${successorRecord.format.name}
									
									<c:if test="${successorRecord.provider != null}">
										<br>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										Repository:${successorRecord.provider.name}
									</c:if>	
							       <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							       <c:if test="${successorRecord.numberOfPredecessors > 0 && successorRecord.numberOfSuccessors > 0}">
										${successorRecord.numberOfPredecessors} 
										<c:if test="${successorRecord.numberOfPredecessors == 1}">
											Predecessor
										</c:if>
										<c:if test="${successorRecord.numberOfPredecessors > 1}">
											Predecessors
										</c:if>
										&nbsp;<img src="page-resources/img/white-book-both.jpg">&nbsp;
										<a href="${viewSuccessorRecord}">${successorRecord.numberOfSuccessors} 
										<c:if test="${successorRecord.numberOfSuccessors == 1}">
											Successor
										</c:if>
										<c:if test="${successorRecord.numberOfSuccessors > 1}">
											Successors
										</c:if> 
										</a>
								    </c:if>
								    <c:if test="${successorRecord.numberOfPredecessors > 0 && successorRecord.numberOfSuccessors < 1}">
										${successorRecord.numberOfPredecessors} 
										<c:if test="${successorRecord.numberOfPredecessors == 1}">
											Predecessor
										</c:if>
										<c:if test="${successorRecord.numberOfPredecessors > 1}">
											Predecessors
										</c:if> 
										
									      &nbsp;<img src="page-resources/img/white-book-left.jpg">
								    </c:if>
									<c:if test="${successorRecord.numberOfSuccessors > 0 && successorRecord.numberOfPredecessors < 1}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<img src="page-resources/img/white-book-right.jpg">
										&nbsp;<a href="${viewSuccessorRecord}">${successorRecord.numberOfSuccessors} 
										<c:if test="${successorRecord.numberOfSuccessors == 1}">
											Successor
										</c:if>
										<c:if test="${successorRecord.numberOfSuccessors > 1}">
											Successors
										</c:if> 
										</a> 
										
								    </c:if>                                    
							<br>								
							<c:forEach var="filter" items="${result.facetFilters}"  varStatus="status"><c:if test="${status.count > 2}">, </c:if><c:if test="${filter.name == 'format_name'}">Schema:${filter.value}</c:if><c:if test="${filter.name == 'set_name'}">Set:${filter.value}</c:if><c:if test="${filter.name == 'provider_name'}">Repository:${filter.value}</c:if><c:if test="${filter.name == 'service_name'}">Service:${filter.value}</c:if><c:if test="${filter.name == 'harvest_start_time'}">Harvet:${filter.value}</c:if><c:if test="${filter.name == 'error'}">Error:${filter.value}</c:if></c:forEach>							
							</p>
						</c:if>	
						<!-- Display of filters In case of successor - end -->					
					</div>
					
					<!-- Display of Search text box - begin -->
					<div class="search_box_div">
						<form name="browseRecordsForm" method="post" action="browseRecords.action">

							<input type="text" id="search_text" name="query" value="${query}" size="40"/>
							<button class="xc_button" type="submit" name="save" >Search</button>
							<br><input type="checkbox" id="search_xml" name="searchXML" value="true" 
							<c:if test="${searchXML}">
								checked
							</c:if>/> Search Full XML content
						</form>	
					</div>
					<!-- Display of Search text box - end -->
					
				<div class="facet_line"/>
			
				<!-- Display of Search results -->
				<c:if test="${!initialLoad}">
					<div class="search_results_div">
						<c:if test="${result.totalNumberOfResults > 0}">
						
								<c:if test="${result.totalNumberOfResults % numberOfResultsToShow == 0}">
									<c:set var="totalNumOfPages" value="${result.totalNumberOfResults / numberOfResultsToShow}"/>
								</c:if>
								<c:if test="${result.totalNumberOfResults % numberOfResultsToShow != 0}">
									<c:set var="totalNumOfPages" value="${result.totalNumberOfResults / numberOfResultsToShow + 1}"/>
								</c:if>
							Page <strong>${currentPageNumber}</strong> of <strong> ${fn:substringBefore(totalNumOfPages,".")} </strong> &nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp; <strong>${result.totalNumberOfResults}</strong> results
									
							<div class="search_div_pager">
								<c:import url="browse_records_pager.jsp"/>
							</div>
						</c:if>	
						

						<c:forEach var="record" items="${result.records}" varStatus="rowCounter">
						<c:if test="${rowCounter.count % 2 != 0}">
							<div class="record_result_odd_div">
						</c:if>
						<c:if test="${rowCounter.count % 2 == 0}">
							<div class="record_result_even_div">
						</c:if>
							<div class="record_number">
								${rowStart + rowCounter.count}.
							</div>
						
							<div class="record_text">
								<c:url var="viewRecord" value="viewRecord.action">
									  <c:param name="recordId" value="${record.id}"/>
									  <c:param name="query" value="${query}"/>
									  <c:param name="searchXML" value="${searchXML}"/>
									  <c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
								  	  <c:param name="selectedFacetValues" value="${selectedFacetValues}"/>
									  <c:param name="rowStart" value="${rowStart}"/>
									  <c:param name="startPageNumber" value="${startPageNumber}"/>
									  <c:param name="currentPageNumber" value="${currentPageNumber}"/>
								  </c:url>
								<a href="${viewRecord}"><c:if test="${record.provider != null}">${record.provider.name}</c:if> 
								<c:if test="${record.service != null}">${record.service.name}</c:if>
								${record.id}</a>
								<br>
								Schema: ${record.format.name}
								<br>
								<c:if test="${record.provider != null}">
									Repository: ${record.provider.name}
									<br>
								</c:if>
								<c:if test="${record.service != null}">
									Service: ${record.service.name}
									<br>
								</c:if>
								<c:if test="${record.harvest != null}">
									Harvest: ${record.harvest.harvestScheduleName} 
									<br>
								</c:if>
								<div class="redError">
								<c:if test="${record.errors != '[]'}">
									Error:
									<c:forEach var="error" items="${record.errors}" varStatus="status"><c:if test="${status.count > 1}">, </c:if>${error}</c:forEach>
									<br>
								</c:if>
								</div>

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
						</div>

						</c:forEach>						   			



				    </div>
				</c:if>		    
						    

			
 		</div>
		<!--  end body -->		
            
        </div>
        <!-- end doc -->
    </body>
</html>

    
