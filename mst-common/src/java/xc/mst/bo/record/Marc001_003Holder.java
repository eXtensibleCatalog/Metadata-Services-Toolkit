package xc.mst.bo.record;


/**
 * A convenience class. This class encapsulates a MARC Bibliographic ID, which consists of 001 and 003 control fields.
 *
 * @author Chris Delis
 *
 */
public class Marc001_003Holder {

    private String s_001;
    private String s_003;

    public Marc001_003Holder(String _001, String _003) {
        s_001 = _001;
        s_003 = _003;
    }

    public String get001() { return s_001; }
    public String get003() { return s_003; }
    
    public boolean equals(Object that) {
        if (that == null) return false;
        if (this == that) return true;
        if(that.getClass() != this.getClass()) return false; 
        Marc001_003Holder thatObj = (Marc001_003Holder)that;
        if(! thatObj.get001().equals(this.get001())) return false;
        if(! thatObj.get003().equals(this.get003())) return false;
        return true;
    }

    public int hashCode() {
        return this.get001().concat(this.get003()).hashCode();
    }

    public String toString() {
    	return "[" + s_003 + "] " + s_001;
    }
}
