Rules for 1.0 release
  * Harvest repositories may be marked deleted
    * Harvest Repositories can NEVER be deleted, but the records contained in them can be marked deleted.
    * When the contents of a harvest repository are marked deleted, the records are marked deleted, AND the harvest schedules associated with the repository are automatically deleted.
    * After this mark-delete operation happens, it is still possible to recreate a new harvest schedule for this repository.  If this happens, the remote OAI-PMH repository should be harvested fully (al records) and we should verify that the records that come in would overwrite the marked-deleted records that would have last-updated dates that are later than the last updated dates in the remote OAI repository.
    * We will fake out the MST as if the records were marked deleted in the external repository.  This is false and does conceptually break oai-pmh.

  * Service repositories can never be deleted
    * To delete a service repository doesn't really make sense.  If you really want to mark records as deleted, then you must delete the harvest repository and the deletions will flow downstream.
  * Service updates
    * The MST will check timestamps on config files and code files w/in services to see if they need to reprocess all records.  That is how you update a service:
      * stop tomcat
      * delete old service on disk (MetadataServicesToolkit/services/XXX)
      * unzip new service
      * start the MST
    * follow that same process if you need to make a configuration change.
  * Make processing rules immutable and undeletable
    * similar to the concept of deleting records in a service, deleting processing rules doesn't really make sense.  You can't undo what you've already done.  If we allow processing rules to be deleted, then we hide the input that went into a service to produce its current output.
    * Proposal for deletion: Processing rules can be deleted if ALL current input records (input to the rule) AND ALL corresponding successor records (output to the service that the rule is sending records to) are also marked deleted. For the implementation, the entry for the rule in the SQL database and the predecessor/successor links and all the records, and the linkage to the rule can all be maintained for stability of the system, but the rule would in effect be disabled, and hidden.
    * Proposal for disabling: we should allow prs to be disabled.