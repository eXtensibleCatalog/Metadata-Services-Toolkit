# Uninstalling the MST and Reinstalling a New Version from Scratch #

In some cases, it may be necessary to completely uninstall the MST software and all of the metadata contained within it. This action may become necessary whenever extensive changes are being made to the software code. Follow the following instructions to uninstall and reinstall the MST:
  1. Delete the ‘MST-instances’ folder under tomcat or tomcat/bin, depending on how Tomcat was installed. (For Windows, Tomcat is usually in C:\Program Files\Apache Software Foundation\Tomcat 6.0.)
  1. Delete the `MetadataServicesToolkit.war` and MetadataServicesToolkit folder under tomcat/webapps.
  1. Follow the instructions in the corresponding sections to reinstall the MST [in Windows](InstallinWindows.md) or [in Unix](InstallinUnix.md).