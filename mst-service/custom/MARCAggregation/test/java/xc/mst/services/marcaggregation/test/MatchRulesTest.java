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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeSuite;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;
import xc.mst.bo.record.SaxMarcXmlRecord;
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
        LOG.debug("MAS:  setup()");
       this.matcherMap = new HashMap<String, FieldMatcher>();
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

        List<String> mpStrs = getConfigFileValues("matchers.value");
        for (String mp : mpStrs) {
            final String n = mp + "Matcher";
            FieldMatcher m = (FieldMatcher) applicationContext.getBean(n);
            m.setName(n);
            matcherMap.put(mp, m);
            m.load();
        }
        this.matchRuleMap = new HashMap<String, MatchRuleIfc>();
        List<String> mrStrs = getConfigFileValues("match.rules.value");
        for (String mrStr : mrStrs) {
           MatchRuleIfc mr = (MatchRuleIfc) applicationContext.getBean(mrStr + "MatchRule");
           matchRuleMap.put(mrStr, mr);
        }
    }

    // http://stackoverflow.com/questions/367626/how-do-i-fix-the-expression-of-type-list-needs-unchecked-conversion
    // TODO move this UP to common utilities.
    private static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
        List<T> r = new ArrayList<T>(c.size());
        for (Object o : c)
            r.add(clazz.cast(o));
        return r;
    }

    private List<String> getConfigFileValues(String name) {
        try {
            // there is probably a more righteous way to grab the service name.
            final PropertiesConfiguration props = new PropertiesConfiguration(MSTConfiguration.getUrlPath() + "/services/" + getUtil().normalizeName("MARCAggregation") +
                    "/META-INF/classes/xc/mst/services/custom.properties");

            final List<String> values = castList(String.class, props.getList(name));
            return values;
        } catch (Exception e) {
            LOG.error("Error loading custom.properties for service: " + this.getServiceName(), e);
            return null;
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

            Set<Long> results = ensureMatch(providerRepo);
            LOG.info("ensureMatch results size ="+results.size());
//            if (!results.isEmpty()) throw new RuntimeException("FAILURE - expected NO results to be returned.");

//            results = ensureMatch(providerRepo);
//            LOG.info("ensureMatch results size ="+results.size());
//            if (results.isEmpty()) throw new RuntimeException("FAILURE - expected some results to be returned.");

            for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                FieldMatcher matcher = me.getValue();
                LOG.info("for matcher "+matcher.getName()+" it has "+matcher.getNumRecordIdsInMatcher()+" recordIds and "+matcher.getNumMatchPointsInMatcher()+ " match points.");
            }

            // the result is number of the 175 records that had 020 fields, result I got was 118, verify this is correct.
            // also note, this is really only testing the 1st matchrule and its matcher, perhaps unload that one, then run again. (2x), and so on, and so on.

            // TODO flush, then results should be empty

            // TODO load, then results should be 118


            // at this point, artificially add a record with known matches, verify you get them, flush, should be no matches, then load, should have the matches back.
            // , ideally harvest from a 2nd repo (that contains some matching records)?

        } catch (Throwable t) {
            LOG.error("Exception occured when running MarkProviderDeletedTest!", t);
            getUtil().throwIt(t);
        }
    }

    protected Set<Long> ensureMatch(Repository repo) throws Throwable {
        List<Record> records = repo.getRecords(new Date(0), new Date(), 0l, getMarc21Format(), null);
        Set<Long> overall = new HashSet<Long>();
        for (Record r : records) {
        	overall.addAll(  process((InputRecord)r)  );
        }
        LOG.info("* done *");
        return overall;
    }

    public Set<Long> process(InputRecord r) {
        Set<Long> matchedRecordIds = new HashSet<Long>();
        try {

            LOG.debug("test:  process record+"+r.getId());
            if (r.getStatus() != Record.DELETED) {
                SaxMarcXmlRecord smr = new SaxMarcXmlRecord(r.getOaiXml());
                smr.setRecordId(r.getId());

                MatchSet ms = new MatchSet(smr);
                for (Map.Entry<String, FieldMatcher> me : this.matcherMap.entrySet()) {
                    String matchPointKey = me.getKey();
                    FieldMatcher matcher = me.getValue();
                    matcher.addRecordToMatcher(smr);  // is this the place to do this?  (was originally missing)
                    ms.addMatcher(matchPointKey, matcher);
                }

                Set<Long> previouslyMatchedRecordIds = null;

                for (Map.Entry<String, MatchRuleIfc> me : this.matchRuleMap.entrySet()) {
                    String matchRuleKey = me.getKey();
                    MatchRuleIfc matchRule = me.getValue();
                    matchedRecordIds.addAll(matchRule.determineMatches(ms));
                }
            } /*else {
                if (r.getSuccessors().size() == 0) {
                    // NEW-DELETED
                } else {
                    // UPDATE-DELETED
                }
            }
                */

        } catch (Throwable t) {
            getUtil().throwIt(t);
        }
        return matchedRecordIds;
    }
}
