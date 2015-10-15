# Harvesting Metadata from a Repository #

Only users with “Harvest” permission can perform the actions described in this section.

A scheduled harvest is a job executed by the MST that harvests an OAI repository at regular intervals. The first time a scheduled harvest runs for a given OAI repository, it adds all records in that repository to the MST’s Solr index, and subsequent runs of the same scheduled harvest will add only those records that were changed since the previous harvest began, and will remove all records in the index which the OAI repository has marked as deleted. This means that the first run of a scheduled harvest will take several hours for repositories with a few million records, but the following runs will finish in much less time, since unchanged records are not harvested.

To add a scheduled harvest, first add the OAI repository to be harvested and then go to the **Add Scheduled Harvest** screen in the **Harvest** tab. Select the repository to schedule a harvest for from the top drop down menu. Select how often the scheduled harvest should be run (hourly, daily, or weekly) and specify the minutes after each hour, hour of the day, or day of the week and hour when the schedule should be run, depending on how often it should run. Optionally, the start and end dates of the scheduled harvest can also be specified to delay the first run of the scheduled harvest or prevent it from running after a certain time. After filling out this information click **Move to Step 2** to continue defining a new scheduled harvest, or **Cancel** to return to the **List Scheduled Harvests** screen without adding a new scheduled harvest.

Step 2 of the **Add Scheduled Harvest** form displays a list of metadata formats and OAI sets that the repository selected in step 1 supports.  Select one or more formats and one or more sets to be harvested during the scheduled harvest (CTRL+click to select multiple sets or formats). Either change the name of the scheduled harvest in the box on the left, or leave it unchanged to accept the default name. Optionally, an email address may be provided. This address will be sent the results of each run of the scheduled harvest. Click **Finish** to add the new scheduled harvest, **Back to Step 1** to change entries on the previous page, or **Cancel** to return to the **List Scheduled Harvests** screen without adding a new scheduled harvest.

The **List Scheduled Harvests** page in the **Harvest** tab displays information on all the scheduled harvests that have been set up. For each scheduled harvest the name of the scheduled harvest, name of the repository harvested, frequency and time when the scheduled harvest will run, and status of the scheduled harvest are shown. Clicking on the name of a scheduled harvest allows a user to edit it, and clicking **Delete** will delete the scheduled harvest.  Note that not all fields will be editable once a harvest gets defined.  The changeable fields are those related to the scheduled time as well as the email address to send messages to.

While a scheduled harvest is running, its name appears in the green box in the top right corner of the MST on every page. If a scheduled harvest is running, clicking the **Pause** button will suspend it until the **Resume** button is clicked. While a scheduled harvest is paused, the MST will not run any other jobs, but pausing a job will free up resources for processes other than the MST running on the same server. Clicking **Abort** will cause the scheduled harvest to stop running and the MST will then begin the next job.

## Note on Harvesting from Koha repositories ##

Per the Open Archives guidelines http://www.openarchives.org/OAI/2.0/guidelines-marcxml.htm we have created the MST to harvest MARCXML records using a Metadata Prefix of MARC21.  In order to harvest from some Koha repositories, it may be required to make a change so that you use the OAI Server of Koha in extended mode.  You will need to change the file /home/koha/opac/koha\_oai.conf as follows:

```
  format:
    marc21:
      metadataPrefix: marc21
      metadataNamespace: http://www.loc.gov/standards/marcxml/schema/MARC21slim
      schema: http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd
      xsl_file: /home/koha/koha-tmpl/intranet-tmpl/prog/en/xslt/identity.xsl
    marcxml:
      metadataPrefix: marxml
      metadataNamespace: http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim
      schema: http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd
    oai_dc:
      metadataPrefix: oai_dc
      metadataNamespace: http://www.openarchives.org/OAI/2.0/oai_dc/
      schema: http://www.openarchives.org/OAI/2.0/oai_dc.xsd
      xsl_file: /home/koha/koha-tmpl/intranet-tmpl/prog/en/xslt/UNIMARCslim2OAIDC.xsl
```