package xc.mst.scheduling;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.processing.DefaultProcessingDirectiveDAO;
import xc.mst.dao.processing.ProcessingDirectiveDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

/**
 * A Thread which checks a processing directive
 * 
 * @author Eric Osisek
 */
public class ProcessingDirectiveWorkerThread extends WorkerThread 
{
	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/** 
	 * Type of thread 
	 */
	public static final String type = Constants.THREAD_PROCESSING_DIRECTIVE;

	/**
	 * The flag for indicating cancel service operation.
	 */
	private boolean isCanceled;

	/**
	 * The flag for indicating service operation pause.
	 */
	private boolean isPaused;
	
	/**
	 * The ID of the processing directive to check
	 */
	private ProcessingDirective processingDirective = null;
	
	/**
	 * The DAO for getting processing directives
	 */
	private ProcessingDirectiveDAO pdDao = new DefaultProcessingDirectiveDAO();

	/**
	 * Manager for getting, inserting and updating records
	 */
	private static RecordService recordService = new DefaultRecordService();
	
	/**
	 * Sets the processing directive to check
	 *
	 * @param newPd The processing directive to check
	 */
	public void setProcessingDirective(ProcessingDirective newPd)
	{
		processingDirective = newPd;
	} // end method setProcessingDirective(ProcessingDirective)
	
	@Override
	public void run() 
	{
		try
		{
			RecordList recordsToCheck = new RecordList(null);
			
			if(processingDirective.getSourceProvider() != null)
				recordsToCheck = recordService.getByProviderId(processingDirective.getSourceProvider().getId());
			else if(processingDirective.getSourceService() != null)
				recordsToCheck = recordService.getByServiceId(processingDirective.getSourceService().getId());
			
			for(Record checkMe : recordsToCheck)
				checkProcessingDirective(checkMe);
			
			SolrIndexManager.getInstance().waitForJobCompletion(5000);
			SolrIndexManager.getInstance().commitIndex();
			
			try
			{
				ServiceWorkerThread serviceThread = new ServiceWorkerThread();
				serviceThread.setServiceId(processingDirective.getService().getId());
				
				if(processingDirective.getOutputSet() != null)
					serviceThread.setOutputSetId(processingDirective.getOutputSet().getId());
				
				Scheduler.scheduleThread(serviceThread);
			} // end try(start the service)
			catch(Exception e)
			{
				log.error("An error occurred while scheduling the service with ID " + processingDirective.getService().getId() + ".", e);
			} // end catch(Exception)
		}
		catch (IndexException e) 
		{
			log.error("Error getting records to check.", e);
		}
	}

	@Override
	public void cancel() 
	{
		isCanceled = true;
	}

	@Override
	public void pause() 
	{
		isPaused = true;
	}

	@Override
	public void proceed() 
	{
		isPaused = false;
	}

	@Override
	public String getJobName() 
	{
		return "Processing Directive";
	}

	@Override
	public String getJobStatus() 
	{
		if(isCanceled)
			return Constants.STATUS_SERVICE_CANCELED;
		else if(isPaused)
			return Constants.STATUS_SERVICE_PAUSED;
		else
			return Constants.STATUS_SERVICE_RUNNING;
	}

	@Override
	public String getType() 
	{
		return type;
	}
	
	/**
	 * Runs the processing directives for this service against the record.  For all matching
	 * processing directives, adds the appropriate recordInput objects to the Lucene index.
	 * Also adds the service ID for all matched processing directives to the list of services
	 * to run when this service finishes.
	 *
	 * @param record The record to match against the processing directives
	 */
	private void checkProcessingDirective(Record record)
	{
		boolean matchedFormat = false;
		boolean matchedSet = false;

		// Check if the record matches any of the metadata formats for the current processing directive
		if(processingDirective.getTriggeringFormats().contains(record.getFormat())) {
			matchedFormat = true;
		}

		if(processingDirective.getTriggeringSets() != null && processingDirective.getTriggeringSets().size() > 0)  {
			for(Set set : record.getSets())
			{
				if(processingDirective.getTriggeringSets().contains(set))
				{
					matchedSet = true;
					break;
				} // end if(the set matched the processing directive)
			} // end loop over the record's sets
		} else {
			// If no triggering sets then process all records
			matchedSet = true;
		}

		if(matchedFormat && matchedSet)
		{
			record.addInputForService(processingDirective.getService());
			record.removeProcessedByService(processingDirective.getService());
			
			try 
			{
				recordService.update(record);
			} 
			catch (DataException e) 
			{
				log.error("Error updating a record in the database.", e);
			} 
			catch (IndexException e) 
			{
				log.error("Error updating a record in the database.", e);
			}
		}
	} // end method checkProcessingDirectives(Record)

	public ProcessingDirective getProcessingDirective() {
		return processingDirective;
	}

	@Override
	public int getProcessedRecordCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalRecordCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
