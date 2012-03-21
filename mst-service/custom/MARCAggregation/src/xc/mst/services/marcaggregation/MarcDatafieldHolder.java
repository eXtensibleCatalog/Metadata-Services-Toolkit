package xc.mst.services.marcaggregation;

import java.util.ArrayList;
import java.util.List;

/**
 * A very specific container. This class encapsulates data fields that contain 1 or more subfields.
 * No error checking will be done for repeating data or enforcement of how many or few fields or few subfields
 * there should be or anything else.
 *
 * Just use this to store and generate a block of
 * text to be placed in an xml file as an output record from the MAS.
 *
 * @author John Brand
 *
 */
public class MarcDatafieldHolder {

    private String                   s_datafield;
    private String                   s_ind1;
    private String                   s_ind2;
    private List<MarcSubfieldHolder> s_subfields = new ArrayList<MarcSubfieldHolder>();

    public MarcDatafieldHolder(String datafield, List<MarcSubfieldHolder> subfields, String ind1, String ind2) {
        s_datafield = datafield;
        s_ind1      = ind1;
        s_ind2      = ind2;
        s_subfields.addAll(subfields);
    }

    public String getDatafield()                           { return s_datafield; }
    public String getInd1()                                { return s_ind1; }
    public String getInd2()                                { return s_ind2; }
    public List<MarcSubfieldHolder> getSubfields()         { return s_subfields; }

    /**
     * @return String that looks something like this format:
     *
      <marc:datafield ind1=" " ind2=" " tag="035">
      <marc:subfield code="a">(OCoLC)1788884</marc:subfield>
      </marc:datafield>
     *
     */
    public String toString() {
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
        final String RIGHT         = ">";
        final String SEP           = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder(LEFT);
        sb.append(DATAFIELD_TAG).append(SP).append(IND1).append(QU).append(s_ind1).append(QU).append(SP);
        sb.append(IND2).append(QU).append(s_ind2).append(QU).append(SP).append(TAG).append(QU).append(s_datafield).append(QU).append(RIGHT).append(SEP);

        for (MarcSubfieldHolder subfield: s_subfields) {
            sb.append(LEFT).append(SUBFIELD_TAG).append(SP).append(CODE).append(QU).append(subfield.getSubfieldName()).append(QU).append(RIGHT);
            sb.append(subfield.getSubfieldContents());
            sb.append(LEFT_CLS).append(SUBFIELD_TAG).append(RIGHT).append(SEP);
        }

        sb.append(LEFT_CLS).append(DATAFIELD_TAG).append(RIGHT).append(SEP);

        return sb.toString();
    }

    // assumption that holds for now , and we rely on it:  there is only 1 subfield within the datafield
    public boolean equals(Object that) {
        if (that == null) return false;
        if ( this == that ) return true;
        if ( !(that instanceof MarcDatafieldHolder) ) return false;

        MarcDatafieldHolder h = (MarcDatafieldHolder) that;
        if (this.getSubfields().size() >0  && h.getSubfields().size()>0) {
            if (this.getSubfields().get(0).getSubfieldContents().equals(h.getSubfields().get(0).getSubfieldContents())) {
                return true;
            }
        }
        else if (this.getSubfields().size() == h.getSubfields().size()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (s_ind1+s_ind2+this.getSubfields().get(0).getSubfieldContents()+s_datafield).hashCode();
    }
}
