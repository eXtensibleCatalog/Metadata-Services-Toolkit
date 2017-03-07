#!/usr/local/bin/perl

# database connection variables
$DATABASE = "xc_marcaggregation";
$DATABASE_DRIVER = "mysql";
$DATABASE_OPTIONS = "";
$DATABASE_USER = "root";
$DATABASE_PASSWORD = "root";

$INDENT = 4;

use DBI;

&connect_to_db();

my $sql_template = <<"EOF";
SELECT
    R.record_id, RP.pred_record_id, MS.leaderByte17, MS.size, t3.xml, if (isnull(ROS.input_record_id), '', '***')

    FROM records R

        LEFT OUTER JOIN record_predecessors RP ON RP.record_id = R.record_id and R.type = 'b' and R.status = 'A'

        LEFT OUTER JOIN merge_scores MS ON RP.pred_record_id=MS.input_record_id 

        LEFT OUTER JOIN xc_marcnormalization.records_xml t3 ON RP.pred_record_id=t3.record_id

        LEFT OUTER JOIN record_of_source ROS ON RP.pred_record_id=ROS.input_record_id  and R.record_id=ROS.output_record_id

ORDER BY RP.record_id,
         RP.pred_record_id

;

EOF


$sth = $DBH->prepare($sql_template);
if (! $sth->execute()) {
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

   my $last_rec_id = "";
   my $last_rec = "";

   my $cnt = 0;
   
   my (@row);
   while (@row = $sth->fetchrow_array()) {
      my ($output_record_id,
      $input_record_id,
      $leaderByte17,
      $size,
      $xml,
      $ros) = @row;

      next if (! $input_record_id);

      $cnt++;
   
      if ($output_record_id ne $last_rec_id) {
          if ($last_rec_id ne "") {
             #if ($cnt > 1) {
                &print_header($last_rec_id);
                print $last_rec . "\n";
             #}
          }
          $last_rec = "";
          $last_rec_id = $output_record_id;

          $cnt = 0;
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

      my $title = "";
#<marc:datafield ind1="1" ind2="0" tag="245"><marc:subfield code="a">Introduction to boolean algebras</marc:subfield><marc:subfield code="h">[electronic resource] /</marc:subfield><marc:subfield code="c">Steven Givant, Paul Halmos.</marc:subfield></marc:datafield>
      if ($xml =~ /<marc:datafield ind1=\".\" ind2=\".\" tag=\"245\">(.*?)<\/marc:datafield>/) {
         $title = $1;
         if ($title =~ /<marc:subfield code=\"a\">(.*?)<\/marc:subfield>/) {
            $title = $1;
         }
      }
   
      $last_rec .= &indent(1) . "INPUT ID: ${input_record_id}${ros}: [${org_code}] ${bib_id}\n";
      $last_rec .= &indent(2) . "245\$a: ${title} \n";
      $last_rec .= &indent(2) . "LDR/17: [${leaderByte17}]; Size: ${size} \n";
   
      my $matchsets = &get_matchsets($DBH, $input_record_id);
      $last_rec .= $matchsets if ($matchsets ne "");
      $last_rec .= "\n";
   
   }
   
   #if ($cnt > 1) {
      if ($output_record_id ne $last_rec_id) {
          &print_header($last_rec_id);
          print $last_rec . "\n";
      }
   #}
   
}

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

1;
