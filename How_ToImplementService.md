Is your repository metadata in a format which the MST doesn't currently support?  Or would you like to do some extra processing on your metadata before drupal harvests it for front end searching?  Not a problem.  As advertised, XC's MST is quite extensible.  This document will guide you through the process of creating a development environment which makes it easy for you to write, test, and deploy your very own MST service.

There's 2 ways you can create an MST service:
  * simply implement the [MetadataService interface](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/0.3.x/mst-common/src/java/xc/mst/services/MetadataService.java)
  * build on our [generic implementations](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/0.3.x/mst-service/impl/src/java/xc/mst/services/impl/service/GenericMetadataService.java) of the MetadataService

The second option is what we strongly recommend you start with.  We left the first option available for the small chance that someone might want to optimize processing in a different way than we've already done it.  However, we've already spent considerable time designing and implementing an optimized, high-throughput system, so most likely you'll simply want to harness that.

## What's the minimal steps to write a service? ##
  1. download and install the jdk (we're using 1.6)
  1. download and install [apache ant](http://ant.apache.org/)
  1. download and install [mysql](http://www.mysql.com/downloads/mysql/) (version 5.0 or higher)
    * (optionally install [MySQL Workbench](http://www.mysql.com/downloads/workbench/))
  1. download and unzip the example service [from here](http://xcmetadataservicestoolkit.googlecode.com/files/example-0.3.0-dev-env.zip)<a href='Hidden comment: 
# 2 ways to get our example service
# download it from [http://xcmetadataservicestoolkit.googlecode.com/svn/dist/mst-service-example/example-0.3.0-dev-env.zip here]
# check it out from [http://xcmetadataservicestoolkit.googlecode.com/svn/branches
/0.3.0/mst-service/example/ here] (recommended so that you can easily get our ongoing updates)'></a>
  1. (optional) change the top directory name from example to the name of your service
  1. update the properties file to your liking<br /><br />_custom.properties_
```
  service.name=MyTotallyAwesomeService
  service.version=1.0
  service.classname=edu.timbuktu.mst.service.MyTotallyAwesomeService
  
  DatabaseUrl=jdbc:mysql://localhost:3306
  Database=MetadataServicesToolkit
  DatabaseUsername=root
  DatabasePassword=root
```
  1. create your MetadataService in src/java (get more details [here](http://www.extensiblecatalog.org/doc/MST/api/xc/mst/services/impl/GenericMetadataService.html#process%28xc.mst.bo.record.InputRecord%29))
```
public class MyTotallyAwesomeService extends GenericMetadataService {
    public List<OutputRecord> process(InputRecord r) {
        List<OutputRecord> records = new ArrayList<OutputRecord>();
        OutputRecord out = null;
        if (r.getSuccessors() != null && r.getSuccessors().size() > 0) {
            // This will overwrite the previous successor record
            out = r.getSuccessors().get(0);
        } else {
            // This creates a new output record
            out = getRecordService().createRecord();
        }
        // see here for more details on mode
        // http://www.extensiblecatalog.org/doc/MST/api/xc/mst/bo/record/RecordIfc.html#setMode%28java.lang.String%29
        r.setMode(Record.JDOM_MODE);
	Element metadataEl = r.getOaiXmlEl();
	Element foo = metadataEl.getChild("foo");
	out.setOaiXml("<bar>you've been barred: "+foo.getText()+"</bar>");
	records.add(out);
	return records;
    }
}
```
  1. to test your service see [this page](ServiceFileSystemTesting.md).
<a href='Hidden comment: 
# <a name="options_to_install">Do one of the following (from easiest to hardest)

Unknown end tag for &lt;/a&gt;


# [ServiceFileSystemTesting File input/output testing]
# deploy your service into the MST
# *note: this doesn"t work yet, so don"t try it*
# issue the ant build task to build your service
```
  ant zip
```
# see the MST services guide to install your service
# checkout or download the entire mst source
'></a>