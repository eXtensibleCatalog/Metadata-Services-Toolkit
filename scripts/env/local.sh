export TOMCAT_HOME=$(cat ant_properties/local.properties | grep tomcat.dir 2> /dev/null | sed 's/^.*=//' 2> /dev/null)
export MYSQL_USER=root
export MYSQL_DIR=
export MST_INSTANCE=$TOMCAT_HOME/MST-instances/MetadataServicesToolkit
export BASE_DIR="${HOME}"
export JAVA_HOME="C:\dev\java\jdk1.6.0_18"

export provider_name="137 - 175"
export provider_url="http:\/\/128.151.244.137:8080\/OAIToolkit_0.6.1\/oai-request.do"

#export provider_name="132 - 40k"
#export provider_url="http:\/\/128.151.244.132:8080\/OAIToolkit_testDataset_40K\/oai-request.do"

#export provider_name="132 - 1M"
#export provider_url="http:\/\/128.151.244.132:8080\/OAIToolkit_ver0.6.1\/oai-request.do"

#norm service
#export service_id=1

#trans service
#export service_id=2

#dbnorm service
export service_id=99

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
