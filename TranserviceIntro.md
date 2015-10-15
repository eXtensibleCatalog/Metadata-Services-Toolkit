# Intro to MARCXML to XC Schema Transformation Service #

The MARCXML to XC Schema Transformation Service converts a MARCXML Bibliographic or Holdings record into the XC Schema. It moves data from the MARCXML fields into the appropriate FRBR level and elements in an XC Schema record.  To find out more about MARC, <a href='http://en.wikipedia.org/wiki/MARC_standards'>wikipedia</a> is a good place to start.

Records processed by the Transformation Service are expected to have been processed by the XC MARCXML Normalization Service beforehand. This other service normalizes data from selected coded MARC fields (such as the 007) and populates locally-defined 9XX fields with the corresponding English-language MARC vocabulary term for each code. Other MARC fields that need normalization to remove extraneous information (such as the 020) are also handled by the Normalization Service. The mapping of each of these XC-specific 9XX fields created by the XC MARCXML Normalization Service is included in the description below. If a particular MARC field is not listed below and the field is not processed by the MARCXML Normalization Service, then the field is not being mapped to the XC Schema at this time.

This section contains information on where data from each MARCXML field is inserted in the XC record. In order to utilize this Service, the reader is expected to have read the sections of the Metadata Services Toolkit documentation that explain how to set up the MST to harvest records and pass them into Metadata Services.

Before applying the Transformation mappings described in this document, the Service checks the MARCXML Leader 06 to determine the Type of Record to be processed. In cases where a MARC tag is mapped the same way for both MARC Bibliographic and Holdings records, this is indicated below. In other cases when a field is valid for both MARC Bibliographic and Holdings records and there are differences between the mapping for the two formats, these are also indicated below.`*` No indication is given when the tag below is valid in only the MARC Bibliographic Format.

_`*` Some Holdings fields are not being mapped to XC Schema records at this time, because some Holdings data, such as identifiers that apply to the FRBR group 1 entity "manifestation", may need to be mapped differently from a Holdings record than the same data would be from a Bibliographic record. This is especially true if a library has used its MARC Holdings data to represent an additional version of a resource using what is known as the "single record technique." These more complex mappings will be dealt with at a later time._


#### XC WEM records output from input bibliographic records ####

Typically there will be 1 work, 1 expression, and 1 manifestation record output from 1 input bibliographic record.  Obviously, though, some bibliographic records output more than 3 records as they contain more than 1 work and expression.  An example is a classical music recording in which a pianist plays Haydn, Mozart, Beethoven in a concert.  In the current implementation of the MARC-to-XC Transformation service, the number of output expressions will always equal the number of output works.  There will always be 1 manifestation record output for each bibliographic input record.  So assuming no holdings are embedded in an input bibliographic record, the number of output records will always be an odd number:
```
  1 MARC bib =>  3 output xc records =  1 work  +  1 expression  + 1 manifestation
  1 MARC bib =>  5 output xc records =  2 works +  2 expressions + 1 manifestation
  1 MARC bib => 21 output xc records = 10 works + 10 expressions + 1 manifestation
```

#### XC holdings records output ####

Some institutions embed holdings data inside of their bibliographic records.  Some have separate holdings records and do not embed holdings data in their bibliographic records.  Some have a mix of both.  Typically a bibliographic record will output 0 or 1 xc holdings records.  Typically an input holdings record will output 1 xc holdings record.  In both of these cases, though, it's possible to output more than 1 holdings record.
```
  1 MARC bib     =>   0..N xc holdings (in addition to any works/expressions/manifestations)
  1 MARC holding =>   1..N xc holdings
```

#### Creation of additional works and expressions per MARC bib record ####
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

<font color='red'>example input <a href='http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-service/custom/MARCToXCTransformation/test/mock_harvest_input/multipleWEs/010.xml'>here</a> and output <a href='http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-service/custom/MARCToXCTransformation/test/mock_harvest_expected_output/multipleWEs/010.xml'>here</a></font>

#### Other general comments ####
Note that each work and expression record created ideally SHOULD have its own distinctive data that pertains to it (like just the language of that particular expression, or the subject just of that particular work) but most MARC data isn't going to provide enough data to tell us what goes with what so we're basically copying all of the data since that's the best we can do.