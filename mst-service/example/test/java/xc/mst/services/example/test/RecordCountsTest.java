/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.example.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;

import xc.mst.bo.provider.Format;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordCounts;

/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 * 
 */
public class RecordCountsTest extends MockHarvestTest {

    private static final Logger LOG = Logger.getLogger(RecordCountsTest.class);

    @Override
    public Format[] getIncomingFormats() throws Exception {
        return new Format[] { getFooBarFormat() };
    }

    public Format getHarvestOutFormat() throws Exception {
        return getFooBarFormat();
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("recordCounts");
        return fileStrs;
    }

    @Override
    public void incrementalHarvestTest(String lastOaiRequest) {
        RecordCounts providerMostRecentIncomingRecordCounts = getRecordCountsDAO().getMostRecentIncomingRecordCounts(getRepoName());
        RecordCounts exampleServiceMostRecentIncomingRecordCounts = getRecordCountsDAO().getMostRecentIncomingRecordCounts(getServiceName());
        RecordCounts exampleServiceMostRecentOutgoingRecordCounts = getRecordCountsDAO().getMostRecentOutgoingRecordCounts(getServiceName());

        RecordCounts providerTotalIncomingRecordCounts = getRecordCountsDAO().getTotalIncomingRecordCounts(getRepoName());
        RecordCounts exampleServiceTotalIncomingRecordCounts = getRecordCountsDAO().getTotalIncomingRecordCounts(getServiceName());
        RecordCounts exampleServiceTotalOutgoingRecordCounts = getRecordCountsDAO().getTotalOutgoingRecordCounts(getServiceName());

        LOG.debug(providerMostRecentIncomingRecordCounts.toString());
        LOG.debug(exampleServiceMostRecentIncomingRecordCounts.toString());
        LOG.debug(exampleServiceMostRecentOutgoingRecordCounts.toString());

        LOG.debug(providerTotalIncomingRecordCounts.toString());
        LOG.debug(exampleServiceTotalIncomingRecordCounts.toString());
        LOG.debug(exampleServiceTotalOutgoingRecordCounts.toString());

        if (lastOaiRequest.contains("010.xml")) {
            testCounts(new int[] { 7, 3, 1, 0, 1, 0, 0, 0, 0 }, providerMostRecentIncomingRecordCounts);
            testCounts(new int[] { 7, 3, 1, 0, 1, 0, 0, 0, 0 }, providerTotalIncomingRecordCounts);

            testCounts(new int[] { 7, 3, 0, 0, 0, 0, 0, 0, 1 }, exampleServiceMostRecentIncomingRecordCounts);
            testCounts(new int[] { 7, 3, 0, 0, 0, 0, 0, 0, 1 }, exampleServiceTotalIncomingRecordCounts);

            testCounts(new int[] { 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, exampleServiceMostRecentOutgoingRecordCounts);
            testCounts(new int[] { 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, exampleServiceTotalOutgoingRecordCounts);

        } else if (lastOaiRequest.contains("020.xml")) {
            testCounts(new int[] { 0, 0, 7, 3, 5, 2, 2, 1, 0 }, providerMostRecentIncomingRecordCounts);
            testCounts(new int[] { 7, 3, 8, 3, 6, 2, 2, 1, 0 }, providerTotalIncomingRecordCounts);

            testCounts(new int[] { 0, 0, 7, 3, 5, 2, 2, 1, 0 }, exampleServiceMostRecentIncomingRecordCounts);
            testCounts(new int[] { 7, 3, 7, 3, 5, 2, 2, 1, 1 }, exampleServiceTotalIncomingRecordCounts);

            testCounts(new int[] { 1, 0, 0, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, exampleServiceMostRecentOutgoingRecordCounts);
            testCounts(new int[] { 4, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, exampleServiceTotalOutgoingRecordCounts);
        }
    }

    @Override
    public void finalTest() throws Exception {
    }

    @Override
    public void testHarvestOut() {
    }

}
