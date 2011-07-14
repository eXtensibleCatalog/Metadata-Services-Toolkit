# the first thing I did was download the four mrc files from here:
# http://docushare.lib.rochester.edu/docushare/dsweb/View/Collection-6173
# and put them in the "randys30" folder
# rename marc files to mrc
# I simply added a prefix to them (eg 01-)
# you need a fresh oaitoolkit.  Simply delete any files in the lucene_index folder
# I also started tomcat with an oaitoolkit running

pwd1=$(pwd)
oaitoolkit_home=/cygdrive/c/dev/xc/oai-toolkit/installation/OAIToolkit-0.6.5
cd $oaitoolkit_home/randys30
after_load="$(date -u +'%Y-%m-%dT%k:%M:%S')Z"
log_file=$pwd1/mst-service/custom/MARCNormalization/test/input_records/randys-30/log
rm $log_file
for file in $(ls -1 *)
do
	before_load=$after_load
	cp $file ../marc/
	cd ..
	#this is only necessary if the leader doesn't have the 'd'
	#cmd=convertload_as_deleted.bat
	cmd=convertload.bat
	echo $file
	if  [ -z $(echo $file | grep 'deleted') ] 
	then
		cmd=convertload.bat
	fi
	echo "==================================================" >> $log_file
	echo "==================================================" >> $log_file
	echo "==== processing ${file}..." >> $log_file
	echo "==== executing cmd: ${cmd}..." >> $log_file
	echo "==================================================" >> $log_file
	./${cmd} >> $log_file
	echo "==================================================" >> $log_file
	echo "" >> $log_file
	sleep 1
	cd randys30
	out_file=$pwd1/mst-service/custom/MARCNormalization/test/input_records/randys-30/$file.xml
	after_load="$(date -u +'%Y-%m-%dT%k:%M:%S')Z"
	echo "curl http://localhost:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marcxml&from=$before_load&until=$after_load | xmllint --format - > $out_file" >> $log_file
	curl "http://localhost:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marcxml&from=$before_load&until=$after_load" | xmllint --format - > $out_file
	echo "==== grep '<record>' $out_file | wc -l" >> $log_file
	grep '<record>' $out_file | wc -l >> $log_file
	echo "==================================================" >> $log_file
	echo "==================================================" >> $log_file
	echo "" >> $log_file
done
cd $pwd1
