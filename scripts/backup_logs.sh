export LOG_FOLDER="${BACKUP_FOLDER}/logs"
mkdir -p ${LOG_FOLDER}

for file in $(ls -1 $MST_INSTANCE/logs/*.txt*) 
do
	cp ${file} ${LOG_FOLDER}
done

for file in $(find $TOMCAT_HOME/logs/*)
do
	cp ${file} ${LOG_FOLDER}
done

