**700:**

If the datafield does not contain a $t subfield, the $a, $b, $c, $d, $e, $g, and $q subfields’ values are concatenated, and the result is set on an element based on the $4 subfield’s value according to the following table:

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
|All other values or $4 not found|xc:creator|Expression|

In addition to lacking a $t, if the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an agentID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds an agentID attribute with a value equal to “xcauth” followed by the remainder of the $0.

If the datafield contains a $t subfield and the 2nd indicator is not 2, the $a, $b, $c, $d, $e, $g, $q, $4, $k, $l, $m, $n, $o, $p, $r, $s and $t subfields’ values are concatenated and added as a work level xc:relation element. If the datafield also contains a $0 subfield whose value starts with “(DLC)”, it adds a workID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a workID attribute with a value equal to “xcauth” followed by the remainder of the $0.

If the datafield contains a $t subfield and the 2nd indicator is 2, the $k, $l, $m, $n, $o, $p, $r, $s, and $t subfields are added to an rdvocab:workTitle in a separate work element unique to the $8 value.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an rdvocab:identifierOfWork element with a type of “lcnaf” and a value of “n” followed by the remainder of the $0 to the work element from the $8.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds an rdvocab:identifierOfWork element with a type of “xcauth” and a value of the remainder of the $0 to the work element from the $8.

**711:**

If the datafield does not contain a $t subfield, the $a, $b, $c, $d, $e, $g, and $q subfields’ values are concatenated, and the result is set on an element based on the $4 subfield’s value according to the following table:

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
|All other values or $4 not found|xc:creator|Expression|

In addition to lacking a $t, if the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an agentID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds an agentID attribute with a value equal to “xcauth” followed by the remainder of the $0.

If the datafield contains a $t subfield and the 2nd indicator is not 2, the $a, $b, $c, $d, $e, $g, $q, $4, $k, $l, $m, $n, $o, $p, $r, $s and $t subfields’ values are concatenated and added as a work level xc:relation element. If the datafield also contains a $0 subfield whose value starts with “(DLC)”, it adds a workID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a workID attribute with a value equal to “xcauth” followed by the remainder of the $0.
If the datafield contains a $t subfield and the 2nd indicator is 2, the $k, $l, $m, $n, $o, $p, $r, $s, and $t subfields are added to an rdvocab:workTitle in a separate work element unique to the $8 value.

If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an rdvocab:identifierOfWork element with a type of “lcnaf” and a value of “n” followed by the remainder of the $0 to the work element from the $8.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds an rdvocab:identifierOfWork element with a type of “xcauth” and a value of the remainder of the $0 to the work element from the $8.

**720:**

The $a, $e, and $4 subfields’ values are concatenated and the result is added to an expression level dcterms:contributor element.

**730:**

If the datafield’s 2nd indicator is not 2, the $a, $d, $g, $k, $l, $m, $n, $o, $p, $r, $s, and $t subfields’ values are concatenated and added as a work level xc:relation element. If the datafield also contains a $0 subfield whose value starts with “(DLC)”, it adds a workID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.

If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a workID attribute with a value equal to “xcauth” followed by the remainder of the $0.
If the datafield’s 2nd indicator is 2, the $a, $d, $g, $k, $l, $m, $n, $o, $p, $r, $s, and $t subfields are added to an rdvocab:workTitle in a separate work element unique to the $8 value.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an rdvocab:identifierOfWork element with a type of “lcnaf” and a value of “n” followed by the remainder of the $0 to the work element from the $8.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds an rdvocab:identifierOfWork element with a type of “xcauth” and a value of the remainder of the $0 to the work element from the $8.

**740:**

The $a, $t, $p, and $v subfields’ values are concatenated and the result is added to a manifestation level dcterms:alternative element.

**752:**

The $a, $b, $c, $d, $f, $g, $h, and $0 subfields’ values are concatenated and the result is added to a work level xc:coverage element.  All subfields except for $a are preceded with a dash instead of a space.

**760:**

The $a, $g, $i, $t, and $3 subfields’ values are concatenated and added as a manifestation level dcterms:isPartOf element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.

**765:**

The $a, $g, $i, $t, and $3 subfields’ values are concatenated and added as a manifestation level dcterms:isVersionOf element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**770:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as a work level dcterms:relation element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**772:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as a work level dcterms:relation element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**773:**

The $a, $g, $i, $t, and $3 subfields’ values are concatenated and added as a manifestation level dcterms:isPartOf element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**775:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as a expression level dcterms:relation element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**776:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as a expression level dcterms:HasFormat element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**777:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as a expression level dcterms:relation element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.

**780:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as a work level dcterms:replaces element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**785:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as a work level dcterms:isReplacedBy element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**786:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as a expression level dcterms:isVersionOf element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.

**787:**

The $a, $g, $i, and $t subfields’ values are concatenated and added as an expression level dcterms:relation element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $z subfield, its value is added as a dcterms:ISBN attribute’s value.