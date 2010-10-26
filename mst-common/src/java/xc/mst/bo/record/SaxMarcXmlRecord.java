package xc.mst.bo.record;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import xc.mst.bo.record.marc.Field;
import xc.mst.bo.record.marc.MarcRecord;
import xc.mst.bo.record.marc.Subfield;
import xc.mst.utils.TimingLogger;
import xc.mst.utils.Util;


public class SaxMarcXmlRecord implements ContentHandler {
	
	private static final Logger LOG = Logger.getLogger(SaxMarcXmlRecord.class);
	private static final List<Field> EMPTY_ARRAY_LIST = new ArrayList<Field>();
	
	private static XMLReader xmlReader = null;

	private static final String LEADER = "leader";
	private static final String CONTROL_FIELD = "controlfield";
	private static final String DATA_FIELD = "datafield";
	private static final String SUB_FIELD = "subfield";
	
	static {
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
		} catch (Throwable t) {
			Util.getUtil().throwIt(t);
		}
	}
	
	protected boolean inTextValueField = false;
	protected Field currentField = null;
	protected Subfield currentSubfield = null;
	protected StringBuilder sb = new StringBuilder();
	
	protected MarcRecord marcRecord = new MarcRecord();
	
	public String getLeader() { 
		return marcRecord.getLeader(); 
	}
	
	public SaxMarcXmlRecord(String marcXml) {
		try {
			TimingLogger.start("sax");
			xmlReader.setContentHandler(this);
			xmlReader.parse(new InputSource(new StringReader(marcXml)));
		} catch (Throwable t) {
			Util.getUtil().throwIt(t);
		} finally {
			TimingLogger.stop("sax");
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sb.append(ch, start, length);
	}

	public void endDocument() throws SAXException {
		LOG.debug(marcRecord.toString());
	}

	@SuppressWarnings("unchecked")
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (LEADER.equals(localName)) {
			marcRecord.setLeader(sb.toString());
			sb.delete(0, sb.length());
		} else if (CONTROL_FIELD.equals(localName)) {
			currentField.setContents(sb.toString());
			sb.delete(0, sb.length());
			
			if (currentField.getTag() == 3) {
				marcRecord.setOrgCode(currentField.getContents());
			}
			currentField = null;
		} else if (DATA_FIELD.equals(localName)) {
			if (currentField.getTag() == 35 && marcRecord.getOrgCode() == null) {
				marcRecord.setOrgCode(currentField.getContents());
			}
			if (currentField.getTag() == 880) {
				if (currentField.getSubfields() != null) {
					for (Subfield sf : currentField.getSubfields()) {
						if (sf.getCode() == '6') {
							currentField.setTag(Integer.parseInt(sf.getContents().substring(0, 3)));
						}
					}
				}
			}
			boolean orgCodeFail = false;
			if (currentField.getTag() >= 900 && currentField.getTag() <= 999) {
				orgCodeFail = true;
				if (currentField.getSubfields() != null) {
					for (Subfield sf : currentField.getSubfields()) {
						if (sf.getCode() == '5' && sf.getContents().equals(marcRecord.getOrgCode())) {
							orgCodeFail = false;
						}
					}	
				}
			}
			if (!orgCodeFail) {
				List dataFields = (List)marcRecord.getDataFields().get(currentField.getTag());
				if (dataFields == null) {
					dataFields = new ArrayList();
					marcRecord.getDataFields().put(currentField.getTag(), dataFields);
				}
				dataFields.add(currentField);
			}
			
			currentField = null;
		} else if (SUB_FIELD.equals(localName)) {
			currentSubfield.setContents(sb.toString());
			sb.delete(0, sb.length());	
			currentSubfield = null;
		}
		inTextValueField = false;
	}

	public void endPrefixMapping(String prefix) throws SAXException {}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {		
		if (inTextValueField) {
			sb.append(ch, start, length);
		}
	}

	public void processingInstruction(String target, String data)
			throws SAXException {		
	}

	public void setDocumentLocator(Locator locator) {		
	}

	public void skippedEntity(String name) throws SAXException {		
	}

	public void startDocument() throws SAXException {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		try {
			if (LEADER.equals(localName)) {
				inTextValueField = true;
			} else if (CONTROL_FIELD.equals(localName)) {
				currentField = new Field();
				String tag = atts.getValue("tag");
				currentField.setTag(Integer.parseInt(tag));
				
				List controlFields = (List)marcRecord.getControlFields().get(currentField.getTag());
				if (controlFields == null) {
					controlFields = new ArrayList();
					marcRecord.getDataFields().put(currentField.getTag(), controlFields);
				}
				controlFields.add(currentField);
				
				marcRecord.getControlFields().put(currentField.getTag(), controlFields);
				inTextValueField = true;
			} else if (DATA_FIELD.equals(localName)) {
				currentField = new Field();
				String tag = atts.getValue("tag");
				currentField.setTag(Integer.parseInt(tag));
				
				String ind1 = StringUtils.trimToNull(atts.getValue("ind1"));
				if (ind1 != null) {
					currentField.setInd1(Integer.parseInt(ind1));
				}
				
				String ind2 = StringUtils.trimToNull(atts.getValue("ind2"));
				if (ind2 != null) {
					currentField.setInd2(Integer.parseInt(ind2));
				}
			} else if (SUB_FIELD.equals(localName)) {
				currentSubfield = new Subfield();
				currentField.getSubfields().add(currentSubfield);
				String codeStr = StringUtils.trimToNull(atts.getValue("code"));
				if (codeStr != null) {
					currentSubfield.setCode(codeStr.charAt(0));
				}
				
				inTextValueField = true;
			}
		} catch (Throwable t) {
			Util.getUtil().throwIt(t);
		}
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {		
		
	}
	
	@SuppressWarnings("unchecked")
	public String getControlField(int target) {
		List l = (List)marcRecord.getControlFields().get(target);
		if (l != null && l.size() > 0) {
			return ((Field)l.get(0)).getContents();
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Field> getDataFields(int targetField) {
		List<Field> fields = (List<Field>)marcRecord.getDataFields().get(targetField);
		if (fields != null)
			return fields;
		else
			return EMPTY_ARRAY_LIST;
	}
	
	@SuppressWarnings("unchecked")
	public List<Field> get945() {
		List<Field> fields = (List<Field>)marcRecord.getDataFields().get(945);
		if (fields != null)
			return fields;
		else
			return EMPTY_ARRAY_LIST;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getSubfield(int targetField, char targetSubfield) {
		List<String> ret = new ArrayList<String>();
		List<Field> dataFields = (List)marcRecord.getDataFields().get(targetField);
		//boolean foundCorrectOrgCode = false;
		if (dataFields != null) {
			for (Field df : dataFields) {
				if (df.getSubfields() != null) {
					for (Subfield sf : df.getSubfields()) {
						if (sf.getCode() == targetSubfield) {
							ret.add(sf.getContents());	
							/*
						} else if (sf.getCode() == '5' && marcRecord.getOrgCode().equals(sf.getContents())) {
							foundCorrectOrgCode = true;
							*/
						}
					}
				}
			}
		}
		/*
		if (targetField >= 900 && targetField <= 999 && !foundCorrectOrgCode) {
			ret.clear();
		}
		*/
		return ret;
	}
	
	public static List<String> getSubfieldOfField(Field df, char subfield) {
		List<String> ret = new ArrayList<String>();
		if (df.getSubfields() != null) {
			for (Subfield sf : df.getSubfields()) {
				if (sf.getCode() == subfield) {
					ret.add(sf.getContents());	
				}
			}
		}
		return ret;
	}
	
	public static int getIndicatorOfField(Field datafield, int indicator) {
		if (indicator == 1) {
			return datafield.getInd1();
		} else if (indicator == 2) {
			return datafield.getInd2();
		} else {
			return -1;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> get004s() {
		List<String> the004s = new ArrayList<String>();
		if (marcRecord.getControlFields() != null) {
			List<Field> fields = (List<Field>)marcRecord.getControlFields().get(4);
			if (fields != null) {
				for (Field f : fields) {
					the004s.add(f.getContents());
				}	
			}
		}
		return the004s;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> get014s(int ind, char code) {
		List<String> the014s = new ArrayList<String>();
		
		if (marcRecord.getDataFields() != null) {
			List<Field> fields = (List<Field>)marcRecord.getDataFields().get(14);
			if (fields != null) {
				for (Field f : fields) {
					if (f.getInd1() == ind) {
						if (f.getSubfields() != null) {
							for (Subfield sf : f.getSubfields()) {
								if (code == sf.getCode()) {
									the014s.add(sf.getContents());
								}
							}
						}
					}
				}	
			}
		}
		return the014s;
	}
	
	public String getOrgCode() {
		return marcRecord.getOrgCode();
	}
}