package xc.mst.services.marcaggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Marc904Holder  implements Comparable<Marc904Holder> {



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
        private String                   s_003field;
        private String                   s_004field;   // can be null theoretically.
        private List<Marc014Holder>      s_subfields = new ArrayList<Marc014Holder>();

        private static final Logger LOG = Logger.getLogger(Marc904Holder.class);

        public Marc904Holder(String _003field, String _004field, List<Marc014Holder> subfields) {
            s_003field = _003field;
            s_004field = _004field;
            s_subfields.addAll(subfields);
        }

        /*
        Holdings record associated with a selected bibliographic record lacks 003: Any holdings record
        processed by Aggregation that lacks an 003 field should generate an error, and the holdings record
        is “held”.

        so we don't get here if no 003 present.
        */
        public String get003field()                           { return s_003field; }
        public String get004field()                           { return s_004field; }
        public List<Marc014Holder> getSubfields()             { return s_subfields; }

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
            datacheck();
            final String DATAFIELD_TAG = "marc:datafield";
            final String SUBFIELD_TAG  = "marc:subfield";
            final String CODE          = "code=";
            final String SP            = " ";
            final String QU            = "\"";
            final String LEFT          = "<";
            final String LEFT_CLS      = "</";
            final String RIGHT         = ">";
            final String SEP           = System.getProperty("line.separator");

            //1st, the 904 based off the 004:
            // (there will always be one?)
            StringBuilder sb = new StringBuilder("<marc:datafield ind1=\" \" ind2=\" \" tag=\"904\">");
            sb.append(SEP);
            sb.append(LEFT).append(SUBFIELD_TAG).append(SP).append(CODE).append(QU).append("a").append(QU).append(RIGHT);
            sb.append(get004field());
            sb.append(LEFT_CLS).append(SUBFIELD_TAG).append(RIGHT).append(SEP);
            sb.append("<marc:subfield code=\"1\">NyRoXCO</marc:subfield>").append(SEP);
            sb.append(LEFT_CLS).append(DATAFIELD_TAG).append(RIGHT).append(SEP);

            //2nd, the 904(s) based off the 014(s):
            if (s_subfields.size() >0) {
                for (Marc014Holder subfield: s_subfields) {
                    if (subfield.getInd1().equals("1")) {
                        if (subfield.get014b() != null) {
                            // we are making assumption for this service that there is an 003.
                            if (subfield.get014b().equals(get003field())) {
                                // create it.
                                sb.append("<marc:datafield ind1=\" \" ind2=\" \" tag=\"904\">");
                                sb.append(SEP);
                                sb.append(LEFT).append(SUBFIELD_TAG).append(SP).append(CODE).append(QU).append("a").append(QU).append(RIGHT);
                                sb.append(subfield.get014a());
                                sb.append(LEFT_CLS).append(SUBFIELD_TAG).append(RIGHT).append(SEP);
                                sb.append("<marc:subfield code=\"1\">NyRoXCO</marc:subfield>").append(SEP);
                                sb.append(LEFT_CLS).append(DATAFIELD_TAG).append(RIGHT).append(SEP);
                            }
                        }
                        else {
                            // create it.
                            sb.append("<marc:datafield ind1=\" \" ind2=\" \" tag=\"904\">");
                            sb.append(SEP);
                            sb.append(LEFT).append(SUBFIELD_TAG).append(SP).append(CODE).append(QU).append("a").append(QU).append(RIGHT);
                            sb.append(subfield.get014a());
                            sb.append(LEFT_CLS).append(SUBFIELD_TAG).append(RIGHT).append(SEP);
                            sb.append("<marc:subfield code=\"1\">NyRoXCO</marc:subfield>").append(SEP);
                            sb.append(LEFT_CLS).append(DATAFIELD_TAG).append(RIGHT).append(SEP);
                        }

                    }
                }
            }
            return sb.toString();
        }

        private void datacheck() {
            if (get004field() != null) return;   // good enough.
            if (s_subfields.size() <1) {
                throw new RuntimeException("bad holding record, no 004 or 014's");
            }
        }

        // assumption that holds for now , and we rely on it:  there is only 1 subfield within the data field
        public boolean equals(Object that) {
            if (that == null) return false;
            if ( this == that ) return true;
            if ( !(that instanceof Marc904Holder) ) return false;

            Marc904Holder h = (Marc904Holder) that;
            return (marcHolderSubfieldsEqual(h));
        }

        // assumption that holds for now , and we rely on it:  there is only 1 subfield within the data field
        protected boolean marcHolderSubfieldsEqual(Marc904Holder h) {
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
        protected boolean differentiatedMarcHolderSubfieldsEqual(Marc904Holder h) {
            return false;
        }

        // you don't really want to see a string from an 010, but if you get something like that or any other unexpected field
        // name just return the entire field value.  For others we know about and can trim to actual matching value, return that.
        // assumption - 1 subfield and only 1, and it will be there.
        //
        private String getString(Marc904Holder h) {
            if (h.getSubfields().size() <1) {
                return "";
            }
            return "";
        }

        public int hashCode() {
            StringBuilder s = new StringBuilder(s_003field);
            if (s_004field !=null) {
                s.append(s_004field);
            }
            for (Marc014Holder subfield: s_subfields) {
                s.append(subfield.getInd1());
                s.append(subfield.get014a().getSubfieldName());
                s.append(subfield.get014a().getSubfieldContents());
                if (subfield.get014b().getSubfieldContents() != null) {
                    s.append(subfield.get014b().getSubfieldName());
                    s.append(subfield.get014b().getSubfieldContents());
                }
            }
            return (s.toString()).hashCode();
        }

        @Override
        public int compareTo(Marc904Holder o2) {
            Marc904Holder o1 = this;
            if (o1.equals(o2)) return 0;
            else {
                return getString(this).compareTo(getString(o2));
            }
        }




}
