package xc.mst.services.marcaggregation.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeSuite;

import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

/**
 * Place for setting up the environment to be able to access MAS's Beans.
 * @author John Brand
 *
 */
public class MASBaseTest extends MockHarvestTest {

    private static final Logger LOG = Logger.getLogger(MASBaseTest.class);

    @Override
    @BeforeSuite
    public void startup() {
        final String sname = "marcaggregation";
        String serviceFolder = MSTConfiguration.getUrlPath() + "/services/" + sname;
        String metaInfFolderStr = serviceFolder + "/META-INF";
        applicationContext = setParentAndChildApplicationContext(sname, "xc/mst/services/spring-service.xml", metaInfFolderStr);

        // TODO get this working in a more generic way - tried below to do it via aggregation, ran into issues. Perhaps do it via
        // my original idea, had a superclass of MockHarvestTest that incorporates this (instead of aggregation, which is really
        // what I want as it is more portable.
        // ParentAndChildApplicationContextManager m= new ParentAndChildApplicationContextManager();
        // applicationContext = m.getParentAndChildApplicationContext(sname, "xc/mst/services/spring-service.xml", metaInfFolderStr);
        // ((ClassPathXmlApplicationContext)applicationContext).refresh();
        super.startup();
    }

    /**
     * By default the test framework ends up being part of the Parent/MST applicationContext for Spring.
     * But sometimes we need to access and test the services child beans. This allows us to access them.
     * Note that in this case, if we create a new applicationContext, we need to make sure its parent is
     * set correctly so we can access the parent beans. An alternative to that would be to do it the way
     * it is done for DefaultServicesService (it subclasses BaseService, thus is loaded as a bean by Spring).
     *
     * @see DefaultServicesService
     * @param serviceName
     *            - we are only setting up child applicationContext for services here in this method
     * @param xmlLocation
     *            - where the spring file is for the child application context.
     */
    public ClassPathXmlApplicationContext setParentAndChildApplicationContext(String serviceName,
            String xmlLocation, String metaInfFolderStr) {
        ClassPathXmlApplicationContext ac = null;
        try {
            // String serviceFolder = MSTConfiguration.getUrlPath() + "/services/" + serviceName;
            // String metaInfFolderStr = serviceFolder + "/META-INF";
            List<URL> urls = new ArrayList<URL>();
            File libFolder = new File(metaInfFolderStr + "/lib");
            String classesFolderStr = metaInfFolderStr + "/classes/";
            // C:\dev\xc\mst\svn\branches\marc_agg\mst-service\custom\MARCAggregation\build\MST-instances\MetadataServicesToolkit\services\marcaggregation\META-INF\lib
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
                ac.setParent(MSTConfiguration.getInstance().getApplicationContext());
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(
                            xmlLocation)));
                } catch (Throwable t) {
                    LOG.error("** Problem encountered reading " + xmlLocation + " **", t);
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
            getUtil().throwIt(t);
        }
        return ac;
    }

    protected List<String> getConfigFileValues(String name) {
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

    // http://stackoverflow.com/questions/367626/how-do-i-fix-the-expression-of-type-list-needs-unchecked-conversion
    // TODO move this UP to common utilities.
    protected static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
        List<T> r = new ArrayList<T>(c.size());
        for (Object o : c)
            r.add(clazz.cast(o));
        return r;
    }

    /*
     * debug stuff, leave it around for now to remind me of the issues I had...
     * System.out.println("*old config=* "+ getConfig().getInstance(). getApplicationContext().toString());
     * MSTConfiguration config = getConfig().getInstance();
     * try {
     * config.getApplicationContext().getBean("MetadataServiceMSTConfiguration");
     * System.out.println("TEST: in old ac MetadataServiceMSTConfiguration");
     *
     * } catch (NoSuchBeanDefinitionException nsbde) {
     * config.getApplicationContext().getBean("MSTConfiguration");
     * System.out.println("TEST: in old ac MSTConfiguration");
     * }
     * try {
     * applicationContext.getBean("MetadataServiceMSTConfiguration");
     * System.out.println("TEST: in new ac MetadataServiceMSTConfiguration");
     *
     * } catch (NoSuchBeanDefinitionException nsbde) {
     * applicationContext.getBean("MSTConfiguration");
     * System.out.println("TEST: in new ac MSTConfiguration");
     * }
     */

}