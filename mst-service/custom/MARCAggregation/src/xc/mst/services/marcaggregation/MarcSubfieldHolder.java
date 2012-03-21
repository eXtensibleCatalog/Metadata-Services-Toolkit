package xc.mst.services.marcaggregation;


/**
 * A very specific container. This class encapsulates a sub fields and contains data from 1 subfield.
 *
 * @author John Brand
 *
 */
public class MarcSubfieldHolder {

    private Character               s_subfield;
    private String                  s_subfieldContents;

    public MarcSubfieldHolder(Character subfield, String subfieldContents) {
        s_subfield         = subfield;
        s_subfieldContents = subfieldContents;
    }

    public Character getSubfieldName()     { return s_subfield; }
    public String getSubfieldContents()    { return s_subfieldContents; }

}
