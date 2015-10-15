Documents
  * Jennifer's Docs - I believe fully satisfy the question of clearly documenting what the service does
    * based on this document and a given set of input, output should be able to created as expected
    * answers questions like
      * when are records considered a match?
      * how are records merged?
  * get actual input
    * Randy has already gotten this from Marcy.  Ben will run the raw marc through the oai-toolkit, run it through marcnormalization, and check it in as input into the service.
    * possibly mock-up the expected output before starting service implementation
  * MarcAggArchitecture
    * This document should be specific as to how the service will be implemented.  This will involve describing database columns, in-memory data structures, and possibly a solr index
    * From our brief discussions it sounds like we'll match up front and merge real-time.


