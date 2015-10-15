latest test on 146 (against 137)
  * issues
    * the first time I went to Browse Records, it threw an "out of heap space" exception.
      * I killed it, upped the memory to 2800M (from 2600M) and restarted
      * did the change of memory matter or was it a memory leak?
      * also, the process bar wasn't displaying
  * as reported by 137
    * lucene\_dbStatistics.sh
```
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh 
Lucene Statistics Value is:true
init(luceneDir): lucene_index
init(logDir): log
 log4j property file: /import/OAIToolkit/OAIToolkit.log4j.properties
2011-05-09 09:59:04,432 [main] (Logging.java:50) INFO  - Logging started in directory: log
2011-05-09 09:59:07,538 [main] (Importer.java:969) INFO  -  *************** Lucene Database Statistics *************** 

 
2011-05-09 09:59:07,539 [main] (Importer.java:971) INFO  - Total records in the Lucene Database are: 6848888
2011-05-09 09:59:07,539 [main] (Importer.java:972) INFO  - 
         Bibliographic records: 2711069
2011-05-09 09:59:07,540 [main] (Importer.java:973) INFO  -       Authority records: 1254933
2011-05-09 09:59:07,540 [main] (Importer.java:974) INFO  -       Holdings records: 2882886
2011-05-09 09:59:07,542 [main] (Importer.java:975) INFO  -       Classification records: 0
2011-05-09 09:59:07,543 [main] (Importer.java:976) INFO  -       Community information records: 0
2011-05-09 09:59:07,543 [main] (Importer.java:977) INFO  -       Deleted records: 3
```
    * totals: 6,848,885
```
curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
```
    * bibs: 2,711,069
```
curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
```
    * hold: 2,882,886
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
```

  * auth: 1,254,930
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'
```
  * as reported by 146
    * vi ./MST-instances/MetadataServicesToolkit/logs/harvestIn/rochester\_137.txt
      * auth new\_act\_cnt: 1,254,930
        * new\_del\_cnt: 3
      * hold new\_act\_cnt: 2,882,886
      * bib new\_act\_cnt: 2,711,069
      * total new\_act\_cnt: 6,848,885
        * deleted: 3
    * vi ./MST-instances/MetadataServicesToolkit/logs/service/marcnormalization.txt
      * incoming
        * hold new\_act\_cnt: 2,882,886
        * bib new\_act\_cnt: 2,711,069
        * total new\_act\_cnt: 6,848,885
      * outgoing
        * hold new\_act\_cnt: 2,882,886
        * bib new\_act\_cnt: 2,711,068
        * total new\_act\_cnt: 6,848,884
        * (the extra one is produced no output - seen in "Browse Records"
    * vi ./MST-instances/MetadataServicesToolkit/logs/service/marctoxctransformation.txt
      * incoming
        * hold new\_act\_cnt: 2,882,886
        * bib new\_act\_cnt: 2,711,068 (one less)
        * total new\_act\_cnt: 6,848,884 (one less)
      * outgoing
        * work new\_act\_cnt: 2,892,685
        * holdings
          * new\_act\_cnt: 2,768,263
          * new\_held\_cnt: 125,373
          * upd\_act\_cnt: 7,355
          * upd\_act\_prev\_held\_cnt: 7,355
        * expressions: new\_act\_cnt: 2,892,685
        * manifestations: new\_act\_cnt: 2,711,059
        * total
          * new\_act\_cnt: 11,264,692
          * new\_held\_cnt: 125,373
          * upd\_act\_cnt: 7,355
          * upd\_act\_prev\_held\_cnt: 7,355
    * mysql counts by type, status
```
/usr/local/tomcat
$  /usr/local/mysql/bin/mysql --user=root --password=pass-e "select status, count(*) c from xc_rochester_137.records group by status order by status"
+--------+---------+
| status | c       |
+--------+---------+
| A      | 6848885 |
| D      |       3 |
+--------+---------+

/usr/local/tomcat
$  /usr/local/mysql/bin/mysql --user=root --password=pass -e "select type, status, count(*) c from xc_rochester_137.records group by type, status order by type, status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
|      | A      | 6848885 |
|      | D      |       3 |
+------+--------+---------+

/usr/local/tomcat
$  /usr/local/mysql/bin/mysql --user=root --password=pass -e "select status, count(*) c from xc_marcnormalization.records group by status order by status"
+--------+---------+
| status | c       |
+--------+---------+
| A      | 6848884 |
+--------+---------+

/usr/local/tomcat
$  /usr/local/mysql/bin/mysql --user=root --password=pass -e "select type, status, count(*) c from xc_marcnormalization.records group by type, status order by type, status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
|      | A      | 1254930 |
| b    | A      | 2711068 |
| h    | A      | 2882886 |
+------+--------+---------+

/usr/local/tomcat
$  /usr/local/mysql/bin/mysql --user=root --password=pass -e "select status, count(*) c from xc_marctoxctransformation.records group by status order by status"
+--------+----------+
| status | c        |
+--------+----------+
| A      | 11272047 |
| H      |   118018 |
+--------+----------+

/usr/local/tomcat
$  /usr/local/mysql/bin/mysql --user=root --password=pass -e "select type, status, count(*) c from xc_marctoxctransformation.records group by type, status order by type, status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| e    | A      | 2892685 |
| h    | A      | 2775618 |
| h    | H      |  118018 |
| m    | A      | 2711059 |
| w    | A      | 2892685 |
+------+--------+---------+
```
    * solr ("Browse Records") agrees w/ the above numbers

## after first update file ##
  * as reported by 137
  * ./custom\_cml.sh
    * (updates2011-05-06.mrc was the only file in /import/marc\_extract/)
    * spewed out tons of to console... position: 0, position: 1, etc
    * a few exceptions and other messages I wouldn't have expected
```
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh
Lucene Statistics Value is:true
init(luceneDir): lucene_index
init(logDir): log
 log4j property file: /import/OAIToolkit/OAIToolkit.log4j.properties
2011-05-12 12:49:04,469 [main] (Logging.java:50) INFO  - Logging started in directory: log
2011-05-12 12:49:07,144 [main] (Importer.java:969) INFO  -  *************** Lucene Database Statistics ***************


2011-05-12 12:49:07,154 [main] (Importer.java:971) INFO  - Total records in the Lucene Database are: 6859918
2011-05-12 12:49:07,155 [main] (Importer.java:972) INFO  -
         Bibliographic records: 2716550
2011-05-12 12:49:07,155 [main] (Importer.java:973) INFO  -       Authority records: 1254933
2011-05-12 12:49:07,155 [main] (Importer.java:974) INFO  -       Holdings records: 2888435
2011-05-12 12:49:07,157 [main] (Importer.java:975) INFO  -       Classification records: 0
2011-05-12 12:49:07,158 [main] (Importer.java:976) INFO  -       Community information records: 0
2011-05-12 12:49:07,158 [main] (Importer.java:977) INFO  -       Deleted records: 3
```
  * 146 attempted update harvest, but 137 isn't reporting any updats
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&from=2011-05-10T20:51:26Z&until=2011-05-12T13:45:02Z' | xmllint --format -
<?xml version="1.0" encoding="UTF-8"?>
<OAI-PMH xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd">
  <responseDate>2011-05-12T17:48:17Z</responseDate>
  <request verb="ListRecords" from="2011-05-10T20:51:26Z" until="2011-05-12T13:45:02Z" metadataPrefix="marc21">http://128.151.244.137:8080/OAIToolkit/oai-request.do</request>
  <error code="noRecordsMatch">The combination of the values of the from, until, set, and metadataPrefix arguments results in an empty list.</error>
</OAI-PMH>

```