svn test...

This file describes the steps to build the project.

Prerequisites:
--------------
1. Java SE development kit(JDK()
2. ANT 1.7.x
3. Apache Tomcat 6.x
4. MySQL Server 5.x

Download the source code from http://code.google.com/p/xcmetadataservicestoolkit/source/checkout

Say the project is downloaded to C:\MetadataServicsToolkit. Using command line prompt, go into the directory C:\MetadataServicsToolkit. ANT is used to build the project.
Run command 'ant' which will create the Zip files required for deployment. The Zip files will be created under 'C:\MetadataServicsToolkit\dist' folder. 
The 'ant' command will download Ivy and required JAR files and so it may take few minutes to complete.

Please follow the MetadataServicsToolkit Installation manual on http://code.google.com/p/xcmetadataservicestoolkit/downloads/list for deploying the build on a server. 
