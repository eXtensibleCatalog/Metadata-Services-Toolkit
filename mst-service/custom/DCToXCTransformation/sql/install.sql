create table example.bibsProcessedLongId (
	bib_001             BIGINT       not null,
	record_id           BIGINT       not null,
	KEY bibsProcessedLongId_bib_001(bib_001),
	KEY bibsProcessedLongId_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table example.bibsProcessedStringId (
	bib_001             char(50)     not null,
	record_id           BIGINT       not null,
	KEY bibsProcessedStringId_bib_001(bib_001),
	KEY bibsProcessedStringId_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table example.bibsYet2ArriveLongId (
	bib_001             BIGINT       not null,
	record_id           BIGINT       not null,
	KEY bibsYet2ArriveLongId_bib_001(bib_001),
	KEY bibsYet2ArriveLongId_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table example.bibsYet2ArriveStringId (
	bib_001             char(50)     not null,
	record_id           BIGINT       not null,
	KEY bibsYet2ArriveStringId_bib_001(bib_001),
	KEY bibsYet2ArriveStringId_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
