package xc.mst.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class AutoServiceReprocessingFileFilter implements FileFilter {

	final String filter = MSTConfiguration.getInstance().getProperty("regexpOfFilesToScanForAutoServiceReprocessing");
	private static final Logger LOG = Logger.getLogger(AutoServiceReprocessingFileFilter.class);

	@Override
	/*
	 * Given a list of acceptable file patterns (applicable as a regex) like "*.properties, *.class"
	 * return whether the file matches one of the patterns.
	 * Also, because this ends up being a recursive directory search let all files of type directory through.
	 */
	public boolean accept(File pathname) {
		// fancy way but you'd still have to loop through and create the string to support variable number of file types
	    // return Pattern.matches(".*\\.(jpg|jpeg|gif|png|bmp)", name);
	    // if only one extension to check :  "\\.jpg"

		if (pathname.isDirectory()) {
			return true;
		}
		
		StringTokenizer st = new StringTokenizer(filter, ",");
		while (st.hasMoreTokens()) {
			if (Pattern.matches(st.nextToken(),pathname.getName())) {				
				return true;
			}
		}
		return false;
	}
}
