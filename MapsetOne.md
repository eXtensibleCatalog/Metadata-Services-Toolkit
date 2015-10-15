# MARCXML to XC Mappings #

The following section describes the actions taken by the Transformation Service on each MARCXML field.

**010** (Bibliographic only):

The $a subfield is moved into a manifestation level xc:recordID field with a type attribute of LCCN.  Whitespace on either side of the number is removed.

**015:**

The $a subfield is moved into a manifestation level xc:identifier field.  This field contains a type attribute with a value equal to the MARCXML field’s $2 subfield.

**016** (Bibliographic only):

The $a subfield is moved into a manifestation level xc:recordID field.  This field contains a type attribute with a value equal to the MARCXML field’s $2 subfield.  If there is no $2 subfield, the type attribute is set to “LAC”.

**022** (Bibliographic only):

The $a subfield is moved to a manifestation level xc:identifier field with a type attribute of “ISSN”.  The $l subfield is moved to a separate manifestation level xc:identifier field with a type attribute of “ISSN-L”.

**024** (Bibliographic only):

The $a subfield is moved to a manifestation level xc:identifier with a type attribute based on the 1st indicator according to the following table:

|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|`_`                |No type attribute   |
|0                  |ISRC                |
|1                  |UPC                 |
|2                  |ISMN                |
|3                  |IAN                 |
|4                  |SICI                |
|7                  |The value of the $2 subfield|
|8                  |No type attribute   |

**028:**

The $a subfield is moved to an element at the manifestation level based on the 1st indicator according to the following table (note that the type attribute values are locally defined):

|1st indicator value|Element|Type attribute value|
|:------------------|:------|:-------------------|
|0                  |xc:identifier|SoundNr             |
|2                  |rdvocab :publisherNumber|No type attribute   |
|3                  |rdvocab:plateNumber|No type attribute   |
|4                  |xc:identifier|VideoNr             |

**030** (Bibliographic only):

The $a subfield is moved to a manifestation level xc:identifier with a type attribute of “CODEN”.

**035** (Bibliographic only):

The Transformation Service will ignore all 035 fields which do not contain a $a subfield in the format “(**`<organization>`**)**`<id>`**” (the Normalization should ensure that invalid 035 fields are fixed so they are in this format.)  The $a subfield’s **`<id>`** is moved to a manifestation level xc:recordID with a type equal to its **`<organization>`**.

**037:**

The $a subfield is moved to a manifestation level xc:identifier.  If the $b subfield is “GPO” or there is an 040 with a $a subfield of “GPO” then the resulting xc:identifier has a type attribute of “GPO”.

**050:**

If the $a subfield’s third character is a number, it is moved to a work level dcterms:subject with a type of “dcterms:LCC”.  (This is a safeguard to avoid mapping 050 fields that contain a textual prefix which could negatively affect XC’s ability to provide faceted access to LC Class number)

**055:**

The $a subfield is moved to a work level dcterms:subject.  If the 2nd indicator is 0, 1, 2, 3, 4, or 5 then the resulting dcterms:subject will have a type attribute of “dcterms:LCC”, otherwise it will not have a type attribute.

**060:**

The $a subfield is moved to a work level dcterms:subject with a type attribute of “dcterms:NLM”.

**074:**

The $a subfield is moved to a manifestation level xc:identifier with a type attribute of “GPOItem”.

**082:**

The $a subfield is moved to a work level dcterms:subject with a type attribute of “dcterms:DDC”.

**086:**

The $a subfield is moved to a manifestation level xc:identifier with a type attribute of “SuDoc”.

**090:**

The $a subfield is moved to a work level dcterms:subject with a type of “dcterms:LCC”.

**092:**

The $a subfield is moved to a work level dcterms:subject with a type attribute of “dcterms:DDC”.

**100:**

The $a, $b, $c, $d, $e, $g, and $q subfields’ values are concatenated, and the result is set on an element based on the $4 subfield’s value according to the following table:

|MARC Value|Element|FRBR Level|
|:---------|:------|:---------|
|aut, lbt, or lyr|rdarole:author|work      |
|cmp       |rdarole:composer|Work      |
|com       |rdarole:compiler|Work      |
|art       |rdarole:artist|Work      |
|drt       |rdarole:director|Expression|
|edt       |rdarole:editor|Expression|
|ill       |rdarole:illustrator|Expression|
|prf, act, dnc, nrt, voc, itr, cnd, or mod|rdarole:performer|Expression|
|pro       |rdarole:producer|Expression|
|trl       |rdarole:translator|Expression|
|All other values or $4 not found|xc:creator|Work      |

If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an agentID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds an agentID attribute with a value equal to “xcauth” followed by the remainder of the $0.

_Note that the planned XC Authority Control Service will populate certain controlled-access fields in MARCXML bibliographic records with $0 when the service matches a MARCXML bibliographic record to a MARCXML authority record.   Whenever the $0 is mentioned in this document, it should be assumed that the $0 is generated by the XC Authority Control Service._

**110:**

The $a, $b, $c, $d, $e, and $g subfields’ values are concatenated, and the result is set on an element based on the $4 subfield’s value according to the following table:

|MARC Value|Element|FRBR Level|
|:---------|:------|:---------|
|aut, lbt, or lyr|rdarole:author|work      |
|cmp       |rdarole:composer|Work      |
|com       |rdarole:compiler|Work      |
|art       |rdarole:artist|Work      |
|drt       |rdarole:director|Expression|
|edt       |rdarole:editor|Expression|
|ill       |rdarole:illustrator|Expression|
|prf, act, dnc, nrt, voc, itr, cnd, or mod|rdarole:performer|Expression|
|pro       |rdarole:producer|Expression|
|trl       |rdarole:translator|Expression|
|All other values or $4 not found|xc:creator|Work      |

If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an agentID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds an agentID attribute with a value equal to “xcauth” followed by the remainder of the $0.

**111:**

The $a, $c, $d, $e, $g, $j and $q subfields’ values are concatenated, and the result is set on an element based on the $4 subfield’s value according to the following table:

|MARC Value|Element|FRBR Level|
|:---------|:------|:---------|
|aut, lbt, or lyr|rdarole:author|Work      |
|cmp       |rdarole:composer|Work      |
|com       |rdarole:compiler|Work      |
|art       |rdarole:artist|Work      |
|drt       |rdarole:director|Expression|
|edt       |rdarole:editor|Expression|
|ill       |rdarole:illustrator|Expression|
|prf, act, dnc, nrt, voc, itr, cnd, or mod|rdarole:performer|Expression|
|pro       |rdarole:producer|Expression|
|trl       |rdarole:translator|Expression|
|All other values or $4 not found|xc:creator|Work      |

If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an agentID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds an agentID attribute with a value equal to “xcauth” followed by the remainder of the $0.

**130:**

The $a, $d, $f, $g, $h, $k, $l, $m, $n, $o, $p, $r, $s, and $t subfields’ values are concatenated, and the result is added as both a work level rdvocab:workTitle and an expression level rdvocab:expressionTitle.

If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a work level rdvocab:identifierOfWork element with a type of “lcnaf” and a value of “n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a work level rdvocab:identifierOfWork element with a type of “xcauth” and a value of the remainder of the $0.