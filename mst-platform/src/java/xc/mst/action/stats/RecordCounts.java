package xc.mst.action.stats;

import java.util.List;

import xc.mst.action.BaseActionSupport;

public class RecordCounts extends BaseActionSupport {

    protected List<String[]> incomingHarvestRecords = null;
    protected List<String[]> incomingServiceRecords = null;
    protected List<String[]> outgoingServiceRecords = null;

    public List<String[]> getIncomingHarvestRecords() {
        return incomingHarvestRecords;
    }

    public void setIncomingHarvestRecords(List<String[]> incomingHarvestRecords) {
        this.incomingHarvestRecords = incomingHarvestRecords;
    }

    public List<String[]> getIncomingServiceRecords() {
        return incomingServiceRecords;
    }

    public void setIncomingServiceRecords(List<String[]> incomingServiceRecords) {
        this.incomingServiceRecords = incomingServiceRecords;
    }

    public List<String[]> getOutgoingServiceRecords() {
        return outgoingServiceRecords;
    }

    public void setOutgoingServiceRecords(List<String[]> outgoingServiceRecords) {
        this.outgoingServiceRecords = outgoingServiceRecords;
    }

    public String viewCounts() {
        incomingHarvestRecords = getRepositoryService().getIncomingHarvestRecords();
        incomingServiceRecords = getRepositoryService().getIncomingServiceRecords();
        outgoingServiceRecords = getRepositoryService().getOutgoingServiceRecords();
        return SUCCESS;
    }
}
