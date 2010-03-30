#configure the amount of memory here
cd /xc/tomcat
export JAVA_OPTS="-server -Xms${MEMORY}M -Xmx${MEMORY}M"
./bin/startup.sh
cd ~-
