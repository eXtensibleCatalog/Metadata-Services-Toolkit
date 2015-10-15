### large index ###
  * I tarred the index on 254
```
2.9G 2011-09-02 15:11 /home/banderson/apache-solr-3.3.0.tgz
```
    * 2,554,876 documents
  * On 2011-09-08, Peter created a new index which seems to be performing much more quickly

### small index ###
> since many of the potential tweaks will be to create the index differently, it'd be good to be able to test performance improvements using a smaller index.  This means we could test the efficacy of those tweaks much more quickly.  However, we'll need to make sure the smaller index test adequately simulates a larger index

### gotchas ###
  * make sure you clear the caches
    * _and make sure your hard disk doesn't have significant caching (like CARLI's sweet SSD-fronted RAID Z3 (ZFS) array_
```
banderson@xc-devel:~$ cat reset_caches.sh
umount /mnt
mount /mnt
sync && echo 1 > /proc/sys/vm/drop_caches
cat /mnt/apache-solr-3.3.0/file_2_clear_cache > ~/delete_me
rm ~/delete_me
banderson@xc-devel:~$ ls -ladh /mnt/apache-solr-3.3.0/file_2_clear_cache
-rw-r--r-- 1 banderson admin 64M 2011-09-07 14:05 /mnt/apache-solr-3.3.0/file_2_clear_cache
banderson@xc-devel:~$ #kill solr
banderson@xc-devel:~$ sudo reset_caches.sh
banderson@xc-devel:~$ #start solr
```