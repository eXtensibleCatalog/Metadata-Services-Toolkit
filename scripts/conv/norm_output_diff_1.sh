rm out 2> /dev/null
#for fn in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/orig-186/1*.xml | sed 's/.*\///g')
for fn in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/orig-186/*.xml | sed 's/.*\///g')
do
	ef=./mst-service/custom/MARCNormalization/test/expected_output_records/orig-186/$fn
	
	svn diff --diff-cmd diff -x -uw ${ef} >> out
done

dos2unix out
sed 's/^Index.*//g' out > out2
#sed 's/^\+\+\+.*//g' out2 > out3
sed 's/.*<oldFileName.*//g' out2 > out3
sed 's/.*<\?xml.*version.*encoding//g' out3 > out2
sed 's/.*\(working copy\)//g' out2 > out3
#cat out2 > out3
#cat out2 > out3
sed 's/^@@@.*//g' out3 > out2
sed 's/^[^-+].*//g' out3 > out2
sed 's/.*<datestamp.*//g' out2 > out3
sed 's/.*<predecessor.*//g' out3 > out2
sed 's/.*<setSpec>.*//g' out2 > out3
sed 's/.*<status>.*//g' out3 > out2
sed 's/.*<\/predecessor.*//g' out2 > out3
sed 's/.*<identifier.*//g' out3 > out2

#cat out3 > out2
sed '/^$/d' out2 > out3
mv out3 out2

rm out3 2> /dev/null
