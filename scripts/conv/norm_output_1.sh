RECORD_PREFIX='<records xmlns="http://www.openarchives.org/OAI/2.0/"><record><header><identifier>oai:mst.rochester.edu:marcnormalization/@OAI_ID@</identifier><oldFileName>@OLD_FILENAME@</oldFileName></header><metadata>'
RECORD_SUFFIX='</metadata></record></records>'
OAI_ID=0
#for fn in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/orig-186/499*.xml | sed 's/.*\///g')
for fn in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/orig-186/*.xml | sed 's/.*\///g')
do
	f=./mst-service/custom/MARCNormalization/test/input_records/orig-186/$fn
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
	OLD_FILENAME="oai-mst.rochester.edu-MetadataServicesToolkit MARCNormalization 10${file_part}.xml"
	ls -1 "./mst-service/custom/MARCNormalization/test/expected_output_records/orig-186-0.2.9/${OLD_FILENAME}"
	of="./mst-service/custom/MARCNormalization/test/expected_output_records/orig-186-0.2.9/${OLD_FILENAME}"
	ef=./mst-service/custom/MARCNormalization/test/expected_output_records/orig-186/$fn
	echo file_part: $file_part
	echo OLD_FILENAME: $OLD_FILENAME
	echo fn: $fn
	echo f: $f
	echo of: $of
	echo ef: $ef
	echo OAI_ID: $OAI_ID

	let OAI_ID=OAI_ID+1
	RECORD_P=$(echo $RECORD_PREFIX | sed "s/@OAI_ID@/${OAI_ID}/g" | sed "s/@OLD_FILENAME@/${OLD_FILENAME}/g")
	echo ${RECORD_P} > ${ef}.new
	cat "${of}" | grep -v '<?xml' >> ${ef}.new
	#cat "${of}"
	echo $RECORD_SUFFIX >> ${ef}.new
	dos2unix ${ef}.new
	cat ${ef}.new | xmllint --format --encode "UTF-8" - > ${ef}.new.2
	#echo '<?xml version="1.0" encoding="UTF-8"?>' > ${ef}
	sed 's/^  //g' ${ef}.new.2 > ${ef}
	#unix2dos ${ef}
	rm ${ef}.new*
done
