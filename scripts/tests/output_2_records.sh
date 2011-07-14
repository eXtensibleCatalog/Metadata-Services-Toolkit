mkdir -p ./scripts/temp
mysql --raw -N -D metadataservicestoolkit -u root --password=root -e "select xml from records_xml where id=1" | xmllint --format - > ./scripts/temp/1.xml
mysql --raw -N -D metadataservicestoolkit -u root --password=root -e "select xml from records_xml where id=176" | xmllint --format - > ./scripts/temp/2.xml
TortoiseMerge scripts/temp/1.xml scripts/temp/2.xml &
