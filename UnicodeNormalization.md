### Problem #1: Canonical and Compatibility Equivalence ###

**Problem**: unicode allows multiple ways to represent equivalent characters.
  * http://www.unicode.org/reports/tr15/tr15-23.html
  * http://en.wikipedia.org/wiki/Unicode_equivalence

**Example**: The GREEK UPSILON WITH ACUTE AND HOOK SYMBOL (http://unicode.org/faq/normalization.html#6)
| -- | NFC|NFD|NFKC|NFKD|
|:---|:---|:--|:---|:---|
|Unicode (UTF-32)|03D3|03D2 0301|038E|03A5 0301|
|Unicode (UTF-8)|cf93|cf92 cc81|ce8e|cea5 cc81|

**Solution**: Normalize everything before it goes into the index.  Normalize all queries sent to the index.  I'd propose (w/out putting too much thought into it) we use NFC.  It should be configurable anyway.
<br />
<br />
### Problem #2: Searches w/ diacritics should display matches w/out diacritics (and vice-versa) ###
**Problem**: Users will expect to have their search results contain a string of characters that have neither "canonical equivalence" nor "compatibility equivalence".

**Solution**: Peter can fill in the blanks (not necessarily in this page) - it sounds like solr 3 handles this.  I just wanted to point out that it's a different issue than the one above

<br />
<br />
### MST output for Drupal testing ###
I believe Peter has everything he needs to test #2.  To test #1, I modified the original diacritics test found ([here](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/bens_perma_branch/mst-service/custom/MARCToXCTransformation/test/mock_harvest_expected_output/diacritics/1.oai-harvest.xml)).  I simply changed the manifestation titles to be ϓellow (with the Greek character encoded 4 different ways) ([see here](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/bens_perma_branch/mst-service/custom/MARCToXCTransformation/test/mock_harvest_expected_output/diacritics/2.oai-harvest.xml)).  I then deleted all the other records, to keep it small.  I'm not sure if that's the easiest way for Peter to test #1, but it's a start.  Peter, let me know if you would like it a different way.


```
$ curl -s 'http://xcmetadataservicestoolkit.googlecode.com/svn/branches/bens_perma_branch/mst-service/custom/MARCToXCTransformation/test/mock_harvest_expected_output/diacritics/2.oai-harvest.xml' | grep -o '....ellow' ./mst-service/custom/MARCToXCTransformation/test/mock_harvest_expected_output/diacritics/2.oai-harvest.xml  | xxd
```

<br />
<br />
### commands and tips ###
  * I wrote some helper tools a few years back
    * downloadable here
      * http://code.google.com/p/andersonbd1/source/browse/#svn%2Ftrunk%2Fhome%2Fscripts%253Fstate%253Dclosed
      * http://code.google.com/p/andersonbd1/source/browse/#svn%2Ftrunk%2Fjs%253Fstate%253Dclosed
    * convert between utf-16 and utf-8(unencoded unicode)
```
$ ./scripts/convert_char_enc.sh utf16 03 D3
char            utf8            utf16           utf32
----            ----            -----           -----
ϓ               cf93            3d3             3d3
```
    * convert between utf-8 and utf-32(unencoded unicode)
```
$ ./scripts/convert_char_enc.sh utf8 cf 93
char            utf8            utf16           utf32
----            ----            -----           -----
ϓ               cf93            3d3             3d3
```
  * see the difference of the Greek character above
    * xxd comes with vim
```
$ curl -s 'http://www.extensiblecatalog.org/doc/MST/4wiki/yellow' | xxd
```
  * xxd works in reverse as well
```
$ echo 'cea5cc81' | xxd -r -p
```