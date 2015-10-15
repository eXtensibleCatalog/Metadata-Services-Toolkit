# Controlling the Data Flow #

Only users with “Processing Rules” permission can perform the actions described in this section.

A Processing Rule is a user defined rule which the MST uses to determine which metadata records should be processed by which metadata services and in what order. Processing Rules consist of a source of metadata, a service to process the metadata, a list of OAI sets and metadata formats which the service should process, and, optionally, a definition of a new OAI set to which processed records should belong.

To add a new Processing Rule, go to the **Add Processing Rule** page under the **Processing Rules** tab. Select either an OAI repository or a metadata service from the radio buttons on the left side of the screen as the source of metadata, and select a metadata service from the radio buttons on the right side of the screen to process the metadata. Then, click **Continue to Step 2** to finish configuring the Processing Rule, or click **Cancel** to return to the **All Processing Rules** screen without adding a new Processing Rule.

Step 2 of the **Add Processing Rule** form displays a list of metadata formats and OAI sets that the source selected in step 1 supports and that the metadata service selected in step 1 accepts as input. Select one or more formats and one or more sets to be processed by the metadata service (CTRL+click to select multiple sets or formats). Optionally, a name and setSpec for an OAI set, into which records processed by the Processing Rule are inserted, may be provided. Click **Finish** to add the new Processing Rule, **Back to Step 1** to change entries on the previous page, or **Cancel** to return to the **List Processing Rules** screen without adding a new Processing Rule. For each Processing Rule, the **List Processing Rules** screen shows the source, metadata service, formats, and input sets that define that Processing Rule. Note that it is not possible to edit Processing Rule settings once the Processing Rule has been completed.

The Processing Rules page also contains a button for deleting a Processing Rule.  Though it will not always be possible to directly delete a Processing Rule when the "Delete" button is pressed, the button shall stay enabled. In this way, if it is possible to directly delete the rule, it shall be done, otherwise, the necessary steps the user needs to take will be described to them via dialog boxes.

The different scenarios for when the user presses the "Delete" button for a processing rule:

  * If all records on the input side are MARKED deleted and if all successors of the records from the input side are MARKED deleted then the processing rule will be deleted immediately. This is accomplished by checking whether the applicable input records are marked deleted and checking whether the processing rule is in use in any running MST job.

  * If there are applicable records in the input repository that are not marked deleted, then the following dialog shall be displayed:

"Cannot delete Processing Rule because there are non deleted records in ${repoName}. Use the 'Delete Records' button on ${repoName}, wait for processing to complete, and then attempt to delete the rule again."

  * If the records are deleted but processing is occurring on either the input or the output repository for the processing rule, then the following dialog shall be displayed:

"Cannot delete processing rule: ${processing rule} while the MST is processing deleted records. Attempt delete again after processing is complete."