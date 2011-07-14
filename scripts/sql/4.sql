/*
select straight_join r.record_id, r.oai_datestamp, 
	r.format_id, r.status, r.prev_status  , x.xml,  max(u.date_updated) as date_updated  
	*/
select count(*)
from  xc_multiple_holding_outputs.record_updates u force index (idx_record_updates_record_id),  
	xc_multiple_holding_outputs.records r IGNORE index (idx_records_format_id) ,  
	xc_multiple_holding_outputs.records_xml x  
where r.record_id = x.record_id  
	and (r.record_id >= 0 or 0 is null)  
	and r.record_id = u.record_id  
	and (u.date_updated >= '1960-01-01' or '1960-01-01' is null)  
	and u.date_updated <= '2011-06-15 13:09:05'
	and status in ('A','D') 
	and r.format_id = 1  
group by u.record_id  
order by u.record_id  limit 1000
