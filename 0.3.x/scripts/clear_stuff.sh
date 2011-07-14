./scripts/clear_logs.sh
./scripts/remove_index.sh

#./scripts/run_sql.sh ../../sql/records.sql
#./scripts/run_sql.sh delete_all_jobs.sql
#./scripts/run_sql.sh delete_all_harvests.sql
#./scripts/run_sql.sh delete_records.sql
#./scripts/run_sql.sh copy_service.sql

cat ./scripts/sql/mst_dump.sql | sed "s/==provider_url==/${provider_url}/g" | sed "s/==provider_name==/${provider_name}/g" | sed "s/==service_id==/${service_id}/g" | sed "s/==service_id_2==/${service_id_2}/g" | sed "s/==begin_comment_run_harvest==/${begin_comment_run_harvest}/g" | sed "s/==end_comment_run_harvest==/${end_comment_run_harvest}/g" | sed "s/==begin_comment_run_norm==/${begin_comment_run_norm}/g" | sed "s/==end_comment_run_norm==/${end_comment_run_norm}/g" | sed "s/==begin_comment_run_trans==/${begin_comment_run_trans}/g" | sed "s/==end_comment_run_trans==/${end_comment_run_trans}/g"  > ./scripts/sql/temp.sql

./scripts/run_sql.sh temp.sql
