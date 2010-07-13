rm out 2> /dev/null
url='http://code.google.com/p/xcmetadataservicestoolkit/source/diff?spec=svn983&r=983&format=side&path=/branches/bens_perma_branch/mst-service/custom/MARCNormalization/test/expected_output_records/orig-186/@FILE@'

for fn in $(cat out2 | grep '^--.*' | sed 's/.*\/\([0-9]*\.xml\).*/\1/g')
do
	echo ${url} | sed "s/@FILE@/${fn}/g"
done

