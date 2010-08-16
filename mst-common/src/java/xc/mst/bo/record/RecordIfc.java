package xc.mst.bo.record;

import java.util.List;

import org.jdom.Element;

public interface RecordIfc {

	public long getId();
	
	public void setMode(String mode);

	public Element getOaiXmlEl();
	public String getOaiXml();

	public char getStatus();
	
	public boolean getDeleted();
	
	public void setMessages(List<RecordMessage> errors);

}
