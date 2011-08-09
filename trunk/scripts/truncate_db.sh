die () {
    echo >&2 "$@"
    exit 1
}

[ "$#" -eq 1 ] || die "1 argument required, $# provided"

mysqldump --no-data --add-drop-table -u root --password=root ${1} > ./build/${1}.sql
mysql -u root --password=root -D ${1} < ./build/${1}.sql
