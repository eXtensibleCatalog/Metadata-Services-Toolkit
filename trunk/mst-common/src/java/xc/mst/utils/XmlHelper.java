/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
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
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	// BDA: These statics are an 11th hour hack.  It's fine for now since it's single threaded, 
	// but should eventually be put in a pool 
	protected static DocumentBuilder docBuilder = null;
	protected static SAXBuilder saxBuilder = new SAXBuilder();
	protected static DOMBuilder domBuilder = new DOMBuilder();
	
	static {
		saxBuilder = new SAXBuilder();
		domBuilder = new DOMBuilder();
		try {
			docBuilder = dbf.newDocumentBuilder();
		} catch (Throwable t) {
			LOG.error("", t);
		}
	}

	
	
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
				docBuilder = dbf.newDocumentBuilder();
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
			TimingLogger.start("getSaxBuilder");
			SAXBuilder sb = getSaxBuilder();
			TimingLogger.stop("getSaxBuilder");
			TimingLogger.start("sax");
			org.jdom.Document doc = sb.build(is);
			TimingLogger.stop("sax");
			return doc;
		} catch (Throwable t) {
			LOG.error("", t);
			Util.getUtil().throwIt(t);
		}
		return null;
	}
	
	public org.jdom.Document getJDomDocument(String str) {
		try {
			TimingLogger.start("str.getBytes");
			byte[] bytes = str.getBytes("UTF-8");
			TimingLogger.stop("str.getBytes");
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			TimingLogger.start("getDomBuilder()");
			DocumentBuilder db = getDocumentBuilder();
			TimingLogger.stop("getDomBuilder()");
			TimingLogger.start("xerces");
			Document doc = db.parse(bais);
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
			
			//TODO some of these are temporary for the initial transformation check.  Remove the following going
			//     forward:  identifier, predecessors, xmlns:, id, workExpressed
			String[] regexps = new String[] {
					"marctoxctransformation/[0-9]*", "marctoxctransformation/XX",
					/*
					"<identifier>.*?</identifier>", "<identifier />",
					"id=..*?\\\" ?", "",
					"<predecessor>.*?</predecessor>", "<predecessor />",
					"xmlns:[a-zA-Z]*?=\\\".*?\\\" ?", "",

					" >", ">",
					"spacial", "spatial",
					"<xc:workExpressed>.*?</xc:workExpressed>", "<xc:workExpressed />",
					"<xc:expressionManifested>.*?</xc:expressionManifested>", "<xc:expressionManifested />",
					"<xc:manifestationHeld>.*?</xc:manifestationHeld>", "<xc:manifestationHeld />",
					*/
					"<datestamp>.*?</datestamp>", "",
					"<request.*?</request>", "",
					"<responseDate.*?</responseDate>", ""
			};
			String file1contents = getString(getSaxBuilder().build(new FileInputStream(file1)).getRootElement());
			for (int i=0; i<regexps.length; i+=2) {
				file1contents = file1contents.replaceAll(regexps[i], regexps[i+1]);	
			}
			LOG.debug("final chart @@"+file1contents.charAt(file1contents.length()-1)+"@@");
			LOG.debug("file1contents:"+file1+"\n"+file1contents);
			
			//LOG.debug("file2: "+new Util().slurp(file2));
			String file2contents = getString(getSaxBuilder().build(new FileInputStream(file2)).getRootElement());
			for (int i=0; i<regexps.length; i+=2) {
				file2contents = file2contents.replaceAll(regexps[i], regexps[i+1]);	
			}
			LOG.debug("final chart @@"+file2contents.charAt(file2contents.length()-1)+"@@");
			LOG.debug("file2contents: "+file2+"\n"+file2contents);
			LOG.debug("!file1contents.equals(file2contents): "+!file1contents.equals(file2contents));
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
