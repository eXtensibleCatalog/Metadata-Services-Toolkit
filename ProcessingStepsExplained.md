This page contains a brief explanation of the steps involved in the MST.  I'll detail it further as needed.

  * external oai-pmh harvest
    * the external records are essentially cached locally for further processing.
    * If a harvest gets interrupted, it cannot be resumed.
      * this is something we plan on fixing
    * The status "x out of y records harvested" is reported to the web ui
    * to double check, you can check mysql
```
mysql -u root --password=YOUR_PASSWORD -e "select count(*) from external_repo_name.records;"
```
  * service processing
    * The status "x out of y records harvested" is reported to the web ui
      * currently y is always 0.  This should be fixed soon.
    * services output records to their own records table just like an external harvest.  To see that in action:
```
mysql -u root --password=YOUR_PASSWORD -e "select count(*) from service_repo_name.records;"
```
    * services get records from an input repository much like an oai-pmh harvest
    * if service processing is interrupted, it should resume where it left off from (see the service\_harvests table)
```
DROP TABLE IF EXISTS service_harvests;
CREATE TABLE service_harvests
(
  service_harvest_id INT(11)  NOT NULL AUTO_INCREMENT,
  service_id         int(11),
  format_id          int(11),
  repo_name          varchar(64),
  set_id             int(11),
  from_date          datetime,
  until_date         datetime,
  highest_id         int(11),
  
  PRIMARY KEY(service_harvest_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
      * service\_id is the service that is doing the harvesting
      * repo\_name is repository the service is harvesting from
      * highest\_id is sort of like a resumption token since we sort by ids
  * solr indexing
    * this process uses the service\_harvests table as well.
    * the solr indexer is similar to a metadataservice, but is inherent and not installable like the others (marcnormalization, marctoxctransformation, etc)
    * in addition to checking the service\_harvests table and viewing the progress in the UI, I've included the solr web admin right in the MST webapp.  It's behind the same authentication screen as the main webapp.  Once you're logged into an mst, you can go to http://localhost:8080/MetadataServicesToolkit/solr/admin/stats.jsp to see how many docs are in the index.