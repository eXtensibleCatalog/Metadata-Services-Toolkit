I sent this email to the oai-implementers list on 2011-02-01T09:09:00-05

```
Hi,

I'm wondering what others are doing when a ListRecords request w/out an until comes in.  Consider this scenario:

t0 - harvest request (with no until) is initiated
t1 - record 101 is added to the repo
t2 - harvest is finished (it took multiple requests to complete)

Should record 101 be included in the harvest data?  If not, will the client better issue their next harvest with a from=t0 (a from=t2 would be invalid because they'd miss out on record 101).

We have implemented both oai-pmh harvesters and providers, so I have to consider both ends of this.  Here's what I'm thinking...

As a Provider
I will simply lock the repo so that the above scenario can't happen.  If someone is already harvesting (there exist unexpired resumptionTokens) then I will not update the repository.

As a Harvester
I will always use the until parameter with the value of the time the harvest was initially started.

I think this keeps me clear of any problems.  Anyone else have thoughts or care to share your solutions?

Thanks,
Ben Anderson
```

there's more to this in the ongoing email thread.


## validation ##

http://re.cs.uct.ac.za/

this url should be prominent somewhere:
http://128.151.244.146:8080/MetadataServicesToolkit/st/marctoxctransformation/oaiRepository.action?verb=Identify

```
curl -s 'http://128.151.244.146:8080/MetadataServicesToolkit/st/marctoxctransformation/oaiRepository.action?verb=Identify' | xmllint --format - | xmllint --noout --schema ./mst-service/example/conf/OAI-PMH.xsd -
curl -s 'http://128.151.189.128:8080/MetadataServicesToolkit/st/marcnormalization/oaiRepository.action?verb=ListRecords&metadataPrefix=marcxml'  | xmllint --format - | xmllint --noout --schema ./mst-platform/src/webapp/all.xsd -
```