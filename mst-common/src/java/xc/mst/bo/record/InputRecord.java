package xc.mst.bo.record;

import java.util.List;

public interface InputRecord extends RecordIfc {
	
	public List<OutputRecord> getSuccessors();

}
