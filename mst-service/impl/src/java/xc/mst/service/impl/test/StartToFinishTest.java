/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.service.impl.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.processing.ProcessingDirective;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.Service;
import xc.mst.oai.Facade;
import xc.mst.oai.OaiRequestBean;
import xc.mst.repo.Repository;
import xc.mst.utils.MSTConfiguration;

/**
 * Tests example service by processing records from a repository through example service.
 *
 * @author Ben Anderson
 * @author Sharmila Ranaganathan
 */
public abstract class StartToFinishTest extends BaseMetadataServiceTest {

    private static final Logger LOG = Logger.getLogger(StartToFinishTest.class);
    protected boolean shouldValidateRepo = false;

    protected Provider provider = null;
    protected Repository repo = null;

    /** XML response of harvest out */
    protected String harvestOutResponse = null;

    protected long getNumberOfRecordsToHarvest() {return 50;}
    protected abstract String getRepoName();
    protected abstract String getProviderUrl();
    protected abstract Format[] getIncomingFormats() throws Exception;
    protected void testProvider() throws Exception {}
    protected abstract void finalTest() throws Exception;

    /**
     * Get format to harvest from MST
     */
    protected abstract Format getHarvestOutFormat() throws Exception;

    protected Repository getServiceRepository() throws Exception {
        return getServicesService().getServiceByName(getServiceName()).getMetadataService().getRepository();
    }

    /**
     * To test harvest out functionality
     */
    protected abstract void testHarvestOut();

    protected Repository getHarvestRepository() throws Exception {
        Repository r = (Repository)MSTConfiguration.getInstance().getBean("Repository");
        r.setName(getRepoName());
        return r;
    }

    protected String[] getPriorServices() {
        return new String[] {};
    }

    @Test
    public void startToFinish() throws Exception  {
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

        postFinalTestTests();
    }

    public void postFinalTestTests() throws Exception {
        harvestOutRecordsFromMST();
        LOG.debug("after harvestOutRecordsFromMST");

        testHarvestOut();
        LOG.debug("after testHarvestOut");
    }

    public void dropOldSchemas() {
        try {
            getRepositoryDAO().deleteSchema(getRepoName());
        } catch (Throwable t) {
        }
        try {
            // This is now being done in MetadataServiceSpecificTest
            //getRepositoryDAO().deleteSchema(getServiceName());
        } catch (Throwable t) {
        }
        for (String ps : getPriorServices()) {
            try {
                getRepositoryDAO().deleteSchema(ps);
            } catch (Throwable t) {
            }
        }
    }

    public void installProvider() throws Exception {
        if (getProvider() != null) {
            LOG.debug("provider already exists!!!");
            return;
        }
        System.setProperty("source.encoding", "UTF-8");

        provider = new Provider();

        provider.setName(getRepoName());
        LOG.debug("getRepoName(): "+getRepoName());
        provider.setDescription("Repository used in TestNG tests");
        provider.setOaiProviderUrl(getProviderUrl());
        provider.setCreatedAt(new java.util.Date());
        provider.setNumberOfRecordsToHarvest(getNumberOfRecordsToHarvest());
        try {
            getProviderService().insertProvider(provider);
        } catch (Exception e) {
            // adding this try/catch because ran a test where exception was thrown, nothing seen in log, hard to debug error.
            LOG.error("error trying to insert provider! "+e);
            throw new RuntimeException(e);
        }

        if (shouldValidateRepo)
            getValidateRepository().validate(provider.getId());

        repo = (Repository)MSTConfiguration.getInstance().getBean("Repository");
        repo.setName(provider.getName());

        testProvider();
    }

    public void installService() throws Exception {
        for (String ps : getPriorServices()) {
            if (getServicesService().getServiceByName(ps) != null) {
                continue;
            }
            getServicesService().addNewService(ps);
        }
        // This is now being done in MetadataServiceSpecificTest
        //getServicesService().addNewService(getServiceName());
    }

    protected void createProcessingRule(Service srcService, String fromRepo, String serviceName) throws Exception {
        if (fromRepo != null && getSetDAO().getBySetSpec(fromRepo) == null) {
            Set s = new Set();
            s.setDisplayName(fromRepo);
            s.setSetSpec(fromRepo);
            s.setIsProviderSet(false);
            s.setIsRecordSet(true);
            getSetDAO().insert(s);
        }

        Set s = getSetDAO().getBySetSpec(serviceName);
        if (s == null) {
            s = new Set();
            s.setDisplayName(serviceName);
            s.setSetSpec(serviceName);
            s.setIsProviderSet(false);
            s.setIsRecordSet(true);
            getSetDAO().insert(s);
        }

        Service service = getServicesService().getServiceByName(serviceName);
        ProcessingDirective pd = new ProcessingDirective();
        pd.setService(service);
        if (srcService != null)
            pd.setSourceService(srcService);
        else
            pd.setSourceProvider(provider);
        List<Format> formats = new ArrayList<Format>();
        for (Format f : getIncomingFormats()) {
            formats.add(f);
        }
        pd.setTriggeringFormats(formats);
        if (fromRepo != null) {
            List<Set> sets = new ArrayList<Set>();
            sets.add(getSetDAO().getBySetSpec(fromRepo));
            pd.setTriggeringSets(sets);
        }
        pd.setOutputSet(s);
        getProcessingDirectiveDAO().insert(pd);
    }

    public void configureProcessingRules() throws Exception {
        List<String> allServices2Run = new ArrayList<String>();
        for (String s : getPriorServices()) {
            allServices2Run.add(s);
        }
        allServices2Run.add(getServiceName());

        for (int i=0; i < allServices2Run.size(); i++) {
            if (i > 0) {
                createProcessingRule(getServicesService().getServiceByName(allServices2Run.get(i-1)),
                        allServices2Run.get(i-1), allServices2Run.get(i));
            } else {
                createProcessingRule(null, null, allServices2Run.get(i));
            }
        }
    }

    protected Provider getProvider() {
        try {
            if (provider == null) {
                provider = getProviderDAO().getByName(getRepoName());
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return provider;
    }

    public void createHarvestSchedule() throws Exception {
        createHarvestSchedule("Test Schedule");
    }

    public void createHarvestSchedule(String name) throws Exception {
        HarvestSchedule schedule = new HarvestSchedule();
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(new Date(System.currentTimeMillis()+2000));
        schedule.setScheduleName(name);
        schedule.setDayOfWeek(nowCal.get(Calendar.DAY_OF_WEEK));
        for (Format f : getIncomingFormats()) {
            schedule.addFormat(f);
        }

        LOG.debug("getSetSpec(): "+getSetSpec());
        if (getSetSpec() != null) {
            Set s = getSetService().getSetBySetSpec(getSetSpec());
            if (s == null) {
                s = new Set();
                s.setSetSpec(getSetSpec());
                s.setDisplayName(getSetSpec());
                getSetService().insertSet(s);
            }
            s = getSetService().getSetBySetSpec(getSetSpec());
            LOG.debug("s: "+s);
            schedule.addSet(s);
        }

        schedule.setHour(nowCal.get(Calendar.HOUR_OF_DAY));
        schedule.setId(111);
        schedule.setMinute(nowCal.get(Calendar.MINUTE));
        schedule.setProvider(getProvider());
        schedule.setRecurrence("Daily");
        schedule.setStartDate(java.sql.Date.valueOf("2009-05-01"));

        getScheduleService().insertSchedule(schedule);
    }

    public void updateHarvestSchedule() throws Exception {
        Calendar nowCal = Calendar.getInstance();
        HarvestSchedule schedule = getScheduleService().getScheduleById(1);
        schedule.setMinute(nowCal.get(Calendar.MINUTE));
        getScheduleService().updateSchedule(schedule);
    }

    /**
     * To test harvesting records from MST using OAI PMH
     *
     * @throws Exception
     */
    public void harvestOutRecordsFromMST() throws Exception {

        OaiRequestBean bean = new OaiRequestBean();

        // Set parameters on the bean based on the OAI request's parameters
        bean.setVerb("ListRecords");
        bean.setMetadataPrefix(getHarvestOutFormat().getName());

        Service service = getServicesService().getServiceByName(getServiceName());
        bean.setServiceId(service.getId());

        Facade facade = (Facade) MSTConfiguration.getInstance().getBean("Facade");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(facade.execute(bean));

        harvestOutResponse = stringBuilder.toString();

        LOG.debug("XML response");
        LOG.debug(harvestOutResponse);
    }

    /**
     * Get harvest out response
     *
     * @return
     */
    public String getHarvestOutResponse() {
        return harvestOutResponse;
    }
}

