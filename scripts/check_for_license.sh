#find ./ -name \*.xml -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|build\|input\|output\)\/' | xargs grep -Hc 'Copyright (c) 2009 University of Rochester' | gawk -F ':' '$2==0{print $1}'

# which files have no copyrights?
#find ./ -name \*.java -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|build\|input\|output\)\/' | xargs grep -Hc 'Copyright (c)' | gawk -F ':' '$2==0{print $1}'


# lets change who the copyright is assigned to
for f in $(find ./ -name \*.java -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|build\|input\|output\)\/' | xargs grep -Hc 'Copyright .*University of Rochester' )
do
	sed -i 's/\(Copyright.*\)\(University of Rochester\)/\1eXtensible Catalog Organization/g' $f
done


