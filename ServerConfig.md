#### AppArmor ####
We've found issues with running the MST with AppArmor (in Ubuntu).  This fix was submitted by a user and is reported to have fixed any issues.

Add the following line to /etc/apparmor.d/usr.sbin.mysqld:
```
 /var/lib/tomcat6/MST-instances/*/db_load.in r,
```

Reload the mysqld profile with the following command:
```
 sudo apparmor_parser -r /etc/apparmor.d/usr.sbin.mysqld
```

#### Hard Disk Space ####
Since the MST requires a good amount of [hard disk space](HardwareRequirements.md), we've taken the approach of adding a massive drive and mounting it at /xc.  This requires pointing both Tomcat and MySQL to this location.
```xml

$ ls -lad /xc/*

drwxr-xr-x  3 tomcat6   admin       4096 2011-05-17 15:44 /xc/MST-instances
drwxr-xr-x  8 mysql     mysql       4096 2011-08-12 14:26 /xc/mysql

$ cat ${TOMCAT_HOME}/MetadataServicesToolkit/WEB-INF/classes/env.properties
mst.root.dir=/xc/

$ mysql -u root --password=root -D xc_marctoxctransformation -e "show variables like '%datadir%'"
+---------------+------------+
| Variable_name | Value      |
+---------------+------------+
| datadir       | /xc/mysql/ |
+---------------+------------+

```

#### File/Folder Permissions ####
Make sure the user you start tomcat with has permissions to the MST-instances folder.  We've been bit by this more than a few times.  Eventually, we'd like to add a dummy-proof feature that displays a message to the admin user in the UI stating "you're permissions aren't acceptable", but for now it's just an exception buried in the MST\_General\_log.txt file.

#### Slow Harvesting ####
We needed to disable our antivirus software to get harvesting to occur at a reasonable speed.

#### SUSE Linux Enterprise ####

A number of changes were required to run on this version: SUSE Linux Enterprise Server 11 (x86\_64).

Prior to making these changes, received Java 'Out of Memory' exceptions when solr indexing and optimization were occurring, as well as '(Too many open files)'

##### Specific Changes Required #####

The following are in /etc/profile.local, so they get set when the shell is entered. Note that if you run Tomcat as an unprivileged user (which you should), you'll get errors with some of the ulimit commands when you become that user. That's because you need to be root to set some of them. In order for them to take effect, first become root, then su to your Tomcat user.


```
devuser@rclxcmst:/usr/local/tomcat/bin> ulimit -a

core file size          (blocks, -c) 1
data seg size           (kbytes, -d) unlimited
scheduling priority             (-e) 0
file size               (blocks, -f) unlimited
pending signals                 (-i) 46778
max locked memory       (kbytes, -l) unlimited
max memory size         (kbytes, -m) 5097192
open files                      (-n) 1000000
pipe size            (512 bytes, -p) 8
POSIX message queues     (bytes, -q) 819200
real-time priority              (-r) 0
stack size              (kbytes, -s) 8192
cpu time               (seconds, -t) unlimited
max user processes              (-u) unlimited
virtual memory          (kbytes, -v) unlimited
file locks                      (-x) unlimited
```

#### Ubuntu ####

A number of changes were required to run on this version: Ubuntu 10.04.3 LTS.

Prior to making these changes, received Java '(Too many open files)' exceptions when solr indexing.

This was on a system that had to process many records.  To give an order of magnitude on resources needed vs. number of records, these records were processed as a repository, through normalization then aggregation.  This is count of them in aggregation, the NULL records represent authority records.

```
+------+--------+----------+
| type | status | count(*) |
+------+--------+----------+
| NULL | A      | 17106235 |
| b    | A      |  5769331 |
| h    | A      |  6956223 |
| b    | D      |    75497 |
+------+--------+----------+
```



###### Ubuntu Version ######
jbrand@xc-brand:/etc/pam.d$ lsb\_release -a
No LSB modules are available.
Distributor ID: Ubuntu
Description:    Ubuntu 10.04.3 LTS
Release:        10.04
Codename:       lucid


##### Specific Changes Required #####
Followed [http://posidev.com/blog/2009/06/04/set-ulimit-parameters-on-ubuntu/
> this] article for how to increase file open limit. Note that just increasing files did not work, also had to bump up the other parameters, similar to the SUSE experience above.

```
sudo edit /etc/security/limits.conf
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
```

Second you have to add a line in the /etc/pam.d/common-session file:

```
$ sudo vi /etc/pam.d/common-session
```

Then add the line:

```
session required pam_limits.so
```

Now after rebooting you can see in the terminal with ulimit -a the change.

```
jbrand@xc-brand:/xc/MST-instances/MetadataServicesToolkit/services$ ulimit -a
core file size          (blocks, -c) 1
data seg size           (kbytes, -d) unlimited
scheduling priority             (-e) 20
file size               (blocks, -f) unlimited
pending signals                 (-i) 46778
max locked memory       (kbytes, -l) unlimited
max memory size         (kbytes, -m) unlimited
open files                      (-n) 262140
pipe size            (512 bytes, -p) 8
POSIX message queues     (bytes, -q) 819200
real-time priority              (-r) 0
stack size              (kbytes, -s) 8192
cpu time               (seconds, -t) unlimited
max user processes              (-u) unlimited
virtual memory          (kbytes, -v) unlimited
file locks                      (-x) unlimited
```