#### Matching ####
  * match points are defined in config
    * {field-subfield}={pattern}
    * Here is where placement of ints/strings would be done
  * match conditions are defined in config
    * perhaps groovy
      * each of the defined match points could be given a variable name and then the conditional could just be in groovy
      * 010a AND 020a AND (240a OR 245a)
  * match points values are stored in memory as much possible
    * for integral values this should not be a problem
      * for integral values which require another key
    * for string values, perhaps we keep the first x bytes in memory and then go to disk for the rest only when we match on those first x bytes
      * another option is to investigate lucene
      * I wonder if there's a library for this specifically?
  * when there is a match follow these steps
    * always overwrite an existing output record.
      * There may actually be more than one output records, if A didn't match B, but now C matches both.  It would have to delete one of A or B output.
      * get all holdings for all matched records and make sure they point to the correct bib. (this should be done from memory - no need to read entire xml and parse).  If a holding does need to be updated, though, you would have to read the entire thing in (unless you went with

#### Merging ####
  * I'd suggest we just go with scenario #2 - it shouldn't be too hard.

#### Questions ####
  * There's 2 possible strategies for storing records (would effect both bibs and holdings)
    * Store full xml (STORE-FULL).
      * This is what we've always done, but we left the interface open so that we (or others) could implement it differently in the future.  The downside to using this strategy is that we'd have to retrieve, parse, and then modify each time we changed a reference.
    * Custom Repository Implementation (create records on the fly)
      * We'd have to add a custom implementation for getting records out.  This looks pretty easy to do.  Might be necessary for xc agg anyways.  Let's go with this for now.
  * how to efficiently store and retrieve?
    * stackoverflow
      * my question: http://stackoverflow.com/questions/6816547/looking-for-a-string-keyed-map-with-efficient-lookup-and-disk-persistence
      * an interesting answer to this question: http://stackoverflow.com/questions/2815083/efficient-data-structure-for-word-lookup-with-wildcards
    * options
      * current favs
        * lucene
          * KeywordAnalyzer?
        * use a hashing algorithm like md5
        * mysql
          * a decent example - http://fernandoipar.com/2009/08/12/indexing-text-columns-in-mysql/
          * you can index the first x chars and then load it in memory.
      * not planning on investigating further
        * custom implementation
        * berkleydb
        * jdbm: http://jdbm.sourceforge.net/
        * redis
<img src='https://docs.google.com/drawings/pub?id=1I607TCukkt6xjcHJ4zMyZano_kPEiM09gRR4h-cN9B0&w=960&h=720&nonsense=true.png' />