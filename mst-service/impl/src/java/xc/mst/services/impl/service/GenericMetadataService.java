package xc.mst.services.impl.service;

import xc.mst.service.impl.test.TestTypeFilter;
import xc.mst.services.impl.service.transformation.XCRecordService;

public abstract class GenericMetadataService extends xc.mst.services.GenericMetadataService {

    protected XCRecordService XCRecordService = null;

    public XCRecordService getXCRecordService() {
        return XCRecordService;
    }

    public void setXCRecordService(XCRecordService xCRecordService) {
        XCRecordService = xCRecordService;
    }

    public void runTests() {
        TestTypeFilter.runTests(this);
    }

}
