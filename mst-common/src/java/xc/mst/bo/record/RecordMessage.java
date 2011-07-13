/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.bo.record;


/**
 * Represents the messages attached to a record
 * 
 * @author Sharmila Ranganathan
 * @author Benjamin D. Anderson
 *
 */
public class RecordMessage {
	
	// BDA: I can't imagine we need 5 levels, so let's keep it to 3.
	//public static final char TRACE = 'T';
	//public static final char LOG = 'L';
	public static final char INFO = 'I';
	public static final char WARN = 'W';
	public static final char ERROR = 'E';
	
	protected long id;
	protected boolean isInputRecord;
	protected int code;
	protected char level;
	protected String detail;
	protected Record record;
	protected int serviceId;

	//BDA: this can't just be set to whatever because it is linked to the servicec config file.
	//     It is only set as a convenience by the MST.
	protected String message;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public boolean isInputRecord() {
		return isInputRecord;
	}
	
	public boolean isErrorRecord() {
		return this.level == (ERROR);
	}
	
	public boolean isInfoRecord() {
		return this.level == (INFO);
	}
	
	public boolean isWarnRecord() {
		return this.level == (WARN);
	}
	
	public void setInputRecord(boolean isInputRecord) {
		this.isInputRecord = isInputRecord;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}

	public char getLevel() {
		return level;
	}
	public void setLevel(char level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public Record getRecord() {
		return record;
	}
	public void setRecord(Record record) {
		this.record = record;
	}

	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public RecordMessage clone() {
		RecordMessage rm = new RecordMessage();
		rm.id = this.id;
		rm.isInputRecord = this.isInputRecord;
		rm.code = this.code;
		rm.level = this.level;
		rm.detail = this.detail;
		rm.record = this.record;
		rm.serviceId = this.serviceId;
		return rm;
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(super.toString());
		buffer.append(" id=" + id);
		buffer.append(" code=" + code);
		buffer.append(" level=" + level);
		buffer.append(" serviceId=" + serviceId);
		buffer.append(" message=" + message);
		buffer.append(" detailed=" + detail);
		return buffer.toString();
	}
}
