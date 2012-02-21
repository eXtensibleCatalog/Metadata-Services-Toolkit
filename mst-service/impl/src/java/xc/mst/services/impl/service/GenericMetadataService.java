package xc.mst.services.impl.service;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.repo.Repository;
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

    public void process(Repository repo, Format inputFormat, Set inputSet,
            Set outputSet) {
        try {
            super.process(repo, inputFormat, inputSet, outputSet);
        } catch (Exception e) {
            LOG.error("GenericMetadataService, processing repo "+ repo.getName()+" failed.", e);
        }
    }

    public void runTests() {
        TestTypeFilter.runTests(this);
    }

}
