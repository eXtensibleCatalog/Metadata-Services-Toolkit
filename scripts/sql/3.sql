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

select *
from record_links rl
	,records rf
	,records rt
where rl.to_record_id = rt.record_id
	and rl.from_record_id = rf.record_id
	and rt.type='e'
	and rf.type='e'
limit 1;
