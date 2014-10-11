#!/bin/perl

# This script splits up a single large output file (the results from mas_sets.pl)
# into many smaller ones (into directory ./output)
#
#    Usage: perl split_mas_sets_output.pl output.txt
#
#

# The results from mas_sets.pl look like this:
#
# -----------------------------------
# OUTPUT ID: 137825839:
# -----------------------------------
#     INPUT ID: 68912920: [ADLdb] 2
#         245$a: Encyclopædia Britannica;
#         LDR/17: [1]; Size: 3196
#                         035a: (ADLdb)2
# 
# 
# -----------------------------------
# OUTPUT ID: 137825840:
# -----------------------------------
#     INPUT ID: 68912921: [ADLdb] 5
#         245$a: How to run seminars and workshops :
#         LDR/17: []; Size: 11951
#         010a: 93005051
#             020a: 0471594776
#             020a: 0471594784
#             020a: 9780471594772
#             020a: 9780471594789
#                         035a: (ADLdb)5
#                         035a: (OCoLC)28293424
# 

$LINES_PER_FILE=100000;
$OUTPUT_DIRECTORY='./output';

$line_no = 0;
$output_id = "";
$line = "";
while (<>) {
   $newest_line = $_;

   if ($newest_line =~ /^OUTPUT ID: ([^:].+):/) {
      $this_output_id = $1;
      $output_id = $this_output_id if ($output_id eq "");
      if ($line_no >= ${LINES_PER_FILE}) {
         open OUT, "> ${OUTPUT_DIRECTORY}/${output_id}_${last_output_id}.txt";
         print OUT $buffer;
         close OUT;
         $buffer = $line;
         $line = "";
         $line_no = 0;
         $output_id = $this_output_id;
      }
      $last_output_id = $this_output_id;
   }
   $line_no++;
   $buffer .= $line;
   $line = $newest_line;
}
$buffer .= $line;

open OUT, "> ${OUTPUT_DIRECTORY}/${output_id}_${last_output_id}.txt";
print OUT $buffer;
close OUT;
