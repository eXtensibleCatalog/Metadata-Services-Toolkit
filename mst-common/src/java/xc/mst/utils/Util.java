package xc.mst.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

public class Util {
	
	public final static Logger LOG = Logger.getLogger(Util.class);
	
	protected ThreadLocal<ClassLoader> currentClassLoader = new ThreadLocal<ClassLoader>();
	
	public static Util getUtil() {
		return (Util)MSTConfiguration.getInstance().getBean("Util");
	}
	
	public String slurp(File file) {
		try {
			return slurp(new FileInputStream(file));
		} catch (FileNotFoundException t) {
			LOG.error(file.getAbsolutePath()+" does not exist");
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return null;
	}
	
	public String slurp(String classpathResource) {
		return slurp(classpathResource, null);
	}
	
	public String slurp(InputStream is) {
		try {
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			return sb.toString();
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return null;
	}
	
	public String slurp(String classpathResource, ClassLoader cl) {
		if (cl == null) {
			cl = getClass().getClassLoader();
		}
		try {
			return slurp(new ClassPathResource(classpathResource, cl).getInputStream());
		} catch (FileNotFoundException t) {
			LOG.error(classpathResource+" does not exist");
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return null;
	}
	
	public ClassLoader getClassLoader() {
		return currentClassLoader.get();
	}
	
	public void setClassLoader(ClassLoader cl) {
		currentClassLoader.set(cl);
	}
	
	public boolean arraysEqualInLength(String[]... arrs) {
		boolean anyNulls = false;
		boolean allNulls = true;
		int length = -1;
		for (int i=0; i<arrs.length; i++) {
			if (arrs[i] == null) {
				anyNulls = true;
			} else {
				allNulls = false;
				if (length == -1 || length == arrs[i].length) {
					length = arrs[i].length;
				} else {
					return false;
				}
			}
		}
		if (anyNulls && !allNulls) {
			return false;
		} else {
			return true;
		}
	}
	
	public void throwIt(Throwable t) {
		if (t instanceof RuntimeException) {
			throw (RuntimeException)t;
		} else {
			throw new RuntimeException(t);
		}
	}

}
