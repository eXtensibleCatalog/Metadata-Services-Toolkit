# Description of MARCXML Holdings Normalization Steps #

## Name: ValidFirstChar014 ##

**Properties section name: none**

**Default setting:  Off**

**Purpose:**  If the field 014 has a 1st indicator value "1" and a $a value beginning with any character EXCEPT those listed in the configuration file for this step (e.g. ValidFirstChar014 = ), the step deletes the 014 field.

_This step is designed to remove 014 fields that include links to bibliographic record numbers that are not present in the current system and that will not be processed through the MARCXML to XC Schema Transformation Service. Enabling this step will prevent holdings record with such 014 fields from being "held" indefinitely until the nonexistent bibliographic record is processed by the system._

## Name: InvalidFirstChar014 ##

**Properties section name:  none**

**Default setting:  Off**

**Purpose:**  If the field 014 has a 1st indicator value "1" and a $a value beginning with any character listed in the configuration file for this step (e.g. InvalidFirstChar014 = ), the step deletes the 014 field.

_This step is designed to remove 014 fields that include links to bibliographic record numbers that are not present in the current system and that will not be processed through the MARCXML to XC Schema Transformation Service. Enabling this step will prevent holdings record with such 014 fields from being "held" indefinitely until the nonexistent bibliographic record is processed by the system._

## Name:  Add014Source ##

**Properties section name: none**

**Default setting: Off**

**Purpose:** If the field 014 has a value beginning with the characters found in MARCNormalization custom.properties file substitutions.014.key, add a subfield b to the 014 containing the characters MARCNormalization custom.properties file substitutions.014.value.

For example by default the file contains:
```
substitutions.014.key=ocm
substitutions.014.value=OCoLC
```
So in this case if 014 starts with ocm then add a subfield b containing the characters 'OCoLC'

_This step is designed to properly identify linkage numbers found in legacy University of Rochester data._

## Name: Replace014 ##

**Properties section name: none**


**Default setting: Off**

**Possible settings:**
  * on
  * off
  * protect

**Purpose:**  Replaces multiple 004 fields (which are not standard MARC) for bound-withs (i.e. a holdings record attached to more than one bibliographic record) with new 014 fields for all but the first linked bib record (004).  This step was created to handle Voyager data extracted as separate bibliographic and holdings files, which will contain multiple 004 fields for bound-withs.

  * on
If records entering the service may contain multiple 004 fields, this step must be set to “on” in order for these records to be processed correctly.

  * protect
If a library wants to detect records with multiple 004 fields without overlaying existing 014 fields, the step can be set to “Protect”.

  * off
If the step is set to “off” and multiple 004 fields are present in a holdings record, the record will NOT be processed properly by other MST services (i.e. only one 004 will be used to link the holdings record to a parent bibliographic record)!


_This step first looks to see if a holdings record has multiple 004 fields.
If only a single 004 field is present, the step does nothing.
If multiple 004 fields are present and the step is set to “on”, the step deletes any existing 014 fields with 1st indicator set to “1”, deletes all but the first 004 field, and creates a new 014 field with 1st indicator set to “1” for each deleted 004 field, with the new 014 field containing the content of the deleted 004 field (link to bib record).  The first occurring 004 field remains in the record without modification.
If multiple 004 fields are present and the step is set to “protect”, the step logs an error for the record, and does not process the record.
Note: Records with multiple 004 fields will not be processed correctly by other MST services!_


## Name: HoldingsLocationName ##

**Properties section name:  LOCATION CODE TO LOCATION**

**Default setting:  Off**

**Purpose:**  Supplies a location name for a location code so that it can be displayed in a form that is understandable to users.

_This step gets the location name from the configuration file for the location code in 852 $b and inserts it in 852 $c.  The section ‘LOCATION CODE TO LOCATION‘ in the configuration file contains sample mapping for location codes to the location they represent.   These should be deleted and replaced with the Institution’s actual location code mappings if this step is used.   For example, the line “RareRev = Preservation/Rare Books” tells the Normalization Service to use the location “Preservation/Rare Books” for the location code “RareRev”.   A single code may not be associated with more than location. If there is not a row for a given location code, that code will not be replaced with a location._

## Name:  LocationLimitName ##

**Properties section name:  LOCATION CODE TO LOCATION LIMIT NAME**

**Default setting:  Off**

**Purpose:**  Groups specific location codes together into a smaller number of general Location Limit Names to facilitate the creation of broad facet values for location.

_Replaces the location code in 852 $b with the corresponding location limit name from the ‘LOCATION CODE TO LOCATION  LIMIT NAME’ section of the configuration file.  The default configuration file contains sample mappings for location codes to general location limit names.  These should be deleted and replaced with the Institution’s actual location codes and mappings if this step is used.    For example, the line “RareRev = Rare Books” tells the Normalization Service to use the location limit “Rare Books” for the location code “RareRev”.   A single code may not be associated with more than one location limit. If there is not a row for a given location code, that code will not be replaced with a location limit._