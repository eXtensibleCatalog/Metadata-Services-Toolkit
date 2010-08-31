export TOMCAT_HOME=/xc/apache-tomcat-6.0.16
export MYSQL_USER=root
export REMOTE_USER=benjamina
export REMOTE_BOX_NAME=128.151.244.170
export REMOTE_BASE_DIR="~/0.3.0"
export BASE_DIR="/xc/mst"
export MYSQL_DIR=/usr/local/mysql/bin/
export MST_INSTANCE=${HOME}/mst/svn/branches/bens_perma_branch/mst-service/custom/MARCNormalization/build/MST-instances/MetadataServicesToolkit
export MEMORY=3000

#export provider_name="137 - 175"
#export provider_url="http:\/\/128.151.244.137:8080\/OAIToolkit_0.6.1\/oai-request.do"

#export provider_name="132 - 40k"
#export provider_url="http:\/\/128.151.244.132:8080\/OAIToolkit_testDataset_40K\/oai-request.do"

export provider_name="132 - 1M"
export provider_url="http:\/\/128.151.244.132:8080\/OAIToolkit_ver0.6.1\/oai-request.do"

#export provider_name="137 - 6M"
#export provider_url="http:\/\/128.151.244.137:8080\/OAIToolkit\/oai-request.do"

#norm service
#export service_id=1
#export service_id_2=2

#dbnorm service
export service_id=99
export service_id_2=199

export begin_comment='\/\*'
export end_comment='\*\/'
export begin_comment_run_harvest=${begin_comment}
export end_comment_run_harvest=${end_comment}
export begin_comment_run_norm=${begin_comment}
export end_comment_run_norm=${end_comment}
export begin_comment_run_trans=
export end_comment_run_trans=
