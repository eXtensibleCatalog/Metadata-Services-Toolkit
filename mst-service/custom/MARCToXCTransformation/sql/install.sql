create table marc_bibs_xc_manifestations (
	bib_man_id          int          not null      AUTO_INCREMENT,
	bib_001             char(50)     not null,
	record_id           BIGINT       not null,
	primary KEY (bib_man_id),
	KEY idx_marc_bibs_xc_manifestations_bib_001(bib_001),
	KEY idx_marc_bibs_xc_manifestations_bib_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table links (
	link_id          int          not null      AUTO_INCREMENT,
	from_record_id   bigint       not null,
	to_record_id     bigint       not null,
	primary KEY (link_id),
	KEY idx_links_from_record_id(from_record_id),
	KEY idx_links_to_record_id(to_record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
