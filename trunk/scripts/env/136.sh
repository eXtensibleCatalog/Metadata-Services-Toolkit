export TOMCAT_HOME=/usr/local/apache-tomcat-6.0.16
export PATH=/usr/local/jdk1.6.0_04/bin:$PATH
export JAVA_HOME=/usr/local/jdk1.6.0_04
export MYSQL_USER=root
export REMOTE_USER=benjamina
export REMOTE_BOX_NAME=128.151.244.136
export REMOTE_BASE_DIR="/drive2/data/ben"
export MYSQL_DIR=/usr/local/mysql/bin/
export MST_INSTANCE=${REMOTE_BASE_DIR}/MST-instances/MetadataServicesToolkit
export MEMORY=1356

#export provider_name="137 - 175"
#export provider_url="http:\/\/128.151.244.137:8080\/OAIToolkit_0.6.1\/oai-request.do"

export provider_name="132 - 40k"
export provider_url="http:\/\/128.151.244.132:8080\/OAIToolkit_testDataset_40K\/oai-request.do"

#export provider_name="132 - 1M"
#export provider_url="http:\/\/128.151.244.132:8080\/OAIToolkit_ver0.6.1\/oai-request.do"

#norm service
#export service_id=1

#trans service
#export service_id=2

#dbnorm service
export service_id=99

#export harvest_schedule_end_date='date_add(current_timestamp, interval 30 minute)'
#export begin_comment_dont_skip_harvest='\/\*'
#export end_comment_dont_skip_harvest='\*\/'
#export begin_comment_skip_harvest=
#export end_comment_skip_harvest=

export harvest_schedule_end_date="\'2010-03-24 00:00:00\'"
export begin_comment_dont_skip_harvest=
export end_comment_dont_skip_harvest=
export begin_comment_skip_harvest='\/\*'
export end_comment_skip_harvest='\*\/'
