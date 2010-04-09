SQL_FOLDER=${BACKUP_FOLDER}/sqldump
mkdir -p ${SQL_FOLDER}
${MYSQL_DIR}/mysqldump --database MetadataServicesToolkit -u ${MYSQL_USER} --password=${MYSQL_PASS}  > ${SQL_FOLDER}/mst_dump.sql
