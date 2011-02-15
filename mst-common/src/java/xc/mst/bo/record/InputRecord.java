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

/**
 * @author Benjamin Anderson
 *
 * @see RecordIfc
 * @see OutputRecord
 * @see xc.mst.services.impl.GenericMetadataService#process(InputRecord)
 */
public interface InputRecord extends RecordIfc {
	
	/**
	 * @return If this is not the first time this service has processed
	 * this record and it previously produced OutputRecords, then they
	 * will be attached as successors.  <b>note: this OutputRecords will
	 * not contain the actual payload xml</b>
	 * 
	 * @see xc.mst.services.impl.GenericMetadataService#process(InputRecord)
	 */
	public List<OutputRecord> getSuccessors();
	
	/**
	 * 
	 * @return May be null.  If it is not null, then it will be used to 
	 * create this screen http://code.google.com/p/xcmetadataservicestoolkit/wiki/LoggingRecordCounts
	 */
	public String getIndexedObjectType();

}
