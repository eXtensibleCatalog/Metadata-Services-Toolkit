# Installing the MySQL Server #

The MySQL Community Server 5.x can be downloaded [here](http://dev.mysql.com/downloads/mysql/).
  * The MST has been reported to work with versions 5.0, 5.1, and 5.5 of MySQL

The installation documentation can be found [here](http://dev.mysql.com/doc/).

## Installing MySQL in Unix ##

This section uses the tar packages for installing MySQL on Solaris. Download the .tar.gz file or the .zip file onto a directory on the server. Use the following commands in sequence:

```
gunzip <.tar.gzFilename>
```

This command will create a file with just the .tar file extension (stripping away the .gz). Use the following command to complete the extract process:

```
tar –xvf <.gzFilename>
```


The above two methods will extract the tomcat files on the server. In order to install the server, follow the steps described [here](http://dev.mysql.com/doc/refman/5.5/en/binary-installation.html).

Note that installing MySQL is not enough, you'll need to configure the MST with the database password and port.  Continue with [this](InstallinUnix.md)



## Installing MySQL in Windows ##

The steps below are intended to assist users who are new to installing the MySQL server. Advanced users can install according to their own needs.

**Setup type:** Typical

**Configure MySQL server now:** Click the box and click the **Finish** button

**Configuration type:** Detailed configuration

**Server type:** Choose **Developer machine** if you are just trying out the MST. Choose **Server machine** if you want to run a production MST and the machine you are installing MySQL server is a web/application server. Choose **Dedicated MySQL server machine** if the machine you are installing the MySQL server on is dedicated only to MySQL and has no other servers running on it.

**Database usage:** Multifunctional database

**Approximate number of concurrent connections to server:** Decision support

**Networking options**

**Enable TCP/IP networking:** Click the box.

**Port number:** 3306. (By default it is 3306. Choose another port number if 3306 is in use.)

**Enable restrict mode:** Click the box.

**Default Character Set:** Select **Manual selected default character set/collation** and set the character set to **UTF8**.

**Install as windows service:** Click the box.

**Include bin directory in windows PATH:** Click the box.

**Security options:** The installation requires you to create a database user name (by default it is "root," but it can be changed) and password. **Please make a note of the user name and password which will later be needed to connect to the database.**