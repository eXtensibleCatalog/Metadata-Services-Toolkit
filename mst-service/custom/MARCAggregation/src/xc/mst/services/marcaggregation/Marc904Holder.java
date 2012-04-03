package xc.mst.services.marcaggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Marc904Holder{

    /**
     * A very specific container representing the 'view'. This class encapsulates 904 data fields that contain 2 subfields.
     * No error checking will be done for repeating data or enforcement of how many or few fields or few subfields
     * there should be or anything else.
     *
     * Just use this to store and generate a block of
     * text to be placed in an xml file as an output record from the MAS.
     *
     * @author John Brand
     *
     */
    private List<String>        s_904s = new ArrayList<String>();
    private static final Logger LOG    = Logger.getLogger(Marc904Holder.class);

    public Marc904Holder(List<String> _904s) {
        s_904s = _904s;
    }

    private List<String> get904s()             { return s_904s; }

    /**
     * Create a 904$a based on the 014$a value if 014 indicator 1 = 1.
     * Also, if 014$b is present,
     * create the new 904 if and only if the contents of the 014$b match the same holdings records 003 value.
     *
     *   In addition, the 904 field contains $1
     *   NyRoXCO to identify it as a field created by an XC service.
     *
     * @return String that looks something like this format:
     *
      <marc:datafield ind1=" " ind2=" " tag="904">
      <marc:subfield code="a">1788884</marc:subfield>
      <marc:subfield code="1">NyRoXCO</marc:subfield>
      </marc:datafield>
     *
     */
    public String toString() {
        final String DATAFIELD_TAG = "marc:datafield";
        final String SUBFIELD_TAG  = "marc:subfield";
        final String CODE          = "code=";
        final String SP            = " ";
        final String QU            = "\"";
        final String LEFT          = "<";
        final String LEFT_CLS      = "</";
        final String RIGHT         = ">";
        final String SEP           = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        for (String subfield: get904s()) {
            sb.append("<marc:datafield ind1=\" \" ind2=\" \" tag=\"904\">");
            sb.append(SEP);
            sb.append(LEFT).append(SUBFIELD_TAG).append(SP).append(CODE).append(QU).append("a").append(QU).append(RIGHT);
            sb.append(subfield);
            sb.append(LEFT_CLS).append(SUBFIELD_TAG).append(RIGHT).append(SEP);
            sb.append("<marc:subfield code=\"1\">NyRoXCO</marc:subfield>").append(SEP);
            sb.append(LEFT_CLS).append(DATAFIELD_TAG).append(RIGHT).append(SEP);
        }
        return sb.toString();
    }
}
