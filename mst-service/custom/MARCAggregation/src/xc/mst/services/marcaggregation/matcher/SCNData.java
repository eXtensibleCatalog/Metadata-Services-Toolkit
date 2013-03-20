package xc.mst.services.marcaggregation.matcher;

public class SCNData {
    public SCNData(String prefix, Integer prefixNum, String scn, String full) {
        this.prefix = prefix;
        this.prefixNum = prefixNum;
        this.scn = scn;
        this.full = full;
    }
    public SCNData(Integer prefixNum, String scn, String full) {
        this.prefixNum = prefixNum;
        this.scn = scn;
        this.full = full;
    }
    public boolean equals(Object that) {
        if (that == null) return false;
        if ( this == that ) return true;
        if ( !(that instanceof SCNData) ) return false;

        SCNData _data = (SCNData) that;
        return (_data.scn.equals(this.scn) && _data.prefixNum.equals(this.prefixNum));
    }

    public int hashCode() {
        return full.hashCode();
    }

    public String full;
    public String prefix;
    public Integer prefixNum;
    public String scn;
}
