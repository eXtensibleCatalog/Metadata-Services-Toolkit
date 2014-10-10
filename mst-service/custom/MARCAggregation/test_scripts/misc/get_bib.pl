#!/usr/bin/perl

# This script returns the incoming marcxml (from marcnormalization)
my $inst;
my $bib;

# database connection variables
$DATABASE = "xc_marcaggregation";
$DATABASE_DRIVER = "mysql";
$DATABASE_OPTIONS = "";
$DATABASE_USER = "root";
$DATABASE_PASSWORD = "root";

my $valid = 0;
if ($#ARGV >= 0) {
   if ($ARGV[0] =~ /^\(([^\(]+)\)\s*([^\s]+)/) {
      $inst = $1;
      $bib = $2;
      $inst =~ tr/a-z/A-Z/;
      $valid = 1;
   }
}
if (! $valid) {
   die "Usage: $0 (UIUdb)123 \n";
}

use DBI;

my $sql_template_1 = <<"EOF";
SELECT
    m.input_record_id from matchpoints_035a m
LEFT OUTER JOIN
   prefixes_035a p on m.prefix_id = p.prefix_id
WHERE
   p.prefix = ? and m.numeric_id = ?
;

EOF

my $sql_template_2 = <<"EOF";
SELECT
   xml
    FROM xc_marcnormalization.records_xml
WHERE
    record_id = ?
;

EOF

#select m.* from matchpoints_035a m left outer join prefixes_035a p on m.prefix_id = p.prefix_id where p.prefix = 'AUGDB' and m.numeric_id = 95900

&connect_to_db();

my (@record_ids);
$sth = $DBH->prepare($sql_template_1);
if (! $sth->execute($inst, $bib)) {
   $err = $sth->errstr;
   $sth->finish;
   &clean_up;
   die "$err";
}
&gather($sth, \@record_ids);
$sth->finish;

if ($#record_ids > 0) {
print "<collection>\n"
}
$sth = $DBH->prepare($sql_template_2);
for my $record_id (@record_ids) {
   if (! $sth->execute($record_id)) {
      $err = $sth->errstr;
      $sth->finish;
      &clean_up;
      die "$err";
   }
   &process($sth);
   print "\n";
}
$sth->finish;
if ($#record_ids > 0) {
print "</collection>\n"
}


&clean_up;

sub gather {
   my ($sth, $arr) = @_;

   my (@row);
   while (@row = $sth->fetchrow_array()) {
      my ($a) = @row;
      push (@{$arr}, $a);
   } 
}

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
