delete from harvests;
delete from harvest_schedule_steps;

update providers set updated_at=null;
