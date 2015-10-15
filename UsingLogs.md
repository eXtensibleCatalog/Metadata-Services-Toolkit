# Overview #

Add your content here.


# Details #

Log usage content to be expanded over time

Service Logs - Each Service in the MST has a service summary log. Each batch of incoming records processed will have the following:
  * incoming with time stamp and record type and a series of columns that describe various service activities that have occurred (descriptions of columns below)
  * Incoming all time summary section
  * Outgoing with time stamp and record type
  * Outgoing all time and record type
  * Info section showing total of record types and record status


Note:  If a service receives a marked deleted record that it has never seen before, it will take no action, and will not log any information about those records.