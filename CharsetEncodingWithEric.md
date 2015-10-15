
```
Hi Eric,
Don't worry - it's not really a starter question.  There are so many layers, you really have to check each one.

Is your mysql table utf8?
$ mysql -t -u root --password=root -D your_db -e "show create table ameels" | grep 'CHARSET'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 |

Is the client connecting to mysql using utf8 when it's reading/writing?  In the command line - you have to set it explicitly (shown below).  I also had to set it explicitly in java/jdbc, so you might have to do the same in php. 
$  mysql -t -u root --password=root -e "show variables like '%char%'"
+--------------------------+---------------------------------------------------------+
| Variable_name            | Value                                                   |
+--------------------------+---------------------------------------------------------+
| character_set_client     | cp850                                                   |
| character_set_connection | cp850                                                   |
| character_set_database   | utf8                                                    |
| character_set_filesystem | binary                                                  |
| character_set_results    | cp850                                                   |
| character_set_server     | utf8                                                    |
| character_set_system     | utf8                                                    |
| character_sets_dir       | C:\Program Files\MySQL\MySQL Server 5.5\share\charsets\ |
+--------------------------+---------------------------------------------------------+

$  mysql --default-character-set=utf8 -t -u root --password=root -e "show variables like '%char%'"
+--------------------------+---------------------------------------------------------+
| Variable_name            | Value                                                   |
+--------------------------+---------------------------------------------------------+
| character_set_client     | utf8                                                    |
| character_set_connection | utf8                                                    |
| character_set_database   | utf8                                                    |
| character_set_filesystem | binary                                                  |
| character_set_results    | utf8                                                    |
| character_set_server     | utf8                                                    |
| character_set_system     | utf8                                                    |
| character_sets_dir       | C:\Program Files\MySQL\MySQL Server 5.5\share\charsets\ |
+--------------------------+---------------------------------------------------------+

is your url correct?
it looks like it is to me.

is your combination of apache/php parsing the query as utf8?
you could just spit the param value back out to check it.

is your http header correct?
$ curl -s -vv 'http://en.wikipedia.org/' 2>&1  | grep 'Content-Type'
< Content-Type: text/html; charset=utf-8

is what you are looking at correct?
This is one of the big gotchas.  I always check by using xxd (or od -x).  For instance I copied and pasted your url into my terminal:

$ echo -n 'الله' | xxd
0000000: d8a7 d984 d984 d987                      ........

so that's the utf8 value.  I wrote my own utf8 converter (there may be better ones now) which probably isn't the easiest thing to setup, but if you're interested it's here:
http://code.google.com/p/andersonbd1/source/browse/#svn%2Ftrunk%2Fhome%2Fscripts

$ convert_char_enc.sh d8 a7
char            utf8            utf16           utf32
----            ----            -----           -----
ا               d8a7            627             627

$ convert_char_enc.sh d9 84
char            utf8            utf16           utf32
----            ----            -----           -----
ل               d984            644             644

$ convert_char_enc.sh d9 87
char            utf8            utf16           utf32
----            ----            -----           -----
ه               d987            647             647

and then wikipedia is your friend:
http://en.wikipedia.org/wiki/Mapping_of_Unicode_character_planes
http://en.wikibooks.org/wiki/Unicode/Character_reference/0000-0FFF

That's how I know the characters you sent in the URL were correct.  So, I'd use a similar approach (using command line curl/mysql to pipe results to xxd) to make sure that your webserver/php is decoding the characters correctly and to make sure your data is in the database correctly.

Hope that helps.

-Ben


-----Original Message-----
From: James, Eric [mailto:eric.james@yale.edu]
Sent: Thursday, September 08, 2011 12:30 PM
To: Anderson, Benjamin D
Subject: php/mysql/utf-8

Ben,

I have a starter question.  I'm using php to query a mysql database with an Arabic query passed as a get parameter.

http://ameelmod.library.yale.edu/ameel/scripts/merscount4ameel.php?type=all&query=الله

But, before even getting to executing the query, I want to echo it to the browser using the code:

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"> </meta> </head> <body> <?php $query = utf8_encode($_GET["query"]); $camelQueryStr = "SELECT COUNT(*) as numRows FROM ameels as Ameel WHERE Ameel.hidden = 0 && CONCAT(Ameel.creator,Ameel.title,Ameel.subject,Ameel.description) like \"%".$query."%\""; echo $camelQueryStr; ...

But the result in the browser is question marks for the query:

SELECT COUNT(*) as numRows FROM ameels as Ameel WHERE Ameel.hidden = 0 && CONCAT(Ameel.creator,Ameel.title,Ameel.subject,Ameel.description) like "%????%"

Am I missing something?

-Eric
```