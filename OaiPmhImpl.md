The MST works this way:

Harvester sends an initial request for all records from provider starting from beginning of time and ending now - which will be calculated at the provider end (if not supplied by the harvester)
<br />
Provider receives request at time A and generates a list of all records with "last updated" timestamps in the range of "beginning-of-time" through "A."  Provider then sends records on that list until it is finished.  If records are added to the provider repo before all the records are harvested, it doesn't matter because the list was generated when the provider received the request, and any new records are ignored.  If records change that are on the list, we just send the updated versions.
<br />
24 hours go by and harvester starts up again.
<br />
Harvester sends a request for all records from timestamp A until now.
<br />
Provider receives the request at time B.  It then generates a list of all records with "last updated" timestamps in the range of time A through time B.  It then sends those records back to the harvester.
<br />
The MST has had to consider the oai-pmh protocol from both ends (harvester and provider).  As a harvester, I think it’s best to always use the from and until params.  As a provider, we can’t force that, but we can suggest it.
<br />
Being in different timezones shouldn’t matter because it’s all based on UTC.  If the provider’s clock is behind the harvester’s clock by an greater amount than the interval between harvests AND the harvester doesn’t use the until param, then there will be an issue.  That’s the only potential problem I can come up with.