package xc.mst.services.marcaggregation;


public class RecordOfSourceData {
    public RecordOfSourceData(char leaderByte17, int size) {
        this.leaderByte17 = leaderByte17;
        this.size = size;
    }
    public final char leaderByte17;
    public final int size;
}
