#cd mst-service/custom/MARCNormalization
cd mst-service/custom/MARCToXCTransformation
old_dir="test/input_records/orig-186"
new_dir="test/mock_harvest_input/orig_186"
pwd
rm -fR $new_dir 2> /dev/null
mkdir $new_dir
echo '<?xml version="1.0" encoding="UTF-8"?><OAI-PMH xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd"><responseDate>2011-03-30T15:15:54Z</responseDate><request verb="ListRecords" metadataPrefix="marc21">http://128.151.244.137:8080/OAIToolkit_demo_175/oai-request.do</request><ListRecords>' > $new_dir/001.xml

SAVEIFS=$IFS
IFS=$(echo -en "\n\b")

for f in $(ls $old_dir/*)
do
  line_count=$(cat $f | wc -l)
  let "tail = $line_count - 2"
  let "head = $tail - 1"
  #tail=$(echo "$let_count - 2" | bc)
  #echo $line_count >> $new_dir/001.xml
  #echo $tail >> $new_dir/001.xml
  cat $f | tail -n $tail | head -n $head >> $new_dir/001.xml
done

echo "</ListRecords></OAI-PMH>" >> $new_dir/001.xml

cat $new_dir/001.xml | xmllint --format - > $new_dir/002.xml
mv $new_dir/002.xml $new_dir/001.xml

cd ~-
IFS=$SAVEIFS
