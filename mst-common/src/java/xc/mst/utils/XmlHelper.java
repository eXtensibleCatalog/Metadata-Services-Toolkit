package xc.mst.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;

public class XmlHelper {
	
	private static final Logger LOG = Logger.getLogger(XmlHelper.class);
	protected SAXBuilder saxBuilder = new SAXBuilder();
	protected DOMBuilder domBuilder = new DOMBuilder();
	protected DocumentBuilder docBuilder = null;
	
	//protected Format xmlFormat = null;
	protected XMLOutputter xmlOutputterPretty = null;
	protected XMLOutputter xmlOutputterCompact = null;
	protected XMLOutputter xmlOutputterRaw = null;
	
	protected SAXBuilder getSaxBuilder() {
		if (saxBuilder ==null) {
			saxBuilder = new SAXBuilder();
		}
		return saxBuilder;
	}
	
	protected DOMBuilder getDomBuilder() {
		if (domBuilder ==null) {
			domBuilder = new DOMBuilder();
		}
		return domBuilder;
	}
	
	protected DocumentBuilder getDocumentBuilder() {
		if (docBuilder == null) {
			try {
				docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (Throwable t) {
				LOG.error("", t);
			}
			LOG.debug("docBuilder: "+docBuilder);
			LOG.debug("docBuilder.getClass(): "+docBuilder.getClass());
		}
		return docBuilder;
	}

	public XMLOutputter getXMLOutputterPretty() {
		if (xmlOutputterPretty == null) {
			Format xmlFormat = org.jdom.output.Format.getPrettyFormat();
			xmlFormat.setEncoding("UTF-8");
			xmlFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE );
			xmlFormat.setLineSeparator("\n");
			xmlOutputterPretty = new XMLOutputter(xmlFormat);
		}
		return xmlOutputterPretty;
	}
	
	public XMLOutputter getXMLOutputterCompact() {
		if (xmlOutputterCompact == null) {
			Format xmlFormat = org.jdom.output.Format.getCompactFormat();
			xmlFormat.setEncoding("UTF-8");
			xmlFormat.setLineSeparator("\n");
			xmlFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE );
			xmlOutputterCompact = new XMLOutputter(xmlFormat);
		}
		return xmlOutputterCompact;
	}
	
	public XMLOutputter getXMLOutputterRaw() {
		if (xmlOutputterRaw == null) {
			Format xmlFormat = org.jdom.output.Format.getRawFormat();
			xmlFormat.setEncoding("UTF-8");
			xmlFormat.setLineSeparator("\n");
			xmlFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE );
			xmlOutputterRaw = new XMLOutputter(xmlFormat);
		}
		return xmlOutputterRaw;
	}
	
	public String getStringPretty(Element el) {
		return getXMLOutputterPretty().outputString(el);
	}

	public String getStringCompact(Element el) {
		return getXMLOutputterCompact().outputString(el);
	}
	
	public String getStringRaw(Element el) {
		return getXMLOutputterRaw().outputString(el);
	}
	
	public String getString(Element el) {
		return getStringCompact(el);
	}
	
	public org.jdom.Document getJDomDocument(InputStream is) {
		try {
			return getSaxBuilder().build(is);
		} catch (Throwable t) {
			LOG.error("", t);
			Util.getUtil().throwIt(t);
		}
		return null;
	}
	
	public org.jdom.Document getJDomDocument(String str) {
		try {
			TimingLogger.start("xerces");
			Document doc = getDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes("UTF-8")));
			TimingLogger.stop("xerces");
			TimingLogger.start("jdom");
			org.jdom.Document d = getDomBuilder().build(doc);
			TimingLogger.stop("jdom");
			return d;
		} catch (Throwable uee) {
			LOG.error("", uee);
			return null;
		}
	}
	
	public boolean diffXmlFiles(String file1, String file2) {
		try {
			//LOG.debug("file1: "+new Util().slurp(file1));
			String file1contents = getString(getSaxBuilder().build(new FileInputStream(file1)).getRootElement());
			file1contents = file1contents.replaceAll("<datestamp>.*</datestamp>", "");
			file1contents = file1contents.replaceAll("<request.*</request>", "");
			file1contents = file1contents.replaceAll("<responseDate.*</responseDate>", "");
			//LOG.debug("file1contents: "+file1contents);
			
			//LOG.debug("file2: "+new Util().slurp(file2));
			String file2contents = getString(getSaxBuilder().build(new FileInputStream(file2)).getRootElement());
			file2contents = file2contents.replaceAll("<datestamp>.*</datestamp>", "");
			file2contents = file2contents.replaceAll("<request.*</request>", "");
			file2contents = file2contents.replaceAll("<responseDate.*</responseDate>", "");
			//LOG.debug("file2contents: "+file2contents);
			return !file1contents.equals(file2contents);
		} catch (Throwable t) {
			Util.getUtil().throwIt(t);
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	public int getNumberOfRecords(String xml) {
		int numberOfRecords = 0;

		org.jdom.Document doc = getJDomDocument(xml);
		LOG.debug("doc.getRootElement():"+doc.getRootElement());
		List<Element> records = doc.getRootElement().getChildren("record");
		if (records != null) {
			numberOfRecords = records.size();
		}
		
		return numberOfRecords;
	}
}
