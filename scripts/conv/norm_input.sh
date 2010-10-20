RECORD_PREFIX='<records xmlns="http://www.openarchives.org/OAI/2.0/"><record><header><identifier>oai:mst.rochester.edu:OAI_ID</identifier></header><metadata>'
RECORD_SUFFIX='</metadata></record></records>'
OAI_ID=0
SAVEIFS=$IFS
IFS=$(echo -en "\n\b")
#for f in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/NormInput/*.xml)
for f in $(ls -1 ./mst-service/custom/MARCToXCTransformation/test/input_records/orig-186/*.xml)
#for f in $(ls -1 ./mst-service/custom/MARCToXCTransformation/test/input_records/orig-186/*10000.xml | sed 's/ /\\ /g')
#for f in $(find ./mst-service/custom/MARCToXCTransformation/test/input_records/orig-186 -printf "'%h/%f'\n" -name "*10000.xml"  | sed 's/ /\\ /g')
do
	#echo "${f}" | sed 's/ /\\ /g'
	echo "${f}"

	# just eyeballed it that every one is just 1
	#grep -c '<marc:record' $f
	let OAI_ID=OAI_ID+1
	echo $OAI_ID
	RECORD_P=$(echo $RECORD_PREFIX | sed "s/OAI_ID/${OAI_ID}/g")
	echo ${RECORD_P} > ${f}.new
	cat "${f}" | grep -v '<?xml' >> "${f}.new"
	echo $RECORD_SUFFIX >> "${f}.new"
	dos2unix "${f}.new"
	cat "${f}.new" | xmllint --format --encode "UTF-8" - > "${f}.new.2"
	#echo '<?xml version="1.0" encoding="UTF-8"?>' > "${f}"
	#cat "${f}.new.2" >> "${f}"
	cat "${f}.new.2" > "${f}"
	rm "${f}.new.2"
	rm "${f}.new"
done
IFS=$SAVEIFS
