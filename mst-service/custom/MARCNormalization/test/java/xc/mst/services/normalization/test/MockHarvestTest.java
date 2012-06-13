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

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Format;
import xc.mst.services.MetadataService;
import xc.mst.services.normalization.NormalizationService;

public class MockHarvestTest extends xc.mst.service.impl.test.MockHarvestTest {

    private static final Logger LOG = Logger.getLogger(MockHarvestTest.class);

    @Override
    public Format[] getIncomingFormats() throws Exception {
        return new Format[] { getMarc21Format() };
    }

    public Format getHarvestOutFormat() throws Exception {
        return getMarc21Format();
    }

    @Override
    public void finalTest() throws Exception {
    }

    @Override
    public void testHarvestOut() {
    }

    public void startToFinish() throws Exception {
    	// Allow tests to have their own overridden configuration settings
    	MetadataService mst = this.getMetadataService();
    	if (mst instanceof NormalizationService) {
    		String configFilename = INPUT_FOLDER + "/" + System.getenv("test.folder") + "/service.xccfg";
    		if ( (new File(configFilename)).exists()) {
    			((NormalizationService) mst).loadConfiguration(getUtil().slurp(configFilename));
    		}
    	}
    	super.startToFinish();
    }
}
