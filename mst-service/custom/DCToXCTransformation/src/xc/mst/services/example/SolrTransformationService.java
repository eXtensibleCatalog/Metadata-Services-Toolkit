/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
 
package xc.mst.services.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

import xc.mst.services.impl.GenericMetadataService;
import xc.mst.services.ServiceValidationException;
import xc.mst.services.example.DCTransformationServiceConstants.FrbrLevel;
import xc.mst.services.example.bo.XCRecord;
import xc.mst.services.example.dao.DCTransformationDAO;
import xc.mst.services.example.service.XCRecordService;
import xc.mst.utils.XmlHelper;

/**
 * A Metadata Service which for each unprocessed marcxml record creates an XC schema
 * record from the data in the unprocessed record.
 *
 * @author Eric Osisek
 */
public abstract class SolrTransformationService extends GenericMetadataService
{
	private final static Logger LOG = Logger.getLogger(SolrTransformationService.class);

	protected XCRecordService XCRecordService = null;
	
	public XCRecordService getXCRecordService() {
		return XCRecordService;
	}

	public void setXCRecordService(XCRecordService xCRecordService) {
		XCRecordService = xCRecordService;
	}
	
	public DCTransformationDAO getTransformationDAO() {
		return getXCRecordService().getDCTransformationDAO();
	}

	protected XmlHelper xmlHelper = new XmlHelper();
	
	/**
	 * Construct a TransformationService Object
	 */
	public void init() {

	}
	
	@Override
	public void loadConfiguration(String config){	
		
	}
	
	@Override
	protected void validateService() throws ServiceValidationException {
		
	}

	public void setInputRecordCount(int inputRecordCount) {
		this.inputRecordCount = inputRecordCount;
	}
	
	protected XCRecord processFieldBasic(XCRecord transformInto, HashMap<String, List<Element>>dcValues, String elementName, FrbrLevel level)
	{
		// Get the target elements Dublin Core record
		List<Element> fields = dcValues.get(elementName);
		
		// If there were no matching fields return the unmodified XC record
		if( fields.isEmpty() || fields == null || fields.size() == 0)
			return transformInto;

		for ( Iterator<Element> itr = fields.iterator(); itr.hasNext();) {
			Element eitr = itr.next();
			String value = eitr.getTextTrim(); 
			
			if(LOG.isDebugEnabled())
				LOG.debug("Adding a " + level + " level " + elementName  + " of \"" + value + "\"");

			// Setup the attribute list
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();

			// Add the element to the XC record
			getXCRecordService().addElement(transformInto, elementName, value.trim(), XCRecord.DCTERMS_NAMESPACE, attributes, level);
		}
		
		// Return the result
		return transformInto;
	}
	
}
