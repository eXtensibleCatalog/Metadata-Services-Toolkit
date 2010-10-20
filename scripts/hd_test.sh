# USAGE:
# ./speed_test.sh /path/to/my/file /path/to/destination 

t0=`scripts/time_ms.sh`

# before you run this script create a file to cp
# just for a few seconds
# cat /dev/zero > myfile.zero
cp $1 $2
t1=`scripts/time_ms.sh`

seconds=`echo $t1 - $t0 | bc`
millis=`echo "$seconds * 1000" | bc`
bytes=`ls -lad ~/myfile.zero  | awk '{print $5}'`

echo bytes: $bytes
echo seconds = $seconds
echo millis = $millis

echo bytes / ms = `echo $bytes / $millis | bc`
