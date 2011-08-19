/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.scheduling;

import xc.mst.utils.MSTConfiguration;

/**
 * A program which starts the Scheduler Thread. This will
 * cause the harvests to be run according to the schedule, which
 * is stored in the MySQL database. It will also allow services
 * to be scheduled by harvests or other services that matched
 * processing directives for them.
 * 
 * @author Eric Osisek
 */
public class StartSchedulerThread {
    /**
     * The main method starts the Scheduler Thread.
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {
        Scheduler scheduler = (Scheduler) MSTConfiguration.getInstance().getBean("Scheduler");
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.start();
    } // end main method
} // end class StartSchedulerThread
