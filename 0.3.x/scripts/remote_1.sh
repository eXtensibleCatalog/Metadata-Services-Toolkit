ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "mkdir -p ${REMOTE_BASE_DIR}/scripts/env" 
ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "mkdir -p ${REMOTE_BASE_DIR}/scripts/sql"
ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "mkdir -p ${REMOTE_BASE_DIR}/sql"
scp scripts/*.sh ${REMOTE_USER}@${REMOTE_BOX_NAME}:${REMOTE_BASE_DIR}/scripts > /dev/null
scp scripts/env/*.sh ${REMOTE_USER}@${REMOTE_BOX_NAME}:${REMOTE_BASE_DIR}/scripts/env > /dev/null
scp scripts/sql/*.sql ${REMOTE_USER}@${REMOTE_BOX_NAME}:${REMOTE_BASE_DIR}/scripts/sql > /dev/null
scp sql/*.sql ${REMOTE_USER}@${REMOTE_BOX_NAME}:${REMOTE_BASE_DIR}/sql > /dev/null

ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "find ${REMOTE_BASE_DIR}/scripts/ | xargs chmod +x"
