package xc.mst.services.example.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;

import xc.mst.bo.provider.Format;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordMessage;

public class MessagesTest extends MockHarvestTest {

    private static final Logger LOG = Logger.getLogger(MessagesTest.class);

    @Override
    public Format[] getIncomingFormats() throws Exception {
        return new Format[] { getFooBarFormat() };
    }

    public Format getHarvestOutFormat() throws Exception {
        return getFooBarFormat();
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("messages");
        return fileStrs;
    }

    @Override
    public void incrementalHarvestTest(String lastOaiRequest) {
        try {
            List<Record> incomingRecords = getRepository().getRecords(new Date(0), new Date(), 0l, null, null);
            Record incomingRecord = incomingRecords.get(0);
            List<RecordMessage> incomingMessages = incomingRecord.getMessages();
            List<Record> outgoingRecords = getServiceRepository().getRecords(new Date(0), new Date(), 0l, null, null);
            Record outgoingRecord = outgoingRecords.get(0);
            List<RecordMessage> outgoingMessages = outgoingRecord.getMessages();

            if (lastOaiRequest.contains("010")) {
                Assert.assertEquals(incomingRecords.size(), 1);
                Assert.assertEquals(incomingMessages.size(), 1);
                Assert.assertEquals(outgoingRecords.size(), 1);
                Assert.assertEquals(outgoingMessages.size(), 1);
            } else if (lastOaiRequest.contains("020")) {
                Assert.assertEquals(incomingRecords.size(), 1);
                Assert.assertEquals(incomingMessages.size(), 0);
                Assert.assertEquals(outgoingRecords.size(), 1);
                Assert.assertEquals(outgoingMessages.size(), 0);
            }
        } catch (Throwable t) {
            getUtil().throwIt(t);
        }
    }

    @Override
    public void finalTest() throws Exception {
    }

}
