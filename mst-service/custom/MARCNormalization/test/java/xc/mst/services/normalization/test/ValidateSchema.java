/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.normalization.test;

import java.io.File;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;
import xc.mst.service.impl.test.MockHarvestTest;

public class ValidateSchema extends BaseTest {
	
	private static final Logger LOG = Logger.getLogger(ValidateSchema.class);

	@Test
	public void validateSchema() {
		LOG.debug("validateSchema");
		try {
			SAXBuilder builder =
				  new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
			
			builder.setFeature(
					  "http://apache.org/xml/features/validation/schema", true);
			
			builder.setProperty(
					  "http://apache.org/xml/properties/schema/external-schemaLocation",   
					  "http://www.loc.gov/MARC21/slim ../conf/MARC21slim_custom.xsd "+
					  "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd ../conf/OAI-PMH.xsd");


			for (String file : new File(MockHarvestTest.EXPECTED_OUTPUT_FOLDER+"/randys-30").list()) {
				LOG.debug("file "+file);
				if (!file.endsWith(".xml")) {
					continue;
				}
				String xml = util.slurp(MockHarvestTest.EXPECTED_OUTPUT_FOLDER+"/randys-30/"+file);
				Document doc = builder.build(new StringReader(xml));
				LOG.debug("validated "+file);
			}
			
		} catch (Throwable t) {
			util.throwIt(t);
		}
	}

}
