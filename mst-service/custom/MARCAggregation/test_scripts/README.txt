1. Included in this directory are utility scripts which can be used in regression testing. 


   a. To get a printout of all aggregated (deduped) sets, run:

perl mas_sets.pl > output.txt


   b. To compare/diff two aggregated sets with each other, run:

perl compare_mas.pl outputA.txt outputB.txt


2. Also included are 8 test scenarios that can be used for regression testing.


   a. The directory Tsets will contain the MARC
      files representing 8 different time steps, T0.mrc, ..., T7.mrc


   b. The directory Tsets_output will contain the expected output, T0.txt, ..., T7.txt
      You can use the utility scripts in 1) above to compare against these time steps.

   c. Documentation explaining what is occuring in each step can be found in the
      following spreadsheets: Tsets_explained.xlsx Tsets_explained_in_detail.xlsx

