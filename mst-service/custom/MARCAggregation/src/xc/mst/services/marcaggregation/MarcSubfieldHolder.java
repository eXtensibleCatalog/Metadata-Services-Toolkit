package xc.mst.services.marcaggregation;

import org.apache.commons.lang.xwork.StringEscapeUtils;


/**
 * A convenience class. This class encapsulates a subfield and contains data from 1 subfield.
 * The subfield is represented by its contents and character 'name.'
 *
 * @author John Brand
 *
 */
public class MarcSubfieldHolder {

    private Character               s_subfield;
    private String                  s_subfieldContents;

    public MarcSubfieldHolder(Character subfield, String subfieldContents) {
        s_subfield         = subfield;
        s_subfieldContents = StringEscapeUtils.unescapeXml(subfieldContents);
    }

    public Character getSubfieldName()     { return s_subfield; }
    public String getSubfieldContents()    { return StringEscapeUtils.escapeXml(s_subfieldContents); }

}
