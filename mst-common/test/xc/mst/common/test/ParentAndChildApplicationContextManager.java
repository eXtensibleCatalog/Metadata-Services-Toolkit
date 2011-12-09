package xc.mst.common.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.Util;

public class ParentAndChildApplicationContextManager {

    protected static final Logger LOG = Logger.getLogger(ParentAndChildApplicationContextManager.class);

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
	public ClassPathXmlApplicationContext getParentAndChildApplicationContext(String serviceName,
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
                Util util = (Util) MSTConfiguration.getInstance(). getApplicationContext().getBean("Util");
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
}
