/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.services;

import java.util.List;
import java.util.Map;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.RegisteredData;
import xc.mst.bo.service.Service;
import xc.mst.constants.Status;
import xc.mst.repo.Repository;
import xc.mst.utils.MSTConfiguration;

public interface MetadataService {

    public void install();

    public void uninstall();

    public void update(String currentVersion);

    // public void process(Record r);

    public Repository getRepository();

    /**
     * This method not only creates new records (inserts), but it can
     * also update records and delete records.
     *
     * @param repo
     * @param inputFormat
     * @param inputSet
     * @param outputSet
     */
    public void setup();

    public void process(Repository repo, Format inputFormat, Set inputSet, Set outputSet);

    public void pause();

    public void resume();

    public void cancel();

    public void finish();

    public Service getService();

    public void setService(Service service);

    public String getServiceName();

    public String getMessage(int code, char type);

    public String getMessage(int code, char type, String[] args);

    public boolean isMessageEnabled(int code, char type);

    // stuff you want to end up displaying in browse records, to make searching for known interesting data easier.
    // will be if the format of '001', need to store the identifiers only, to be able to retrieve them later for browse records.
    // also need to store a human readable version that gets displayed in the UI.
    // fogbugz 828
    public void registerId(String readable, String identifier);

    // get the set of identifiers that the service has registered.  This is the actual indexed identifier + a human readable version
    // fogbugz 828
    public Map<String, String> getIdentifiers();

    // on a record by record basis SolrIndexService will ask a service for that records registered identifiers
    public List<RegisteredData> getRegisteredIdentifiers(InputRecord ri);


    // leftover methods that I think can be eventually deleted
    public void runService(int serviceId, int outputSetId);

    public void setStatus(Status status);

    public boolean sendReportEmail(String problem);

    public void setCanceled(boolean isCanceled);

    public void setPaused(boolean isPaused);

    public Status getServiceStatus();

    public int getProcessedRecordCount();

    public long getTotalRecordCount();

    public List<String> getUnprocessedErrorRecordIdentifiers();

    public void setUnprocessedErrorRecordIdentifiers(List<String> l);

    public MSTConfiguration getConfig();

    public MetadataServiceManager getMetadataServiceManager();

    public void setMetadataServiceManager(MetadataServiceManager msm);
}
