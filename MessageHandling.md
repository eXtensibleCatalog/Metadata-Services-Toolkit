# Messages #

## Currently ##
  * service custom.properties file
```
error.code=101
error.descriptionFile=101.html
...
error.code=107
error.descriptionFile=107.html
```
  * NormalizationService.java
```
errors.add(service.getId() + "-107: Invalid 035 Data Field (035s should not contain a $" + subfield.getAttribute("code").getValue() + " subfield)");
```
  * Facets
    * Facet-values for the "message" facet are created based on the dynamic String produced in java.  This means that there can be more than one facet-value for the 107 error code if there are more than one different types of invalid 035 data fields.
  * web-ui
    * facet-values are listed like this
```
1-107: Invalid 035 Data Field (035s should not contain a $9 subfield) (3) (i)
1-107: Invalid 035 Data Field (035s should not contain a $b subfield) (3) (i)
```
    * there is a little icon (i) that, when clicked, displays the html file referenced in the service's custom.properties file.  In the above example, clicking either of the (i) icons would display the 107.html file.  However, clicking the actual facet-value (thus doing a faceted search) would give a different set of results for each.

## Proposal ##
  * These were all decided on in our 2010-07-15 meeting.  I'm documenting these just to keep track of them.
    * Just as a matter of semantics, we change the name "Errors" to "Messages".
    * Allow for different levels of Messages (eg ERROR, WARN, INFO).
    * Clearly keep track of which service assigned the error to the record.
    * Distinguish whether the error is on an incoming record or an outgoing record.
  * These were agreed upon in a follow up email thread (07/16)
    * Don't allow the creation of dynamic facet-values from code.  The services currently declare error message types which can result in an unknown # of facet-values.  We will make the relationship of declared-error-codes -> facet-values a one-to-one relationship.
    * Optionally allow for dynamic message details for services that require it.  These details won't be made known to the solr index and thus aren't included as facet values and can't be searched upon.  However, when you are in the web-ui and are viewing an individual record, then you will see the detailed description of the message.
      * This would change the above example in the following way:
        * service custom.properties
```
error.code=101
error.descriptionFile=101.html
error.text=Cannot create 035 from 001 (001 control field missing)
...
error.code=107
error.descriptionFile=107.html
error.text=Invalid 035 Data Field
```
        * NormalizationService.java - one of the following (depending on whether the implementer needs the additional description field)
```
errors.add(getError(107));
```
```
errors.add(getError(107), "035s should not contain a $9 subfield");
```
        * web-ui (faceted searching options)
```
1-107: Invalid 035 Data Field (6) (i)
```
        * web-ui (viewing a specific record) (either of these - again, depending on whether or not a detailed description was used
```
1-107: Invalid 035 Data Field
```
```
1-107: Invalid 035 Data Field (035s should not contain a $9 subfield)
```
      * If the service implementer doesn't like that consolidation of facet-values, then they could declare more message types.
        * custom.properties:
```
error.code=107
error.descriptionFile=107.html
error.text=Invalid 035 Data Field (035s should not contain a $9 subfield)

error.code=108
error.descriptionFile=107.html
error.text=Invalid 035 Data Field (035s should not contain a $b subfield)
```
        * NormalizationService.java - something similar to this
```
if ("$9".equals(subfield))
  errors.add(getError(107));
else if ("$b".equals(subfield))
  errors.add(getError(108));
```
        * web-ui (faceted searching options)
```
1-107: Invalid 035 Data Field (035s should not contain a $9 subfield) (3) (i)
1-108: Invalid 035 Data Field (035s should not contain a $b subfield) (3) (i)
```
        * web-ui (viewing a specific record)
```
1-107: Invalid 035 Data Field (035s should not contain a $9 subfield)
```
    * Implementation Details
      * I believe the above proposed solution would involve adding these tables (in addition to making the necessary changes in configs and code as mentioned above) to the metadataservicestoolkit schema:
```
create table record_messages (
  record_message_id  int(11)      NOT NULL AUTO_INCREMENT,
  rec_in_out         char(1)      not null,
  record_id          int          NOT NULL,
  repo_id            int          not null,
  msg_code           int          not null,
  msg_level          char(1)      not null,
  service_id         int(11)      not null,
    
  PRIMARY KEY (record_message_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table record_message_details (
  record_message_id  int(11)      NOT NULL AUTO_INCREMENT,
  detail             varchar      not null,

  PRIMARY KEY (record_message_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
```
      * I would also suggest creating a sequence (similar to the pattern found here) so that record\_messages and record\_message\_details can be associated before they are persisted.
        * [create\_oai\_id\_seq.sql](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/0.3.0/mst-common/src/java/xc/mst/repo/sql/create_oai_id_seq.sql)
        * [RepositoryDAO](http://code.google.com/p/xcmetadataservicestoolkit/source/browse/branches/0.3.0/mst-common/src/java/xc/mst/repo/RepositoryDAO.java)