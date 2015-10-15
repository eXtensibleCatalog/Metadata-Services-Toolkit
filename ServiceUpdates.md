# Introduction #
This page documents the design and testing decisions made for the feature: "Service Updates."

When a service is initially installed, the MST now goes through each applicable file in the service (see regexpOfFilesToScanForAutoServiceReprocessing below to find out more about what an applicable file is) and saves the latest file time found from this set of service files.  It saves this in the database in Timestamp format with the milliseconds truncated to 0.

On startup of the MST, each installed service is checked for updated files.  The entire set of applicable service files gets checked and the latest file time found among the set of service files gets compared to the existing file time saved in the database.  If a newer service file time is found, the contents of service\_harvests associated with the updated service are deleted.  The service must then reprocess by finding all processing directives where the service is the target, and re-executing these directives.

# Design Details #
**New database column**

A new column has been added to the metadataservicestoolkit.services table of type Timestamp:  _services\_last\_updated_.  On initial service install this gets populated with the latest file time found for the service.   Then on subsequent starts of the MST, the file times are checked in the service's files, and compared to the time in 'services\_last\_updated'.

Note that when a file is found with a timestamp later than that stored in the database, the timestamp from the file is used as the new latest time and the newly found latest timestamp is written to the database.

After updating the database, the job to reprocess the files gets scheduled and run.

**New properties**

Two new properties have been introduced:
  1. _regexpOfFilesToScanForAutoServiceReprocessing_
  1. _isCheckingForUpdatedServiceFiles_
Only files types found in property regexpOfFilesToScanForAutoServiceReprocessing shall be considered when deciding whether a service has been updated.  By default these file types are: `*`\.jar,.`*`\.class,.`*`\.xccfg,.`*`\.properties but this is configurable.  Note the regex syntax used to describe the file types.  As a regular expression is constructed to determine if the file type matches, please use regular expressions if you modify the property regexpOfFilesToScanForAutoServiceReprocessing.

This feature can be disabled with new property:  isCheckingForUpdatedServiceFiles, by default the MST checks for updated service files.

# Testing #

Check that the new column is populated after install of a new service: "select service\_last\_modified from metadataservicestoolkit.services where service\_id=1"

For operations after this initial one I used a system set up through MARCtoXCTransformation start2finishtest.

Update a service's file that is of a type not in the list above (like an html file).  Through checking debug statements in the general log, make sure the service was seen as 'not updated' on restart of the MST through Tomcat stop/start.

Update a service's file that is of a type in the list above (like a xccfg file).  Assure the contents of service\_harvests associated with the updated service are deleted: "select `*` from metadataservicestoolkit.service\_harvests where service\_id=1" Then, after reprocessing, check that the harvest exists again using the same query.

To do:
  * Test multiple processing rules all run for an updated service.
  * Create a more automated unit test.