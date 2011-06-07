while [ 1=1 ]
do
	#echo ${REMOTE_MYSQL_PASS}
	mysql -s -P 3346 -u root --password=${REMOTE_MYSQL_PASS} -e "select 1;" > /dev/null
	sleep 20
done
