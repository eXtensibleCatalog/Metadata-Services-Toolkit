echo pass: ${REMOTE_MYSQL_PASS}
echo port: ${REMOTE_MYSQL_PORT}
while [ 1=1 ]
do
	mysql -s -P ${REMOTE_MYSQL_PORT} -u root --password=${REMOTE_MYSQL_PASS} -e "select 1;" > /dev/null
	sleep 20
done
