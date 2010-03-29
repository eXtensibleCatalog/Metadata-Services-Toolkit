delete from services where service_id=3;
insert into services (
	service_id,
	service_name,
	service_jar,
	service_configuration,
	class_name,
	identifier,
	warnings,
	errors,
	input_record_count,
	output_record_count,
	last_log_reset,
	log_file_name,
	harvest_out_warnings,
	harvest_out_errors,
	harvest_out_records_available,
	harvest_out_records_harvested,
	harvest_out_last_log_reset,
	harvest_out_log_file_name,
	status,
	xccfg_file_name,
	version,
	is_deleted
)
select 
	3,
	'db norm service',
	service_jar,
	service_configuration,
	'xc.mst.services.normalization.DBNormalizationService',
	identifier,
	warnings,
	errors,
	input_record_count,
	output_record_count,
	last_log_reset,
	log_file_name,
	harvest_out_warnings,
	harvest_out_errors,
	harvest_out_records_available,
	harvest_out_records_harvested,
	harvest_out_last_log_reset,
	harvest_out_log_file_name,
	status,
	xccfg_file_name,
	version,
	is_deleted
from services where service_id=1;

delete from services_to_input_formats where service_id=3;
insert services_to_input_formats (
	service_id,
	format_id
)
select 3, format_id
from services_to_input_formats
where service_id=1;

delete from services_to_output_formats where service_id=3;
insert services_to_output_formats (
	service_id,
	format_id
)
select 3, format_id
from services_to_output_formats
where service_id=1;
