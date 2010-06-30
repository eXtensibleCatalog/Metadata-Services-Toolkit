/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.common.test;

import org.testng.annotations.Test;

import xc.mst.manager.record.RecordService;

public class GenericTest extends BaseTest {
	
	@Test
	public void goTest() {
		
		try {
			RecordService rs = (RecordService)getBean("RecordService");
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
