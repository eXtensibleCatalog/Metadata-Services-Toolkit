package xc.mst.bo.record;

import java.util.List;

import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;

/**
 * Represents the messages attached to a record
 * 
 * @author Sharmila Ranganathan
 *
 */
public class RecordMessage {
	
	private long id;
	private Provider repository;
	
	private Record record;
	
	private boolean isInputRecord;
	
	private String messageCode;
	
	private String messageLevel;
	
	private int serviceId;
	
	private String detailedMessage;
	
	private String message;
	
	public RecordMessage(int serviceId, String messageCode, String messageLevel, String detailedMessage) {
		this.serviceId = serviceId;
		this.messageCode = messageCode;
		this.messageLevel = messageLevel;
		this.detailedMessage = detailedMessage;
	}

	public RecordMessage(String message) {
		this.message = message;
	}
	
	public RecordMessage(int serviceId, String messageCode, String messageLevel) {
		this.serviceId = serviceId;
		this.messageCode = messageCode;
		this.messageLevel = messageLevel;
	}

	public Provider getRepository() {
		return repository;
	}

	public void setRepository(Provider repository) {
		this.repository = repository;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public boolean isInputRecord() {
		return isInputRecord;
	}

	public void setInputRecord(boolean isInputRecord) {
		this.isInputRecord = isInputRecord;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessageLevel() {
		return messageLevel;
	}

	public void setMessageLevel(String messageLevel) {
		this.messageLevel = messageLevel;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public String getDetailedMessage() {
		return detailedMessage;
	}

	public void setDetailedMessage(String detailedMessage) {
		this.detailedMessage = detailedMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("messageCode=" + messageCode);
		buffer.append(" messageLevel=" + messageLevel);
		buffer.append(" service=" + serviceId);
		buffer.append(" message=" + message);
		buffer.append(" detailedMessage=" + detailedMessage);
		

		return buffer.toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
