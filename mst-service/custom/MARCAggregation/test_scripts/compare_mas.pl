#!/bin/perl

my $file1 = $ARGV[0];
my $file2 = $ARGV[1];

die "File '${file1}' doesn't exist.\n" if (! -e ${file1});
die "File '${file2}' doesn't exist.\n" if (! -e ${file2});

# -----------------------------------
# OUTPUT ID: 671:
# -----------------------------------
#     INPUT ID: 336: [BENdb] 217731
#         LDR/17: [I]; Size: 7602 
#             020a: 0387684360
#             020a: 9780387684369
#                         035a: (BENdb)217731
#                         035a: (OCoLC)314182885
#

my @file1_data;
open IN, "< ${file1}" || die "Couldn't open file ${file1} for reading: $! \n";
while (<IN>) {
   push(@file1_data, $_);
}
close IN;

my @file2_data;
open IN, "< ${file2}" || die "Couldn't open file ${file2} for reading: $! \n";
while (<IN>) {
   push(@file2_data, $_);
}
close IN;

my %set1 = ();
my %set2 = ();

&collect_data(\%set1, \@file1_data);
&collect_data(\%set2, \@file2_data);

my $cnt_set1 = keys (%set1);
my $cnt_set2 = keys (%set2);

my $ret1 = &compare(\%set1, $file1, \%set2, $file2);
my $ret2 = &compare(\%set2, $file2, \%set1, $file1);

if ($ret1==0 && $ret2==0) {
   print "These two files are equal.\n";
}

sub compare {
   my ($set1_ref, $set1_filename, $set2_ref, $set2_filename) = @_;
   my $ret = 0;

   foreach my $set1_id (sort keys %{$set1_ref}) {
   
      my $matched = 0;
   
      my @set1 = ();
      foreach my $e1 (keys(%{$$set1_ref{$set1_id}})) {
         push (@set1, $e1);
      }
   
      foreach my $set2_id (sort keys %{$set2_ref}) {
   
         my @set2 = ();
         foreach my $e2 (keys(%{$$set2_ref{$set2_id}})) {
            push (@set2, $e2);
         }
   
         if (&same_arrays(\@set1, \@set2)) {
             #print "Set 1:\n"; &print_array(\@set1);
             #print "Set 2:\n"; &print_array(\@set2);
             #print "***THEY MATCHED***\n";
   
             $matched = 1;
             last;
         }
   
      }
   
      if (! $matched) {
         print "Missing in '${set2_filename}':\n";
         &print_array(\@set1);
         print "\n";
         $ret = 1;
#        last;
      }
   }
   return $ret;
}

sub print_array {
   my ($ar) = @_;
   print "[\n";
   foreach my $a (sort @{$ar}) {
      print " " . $a . "\n";
   }
   print "]\n";
}

sub same_arrays {
   my ($ar1, $ar2) = @_;
   my $cnt1 = @{$ar1};
   my $cnt2 = @{$ar2};
   return 0 if ($cnt1 != $cnt2);
   foreach my $a (@{$ar1}) {
      my $matched = 0;
      foreach my $b (@{$ar2}) {
         if ($a eq $b) {
            $matched = 1;
            last;
         }
      }
      return 0 if (! $matched);
   }
   return 1;
}

sub collect_data {
   my ($set_ref, $data_ref) = @_;

   my $output_id = "";
   my $bib_id = "";
   my $bib_info = "";
   
   my $line;
   my $next_line_is_header = 0;
   my $next_line_is_end_of_header = 0;
   my $next_line_is_bib = 0;
   
   foreach my $line (@{$data_ref}) {
   
      if ($next_line_is_header) {
         $next_line_is_header = 0;
         if ($line =~ /^OUTPUT ID: ([0-9]+):/) {
            $output_id = $1;
            $next_line_is_end_of_header = 1;
         } else {
            die "0Parsing error!\n";
         }
         next;
      }
   
      if ($next_line_is_end_of_header) {
         $next_line_is_end_of_header = 0;
         if ($line =~ /^----------/) {
            $next_line_is_bib = 1;
         } else {
            die "1Parsing error!\n";
         }
         next;
      }
   
      if ($line =~ /^----------/) {
         if ($next_line_is_end_of_header) { 
            $next_line_is_end_of_header = 0;
            $next_line_is_bib = 1;
         } else {
            if ($output_id ne "") {
               # process set
               if ($bib_info ne "") {
                  $$set_ref{$output_id}{$bib_id} = $bib_info;
               }
            }
            $output_id = "";
            $bib_id = "";
            $bib_info = "";
   
            $next_line_is_header = 1;
            $next_line_is_bib = 0;
         }
         next;
      }
   
      if ($line =~ /^\s*$/) {
         # process bib
         if ($bib_id ne "") {
               if ($bib_info ne "") {
                  $$set_ref{$output_id}{$bib_id} = $bib_info;
               }
         }
         $bib_id = "";
         $bib_info = "";
   
         $next_line_is_bib = 1;
         next;
      }
   
      if ($next_line_is_bib) {
         $next_line_is_bib = 0;
         if ($line =~ /^\s+INPUT ID: [0-9]+\**: (.+)$/) {
            $bib_id = $1;
         } else {
            die "2Parsing error!\n";
         }
         next;
      }
   
      $bib_info .= $line;
   
   }
   
   if ($output_id ne "") {
      # process set
      if ($bib_info ne "") {
         $$set_ref{$output_id}{$bib_id} = $bib_info;
      }
      $output_id = "";
      $bib_id = "";
      $bib_info = "";
   }

}

