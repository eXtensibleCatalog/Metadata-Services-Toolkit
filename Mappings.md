# DC to XC Schema Transformation Service #

The DC to XC Transformation Service converts the DC (simple and qualified) metadata format into XC records. This is done by mapping a finite set of elements to a work, expression, or manifestation.

## DC to XC Mappings ##

The service is agnostic as to whether or not the DC elements are simple or qualified. As long as it is in one of the following namespaces, then it will be processed:
  * http://purl.org/dc/elements/1.1/
  * http://purl.org/dc/terms

The following elements are mapped to the corresponding FRBR record.

|Work|Expression|Manifestation|
|:---|:---------|:------------|
|abstract|available |accessRights |
|audience|bibliographicCitation|accrualMethod|
|coverage|conformsTo|accrualPeriodicity|
|creator|contributor|accrualPolicy|
|isReplacedBy|dateAccepted|alternative  |
|replaces|dateCopyrighted|created      |
|spatial|educationLevel|date         |
|subject|hasFormat |dateSubmitted|
|temporal|hasVersion|description  |
|    |instructionMethod|extent       |
|    |isFormatOf|format       |
|    |isReferencedBy|hasPart      |
|    |isRequiredBy|identifier   |
|    |isVersionOf|isPartOf     |
|    |language  |issued       |
|    |mediator  |license      |
|    |references|medium       |
|    |relation  |modified     |
|    |requires  |provenance   |
|    |source    |publisher    |
|    |type      |rights       |
|    |          |rightsHolder |
|    |          |tableOfContents|
|    |          |title        |
|    |          |valid        |