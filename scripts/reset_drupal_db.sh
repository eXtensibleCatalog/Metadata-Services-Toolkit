# admin > eXtensible Catalog > Solr ...> Delete...

#mysqldump --no-data --add-drop-table -u root --password=root xc10rctest xc_entity_properties xc_entity_relationships xc_sql_metadata node node_comment_statistics node_revision
mysqldump --no-data --add-drop-table -u root --password=root xc10rctest xc_entity_properties xc_entity_relationships xc_sql_metadata node node_comment_statistics > ./build/d.sql
mysql -u root --password=root -D xc10rctest < ./build/d.sql
