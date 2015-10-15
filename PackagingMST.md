# Packaging the MST for a release #
  * run start2finishtests
  * [run regression tests](http://code.google.com/p/xcmetadataservicestoolkit/wiki/RegressionTests)
  * increment version # in props files
    * I wrote a script that automatically updates the below files
```
./scripts/update_version.sh 0.3.5.1
```
      * Warning:  `DefaultRepository.installOrUpdateIfNecessary` contains a check of version number.  Update this code if necessary when you update the version number.
    * ./MST-instances/build.properties
    * ./mst-common/build.properties
    * ./mst-platform/build.properties
    * ./mst-service/impl/build.properties
    * ./mst-service/example/build.properties
    * ./mst-service/example/custom.properties
    * ./mst-service/custom/`*`/build.properties (don't change this manually - it will be copied from ./mst-service/example/build.properties)
    * ./mst-service/custom/`*`/custom.properties
  * mst-service/custom/MARCNormalization/src/service.xccfg - make sure OrganizationCode is set to CHANGE\_ME
  * producing the files (this packages everything)
```
ant package-all
```
  * svn
    * merge to trunk
  * tag (if using trunk model:)
```
svn cp https://xcmetadataservicestoolkit.googlecode.com/svn/trunk https://xcmetadataservicestoolkit.googlecode.com/svn/tags/1.0.0 -m "creating tag"
```
    * old way - tag 0.3
```
svn cp https://xcmetadataservicestoolkit.googlecode.com/svn/branches/0.3.x https://xcmetadataservicestoolkit.googlecode.com/svn/tags/0.3.5 -m "creating tag"
```
  * ivy
    * publish ivy remotely  (data stored locally, i.e. C:\Users\JohnB\.ivy2) (to ftp.extensiblecatalog.org, use Ben's acct., goto Ivy dir)
    * make sure ftp is binary (otherwise it translates CRLF to LF and breaks the sha1 and md5 checksums).
  * make sure example service instructions still work
    * be sure to delete local ivy caches (local and cache)
    * follow the instructions on [this page](HowToImplementService.md) - do all the instructions still make sense?
  * Deploying to google code
    * upload new files (example as dev-env)
    * delete (deprecate) old files
      * click delete in google code and then it'll ask you "delete forever" or "depecrate"
    * change NotReleased issues to Fixed
    * update ReleaseNotes page (using previous releases as example)
    * update HowToImplementService
      * from link for "download and unzip the example service from here"
    * update main page
  * update the demo site with the latest release:  128.151.244.170
    * when you update the MST demo, that you alert Peter so that he can ensure that the Drupal install script continues to work.

  * send an email to the list General@xcproject.org