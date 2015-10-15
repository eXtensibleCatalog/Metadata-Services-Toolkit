| **MARC Value** | **Resulting XC schema element and attribute** | **FRBR level** |
|:---------------|:----------------------------------------------|:---------------|
| **General instruction:  If multiple $4's are present in a MARC tag, map the data in the tag to multiple roles as listed below, deduping if multiple MARC relators map to the same XC Schema element below.  Ignore roles that aren't listed below.** |                                               |                |
| 100, 110, 111, 700, 710, 711 with  $4 aut, lbt, lyr (any of these) | rdarole:author                                | work           |
| 100, 110, 111, 700, 710, 711 with  $4 cmp | rdarole:composer                              | work           |
| 100, 110, 111, 700, 710, 711 with $4 com | rdarole:compiler                              | work           |
| 100, 110, 111 700, 710, 711 with $4 art | rdarole:artist                                | work           |
| 700, 710, 711 with $4 ths | xc:thesisAdvisor                              | work           |
| 100, 110, 111, 700, 710, 711 with $4 drt | rdarole:director                              | expression     |
| 100, 110, 111, 700, 710, 711 with $4 edt | rdarole:editor                                | expression     |
| 100, 110, 111, 700, 710, 711 with $4 ill | rdarole:illustrator                           | expression     |
| 100, 110, 111, 700, 710, 711 with $4 prf, act, dnc, nrt, voc, itr, cnd, mod (any of these) | rdarole:performer                             | expression     |
| 100, 110, 111, 700, 710, 711 with $4 pro | rdarole:producer                              | expression     |
| 100, 110, 111, 700, 710, 711 with $4 trl | rdarole:translator                            | expression     |
