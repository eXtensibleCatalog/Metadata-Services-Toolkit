package xc.mst.services.marcaggregation;


public class RecordOfSourceData {
    public RecordOfSourceData(char leaderByte17, int size) {
        this.leaderByte17 = leaderByte17;
        this.size = size;
    }
    public final char leaderByte17;
    public final int size;
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("[LDR/17: ");
    	sb.append(leaderByte17);
    	sb.append("; Size: ");
    	sb.append(size);
    	sb.append("]");
    	return sb.toString();
    }
}
