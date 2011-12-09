/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.services.marcaggregation.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeSuite;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.OutputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.common.test.ParentAndChildApplicationContextManager;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.repo.Repository;
import xc.mst.services.marcaggregation.matcher.FieldMatcher;
import xc.mst.services.marcaggregation.matcher.MatchSet;
import xc.mst.services.marcaggregation.matchrules.MatchRuleIfc;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;


public class MatchRulesTest extends MockHarvestTest {

  private static final Logger LOG = Logger.getLogger(MatchRulesTest.class);

    protected Map<String, FieldMatcher> matcherMap = null;
    protected Map<String, MatchRuleIfc> matchRuleMap = null;

    @Override
    @BeforeSuite
    public void startup() {
    	final String sname = "marcaggregation";
        String serviceFolder = MSTConfiguration.getUrlPath() + "/services/" + sname;
        String metaInfFolderStr = serviceFolder + "/META-INF";
        applicationContext = setParentAndChildApplicationContext(sname, "xc/mst/services/spring-service.xml", metaInfFolderStr);


        //TODO get this working in a more generic way - tried below to do it via aggregation, ran into issues.  Perhaps do it via
        //   my original idea, had a superclass of MockHarvestTest that incorporates this (instead of aggregation, which is really
        //   what I want as it is more portable.
    	//ParentAndChildApplicationContextManager m= new ParentAndChildApplicationContextManager();
        //applicationContext = m.getParentAndChildApplicationContext(sname, "xc/mst/services/spring-service.xml", metaInfFolderStr);
        //((ClassPathXmlApplicationContext)applicationContext).refresh();
        super.startup();
    }

    /**
     * By default the test framework ends up being part of the Parent/MST applicationContext for Spring.
     * But sometimes we need to access and test the services child beans.  This allows us to access them.
     * Note that in this case, if we create a new applicationContext, we need to make sure its parent is
     * set correctly so we can access the parent beans.  An alternative to that would be to do it the way
     * it is done for DefaultServicesService (it subclasses BaseService, thus is loaded as a bean by Spring).
     *
     * @see DefaultServicesService
     * @param serviceName - we are only setting up child applicationContext for services here in this method
     * @param xmlLocation - where the spring file is for the child application context.
     */
	public ClassPathXmlApplicationContext setParentAndChildApplicationContext(String serviceName,
			String xmlLocation, String metaInfFolderStr) {
        ClassPathXmlApplicationContext ac = null;
		try {
            //String serviceFolder = MSTConfiguration.getUrlPath() + "/services/" + serviceName;
            //String metaInfFolderStr = serviceFolder + "/META-INF";
            List<URL> urls = new ArrayList<URL>();
            File libFolder = new File(metaInfFolderStr + "/lib");
            String classesFolderStr = metaInfFolderStr + "/classes/";
            //C:\dev\xc\mst\svn\branches\marc_agg\mst-service\custom\MARCAggregation\build\MST-instances\MetadataServicesToolkit\services\marcaggregation\META-INF\lib
            if (libFolder != null) {
                if (libFolder.listFiles() != null) {
                    for (File f : libFolder.listFiles()) {
                        if (f.getName().endsWith(".jar")) {
                            urls.add(f.toURI().toURL());
                        }
                    }
                }
                File f = new File(classesFolderStr);
                URI uri = f.toURI();
                URL url = uri.toURL();
                url = new URL(url.toString() + "/");
                urls.add(url);
                URL[] urlsArr = urls.toArray(new URL[] {});
                URLClassLoader loader = new URLClassLoader(urlsArr, getClass().getClassLoader());
                ac = new ClassPathXmlApplicationContext();
                ac.setClassLoader(loader);
                ac.setConfigLocation(xmlLocation);
                ac.setParent(MSTConfiguration.getInstance(). getApplicationContext());
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(
                            xmlLocation)));
                } catch (Throwable t) {
                    LOG.error("** Problem encountered reading "+ xmlLocation+ " **", t);
                }
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Util util = (Util) getConfig().getBean("Util");
                util.setClassLoader(loader);
                // must call refresh when creating a new ac.
                ac.refresh();
                util.setClassLoader(null);

            }
        } catch (Throwable t) {
            LOG.error("** Problem encountered trying to setup applicationContext! ! **", t);
        }
        return ac;
	}

    public void setup() {
        this.matcherMap = new HashMap<String, FieldMatcher>();
        String[] mpStrs = new String[] {
                "Lccn",
                "ISBN",
                "ISSN",
                "SystemControlNumber",
                "x024a",
                "x028ab",
                "x130a",
                "x240a",
                "x260abc",
                "x245ah"};
        /*
         * debug stuff, leave it around for now to remind me of the issues I had...
    	System.out.println("*old config=* "+ getConfig().getInstance(). getApplicationContext().toString());
    	MSTConfiguration config = getConfig().getInstance();
        try {
            config.getApplicationContext().getBean("MetadataServiceMSTConfiguration");
            System.out.println("TEST: in old ac MetadataServiceMSTConfiguration");

        } catch (NoSuchBeanDefinitionException nsbde) {
            config.getApplicationContext().getBean("MSTConfiguration");
            System.out.println("TEST: in old ac MSTConfiguration");
        }
        try {
            applicationContext.getBean("MetadataServiceMSTConfiguration");
            System.out.println("TEST: in new ac MetadataServiceMSTConfiguration");

        } catch (NoSuchBeanDefinitionException nsbde) {
            applicationContext.getBean("MSTConfiguration");
            System.out.println("TEST: in new ac MSTConfiguration");
        }
         */

        for (String mp : mpStrs) {
            FieldMatcher m = (FieldMatcher) applicationContext.getBean(mp + "Matcher");
            matcherMap.put(mp, m);
            m.load();
        }
        this.matchRuleMap = new HashMap<String, MatchRuleIfc>();
        String[] mrStrs = new String[] {
                "Step1a",
//              "Step2a",
                "Step3a",
        };
        for (String mrStr : mrStrs) {
           MatchRuleIfc mr = (MatchRuleIfc) applicationContext.getBean(mrStr + "MatchRule");
           matchRuleMap.put(mrStr, mr);
        }
    }

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("demo_175");
        return fileStrs;
    }


    public void finalTest() {
    	setup();
        try {
            // These first 2 steps are done in MockHarvestTest
            // - harvest records into MST and run them through norm service

            System.out.println("****START MatchRulesTest *****");
            Repository providerRepo = getRepositoryService().getRepository(this.provider);
            ensureMatch(providerRepo);

            Repository serviceRepo = getServiceRepository();
//            ensureAllRecordsMatchStatus(serviceRepo, Record.ACTIVE);

            // - ensure there are scheduled harvests
            HarvestSchedule hs = getHarvestScheduleDAO().getHarvestScheduleForProvider(this.provider.getId());
            assert hs != null : "there should be a harvestSchedule for the provider";

//            getProviderService().markProviderDeleted(this.provider);
//            waitUntilFinished();

            // - ensure there are no harvest schedules
//            hs = getHarvestScheduleDAO().getHarvestScheduleForProvider(this.provider.getId());
//            assert hs == null : "there should be a harvestSchedule for the provider";

            // - harvest from provider and norm service
            // - make sure all records are deleted
//            ensureAllRecordsMatchStatus(providerRepo, Record.DELETED);
//            ensureAllRecordsMatchStatus(serviceRepo, Record.DELETED);

            // - create a new harvest schedule
            // clear out previous harvest
//            getProvider().setLastOaiRequest(null);
//            getProviderService().updateProvider(getProvider());
//            createHarvestSchedule(); // you'll end up with active records again...must be from beginning
//            waitUntilFinished();

            // - harvest from provider and norm service
            // - make sure all records are active again
            // - make sure the same ids are used
//            ensureAllRecordsMatchStatus(providerRepo, Record.ACTIVE);
//            ensureAllRecordsMatchStatus(serviceRepo, Record.ACTIVE);

        } catch (Throwable t) {
            LOG.error("Exception occured when running MarkProviderDeletedTest!", t);
            getUtil().throwIt(t);
        }
    }

    protected void ensureMatch(Repository repo) throws Throwable {
        List<Record> records = repo.getRecords(new Date(0), new Date(), 0l, getMarc21Format(), null);
        for (Record r : records) {
//            if (r.getStatus() != status) {
//                throw new RuntimeException("For repo: " + repo.getName() + ",record with id: " + r.getId() + " has status:" + r.getStatus() + " while expecting:" + status);
//            }
        	process((InputRecord)r);
        }
    }

    public List<OutputRecord> process(InputRecord r) {
        try {

            if (r.getStatus() != Record.DELETED) {
                SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());

                MatchSet ms = new MatchSet(smr);
                for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                    String matchPointKey = me.getKey();
                    FieldMatcher matcher = me.getValue();
                    ms.addMatcher(matchPointKey, matcher);
                }

                Set<Long> previouslyMatchedRecordIds = null;

                Set<Long> matchedRecordIds = new HashSet<Long>();
                for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
                    String matchRuleKey = me.getKey();
                    MatchRuleIfc matchRule = me.getValue();
                    matchedRecordIds.addAll(matchRule.determineMatches(ms));
                }

                if (r.getSuccessors().size() == 0) {
                    // NEW-ACTIVE

                } else {
                    // UPDATE-ACTIVE
                        // unmerge
                        /*
                        for (inputBibId : inputBibIds) {
                            customProcessQueue.push(inputBibId)
                        }
                        for (inputHoldingId : inputHoldingIds) {
                            customProcessQueue.push(inputHoldingId)
                        }
                        */
                }
            } else {
                if (r.getSuccessors().size() == 0) {
                    // NEW-DELETED
                } else {
                    // UPDATE-DELETED
                }
            }

        } catch (Throwable t) {
            getUtil().throwIt(t);
        }
        return null;
    }
}
