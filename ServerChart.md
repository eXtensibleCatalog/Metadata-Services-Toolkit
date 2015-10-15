### Server Statuses ###
**actively used in MST development cycle**
|**server**|**current status**|
|:---------|:-----------------|
|136       |Contains first initial load and first update test where the transformation records were all screwy.  This machine has problems as well.  It has had to be reset several times because it becomes unresponsive.  Now after a reset, mysql is failing about 2 out of 3 queries.  Ralph is looking into it.|
|146       |John and Ben have both added fixes since the 136 failure.  John will be deploying our latest code here and we're hoping the update will work.  He will be running the harvest from the file system.|
|224       |Miscellaneous MST testing.  Now has MST 1.3.0 with T0.|
|247       |MST testing.  Now has MST 1.2.1 with T2.|

**outside of the MST development cycle**
|**server**|**notes**|
|:---------|:--------|
|133       |Ralph is using for production testing|
|170       |public demo|

**OAI Toolkit hosts**
|**server**|**notes**|
|:---------|:--------|
|137       |virtual, 2 2400MHz processors (psrinfo -v), 3840 MBytes memory (/usr/sbin/prtconf | grep Memory)|
|135       |         |

### Servers Specs ###
|**IP Address**|**Server name**|**Disk Space**|**Processor Speed**|**RPMs**|OS|**Processors**|**CPU Type**|**Physical or Virtual**|**RAM**|
|:-------------|:--------------|:-------------|:------------------|:-------|:-|:-------------|:-----------|:----------------------|:------|
|128.151.244.170|urxcmst        |168           |1.5 GHz            |10K RPM |Solaris 10 (64-bit)|2             |SPARC V9    |Physical               |8192 M |
|128.151.244.133|xcmst3         |112           |3 GHz              |10K RPM |SLES 11 (64-bit)|2             |	Intel Xeon |Virtual                |4592 M |
|128.151.244.136|xcmst2         |125           |2.40 GHz           |10K RPM |SLES 11 (64-bit)|2             |Intel Xeon E7340|Virtual                |4096 M |
|128.151.244.146|xcmst          |75            |3 GHz              |10K RPM |Solaris 10 (64-bit)|2             |Intel Xeon 5160|Virtual                |4948 M |
|192.17.55.224 |CARLI          |200           |quad-core 2.7 Ghz  |??      |Ubuntu Server 10.04.2 LTS 64-bit|2             |??          |Virtual                |3964 M |
|192.17.55.225 |CARLI          |200           |quad-core 2.7 Ghz  |??      |Ubuntu Server 10.04.2 LTS 64-bit|2             |??          |Virtual                |3964 M |