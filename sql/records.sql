drop table if exists records;
create table records (
	id             serial      primary key,
	service_id     int,
	identifier     char(50)    not null,
	datestamp      timestamp   not null,
	setSpec        char(50)    not null
) engine=MyISAM default charset=utf8;

/* this may have to change to a blob in the future */
drop table if exists records_xml;
create table records_xml (
	id             serial         primary key,
	xml            varchar(21842)
) engine=MyISAM default charset=utf8;
