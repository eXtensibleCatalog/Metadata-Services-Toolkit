# Installing Multiple Instances of the Same Service #

There could be times where you might need to install and configure 2 distinct instances of the same service.  For instance, for the marcnormalization service you might want 2 different set up with different configuration options, for instance each one could have a different `OrganizationCode` set in the configuration file.

To do this simply follow these steps:
  1. Follow the instructions [here](Metadata.md) for adding services.
  1. Unzip the 1st copy of the service into the services directory.
  1. Rename this copy to something other than the default, for instance marcnormalization2
  1. Unzip the 2nd copy of the service into the services directory.  This instance does not have to be renamed.
  1. Configure each of the services with the desired options.