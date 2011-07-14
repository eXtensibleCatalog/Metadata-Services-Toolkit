java -cp $(ls -1 /cygdrive/c/dev/xc/mst/svn/branches/bens_perma_branch/mst-service/example/build/lib/*.jar | gawk '{t=t";"$1} END {gsub(/\/cygdrive\/c/, "c:", t); print t}') org.testng.TestNG -testclass xc.mst.common.test.GenericTest

# /usr/local/tomcat
#$ java -cp './lib/*:/usr/local/tomcat/webapps/MetadataServicesToolkit/WEB-INF/lib/*' -Xms2800M -Xmx2800M org.testng.TestNG -testclass xc.mst.common.test.GenericTest


