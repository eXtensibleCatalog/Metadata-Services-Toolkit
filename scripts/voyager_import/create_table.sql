drop table if exists voyager_searches;

create table voyager_searches (
  search_date     datetime,
  stat_string     varchar(255),
  session_id      varchar(255),
  search_type     varchar(255),
  search_string   varchar(255),
  limit_flag      varchar(255),
  limit_string    varchar(255),
  index_type      varchar(255),
  relevance       varchar(255),
  hyperlink       varchar(255),
  hits            varchar(255),
  search_tab      varchar(255),
  client_type     varchar(255),
  client_ip       varchar(255),
  dbkey           varchar(255),
  redirect_flag   varchar(255)

) ENGINE=MyISAM DEFAULT CHARSET=utf8;

