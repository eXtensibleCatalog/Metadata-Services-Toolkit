**210:**

The $a and $b subfields’ values are concatenated and the result is added as a work level dcterms:alternative.

**222:**

The $a and $b subfields’ values are concatenated and the result is added as a work level dcterms:alternative.

**240:**

The $a, $d, $f, $g, $h, $k, $l, $m, $n, $o, $p, $r, and $s subfields’ values are concatenated, and the result is added as both a work level rdvocab:workTitle and an expression level rdvocab:expressionTitle.
If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a work level rdvocab:identifierOfWork element with a type of “lcnaf” and a value of “n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a work level rdvocab:identifierOfWork element with a type of “xcauth” and a value of the remainder of the $0.

**243:**

The $a, $d, $f, $g, $h, $k, $l, $m, $n, $o, $p, $r, and $s subfields’ values are concatenated, and the result is added as both a work level rdvocab:workTitle and an expression level rdvocab:expressionTitle.
If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds a work level rdvocab:identifierOfWork element with a type of “lcnaf” and a value of “n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a work level rdvocab:identifierOfWork element with a type of “xcauth” and a value of the remainder of the $0.

**245:**

The $a, $d, $f, $g, $k, $n, $p, and $s subfields’ values are concatenated and added as a manifestation level dctems:title element.

**246:**

If the 2nd indicator is 1, the $a, $b, $f, $n, and $p subfields’ values are concatenated and added as a manifestation level dcterms:title, otherwise the $a, $b, $f, $n, and $p subfields’ values are concatenated and added as a manifestation level dcterms:alternative element.

**247:**

The $a, $b, $f, $n, and $p subfields’ values are concatenated and added as a manifestation level dcterms:alternative.

**250:**

The $a subfield’s value is added as an expression level dcterms:version element.  In addition, the $a and $b subfields’ values are concatenated and added as a manifestation level rdvocab:editionStatement element.

**254:**

The $a subfield’s value is added as both a manifestation level rivocab:editionStatement element and an expression level dcterms:version element.

**255:**

The $a, $b, $c, $d, $e, $f, and $g subfields’ values are concatenated and added as an expression level rdvocab:scale element.

**260:**

The $a and $e subfields’ values are moved to separate manifestation level rdvocab:placeOfProduction elements.  The $b and $f subfields’ values are moved to separate manifestation level dcterms:publisher elements.  The $c and $g subfields’ values are moved to separate manifestation level dcterms:issued elements

**300:**

The $a subfield’s value is moved to a manifestation level dcterms:extent element.  The $c subfield’s value is moved to a manifestation level rdvocab:dimensions element.  The $b subfield’s value is moved to an element depending on the Leader 06 value according to the following table:

|Leader 06 Value|Element|FRBR Level|
|:--------------|:------|:---------|
|i or j         |rdvocab:soundcharacteristics|Manifestation|
|a, c, d, or t  |rdvocab:illistrativeContent|Expression|
|Any other value|xc:otherPhysicalDetails|Manifestation|

**310:**

The $a and $b subfields’ values are concatenated and added as a manifestation level rdvocab:frequency element.

**321:**

The $a and $b subfields’ values are concatenated and added as a manifestation level rdvocab:frequency element.

**362:**

The $a and $z subfields’ values are concatenated and added as a manifestation level rdvocab:numberingOfSerials element.

**440:**

The $a, $n, $p, and $v subfields’ values are concatenated and added as a manifestation level xc:isPartOf element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.  If the datafield contains a $0 subfield whose value starts with “(DLC)”, it adds an workID attribute with a value equal to “lcnaf:n” followed by the remainder of the $0.  If the datafield contains a $0 subfield whose value starts with “(**`<organization_code>`**)” where **`<organization_code>`** is the organization code in the MST’s configuration file, it adds a workID attribute with a value equal to “xcauth” followed by the remainder of the $0.

**490:**

If the 1st indicator is 0, the $a and $v subfields’ values are concatenated and added as a manifestation level dcterms:isPartOf element.  If the datafield contains a $x subfield, its value is added as a dcterms:ISSN attribute’s value.