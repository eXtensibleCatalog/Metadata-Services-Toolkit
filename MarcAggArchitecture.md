This document goes into great detail about the implementation of the MARC Aggregation Service (abbreviated MAS below).  See the [intro page](MarcAggIntro.md) for a basic overview of the MARC Aggregration Service.



---

## Current status of this document ##
  * Under review by Dave

---

## TODOs ##
  * address holdings embedded in bibs (could be multiple)

---

## Rules ##
    * ### Transitive Relation ###
      * if A matches B and B matchs C, then A and C are considered matches regardless of whether A and C match on their own.  (see [wikipedia](http://en.wikipedia.org/wiki/Transitive_relation)).

---

## Processing Steps ##
    * if [in\_process\_record](#in_process_record.md) is a [bib](GeneralGlossary#bibliographic_record_(bib).md), then:
      * if [in\_process\_record](#in_process_record.md) is marked as [deleted](http://www.openarchives.org/OAI/openarchivesprotocol.html#DeletedRecords) (according to [oai-pmh](GeneralGlossary#oai-pmh.md)):
        * if the in\_process\_record's oai-id does not match any of the identifiers in the [processed\_records](#processed_records.md) set (this is determined by checking the [pred2succ map](#pred2succ_map.md)), then:
          * discard the in\_process\_record and continue on to the next.
        * otherwise we can infer that an earlier version of this [in\_process\_record](#in_process_record.md) had previously been processed by the [MAS](#MAS.md)
          * _note: an [oai-pmh\_harvester](GeneralGlossary#oai-pmh_harvester.md) will not be informed of this holdings' change in status until the incoming holding is marked as deleted.  It does means that this record will not be included in any subsequent oai-pmh\_harvests._
          * if the in\_process\_record has not been merged with any other [processed\_records](#processed_records.md) (determined by checking [merged\_records\_map](#merged_records_map.md)), then:
            * mark the [existing\_output\_record](#existing_output_record.md) as deleted
          * otherwise the in\_process\_record must be [unmerged](#Unmerge.md)
      * otherwise the in\_process\_record is not deleted, but active
        * if in\_process\_record is an update (determined by checking the [pred2succ map](#pred2succ_map.md)), then:
          * determine what the previous match points were by querying the [match\_point tables](#matchpoint_tracking.md) in real time
          * [determine matches](#Determine_matches.md) and [merge](#Merge.md) those records accordingly
          * if there were prior record matches that no longer match, those records must be [unmerged](#Unmerge.md)
        * otherwise
          * [determine matches](#Determine_matches.md) and [merge](#Merge.md) those records accordingly
        * pull out and preserve the [dynamic\_content](#dynamic_content.md)
        * if in\_process\_record is record\_of\_source, pull out and preserve [static\_content](#static_content.md)
    * otherwise the [in\_process\_record](#in_process_record.md) is a [holdings](GeneralGlossary#holdings_record_(hold).md):
      * otherwise this holding
### Determine matches ###
    * Determine all [match points](#match_point.md) of the [in\_process\_record](#in_process_record.md).
    * For each [match\_point](#match_point.md) of the [in\_process\_record](#in_process_record.md), find which [processed\_records](#processed_records.md) have [match\_points](#match_point.md) equal to the in\_process\_record.  This is determined by checking the [match\_point caches](#match_point_maps.md).  We'll call this record set [processed\_records\_with\_common\_match\_points](#processed_records_with_common_match_points.md).
    * Run each [match\_rule](#match_rule.md) to determine if there are any [processed\_records\_with\_common\_match\_points](#processed_records_with_common_match_points.md) that match the in\_process\_record.  The match\_rules have to be run because having equivalent match points doesn't guarantee a match.  For example the 010a might match another record, but if the 020a doesn't match as well, then the records are not considered a match.  (see [step 2a](MarcAggMatchPointsAndErrorCases#Step_2A:.md)).
    * if there are [matched\_records](#matched_records.md), then [merge](#Merge.md)
      * ensure that the in\_process\_record still matches each record it previously matched.
        * if there are records that no longer match, then they need to be [unmerged](#Unmerge.md)
    * otherwise create a new output\_record

### Merge ###
  * if there are 2 or more [existing\_output\_record](#existing_output_record.md) for the [matched\_records](#matched_records.md) (this scenario may result from a [Transitive\_Relation](#Transitive_Relation.md)), then:
    * preserve only one of the existing\_output\_record - the one whose [record\_of\_source](#record_of_source.md) has the highest [merge\_score](#merge_score.md).
    * mark all of the other [existing\_output\_records](#existing_output_records.md) as deleted
      * determine the 001s of the soon\_to\_be\_deleted existing\_output\_records by issuing a realtime query to [merged\_035s table](#merged_035.md)
  * Determine the [merge\_score](#merge_score.md) of the [in\_process\_record](#in_process_record.md).
    * If it is higher than the merge\_score of the [existing\_output\_record](#existing_output_record.md), then the in\_process\_record will become the new [record\_of\_source](#record_of_source.md).
      * Determine the [static\_content](#static_content.md) of the in\_process\_record.
    * otherwise the record\_of\_source for the existing\_output\_record remains the same

### Unmerge ###
  * For records that need to be unmerged, simply delete all remnants of previously matching records that no longer match.
    * delete them out of the in-memory data structures and the db
    * reprocess each record

---

## Matching Implementation ##
The [Match Points](MarcAggMatchPointsAndErrorCases.md) document describes the rules for determining whether 2 records should be considered a match.  Each [match\_point](#match_point.md) will implement the
[FieldMatcher](http://www.extensiblecatalog.org/doc/MST/javadoc/marcaggregation/xc/mst/services/marcaggregation/matcher/FieldMatcher.html) interface.  A couple of stub implementations are:
  * [SystemControlNumber](http://www.extensiblecatalog.org/doc/MST/javadoc/marcaggregation/xc/mst/services/marcaggregation/matcher/SystemControlNumberMatcher.html)
  * [LccnMatcher](http://www.extensiblecatalog.org/doc/MST/javadoc/marcaggregation/xc/mst/services/marcaggregation/matcher/LccnMatcher.html)

The above classes demonstrate how to determine matches on specific fields.  Each of these matchers will be assigned names in file: custom.properties (the standard config file for the service)  These 4 matchers are used currently:
```
matchers.value=SystemControlNumber, Lccn, ISBN, ISSN, x024a
```
The match\_rules will also be declared in a config file.  Those declarations will look something like this:

file: custom.properties (the standard config file for the service)
```
match.rules.value=Step1a, Step2abc
```
  * This means that we have 2 enabled match rules.

---

## Merging Implementation ##
  * The merging algorithm will follow the logic set out in [scenario #2](MarcAggMerging#2nd_scenario:.md).


---

## Data Structures ##
(what follows is largely not how its implemented, but saving it because it could be the source of good ideas).

Match point values are stored in memory as much possible.  Integral values are easy.  String fields that are enumerated are easy (eg OCLC, NRU).  Text fields for exact matching are a little more difficult.  Text fields for fuzzy matches are even more difficult, but might be w/in our grasp if we hit a home-run with exact text field matching.  See [here](ServicesExplained#Platform_Data_Structures.md) for generic platform structures.

  * ### in-memory data structures ###
> > each of these will also be persisted so that they be fully loaded again on successive runs.
      * #### pred2succ\_map ####
        * purpose: to determine if there is an [existing\_output\_record](#existing_output_record.md) associated with the [in\_process\_record](#in_process_record.md)
        * key: input\_record\_id
        * value: output\_record\_id
      * #### merged\_records\_map ####
        * purpose: to determine (combined with the pred2succ\_map) what records have been merged with a particular record.
        * key: output\_record\_id
        * value: input\_record\_ids
        * implementation: this will be a wrapper of multiple maps
      * #### merge\_scores ####
        * purpose: this allows us to easily determine whether the in\_process\_record should be the [record\_of\_source](#record_of_source.md)
        * key: output record id
        * value: the [existing\_output\_record's](#existing_output_record.md) [record\_of\_source](#record_of_source.md) [merge\_score](#merge_score.md)
      * #### bibs2holdings ####
        * purpose: when a bib is merged, we need to know if there are any holdings that already point to it.  If so, we'll need to update that holding.
        * key: 001 & 003
        * value: output holding id
      * #### match\_point\_maps ####
        * purpose: each [FieldMatcher](http://www.extensiblecatalog.org/doc/MST/javadoc/marcaggregation/xc/mst/services/marcaggregation/matcher/FieldMatcher.html) implementation has it's own structure because the match point might be a single field, a combination of field/subfield, or some other combination.
        * examples: matchpoints\_035, matchpoints\_010a
        * key: matchpoint key
        * value: input\_record\_id
  * ### Diagrams ###
    * see [here](ServicesExplained#Platform_Data_Structures.md) for generic platform structures.
<img src='https://docs.google.com/drawings/pub?id=18RJm5vK7wdEvQtOoAFVbzJNOaYTw-Dj4zgYbuoUDorQ&w=940&h=713&filetype=.png' />
<img src='http://www.extensiblecatalog.org/doc/MST/4wiki/erd.png' />
    * see [here](ServicesExplained#Platform_Data_Structures.md) for generic platform structures.
  * ### db tables ###
    * #### merge\_tracking ####
      * #### merged\_records ####
        * purpose: provides a mapping of input records to output records.  This allows for 2 paths:
```
   -------------------------------------------------------------------
  | given               | can be determined                           |
  |-------------------------------------------------------------------|
  | an output_record_id | all the input_records that have been merged |
  |                     | together to create this output_record       |
  |-------------------------------------------------------------------|
  | an input_record_id  | all the other input_records that have been  |
  |                     | merged with this input_record and the       |
  |                     | corresponding output_record                 |
   -------------------------------------------------------------------
```
        * maps to: [pred2succ\_map](#pred2succ_map.md) and [merged\_records\_map](#merged_records_map.md)
      * #### merge\_scores (db) ####
        * purpose: this allows us to easily determine whether the in\_process\_record should be the [record\_of\_source](#record_of_source.md)
        * maps to: [merge\_scores](#merge_scores.md)
    * #### holdings\_activation ####
      * #### bibs2holdings ####
        * purpose: when a bib is merged, we need to know if there are any holdings that already point to it.  If so, we'll need to update that holding.
        * desc: the 001 is most likely always a numeric, however, the MAS shouldn't fail if an alpha is present.  For this reason, I'm allocating 2 columns of different types for the 001.
    * #### matchpoint\_tracking ####
      * purpose: these tables provide a mapping from various matchpoits to input\_records
    * #### dynamic\_record\_data ####
      * description: these tables comprise the [dynamic\_content](#dynamic_content.md) of output\_records
        * #### merged\_035 ####
          * purpose: when a merge happens, additional 035s need to be added to the output\_record.  This table prevents us from reading, parsing, and re-writing the full record xml when that happens.
        * #### merged\_904 ####
          * purpose: when a merge happens, the holdings that reference an existing\_output\_record that is to be deleted needs to be updated.  This table prevents us from reading, parsing, and re-writing the full record xml when that happens.
  * ### lucene ###
    * purpose: to support the text matching (exact and possibly fuzzy) of the FieldMatchers.
    * as I implement each specific matcher, I'll fill this out in more detail, but for now one example is the 245a (http://www.loc.gov/marc/bibliographic/bd245.html)
    * feasibility
      * first iteration
        * store full text unadulterated in a lucene index containing only 245a's.  I don't know how fast this will be.  This is a lookup that needs to be done for every single record, so even 1-2 ms might be too slow.  It's worth trying to see how fast it'll be.
      * second iteration (if first isn't fast enough)
        * I could split each field-specific index into 2 separate indexes:
          1. the first index only indexes the first x chars and can fit entirely in memory.  When a match is found and it's size x, then we need to check the second index
          1. the second index contains the full text field and can't fit in memory.  It will be queried much less frequently, but can answer definitively.
      * third iteration (if second isn't fast enough)
        * try the same approach with MySQL.
      * fourth iteration (if third isn't fast enough)
        * go back to drawing board)

---

## Implementation plans ##
  * Testing correctness of output
    * This depends on getting examples.
    * For the first iteration, having some examples for just a few of the field types would be enough.
  * Testing performance
    * I will test performance as soon as possible to determine the feasibility of the design.

---

## tricky scenarios ##
  * matchpoints change scenarios
    1. A is processed.  A is updated and no longer has matchpoint MP1. How to reflect this in the data structures (remove MP1)?
      * will query the necessary db tables live. updates do not need to be quite as optimized and this query should only take 1-2 millis.
  * merge\_score scenarios
    1. A and B are merged.  B has a higher merge\_score than A and is kept as the record\_of\_source.  B is updated and now has a lower merge\_score than A.
      * B will remain record\_of\_source.  An input record never stops being the record\_of\_source unless another merged\_record is processed with a higher merge\_score.
  * unmerge scenarios
    1. A is processed. B is processed.  C is processed and matches A and B and all 3 are merged.  C is updated and matches neither A nor B.  All 3 records must be unmerged.
    1. A is processed. B is processed and matches A.  C is processed and matches B.  C is updated and no longer matches B.  The above solution works.
    1. A is processed. B is processed and matches A.  C is processed and matches B.  C is updated and still longer matches B.
      * solution for all three is documented in [unmerge above](#Unmerge.md)

---

## Glossary ##
  * ### dynamic\_content ###
> > In contrast to [static\_content](#static_content.md), dynamic\_content is the portion of content in an output record that originates from more than one input record.  The obviously necessary fields are those that contain ids and references.  In the case of bibs, the [MAS](#MAS.md) moves 001s and 003s to 035as.  In the case of holdings, the [MAS](#MAS.md) moves 004s and 014s to 904s. In both of these cases, the fields from all of the input records are preserved as dynamic\_content in the associated output record.  These id/reference fields plus the [keep\_fields](#keep_fields.md) comprise the dynamic\_content.
  * ### held\_records ###
> > The set of holdings records in the set of [processed\_records](#processed_records.md) that are still waiting for their referenced bibs to arrive.
  * ### in\_process\_record ###
> > The input record that is currently being processed by the [MAS](#MAS.md).
  * ### keep\_fields ###
> > [dynamic\_content](#dynamic_content.md) that is guaranteed to be preserved in the output record.  Besides these fields, the only content guaranteed to be preserved in the output record is the content of the 001 and 003 (for bibs) and the content of the 001, 003, 004, and 014s for holdings.  All the other content is typically lost for all merged records other than the record\_of\_source.  keep\_fields allow the ability to truly merge the content of multiple input records into one output record.
  * ### MAS ###
> > the MARC Aggregation Service which is an [MST service](GeneralGlossary#metadata_service.md).
  * ### match\_point ###
> > a specific field/subfield in a record which is used as a basis for determining a match.  (see [this page](MarcAggMatchPointsAndErrorCases.md) for more).
  * ### match\_rule ###
> > a conditional statement that when true denotes that two records match.  A match\_rule is made up of one or more [match\_points](#match_point.md). (see [Step 2A](MarcAggMatchPointsAndErrorCases#Step_2A:.md))
  * ### matched\_records ###
> > the set of [processed\_records](#processed_records.md) that match the [in\_process\_record](#in_process_record.md).
  * ### merge\_score ###
> > the computed score of an input record according to [this rule (2nd scenario)](MarcAggMerging#2nd_scenario:.md).  This computation is made for all [matched\_records](#matched_records.md) and the one with the highest score becomes the [record\_of\_source](#record_of_source.md).
  * ### output\_records ###
> > the set of records in the output repository of the service (and available for oai-pmh harvesting).
  * ### existing\_output\_record ###
> > an output record which is a successor either of the [in\_process\_record](#in_process_record.md) (in the case of an update) or a record from the [processed\_records](#processed_records.md) that matches the in\_process\_record.
  * ### processed\_records ###
> > the set of input records that the [MAS](#MAS.md) has already processed.
  * ### processed\_records\_match\_points ###
> > the set of all match points of all [processed\_records](#processed_records.md).
  * ### processed\_records\_with\_common\_match\_points ###
> > the set all records that have common match points with the [in\_process\_record](#in_process_record.md) (not necessarily matches).  The distinction between this set and [matched\_records](#matched_records.md) is that having equivalent match points doesn't guarantee a match.  For example the 010a might match another record, but if the 020a doesn't match as well, then the records are not considered a match.  (see [step 2a](MarcAggMatchPointsAndErrorCases#Step_2A:.md)).
    * DL- what is the difference between having common match points, and matches?  I think this tripped me up in the other document and I commented there as well.  Perhaps you could carefully define these two "states".  <font color='red'>Dave, is this clear now?</font>
  * ### record\_of\_source ###
> > the particular input record out of the set of [matched\_records](#matched_records.md) that is used as the basis for the [static\_content](#static_content.md) of the output record.  This is the record with the highest [merge\_score](#merge_score.md) out of the set of a particular set of [matched\_records](#matched_records.md)
  * ### static\_content ###
> > the static content of an output record is the portion of the [record\_of\_source](#record_of_source.md) that is copied unadulterated from the [record\_of\_source](#record_of_source.md) to the output record.  For the current implementation, this is the entire record minus the 001, 003, 035a, 004, and 014 fields.  If there is content from all merged records that needs to be preserved, it should be done using [keep\_fields](#keep_fields.md).  The static\_content is stored in the xml column of the records\_xml table (found in the "Repository Core" grouping of tables in [this diagram](MarcAggArchitecture#Diagrams.md)

<img src='http://www.extensiblecatalog.org/doc/MST/web_safe_GIFs/gifs/ffffff.gif' height='900' width='1' />