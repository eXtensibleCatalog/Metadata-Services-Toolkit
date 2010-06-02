package xc.mst.utils;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlHelper {
	
	private static final Logger LOG = Logger.getLogger(XmlHelper.class);
	protected static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	protected Format xmlFormat = null;
	protected XMLOutputter xmlOutputter = null;
	protected DocumentBuilder documentBuilder = null;
	protected DOMBuilder domBuilder = null;
	
	public DocumentBuilder getDocumentBuilder() {
		if (this.documentBuilder == null) {
			try {
				return dbf.newDocumentBuilder();
			} catch (Throwable t) {
				LOG.debug("", t);
				Util.getUtil().throwIt(t);
			}
		}
		return this.documentBuilder;
	}
	
	public DOMBuilder getDOMBuilder() {
		if (this.domBuilder == null) {
			this.domBuilder = new DOMBuilder();
		}
		return this.domBuilder;
	}
	
	public XMLOutputter getXMLOutputter() {
		if (xmlOutputter == null) {
			xmlFormat = org.jdom.output.Format.getPrettyFormat();
			xmlOutputter = new XMLOutputter(xmlFormat);
		}
		return xmlOutputter;
	}
	
	public String getString(Element el) {
		return getXMLOutputter().outputString(el);
	}
	
	public org.jdom.Document getJDomDocument(String str) {
		try {
			return getDOMBuilder().build(getDocumentBuilder().parse(
					new ByteArrayInputStream(str.getBytes())));
			
		} catch (Throwable t) {
			LOG.error("", t);
			Util.getUtil().throwIt(t);
		}
		return null;
	}
	

}
