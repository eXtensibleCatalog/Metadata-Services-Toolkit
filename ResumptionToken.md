## completeListSize ##

**background:**
This section pertains to the optional attribute [completeListSize](http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl) in the resumptionToken of an oai-pmh ListRecords request.

I've also inquired of this issue on [stackoverflow](http://stackoverflow.com/questions/4400451/optimize-mysql-count-query)

The way we page through chunks of records when a client is harvesting from the MST is to sort by recordId and then add the previous highest recordId to the subsequent queries.
```
select straight_join 
  r.record_id,
  r.oai_datestamp,
  r.format_id,
  r.status,
  x.xml,
  max(u.date_updated) as date_updated
from 
  ${REPO_NAME}.record_updates u force index (idx_${REPO_NAME}_record_updates_record_id),
  ${REPO_NAME}.records r IGNORE index (idx_${REPO_NAME}_records_format_id),
  ${REPO_NAME}.records_xml x,
  ${REPO_NAME}.record_sets rs ignore index (idx_${REPO_NAME}_record_sets_set_id)
where r.record_id = x.record_id 
  and (r.record_id > ${RECORD_ID} or ${RECORD_ID} is null)
  and r.record_id = u.record_id
  and (u.date_updated > ${START_DATE} or ${START_DATE} is null)
  and u.date_updated <= ${END_DATE}
  and r.format_id = ${FORMAT_ID}
  and r.record_id = rs.record_id
  and rs.set_id = ${SET_ID}
group by u.record_id
order by u.record_id 
limit 5
```
this query is very fast (even as you page through millions of records):
```
$ export REPO_NAME=marctoxctransformation; export RECORD_ID=null; export START_DATE=null; export END_DATE=\'2020-01-01\'; export FORMAT_ID=7; export SET_ID=10;
$ echo "the_above_query" | mysql -u root --password=password
```
this command, however, is not fast
```
devuser@xcmst > date; echo "select straight_join 
   count(distinct u.record_id)
from 
  ${REPO_NAME}.record_updates u force index (idx_${REPO_NAME}_record_updates_record_id),
  ${REPO_NAME}.records r IGNORE index (idx_${REPO_NAME}_records_format_id),
  ${REPO_NAME}.records_xml x,
  ${REPO_NAME}.record_sets rs ignore index (idx_${REPO_NAME}_record_sets_set_id)
where r.record_id = x.record_id 
  and (r.record_id > ${RECORD_ID} or ${RECORD_ID} is null)
  and r.record_id = u.record_id
  and (u.date_updated > ${START_DATE} or ${START_DATE} is null)
  and u.date_updated <= ${END_DATE}
  and r.format_id = ${FORMAT_ID}
  and r.record_id = rs.record_id
  and rs.set_id = ${SET_ID}
" | mysql -u root --password=pass; date
Fri Dec 17 13:46:21 EST 2010
count(distinct u.record_id)
11772117
Fri Dec 17 13:50:56 EST 2010
```
The purpose of this query is to fill in the completeListSize attribute below
```
$ curl -s 'http://128.151.244.135:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marcxml' | xmllint --format - | tail -n 3
    <resumptionToken cursor="0" completeListSize="5828154">137|5000</resumptionToken>
  </ListRecords>
</OAI-PMH>
```
currently, the MST-0.3.0-RC2 excludes this optional attribute:
```
$ curl -s "http://128.151.244.146:8080/MetadataServicesToolkit/st/marctoxctransformation/oaiRepository?verb=ListRecords&metadataPrefix=xc"  | xmllint --format - | tail -n 3
    <resumptionToken>10|1000</resumptionToken>
  </ListRecords>
</OAI-PMH>
```

There are a few different options solutions to deal w/ this:
  1. continue to exclude it
    * this is just sad and no one wants this
  1. run a background thread which will report it when it finally gets it
    * this would be find for automated processes.  It's still somewhat sad, though.
  1. the initial request will take a loooong time
    * IMO, this is not a real solution
  1. speed up the query (taking a guess if necessary)
    * I started down this route (which is the rest of this wiki page), but I think it's not a full solution.  It needs to be combined with #2 above and possibly the record\_counts tables (to optimize requests for everything)

I am currently exploring option #4 and this is the algorithm I'm working on:
Keep track of how many records you've already gotten during this harvest (in the resumptionToken) - will be referred to below as numAlreadyHarvested.

  1. **if all records match the query**<br />then you can just do a "select count(1) from records;"*** how to determine this?
      ***if at least one record doesn't match the input format <br />then false*** This demonstrates a strategy that lets you ask the question w/out worrying about the query taking a long time.  This strategy is assumed to be used for all similar questions below, but I won't repeat for each step (although it's assumed to be implemented):
        * preliminary query to make sure it isn't going to be super slow.**<br />**if rows is > 100**<br />then false**```
explain select count(*)
from ${REPO_NAME}.records
where format_id <> ${FORMAT_ID}
```
        ***if the following query > 0 <br />then false**```
select count(*)
from ${REPO_NAME}.records
where format_id <> ${FORMAT_ID}
```
      ***if at least one record doesn't have the input set as its one and only set <br />then false*****if there are any sets that don't match the input set <br />then false**```
select count(*)
from ${REPO_NAME}.record_sets
where set_id <> ${SET_ID}
```
        ***if the # of record\_sets doesn't match the # of records <br />then false**```
select 1 from dual where (select count(*) from records)=(select count(*) from record_sets)
```
          ***note: this means you shouldn't really use more than one set*** check record\_updates table outside of date
        ***if any records fall outside the date\_range <br /> then false**```
select count(*)
from ${REPO_NAME}.record_updates
where (${START_DATE} is not null and date_updated < ${START_DATE})
  or (${END_DATE} is not null and date_updated > ${END_DATE})
```
        ***else (all of the above were false), then truth**1.**if no records match the query <br />then return 0*** how to determine?
      * if any one of the following things are true (this is the opposite of the above because above ALL of those queries had to be false.  In this scenario, any one of these queries can be true.)
      ***if no record matches the input format*** This is demonstrates the above strategy again, but checking for the opposite:
        * preliminary query to make sure it isn't going to be super slow.**<br />**if rows is < 100**<br />then continue w/ next query**```
explain select count(*)
from ${REPO_NAME}.records
where format_id = ${FORMAT_ID}
```
        ***if the following query is 0 <br />then truth**```
select count(*)
from ${REPO_NAME}.records
where format_id = ${FORMAT_ID}
```
      ***if no records have the input set as its set <br />then truth**```
select count(*)
from ${REPO_NAME}.record_sets
where set_id = ${SET_ID}
```
      ***if no records fall w/in the date\_range <br /> then truth**```
select count(*)
from ${REPO_NAME}.record_updates
where (${START_DATE} is null or date_updated > ${START_DATE})
  and (${END_DATE} is null or date_updated < ${END_DATE})
```
  1. else if the actual query will return less than X records (**using startingId in the query**)
    * how to determine this?
      * this query is less than configurable amount (default: 1 million)
```
explain select straight_join 
   count(distinct u.record_id)
from 
  ${REPO_NAME}.record_updates u force index (idx_${REPO_NAME}_record_updates_record_id),
  ${REPO_NAME}.records r IGNORE index (idx_${REPO_NAME}_records_format_id),
  ${REPO_NAME}.records_xml x,
  ${REPO_NAME}.record_sets rs ignore index (idx_${REPO_NAME}_record_sets_set_id)
where r.record_id = x.record_id 
  and (r.record_id > ${RECORD_ID} or ${RECORD_ID} is null)
  and r.record_id = u.record_id
  and (u.date_updated > ${START_DATE} or ${START_DATE} is null)
  and u.date_updated <= ${END_DATE}
  and r.format_id = ${FORMAT_ID}
  and r.record_id = rs.record_id
  and rs.set_id = ${SET_ID}
```
  1. else take a guess
    * determine % of records that have the input set
```
select 
  explain select count(1) from ${REPO_NAME}.record_sets where set_id=${SET_ID}
```
    * determine % of records that have the input format
```
explain select count(1) from ${REPO_NAME}.records where format_id=${FORMAT_ID}
```
    * determine % of records that fall w/in the date range
```
explain select count(*)
from ${REPO_NAME}.record_updates
where (${START_DATE} is null or date_updated > ${START_DATE})
  and (${END_DATE} is null or date_updated < ${END_DATE})
```
    * determine avg number of updates per record**following query is untested**```
select ((select count(*) from record_updates))/((select count(*) from records))
```
    * guess = (% records with the format) X (% of records with the set) X (% of records in date range) X (avg # of updates per record)
      * If the guess is < than 1 million + numAlreadyHarvested, then use 1 million + numAlreadyHarvested**


### tests ###

params for all tests
  1. all tests return 2 records per request.
  1. all tests use 3 as the param by which to guess

| **data set #** | **format 1** | **format 2** | **set 1** | **set 2** | **dr 1** | **dr 2** | **updates per rec** |
|:---------------|:-------------|:-------------|:----------|:----------|:---------|:---------|:--------------------|
|1               |10            |0             |10         |0          |10        |0         |1                    |
|2               |10            |0             |0          |0          |10        |0         |1                    |
|3               |10            |0             |5          |5          |10        |0         |1                    |
|4               |10            |0             |0          |0          |5         |5         |1                    |
|5               |5             |5             |0          |0          |10        |0         |1                    |
|6               |5             |5             |5          |5          |5         |5         |3                    |

| **req #** | **fmt** | **set** | **date range** |
|:----------|:--------|:--------|:---------------|
| 1         | fmt1    | none    | none           |
| 2         | fmt1    | none    | dr1            |
| 3         | fmt1    | none    | dr2            |
| 5         | fmt1    | set1    | none           |
| 6         | fmt1    | set2    | none           |
| 7         | fmt2    | none    | none           |
| 8         | fmt1    | set1    | dr1            |

expected results of tests
| **test #** | **data set #** | **req #** | **completeListSize** |
|:-----------|:---------------|:----------|:---------------------|
| 1          | 1              | 1         | 10-                  |
| 2          | 1              | 2         | 10                   |
| 3          | 1              | 3         | 0                    |
| 4          | 1              | 5         | 10-                  |
| 5          | 1              | 6         | 0                    |
| 6          | 1              | 7         | 0                    |
| 7          | 2              | 1         | 10-                  |
| 8          | 2              | 5         | 0                    |
| 9          | 3              | 1         | 10-                  |
| 10         | 3              | 5         | 5?,5-                |
| 11         | 4              | 1         | 10                   |
| 12         | 4              | 2         | 5?,5-                |
| 13         | 5              | 1         | 5?,5-                |
| 14         | 6              | 2         | 2.5?,5-              |
| 15         | 6              | 8         | 1.25?,5-             |