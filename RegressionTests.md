as we continue to add to the number of tests we have and as we've recently double our MST development staff, we'd do well to have set of tests clearly defined so that:

  * we can verify to ourselves that we didn't break anything with our recent changes
  * we can verify that Ben won't cripple John by introducing bugs

My original hesitation with official regression tests was that many of our tests are dependent on an external oai-toolkit.  That seemed messy to me as that external dependency might change.  After further review, I'm thinking it's not that big of an issue.  Moving forward, we should always have a working oai-toolkit.  Perhaps we have a config param that all tests will use which we can easily change when the source oai-toolkit switches servers.

Anyways, I like to take it slow and not overdesign this just yet.  My current solution would be to simply come up with a list of tests that we should run (perhaps later grouped better with a single ant target).

Check to make sure all of the following targets run w/out errors before checking into a shared source branch (anything but your private branch).
  * package-all
```
ant package-all; message
```
  * MarkDeletedTest
```
cd ./mst-service/custom/MARCNormalization/; ant -Dtest=MarkProviderDeleted test; cd ~-; message
```
  * RecordCountsTest
```
cd ./mst-service/example; ant -Dtest=RecordCounts test; cd ~-; message
```
  * StartToFinish in MARCToXCTransformation
```
cd ./mst-service/custom/MARCNormalization/; ant zip; cd ~-; cd ./mst-service/custom/MARCToXCTransformation/; ant start2finishtest; cd ~-; message
```
  * test transformation set
```
cd ./mst-service/custom/MARCToXCTransformation/; ant -Dtest=MockHarvest -Dtest.folder=orig_186 test
```
  * another transformation test
```
cd ./mst-service/custom/MARCToXCTransformation/; ant -Dtest=MockHarvest -Dtest.folder=fb1017 test;
```
  * DC Transformation Service
```
cd ./mst-service/custom/DCToXCTransformation/; ant -Dtest=MockHarvest -Dtest.folder=urresearch test
```