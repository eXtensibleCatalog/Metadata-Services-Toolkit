package xc.mst.utils;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlHelper {
	
	private static final Logger LOG = Logger.getLogger(XmlHelper.class);
	protected SAXBuilder builder = new SAXBuilder();
	
	protected Format xmlFormat = null;
	protected XMLOutputter xmlOutputterPretty = null;
	protected XMLOutputter xmlOutputterCompact = null;

	public XMLOutputter getXMLOutputterPretty() {
		if (xmlOutputterPretty == null) {
			xmlFormat = org.jdom.output.Format.getPrettyFormat();
			xmlFormat.setEncoding("UTF-8");
			xmlOutputterPretty = new XMLOutputter(xmlFormat);
		}
		return xmlOutputterPretty;
	}
	
	public XMLOutputter getXMLOutputterCompact() {
		if (xmlOutputterCompact == null) {
			xmlFormat = org.jdom.output.Format.getCompactFormat();
			xmlFormat.setEncoding("UTF-8");
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
			return builder.build(is);
		} catch (Throwable t) {
			LOG.error("", t);
			Util.getUtil().throwIt(t);
		}
		return null;
	}
	
	public org.jdom.Document getJDomDocument(String str) {
		try {
			return builder.build(str);
		} catch (Throwable uee) {
			LOG.error("", uee);
			return null;
		}
	}
	
	public boolean diffXmlFiles(String file1, String file2) {
		try {
			LOG.debug("file1: "+new Util().slurp(file1));
			String file1contents = getString(builder.build(new FileInputStream(file1)).getRootElement());
			LOG.debug("file1contents: "+file1contents);
			
			LOG.debug("file2: "+new Util().slurp(file2));
			String file2contents = getString(builder.build(new FileInputStream(file2)).getRootElement());
			LOG.debug("file2contents: "+file2contents);
			return !file1contents.equals(file2contents);
		} catch (Throwable t) {
			Util.getUtil().throwIt(t);
			return true;
		}
	}

}
