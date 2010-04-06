#configure the amount of memory here
cd ${MST_INSTANCE}/../..
export JAVA_OPTS="-server -Xms${MEMORY}M -Xmx${MEMORY}M"
${TOMCAT_HOME}/bin/startup.sh
cd ~-
