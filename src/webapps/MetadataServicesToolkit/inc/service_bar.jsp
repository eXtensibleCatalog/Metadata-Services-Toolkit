<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

Process Description &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;
<c:if test="${currentProcess!=null}">
    <xxx id="currentProcess">${currentProcess}</xxx>
</c:if>

<c:set var="varPause" value="pause"/>

<c:choose>
    <c:when test="${currentProcess!=null}">
        <c:choose>
            <c:when test="${displayType!=null}">
                 <c:choose>
                    <c:when test="${displayType eq varPause}">
                         <button style="display:none;vertical-align:bottom;" id='resumeButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("resume");' name='Resume'>Resume</button> &nbsp;&nbsp;&nbsp;
                         <button id='pauseButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("pause");' name='Pause'>Pause</button> &nbsp;&nbsp;&nbsp;
                         <button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("abort");' name='Abort'>Abort</button>");

                    </c:when>
                    <c:otherwise>
                          <button id='resumeButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("resume");' name='Resume'>Resume</button> &nbsp;&nbsp;&nbsp;
                          <button style="display:none;vertical-align:bottom;" id='pauseButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("pause");' name='Pause'>Pause</button> &nbsp;&nbsp;&nbsp;
                          <button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("abort");' name='Abort'>Abort</button>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <button style="display:none;vertical-align:bottom;" id='resumeButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("resume");' name='Resume'>Resume</button> &nbsp;&nbsp;&nbsp;
                <button id='pauseButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("pause");' name='Pause'>Pause</button> &nbsp;&nbsp;&nbsp;
                <button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("abort");' name='Abort'>Abort</button>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
         <button style="display:none;vertical-align:bottom;" id='resumeButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("resume");' name='Resume'>Resume</button> &nbsp;&nbsp;&nbsp;
         <button id='pauseButton' disabled style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("pause");' name='Pause'>Pause</button> &nbsp;&nbsp;&nbsp;
         <button id='abortButton' disabled class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus("abort");' name='Abort'>Abort</button>
    </c:otherwise>
</c:choose>