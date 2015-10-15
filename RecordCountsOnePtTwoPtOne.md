# T2 #
  * - Update 2 = deletes  (from 128.151.244.132)
  * On 2/08/12 we restarted our side. t2 from 128.151.244.132 run completed, numbers:
  * as reported by 132 (t2 run on --/--/-- on 132)
```
voyager@xcvoyoai2 > cd /import/OAIToolkit
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh

 log4j property file: /import/OAIToolkit/OAIToolkit.log4j.properties
2012-03-01 08:37:32,246 [main] (Logging.java:50) INFO  - Logging started in directory: log
2012-03-01 08:37:36,139 [main] (Importer.java:979) INFO  -  *************** Lucene Database Statistics ***************


2012-03-01 08:37:36,141 [main] (Importer.java:981) INFO  - Total records in the Lucene Database are: 5818467
2012-03-01 08:37:36,142 [main] (Importer.java:982) INFO  -  Bibliographic records: 2826181
2012-03-01 08:37:36,142 [main] (Importer.java:983) INFO  -       Deleted Bibliographic records: 13
2012-03-01 08:37:36,142 [main] (Importer.java:984) INFO  -  Authority records: 0
2012-03-01 08:37:36,142 [main] (Importer.java:985) INFO  -       Deleted Authority records: 0
2012-03-01 08:37:36,144 [main] (Importer.java:986) INFO  -  Holdings records: 2992233
2012-03-01 08:37:36,144 [main] (Importer.java:987) INFO  -       Deleted Holdings records: 40
2012-03-01 08:37:36,144 [main] (Importer.java:988) INFO  -  Classification records: 0
2012-03-01 08:37:36,144 [main] (Importer.java:989) INFO  -       Deleted Classification records: 0
2012-03-01 08:37:36,145 [main] (Importer.java:990) INFO  -  Community information records: 0
2012-03-01 08:37:36,145 [main] (Importer.java:991) INFO  -       Deleted Community information records: 0
2012-03-01 08:37:36,145 [main] (Importer.java:992) INFO  -  Deleted records: 53

```
```
   * Curl all matches lucene_dbStatistics
   * Total (matches lucene_dbStatistics above -> Total - Deleted):
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
<resumptionToken cursor="1000" completeListSize="5818414">12|999|1000|5818414|1</resumptionToken>
```

  * Holds:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
 <resumptionToken cursor="1000" completeListSize="2992233">13|1738|1000|2992233|0</resumptionToken>
```

  * Bibs:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
<resumptionToken cursor="1000" completeListSize="2826181">15|2346|1000|2826181|0</resumptionToken>
```

  * Auth:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'

```




  * as reported by 192.17.55.225
    * harvestIn file
```
incoming 2012-02-29T18:18:24Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-29T18:18:24Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
incoming 2012-02-29T18:18:24Z b                              unexpected_error_cnt:            0
incoming 2012-02-29T18:18:24Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-29T18:18:24Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
incoming 2012-02-29T18:18:24Z h                              unexpected_error_cnt:            0
incoming 2012-02-29T18:18:24Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-29T18:18:24Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           53
incoming 2012-02-29T18:18:24Z TOTALS                         unexpected_error_cnt:            0
29 Feb 2012 12:18:56,969  INFO [REPOSITORY_12415] -
incoming all time             b                                       new_act_cnt:    2,826,194               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:           13
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:           40
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,818,467               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:           53
incoming all time             TOTALS                         unexpected_error_cnt:            0
29 Feb 2012 12:19:26,708  INFO [REPOSITORY_12415] -
rochester_132_local:bib-active:   2,826,181rochester_132_local:bib-deleted:          13rochester_132_local:hold-active:   2,992,233
rochester_132_local:hold-deleted:          40
                  other-active:           0                 other-deleted:           0
                  total-active:   5,818,414                 total-deleted:          53
```

  * normalization log
```
incoming 2012-02-29T18:20:03Z other                                   new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-29T18:20:03Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           53
incoming 2012-02-29T18:20:03Z other                          unexpected_error_cnt:            0
incoming 2012-02-29T18:20:03Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-29T18:20:03Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           53
incoming 2012-02-29T18:20:03Z TOTALS                         unexpected_error_cnt:            0
29 Feb 2012 12:21:01,859  INFO [SERVICE_12419] -
incoming all time             b                                       new_act_cnt:    2,826,194               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           53
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,818,467               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:           53
incoming all time             TOTALS                         unexpected_error_cnt:            0
29 Feb 2012 12:21:01,859  INFO [SERVICE_12419] -
outgoing 2012-02-29T18:20:03Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-29T18:20:03Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
outgoing 2012-02-29T18:20:03Z b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:20:03Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:20:03Z b                              upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-29T18:20:03Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-29T18:20:03Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
outgoing 2012-02-29T18:20:03Z h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:20:03Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:20:03Z h                              upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-29T18:20:03Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-29T18:20:03Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           53
outgoing 2012-02-29T18:20:03Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:20:03Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:20:03Z TOTALS                         upd_del_prev_act_cnt:           53      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
29 Feb 2012 12:21:01,860  INFO [SERVICE_12419] -
outgoing all time             b                                       new_act_cnt:    2,826,193               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:           13
outgoing all time             b                              upd_act_prev_act_cnt:        5,461      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:           40
outgoing all time             h                              upd_act_prev_act_cnt:          518      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    5,818,466               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:           53
outgoing all time             TOTALS                         upd_act_prev_act_cnt:        5,979      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:           53      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
29 Feb 2012 12:21:09,349  INFO [SERVICE_12419] -
                      b-active:   2,826,180                     b-deleted:          13                      h-active:   2,992,233
                     h-deleted:          40
                  total-active:   5,818,413                 total-deleted:          53
```

  * transformation log
```
incoming 2012-02-29T18:22:18Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-29T18:22:18Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
incoming 2012-02-29T18:22:18Z b                              unexpected_error_cnt:            0
incoming 2012-02-29T18:22:18Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-29T18:22:18Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
incoming 2012-02-29T18:22:18Z h                              unexpected_error_cnt:            0
incoming 2012-02-29T18:22:18Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-29T18:22:18Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           53
incoming 2012-02-29T18:22:18Z TOTALS                         unexpected_error_cnt:            0
29 Feb 2012 12:24:33,880  INFO [SERVICE_12421] -
incoming all time             b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             hold                                    new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,826,193               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,818,466               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:           53
incoming all time             TOTALS                         unexpected_error_cnt:            0
29 Feb 2012 12:24:33,881  INFO [SERVICE_12421] -
outgoing 2012-02-29T18:22:18Z work                                    new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-29T18:22:18Z work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
outgoing 2012-02-29T18:22:18Z work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z work                           upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z holdings                                new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-29T18:22:18Z holdings                                upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
outgoing 2012-02-29T18:22:18Z holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z holdings                       upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z expression                              new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-29T18:22:18Z expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
outgoing 2012-02-29T18:22:18Z expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z expression                     upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z manifestation                           new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-29T18:22:18Z manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
outgoing 2012-02-29T18:22:18Z manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z manifestation                  upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z other                                   new_act_cnt:            0               new_held_cnt:           12                new_del_cnt:            0
outgoing 2012-02-29T18:22:18Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-29T18:22:18Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z TOTALS                                  new_act_cnt:            0               new_held_cnt:           12                new_del_cnt:            0
outgoing 2012-02-29T18:22:18Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           79
outgoing 2012-02-29T18:22:18Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-29T18:22:18Z TOTALS                         upd_del_prev_act_cnt:           79      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
29 Feb 2012 12:24:33,882  INFO [SERVICE_12421] -
outgoing all time             work                                    new_act_cnt:    3,016,295               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:        6,416               upd_held_cnt:            0                upd_del_cnt:           13
outgoing all time             work                           upd_act_prev_act_cnt:        6,416      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    3,005,500               new_held_cnt:          349                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:          779               upd_held_cnt:            4                upd_del_cnt:           40
outgoing all time             holdings                       upd_act_prev_act_cnt:          749      upd_act_prev_held_cnt:           30       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            4     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    3,016,295               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:        6,416               upd_held_cnt:            0                upd_del_cnt:           13
outgoing all time             expression                     upd_act_prev_act_cnt:        6,416      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,826,193               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:           13
outgoing all time             manifestation                  upd_act_prev_act_cnt:        5,461      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:            0               new_held_cnt:           12                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            6
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            6      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   11,864,283               new_held_cnt:          361                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       19,072               upd_held_cnt:            4                upd_del_cnt:           85
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       19,042      upd_act_prev_held_cnt:           30       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            4     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:           85      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
29 Feb 2012 12:24:49,215  INFO [SERVICE_12421] -
                 other-deleted:           6                      e-active:   3,016,279                     e-deleted:          13
                      h-active:   3,005,486                     h-deleted:          40                        h-held:         323
                      m-active:   2,826,180                     m-deleted:          13                      w-active:   3,016,279
                     w-deleted:          13
                  total-active:  11,864,224                 total-deleted:          85                    total-held:         323
```

  * rules file
```
29 Feb 2012 12:21:09,492  INFO [SERVICE_12419] - Rules for Normalization:
29 Feb 2012 12:21:09,492  INFO [SERVICE_12419] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
29 Feb 2012 12:21:09,493  INFO [SERVICE_12419] - HTA=5818467, NTA=5818466 ** FAIL **
29 Feb 2012 12:21:09,493  INFO [SERVICE_12419] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
29 Feb 2012 12:21:09,493  INFO [SERVICE_12419] - HBA=2826194, NBA=2826193 ** FAIL **
29 Feb 2012 12:21:09,493  INFO [SERVICE_12419] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
29 Feb 2012 12:21:09,493  INFO [SERVICE_12419] - HHA=2992273, NHA=2992273 ** PASS **
29 Feb 2012 12:21:09,493  INFO [SERVICE_12419] - %%%
29 Feb 2012 12:24:49,344  INFO [SERVICE_12421] - %%%
29 Feb 2012 12:24:49,345  INFO [SERVICE_12421] - Rules for MarcToXCTransformation:
29 Feb 2012 12:24:49,345  INFO [SERVICE_12421] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
29 Feb 2012 12:24:49,345  INFO [SERVICE_12421] - NBA=2826193, TMA=2826193 ** PASS **
29 Feb 2012 12:24:49,345  INFO [SERVICE_12421] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
29 Feb 2012 12:24:49,345  INFO [SERVICE_12421] - NBD=0, TMD=0 ** PASS **
29 Feb 2012 12:24:49,345  INFO [SERVICE_12421] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
29 Feb 2012 12:24:49,345  INFO [SERVICE_12421] - NHA=2992273, THA=3005500, THH=349 ** PASS **
29 Feb 2012 12:24:49,346  INFO [SERVICE_12421] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
29 Feb 2012 12:24:49,346  INFO [SERVICE_12421] - TEA=3016295, TWA=3016295 ** PASS **
29 Feb 2012 12:24:49,346  INFO [SERVICE_12421] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
29 Feb 2012 12:24:49,346  INFO [SERVICE_12421] - TWA=3016295, TMA=2826193 ** PASS **
29 Feb 2012 12:24:49,346  INFO [SERVICE_12421] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
29 Feb 2012 12:24:49,346  INFO [SERVICE_12421] - TWA=3016295, TMA=2826193 ** PASS **
```


  * database
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select count(*) from xc_rochester_132_local.records"
+----------+
| count(*) |
+----------+
|  5818467 |
+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_132_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5818467 |
+--------+----------+
 
jbrand@xc-devel:~$ mysql -u root --password=* -e "select status, count(*) from xc_rochester_132_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5818414 |
| D      |       53 |
+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5818413 |
| D      |       53 |
+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| b    | A      |  2826180 |
| h    | A      |  2992233 |
| b    | D      |       13 |
| h    | D      |       40 |
+------+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| w    | A      |  3016279 |
| e    | A      |  3016279 |
| m    | A      |  2826180 |
| h    | A      |  3005486 |
| m    | D      |       13 |
| h    | D      |       40 |
| NULL | D      |        6 |
| w    | D      |       13 |
| e    | D      |       13 |
| h    | H      |      323 |
+------+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      | 11864224 |
| D      |       85 |
| H      |      323 |
+--------+----------+

```


# T1 #
  * - Update 1  (from 128.151.244.132)
  * On 2/08/12 we restarted our side. t1 from 128.151.244.132 run completed, numbers:

  * as reported by 132 (t1 run on --/--/-- on 132)
```
voyager@xcdevvoyoai > ./lucene_dbStatistics.sh
 log4j property file: /import/OAIToolkit/OAIToolkit.log4j.properties
2012-02-23 15:06:13,124 [main] (Logging.java:50) INFO  - Logging started in directory: log
2012-02-23 15:06:16,644 [main] (Importer.java:979) INFO  -  *************** Lucene Database Statistics ***************


2012-02-23 15:06:16,645 [main] (Importer.java:981) INFO  - Total records in the Lucene Database are: 5818467
2012-02-23 15:06:16,645 [main] (Importer.java:982) INFO  -  Bibliographic records: 2826194
2012-02-23 15:06:16,645 [main] (Importer.java:983) INFO  -       Deleted Bibliographic records: 0
2012-02-23 15:06:16,646 [main] (Importer.java:984) INFO  -  Authority records: 0
2012-02-23 15:06:16,646 [main] (Importer.java:985) INFO  -       Deleted Authority records: 0
2012-02-23 15:06:16,647 [main] (Importer.java:986) INFO  -  Holdings records: 2992273
2012-02-23 15:06:16,647 [main] (Importer.java:987) INFO  -       Deleted Holdings records: 0
2012-02-23 15:06:16,647 [main] (Importer.java:988) INFO  -  Classification records: 0
2012-02-23 15:06:16,648 [main] (Importer.java:989) INFO  -       Deleted Classification records: 0
2012-02-23 15:06:16,648 [main] (Importer.java:990) INFO  -  Community information records: 0
2012-02-23 15:06:16,648 [main] (Importer.java:991) INFO  -       Deleted Community information records: 0
2012-02-23 15:06:16,648 [main] (Importer.java:992) INFO  -  Deleted records: 0
```

```
   * Curl all matches lucene_dbStatistics ==> <==
   * Total (matches lucene_dbStatistics above, ==> <==):
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
 <resumptionToken cursor="1000" completeListSize="5818467">7|999|1000|5818467|0</resumptionToken>

```

  * Holds:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
<resumptionToken cursor="1000" completeListSize="2992273">8|1738|1000|2992273|0</resumptionToken>

```

  * Bibs:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
<resumptionToken cursor="1000" completeListSize="2826194">9|2346|1000|2826194|0</resumptionToken>

```

  * Auth:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'
<nothing returned>
```




  * as reported by 192.17.55.225
    * harvestIn file
```
20 Feb 2012 14:10:01,597  INFO [REPOSITORY_1425] -
incoming 2012-02-20T20:07:48Z b                                       new_act_cnt:        4,780               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:07:48Z b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:07:48Z b                              unexpected_error_cnt:            0
incoming 2012-02-20T20:07:48Z h                                       new_act_cnt:        8,637               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:07:48Z h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:07:48Z h                              unexpected_error_cnt:            0
incoming 2012-02-20T20:07:48Z TOTALS                                  new_act_cnt:       13,417               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:07:48Z TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:07:48Z TOTALS                         unexpected_error_cnt:            0
20 Feb 2012 14:10:01,598  INFO [REPOSITORY_1425] -
incoming all time             b                                       new_act_cnt:    2,826,194               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,818,467               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
20 Feb 2012 14:10:29,141  INFO [REPOSITORY_1425] -
rochester_132_local:bib-active:   2,826,194rochester_132_local:hold-active:   2,992,273
                  other-active:           0
                  total-active:   5,818,467
```

  * normalization log
```

incoming 2012-02-20T20:11:04Z b                                       new_act_cnt:        4,780               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:11:04Z b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:11:04Z b                              unexpected_error_cnt:            0
incoming 2012-02-20T20:11:04Z h                                       new_act_cnt:        8,637               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:11:04Z h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:11:04Z h                              unexpected_error_cnt:            0
incoming 2012-02-20T20:11:04Z TOTALS                                  new_act_cnt:       13,417               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:11:04Z TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:11:04Z TOTALS                         unexpected_error_cnt:            0
20 Feb 2012 14:14:45,939  INFO [SERVICE_14210] -
incoming all time             b                                       new_act_cnt:    2,826,194               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,818,467               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
20 Feb 2012 14:14:45,940  INFO [SERVICE_14210] -
outgoing 2012-02-20T20:11:04Z b                                       new_act_cnt:        4,780               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-20T20:11:04Z b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-20T20:11:04Z b                              upd_act_prev_act_cnt:        5,461      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:11:04Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:11:04Z b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-20T20:11:04Z h                                       new_act_cnt:        8,637               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-20T20:11:04Z h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-20T20:11:04Z h                              upd_act_prev_act_cnt:          518      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:11:04Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:11:04Z h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-20T20:11:04Z TOTALS                                  new_act_cnt:       13,417               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-20T20:11:04Z TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-20T20:11:04Z TOTALS                         upd_act_prev_act_cnt:        5,979      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:11:04Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:11:04Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
20 Feb 2012 14:14:45,941  INFO [SERVICE_14210] -
outgoing all time             b                                       new_act_cnt:    2,826,193               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             b                              upd_act_prev_act_cnt:        5,461      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             h                              upd_act_prev_act_cnt:          518      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    5,818,466               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:        5,979      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
20 Feb 2012 14:14:52,745  INFO [SERVICE_14210] -
                      b-active:   2,826,193                      h-active:   2,992,273
                  total-active:   5,818,466

```

  * transformation log
```
incoming 2012-02-20T20:15:50Z hold                                    new_act_cnt:        8,637               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:15:50Z hold                                    upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:15:50Z hold                           unexpected_error_cnt:            0
incoming 2012-02-20T20:15:50Z bib                                     new_act_cnt:        4,780               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:15:50Z bib                                     upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:15:50Z bib                            unexpected_error_cnt:            0
incoming 2012-02-20T20:15:50Z TOTALS                                  new_act_cnt:       13,417               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-20T20:15:50Z TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-20T20:15:50Z TOTALS                         unexpected_error_cnt:            0
20 Feb 2012 14:22:03,724  INFO [SERVICE_14214] -
incoming all time             hold                                    new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,826,193               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,818,466               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
20 Feb 2012 14:22:03,725  INFO [SERVICE_14214] -
outgoing 2012-02-20T20:15:50Z work                                    new_act_cnt:        5,736               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-20T20:15:50Z work                                    upd_act_cnt:        6,416               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-20T20:15:50Z work                           upd_act_prev_act_cnt:        6,416      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z holdings                                new_act_cnt:        8,379               new_held_cnt:          297                new_del_cnt:            0
outgoing 2012-02-20T20:15:50Z holdings                                upd_act_cnt:          758               upd_held_cnt:            4                upd_del_cnt:            0
outgoing 2012-02-20T20:15:50Z holdings                       upd_act_prev_act_cnt:          749      upd_act_prev_held_cnt:            9       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z holdings                      upd_held_prev_act_cnt:            4     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z expression                              new_act_cnt:        5,736               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-20T20:15:50Z expression                              upd_act_cnt:        6,416               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-20T20:15:50Z expression                     upd_act_prev_act_cnt:        6,416      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z manifestation                           new_act_cnt:        4,780               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-20T20:15:50Z manifestation                           upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-20T20:15:50Z manifestation                  upd_act_prev_act_cnt:        5,461      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z other                                   new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-20T20:15:50Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            6
outgoing 2012-02-20T20:15:50Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z other                          upd_del_prev_act_cnt:            6      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z TOTALS                                  new_act_cnt:       24,631               new_held_cnt:          297                new_del_cnt:            0
outgoing 2012-02-20T20:15:50Z TOTALS                                  upd_act_cnt:       19,051               upd_held_cnt:            4                upd_del_cnt:            6
outgoing 2012-02-20T20:15:50Z TOTALS                         upd_act_prev_act_cnt:       19,042      upd_act_prev_held_cnt:            9       upd_act_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z TOTALS                        upd_held_prev_act_cnt:            4     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-20T20:15:50Z TOTALS                         upd_del_prev_act_cnt:            6      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
20 Feb 2012 14:22:03,727  INFO [SERVICE_14214] -
outgoing all time             work                                    new_act_cnt:    3,016,295               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:        6,416               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:        6,416      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    3,005,500               new_held_cnt:          349                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:          779               upd_held_cnt:            4                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:          749      upd_act_prev_held_cnt:           30       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            4     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    3,016,295               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:        6,416               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:        6,416      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,826,193               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:        5,461      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            6
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            6      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   11,864,283               new_held_cnt:          349                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       19,072               upd_held_cnt:            4                upd_del_cnt:            6
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       19,042      upd_act_prev_held_cnt:           30       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            4     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            6      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
20 Feb 2012 14:22:19,152  INFO [SERVICE_14214] -
                 other-deleted:           6                      e-active:   3,016,292                      h-active:   3,005,526
                        h-held:         323                      m-active:   2,826,193                      w-active:   3,016,292
                  total-active:  11,864,303                 total-deleted:           6                    total-held:         323
```

  * rules file
```
20 Feb 2012 14:14:52,781  INFO [SERVICE_14210] - Rules for Normalization:
20 Feb 2012 14:14:52,781  INFO [SERVICE_14210] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
20 Feb 2012 14:14:52,781  INFO [SERVICE_14210] - HTA=5818467, NTA=5818466 ** FAIL **
20 Feb 2012 14:14:52,781  INFO [SERVICE_14210] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
20 Feb 2012 14:14:52,781  INFO [SERVICE_14210] - HBA=2826194, NBA=2826193 ** FAIL **
20 Feb 2012 14:14:52,782  INFO [SERVICE_14210] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
20 Feb 2012 14:14:52,782  INFO [SERVICE_14210] - HHA=2992273, NHA=2992273 ** PASS **
20 Feb 2012 14:14:52,782  INFO [SERVICE_14210] - %%%
20 Feb 2012 14:22:19,194  INFO [SERVICE_14214] - %%%
20 Feb 2012 14:22:19,195  INFO [SERVICE_14214] - Rules for MarcToXCTransformation:
20 Feb 2012 14:22:19,195  INFO [SERVICE_14214] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
20 Feb 2012 14:22:19,195  INFO [SERVICE_14214] - NBA=2826193, TMA=2826193 ** PASS **
20 Feb 2012 14:22:19,195  INFO [SERVICE_14214] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
20 Feb 2012 14:22:19,195  INFO [SERVICE_14214] - NBD=0, TMD=0 ** PASS **
20 Feb 2012 14:22:19,196  INFO [SERVICE_14214] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
20 Feb 2012 14:22:19,196  INFO [SERVICE_14214] - NHA=2992273, THA=3005500, THH=349 ** PASS **
20 Feb 2012 14:22:19,196  INFO [SERVICE_14214] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
20 Feb 2012 14:22:19,196  INFO [SERVICE_14214] - TEA=3016295, TWA=3016295 ** PASS **
20 Feb 2012 14:22:19,196  INFO [SERVICE_14214] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
20 Feb 2012 14:22:19,196  INFO [SERVICE_14214] - TWA=3016295, TMA=2826193 ** PASS **
20 Feb 2012 14:22:19,196  INFO [SERVICE_14214] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
20 Feb 2012 14:22:19,197  INFO [SERVICE_14214] - TWA=3016295, TMA=2826193 ** PASS **
```


  * database
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select count(*) from xc_rochester_132_local.records"
+----------+
| count(*) |
+----------+
|  5818467 |
+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_132_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5818467 |
+--------+----------+
 
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_132_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5818467 |
+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5818466 |
+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| b    | A      |  2826193 |
| h    | A      |  2992273 |
+------+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| w    | A      |  3016292 |
| e    | A      |  3016292 |
| m    | A      |  2826193 |
| h    | A      |  3005526 |
| NULL | D      |        6 |
| h    | H      |      323 |
+------+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      | 11864303 |
| D      |        6 |
| H      |      323 |
+--------+----------+

```






# T0 #
  * Initial Load  (from 128.151.244.132)
  * On 2/08/12 we restarted our side. t0 from 128.151.244.132 run completed, numbers:

  * as reported by 132 (t0 run on --/--/-- on 132)
```
voyager@xcdevvoyoai > ./lucene_dbStatistics.sh
 log4j property file: /import/OAIToolkit/OAIToolkit.log4j.properties
2012-02-13 14:51:51,254 [main] (Logging.java:50) INFO  - Logging started in directory: log
2012-02-13 14:51:54,199 [main] (Importer.java:979) INFO  -  *************** Lucene Database Statistics ***************


2012-02-13 14:51:54,200 [main] (Importer.java:981) INFO  - Total records in the Lucene Database are: 5805050
2012-02-13 14:51:54,200 [main] (Importer.java:982) INFO  -  Bibliographic records: 2821414
2012-02-13 14:51:54,200 [main] (Importer.java:983) INFO  -       Deleted Bibliographic records: 0
2012-02-13 14:51:54,200 [main] (Importer.java:984) INFO  -  Authority records: 0
2012-02-13 14:51:54,200 [main] (Importer.java:985) INFO  -       Deleted Authority records: 0
2012-02-13 14:51:54,202 [main] (Importer.java:986) INFO  -  Holdings records: 2983636
2012-02-13 14:51:54,202 [main] (Importer.java:987) INFO  -       Deleted Holdings records: 0
2012-02-13 14:51:54,202 [main] (Importer.java:988) INFO  -  Classification records: 0
2012-02-13 14:51:54,202 [main] (Importer.java:989) INFO  -       Deleted Classification records: 0
2012-02-13 14:51:54,202 [main] (Importer.java:990) INFO  -  Community information records: 0
2012-02-13 14:51:54,203 [main] (Importer.java:991) INFO  -       Deleted Community information records: 0
2012-02-13 14:51:54,203 [main] (Importer.java:992) INFO  -  Deleted records: 0

```

```
   * Curl all matches lucene_dbStatistics ==> <==
   * Total (matches lucene_dbStatistics above, ==> <==):
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
 <resumptionToken cursor="1000" completeListSize="5805050">3|999|1000|5805050|1</resumptionToken>
```

  * Holds:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="1000" completeListSize="2983636">4|1738|1000|2983636|0</resumptionToken>  
```

  * Bibs:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
   <resumptionToken cursor="1000" completeListSize="2821414">5|2346|1000|2821414|0</resumptionToken>
```

  * Auth:
```
$ curl -s 'http://128.151.244.132:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'

<** returned nothing **>

```


  * as reported by 192.17.55.225
    * harvestIn file
```
incoming 2012-02-09T14:24:30Z b                                       new_act_cnt:    2,821,414               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T14:24:30Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T14:24:30Z b                              unexpected_error_cnt:            0
incoming 2012-02-09T14:24:30Z h                                       new_act_cnt:    2,983,636               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T14:24:30Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T14:24:30Z h                              unexpected_error_cnt:            0
incoming 2012-02-09T14:24:30Z TOTALS                                  new_act_cnt:    5,805,050               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T14:24:30Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T14:24:30Z TOTALS                         unexpected_error_cnt:            0
09 Feb 2012 09:43:08,470  INFO [REPOSITORY_8524] -
incoming all time             b                                       new_act_cnt:    2,821,414               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,983,636               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,805,050               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
09 Feb 2012 09:43:29,985  INFO [REPOSITORY_8524] -
rochester_132_local:bib-active:   2,821,414rochester_132_local:hold-active:   2,983,636
                  other-active:           0
                  total-active:   5,805,050
```

  * normalization log
```
incoming 2012-02-09T15:43:31Z b                                       new_act_cnt:    2,821,414               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T15:43:31Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T15:43:31Z b                              unexpected_error_cnt:            0
incoming 2012-02-09T15:43:31Z h                                       new_act_cnt:    2,983,636               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T15:43:31Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T15:43:31Z h                              unexpected_error_cnt:            0
incoming 2012-02-09T15:43:31Z TOTALS                                  new_act_cnt:    5,805,050               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T15:43:31Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T15:43:31Z TOTALS                         unexpected_error_cnt:            0
09 Feb 2012 11:40:17,245  INFO [SERVICE_9543] -
incoming all time             b                                       new_act_cnt:    2,821,414               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,983,636               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,805,050               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
09 Feb 2012 11:40:17,246  INFO [SERVICE_9543] -
outgoing 2012-02-09T15:43:31Z b                                       new_act_cnt:    2,821,413               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-09T15:43:31Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-09T15:43:31Z b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-09T15:43:31Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-09T15:43:31Z b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-09T15:43:31Z h                                       new_act_cnt:    2,983,636               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-09T15:43:31Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-09T15:43:31Z h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-09T15:43:31Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-09T15:43:31Z h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-09T15:43:31Z TOTALS                                  new_act_cnt:    5,805,049               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-09T15:43:31Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-09T15:43:31Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-09T15:43:31Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-09T15:43:31Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
09 Feb 2012 11:40:17,246  INFO [SERVICE_9543] -
outgoing all time             b                                       new_act_cnt:    2,821,413               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,983,636               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    5,805,049               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
09 Feb 2012 11:40:22,615  INFO [SERVICE_9543] -
                      b-active:   2,821,413                      h-active:   2,983,636
                  total-active:   5,805,049
```

  * transformation log
```
incoming 2012-02-09T17:40:31Z hold                                    new_act_cnt:    2,983,636               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T17:40:31Z hold                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T17:40:31Z hold                           unexpected_error_cnt:            0
incoming 2012-02-09T17:40:31Z bib                                     new_act_cnt:    2,821,413               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T17:40:31Z bib                                     upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T17:40:31Z bib                            unexpected_error_cnt:            0
incoming 2012-02-09T17:40:31Z TOTALS                                  new_act_cnt:    5,805,049               new_held_cnt:            0                new_del_cnt:            0
incoming 2012-02-09T17:40:31Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2012-02-09T17:40:31Z TOTALS                         unexpected_error_cnt:            0
09 Feb 2012 13:41:00,192  INFO [SERVICE_11540] -
incoming all time             hold                                    new_act_cnt:    2,983,636               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,821,413               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    5,805,049               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
09 Feb 2012 13:41:00,193  INFO [SERVICE_11540] -
outgoing 2012-02-09T17:40:31Z work                                    new_act_cnt:    3,010,559               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-09T17:40:31Z work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-09T17:40:31Z work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z holdings                                new_act_cnt:    2,997,121               new_held_cnt:           52                new_del_cnt:            0
outgoing 2012-02-09T17:40:31Z holdings                                upd_act_cnt:           21               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-09T17:40:31Z holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:           21       upd_act_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z expression                              new_act_cnt:    3,010,559               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-09T17:40:31Z expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-09T17:40:31Z expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z manifestation                           new_act_cnt:    2,821,413               new_held_cnt:            0                new_del_cnt:            0
outgoing 2012-02-09T17:40:31Z manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-09T17:40:31Z manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z TOTALS                                  new_act_cnt:   11,839,652               new_held_cnt:           52                new_del_cnt:            0
outgoing 2012-02-09T17:40:31Z TOTALS                                  upd_act_cnt:           21               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2012-02-09T17:40:31Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:           21       upd_act_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2012-02-09T17:40:31Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
09 Feb 2012 13:41:00,194  INFO [SERVICE_11540] -
outgoing all time             work                                    new_act_cnt:    3,010,559               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,997,121               new_held_cnt:           52                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:           21               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:           21       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    3,010,559               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,821,413               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   11,839,652               new_held_cnt:           52                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:           21               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:           21       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
09 Feb 2012 13:41:11,250  INFO [SERVICE_11540] -
                      e-active:   3,010,559                      h-active:   2,997,142                        h-held:          31
                      m-active:   2,821,413                      w-active:   3,010,559
                  total-active:  11,839,673                    total-held:          31

```

  * rules file
```
09 Feb 2012 11:40:22,708  INFO [SERVICE_9543] - Rules for Normalization:
09 Feb 2012 11:40:22,708  INFO [SERVICE_9543] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
09 Feb 2012 11:40:22,708  INFO [SERVICE_9543] - HTA=5805050, NTA=5805049 ** FAIL **
09 Feb 2012 11:40:22,708  INFO [SERVICE_9543] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
09 Feb 2012 11:40:22,709  INFO [SERVICE_9543] - HBA=2821414, NBA=2821413 ** FAIL **
09 Feb 2012 11:40:22,709  INFO [SERVICE_9543] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
09 Feb 2012 11:40:22,709  INFO [SERVICE_9543] - HHA=2983636, NHA=2983636 ** PASS **
09 Feb 2012 11:40:22,709  INFO [SERVICE_9543] - %%%
09 Feb 2012 13:41:11,528  INFO [SERVICE_11540] - %%%
09 Feb 2012 13:41:11,529  INFO [SERVICE_11540] - Rules for MarcToXCTransformation:
09 Feb 2012 13:41:11,529  INFO [SERVICE_11540] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
09 Feb 2012 13:41:11,529  INFO [SERVICE_11540] - NBA=2821413, TMA=2821413 ** PASS **
09 Feb 2012 13:41:11,529  INFO [SERVICE_11540] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
09 Feb 2012 13:41:11,530  INFO [SERVICE_11540] - NBD=0, TMD=0 ** PASS **
09 Feb 2012 13:41:11,530  INFO [SERVICE_11540] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
09 Feb 2012 13:41:11,530  INFO [SERVICE_11540] - NHA=2983636, THA=2997121, THH=52 ** PASS **
09 Feb 2012 13:41:11,530  INFO [SERVICE_11540] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
09 Feb 2012 13:41:11,530  INFO [SERVICE_11540] - TEA=3010559, TWA=3010559 ** PASS **
09 Feb 2012 13:41:11,530  INFO [SERVICE_11540] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
09 Feb 2012 13:41:11,530  INFO [SERVICE_11540] - TWA=3010559, TMA=2821413 ** PASS **
09 Feb 2012 13:41:11,530  INFO [SERVICE_11540] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
09 Feb 2012 13:41:11,531  INFO [SERVICE_11540] - TWA=3010559, TMA=2821413 ** PASS **
```

  * no output errors  (tracking because they impact rules above)
    * normalization
```
oai:mst.rochester.edu:MetadataServicesToolkit/rochester_132_local/4910505
Schema: marc21
Repository: rochester_132_local
Error: Produced no output 
```

  * database
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select count(*) from xc_rochester_132_local.records"
+----------+
| count(*) |
+----------+
|  5805050 |
+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_132_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5805050 |
+--------+----------+
 
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_132_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5805050 |
+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  5805049 |
+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| b    | A      |  2821413 |
| h    | A      |  2983636 |
+------+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| w    | A      |  3010559 |
| e    | A      |  3010559 |
| m    | A      |  2821413 |
| h    | A      |  2997142 |
| h    | H      |       31 |
+------+--------+----------+

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      | 11839673 |
| H      |       31 |
+--------+----------+


```