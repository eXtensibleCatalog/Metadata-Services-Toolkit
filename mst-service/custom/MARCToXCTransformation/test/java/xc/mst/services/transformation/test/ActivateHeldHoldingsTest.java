package xc.mst.services.transformation.test;

import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.record.RecordCounts;

public class ActivateHeldHoldingsTest extends MockHarvestTest {

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("recordCounts");
        return fileStrs;
    }

    @Override
    public void finalTest() throws Exception {
        RecordCounts serviceMostRecentOutgoingRecordCounts = getRecordCountsDAO().getMostRecentOutgoingRecordCounts(getServiceName());

        testCounts(new int[] {6,2,0, 2,0,0, 0,2,0, 0,0,0, 0,0,0}, serviceMostRecentOutgoingRecordCounts);

        createHarvestSchedule();
        waitUntilFinished();

    }

}
