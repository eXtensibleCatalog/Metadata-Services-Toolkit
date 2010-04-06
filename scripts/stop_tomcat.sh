${TOMCAT_HOME}/bin/shutdown.sh
sleep 5
ps -ef | grep "java.*${MEMORY}M" | awk '{print $2}' | xargs kill -9 
