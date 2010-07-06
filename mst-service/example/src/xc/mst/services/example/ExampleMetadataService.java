package xc.mst.services.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.record.Record;
import xc.mst.services.impl.GenericMetadataService;
import xc.mst.services.service.FooService;

public class ExampleMetadataService extends GenericMetadataService {
	
	private static final Logger LOG = Logger.getLogger(ExampleMetadataService.class);
	public static final String FOO_NS = "foo:bar";
	
	protected FooService fooService = null;
	
	public FooService getFooService() {
		return fooService;
	}

	public void setFooService(FooService fooService) {
		this.fooService = fooService;
	}

	public List<Record> process(Record r) {
		List<Record> records = new ArrayList<Record>();
		try {
			Record out = null;
			LOG.debug("in.getStatus(): "+r.getStatus());
			if (r.getSuccessors() != null && r.getSuccessors().size() > 0) {
				out = r.getSuccessors().get(0);
			} else {
				out = getRecordService().createSuccessor(r, getService());
			}
			if (r.getStatus() == Record.DELETED) {
				out.setStatus(Record.DELETED);
			}
			out.setFormat(getFormatDAO().getById(1));
			r.setMode(Record.JDOM_MODE);
			Element metadataEl = r.getOaiXmlEl();
			Element foo = metadataEl.getChild("foo", Namespace.getNamespace(FOO_NS));
			if (foo != null) {
				getFooService().fooFound(foo.getText());
				// you could do this 
				// out.setOaiXml("<bar>you've been barred: "+StringEscapeUtils.escapeXml(foo.getText())+"</bar>");
				// or you could do this
				Element barEl = new Element("bar", Namespace.getNamespace(FOO_NS));
				barEl.setText("you've been barred: "+foo.getText());
				out.setOaiXmlEl(barEl);
			} else {
				Element barEl = new Element("bar", Namespace.getNamespace(FOO_NS));
				barEl.setText("you've been foobarred!");
				metadataEl.addContent(barEl);
				out.setOaiXmlEl(metadataEl);
			}
	
			records.add(out);
		} catch (Throwable t) {
			util.throwIt(t);
		}
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
