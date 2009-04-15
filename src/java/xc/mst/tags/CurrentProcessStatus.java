

package xc.mst.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.apache.log4j.Logger;
import xc.mst.constants.Constants;
import xc.mst.scheduling.Scheduler;

/**
 * Used to display the current process that is running in the MST
 *
 * @author Tejaswi Haramurali
 */
public class CurrentProcessStatus extends SimpleTagSupport
{
    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    @Override
    public void doTag() throws JspException
    {
        try
        {
            String currentProcess = null;
            JspWriter out = getJspContext().getOut();

            if(Scheduler.getRunningJob()!=null)
            {
                currentProcess = Scheduler.getRunningJob().getName();
            }
            if(currentProcess!=null)
            {

                    
                    out.print("<xxx id='currentProcess'>"+currentProcess+"</xxx>");
                    Object tempObject =  getJspContext().findAttribute("serviceBarDisplay");
                    if(tempObject!=null)
                    {
                        String serviceBarDisplay = tempObject.toString();
                        System.out.println("ServiceDisplay is "+serviceBarDisplay);
                        if(serviceBarDisplay.equalsIgnoreCase("pause"))
                        {
                            out.print("<button style=\"display:none;\" id='resumeButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"resume\");' name='Resume'>Resume</button> &nbsp;&nbsp;&nbsp;");
                            out.print("<button id='pauseButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"pause\");' name='Pause'>Pause</button> &nbsp;&nbsp;&nbsp;");
                            out.print("<button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"abort\");' name='Abort'>Abort</button>");
                        }
                        else
                        {
                            out.print("<button id='resumeButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"resume\");' name='Resume'>Resume</button> &nbsp;&nbsp;&nbsp;");
                            out.print("<button style=\"display:none;\" id='pauseButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"pause\");' name='Pause'>Pause</button> &nbsp;&nbsp;&nbsp;");
                            out.print("<button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"abort\");' name='Abort'>Abort</button>");
                        }
                    }
                    else
                    {
                         out.print("<button style=\"display:none;\" id='resumeButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"resume\");' name='Resume'>Resume</button> &nbsp;&nbsp;&nbsp;");
                         out.print("<button id='pauseButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"pause\");' name='Pause'>Pause</button> &nbsp;&nbsp;&nbsp;");
                         out.print("<button id='abortButton' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"abort\");' name='Abort'>Abort</button>");
                    }
            }
            else //there is no current process being run by the MST
            {
                out.print("<button style=\"display:none;\" id='resumeButton' style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"resume\");' name='Resume'>Resume</button> &nbsp;&nbsp;&nbsp;");
                out.print("<button id='pauseButton' disabled style='vertical-align:bottom;' class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"pause\");' name='Pause'>Pause</button> &nbsp;&nbsp;&nbsp;");
                out.print("<button id='abortButton' disabled class='xc_button' type='button' onclick='javascript:YAHOO.xc.mst.serviceStatusBar.alterStatus(\"abort\");' name='Abort'>Abort</button>");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
        }
    }
}
