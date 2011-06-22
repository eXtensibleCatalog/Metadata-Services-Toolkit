/*
select straight_join count(*)
	, rl.to_record_id
from 
	record_links rl,
	records rw,
  	records re
where rw.type = 'w'
	and re.type = 'e'
	and rl.from_record_id = re.record_id
	and rl.to_record_id = rw.record_id
group by rl.to_record_id
	, rl.from_record_id
having count(*) > 1
limit 2;
*/

/*
select count(*)
from record_links rl;
*/
/*
+----------+
| count(*) |
+----------+
|  2936749 |
+----------+
*/

/*
select count(*), r.type
from record_links rl
	,records r
where rl.from_record_id = r.record_id
group by r.type;
*/
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

/*
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

*/

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
/*
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

/*
select *
from record_links rl
	,records rf
	,records rt
where rl.to_record_id = rt.record_id
	and rl.from_record_id = rf.record_id
	and rt.type='e'
	and rf.type='e'
limit 1;
*/
/*
+----------------+--------------+-----------+---------------------+------+--------+-------------+-----------+-----------+---------------------+------+--------+-------------+-----------+
| from_record_id | to_record_id | record_id | oai_datestamp       | type | status | prev_status | format_id | record_id | oai_datestamp       | type | status | prev_status | format_id |
+----------------+--------------+-----------+---------------------+------+--------+-------------+-----------+-----------+---------------------+------+--------+-------------+-----------+
|       13396549 |     13396543 |  13396549 | 2011-06-14 14:16:32 | e    | A      | N           |         5 |  13396543 | 2011-06-14 14:16:32 | e    | A      | N           |         5 |
+----------------+--------------+-----------+---------------------+------+--------+-------------+-----------+-----------+---------------------+------+--------+-------------+-----------+
*/

/*
select * 
from record_links
where from_record_id in 
(13396543,13396547,13396549, 13396551, 13396555) or to_record_id in 
(13396543,13396547,13396549, 13396551, 13396555);
*/

/*
select *
from record_predecessors
where record_id in 
(13396543,13396547,13396549, 13396551, 13396555);
*/
/*
select distinct pred_record_id 
from record_predecessors
where record_id in 
(13396541, 13396542, 13396543, 13396544, 13396545, 13396547, 13396548, 13396549, 13396551, 13396552, 13396555);
*/
/*
select distinct record_id 
from record_predecessors
where pred_record_id in 
(6692601, 6692602, 6692603, 6692604, 6692605, 6692606, 13371151, 13371152, 13371153, 13371154, 13371155 );
*/
/*
select *
from record_predecessors p,
	records r,
	record_updates u
where p.record_id = r.record_id
	and r.record_id = u.record_id
	and r.record_id = 13370940;
*/

/*
select *
from record_predecessors
where record_id in 
(6683810, 6692601, 6692602, 6692603, 6692604, 6692605, 6692606);
*/

/*
select * 
from record_oai_ids
where record_id in (4083, 12874, 12875, 12876, 12877, 12878, 12879 );
*/

/*
select *
from record_updates
where record_id in ( 6683735, 13370940, 13395911);
*/

/*
select count(*) from xc_rochester_137.records rr, xc_marcnormalization.records nr where rr.record_id = nr.record_id
*/

select o.oai_id
from xc_rochester_137.record_oai_ids o
	,xc_marcnormalization.records nr
	,xc_marcnormalization.record_predecessors nrp
	,xc_marctoxctransformation.records tr
	,xc_marctoxctransformation.record_predecessors trp
where tr.record_id = 29735902
	and trp.record_id = tr.record_id
	and trp.pred_record_id = nr.record_id
	and nrp.record_id = nr.record_id
	and nrp.pred_record_id = o.record_id

