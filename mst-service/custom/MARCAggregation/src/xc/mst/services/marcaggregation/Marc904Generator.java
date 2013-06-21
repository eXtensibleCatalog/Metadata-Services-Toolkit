package xc.mst.services.marcaggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Create the 904's based on 004/014, don't try to dedup here, just produce a list of 904's,
 * given 1 record's information - an 003, 004 and list of 014's.
 *
 * Create a 904$a based on the 014$a value if 014 indicator 1 = 1.
 * Also, if 014$b is present,
 * create the new 904 if and only if the contents of the 014$b match the same holdings records 003 value.
 *
 * @author John Brand
 *
 */
public class Marc904Generator {
    private String                   s_003field;
    private String                   s_004field;   // can be null theoretically.
    private List<Marc014Holder>      s_subfields = new ArrayList<Marc014Holder>();

    private static final Logger LOG = Logger.getLogger(Marc904Generator.class);

    public Marc904Generator(String _003field, String _004field, List<Marc014Holder> subfields) {
        s_003field = _003field;
        s_004field = _004field;
        s_subfields.addAll(subfields);
    }

    /*
    Holdings record associated with a selected bibliographic record lacks 003: Any holdings record
    processed by Aggregation that lacks an 003 field should generate an error, and the holdings record
    is "held".

    so we don't get here if no 003 present.
    */
    private String get003field()                           { return s_003field; }
    private String get004field()                           { return s_004field; }
    private List<Marc014Holder> getSubfields()             { return s_subfields; }

    /**
     * Create a 904$a based on the 014$a value if 014 indicator 1 = 1.
     * Also, if 014$b is present,
     * create the new 904 if and only if the contents of the 014$b match the same holdings records 003 value.
     *
     * @return dedup'd list of Strings that will be the $a fields for the new 904's.
     *
     */
    public List<String> get904s() {
        datacheck();
        List<String> list = new ArrayList<String>();
        if (get004field() != null) {
            list.add(get004field());
        }

        //2nd, the 904(s) based off the 014(s):
        if (getSubfields().size() >0) {
            for (Marc014Holder subfield: s_subfields) {
                if (subfield.getInd1().equals("1")) {
                    if (subfield.get014b() != null) {
                        // we are making assumption for this service that there is an 003.
                        if (subfield.get014b().equals(get003field())) {
                            // create it.
                            if (!list.contains(subfield.get014a().getSubfieldContents())) {
                                list.add(subfield.get014a().getSubfieldContents());
                            }
                        }
                    }
                    else {
                        // create it.
                        if (!list.contains(subfield.get014a().getSubfieldContents())) {
                            list.add(subfield.get014a().getSubfieldContents());
                        }
                    }
                }
            }
        }
        return list;
    }

    private void datacheck() {
        if (get004field() != null) return;   // good enough.
        if (s_subfields.size() <1) {
            throw new RuntimeException("bad holding record, no 004 or 014's");
        }
    }
}
