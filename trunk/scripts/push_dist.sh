echo ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "mkdir -p ${REMOTE_BASE_DIR}/dist"
ssh ${REMOTE_USER}@${REMOTE_BOX_NAME} "mkdir -p ${REMOTE_BASE_DIR}/dist"
scp dist/* ${REMOTE_USER}@${REMOTE_BOX_NAME}:${REMOTE_BASE_DIR}/dist
scp MetadataServicesToolkit.war ${REMOTE_USER}@${REMOTE_BOX_NAME}:${REMOTE_BASE_DIR}/dist
