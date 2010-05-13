package xc.mst.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

public class Util {
	
	public final static Logger LOG = Logger.getLogger(Util.class);
	
	public String slurp(String classpathResource) {
		try {
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(new ClassPathResource(classpathResource).getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			return sb.toString();
		} catch (Throwable t) {
			LOG.error("", t);
			return null;
		}
	}

}
