# Browsing and Setting Up the Metadata Services Toolkit #

Only users with "Configuration" permission can perform the operations described in this section (aside from logging in and editing their account settings). Permissions are described in detail in the ["Managing Users"](UserManagement.md) section, but for now it is enough to know that all administrator accounts have this permission.


## Logging In ##

Open your browser and enter the URL `http://<host_name>:<port_number>/<war_file_name>/index.jsp`.

By default, the URL is http://localhost:8080/MetadataServicesToolkit/index.jsp.

The log in screen will be displayed.

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mst.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mst.jpg)

The MST comes pre-configured with an administrator account:

Enter user name : admin

Enter the password : mst4world

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mst2.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mst2.jpg)

You are now logged into the Metadata Services Toolkit application.

## My Account ##

Click on **My Account** in the upper right hand corner.

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mst3.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mst3.jpg)

Enter a valid email address for "admin." (The default email address "MST\_admin@mst.com" is a dummy address.) A valid admin email is required in order to send emails when a new user registers, to reset the admin password if it is forgotten, and to be notified when an error occurs in MST. For security reasons, change the password by clicking on the **Change Password** button.

## Setting Up Email ##

Click on the **Configuration** menu and select **Email Configuration**. Enter the information to set up email for the application.

_Note that if no email information is set up, then users cannot register with the system, neither new local users nor new LDAP users, and the email feature will not be functional._

![http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mst4.jpg](http://www.extensiblecatalog.org/doc/MST/MSTWikiPics/mst4.jpg)

## Configuring the LDAP ##

Optionally, you can configure the MST to use an LDAP server for external authentication. To do so, hover over **Configuration** and select **LDAP Configuration**.  Fill in the name your institution uses to refer to the LDAP server in the **Display Name** box (this name will appear in the user interface above the username and password boxes).  The **Server URL** box is for the URL of the LDAP server and the 'Port' is the port on which it can be accessed. The "Username Fieldname" is the field on the LDAP server that contains the username, and 'LDAP Start' is the location on the LDAP server in which the users are stored. If the LDAP server contains a form users may fill out if they forgot their password, you can configure the MST to link to it. To do so, select **Yes** next to **Show Forgot Password URL** and enter the URL in the **Forgot Password URL** box. After all the information has been entered, click the **Update LDAP Server** button. To remove LDAP support after it has been configured, click the **Clear LDAP Server** button.

The server name should start with something like "LDAP://".