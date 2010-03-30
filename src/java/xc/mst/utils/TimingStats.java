package xc.mst.utils;

import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TimingStats {
	public static final String DEFAULT = "DEFAULT";
	public static final Logger LOG = Logger.getLogger(TimingLogger.class);
	
	protected static int longestThreadName = 0;
	protected boolean manualShutOff = false;

	protected Map<String, Timer> namedTimers = new TreeMap<String, Timer>();
	protected Map<String, Long> namedLastTimes = new TreeMap<String, Long>();
	protected Map<String, Long> namedBeginTimes = new TreeMap<String, Long>();
	
	public void log(String message) {
		log(null, message, false);
	}
	
	public void log(String message, boolean suppressOutput) {
		log(null, message, suppressOutput);
	}
	
	public void log(String name, String message, boolean suppressOutput) {
		if (TimingStats.LOG.isDebugEnabled() && !manualShutOff) {
			long tnow = System.currentTimeMillis();
			if (name == null) {
				name = DEFAULT;
			}
			
			Long lastTime = namedLastTimes.get(name);
			long incrTime = 0;
			if (lastTime == null) {
				lastTime = 0l;
			} else {
				incrTime = tnow-lastTime;
			}
			
			Long beginTime = namedBeginTimes.get(name);
			if (beginTime == null) {
				beginTime = tnow;
				namedBeginTimes.put(name, beginTime);
			}

			if (!suppressOutput) {
				String thisTime = StringUtils.leftPad((incrTime)+"", 6);
				String totalTime = StringUtils.leftPad((tnow-beginTime)+"", 7);

				StringBuilder sb = getStringBuilder();
				sb.append("TimingLogger! "+thisTime+" "+totalTime+" : "+message);
				LOG.debug(sb.toString());				
			}
			if (name != DEFAULT) {
				Timer timer = namedTimers.get(name);
				if (timer == null) {
					timer = new Timer();
					namedTimers.put(name, timer);
				}
				timer.totalTime.addAndGet(incrTime);
				if (timer.longestTime < incrTime) {
					timer.longestTime = incrTime;
				}
				timer.numTimes.incrementAndGet();
			}
			namedLastTimes.put(name, tnow);
		}
	}
	
	protected StringBuilder getStringBuilder() {
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
		return sb;
	}
	
	public void start(String name) {
		if (TimingStats.LOG.isDebugEnabled() && !manualShutOff) {
			long tnow = System.currentTimeMillis();
			namedLastTimes.put(name, tnow);
		}
	}
	
	public void stop(String name) {
		if (TimingStats.LOG.isDebugEnabled() && !manualShutOff) {
			log(name, null, true);
		}
	}
	
	
	public void turnOn() {
		if (TimingStats.LOG.isDebugEnabled()) {
			manualShutOff = false;
		}
	}
	
	
	public void turnOff() {
		if (TimingStats.LOG.isDebugEnabled()) {
			manualShutOff = true;
		}
	}
	
	public void add(String name, long val) {
		if (!manualShutOff) {
			Timer timer = namedTimers.get(name);
			if (timer == null) {
				timer = new Timer();
				namedTimers.put(name, timer);
			}
			timer.totalTime.addAndGet(val);
			if (timer.longestTime < val) {
				timer.longestTime = val;
			}
			timer.numTimes.incrementAndGet();
		}
	}
	
	public void reset(String name) {
		StringBuilder sb = getStringBuilder();
		Timer timer = namedTimers.get(name);
		if (timer != null) {
			String totalTime = StringUtils.leftPad(timer.totalTime.get()+"", 10);
			String avgTime = "";
			if (timer.numTimes.get() != 0) {
				double avg = (0.+timer.totalTime.get())/timer.numTimes.get();
				avgTime = String.format("%.2f", avg);
				avgTime = StringUtils.leftPad(avgTime, 10);
			}
			String longestTime = StringUtils.leftPad(timer.longestTime+"", 7);
			String num = StringUtils.leftPad(timer.numTimes+"", 7);
			sb.append("TimingLogger! total: "+totalTime+"    avg:"+avgTime+"    longest:"+longestTime+"    num:"+num+"   "+name);
			LOG.debug(sb);
		}
	}
	
	public void reset() {
		LOG.debug("reset()");
		LOG.debug("***");
		reset(true);
		LOG.debug("***");
	}
	
	public void reset(boolean includeDefault) {
		if (TimingStats.LOG.isDebugEnabled() && !manualShutOff) {
			LOG.debug("namedTimers.size(): "+namedTimers.size());
			LOG.debug("includeDefault: "+includeDefault);
			for (String key : namedTimers.keySet()) {
				reset(key);
			}
			namedTimers = new TreeMap<String, Timer>();
			Long nullLastTime = namedLastTimes.get(DEFAULT);
			namedLastTimes =  new TreeMap<String, Long>();
			Long nullBeginTime = namedBeginTimes.get(DEFAULT);
			namedBeginTimes =  new TreeMap<String, Long>();
			if (!includeDefault) {
				namedLastTimes.put(DEFAULT, nullLastTime);
				namedBeginTimes.put(DEFAULT, nullBeginTime);
			}
		}
	}
	
	public class Timer {
		public AtomicLong totalTime = new AtomicLong(0);
		public Long longestTime = 0l;
		public AtomicLong numTimes = new AtomicLong(0);
		public void reset() {
			totalTime = new AtomicLong();
			longestTime = 0l;
			numTimes = new AtomicLong();
		}
	}

}
