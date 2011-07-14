folder=${1}
echo compressing ${folder}
mkdir -p ${folder}_ids
rm -fR ./${folder}_ids/*
cd ${folder}
for file in *
do
    echo $file
    grep 'oai:library.rochester.edu:URVoyager1/' $file > ../${folder}_ids/${file}
done
cd ~-
