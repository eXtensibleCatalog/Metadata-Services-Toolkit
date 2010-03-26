#echo "scp -r ant_build/webapp/* ${REMOTE_USER}@${REMOTE_BOX_NAME}:${TOMCAT_HOME}/webapps/MetadataServicesToolkit"
scp -r ant_build/webapp/WEB-INF/classes/* ${REMOTE_USER}@${REMOTE_BOX_NAME}:${TOMCAT_HOME}/webapps/MetadataServicesToolkit/WEB-INF/classes
#scp -r ant_build/webapp/WEB-INF/lib/jaxp-api.jar ${REMOTE_USER}@${REMOTE_BOX_NAME}:${TOMCAT_HOME}/webapps/MetadataServicesToolkit
#echo ssh  ${REMOTE_USER}@${REMOTE_BOX_NAME} \"ls -lad ${TOMCAT_HOME}/webapps/MetadataServicesToolkit/hello\"
#ssh  ${REMOTE_USER}@${REMOTE_BOX_NAME} "ls -lad ${TOMCAT_HOME}/webapps/MetadataServicesToolkit\/hello"
#ssh  ${REMOTE_USER}@${REMOTE_BOX_NAME} "ls -lad ${TOMCAT_HOME}/webapps/hello"
