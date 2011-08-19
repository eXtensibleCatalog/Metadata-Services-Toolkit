package xc.mst.bo.record.marc;

import java.util.List;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectProcedure;

public class MarcRecord {

    protected String leader = null;
    protected String orgCode = "";

    /**
     * The structure is as follows
     * tag
     * 1 ->
     */
    protected TIntObjectHashMap controlFields = new TIntObjectHashMap();
    protected TIntObjectHashMap dataFields = new TIntObjectHashMap();

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public TIntObjectHashMap getControlFields() {
        return controlFields;
    }

    public void setControlFields(TIntObjectHashMap controlFields) {
        this.controlFields = controlFields;
    }

    public TIntObjectHashMap getDataFields() {
        return dataFields;
    }

    public void setDataFields(TIntObjectHashMap dataFields) {
        this.dataFields = dataFields;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("\n");
        sb.append(" leader:<" + leader + ">");
        sb.append("\n");
        sb.append(" controlFields: [");
        getControlFields().forEachEntry(new TIntObjectProcedure() {
            @SuppressWarnings("unchecked")
            public boolean execute(int key, Object value) {
                List<Field> l = (List<Field>) value;
                for (Field f : l) {
                    sb.append("\n");
                    sb.append(" tag:" + f.getTag());
                    sb.append(" contents:<" + f.getContents() + ">");
                }
                return true;
            }
        });
        sb.append("\n");
        sb.append(" ]");
        sb.append("\n");
        sb.append(" dataFields: [");
        getDataFields().forEachEntry(new TIntObjectProcedure() {
            @SuppressWarnings("unchecked")
            public boolean execute(int key, Object value) {
                List<Field> l = (List<Field>) value;
                for (Field f : l) {
                    sb.append("\n");
                    sb.append(" tag:" + f.getTag());
                    sb.append(" ind1:" + f.getInd1());
                    sb.append(" ind2:" + f.getInd2());
                    sb.append(" subfields: [");
                    if (f.getSubfields() != null) {
                        for (Subfield sf : f.getSubfields()) {
                            sb.append("\n");
                            sb.append(" code:" + sf.getCode());
                            sb.append(" contents:<" + sf.getContents() + ">");
                        }
                    }
                    sb.append("\n");
                    sb.append("]");
                }
                return true;
            }
        });
        sb.append("\n");
        sb.append(" ]");

        return sb.toString();
    }
}
