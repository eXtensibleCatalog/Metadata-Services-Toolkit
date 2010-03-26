ant prepareWebApp

ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "mkdir -p ${REMOTE_BASE_DIR}/scripts/env"
scp scripts/*.sh ${REMOTE_USER}@${REMOTE_BOX_NAME}:${REMOTE_BASE_DIR}/scripts
scp scripts/env/*.sh ${REMOTE_USER}@${REMOTE_BOX_NAME}:${REMOTE_BASE_DIR}/scripts/env

ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "find ${REMOTE_BASE_DIR}/scripts/ | xargs chmod +x"

ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "cd ${REMOTE_BASE_DIR}; scripts/go.sh 170 stop_tomcat"
scripts/push_code.sh
ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "cd ${REMOTE_BASE_DIR}; scripts/go.sh 170 clear_stuff"
ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "cd ${REMOTE_BASE_DIR}; scripts/go.sh 170 start_tomcat"
