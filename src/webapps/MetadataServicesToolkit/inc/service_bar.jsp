<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="varPause" value="pause"/>
<c:set var="varResume" value="resume"/>
<c:set var="varAbort" value="abort"/>

<li style="float:left;margin-top:8px;">    
<c:if test="${currentProcess!=null}">
	<c:choose>
            <c:when test="${displayType!=null}">
                    <c:if test="${displayType eq varPause}">
                    </c:if>
                    <c:if test="${displayType eq varResume}">
                          <img src="page-resources/img/loading_graphic.gif">
                    </c:if>
                    <c:if test="${displayType eq varAbort}">
                          <img src="page-resources/img/loading_graphic.gif">
                    </c:if>

            </c:when>
            <c:otherwise>
               <img src="page-resources/img/loading_graphic.gif">
            </c:otherwise>
       </c:choose>
</c:if>

Process Description :
<c:if test="${currentProcess!=null}">
        <c:set var="nameLength" value="${fn:length(currentProcess)}"/>
        <c:choose>
            <c:when test="${nameLength<25}">
                <xxx id="currentProcess">${currentProcess}</xxx>
            </c:when>
            <c:otherwise>
                <xxx id="currentProcess" title="${currentProcess}">${fn:substring(currentProcess,0,20)}...</xxx>
            </c:otherwise>
        </c:choose>
</c:if>
<c:if test="${currentProcess == null}">
    <xxx id="currentProcess">No Process is running</xxx>
</c:if>
</li>



<li style="float:right;"> 
<c:choose>
    <c:when test="${currentProcess!=null}">
        <c:choose>
            <c:when test="${displayType!=null}">

                    <c:if test="${displayType eq varPause}">
                         <button style="display:none;" id='resumeButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("resume");' name='Resume'>Resume</button>
                         <button id='pauseButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("pause");' name='Pause'>Pause</button>
                         <button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("abort");' name='Abort'>Abort</button>

                    </c:if>
                    <c:if test="${displayType eq varResume}">
                          <button id='resumeButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("resume");' name='Resume'>Resume</button>
                          <button style="display:none;" id='pauseButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("pause");' name='Pause'>Pause</button>
                          <button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("abort");' name='Abort'>Abort</button>
                    </c:if>
                    <c:if test="${displayType eq varAbort}">
                          <button id='resumeButton' style='display:none;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("resume");' name='Resume'>Resume</button>
                          <button disabled id='pauseButton' class="xc_button_disabled" type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("pause");' name='Pause'>Pause</button>
                          <button disabled id='abortButton' class="xc_button_disabled" type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("abort");' name='Abort'>Abort</button>
                    </c:if>

            </c:when>
            <c:otherwise>
                <button style="display:none;" id='resumeButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("resume");' name='Resume'>Resume</button>
                <button id='pauseButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("pause");' name='Pause'>Pause</button>
                <button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("abort");' name='Abort'>Abort</button>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
            <button style="display:none;" id='resumeButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("resume");' name='Resume'>Resume</button>
            <button id='pauseButton' disabled class="xc_button_disabled" type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("pause");' name='Pause'>Pause</button>
            <button id='abortButton' disabled class="xc_button_disabled" type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.yuiAlterStatus("abort");' name='Abort'>Abort</button>
    </c:otherwise>
</c:choose>

 </li>