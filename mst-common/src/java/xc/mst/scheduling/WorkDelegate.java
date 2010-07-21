package xc.mst.scheduling;

public interface WorkDelegate {

	public void setup();
	
	public boolean doSomeWork();
	
	public void pause();
	
	public void resume();
	
	public void cancel();
	
	public void finish();
	
	public String getName();
	
	public String getDetailedStatus();
	
	public int getRecordsProcessed();

	public int getTotalRecords();
	
	public WorkerThread getWorkerThread();
	public void setWorkerThread(WorkerThread wt);
	
}
