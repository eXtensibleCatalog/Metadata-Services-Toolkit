# Verifying Record Counts #

### Introduction ###
This page describes what record counts mean when you see them listed in log files, and how to decode whether your MST is behaving per your expectations through the use of rules.  It also attempts to share some implementation details and examples.

### Overview of what we are counting ###

We are counting active, updates, and deletes, but there are at least four categories - we shall count the following kinds of arriving records (assume that active is defined as a record that is not marked-deleted):

  1. new record (active)
  1. new record (marked-deleted)
  1. updated record (active)
  1. updated record (marked-deleted)

We also count some additional categories that would also look at what the updated records are replacing (two of the above cases):

  * 3a) updated (active replacing an existing active)
  * 3b) updated (active replacing an existing marked-deleted)
  * 4a) updated (marked-deleted replacing an existing active)
  * 4b) updated (marked-deleted replacing an existing marked-deleted)

active records 	all incoming records not marked as deleted
updates 	incoming records that have previously been harvested from this provider
deleted records 	incoming records that have been marked as deleted (if a record is active, then deleted, then active, then deleted, this counter will be incremented twice

## A Closer Look at Counting in a Service Log File ##

### transformation log ###
```

    incoming 2012-02-29T18:22:18Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    incoming 2012-02-29T18:22:18Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
    incoming 2012-02-29T18:22:18Z b                              unexpected_error_cnt:            0
    incoming 2012-02-29T18:22:18Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    incoming 2012-02-29T18:22:18Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
    incoming 2012-02-29T18:22:18Z h                              unexpected_error_cnt:            0
    incoming 2012-02-29T18:22:18Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    incoming 2012-02-29T18:22:18Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           53
    incoming 2012-02-29T18:22:18Z TOTALS                         unexpected_error_cnt:            0
    29 Feb 2012 12:24:33,880  INFO [SERVICE_12421] -
    incoming all time             b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
    incoming all time             b                              unexpected_error_cnt:            0
    incoming all time             hold                                    new_act_cnt:    2,992,273               new_held_cnt:            0                new_del_cnt:            0
    incoming all time             hold                                    upd_act_cnt:          518               upd_held_cnt:            0                upd_del_cnt:            0
    incoming all time             hold                           unexpected_error_cnt:            0
    incoming all time             bib                                     new_act_cnt:    2,826,193               new_held_cnt:            0                new_del_cnt:            0
    incoming all time             bib                                     upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:            0
    incoming all time             bib                            unexpected_error_cnt:            0
    incoming all time             h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
    incoming all time             h                              unexpected_error_cnt:            0
    incoming all time             TOTALS                                  new_act_cnt:    5,818,466               new_held_cnt:            0                new_del_cnt:            0
    incoming all time             TOTALS                                  upd_act_cnt:        5,979               upd_held_cnt:            0                upd_del_cnt:           53
    incoming all time             TOTALS                         unexpected_error_cnt:            0
    29 Feb 2012 12:24:33,881  INFO [SERVICE_12421] -
    outgoing 2012-02-29T18:22:18Z work                                    new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
    outgoing 2012-02-29T18:22:18Z work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z work                           upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z holdings                                new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z holdings                                upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
    outgoing 2012-02-29T18:22:18Z holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z holdings                       upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z expression                              new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
    outgoing 2012-02-29T18:22:18Z expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z expression                     upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z manifestation                           new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           13
    outgoing 2012-02-29T18:22:18Z manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z manifestation                  upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z other                                   new_act_cnt:            0               new_held_cnt:           12                new_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z TOTALS                                  new_act_cnt:            0               new_held_cnt:           12                new_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           79
    outgoing 2012-02-29T18:22:18Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing 2012-02-29T18:22:18Z TOTALS                         upd_del_prev_act_cnt:           79      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    29 Feb 2012 12:24:33,882  INFO [SERVICE_12421] -
    outgoing all time             work                                    new_act_cnt:    3,016,295               new_held_cnt:            0                new_del_cnt:            0
    outgoing all time             work                                    upd_act_cnt:        6,416               upd_held_cnt:            0                upd_del_cnt:           13
    outgoing all time             work                           upd_act_prev_act_cnt:        6,416      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing all time             work                           upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing all time             holdings                                new_act_cnt:    3,005,500               new_held_cnt:          349                new_del_cnt:            0
    outgoing all time             holdings                                upd_act_cnt:          779               upd_held_cnt:            4                upd_del_cnt:           40
    outgoing all time             holdings                       upd_act_prev_act_cnt:          749      upd_act_prev_held_cnt:           30       upd_act_prev_del_cnt:            0
    outgoing all time             holdings                      upd_held_prev_act_cnt:            4     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing all time             holdings                       upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing all time             expression                              new_act_cnt:    3,016,295               new_held_cnt:            0                new_del_cnt:            0
    outgoing all time             expression                              upd_act_cnt:        6,416               upd_held_cnt:            0                upd_del_cnt:           13
    outgoing all time             expression                     upd_act_prev_act_cnt:        6,416      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing all time             expression                     upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing all time             manifestation                           new_act_cnt:    2,826,193               new_held_cnt:            0                new_del_cnt:            0
    outgoing all time             manifestation                           upd_act_cnt:        5,461               upd_held_cnt:            0                upd_del_cnt:           13
    outgoing all time             manifestation                  upd_act_prev_act_cnt:        5,461      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing all time             manifestation                  upd_del_prev_act_cnt:           13      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing all time             other                                   new_act_cnt:            0               new_held_cnt:           12                new_del_cnt:            0
    outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            6
    outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
    outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing all time             other                          upd_del_prev_act_cnt:            6      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    outgoing all time             TOTALS                                  new_act_cnt:   11,864,283               new_held_cnt:          361                new_del_cnt:            0
    outgoing all time             TOTALS                                  upd_act_cnt:       19,072               upd_held_cnt:            4                upd_del_cnt:           85
    outgoing all time             TOTALS                         upd_act_prev_act_cnt:       19,042      upd_act_prev_held_cnt:           30       upd_act_prev_del_cnt:            0
    outgoing all time             TOTALS                        upd_held_prev_act_cnt:            4     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
    outgoing all time             TOTALS                         upd_del_prev_act_cnt:           85      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
    29 Feb 2012 12:24:49,215  INFO [SERVICE_12421] -
                     other-deleted:           6                      e-active:   3,016,279                     e-deleted:          13
                          h-active:   3,005,486                     h-deleted:          40                        h-held:         323
                          m-active:   2,826,180                     m-deleted:          13                      w-active:   3,016,279
                         w-deleted:          13
                      total-active:  11,864,224                 total-deleted:          85                    total-held:         323
```

### Counting Explained ###
In the log snippet above, notice:
```
outgoing all time             holdings                              upd_act_cnt:          779
```

This total, 779, equals the composite of the following sub-categories:

```
outgoing all time             holdings                       upd_act_prev_act_cnt:          749      upd_act_prev_held_cnt:           30       upd_act_prev_del_cnt:            0
```

Another example:
  * We received 144 generic updated active records.
  * These break down further to all 144 being updated active records replacing active records.
  * So 2 counters get updated, upd\_act\_cnt += 144 and upd\_act\_prev\_act\_cnt += 144.
  * They are both incremented as a result of a updated active record being received that replaces one that was previously active.
  * It could have been an updated active record being received that replaces one that was previously held,
  * or an updated active record received that replaces one that was previously deleted.

Another example:
  * upd\_act\_cnt:        6,755
  * upd\_act\_prev\_act\_cnt:        4,212
  * upd\_act\_prev\_held\_cnt:        2,543
  * upd\_act\_prev\_del\_cnt:            0
  * For the above, we have 6,755 upd\_act\_cnt, and that breaks down to 4,212 of 1 type and 2,543 of another.

This same system is in use throughout.

### Names of Things We Count in the Logs ###

You may notice in the service logs that the things we count are in categories and have names.  Names like 'b' and 'h' and 'bibs' and 'holdings' and 'works' and 'expressions' and 'unknown'.

These names get used when the service sets them in the proper way.

`RecordIfc` tells through comments that get/setType are key for `RecordCount` reporting. So if you want to see something besides 'Totals' and `RecordCounts.OTHER` (unknown) then the service must set the Record's type.

As explained in DCTransformationService: setting this here increments this type in the record counts when incremented in `GenericMetadataService.process()` -- else it then increments `RecordCounts.OTHER`

### Unique Cases ###

During the running of the T0 test, I noticed 21 upd\_act\_cnt records reported.  This is interesting, because all records were seen for the 1st time during t0.  The reason appears to be that these holdings records were marked HELD because they were waiting on a bib, then when the bib arrived the status changed to active.  The code in `RecordCounts.java` looks to see if the previous status was NULL, if so it KNOWS it is an active new record.  If it had a previous status of H it thinks it saw it already.

Per our design the upd\_act\_cnt counter is incremented when the holding is received.  We received a record affecting another record thus consider this an update case.

## Rules ##

One important tool to verifying record processing through the MST is rules.

As records get processed through the MST there is the option to apply rules to the records seen as they are processed by the MST.  There is a hook to check rules at the end of each stage of processing.  This feature is configurable and when enabled the rules results are logged in a separate log.

To control whether the rules are on and which services and provider the rules operate on, edit `<tomcat_working_dir>/MST-instances/MetadataServicesToolkit/install.properties`.  These are the default settings, if you need something different, uncomment the line in the file and make the change.  For instance, uncomment rule\_checking\_enabled and set it to false to turn off this feature.

```
#rule_checking_enabled=true
```

Currently, to change the rules requires changing the java code.  This shall change in a future release allowing rules to be edited via a script instead of compiled code.

Also, currently the code as compiled checks rules at services.  The released code checks rules at the end of dctoxctransformation, marcnormalization and marctoxctransformation.

For these 3 services the service retrieves the input record counts to the service and the output record counts from the service, and then applies rules to see if the output of the service makes sense.

## Initial set of rules: ##
```
message.ruleCheckingHeaderNormalization = Rules for Normalization:
message.ruleNormalizationNBIA_eq_NBOA = Normalization Bibs In Active (NBIA) = Normalization Bibs Out Active (NBOA)
message.ruleNormalizationNHIA_eq_NHOA = Normalization Holdings In Active (NHIA) = Normalization Holdings Out Active (NHOA)
message.ruleNormalizationNTIA_eq_NTOA = Normalization Total In Active (NTIA) = Normalization Total Out Active (NTOA)

message.ruleCheckingHeaderTransformation = Rules for MarcToXCTransformation:
message.ruleTransformationTBIA_eq_TMA = Transformation Bibs In Active (TBIA) = Transformation Manifestations Active (TMA)
message.ruleTransformationTBID_eq_TMD = Transformation Bibs In Deleted (TBID) = Transformation Manifestations Deleted (TMD)
message.ruleTransformationTHIA_leq_THOA_THH = Transformation Holdings In Active (THIA) <= Transformation Holdings Out Active (THOA) + Transformation Holdings Held (THH)
message.ruleTransformationTEA_eq_TWA = Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
message.ruleTransformationTWA_geq_TMA = Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
message.ruleTransformationTEA_geq_TMA = Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)

message.ruleCheckingHeaderDCTransformation = Rules for DCToXCTransformation:
message.ruleDCTransformationDCTBIA_3x_DCT_TOT= 3 * (DC Transformation Bibs In Active (DCTBIA)) = DC Transformation Totals Out Active (DCTTOA)
message.ruleDCTransformationDCTBIA_eq_DCTWOA = DC Transformation Bibs In Active (DCTBIA) = DC Transformation Works Out Active (DCTWOA)
message.ruleDCTransformationDCTBIA_eq_DCTMOA = DC Transformation Bibs In Active (DCTBIA) = DC Transformation Manifestations Out Active (DCTMOA)
message.ruleDCTransformationDCTBIA_eq_DCTEOA = DC Transformation Bibs In Active (DCTBIA) = DC Transformation Expressions Out Active (DCTEOA)

message.ruleCheckingHeaderAggregation = Rules for MarcAggregation:
message.ruleAggregationMABIA_geq_MABOA = Marc Aggregation Bibs In Active (MABIA) >= Marc Aggregation Bibs Out Active (MABOA)
message.ruleAggregationMAHIA_eq_MAHOA = Marc Aggregation Holdings In Active (MAHIA) = Marc Aggregation Holdings Out Active (MAHOA)
message.ruleAggregationMATIA_geq_MATOA = Marc Aggregation Total In Active (MATIA) >= Marc Aggregation Total Out Active (MATOA)
```

## Results ##
The MST allows for displaying record count data in a separate log, `<tomcat_working_dir>/MST-instances/MetadataServicesToolkit/logs/MST_rules_log.txt`  This log can be downloaded from the 'General' tab under 'Logs.'

### Sample Log Output ###
The log contains a header showing which rules are being applied, followed by the results.  Each rule gets displayed followed a 2nd line showing the results.
```
05 Apr 2012 14:01:40,977  INFO [SERVICE_1451] - %%%
05 Apr 2012 14:01:40,978  INFO [SERVICE_1451] - Rules for Normalization:
05 Apr 2012 14:01:40,978  INFO [SERVICE_1451] - Normalization Total In Active (NTIA) = Normalization Total Out Active (NTOA)
05 Apr 2012 14:01:40,978  INFO [SERVICE_1451] - NTIA=954, NTOA=954 ** PASS **
05 Apr 2012 14:01:40,978  INFO [SERVICE_1451] - Normalization Bibs In Active (NBIA) = Normalization Bibs Out Active (NBOA)
05 Apr 2012 14:01:40,978  INFO [SERVICE_1451] - NBIA=431, NBOA=431 ** PASS **
05 Apr 2012 14:01:40,978  INFO [SERVICE_1451] - Normalization Holdings In Active (NHIA) = Normalization Holdings Out Active (NHOA)
05 Apr 2012 14:01:40,979  INFO [SERVICE_1451] - NHIA=523, NHOA=523 ** PASS **
05 Apr 2012 14:01:40,979  INFO [SERVICE_1451] - %%%
05 Apr 2012 14:02:26,465  INFO [SERVICE_1451] - %%%
05 Apr 2012 14:02:26,465  INFO [SERVICE_1451] - Rules for MarcAggregation:
05 Apr 2012 14:02:26,466  INFO [SERVICE_1451] - Marc Aggregation Total In Active (MATIA) >= Marc Aggregation Total Out Active (MATOA)
05 Apr 2012 14:02:26,466  INFO [SERVICE_1451] - MATIA=954, MATOA=954 ** PASS **
05 Apr 2012 14:02:26,466  INFO [SERVICE_1451] - Marc Aggregation Bibs In Active (MABIA) >= Marc Aggregation Bibs Out Active (MABOA)
05 Apr 2012 14:02:26,466  INFO [SERVICE_1451] - MABIA=431, MABOA=431 ** PASS **
05 Apr 2012 14:02:26,466  INFO [SERVICE_1451] - Marc Aggregation Holdings In Active (MAHIA) = Marc Aggregation Holdings Out Active (MAHOA)
05 Apr 2012 14:02:26,466  INFO [SERVICE_1451] - MAHIA=523, MAHOA=523 ** PASS **
05 Apr 2012 14:02:26,467  INFO [SERVICE_1451] - %%%
```


## Modifying Rules ##
Currently this requires changing the Java code corresponding to the service.  In a future release this will be accessible via a script to be dynamically modifiable.