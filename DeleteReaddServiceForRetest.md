#Delete and re-add a service for testing purposes such that we totally run all records through that service again.

Needed to change custom.properties  to enable some additional messages to log for marctoxctransformation.

```
cd META-INF/classes/xc/mst/services/
vi custom.properties
sudo vi custom.properties
mysql -u root --password=* -D MetadataServicesToolkit -e "select * from services"
mysql -u root --password=* -D MetadataServicesToolkit -e "select * from service_harvests"
mysql -u root --password=* -D MetadataServicesToolkit -e "delete from service_harvests where repo_name='marctoxctransformation'"
mysql -u root --password=* -D MetadataServicesToolkit -e "delete from service_harvests where service_id=2"
mysql -u root --password=* -D MetadataServicesToolkit -e "select * from service_harvests"
mysql -u root --password=* -D MetadataServicesToolkit -e "select * from services"
mysql -u root --password=* -D MetadataServicesToolkit -e "delete from services where service_id=2"
sudo /etc/init.d/tomcat6 start
mysql -u root --password=* -D xc_marctoxctransformation -e "show tables"
mysql -u root --password=* -D xc_marctoxctransformation -e "select * from outgoing_record_counts"
mysql -u root --password=* -D xc_marctoxctransformation -e "select * from incoming_record_counts"
mysql -u root --password=* -D xc_marctoxctransformation -e "select * from records"
```
