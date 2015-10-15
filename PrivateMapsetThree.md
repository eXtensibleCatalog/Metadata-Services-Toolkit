**500:**

The $a and$3 subfields’ values are concatenated and added as a manifestation level dcterms:description element.

**501:**

The $a subfield’s value is added as an expression level dcterms:relation element.

**502:**

The $a subfield’s value is added as a work level rdvocab:dissertationOrThesisInformation element.

**504:**

The $a and $b subfields’ values are concatenated and added as a manifestation level dcterms:description element.

**505:**

The $a, $g, $r, $t, and $u subfields’ values are concatenated and added as a manifestation level dcterms:tableOfContents element.

**506** (Bibliographic only):

The $a, $b, $c, $d, $e, $f, $u, and $3 subfields’ values are concatenated and added as a manifestation level dcterms:rights element.

**507:**

The $a and $b subfields’ values are concatenated and added as an expression level rdvocab:scale element.

**508:**

The $a subfield’s value is added as an expression level rdvocab:artisticAndOrTechnicalCredits element.

**510:**

The $a, $b, $c, and $3 subfields’ values are concatenated and added as an expression level dcterms:isReferencedBy element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.

**511:**

The $a subfield’s value is added as an expression level rdvocab:performerNarratorAndOrPresenter element.

**513:**

The $a and $b subfields’ values are concatenated and added as a work level dcterms:temporal element.

**515:**

The $a subfield’s value is added as a manifestation level rdvocab:numberingOfSerials element.

**518:**

The $a and $3 subfields’ values are concatenated and added as a expression level rdvocab:placeAndDateOfCapture element.

**520:**

The $a, $b, $c, $u, and $3 subfields’ values are concatenated and added as a work level dcterms:abstract element.

**521:**

The $a, $b, and $3 subfields’ values are concatenated and added as a work level dcterms:audience element.

**522:**

The $a subfield’s value is added as a work level dcterms:spatial element.

**525:**

The $a subfield’s value is added as a work level dcterms:relation element.

**530:**

The $a, $b, $c, $d, $u, and $3 subfields’ values are concatenated and added as an expression level dcterms:hasFormat element.

**533:**

The $a, $b, $c, $d, $e, $f, $m, $n, and $3 subfields’ values are concatenated and added as an expression level dcterms:hasFormat element.

**534:**

The $a, $b, $c, $e, $f, $k, $l, $m, $n, $p, and $t subfields’ values are concatenated and added as an expression level dcterms:isFormatOf element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.

**538** (Bibliographic only):

The $a, $i, $u, and $3 subfields’ values are concatenated and added as an expression level dcterms:requires element.

**540:**

The $a, $b, $c, $d, $u, and $3 subfields’ values are concatenated and added as a manifestation level dcterms:rights element.

**544:**

The $a, $b, $c, $d, $e, $n, and $3 subfields’ values are concatenated and added as a manifestation level dcterms:description element.

**546:**

The $a, $b, and $3 subfields’ values are concatenated and added as an expression level dcterms:language element.

**547:**

The $a subfield’s value is added as a manifestation level dcterms:description element.

**550:**

The $a subfield’s value is added as an expression level dcterms:description element.

**555:**

The $a, $b, $c, $d, $u, and $3 subfields’ values are concatenated and added as a manifestation level dcterms:description element.

**580:**

The $a subfield’s value is added as an expression level dcterms:relation element.

**586:**

The $a and $3 subfields’ values are concatenated and added as an expression level rivocab:awards element.

**59X:**

The $a subfield’s value is added as a manifestation level dcterms:description element.

**600:**

The $a, $b, $c, $d, $e, $f, $g, $k, $l, $m, $n, $o, $p, $q, $r, $s, $t, $v, $x, $y, $z, $2, $3, and $4 subfields’ values are concatenated and added as a work level xc:subject element. A dash is inserted instead of a space before any $v, $x, $y, or $z subfield.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a subjID attribute with a value equal to “lcnaf:sh” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a subjID attribute with a value equal to “xcauth” followed by the remainder of the $0.

The xc:subject element is assigned a type attribute based on the 2nd indicator according to the following table:

|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|0                  |dcterms:LCSH        |
|1                  |lcac                |
|2                  |dcterms:MESH        |
|3                  |nal                 |
|5                  |cash                |
|6                  |rvm                 |
|7                  |The value of the $2 subfield|

**610:**

The $a, $b, $c, $d, $e, $f, $g, $k, $l, $m, $n, $o, $p, $q, $r, $s, $t, $v, $x, $y, $z, $2, $3, and $4 subfields’ values are concatenated and added as a work level xc:subject element. A dash is inserted instead of a space before any $v, $x, $y, or $z subfield.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a subjID attribute with a value equal to “lcnaf:sh” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a subjID attribute with a value equal to “xcauth” followed by the remainder of the $0.

The xc:subject element is assigned a type attribute based on the 2nd indicator according to the following table:

|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|0                  |dcterms:LCSH        |
|1                  |lcac                |
|2                  |dcterms:MESH        |
|3                  |nal                 |
|5                  |cash                |
|6                  |rvm                 |
|7                  |The value of the $2 subfield|

**611:**

The $a, $c, $d, $e, $f, $g, $j, $k, $l, $n, $p, $q, $s, $t, $v, $x, $y, $z, $2, $3, and $4 subfields’ values are concatenated and added as a work level xc:subject element. A dash is inserted instead of a space before any $v, $x, $y, or $z subfield.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a subjID attribute with a value equal to “lcnaf:sh” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a subjID attribute with a value equal to “xcauth” followed by the remainder of the $0.

The xc:subject element is assigned a type attribute based on the 2nd indicator according to the following table:

|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|0                  |dcterms:LCSH        |
|1                  |lcac                |
|2                  |dcterms:MESH        |
|3                  |nal                 |
|5                  |cash                |
|6                  |rvm                 |
|7                  |The value of the $2 subfield|

**630:**

The $a, $d, $e, $f, $g, $k, $l, $m, $n, $o, $p, $r, $s, $t, $v, $x, $x, $y, $z, $2, $3, and $4 subfields’ values are concatenated and added as a work level xc:subject element. A dash is inserted instead of a space before any $v, $x, $y, or $z subfield.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a subjID attribute with a value equal to “lcnaf:sh” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a subjID attribute with a value equal to “xcauth” followed by the remainder of the $0.

The xc:subject element is assigned a type attribute based on the 2nd indicator according to the following table:

|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|0                  |dcterms:LCSH        |
|1                  |lcac                |
|2                  |dcterms:MESH        |
|3                  |nal                 |
|5                  |cash                |
|6                  |rvm                 |
|7                  |The value of the $2 subfield|

**648:**

The $a, $v, $x, $y, and $z subfields’ values are concatenated and added as a work level xc:temporal element. A dash is inserted instead of a space before any $v, $x, $y, or $z subfield.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a chronID attribute with a value equal to “lcnaf:sh” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a chronID attribute with a value equal to “xcauth” followed by the remainder of the $0.

The xc:subject element is assigned a type attribute based on the 2nd indicator according to the following table:


|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|0                  |dcterms:LCSH        |
|1                  |lcac                |
|2                  |dcterms:MESH        |
|3                  |nal                 |
|5                  |cash                |
|6                  |rvm                 |
|7                  |The value of the $2 subfield|

**650:**

The $a, $b, $c, $d, $e, $v, $x, $y, $z, $2, $3, and $4 subfields’ values are concatenated and added as a work level xc:subject element. A dash is inserted instead of a space before any $v, $x, $y, or $z subfield.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a subjID attribute with a value equal to “lcnaf:sh” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a subjID attribute with a value equal to “xcauth” followed by the remainder of the $0.

The xc:subject element is assigned a type attribute based on the 2nd indicator according to the following table:


|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|0                  |dcterms:LCSH        |
|1                  |lcac                |
|2                  |dcterms:MESH        |
|3                  |nal                 |
|5                  |cash                |
|6                  |rvm                 |
|7                  |The value of the $2 subfield|

**651:**

The $a, $e, $v, $x, $y, $z, $2, $3, and $4 subfields’ values are concatenated and added as a work level xc:spatial element. A dash is inserted instead of a space before any $v, $x, $y, or $z subfield.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a geoID attribute with a value equal to “lcnaf:sh” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a geoID attribute with a value equal to “xcauth” followed by the remainder of the $0.

The xc:spatial element is assigned a type attribute based on the 2nd indicator according to the following table:


|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|0                  |dcterms:LCSH        |
|1                  |lcac                |
|2                  |dcterms:MESH        |
|3                  |nal                 |
|5                  |cash                |
|6                  |rvm                 |
|7                  |The value of the $2 subfield|

**653:**

The $a subfield’s value is moved to a work level dcterms:subject element.

**654:**

The $a, $b, $c, $e, $v, $y, $z, $2, $3, and $4 subfields’ values are concatenated and added as a work level xc:subject element. A dash is inserted instead of a space before any subfield except $a.  If the datafield contains a $2 subfield it is set on the xc:subject element as a type attribute.

**655:**

The $a, $e, $v, $x, $y, $z, $2, $3, and $4 subfields’ values are concatenated and added as a work level xc:type element. A dash is inserted instead of a space before any $v, $x, $y, or $z subfield.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a subjID attribute with a value equal to “lcnaf:sh” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a subjID attribute with a value equal to “xcauth” followed by the remainder of the $0.

The xc:type element is assigned a type attribute based on the 2nd indicator according to the following table:

|1st indicator value|Type attribute value|
|:------------------|:-------------------|
|0                  |dcterms:LCSH        |
|1                  |lcac                |
|2                  |dcterms:MESH        |
|3                  |nal                 |
|5                  |cash                |
|6                  |rvm                 |
|7                  |The value of the $2 subfield|