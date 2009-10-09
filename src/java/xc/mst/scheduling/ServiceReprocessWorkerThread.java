package xc.mst.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.processing.Job;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.IndexException;
import xc.mst.manager.processingDirective.DefaultJobService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.JobService;
import xc.mst.manager.processingDirective.ServicesService;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

/**
 * A Thread which re process the records through service.
 * When a service is updated, the records need to be re processed.
 * 
 * @author Sharmila Ranganathan
 */
public class ServiceReprocessWorkerThread extends WorkerThread 
{
	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/** 
	 * Type of thread 
	 */
	public static final String type = Constants.THREAD_SERVICE_REPROCESS;

	/**
	 * The flag for indicating cancel service operation.
	 */
	private boolean isCanceled;

	/**
	 * The flag for indicating service operation pause.
	 */
	private boolean isPaused;
	
	/**
	 * The ID of the service whose records needs to be reprocessed
	 */
	private int serviceId;
	
	/**
	 * The service whose records needs to be reprocessed
	 */
	private Service service;
	
	/**
	 * Manager for getting, inserting and updating records
	 */
	private static RecordService recordService = new DefaultRecordService();
	
	/**
	 * Manager for getting, inserting and updating jobs
	 */
	private static JobService jobService = new DefaultJobService();
	
	@Override
	public void run() 
	{
		try
		{
			ServicesService serviceManager = new DefaultServicesService();
			service = serviceManager.getServiceById(serviceId);
			
			// Reprocess the records processed by the service
    		RecordList records = recordService.getProcessedByServiceId(service.getId());
    		for(Record record : records)
    		{
    			record.addInputForService(service);
    			recordService.update(record);
    		}

    		List<Service> servicesToRun = new ArrayList<Service>();

    		if(!servicesToRun.contains(service))
    			servicesToRun.add(service);
    		
    		// Mark the records output by the old service as deleted
    		records = recordService.getByServiceId(service.getId());
    		for(Record record : records)
    		{
    			record.setDeleted(true);
    			record.setUpdatedAt(new Date());

    			for(Service processingService : record.getProcessedByServices())
    			{
    				record.addInputForService(processingService);
    				if(!servicesToRun.contains(processingService))
    					servicesToRun.add(processingService);
    			}

    			recordService.update(record);
    		}

    		SolrIndexManager.getInstance().commitIndex();

    		for(Service runMe : servicesToRun)
    		{
    			try {
					Job job = new Job(runMe, 0, Constants.THREAD_SERVICE);
					job.setOrder(jobService.getMaxOrder() + 1); 
					jobService.insertJob(job);
				} catch (DatabaseConfigException dce) {
					log.error("DatabaseConfig exception occured when ading jobs to database", dce);
				}
    		}
    		
    		// Reset the input, output counts
    		service.setHarvestOutWarnings(0);
    		service.setHarvestOutErrors(0);
    		service.setHarvestOutRecordsAvailable(0);
    		service.setServicesWarnings(0);
    		service.setServicesErrors(0);
    		service.setInputRecordCount(0);
    		service.setOutputRecordCount(0);
    		
    		serviceManager.updateService(service);

		} catch (DataException de) {
			log.error("Exception occured while updating records.", de);
		} catch (IndexException ie) {
			log.error("Exception occured while commiting to Solr index.", ie);
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

	public Service getService() {
		return service;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
}
