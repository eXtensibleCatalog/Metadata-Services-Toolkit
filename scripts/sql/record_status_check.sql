/*
explain select 1
from records r
where status in ('F','H') 
	-- and r.format_id = 1
limit 1
*/

select 1
from records r,
	record_sets rs
where status in ('F','H') 
	-- and r.format_id = 1
	and rs.record_id = r.record_id
	-- and rs.set_id = 3
limit 1
