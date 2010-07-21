create table repo_name.records (
	record_id        int        NOT NULL,
	oai_datestamp     datetime,
	status           char(1),
	format_id        int,

	PRIMARY KEY (record_id),
	KEY idx_REPO_NAME_records_date_created (oai_datestamp),
	KEY idx_REPO_NAME_records_status (status),
	KEY idx_REPO_NAME_records_format_id (format_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_updates (
	id                    int        NOT NULL    AUTO_INCREMENT,
	record_id             int,
	date_updated          datetime,
	PRIMARY KEY (id),
	KEY idx_REPO_NAME_record_updates_date_updated (date_updated)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.records_xml (
	record_id      int         not null,
	xml            longtext,
	PRIMARY KEY (record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_sets (
	record_id             int,
	set_id                int,
	PRIMARY KEY (record_id, set_id),
	KEY idx_REPO_NAME_records_set_record_id(record_id),
	KEY idx_REPO_NAME_records_set_set_id(set_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_predecessors (
	id                    BIGINT        NOT NULL    AUTO_INCREMENT,
	record_id             BIGINT,
	pred_record_id        BIGINT,
	PRIMARY KEY (id),
	KEY idx_REPO_NAME_records_set_record_id(record_id),
	KEY idx_REPO_NAME_records_set_pred_record_id(pred_record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
