

### MARC Transformation Service for 700, 710, 711 and 730 with 2nd indicator 2 ###
### (creation of additional works and expressions) ###

This document was originally a spreadsheet in UR's docushare. The original can be found [here](http://docushare.lib.rochester.edu/docushare/dsweb/Services/Document-35072).

The RDA names have changed again – as of July 2010 the element names should be titleOfThe Work and titleOfTheExpression.
<font color='red'>Is this still true?  Looks like our output is titleOfWork and titleOfExpression</font>

DL - I think Jennifer has a list of transformation service changes that we should schedule to work on.

Transformation service creates a SEPARATE work record for each 7XX field with 2nd indicator 2.  Map the subfields below:

> _**Note**: that for each of these subfield “l” has been removed from the mapping_
```
700 	kmnoprst
710	kmnoprst
711 	fkpst  
```
to titleOfTheWork for each of these fields so that each work record contains this element plus work/creator mapped from the corresponding 959.  Transformation Service also copies all other elements from the ORIGINAL work record (except the creator and titleOfTheWork) into the new work record.

> _**Note**: the service was previously mapping this to title not workTitle, which should be changed so that it is now titleOfTheWork._

Transformation service also creates a SEPARATE expression record for each 7XX field with 2nd indicator 2, to go with the separate work record that is also created.  This expression record contains ALL of the data from the expression record created from the original MARC record as a whole (i.e. just make a copy of it).  There is one exception:  replace the titleOfTheExpression (formerly expressionTitle) from the original expression record (the one mapped from the 130 or 240) with the titleOfTheWork (formerly workTitle) from the corresponding work record (i.e. mapped from the 7XX $t and following subfields) so that each expression record and work record created from the 7XX field have the same data in their respective titleOfTheWork and titleOfTheExpression elements.  The one difference here is that 730 $l (language) is mapped to titleOfTheExpression but not to titleOfTheWork.

#### Instruction for 730 with 2nd indicator 2: ####
<font color='red'>example input <a href='http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/bens_perma_branch/mst-service/custom/MARCToXCTransformation/test/mock_harvest_input/multipleWEs/010.xml'>here</a> and output <a href='http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/bens_perma_branch/mst-service/custom/MARCToXCTransformation/test/mock_harvest_expected_output/multipleWEs/010.xml'>here</a></font>

Transformation service creates a SEPARATE work record for each 7XX field with 2nd indicator 2.  Map the subfields below:

> _**Note**: that “l” has been deleted – we won’t be mapping it to titleOfTheWork but only to titleOfTheExpression as indicated below._
```
 730	adgkmnoprst    
```

to titleOfTheWork for each of these fields so that each work record contains this element (note that there should not be a corresponding 959).    Transformation Service should also copy all other elements from the ORIGINAL work record (except the creator and titleOfTheWork) into the new work record.

> _**Note**:  the service was previously mapping this to title not workTitle, which should be changed so that it is now titleOfTheWork._

Transformation service also creates a SEPARATE expression record for each 7XX field with 2nd indicator 2, to go with the separate work record that is also created.  This expression record contains ALL of the data from the expression record created from the original MARC record as a whole (i.e. just make a copy of it).  There is one exception:  replace the titleOfTheExpression (formerly expressionTitle) from the original expression record (the one mapped from the 130 or 240) with the titleOfTheWork (formerly workTitle) from the corresponding work record (i.e. mapped from the 7XX $t and following subfields) so that each expression record and work record created from the 7XX field have the same data in their respective titleOfTheWork and titleOfTheExpression elements.  The one difference here is that 730 $l (language) SHOULD be mapped to titleOfTheExpression but not to titleOfTheWork.

#### Other general comments ####
Note that each work and expression record created ideally SHOULD have its own distinctive data that pertains to it (like just the language of that particular expression, or the subject just of that particular work) but most MARC data isn’t going to provide enough data to tell us what goes with what so we’re basically copying all of the data since that’s the best we can do.