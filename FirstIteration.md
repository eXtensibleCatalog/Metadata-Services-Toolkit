Resources
  * [XC RIT senior project on docushare](http://docushare.lib.rochester.edu/docushare/dsweb/View/Collection-3522)


---

<a href='http://www.extensiblecatalog.org/doc/MST/4wiki/agg_services.jpg'>
<img src='http://www.extensiblecatalog.org/doc/MST/4wiki/agg_services.jpg' width='830' />
</a>

---

### MARC Aggregation ###
merges marc-bib records based on ids such as OCoLC, LCCN, ISBN, ISSN, etc

---

### MARC Authority ###
inserts authority ids (works, group 2&3 entities)
  * Essentially we're matching MARC-auth records to elements w/in MARC-bib records?
    * what elements are we matching on (for both MARC-auth and MARC-bib)?
    * Is this going to be text matching only or on ids?
    * This is where the complex algorithms come into play?
    * What value does attaching authority ids provide?
  * Authority Files are harvested in through oai-pmh?
    * we discussed VIAF vs LCC, but the service can be agnostic as long as the formats are the same, correct?  (ie - the authority source could be configured via processing rules)?

---

### XC Transformation ###
transforms various formats (marcxcml/dc) into xc (frbr) and merges WEMs
  * text matching (works only)
    * complex algorithm again?
  * merges OCoLC, LCCN, ISBN, ISSN again since non-MARC formats haven't yet been merged


---

### XC Authority ###
inserts group 2&3 authority ids
  * This does not insert authority ids for works?
  * What does this really accomplish? Are these statements true?
    * applies only to records of non-MARC origin
    * no extra merging happens after this point
    * accomplishes nothing w/in the MST
    * it is merely for the purpose of enhancing the data for Drupal's sake

---
