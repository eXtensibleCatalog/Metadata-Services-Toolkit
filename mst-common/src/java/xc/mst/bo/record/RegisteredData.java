package xc.mst.bo.record;

public class RegisteredData {
    private String identifier;
    private String data;

    public RegisteredData(String id, String data) {
        this.identifier = id;
        this.data = data;
    }

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

    public String toString() {
        return this.identifier+": "+ this.data;
    }
}
