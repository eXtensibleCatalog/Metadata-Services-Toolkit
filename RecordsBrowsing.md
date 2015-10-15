# Browsing Metadata Records #

Only users with “Browse Records” permission can perform the actions described in this section.

The **Browse Records** tab allows users to search all records in the MST’s Solr index using a faceted browsing interface. This interface facets on the name of the repository from which a record was harvested, the name of the service which produced the record, the format of the record, the set(s) which contain(s) the record, the harvest event in which the record was harvested, and the errors reported for the record. To display records, either select a facet or enter a search in the search box along with the identifier that describes the type of data you are searching for.  The records are indexed by the MST's Solr Index Service.  See below for more information on this service.

Selected data gets indexed for each Metadata Record, and the user can access this data by selecting the identifier it was indexed with along with typing in the string to search for.  This data gets indexed in two ways, individually and as part of an overall group.  For instance various title fields get indexed including xc schema elements xc:titleOfExpression, dcterms:title, rdvocab:titleOfWork and marc elements marc:datafield '245'$ab.  Each unique piece of data gets indexed along with a unique identifier, i.e. 'xc:titleOfExpression', 'dcterms:title', 'rdvocab:titleOfWork' and marc elements 'marc:datafield '245'$ab' as well as with group identifier 'title.'  So if you use the broader identifiers like 'title' you'll get more record hits.  All identifiers are listed below.

Clicking on the **OAI Identifer** of a record will display all the details about that record as well as the XML for the record itself. Clicking on the **Successors** link for a record will display all records which were created when a metadata service processed the record. Finally, clicking on the **Predecessors** link will display all records that a metadata service used to produce the record.


## Identifier List: ##

```
        "Record Identifier"
        " - xc:recordID"
        " - OAI Header ID"
        " - marc:controlfield 001 Bibliographic"
        " - marc:controlfield 001 Holdings"
        " - marc:datafield 035 Bibliographic"
        "Resource Identifier"
        " - dcterms:identifier Manifestation"
        " - dcterms:identifier Holdings"
        "Title"
        " - marc:datafield 245$ab"
        " - rdvocab:titleOfWork"
        " - xc:titleOfExpression"
        " - dcterms:title"
        "Call Number"
        " - xc:callNumber"
        " - marc:datafield 852$h"
        " - marc:datafield 852$i"
        " - marc:datafield 953$h"
        "Creator/Contributor"
        " - xc:contributor"
        " - xc:creator"
        " - dcterms:contributor"
        " - dcterms:creator" 
        " - rdarole:author"
        " - rdarole:compiler"
        " - rdarole:composer"
        " - rdarole:director"
        " - rdarole:editor"
        " - rdarole:illustrator"
        " - rdarole:performer"
        " - rdarole:producer"
        " - rdarole:speaker"
        " - rdarole:translator"
        " - marc:datafield 100$a"
        " - marc:datafield 110$a"
        " - marc:datafield 111$a"
        " - marc:datafield 700$a"
        " - marc:datafield 710$a"
        " - marc:datafield 711$a"
        "Link to Parent Record"
        " - xc:workExpressed"
        " - xc:expressionManifested"
        " - xc:manifestationHeld"
        " - marc:controlfield 004"
        " - marc:datafield 014$a"
```

## Solr Index Service ##

This service runs when no other MST services or harvests are running.  For instance, if the MST harvests from a repository and no processing directives are setup to use the repository data, and nothing else is scheduled in the MST, the indexer will run when the harvest completes.  Generally, an MST user sets up a harvest, and some processing directives directing the MST to process the records through the services, and the indexer runs when that work completes.