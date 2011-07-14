scripts/remote_1.sh

ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "cd ${REMOTE_BASE_DIR}; scripts/go.sh ${1} ${3}"
