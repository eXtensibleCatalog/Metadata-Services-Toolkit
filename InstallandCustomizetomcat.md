# Installing Apache Tomcat #

Apache Tomcat 6.0.x can be downloaded [here](http://tomcat.apache.org/download-60.cgi).
  * **Make sure you get version 6.  The MST has not been tested with version 7.**

The three modes of distribution of Tomcat are the .zip, .tar.gz, and Windows installer files. The first two (.zip and .tar.gz ) files are  self-extracting files. Installing Tomcat using one of these two files is as easy as extracting the files. The third option is applicable only to the Windows platform server.

## Steps To Install in Unix ##

Download the .tar.gz file or the .zip file onto a directory on the server. If you are using the .zip file, execute the following command to extract the contents:

```
unzip <zipFilename>
```

OR

If you are using the .tar.gz file, use the following commands in sequence:

```
gunzip <.tar.gzFilename>
```

This command will create a file with just the .tar file extension (stripping away the .gz). Using the following command will complete the extract process:

```
tar –xvf <.gzFilename>
```


## Steps To Install in Windows ##

A simple way to install Tomcat is by downloading and running the “Windows Service Installer” under Binary distributions, core.