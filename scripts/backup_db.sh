sql_folder=${folder}/../sqldump
mkdir -p ${sql_folder}
${MYSQL_DIR}/mysqldump --database MetadataServicesToolkit -u ${MYSQL_USER} --password=${MYSQL_PASS}  > ${sql_folder}/mst_dump.sql
