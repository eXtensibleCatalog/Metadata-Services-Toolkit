## Useful Information ##

### Download Links and Known Issues ###

  * See the ["Release Notes"](ReleaseNotes.md) section for specific versions of the 0.3.x series of releases.

### Back-End Monitoring ###
  * Ways to monitor the MST (besides watching the counter tick down from 108:00 and entering 4 8 15 16 23 42):
    * prstat (Solaris) or top (Linux) - Java and MySQL should be hogging the processor.
    * inspect ./MST-instances/MetadataServicesToolkit/logs/MST\_General\_log.txt
      * grep for "eption" - Looking for exceptions
      * If you are running a harvest or a service, you should see timing stats output every few minutes.
    * See if records are being inserted into the repository.

```
mysql -u root --password=YOUR_PASSWORD -e "select count(*) from external_repo_name.records;"
or
mysql -u root --password=YOUR_PASSWORD -e "select count(*) from service_name.records;"
```

  * The biggest issue we have been running into recently is with query optimization. No query should take longer than a second. If they do, there is a problem:

```
mysql -u root --password=YOUR_PASSWORD -e "show full processlist \G"
```

### Tips for Restarting ###
  * The MST should be robust and whatnot, but it is not entirely there yet. If, by chance, you run out of memory, or something else happens, and you want to start over....
    * Rerun the sql

```
mysql -u root --password=root < ./MST-instances/!MetadataServicesToolkit/sql/create_database_script.sql
```

  * (You do not need to drop your xc\_databases, because when a new one is created with the same name, the MST will drop it for you.)
  * Delete logs and Solr index (2 options)

```
rm -fR ./MST-instances/MetadataServicesToolkit/solr/data
rm -fR ./MST-instances/MetadataServicesToolkit/logs/*
```

  * Reinstall your services
    * (Just in the ui - you do not need to unzip them again.)

### More Tips ###
  * To delete Solr index in a live system:

```
export SERVER=localhost:8080;
export JSESSIONID=blah-blah;
curl "http://${SERVER}/MetadataServicesToolkit/solr/update;jsessionid=${JSESSIONID}" -H "Content-Type: text/xml" --data-binary '<delete><query>*:*</query></delete>';
curl "http://${SERVER}/MetadataServicesToolkit/solr/update;jsessionid=${JSESSIONID}" -H "Content-Type: text/xml" --data-binary '<commit />';
curl "http://${SERVER}/MetadataServicesToolkit/solr/update;jsessionid=${JSESSIONID}" -H "Content-Type: text/xml" --data-binary '<optimize />';
curl "http://${SERVER}/MetadataServicesToolkit/devAdmin?op=refreshSolr;jsessionid=${JSESSIONID}"
```

  * You should get responses as such:

```
<result status="0"></result>
```