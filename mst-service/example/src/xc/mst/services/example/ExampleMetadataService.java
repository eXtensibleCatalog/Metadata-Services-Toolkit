package xc.mst.services.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import xc.mst.bo.record.Record;
import xc.mst.services.impl.GenericMetadataService;

public class ExampleMetadataService extends GenericMetadataService {
	
	private static final Logger LOG = Logger.getLogger(ExampleMetadataService.class);
	
	public List<Record> process(Record r) {
		List<Record> records = new ArrayList<Record>();
		Record out = getRecordService().createSuccessor(r, getService());
		Element metadataEl = r.getOaiXmlEl();
		Element foo = metadataEl.getChild("foo");
		out.setOaiXml("<bar>you've been barred: "+foo.getText()+"</bar>");
		records.add(out);
		return records;
	}
	
	@Override
	public void postInstall() {
		// The install.sql is automatically run, so you don't need to run
		// that explicitly here.  But perhaps you need this hook for something
		// else.
		LOG.debug("postInstall()");
	}
	
	@Override
	public void postUninstall() {
		// The uninstall.sql is automatically run, so you don't need to run
		// that explicitly here.  But perhaps you need this hook for something
		// else.
		LOG.debug("postUnInstall()");
	}

}