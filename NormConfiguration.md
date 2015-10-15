# Configuring the Normalization Service #

Before actually configuring the Normalization Service, the reader is expected to have read the previous sections explaining how to set up the MST to harvest records and pass them into Metadata Services.

The Normalization Service is configured by a properties file in the MST-instances/MetadataServicesToolkit/services/Normalization/serviceConfig directory. This properties file contains information on which Normalization Steps the Normalization Service should run and which ones it should skip. This file contains several lines of the format:

**`<normalization_step_name>`** = 1

Where **`<normalization_step_name>`** is the name of the step and "1" indicates that the step is enabled (set this value to "0" to disable a step and "1" to enable it.)

It also contains sections that define the properties used by the step. The sections are defined like so:

```
#-----------------------------------------
<Section name>
#-----------------------------------------
```

These pages show all the properties defined in this file:

[Bibliographic Record Steps](http://code.google.com/p/xcmetadataservicestoolkit/wiki/BibrecordSteps)

[Holdings Record Steps](http://code.google.com/p/xcmetadataservicestoolkit/wiki/HoldrecordSteps)


<font color='red'>There is a property that you MUST change or the normalization service will not run.  Find this line in <code>MST-instances/MetadataServicesToolkit/services/marcnormalization/META-INF/classes/service.xccfg</code> and put in your organization code</font>:
```
OrganizationCode = CHANGE_ME
```

CHANGE\_ME to the value needed for your institution.

### Important Note: ###

It is possible to change the configuration of steps in the Normalization Service by turning them on and off. However, adding new steps to the service and changing the definition of existing steps requires changes to the configuration file as well as to the Normalization JAR file.

**Whenever a change is made to any of these, all data must be reprocessed through this service and through all succeeding MST services to enable the OAI-PMH processing of updated records to continue to be handled automatically.**

We advise libraries who wish to make changes to the Normalization Service (or to any other MST metadata service) to collect several changes and implement them all at the same time, thereby minimizing the number of times that the data will need to be reprocessed.