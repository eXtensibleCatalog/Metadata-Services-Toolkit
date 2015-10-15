  * go to tomcat root
```
cd /usr/local/tomcat
```
  * Delete existing install
```
ps -ef | grep "java.*\-Xms${MEMORY}M" | awk '{print $2}' | xargs kill -9; 
rm -fR ./MST-instances* ./webapps/MetadataServicesToolkit*; 
rm -fR ./logs/*
```
  * download mst
```
/usr/sfw/bin/wget http://xcmetadataservicestoolkit.googlecode.com/files/MST-instances-0.3.5.zip;
/usr/sfw/bin/wget http://xcmetadataservicestoolkit.googlecode.com/files/marcnormalization-0.3.5.zip;
/usr/sfw/bin/wget http://xcmetadataservicestoolkit.googlecode.com/files/marctoxctransformation-0.3.5.zip;
unzip MST-instances-0.3.5.zip;
cd ./MST-instances/MetadataServicesToolkit/services/; unzip ../../../marcnormalization-0.3.5.zip; unzip ../../../marctoxctransformation-0.3.5.zip; cd ~-
mv ./MST-instances/MetadataServicesToolkit.war ./webapps;
```
  * Make changes necessary to service.xccfg (U of R specific changes listed below:)
    * change CHANGE\_ME to NRU
    * `Add014Source   1`
    * `InvalidFirstChar014 = 0`
  * Even more now:  See complete list of U of R specific changes @ [here.](https://docs.google.com/spreadsheet/ccc?key=0ArAewjrTTKSUdFhwYWtlMTJpQWJqc2ptdFJtei05Wnc)
```
vi ./MST-instances/MetadataServicesToolkit/services/marcnormalization/META-INF/classes/service.xccfg
```

  * uncomment `OaiRepoAdminEmail`
```
vi ./MST-instances/MetadataServicesToolkit/install.properties
```
  * change db password
```
vi ./MST-instances/MetadataServicesToolkit/install.properties
```
  * pick a new password
```
echo -n 'mst4world' | sha1sum | xxd -p -r | base64
```
  * and put it in the sql script
```
vi MST-instances/MetadataServicesToolkit/sql/create_database_script.sql
```
  * if MST-instances directory not in tomcat/bin (default) then need to point to it.  Add `webapps/MetadataServicesToolkit/WEB-INF/classes/env.properties` - this contains one line pointing to the instance, for example: mst.root.dir=/xc/
  * if on CARLI server may need to run chown -R to tomcat6 and chgrp -R to admin on MST-instances  (same applies to the war file)
  * run install sql
```
mysql --user=root --password=pass < MST-instances/MetadataServicesToolkit/sql/create_database_script.sql
```
  * customize error display for browse records by editing:
    * `MST-instances/MetadataServicesToolkit/services/marctoxctransformation/META-INF/classes/xc/mst/services/custom.properties`
    * `MST-instances/MetadataServicesToolkit/services/marcnormalization/META-INF/classes/xc/mst/services/custom.properties`
  * **make sure you have enough hard drive space**
  * start tomcat
```
export JAVA_OPTS="-Xms2400M -Xmx2400M" -server; ./bin/startup.sh
```
  * make sure your java process is actually using the right amount of memory (some catalina.sh's have been modified)
  * check mysql connection
```
cd /usr/local/tomcat/webapps/MetadataServicesToolkit/WEB-INF/lib;
java -cp ./mst-common-0.3.4.jar:./mysql-connector-java-5.0.8.jar xc.mst.dao.TestConnection com.mysql.jdbc.Driver "jdbc:mysql://localhost:3306" user pass
```
  * setup email configuration using MST UI