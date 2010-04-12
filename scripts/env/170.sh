export TOMCAT_HOME=/xc/apache-tomcat-6.0.16
export MYSQL_USER=root
export REMOTE_USER=benjamina
export REMOTE_BOX_NAME=128.151.244.170
export REMOTE_BASE_DIR="/xc/mst"
export BASE_DIR="/xc/mst"
export MYSQL_DIR=/usr/local/mysql/bin/
export MST_INSTANCE=${TOMCAT_HOME}/MST-instances/MetadataServicesToolkit
export MEMORY=2000

#export provider_name="137 - 175"
#export provider_url="http:\/\/128.151.244.137:8080\/OAIToolkit_0.6.1\/oai-request.do"

#export provider_name="132 - 40k"
#export provider_url="http:\/\/128.151.244.132:8080\/OAIToolkit_testDataset_40K\/oai-request.do"

#export provider_name="132 - 1M"
#export provider_url="http:\/\/128.151.244.132:8080\/OAIToolkit_ver0.6.1\/oai-request.do"

export provider_name="137 - 6M"
export provider_url="http:\/\/128.151.244.137:8080\/OAIToolkit\/oai-request.do"

#norm service
#export service_id=1
#export service_id_2=2

#dbnorm service
export service_id=99
export service_id_2=199

export harvest_schedule_end_date='date_add(current_timestamp, interval 30 minute)'
export begin_comment_dont_skip_harvest='\/\*'
export end_comment_dont_skip_harvest='\*\/'
export begin_comment_skip_harvest=
export end_comment_skip_harvest=

#export harvest_schedule_end_date="\'2010-03-24 00:00:00\'"
#export begin_comment_dont_skip_harvest=
#export end_comment_dont_skip_harvest=
#export begin_comment_skip_harvest='\/\*'
#export end_comment_skip_harvest='\*\/'
