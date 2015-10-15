<font color='red'><b><i>This service and its documentation is a work in progress</i></b></font>

<img src='https://docs.google.com/drawings/pub?id=1wqHaVTsxBiqOXJHrIvJhKzIZGE4N1LGCmU0FW8c6f4c&w=724&h=532&filetype=.png' />

See [this page](ServicesExplained.md) for general information on MST services.  The MARC Aggregagation Service takes MARC bibs and holdings records as input and outputs MARC bibs and holdings records.  Its function is to match MARC bib records that describe the same resource and aggregate these records into a single record.  Details on how a match is found can be found on [on this page](MarcAggMatchPointsAndErrorCases.md).  Details on how matched records are merged into one is described on [this page](MarcAggMerging.md).

### Processing Steps ###

_There are some nuances that are left out of this high-level description.  For a closer look see the more fine-grained details [here](MarcAggArchitecture#Processing_Steps.md)_

  * The first thing the ([MAS](MarcAggArchitecture#MAS.md)) does is to determine if the ([in\_process\_record](MarcAggArchitecture#in_process_record.md)) is a [bib](GeneralGlossary#bibliographic_record_(bib).md) or a [holding](GeneralGlossary#holdings_record_(hold).md).
  * If the record is a bib:
    * pull out of the [match point](MarcAggArchitecture#match_point.md) values from the record (eg 035, 010a, 020a, 022a, etc).
    * check the [in\_process\_record](MarcAggArchitecture#in_process_record.md)'s match points against the match points of all other [processed\_records](MarcAggArchitecture#processed_records.md)
    * If the MAS finds any [processed\_records](MarcAggArchitecture#processed_records.md) with equivalent match points, it then runs the [match rules](MarcAggArchitecture#match_rule.md) to determine if the [in\_process\_record](MarcAggArchitecture#in_process_record.md) matches any of the [processed\_records](MarcAggArchitecture#processed_records.md) (because equivalent match points don't guarantee a match).  For example the 010a might match another record, but if the 020a doesn't match as well, then the records are not considered a match.  (see [step 2a](MarcAggMatchPointsAndErrorCases#Step_2A:.md)).
    * If it finds a match, then there will be an [existing output record](MarcAggArchitecture#existing_output_record.md) using the matched input record as the [record of source](MarcAggArchitecture#record_of_source.md).  Comparing the [merge scores](MarcAggArchitecture#merge_score.md) of the in\_process\_record and all matched input records, it will be determined whether the in\_process\_record should be used as the [record of source](MarcAggArchitecture#record_of_source.md) (overwriting the [existing output record](MarcAggArchitecture#existing_output_record.md)) or whether the [existing output record](MarcAggArchitecture#existing_output_record.md) should remain untouched.
    * If there was no match found or if the [in\_process\_record](MarcAggArchitecture#in_process_record.md)'s [merge score](MarcAggArchitecture#merge_score.md) is higher than the current [record of source's](MarcAggArchitecture#record_of_source.md) merge score
      * the in\_process\_record will be used as the record\_of\_source (creating or replacing the [static\_content](MarcAggArchitecture#static_content.md) of the existing\_output\_record
    * add the in\_process\_record's [dynamic\_content](MarcAggArchitecture#dynamic_content.md) to the output record.
  * If the record is a holding:
    * Pass it along (create an output holding record, 1 holding in, 1 holding out, do not modify it in any way).