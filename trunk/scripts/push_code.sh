#echo "scp -r ant_build/webapp/* ${REMOTE_USER}@${REMOTE_BOX_NAME}:${TOMCAT_HOME}/webapps/MetadataServicesToolkit"
scp -r ant_build/webapp/WEB-INF/classes/* ${REMOTE_USER}@${REMOTE_BOX_NAME}:${TOMCAT_HOME}/webapps/MetadataServicesToolkit/WEB-INF/classes
scp ant_build/webapp/WEB-INF/*.xml ${REMOTE_USER}@${REMOTE_BOX_NAME}:${TOMCAT_HOME}/webapps/MetadataServicesToolkit/WEB-INF
scp ant_properties/${1}.properties ${REMOTE_USER}@${REMOTE_BOX_NAME}:${TOMCAT_HOME}/webapps/MetadataServicesToolkit/WEB-INF/classes/env.properties
#scp -r ant_build/webapp/WEB-INF/lib/jaxp-api.jar ${REMOTE_USER}@${REMOTE_BOX_NAME}:${TOMCAT_HOME}/webapps/MetadataServicesToolkit
#echo ssh  ${REMOTE_USER}@${REMOTE_BOX_NAME} \"ls -lad ${TOMCAT_HOME}/webapps/MetadataServicesToolkit/hello\"
#ssh  ${REMOTE_USER}@${REMOTE_BOX_NAME} "ls -lad ${TOMCAT_HOME}/webapps/MetadataServicesToolkit\/hello"
#ssh  ${REMOTE_USER}@${REMOTE_BOX_NAME} "ls -lad ${TOMCAT_HOME}/webapps/hello"
