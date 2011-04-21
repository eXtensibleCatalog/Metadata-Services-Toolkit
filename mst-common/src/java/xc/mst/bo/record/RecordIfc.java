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

/**
 * The is the base record interface to be used by service implementers.
 * 
 * @author Benjamin Anderson
 *
 */
public interface RecordIfc {

	/**
	 * @return the MST wide unique id of this Record.  It is the responsibility of the 
	 * MST (not the service implementer) to assign this value.  To create an OutputRecord, use
	 * this helper method getRecordService().createRecord().  When you do this, an id will
	 * be injected into your newly created OutputRecord.
	 */
	public long getId();
	
	/**
	 * 
	 * @param mode
	 * 
	 * There are ways to read and write Records (Record.STRING_MODE and Record.JDOM_MODE).  By default
	 * records are in Record.JDOM_MODE.  If you attempt to call RecordIfc.getOaiXmlEl or 
	 * OutputRecord.setOaiXmlEl when you are in Record.STRING_MODE, a RuntimeException will be thrown.
	 * Likewise, if you attempt to call RecordIfc.getOaiXml or OutputRecord.setOaiXml when you are in
	 * Record.JDOM_MODE a RuntimeException will be thrown.
	 */
	public void setMode(String mode);

	/** 
	 * @see RecordIfc#setMode(java.lang.String)
	 */
	public Element getOaiXmlEl();
	
	/** 
	 * @see RecordIfc#setMode(java.lang.String)
	 */
	public String getOaiXml();
	
	/**
	 * 
	 * @return One of Record.ACTIVE (A), Record.HELD (H), or Record.DELETED (D)
	 */
	public char getStatus();

	/**
	 * 
	 * @return One of Record.ACTIVE (A), Record.HELD (H), or Record.DELETED (D)
	 */
	public char getPreviousStatus();
	public void setPreviousStatus(char status);
	
	/**
	 * @return Equivalent to getStatus() == Record.DELETED
	 */
	public boolean getDeleted();
	
	public void setMessages(List<RecordMessage> errors);

}
