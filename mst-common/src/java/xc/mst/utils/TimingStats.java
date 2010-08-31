/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TimingStats {
	public static final String DEFAULT = "DEFAULT";
	public static final Logger LOG = Logger.getLogger(TimingLogger.class);
	
	protected static int longestThreadName = 0;
	protected int currentIndent = 0;
	protected boolean manualShutOff = false;

	protected ListOrderedMap namedTimers = new ListOrderedMap();
	protected Map<String, Integer> indentation = new TreeMap<String, Integer>();
	protected Map<String, Long> namedLastTimes = new TreeMap<String, Long>();
	protected Map<String, Long> namedBeginTimes = new TreeMap<String, Long>();
	protected long lastReset = System.currentTimeMillis();
	
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
				Timer timer = (Timer)namedTimers.get(name);
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
			Timer timer = (Timer)namedTimers.get(name);
			if (timer == null) {
				timer = new Timer();
				namedTimers.put(name, timer);
			}
			long tnow = System.currentTimeMillis();
			namedLastTimes.put(name, tnow);
			if (!indentation.containsKey(name)) {
				indentation.put(name, currentIndent);	
			}
			currentIndent++;
		}
	}
	
	public void stop(String name) {
		if (TimingStats.LOG.isDebugEnabled() && !manualShutOff) {
			log(name, null, true);
			currentIndent--;
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
			Timer timer = (Timer)namedTimers.get(name);
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
		Timer timer = (Timer)namedTimers.get(name);
		if (timer != null) {
			String totalTime = StringUtils.leftPad(timer.totalTime.get()+"", 10);
			if (indentation.containsKey(name)) {
				//LOG.debug("indentation.get("+name+"):"+ indentation.get(name));
				StringBuilder sb2 = new StringBuilder();
				for (int i=0; i<indentation.get(name); i++) {
					sb2.append("  ");
				}
				name = sb2.append(name).toString();
			}
			String avgTime = "";
			if (timer.numTimes.get() != 0) {
				double avg = (0.+timer.totalTime.get())/timer.numTimes.get();
				avgTime = String.format("%.2f", avg);
				avgTime = StringUtils.leftPad(avgTime, 12);
			}
			String longestTime = StringUtils.leftPad(timer.longestTime+"", 9);
			String num = StringUtils.leftPad(timer.numTimes+"", 7);
			sb.append("TimingLogger! total: "+totalTime+"    avg:"+avgTime+"    longest:"+longestTime+"    num:"+num+"   "+name);
			LOG.debug(sb);
		}
	}
	
	public void reset() {
		LOG.debug("reset()");
		LOG.debug("***");
		reset(true);
		Runtime r = Runtime.getRuntime();
		long maxMem = r.maxMemory()/1048576;
		long totalMem = r.totalMemory()/1048576;
		
		long freeBytes = r.freeMemory();
		long freeMem = freeBytes/1048576;
		LOG.debug("");
		LOG.debug("Free  memory: " + Long.toString(freeMem) + "MB.");
		LOG.debug("Total memory: " + Long.toString(totalMem) + "MB.");
		LOG.debug("Max'm memory: " + Long.toString(maxMem) + "MB.");
		LOG.debug("***");
	}
	
	public void reset(boolean includeDefault) {
		if (TimingStats.LOG.isDebugEnabled() && !manualShutOff) {
			if (lastReset != 0) {
				long timeSinceLastReset = System.currentTimeMillis()-lastReset;
				lastReset = System.currentTimeMillis();
				LOG.debug("timeSinceLastReset: "+timeSinceLastReset);
			}
			LOG.debug("namedTimers.size(): "+namedTimers.size());
			LOG.debug("includeDefault: "+includeDefault);
			OrderedMapIterator omi = namedTimers.orderedMapIterator(); 
			while (omi.hasNext()) {
				reset((String)omi.next());
			}
			/*
			for (String key : namedTimers.keySet()) {
				reset(key);
			}
			*/
			namedTimers = new ListOrderedMap();
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
