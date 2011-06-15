create table repo_name.records (
	record_id        int        NOT NULL,
	oai_datestamp     datetime,
	type             char(1),
	status           char(1),
	prev_status      char(1),
	format_id        int,

	PRIMARY KEY (record_id)
	
	/*
	,
	To increase performance on an initial load,
	I'm going to delay the creation of these indicies
	until the completion of the first harvest/service
	KEY idx_records_date_created (oai_datestamp),
	KEY idx_records_status (status),
	KEY idx_records_format_id (format_id)
	*/
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_updates (
	/*
	will add the id column later, with the indexes.
	on second thought, I do't know that we really need it
	id                    int        NOT NULL    AUTO_INCREMENT,
	*/
	record_id             int,
	date_updated          datetime

	/*,
	PRIMARY KEY (id),
	KEY idx_record_updates_date_updated (date_updated),
	KEY idx_record_id (record_id)
	*/
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.records_xml (
	record_id      int         not null,
	xml            longtext,
	
	PRIMARY KEY (record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_sets (
	record_id             int,
	set_id                int,
	
	PRIMARY KEY (record_id, set_id)
	/*
	KEY idx_records_set_record_id(record_id),
	KEY idx_records_set_set_id(set_id)
	*/
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_predecessors (
	/*
	id                    BIGINT        NOT NULL    AUTO_INCREMENT,
	*/
	record_id             BIGINT,
	pred_record_id        BIGINT,
	
	PRIMARY KEY (record_id, pred_record_id)
	/*,
	KEY idx_record_predecessors_record_id(record_id),
	KEY idx_record_predecessors_set_pred_record_id(pred_record_id)
	*/
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.properties (
	prop_key     varchar(255)      not null,
	value        varchar(255),

	primary key(prop_key)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.incoming_record_counts (
	incoming_record_count_id      int         NOT NULL    AUTO_INCREMENT,
	harvest_start_date            datetime    not null,
	type_name                     varchar(35) not null,

	new_act_cnt                   int    not null default 0,
	new_del_cnt                   int    not null default 0,
	upd_act_cnt                   int    not null default 0,
	upd_del_cnt                   int    not null default 0,

	upd_act_prev_act_cnt          int    not null default 0,
	upd_act_prev_del_cnt          int    not null default 0,

	upd_del_prev_act_cnt          int    not null default 0,
	upd_del_prev_del_cnt          int    not null default 0,

	unexpected_error_cnt          int    not null default 0,

	primary key (incoming_record_count_id),
	KEY idx_incoming_record_counts_type_name (type_name),
	unique key idx_incoming_record_counts_date_type(harvest_start_date, type_name)

) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.outgoing_record_counts (
	outgoing_record_count_id      int         NOT NULL    AUTO_INCREMENT,
	harvest_start_date            datetime    not null,
	type_name                     varchar(35) not null,

	new_act_cnt                   int    not null default 0,
	new_held_cnt                  int    not null default 0,
	new_del_cnt                   int    not null default 0,

	upd_act_cnt                   int    not null default 0,
	upd_held_cnt                  int    not null default 0,
	upd_del_cnt                   int    not null default 0,

	upd_act_prev_act_cnt          int    not null default 0,
	upd_act_prev_held_cnt         int    not null default 0,
	upd_act_prev_del_cnt          int    not null default 0,

	upd_held_prev_act_cnt         int    not null default 0,
	upd_held_prev_held_cnt        int    not null default 0,
	upd_held_prev_del_cnt         int    not null default 0,

	upd_del_prev_act_cnt          int    not null default 0,
	upd_del_prev_held_cnt         int    not null default 0,
	upd_del_prev_del_cnt          int    not null default 0,

	primary key (outgoing_record_count_id),
	KEY idx_outgoing_record_counts_type_name (type_name),
	unique key idx_outgoing_record_counts_date_type(harvest_start_date, type_name)

) ENGINE=MyISAM DEFAULT CHARSET=utf8;

