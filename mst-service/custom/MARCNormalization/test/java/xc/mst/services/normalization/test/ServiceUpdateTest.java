package xc.mst.services.normalization.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.processing.Job;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.processingDirective.ServicesService;

/*
import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.Job;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.constants.Status;
import xc.mst.dao.DataException;
import xc.mst.harvester.HarvestManager;
import xc.mst.manager.BaseService;
import xc.mst.repo.DefaultRepository;
import xc.mst.repo.Repository;
import xc.mst.services.MetadataServiceManager;
import xc.mst.services.RepositoryDeletionManager;
import xc.mst.services.SolrWorkDelegate;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.TimingLogger;
*/

import java.io.File;
import java.util.Date;

import xc.mst.utils.MSTConfiguration;

public class ServiceUpdateTest extends xc.mst.services.normalization.test.StartToFinishTest {

    protected static final Logger LOG = Logger.getLogger(ServiceUpdateTest.class);

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("demo_175");
        return fileStrs;
    }

    // @Override
    // public String getProviderUrl() {
    // LOG.debug("**** getProviderUrl() SUBCLASS ");
    // return "http://128.151.244.137:8080/OAIToolkit_demo_175/oai-request.do";
    // }

    @Override
    @Test
    public void startToFinish() throws Exception {
        printClassPath();

        dropOldSchemas();
        LOG.info("after dropOldSchemas");
        installProvider();
        LOG.info("after installProvider");
        installService();
        LOG.info("after installService");

        configureProcessingRules();
        LOG.info("after configureProcessingRules");
        createHarvestSchedule();
        LOG.info("after createHarvestSchedule");

        waitUntilFinished();
        LOG.info("after waitUntilFinished");

        finalTest();
        LOG.info("after finalTest");

        postFinalTestTests();
        LOG.info("after postFinalTestTests");
    }

    public void finalTest() {
        try {

            System.out.println("****START ServiceUpdateTest *****");

            ServicesService servicesService = new DefaultServicesService();
            Service service = getServicesService().getServiceByName(getServiceName());

            // should just have written latest time into db at service install time.
            if (servicesService.doesServiceFileTimeNeedUpdate(service)) {
                throw new RuntimeException("ERROR:should just have written latest time into db at service install time.");
            }

            // find the config file and update the file time!
            File configFile = new File(MSTConfiguration.getUrlPath() + "/services/MARCNormalization/META-INF/classes/service.xccfg");

            if (configFile.exists()) {

                Date current = new Date();
                configFile.setLastModified(current.getTime());

                // now after a file was 'updated' service file time needs updating
                if (!servicesService.doesServiceFileTimeNeedUpdate(service)) {
                    throw new RuntimeException("ERROR:should NEED update, just touched a service file.");
                }
            } else {
                LOG.info("*** Could not find config file for test to modify!");
            }

            // we already proved that file time is/can be written correctly,
            // but let's do it again then kick off a reprocessing.
            servicesService.updateServiceLastModifiedTime(getServiceName(), service);
            if (servicesService.doesServiceFileTimeNeedUpdate(service)) {
                throw new RuntimeException("ERROR:should just have written latest time into db.");
            }

            // must attach the applicable processing directives where this service is the destination in the PD
            // then we will create one job for each PD found
            List<ProcessingDirective> procDirectives = getProcessingDirectiveDAO().getByDestinationServiceId(service.getId());
            if (procDirectives == null || procDirectives.size() < 1) {
                LOG.debug("*** Found a service with updated files, but has no applicable processingDirectives, no work to reprocess!");
            } else {
                try { // now must re-process
                    // this mimics the code in Scheduler to reprocess. Ideally it is broken into a method both can
                    // call, i.e. morph DefaultServicesService.reprocessService(Service) into this.
                    for (ProcessingDirective procDirective : procDirectives) {

                        Job job = new Job(service, 0, Constants.THREAD_SERVICE);
                        job.setOrder(getJobService().getMaxOrder() + 1);
                        job.setProcessingDirective(procDirective);
                        LOG.debug("Creating new job THREAD_SERVICE, processing directive= " + procDirective);
                        getJobService().insertJob(job);
                    }
                } catch (DatabaseConfigException dce) {
                    LOG.error("DatabaseConfig exception occured when ading jobs to database", dce);
                    throw new RuntimeException("DatabaseConfig exception occured when ading jobs to database", dce);
                }
            }
            if (getScheduler().getRunningJob() != null) {
                LOG.debug("scheduler.getRunningJob().getJobStatus(): " + getScheduler().getRunningJob().getJobStatus());
                LOG.debug("scheduler.getRunningJob().getJobName(): " + getScheduler().getRunningJob().getJobName());
                // hmm when I ran my test solr job was still running, so this test passed, but really I want to
                // see to it that THREAD_SERVICE job makes it into queue and runs.
                // That is a TBD.
            } else {
                throw new RuntimeException("ERROR:should now be a job running.");
            }

            waitUntilFinished();
            // in a way, returning from waitUntilFinished proved we successfully inserted/ran a job.
            LOG.info("after waitUntilFinished (AGAIN)");
        } catch (Throwable t) {
            LOG.error("Exception occured when running ServiceUpdateTest!", t);
            getUtil().throwIt(t);
        }
    }

    @Override
    public void postFinalTestTests() throws Exception {
        // super.postFinalTestTests();
    }

}
