for f in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/NormInput/*.xml)
#for f in $(ls -1 ./mst-service/custom/MARCNormalization/test/input_records/NormInput/481.xml)
do
	#cat ${f} | sed 's/\(<?xml.*\)\".>/\1" encoding=\"UTF-8\"?>/g' > ${f}.new
	#mv ${f}.new ${f}
	#cat ${f}.new.2 >> ${f}
	#rm ${f}.new*
	#mv ${f}.new ${f}
done
