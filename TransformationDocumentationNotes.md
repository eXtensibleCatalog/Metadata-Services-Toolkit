2011-07-12<br />
**who are we documenting for?**
  * programmers
    * programmers benefit from clean, well documented code.  So even if we went this route, there may be work to do.
  * metadata experts?
    * a metadata expert would be interested in knowing **exactly** what is happening, but might get lost trying to read java code.

[JB](JB.md)  The most useful thing for metadata experts is to see a chart of exactly what gets mapped to what, rather than either trying to extract that information from the code itself, or reading a prose description of what the mapping does.  One problem, however, is when the documentation gets too verbose to be contained in chart form and needs to link out to other documents.  I think that is a problem we can solve.

**Strategies for ongoing documentation**
  1. let the [code](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/bens_perma_branch/mst-service/impl/src/java/xc/mst/services/impl/service/SolrTransformationService.java#1981) be the documentation
    * might not be that bad if we write it in a way that a non-programmer could follow.  We might even be able to provide links between methods

[JB](JB.md) Metadata experts will want to have access to just the chart of relevant mappings.  So even if we embed everything in the code itself, I see a use case for maintaining a mapping chart.  I get lots of requests to see the mapping chart.

  1. use a more formal pseudocode approach - perhaps even a defined standard like [ebnf](http://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_Form)
    * this _might_ make it easier for a non-programmer to understand
    * this might end up being as complex as code.
    * we'd probably still want subroutines

[JB](JB.md) I took a quick look at ebnf and there would be a BIG learning curve for that for non-programmers.

  1. follow [existing](PrivateMapsetFour.md), somewhat ambiguous English
    * I'm not sure this can work - it's so ambiguous.

[JB](JB.md) Agree that this prose documentation is ambiguous and not useful.  One of the previous programmers started it, but I don't think others that came after kept it up to date.  I recommend that we abandon this format altogether, except for when we need prose to supplement the mapping table (or maybe the mapping table supplements the prose...)

  1. test cases as documentation - show me, don't tell me

**if we maintain a separate (from the code) set of documentation**
  * it means that maintenance becomes more difficult and fragile.
  * we **must** be vigilant to keep the documentation and code in sync.

[JB](JB.md) - yes, and that has been the problem with the prose documentation.  However, if I'm going to document needed changes to the service using a spec in the form of a mapping table, then that work is already being done, and we just need to keep the code and the spec in sync.

Ben, I experimented with putting the most recent version of the chart into Google Code (as opposed to the wiki) and think this will work pretty well:  https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdHFFOUx2NTZleVdrZVc0eTl0VFFUYVE&hl=en_US

This will still need to be cleaned up, but I think it provides the best place for us to start.


---

<br />
<br />
on 2011-06-27, Ben started going through transformation documentation (more notes [in fogbugz](https://extensiblecatalog.fogbugz.com/default.asp?713))

  * I started going through the code and checking it against the [already existent documentation](PrivateMapsetOne.md)
    * I got through the first few (I'll gain momentum as I go) before deciding to jump into the next bullet
  * Dave wanted to focus on how the code decides when to create additional works/expressions and how it decides which fields are mapped to these additional records, so that's where I'm at now.
    * At first glance the documentation seems _ok_, but should probably be enhanced to really draw out when and how additional works/expressions are created.

[JB](JB.md) - yes, this is stuff that can't be expressed well in chart form, so the supplemental documents that I created should form the basis for some new, more pulled-together document.

  * 730s ([example here](http://128.151.244.146:8080/MetadataServicesToolkit/st/viewRecord.action?recordId=6585186&query=6585186&searchXML=true&selectedFacetNames=&selectedFacetValues=&rowStart=0&startPageNumber=1&currentPageNumber=1))