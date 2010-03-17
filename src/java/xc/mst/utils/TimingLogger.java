package xc.mst.utils;

public class TimingLogger {
	@SuppressWarnings("unchecked")
	/*
	protected static ThreadLocal timingStats = new ThreadLocal() {
		protected synchronized Object initialValue() {
			TimingStats ts = new TimingStats();
			ts.log("TimingStats()");
            return ts;
        }
	};
	*/
	
	protected static TimingStats ts = new TimingStats();
	
	
	public static void log(String message) {
		if (TimingStats.LOG.isDebugEnabled()) {
			//TimingStats ts = (TimingStats)timingStats.get();
			ts.log(message);
		}
	}
	
	public static void reset() {
		if (TimingStats.LOG.isDebugEnabled()) {
			//TimingStats ts = (TimingStats)timingStats.get();
			ts.reset();
			ts.log("ts.reset");
		}
	}

}
