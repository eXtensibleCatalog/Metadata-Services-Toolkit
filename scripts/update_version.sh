version=${1}
sed -i "s/^mi.version=.*/mi.version=${version}/" ./MST-instances/build.properties 
sed -i "s/^mc.version=.*/mc.version=${version}/" ./mst-common/build.properties
sed -i "s/^mp.version=.*/mp.version=${version}/" ./mst-platform/build.properties
sed -i "s/^ms.version=.*/ms.version=${version}/" ./mst-service/impl/build.properties

sed -i "s/^mst-service-impl.version=.*/mst-service-impl.version=${version}/" ./mst-service/example/build.properties
sed -i "s/^mst-instances.version=.*/mst-instances.version=${version}/" ./mst-service/example/build.properties
sed -i "s/^service.version=.*/service.version=${version}/" ./mst-service/example/custom.properties

ant.bat ms.copy-example

for service in ./mst-service/custom/*
do 
  echo $service
  cd $service
  sed -i "s/^service.version=.*/service.version=${version}/" ./custom.properties
  cd ~-
done

grep 'version' ./MST-instances/build.properties ./mst-common/build.properties ./mst-platform/build.properties ./mst-service/impl/build.properties ./mst-service/example/build.properties ./mst-service/example/custom.properties ./mst-service/custom/*/build.properties ./mst-service/custom/*/custom.properties | grep -v ivy
