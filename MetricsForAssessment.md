There are currently 2 tests we will run to determine performance metrics of the drupal toolkit application.  In both of these tests, solr is stopped, the filesystem caches are cleared, and then solr is restarted.  Denise provided to us a voyager query set (one week of search data) which we will use to perform the following tests.  The tests will be run **on the same machine that is running the drupal toolkit** so as to take network overhead out of the picture.

#### notes on the set of keywords ####
  * I have yet to fully analyze this set

### solr-only test ###
  * this test takes drupal completely out of the picture and sends queries to solr directly.  They are, however, the exact queries that drupal sends to solr.  Here's an example (not necessarily the exact query we will use).
```
$ curl -s 'http://localhost:8984/solr/dtmilestone3/select/?q=text%3ARichard%0D%0A&version=2.2&start=0&rows=10&indent=on' | grep 'QTime'
  <int name="QTime">400</int>
```
  * these queries are exactly the ones command line query with bells and whistles
    * http://drupalcode.org/project/xc.git/blob/HEAD:/xc_solr/resources/perf_tests/README

### query through drupal ###
  * This test consists of the one major drupal request which delivers the results set and facets, which obviously includes the solr query time.
    * **note:** There is more than one request and a few ajaxy things going on which make the whole page take a little while to fully render.  However, I think all those other things (syndetics, for example) are fine.  They aren't holding up the results screen from printing.  There is pretty much one drupal request that I think we can focus on.