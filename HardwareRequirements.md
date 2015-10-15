# Hardware Requirements #

  * **CPU**: The main metric for the MST is  the clock speed.
    * Note:  We had [a problem](http://stackoverflow.com/questions/6749159/thread-sleep-is-hung) running the MST on a VMWare ESX 3.5 system running Solaris with 2 CPUs allocated to it.  With this configuration the MST hung sporadically.  We recommend running a different operating system is you wish to run the MST on a multiprocessor VMWare ESX 3.5 system. This may not be an issue with VMWare vSphere 4 or 5.
  * **RAM**: You will want, at minimum, 4G (6G to be on the safe side).  This is dependent on the size of the repositories you process. We found that 6G (3G devoted to the JVM) was acceptable for our repository size of 6 million records.
    * To allocate 3G of memory, we allotted 3G to the initial java heap size and to the maximum java heap size.  Example:
      * CATALINA\_OPTS="-server -Xms3000m –Xmx3000m";
  * **Hard Disk:**
    * In both of our tests we used 10k RPM hard drives.
    * Space Needed: (5.9 million incoming MARC records)
      * MySQL
        * Harvest repository of 5.9 million records = 15G
        * Normalized repository of 5.9 million records = 20G
        * Transformed XC repository of 11.6 million records = 13G
      * Solr
        * 20G for all of the above records
      * Total = 70G
  * **Our Results** (the only real difference between our servers was the CPU):
    * Server with CPU: 3 GHz Intel Xeon 5160
      * Harvested records: 5.8 million records in 2hr:17m = 2.5 million records/hr
      * Normalized records: 5.8 million records in 2hr:21m = 2.5 million records/hr
      * Transformed records: 5.8 million records in 2hr:07m = 2.5 million records/hr
      * Total time: 6hr:45m
      * Solr indexing (the records are available for OAI-PMH harvesting before indexing completes):
        * Harvested records: 5.8 million records in  3hr:21m = 1.5 million records/hr
        * Normalized records: 5.8 million records in 4hr:17m = 1.3 million records/hr
        * FRBRized records: 11 million records in 3hr:05m = 3.5 million records/hr
        * Total indexing: 22.6 million records in 10hr:43m
    * Server with CPU: 1.5 GHz SPARC V9
      * Harvested records: 5.8 million records in 3hr:21m = 1.7 million records/hr
      * Normalized records: 5.8 millions records in 5hr:41m = 1 million records/hr
      * Transformed records: 5.8 million records in 6hr:09m = 0.9 million records/hr
      * Total time: 15hr:11m
      * Solr indexing (the records are available for OAI-PMH harvesting before indexing completes):
        * Harvested records: 5.8 million records in 10hr:30m = 550 thousand records/hr
        * Normalized records: 5.8 million records in 10hr:30m = 550 thousand records/hr
        * FRBRized records: 11 million records in 10hr:00m = 1.1 million records/hr
        * Total time: 31hr:00m