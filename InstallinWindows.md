# Installing the Metadata Services Toolkit in Windows #

## Downloading the Metadata Services Toolkit ##

Download the MST-instances-`*`.zip file [here](http://code.google.com/p/xcmetadataservicestoolkit/downloads/list).

Unzip and extract its contents into `<tomcat_working_dir>`.

### What Is the `<tomcat_working_dir>`? ###

The location of this directory differs depending upon the way Tomcat is installed. In Windows, if you use the Windows installer to install Tomcat, by default the root directory of the installation (e.g. C:\Program Files\Apache Software Foundation\Tomcat 6.0) is your `<tomcat_working_dir>`. Otherwise, if a .zip file is used to install Tomcat, then `<tomcat_folder_path>`\bin is your `<tomcat_working_dir>`. To change/view the Tomcat working directory (for the Windows installer), go to **Start** menu -> **Apache Tomcat 6.0** -> **Configure Tomcat**. Choose the **Startup** tab and change the **Working Path**.

In this document we are assuming that the MST is installed on Windows and a Windows installer is used to install Tomcat. In this case, the `<tomcat_working_dir>` is C:\Program Files\Apache Software Foundation\Tomcat 6.0, which is the location suggested by the installer, unless a different path is chosen.

Below is a screen showing the tomcat\_working\_dir with the MST release .zip file extracted into it.

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/tomcat6.0.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/tomcat6.0.jpg)

This will create a folder called "MST-instances" inside the tomcat\_working\_dir. "MST-instances" will contain an instance folder for each MST installation. The instance folder name should match the WAR file name. The path for the instance folder will be `<tomcat_working_dir>`\MST-instances\`<war_file_name>`.

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mstinstances.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mstinstances.jpg)

By default, the WAR file is called MetadataServicesToolkit.war, so the instance folder name is "MetatdataServicesToolkit." Here, the default path of the default instance would be: `<tomcat_working_dir>`\MST-instances\MetadataServicesToolkit. This is where all configuration files and folders for that instance of the MetadataServicesToolkit (MST) are located.

<a href='Hidden comment: 
===Changing the Default WAR File Name and Instance Folder Name to Enable Multiple Installations of the MST===

*_Note: Multiple Installations not currently supported!_*

Extract the .zip file to <tomcat_working_dir> and change the WAR file name to !MetadataServicesToolkit_ production.war. Next, change the instance folder name to <tomcat_working_dir>\MST-instances\!MetadataServicesToolkit_production to match the WAR file name.

There will be a need to change the default WAR file name and instance folder (<tomcat_working_dir>\MST-instances\!MetadataServicesToolkit) name in case of multiple installations where <tomcat_working_dir>\ MST-instances\!MetadataServicesToolkit already exists and contains configuration details of the !MetadataServicesToolkit.
'></a>

## Setting Up the Database ##

Open the MySQL command line client by going to **Start** menu -> **Program Files** -> **MySQL** -> **MySQL Server x.x** -> **MySQL Command Line Client**.

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlserver.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlserver.jpg)

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlcommandline.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlcommandline.jpg)

Enter the database password (the one given while installing the MySQL server in the previous section).

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlcommandline2.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlcommandline2.jpg)

You will now be given a MySQL prompt.

The next step is to run the script to create the database "MetadataServicesToolkit." The command to run the script is:

```
source <path_to_sql_script>
```

Where `<path_to_sql_script>` is the path where the SQL script is located.

The SQL script is located inside `<tomcat_working_dir>`\MST-instances\MetadataServicesToolkit\sql\create\_database\_script.sql

<a href='Hidden comment: 
_Note that in the case of multiple installations, you must open the create_database_script.sql and change the database name from default "!MetadataServicesToolkit" to a different name and then run the script._
'></a>

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlcommandline3.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlcommandline3.jpg)

This will create the database and the screen will resemble the one below.

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlcommandline4.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mysqlcommandline4.jpg)