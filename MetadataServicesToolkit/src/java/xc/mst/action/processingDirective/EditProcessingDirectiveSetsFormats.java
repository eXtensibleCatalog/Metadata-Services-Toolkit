
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.processingDirective;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.processingDirective.DefaultProcessingDirectiveService;
import xc.mst.manager.processingDirective.ProcessingDirectiveService;
import xc.mst.manager.repository.DefaultFormatService;
import xc.mst.manager.repository.DefaultSetService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.SetService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.UserService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This action method is the second and last step in editing a processing directive
 *
 * @author Tejaswi Haramurali
 */
public class EditProcessingDirectiveSetsFormats extends ActionSupport implements ServletRequestAware
{
	 /** Serial ID*/
	private static final long serialVersionUID = -4669427548096070810L;

	/** Temporary Processing Directive Object that is used to display details on the JSP */
    private ProcessingDirective temporaryProcessingDirective;

    /** creates service object for sets  */
    private SetService setService = new DefaultSetService();

    /** creates service object for formats  */
    private FormatService formatService = new DefaultFormatService();

    /** creates service object for Processing Directives  */
    private ProcessingDirectiveService PDService = new DefaultProcessingDirectiveService();

    /** User Service object */
    private UserService userService = new DefaultUserService();

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
       
            
            ProcessingDirective tempProcDir = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");
            String sourceType = (String)request.getSession().getAttribute("sourceType");
            if((tempProcDir==null)||(sourceType==null))
            {
                this.addFieldError("editProcessingDirectiveSetsFormatsError", "Error occurred while displaying Step-2 of edit processing rules. An email has been sent to the administrator.");
                userService.sendEmailErrorReport();
                errorType = "error";
                return INPUT;
            }
            List<Format> tempFormatList = new ArrayList<Format>();
            List<Set> tempSetList = null;
            if(sourceType.equalsIgnoreCase("provider")) //source is a provider
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
                tempSetList = tempProcDir.getSourceProvider().getHarvestedRecordSets();
            }
            else //source is a service
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
                tempSetList = tempProcDir.getSourceService().getOutputSets();
            }
            setFormatList(tempFormatList);
            setSetList(tempSetList);
            setTemporaryProcessingDirective(tempProcDir);
            return SUCCESS;
       
    }

     /**
     * Step 2 in editing new processing directive
      *
     * @return {@link #SUCCESS}
     */
    public String editProcessingDirectivesSetsFormats()
    {
    	try
    	{
    		temporaryProcessingDirective = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");

    		if(getMaintainSourceSets()!=null)
    			temporaryProcessingDirective.setMaintainSourceSets(true);
    		else
    			temporaryProcessingDirective.setMaintainSourceSets(false);
                
    		String sourceType = (String)request.getSession().getAttribute("sourceType");

    		if((temporaryProcessingDirective==null)||(sourceType==null))
    		{
    			this.addFieldError("editProcessingDirectiveSetsFormatsError", "Error occurred while editing processing rule. An email has been sent to the administrator.");
    			userService.sendEmailErrorReport();
    			errorType = "error";
    			return INPUT;
    		}

    		String[] SetIdList = getSetsSelected();
    		String[] FormatIdList = getFormatsSelected();
    		List<Format> tempFormatList = new ArrayList<Format>();
    		List<Set> tempSetList = new ArrayList<Set>();

    		if(FormatIdList!=null) //formats selected
    		{
    			for(int i=0;i<FormatIdList.length;i++)
    			{
    				Format format = formatService.getFormatById(Integer.parseInt(FormatIdList[i]));
    				tempFormatList.add(format);
    			}
    		}

    		if(SetIdList!=null) //sets selected
    		{
    			for(int i=0;i<SetIdList.length;i++)
    			{
    				if(Integer.parseInt(SetIdList[i])!=0)
    				{
    					Set set = setService.getSetById(Integer.parseInt(SetIdList[i]));
    					tempSetList.add(set);
    				}
    				else
    				{
    					if(sourceType.equalsIgnoreCase("provider"))
    						tempSetList = temporaryProcessingDirective.getSourceProvider().getHarvestedRecordSets();
    					else
    						tempSetList = temporaryProcessingDirective.getSourceService().getOutputSets();
    					
    					break;
    				}
    			}
    		}
	
    		temporaryProcessingDirective.setTriggeringFormats(tempFormatList);
    		temporaryProcessingDirective.setTriggeringSets(tempSetList);
    		request.getSession().setAttribute("temporaryProcessingDirective",temporaryProcessingDirective);

    		Set setExists = setService.getSetBySetSpec(outputSetSpec);
    		if((setExists==null)&&(outputSetSpec!=null)&&(!outputSetSpec.equalsIgnoreCase(""))) //output set doesnt already exist
    		{
    			Set tempSet = new Set();
    			tempSet.setDisplayName(outputSetName);
    			tempSet.setSetSpec(outputSetSpec);
    			setService.insertSet(tempSet);
    			temporaryProcessingDirective.setOutputSet(tempSet);
    		}
    		else
    			temporaryProcessingDirective.setOutputSet(setExists);
                        
    		PDService.updateProcessingDirective(temporaryProcessingDirective);

    		request.getSession().setAttribute("temporaryProcessingDirective",null);
    		return SUCCESS;
    	}
    	catch(DatabaseConfigException dce)
    	{
    		log.error(dce.getMessage(),dce);
    		this.addFieldError("editProcessingDirectives2Error", "Unable to connect to the database. Database configuation may be incorrect");
    		errorType = "error";
    		return ERROR;
    	}
    	catch(DataException de)
    	{
    		log.error(de.getMessage(),de);
    		this.addFieldError("editProcessingDirectives2Error", "Error occurred while editing processing rule. An email has been sent to the administrator.");
    		userService.sendEmailErrorReport();
    		errorType = "error";
    		return ERROR;
    	}
    }

    /**
     * Returns to the previous step in the edit Processing Directive process
     * 
     * @return status of the operation
     */
    public String editProcessingDirectiveGoBack()
    {
         try
        {
           
            ProcessingDirective tempProcDir = (ProcessingDirective)request.getSession().getAttribute("temporaryProcessingDirective");
            if((tempProcDir==null))
                {
                    this.addFieldError("editProcessingDirectiveSetsFormatsError", "Error occurred returning to step-1 of editing processing rule . An email has been sent to the administrator.");
                    userService.sendEmailErrorReport();
                    errorType = "error";
                    return INPUT;
                }
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
        catch(DatabaseConfigException dce)
        {
            log.error(dce.getMessage(),dce);
            this.addFieldError("editPDGoBack", "Unable to connect to database. Database configuration may be incorrect");
            errorType = "error";
            return INPUT;
        }
    }

    /**
     * Cancels the editing of the processing directive and returns to the list of processing directives
     *
     * @return status of the return operation
     */
    public String editProcessingDirectiveCancel()
    {
                 
            request.getSession().setAttribute("temporaryProcessingDirective",null);
            return SUCCESS;
       
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
