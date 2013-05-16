#!/usr/local/bin/perl

# database connection variables
$DATABASE = "xc_marcaggregation";
$DATABASE_DRIVER = "mysql";
$DATABASE_OPTIONS = "";
$DATABASE_USER = "root";
$DATABASE_PASSWORD = "";

$INDENT = 4;

use DBI;

&connect_to_db();

$sql = <<"EOF";
SELECT
    t1.output_record_id, t1.input_record_id, t2.leaderByte17, t2.size, t3.xml

    FROM bib_records t1

        LEFT OUTER JOIN merge_scores t2 ON t1.input_record_id=t2.input_record_id 

        LEFT OUTER JOIN xc_marcnormalization.records_xml t3 ON t1.input_record_id=t3.record_id

        where t1.output_record_id in (SELECT
                        output_record_id
                        FROM bib_records
                        GROUP BY output_record_id
                        HAVING COUNT(*)>1
                    )

ORDER BY t1.output_record_id,
         t1.input_record_id;


EOF

$sth = $DBH->prepare($sql);
if (! $sth->execute()) {
   $err = $sth->errstr;
   $sth->finish;
   &clean_up;
   die "$err";
}

my $last_rec_id = "";
my $last_rec = "";

my (@row);
while (@row = $sth->fetchrow_array()) {
   my ($output_record_id,
   $input_record_id,
   $leaderByte17,
   $size,
   $xml) = @row;

   if ($output_record_id ne $last_rec_id) {
       if ($last_rec_id ne "") {
          &print_header($last_rec_id);
          print $last_rec . "\n";
       }
       $last_rec = "";
       $last_rec_id = $output_record_id;
   }

#<marc:record xmlns:marc="http://www.loc.gov/MARC21/slim" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.loc.gov/MARC21/slim http://localhost:8080/OAIToolkit/schema/MARC21slim_custom.xsd"><marc:leader>00793nam a2200277 i 4500</marc:leader><marc:controlfield tag="001">81802</marc:controlfield><marc:controlfield tag="003">NIUdb</marc:controlfield>

   my $bib_id = "";
   if ($xml =~ /<marc:controlfield tag=\"001\">([^<]+)</) {
      $bib_id = $1;
   }
   my $org_code = "";
   if ($xml =~ /<marc:controlfield tag=\"003\">([^<]+)</) {
      $org_code = $1;
   }

   $last_rec .= &indent(1) . "INPUT ID: ${input_record_id}: [${org_code}] ${bib_id}\n";
   $last_rec .= &indent(2) . "LDR/17: [${leaderByte17}]; Size: ${size} \n";

   my $matchsets = &get_matchsets($DBH, $input_record_id);
   $last_rec .= $matchsets if ($matchsets ne "");
   $last_rec .= "\n";

}

if ($output_record_id ne $last_rec_id) {
    &print_header($last_rec_id);
    print $last_rec . "\n";
}

$sth->finish;
&clean_up;


sub get_matchsets {
   my ($dbh, $id) = @_;
   my $ret = "";

   my @sql_info = (
      "010a", "matchpoints_010a" , "numeric_id" ,
      "020a", "matchpoints_020a" , "string_id" ,
      "022a", "matchpoints_022a" , "string_id" ,
      "024a", "matchpoints_024a" , "string_id" ,
      "035a", "matchpoints_035a" , "full_string" ,
   );

   for (my $i=0; $i < ($#sql_info / 3); $i++) {
      my $label = $sql_info[$i*3];
      my $table = $sql_info[$i*3 + 1];
      my $field = $sql_info[$i*3 + 2];

      my $sql =<<"EOF";
SELECT ${field} from ${table} where input_record_id=?
EOF
      my $sth = $dbh->prepare($sql);
      if ($sth->execute($id)) {
         my (@row);
         while (@row = $sth->fetchrow_array()) {
            my ($numeric_id) = @row;
            $ret .= &indent(($i + 2));
            $ret .= "${label}: " . $numeric_id . "\n";
         }
         $sth->finish;
      }
   }

   return $ret;
}

sub indent {
   my ($sz) = @_;
   my $ret = "";
   for (my $i=0; $i < $sz * $INDENT; $i++) {
      $ret .= " ";
   }
   return $ret;
}

sub print_header {
   my ($id) = @_;
   print "-----------------------------------\n";
   print "OUTPUT ID: " . $id . ":\n";
   print "-----------------------------------\n";
}


# free up connection to the database
sub clean_up {
   $DBH->disconnect;
}

sub connect_to_db {
   my ($dsn) = "DBI:$DATABASE_DRIVER:database=$DATABASE;$DATABASE_OPTIONS";
   $DBH = DBI->connect($dsn, $DATABASE_USER, $DATABASE_PASSWORD);
}


