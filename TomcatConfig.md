## server.xml ##

#### UTF-8 ####
You will want to add utf-8 encoding (if it is not already enabled) to your ${TOMCAT\_HOME}/conf/server.xml file - see [this page](http://code.google.com/p/xcmetadataservicestoolkit/source/diff?spec=svn1207&r=1076&format=side&path=/branches/bens_perma_branch/server-conf/server.xml&old_path=/branches/bens_perma_branch/server-conf/server.xml&old=1075).

### Startup options in UNIX ###

> The amount of memory you will need to allocate is dependent on how many records you will be processing and which services you have installed. On the hardware requirements page we make recommendations based on our testing with our data.  That page can be found [here](http://code.google.com/p/xcmetadataservicestoolkit/wiki/HardwareRequirements).

A few ways exist to allocate the necessary memory for Tomcat:

  * set and export the environment variable
```
export CATALINA_OPTS="-server -Xms3000m –Xmx3000m"
```

  * edit the tomcat file catalina.sh and set CATALINA\_OPTS there
```
CATALINA_OPTS="-server -Xms2048m -Xmx2048m"
```

If you encounter the error "java.lang.OutOfMemoryError: PermGen space" you may need to also increase the PermGen space.  Add the following to CATALINA\_OPTS:

```
-XX:MaxPermSize=128m
```

### Startup options in Windows ###

After installing Tomcat, increase its heap size to the amount of memory necessary for your site. This can be done using the Configure Tomcat application.  In the **Start** menu, select **All Programs** -> **Apache Tomcat** -> **Configure Tomcat**.  (There is more information about memory further down the page.)

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/configuretomcat.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/configuretomcat.jpg)

When the application opens, navigate to the **Java** tab and enter the desired amount in the **Initial memory pool** and **Maximum memory pool** fields. The amount of memory you will need to allocate is dependent on how many records you will be processing and which services you have installed. On the hardware requirements page we make recommendations based on our testing with our data.  That page can be found [here](http://code.google.com/p/xcmetadataservicestoolkit/wiki/HardwareRequirements).

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/apachetomcatproperties.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/apachetomcatproperties.jpg)

_Note that Tomcat cannot be running when the Configure Tomcat application is opened._