
```

select count(*)
from record_links rl;

/*
+----------+
| count(*) |
+----------+
|  2936749 |
+----------+
*/

select count(*), r.type
from record_links rl
	,records r
where rl.from_record_id = r.record_id
group by r.type;

/*
+----------+------+
| count(*) | type |
+----------+------+
|      565 | e    |
|  2935141 | h    |
|      513 | m    |
|      530 | w    |
+----------+------+
*/

select count(*), r.type
from record_links rl
	,records r
where rl.to_record_id = r.record_id
group by r.type;
+----------+------+
| count(*) | type |
+----------+------+
|      565 | e    |
|      537 | h    |
|  2819676 | m    |
|      531 | w    |
+----------+------+

/*
select count(*), rf.type, rt.type
from record_links rl
	,records rf
	,records rt
where rl.to_record_id = rt.record_id
	and rl.from_record_id = rf.record_id
group by rf.type,
	rt.type;
*/

+----------+------+------+
| count(*) | type | type |
+----------+------+------+
|      180 | e    | e    |
|      338 | e    | h    |
|       35 | e    | m    |
|       12 | e    | w    |
|       59 | h    | e    |
|      161 | h    | h    |
|  2819082 | h    | m    |
|      399 | h    | w    |
|      319 | m    | e    |
|       37 | m    | h    |
|      153 | m    | m    |
|        4 | m    | w    |
|        7 | w    | e    |
|        1 | w    | h    |
|      406 | w    | m    |
|      116 | w    | w    |
+----------+------+------+
*/

select *
from record_links rl
	,records rf
	,records rt
where rl.to_record_id = rt.record_id
	and rl.from_record_id = rf.record_id
	and rt.type='e'
	and rf.type='e'
limit 1;

+----------------+--------------+-----------+---------------------+------+--------+-------------+-----------+-----------+---------------------+------+--------+-------------+-----------+
| from_record_id | to_record_id | record_id | oai_datestamp       | type | status | prev_status | format_id | record_id | oai_datestamp       | type | status | prev_status | format_id |
+----------------+--------------+-----------+---------------------+------+--------+-------------+-----------+-----------+---------------------+------+--------+-------------+-----------+
|       13396549 |     13396543 |  13396549 | 2011-06-14 14:16:32 | e    | A      | N           |         5 |  13396543 | 2011-06-14 14:16:32 | e    | A      | N           |         5 |
+----------------+--------------+-----------+---------------------+------+--------+-------------+-----------+-----------+---------------------+------+--------+-------------+-----------+


$ mysql -P 3336 -t -u root --password=pass -D xc_marctoxctransformation -e "select * from record_predecessors where record_id in (13396549, 13396543)"
+-----------+----------------+
| record_id | pred_record_id |
+-----------+----------------+
|  13396543 |        6692602 |
|  13396543 |       13371151 |
|  13396549 |        6692604 |
|  13396549 |       13371153 |
+-----------+----------------+

$ mysql -P 3336 -t -u root --password=pass -D xc_marcnormalization -e "select * from record_predecessors where record_id in (6692602, 13371151, 6692604, 13371153)"
+-----------+----------------+
| record_id | pred_record_id |
+-----------+----------------+
|   6692602 |          12875 |
|   6692604 |          12877 |
|  13371151 |       13366213 |
|  13371153 |       13366215 |
+-----------+----------------+

$ mysql -P 3336 -t -u root --password=pass -D xc_rochester_137 -e "select * from record_oai_ids where record_id in (12875, 12877, 13366213, 13366215);"
+-----------+----------------------------------------------+
| record_id | oai_id                                       |
+-----------+----------------------------------------------+
|     12875 | oai:library.rochester.edu:URVoyager1/12874   |
|     12877 | oai:library.rochester.edu:URVoyager1/12876   |
|  13366213 | oai:library.rochester.edu:URVoyager1/6679939 |
|  13366215 | oai:library.rochester.edu:URVoyager1/6679941 |
+-----------+----------------------------------------------+

banderson@xc-devel:/xc/file_harvest$ grep -n 'oai:library.rochester.edu:URVoyager1/12874<' ./initial_load_from_137/*
./initial_load_from_137/7_9999_10000_6679727:165702:        <identifier>oai:library.rochester.edu:URVoyager1/12874</identifier>

banderson@xc-devel:/xc/file_harvest$ grep -n 'oai:library.rochester.edu:URVoyager1/12876<' ./initial_load_from_137/*
./initial_load_from_137/7_9999_10000_6679727:165814:        <identifier>oai:library.rochester.edu:URVoyager1/12876</identifier>

banderson@xc-devel:/xc/file_harvest$ grep -n '/6679939<' ./initial_load_from_137/*

banderson@xc-devel:/xc/file_harvest$ grep -n '/6679941<' ./initial_load_from_137/*

banderson@xc-devel:/xc/file_harvest$ grep -n 'oai:library.rochester.edu:URVoyager1/12874<' ./
file_harvester.sh          initial_load_from_137/     initial_load_from_137.out  update_1_from_137/         update_1_from_137.out      

banderson@xc-devel:/xc/file_harvest$ grep -n 'oai:library.rochester.edu:URVoyager1/12874<' ./update_1_from_137
update_1_from_137/     update_1_from_137.out  
banderson@xc-devel:/xc/file_harvest$ grep -n 'oai:library.rochester.edu:URVoyager1/12874<' ./update_1_from_137/*
banderson@xc-devel:/xc/file_harvest$ grep -n 'oai:library.rochester.edu:URVoyager1/12876<' ./update_1_from_137/*
banderson@xc-devel:/xc/file_harvest$ grep -n 'oai:library.rochester.edu:URVoyager1/6679939<' ./update_1_from_137/*
./update_1_from_137/13_5352881_20000_24967:14548:        <identifier>oai:library.rochester.edu:URVoyager1/6679939</identifier>
banderson@xc-devel:/xc/file_harvest$ grep -n 'oai:library.rochester.edu:URVoyager1/6679941<' ./update_1_from_137/*
./update_1_from_137/13_5352881_20000_24967:14657:        <identifier>oai:library.rochester.edu:URVoyager1/6679941</identifier>
```