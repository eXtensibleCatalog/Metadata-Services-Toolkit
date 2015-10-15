This page was used to track performance improvements as they were made to the MST.

The most recent results are at the top (by service).  Explanations of the results are underneath the graph (linked from specific rows).

| **architecture** | **server** | **service** | **number of**<br /> records| **type of records** | **xml**<br />parse<br />(ms)| **process**<br />service<br />(ms)| **db-write**<br />(ms)| **total**<br />ms/record| **records/hr** | **total time** |
|:-----------------|:-----------|:------------|:---------------------------|:--------------------|:----------------------------|:----------------------------------|:----------------------|:------------------------|:---------------|:---------------|
|db/solr           |170         |trans        |in:5.8M<br />out:11M        |bibs/holdings        |0.5                          |1.5                                |0.4                    |3.1                      |900k            |6hr 9m          |[details](RecordBreakdown#dom_improve_load_data_infile.md) |
|db/solr           |170         |trans        |in:2.5M<br />out:4.9M       |bibs/holdings        |2.1                          |1.7                                |2.5                    |7.5                      |500k            |4hr 55m         |[details](RecordBreakdown#delayed_indexing.md) |
|db/solr           |170         |trans        |N/A                         |bibs/holdings        |2.1                          |1.7                                |3.7                    |8.5                      |400k            |N/A             |[details](RecordBreakdown#sax.md) |
|db/solr           |170         |trans        |N/A                         |bibs/holdings        |2.5                          |3.5                                |4.1                    |11                       |327k            |N/A             |
| **architecture** | **server** | **service** | **number of**<br /> records| **type of records** | **xml**<br />parse<br />(ms)| **process**<br />service<br />(ms)| **db-write**<br />(ms)| **total**<br />ms/record| **records/hr** | **total time** |
|db/solr           |170         |norm         |5.8M                        |bibs/holdings        |0.9                          |0.8                                |0.6                    |3                        |1M              |5hr 41m         |[details](RecordBreakdown#dom_improve_load_data_infile.md) |
|db/solr           |170         |norm         |2.5M                        |bibs/holdings        |2.5                          |1                                  |2.2                    |7                        |517k            |4hr 50m         |[details](RecordBreakdown#delayed_indexing.md) |
|db/solr           |170         |norm         |2.5M                        |bibs/holdings        |2.5                          |1                                  |3.5                    |7                        |450k            |5hr 30m         | [details](RecordBreakdown#noxpath.md) |
|db/solr           |170         |norm         |2.5M                        |bibs/holdings        |2.5                          |5                                  |3                      |11                       |330k            |7hr 30m         |[details](RecordBreakdown#db_and_solr.md) |
|solr-only         |170         |norm         |1M                          |bibs/holdings        |2.5                          |5                                  |21                     |29                       |125k            |8hr             |
|solr-only         |170         |norm         |2M                          |bibs/holdings        |                             |                                   |                       |                         |                |55hr            |

<br />
### dom\_improve\_load\_data\_infile ###
I had incorrectly been reinstantiating a factory class each time dom parsed xml.  I was tipped off when I noticed that harvesting was parsing much quicker (it parsed 5000 records at a time instead of 1 at a time).  The other big change was switching from batch prepared statements to mysql's "load data infile".

<br />
### delayed\_indexing ###
inserts into the database are slowed down and degrade much quicker with the number of indexes on the tables.  In this approach, I don't create indexes on the tables until after the first run through.

<br />
### sax ###
Switching to sax isn't what really sped this up, but it's what enabled it to speed up.  Instead of parsing the xml into dom and then accessing the data via dom in the transformation process, I parse the data using sax into a custom class I created which allows for much quicker data access during processing.

<br />
### noxpath ###
the norm service was using xpath to traverse the xml tree.  Using xpath is not very performant, so I changed it to programatically traverse the tree as such:<br />
**was**
```
XPath xpath = XPath.newInstance(".//marc:subfield[@code='8']");
xpath.addNamespace("marc", "http://www.loc.gov/MARC21/slim");
elements = xpath.selectNodes(marcXml);
```
**is now**
```
elements = new ArrayList<Element>();
for (Object o : marcXml.getChildren("subfield", marcXml.getNamespace())) {
    Element e = (Element)o;
    if ("8".equals(e.getAttributeValue("code"))) {
        elements.add(e);
    }
}
```
What's really good about this is that service processing has become minimal.  The bottlenecks are now pretty much down to xml parsing and writing to the db.  It means that multi-threading the services themselves won't give us much of a gain.  That's good because that would be a hard task to perform.

<br />
### db\_and\_solr ###
So on this machine we’re about 2.5x faster than we were for the first 1M records (this factor increases on a faster cpu).  Also, while the solr architecture suffered degradation after the 1M record mark, the db-solution showed no signs of slowing down after 4M records.  So, the db solution is about 10x faster for the 2nd set of 1 million records.  And along with performance gains, this code re-architecture phase has produced better organized code which means the next set of changes can be done more quickly.

<br />
### Ideas for the future ###
These numbers are based on a single threaded process limited by a cpu clock speed of 1.5 GHz.  Here’s a reminder of things that can still be done in the future to increase speed:
  * multithread
    * xml parsing
    * service processing
    * db writes
    * write metdata xml to file system
      * TESTED - this is much faster.  The time to do the fs write is negligible.  The RECORDS\_XML table was taking between 30-50% of overall db writes, so this will cut down on 1-1.5 ms.  This is probably not worth it since moving to "load data infile"
  * service by service optimization.  There are things that could be done on a line-by-line basis to increase performance, but I’m not sure how much of a gain that would be
  * norm - switch to the sax model I'm using from trans
  * switch from jdom to a faster dom manipulation library

### random notes ###

the specs really come down to cpu clock speed:
  * print data (clock speed) of cpu:
```
/usr/sbin/psrinfo -v
```
    * I also have a little script (scripts/test\_cpu.sh) to make comparisons to different machines
  * get serial # of disk drive to look up rpms:
```
iostat -E
```
    * but I'm not convinced this really matters because it's writing much slower than the hdd is capable of.  I also wrote a script to verify the speed (scripts/test\_hd.sh).  This shows that the drive is capable of writing around an order of magnitude faster than mysql is.  My guess is that the majority of the time mysql is doing writes, it is using the cpu.  This is somewhat verified by prstat.
  * get the memory available on the machine
```
prtconf -v | grep Memory
```
  * is solaris swapping?
    * si = swaps in
    * so = swaps out
```
vmstat -S 2
```