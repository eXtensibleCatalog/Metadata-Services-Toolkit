<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mst" uri="mst-tags"%>
<table width="100%" height="40px">
<tr>


	<mst:pager currentPageNumber="${currentPageNumber}" 
	     startPageNumber="${startPageNumber}"  
		 totalHits="${totalHits}"
		 numberOfPagesToShow="${numberOfPagesToShow}"  
		 numberOfResultsToShow="${numberOfResultsToShow}" >	
<td width="7%" class="rightBorderTd">
		<mst:firstPage>
	          <c:url var="browseUrl" value="browseRecords.action">
		           <c:param name="rowStart" value="0"/>
			       <c:param name="startPageNumber" value="1"/>
			       <c:param name="currentPageNumber" value="1"/>
				   <c:param name="query" value="${query}"/>
				   <c:param name="searchXML" value="${searchXML}"/>
				   <c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
				   <c:param name="selectedFacetValues" value="${selectedFacetValues}"/>	
		      </c:url>										

		      <a href="${browseUrl}">First</a> 

		</mst:firstPage>
</td>				
<td >		<mst:previousPage>
		    <c:url var="browseUrl" value="browseRecords.action">
	                <c:param name="rowStart" value="${rowStart}"/>
					<c:param name="startPageNumber" value="${prevousPageStartPageNumber}"/>
					<c:param name="currentPageNumber" value="${currentPageNumber - 1}"/>
					<c:param name="query" value="${query}"/>
					<c:param name="searchXML" value="${searchXML}"/>
					<c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
					<c:param name="selectedFacetValues" value="${selectedFacetValues}"/>	
			</c:url>
			
			<&nbsp;<a href="${browseUrl}">Previous</a> &nbsp;&nbsp;
	

			<mst:morePrevious>
		          <c:url var="browseUrl" value="browseRecords.action">
						<c:param name="rowStart" value="${rowStart}"/>
						<c:param name="startPageNumber" value="${startPageNumberForPreviousSet}"/>
						<c:param name="currentPageNumber" value="${startPageNumberForPreviousSet}"/>
						<c:param name="query" value="${query}"/>
						<c:param name="searchXML" value="${searchXML}"/>
						<c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
						<c:param name="selectedFacetValues" value="${selectedFacetValues}"/>	
			      </c:url>										
			 
			      &nbsp;<a href="${browseUrl}">....</a> &nbsp;&nbsp;
			      
			 </mst:morePrevious>
			 
		</mst:previousPage>
								
		<mst:forEachPage var="pageNumber">
		    <c:if test="${pageNumber != currentPageNumber}">
			     <c:url var="browseUrl" value="browseRecords.action">
				     <c:param name="rowStart" value="${rowStart}"/>
					 <c:param name="startPageNumber" value="${startPageNumber}"/>
					 <c:param name="currentPageNumber" value="${pageNumber}"/>
					 <c:param name="query" value="${query}"/>
					 <c:param name="searchXML" value="${searchXML}"/>
					 <c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
					 <c:param name="selectedFacetValues" value="${selectedFacetValues}"/>	
				 </c:url>		
																			
				 <a href="${browseUrl}">${pageNumber}</a>&nbsp;&nbsp;
		     </c:if>
										
			 <c:if test="${pageNumber == currentPageNumber}">

				${pageNumber}&nbsp;&nbsp;
				
			 </c:if>
		</mst:forEachPage>								
															
		<mst:nextPage>

	       <mst:moreNext>
		        <c:url var="browseUrl" value="browseRecords.action">
				    <c:param name="rowStart" value="${nextSetRowStart}"/>
					<c:param name="startPageNumber" value="${startPageNumberForNextSet}"/>
					<c:param name="currentPageNumber" value="${startPageNumberForNextSet}"/>	
					<c:param name="query" value="${query}"/>
					<c:param name="searchXML" value="${searchXML}"/>
					<c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
					<c:param name="selectedFacetValues" value="${selectedFacetValues}"/>	
			    </c:url>	
			    
				<a href="${browseUrl}">....</a>&nbsp;&nbsp;&nbsp;
				
	        </mst:moreNext>

		    <c:url var="browseUrl" value="browseRecords.action">
			    <c:param name="rowStart" value="${rowStart}"/>
				<c:param name="startPageNumber" value="${nextPageStartPageNumber}"/>
				<c:param name="currentPageNumber" value="${currentPageNumber + 1}"/>	
				<c:param name="query" value="${query}"/>
				<c:param name="searchXML" value="${searchXML}"/>
				<c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
				<c:param name="selectedFacetValues" value="${selectedFacetValues}"/>	
			</c:url>	

		     <a href="${browseUrl}">Next</a>&nbsp;> &nbsp;&nbsp;
		    
	    </mst:nextPage>
</td>	    
<td width="7%" class="leftBorderTd">
	       <mst:lastPage>
		        <c:url var="browseUrl" value="browseRecords.action">
				    <c:param name="rowStart" value="${rowstartForLastPage}"/>
					<c:param name="startPageNumber" value="${startPageNumber}"/>
					<c:param name="currentPageNumber" value="${currentPageNumber}"/>	
					<c:param name="query" value="${query}"/>
					<c:param name="searchXML" value="${searchXML}"/>
					<c:param name="selectedFacetNames" value="${selectedFacetNames}"/>
					<c:param name="selectedFacetValues" value="${selectedFacetValues}"/>	
			    </c:url>
			    
				<a href="${browseUrl}">Last</a> 

			</mst:lastPage>	
</td>				    
	</mst:pager>	

				         
</tr>
</table>