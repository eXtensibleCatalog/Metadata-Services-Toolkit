## Introduction ##
Want to keep track of some changes I made to the MySQL setup on 225 via modifying /etc/mysql/my.cnf

## Details ##

Tried tuning mysql through my.cnf and by setting parameters in my.cnf as recommended by tuning tool https://launchpad.net/mysql-tuning-primer
from:  http://forge.mysql.com/projects/project.php?id=44.

Changes I made are between the 'jfb' comments:

```
# jfb add:
max_connections         = 32
table_open_cache        = 512
table_cache             = 512
table_definition_cache  = 512
innodb_buffer_pool_size = 4G
key_buffer_size         = 4G
# end jfb add:
#thread_concurrency     = 10
#
# * Query Cache Configuration
#
query_cache_limit       = 1M
# jfb modify, was 16m:
query_cache_size        = 128M
# end jfb add:
```

## More, Later ##

I ran the script prior to completing the run of records into xc\_marcaggregation database.  After one school completed, I ran the script again, and it made further recommendations, I have not acted on them yet.  They are:

```
jbrand@xc-brand:~$ ./tuning-primer.sh

        -- MYSQL PERFORMANCE TUNING PRIMER --
             - By: Matthew Montgomery -

MySQL Version 5.1.63-0ubuntu0.10.04.1 x86_64

Uptime = 2 days 19 hrs 16 min 34 sec
Avg. qps = 1767
Total Questions = 428172375
Threads Connected = 7

Server has been running for over 48hrs.
It should be safe to follow these recommendations

To find out more information on how each of these
runtime variables effects performance visit:
http://dev.mysql.com/doc/refman/5.1/en/server-system-variables.html
Visit http://www.mysql.com/products/enterprise/advisors.html
for info about MySQL's Enterprise Monitoring and Advisory Service

SLOW QUERIES
The slow query log is NOT enabled.
Current long_query_time = 10.000000 sec.
You have 4 out of 428172396 that take longer than 10.000000 sec. to complete
Your long_query_time seems to be fine

BINARY UPDATE LOG
The binary update log is NOT enabled.
You will not be able to do point in time recovery
See http://dev.mysql.com/doc/refman/5.1/en/point-in-time-recovery.html

WORKER THREADS
Current thread_cache_size = 8
Current threads_cached = 0
Current threads_per_sec = 0
Historic threads_per_sec = 0
Your thread_cache_size is fine

MAX CONNECTIONS
Current max_connections = 32
Current threads_connected = 7
Historic max_used_connections = 7
The number of used connections is 21% of the configured maximum.
Your max_connections variable seems to be fine.

INNODB STATUS
Current InnoDB index space = 730 M
Current InnoDB data space = 2.66 G
Current InnoDB buffer pool free = 26 %
Current innodb_buffer_pool_size = 4.00 G
Depending on how much space your innodb indexes take up it may be safe
to increase this value to up to 2 / 3 of total system memory
MEMORY USAGE
Max Memory Ever Allocated : 8.14 G
Configured Max Per-thread Buffers : 85 M
Configured Max Global Buffers : 8.12 G
Configured Max Memory Limit : 8.21 G
Physical Memory : 31.48 G
Max memory limit seem to be within acceptable norms

KEY BUFFER
Current MyISAM index space = 13.61 G
Current key_buffer_size = 4.00 G
Key cache miss rate is 1 : 11868
Key buffer free ratio = 18 %
You could increase key_buffer_size
It is safe to raise this up to 1/4 of total system memory;
assuming this is a dedicated database server.

QUERY CACHE
Query cache is enabled
Current query_cache_size = 128 M
Current query_cache_used = 71 M
Current query_cache_limit = 1 M
Current Query cache Memory fill ratio = 55.90 %
Current query_cache_min_res_unit = 4 K
Query Cache is 28 % fragmented
Run "FLUSH QUERY CACHE" periodically to defragment the query cache memory
If you have many small queries lower 'query_cache_min_res_unit' to reduce fragmentation.
MySQL won't cache query results that are larger than query_cache_limit in size

SORT OPERATIONS
Current sort_buffer_size = 2 M
Current read_rnd_buffer_size = 256 K
Sort buffer seems to be fine

JOINS
Current join_buffer_size = 132.00 K
You have had 0 queries where a join could not use an index properly
Your joins seem to be using indexes properly

OPEN FILES LIMIT
Current open_files_limit = 1066 files
The open_files_limit should typically be set to at least 2x-3x
that of table_cache if you have heavy MyISAM usage.
Your open_files_limit value seems to be fine

TABLE CACHE
Current table_open_cache = 512 tables
Current table_definition_cache = 512 tables
You have a total of 379 tables
You have 382 open tables.
The table_cache value seems to be fine

TEMP TABLES
Current max_heap_table_size = 16 M
Current tmp_table_size = 16 M
Of 56509 temp tables, 0% were created on disk
Created disk tmp tables ratio seems fine

TABLE SCANS
Current read_buffer_size = 128 K
Current table scan ratio = 0 : 1
read_buffer_size seems to be fine

TABLE LOCKING
Current Lock Wait ratio = 0 : 428172690
Your table locking seems to be fine
```