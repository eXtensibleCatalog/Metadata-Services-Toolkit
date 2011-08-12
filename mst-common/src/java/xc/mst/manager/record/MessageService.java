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
                final MetadataService service = getServicesService().getServiceById(m.getServiceId()).getMetadataService();
                final int code = m.getCode();
                if (m.isInfoRecord()) {
                    if (service.isMessageEnabled(code, RecordMessage.INFO)) {
                        m.setMessage(service.getMessage(code, RecordMessage.INFO));
                    }
                } else if (m.isWarnRecord()) {
                    if (service.isMessageEnabled(code, RecordMessage.WARN)) {
                        m.setMessage(service.getMessage(code, RecordMessage.WARN));
                    }
                } else { // default == "error" record
                    if (service.isMessageEnabled(code, RecordMessage.ERROR)) {
                        m.setMessage(service.getMessage(code, RecordMessage.ERROR));
                    }
                }
            } catch (Throwable t) {
                LOG.error("", t);
            }
        }
    }
}
