#!/usr/bin/perl
my $id = $ARGV[0];

# database connection variables
$DATABASE = "xc_marcaggregation";
$DATABASE_DRIVER = "mysql";
$DATABASE_OPTIONS = "";
$DATABASE_USER = "root";
$DATABASE_PASSWORD = "root";

if ($#ARGV >= 1) {
   $DATABASE = $ARGV[1];
}

use DBI;

my $sql_template = <<"EOF";
SELECT
   xml
    FROM records_xml
WHERE
    record_id = ?
;

EOF

&connect_to_db();

$sth = $DBH->prepare($sql_template);
if (! $sth->execute($id)) {
   $err = $sth->errstr;
   $sth->finish;
   &clean_up;
   die "$err";
}

&process($sth);

$sth->finish;

&clean_up;


   
sub process {
   my ($sth) = @_;

   my (@row);
   while (@row = $sth->fetchrow_array()) {
      my ($xml) = @row;
      print $xml;
   } 
}

# free up connection to the database
sub clean_up {
   $DBH->disconnect;
}

sub connect_to_db {
   my ($dsn) = "DBI:$DATABASE_DRIVER:database=$DATABASE;$DATABASE_OPTIONS";
   $DBH = DBI->connect($dsn, $DATABASE_USER, $DATABASE_PASSWORD);
}


1;
