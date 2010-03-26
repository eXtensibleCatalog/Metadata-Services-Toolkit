drop table if exists records;
create table records (
	id             serial      primary key,
	service_id     int,
	identifier_1   char(10)    not null,
	identifier_2   char(10)    not null,
	datestamp      timestamp   not null,
	setSpec        char(10)    not null
) engine=MyISAM default charset=utf8;

create index records_identifier_2_idx on records(identifier_2);
create index records_datestamp_idx on records(datestamp);

/* this may have to change to a blob in the future */
drop table if exists records_xml;
create table records_xml (
	id             serial         primary key,
	xml            longtext
) engine=MyISAM default charset=utf8;
