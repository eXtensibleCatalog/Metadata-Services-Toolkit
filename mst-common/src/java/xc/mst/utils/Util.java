/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.utils;

import gnu.trove.TLongHashSet;
import gnu.trove.TLongProcedure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.core.io.ClassPathResource;

public class Util {
	
	public final static Logger LOG = Logger.getLogger(Util.class);
	protected static DateTimeFormatter UTC_SECOND_FORMATTER = null;
	protected static DateTimeFormatter UTC_DAY_FORMATTER = null;
	static {
		UTC_SECOND_FORMATTER = ISODateTimeFormat.dateTime();
		UTC_SECOND_FORMATTER = UTC_SECOND_FORMATTER.withZone(DateTimeZone.UTC);
		
		UTC_DAY_FORMATTER = ISODateTimeFormat.date();
		UTC_DAY_FORMATTER = UTC_DAY_FORMATTER.withZone(DateTimeZone.UTC);
	}
	public String printDateTime(Date d) {
		try {
			String s = UTC_SECOND_FORMATTER.print(d.getTime());
			s = s.substring(0, s.length()-5)+"Z";
			return s;
		} catch (Throwable t) {
			return null;
		}
	}
	
	public String printDate(Date d) {
		try {
			String s = UTC_DAY_FORMATTER.print(d.getTime());
			return s;
		} catch (Throwable t) {
			return null;
		}
	}
	
	public Date parseDateTime(String s) {
		try {
			DateTime dt = UTC_SECOND_FORMATTER.parseDateTime(s);
			Date d = dt.toDate();
			return d;
		} catch (Throwable t) {
			return null;
		}
	}
	
	public Date parseDate(String s) {
		try {
			DateTime dt = UTC_DAY_FORMATTER.parseDateTime(s);
			Date d = dt.toDate();
			return d;
		} catch (Throwable t) {
			return null;
		}
	}
	
	protected ThreadLocal<ClassLoader> currentClassLoader = new ThreadLocal<ClassLoader>();
	
	public static Util getUtil() {
		return (Util)MSTConfiguration.getInstance().getBean("Util");
	}
	
	public String getDBSchema(String name) {
		if (!name.startsWith(MSTConfiguration.getInstance().getProperty("dbRepoTablesPrefix")))
			return MSTConfiguration.getInstance().getProperty("dbRepoTablesPrefix")+normalizeName(name);
		else
			return name;
	}
	
	public String normalizeName(String name) {
		if (name == null) {
			return name;
		} else {
			name = name.replaceAll(" ", "_").toLowerCase();
			name = name.replaceAll("-", "_").toLowerCase();
			return name;
		}
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
		File f = new File(classpathResource);
		if (f.exists()) {
			return slurp(f);
		} else {
			return slurp(classpathResource, null);
		}
	}
	
	public String slurp(InputStream is) {
		try {
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
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
			LOG.debug(classpathResource+" does not exist");
		} catch (Throwable t) {
			LOG.error("", t);
		}
		return null;
	}
	
	public void spit(String fileName, String content) {
		File f = new File(fileName);
		if (f.exists()) {
			f.delete();
		}
		Writer fw = null;
		try {
			fw = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			fw.write(content);
			fw.close();
		} catch (Throwable t) {
			throwIt(t);
		}
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
	
	public String getString(TLongHashSet tlohm) {
		final StringBuilder sb = new StringBuilder();
		sb.append("total_size:");
		sb.append(tlohm.size());
		sb.append(" ");
		tlohm.forEach(new TLongProcedure() {
			public boolean execute(long value) {
				sb.append(value+", ");
				return true;
			}
		});
		return sb.toString();
	}
	
	protected List<String> redundantTokens = null;
	public String getNonRedundantOaiId(String oaiId) {
		if (redundantTokens == null) {
			redundantTokens = MSTConfiguration.getInstance().getPropertyAsList("harvest.redundantToken");
		}
		for (String redundantToken : redundantTokens) {
			oaiId = oaiId.replaceAll(redundantToken, "");	
		}
		return oaiId;
	}

}
