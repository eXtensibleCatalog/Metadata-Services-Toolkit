/*
select straight_join r.record_id, 
	r.oai_datestamp, 
	r.format_id, r.status, r.prev_status  , 
	x.xml,  
	max(u.date_updated) as date_updated  
from  xc_test_repo.record_updates u force index (idx_record_updates_record_id),  
	xc_test_repo.records r ,  
	xc_test_repo.records_xml x  
where r.record_id = x.record_id  
	and (r.record_id > ? or ? is null)  
	and r.record_id = u.record_id  
	and (u.date_updated >= ? or ? is null)  
	and u.date_updated <= ?   
	and status in ('A','D') 
group by u.record_id  
order by u.record_id  limit 1000
*/

select straight_join 1, u.date_updated  
from  xc_test_repo.record_updates u force index (idx_record_updates_date_updated) ,  
	xc_test_repo.records r 
where r.record_id = u.record_id  
/*
	and (u.date_updated >= '2011-06-07 08:51:34' or '2011-06-07 08:51:34' is null)  
	*/
	and u.date_updated <= '2011-06-07 08:52:14'
	and status in ('A','D') 
group by u.date_updated
	/*
limit 1;
*/
