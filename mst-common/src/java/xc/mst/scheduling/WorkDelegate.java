package xc.mst.scheduling;

public interface WorkDelegate {

	public void setup();
	
	public boolean doSomeWork();
	
	public void cancel();
	
	public void finish();
	
	public String getName();
	
	public String getDetailedStatus();
	
}
