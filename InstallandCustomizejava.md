# Installing the Java SE Development Kit (JDK) #

### Download and install Java SE 6 ###
The Java SE Development Kit 6 (JDK) can be downloaded [here](http://java.sun.com/javase/downloads/index.jsp).
  * Make sure you get the JDK (not the JRE).
  * Make sure you get the latest Java SE 6.  The MST currently **does not work with Java SE 7**.

<a href='Hidden comment: 
The release notes to guide the installation can be found [http://java.sun.com/javase/6/webnotes/install/ here].

Choose from the manuals under the JDK column and select the appropriate platform used, e.g. Solaris/Linux/Windows. Also, make sure to select the correct [http://windowshelp.microsoft.com/Windows/en-US/help/41531554-d5ef-4f2c-8fb9-149bdc5c8a701033.mspx type of OS] (either 32 bit or 64 bit). The manual for 64 bit OS is labeled specifically.
'></a>

**_Note: Please install the Sun/Oracle JDK, even if you already have an OpenJDK installed.  Sun's JDK is much faster at XML parsing, so the MST will go slower with OpenJDK._**

<a href='Hidden comment: 
===Steps To Set the JAVA_HOME Environment Variable===

The JAVA_HOME environment variable holds the location of the root directory of a java install. You must set this environment variable if any one of the following is true:
*aYour operating System is Unix
*bYour operating System is Windows but Tomcat software (described in the next section) is *not* installed using the Windows Service Installer. (We recommend installing Tomcat using the Windows Service Installer, which is described in the next section. If you plan to follow those instructions, you may skip this section.)

The sole purpose of setting up this environment variable is to let Tomcat know about the Java installed on the server machine.

===Unix===

Before setting up the environment variable, make sure you have the path to the correct version of Java.

In Unix, all environment variables are stored in a file named “.profile,”, (for Bash and Ksh), which is usually stored in the root directory of the user. It is a hidden file, so it is not visible with a command to list files (e.g. ls -lrt). You must use the following command to see it:

```
ls -lart
```

Once you have located the file, use a familiar editor to create the environment variable. The following screens show it using the vi editor.

http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/putty.jpg

http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/putty2.jpg

Save the changes to the file and start a new terminal so that the changes take effect. To check if the environment variable has been set up, key in the following command to verify:

```
echo $JAVA_HOME
```

http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/putty3.jpg

====Unix Ubuntu====

Add the following line to /etc/apparmor.d/usr.sbin.mysqld:
```
 /var/lib/tomcat6/MST-instances/*/db_load.in r,
```

Reload the mysqld profile with the following command:
```
 sudo apparmor_parser -r /etc/apparmor.d/usr.sbin.mysqld
```

===Windows:===

http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/systemproperties.jpg

Go to the *Start* menu -> right click on *My Computer* and select *Properties*. Select the *Advanced* tab and click on the *Environment Variables* button.

http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/environmentvariables.jpg

Add a new System variable:
*a*Variable name:* JAVA_HOME
*b*Variable value:* <Path where JDK is installed> (For example: C:\Program Files\Java\jdk1.6.0_12)

Click *OK*.

http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/editsystemvariable.jpg

'></a>