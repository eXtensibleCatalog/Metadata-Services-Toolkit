create table repo_name.record_oai_ids (
    record_id        int          NOT NULL,
    oai_id           varchar(255) not null,
    
	PRIMARY KEY (record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;