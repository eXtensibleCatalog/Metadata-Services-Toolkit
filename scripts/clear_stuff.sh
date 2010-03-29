./scripts/clear_logs.sh
./scripts/remove_index.sh
./scripts/run_sql.sh delete_all_jobs.sql
./scripts/run_sql.sh delete_all_harvests.sql
./scripts/run_sql.sh delete_records.sql
./scripts/run_sql.sh ../../sql/records.sql
