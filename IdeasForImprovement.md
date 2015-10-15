I'm not to a point where I could suggest any of these ideas as the best right now.  This is just a place where I'm throwing everything into the pot.  I'll break up the ideas into 3 sections:

  1. hardware
    * Could SSD's be a solution?  They are getting cheap.
      * CARLI's external storage array has write-optimized and read-optimized Solid-State drives (this is all the virtual machine should ever see).  The solid-state drives read-write data to a RAID Z3 (ZFS) array of 1TB 7200RPM SATA drives behind the scenes (and without the knowledge of the virtual machine).
      * Dave's mac has an SSD
    * more memory?  similar to CARLI's vufind approach.
  1. index structure
    * optimizing the structure of the index is perhaps one of the most effective ways to speed up queries
    * **Solr 1.4 Enterprise Search Server**
      * **Chap. 2 - Schema and Text Analysis: One combined index or multiple indices**
        * For a large number of documents, a strategy using multiple indices will prove to be more scalable. Only testing will indicate what "large" is for your data and your queries, but less than a million documents will not likely benefit from multiple indices. Ten million documents have been suggested as a reasonable maximum number for a single index. There are seven million tracks in MusicBrainz, so we'll definitely have to put tracks in its own index.
      * **Chap. 9 - Scaling Solr: Optimizing a single Solr server (Scale High)**
        * Good schema design is probably one of the most important things you can do to enhance the scalability of Solr. You should refer to Chapter 2 for a refresher on many of the design questions that are inextricably tied to scalability. The biggest schema issue to look at for maximizing scalability is: Are you storing the minimum information you need to meet the needs of your users?
    * brief analysis using luke
      * note: I had to build luke from src to support the newest index format
      * num of docs: 2,554,876
      * num fields: 42
      * num terms: 56,332,800
      * the following terms occur in every document (good candidates for stop words)
        * field: text
          * expression
          * marctoxctransformation
          * mst
          * toolkit
          * work
          * oai
          * oaimstrochesteredumetadataservicestoolkitmarctoxctransformation
          * metadata
          * services
          * rochester
          * edu
          * manifestation
        * field: type
          * manifestation
        * field: node\_type\_s
          * xc\_manifestation
        * field: metadata\_type\_s
          * manifestation
        * field: source\_id\_s
          * 3
  1. querying
    * currently 3 queries are sent during a search
      * 1 to wordnet
      * 2 to the frbr catalog
        * 1 for search and 1 for facets
        * Peter merged these 2 into 1 query and found that the time wasn't any faster than the cumulative time of the 2.
  1. drupal
    * double clutch
      * when you click the search button, it sends a POST which redirects to a GET.  The GET seems to contain the exact same info.  The POST takes about 200 ms to respond.  We should be able to get rid of that extra trip.
    * mysql queries
      * limit the number of mysql queries?
      * optimize the queries
    * additional caching
      * drupal is really fast when you use the page cache.  However, we can't rely on that.
      * perhaps we can add some sort of middle ground.  Although it seems it's already doing the next best thing.
        * Pro Drupal Development - Chapter 15 - Caching (p359)
```
BLOCK_CACHE_PER_ROLE - Each role sees a separate cached block.  Default for blocks that do not declare a cache setting.
```
        * to me that says unless a block explicitly states otherwise (which perhaps we should check), that we can't do any better here.
