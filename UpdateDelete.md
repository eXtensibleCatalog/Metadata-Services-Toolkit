### The Main Repository Tables ###

| **records** |
|:------------|
| **record\_id** | **status**  | **format\_id**|
| 200         | A           | 1           |

| **record\_updates** |
|:--------------------|
| **record\_id**      | **date\_updated**   |
| 100                 | 2011-01-17          |

| **records\_sets** |
|:------------------|
| **record\_id**    | **set\_id**       |
| 200               | 1                 |

| **record\_predecessors** |
|:-------------------------|
| **record\_id**           | **pred\_record\_id**     |
| 200                      | 100                      |

| **records\_xml** |
|:-----------------|
| **record\_id**   | **xml**          |
| 200              | 

&lt;foo&gt;

bar

&lt;/foo&gt;

 |

| **record\_links** |
|:------------------|
| **from\_record\_id** | **to\_record\_id** |
| 201               | 200               |

Indexes on these tables need to fit in memory in order for queries to work efficiently.  To give an example of size (this is on 146 after processing full UR data):
```
bash-3.00$ du -sh /usr/local/mysql/var/xc_marcnormalization/record*.MYI
   1K   /usr/local/mysql/var/xc_marcnormalization/record_links.MYI
 287M   /usr/local/mysql/var/xc_marcnormalization/record_predecessors.MYI
 386M   /usr/local/mysql/var/xc_marcnormalization/record_sets.MYI
 148M   /usr/local/mysql/var/xc_marcnormalization/record_updates.MYI
 261M   /usr/local/mysql/var/xc_marcnormalization/records.MYI
  57M   /usr/local/mysql/var/xc_marcnormalization/records_xml.MYI
```
```
bash-3.00$ du -sh /usr/local/mysql/var/xc_marctoxctransformation/record*.MYI
  86M   /usr/local/mysql/var/xc_marctoxctransformation/record_links.MYI
 585M   /usr/local/mysql/var/xc_marctoxctransformation/record_predecessors.MYI
 396M   /usr/local/mysql/var/xc_marctoxctransformation/record_sets.MYI
 300M   /usr/local/mysql/var/xc_marctoxctransformation/record_updates.MYI
 534M   /usr/local/mysql/var/xc_marctoxctransformation/records.MYI
 120M   /usr/local/mysql/var/xc_marctoxctransformation/records_xml.MYI
```

The bigger these indexes are, the harder they'll be to load into memory.  Also, the more CPU processing time it'll take to query and insert data.  It's possible we can trim the sizes of these indexes by making sure we aren't over indexing.

The question is posed whether or not we can update records or whether we should delete and create new records or whether we can update existing output records.  Ben implemented a solution for updating records (instead of deleting/re-creating) in the MARC-to-XC Transformation service.  There is concern about whether this strategy makes sense (either for humans or for the Drupal Toolkit).  There is also concern whether the Aggregation service has any hope of using a similar strategy.

### Solution #1: DELETE/CREATE(arguments for) ###
  * Dave (email on 01/13/11)
    * We should mark records deleted and use new identifiers for new records...this approach causes other issues:
      * it breaks an assumption that generally an updated record represents in new version of the same record (this breaks nothing in the software, but it does break a human beings ability to understand our software)
        * BDA: I don't necessarily disagree with the statement, but I don't think it applies to the UPDATE strategy.  It is a rare case that an output record will actually be a different record entirely than it previously was
          * the case is only for works, expressions, holdings in the transformation service
          * I'm not convinced we'll have this problem for aggregation or authority
      * it breaks a potential to do some performance optimizations in Drupal or other software that harvests from the MST.  For instance, if records are usually about the same thing, then we might approach updates in Drupal differently in the future, rather then always reconstructing everything in Drupal whenever a service is upgraded.
        * BDA: Dave - is this still valid?
      * Drupal might want to keep track of metadata that is attached to a node and if every time a service is upgraded, that node has completely new meaning, then, hmmm
        * BDA: Dave - is this still valid?
      * This issue will matter more in Aggregation where the output records will be linked to specific things in the world: A single XC Work Record will exist for an actual "work" and our goal will be for that service to always keep that OAI Identifier tied to that work even after an upgrade to the service.  This DOES NOT APPLY to Transformation because Transformation will create duplicate work Records for a single actual "work."  But that said, Transformation should not behave completely differently from the way Aggregation will.
        * BDA: I'm not sure how this example is a con for the UPDATE strategy.
  * Peter (email on 01/18/11)
    * It is a complex problem, and if old IDs can be reuse for new records, it would evolve a lots of problems in Drupal side. This pattern causes lots of problems in library fields. Let's think about Universal Devimal Classification. They sometime reissue the same numbers for different concepts, and it requires, that the UDC numbers will have meaning only if we know the version numbers. But unfortunatelly, there is no service available to keep track of all UDC numbers and theirs versions. <br /><br />The situation in Drupal: it is important, that when we import(harvest) a record, it has some partents and children. Lets imaging the following situation:<br />initial harvest: records #1 (holdings), #2 (manifestation), #3 (expression), #4 (work).<br />We create a table for uplinks: 1->2, 2->3, 3->4.<br />If we update #2 as another manifestation, or another type, which is not in the uplink chain, the 1->2 and 2->3 relations will be false, we should delete them. And #1 won't have parent, and #3 won't have children. Moreover, we should modify the metadata part of #1 and #3 and delete these information, and we should delete #2 from Solr as well.<br /><br />I draw the worst scenario. A better one, if with updated records contains every belonging changes, i.e. every records, which touched by the changes. I can imagine, that such changes (given the nature of the FRBR), would change almost all records in the DB (it must be a mathematical model to eliminate this). Anyway: if the harvested records take a consistent state (each links point to the correct record), that we can keep consistency inside Drupal. The result will be only longer  harvesting.

### Solution #2: UPDATE existing IDs ###
  * Ben
    * My major concern with deleting instead of updating is with database index size (highlighted above).  If we delete and insert instead of updating, then we’re talking about potentially increasing the size of repositories quite a bit.  We can do our best to optimize the code and the hardware, but this design decision could play the biggest factor in the size of repositories we can support.  This isn’t merely a case of adding more hard drives.  It effects the amount of memory you need as well and the amount of cpu time it takes to sort through an index.  I don’t know exactly the effect, but I’m skeptical that the potential performance risks here  outweigh the benefits (which I’m still struggling to grasp).
      * Transformation solution
```
An output record’s type will not vary.  Records are never changed from some type to another type.  The only thing that might change a record is if the number of works, expressions, or holdings differs.  For example:
1) Input Record 100 produced 5 output records (200 and 201 are works, 202 and 203 are expressions, and 204 is the manifestation)
2) A new version of Input Record 100 is processed and produces 3 output records (200 is a work, 202 is an expression, 204 is the manifestation, 201 and 203 are deleted).
It is possible that what was 201 is now 200 if it was the original 200 is the one that was deleted.  The manifestation id will remain the same.
```
      * How often will the # of works, expressions, and holdings change?  Just updating the records (w/out updating the # of output records) won’t have any impact.  I would also venture to say that for a human, it would be confusing that a record was deleted when it was merely updated.  Now they have 2 (or 3,4,5…) output records for 1 input record.  What % of time it will have a completely new meaning.  Is it 50% or .00001 %?  I would guess it’d be closer to the latter, but I really have no idea.
    * I think there's ways to get around Peter's dilemma above.  We'll never update a work w/out updating it's corresponding expression and manifestation as well.  So, couldn't you just delete the links and recreate them?
    * I don't know enough about aggregation to argue one way or the other.  Perhaps it isn't possible to use such a strategy.

### Aggregation Service ###

Dave, Randy and Ben met a few weeks ago (1/27 I think) to discuss this topic.  Dave seemed ok with the UPDATE solution for marcnormalization and marctoxctransformation, but didn't think it would work for aggregation and authority.  Ben still doesn't see why UPDATE wouldn't work in theory, but Dave did enlighten me to a potential performance issue that I hadn't considered.

**Definitions**
  * A - incoming records
  * B - outgoing records
  * AGG - aggregation service

**Scenario**
  * AGG begins processing A1
  * A1 produces B1
  * AGG begins processing A2
  * A2 matches A1
  * AGG fetches A1
  * AGG merges A1 and A2 and updates B1 accordingly

Now suppose AGG is updated and all records must be reprocessed.

These are the steps involved with the UDPATE method:
  * AGG begins processing A1
  * A1 matches A2
  * AGG fetches A2
  * AGG merges A1 and A2 and updates B1 accordingly
  * AGG begins processing A2
  * A2 matches A1
  * AGG fetches A1
  * AGG merges A1 and A2 and updates B1 accordingly

With the DELETE/CREATE method, the steps would be fewer:
  * all existing B records are marked as deleted
  * AGG begins processing A1
  * A1 produces B2
  * AGG begins processing A2
  * A2 matches A1
  * AGG fetches A1
  * AGG merges A1 and A2 and updates B2 accordingly

For this scenario, DELETE/CREATE saves you from one fetch and one merge.  The performance benefit saved here will depend on the ratio of merged records.  My initial hesitation (detailed above) in regards to the DELETE/CREATE solution was geared towards keeping the database small.  In response, Dave suggested the following strategy:
  * instead of creating unique ids per MST, create unique ids per service
    * in reality, they could still be unique by prefixing the service id - eg
      * 1000001
      * 2000001

I **_believe_** this strategy should work, but it would take time to implement.  Dave raised the question again (2/17) about whether this is the best strategy.  I reviewed the PPT for the aggregation service and see the above scenario as the only downside.  I still believe the UPDATE method should work fine and that it is actually simpler than the DELETE method.  However, I do note that the above scenario would be slightly slower (whether it would have any real effect or not - I don't know).

**Outstanding question**
  * Do we know what % of records we'll be merging?
  * Is there anything else I'm missing about downsides to the UPDATE strategy?