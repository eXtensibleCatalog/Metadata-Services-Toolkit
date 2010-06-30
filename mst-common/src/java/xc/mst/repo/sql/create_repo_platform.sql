create table REPOS (
	repo_id          int          not null      AUTO_INCREMENT,
	repo_name        varchar(25)  not null,
	service_id       int,
	provider_id      int,
	PRIMARY KEY (repo_id),
	KEY idx_repos_repo_name (repo_name),
	KEY idx_repos_service_id (service_id),
	KEY idx_repos_provider_id (provider_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
