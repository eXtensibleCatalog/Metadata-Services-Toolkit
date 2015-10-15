<font color='red'>Questions needing to be answered are in red.</font>

### Other Documents ###
This [docushare folder ](http://docushare.lib.rochester.edu/docushare/dsweb/View/Collection-5230) contains the following:
  * **"Aggregation service processing steps"**: Dave's implementation design and detail
  * **"Aggregation Service Match Points"**: Jennifer's matching algorithms
  * **"Aggregation Service Merge Functionality"**: Jennifer's merge algorithms

<font color='red'>Would it be worthwhile to make these documents public?  We could do so either by switching to google docs (which IMO dwarfs docushare) or the wiki.</font>

### Ben's implementation design ###
<img src='https://docs.google.com/drawings/pub?id=1I607TCukkt6xjcHJ4zMyZano_kPEiM09gRR4h-cN9B0&w=960&h=720&nonsense=true.png' />

**Diffs to Dave's Design**
  1. distinction between "Records to Process" and "Processed Records" among the "Input Records" is no more of a distinction than in any other service (the MST platform keeps tracks of which records it has already processed)
  1. Output Records never contain uplinks to Input Records.  They always contain uplinks to Output Record ids.  If an Output Record contains an uplink to a record that hasn’t been processed yet, a recordId is set aside for it.  That Output Record will be marked as held until the linked record arrives.
  1. Match algorithm isn’t really a record-by-record comparison.  Instead it checks the collection of all matchpoint ids the service has already processed.  In some sense it is a record-by-record comparison - it's just a different way of looking at it.

**First Iteration**
  1. Ability to hold all necessary data in memory
    * no one-off database lookups are necessary
  1. Merge algorithm randomly selects a record (most likely the last in)

**Subsequent Iterations**
  1. smarter merging either by:
    * selecting which record to use
    * actually aggregating records
  1. to support the above feature, it might make sense to not actually store the output xml (because of ids), but generate records on the fly for downstream oai-pmh requests

**Plan for Implementation**
  1. add tasks to fogbugz and provide estimates (will be more accurate than the total guesses below).  The estimates below assume 100% devotion to aggregation service.
  1. enhance documentation
    * estimate: 3 days
  1. implement first iteration
    * estimate: 1 week
  1. create tests for each of Dave’s scenarios (pp 3-4 of the ppt)
    * estimate: 1 weeks
  1. Run against full record set from voyager
    * determine how many records matched
    * determine performance specifically of the merge process
    * estimate: 1 weeks
  1. Run against full record set from voyager + irplus
    * find the above determinations as well
    * estimate: 3 days
  1. go back to step #1 for subsequent iteration (more advanced merge algorithm)

### From Dave's PPT ###
I think it's important to go through Dave's implementation line-by-line to make sure the actual implementation doesn't overlook anything Dave has already thoroughly considered. <font color='blue'>BDA: my notes/diffs in blue</font><font color='red'>What do we think about this approach?  Is it worth continuing?</font><br />
**PROCESS Record: Take an input (W, E, M, or H) record**
  * search for matching records by comparing it with each (all) of the "processed records" at the same FRBR level, using the match algorithm for the appropriate FRBR level (four match algorithms),
    * <font color='blue'>BDA: search for matching records by checking the matchpoint caches</font>*** for each match, check the successor of that record to see if it is a merged record (if it has multiple predecessors, then it is merged), and include the other predecessors in the found set (no need to compare with those, since they will definitely match).
    ***<font color='blue'>BDA: The matchpoint cache will contain the incoming record ids that match.  With those incoming record ids, you can find all the output record ids.  From there you can check the "records with multiple predecessors cache" to get any other predecessors.  (This is important if the record currently being processed (A) matches record X and X matches Y, but A doesn't match Y.  Without doing all these steps, you'd fail.)</font>*** For any matching processed record (merged or not), mark it's successor output record as deleted, and temporarily keep track of the corresponding predecessor(s) for remerge. NOTE: If the marked-deleted record is in Held Records, just permanently delete it, if it is in Released Records, mark it deleted, and follow DELETE OUTPUT algorithm for that record.
    ***<font color='blue'>BDA: If the record is to be merged, instead of deleting the existing successor records for matches, we'll just overwrite one of them.  If there are more than one, all but one will be deleted.  This will prevent us from having to update uplinks in child records.  There will still be a need to update child records if there were multiple successors (and thus required a delete).  To accomplish this, we'd need to reprocess that record individually or just get the output and modify as needed (the latter seems to make more sense).</font>*** Continue matching until you have done the comparison with all records in the processed records set.  Then run the group of matched records through the appropriate merge algorithm (there are four merge algorithms, one for each frbr level), producing one new output record.
    ***<font color='blue'>BDA: For the first implementation we'll just use the current record as the merged record (with modified uplinks and xc:recordIds).</font>*** Set predecessor links between that record and the matched set of input records
    ***<font color='blue'>BDA: update all caches accordingly (caches are periodically flushed to the db)</font>*** Uplinks in the merged output record are the set of the uplinks in the matched input records (unique, but we can't check that yet, because two uplinks that look different could point to the same merged entity at a higher level, so we dedup later, once).
    ***<font color='blue'>BDA: uplinks in output records will always be to other output records (which may be non-existent or held).  This way the output record content won't need to be updated later on - just switched from HELD to ACTIVE</font>*** Check if this record can be released: If EVERY uplink points to a parent record that is BOTH in the "processed records" set and has a successor in the Released Records set, then we can proceed to the RELEASE Record steps below. If not, then we add a row to the WAITING FOR RELEASE QUEUE (record id, and parent OAI-ID).**
    * <font color='blue'>BDA: If every parent is non-held (check the record status cache), then the record will be marked as active.  Otherwise, it'll be held.</font>*** Move currently processing record into Processed Records set**

**RELEASE Record (this algorithm handles one Held Record that is "ready for release" by modifying it's uplinks and then releasing it):**
  * Rules: All uplinks in Held records will point to records in the Processed Records set. All uplinks in Released Records will point to records in the Released Records set.
    * <font color='blue'>BDA: All output records will link to other output records.  If an output record points to a record that doesn't yet exist (hasn't come into the agg service yet), then it will be marked as held.</font>*** Modify uplinks in the Held record by following each parent records successor link and then modifying each uplinks to point to there (which will always be a Released Record).
    ***<font color='blue'>BDA: This step isn't necessary (has already been addressed).</font>*** Deduplicate the modified uplinks (it is important to do that now AFTER we have rewritten the links) - use simple string comparison.
    ***<font color='blue'>BDA: yes - dedupe.</font>*** Move record from Held Records set into the Released Records set.
    ***<font color='blue'>BDA: Mark record as "active"</font>*** Check the WAITING FOR RELEASE queue to see if any records are waiting on this record. If so, record the Held-Record-ID from this table, and remove the corresponding row.  Now, query the table to see if this Held-Record-ID is waiting on any OTHER parents (some records will be waiting on multiple records).  If it is not waiting on any others, run RELEASE Record on this Held-Record-ID (save this to a TO-RELEASE queue until the next step is complete)
    ***<font color='blue'>BDA:</font>*** Query ORPHANS table using the record-id of the predecessor of the record being released.  For each record found, examine child uplinks and rewrite each one that used to point to the predecessor, and rewrite them to point to the newly Released record.  Deduplicate uplinks in the child record (DO NOT SKIP THIS STEP).
  * Continue to RELEASE any records added to the TO-RELEASE queue.**

**DELETE OUTPUT Record:**
  * Look for children (in "Released Records" set) that point to this record.  They now point to a deleted record (which is okay temporarily, but the deleted record id cannot be reused, because the children will point to this deleted record id for an indeterminate amount of time, and downstream services would be corrupted if reused)
  * In the ORPHANS table, save the child's record-id also save the ID of the marked-deleted parent records predecessor

<a href='Hidden comment: 
testing embedding google docs
<wiki:gadget url="http://www.extensiblecatalog.org/doc/MST/google_gadgets/agg_imp_doc.xml" height="800" width="800" />
'></a>