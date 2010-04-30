/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.bo.processing;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.service.Service;

/**
 * Represents a job in queue
 *
 * @author Sharmila Ranganathan
 */
public class Job {
	
	/** The processing directive's id */
	private int id = -1;
	
	/** Service to run */
	public Service service;
	
	/** Harvest schedule to run */
	public HarvestSchedule harvestSchedule;
	
	/** Output set to write the service output to */
	public int outputSetId;
	
	/** Processing Directive to run */
	public ProcessingDirective processingDirective;
	
	/** Order in which job has to be executed */
	public int order;
	
	/** Type of job */
	public String jobType;
	
	/** Default constructor */
	public Job() {
	}
	
	/** Constructor */
	public Job(Service service, Integer outputSetId, String jobType) {
		if (outputSetId == null) {
			outputSetId = 0;
		}
		this.service = service;
		this.outputSetId = outputSetId;
		this.jobType = jobType;
	}
	
	/** Constructor */
	public Job(HarvestSchedule harvestSchedule, String jobType) {
		this.harvestSchedule = harvestSchedule;
		this.jobType = jobType;
	}
	
	/** Constructor */
	public Job(ProcessingDirective processingDirective, String jobType) {
		this.processingDirective = processingDirective;
		this.jobType = jobType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public HarvestSchedule getHarvestSchedule() {
		return harvestSchedule;
	}

	public void setHarvestSchedule(HarvestSchedule harvestSchedule) {
		this.harvestSchedule = harvestSchedule;
	}

	public int getOutputSetId() {
		return outputSetId;
	}

	public void setOutputSetId(int outputSetId) {
		this.outputSetId = outputSetId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public ProcessingDirective getProcessingDirective() {
		return processingDirective;
	}

	public void setProcessingDirective(ProcessingDirective processingDirective) {
		this.processingDirective = processingDirective;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ Id="+id);
		sb.append(" Service="+service);
		sb.append(" Processing directive="+processingDirective);
		sb.append(" harvestSchedule="+harvestSchedule);
		sb.append(" order="+order+"]");
		
		return sb.toString();
		
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
}
