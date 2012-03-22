/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.services;

import gnu.trove.TLongByteHashMap;
import gnu.trove.TLongHashSet;

import java.io.File;
import java.util.*;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.RecordCounts;
import xc.mst.bo.record.RecordIfc;
import xc.mst.bo.record.RecordMessage;
import xc.mst.bo.record.RegisteredData;
import xc.mst.bo.service.Service;
import xc.mst.bo.service.ServiceHarvest;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.dao.DataException;
import xc.mst.dao.MetadataServiceDAO;
import xc.mst.email.Emailer;
import xc.mst.repo.Repository;
import xc.mst.repo.TestRepository;
import xc.mst.utils.LogWriter;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;

/**
 * A copy of the MST is designed to interface with one or more Metadata Services
 * depending on how it's configured. There are several Metadata Services which
 * may be used, each one of which extends the MetadataService class. The
 * MetadataService class provides a common interface through which the MST can
 * invoke functionality on a Metadata Service.
 *
 * @author Benjamin D. Anderson
 */

public abstract class GenericMetadataService extends SolrMetadataService
        implements MetadataService, MetadataServiceExtras,
        ApplicationContextAware {

    protected static Logger LOG = Logger.getLogger(Constants.LOGGER_PROCESSING);

    protected ApplicationContext applicationContext = null;
    protected MetadataServiceDAO metadataServiceDAO = null;
    protected List<ProcessingDirective> processingDirectives = null;
    protected List<RecordMessage> messages2insert = new ArrayList<RecordMessage>();
    protected List<RecordMessage> messages2delete = new ArrayList<RecordMessage>();
    protected TLongHashSet messages2deleteByRecordId = new TLongHashSet();
    protected Emailer mailer = new Emailer();

    protected MetadataServiceManager metadataServiceManager = null;

    protected boolean preserveStatuses = true;
    protected TLongByteHashMap previousStatuses = new TLongByteHashMap();
    protected TLongByteHashMap tempPreviousStatuses = new TLongByteHashMap();

    /**
     * A list of services to run after this service's processing completes The
     * keys are the service IDs and the values are the IDs of the sets that
     * service's records should get added to
     */
    protected HashMap<Integer, Integer> servicesToRun = new HashMap<Integer, Integer>();

    protected boolean stopped = false;
    protected boolean paused = false;
    protected Semaphore running = new Semaphore(1);
    protected Set outputSet;
    protected List<String> unprocessedErrorRecordIdentifiers = new ArrayList<String>();

    protected long startTime = new Date().getTime();
    protected long endTime = 0;
    protected long timeDiff = 0;

    protected Repository repository = null;

    /**
     * A list of identifiers to add to the record currently being processed
     */
    protected LinkedHashMap<String, String> m_identifiers = new LinkedHashMap<String, String>();

    static {
        LOG.debug("GenericMetadataService class loaded!!!");
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public MetadataServiceManager getMetadataServiceManager() {
        return metadataServiceManager;
    }

    public void setMetadataServiceManager(
            MetadataServiceManager metadataServiceManager) {
        this.metadataServiceManager = metadataServiceManager;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public MetadataServiceDAO getMetadataServiceDAO() {
        return metadataServiceDAO;
    }

    public void setMetadataServiceDAO(MetadataServiceDAO metadataServiceDAO) {
        this.metadataServiceDAO = metadataServiceDAO;
    }

    public Set getOutputSet() {
        return outputSet;
    }

    public void setOutputSet(Set outputSet) {
        this.outputSet = outputSet;
    }

    public Repository getRepository() {
        return this.repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setInputRecordCount(int inputRecordCount) {
    }

    public void setup() {
    }

    public void cancel() {
        stopped = true;
        running.acquireUninterruptibly();
        running.release();
    }

    public void finish() {
        running.acquireUninterruptibly();
        running.release();
    }

    public void pause() {
        LOG.debug("pausing...");
        paused = true;
        running.acquireUninterruptibly();
        running.release();
        LOG.debug("paused.");
    }

    public void resume() {
        paused = false;
    }

    public void install() {
        try {
            getMetadataServiceDAO().executeServiceDBScripts(
                    config.getServicePath() + getServiceName()
                            + "/sql/install.sql");
            postInstall();
        } catch (Throwable t) {
            LOG.error("", t);
        }
    }

    public void postInstall() {
    }

    public void uninstall() {
        try {
            getMetadataServiceDAO().executeServiceDBScripts(
                    config.getServicePath() + getServiceName()
                            + "/sql/uninstall.sql");
            postInstall();
        } catch (Throwable t) {
            LOG.error("", t);
        }
        postUninstall();
    }

    public void postUninstall() {
    }

    public void update(String cvStr) {
        update(cvStr, getService().getVersion());
    }

    public void update(String pvStr, String cvStr) {
        List<String> fileNames = new ArrayList<String>();
        File dir = new File(config.getServicePath() + getServiceName()
                + "/sql/");
        for (String file : dir.list()) {
            if (file.contains("update.") && file.endsWith(".sql")) {
                fileNames.add(file);
            }
        }
        update(pvStr, cvStr, fileNames);
    }

    public void update(String pvStr, String cvStr, List<String> fileNames) {
        List<String> orderedFileNames2run = internalUpdate(pvStr, cvStr,
                fileNames);
        for (String fn : orderedFileNames2run) {
            try {
                getMetadataServiceDAO().executeServiceDBScripts(fn);
            } catch (Throwable t) {
                LOG.error("", t);
            }
        }

        postUpdate();
    }

    public List<String> internalUpdate(String pvStr, String cvStr,
            List<String> fileNames) {
        List<Integer> pvs = getSubversions(pvStr);
        List<Integer> cvs = getSubversions(cvStr);

        List<List<Integer>> fileVersions = new ArrayList<List<Integer>>();

        String update = "update.";
        for (String file : fileNames) {
            int idx = file.indexOf(update);
            String fileVers = file.substring(idx + update.length());
            fileVers = fileVers.substring(0, fileVers.length() - 4);
            fileVersions.add(getSubversions(fileVers));
        }

        int mostSubVersions = 0;
        List<List<Integer>> allSubVersions = new ArrayList<List<Integer>>();
        allSubVersions.addAll(fileVersions);
        allSubVersions.add(pvs);
        allSubVersions.add(cvs);
        for (List<Integer> v : allSubVersions) {
            if (v.size() > mostSubVersions) {
                mostSubVersions = v.size();
            }
        }
        for (List<Integer> v : allSubVersions) {
            while (mostSubVersions != v.size()) {
                v.add(0);
            }
        }

        List<List<Integer>> updates2run = new ArrayList<List<Integer>>();
        for (int i = 0; i < fileVersions.size(); i++) {
            List<Integer> fv = fileVersions.get(i);
            boolean greaterThanPrevious = false;
            boolean lessThanCurrent = false;
            for (int j = 0; j < fv.size(); j++) {
                Integer fsv = fv.get(j);
                Integer csv = cvs.get(j);
                Integer psv = pvs.get(j);

                boolean isFinalSubversion = j + 1 == fv.size();
                boolean stillValid = false;
                if (fsv > psv) {
                    greaterThanPrevious = true;
                }
                if (fsv < csv) {
                    lessThanCurrent = true;
                }
                if (!isFinalSubversion && (fsv >= psv || greaterThanPrevious)
                        && (fsv <= csv || lessThanCurrent)) {
                    stillValid = true;
                }
                if (isFinalSubversion && (fsv > psv || greaterThanPrevious)
                        && (fsv <= csv || lessThanCurrent)) {
                    stillValid = true;
                }

                if (!stillValid) {
                    break;
                }
                if (stillValid && isFinalSubversion) {
                    updates2run.add(fv);
                }
            }
        }

        List<String> orderedFileNames2run = new ArrayList<String>();
        while (updates2run.size() > 0) {
            int minVersionIdx = 0;
            for (int i = 1; i < updates2run.size(); i++) {
                List<Integer> isv = updates2run.get(i);
                List<Integer> msv = updates2run.get(minVersionIdx);
                boolean newMin = true;
                for (int j = 0; j < isv.size(); j++) {
                    if (msv.get(j) < isv.get(j)) {
                        newMin = false;
                        break;
                    } else if (isv.get(j) < msv.get(j)) {
                        newMin = true;
                        break;
                    }
                }
                if (newMin) {
                    minVersionIdx = i;
                }
            }
            int i = 0;
            SortedMap<Integer, String> files = new TreeMap<Integer, String>();
            List<Integer> minVers = updates2run.get(minVersionIdx);
            for (List<Integer> fv : fileVersions) {
                if (fv.equals(minVers)) {
                    String fileName = fileNames.get(i);
                    files.put(fileName.length(), fileName);
                    updates2run.remove(minVers);
                }
                i++;
            }
            for (String filename : files.values()) {
                orderedFileNames2run.add(filename);
            }
        }

        return orderedFileNames2run;

    }

    protected List<Integer> getSubversions(String s) {
        List<Integer> versions = new ArrayList<Integer>();
        for (String v : s.split("\\.")) {
            versions.add(Integer.parseInt(v));
        }
        return versions;
    }

    public void postUpdate() {
    }

    /**
     *
     *
     * @param r
     *            <b>note: modifying input objects has no effect on the
     *            system</b>
     *            <ul>
     *            <li>
     *            r.status
     *            <ul>
     *            <li>
     *            If this InputRecord has been deleted, then the status will be Record.DELETED. Otherwise it will be Record.ACTIVE.</li>
     *            </ul>
     *            </li>
     *            <li>
     *            r.successors
     *            <ul>
     *            <li>
     *            If this InputRecord has been processed before (determined by the oai-id), then the record will have successor Records attached to it. The only data attached to these records is the id. The content (xml) is not attached. If implementers find it necessary to have this, we may provide an optional way to get that content.</li>
     *            </ul>
     *            </li>
     *            <li>
     *            r.successors.predecessors
     *            <ul>
     *            <li>
     *            If this InputRecord has successors associated with it, then the predecessors of the successors will also be attached. As with InputRecord.successors, these predecessor records only have the id associated with them. For a typical one-to-one service, this data is somewhat redundant. But for more complex services in which a Record may have more than one predecessor, it becomes necessary.</li>
     *            </ul>
     *            </li>
     *            </ul>
     * @return The process method returns a list of OutputRecords that are
     *         inserted, updated, or deleted as a result of processing this
     *         InputRecord. The below attributes are in the context of that list
     *         of Records returned by the process method. In order to create a
     *         new record, use this helper method
     *         getRecordService().createRecord(). If you wish to overwrite an
     *         already existing record (ie it was attached to the InputRecord as
     *         a successor), simply set the xml and add it to the returned list.
     *         <b>note: any inserts, updates, and deletes must be added to the
     *         returned list otherwise they won't be recorded</b>
     *         <ul>
     *         <li>
     *         OutputRecord.id
     *         <ul>
     *         <li>
     *         If a record already exists with this id, then that record will be overwritten with the new contents. If this id is left blank, then the MST will assign it a new id.</li>
     *         </ul>
     *         </li>
     *         <li>
     *         OutputRecord.status
     *         <ul>
     *         <li>
     *         Record.ACTIVE (default) - Record will be made available for oai-pmh harvesting once persisted.</li>
     *         <li>
     *         Record.HELD - Record will be persisted and will await further notice to be made active. These records are not included in oai-pmh responses.</li>
     *         <li>
     *         Record.DELETED - Record will be marked as deleted. These records are included in oai-pmh responses.</li>
     *         </ul>
     *         </li>
     *         </ul>
     *
     * @see xc.mst.bo.record.RecordIfc
     * @see xc.mst.bo.record.InputRecord
     * @see xc.mst.bo.record.OutputRecord
     */
    public abstract List<OutputRecord> process(InputRecord r);

    protected ServiceHarvest getServiceHarvest(Format inputFormat,
            xc.mst.bo.provider.Set inputSet, String repoName, Service service) {
        LOG.debug("inputFormat: " + inputFormat);
        LOG.debug("inputSet: " + inputSet);
        LOG.debug("repoName: " + repoName);
        LOG.debug("service.getId(): " + service.getId());
        ServiceHarvest sh = getServiceDAO().getServiceHarvest(inputFormat,
                inputSet, repoName, getService());
        if (sh == null) {
            sh = new ServiceHarvest();
            sh.setFormat(inputFormat);
            sh.setRepoName(repoName);
            sh.setSet(inputSet);
            sh.setService(getServiceDAO().getService(getService().getId()));
        }
        if (sh.getHighestId() == null) {
            LOG.debug("sh.getHighestId(): " + sh.getHighestId());
            if (sh.getUntil() != null) {
                sh.setFrom(sh.getUntil());
            } else {
                sh.setFrom(new Date(System.currentTimeMillis()
                        - (1000l * 60 * 60 * 24 * 365 * 50)));
            }
            sh.setUntil(new Date());
            LOG.debug("sh.getUntil(): " + sh.getUntil());
            LOG.debug("sh.getFrom(): " + sh.getFrom());
        } else {
            if (sh.getUntil() == null || sh.getFrom() == null) {
                throw new RuntimeException("bogus data in service_harvests");
            }
        }
        getServiceDAO().persist(sh);
        LOG.debug("sh.getId(): " + sh.getId());
        return sh;
    }

    protected List<Record> getRecords(Repository repo, ServiceHarvest sh,
            Format inputFormat, Set inputSet) {
        return repo.getRecords(sh.getFrom(), sh.getUntil(), sh.getHighestId(),
                inputFormat, inputSet);
    }

    protected boolean commitIfNecessary(boolean force, long processedRecordCount) {
        if (!isSolrIndexer()) {
            if (getRepository().commitIfNecessary(force, processedRecordCount,
                    getMetadataServiceManager() == null ? null : getMetadataServiceManager().getIncomingRecordCounts(),
                    getMetadataServiceManager() == null ? null : getMetadataServiceManager().getOutgoingRecordCounts())) {

                if (!isTestRepository()) {
                    getMetadataServiceManager().getIncomingRecordCounts().clear();
                    getMetadataServiceManager().getOutgoingRecordCounts().clear();
                }

                // the below change was recommended by Kyushu developers, GC issue 316
                getRepositoryDAO().persistPreviousStatuses(getRepository().getName(), tempPreviousStatuses);
                tempPreviousStatuses.clear();

                getMessageDAO().deleteMessagesByRecordId(getService().getId(),
                        messages2deleteByRecordId);
                messages2deleteByRecordId.clear();
                getMessageDAO().deleteMessages(messages2delete);
                messages2delete.clear();

                LOG.debug("getMessageDAO().persistMessages(messages2insert);");
                LOG.debug("messages2insert.size(): " + messages2insert.size());
                getMessageDAO().persistMessages(messages2insert);
                messages2insert.clear();

                try {
                    getServiceDAO().update(service);
                } catch (DataException de) {
                    LOG.error("Exception occured while updating the service",
                            de);
                }
                return true;
            }
        }
        return force;
    }

    public void process(Repository repo, Format inputFormat, Set inputSet,
            Set outputSet) {
        startTime = new Date().getTime();
        processedRecordCount = 0;
        processStatusDisplay(repo, inputFormat, inputSet, outputSet);
        running.acquireUninterruptibly();

        if (!isSolrIndexer() && preserveStatuses) {
            previousStatuses.clear();
            previousStatuses.ensureCapacity(repo.getSize());
            LOG.debug("previousStatuses.ensureCapacity(" + repo.getSize()
                    + ");");
            tempPreviousStatuses.ensureCapacity(MSTConfiguration.getInstance()
                    .getPropertyAsInt("db.insertsAtOnce", 10000));
            getRepositoryDAO().populatePreviousStatuses(
                    getRepository().getName(), previousStatuses, true);
        }

        LOG.debug("gettingServiceHarvest");
        ServiceHarvest sh = getServiceHarvest(inputFormat, inputSet,
                repo.getName(), getService());
        // this.totalRecordCount = repo.getRecordCount(sh.getFrom(),
        // sh.getUntil(), inputFormat, inputSet);
        LOG.debug("sh: " + sh);
        this.totalRecordCount = repo.getRecordCount(sh.getFrom(),
                sh.getUntil(), inputFormat, inputSet);
        List<Record> records = getRecords(repo, sh, inputFormat, inputSet);

        if (getMetadataServiceManager() != null) {
            getMetadataServiceManager().setIncomingRecordCounts(
                    new RecordCounts(sh.getUntil(), RecordCounts.INCOMING));
            getMetadataServiceManager().setOutgoingRecordCounts(
                    new RecordCounts(sh.getUntil(), RecordCounts.OUTGOING));
        }

        int getRecordLoops = 0;
        boolean previouslyPaused = false;
        boolean atLeastOneRecordProcessed = false;
        while (records != null && records.size() > 0 && !stopped) {
            atLeastOneRecordProcessed = true;
            if (paused) {
                previouslyPaused = true;
                running.release();
                try {
                    Thread.sleep(1000);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
                continue;
            }
            if (previouslyPaused) {
                running.acquireUninterruptibly();
                previouslyPaused = false;
            }
            if (++getRecordLoops % 100 == 0) {
                // TODO here is the place to display performance!
                // processedRecordCount is a sensible count to pass here as the number of records updated.
                TimingLogger.reset(processedRecordCount);
            }
            //TODO here is the code to break out!!!!!
            for (Record in : records) {
            /*
            for (int i=0; i<records.size();) {
                Record in = customProcessQueue.pop();
                if (in == null) {
                    in = records.get(i);
                    i++;
                }
                */
                //START, intro stuff before processing


                // TODO: currently the injected records here only contain ids.
                // This is helpful enough if you simply want to overwrite the
                // the existing record. Although I can't think of a reason
                // why, someone might also want the xml with these injected
                // records.
                // We may want to supply an optional way of doing that.
                in.setPreviousStatus(Record.NULL);
                injectKnownData(in);
                if (in.getMessages() != null) {
                    for (RecordMessage rm : in.getMessages()) {
                        if (rm.getServiceId() == getService().getId()) {
                            messages2delete.add(rm.clone());
                        }
                    }
                }
                if (preserveStatuses) {
                    if (!isSolrIndexer()) {
                        previousStatuses.put(in.getId(), (byte) in.getStatus());
                    }
                    tempPreviousStatuses.put(in.getId(), (byte) in.getStatus());
                }
                Map<Long, OutputRecord> origSuccessorMap = new HashMap<Long, OutputRecord>();
                if (in.getSuccessors() != null && in.getSuccessors().size() > 0) {
                    for (OutputRecord or : in.getSuccessors()) {
                        origSuccessorMap.put(or.getId(), or.clone());
                    }
                }
                //END, intro stuff before processing

                // START, real processing!
                TimingLogger.start(getServiceName() + ".process");
                List<OutputRecord> out = null;
                boolean unexpectedError = false;
                try {
                    out = process(in);
                } catch (Throwable t) {
                    unexpectedError = true;
                    LOG.error("error processing record w/ id: " + in.getId(), t);
                }
                TimingLogger.stop(getServiceName() + ".process");
                // END, real processing!


                // START, post-processing record count handling!
                if (!isSolrIndexer() && !isTestRepository()) {
                    if (in.getType() != null) {
                        getMetadataServiceManager().getIncomingRecordCounts()
                                .incr(in.getType(), in.getStatus(),
                                        in.getPreviousStatus());
                    } else {
                        getMetadataServiceManager().getIncomingRecordCounts()
                                .incr(RecordCounts.OTHER, in.getStatus(),
                                        in.getPreviousStatus());
                    }
                    getMetadataServiceManager().getIncomingRecordCounts().incr(
                            null, in.getStatus(), in.getPreviousStatus());
                }

                if (unexpectedError) {
                    if (!isSolrIndexer() && !isTestRepository()) {
                        if (in.getType() != null) {
                            getMetadataServiceManager().getIncomingRecordCounts()
                                    .incr(in.getType(),
                                            RecordCounts.UNEXPECTED_ERROR);
                        }
                        getMetadataServiceManager().getIncomingRecordCounts().incr(
                                null, RecordCounts.UNEXPECTED_ERROR);
                    }
                } else {
                    processedRecordCount++;
                    if (out != null) {
                        for (RecordIfc rout : out) {
                            messages2deleteByRecordId.add(rout.getId());
                            Record rout2 = (Record) rout;
                            if (origSuccessorMap.containsKey(rout2.getId())) {
                                rout2.setPreviousStatus(origSuccessorMap.get(
                                        rout2.getId()).getStatus());
                            }
                            LOG.debug("rout2.getIndexedObjectType(): "
                                    + rout2.getType() + " rout2.getStatus(): "
                                    + rout2.getStatus()
                                    + " rout2.getPreviousStatus(): "
                                    + rout2.getPreviousStatus());
                            if (!isTestRepository()) {
                                if (rout2.getType() != null) {
                                    getMetadataServiceManager()
                                            .getOutgoingRecordCounts().incr(
                                                    rout2.getType(),
                                                    rout2.getStatus(),
                                                    rout2.getPreviousStatus());
                                } else {
                                    getMetadataServiceManager()
                                            .getOutgoingRecordCounts().incr(
                                                    RecordCounts.OTHER,
                                                    rout2.getStatus(),
                                                    rout2.getPreviousStatus());
                                }
                                getMetadataServiceManager()
                                        .getOutgoingRecordCounts().incr(null,
                                                rout2.getStatus(),
                                                rout2.getPreviousStatus());
                            }
                            rout2.addPredecessor(in);
                            rout2.setService(getService());
                            if (rout2.getId() == -1) {
                                getRepositoryDAO().injectId(rout2);
                            }
                            if (outputSet != null) {
                                rout2.addSet(outputSet);
                            }
                            getRepository().addRecord(rout2);
                        }
                    }
                }
                // END, post-processing record count handling!

                sh.setHighestId(in.getId());
                updateService(out, sh);

                // TODO not inserting errors on input record.
                // Update the error message on incoming record
                // repo.addRecord(in);
            }
            //TODO end , code to break out!!!

            if (commitIfNecessary(false, processedRecordCount)) {
                getServiceDAO().persist(sh);
            }

            this.totalRecordCount = repo.getRecordCount(sh.getFrom(),
                    sh.getUntil(), inputFormat, inputSet);
            records = getRecords(repo, sh, inputFormat, inputSet);
        }
        // TODO not inserting errors on input record.

        if (atLeastOneRecordProcessed) {
            commitIfNecessary(true, processedRecordCount);
        }
        if (!stopped) {
            sh.setHighestId(null);
            getServiceDAO().persist(sh);
        }
        if (atLeastOneRecordProcessed) {
            if (!isSolrIndexer())
                getRepository().processComplete();
        }
        if (!previouslyPaused) {
            running.release();
        }

        if (!isSolrIndexer() && !isTestRepository()) {
            RecordCounts mostRecentIncomingRecordCounts = getRecordCountsDAO()
                    .getMostRecentIncomingRecordCounts(
                            getRepository().getName());
            // I'm subtracting 1s from startTime because they might actually be
            // equal by the second
            try {
                if (mostRecentIncomingRecordCounts == null) {
                    LOG.error("*** GenericMetadataService.process: mostRecentIncomingRecordCounts == null!");
                    LogWriter
                            .addInfo(
                                    service.getServicesLogFileName(),
                                    "GenericMetadataService-unable to print record counts, null mostRecentIncomingRecordCounts!");
                } else if (mostRecentIncomingRecordCounts.getHarvestStartDate() == null) {
                    LOG.error("*** GenericMetadataService.process: mostRecentIncomingRecordCounts.getHarvestStartDate() == null!");
                    LogWriter
                            .addInfo(
                                    service.getServicesLogFileName(),
                                    "GenericMetadataService-unable to print record counts, null harvest start date!");
                } /*else if (mostRecentIncomingRecordCounts.getHarvestStartDate()
                        .getTime() >= (startTime - 1000)) {
                    processServiceRecordCounts(mostRecentIncomingRecordCounts);
                  } */else if (atLeastOneRecordProcessed) {
                    processServiceRecordCounts(mostRecentIncomingRecordCounts);
                }
            } catch (Throwable t) {
                LOG.error("*** HarvestManager.finishInner: exception " + t);
                LogWriter
                        .addInfo(service.getServicesLogFileName(),
                                "GenericMetadataService-unable to print record counts, unexpected exception!");
            }
        }
        setStatus(Status.NOT_RUNNING);
    }

    protected void processStatusDisplay(Repository repo, Format inputFormat, Set inputSet, Set outputSet) {
        setStatus(Status.RUNNING);
        LOG.info("getClass(): " + getClass());
        LOG.info("inputFormat: " + inputFormat);
        LOG.info("inputSet: " + inputSet);
        LOG.info("outputSet: " + outputSet);
        LOG.info(getClass().getName() + ".process(" + repo.getName() + ", "
                + (inputFormat == null ? "null" : inputFormat.getName())
                + ", "
                + (inputSet == null ? "null" : inputSet.getDisplayName())
                + ", "
                + (outputSet == null ? "null" : outputSet.getDisplayName())
                + ")");
    }

    protected void processServiceRecordCounts(RecordCounts mostRecentIncomingRecordCounts) {
        for (RecordCounts rc : new RecordCounts[] {
                mostRecentIncomingRecordCounts,
                getRecordCountsDAO().getTotalIncomingRecordCounts(
                        getRepository().getName()),
                getRecordCountsDAO()
                        .getMostRecentOutgoingRecordCounts(
                                getRepository().getName()),
                getRecordCountsDAO().getTotalOutgoingRecordCounts(
                        getRepository().getName()) }) {
            LogWriter.addInfo(service.getServicesLogFileName(),
                    rc.toString(getRepository().getName()));
        }
        LogWriter.addInfo(service.getServicesLogFileName(),
                getRepository().getRecordStatsByType());
        applyRulesToRecordCounts(mostRecentIncomingRecordCounts);
    }

    protected void applyRulesToRecordCounts(RecordCounts mostRecentIncomingRecordCounts) {
    }

    protected boolean isSolrIndexer() {
        boolean ret = getRepository() == null;
        LOG.debug("ret: " + ret);
        return ret;
    }

    protected boolean isTestRepository() {
        return getRepository() != null && getRepository() instanceof TestRepository;
    }

    protected void updateService(List<OutputRecord> outputRecords,
            ServiceHarvest sh) {
        if (!isSolrIndexer() && !(getRepository() instanceof TestRepository)) {
            // Set number of input and output records.
            LOG.debug("service.getName(): " + service.getName());
            // LOG.debug("records.size(): "+inputRecords.size());
            LOG.debug("service.getInputRecordCount(): "
                    + service.getInputRecordCount());
            // LOG.debug("records.size(): "+inputRecords.size());
            LOG.debug("getRepository().getSize(): " + getRepository().getSize());
            service.setInputRecordCount(service.getInputRecordCount() + 1);
            if (outputRecords != null)
                service.setOutputRecordCount(service.getOutputRecordCount()
                        + outputRecords.size());

            // TODO : currently # of output records and
            // HarvestOutRecordsAvailable are same. So we can get rid of one of
            // the fields in Services.
            // TODO : Should # of harvest out records available include deleted
            // records too?
            service.setHarvestOutRecordsAvailable(service
                    .getOutputRecordCount());
        }
    }

    protected void injectKnownData(Record in) {
        if (preserveStatuses && previousStatuses.contains(in.getId())) {
            TimingLogger.start("injectKnownData");
            if (!isSolrIndexer()) {
                TimingLogger.start("injectSuccessorIds");
                getRepository().injectSuccessorIds(in);
                TimingLogger.stop("injectSuccessorIds");
            }
            in.setPreviousStatus((char) previousStatuses.get(in.getId()));
            TimingLogger.stop("injectKnownData");
        }
    }

    public MSTConfiguration getConfig() {
        return config;
    }

    //addMessage but note, attach to OutputRecord, unused?
    protected void addMessage(OutputRecord record, int code, char level) {
        addMessage(record, code, level, null);
    }

    //addMessage but note, attach to OutputRecord, unused?
    protected void addMessage(OutputRecord record, int code, char level,
            String detail) {
        Record r = (Record) record;
        RecordMessage rm = new RecordMessage();
        getMessageDAO().injectId(rm);
        rm.setServiceId(getService().getId());
        rm.setInputRecord(false);
        rm.setCode(code);
        rm.setLevel(level);
        rm.setDetail(detail);
        rm.setRecord(r);

        messages2insert.add(rm);
    }

    protected void addMessage(InputRecord record, int code, char level, String detail, Service service) {
        if (!isMessageEnabled(code, level)) {
            LOG.debug("Will not addMessage, because the message is disabled. level=" + level + " code=" + code);
            return;
        }
        Record r = (Record) record;
        RecordMessage rm = new RecordMessage();
        getMessageDAO().injectId(rm);
        rm.setServiceId(service.getId());
        rm.setInputRecord(true);
        rm.setCode(code);
        rm.setLevel(level);
        rm.setDetail(detail);
        rm.setRecord(r);

        messages2insert.add(rm);
    }

    protected void addMessage(InputRecord record, int code, char level) {
        addMessage(record, code, level, null);
    }

    protected void addMessage(InputRecord record, int code, char level,
            String detail) {
        addMessage(record, code, level, detail, getService());
    }

    public String getMessage(int code, char type) {
        return getMessage(code, type, new String[] {});
    }

    public String getMessage(int code, char type, String[] args) {
        // don't use type as part of message retrieval...yet.
        // also, don't use args as part of message retrieval yet (need to debug it more) but:
        //    think it is useful to be able to build messages with variable parts included in them,
        //    that is what the args are for.

        final String prop = "error." + code + ".text";
        String s;
/*
        if (args.length > 0) {
            s= MSTConfiguration.getMSTString(prop, args);   //TODO problem with this method.  Returns null?
        }

        if (s == null) {
            LOG.error("ERROR with getMessage, code=" + code + " type=" + type + " config type details: " + config.getClass().getName() + " toStr: " + config.toString());
        } else {
            LOG.debug("INFO with getMessage, code=" + code + " type=" + type + " config type details: " + config.getClass().getName() + " toStr: " + config.toString());
        }
*/
        s= config.getProperty(prop);
        return s;
    }

    // look for an entry like error.852.enabled=true
    // if not found, return false, if found, parse for true/false
    public boolean isMessageEnabled(int code, char type) {
        // don't use type as part of message retrieval...yet.
        if (type == RecordMessage.ERROR) { // default to enabled for error messages.
            return config.getPropertyAsBoolean("error." + code + "enabled", true);
        }
        return config.getPropertyAsBoolean("error." + code + "enabled", false);
    }

    // stuff you want to end up displaying in browse records, to make searching for known interesting data easier.
    // fogbugz 828
    public void registerId(String readable, String identifier) {
        m_identifiers.put(readable, identifier);
    }
    public LinkedHashMap<String, String> getIdentifiers() {
        return m_identifiers;
    }
    // For now, SolrIndexService overrides this only.  But leave at this level to give option later for services to provide this
    // information directly.
    public List<RegisteredData> getRegisteredIdentifiers(InputRecord ri) {
        return new ArrayList<RegisteredData> ();
    }

}
