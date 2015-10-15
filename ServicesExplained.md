## What is a Metadata Service? ##

The **[Metadata Service Toolkit (MST)](GeneralGlossary#Metadata_Services_Toolkit_(MST).md)** is a platform that inputs a set of records (ie [repository](GeneralGlossary#repository.md)) and outputs another set of records (ie repository).  A **[record](GeneralGlossary#record.md)** is an xml document of a specific type.  A **[metadata service](GeneralGlossary#metadata_service.md)** is the process by which 1 input record produces 0..N output records of the same or a different type.  The protocol used to pull records into a service is **[oai-pmh](GeneralGlossary#oai-pmh.md)**.  All services process records one-by-one sequentially.  So oai-pmh might get 5,000 records at a time, but the service still only processes one at a time.  At the end of processing an incoming record (xml document) the service will have decided whether to add any output records to its repository.  The MST platform handles all of the common functionality involved in this process so that individual services can focus entirely on processing and outputting records in a way unique to the service.

## Component Focused View ##
<font color='red'>PLEASE NOTE!!!:<br />The MST is currently designed to process Services in the following order: <ol><li>MARC Normalization</li> <li>Aggregation Service</li> <li>MARC-XC Transformation</li></ol></font><br />

![https://docs.google.com/drawings/pub?id=1hGdfO5sgolyzCZOkOo1SP6rvvTSHwJkJeT0UXmbP1ZE&w=600&bogus=file.png](https://docs.google.com/drawings/pub?id=1hGdfO5sgolyzCZOkOo1SP6rvvTSHwJkJeT0UXmbP1ZE&w=600&bogus=file.png)

DL - I propose that you make all of the arrows go in the direction of record movement, rather than "request direction"  So I would reverse the arrow heads on both red and yellow of the arrows in the picture.

<font color='red'>BA - That’s how I originally had it way back when I created the presentation for code4lib.  At Dave’s request I changed it to the way it is now.  I can see it both ways.</font>

## Document Focused View ##
![https://docs.google.com/drawings/pub?id=1S06QAhOHti7oz3BOZidTetMHuyJX8WKl6uS_PhrLnzc&w=400&bogus=file.png](https://docs.google.com/drawings/pub?id=1S06QAhOHti7oz3BOZidTetMHuyJX8WKl6uS_PhrLnzc&w=400&bogus=file.png)

## Platform Data Structures ##
The MST uses MySQL (a popular and fast open source relational database) to store records in repositories.  It loads some of these tables into memory prior to harvesting and service processing to allow for higher throughput.  Many of these in-memory data structures use [trove (a high performance collections library for java)](http://trove.starlight-systems.com/).  Some tables are, however, queried in realtime based on the estimated frequency of such queries.  For example, the main focus of optimization is on the initial loading of data.  Since the goal is to be able to process records at about a pace of 1ms/record, even a fast db query would be considered significant.

The top section of this diagram is the rough equivalent of a [class diagram](http://en.wikipedia.org/wiki/Class_Diagram) and the bottom part is an [ERD](http://en.wikipedia.org/wiki/Entity-relationship_model).  These diagrams aren't exhaustive, but give you a good idea of how the platform works and how a service implementer can make use of the data provided by the mst-platform.  A more exhaustive list can be found in the actual [sql files](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/#svn%2Ftrunk%2FMST-instances%2FMST-instances%2FMetadataServicesToolkit%2Fsql).

  * **In-Memory Stuctures**
    * **oai\_id\_2\_record\_id map**
      * **description**: This cache determines if this particular record has previously been processed by the service.  It also keeps track of what the record's status was at that point.  An instance is a class of type [DynMap](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-common/src/java/xc/mst/cache/DynMap.java).  This class allows for both alpha and numeric identifiers.  If the identifiers are numeric, it takes up considerably less memory.  This is the purpose of the harvest.redundantToken property in the [properties file](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-common/src/java/default.properties#73).  The value of this property is a comma separated list of redundant characters to strip out of an oai-id.  That functionality is handled in the [Util.getNonRedundantOaiId](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-common/src/java/xc/mst/utils/Util.java#227) method.  Perhaps in a future this method could replace the redundant portion with a numeric instead of just swapping it.  That way uniqueness could be preserved across multiple repositories.
    * **previous statuses map**
      * **description**: This cache determines if this particular record has previously been processed by the service.  It also keeps track of what the record's status was at that point.

<img src='https://docs.google.com/drawings/pub?id=1UGYpBYBRILrmVRPjpvUy-SrO1FAUUKt_XA3yo3GzBLA&w=750&h=386&filetype=.png' />

<table>
<tr>
<td valign='top'>
<img src='http://www.extensiblecatalog.org/doc/MST/4wiki/repo.png' />
</td>

<td valign='top'>
<img src='http://www.extensiblecatalog.org/doc/MST/4wiki/harvest_repo.png' />

<img src='http://www.extensiblecatalog.org/doc/MST/4wiki/service_repo.png' />
</td>
</tr>
</table>

<img src='http://www.extensiblecatalog.org/doc/MST/web_safe_GIFs/gifs/ffffff.gif' height='500' width='1' />