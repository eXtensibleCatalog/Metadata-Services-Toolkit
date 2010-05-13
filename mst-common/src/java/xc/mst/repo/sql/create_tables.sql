create table REPO_NAME_RECORDS (
	record_id        int        NOT NULL    AUTO_INCREMENT,
	oai_pmh_id_4     char(35),
	oai_pmh_id_3     char(35),
	oai_pmh_id_2     char(35),
	oai_pmh_id_1     char(35),
	date_created     datetime,
	status           char(1),
	format_id        int,

	PRIMARY KEY (record_id),
	KEY idx_REPO_NAME_oai_pmh_id_4 (oai_pmh_id_4),
	KEY idx_REPO_NAME_date_created (date_created),
	KEY idx_REPO_NAME_status (status),
	KEY idx_REPO_NAME_format_id (format_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
