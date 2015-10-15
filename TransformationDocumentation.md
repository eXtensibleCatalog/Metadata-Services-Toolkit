### General Intro to MARCToXCTransformation service ###
A **[record](GeneralGlossary#record.md)** is an xml document of a specific type.  A **[metadata service](GeneralGlossary#metadata_service.md)** is the process by which 1 input record produces 0..N output records of the same or a different type.  The protocol used to pull records into a service is **[oai-pmh](GeneralGlossary#oai-pmh.md)**.  All services process records one-by-one sequentially.  So oai-pmh might get 5,000 records at a time, but the service still only processes one at a time.  At the end of processing an incoming record (xml document) the service will have decided whether to create any output records.  The Transformation Service inputs MARC [bibs](GeneralGlossary#bibliographic_record_(bib).md) and MARC [holdings](GeneralGlossary#holdings_record_(hold).md) records and outputs [XC/FRBR](GeneralGlossary#Functional_Requirements_for_Bibliographic_Records_(FRBR).md) records.  MARC is a standard first created in the 60s and still used almost unanimously in library catalogs.  [XC schema](GeneralGlossary#xc_schema.md) is a new schema specifically designed for this project, but open to be used for other purposes.  It is based on frbr, which represents bibliographic data using an [entity-relationship model](http://en.wikipedia.org/wiki/Entity-relationship_model).  The types of records defined by the xc schema are works, expressions, manifestations, and holdings.

When a record comes into the service, the first thing checked is whether or not the record is marked as "deleted."  A record can be marked ["deleted"](http://www.openarchives.org/OAI/openarchivesprotocol.html#DeletedRecords) according to [oai-pmh](GeneralGlossary#oai-pmh.md) because this protocol is designed to support synchronization between two repositories - a marked-deleted record is sent from one repository to the other so that the receiving repository can know to actually delete the record.  If the incoming record is deleted, the Transformation Service checks to see if it has previously processed this record.  Each record has a unique id associated with it, which is how the TS knows if it's seen it before.  If TS has seen this record before and it is marked as deleted, then it will mark as delete all of the previous output records that came from this input record (if any exist).  If the input record is marked deleted and TS has not seen this record before it simply produces no output.

The next thing the TS does is parses the raw xml of the record into an object oriented structure so that it has quick access to various parts of the record for successive tasks.

The next thing the TS does is determine whether the incoming record is a bib or a holding.

The leader element is the first element in a marcxml record.  It looks something like this
```
<marc:record xmlns:marc="http://www.loc.gov/MARC21/slim">
  <marc:leader>01202cam a22003490  4500</marc:leader>
  <marc:controlfield tag="001">2</marc:controlfield>
```
If the 7th character in the text of leader record (in the example above that would be 'a') is one of the following characters abcdefghijkmnoprt, then the record is considered a bib.  If the 7th character is one of the following characters uvxy, then the record is considered a holding.  If it is neither a bib nor a holding, it produces no output and continues on to the next input record.

The TS then creates an [AggregateXCRecord](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-common/src/java/xc/mst/bo/record/AggregateXCRecord.java) object.  This object encompasses all of the output records derived from the input record.  A marcxml record mostly consists of datafields.  Those datafields look like this:
```
  <marc:datafield ind1=" " ind2=" " tag="010">
    <marc:subfield code="a">76007005</marc:subfield>
  </marc:datafield>
  ...
  <marc:datafield ind1=" " ind2=" " tag="050">
    <marc:subfield code="a">RC514</marc:subfield>
    <marc:subfield code="b">.H34 1976</marc:subfield>
  </marc:datafield>
```
The first datafield would be referred to as the 010 field, the second the 050 field.


---

### Processing a marcxml bib record ###
If the record is a bib, it processes each of these fields in order:
```
010 015 016 022 024 028 030 035 037 050 055 060 074 082 084 086 090 092 100 110 111 130 210 222 240 243 245 246 
247 250 254 255 260 300 310 321 362 440 490 500 501 502 504 505 506 507 508 510 511 513 515 518 520 521 522 525 
530 533 534 538 540 544 546 547 550 555 580 586 59X 600 610 611 630 648 650 651 653 654 655 720 740 752 760 765 
770 772 773 775 776 777 780 785 786 787 800 810 811 830 852 856 866 867 868 931 932 933 934 935 937 939 943 945 
947 959 963 965 967 969 700 710 711 730
```
The vast majority of the processing of the above fields are simple mappings from the marcxml record to an element in one of the newly created frbr records.  For example using the above marcxml input, you'd get something like this:
```
<xc:frbr xmlns:xc="http://www.extensiblecatalog.info/Elements">
  <xc:entity id="oai:mst.rochester.edu:MetadataServicesToolkit/marctoxctransformation/13359451" type="manifestation">
    <xc:recordID type="LCCN">76007005</xc:recordID>
    ...
```
and
```
<xc:frbr xmlns:xc="http://www.extensiblecatalog.info/Elements"
         xmlns:dcterms="http://purl.org/dc/terms/">
  <xc:entity id="oai:mst.rochester.edu:MetadataServicesToolkit/marctoxctransformation/13359452" type="work">
    <dcterms:subject xsi:type="dcterms:LCC">RC514</dcterms:subject>
    ...
```
Specifics about the way these fields are mapped can be found on the following pages:
[0-199](MapsetOne.md),
[200-499](MapsetTwo.md),
[500-699](MapsetThree.md),
[700-799](MapsetFour.md),
[800-999](MapsetFive.md).


Typically there will be 1 work, 1 expression, and 1 manifestation record output from 1 input bibliographic record.  Obviously, though, some bibliographic records output more than 3 records as they contain more than 1 work and expression.  An example is a classical music recording in which a pianist plays Haydn, Mozart, Beethoven in a concert.  In the current implementation of the MARC-to-XC Transformation service, the number of output expressions will always equal the number of output works.  There will always be 1 manifestation record output for each bibliographic input record.  So assuming no holdings are embedded in an input bibliographic record, the number of output records will always be an odd number:
```
  1 MARC bib =>  3 output xc records =  1 work  +  1 expression  + 1 manifestation
  1 MARC bib =>  5 output xc records =  2 works +  2 expressions + 1 manifestation
  1 MARC bib => 21 output xc records = 10 works + 10 expressions + 1 manifestation
```

When a record with a 700, 710, 711, or 730 field is found with 2nd indicator 2, the general rule is to create additional works and expressions (beyond the assumed ones for any bib).  It's a little more nuanced, but that's a good general rule. The service creates a SEPARATE work record for each these 7XX field with 2nd indicator 2.  The subfields below are mapped:
> _**Note**: that for each of these subfield "l" has been removed from the mapping_
```
  700 kmnoprst
  710 kmnoprst
  711 fkpst  
  730 adgkmnoprst    
```
to titleOfTheWork for each of these fields so that each work record contains this element plus work/creator mapped from the corresponding 959.  Transformation Service also copies all other elements from the ORIGINAL work record (except the creator and titleOfTheWork) into the new work record.

Transformation service also creates a SEPARATE expression record for each of these 7XX field with 2nd indicator 2, to go with the separate work record that is also created.  This expression record contains ALL of the data from the expression record created from the original MARC record as a whole (i.e. just make a copy of it).  There is one exception:  replace the titleOfTheExpression (formerly expressionTitle) from the original expression record (the one mapped from the 130 or 240) with the titleOfTheWork (formerly workTitle) from the corresponding work record (i.e. mapped from the 7XX $t and following subfields) so that each expression record and work record created from the 7XX field have the same data in their respective titleOfTheWork and titleOfTheExpression elements.  The one difference here is that 730 $l (language) is mapped to titleOfTheExpression but not to titleOfTheWork.

example input [here](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/bens_perma_branch/mst-service/custom/MARCToXCTransformation/test/mock_harvest_input/multipleWEs/010.xml) and output [here](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/bens_perma_branch/mst-service/custom/MARCToXCTransformation/test/mock_harvest_expected_output/multipleWEs/010.xml)

Note that each work and expression record created ideally SHOULD have its own distinctive data that pertains to it (like just the language of that particular expression, or the subject just of that particular work) but most MARC data isn't going to provide enough data to tell us what goes with what so we're basically copying all of the data since that's the best we can do.

The other case where processing datafields does something other than simply mapping to an xc element in either a work, expression, or manifestation is when holdings data is embedded in marcxml bibs.  Some institutions embed holdings data inside of their bibliographic records.  Some have separate holdings records and do not embed holdings data in their bibliographic records.  Some have a mix of both.  Typically a bibliographic record will output 0 or 1 xc holdings records.  Typically an input holdings record will output 1 xc holdings record.  In both of these cases, though, it's possible to output more than 1 holdings record.
```
  1 MARC bib     =>   0..N xc holdings (in addition to any works/expressions/manifestations)
```
The datafield that signals the TS to create a new xc-holdings is the 852 field.  More details on how 852s are handled can be found [here](MapsetFive#852.md).


---

### Processing a marcxml holdings record ###
Just as marcxml bib records can produce 0..N xc holdings, marcxml holdings records can produce 1..N xc holdings records.
```
  1 MARC holding =>   1..N xc holdings
```
marcxml holdings records contain references (aka uplinks) to marcxml bibs.  Typically the mapping is done from the 004 field in a holding to an 001 field in a bib:
```
<marc:record xmlns:marc="http://www.loc.gov/MARC21/slim">
  <marc:leader>01202cam a22003490  4500</marc:leader>
  <marc:controlfield tag="001">2</marc:controlfield>
  <marc:controlfield tag="003">NRU</marc:controlfield>
```

```
<marc:record xmlns:marc="http://www.loc.gov/MARC21/slim">
  <marc:leader>00198cx  a22000973  4500</marc:leader>
  <marc:controlfield tag="001">2</marc:controlfield>
  <marc:controlfield tag="003">NRU</marc:controlfield>
  <marc:controlfield tag="004">2</marc:controlfield>
```
note: marc records are uniquely keyed by the combination of record type, 001, and 003.

Some holdings records contain more than one reference to bibs.  These are typically called "bounds-with" holdings.  In these cases, the first reference is still put in the 004 and additional references are put in 014s
```
<marc:record xmlns:marc="http://www.loc.gov/MARC21/slim">
  <marc:leader>00266cx  a22001093  4500</marc:leader>
  <marc:controlfield tag="001">106982</marc:controlfield>
  <marc:controlfield tag="003">NRU</marc:controlfield>
  <marc:controlfield tag="004">83479</marc:controlfield>
  ...
  <marc:datafield ind1=" " ind2=" " tag="014">
    <marc:subfield code="a">01316877</marc:subfield>
  </marc:datafield>
```

These references/uplinks are preserved in the xc output records like this:

```
<xc:frbr xmlns:xc="http://www.extensiblecatalog.info/Elements">
  <xc:entity id="oai:mst.rochester.edu:MetadataServicesToolkit/marctoxctransformation/13359454" type="holdings">
    ...
    <xc:manifestationHeld>oai:mst.rochester.edu:MetadataServicesToolkit/marctoxctransformation/123</xc:manifestationHeld>
    <xc:manifestationHeld>oai:mst.rochester.edu:MetadataServicesToolkit/marctoxctransformation/456</xc:manifestationHeld>
  </xc:entity>
</xc:frbr>
```


Holdings records (both input marcxml and output xc) contain references (aka uplinks)
```
004 014 506 852 856 866 867 868
```
Specifics about the way these fields are mapped can be found on the following pages:
[0-199](MapsetOne.md),
[500-699](MapsetThree.md),
[800-999](MapsetFive.md).

### to add ###
uplinks between works, expressions, and manifestations.
holdings - go through each of the above fields.