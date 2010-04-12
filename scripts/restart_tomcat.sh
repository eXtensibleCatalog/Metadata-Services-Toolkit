. scripts/env/local.sh
. scripts/env/local.pass.sh
ant -Dbox=${1} prepareWebApp
. scripts/env/${1}.sh
. scripts/env/${1}.pass.sh 2> /dev/null

scripts/remote_1.sh

ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "cd ${REMOTE_BASE_DIR}; scripts/go.sh ${1} stop_tomcat"
scripts/push_code.sh ${1}
ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "cd ${REMOTE_BASE_DIR}; scripts/go.sh ${1} start_tomcat"
