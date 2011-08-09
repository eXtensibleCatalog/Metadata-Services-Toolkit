for file in $(ls -1 $MST_INSTANCE/logs/*.txt*) 
do
	#echo "" > $file
	rm $file
	echo clearing $file
done

for file in $(find $MST_INSTANCE/logs/ -regex '.*\.txt.[0-9]*')
do
	rm $file
	echo removing $file
done

. ./scripts/clear_tc_files.sh
