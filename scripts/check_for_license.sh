#find ./ -name \*.xml -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|build\|input\|output\)\/' | xargs grep -Hc 'Copyright (c) 2009 University of Rochester' | gawk -F ':' '$2==0{print $1}'

find ./ -name \*.sh -printf '"%h/%f"\n' | grep -v svn | grep -v '\/\(bin\|build\|input\|output\)\/' | xargs grep -Hc 'Copyright (c) 2009 University of Rochester' | gawk -F ':' '$2==0{print $1}'
