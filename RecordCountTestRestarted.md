# T0 - Initial Load  (from file harvest) #
  * You'll have to look below at the 6/28/11 data to see relevent OAI toolkit numbers, but on 8/22/11 we restarted our side, harvesting from file. 8/22/2011 t0 from file run completed, numbers:

  * as reported by 128.174.70.240
    * harvestIn file
```
incoming 2011-08-22T20:29:15Z b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T20:29:15Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-22T20:29:15Z b                              unexpected_error_cnt:            0
incoming 2011-08-22T20:29:15Z a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-22T20:29:15Z a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-22T20:29:15Z a                              unexpected_error_cnt:            0
incoming 2011-08-22T20:29:15Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T20:29:15Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-22T20:29:15Z h                              unexpected_error_cnt:            0
incoming 2011-08-22T20:29:15Z TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-22T20:29:15Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-22T20:29:15Z TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 17:33:07,657  INFO [REPOSITORY_15229] -
incoming all time             b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             a                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 17:33:42,042  INFO [REPOSITORY_15229] -
rochester_137_local:bib-active:   2,630,623rochester_137_local:hold-active:   2,794,171rochester_137_local:auth-active:   1,254,930
rochester_137_local:auth-deleted:           3
                  other-active:           0                 other-deleted:           0
                  total-active:   6,679,724                 total-deleted:           3

```

  * normalization log
```
incoming 2011-08-22T22:33:44Z b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T22:33:44Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-22T22:33:44Z b                              unexpected_error_cnt:            0
incoming 2011-08-22T22:33:44Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T22:33:44Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-22T22:33:44Z h                              unexpected_error_cnt:            0
incoming 2011-08-22T22:33:44Z other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-22T22:33:44Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-22T22:33:44Z other                          unexpected_error_cnt:            0
incoming 2011-08-22T22:33:44Z TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-22T22:33:44Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-22T22:33:44Z TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 20:20:43,427  INFO [SERVICE_17233] -
incoming all time             b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 20:20:43,428  INFO [SERVICE_17233] -
outgoing 2011-08-22T22:33:44Z b                                       new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T22:33:44Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-22T22:33:44Z b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T22:33:44Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-22T22:33:44Z h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T22:33:44Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-22T22:33:44Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T22:33:44Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-22T22:33:44Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T22:33:44Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
22 Aug 2011 20:20:43,431  INFO [SERVICE_17233] -
outgoing all time             b                                       new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
22 Aug 2011 20:20:52,359  INFO [SERVICE_17233] -
                  other-active:   1,254,930                      b-active:   2,630,622                      h-active:   2,794,171
                  total-active:   6,679,723

```

  * transformation log
```
incoming 2011-08-23T01:20:57Z hold                                    new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T01:20:57Z hold                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T01:20:57Z hold                           unexpected_error_cnt:            0
incoming 2011-08-23T01:20:57Z bib                                     new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T01:20:57Z bib                                     upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T01:20:57Z bib                            unexpected_error_cnt:            0
incoming 2011-08-23T01:20:57Z other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T01:20:57Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T01:20:57Z other                          unexpected_error_cnt:            0
incoming 2011-08-23T01:20:57Z TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T01:20:57Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T01:20:57Z TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 22:54:03,630  INFO [SERVICE_20220] -
incoming all time             hold                                    new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 22:54:03,631  INFO [SERVICE_20220] -
outgoing 2011-08-23T01:20:57Z work                                    new_act_cnt:    2,808,321               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T01:20:57Z work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T01:20:57Z work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z holdings                                new_act_cnt:    2,682,218               new_held_cnt:      120,156                new_del_cnt:            0
outgoing 2011-08-23T01:20:57Z holdings                                upd_act_cnt:          124               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T01:20:57Z holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z expression                              new_act_cnt:    2,808,321               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T01:20:57Z expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T01:20:57Z expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z manifestation                           new_act_cnt:    2,630,614               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T01:20:57Z manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T01:20:57Z manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z TOTALS                                  new_act_cnt:   10,929,474               new_held_cnt:      120,156                new_del_cnt:            0
outgoing 2011-08-23T01:20:57Z TOTALS                                  upd_act_cnt:          124               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T01:20:57Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T01:20:57Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
22 Aug 2011 22:54:03,632  INFO [SERVICE_20220] -
outgoing all time             work                                    new_act_cnt:    2,808,321               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,682,218               new_held_cnt:      120,156                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:          124               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,808,321               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,630,614               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   10,929,474               new_held_cnt:      120,156                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:          124               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
22 Aug 2011 22:54:15,471  INFO [SERVICE_20220] -
                      e-active:   2,808,321                      h-active:   2,682,342                        h-held:     120,032
                      m-active:   2,630,614                      w-active:   2,808,321
                  total-active:  10,929,598                    total-held:     120,032

```

  * rules file
```
22 Aug 2011 20:20:52,504  INFO [SERVICE_17233] - Rules for Normalization:
22 Aug 2011 20:20:52,504  INFO [SERVICE_17233] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
22 Aug 2011 20:20:52,504  INFO [SERVICE_17233] - HTA=6679724, NTA=6679723 ** FAIL **
22 Aug 2011 20:20:52,504  INFO [SERVICE_17233] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
22 Aug 2011 20:20:52,505  INFO [SERVICE_17233] - HBA=2630623, NBA=2630622 ** FAIL **
22 Aug 2011 20:20:52,505  INFO [SERVICE_17233] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
22 Aug 2011 20:20:52,505  INFO [SERVICE_17233] - HHA=2794171, NHA=2794171 ** PASS **
22 Aug 2011 20:20:52,505  INFO [SERVICE_17233] - %%%
22 Aug 2011 22:54:15,694  INFO [SERVICE_20220] - %%%
22 Aug 2011 22:54:15,695  INFO [SERVICE_20220] - Rules for Transformation:
22 Aug 2011 22:54:15,695  INFO [SERVICE_20220] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - NBA=2630622, TMA=2630614 ** FAIL **
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - NBD=0, TMD=0 ** PASS **
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - NHA=2794171, THA=2682218, THH=120156 ** PASS **
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - TEA=2808321, TWA=2808321 ** PASS **
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - TWA=2808321, TMA=2630614 ** PASS **
22 Aug 2011 22:54:15,696  INFO [SERVICE_20220] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
22 Aug 2011 22:54:15,697  INFO [SERVICE_20220] - TWA=2808321, TMA=2630614 ** PASS **
```

mysql -u root --password=root -D xc\_marcnormalization -e "select count(**) from xc\_rochester\_137\_local.records"
mysql -u root --password=root -D xc\_marcnormalization -e "select status, count(**) from xc\_rochester\_137\_local.records"
mysql -u root --password=root -D xc\_marcnormalization -e "select status, count(**) from xc\_rochester\_137\_local.records group by status order by status"
mysql -u root --password=root -D xc\_marcnormalization -e "select status, count(**) from records group by status order by status"
mysql -u root --password=root -D xc\_marcnormalization -e "select type, status, count(**) from records group by type, status order by status, status"
mysql -u root --password=root -D xc\_marctoxctransformation -e "select type, status, count(**) from records group by type, status order by status, status"
mysql -u root --password=root -D xc\_marctoxctransformation -e "select status, count(**) from records group by status order by status"**

  * database
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select count(*) from xc_rochester_137_local.records"
+----------+
| count(*) |
+----------+
|  6679727 |
+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679727 |
+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679724 |
| D      |        3 |
+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679723 |
+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| b    | A      |  2630622 |
| h    | A      |  2794171 |
| NULL | A      |  1254930 |
+------+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select type, status, count(*) from records group by type, status order by status, status"

+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| w    | A      |  2808321 |
| e    | A      |  2808321 |
| m    | A      |  2630614 |
| h    | A      |  2682342 |
| h    | H      |   120032 |
+------+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      | 10929598 |
| H      |   120032 |
+--------+----------+

```


# T1 - Update 1  (from file harvest) #

  * On 8/22/11 we restarted our side, harvesting from file. 8/23/2011 t1 from file run completed, numbers:

  * as reported by 128.174.70.240
    * harvestIn file
```
incoming 2011-08-23T18:10:35Z b                                       new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T18:10:35Z b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T18:10:35Z b                              unexpected_error_cnt:            0
incoming 2011-08-23T18:10:35Z h                                       new_act_cnt:        2,483               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T18:10:35Z h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T18:10:35Z h                              unexpected_error_cnt:            0
incoming 2011-08-23T18:10:35Z TOTALS                                  new_act_cnt:        4,938               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T18:10:35Z TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T18:10:35Z TOTALS                         unexpected_error_cnt:            0
23 Aug 2011 13:16:48,913  INFO [REPOSITORY_1336] -
incoming all time             b                                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             a                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
23 Aug 2011 13:17:24,268  INFO [REPOSITORY_1336] -
rochester_137_local:bib-active:   2,633,078rochester_137_local:hold-active:   2,796,654rochester_137_local:auth-active:   1,254,930
rochester_137_local:auth-deleted:           3
                  other-active:           0                 other-deleted:           0
                  total-active:   6,684,662                 total-deleted:           3
```

  * normalization file
```
incoming 2011-08-23T18:18:33Z b                                       new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T18:18:33Z b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T18:18:33Z b                              unexpected_error_cnt:            0
incoming 2011-08-23T18:18:33Z h                                       new_act_cnt:        2,483               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T18:18:33Z h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T18:18:33Z h                              unexpected_error_cnt:            0
incoming 2011-08-23T18:18:33Z TOTALS                                  new_act_cnt:        4,938               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-23T18:18:33Z TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-23T18:18:33Z TOTALS                         unexpected_error_cnt:            0
23 Aug 2011 13:21:35,741  INFO [SERVICE_13317] -
incoming all time             b                                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
23 Aug 2011 13:21:35,742  INFO [SERVICE_13317] -
outgoing 2011-08-23T18:18:33Z b                                       new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:18:33Z b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:18:33Z b                              upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:18:33Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:18:33Z b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:18:33Z h                                       new_act_cnt:        2,483               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:18:33Z h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:18:33Z h                              upd_act_prev_act_cnt:       10,016      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:18:33Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:18:33Z h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:18:33Z TOTALS                                  new_act_cnt:        4,938               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:18:33Z TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:18:33Z TOTALS                         upd_act_prev_act_cnt:       20,029      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:18:33Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:18:33Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
23 Aug 2011 13:21:35,743  INFO [SERVICE_13317] -
outgoing all time             b                                       new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             b                              upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             h                              upd_act_prev_act_cnt:       10,016      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       20,029      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
23 Aug 2011 13:21:43,180  INFO [SERVICE_13317] -
                  other-active:   1,254,930                      b-active:   2,633,077                      h-active:   2,796,654
                  total-active:   6,684,661
```

  * transformation file
```
incoming all time             hold                                    new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
23 Aug 2011 13:28:37,509  INFO [SERVICE_13321] -
outgoing 2011-08-23T18:23:55Z work                                    new_act_cnt:        2,619               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                                new_act_cnt:        2,471               new_held_cnt:           12                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                                upd_act_cnt:       10,014               upd_held_cnt:            2                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                              new_act_cnt:        2,619               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                           new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                                  new_act_cnt:       10,164               new_held_cnt:           12                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                                  upd_act_cnt:       40,111               upd_held_cnt:            2                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
23 Aug 2011 13:28:37,510  INFO [SERVICE_13321] -
outgoing all time             work                                    new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,684,689               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:       10,138               upd_held_cnt:            2                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,633,069               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   10,939,638               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       40,235               upd_held_cnt:            2                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
23 Aug 2011 13:28:51,101  INFO [SERVICE_13321] -
                      e-active:   2,810,940                      h-active:   2,684,813                        h-held:     120,044
                      m-active:   2,633,069                      w-active:   2,810,940
                  total-active:  10,939,762                    total-held:     120,044
incoming all time             hold                                    new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
23 Aug 2011 13:28:37,509  INFO [SERVICE_13321] -
outgoing 2011-08-23T18:23:55Z work                                    new_act_cnt:        2,619               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                                new_act_cnt:        2,471               new_held_cnt:           12                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                                upd_act_cnt:       10,014               upd_held_cnt:            2                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                              new_act_cnt:        2,619               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                           new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                                  new_act_cnt:       10,164               new_held_cnt:           12                new_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                                  upd_act_cnt:       40,111               upd_held_cnt:            2                upd_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing 2011-08-23T18:23:55Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
23 Aug 2011 13:28:37,510  INFO [SERVICE_13321] -
outgoing all time             work                                    new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,684,689               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:       10,138               upd_held_cnt:            2                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,633,069               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   10,939,638               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       40,235               upd_held_cnt:            2                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
23 Aug 2011 13:28:51,101  INFO [SERVICE_13321] -
                      e-active:   2,810,940                      h-active:   2,684,813                        h-held:     120,044
                      m-active:   2,633,069                      w-active:   2,810,940
                  total-active:  10,939,762                    total-held:     120,044
```

  * rules file
```
23 Aug 2011 13:21:43,192  INFO [SERVICE_13317] - Rules for Normalization:
23 Aug 2011 13:21:43,192  INFO [SERVICE_13317] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
23 Aug 2011 13:21:43,192  INFO [SERVICE_13317] - HTA=6684662, NTA=6684661 ** FAIL **
23 Aug 2011 13:21:43,192  INFO [SERVICE_13317] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
23 Aug 2011 13:21:43,192  INFO [SERVICE_13317] - HBA=2633078, NBA=2633077 ** FAIL **
23 Aug 2011 13:21:43,193  INFO [SERVICE_13317] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
23 Aug 2011 13:21:43,193  INFO [SERVICE_13317] - HHA=2796654, NHA=2796654 ** PASS **
23 Aug 2011 13:21:43,193  INFO [SERVICE_13317] - %%%
23 Aug 2011 13:28:51,115  INFO [SERVICE_13321] - %%%
23 Aug 2011 13:28:51,115  INFO [SERVICE_13321] - Rules for Transformation:
23 Aug 2011 13:28:51,115  INFO [SERVICE_13321] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
23 Aug 2011 13:28:51,115  INFO [SERVICE_13321] - NBA=2633077, TMA=2633069 ** FAIL **
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - NBD=0, TMD=0 ** PASS **
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - NHA=2796654, THA=2684689, THH=120168 ** PASS **
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - TEA=2810940, TWA=2810940 ** PASS **
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - TWA=2810940, TMA=2633069 ** PASS **
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
23 Aug 2011 13:28:51,116  INFO [SERVICE_13321] - TWA=2810940, TMA=2633069 ** PASS **
```


# T2 - Delete 1  (from file harvest) #

  * On 8/22/11 we restarted our side, harvesting from file. 8/24/2011 t2 from file run completed, numbers:

  * as reported by 128.174.70.240
    * harvestIn file
```
incoming 2011-08-24T11:25:50Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:           28
incoming 2011-08-24T11:25:50Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           28
incoming 2011-08-24T11:25:50Z b                              unexpected_error_cnt:            0
incoming 2011-08-24T11:25:50Z a                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:           10
incoming 2011-08-24T11:25:50Z a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-24T11:25:50Z a                              unexpected_error_cnt:            0
incoming 2011-08-24T11:25:50Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:           59
incoming 2011-08-24T11:25:50Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
incoming 2011-08-24T11:25:50Z h                              unexpected_error_cnt:            0
incoming 2011-08-24T11:25:50Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:           97
incoming 2011-08-24T11:25:50Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           68
incoming 2011-08-24T11:25:50Z TOTALS                         unexpected_error_cnt:            0
24 Aug 2011 06:27:06,741  INFO [REPOSITORY_6420] -
incoming all time             b                                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:           28
incoming all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:           28
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:           13
incoming all time             a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             a                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:           59
incoming all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:           40
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:          100
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:           68
incoming all time             TOTALS                         unexpected_error_cnt:            0
24 Aug 2011 06:27:46,069  INFO [REPOSITORY_6420] -
rochester_137_local:bib-active:   2,633,050rochester_137_local:bib-deleted:          56rochester_137_local:hold-active:   2,796,614
rochester_137_local:hold-deleted:          99rochester_137_local:auth-active:   1,254,930rochester_137_local:auth-deleted:          13
                  other-active:           0                 other-deleted:           0
                  total-active:   6,684,594                 total-deleted:         168
```

  * normalization file
```
incoming 2011-08-24T11:29:19Z other                                   new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:           97
incoming 2011-08-24T11:29:19Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           68
incoming 2011-08-24T11:29:19Z other                          unexpected_error_cnt:            0
incoming 2011-08-24T11:29:19Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:           97
incoming 2011-08-24T11:29:19Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           68
incoming 2011-08-24T11:29:19Z TOTALS                         unexpected_error_cnt:            0
24 Aug 2011 06:30:46,680  INFO [SERVICE_6427] -
incoming all time             b                                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:          100
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           68
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:          100
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:           68
incoming all time             TOTALS                         unexpected_error_cnt:            0
24 Aug 2011 06:30:46,682  INFO [SERVICE_6427] -
outgoing 2011-08-24T11:29:19Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-24T11:29:19Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           28
outgoing 2011-08-24T11:29:19Z b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:29:19Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:29:19Z b                              upd_del_prev_act_cnt:           28      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-24T11:29:19Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-24T11:29:19Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
outgoing 2011-08-24T11:29:19Z h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:29:19Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:29:19Z h                              upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-24T11:29:19Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-24T11:29:19Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           68
outgoing 2011-08-24T11:29:19Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:29:19Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:29:19Z TOTALS                         upd_del_prev_act_cnt:           68      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
24 Aug 2011 06:30:46,682  INFO [SERVICE_6427] -
outgoing all time             b                                       new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:           28
outgoing all time             b                              upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:           28      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:           40
outgoing all time             h                              upd_act_prev_act_cnt:       10,016      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:           68
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       20,029      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:           68      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
24 Aug 2011 06:30:54,372  INFO [SERVICE_6427] -
                  other-active:   1,254,930                      b-active:   2,633,049                     b-deleted:          28
                      h-active:   2,796,614                     h-deleted:          40
                  total-active:   6,684,593                 total-deleted:          68
```

  * transformation file
```
24 Aug 2011 06:35:46,331  INFO [SERVICE_6430] -
incoming 2011-08-24T11:33:31Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-24T11:33:31Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           28
incoming 2011-08-24T11:33:31Z b                              unexpected_error_cnt:            0
incoming 2011-08-24T11:33:31Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-24T11:33:31Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
incoming 2011-08-24T11:33:31Z h                              unexpected_error_cnt:            0
incoming 2011-08-24T11:33:31Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-24T11:33:31Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           68
incoming 2011-08-24T11:33:31Z TOTALS                         unexpected_error_cnt:            0
24 Aug 2011 06:35:46,344  INFO [SERVICE_6430] -
incoming all time             b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           28
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             hold                                    new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:           68
incoming all time             TOTALS                         unexpected_error_cnt:            0
24 Aug 2011 06:35:46,348  INFO [SERVICE_6430] -
outgoing 2011-08-24T11:33:31Z work                                    new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-24T11:33:31Z work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           28
outgoing 2011-08-24T11:33:31Z work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z work                           upd_del_prev_act_cnt:           28      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z holdings                                new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-24T11:33:31Z holdings                                upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           40
outgoing 2011-08-24T11:33:31Z holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z holdings                       upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z expression                              new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-24T11:33:31Z expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           28
outgoing 2011-08-24T11:33:31Z expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z expression                     upd_del_prev_act_cnt:           28      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z manifestation                           new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-24T11:33:31Z manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:           28
outgoing 2011-08-24T11:33:31Z manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z manifestation                  upd_del_prev_act_cnt:           28      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z other                                   new_act_cnt:            0               new_held_cnt:           28                new_del_cnt:            0
outgoing 2011-08-24T11:33:31Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-24T11:33:31Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z TOTALS                                  new_act_cnt:            0               new_held_cnt:           28                new_del_cnt:            0
outgoing 2011-08-24T11:33:31Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          124
outgoing 2011-08-24T11:33:31Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-24T11:33:31Z TOTALS                         upd_del_prev_act_cnt:          124      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
24 Aug 2011 06:35:46,349  INFO [SERVICE_6430] -
outgoing all time             work                                    new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:           28
outgoing all time             work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:           28      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,684,689               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:       10,138               upd_held_cnt:            2                upd_del_cnt:           40
outgoing all time             holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:           40      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:           28
outgoing all time             expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:           28      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,633,069               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:           28
outgoing all time             manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:           28      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:            0               new_held_cnt:           28                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   10,939,638               new_held_cnt:      120,196                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       40,235               upd_held_cnt:            2                upd_del_cnt:          124
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:          124      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
24 Aug 2011 06:36:00,514  INFO [SERVICE_6430] -
                      e-active:   2,810,912                     e-deleted:          28                      h-active:   2,684,773
                     h-deleted:          40                        h-held:     120,044                      m-active:   2,633,041
                     m-deleted:          28                      w-active:   2,810,912                     w-deleted:          28
                  total-active:  10,939,638                 total-deleted:         124                    total-held:     120,044
```

  * rules file
```
24 Aug 2011 06:30:54,431  INFO [SERVICE_6427] - Rules for Normalization:
24 Aug 2011 06:30:54,431  INFO [SERVICE_6427] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
24 Aug 2011 06:30:54,431  INFO [SERVICE_6427] - HTA=6684662, NTA=6684661 ** FAIL **
24 Aug 2011 06:30:54,431  INFO [SERVICE_6427] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
24 Aug 2011 06:30:54,431  INFO [SERVICE_6427] - HBA=2633078, NBA=2633077 ** FAIL **
24 Aug 2011 06:30:54,431  INFO [SERVICE_6427] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
24 Aug 2011 06:30:54,431  INFO [SERVICE_6427] - HHA=2796654, NHA=2796654 ** PASS **
24 Aug 2011 06:30:54,431  INFO [SERVICE_6427] - %%%
24 Aug 2011 06:36:00,524  INFO [SERVICE_6430] - %%%
24 Aug 2011 06:36:00,526  INFO [SERVICE_6430] - Rules for Transformation:
24 Aug 2011 06:36:00,526  INFO [SERVICE_6430] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
24 Aug 2011 06:36:00,526  INFO [SERVICE_6430] - NBA=2633077, TMA=2633069 ** FAIL **
24 Aug 2011 06:36:00,526  INFO [SERVICE_6430] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
24 Aug 2011 06:36:00,526  INFO [SERVICE_6430] - NBD=0, TMD=0 ** PASS **
24 Aug 2011 06:36:00,527  INFO [SERVICE_6430] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
24 Aug 2011 06:36:00,527  INFO [SERVICE_6430] - NHA=2796654, THA=2684689, THH=120168 ** PASS **
24 Aug 2011 06:36:00,527  INFO [SERVICE_6430] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
24 Aug 2011 06:36:00,527  INFO [SERVICE_6430] - TEA=2810940, TWA=2810940 ** PASS **
24 Aug 2011 06:36:00,527  INFO [SERVICE_6430] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
24 Aug 2011 06:36:00,527  INFO [SERVICE_6430] - TWA=2810940, TMA=2633069 ** PASS **
24 Aug 2011 06:36:00,527  INFO [SERVICE_6430] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
24 Aug 2011 06:36:00,527  INFO [SERVICE_6430] - TWA=2810940, TMA=2633069 ** PASS **
```

  * database
```

```








# T2 - Delete 1  (from file harvest) #

  * On 8/18/11 we restarted our side, harvesting from file. 8/22/2011 t2 from file run completed, numbers:

  * relevant OAI Toolkit log file (ip address 137)
    * librarian\_load.log: (7/11/11 date)
      * note that because our number of deletes in MST doesn't correlate to OAI toolkit expected numbers, I will restart, again!  Note that we do correlate with our file harvest numbers, now moved to /xc/file\_harvest/archive, both 137\_delete\_1 and 137\_delete\_2 contain 2272 records
```
cd /xc/file_harvest/archive/137_delete_2; grep -c "<record>" *
```

```

INFO  -  *********** START OF LOAD PROCESS ************

INFO  - [LIB] Load started at 2011/07/11 09:10:57
INFO  - [LIB] Start loading of MARCXML files from modifiedxml


INFO  - [LIB] Import statistics for deleted.auth.marc2011-Mar-05.xml: created 10, updated: 0, skipped: 0, invalid: 0, deleted: 0, bib: 0, auth: 10, holdings: 0 records. Invalid files: 0. It took 00:00:00.480. checkTime: 00
:00:00.341. insertTime: 00:00:00.034. others: 00:00:00.106


INFO  - [LIB] Import statistics for deleted.bib.marc2011-Mar-05.xml: created 28, updated: 0, skipped: 0, invalid: 0, deleted: 28, bib: 28, auth: 0, holdings: 0 records. Invalid files: 0. It took 00:00:02.051. checkTime: 00
:00:00.288. insertTime: 00:00:00.035. others: 00:00:01.728


INFO  - [LIB] Import statistics for deleted.mfhd.marc2011-Mar-05.xml: created 59, updated: 0, skipped: 0, invalid: 0, deleted: 40, bib: 0, auth: 0, holdings: 59 records. Invalid files: 0. It took 00:00:01.352. checkTime: 0
0:00:00.380. insertTime: 00:00:00.031. others: 00:00:00.941


INFO  - [LIB] Load completed at 2011/07/11 09:11:03
INFO  -  *********** END OF LOAD PROCESS ************

INFO  - [LIB] Import statistics summary: created 97, updated: 0, skipped: 0, invalid: 0, deleted: 68, bib: 28, auth: 10, holdings: 59 records. Invalid files: 0. It took 00:00:05.206. checkTime: 00:00:01.009. insertTime: 00
:00:00.100. others: 00:00:04.099

INFO  - [LIB] Import finished
INFO  -  *********** START OF LOAD PROCESS ************

INFO  - [LIB] Load started at 2011/07/11 10:32:25
INFO  - [LIB] Start loading of MARCXML files from modifiedxml


INFO  - [LIB] Import statistics for deleted.auth.marc2011-Mar-05.xml: created 0, updated: 0, skipped: 0, invalid: 0, deleted: 10, bib: 0, auth: 0, holdings: 0 records. Invalid files: 0. It took 00:00:00.907. checkTime: 00:
00:00.334. insertTime: 00:00:00.026. others: 00:00:00.547


INFO  - [LIB] Import statistics for deleted.bib.marc2011-Mar-05.xml: created 0, updated: 0, skipped: 0, invalid: 0, deleted: 56, bib: 0, auth: 0, holdings: 0 records. Invalid files: 0. It took 00:00:01.027. checkTime: 00:0
0:00.281. insertTime: 00:00:00.033. others: 00:00:00.713


INFO  - [LIB] Import statistics for deleted.mfhd.marc2011-Mar-05.xml: created 0, updated: 0, skipped: 0, invalid: 0, deleted: 99, bib: 0, auth: 0, holdings: 0 records. Invalid files: 0. It took 00:00:01.192. checkTime: 00:
00:00.421. insertTime: 00:00:00.026. others: 00:00:00.745


INFO  - [LIB] Load completed at 2011/07/11 10:32:30
INFO  -  *********** END OF LOAD PROCESS ************

INFO  - [LIB] Import statistics summary: created 0, updated: 0, skipped: 0, invalid: 0, deleted: 165, bib: 0, auth: 0, holdings: 0 records. Invalid files: 0. It took 00:00:04.366. checkTime: 00:00:01.036. insertTime: 00:00
:00.085. others: 00:00:03.246
```

  * as reported by 128.174.70.240
    * harvestIn file

```
incoming 2011-08-22T18:37:49Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:        1,019
incoming 2011-08-22T18:37:49Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          770
incoming 2011-08-22T18:37:49Z b                              unexpected_error_cnt:            0
incoming 2011-08-22T18:37:49Z a                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T18:37:49Z a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            1
incoming 2011-08-22T18:37:49Z a                              unexpected_error_cnt:            0
incoming 2011-08-22T18:37:49Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:           80
incoming 2011-08-22T18:37:49Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          402
incoming 2011-08-22T18:37:49Z h                              unexpected_error_cnt:            0
incoming 2011-08-22T18:37:49Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:        1,099
incoming 2011-08-22T18:37:49Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:        1,173
incoming 2011-08-22T18:37:49Z TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 13:39:48,310  INFO [REPOSITORY_13233] -
incoming all time             b                                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:        1,019
incoming all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:          770
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            1
incoming all time             a                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:           80
incoming all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:          402
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:        1,102
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:        1,173
incoming all time             TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 13:40:29,801  INFO [REPOSITORY_13233] -
rochester_137_local:bib-active:   2,632,308rochester_137_local:bib-deleted:       1,789rochester_137_local:hold-active:   2,796,252
rochester_137_local:hold-deleted:         482rochester_137_local:auth-active:   1,254,929rochester_137_local:auth-deleted:           4
                  other-active:           0                 other-deleted:           0
                  total-active:   6,683,489                 total-deleted:       2,275
```

  * marcnormalization
```
incoming 2011-08-22T18:41:50Z other                                   new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:        1,099
incoming 2011-08-22T18:41:50Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:        1,173
incoming 2011-08-22T18:41:50Z other                          unexpected_error_cnt:            0
incoming 2011-08-22T18:41:50Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:        1,099
incoming 2011-08-22T18:41:50Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:        1,173
incoming 2011-08-22T18:41:50Z TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 13:44:12,898  INFO [SERVICE_13240] -
incoming all time             b                                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:        1,102
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:        1,173
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:        1,102
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:        1,173
incoming all time             TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 13:44:12,899  INFO [SERVICE_13240] -
outgoing 2011-08-22T18:41:50Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T18:41:50Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          770
outgoing 2011-08-22T18:41:50Z b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z b                              upd_del_prev_act_cnt:          770      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T18:41:50Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          402
outgoing 2011-08-22T18:41:50Z h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z h                              upd_del_prev_act_cnt:          402      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z other                                   new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T18:41:50Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            1
outgoing 2011-08-22T18:41:50Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z other                          upd_del_prev_act_cnt:            1      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T18:41:50Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:        1,173
outgoing 2011-08-22T18:41:50Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:41:50Z TOTALS                         upd_del_prev_act_cnt:        1,173      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
22 Aug 2011 13:44:12,899  INFO [SERVICE_13240] -
outgoing all time             b                                       new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:          770
outgoing all time             b                              upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:          770      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:          402
outgoing all time             h                              upd_act_prev_act_cnt:       10,016      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:          402      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            1
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            1      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:        1,173
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       20,029      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:        1,173      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
22 Aug 2011 13:44:22,216  INFO [SERVICE_13240] -
                  other-active:   1,254,929                 other-deleted:           1                      b-active:   2,632,307
                     b-deleted:         770                      h-active:   2,796,252                     h-deleted:         402
                  total-active:   6,683,488                 total-deleted:       1,173
```

  * marctoxctransformation
```
incoming 2011-08-22T18:46:47Z b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T18:46:47Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          770
incoming 2011-08-22T18:46:47Z b                              unexpected_error_cnt:            0
incoming 2011-08-22T18:46:47Z h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T18:46:47Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          402
incoming 2011-08-22T18:46:47Z h                              unexpected_error_cnt:            0
incoming 2011-08-22T18:46:47Z other                                   new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T18:46:47Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            1
incoming 2011-08-22T18:46:47Z other                          unexpected_error_cnt:            0
incoming 2011-08-22T18:46:47Z TOTALS                                  new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-22T18:46:47Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:        1,173
incoming 2011-08-22T18:46:47Z TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 13:50:37,908  INFO [SERVICE_13244] -
incoming all time             b                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          770
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             hold                                    new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          402
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            1
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:        1,173
incoming all time             TOTALS                         unexpected_error_cnt:            0
22 Aug 2011 13:50:37,909  INFO [SERVICE_13244] -
outgoing 2011-08-22T18:46:47Z work                                    new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T18:46:47Z work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          770
outgoing 2011-08-22T18:46:47Z work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z work                           upd_del_prev_act_cnt:          770      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z holdings                                new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T18:46:47Z holdings                                upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          409
outgoing 2011-08-22T18:46:47Z holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z holdings                       upd_del_prev_act_cnt:          409      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z expression                              new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T18:46:47Z expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          770
outgoing 2011-08-22T18:46:47Z expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z expression                     upd_del_prev_act_cnt:          770      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z manifestation                           new_act_cnt:            0               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-22T18:46:47Z manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:          770
outgoing 2011-08-22T18:46:47Z manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z manifestation                  upd_del_prev_act_cnt:          770      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z other                                   new_act_cnt:            0               new_held_cnt:          759                new_del_cnt:            0
outgoing 2011-08-22T18:46:47Z other                                   upd_act_cnt:            0               upd_held_cnt:            7                upd_del_cnt:            0
outgoing 2011-08-22T18:46:47Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z other                         upd_held_prev_act_cnt:            7     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z TOTALS                                  new_act_cnt:            0               new_held_cnt:          759                new_del_cnt:            0
outgoing 2011-08-22T18:46:47Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            7                upd_del_cnt:        2,719
outgoing 2011-08-22T18:46:47Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z TOTALS                        upd_held_prev_act_cnt:            7     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-22T18:46:47Z TOTALS                         upd_del_prev_act_cnt:        2,719      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
22 Aug 2011 13:50:37,910  INFO [SERVICE_13244] -
outgoing all time             work                                    new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:          770
outgoing all time             work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:          770      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,684,689               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:       10,138               upd_held_cnt:            2                upd_del_cnt:          409
outgoing all time             holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:          409      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:          770
outgoing all time             expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:          770      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,633,069               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:          770
outgoing all time             manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:          770      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:            0               new_held_cnt:          759                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            7                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            7     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   10,939,638               new_held_cnt:      120,927                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       40,235               upd_held_cnt:            9                upd_del_cnt:        2,719
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            7     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:        2,719      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
22 Aug 2011 13:50:49,682  INFO [SERVICE_13244] -
                    other-held:         410                      e-active:   2,810,170                     e-deleted:         770
                      h-active:   2,684,001                     h-deleted:         402                        h-held:     120,044
                      m-active:   2,632,299                     m-deleted:         770                      w-active:   2,810,170
                     w-deleted:         770
                  total-active:  10,936,640                 total-deleted:       2,712                    total-held:     120,454
```

  * rules file
```
22 Aug 2011 13:44:22,225  INFO [SERVICE_13240] - Rules for Normalization:
22 Aug 2011 13:44:22,225  INFO [SERVICE_13240] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
22 Aug 2011 13:44:22,225  INFO [SERVICE_13240] - HTA=6684662, NTA=6684661 ** FAIL **
22 Aug 2011 13:44:22,225  INFO [SERVICE_13240] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
22 Aug 2011 13:44:22,225  INFO [SERVICE_13240] - HBA=2633078, NBA=2633077 ** FAIL **
22 Aug 2011 13:44:22,225  INFO [SERVICE_13240] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
22 Aug 2011 13:44:22,225  INFO [SERVICE_13240] - HHA=2796654, NHA=2796654 ** PASS **
22 Aug 2011 13:44:22,225  INFO [SERVICE_13240] - %%%
22 Aug 2011 13:50:49,691  INFO [SERVICE_13244] - %%%
22 Aug 2011 13:50:49,692  INFO [SERVICE_13244] - Rules for Transformation:
22 Aug 2011 13:50:49,692  INFO [SERVICE_13244] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
22 Aug 2011 13:50:49,692  INFO [SERVICE_13244] - NBA=2633077, TMA=2633069 ** FAIL **
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - NBD=0, TMD=0 ** PASS **
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - NHA=2796654, THA=2684689, THH=120168 ** PASS **
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - TEA=2810940, TWA=2810940 ** PASS **
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - TWA=2810940, TMA=2633069 ** PASS **
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
22 Aug 2011 13:50:49,693  INFO [SERVICE_13244] - TWA=2810940, TMA=2633069 ** PASS **
```

# T1 - Update 1  (from file harvest) #

  * On 8/18/11 we restarted our side, harvesting from file. 8/19/2011 t1 from file run completed, numbers:

  * relevant OAI Toolkit log file (ip address 137)
    * librarian\_load.log.2011-06-30:
```

INFO  - [LIB] Import statistics summary: created 4938, updated: 20029, skipped: 0, invalid: 12, deleted: 0, bib: 2455, auth: 0, holdings: 2483 records. Invalid files: 0.
```
  * as reported by 128.174.70.240
    * harvestIn file

```
incoming 2011-08-19T16:15:26Z b                                       new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:15:26Z b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:15:26Z b                              unexpected_error_cnt:            0
incoming 2011-08-19T16:15:26Z h                                       new_act_cnt:        2,483               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:15:26Z h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:15:26Z h                              unexpected_error_cnt:            0
incoming 2011-08-19T16:15:26Z TOTALS                                  new_act_cnt:        4,938               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:15:26Z TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:15:26Z TOTALS                         unexpected_error_cnt:            0
19 Aug 2011 11:21:53,307  INFO [REPOSITORY_11611] -
incoming all time             b                                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             a                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
19 Aug 2011 11:22:25,873  INFO [REPOSITORY_11611] -
rochester_137_local:bib-active:   2,633,078rochester_137_local:hold-active:   2,796,654rochester_137_local:auth-active:   1,254,930
rochester_137_local:auth-deleted:           3
                  other-active:           0                 other-deleted:           0
                  total-active:   6,684,662                 total-deleted:           3
```

  * marcnormalization
```
incoming 2011-08-19T16:23:41Z b                                       new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:23:41Z b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:23:41Z b                              unexpected_error_cnt:            0
incoming 2011-08-19T16:23:41Z h                                       new_act_cnt:        2,483               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:23:41Z h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:23:41Z h                              unexpected_error_cnt:            0
incoming 2011-08-19T16:23:41Z TOTALS                                  new_act_cnt:        4,938               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:23:41Z TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:23:41Z TOTALS                         unexpected_error_cnt:            0
19 Aug 2011 11:26:16,785  INFO [SERVICE_11622] -
incoming all time             b                                       new_act_cnt:    2,633,078               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,662               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
19 Aug 2011 11:26:16,786  INFO [SERVICE_11622] -
outgoing 2011-08-19T16:23:41Z b                                       new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-19T16:23:41Z b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-19T16:23:41Z b                              upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-19T16:23:41Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-19T16:23:41Z b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-19T16:23:41Z h                                       new_act_cnt:        2,483               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-19T16:23:41Z h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-19T16:23:41Z h                              upd_act_prev_act_cnt:       10,016      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-19T16:23:41Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-19T16:23:41Z h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-19T16:23:41Z TOTALS                                  new_act_cnt:        4,938               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-19T16:23:41Z TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-19T16:23:41Z TOTALS                         upd_act_prev_act_cnt:       20,029      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-19T16:23:41Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-19T16:23:41Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
19 Aug 2011 11:26:16,787  INFO [SERVICE_11622] -
outgoing all time             b                                       new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             b                              upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             h                              upd_act_prev_act_cnt:       10,016      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       20,029      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
19 Aug 2011 11:26:24,268  INFO [SERVICE_11622] -
                  other-active:   1,254,930                      b-active:   2,633,077                      h-active:   2,796,654
                  total-active:   6,684,661
```
  * transformation
```
incoming 2011-08-19T16:28:20Z hold                                    new_act_cnt:        2,483               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:28:20Z hold                                    upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:28:20Z hold                           unexpected_error_cnt:            0
incoming 2011-08-19T16:28:20Z bib                                     new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:28:20Z bib                                     upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:28:20Z bib                            unexpected_error_cnt:            0
incoming 2011-08-19T16:28:20Z TOTALS                                  new_act_cnt:        4,938               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-19T16:28:20Z TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-19T16:28:20Z TOTALS                         unexpected_error_cnt:            0
19 Aug 2011 11:32:58,047  INFO [SERVICE_11626] -
incoming all time             hold                                    new_act_cnt:    2,796,654               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:       10,016               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,633,077               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,684,661               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:       20,029               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
19 Aug 2011 11:32:58,048  INFO [SERVICE_11626] -
outgoing 2011-08-19T16:28:20Z work                                    new_act_cnt:        2,619               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-19T16:28:20Z work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-19T16:28:20Z work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z holdings                                new_act_cnt:        2,471               new_held_cnt:           12                new_del_cnt:            0
outgoing 2011-08-19T16:28:20Z holdings                                upd_act_cnt:       10,014               upd_held_cnt:            2                upd_del_cnt:            0
outgoing 2011-08-19T16:28:20Z holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z expression                              new_act_cnt:        2,619               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-19T16:28:20Z expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-19T16:28:20Z expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z manifestation                           new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-19T16:28:20Z manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-19T16:28:20Z manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z TOTALS                                  new_act_cnt:       10,164               new_held_cnt:           12                new_del_cnt:            0
outgoing 2011-08-19T16:28:20Z TOTALS                                  upd_act_cnt:       40,111               upd_held_cnt:            2                upd_del_cnt:            0
outgoing 2011-08-19T16:28:20Z TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing 2011-08-19T16:28:20Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
19 Aug 2011 11:32:58,049  INFO [SERVICE_11626] -
outgoing all time             work                                    new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,684,689               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:       10,138               upd_held_cnt:            2                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:       10,014      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,810,940               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:       10,042               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:       10,042      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,633,069               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:       10,013               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:       10,013      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   10,939,638               new_held_cnt:      120,168                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:       40,235               upd_held_cnt:            2                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:       40,111      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            2      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
19 Aug 2011 11:33:11,565  INFO [SERVICE_11626] -
                      e-active:   2,810,940                      h-active:   2,684,813                        h-held:     120,044
                      m-active:   2,633,069                      w-active:   2,810,940
                  total-active:  10,939,762                    total-held:     120,044
```


  * rules file
```
19 Aug 2011 11:26:24,283  INFO [SERVICE_11622] - Rules for Normalization:
19 Aug 2011 11:26:24,283  INFO [SERVICE_11622] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
19 Aug 2011 11:26:24,283  INFO [SERVICE_11622] - HTA=6684662, NTA=6684661 ** FAIL **
19 Aug 2011 11:26:24,283  INFO [SERVICE_11622] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
19 Aug 2011 11:26:24,283  INFO [SERVICE_11622] - HBA=2633078, NBA=2633077 ** FAIL **
19 Aug 2011 11:26:24,284  INFO [SERVICE_11622] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
19 Aug 2011 11:26:24,284  INFO [SERVICE_11622] - HHA=2796654, NHA=2796654 ** PASS **
19 Aug 2011 11:26:24,284  INFO [SERVICE_11622] - %%%
19 Aug 2011 11:33:11,591  INFO [SERVICE_11626] - %%%
19 Aug 2011 11:33:11,598  INFO [SERVICE_11626] - Rules for Transformation:
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - NBA=2633077, TMA=2633069 ** FAIL **
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - NBD=0, TMD=0 ** PASS **
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - NHA=2796654, THA=2684689, THH=120168 ** PASS **
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - TEA=2810940, TWA=2810940 ** PASS **
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
19 Aug 2011 11:33:11,599  INFO [SERVICE_11626] - TWA=2810940, TMA=2633069 ** PASS **
19 Aug 2011 11:33:11,600  INFO [SERVICE_11626] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
19 Aug 2011 11:33:11,600  INFO [SERVICE_11626] - TWA=2810940, TMA=2633069 ** PASS **
```

  * database
```
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ mysql -u root --password=root -D xc_marcnormalization -e "select count(*) from xc_rochester_137_local.records"
+----------+
| count(*) |
+----------+
|  6684665 |
+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ mysql -u root --password=root -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6684665 |
+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ mysql -u root --password=root -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6684662 |
| D      |        3 |
+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ mysql -u root --password=root -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6684661 |
+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ mysql -u root --password=root -D xc_marcnormalization -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| b    | A      |  2633077 |
| h    | A      |  2796654 |
| NULL | A      |  1254930 |
+------+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ mysql -u root --password=root -D xc_marctoxctransformation -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| w    | A      |  2810940 |
| e    | A      |  2810940 |
| m    | A      |  2633069 |
| h    | A      |  2684813 |
| h    | H      |   120044 |
+------+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ mysql -u root --password=root -D xc_marctoxctransformation -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      | 10939762 |
| H      |   120044 |
+--------+----------+
```


# T0 - Initial Load  (from file harvest) #
  * You'll have to look below at the 6/28/11 data to see relevent OAI toolkit numbers, but on 8/18/11 we restarted our side, harvesting from file. 8/18/2011 t0 from file run completed, numbers:

  * as reported by 128.174.70.240
    * harvestIn file
```
incoming 2011-08-18T20:06:42Z b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-18T20:06:42Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-18T20:06:42Z b                              unexpected_error_cnt:            0
incoming 2011-08-18T20:06:42Z a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-18T20:06:42Z a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-18T20:06:42Z a                              unexpected_error_cnt:            0
incoming 2011-08-18T20:06:42Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-18T20:06:42Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-18T20:06:42Z h                              unexpected_error_cnt:            0
incoming 2011-08-18T20:06:42Z TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-18T20:06:42Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-18T20:06:42Z TOTALS                         unexpected_error_cnt:            0
18 Aug 2011 16:59:55,985  INFO [REPOSITORY_1556] -
incoming all time             b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             a                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
18 Aug 2011 17:00:27,142  INFO [REPOSITORY_1556] -
rochester_137_local:bib-active:   2,630,623rochester_137_local:hold-active:   2,794,171rochester_137_local:auth-active:   1,254,930
rochester_137_local:auth-deleted:           3
                  other-active:           0                 other-deleted:           0
                  total-active:   6,679,724                 total-deleted:           3
```

  * normalization log
```
incoming 2011-08-18T22:00:30Z b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-18T22:00:30Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-18T22:00:30Z b                              unexpected_error_cnt:            0
incoming 2011-08-18T22:00:30Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-18T22:00:30Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-18T22:00:30Z h                              unexpected_error_cnt:            0
incoming 2011-08-18T22:00:30Z other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-18T22:00:30Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-18T22:00:30Z other                          unexpected_error_cnt:            0
incoming 2011-08-18T22:00:30Z TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-18T22:00:30Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-18T22:00:30Z TOTALS                         unexpected_error_cnt:            0
18 Aug 2011 19:43:09,958  INFO [SERVICE_1750] -
incoming all time             b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
18 Aug 2011 19:43:09,959  INFO [SERVICE_1750] -
outgoing 2011-08-18T22:00:30Z b                                       new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-18T22:00:30Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-18T22:00:30Z b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-18T22:00:30Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-18T22:00:30Z h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-18T22:00:30Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-18T22:00:30Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-18T22:00:30Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-18T22:00:30Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-18T22:00:30Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
18 Aug 2011 19:43:09,960  INFO [SERVICE_1750] -
outgoing all time             b                                       new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
18 Aug 2011 19:43:16,638  INFO [SERVICE_1750] -
                  other-active:   1,254,930                      b-active:   2,630,622                      h-active:   2,794,171
                  total-active:   6,679,723
```

  * transformation log
```
```
  * MST\_rules\_log\_txt
```
18 Aug 2011 19:43:16,863  INFO [SERVICE_1750] - Rules for Normalization:
18 Aug 2011 19:43:16,863  INFO [SERVICE_1750] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
18 Aug 2011 19:43:16,864  INFO [SERVICE_1750] - HTA=6679724, NTA=6679723 ** FAIL **
18 Aug 2011 19:43:16,864  INFO [SERVICE_1750] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
18 Aug 2011 19:43:16,864  INFO [SERVICE_1750] - HBA=2630623, NBA=2630622 ** FAIL **
18 Aug 2011 19:43:16,864  INFO [SERVICE_1750] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
18 Aug 2011 19:43:16,864  INFO [SERVICE_1750] - HHA=2794171, NHA=2794171 ** PASS **
18 Aug 2011 19:43:16,864  INFO [SERVICE_1750] - %%%
18 Aug 2011 22:18:51,675  INFO [SERVICE_19543] - %%%
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - Rules for Transformation:
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - NBA=2630622, TMA=2630614 ** FAIL **
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - NBD=0, TMD=0 ** PASS **
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - NHA=2794171, THA=2682218, THH=120156 ** PASS **
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
18 Aug 2011 22:18:51,677  INFO [SERVICE_19543] - TEA=2808321, TWA=2808321 ** PASS **
18 Aug 2011 22:18:51,678  INFO [SERVICE_19543] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
18 Aug 2011 22:18:51,678  INFO [SERVICE_19543] - TWA=2808321, TMA=2630614 ** PASS **
18 Aug 2011 22:18:51,678  INFO [SERVICE_19543] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
18 Aug 2011 22:18:51,678  INFO [SERVICE_19543] - TWA=2808321, TMA=2630614 ** PASS **
```
Notes on above - all as expected the FAILS are because of logged 'no output produced' errors that can be seen in browse records, 1 out of normalization, 8 out of transformation.

  * database
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select count(*) from xc_rochester_137_local.records"
+----------+
| count(*) |
+----------+
|  6679727 |
+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679727 |
+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679724 |
| D      |        3 |
+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679723 |
+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| b    | A      |  2630622 |
| h    | A      |  2794171 |
| NULL | A      |  1254930 |
+------+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| w    | A      |  2808321 |
| e    | A      |  2808321 |
| m    | A      |  2630614 |
| h    | A      |  2682342 |
| h    | H      |   120032 |
+------+--------+----------+
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      | 10929598 |
| H      |   120032 |
+--------+----------+
```

# T0 - Initial Load  (from file harvest) #
  * You'll have to look below at the 6/28/11 data to see relevent OAI toolkit numbers, but on 8/12/11 we restarted our side, harvesting from file. 8/12/2011 t0 from file run completed, numbers:

  * as reported by 128.174.70.247
    * harvestIn file
```
incoming 2011-08-12T19:26:01Z b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-12T19:26:01Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-12T19:26:01Z b                              unexpected_error_cnt:            0
incoming 2011-08-12T19:26:01Z a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-12T19:26:01Z a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-12T19:26:01Z a                              unexpected_error_cnt:            0
incoming 2011-08-12T19:26:01Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-12T19:26:01Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-12T19:26:01Z h                              unexpected_error_cnt:            0
incoming 2011-08-12T19:26:01Z TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-12T19:26:01Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-12T19:26:01Z TOTALS                         unexpected_error_cnt:            0
12 Aug 2011 16:15:20,098  INFO [REPOSITORY_14626] -
incoming all time             b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             a                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
12 Aug 2011 16:15:50,390  INFO [REPOSITORY_14626] -
rochester_137_local:bib-active:   2,630,623rochester_137_local:hold-active:   2,794,171rochester_137_local:auth-active:   1,254,930
rochester_137_local:auth-deleted:           3
                  other-active:           0                 other-deleted:           0
                  total-active:   6,679,724                 total-deleted:           3
```

  * marcnormalization file
```
incoming 2011-08-12T19:26:01Z b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-12T19:26:01Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-12T19:26:01Z b                              unexpected_error_cnt:            0
incoming 2011-08-12T19:26:01Z a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-12T19:26:01Z a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-12T19:26:01Z a                              unexpected_error_cnt:            0
incoming 2011-08-12T19:26:01Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-08-12T19:26:01Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-12T19:26:01Z h                              unexpected_error_cnt:            0
incoming 2011-08-12T19:26:01Z TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming 2011-08-12T19:26:01Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-08-12T19:26:01Z TOTALS                         unexpected_error_cnt:            0
12 Aug 2011 16:15:20,098  INFO [REPOSITORY_14626] -
incoming all time             b                                       new_act_cnt:    2,630,623               new_held_cnt:            0                new_del_cnt:            0
incoming all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             b                              unexpected_error_cnt:            0
incoming all time             a                                       new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            3
incoming all time             a                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             a                              unexpected_error_cnt:            0
incoming all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             h                              unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,679,724               new_held_cnt:            0                new_del_cnt:            3
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
12 Aug 2011 16:15:50,390  INFO [REPOSITORY_14626] -
rochester_137_local:bib-active:   2,630,623rochester_137_local:hold-active:   2,794,171rochester_137_local:auth-active:   1,254,930
rochester_137_local:auth-deleted:           3
                  other-active:           0                 other-deleted:           0
                  total-active:   6,679,724                 total-deleted:           3
12 Aug 2011 19:01:41,118  INFO [SERVICE_16615] -
outgoing 2011-08-12T21:15:53Z b                                       new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-12T21:15:53Z b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-12T21:15:53Z b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-12T21:15:53Z h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-12T21:15:53Z h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-12T21:15:53Z other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-12T21:15:53Z other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-12T21:15:53Z TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-12T21:15:53Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-12T21:15:53Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
12 Aug 2011 19:01:41,119  INFO [SERVICE_16615] -
outgoing all time             b                                       new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             b                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             b                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             b                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             b                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             h                                       new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             h                                       upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             h                              upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             h                             upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             h                              upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             other                          upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             other                         upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             other                          upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
12 Aug 2011 19:01:47,639  INFO [SERVICE_16615] -
                  other-active:   1,254,930                      b-active:   2,630,622                      h-active:   2,794,171
                  total-active:   6,679,723
```
  * marctoxctransformation file
```
incoming all time             hold                                    new_act_cnt:    2,794,171               new_held_cnt:            0                new_del_cnt:            0
incoming all time             hold                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             hold                           unexpected_error_cnt:            0
incoming all time             bib                                     new_act_cnt:    2,630,622               new_held_cnt:            0                new_del_cnt:            0
incoming all time             bib                                     upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             bib                            unexpected_error_cnt:            0
incoming all time             other                                   new_act_cnt:    1,254,930               new_held_cnt:            0                new_del_cnt:            0
incoming all time             other                                   upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             other                          unexpected_error_cnt:            0
incoming all time             TOTALS                                  new_act_cnt:    6,679,723               new_held_cnt:            0                new_del_cnt:            0
incoming all time             TOTALS                                  upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
incoming all time             TOTALS                         unexpected_error_cnt:            0
12 Aug 2011 21:34:47,109  INFO [SERVICE_1961] -
outgoing 2011-08-13T00:02:05Z work                                    new_act_cnt:    2,808,321               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-13T00:02:05Z work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-13T00:02:05Z work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z holdings                                new_act_cnt:    2,682,218               new_held_cnt:      120,156                new_del_cnt:            0
outgoing 2011-08-13T00:02:05Z holdings                                upd_act_cnt:          124               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-13T00:02:05Z holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z expression                              new_act_cnt:    2,808,321               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-13T00:02:05Z expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-13T00:02:05Z expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z manifestation                           new_act_cnt:    2,630,614               new_held_cnt:            0                new_del_cnt:            0
outgoing 2011-08-13T00:02:05Z manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-13T00:02:05Z manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z TOTALS                                  new_act_cnt:   10,929,474               new_held_cnt:      120,156                new_del_cnt:            0
outgoing 2011-08-13T00:02:05Z TOTALS                                  upd_act_cnt:          124               upd_held_cnt:            0                upd_del_cnt:            0
outgoing 2011-08-13T00:02:05Z TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing 2011-08-13T00:02:05Z TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
12 Aug 2011 21:34:47,110  INFO [SERVICE_1961] -
outgoing all time             work                                    new_act_cnt:    2,808,321               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             work                                    upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             work                           upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             work                          upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             work                           upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             holdings                                new_act_cnt:    2,682,218               new_held_cnt:      120,156                new_del_cnt:            0
outgoing all time             holdings                                upd_act_cnt:          124               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             holdings                       upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             holdings                      upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             holdings                       upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             expression                              new_act_cnt:    2,808,321               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             expression                              upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             expression                     upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             expression                    upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             expression                     upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             manifestation                           new_act_cnt:    2,630,614               new_held_cnt:            0                new_del_cnt:            0
outgoing all time             manifestation                           upd_act_cnt:            0               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             manifestation                  upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:            0       upd_act_prev_del_cnt:            0
outgoing all time             manifestation                 upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             manifestation                  upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
outgoing all time             TOTALS                                  new_act_cnt:   10,929,474               new_held_cnt:      120,156                new_del_cnt:            0
outgoing all time             TOTALS                                  upd_act_cnt:          124               upd_held_cnt:            0                upd_del_cnt:            0
outgoing all time             TOTALS                         upd_act_prev_act_cnt:            0      upd_act_prev_held_cnt:          124       upd_act_prev_del_cnt:            0
outgoing all time             TOTALS                        upd_held_prev_act_cnt:            0     upd_held_prev_held_cnt:            0      upd_held_prev_del_cnt:            0
outgoing all time             TOTALS                         upd_del_prev_act_cnt:            0      upd_del_prev_held_cnt:            0       upd_del_prev_del_cnt:            0
12 Aug 2011 21:34:59,254  INFO [SERVICE_1961] -
                      e-active:   2,808,321                      h-active:   2,682,342                        h-held:     120,032
                      m-active:   2,630,614                      w-active:   2,808,321
                  total-active:  10,929,598                    total-held:     120,032
```

  * MST\_rules\_log\_txt
```
12 Aug 2011 19:01:47,953  INFO [SERVICE_16615] - Rules for Normalization:
12 Aug 2011 19:01:47,953  INFO [SERVICE_16615] - Harvest Total Active (HTA) = Normalization Total Active (NTA)
12 Aug 2011 19:01:47,953  INFO [SERVICE_16615] - HTA=6679724, NTA=6679723 ** FAIL **
12 Aug 2011 19:01:47,954  INFO [SERVICE_16615] - Harvest Bibs Active (HBA) = Normalization Bibs Active (NBA)
12 Aug 2011 19:01:47,954  INFO [SERVICE_16615] - HBA=2630623, NBA=2630622 ** FAIL **
12 Aug 2011 19:01:47,954  INFO [SERVICE_16615] - Harvest Holdings Active (HHA) = Normalization Holdings Active (NHA)
12 Aug 2011 19:01:47,954  INFO [SERVICE_16615] - HHA=2794171, NHA=2794171 ** PASS **
12 Aug 2011 19:01:47,954  INFO [SERVICE_16615] - %%%
12 Aug 2011 21:34:59,350  INFO [SERVICE_1961] - %%%
12 Aug 2011 21:34:59,352  INFO [SERVICE_1961] - Rules for Transformation:
12 Aug 2011 21:34:59,352  INFO [SERVICE_1961] - Normalization Bibs Active (NBA) = Transformation Manifestations Active (TMA)
12 Aug 2011 21:34:59,353  INFO [SERVICE_1961] - NBA=2630622, TMA=2630614 ** FAIL **
12 Aug 2011 21:34:59,353  INFO [SERVICE_1961] - Normalization Bibs Deleted (NBD) = Transformation Manifestations Deleted (TMD)
12 Aug 2011 21:34:59,353  INFO [SERVICE_1961] - NBD=0, TMD=0 ** PASS **
12 Aug 2011 21:34:59,354  INFO [SERVICE_1961] - Normalization Holdings Active (NHA) <= Transformation Holdings Active (THA) + Transformation Holdings Held (THH)
12 Aug 2011 21:34:59,354  INFO [SERVICE_1961] - NHA=2794171, THA=2682218, THH=120156 ** PASS **
12 Aug 2011 21:34:59,354  INFO [SERVICE_1961] - Transformation Expressions Active (TEA) = Transformation Works Active (TWA)
12 Aug 2011 21:34:59,354  INFO [SERVICE_1961] - TEA=2808321, TWA=2808321 ** PASS **
12 Aug 2011 21:34:59,354  INFO [SERVICE_1961] - Transformation Works Active (TWA) >= Transformation Manifestations Active (TMA)
12 Aug 2011 21:34:59,354  INFO [SERVICE_1961] - TWA=2808321, TMA=2630614 ** PASS **
12 Aug 2011 21:34:59,354  INFO [SERVICE_1961] - Transformation Expressions Active (TEA) >= Transformation Manifestations Active (TMA)
12 Aug 2011 21:34:59,354  INFO [SERVICE_1961] - TWA=2808321, TMA=2630614 ** PASS **
```
Notes on above - all as expected the FAILS are because of logged 'no output produced' errors that can be seen in browse records, 1 out of normalization, 8 out of transformation.


# T0 - Initial Load  (from file harvest) #
  * You'll have to look below at the 6/28/11 data to see relevent OAI toolkit numbers, but on 7/13/11 we restarted our side, harvesting from file. 7/13/2011 t0 from file run completed, numbers:

  * as reported by 128.174.70.240
    * database

```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select count(*) from xc_rochester_137_local.records"
+----------+
| count(*) |
+----------+
|  6679727 |
+----------+
```
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679727 |
+--------+----------+
```
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679724 |
| D      |        3 |
+--------+----------+
```
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679723 |
+--------+----------+
```
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marcnormalization -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| h    | A      |  2794171 |
| NULL | A      |  1254930 |
| b    | A      |  2630622 |
+------+--------+----------+
```
```
jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| w    | A      |  2808321 |
| e    | A      |  2808321 |
| m    | A      |  2630614 |
| h    | A      |  2682218 |
| h    | H      |   120156 |
+------+--------+----------+
```

  * (note that holdings active and holdings held have each changed by 25 from our initial run in June.  Total still same.)

```

jbrand@xc-devel:~$ mysql -u root --password=* -D xc_marctoxctransformation -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      | 10929474 |
| H      |   120156 |
+--------+----------+
```

  * as reported by 128.174.70.240
    * harvestIn/rochester\_137.txt  (matches above)
```
rochester_137_local:bib-active:   2,630,623rochester_137_local:hold-active:   2,794,171rochester_137_local:auth-active:   1,254,930
rochester_137_local:auth-deleted:           3
                  other-active:           0                 other-deleted:           0
                  total-active:   6,679,724                 total-deleted:           3
```

  * services/marcnormalization.txt  (off 1, but okay, because 1 error seen in 'Browse Records: 108:Produced no output')

```
                  other-active:   1,254,930                      b-active:   2,630,622                      h-active:   2,794,171
                  total-active:   6,679,723
```

  * services/marctoxctransformation.txt
    * expressions=works, both are > manifestions
    * bibs in should be = manifestations - 8 less manifestations can be explained by errors (no output).
    * holds out 2802374 > holds in 2794171 by 8203 (These are accounted for - extra holds within holds and extra holds within bibs)

```
                      e-active:   2,808,321                      h-active:   2,682,218                        h-held:     120,156
                      m-active:   2,630,614                      w-active:   2,808,321
                  total-active:  10,929,474                    total-held:     120,156
```




# T1 - Update applied #

  * You'll have to look below at earlier T1 data to see relevent OAI toolkit numbers, but on 7/13/11 we restarted our side, harvesting from file. 7/14/2011 t1 from file run completed, numbers:

  * as reported by 128.174.70.240
    * database
```
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ !1788
mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6684665 |
+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ !1789
mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from xc_rochester_137_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6684662 |
| D      |        3 |
+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ !1790
mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6684661 |
+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ !1791
mysql -u root --password=* -D xc_marcnormalization -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| b    | A      |  2633077 |
| h    | A      |  2796654 |
| NULL | A      |  1254930 |
+------+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ !1793
mysql -u root --password=* -D xc_marctoxctransformation -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      | 10939762 |
| H      |   120044 |
+--------+----------+
jbrand@xc-devel:/xc/MST-instances/MetadataServicesToolkit/logs$ !1792
mysql -u root --password=* -D xc_marctoxctransformation -e "select type, status, count(*) from records group by type, status order by status, status"
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| w    | A      |  2810940 |
| e    | A      |  2810940 |
| m    | A      |  2633069 |
| h    | A      |  2684813 |
| h    | H      |   120044 |
+------+--------+----------+
```

  * harvestIn/rochester\_137.txt - there was nothing in there related to the latest harvest.

  * MST\_General.txt - harvest info:
```
RecordCounts for repo: rochester_137_local
incoming 2011-07-13T13:29:10Z rochester_137_local:bib                 new_act_cnt:        2,455               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-07-13T13:29:10Z rochester_137_local:bib                 upd_act_cnt:           14               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-07-13T13:29:10Z rochester_137_local:bib        unexpected_error_cnt:            0
incoming 2011-07-13T13:29:10Z rochester_137_local:hold                new_act_cnt:        2,483               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-07-13T13:29:10Z rochester_137_local:hold                upd_act_cnt:           15               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-07-13T13:29:10Z rochester_137_local:hold       unexpected_error_cnt:            0
incoming 2011-07-13T13:29:10Z TOTALS                                  new_act_cnt:        4,938               new_held_cnt:            0                new_del_cnt:            0
incoming 2011-07-13T13:29:10Z TOTALS                                  upd_act_cnt:           29               upd_held_cnt:            0                upd_del_cnt:            0
incoming 2011-07-13T13:29:10Z TOTALS                         unexpected_error_cnt:            0
```

  * services/marcnormalization.txt  (off 1, but okay, because 1 error seen in 'Browse Records: 108:Produced no output')

```
                  other-active:   1,254,930                      b-active:   2,633,077                      h-active:   2,796,654
                  total-active:   6,684,661
```

  * services/marctoxctransformation.txt
    * expressions=works, both are > manifestions
    * bibs in should be = manifestations - 8 less manifestations can be explained by errors (no output).
    * holds out 2,684,813+120,044 > holds in 2,796,654 by 8203 (These are accounted for - extra holds within holds and extra holds within bibs)

```

                      e-active:   2,810,940                      h-active:   2,684,813                        h-held:     120,044
                      m-active:   2,633,069                      w-active:   2,810,940
                  total-active:  10,939,762                    total-held:     120,044
```


# T2 - Update applied #

  * You'll have to look below at earlier T2 data to see relevent OAI toolkit numbers, but on 7/13/11 we restarted our side, harvesting from file. 7/20/2011 t2 from file run completed, numbers follow.  NOTE, we can't tie these numbers out to OAI Toolkit, because an additional file of deletes only was run through the MST (the wrong deleted file harvest file).  Can still trace the numbers through our system.

  * as reported by 128.174.70.240
    * database
```

mysql -u root --password=* -e "select status, count(*) from xc_rochester_137_local.records"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6688036 |
+--------+----------+

mysql -u root --password=* -e "select status, count(*) from xc_rochester_137_local.records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6683489 |
| D      |     4547 |
+--------+----------+

mysql -u root --password=* -e "select status, count(*) c from xc_marcnormalization.records group by status order by status"
+--------+---------+
| status | c       |
+--------+---------+
| A      | 6683488 |
| D      |    1173 |
+--------+---------+

mysql -u root --password=* -e "select type, status, count(*) c from xc_marcnormalization.records group by type, status order by status, status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| b    | A      | 2632307 |
| h    | A      | 2796252 |
| NULL | A      | 1254929 |
| NULL | D      |       1 |
| h    | D      |     402 |
| b    | D      |     770 |
+------+--------+---------+


mysql -u root --password=* -D xc_marctoxctransformation -e "select status, count(*) c from records group by status order by status"
+--------+----------+
| status | c        |
+--------+----------+
| A      | 10936640 |
| D      |     2712 |
| H      |   120454 |
+--------+----------+

mysql -u root --password=* -D xc_marctoxctransformation -e "select type, status, count(*) c from records group by type, status order by type, status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| NULL | H      |     410 |
| e    | A      | 2810170 |
| e    | D      |     770 |
| h    | A      | 2684001 |
| h    | D      |     402 |
| h    | H      |  120044 |
| m    | A      | 2632299 |
| m    | D      |     770 |
| w    | A      | 2810170 |
| w    | D      |     770 |
+------+--------+---------+
```

  * harvestIn/rochester\_137.txt  (note: extra deletes seen because file of deletes that were never seen was processed)
```
rochester_137_local:bib-active:   2,632,308rochester_137_local:bib-deleted:       3,578rochester_137_local:hold-active:   2,796,252
rochester_137_local:hold-deleted:         964rochester_137_local:auth-active:   1,254,929rochester_137_local:auth-deleted:           5
                  other-active:           0                 other-deleted:           0
                  total-active:   6,683,489                 total-deleted:       4,547
```

  * services/marcnormalization.txt  (off 1, but okay, because 1 error seen in 'Browse Records: 108:Produced no output') (Note that there are extra deleted's in the input but they are not seen in marcnormalization because it had never seen them as active records.)

```
                  other-active:   1,254,929                 other-deleted:           1                      b-active:   2,632,307
                     b-deleted:         770                      h-active:   2,796,252                     h-deleted:         402
                  total-active:   6,683,488                 total-deleted:       1,173
```

  * services/marctoxctransformation.txt
    * expressions=works, both are > manifestions
    * bibs in (2,632,307) should be = manifestations (2,632,299) 8 less manifestations can be explained by 8 errors (no output).
    * holds out 2804045 > holds in 2,796,252 by 7793 (These are accounted for - extra holds within holds and extra holds within bibs)

```

                    other-held:         410                      e-active:   2,810,170                     e-deleted:         770
                      h-active:   2,684,001                     h-deleted:         402                        h-held:     120,044
                      m-active:   2,632,299                     m-deleted:         770                      w-active:   2,810,170
                     w-deleted:         770
                  total-active:  10,936,640                 total-deleted:       2,712                    total-held:     120,454
```




# T0 - Initial Load #

  * 6/28/11 Restarting test today from scratch with latest 1.0 candidate MST.  137 reset to starting state by Ralph. We will harvest from 137 and use 128.174.70.240 for the MST.

  * as reported by 137
```
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh
0 [main] INFO lucene_dbStatistics  -  *************** Lucene Database Statistics ***************


2 [main] INFO lucene_dbStatistics  - Total records in the Lucene Database are: 6679727
2 [main] INFO lucene_dbStatistics  -  Bibliographic records: 2630623
2 [main] INFO lucene_dbStatistics  -     Deleted Bibliographic records: 0
2 [main] INFO lucene_dbStatistics  -  Authority records: 1254930
2 [main] INFO lucene_dbStatistics  -     Deleted Authority records: 3
2 [main] INFO lucene_dbStatistics  -  Holdings records: 2794171
3 [main] INFO lucene_dbStatistics  -     Deleted Holdings records: 0
3 [main] INFO lucene_dbStatistics  -  Classification records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Classification records: 0
3 [main] INFO lucene_dbStatistics  -  Community information records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Community information records: 0
3 [main] INFO lucene_dbStatistics  -  Deleted records: 3
```

```
   * Curl all matches lucene_dbStatistics, 2630623+1254930+2794171=6679724 (+3 deleted=6679727)
   * Total (matches lucene_dbStatistics above, 6679727 inc. 3 deleted):
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6679724">3|4999|5000|6679724</resumptionToken>
```

  * Holds:
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2794171">4|8588|5000|2794171</resumptionToken>
```

  * Bibs:
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2630623">5|11809|5000|2630623</resumptionToken>
```

  * Auth:
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="1254930">6|5426193|5000|1254930</resumptionToken>
```

  * as reported by 128.174.70.240
    * harvestIn/rochester\_137.txt  (matches above)
```
      rochester_137:bib-active:   2,630,623     rochester_137:auth-active:   1,254,930    rochester_137:auth-deleted:           3
     rochester_137:hold-active:   2,794,171
                  other-active:           0                 other-deleted:           0
                  total-active:   6,679,724                 total-deleted:           3
```

  * services/marcnormalization.txt  (off 1, but okay, because 1 error seen in 'Browse Records: 108:Produced no output')
```
                  other-active:   1,254,930                      b-active:   2,630,622                      h-active:   2,794,171
                  total-active:   6,679,723
```

  * services/marctoxctransformation.txt
    * expressions=works, both are > manifestions
    * holds out 2802374 > holds in 2794171 by 8203 (about 22 or 25 of these can be explained by extra 852's within holding records.)
```
                      e-active:   2,808,321                      h-active:   2,682,342                        h-held:     120,032
                      m-active:   2,630,614                      w-active:   2,808,321
                  total-active:  10,929,598                    total-held:     120,032
```
  * mysql counts by type, status

```
mysql -u root --password=* -D xc_rochester_137 -e "select count(*) from records";
+----------+
| count(*) |
+----------+
|  6679727 |
+----------+
```

```
mysql -u root --password=* -D xc_rochester_137 -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679724 |
| D      |        3 |
+--------+----------+
```

```
mysql -u root --password=* -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6679723 |
+--------+----------+
```

```
mysql -u root --password=* -D xc_marcnormalization -e "select type, status, count(*) c from records group by type, status order b
y status,status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| b    | A      | 2630622 |
| h    | A      | 2794171 |
| NULL | A      | 1254930 |
+------+--------+---------+
```

```
mysql --user=root --password=* -e "select status, count(*) c from xc_marctoxctransformation.records group by status order by stat
us"
+--------+----------+
| status | c        |
+--------+----------+
| A      | 10929598 |
| H      |   120032 |
+--------+----------+
```

```
mysql --user=root --password=* -e "select type, status, count(*) c from xc_marctoxctransformation.records group by type, status o
rder by type, status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| e    | A      | 2808321 |
| h    | A      | 2682342 |
| h    | H      |  120032 |
| m    | A      | 2630614 |
| w    | A      | 2808321 |
+------+--------+---------+
```

# T1 - Update applied #

  * as reported by 137

```
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh
0 [main] INFO lucene_dbStatistics  -  *************** Lucene Database Statistics ***************


2 [main] INFO lucene_dbStatistics  - Total records in the Lucene Database are: 6684665
2 [main] INFO lucene_dbStatistics  -  Bibliographic records: 2633078
2 [main] INFO lucene_dbStatistics  -     Deleted Bibliographic records: 0
2 [main] INFO lucene_dbStatistics  -  Authority records: 1254930
2 [main] INFO lucene_dbStatistics  -     Deleted Authority records: 3
2 [main] INFO lucene_dbStatistics  -  Holdings records: 2796654
2 [main] INFO lucene_dbStatistics  -     Deleted Holdings records: 0
3 [main] INFO lucene_dbStatistics  -  Classification records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Classification records: 0
3 [main] INFO lucene_dbStatistics  -  Community information records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Community information records: 0
3 [main] INFO lucene_dbStatistics  -  Deleted records: 3
```

  * Curl all matches lucene\_dbStatistics, 2633078+1254930 +2796654 +3 =6684885
  * Total (matches lucene\_dbStatistics above, -- inc. 3 deleted):
```
/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6684662">null|4999|5000|6684662</resumptionToken>
```

  * Holds
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6684662">null|4999|5000|6684662</resumptionToken>
```

  * Bibs
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6684662">null|4999|5000|6684662</resumptionToken>
```

  * Auth
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6684662">null|4999|5000|6684662</resumptionToken>
```

  * as reported by 128.174.70.240
    * harvestIn/rochester\_137.txt  (matches above lucene)
```
      rochester_137:bib-active:   2,633,078     rochester_137:auth-active:   1,254,930    rochester_137:auth-deleted:           3
     rochester_137:hold-active:   2,796,654
                  other-active:           0                 other-deleted:           0
                  total-active:   6,684,662                 total-deleted:           3

```


  * services/marcnormalization.txt  (off 1, but okay, because 1 error seen in 'Browse Records: 108:Produced no output')
```
                  other-active:   1,254,930                      b-active:   2,633,077                      h-active:   2,796,654
                  total-active:   6,684,661
```

  * services/marctoxctransformation.txt
    * expressions=works, both are > manifestions
    * holds out - > holds in by 8203 (8167 of these because of hold within bib, 36 because of multiple holds within hold records, adds up!)
```
                      e-active:   2,810,940                      h-active:   2,684,813                        h-held:     120,044
                      m-active:   2,642,031                      w-active:   2,810,940
                  total-active:  10,948,724                    total-held:     120,044
```

  * mysql counts by type, status

```
mysql -u root --password=* -D xc_rochester_137 -e "select count(*) from records";
+----------+
| count(*) |
+----------+
|  6684665 |
+----------+
```

```
jbrand@xc-devel:~$ mysql -u * --password=root -D xc_rochester_137 -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6684662 |
| D      |        3 |
+--------+----------+
```

```
jbrand@xc-devel:~$ mysql -u * --password=root -D xc_marcnormalization -e "select status, count(*) from records group by status order by status"
+--------+----------+
| status | count(*) |
+--------+----------+
| A      |  6684661 |
+--------+----------+
```

```
jbrand@xc-devel:~$ mysql -u * --password=root -D xc_marcnormalization -e "select type, status, count(*) c from records group by type, status order by status,status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| NULL | A      | 1254930 |
| b    | A      | 2633077 |
| h    | A      | 2796654 |
+------+--------+---------+
```

```
jbrand@xc-devel:~$ mysql --user=root --password=* -e "select status, count(*) c from xc_marctoxctransformation.records group by status order by status"
+--------+----------+
| status | c        |
+--------+----------+
| A      | 10948724 |
| H      |   120044 |
+--------+----------+
```

```
jbrand@xc-devel:~$ mysql --user=root --password=* -e "select type, status, count(*) c from xc_marctoxctransformation.records group by type, status order by type, status"
+------+--------+---------+
| type | status | c       |
+------+--------+---------+
| e    | A      | 2810940 |
| h    | A      | 2684813 |
| h    | H      |  120044 |
| m    | A      | 2642031 |
| w    | A      | 2810940 |
+------+--------+---------+
```

```
jbrand@xc-devel:~$ mysql --user=root --password=* -e "select count(*), rn.type, rt.type from xc_marctoxctransformation.record_predecessors rp,xc_marcnormalization.records rn,xc_marctoxctran
.pred_record_id = rn.record_id and rp.record_id = rt.record_id group by rn.type, rt.type"
+----------+------+------+
| count(*) | type | type |
+----------+------+------+
|  2810940 | b    | e    |
|     8167 | b    | h    |
|  2642031 | b    | m    |
|  2810940 | b    | w    |
|  2796690 | h    | h    |
+----------+------+------+
```




# T2 - Delete applied #
  * 7/11/11 Ralph applied delete today to 137, & I reharvested from 137.
  * as reported by 137

```
voyager@xcvoyoai2 > ./lucene_dbStatistics.sh
0 [main] INFO lucene_dbStatistics  -  *************** Lucene Database Statistics ***************


1 [main] INFO lucene_dbStatistics  - Total records in the Lucene Database are: 6684762
2 [main] INFO lucene_dbStatistics  -  Bibliographic records: 2633050
2 [main] INFO lucene_dbStatistics  -     Deleted Bibliographic records: 56
2 [main] INFO lucene_dbStatistics  -  Authority records: 1254930
2 [main] INFO lucene_dbStatistics  -     Deleted Authority records: 13
2 [main] INFO lucene_dbStatistics  -  Holdings records: 2796614
3 [main] INFO lucene_dbStatistics  -     Deleted Holdings records: 99
3 [main] INFO lucene_dbStatistics  -  Classification records: 0
3 [main] INFO lucene_dbStatistics  -     Deleted Classification records: 0
3 [main] INFO lucene_dbStatistics  -  Community information records: 0
4 [main] INFO lucene_dbStatistics  -     Deleted Community information records: 0
4 [main] INFO lucene_dbStatistics  -  Deleted records: 168
voyager@xcvoyoai2 > pwd
/import/OAIToolkit
```

  * Curl all matches lucene\_dbStatistics,
    * Total
```
curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="6684594">15|4999|5000|6684594</resumptionToken>
```
      * Total check -> 6684762 - 168 = 6684594

  * Holds
```
curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=hold' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2796614">16|8588|5000|2796614</resumptionToken>
```
    * Holds check -> curl result = lucene stats result

  * Bibs
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=bib' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="2633050">17|11809|5000|2633050</resumptionToken>
```
    * Bibs check -> curl result = lucene stats result

  * Auth:
```
$ curl -s 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21&set=auth' | xmllint --format - | grep 'resumptionToken'
    <resumptionToken cursor="5000" completeListSize="1254930">18|5426193|5000|1254930</resumptionToken>
```
    * Auth check -> curl result = lucene stats result

  * as reported by 128.174.70.240
    * harvestIn/rochester\_137.txt  (matches above)
```
      rochester_137:bib-active:   2,633,050     rochester_137:bib-deleted:          56     rochester_137:auth-active:   1,254,930
    rochester_137:auth-deleted:          13     rochester_137:hold-active:   2,796,614    rochester_137:hold-deleted:          99
                  other-active:           0                 other-deleted:           0
                  total-active:   6,684,594                 total-deleted:         168
```

  * mysql counts by type, status