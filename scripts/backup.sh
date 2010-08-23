export BACKUP_FOLDER="${HOME}/mst/backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p ${BACKUP_FOLDER} 2> /dev/null

. ./scripts/backup_logs.sh
. ./scripts/backup_db.sh
#. ./scripts/backup_index.sh
