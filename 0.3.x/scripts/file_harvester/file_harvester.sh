fileName='initial.xml'
baseUrl='http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords'
extraUrl='&metadataPrefix=marc21&from=2011-06-23T14:45:00Z'

folder='/xc/file_harvest/137_delete_2'

rm -fR $folder
mkdir -p $folder

date
echo "sending request ${baseUrl}${extraUrl}"
curl -s ${baseUrl}${extraUrl} | xmllint --format - > ${folder}/${fileName}
echo "request complete"
resumption=$(grep 'resumptionToken' ${folder}/${fileName} | sed 's/^.*\?>\(.*\)<.*$/\1/g')
while [ -n "$resumption" ]
do
    echo "resumptionToken: $resumption"
    extraUrl="&resumptionToken=${resumption}"
    echo "sending request ${baseUrl}${extraUrl}"
    fileName=$(echo ${resumption} | sed 's/|/_/g').xml
    curl -s ${baseUrl}${extraUrl} | xmllint --format - > ${folder}/${fileName}
    echo "request complete $(date)"
    resumption=$(grep 'resumptionToken' ${folder}/${fileName} | grep -v request | sed 's/^.*\?>\(.*\)<.*$/\1/g')
    echo "resumptionToken2: $resumption"
done
date
