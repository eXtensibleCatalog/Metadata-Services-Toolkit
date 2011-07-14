#find ./ -name \*.xml -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|build\|input\|output\)\/' | xargs grep -Hc 'Copyright (c) 2009 University of Rochester' | gawk -F ':' '$2==0{print $1}'

# which files have no copyrights?
#find ./ -name \*.java -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|build\|input\|output\)\/' | xargs grep -Hc 'Copyright (c)' | gawk -F ':' '$2==0{print $1}'


for f in $(find ./mst-platform/src/webapp/st -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|test\|MST-instances\/MST-instances\/MetadataServicesToolkit\/solr\|build-custom\|spring-service-custom\|build\|yui\|solr\|input\|docs\|output\)\(/\|\.\|$\)' | grep -iv '\.\(jsp\|gif\|png\|jpg\)' | grep '\..*\.' | xargs grep -Hc 'Copyright .*University of Rochester' | gawk -F ':' '{print $1}')
do
	echo ${f}
	sed -i 's/\(Copyright.*\)\(University of Rochester\)/\1eXtensible Catalog Organization/g' $f
	#cat license > build/out
	#dos2unix ${f}
	#cat ${f} >> build/out
	#cat build/out > ${f}
done

# lets change who the copyright is assigned to
#for f in $(find ./ -name \*.xml -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|build\|input\|output\)\/' | xargs grep -Hc 'Copyright .*University of Rochester' | gawk -F ':' '{print $1}')
#do
#	echo $f
	#sed -i 's/\(Copyright.*\)\(University of Rochester\)/\1eXtensible Catalog Organization/g' $f
#done


