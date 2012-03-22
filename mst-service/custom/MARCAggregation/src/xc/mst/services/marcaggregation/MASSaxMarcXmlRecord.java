package xc.mst.services.marcaggregation;

import org.xml.sax.XMLReader;

import xc.mst.bo.record.SaxMarcXmlRecord;

public class MASSaxMarcXmlRecord extends SaxMarcXmlRecord {

    public MASSaxMarcXmlRecord(String marcXml) {
        super(marcXml);
    }


    protected XMLReader getXmlReader() {
        return xmlReader;
    }
}
