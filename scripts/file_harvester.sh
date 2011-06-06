fileName='initial'
baseUrl='http://128.151.244.135:8080/OAIToolkit/oai-request.do?verb=ListRecords'
extraUrl='&metadataPrefix=marc21&from=2011-05-23T13:51:00Z'

folder='/cygdrive/e/curltest'

rm -fR $folder
mkdir -p $folder

echo "sending request ${baseUrl}${extraUrl}"
curl -s ${baseUrl}${extraUrl} | xmllint --format - > ${folder}/${fileName}
echo "request complete"
resumption=$(grep 'resumptionToken' ${folder}/${fileName} | sed 's/^.*\?>\(.*\)<.*$/\1/g')
while [ -n "$resumption" ]
do
	echo "resumptionToken: $resumption"
	extraUrl="&resumptionToken=${resumption}"
	echo "sending request ${baseUrl}${extraUrl}"
	fileName=$(echo ${resumption} | sed 's/|/_/g') 
	curl -s ${baseUrl}${extraUrl} | xmllint --format - > ${folder}/${fileName}
	echo "request complete"
	resumption=$(grep 'resumptionToken' ${folder}/${fileName} | grep -v request | sed 's/^.*\?>\(.*\)<.*$/\1/g')
	echo "resumptionToken2: $resumption"
done
