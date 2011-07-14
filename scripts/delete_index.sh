export server="localhost:8080/drupal-solr"
curl http://$server/update -H "Content-Type: text/xml" --data-binary '<delete><query>*:*</query></delete>'
curl http://$server/update -H "Content-Type: text/xml" --data-binary '<commit />'
curl http://$server/update -H "Content-Type: text/xml" --data-binary '<optimize />' 

