package xc.mst.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
	protected XMLOutputter xmlOutputterPretty = null;
	protected XMLOutputter xmlOutputterCompact = null;
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
	
	public XMLOutputter getXMLOutputterPretty() {
		if (xmlOutputterPretty == null) {
			xmlFormat = org.jdom.output.Format.getPrettyFormat();
			xmlOutputterPretty = new XMLOutputter(xmlFormat);
		}
		return xmlOutputterPretty;
	}
	
	public XMLOutputter getXMLOutputterCompact() {
		if (xmlOutputterCompact == null) {
			xmlFormat = org.jdom.output.Format.getCompactFormat();
			xmlOutputterCompact = new XMLOutputter(xmlFormat);
		}
		return xmlOutputterCompact;
	}
	
	public String getStringPretty(Element el) {
		return getXMLOutputterPretty().outputString(el);
	}
	
	public String getString(Element el) {
		return getXMLOutputterCompact().outputString(el);
	}
	
	public org.jdom.Document getJDomDocument(InputStream is) {
		try {
			return getDOMBuilder().build(getDocumentBuilder().parse(is));
		} catch (Throwable t) {
			LOG.error("", t);
			Util.getUtil().throwIt(t);
		}
		return null;
	}
	
	public org.jdom.Document getJDomDocument(String str) {
		return getJDomDocument(new ByteArrayInputStream(str.getBytes()));
	}

}
