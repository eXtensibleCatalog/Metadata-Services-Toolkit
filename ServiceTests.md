Right now there are 3 major service level tests:
  * **StartToFinishTest.java**
```
ant start2finishtest
```
    * This performs a real harvest to a real url, runs through your service, and harvests your newly created mst repo.  There are hooks at each level to customize this test for your service
  * **MockHarvestTest.java**
```
ant -Dtest=MockHarvest test
```
    * This is nearly identical to the StartToFinishTest except that it performs a mock harvest using a file instead of a url.
  * **ProcessFilesTest.java**
```
ant -Dtest=ProcessFiles test
```
    * (see ServiceFileSystemTesting) for more info.  This test mocks out the entire MST.  It also adds extra info (mainly predecessor records) to your output records that aren't part of oai-pmh.  This helps for testing expected output against actual output.