
```
MST version 1.5.1

=============
Hardware / OS

VMWare Host:
vSphere version 5.1.0

VMWare Guest:
Ubuntu Linux 2.6.32 (64-bit)
VM Version: 8
CPU: 2 vCPU (4.38 GHz total allocated)
Memory: 8192 MB


Disk Usage:
Mysql: 61G
13G	MARC Repository
17G	MARCNormalization
18G	MARCAggregation
12G	MARCToXCTransformation
108M	MetadataServicesToolkit

MST (tomcat): 6.4G

============
JVM settings

-Djava.awt.headless=true 
-Xmx6g
-Xms6g 
-XX:+UseConcMarkSweepGC 
-XX:+CMSIncrementalMode

===============
MARC Repository
total records: 4,730,386

Processing
beg: 03 Mar 2013 08:54:00,555
end: 03 Mar 2013 09:43:41,403
Elapsed time: 49 minutes and 41 seconds
Rate: 5.7M records / hour

SOLR indexing
beg: 03 Mar 2013 19:06:36,640
end: 03 Mar 2013 22:24:49,802
Elapsed time: 3 hours, 18 minutes and 13 seconds
Rate: 1.4M records / hour

Total Elapsed time (Processing + SOLR indexing): 4 hours, 7 minutes and 54 seconds
Rate (Processing + SOLR indexing): 1.1M records / hour

 
=================
MARCNormalization
total input records: 4,730,386
total output records: 4,730,385
bibs: 2,302,083
holdings: 2,428,302

Processing
beg: 03 Mar 2013 09:43:42,955
end: 03 Mar 2013 10:59:57,477
Elapsed time: 1 hour, 16 minutes and 15 seconds
Rate: 3.7M records / hour

SOLR indexing
beg: 03 Mar 2013 22:33:06,856
end: 04 Mar 2013 02:30:44,745
Elapsed time: 3 hours, 57 minutes and 38 seconds
Rate: 1.2M records / hour

Total Elapsed time (Processing + SOLR indexing): 5 hours, 13 minutes and 53 seconds
Rate (Processing + SOLR indexing): 900K records/hour


===============
MARCAggregation
total input records: 4,730,385
total output records: 4,727,924
bibs: 2,299,622
holdings: 2,428,302

Processing
beg: 03 Mar 2013 10:59:58,418
end: 03 Mar 2013 14:54:06,420
Elapsed time: 3 hours, 54 minutes and 8 seconds
Rate: 1.2M records / hour

SOLR indexing
beg: 04 Mar 2013 02:40:43,942
end: 04 Mar 2013 07:20:16,915
Elapsed time: 4 hours, 39 minutes and 33 seconds
Rate: 1.0 M records / hour

Total Elapsed time (Processing + SOLR indexing): 8 hours, 33 minutes and 41 seconds
Rate (Processing + SOLR indexing): 500K records / hour


======================
MARCToXCTransformation
total input records: 4,727,924
total output records: 9,436,160

Processing
beg: 03 Mar 2013 14:54:08,123
end: 03 Mar 2013 19:00:01,399
Elapsed time: 4 hours, 5 minutes and 53 seconds
Rate: 1.2M records / hour

SOLR indexing
beg: 04 Mar 2013 07:23:16,827
end: 04 Mar 2013 11:03:56,618
Elapsed time: 3 hours, 40 minutes and 40 seconds
Rate: 1.3M records / hour

Total Elapsed time (Processing + SOLR indexing): 7 hours, 46 minutes and 33 seconds
Rate (Processing + SOLR indexing): 600K records / hour


======
TOTALS

Processing time: 10 hours, 5 minutes and 57 seconds
SOLR indexing time: 15 hours, 36 minutes and 4 seconds
Processing + SOLR indexing time: 1 day, 1 hour, 42 minutes and 1 second
Total Elapsed time (including Mysql indexing time, etc.): 1 day, 2 hours, 9 minutes and 56 seconds
Rate (Everything): 200K / hour 


```