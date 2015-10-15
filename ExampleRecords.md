## These example input records: ##

```
<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dcterms:title>Segmentation Propagation during a Camera Saccade</dcterms:title>
  <dcterms:identifier xsi:type="dcterms:URI">http://hdl.handle.net/1802/26</dcterms:identifier>
  <dc:type>Technical Report</dc:type>
  <dcterms:creator>Green, Isaac, A. (1974 - )</dcterms:creator>
  <dcterms:creator>Nelson, Randal, C. </dcterms:creator>
  <dcterms:abstract>In this paper, we present a method for propagating segmentation information across a  saccade for a foveating camera. In particular, we take a region of interest from a wide-angle,  low-fidelity image and propagate its segmentation information to a zoomed, high-fidelity  image containing that region. Our method uses normalized greyscale templates to estimate  the change in translation and magnification required to transform the segmented region.  This process is useful for systems which detect regions of interest at low-fidelity and then  perform a saccade to provide a high-fidelity view of that region of interest. We show how  using this method increases the performance of an active object recognition system.</dcterms:abstract>
  <dcterms:language>eng</dcterms:language>
  <dcterms:subject>active object recognition</dcterms:subject>
  <dcterms:subject>segmentation</dcterms:subject>
  <dcterms:subject>object tracking</dcterms:subject>
  <dcterms:publisher>University of Rochester. Computer Science Department.</dcterms:publisher>
  <dcterms:rights>This item is protected by copyright, with all rights reserved.</dcterms:rights>
  <dcterms:bibliographicCitation/>
  <dcterms:dateAccepted>Thu, 17 Jul 2003 15:40:11</dcterms:dateAccepted>
  <dcterms:issued>Month: 11 Year: 2002 </dcterms:issued>
  <dcterms:modified>Thu, 17 Jul 2003 15:40:11</dcterms:modified>
</oai_dc:dc>
```

## Return these example output records: ##

```
<xc:frbr xmlns:xc="http://www.extensiblecatalog.info/Elements" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rdvocab="http://rdvocab.info/Elements" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdarole="http://rdvocab.info/roles">
  <xc:entity type="work" id="oai:mst.rochester.edu:dctoxctransformation/23595001">
    <dcterms:subject>active object recognition</dcterms:subject>
    <dcterms:subject>segmentation</dcterms:subject>
    <dcterms:subject>object tracking</dcterms:subject>
    <dcterms:creator>Green, Isaac, A. (1974 - )</dcterms:creator>
    <dcterms:creator>Nelson, Randal, C.</dcterms:creator>
    <dcterms:abstract>In this paper, we present a method for propagating segmentation information across a  saccade for a foveating camera. In particular, we take a region of interest from a wide-angle,  low-fidelity image and propagate its segmentation information to a zoomed, high-fidelity  image containing that region. Our method uses normalized greyscale templates to estimate  the change in translation and magnification required to transform the segmented region.  This process is useful for systems which detect regions of interest at low-fidelity and then  perform a saccade to provide a high-fidelity view of that region of interest. We show how  using this method increases the performance of an active object recognition system.</dcterms:abstract>
  </xc:entity>
</xc:frbr>
<xc:frbr xmlns:xc="http://www.extensiblecatalog.info/Elements" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rdvocab="http://rdvocab.info/Elements" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdarole="http://rdvocab.info/roles">
  <xc:entity type="expression" id="oai:mst.rochester.edu:dctoxctransformation/23595002">
    <dcterms:type>Technical Report</dcterms:type>
    <dcterms:dateAccepted>Thu, 17 Jul 2003 15:40:11</dcterms:dateAccepted>
    <dcterms:language>eng</dcterms:language>
    <dcterms:bibliographicCitation/>
    <xc:workExpressed>oai:mst.rochester.edu:dctoxctransformation/23595001</xc:workExpressed>
  </xc:entity>
</xc:frbr>
<xc:frbr xmlns:xc="http://www.extensiblecatalog.info/Elements" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rdvocab="http://rdvocab.info/Elements" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdarole="http://rdvocab.info/roles">
  <xc:entity type="manifestation" id="oai:mst.rochester.edu:dctoxctransformation/23595003">
    <dcterms:rights>This item is protected by copyright, with all rights reserved.</dcterms:rights>
    <dcterms:issued>Month: 11 Year: 2002</dcterms:issued>
    <dcterms:modified>Thu, 17 Jul 2003 15:40:11</dcterms:modified>
    <dcterms:identifier>http://hdl.handle.net/1802/26</dcterms:identifier>
    <dcterms:publisher>University of Rochester. Computer Science Department.</dcterms:publisher>
    <dcterms:title>Segmentation Propagation during a Camera Saccade</dcterms:title>
    <xc:expressionManifested>oai:mst.rochester.edu:dctoxctransformation/23595002</xc:expressionManifested>
  </xc:entity>
</xc:frbr>
```