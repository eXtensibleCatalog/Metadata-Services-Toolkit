package xc.mst.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TimingStats {
	public static final Logger LOG = Logger.getLogger(TimingLogger.class);
	protected static int longestThreadName = 0;
	protected long t0 = System.currentTimeMillis();
	protected long lastTime = t0;
	
	public void log(String message) {
		if (TimingStats.LOG.isDebugEnabled()) {
			long tnow = System.currentTimeMillis();
			String thisTime = StringUtils.leftPad((tnow-lastTime)+"", 6);
			String totalTime = StringUtils.leftPad((tnow-t0)+"", 7);
			String threadName = Thread.currentThread().getName();
			int threadNameLength = threadName.length();
			if (longestThreadName < threadNameLength) {
				longestThreadName = threadNameLength;
			}
			int diff = longestThreadName - threadNameLength;
			StringBuilder sb = new StringBuilder();
			if (diff > 0) {
				for (int i=0; i<diff; i++) {
					sb.append(" ");
				}
			}
			sb.append("TimingLogger! "+thisTime+" "+totalTime+" : "+message);
			LOG.debug(sb.toString());
			lastTime = tnow;
		}
	}
	
	public void reset() {
		if (TimingStats.LOG.isDebugEnabled()) {
			t0 = System.currentTimeMillis();
			lastTime = t0;
		}
	}

}
