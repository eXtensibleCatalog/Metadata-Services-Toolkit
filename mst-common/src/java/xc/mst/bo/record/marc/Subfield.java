package xc.mst.bo.record.marc;

public class Subfield {

    protected char code = (char) -1;

    protected String contents = null;

    public char getCode() {
        return code;
    }

    public void setCode(char code) {
        this.code = code;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

}
