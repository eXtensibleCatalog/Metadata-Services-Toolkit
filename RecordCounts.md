Comments interspersed below - JB
Follow-up comments 9/19/11 - JB

This page pertains to the accounting of records for the Release Candidates of MST 0.3.0

CARLI
  * http://xc.ilcso.uiuc.edu:8080/MetadataServicesToolkit/
    * in browse records, you can enter record ids
  * oai-toolkit - 16,786,009 records
```
$ curl 'http://xc.ilcso.uiuc.edu:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marcxml' | xmllint --format - | tail -n 3
    <resumptionToken cursor="0" completeListSize="16786009">14|4193</resumptionToken>
```
  * mst harvesting - 16,786,009 records output
```
banderso@xc:/opt/tomcat/bin/MST-instances/MetadataServicesToolkit/logs$ mysql -u root -p -D MetadataServicesToolkit -e "select count(*) from uiu.records"
+----------+
| count(*) |
+----------+
| 16786009 | 
+----------+
```
  * marcnormalization - 16,786,001 records output
```
banderso@xc:/opt/tomcat/bin/MST-instances/MetadataServicesToolkit/logs$ mysql -u root -p -D MetadataServicesToolkit -e "select count(*) from marcnormalization.records"
+----------+
| count(*) |
+----------+
| 16786001 | 
+----------+
```
    * 8 records were not processed
    * 7 can be seen in the UI by selecting the "Error" facet with value "1-108:Produced no output"
    * I can provide further info on these if need be, but I'm thinking there's few enough to ignore for now.

---

JB - I looked at these and it would take quite a bit of painstaking work to figure out the problems just by eyeballing the records.  If you could send me the records I could try to validate them using MARCEdit.  Most of these 7 records contain non-Roman characters so I wonder if that is where the problem lies.  But at least one of them doesn't.

---


  * marctoxctransformation - 24,075,135 records output
    * 4,568,965 input records did not produce output
```
banderso@xc:~$ mysql -u root -p -D MetadataServicesToolkit -e "select count(*) from marcnormalization.records nr where record_id not in (select pred_record_id as c from marctoxctransformation.record_predecessors)"
+----------+
| count(*) |
+----------+
|  4568965 | 
+----------+

banderso@xc:~$ mysql -u root -p -D MetadataServicesToolkit -e "select count(*) from marcnormalization.records nr where record_id not in (select pred_record_id as c from marctoxctransformation.record_predecessors) and not exists (select 1 from marcnormalization.record_sets nrs where nrs.record_id = nr.record_id and nrs.set_id = 3)"
+----------+
| count(*) |
+----------+
|      416 | 
+----------+
```
      * 4,568,549 authority records
        * confirmed in the UI and by subtracting 416 from 4568965 (from the queries above)
        * These filled up the MST\_General logs with this message (the general log is not a good place for this
```
23 Nov 2010 11:13:24,901 xc.mst.utils.LogWriter:130 ERROR [Thread-7] - Record Id 18582720 with leader character z not processed.
```


---

JB - I looked at one and it is definitely an authority record.

---



  * examples: 18582721, 18582722, 18582723, 18582724
  * org.jdom.IllegalAddException (405)
    * from the handful I looked at, I believe this exception is due to the fact that there are multiple subfields for a given code.  eg:
```
<marc:datafield ind1="0" ind2=" " tag="776">
  <marc:subfield code="t">Great American court cases.</marc:subfield>
  <marc:subfield code="d">Detroit : Gale Group, c1999-</marc:subfield>
  <marc:subfield code="w">(DLC)99011419</marc:subfield>
  <marc:subfield code="z">0787629472 (set)</marc:subfield>
  <marc:subfield code="z">9780787629472 (set)</marc:subfield>
</marc:datafield>
```
    * process776(SolrTransformationService.java:3087) (341)
      * examples: 26807422, 26807424, 26807425, 26807426

---

JB - subfield z is repeatable for 776, so we should alter the Transformation Service to make sure it will include multiple attributes when it encounters multiple subfields for all those that are repeatable. I can add that as a bug for Transformation. - now documented in Transformation spreadsheet.

---


  * process055(SolrTransformationService.java:455)  (36)
    * examples: 21489896

---

JB - For the example you sent, this looks like a data problem - these subfields in the 055 are NOT repeatable in MARC.  So these should get reported back to CARLI as a data problem.  There is perhaps a bigger problem with Transformation, though, since MARC is not consistent regarding what is repeatable and what is not.  We can't catch all errors of this kind.  Having Normalization check for which ones are repeatable and which ones not would be a huge job.

---


  * process785(SolrTransformationService.java:3151) (13)
  * process780(SolrTransformationService.java:3128) (12)
  * process787(SolrTransformationService.java:3189) (3)

---

JB - you didn't send examples of these but I suspect the problem is similar to the 776 since these are all similar fields.  When I suggest a solution to the bug for the 776 I can include it for all of these fields too.  Now documented in Transformation spreadsheet.

---


  * non-numeric values where numeric values are expected (11)
    * content of tag 880 subfield 6
      * example contents 00-, 30-, 10-, 51-
        * I don't know the record ids.  I added code to provide these the next time it runs through.

---

JB - these MARC tags can legitimately contain non-numeric or alphabetic characters.  Here are some examples from the MARC documentation:
$6880-01
$6100-01/(N
I'm thinking another change is needed for Transformation for this.  Now documented in Transformation spreadsheet.

---


  * tags
    * examples: 26024469 (ews), 32317522 (a86)

---

JB - these just look like garbage for the contributing library to be notified about to deal with in their ILS.