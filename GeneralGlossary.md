### bibliographic record (bib) ###
> a [record](#record.md) that describes a specific resource in a library (books, sound recordings, video recordings, and so forth).  In the context of this wiki, when we use the term bib, we are always referring to a MARC bibliographic record.  If a library has 2 copies of the same resource, there will only be one bib record. (see [wikipedia entry ](http://en.wikipedia.org/wiki/Bibliographic_record) for more).  The MST determines where or not a MARC record is a bib record by inspecting the 7th character of the leader.  If that character is one of a,b,c,d,e,f,g,h,i,j,k,m,n,o,p,r,t then it is considered a bib.  Here's an example (notice the 'a'):
```
<marc:leader>01202cam a22003490  4500</marc:leader>
```

### Functional Requirements for Bibliographic Records (FRBR) ###
> a conceptual entity-relationship model built upon relationships between and among entities.  One of the main concepts of the XC software suite is to transform older formats (eg MARC) into the new FRBR format.  A good example is transforming a MARC bib into an XC-work, XC-expression, and XC-manifestation record.  (see [wikipedia](http://en.wikipedia.org/wiki/FRBR) for more)

### holdings record (hold) ###
> a [record](#record.md) that provide copy-specific information on a library resource (call number, shelf location, volumes held, and so forth).  In the context of this wiki, this term could be referring to a MARC holdings record or an XC holdings record. (see [wikipedia entry](http://en.wikipedia.org/wiki/MARC_standards#MARC_formats) for more).  The MST determines where or not a MARC record is a holdings record by inspecting the 7th character of the leader.  If that character is one of u,v,x,w, then it is considered a holdings.  Here's an example (notice the 'x'):
```
<marc:leader>00334cx  a22001093  4500</marc:leader>
```

### Metadata Services Toolkit (MST) ###
> a platform that inputs a set of records (ie repository) and outputs another set of records (ie repository).

### metadata service ###
> the process by which 1 input record produces 0..N output records of the same or a different type. The protocol used to pull records into a service is oai-pmh

### oai-pmh ###
> a protocol used to transfer records from one repository to another.  This protocol not only allows for a one-time transfer that gets all records from a repository at a certain point in time, but also allows repositories to keep in sync with updates and deletes.  The MST acts as both an oai-pmh harvester and as a provider of oai-pmh repositories.  As a harvester, the MST should be accommodating of all the various options an oai-pmh repository might support (eg [seconds granularity](http://www.openarchives.org/OAI/openarchivesprotocol.html#Datestamp) or [deleted records](http://www.openarchives.org/OAI/openarchivesprotocol.html#DeletedRecords)).  (see [the spec](http://www.openarchives.org/OAI/openarchivesprotocol.html) for more).

### oai-pmh harvest ###
> a harvest is the period of time when a harvester requests records and the provide provides them.  Harvests can be scheduled in the mst to run once an hour, once a day, or once a week.  When run, it will only harvest changes that have been made since the start of the previous run.

### oai-pmh harvester ###
> the role of the one who harvests records out of an oai-pmh repository.

### oai-pmh provider ###
> the role of the one who provides records from an oai-pmh repository.

### record ###
> an xml document of a specific type.

### repository ###
> a set of records.

### xc schema ###
> XC schema is a new schema based on [frbr](GeneralGlossary#Functional_Requirements_for_Bibliographic_Records_(FRBR).md) specifically designed for the xc project, but open to be used for other purposes.


<img src='http://www.extensiblecatalog.org/doc/MST/web_safe_GIFs/gifs/ffffff.gif' height='700' width='1' />