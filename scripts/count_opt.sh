export DB_NAME="perftest"
/usr/local/mysql/bin/mysql -u root --password=Mxdev08 -e "show variables like 'key_buffer_size'"

/usr/local/mysql/bin/mysql -u root --password=Mxdev08 -e "show status like 'key_reads'"
/usr/local/mysql/bin/mysql -u root --password=Mxdev08 -e "show status like 'key_read_requests'"

/usr/local/mysql/bin/mysql -u root --password=Mxdev08 -e "load index into cache ${DB_NAME}.record_updates;"
date; /usr/local/mysql/bin/mysql -u root --password=Mxdev08 -e "select count(*) from ${DB_NAME}.record_updates where date_updated > '2009-10-11 15:33:22' "; date
date; /usr/local/mysql/bin/mysql -u root --password=Mxdev08 -e "select count(distinct record_id) from ${DB_NAME}.record_updates where date_updated > '2009-10-11 15:33:22' "; date

/usr/local/mysql/bin/mysql -u root --password=Mxdev08 -e "show status like 'key_reads'"
/usr/local/mysql/bin/mysql -u root --password=Mxdev08 -e "show status like 'key_read_requests'"
