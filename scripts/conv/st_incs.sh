find . -name \*.jsp | grep -v svn | grep -v build | grep -v bin | xargs grep '\/inc' 2> /dev/null | sed 's/:.*//g' | sort -u > 'incs.out'
for file in $(cat incs.out)
do
	echo $file
	sed 's/\/inc/\/st\/inc/g' $file > $file.out
	mv -f $file.out $file
done
