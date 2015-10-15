# The XC Schema #

The XC Schema is a FRBR-based schema designed to optimize the functionality of the XC Drupal Toolkit user interface. It includes five levels, representing the four FRBR Group 1 entities (Work, Expression, Manifestation, and Item).

A fifth level, "Holdings", sits between the Manifestation and Item levels and serves as a carrier for data that originates as MARC Holdings data.

The XC Item level has not yet been implemented, but has been defined for potential future use.

The XC Schema contains all DC terms properties, plus a subset of RDA (_Resource Description and Access_) elements and roles.  A few XC data elements have been newly defined to meet the specific needs of the XC software.

A revision of the schema (with an xsd) is planned, which will include some additional data elements. This revision will update the names and URIs of the RDA properties used within the XC Schema to correspond to the RDA elements and properties as ultimately published in that standard.


# Documentation #

The elements of the XC Schema are defined here: [XC Schema Elements 1.0](https://docs.google.com/open?id=0B7AewjrTTKSUMTQwM2IwYjUtNGNhMi00ZDNmLTgyZDMtYTNlODQzZGFlNjNm)


Additional information about the XC Schema and how the eXtensible Catalog uses metadata is available in the paper, _Supporting the eXtensible Catalog through Metadata Design and Services_  by Jennifer Bowen (2009) [Available via UR Research](http://hdl.handle.net/1802/6377)