for file in $(svn status | gawk '{print $2}' | grep -v '\.properties' | sed 's/Changelist//g' |  sed '/^$/d' | sed 's/\\/\//g') 
do
	#echo ${file}
	scp ${file} benjamina@128.151.244.170:~/mst/svn/branches/bens_perma_branch/${file}
done
