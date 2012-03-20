package xc.mst.services.marcaggregation;

import java.util.ArrayList;
import java.util.List;

/**
 * A very specific container. This class encapsulates data fields that contain 1 or more subfields.
 * No error checking will be done for repeating data.  Just use this to store and generate a block of
 * text to be placed in an xml file as an output record from the MAS.
 *
 * @author John Brand
 *
 */
public class MarcDatafieldHolder {

    private static String                  s_datafield;
    private static String                  s_ind1;
    private static String                  s_ind2;
    private static ArrayList<String>       s_subfields = new ArrayList<String>();

    public MarcDatafieldHolder(String datafield, List<String> subfields, String ind1, String ind2) {
        s_datafield = datafield;
        s_ind1      = ind1;
        s_ind2      = ind2;
        s_subfields.addAll(subfields);
    }

    public String getDatafield()        { return s_datafield; }
    public String getInd1()             { return s_ind1; }
    public String getInd2()             { return s_ind2; }
    public List<String> getSubfields()  { return s_subfields; }  //TODO this needs to be a pair of subfield code + subfield

    /**
     * @return String that looks something like this format:
     *
      <marc:datafield ind1=" " ind2=" " tag="035">
      <marc:subfield code="a">(OCoLC)1788884</marc:subfield>
      </marc:datafield>
     *
     */
    public String getMarcdatafield() {
        final String DATAFIELD_TAG = "marc:datafield";
        final String SUBFIELD_TAG  = "marc:subfield";
        final String IND1          = "ind1=";
        final String IND2          = "ind2=";
        final String TAG           = "tag=";
        final String CODE          = "code=";
        final String SP            = " ";
        final String QU            = "\"";
        final String LEFT          = "<";
        final String LEFT_CLS      = "</";
        final String RIGHT          = ">";

        StringBuilder sb = new StringBuilder(LEFT);
        sb.append(DATAFIELD_TAG).append(SP).append(IND1).append(QU).append(s_ind1).append(QU).append(SP);
        sb.append(IND2).append(QU).append(s_ind2).append(QU).append(SP).append(TAG).append(s_datafield);

        for (String subfield: s_subfields) {
            sb.append(LEFT).append(SUBFIELD_TAG).append(SP).append(CODE).append().append(RIGHT);
            sb.append(subfield);
            sb.append(LEFT_CLS).append(SUBFIELD_TAG).append(RIGHT);
        }

        sb.append(LEFT_CLS).append(DATAFIELD_TAG).append(RIGHT);

        return sb.toString();
    }
}
