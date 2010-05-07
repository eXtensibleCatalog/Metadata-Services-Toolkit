package xc.mst.common.test;

import org.testng.annotations.Test;

import xc.mst.manager.record.RecordService;
import xc.mst.utils.MSTConfiguration;

public class GenericTest extends BaseTest {
	
	@Test
	public void goTest() {
		
		try {
			RecordService rs = (RecordService)MSTConfiguration.getBean("RecordService");
			System.out.println("rs.getRecordDAO(): "+rs.getRecordDAO());
		
			/*
			List<URL> urls = new ArrayList<URL>();

			System.out.println("hello");
			File f = new File(".");
			URL url = f.toURI().toURL();
			System.out.println("url: "+url);
			//url = new URL(url.toString()+"/");
			//System.out.println("url: "+url);
			urls.add(url);
			
			URL[] urlsArr = urls.toArray(new URL[]{});
    		URLClassLoader loader = new URLClassLoader(urlsArr, getClass().getClassLoader());
    		
    		BufferedReader br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("build.properties")));
    		StringBuilder sb = new StringBuilder();
    		String line = null;
    		while ((line = br.readLine()) != null) {
    		sb.append(line + "\n");
    		}
    		br.close();
    		System.out.println(sb.toString());
    		*/
		} catch (Throwable t) {
			t.printStackTrace(System.out);
			throw new RuntimeException(t);
		}
	}

}
