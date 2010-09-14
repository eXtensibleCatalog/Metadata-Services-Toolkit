-- -------------------------------------------------------------
-- Table structure for Transformation service
-- -------------------------------------------------------------

-- -------------------------------------------------------------
-- Table structure for bibliographic_manifestation
-- -------------------------------------------------------------

-- ben's custom tables --
create table bibsProcessedLongId (
	bib_001             BIGINT       not null,
	record_id           BIGINT       not null,
	KEY bibsProcessedLongId_bib_001(bib_001),
	KEY bibsProcessedLongId_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table bibsProcessedStringId (
	bib_001             char(50)     not null,
	record_id           BIGINT       not null,
	KEY bibsProcessedStringId_bib_001(bib_001),
	KEY bibsProcessedStringId_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table bibsYet2ArriveLongId (
	bib_001             BIGINT       not null,
	record_id           BIGINT       not null,
	KEY bibsYet2ArriveLongId_bib_001(bib_001),
	KEY bibsYet2ArriveLongId_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table bibsYet2ArriveStringId (
	bib_001             char(50)     not null,
	record_id           BIGINT       not null,
	KEY bibsYet2ArriveStringId_bib_001(bib_001),
	KEY bibsYet2ArriveStringId_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table links (
	link_id          int          not null      AUTO_INCREMENT,
	from_record_id   bigint       not null,
	to_record_id     bigint       not null,
	primary KEY (link_id),
	KEY idx_links_from_record_id(from_record_id),
	KEY idx_links_to_record_id(to_record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
