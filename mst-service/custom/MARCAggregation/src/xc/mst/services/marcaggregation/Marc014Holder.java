package xc.mst.services.marcaggregation;

public class Marc014Holder {


    /**
     * A convenience class. This class encapsulates a 014 datafield and contains data from $a subfield
     * and optionally $b subfield.
     *
     * It also includes indicator 1.  If this is not set to 1, we ignore the 014.
     *
     * We will be primarily interested in the 014$a contents.
     *
     * @author John Brand
     *
     */

        private MarcSubfieldHolder      s_014aContents;
        private MarcSubfieldHolder      s_014bContents;   // can be null
        private String                  s_ind1;

        public Marc014Holder(MarcSubfieldHolder aContents, MarcSubfieldHolder bContents, String ind1) {
            s_014aContents = aContents;
            s_014bContents = bContents;
            s_ind1 = ind1;
        }

        public String getInd1()                { return s_ind1; }
        public MarcSubfieldHolder get014a()    { return s_014aContents; }
        public MarcSubfieldHolder get014b()    { return s_014bContents; }
}
