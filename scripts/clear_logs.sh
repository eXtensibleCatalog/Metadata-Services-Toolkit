. ./scripts/env.sh

for file in $(ls -1 $TOMCAT_HOME/MST-instances/MetadataServicesToolkit/logs/*.txt) 
do
	echo "" > $file
	echo clearing $file
done

for file in $(find $TOMCAT_HOME/MST-instances/MetadataServicesToolkit/logs/ -regex '.*\.txt.[0-9]*')
do
	rm $file
	echo removing $file
done

