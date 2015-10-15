If you are processing any considerable amount of data in your MST, you will most likely want to tweak some MySQL options.  This is easily accomplished by modifying the my.cnf file and restarting MySQL.  See [MySQL's documentation](http://dev.mysql.com/doc/refman/5.5/en/option-files.html) to learn more about how to edit your startup options.  Here's a list of tweaks that we found to be helpful and would suggest others use as well:

### my.cnf ###
```
# MySQL changed the name of this param in 5.5
#   note: MySQL will fail to start up if you mix this up
# This is the name in 5.0/5.1
# key_buffer = 16M
# This is the name in 5.5
key_buffer_size = 16M

max_allowed_packet = 128M
thread_stack = 256K

# Point the following paths to different dedicated disks
datadir=/xc/mysql/
tmpdir=/xc/mysql/tmp
```
<a href='Hidden comment: 
these are settings I tweaked, but I"m not sure actually helped.  CARLI"s servers do not have these non-default options and it runs fine.  So I"m burying these down here:

5.0/5.1/5.5
sort_buffer_size = 2M
read_buffer_size = 2M
bulk_insert_buffer_size = 64M
myisam_sort_buffer_size = 64M
myisam_max_sort_file_size = 10G
myisam_max_extra_sort_file_size = 10G
transaction_isolation = READ-COMMITTED
init_connect = "SET AUTOCOMMIT=0"

5.0/5.1
table_cache = 512

5.5
table_open_cache = 512

also note that the values I was using for our UR servers are actually higher.  I"m guessing that if it worked on CARLI, then the lower numbers (the ones that are posted) are just fine.  However, CARLI does have a pretty slick harddrive cache that might be masking a potential problem with the lower numbers.  I"m guessing that"s not the case, though.

'></a>