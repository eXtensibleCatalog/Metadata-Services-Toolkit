  * **1.5.5** - 2015-07-09
    * Fixed a major MARCAggregationService bug (malformed record would get created if it contained empty or missing 008 control field)
  * **1.5.4** - 2014-10-22
    * Many MARCAggregationService performance enhancements
  * **1.5.2** - 2014-04-16
    * NullPointerException was occuring on nightly updates -- fixed.
  * **1.5.2** - 2014-01-28
    * Lots of MARCAggregationService(MAS) bug fixes.
    * MAS - Better "bad" data handling
    * MAS - More efficient at handling large data repositories
    * The platform now allows a service to process records in two passes, which MAS now does since it speeds up processing considerably
  * **1.5.1** - 2013-03-03
  * **Issues fixed**
    * Transformation Service was utilizing too much memory (RAM). This update should alleviate a lot of performance issues in version 1.5.0.
  * **1.5.0** - 2013-01-30
  * **Issues fixed**
    * Probably the most important change made in the version is the inclusion of the MARC Aggregation Service (MAS)
    * Another important change made in this version was the added MAS support to Transformation Service. Previously, Transformation was not equipped to handle deduplicated source records (and the complex relationships inherent in such an environment, e.g., links to holdings)
    * Normalization Service has also added MAS support.
    * Here is the [MARC Norm and Transform list](http://extensiblecatalog.lib.rochester.edu:8080/secure/IssueNavigator.jspa?reset=true&jqlQuery=project+%3D+MST+AND+fixVersion+in+%2810023%2C+10048%29+AND+status+%3D+Resolved+ORDER+BY+priority+DESC) of issues addressed during this release.
    * Here is the [MAS list](http://extensiblecatalog.lib.rochester.edu:8080/browse/MST/fixforversion/10023) of issues addressed during this release.

  * **Known issues**
    * see [Jira MST list](http://extensiblecatalog.lib.rochester.edu:8080/browse/MST) for the issues that are in work queue.  Be sure to at least look at the High-Priority issues so you know what doesn't work.
    * if you find other issues, please [add them](http://extensiblecatalog.lib.rochester.edu:8080/browse/MST) using the 'create issue' button.  Note that soon we shall have a process for our community to directly access Jira for adding and monitoring issues.
  * **Files for Download**
    * [MST Platform](http://xcmetadataservicestoolkit.googlecode.com/files/MST-instances-1.5.0.zip)
    * [MARC Normalization Service](http://xcmetadataservicestoolkit.googlecode.com/files/marcnormalization-1.5.0.zip)
    * [MARC Aggregation Service](http://xcmetadataservicestoolkit.googlecode.com/files/marcaggregation-1.5.0.zip)
    * [MARC-XC Transformation Service](http://xcmetadataservicestoolkit.googlecode.com/files/marctoxctransformation-1.5.0.zip)
    * [DC-XC Transformation Service](http://xcmetadataservicestoolkit.googlecode.com/files/dctoxctransformation-1.5.0.zip)


  * **1.4.0** - 2012-06-05
  * **Issues fixed**
    * We found that the Solr optimization process added for 1.1.1 was rather expensive to run regularly.  So this has been broken out into a separate script to be run by an administrator.  More details can be found on [our wiki](http://code.google.com/p/xcmetadataservicestoolkit/wiki/OptimizeSolrIndex).
    * We are using Jira now to track issues.  Here is the [list](http://extensiblecatalog.lib.rochester.edu:8080/secure/IssueNavigator.jspa?reset=true&jqlQuery=project+%3D+MST+AND+fixVersion+%3D+%22Release+1.4+-+Misc%22+AND+status+%3D+Closed+ORDER+BY+priority+DESC) of issues addressed during this release.

  * **Known issues**
    * see [this list](http://extensiblecatalog.lib.rochester.edu:8080/secure/IssueNavigator.jspa?reset=true&jqlQuery=fixVersion+%3D+%22Release+1.5+-+Norm+and+Transform+Changes%22+AND+project+%3D+MST) for the issues we are currently working on for 1.5.0.  Be sure to at least look at the High-Priority issues so you know what doesn't work.
    * also see [this page](http://code.google.com/p/xcmetadataservicestoolkit/wiki/CodeSprints) for the general statement of work for the code sprint in progress.  Also see a more complete list of future MST work [here.](http://extensiblecatalog.lib.rochester.edu:8080/browse/MST)
    * if you find other issues, please [add them](http://extensiblecatalog.lib.rochester.edu:8080/browse/MST) using the 'create issue' button.  Note that soon we shall have a process for our community to directly access Jira for adding and monitoring issues.
  * **Files for Download**
    * [MST Platform](http://xcmetadataservicestoolkit.googlecode.com/files/MST-instances-1.4.0.zip)
    * [MARC Normalization Service](http://xcmetadataservicestoolkit.googlecode.com/files/marcnormalization-1.4.0.zip)
    * [MARC-XC Transformation Service](http://xcmetadataservicestoolkit.googlecode.com/files/marctoxctransformation-1.4.0.zip)
    * [DC-XC Transformation Service](http://xcmetadataservicestoolkit.googlecode.com/files/dctoxctransformation-1.4.0.zip)


  * **1.3.0** - 2012-03-05
  * **Issues fixed**
    * Add Normalization Service mapping step to accommodate 008 for microform (977 field) (008FormOfItem step)
    * Fix Normalization Service so `006 Fiction/NonFiction` treated the same as `008 (FictionOrNonfiction step)`
    * Fix Normalization Service so 006 Audience treated the same as 008 (006Audience step)
    * Transformation Service change:  make lsch and mesh attributes lower case
    * Fix Transformation Service so reactivating deleted records should now work correctly
    * Add Normalization Service Step to clean up 010 field (LCCNCleanup step)
  * **Updated Normalization Service Documentation**
    * [Configuring the Normalization Service](http://code.google.com/p/xcmetadataservicestoolkit/wiki/NormConfiguration)
    * [Description of MARCXML Bibliographic Normalization Steps](http://code.google.com/p/xcmetadataservicestoolkit/wiki/BibrecordSteps)
    * [Description of MARCXML Holdings Normalization Steps](http://code.google.com/p/xcmetadataservicestoolkit/wiki/HoldrecordSteps)
    * [Normalization Recommended Default Settings](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdFhwYWtlMTJpQWJqc2ptdFJtei05Wnc#gid=0)

  * **Known issues**
    * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=2&q=label%3A1.x.x) for the issues we are currently working on for 1.4.0.  Be sure to at least look at the High-Priority issues so you know what doesn't work.
    * also see [this page](http://code.google.com/p/xcmetadataservicestoolkit/wiki/CodeSprints) for the general statement of work for the code sprint in progress.
    * if you find other issues, please [add them](http://code.google.com/p/xcmetadataservicestoolkit/issues/entry) to our list

  * **1.2.1** - 2012-01-18
  * **Issues fixed**
    * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=1&q=id:330+OR+id:139)
  * **1.2.0** - 2011-11-23
    * **Improvements**
      * Added a new Normalization step. This step looks for 014 fields that have 1st indicator set as “1”, and in which the first character in the field itself is the configured value, e.g. “0”, and deletes these fields.    See: [Normalization documentation](http://code.google.com/p/xcmetadataservicestoolkit/wiki/OffSteps)
    * **Issues fixed**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=1&q=id:316+OR+id:321+OR+id:322+OR+id:323+OR+id:324+OR+id:325+OR+id:326)
    * **Known issues**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=2&q=label%3A1.x.x) for the issues we are currently working on for 1.x.x.  Be sure to at least look at the High-Priority issues so you know what doesn't work.
      * if you find other issues, please [add them](http://code.google.com/p/xcmetadataservicestoolkit/issues/entry) to our list
  * **1.1.1** - 2011-10-24
    * **Improvements**
      * Improved searching speed from 'Browse Records' by optimizing the Solr index.
      * Added 'rdarole:author' as an index field for the 'Browse Records' screen.  See documentation on this feature [here](http://code.google.com/p/xcmetadataservicestoolkit/wiki/RecordsBrowsing).

  * **1.1.0** - 2011-10-07
    * **Improvements**
      * Updated Solr and Lucene to 3.3.0.
      * Improved searching capability within the 'Browse Records' screen.  See documentation on this feature [here](http://code.google.com/p/xcmetadataservicestoolkit/wiki/RecordsBrowsing).


  * **1.0.0 beta** - 2011-08-31
    * **Issues fixed**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=1&q=id:309+OR+id:311+OR+id:314+OR+id:315)
    * **Improvements**
      * Add new configurable MARC Normalization step add014source.  For more information see the [user information.](http://code.google.com/p/xcmetadataservicestoolkit/wiki/HoldrecordSteps)
      * Add to MARC Transformation a check for an 014 $b, an additional test is now made to decide whether to ignore the 014 field based on a comparison to the 003 code.
      * Added the General MST log and Record Count Rules and Results log to be retrievable from the General Logs tab of the MST UI.
      * Verifying Record Counts - Added code to aid with verifying record counts out of the MST and services and compares these to given rules. See [this.](http://code.google.com/p/xcmetadataservicestoolkit/wiki/verifyRecordCounts)


  * **0.3.5** - 2011-07-01
    * **Issues fixed**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=1&q=id:205+OR+id:273+OR+id:290+OR+id:291+OR+id:300+OR+id:301+OR+id:308)
    * **Other fixes**
      * Fixed email capability.
    * **Improvements**
      * Extensive work on record counts.  Verifying that the number of records in the oai-toolkit match the MST.  We've done extensive testing with updates and deletes as well.  We're mostly satisfied with our results thus far, but are holding out a 1.0 release until we do a little more verification.
      * Updating a Service
        * On startup of the MST, each installed service is checked for updated files. If any are found, the service reprocesses all records.  This is helpful for configuration changes or code changes.
      * Repository deletion - When the 'Delete' button is pressed, instead of deleting the repository, all the records are marked deleted in the repository.  These deletions are then processed through the services also.
      * Services may not be deleted - the button to do so has been removed.
      * Processing Rules are immutable - Once a rule has been created, it can not be deleted unless the records it refers to (i.e. predecessors and successors) have been deleted or do not yet exist.  Also, one a rule has been created, the settings can be viewed but not changed.
      * Repository's are now immutable.  Once created a repository's settings can be viewed but not changed.
    * **Known issues**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=2&q=label%3A0.3.x) for the issues we are currently working on for 0.3.x.  Be sure to at least look at the High-Priority issues so you know what doesn't work.
      * if you find other issues, please [add them](http://code.google.com/p/xcmetadataservicestoolkit/issues/entry) to our list

  * **0.3.4** - 2011-04-01
    * **note**: The metadataprefix used by the xc to denote the [marc21 xml schema](http://www.loc.gov/standards/marcxml/) format had previously been 'marcxml'.  In a prior release the xc-oai-toolkit switched to using 'marc21' as the prefix name (per [oai's recommendation](http://www.openarchives.org/OAI/2.0/guidelines-marcxml.htm)).  This release of the MST follows that same change and removed the 'marcxml' metadataprefix.
    * **Issues fixed**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=1&q=id:242+OR+id:270+OR+id:276+OR+id:299+OR+id:301+OR+id:302+OR+id:303+OR+id:304)
    * **Known issues**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=2&q=label%3A0.3.x) for the issues we are currently working on for 0.3.x.  Be sure to at least look at the High-Priority issues so you know what doesn't work.
      * if you find other issues, please [add them](http://code.google.com/p/xcmetadataservicestoolkit/issues/entry) to our list
  * **0.3.3** - 2011-02-21
    * **Issues fixed**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=1&q=id:273+OR+id:292+OR+id:294)
    * **Installation Documentation**
      * http://code.google.com/p/xcmetadataservicestoolkit/wiki/InstallationDocumentation  (same as 0.3.0)
    * **Known issues**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=2&q=label%3A0.3.x) for the issues we are currently working on for 0.3.x.  Be sure to at least look at the High-Priority issues so you know what doesn't work.
      * if you find other issues, please [add them](http://code.google.com/p/xcmetadataservicestoolkit/issues/entry) to our list
  * **0.3.2**
    * **Issues fixed**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=1&q=id:287+OR+id:284)
    * **Installation Documentation**
      * http://code.google.com/p/xcmetadataservicestoolkit/wiki/InstallationDocumentation  (same as 0.3.0)
    * **Known issues**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=2&q=label%3A0.3.x) for the issues we are currently working on for 0.3.x.  Be sure to at least look at the High-Priority issues so you know what doesn't work.
      * if you find other issues, please [add them](http://code.google.com/p/xcmetadataservicestoolkit/issues/entry) to our list
  * **0.3.1**
    * **Issues fixed**
      * see [this list](http://code.google.com/p/xcmetadataservicestoolkit/issues/list?can=1&q=id:281+OR+id:282+OR+id:285+OR+id:286+OR+id:288+OR+id:289)
    * **Installation Documentation**
      * http://code.google.com/p/xcmetadataservicestoolkit/wiki/InstallationDocumentation  (same as 0.3.0)
  * **0.3.0**
    * **Improvements**
      * **Easier to Implement a Service**
        * The MST is a platform which relies on particular services to do the actual work of transforming records.  The XC team owns and manages a few core services, but the platform is open and extensible so that anyone can write a service to meet their specific needs.  Writing your own MST service has been made much easier in the 0.3.x release.  Simply download our example development environment, tweak it to your likings, write test scenarios until you're happy with the output, and package up your service for deployment into any MST.
        * more info here: http://code.google.com/p/xcmetadataservicestoolkit/wiki/HowToImplementService
      * **Service Testing Infrastructure**
        * Writing services is all about outputting xml records based on particular xml input records.  This is what a running MST system does.  Service implementers, though, will want to have a quick and easy way to automate their input and output.  Included in the 0.3.x release is an advanced testing infrastructure to quickly mock out input and output records.
        * more info here: http://code.google.com/p/xcmetadataservicestoolkit/wiki/ServiceFileSystemTesting
      * **Performance Increases Across the Board**
        * Our goal for the MST has been to process 1M MARC records/hr and have little to no degradation as the MST processed several million records. This goal was a main focus for the 0.3.x work.  Our first task was to figure out how well the 0.2.x system was performing.  The first service in our chain of services, the marc-normalization service, served as our initial metric. The normalization service was processing records at a speed of 125k/hr and before processing 2M records it essentially crawled to a halt.  As of the 0.3.x release this service is now processing records at a speed of 1.2M records/hr with no degradation on a set of 6M records on a less than optimal server (1.5GHz cpu).
        * how we did it
          * used mysql (instead of solr) for our relational data
          * switched to sax processing instead of dom/xpath
        * more info here: http://code.google.com/p/xcmetadataservicestoolkit/wiki/RecordBreakdown
      * **DC Transformation Service**
        * This is a new service that was contributed by [Kyushu University](http://www.kyushu-u.ac.jp/english/) that we've incorporated into our core services.
    * **Installation Documentation**
      * http://code.google.com/p/xcmetadataservicestoolkit/wiki/InstallationDocumentation