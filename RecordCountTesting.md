  * 6/23/11 Processing set of deleted files on 137, we used our file harvester to get these and placed them on the filesystem of 146, harvested and checked the numbers throughout the system.

```
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh
0 [main] INFO lucene_dbStatistics  -  *************** Lucene Database Statistics ***************

2 [main] INFO lucene_dbStatistics  - Total records in the Lucene Database are: 6688036
2 [main] INFO lucene_dbStatistics  -  Bibliographic records: 2632308
2 [main] INFO lucene_dbStatistics  -     Deleted Bibliographic records: 3578
2 [main] INFO lucene_dbStatistics  -  Authority records: 1254929
2 [main] INFO lucene_dbStatistics  -     Deleted Authority records: 5
2 [main] INFO lucene_dbStatistics  -  Holdings records: 2796252
3 [main] INFO lucene_dbStatistics  -     Deleted Holdings records: 964
3 [main] INFO lucene_dbStatistics  -  Classification records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Classification records: 0
3 [main] INFO lucene_dbStatistics  -  Community information records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Community information records: 0
3 [main] INFO lucene_dbStatistics  -  Deleted records: 4547
```

```
  * Overall
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6683489">31|5005|5000|6683489</resumptionToken>
```
  * Check -> 6688036 - 4547 = 6683489

  * Holds:
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2796252">32|8595|5000|2796252</resumptionToken>
```
    * Check -> 2796252 = 2796252

  * Bibs:
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2632308">33|11814|5000|2632308</resumptionToken>
```
    * Check -> 2632308 = 2632308

  * Auth:
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="1254929">34|5429793|5000|1254929</resumptionToken>
```
    * Check -> 1254929 = 1254929
  * Curl total = curl auth+hold+bib


  * as reported by 146 mst:

```
mysql -u root --password=* -D xc_rochester_137_local -e "select count(*) from records";
+----------+
| count(*) |
+----------+
|  6593297 |
+----------+
```

```
devuser@xcmst > mysql -u root --password=* -D xc_rochester_137_local -e "select status, count(*) from records group by status order by status";
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6588750 |
| D      |     4547 |
+--------+----------+
```
  * problem difference between overall incoming seen in mst vs. curl/lucene, difference traced to authorities.  Ben traced this to Java heap space exceptions during the harvest when bringing in the authority records.

```
mysql -u root --password=* -e "select status, count(*) c from xc_marcnormalization.records group by status order by status";
+--------+---------+
| status | c       |
+--------+---------+
| A      | 6588749 |
| D      |    1173 |
+--------+---------+
```

```
mysql -u root --password=* -e "select type, status, count(*) c from xc_marcnormalization.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| NULL | A      | 1160190 |
| NULL | D      |       1 |
| b    | A      | 2632307 |
| b    | D      |     770 |
| h    | A      | 2796252 |
| h    | D      |     402 |
+------+--------+---------+
```

```
devuser@xcmst > /usr/local/mysql/bin/mysql --user=root --password=* -e "select status, count(*) c from xc_marctoxctransformation.records group by status order by status"
+--------+----------+
| status | c        |
+--------+----------+
| A      | 10936516 |
| D      |     2712 |
| H      |   120578 |
+--------+----------+
```

```
devuser@xcmst > mysql -u root --password=* -e "select type, status, count(*) c from xc_marctoxctransformation.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| NULL | H      |     410 |
| e    | A      | 2810170 |
| e    | D      |     770 |
| h    | A      | 2683877 |
| h    | D      |     402 |
| h    | H      |  120168 |
| m    | A      | 2632299 |
| m    | D      |     770 |
| w    | A      | 2810170 |
| w    | D      |     770 |
+------+--------+---------+
```


  * marcnormalization.txt:
```
                  other-active:   1,160,190                 other-deleted:           1                      b-active:   2,632,307
                     b-deleted:         770                      h-active:   2,796,252                     h-deleted:         402
                  total-active:   6,588,749                 total-deleted:       1,173

```

  * Normalization numbers make sense = in is 1> than out, because of 'no output' error.

  * (SOLR-agrees) Should be one output for every input, off by 1, but this is okay, because 1 record had this error:
Error: 2-108:Produced no output , 2-108:Produced no output


  * marctoxctransformation.txt:
```
                    other-held:         410                      e-active:   2,810,170                     e-deleted:         770
                      h-active:   2,683,877                     h-deleted:         402                        h-held:     120,168
                      m-active:   2,632,299                     m-deleted:         770                      w-active:   2,810,170
                     w-deleted:         770
                  total-active:  10,936,516                 total-deleted:       2,712                    total-held:     120,578
```


  * bibs from marcnorm 2633077 = 2632307 + 770, manifestations 2632299  (good, should be 1:1, off 8, 8 no output records)
  * works = 2810170+770, expressions = 2810170+770, manifestations 2632299+770 (good, each are >= manifestations)
  * (good expressions = works?)
  * holdings in = 2796252(a)+402(h)=2796654, holdings out =  2683877 + 402+120168 = 2804447 (perhaps should be ==, but holdings out are > 7793 holdings in, could be explained by 852, and could be correct.  Will verify with correct fix for 852holding processing.)


---





  * 6/23/11 Did some testing of our bug fixes using harvest from file.  Update results good.    Note, the focus was tie-out of numbers within the MST since we had been working on fixing errors within the MST.

```
mysql -u root --password=root -e "select type, status, count(*) c from xc_marctoxctransformation.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| e    | A      | 2810940 |
| h    | A      | 2684689 |
| h    | H      |  120168 |
| m    | A      | 2633069 |
| w    | A      | 2810940 |
+------+--------+---------+
devuser@xcmst > !521
mysql -u root --password=root -e "select status, count(*) c from xc_marcnormalization.records group by status order by status";
+--------+---------+
| status | c       |
+--------+---------+
| A      | 6589922 |
+--------+---------+
devuser@xcmst > !520
mysql -u root --password=root -D xc_rochester_137_local -e "select count(*), type, status from records group by type, status order by type, status";
+----------+------+--------+
| count(*) | type | status |
+----------+------+--------+
|  6589923 | NULL | A      |
|        3 | NULL | D      |
+----------+------+--------+
devuser@xcmst > !519
mysql -u root --password=root -e "select type, status, count(*) c from xc_marcnormalization.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| NULL | A      | 1160191 |
| b    | A      | 2633077 |
| h    | A      | 2796654 |
+------+--------+---------+
```
Note,
b from norm 8> m  good, 8 no output errors
w = e                    good
w,e >= m              good
h from trans > h from norm  TBD on whether all caused by 852.


---


  * 6/23/11 Did some testing of our bug fixes using harvest from file. Initial results good.  Note, the focus was tie-out of numbers within the MST since we had been working on fixing errors within the MST.

```

devuser@xcmst > mysql -u root --password=* -D xc_rochester_137_local -e "select count(*), type, status from records group by type, status order by type, status";
+----------+------+--------+
| count(*) | type | status |
+----------+------+--------+
|  6584985 | NULL | A      |
|        3 | NULL | D      |
+----------+------+--------+

devuser@xcmst > mysql -u root --password=* -e "select status, count(*) c from xc_marcnormalization.records group by status order by status";
+--------+---------+
| status | c       |
+--------+---------+
| A      | 6584984 |
+--------+---------+

Note, fine,1 error record


devuser@xcmst > mysql -u root --password=* -e "select type, status, count(*) c from xc_marcnormalization.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| NULL | A      | 1160191 |
| b    | A      | 2630622 |
| h    | A      | 2794171 |
+------+--------+---------+


devuser@xcmst > mysql -u root --password=* -e "select type, status, count(*) c from xc_marctoxctransformation.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| e    | A      | 2808321 |
| h    | A      | 2682218 |
| h    | H      |  120156 |
| m    | A      | 2630614 |
| w    | A      | 2808321 |
+------+--------+---------+
```

Note,
b from norm 8> m  good, 8 no output errors
w = e                    good
w,e >= m              good
h from trans > h from norm  TBD on whether all caused by 852.

  * Ran 1st update, Ralph updated 137 on 6/13, ran harvest on 136 against 137 on 6/14/11 ---

```
devuser@xcmst2 >  mysql -u root --password=root -D xc_rochester_137 -e "select count(*), status from records group by status ";
+----------+--------+
| count(*) | status |
+----------+--------+
|  6684662 | A      |
|        3 | D      |
+----------+--------+
```
  * (SOLR-agrees)

```
devuser@xcmst2 > mysql -u root --password=root -D xc_rochester_137 -e "select count(*), type, status from records group by type, status order by type, status";
+----------+------+--------+
| count(*) | type | status |
+----------+------+--------+
|  6684662 | NULL | A      |
|        3 | NULL | D      |
+----------+------+--------+
```
  * (SOLR-agrees)

```
devuser@xcmst2 > mysql -u root --password=root -e "select status, count(*) c from xc_marcnormalization.records group by status order by status";
+--------+---------+
| status | c       |
+--------+---------+
| A      | 6684661 |
+--------+---------+
```
  * (SOLR-agrees)

```
devuser@xcmst2 > mysql -u root --password=root -e "select type, status, count(*) c from xc_marcnormalization.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| NULL | A      | 1254930 |
| b    | A      | 2633077 |
| h    | A      | 2796654 |
+------+--------+---------+
```
  * (SOLR-agrees) Should be one output for every input, off by 1, but this is okay, because 1 record had this error:
Error: 2-108:Produced no output , 2-108:Produced no output

```
devuser@xcmst2 > mysql -u root --password=root -e "select status, count(*) c from xc_marctoxctransformation.records group by status order by status";
+--------+----------+
| status | c        |
+--------+----------+
| A      | 10932998 |
| H      |   120146 |
+--------+----------+
```

```
devuser@xcmst2 > mysql -u root --password=root -e "select type, status, count(*) c from xc_marctoxctransformation.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| e    | A      | 2809491 |
| h    | A      | 2682540 |
| h    | H      |  120146 |
| m    | A      | 2631579 |
| w    | A      | 2809388 |
+------+--------+---------+
```
  * bibs from marcnorm 2633077, manifestations 2631579  (bad, should be 1:1, off 1498, hmm during yesterday's run, there were 8 less manifestations, now we are plus 1498?)
  * works = 2809388, expressions = 2809491, manifestations 2631579 (good, each are >= manifestations)
  * (bad expressions != works?)
  * holdings in = 2796654, holdings out =  2682540 + 120146 = 2802686 (bad, should be ==, but holdings out are > 6032 holdings in, similar to yesterday)


  * logs:
    * harvestIn log:
```
14 Jun 2011 14:04:14,043  INFO [Thread-14] -
incoming all time             rochester_137:auth                      new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             rochester_137:auth                      upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_137:auth             unexpected_error_cnt:            0
incoming all time             rochester_137:hold                      new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             rochester_137:hold                      upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_137:hold             unexpected_error_cnt:            0
incoming all time             rochester_137:bib                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:            0
incoming all time             rochester_137:bib                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_137:bib              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
14 Jun 2011 14:05:26,183  INFO [Thread-14] -
      rochester_137:bib-active:   2,633,078     rochester_137:auth-active:   1,254,930    rochester_137:auth-deleted:           3
     rochester_137:hold-active:   2,796,654
                  other-active:           0                 other-deleted:           0
                  total-active:   6,684,662                 total-deleted:           3
```

  * normalization log:
```
14 Jun 2011 14:10:46,833  INFO [Thread-19] -
outgoing all time             hold                                    new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             hold                                    upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             hold                           upd_act_prev_act_cnt:       10,016      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             hold                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             hold                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             bib                                     new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             bib                                     upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             bib                            upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             bib                           upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             bib                            upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       20,029      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
14 Jun 2011 14:10:59,710  INFO [Thread-19] -
                  other-active:   1,254,930                      b-active:   2,633,077                      h-active:   2,796,654
                  total-active:   6,684,661
```

  * transformation log:
```
14 Jun 2011 14:20:42,659  INFO [Thread-20] -
outgoing all time             work                                    new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,684,689               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:       10,014               upd_held_cnt:            2                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,633,069               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   10,939,638               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       40,111               upd_held_cnt:            2                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
14 Jun 2011 14:21:10,938  INFO [Thread-20] -
e-active:   2,809,491                      h-active:   2,682,540                        h-held:     120,146
                      m-active:   2,631,579                      w-active:   2,809,388
                  total-active:  10,932,998                    total-held:     120,146
```


  * (login as voyager onto 137:)
```
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh
0 [main] INFO lucene_dbStatistics  -  *************** Lucene Database Statistics ***************


2 [main] INFO lucene_dbStatistics  - Total records in the Lucene Database are: 6684665
2 [main] INFO lucene_dbStatistics  -  Bibliographic records: 2633078
2 [main] INFO lucene_dbStatistics  -     Deleted Bibliographic records: 0
2 [main] INFO lucene_dbStatistics  -  Authority records: 1254930
2 [main] INFO lucene_dbStatistics  -     Deleted Authority records: 3
2 [main] INFO lucene_dbStatistics  -  Holdings records: 2796654
3 [main] INFO lucene_dbStatistics  -     Deleted Holdings records: 0
3 [main] INFO lucene_dbStatistics  -  Classification records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Classification records: 0
3 [main] INFO lucene_dbStatistics  -  Community information records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Community information records: 0
3 [main] INFO lucene_dbStatistics  -  Deleted records: 3
```

  * curl
    * total
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6684662">15|4999|5000|6684662</resumptionToken>
```
    * bibs
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2633078">16|11809|5000|2633078</resumptionToken>
```

  * holds
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2796654">17|8588|5000|2796654</resumptionToken>
```

  * auths
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="1254930">18|5429793|5000|1254930</resumptionToken>
```

  * total -> 1254930 + 2796654 + 2633078 = 6684662 (good)


---

  * Start 6/13/10 end to end test using 137
    * Complete harvest on Friday:
```
devuser@xcmst2 > mysql -u root --password=root -D xc_rochester_137 -e "select count(*), status from records group by status ";
+----------+--------+
| count(*) | status |
+----------+--------+
|  6679724 | A      |
|        3 | D      |
+----------+--------+
```
  * (SOLR-agrees)

```
devuser@xcmst2 > mysql -u root --password=root -D xc_rochester_137 -e "select count(*), type, status from records group by type, status order by type, status";
+----------+------+--------+
| count(*) | type | status |
+----------+------+--------+
|  6679724 | NULL | A      |
|        3 | NULL | D      |
+----------+------+--------+
```
  * (SOLR-agrees)

```
devuser@xcmst2 > mysql -u root --password=root -e "select status, count(*) c from xc_marcnormalization.records group by status order by status";
+--------+---------+
| status | c       |
+--------+---------+
| A      | 6679723 |
+--------+---------+
```
  * (SOLR - agrees)

```
devuser@xcmst2 > mysql -u root --password=root -e "select type, status, count(*) c from xc_marcnormalization.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| NULL | A      | 1254930 |
| b    | A      | 2630622 |
| h    | A      | 2794171 |
+------+--------+---------+
```
  * (SOLR - agrees) Should be one output for every input, off by 1, but this is okay, because 1 record had this error: Error: 2-108:Produced no output , 2-108:Produced no output

```
devuser@xcmst2 > mysql -u root --password=root -e "select status, count(*) c from xc_marctoxctransformation.records group by status order by status";
+--------+----------+
| status | c        |
+--------+----------+
| A      | 10929474 |
| H      |   120156 |
+--------+----------+
```
  * 11,049,630 = total of above(SOLR - I don't see the 120156, the A number agrees)

```
devuser@xcmst2 > mysql -u root --password=root -e "select type, status, count(*) c from xc_marctoxctransformation.records group by type, status order by type, status";
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| e    | A      | 2808321 |
| h    | A      | 2682218 |
| h    | H      |  120156 |
| m    | A      | 2630614 |
| w    | A      | 2808321 |
+------+--------+---------+
```

  * bibs 2630622, manifestations 2630614  (should be 1:1, off 8) okay because 8 of these errors: Error: 1-102:Produced no output , 1-102:Produced no output
  * predecessor on one of these: Error: 2-107:Invalid 035 Data Field , 2-107:Invalid 035 Data Field
  * works, expressions 2808321, manifestions 2630614 (good, >= man.)
  * h in = 2794171, h out = 120156+2682218 = 2802374 (in != out, and it should?) (8203 difference)


  * (login as voyager onto 137:)
```
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh
0 [main] INFO lucene_dbStatistics  -  *************** Lucene Database Statistics ***************


1 [main] INFO lucene_dbStatistics  - Total records in the Lucene Database are: 6679727
1 [main] INFO lucene_dbStatistics  -  Bibliographic records: 2630623
2 [main] INFO lucene_dbStatistics  -     Deleted Bibliographic records: 0
2 [main] INFO lucene_dbStatistics  -  Authority records: 1254930
2 [main] INFO lucene_dbStatistics  -     Deleted Authority records: 3
2 [main] INFO lucene_dbStatistics  -  Holdings records: 2794171
2 [main] INFO lucene_dbStatistics  -     Deleted Holdings records: 0
2 [main] INFO lucene_dbStatistics  -  Classification records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Classification records: 0
3 [main] INFO lucene_dbStatistics  -  Community information records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Community information records: 0
3 [main] INFO lucene_dbStatistics  -  Deleted records: 3
```

  * curl
    * total
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6679724">9|4999|5000|6679724</resumptionToken>
```

  * bibs
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2630623">10|11809|5000|2630623</resumptionToken>
```

  * holds
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2794171">11|8588|5000|2794171</resumptionToken>
```

  * auth
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="1254930">12|5429793|5000|1254930</resumptionToken>
```

  * total = bibs+holds+auth = 6679724 (good)

  * -End 6/13/10 end to end test using 137 -----------------------------------------------------------------------










latest test on 146 (against 137)
5/23/2011 3:07 PM  128.151.244.135

  * lucene\_dbStatistics.sh

```
voyager@urxcoai# ./lucene_dbStatistics.sh
Lucene Statistics Value is:true
init(luceneDir): lucene_index
init(logDir): log
 log4j property file: /import/OAIToolkit/OAIToolkit.log4j.properties
2011-05-23 15:05:46,700 [main] (Logging.java:50) INFO  - Logging started in directory: log
2011-05-23 15:05:52,322 [main] (Importer.java:987) INFO  -  *************** Lucene Database Statistics ***************


2011-05-23 15:05:52,323 [main] (Importer.java:989) INFO  - Total records in the Lucene Database are: 7452718
2011-05-23 15:05:52,323 [main] (Importer.java:990) INFO  -  Bibliographic records: 2667733
2011-05-23 15:05:52,324 [main] (Importer.java:991) INFO  -       Deleted Bibliographic records: 0
2011-05-23 15:05:52,325 [main] (Importer.java:992) INFO  -  Authority records: 1950769
2011-05-23 15:05:52,325 [main] (Importer.java:993) INFO  -       Deleted Authority records: 3
2011-05-23 15:05:52,325 [main] (Importer.java:994) INFO  -  Holdings records: 2834213
2011-05-23 15:05:52,326 [main] (Importer.java:995) INFO  -       Deleted Holdings records: 0
2011-05-23 15:05:52,327 [main] (Importer.java:996) INFO  -  Classification records: 0
2011-05-23 15:05:52,327 [main] (Importer.java:997) INFO  -       Deleted Classification records: 0
2011-05-23 15:05:52,327 [main] (Importer.java:998) INFO  -  Community information records: 0
2011-05-23 15:05:52,328 [main] (Importer.java:999) INFO  -       Deleted Community information records: 0
2011-05-23 15:05:52,328 [main] (Importer.java:1000) INFO  -  Deleted records: 3
voyager@urxcoai#
```

  * totals:
```
$ curl -s 'http://128.151.244.135:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="7452715">4|4999|5000|7452715</resumptionToken>
```
  * bibs:
```
$ curl -s 'http://128.151.244.135:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2667733">8|11809|5000|2667733</resumptionToken>
```
  * holds:
```
$ curl -s 'http://128.151.244.135:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2834213">6|8588|5000|2834213</resumptionToken>
```
  * auth:
```
$ curl -s 'http://128.151.244.135:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth'  | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="1950769">13|5419649|5000|1950769</resumptionToken>
```
  * bibs + holds + auth = total 7452715


  * as reported by 146: (on 5/20/2011)

```
incoming all time             rochester_135:bib                       new_act_cnt:    2,624,789               new_held_cnt:            0                new_del_cnt:            0
incoming all time             rochester_135:bib                       upd_act_cnt:       11,261               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_135:bib              unexpected_error_cnt:            0
incoming all time             rochester_135:auth                      new_act_cnt:    1,985,625               new_held_cnt:            0                new_del_cnt:            3
incoming all time             rochester_135:auth                      upd_act_cnt:       22,514               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_135:auth             unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    7,397,819               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:       47,333               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
incoming all time             rochester_135:hold                      new_act_cnt:    2,787,405               new_held_cnt:            0                new_del_cnt:            0
incoming all time             rochester_135:hold                      upd_act_cnt:       13,558               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_135:hold             unexpected_error_cnt:            0
20 May 2011 23:44:16,700  INFO [Thread-17] -
             total-Active:   7,397,819            total-Deleted:           3
```

  * Does not match because need to do a harvest. Did so 5/24/11 at 9:00 AM

  * 5/24/11 MST logs after harvest and processing by services
    * 5/24/11 harvestIn/rochester\_135.txt
```

incoming all time             rochester_135:bib                       new_act_cnt:    2,656,470               new_held_cnt:            0                new_del_cnt:            0
incoming all time             rochester_135:bib                       upd_act_cnt:       41,173               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_135:bib              unexpected_error_cnt:            0
incoming all time             rochester_135:auth                      new_act_cnt:    1,985,625               new_held_cnt:            0                new_del_cnt:            3
incoming all time             rochester_135:auth                      upd_act_cnt:       90,008               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_135:auth             unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    7,462,772               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:      175,616               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
incoming all time             rochester_135:hold                      new_act_cnt:    2,820,677               new_held_cnt:            0                new_del_cnt:            0
incoming all time             rochester_135:hold                      upd_act_cnt:       44,435               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             rochester_135:hold             unexpected_error_cnt:            0
24 May 2011 09:34:56,086  INFO [Thread-319] -
             total-Active:   7,462,772            total-Deleted:           3

```

  * 5/24/11 services/marcnormalization.txt
```
incoming all time             hold                                    new_act_cnt:    2,820,526               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:       30,853               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,656,620               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:       29,912               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    7,462,772               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:      128,259               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0

outgoing all time             hold                                    new_act_cnt:    2,820,526               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             hold                                    upd_act_cnt:       30,853               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             hold                           upd_act_prev_act_cnt:       30,853      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             hold                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             hold                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             bib                                     new_act_cnt:    2,656,619               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             bib                                     upd_act_cnt:       29,912               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             bib                            upd_act_prev_act_cnt:       29,912      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             bib                           upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             bib                            upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    7,462,771               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:      128,259               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:      128,259      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0

             total-Active:  11,064,359            total-Deleted:       4,327               total-Held:     115,703
                 e-Active:   2,845,275                 h-Active:   2,713,116                   h-Held:     115,703
                 m-Active:   2,660,692                 w-Active:   2,845,275
```

  * marcnormalization, in matches out except 1 bib record, (the extra one is produced no output - seen in "Browse Records"
    * solr: h: 2820526, bib: 2656619, auth: 1985626, error: no output: 8
      * total 7462771 (not counting error record(s))

  * 5/24/11 services/marctoxctransformation.txt
```
incoming all time             hold                                    new_act_cnt:    2,820,526               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:       30,853               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,656,619               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:       29,912               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    7,462,771               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:      128,259               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0

outgoing all time             work                                    new_act_cnt:    2,845,403               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:       26,157               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:       26,157      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,715,642               new_held_cnt:      117,249                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:       25,453               upd_held_cnt:        1,306                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:       25,344      upd_act_prev_held_cnt:          109       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:           33     upd_held_prev_held_cnt:        1,273      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,845,403               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:       26,157               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:       26,157      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   11,067,140               new_held_cnt:      117,249                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:      103,598               upd_held_cnt:        1,306                upd_del_cnt:        4,328
outgoing all time             TOTALS                         upd_act_prev_act_cnt:      103,489      upd_act_prev_held_cnt:          109       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:           33     upd_held_prev_held_cnt:        1,273      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:        2,849      upd_del_prev_held_cnt:        1,479       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,660,692               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:       25,831               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:       25,831      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0

```
```
```