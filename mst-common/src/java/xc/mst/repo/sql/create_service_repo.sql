create table repo_name.record_links (
	from_record_id   bigint       not null,
	to_record_id     bigint       not null,
	KEY idx_links_from_record_id(from_record_id),
	KEY idx_links_to_record_id(to_record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
