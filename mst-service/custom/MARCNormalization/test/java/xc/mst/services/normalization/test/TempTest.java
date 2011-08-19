/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.normalization.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.jdom.Document;
import org.jdom.Element;
import org.testng.annotations.Test;

import xc.mst.repo.TestRepository;
import xc.mst.service.impl.test.BaseMetadataServiceTest;
import xc.mst.utils.XmlHelper;

public class TempTest extends BaseMetadataServiceTest {

    @Test
    public void doTest101() {
        try {

            String xml = "<a>  <b><hello d=\"1\" c=\"1\" z=\"1\" a=\"1\">  some space before and   after    </hello>  </b></a>";
            Document d = xmlHelper.getJDomDocument(xml);
            /*
            Element el = new Element("hello");
            el.setAttribute("d", "1");
            el.setAttribute("c", "1");
            el.setAttribute("z", "1");
            el.setAttribute("a", "1");
            */
            Element el = d.getRootElement();
            LOG.debug("raw: " + new XmlHelper().getStringRaw(el));
            LOG.debug("compact: " + new XmlHelper().getStringCompact(el));
            LOG.debug("pretty: " + new XmlHelper().getStringPretty(el));
        } catch (Throwable t) {
            LOG.error("", t);
        }
    }

    public void doTest100() {
        try {
            for (String file : new String[] {
                    TestRepository.EXPECTED_OUTPUT_RECORDS + "/NormInput/499882.xml",
                    TestRepository.ACTUAL_OUTPUT_RECORDS + "/NormInput/499882.xml" }) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = br.readLine();
                while (line != null) {
                    LOG.debug("line: " + line);
                    line = br.readLine();
                }
            }

            new XmlHelper().diffXmlFiles(
                    TestRepository.ACTUAL_OUTPUT_RECORDS + "/NormInput/499882.xml",
                    TestRepository.EXPECTED_OUTPUT_RECORDS + "/NormInput/499882.xml");

        } catch (Throwable t) {
            LOG.error("", t);
        }
    }

}
