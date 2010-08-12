o="./mst-service/custom/MARCNormalization/test/expected_output_records/orig-186"
for fn in $(ls -1 $o/*.xml | sed 's/.*\///g')
do
	echo $o/$fn.bk
	#cat ${o}/$fn | sed '/<status>active<\/status>/d' > ${o}/${fn}.bak
	cat $o/$fn | sed '/<status>active<\/status>/d' | sed 's/<header>/\<header status=\"active\"\>/g' > $o/${fn}.bak
	mv $o/$fn.bak $o/$fn
done
