## Random Tests ##
  * OneOff
    * building w3docmDocument in xerces - 1.09, 1.02, 1.03
    * building jdom from the w3docmDocument 0.72, 0.57, 0.63

## Services Cache ##
## Records ##
> Would it make sense for Records to know how much of them has been loaded?  For example you could have a method Record.getLoadedLevel could return:
  * ID\_ONLY
  * EVERYTHING\_BUT\_XML
  * EVERYTHING
### Successor/Predecessor Map ###
  * The cache should follow these guidelines
    * initial loads can't perform one-off db queries for each record
    * 
  * This cache can work a few different ways.
    * The commonality all of the following methods
      * Any in-memory data structure is loaded from the db when a service begins, is modified during processing, and persisted when the service completes
      * One off db queries should not be done for initial inserts
```
xc.mst.services.normalization.test
```
    * It can be loaded for each batch set of records.
  * predecessor id (long) = 8 bytes
  * successor ids (long) `*` avg 2.5 successors = 20 bytes
  * total = 28 bytes
|num of pred records|Memory|
|:------------------|:-----|
|1,000,000          |20 MB |
|10,000,000         |200 MB|
per service (4 services)


## Required memory ##

The amount of memory required for the MST depends upon a few factors:
  * harvest cache = 217 MB = 5.9M records
    * harvest.redundantToken=oai:library.rochester.edu:URVoyager1/,oai:,extensiblecatalog.info:
    * assumes that the remaining id is numerical
  * norm predecessor cache = 120 MB = 5.9 M
  * trans
    * assumes that your
    * bib maps = 127 MB
    * pred = 125 MB
  * overall used memory = 478M
    * assumes that your bib ids (001) are numerical