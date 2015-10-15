# Starting the Server in Unix #

Go to `<tomcat_install_folder>`/bin. In this case it is usr/local/tomcat/bin. **_(make sure you run startup.sh from the bin directory)_** Run the server using the following command:

```
startup.sh
```

The amount of memory you will need to allocate is dependent on how many records you will be processing and which services you have installed.  We have found the above number to work well on our 5.9 million record set.  For instructions on how to allocate the memory required, please go [here](http://code.google.com/p/xcmetadataservicestoolkit/wiki/InstallandCustomizetomcat).

### Stopping the Server ###

_Stop the server only when you want to bring down Metadata Services Toolkit._

The server can be stopped using the following command:

```
shutdown.sh
```

Tomcat does not always shut down properly, so to be sure it has exited, you might want to enter the following command (be careful not to kill non-MST Java processes):

```
$ ps -ef | grep "java.*Xms....M" | awk '{print $2}' | xargs kill -9
```