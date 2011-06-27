folder=${1}
echo extracting sets for ${folder}
mkdir -p ${folder}_sets
rm -fR ./${folder}_sets/*
cd ${folder}
for file in *
do
    echo $file
    grep '<setSpec>' $file > ../${folder}_sets/${file}
done
cd ~-
