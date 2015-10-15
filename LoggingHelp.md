The plethora of log4j.config.txt files is somewhat confusing.  This page is an attempt to explain what we have and how it works.  More work could be done to automate and simplify this further.

Files checked into svn:

```
./mst-common/test/log4j.config.txt

./MST-instances/MST-instances/MetadataServicesToolkit/log4j.config.txt
./MST-instances/MST-instances/MetadataServicesToolkit/log4j.config.txt.prod
./MST-instances/MST-instances/MetadataServicesToolkit/log4j.config.txt.test

./mst-service/example/conf/log4j.config.txt
./mst-service/example/conf/log4j.config.txt.prod
./mst-service/example/conf/log4j.config.txt.test

./mst-service/impl/test/log4j.config.txt
```

of these, only these should be editted manually (the others should just be copied over manually):
```
./mst-service/example/conf/log4j.config.txt.prod
./mst-service/example/conf/log4j.config.txt.test
```

when ./mst-service/example/conf/log4j.config.txt.test is modified, it should be manually copied to (at least until it's automated):
```
./mst-common/test/log4j.config.txt
./MST-instances/MST-instances/MetadataServicesToolkit/log4j.config.txt.test
```

when ./mst-service/example/conf/log4j.config.txt.prod is modified, it should be manually copied to
```
./MST-instances/MST-instances/MetadataServicesToolkit/log4j.config.txt
```

If you wish to push your changes of a log4j.config.txt file to custom services (for testing purposes), run one of these commands (the first 2 are equivalent since prod is the default)
```
ant ms.copy-example
ant log.suffix=prod ms.copy-example
ant log.suffix=test ms.copy-example
```