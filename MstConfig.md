# Configuring the MST #

## Customizing Configuration Files ##

### install.properties _(Required)_ ###

Go to `<tomcat_working_dir>`/MST-instances/MetadataServicesToolkit and open install.properties using vi editor or any other editor. You will find details on those properties in that file.

Make necessary changes to represent your institution’s information, then save and close. Note that having the “#” character at the beginning of the line means that the MST will ignore the value for that property and will instead use the default value. Therefore, make sure to uncomment (remove the #) for the lines that you need to change.

_**This file is where your database password is, so be sure that it is right in this file.  If you use the default password 'root' you do not need to indicate that in this file.**_

For instance, change the value for `OaiRepoAdminEmail` to a valid email address for your institution, and uncomment it:

```
# The email address of the MST administrator for the OAI repositories
# OaiRepoAdminEmail=mstadmin@library.rochester.edu
```

Another important one you will want to change is:
```
DomainNameIdentifier=mst.rochester.edu
```

Note that not all possible editable properties are listed in install.properties.  To see a complete list of properties used the MST visit this link on your MST:
```
http://my.server.name:8080/MetadataServicesToolkit/devAdmin?op=props
```

### log4j.config.txt _(Optional)_ ###

Go to `<tomcat_working_dir>`/MST-instances/MetadataServicesToolkit and open log4j.config.txt using vi or any other editor.

By default, logs will be created under `<tomcat_working_dir>`/MST-instances/MetadataServicesToolkit/logs.

If you wish to, you may change the logging level here.

## Service Configuration Folder ##

Download the .zip files of the services you wish to install [here](http://code.google.com/p/xcmetadataservicestoolkit/downloads/list).



Unzip and extract them into `<tomcat_working_dir>`/MST-instances/MetadataServicesToolkit/services.

Repeat the steps above to install any additional services. Each service has additional configuration parameters and additional installation instructions located in the …/services/`<service_name>`/META-INF/classes/xc/mst/services/custom.properties file.

## Deploying the WAR File ##

Move MetadataServicesToolkit.war from `<tomcat_working_dir>`/MST-instances to `<tomcat_install_folder>`/webapps.