/*
select straight_join count(1), rn.type
from 
	xc_marctoxctransformation.records rt, 
	xc_marctoxctransformation.record_predecessors pt,
	xc_marcnormalization.records rn
where 
	rt.type='H' 
	and rt.record_id = pt.record_id
	and rn.record_id = pt.pred_record_id
	/*
/*
group by 
	rn.type
*/
/*
select straight_join count(1)
from 
	xc_marcnormalization.records rn
where 
	rn.type='H' 
*/
select straight_join count(1), pt.pred_record_id
from 
	xc_marctoxctransformation.record_predecessors pt,
	xc_marctoxctransformation.records rt
where 
	rt.type='H' 
	and rt.record_id = pt.record_id
group by pt.pred_record_id
having count(1) > 1
limit 1;
/*
*/
