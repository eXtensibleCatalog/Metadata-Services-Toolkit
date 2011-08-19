/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
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

    public long getTotalRecords();

    public WorkerThread getWorkerThread();
    public void setWorkerThread(WorkerThread wt);

}
