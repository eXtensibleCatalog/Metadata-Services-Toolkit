Here is the info you need to get started with the MST 0.3.x release.  Make sure you at least skim over this page before jumping into the documentation as we've put some updates here that haven't yet made it into the documentation.
<br />
**note:** we are planning to fix known issues (as well as newly reported issues) over the next few weeks and will be releasing new versions as we go.

### Download Links and Known Issues ###
  * see the ReleaseNotes for specific versions of the 0.3.x series of releases.

### Documentation ###
**note:** some of these documents link to each other.  I haven't updated all of those links, so don't trust them.  The links below are correct.  If you're viewing the documentation on the web, then you can trust you are viewing the latest.  If you open a .doc file, you're viewing an older document.
  * [MST Installation manual](http://www.extensiblecatalog.org/doc/MST/installation/mst_installation_manual.htm)
  * [MST user manual](http://www.extensiblecatalog.org/doc/MST/MST_User_Manual.htm)
  * [Normalization Service](http://www.extensiblecatalog.org/doc/MST/Normalization_Service_Documentation.htm)
  * [MARC-to-XC Transformation Service](http://www.extensiblecatalog.org/doc/MST/TransformationServiceDocumentation.htm)
  * [DC-to-XC Transformation Service](http://www.extensiblecatalog.org/doc/MST/DCTransformationServiceDocumentation.htm)

### What kind of hardware will you need? ###
  * **CPU**: The MST is currently singled threaded, so it doesn't matter how many cores or cpus you have.  The main metric is the clock speed.
  * **RAM**: You'll want at minimum 4G (6G to be on the safe side).  This is dependent on the size of the repositories you process.  We found that 6G (3G devoted to the jvm) was acceptable for our repository size of 6 million records.
  * **Hard Disk**
    * In both of our tests we used 10k RPM hard drives.
    * Space Needed (5.9 M incoming MARC records)
      * mysql
        * harvest repo of 5.9M records = 15G
        * normalized repo of 5.9M records = 20G
        * transformed xc repo or 11.6M records = 13G
      * solr
        * 20 G for all of the above records
      * total = 70G
  * **Our Results**: (the only real difference between our servers was the cpu)
    * server w/ cpu: 3 GHz Intel Xeon 5160
      * harvest: 5.8M records in 2hr:17m = 2.5M records/hr
      * norm:    5.8M records in 2hr:21m = 2.5M records/hr
      * trans:   5.8M records in 2hr:07m = 2.5M records/hr
      * total:                   6hr:45m
      * solr indexing (the records are available for oai-pmh harvesting before indexing completes)
        * harvested records:  5.8M records in  3hr:21m = 1.5M records/hr
        * normalized records: 5.8M records in  4hr:17m = 1.3M records/hr
        * frbrized records:    11M records in  3hr:05m = 3.5M records/hr
        * total indexing:    22.6M records in 10hr:43m
    * server w/ cpu: 1.5 GHz SPARC V9
      * harvest: 5.8M records in 3hr:21m = 1.7M records/hr
      * norm:    5.8M records in 5hr:41m =   1M records/hr
      * trans:   5.8M records in 6hr:09m = 0.9M records/hr
      * total:                  15hr:11m
      * solr indexing (the records are available for oai-pmh harvesting before indexing completes)
        * harvested records:  5.8M records in 10hr:30m = 550k records/hr
        * normalized records: 5.8M records in 10hr:30m = 550k records/hr
        * frbrized records:    11M records in 10hr:00m = 1.1M records/hr
        * total                               31hr:00m

### What kind of permissions does the mysql user require? ###
**root. yes, root.**  If you don't want to use the root user, you'll need to make your non-root user have permissions to create and drop databases, functions, and procedures.  We understand this is a little different than the norm, but the MST is not your typical application either.

  * why root?<br /><br />The MST creates new databases for each repository (harvest/service).  These databases contains common tables (records, records\_xml, etc).  For services, they can also contain other custom tables.  Giving each service its own database simplifies things for the service implementer as each service is defaulted to using its own database and doesn’t need to worry about name collisions.  Alternatively, instead of giving each repo its own database, we could use table name prefixes (eg marcnormalization\_records, marcnormalization\_records\_xml, etc) to distinguish between repos and keep all MST data in one database.  However this makes it more difficult for service implementers to not bump into each other (or provide an environment which restricts them from doing so).  That is the reason why the MST creates databases on the fly and requires a mysql user with permissions to do so.<br /><br />
  * "But this is against our policy"<br /><br />Consider the mysql server used by the MST to be part of the MST application.  In other words, the mysql server used by the MST has only MST data in it and is run on the same machine as the MST.  It has not been in our design to have the MST’s mysql data hosted on some other machine.  It’s possible we could tailor to this in a future release if need be, but that’s not the way it’s expected to work as of now.  If you don’t want to give the MST root access because other applications share the same database, you might consider installing another mysql instance on that machine.<br /><br />
  * "But databases are being created about which I know nothing"<br /><br />We understand db admins like to have control over database tables, schemas, users, etc, which is why we suggest viewing the MST’s database as part of the MST application.  If you’re concerned that the MST might not play nicely with other databases sharing the same mysql instance, then you can sandbox it by giving it its own instance.  That’s the nature of the MST.  We’re providing a toolkit and platform for services to be written about which we know nothing.  To provide this kind of flexibility, you must give up some control.  I’m open to hearing other suggestions to accomplish this goal.

### Back-end Monitoring ###
  * ways to monitor the mst (besides watching the counter tick in the web-ui):
    * prstat (solaris) or top (linux) - java and mysql should be hogging the processor
    * inspect ./MST-instances/MetadataServicesToolkit/logs/MST\_General\_log.txt
      * grep for 'eption' - looking for exceptions
      * if you're running a harvest or a service, you should see timing stats output every few minutes
    * see if records are being inserted in the repo
```
mysql -u root --password=YOUR_PASSWORD -e "select count(*) from external_repo_name.records;"
or
mysql -u root --password=YOUR_PASSWORD -e "select count(*) from service_name.records;"
```
    * the biggest issue I've been running into recently is with query optimization.  No queries should take more than a second.  If they are, there is a problem:
```
mysql -u root --password=YOUR_PASSWORD -e "show full processlist \G"
```

### Tips for restarting ###
  * The MST should be robust and whatnot, but it isn't entirely yet.  If, by chance, you run out of memory, or something else happens, and you want to start over...
    * rerun the sql
```
mysql -u root --password=root < ./MST-instances/!MetadataServicesToolkit/sql/create_database_script.sql
```
      * (You don't need to drop your xc\_databases because when a new one is created with the same name, the MST will drop it then.)
    * delete logs and solr index (2 options)
```
rm -fR ./MST-instances/MetadataServicesToolkit/solr/data
rm -fR ./MST-instances/MetadataServicesToolkit/logs/*
```
    * reinstall your services
      * (just in the ui - you don't need to unzip them again)

### More tips ###
  * to delete solr index in a live system
```
export SERVER=localhost:8080;
export JSESSIONID=blah-blah;
curl "http://${SERVER}/MetadataServicesToolkit/solr/update;jsessionid=${JSESSIONID}" -H "Content-Type: text/xml" --data-binary '<delete><query>*:*</query></delete>';
curl "http://${SERVER}/MetadataServicesToolkit/solr/update;jsessionid=${JSESSIONID}" -H "Content-Type: text/xml" --data-binary '<commit />';
curl "http://${SERVER}/MetadataServicesToolkit/solr/update;jsessionid=${JSESSIONID}" -H "Content-Type: text/xml" --data-binary '<optimize />';
curl "http://${SERVER}/MetadataServicesToolkit/devAdmin?op=refreshSolr;jsessionid=${JSESSIONID}"
```
  * you should get responses as such
```
<result status="0"></result>
```