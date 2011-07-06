package xc.mst.manager.record;

import org.testng.log4testng.Logger;

import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;
import xc.mst.manager.BaseService;
import xc.mst.services.MetadataService;

public class MessageService extends BaseService {

    private static final Logger LOG = Logger.getLogger(MessageService.class);

    public void injectMessages(Record r) {
	getMessageDAO().injectMessages(r);
	injectMessageMessage(r);
    }

    public void injectMessageMessage(Record r) {
	for (RecordMessage m : r.getMessages()) {
	    try {
		final MetadataService service = getServicesService()
			.getServiceById(m.getServiceId()).getMetadataService();
		final int code = m.getCode();
		if (m.isInfoRecord()) {
		    final String type = "info";
		    if (service.isMessageEnabled(code, type)) {
			m.setMessage(service.getMessage(code, type));
		    }
		} else if (m.isWarnRecord()) {
		    final String type = "warn";
		    if (service.isMessageEnabled(code, type)) {
			m.setMessage(service.getMessage(code, type));
		    }
		} else { // default == "error" record
		    final String type = "error";
		    if (service.isMessageEnabled(code, type)) {
			m.setMessage(service.getMessage(code, type));
		    }
		}
	    } catch (Throwable t) {
		LOG.error("", t);
	    }
	}
    }
}
