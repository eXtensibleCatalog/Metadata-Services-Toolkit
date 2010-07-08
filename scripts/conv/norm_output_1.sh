RECORD_PREFIX='<records xmlns="http://www.openarchives.org/OAI/2.0/"><record><header><identifier>oai:mst.rochester.edu:OAI_ID</identifier></header><metadata>'
RECORD_SUFFIX='</metadata></record></records>'
OAI_ID=0
#for fn in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/orig-184/499*.xml | sed 's/.*\///g')
for fn in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/orig-184/*.xml | sed 's/.*\///g')
do
	f=./mst-service/custom/MARCNormalization/test/input_records/orig-184/$fn
	file_part=$OAI_ID
	if [ $OAI_ID -lt 10 ]
	then
		file_part=0${file_part}
	fi
	if [ $OAI_ID -lt 100 ]
	then
		file_part=0${file_part}
	fi
	echo 
	echo 
	ls -1 "./mst-service/custom/MARCNormalization/test/expected_output_records/orig-184-0.2.9/oai-mst.rochester.edu-MetadataServicesToolkit MARCNormalization 10${file_part}.xml"
	of="./mst-service/custom/MARCNormalization/test/expected_output_records/orig-184-0.2.9/oai-mst.rochester.edu-MetadataServicesToolkit MARCNormalization 10${file_part}.xml"
	ef=./mst-service/custom/MARCNormalization/test/expected_output_records/orig-184/$fn
	echo file_part: $file_part
	echo fn: $fn
	echo f: $f
	echo of: $of
	echo ef: $ef
	echo OAI_ID: $OAI_ID

	RECORD_P=$(echo $RECORD_PREFIX | sed "s/OAI_ID/${OAI_ID}/g")
	echo ${RECORD_P} > ${ef}.new
	cat "${of}" | grep -v '<?xml' >> ${ef}.new
	#cat "${of}"
	echo $RECORD_SUFFIX >> ${ef}.new
	dos2unix ${ef}.new
	cat ${ef}.new | xmllint --format --encode "UTF-8" - > ${ef}.new.2
	#echo '<?xml version="1.0" encoding="UTF-8"?>' > ${ef}
	cat ${ef}.new.2 > ${ef}
	rm ${ef}.new*
	let OAI_ID=OAI_ID+1
done
