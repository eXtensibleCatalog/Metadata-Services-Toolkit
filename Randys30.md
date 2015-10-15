## Prepping the MST ##
  * delete files
```
rm ./mst-service/custom/MARCToXCTransformation/test/mock_harvest_input/randys-30/*
```
  * run mock harvest
```
ant mc.deploy; ant ms.publish; ant -Dlog.suffix=test ms.copy-example; cd ./mst-service/custom/MARCToXCTransformation/; ant retrieve; ant -Dtest=Mock -Dtest.folder=randys-30 test > ../../../build/out; cd ~-; message
```
  * start tomcat
  * delete excess harvest\_schedules
```
mysql -u root --password=root -D MetadataServicesToolkit -e "delete from harvest_schedules where harvest_schedule_id <> 1;"
```
  * copy the file into
```
cp ./mst-service/custom/MARCToXCTransformation/test/mock_harvest_input/randys-30/010* /cygdrive/c/dev/java/test/mock_harvest_input/randys-30/
```
  * delete old data
```
mysql -u root --password=root -D xc_randys_30 -e "truncate record_oai_ids; truncate record_predecessors; truncate record_sets; truncate record_updates; truncate records; truncate records_xml; truncate properties;"
```
```
export SERVER=localhost:8080; export JSESSIONID=;
./scripts/solr_query_delete.sh
```
  * start a harvest
```
mysql -u root --password=root -D MetadataServicesToolkit -e "update harvest_schedules set hour=10, minute=39, day_of_week=0;"
```

## Resetting existing MST (with correct provider already configured) ##
  * stop tomcat
```
./scripts/truncate_db.sh xc_marctoxctransformation;
./scripts/truncate_db.sh xc_randys_30;
mysql -t -u root --password=root -D MetadataServicesToolkit -e "update oai_id_sequence set id=1;";
rm -fR ./mst-service/custom/MARCToXCTransformation/build/MST-instances/MetadataServicesToolkit/solr/data/*;
rm -fR ./mst-service/custom/MARCToXCTransformation/build/MST-instances/MetadataServicesToolkit/logs/*;
mysql -u root --password=root -D MetadataServicesToolkit -e "update providers set last_oai_request=null;"
```
  * start tomcat
```
cp ./mst-service/custom/MARCToXCTransformation/test/mock_harvest_input/randys-30/010* /cygdrive/c/dev/java/test/mock_harvest_input/randys-30/
```
  * you have to do the above files one at a time and start a harvest
```
mysql -u root --password=root -D MetadataServicesToolkit -e "update harvest_schedules set hour=10, minute=39, day_of_week=0;"
```

## Harvesting from Drupal ##
  * if you need to clear it out
    * xc->Metadata Storage Configuration and Utilities->delete
    * truncate additional table
```
mysql -u root --password=root -D xc10rctest -e "truncate xc_entity_relationships"
```
  * harvest
    * create repo
      * http://localhost:8080/MetadataServicesToolkit/st/marctoxctransformation/oaiRepository.action
    * create schedule
      * regex
      * Create nodes first, then index with Solr.
      * click harvest