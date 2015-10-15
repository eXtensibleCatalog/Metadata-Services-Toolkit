The <a href='http://www.extensiblecatalog.org'>eXtensible Catalog (XC)</a> project is an open-source suite of applications that provides libraries with an alternative way to reveal their collections to library users.

Please visit our project website at http://www.extensiblecatalog.org for a more complete overview of the eXtensible Catalog project and the software we are creating.

### Latest News ###
  * **Update:** 2015-07-09: 1.5.5 released.  See [release notes](http://code.google.com/p/xcmetadataservicestoolkit/wiki/ReleaseNotes)

  * **Update:** 2014-10-22: 1.5.4 released.  See [release notes](http://code.google.com/p/xcmetadataservicestoolkit/wiki/ReleaseNotes)

  * **Update:** 2014-04-16: 1.5.3 released.  See [release notes](http://code.google.com/p/xcmetadataservicestoolkit/wiki/ReleaseNotes)

  * **Update:** 2014-01-28: 1.5.2 released.  See [release notes](http://code.google.com/p/xcmetadataservicestoolkit/wiki/ReleaseNotes)

  * **Issue Tracking and public access** now in [Jira](http://jira.carli.illinois.edu:8080/browse/MST)

### Metadata Services Toolkit ###

The Metadata Services Toolkit consists of a core application (MST) plus a set of plug-in metadata services, each designed to process metadata and produce new records for a specific, targeted use. Currently, we have completed services to normalize MARC metadata, transform MARC metdata into XC(frbrized) records, and transform DC records into XC records.  We are still working on services to aggregate metadata from a variety of sources and provide authority control. An API embedded in the MST, along with a developer's guide, allows third‐party development of additional services that can then be shared with the open source community.

The Metadata Services Toolkit includes a web-based user interface that administrators and catalogers can use to monitor, debug, and configure the data processing steps integral to each of the metadata services. A faceted-browsing user interface allows catalogers to interact with the data at each step of the processing. The MST reveals the output of each service as an OAI-PMH repository, which makes it available for harvesting by other XC software components, as well as other non-XC applications. This enables the MST to be used with non-XC OAI-PMH applications as well as with other XC software.

The following services are currently available for use:
  * MARC Normalization Services
  * Transformation Services (MARC to XC, and DC to XC)

The following services will be included in a future release of the Metadata Services Toolkit:

  * DC Normalization Services
  * Authority Control Services
  * Aggregation

Additional information about the Metadata Services Toolkit and its services can be found in the metadata white paper, "<a href='http://hdl.handle.net/1802/6377'>Supporting the eXtensible Catalog through Metadata Design and Services</a>".

The software code repository for the Metadata Services Toolkit is: http://code.google.com/p/xcmetadataservicestoolkit