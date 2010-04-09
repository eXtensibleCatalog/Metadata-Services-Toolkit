INDEX_FOLDER=${BACKUP_FOLDER}/index
mkdir -p ${INDEX_FOLDER}
cd ${INDEX_FOLDER}

ORIG_PWD=${PWD}

tar cf ./solr.tar ${MST_INSTANCE}/solr
gzip solr.tar

cd ${ORIG_PWD}
