create table repo_name.records (
	record_id        int        NOT NULL,
	oai_datestamp     datetime,
	status           char(1),
	format_id        int
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_updates (
	record_id             int,
	date_updated          datetime
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.records_xml (
	record_id      int         not null,
	xml            longtext
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_sets (
	record_id             int,
	set_id                int
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table repo_name.record_predecessors (
	record_id             BIGINT,
	pred_record_id        BIGINT
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


CREATE TABLE repo_name.record_messages (
  record_message_id  BIGINT      NOT NULL AUTO_INCREMENT,
  rec_in_out         char(1)      not null,
  record_id          int          NOT NULL,
  msg_code           varchar(10)         not null,
  msg_level          varchar(100)      not null,
  service_id         int(11)      not null,
  detail             TEXT,    
  PRIMARY KEY (record_message_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

