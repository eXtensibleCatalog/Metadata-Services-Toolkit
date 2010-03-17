export TOMCAT_HOME=$(cat local.properties | sed 's/^.*=//')
echo $TOMCAT_HOME
