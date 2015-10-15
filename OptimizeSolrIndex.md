# Optimizing the SOLR Index #

The MST uses [SOLR](http://lucene.apache.org/solr/) to index and search MST records.  It is highly recommended that MST administrators optimize this index regularly in order for MST to perform well.  At the very minimum, it should be done after you have completed your first "large" harvest (since this will most likely entail harvesting a complete repository).  It is difficult to say exactly what is the best recommendation for how often you should do this, since it depends on the size of your repository and how frequent and how large your regular harvests will be.  Perhaps, you may wish to do this once a week, or once a month.  One important thing to note regarding optimizing the SOLR index is that it must be done when MST is not currently in the process of harvesting (since only one process may modify the SOLR index at a time).  MST provides a script to perform this optimization:

1st check that the script has execute permission, if not, perform the chmod step too.

```
cd [tomcat]/bin/MST-instances/MetadataServicesToolkit
chmod +x optimize_solr_index.sh
sh ./optimize_solr_index.sh
```