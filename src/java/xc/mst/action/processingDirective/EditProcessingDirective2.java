
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.constants.Constants;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.repository.DefaultFormatService;
import xc.mst.manager.repository.DefaultSetService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.SetService;

/**
 * This action method is the second and last step in editing a processing directive
 *
 * @author Tejaswi Haramurali
 */
public class EditProcessingDirective2 extends ActionSupport implements ServletRequestAware
{
    /** Temporary Processing Directive Object that is used to display details on the JSP */
    ProcessingDirective temporaryProcessingDirective;

    /** creates service object for sets  */
    private SetService setService = new DefaultSetService();

    /** creates service object for formats  */
    private FormatService formatService = new DefaultFormatService();

    /** creates service object for Processing Directives  */
    private ProcessingDirectiveService PDService = new DefaultProcessingDirectiveService();

    /** The complete list of sets in the system */
    private List<Set> setList;

    /** The complete list of formats in the system */
    private List<Format> formatList;


    /** The ID of the processing directive to be edited */
    private int processingDirectiveId;

    /** The list of sets that have been newly selected by the user */
    private String[] setsSelected;

    /** The list of formats that have been newly selected by the user */
    private String[] formatsSelected;

     /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** Attribute that determies whether sets are to be maintained*/
    private String[] maintainSourceSets;

    /** The output set name for a processing directive*/
    private String outputSetName;

    /** The specifications of the output set*/
    private String outputSetSpec;
    
	/** Error type */
	private String errorType; 

    /** Request */
    private HttpServletRequest request;

    /**
	 * Set the servlet request.
	 *
	 * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

    /**
     * Sets the temporary processing directive
     *
     * @param temporaryProcessingDirective
     */
    public void setTemporaryProcessingDirective(ProcessingDirective temporaryProcessingDirective)
    {
        this.temporaryProcessingDirective = temporaryProcessingDirective;
    }

    /**
     * Returns temporary processing directive
     *
     * @return processing directive object
     */
    public ProcessingDirective getTemporaryProcessingDirective()
    {
        return this.temporaryProcessingDirective;
    }

    /**
     * Sets the list of sets that the user has selected
     *
     * @param setsSelected list of sets
     */
    public void setSetsSelected(String[] setsSelected)
    {
        this.setsSelected = setsSelected;
    }

    /**
     * Returns the list of sets selected by the user
     *
     * @return list of sets
     */
    public String[] getSetsSelected()
    {
        return setsSelected;
    }

    /**
     * Sets the list of formats selected by the user
     *
     * @param formatsSelected list of formats
     */
    public void setFormatsSelected(String[] formatsSelected)
    {
        this.formatsSelected = formatsSelected;
    }

    /**
     * Returns the list of formats selected by the user
     *
     * @return list of formats
     */
    public String[] getFormatsSelected()
    {
        return formatsSelected;
    }


    /**
     * Sets the ID of the processing directive to be edited
     *
     * @param processingDirectiveId ID of the processing directive
     */
    public void setProcessingDirectiveId(String processingDirectiveId)
    {

        this.processingDirectiveId = Integer.parseInt(processingDirectiveId);
    }

    /**
     * Returns the ID of the processing directive to be edited
     *
     * @return ID of the processing directive
     */
    public int getProcessingDirectiveId()
    {

        return processingDirectiveId;
    }

     /**
     * Sets the list of all sets that are relevant to that Provider/Service
      *
     * @param setList list of sets
     */
    public void setSetList(List<Set> setList)
    {
        this.setList = setList;
    }

    /**
     * Gets the list of all sets that are relevant to that Provider/Service
     *
     * @return list of sets
     */
    public List<Set> getSetList()
    {
        return setList;
    }

    /**
     * Sets the list of all formats that are relevant to that Provider/Service
     *
     * @param formatList list of formats
     */
    public void setFormatList(List<Format> formatList)
    {
        this.formatList = formatList;
    }

    /**
     * Gets the list of all formats that are relevant to that Provider/Service
     *
     * @return list of formats
     */
    public List<Format> getFormatList()
    {
        return formatList;
    }


     /**
     * Sets the boolean value representing the user's choice to maintain sets
      *
     * @param maintainSets
     */
    public void setMaintainSourceSets(String[] maintainSourceSets)
    {

        this.maintainSourceSets = maintainSourceSets;
    }

    /**
     * Returns the boolean value representing the user's choice to maintain sets
     *
     * @return
     */
    public String[] getMaintainSourceSets()
    {
        return maintainSourceSets;
    }

    /**
     * Sets the name of the output set
     *
     * @param outputSetName output set
     */
    public void setOutputSetName(String outputSetName)
    {
        this.outputSetName = outputSetName;
    }

    /**
     * Returns value of the output set
     *
     * @return output set
     */
    public String getOutputSetName()
    {
        return outputSetName;
    }

    /**
     * Sets the specifications of the output set
     *
     * @param outputSetSpec output set
     */
    public void setOutputSetSpec(String outputSetSpec)
    {
        this.outputSetSpec = outputSetSpec;
    }

    /**
     * Returns specifications of the output set
     *
     * @return output set
     */
    public String getOutputSetSpec()
    {
        return outputSetSpec;
    }

     /**
     * Overrides default implementation to view the edit processing directives (step 2) page.
      *
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            
            ProcessingDirective tempProcDir = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");
            String sourceType = (String)request.getSession().getAttribute("sourceType");
            List<Format> tempFormatList = new ArrayList<Format>();
            List<Set> tempSetList = null;
            if(sourceType.equalsIgnoreCase("provider"))
            {
                List<Format> tempList = tempProcDir.getSourceProvider().getFormats();
                List<Format> compareList = tempProcDir.getService().getInputFormats();
                Iterator<Format> tempIter = tempList.iterator();
                while(tempIter.hasNext())
                {
                    boolean flags = false;
                    Iterator<Format> compareIter = compareList.iterator();
                    Format tempFormat = (Format)tempIter.next();
        
                    while(compareIter.hasNext())
                    {
                        Format compareFormat = (Format)compareIter.next();
                       
                        if(compareFormat.getId()==tempFormat.getId())
                        {
                            flags = true;
                            break;
                        }
                    }
                    if(flags==true)
                    {
                        tempFormatList.add(tempFormat);
                    }
                }
                tempSetList = tempProcDir.getSourceProvider().getSets();
            }
            else
            {
                List<Format> tempList = tempProcDir.getSourceService().getOutputFormats();
                List<Format> compareList = tempProcDir.getService().getInputFormats();
                Iterator<Format> tempIter = tempList.iterator();
                while(tempIter.hasNext())
                {
                    boolean flags = false;
                    Iterator<Format> compareIter = compareList.iterator();
                    Format tempFormat = (Format)tempIter.next();
                   
                    while(compareIter.hasNext())
                    {
                        Format compareFormat = (Format)compareIter.next();
                        
                        if(compareFormat.getId()==tempFormat.getId())
                        {
                            flags = true;
                            break;
                        }
                    }
                    if(flags==true)
                    {
                        tempFormatList.add(tempFormat);
                    }
                }
                tempSetList = setService.getAllSets();
            }
            setFormatList(tempFormatList);
            setSetList(tempSetList);
            setTemporaryProcessingDirective(tempProcDir);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("editProcessingDirective2Error", "Error : Problem in editing the directive");
            errorType = "error";
            return INPUT;
        }
    }

     /**
     * Step 2 in editing new processing directive
      *
     * @return {@link #SUCCESS}
     */
    public String editProcessingDirectives2()
    {

         try
           {
                
                temporaryProcessingDirective = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");

                if(getMaintainSourceSets()!=null)
                {
                     temporaryProcessingDirective.setMaintainSourceSets(true);
                }
                else
                {
                     temporaryProcessingDirective.setMaintainSourceSets(false);
                }
               
               
                String sourceType = (String)request.getSession().getAttribute("sourceType");

                String[] SetIdList = getSetsSelected();
                String[] FormatIdList = getFormatsSelected();
                List<Format> tempFormatList = new ArrayList();
                List<Set> tempSetList = new ArrayList();

                if(FormatIdList!=null)
                {
                    for(int i=0;i<FormatIdList.length;i++)
                    {
                       Format format = formatService.getFormatById(Integer.parseInt(FormatIdList[i]));
                       tempFormatList.add(format);
                    }
                }

                if(SetIdList!=null)
                {
                    for(int i=0;i<SetIdList.length;i++)
                    {
                       Set set = setService.getSetById(Integer.parseInt(SetIdList[i]));
                       tempSetList.add(set);
                    }
                }

                if(sourceType.equalsIgnoreCase("provider"))
                {
                    // Validation to ensure that processing directive with same details is not inserted again
                    int flag = 1;
                    List<ProcessingDirective> procDirList = PDService.getBySourceProviderId(temporaryProcessingDirective.getSourceProvider().getId());
                    if(!procDirList.isEmpty())
                    {
                        Iterator<ProcessingDirective> iter = procDirList.iterator();
                        while(iter.hasNext())
                        {
                            ProcessingDirective tempProcDir = (ProcessingDirective)iter.next();

                            if(tempProcDir.getService().getId()== temporaryProcessingDirective.getService().getId())
                            {
                                if(temporaryProcessingDirective.getId()!=tempProcDir.getId())
                                {
                                    flag = 0;
                                    break;
                                }
                            }
                        }
                    }
                    temporaryProcessingDirective.setTriggeringFormats(tempFormatList);
                    temporaryProcessingDirective.setTriggeringSets(tempSetList);
                    request.getSession().setAttribute("temporaryProcessingDirective",temporaryProcessingDirective);
                    if(flag==1)
                    {
                       Set setExists = setService.getSetBySetSpec(outputSetSpec);
                       if(setExists==null)
                        {
                            if((outputSetSpec!=null)&&(!outputSetSpec.equalsIgnoreCase("")))
                            {
                                Set tempSet = new Set();
                                tempSet.setDisplayName(outputSetName);
                                tempSet.setSetSpec(outputSetSpec);
                                setService.insertSet(tempSet);
                                temporaryProcessingDirective.setOutputSet(tempSet);
                            }
                            else
                            {
                                temporaryProcessingDirective.setOutputSet(setExists); //sets a null value as output set
                            }
                        }
                        else
                        {
                            List<ProcessingDirective> PDList = PDService.getAllProcessingDirectives();
                            boolean outputSetFlag = false;
                            Iterator<ProcessingDirective> procDirIter = PDList.iterator();
                            while(procDirIter.hasNext())
                            {
                                ProcessingDirective processingDirective = (ProcessingDirective)procDirIter.next();
                                if(processingDirective.getOutputSet()!=null)
                                {
                                    if(processingDirective.getOutputSet().getSetSpec().equals(setExists.getSetSpec()))
                                    {
                                        if(processingDirective.getId()==temporaryProcessingDirective.getId())
                                        {
                                            outputSetFlag = true;
                                            break;
                                        }
                                        else
                                        {
                                            outputSetFlag = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if(outputSetFlag==true)
                            {
                                temporaryProcessingDirective.setOutputSet(setExists);
                            }
                            else
                            {
                                this.addFieldError("listProcessingDirectivesError", "Updation unsuccessful : Output Source Set with set specification "+setExists.getSetSpec()+" already exists");
                                errorType = "error";
                                setFormatList(temporaryProcessingDirective.getSourceProvider().getFormats());
                                setSetList(temporaryProcessingDirective.getSourceProvider().getSets());
                                setTemporaryProcessingDirective(temporaryProcessingDirective);
                                return INPUT;
                            }
                        }
                       PDService.updateProcessingDirective(temporaryProcessingDirective);
                    }
                    else
                    {
                         this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful : Cannot insert Processing Directive with same Source:'"+temporaryProcessingDirective.getSourceProvider().getName()+"' and Service:'"+temporaryProcessingDirective.getService()+"' combination");
                         errorType = "error";
                         setFormatList(temporaryProcessingDirective.getSourceProvider().getFormats());
                         setSetList(temporaryProcessingDirective.getSourceProvider().getSets());
                         Set tempSet = new Set();
                         tempSet.setDisplayName(outputSetName);
                         tempSet.setSetSpec(outputSetSpec);
                         temporaryProcessingDirective.setOutputSet(tempSet);
                         setTemporaryProcessingDirective(temporaryProcessingDirective);
                         return INPUT;
                    }
                    // end of validation and insertion(if applicable)

                }
                else
                {
                    // Validation to ensure that processing directive with same details is not inserted again
                    int flag = 1;
                    List<ProcessingDirective> procDirList = PDService.getBySourceServiceId(temporaryProcessingDirective.getSourceService().getId());
                    if(!procDirList.isEmpty())
                    {
                        Iterator<ProcessingDirective> iter = procDirList.iterator();
                        while(iter.hasNext())
                        {
                            ProcessingDirective tempProcDir = (ProcessingDirective)iter.next();
                            if(tempProcDir.getService().getId()== temporaryProcessingDirective.getService().getId())
                            {
                               if(temporaryProcessingDirective.getId()!=tempProcDir.getId())
                                {
                                    flag = 0;
                                    break;
                                }
                            }
                        }
                    }
                    temporaryProcessingDirective.setTriggeringFormats(tempFormatList);
                    temporaryProcessingDirective.setTriggeringSets(tempSetList);
                    request.getSession().setAttribute("temporaryProcessingDirective",temporaryProcessingDirective);
                    if(flag==1)
                    {
                       Set setExists = setService.getSetBySetSpec(outputSetSpec);
                       if(setExists==null)
                        {
                           if((outputSetSpec!=null)&&(!outputSetSpec.equalsIgnoreCase("")))
                           {
                                Set tempSet = new Set();
                                tempSet.setDisplayName(outputSetName);
                                tempSet.setSetSpec(outputSetSpec);
                                setService.insertSet(tempSet);
                                temporaryProcessingDirective.setOutputSet(tempSet);
                           }
                           else
                           {
                               temporaryProcessingDirective.setOutputSet(setExists);
                           }
                        }
                        else
                        {
                            List<ProcessingDirective> PDList = PDService.getAllProcessingDirectives();
                            boolean outputSetFlag = false;
                            Iterator<ProcessingDirective> procDirIter = PDList.iterator();
                            while(procDirIter.hasNext())
                            {
                                ProcessingDirective processingDirective = (ProcessingDirective)procDirIter.next();
                                if(processingDirective.getOutputSet()!=null)
                                {
                                    if(processingDirective.getOutputSet().getSetSpec().equals(setExists.getSetSpec()))
                                    {
                                        if(processingDirective.getId()==temporaryProcessingDirective.getId())
                                        {
                                            outputSetFlag = true;
                                            break;
                                        }
                                        else
                                        {
                                            outputSetFlag = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if(outputSetFlag==true)
                            {
                                temporaryProcessingDirective.setOutputSet(setExists);
                            }
                            else
                            {
                                this.addFieldError("listProcessingDirectivesError", "Updation unsuccessful : Output Source Set with set specification "+setExists.getSetSpec()+" already exists");
                                errorType = "error";
                                setFormatList(temporaryProcessingDirective.getSourceService().getOutputFormats());
                                setSetList(setService.getAllSets());
                                setTemporaryProcessingDirective(temporaryProcessingDirective);
                                return INPUT;
                            }
                        }
                       PDService.updateProcessingDirective(temporaryProcessingDirective);
                    }
                    else
                    {
                         this.addFieldError("listProcessingDirectivesError", "Insertion unsuccessful : Cannot insert Processing Directive with same Source:'"+temporaryProcessingDirective.getSourceService().getName()+"' and Service:'"+temporaryProcessingDirective.getService().getName()+"' combination");
                         errorType = "error";
                         setFormatList(temporaryProcessingDirective.getSourceService().getOutputFormats());
                         setSetList(setService.getAllSets());
                         Set tempSet = new Set();
                         tempSet.setDisplayName(outputSetName);
                         tempSet.setSetSpec(outputSetSpec);
                         temporaryProcessingDirective.setOutputSet(tempSet);
                         setTemporaryProcessingDirective(temporaryProcessingDirective);
                         return INPUT;
                    }
                    // end of validation

                }

                request.getSession().setAttribute("temporaryProcessingDirective",null);
                return SUCCESS;
           }
           catch(Exception e)
           {
                log.debug(e);
                e.printStackTrace();
                this.addFieldError("editProcessingDirectives2Error", "Error : The edit was unsuccessful");
                errorType = "error";
                return ERROR;
           }

    }

    /**
     * Returns to the previous step in the edit Processing Directive process
     * 
     * @return status of the operation
     */
    public String editPDGoBack()
    {
         try
        {
           
            ProcessingDirective tempProcDir = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");

            if(maintainSourceSets==null)
                {

                    tempProcDir.setMaintainSourceSets(false);
                }
                else
                {

                    tempProcDir.setMaintainSourceSets(true);
                }

                String[] SetIdList = setsSelected;
                String[] FormatIdList = formatsSelected;
                List<Format> tempFormatList = new ArrayList<Format>();
                List<Set> tempSetList = new ArrayList<Set>();

                if(FormatIdList!=null)
                {
                    for(int i=0;i<FormatIdList.length;i++)
                    {
                       Format format = formatService.getFormatById(Integer.parseInt(FormatIdList[i]));
                       tempFormatList.add(format);
                    }
                }

                
                tempProcDir.setTriggeringFormats(tempFormatList);
                if(SetIdList!=null)
                {
                    for(int i=0;i<SetIdList.length;i++)
                    {
                       Set set = setService.getSetById(Integer.parseInt(SetIdList[i]));
                       tempSetList.add(set);
                    }
                }

               
                tempProcDir.setTriggeringSets(tempSetList);
                Set setExists = setService.getSetBySetSpec(outputSetSpec);
                if(setExists==null)
                {
                    if((outputSetSpec!=null)&&(!outputSetSpec.equalsIgnoreCase("")))
                    {
                        Set tempSet = new Set();
                        tempSet.setDisplayName(outputSetName);
                        tempSet.setSetSpec(outputSetSpec);
                        //setService.insertSet(tempSet);
                        tempProcDir.setOutputSet(tempSet);
                    }
                    else
                    {
                        tempProcDir.setOutputSet(setExists);
                    }
                }
                else
                {
                    tempProcDir.setOutputSet(setExists);
                }
                request.getSession().setAttribute("temporaryProcessingDirective",tempProcDir);
                return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.debug(e);
            return INPUT;
        }
    }

    /**
     * Cancels the editing of the processing directive and returns to the list of processing directives
     *
     * @return status of the return operation
     */
    public String editPDCancel()
    {
        try
        {            
            request.getSession().setAttribute("temporaryProcessingDirective",null);
            return SUCCESS;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return INPUT;
        }
    }

	 /**
     * Returns error type
      *
     * @return error type
     */
	public String getErrorType() {
		return errorType;
	}

    /**
     * Sets error type
     * 
     * @param errorType error type
     */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
