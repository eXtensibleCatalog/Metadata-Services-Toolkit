truncate voyager_searches;
load data infile 'c:/dev/xc/mst/svn/trunk/scripts/voyager_import/voysearchlog20110919.csv'
into table voyager_searches
FIELDS TERMINATED BY ','
ENCLOSED BY '\'';
/* LINES TERMINATED BY '\\n'; */
select count(*) from voyager_searches;
