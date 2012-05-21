create table repo_name.record_oai_ids (
    record_id        BIGINT       NOT NULL,
    oai_id           varchar(255) not null,

  PRIMARY KEY (record_id),
  KEY idx_oai_id (oai_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
