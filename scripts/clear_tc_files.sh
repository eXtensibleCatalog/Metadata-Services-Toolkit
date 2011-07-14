for file in $(find $TOMCAT_HOME/logs/*)
do
	rm $file
	echo removing $file
done
