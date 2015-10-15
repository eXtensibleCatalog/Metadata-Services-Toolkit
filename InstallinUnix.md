# Installing the Metadata Services Toolkit in Unix #

## Downloading the Metadata Services Toolkit ##

Download the MST-instances-`*`.zip file [here](http://code.google.com/p/xcmetadataservicestoolkit/downloads/list).

Unzip and extract the contents into `<tomcat_working_dir>`.

### What Is the `<tomcat_working_dir>`? ###

The location of this directory differs depending upon the way Tomcat is installed. If a .zip file is used to install Tomcat then `<tomcat_folder_path>`\bin is your `<tomcat_working_dir>`.

This document assumes that MST is installed on Unix and a .zip file is used to install Tomcat. In this case, the `<tomcat_working_dir>` is /usr/local/tomcat /bin. Below is a screen showing the tomcat\_working\_dir with the MST release .zip file extracted into it.

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/sshsecureshell.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/sshsecureshell.jpg)

This will create a folder called "MST-instances" inside the tomcat\_working\_dir. "MST-instances" will contain an instance folder for each MST installation. The instance folder name should match the WAR file name. The path for the instance folder will be `<tomcat_working_dir>`\MST-instances\`<war_file_name>`. **_Make sure MST-instances is inside of `<tomcat_working_dir/bin>`._**

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/sshsecureshell2.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/sshsecureshell2.jpg)

By default, the WAR file is called MetadataServicesToolkit.war, so the instance folder name is "MetatdataServicesToolkit." Here, the default path of the default instance would be: `<tomcat_working_dir>`\MST-instances\MetadataServicesToolkit. This is where all configuration files and folders for that instance of MetadataServicesToolkit (MST) are located.

<a href='Hidden comment: 
===Changing the Default WAR File Name and Instance Folder Name to Enable Multiple Installations of the MST===

*_Note:  Multiple Installations not currently supported!_*

Extract the .zip file to <tomcat_working_dir> and change the WAR file name to !MetadataServicesToolkit_ production.war.  Next, change the instance folder name to <tomcat_working_dir>\MST-instances\!MetadataServicesToolkit_production to match the WAR file name.

There will be a need to change the default WAR file name and instance folder (<tomcat_working_dir>\MST-instances\!MetadataServicesToolkit) name in case of multiple installations where <tomcat_working_dir>\ MST-instances\!MetadataServicesToolkit already exists and has configuration details of the !MetadataServicesToolkit.
'></a>

## Setting Up the Database ##

Go to the folder where MySQL is installed. In this case, it is /usr/local/mysql. To log into the MySQL server, use following command:

```
mysql --user=<user_name> --password=<password>
```

Use the username and password given while installing MySQL server.

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/sshsecureshell3.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/sshsecureshell3.jpg)

You have now successfully logged into the MySQL server.

Run the script to create the database "MetadataServicesToolkit." The command to run the script is:

```
source <path_to_sql_script>
```

Where `<path_to_sql_script>` is the path where the SQL script is located.

The SQL script is inside `<tomcat_working_dir>`/MST-instances/MetadataServicesToolkit/sql/create\_database\_script.sql.

Now is a good time to mention that you'll probably want to change the database password, and the MST needs to know what it is.

Change db password, and put it into the install.properties file:

```
vi ./MST-instances/MetadataServicesToolkit/install.properties
```


_Note that in the **possible future** case of multiple installations, you must open the create\_database\_script.sql and change the database name from default "MetadataServicestoolkit" to a different name and then run the script._

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/sshsecureshell4.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/sshsecureshell4.jpg)

# What about installing MST using the tomcat package included in the Linux distribution (distro)? #

Up until now, the instructions for installing MST assumed that apache tomcat was installed manually via the .zip (or tar.gz) files downloaded from the apache tomcat website. This is the easiest way to install MST because the environment is very consistent across different Linux operating systems. Installing MST on a particular distro, however, is dependent on the particular distro itself.  Below, I will jot down some notes on what I had to do in order to install MST on a particular distro, Ubuntu 10.04.4 LTS. It is possible some of these steps may apply to other distros, but I expect there will be differences. In the future, if someone is able to successfully install MST on a different distro -- and is willing to share this information on this wiki -- it can be added to the list below.

## Notes on installing MST under tomcat6 on Ubuntu 10.04.4 LTS ##

```


# install MST 
cd /var/lib/tomcat6
sudo unzip [path/to/downloads]/MST-instances-1.0.4.zip
cd MST-instances
sudo cp MetadataServicesToolkit.war /var/lib/tomcat6/webapps/

# install services
cd /var/lib/tomcat6/MST-instances/MetadataServicesToolkit/services
sudo unzip [path/to/downloads]/[service name].zip

# allow MST (via tomcat6) access privileges:
sudo chown -R tomcat6 /var/lib/tomcat6/MST-instances

# add MST libraries to the java JVM classpath
cd /usr/share/tomcat6/bin/
sudo cat > setenv.sh <<EOF
CLASSPATH=/var/lib/tomcat6/MST-instances/MetadataServicesToolkit/lib
export CLASSPATH
EOF
sudo chmod a+x setenv.sh

# if apparmor is installed, mysql will not be able to load database files created by MST
# one way to enable this is to put mysqld into "complain mode" which will log access
# instead of prohibiting access:
sudo aa-complain /usr/sbin/mysqld
#[Setting /usr/sbin/mysqld to complain mode.]
sudo /etc/init.d/apparmor reload
#[Reloading AppArmor profiles : done.]

# chances are you'll need to increase the memory usage for tomcat6
# edit /etc/init.d/tomcat6
# near the top of the script, set the JAVA_OPTS variable:
#
JAVA_OPTS="-server -d64 -Xms12g -Xmx12g"

# set up a more efficient garbage collector
# edit /etc/default/tomcat6
# Use a CMS garbage collector for improved response time
JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC"

# increase the system limits for user tomcat6
# see: http://code.google.com/p/xcmetadataservicestoolkit/wiki/ServerConfig 
# sudo vi /etc/security/limits.conf
# add the following entries:
@tomcat6  hard    nofile  262140
@tomcat6  soft    nofile  262140
@tomcat6  hard    core  1
@tomcat6  soft    core  1
@tomcat6  hard    priority  20
@tomcat6  soft    priority  20
@tomcat6  hard    sigpending  46778
@tomcat6  soft    sigpending  46778
@tomcat6  hard    memlock  unlimited
@tomcat6  soft    memlock  unlimited

# load pam_limits.so
# see: http://code.google.com/p/xcmetadataservicestoolkit/wiki/ServerConfig
# sudo vi /etc/pam.d/common-session
session required pam_limits.so

# I notice the following mysql exceptions during idle time at night:
# Can not read response from server. Expected to read 4 bytes, read 0 bytes before connection was unexpectedly lost.
# the following parameter changes in my.cnf seemed to have helped:
# sudo vi /etc/mysql/my.cnf
interactive_timeout=180 # "No.of sec. a server waits for activity on interactive connection before closing it"
wait_timeout=180 # "No. of sec. a server waits for an activity on a connection before closing it"
max_connect_errors=9999 # "More than this number of interrupted connections from a host this host will be blocked from further connections"
skip-name-resolve # "Don't resolved host names. All host names are IP's"
###############

# then, reboot the server:
sudo init 6

```