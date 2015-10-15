The thing we most want to minimize in order to optimize performance is doing one-off database queries for each incoming record (both for harvests and services).  Answering the question "have I seen this record previously ?" is an important one because it determines whether the harvester/service needs to insert a new record (assigning a new id) or update an existing record (get the existing id).  For an initial run, this question doesn't need to be answered per record because we can assume the answer is no.  However, for ongoing harvests it would still probably be too slow to ask the database each time.  In order to answer this question effectively, we should keep a hashtable in memory (input-id -> output-id).  For services, this map is easy because we can use our internal MST record ids (which are numeric and unique across the entire system).  For harvests, though, the unique part is based on an external oai-id of which we don't know much about.

To keep the all oai-ids in memory for a given repository requires a decent amount of memory for a large repository.
```
(50 chars * 2 bytes + 38 + 4) * 10,000,000 = 1.4G
```
<sub>REF: [http://www.javamex.com/tutorials/memory/string_memory_usage.shtml]</sub>

This is not totally unreasonable, but it also wouldn't be difficult to reduce it by about 75% by simply stripping out redundant data in the oai-id so that what remains is a smaller (unique per repo) representation of the oai-id.  In order to do this, we simply allow the user the ability to enter the redundant portion of the oai-id per repo.  If the user does not configure this, the MST will simply use more memory than it needs to.  This should work fine until you get into 10s of millions of records (obviously dependent on how much memory you have).

  * the user configures a redundant section of oai-id
    * this will most likely be something like "oai:extensiblecatalog.info:"
    * examples:
      1. oai:extensiblecatalog.info:0
        * results in 0
      1. oai:extensiblecatalog.info:bib:0
        * results in bib:0
      1. oai:extensiblecatalog.info:bib/0
        * results in bib/0

## FAQ ##
  * **Question**: What if, for a particular repository, a user enters “repo.domain.edu” as the redundant section.  And then the repository has a few records without that string contained in their identifier?  How would the MST behave in the harvesting case? In the service processing step?
    * **Answer**: If an oai-id does not contain the redundantToken, the oai-id will simply be kept in memory in full.  All this means is that memory will not be used efficiently.  This question only applies to harvesting because services will map<br />input-record-ids -> output-record-ids<br />based on our own internal (unique per mst) integral ids.
  * **Question**:What are some other potential error conditions that we can account for and at least explain the ramifications in advance?
    * **Answer**: The main risk is running out of memory.  In order to run out of memory, some combination of these 3 things would have to happen:
      1. user did not enter the redundant portion of the oai-id
      1. there are 10s of millions of records in the harvested repository
      1. the system running the MST has less than 2G of RAM<br /><br />We could potentially allow for another optional way to do it that doesn't require a memory cache.  We could do a db lookup to map input-records to output-records.  However, this will significantly slow down the harvest.  Although, it might not be a big deal since it'll only matter for subsequent harvests.  I don't think this is necessary at the time, but good to know we could do it.