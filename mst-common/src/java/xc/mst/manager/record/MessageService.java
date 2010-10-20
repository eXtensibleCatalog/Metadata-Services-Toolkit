package xc.mst.manager.record;

import java.util.List;

import org.testng.log4testng.Logger;

import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.manager.BaseService;

public class MessageService extends BaseService {
	
	private static final Logger LOG = Logger.getLogger(MessageService.class);

	public void injectMessages(List<Record> records) { 
		getMessageDAO().injectMessages(records);
		for (Record r : records) {
			injectMessageMessage(r);
		}
	}
	
	public void injectMessages(Record r) {
		getMessageDAO().injectMessages(r);
		injectMessageMessage(r);
	}
	
	public void injectMessageMessage(Record r) {
		for (RecordMessage m : r.getMessages()) {
			try {
				m.setMessage(getServicesService().getServiceById(m.getServiceId()).getMetadataService().getMessage(m.getCode()));
			} catch (Throwable t) {
				LOG.error("", t);
			}
		}
	}
}
	
