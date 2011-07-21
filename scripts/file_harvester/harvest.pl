#!/usr/bin/perl
use LWP::UserAgent;
use HTML::Parser;

$URL = 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&metadataPrefix=marc21';
$RES_URL = 'http://128.151.244.137:8080/OAIToolkit/oai-request.do?verb=ListRecords&resumptionToken=';

my $ua = new LWP::UserAgent;
$ua->timeout(300);

my $request = HTTP::Request->new('GET');
$request->url($URL);

my $now;
my $before = time;

my $response = $ua->request($request);

while ($response->content =~ /<resumptionToken cursor=".+" completeListSize=".+">([^<]+)<\/resumptionToken>/) {
   my $token = $1;

   $now = time;
   print ($now - $before);
   print " seconds\n";

   ###print "requesting ${RES_URL}${token} ...\n";
   $request->url($RES_URL . $token);
   $before = time;
   $response = $ua->request($request);
}



