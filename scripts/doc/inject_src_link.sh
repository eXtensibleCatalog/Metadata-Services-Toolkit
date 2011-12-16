die () {
    echo >&2 "$@"
    exit 1
}

[ "$#" -eq 1 ] || die "1 argument required, $# provided"

servicename=${1}
dir=./mst-service/custom/${servicename}
#baseurl=${2}
baseurl="http\://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/bens_perma_branch/mst-service/custom/${servicename}/src/"
echo adding src link for dir: ${dir}
prev_pwd=${PWD}
cd ${dir}
ant.bat copy.src
cd ./build/src
for file in $(find . -name \*.java)
do
  echo $file 
  sed -i "s:\(^ \* @author.*\):\1\n * \n * @see <a href=\"${baseurl}${file}\">src on google code</a>:g" ${file}
done
cd ../..
ant.bat javadoc
cd $prev_pwd

