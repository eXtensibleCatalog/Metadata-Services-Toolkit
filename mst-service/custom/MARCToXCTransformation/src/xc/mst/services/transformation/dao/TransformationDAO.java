/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.transformation.dao;

import gnu.trove.TLongLongHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xc.mst.dao.BaseDAO;

/**
 * 
 * @author Benjamin D. Anderson
 *
 */
public class TransformationDAO extends BaseDAO {
	
	public void persistBibMaps(
			TLongLongHashMap bibsProcessedLongId, 
			Map<String, Long> bibsProcessedStringId,
			TLongLongHashMap bibsYet2ArriveLongId,
			Map<String, Long> bibsYet2ArriveStringId ) {
		
	}
	
	public void loadBibMaps	(
			TLongLongHashMap bibsProcessedLongId, 
			Map<String, Long> bibsProcessedStringId,
			TLongLongHashMap bibsYet2ArriveLongId,
			Map<String, Long> bibsYet2ArriveStringId ) {

	}
	
	public List<Long> getLinkedRecordIds(Long toRecordId) {
		List<Long> linkedRecordsIds = new ArrayList<Long>();
		return linkedRecordsIds;
	}
	
	public void persistLinkedRecordIds(long[] recordIds) {
		
	}
}
