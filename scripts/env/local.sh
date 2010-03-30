export TOMCAT_HOME=$(cat local.properties | sed 's/^.*=//')
export MYSQL_USER=root
export MYSQL_PASS=root
export MYSQL_DIR=
export MST_INSTANCE=$TOMCAT_HOME/MST-instances/MetadataServicesToolkit
export BASE_DIR="${HOME}"

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
