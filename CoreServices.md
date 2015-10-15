### What are Core Services? ###

We're using the term "core" to identify which services are being managed by the XC team.  The code for these services is held w/in [XC's code repository](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/#svn/trunk/mst-service/custom).
The current set of core services are:
  1. marc normalization
  1. marc-to-xc transformation
  1. dc-to-xc transformation
  1. aggregation

These services are at different stages of completeness, but we are working hard to finish them all.

If you would like to contribute to one of these services or would like to contribute a new service, please contact Randy Cook, XCO Director of Community Development <a href='mailto:rcook@library.rochester.edu'>rcook@library.rochester.edu</a>

### Instructions for Working on Core Services ###
  1. Do steps 1-3 from the [Quick and Dirty Tutorial](HowToImplementService.md)
  1. Checkout the entire MST code repo for the branch you wish to work on.  This would currently be the [trunk](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/#svn/trunk).
  1. From the top level directory (trunk), issue the following commands:
    * This will grab all dependencies and build all the sub-projects.
```
ant package-all
```
  1. you are now ready to use your service.  cd to your service directory (mst-service/custom/_specific\_core\_service_) and issue the test commands (see [this page](ServiceFileSystemTesting.md)}.

### Notes for XC developer creating a new core service ###
  1. create example zip
```
$ cd ./mst-service/example/; ant zip-dev-env; cd ~-
```
  1. unzip mst-service/example/build/example-1.2.0-dev-env.zip in mst-service/custom  (or later if available)
  1. rename example directory to new service name
  1. delete files
    * those which are automatically copied according to [mst-service/impl/build.xml->ms.copy-example](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/mst-service/impl/build.xml#162)
    * new\_service/test/java/xc/mst/services/example/`*` except for MockHarvestTest and StartToFinishTest
      * change the folder and package for these
      * note: can you find a better base for these?
  1. add a line to build.xml->copy-to-custom-services ([here](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/build.xml#39) and [here](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/trunk/build.xml#70))
  1. rename ExampleMetadataService.java and move to a new package
  1. change custom.properties to use this new class
  1. create dev.properties
  1. svn ci
  1. ant ms.copy-example
  1. add ignores for the previously deleted files
  1. svn ci
  1. cd ./mst-service/custom/new\_folder/; ant -Dtest=ProcessFile test; cd ~-;