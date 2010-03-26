/xc/tomcat/bin/shutdown.sh
sleep 5
ps -ef | grep java.*apache.*ClassLoader | awk '{print $2}' | xargs kill -9 
