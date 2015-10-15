## Inputs ##
  * Record.status
    * if Record has been deleted, then the status will be Record.DELETED.
  * Record.successors
    * if this Record has been processed before (determined by the oai-id), then the record with have successor Records attached to it.  The only data attached to these records is the id.  **The content (xml) is not attached**.  If implementers find it necessary to have this, we can provide an optional way to do this.
  * Record.successors.predecessors
    * if this Record has successors associated with it, then the predecessors of the successors will also be attached.  As with Record.successors, these predecessor records only have the id associated with them.  For a typical one-to-one service, this data is somewhat redundant.  But for more complex services in which a Record may have more than one predecessor, it becomes necessary.

## Outputs ##
The below attributes are in the context of the list of Records returned by the process method.

  * Record.id
    * If a record already exists with this id, then that record will be overwritten with the new contents.
  * Record.status
    * Record.ACTIVE (default) - Record will be made available for oai-pmh harvesting once persisted.
    * Record.HELD - Record will be persisted and will await further notice to be made active.  These records are not included in oai-pmh responses.
    * Record.DELETED - Record will be marked as deleted.  These records are included in oai-pmh responses.

## Helper methods ##
  * RecordService.createSuccessor.  Ex:
`out = getRecordService().createSuccessor(r, getService());`
    * A service implementer will typically want to use this method for creating output records.  If the record already exists, it isn't necessary to do it this way.  Instead you can just populate the existing record with the new content.