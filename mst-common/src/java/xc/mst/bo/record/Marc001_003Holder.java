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

}
