| **Voyager Table Location and Value** | **XC FRBR Level** | **XC Schema equivalent** | **Comments** | **How used in XC** |
|:-------------------------------------|:------------------|:-------------------------|:-------------|:-------------------|
|MFHD\_ITEM/CHRON                      |item               |xc:chronology             |              |user identification of resource|
|ITEM/COPY\_NUMBER                     |item               |xc:copyNumber             |              |                    |
|MFHD\_ITEM/ITEM\_ENUM                 |item               |xc:enumeration            |              |user identification of resource|
|MFHD\_ID/MFHD\_ID                     |item               |xc:holdingsExemplified    |              |FRBR linking        |
|ITEM/PERM\_LOCATION                   |item               |xc:location               |              |facet value         |
|ITEM\_BARCODE (has its own Voyager table?)|item               |xc:pieceDesignation       |              |                    |
|ITEM/ITEM\_ID                         |item               |xc:recordID               |"Add prefix for institution as type attribute, e.g. type=""NRU"""|FRBR linking        |
|None - supplied by OAI Toolkit        |item               |xc:serviceProvider        |              |NCIP                |
|ITEM\_STATUS/                         |item               |xc:status                 |              |facet value         |
|ITEM/TEMP\_LOCATION                   |item               |xc:tempLocation           |              |facet value         |