<font color='red'>2011-06-27.  BDA started going down the road of converting the spreadsheet to wiki, but per Dave's orders I will focus on just documenting what the code actually does first.</font>

This document was originally a spreadsheet in UR's docushare.  The original can be found <a href='http://docushare.lib.rochester.edu/docushare/dsweb/Services/Document-31167'>here</a>.

If specific subfields are not listed in mapping instructions, map all.

Eliminate subfield code and data, leaving only a single space between the two subfields, unless instructed otherwise (in 6XX fields, for example)

Do not map the following subfields, wherever they appear: $5, $6, $8 (for now)

For fields that can appear in either the bib or holding records, the same instructions are provided.  If the same data appears in both records, this may need to be deduped

Fields with pink highlighting are highest priority; no highlighting second priority.

Those with grey highlighting will not be mapped at this time.

Those with blue highlighting won't actually be mapped during the transformation routine because Normalization services will move data to a 9XX or somewhere else in the MARCXML record.

Green highlighting:  formerly yellow; issue now resolved and ready for mapping.

July 31:  all formerly-yellow now dealt with.  Several fields greyed out, others now green.  Test records identified

Sept 12:  some changes made - these cells now have YELLOW highlighting - feel free to get rid of the yellow - JB

Oct3:  clarified which of the parallel elements (e.g. identifiers) field should map to:  xc or dcterms (yellow) and added schema tokens for all elements
Updated doc to match latest changes in the schema (element names, etc.)

Nov. 27