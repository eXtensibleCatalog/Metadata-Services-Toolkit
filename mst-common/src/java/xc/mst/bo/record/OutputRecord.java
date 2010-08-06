package xc.mst.bo.record;

import java.util.List;

import org.jdom.Element;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;

public interface OutputRecord extends RecordIfc {
	
	public List<InputRecord> getPredecessors();
	
	public void setOaiXmlEl(Element oaiXmlEl);
	
	public void setOaiXml(String oaiXml);
	
	public void setFormat(Format format);
	
	public void setStatus(char status);

	public void addSet(Set set);

}
