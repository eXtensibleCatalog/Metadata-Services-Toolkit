/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.bo.record;

import java.util.List;

import org.jdom.Element;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;

/**
 * @author Benjamin Anderson
 * 
 * The process method of GenericMetadataService returns a list of OutputRecords.
 * To create an OutputRecord, use
 * this helper method getRecordService().createRecord().  When you do this, an id will
 * be injected into your newly created OutputRecord.
 * 
 * @see RecordIfc
 * @see InputRecord
 * @see xc.mst.services.impl.GenericMetadataService#process(InputRecord)
 *
 */
public interface OutputRecord extends RecordIfc {
	
	/**
	 * @return If this is not the first time this service has processed
	 * this record and it previously produced OutputRecords, then they
	 * will be attached as successors.  Those successor OutputRecords will point
	 * back to their InputRecords (which could be more than one).  For a one-to-one
	 * service, these predecessors are redundant, but for services that have a one-many,
	 * many-one, or many-many with the InputRecord-OutputRecord, this info is necessary.
	 * 
	 * @see xc.mst.services.impl.GenericMetadataService#process(InputRecord)
	 */
	public List<InputRecord> getPredecessors();
	
	/**
	 * @see RecordIfc#setMode(java.lang.String)
	 */
	public void setOaiXmlEl(Element oaiXmlEl);

	/**
	 * @see RecordIfc#setMode(java.lang.String)
	 */
	public void setOaiXml(String oaiXml);
	
	/**
	 * If your service produces more than one output format, then you must explicitly
	 * set the format.  However, If your service only declares
	 * one output Format, the MST will set it for you automatically.
	 */
	public void setFormat(Format format);
	
	/**
	 * The status is Record.ACTIVE (A) by default.  
	 */
	public void setStatus(char status);

	public void addSet(Set set);

}
