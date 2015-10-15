# Adding and Updating Metadata Services #

Only users with “Services” permission can perform the actions described in this section.

A metadata service is a subroutine of the MST which takes a set of metadata records as input and produces a new set of output records based on the service’s unique functionality. The new set of metadata records are created by combining and improving data from records passed into the service. These output records are added to a Solr index in the MST.

Metadata services for normalizing MARCXML data can be downloaded from the [google code website](http://code.google.com/p/xcmetadataservicestoolkit/downloads/list), or from others in the XC community.
Future releases of the MST will also include metadata services for aggregating data from multiple XC schema records and for Authority Control. Metadata services will be released as a .zip file containing all the files required to run them. Unzipping these files into MST-instances/MetadataServicesToolkit will make them available to be added to the MST.

## Adding a Metadata Service ##

Steps to add a new metadata service to the MST:
  1. Download the .zip file for the metadata service from the [google code website](http://code.google.com/p/xcmetadataservicestoolkit/downloads/list) or a third party website.
  1. Unzip the downloaded file to MST-instances/MetadataServicesToolkit.
  1. Log into the MST with a user that has “Services” permission.
  1. Navigate to the **Add Service** page in the **Services** tab.
  1. Select the configuration file that was contained in the downloaded .zip file from the dropdown and click **Add**.

### Installing Multiple Instances of the Same Service ###
For multiple instances of the same service, follow [these](MultipleServiceInstances.md) instructions.


## Updating a Metadata Service ##

The **All Services** page in the **Services** tab shows information on all services added to the MST. This page shows the name of each service as well as its status and the URL of the OAI repository from which its output records can be harvested. The **Service** and **Harvest Out** buttons download log files for the processing done by a metadata service and the OAI repository of a metadata service, respectively.

While a metadata service is running, its name appears in the green **Process Description** box in the top right corner of the MST on every page. If a metadata service is running, clicking the **Pause** button will suspend it until the **Resume** button is clicked. While a metadata service is paused, the MST will not run any other jobs, but pausing a job will free up resources for processes other than the MST running on the same server. Clicking **Abort** will cause the metadata service to stop running and the MST will then begin the next job.

To update a service with a later version, stop the Tomcat Server with the MST in a stable state. Individual files can be replaced as needed in the service’s installation directory in `MST-instances/MetadataServicesToolkit/services/``<your-service>`.  Restart the Tomcat Server. When the MST restarts, it checks each installed service and checks the timestamps of the service’s `*`.jar, `*`.class, `*`.xccfg, and `*`.properties files. If it finds an updated file time, it reprocesses the service’s files.

### Configuring the feature to reprocess service files after update ###
Two properties are used to configure this feature:
```
# At start of MST, check for updated service files, if find them, delete records out of associated service harvest.
isCheckingForUpdatedServiceFiles=true

# if isCheckingForUpdatedServiceFiles=true -> scan these file types for timestamp later than that stored by MST.
regexpOfFilesToScanForAutoServiceReprocessing =.*\.jar,.*\.class,.*\.xccfg,.*\.properties
```

You can override these in the install.properties file to turn the feature off or change which file types are scanned for update.

More information on this feature [here](ServiceUpdates.md).