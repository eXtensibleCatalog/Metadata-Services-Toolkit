package xc.mst.services.marcaggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.services.marcaggregation.matcher.ISBNMatcher;
import xc.mst.services.marcaggregation.matcher.ISSNMatcher;
import xc.mst.services.marcaggregation.matcher.LccnMatcher;

/**
 * A very specific container representing the 'view'. This class encapsulates data fields that contain 1 or more subfields.
 * No error checking will be done for repeating data or enforcement of how many or few fields or few subfields
 * there should be or anything else.
 *
 * Implements Comparable so can be placed into TreeSet and 'automatically' get the deduping benefit.
 *
 * Just use this to store and generate a block of
 * text to be placed in an xml file as an output record from the MAS.
 *
 * @author John Brand
 *
 */
public class MarcDatafieldHolder implements Comparable<MarcDatafieldHolder> {

    private String                   s_datafield;
    private String                   s_ind1;
    private String                   s_ind2;
    private List<MarcSubfieldHolder> s_subfields = new ArrayList<MarcSubfieldHolder>();

    private static final Logger LOG = Logger.getLogger(MarcDatafieldHolder.class);

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

        if (s_subfields.size() >0) {
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
        return "";
    }

    // assumption that holds for now , and we rely on it:  there is only 1 subfield within the data field
    public boolean equals(Object that) {
        if (that == null) return false;
        if ( this == that ) return true;
        if ( !(that instanceof MarcDatafieldHolder) ) return false;

        MarcDatafieldHolder h = (MarcDatafieldHolder) that;
        return (marcHolderSubfieldsEqual(h));
    }

    // assumption that holds for now , and we rely on it:  there is only 1 subfield within the data field
    protected boolean marcHolderSubfieldsEqual(MarcDatafieldHolder h) {
        if (this.getSubfields().size() >0  && h.getSubfields().size()>0) {
            if (differentiatedMarcHolderSubfieldsEqual(h)) {
                return true;
            }
        }
        else if (this.getSubfields().size() <1 && h.getSubfields().size() <1) {
            return true;
        }
        return false;
    }

    //   it would be grand to do this in an object-oriented way, i.e. subclass this class overriding this method,
    //   but for now use the lowly if statement.
    protected boolean differentiatedMarcHolderSubfieldsEqual(MarcDatafieldHolder h) {

        if (h.getDatafield().equals("035")) {
            return  this.getSubfields().get(0).getSubfieldContents().equals(h.getSubfields().get(0).getSubfieldContents());
        }
        else if (h.getDatafield().equals("024")) {
            return  this.getSubfields().get(0).getSubfieldContents().equals(h.getSubfields().get(0).getSubfieldContents());
        }
        else if (h.getDatafield().equals("010")) {
            return  LccnMatcher.getUniqueId(this.getSubfields().get(0).getSubfieldContents()) ==
                    LccnMatcher.getUniqueId(   h.getSubfields().get(0).getSubfieldContents());
        }
        else if (h.getDatafield().equals("020")) {

            //LOG.info("*** 020:this->"+ISBNMatcher.getIsbn(this.getSubfields().get(0).getSubfieldContents())+"<-"   +
            //        " that->"+ISBNMatcher.getIsbn(h.getSubfields().get(0).getSubfieldContents())+"<-");

            return  ISBNMatcher.getIsbn(this.getSubfields().get(0).getSubfieldContents()).equals(
                    ISBNMatcher.getIsbn(   h.getSubfields().get(0).getSubfieldContents()));
        }
        else if (h.getDatafield().equals("022")) {
            return  ISSNMatcher.getAllButDash(this.getSubfields().get(0).getSubfieldContents()).equals(
                    ISSNMatcher.getAllButDash(   h.getSubfields().get(0).getSubfieldContents()));
        }
        return false;
    }

    // you don't really want to see a string from an 010, but if you get something like that or any other unexpected field
    // name just return the entire field value.  For others we know about and can trim to actual matching value, return that.
    // assumption - 1 subfield and only 1, and it will be there.
    //
    private String getString(MarcDatafieldHolder h) {
        if (h.getSubfields().size() <1) {
            return "";
        }

        if (h.getDatafield().equals("020")) {
            return  ISBNMatcher.getIsbn(h.getSubfields().get(0).getSubfieldContents());
        }
        else if (h.getDatafield().equals("022")) {
            return  ISSNMatcher.getAllButDash(h.getSubfields().get(0).getSubfieldContents());
        }
        // 035 or 024 unknown
        return h.getSubfields().get(0).getSubfieldContents();
    }

    public int hashCode() {
        String s = "";
        if (this.getSubfields().size()>0) {
            s= this.getSubfields().get(0).getSubfieldContents();
        }
        return (s_ind1 + s_ind2+ s + s_datafield).hashCode();
    }

    @Override
    public int compareTo(MarcDatafieldHolder o2) {
        MarcDatafieldHolder o1 = this;
        if (o1.equals(o2)) return 0;
        
        try {
	        if (o1.getDatafield().equals("010")) {
	            Long long0 =LccnMatcher.getUniqueId(this.getSubfields().get(0).getSubfieldContents());
	            if (o2.getDatafield().equals("010")) {
		            Long long1 =LccnMatcher.getUniqueId(o2.getSubfields().get(0).getSubfieldContents());
		            return  long0.compareTo(long1);
	            }
	        }
        } catch (Exception e) {
        	// fall through...
        }
        
        return getString(this).compareTo(getString(o2));
    }
}
