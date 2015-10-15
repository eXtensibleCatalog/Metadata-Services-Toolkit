# Introduction #
This is a list of changes to the MST UI as we transition from 0.3.4 to 1.0.  It is a living document until we reach agreement on the changes made and the changes needed.

# Changes #

## Completed Changes ##
### Completed changes for “Repository Updates and Deletes (Mark Deleted - Harvest Repository)” ###

"Delete" button shall be renamed "Delete Records." (now complete)

http://www.extensiblecatalog.org/doc/MST/4wiki/Repo_Mark_Deleted.PNG


---


New dialog messages if  Repository -> <click on link for an available repository> -> Delete is pressed:

http://www.extensiblecatalog.org/doc/MST/4wiki/Repo_Mark_Deleted_Dialog_1.PNG

> _If the repository contains harvested records, a 2nd dialog is shown with an updated message:_

http://www.extensiblecatalog.org/doc/MST/4wiki/Repo_Mark_Deleted_Dialog_2.PNG



---

### Completed changes for “Service repositories can never be deleted” ###

Removed Delete button from Service menu, so if you “List Services”, next to each installed Service, you will no longer see ‘Delete’ as an option.

http://www.extensiblecatalog.org/doc/MST/4wiki/Services_No_Delete.PNG

---


### Completed Change - "Processing Rule Changes" ###
From a User Interface perspective:

Though it will not always be possible to directly delete a Processing Rule when the "Delete" button is pressed, the button shall stay enabled.  In this way, if it is possible to directly delete the rule, it shall be done, otherwise, the necessary steps the user needs to take will be described to them via dialog boxes.

The different use cases for when the user presses the "Delete" button for a processing rule:

  * If all records on the input side are MARKED deleted and if all successors of the records from the input side are MARKED deleted then the processing rule will be deleted immediately.  This is accomplished by checking whether the applicable input records are marked deleted and checking whether the processing rule is in use in any running MST job.

  * If there are applicable records in the input repository that are not marked deleted, then the following dialog shall be displayed:

_"Cannot delete Processing Rule because there are non deleted records in ${repoName}. Use the 'Delete Records' button on ${repoName}, wait for processing to complete, and then attempt to delete the rule again."_

  * If the records are deleted but processing is occurring on either the input or the output repository for the processing rule, then the following dialog shall be displayed:

_"Cannot delete processing rule: ${processing rule} while the MST is processing deleted records.  Attempt delete again after processing is complete."_

http://www.extensiblecatalog.org/doc/MST/4wiki/PR_Delete_Dialog.PNG


---


**Also the Processing Rule shall be immutable:**

http://www.extensiblecatalog.org/doc/MST/4wiki/PR_Step1.PNG


---


http://www.extensiblecatalog.org/doc/MST/4wiki/PR_Step2.PNG


---

### Completed Change - "Make Harvest Immutable" ###

http://www.extensiblecatalog.org/doc/MST/4wiki/Harvest.PNG

---


http://www.extensiblecatalog.org/doc/MST/4wiki/Harvest2.PNG


---


### Completed Change - "Make Repository Immutable" ###

http://www.extensiblecatalog.org/doc/MST/4wiki/No_Edit_Repository.PNG

---

http://www.extensiblecatalog.org/doc/MST/4wiki/No_Edit_Repository2.PNG


---


### Completed change - "Edit Service Changes" ###
No longer allow any 'Edit Service' options from this page but point to user documentation on how to update service.

http://www.extensiblecatalog.org/doc/MST/4wiki/Service_Edit.PNG


### Proposed changes for “Repository Updates and Deletes” ###
  * Individual Repository “Delete” buttons should be disabled (or removed) for a repository if the repository records have been marked deleted or if no records are available.
(5/24/11 update - version 1.1 feature)


### Proposed change – “Delete All Data” button ###
So that a user can start over – where does this new button go?  Perhaps a new page under “Configuration”, “Reset Configuration” with this button on it and some surrounding implications of pressing it.

(5/24/11 update - version 1.1 feature)