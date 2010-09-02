/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.dc2xc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.services.impl.GenericMetadataService;
import xc.mst.services.dc2xc.service.FooService;

public class DCTransformationService extends GenericMetadataService {
	
	private static final Logger LOG = Logger.getLogger(DCTransformationService.class);
	public static final String FOO_NS = "foo:bar";
	
	protected FooService fooService = null;
	
	public FooService getFooService() {
		return fooService;
	}

	public void setFooService(FooService fooService) {
		this.fooService = fooService;
	}

	public List<OutputRecord> process(InputRecord r) {
		List<OutputRecord> records = new ArrayList<OutputRecord>();
		try {
			OutputRecord out = null;
			if (r.getSuccessors() != null && r.getSuccessors().size() > 0) {
				out = r.getSuccessors().get(0);
			} else {
				out = getRecordService().createRecord();
			}
			out.setFormat(getFormatDAO().getById(1));
			if (r.getStatus() == Record.DELETED) {
				out.setStatus(Record.DELETED);
				LOG.debug("deleted r.getId():"+r.getId()+" out.getId():"+out.getId());
			} else {
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
