-- -------------------------------------------------------------
-- Create database MetadataServicesToolkit
-- -------------------------------------------------------------

DROP DATABASE IF EXISTS MetadataServicesToolkit;

CREATE DATABASE IF NOT EXISTS MetadataServicesToolkit DEFAULT CHARACTER SET utf8;

USE MetadataServicesToolkit;

-- -------------------------------------------------------------
-- Table structure for servers
-- -------------------------------------------------------------

DROP TABLE IF EXISTS servers;
CREATE TABLE servers
(
  server_id INT(11) NOT NULL AUTO_INCREMENT,
  url VARCHAR(127) NOT NULL,
  name VARCHAR(127) NOT NULL,
  type INT(11) NOT NULL,  
  port INT(11) NOT NULL,
  username_attribute VARCHAR(127) NOT NULL,
  start_location VARCHAR(127) NOT NULL,
  institution VARCHAR(127),
  forgot_password_url VARCHAR(127),
  forgot_password_label VARCHAR(63),
  show_forgot_password_link BOOLEAN NOT NULL DEFAULT false,

  PRIMARY KEY(server_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table 'users';
-- -------------------------------------------------------------

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
  user_id INT(11) NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  password VARCHAR(63),
  email VARCHAR(255) NOT NULL,
  server_id INT(11) NOT NULL,
  last_login DATETIME,
  account_created DATETIME NOT NULL,
  failed_login_attempts INT(11), 	
  
  PRIMARY KEY (user_id),
  INDEX idx_users_server_id (server_id),
  FOREIGN KEY(server_id) REFERENCES servers(server_id) ON DELETE CASCADE ON UPDATE CASCADE  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table formats
-- -------------------------------------------------------------

DROP TABLE IF EXISTS formats;
CREATE TABLE formats
(
  format_id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(127) NOT NULL,
  namespace VARCHAR(255) NOT NULL,
  schema_location VARCHAR(255) NOT NULL,
  
  PRIMARY KEY(format_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table providers;
-- -------------------------------------------------------------

DROP TABLE IF EXISTS providers;
CREATE TABLE providers
(
  provider_id INT(11) NOT NULL AUTO_INCREMENT,
  created_at DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  name VARCHAR(255) NOT NULL,
  oai_provider_url VARCHAR(255) NOT NULL,
  user_id INT(11) NOT NULL,
  title VARCHAR(127),
  creator VARCHAR(127),
  subject VARCHAR(63),
  description TEXT,
  publisher VARCHAR(127),
  contributors VARCHAR(511),
  date DATETIME,
  type VARCHAR(11),
  format VARCHAR(63),
  identifier INT(11),
  language VARCHAR(15),
  relation VARCHAR(255),
  coverage VARCHAR(255),
  rights VARCHAR(255),
  service BOOLEAN NOT NULL DEFAULT true,
  next_list_sets_list_formats DATETIME,
  protocol_version VARCHAR(7),
  last_validated DATETIME,
  identify BOOLEAN NOT NULL DEFAULT true,
  listformats BOOLEAN NOT NULL DEFAULT true,
  listsets BOOLEAN NOT NULL DEFAULT true,
  warnings INT(11) NOT NULL DEFAULT 0,
  errors INT(11) NOT NULL DEFAULT 0,
  records_added INT(11) NOT NULL DEFAULT 0,
  records_replaced INT(11) NOT NULL DEFAULT 0,
  last_oai_request VARCHAR(511),
  last_harvest_end_time DATETIME,
  last_log_reset DATETIME,
  log_file_name VARCHAR(255) NOT NULL,

  PRIMARY KEY (provider_id ),

  INDEX idx_providers_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for harvest_schedules
-- -------------------------------------------------------------

DROP TABLE IF EXISTS harvest_schedules;
CREATE TABLE harvest_schedules
(
  harvest_schedule_id INT(11) NOT NULL AUTO_INCREMENT,
  schedule_name VARCHAR(127),
  recurrence VARCHAR(127),
  provider_id INT(11) NOT NULL,
  start_date DATETIME,
  end_date DATETIME,
  minute INT(11),
  day_of_week INT(11),
  hour INT(11),
  notify_email VARCHAR(127),

  PRIMARY KEY (harvest_schedule_id),

  INDEX idx_harvest_schedules_hour (hour),
  INDEX idx_harvest_schedules_minute (minute),
  INDEX idx_harvest_schedules_day_of_week (day_of_week),
  INDEX idx_harvest_schedules_start_date (start_date),
  INDEX idx_harvest_schedules_end_date (end_date),
  INDEX idx_harvest_schedules_provider_id (provider_id),
  FOREIGN KEY (provider_id) REFERENCES providers(provider_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table harvest_schedule_steps
-- -------------------------------------------------------------

DROP TABLE IF EXISTS harvest_schedule_steps;
CREATE TABLE harvest_schedule_steps
(
  harvest_schedule_step_id INT(11) NOT NULL AUTO_INCREMENT,
  harvest_schedule_id INT(11) NOT NULL, 
  format_id INT(11) NOT NULL,
  set_id INT(11),
  last_ran DATETIME,

  PRIMARY KEY (harvest_schedule_step_id),
  
  INDEX idx_harvest_schedules_step_harvest_schedule_id(harvest_schedule_id),
  FOREIGN KEY (harvest_schedule_id) REFERENCES harvest_schedules(harvest_schedule_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_harvest_schedules_step_set_id (set_id),
  INDEX idx_harvest_schedules_step_format_id (format_id),
  FOREIGN KEY (format_id) REFERENCES formats(format_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table harvests
-- -------------------------------------------------------------

DROP TABLE IF EXISTS harvests;
CREATE TABLE harvests
(
  harvest_id INT(11) NOT NULL AUTO_INCREMENT,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP,
  request TEXT,
  result LONGTEXT,
  harvest_schedule_id INT(11) NOT NULL,

  PRIMARY KEY (harvest_id),

  INDEX idx_harvests_harvest_schedule_id (harvest_schedule_id),
  FOREIGN KEY (harvest_schedule_id) REFERENCES harvest_schedules(harvest_schedule_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table logs
-- -------------------------------------------------------------

DROP TABLE IF EXISTS logs;
CREATE TABLE logs
(
  log_id INT(11) NOT NULL AUTO_INCREMENT,
  warnings INT(11) NOT NULL DEFAULT 0,
  errors INT(11) NOT NULL DEFAULT 0,
  last_log_reset DATETIME,
  log_file_name VARCHAR(255) NOT NULL,
  log_file_location VARCHAR(512) NOT NULL,

  PRIMARY KEY(log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Insert the general logs
-- -------------------------------------------------------------

INSERT INTO logs(warnings,
                 errors,
                 log_file_name,
                 log_file_location)
VALUES (0,
        0,
        "Repository Management",
        "logs/general/repositoryManagement.txt");

INSERT INTO logs(warnings,
                 errors,
                 log_file_name,
                 log_file_location)
VALUES (0,
        0,
        "User Management",
        "logs/general/userManagement.txt");

INSERT INTO logs(warnings,
                 errors,
                 log_file_name,
                 log_file_location)
VALUES (0,
        0,
        "Authentication Server Management",
        "logs/general/authServerManagement.txt");

INSERT INTO logs(warnings,
                 errors,
                 log_file_name,
                 log_file_location)
VALUES (0,
        0,
        "MySQL",
        "logs/general/mysql.txt");

INSERT INTO logs(warnings,
                 errors,
                 log_file_name,
                 log_file_location)
VALUES (0,
        0,
        "Solr Index",
        "logs/general/solr.txt");

INSERT INTO logs(warnings,
                 errors,
                 log_file_name,
                 log_file_location)
VALUES (0,
        0,
        "Jobs Management",
        "logs/general/jobs.txt");

INSERT INTO logs(warnings,
                 errors,
                 log_file_name,
                 log_file_location)
VALUES (0,
        0,
        "Service Management",
        "logs/general/serviceManagement.txt");

INSERT INTO logs(warnings,
                 errors,
                 log_file_name,
                 log_file_location)
VALUES (0,
        0,
        "MST Configuration",
        "logs/general/configuration.txt");

-- -------------------------------------------------------------
-- Table structure for sets
-- -------------------------------------------------------------

DROP TABLE IF EXISTS sets;
CREATE TABLE sets
(
  set_id INT(11) NOT NULL AUTO_INCREMENT,
  display_name VARCHAR(127),
  description VARCHAR(255),
  set_spec VARCHAR(127) NOT NULL,
  is_provider_set BOOLEAN,
  is_record_set BOOLEAN,
  provider_id INT(11) NOT NULL,

  PRIMARY KEY(set_id),

  INDEX idx_sets_set_spec(set_spec),
  INDEX idx_sets_provider_id(provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Insert the default sets
-- -------------------------------------------------------------

INSERT INTO sets (display_name,
                  description,
                  set_spec,
                  is_provider_set,
                  is_record_set,
                  provider_id)
VALUES ("MARCXML Bibliographic Records",
        "A set of all MARCXML Bibliographic records in the repository.",
        "MARCXMLbibliographic",
        false,
        true,
        0);

INSERT INTO sets (display_name,
                  description,
                  set_spec,
                  is_provider_set,
                  is_record_set,
                  provider_id)
VALUES ("MARCXML Holdings Records",
        "A set of all MARCXML holdings records in the repository.",
        "MARCXMLholdings",
        false,
        true,
        0);

INSERT INTO sets (display_name,
                  description,
                  set_spec,
                  is_provider_set,
                  is_record_set,
                  provider_id)
VALUES ("MARCXML Authority Records",
        "A set of all MARCXML Authority records in the repository.",
        "MARCXMLauthority",
        false,
        true,
        0);


-- -------------------------------------------------------------
-- Table structure for table formats_to_providers
-- -------------------------------------------------------------

DROP TABLE IF EXISTS formats_to_providers;
CREATE TABLE formats_to_providers
(
  format_to_provider_id INT(11) NOT NULL AUTO_INCREMENT,
  format_id INT(11) NOT NULL,
  provider_id INT(11) NOT NULL,

  PRIMARY KEY(format_to_provider_id),

  INDEX idx_formats_to_providers_provider_id(provider_id),
  FOREIGN KEY (provider_id) REFERENCES providers(provider_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_formats_to_providers_format_id(format_id),
  FOREIGN KEY (format_id) REFERENCES formats(format_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table harvests_to_records
-- -------------------------------------------------------------

DROP TABLE IF EXISTS harvests_to_records;
CREATE TABLE harvests_to_records
(
  harvest_to_record_id INT(11) NOT NULL AUTO_INCREMENT,
  harvest_id INT(11) NOT NULL,
  record_id BIGINT(11) NOT NULL,

  PRIMARY KEY(harvest_to_record_id),

  INDEX idx_harvests_to_records_harvest_id(harvest_id),
  FOREIGN KEY (harvest_id) REFERENCES harvests(harvest_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_harvests_to_records_record_id(record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table resumption_tokens
-- -------------------------------------------------------------

DROP TABLE IF EXISTS resumption_tokens;
CREATE TABLE resumption_tokens
(
  resumption_token_id BIGINT(11) NOT NULL AUTO_INCREMENT,
  set_spec VARCHAR(255),
  metadata_format VARCHAR(511),
  starting_from TIMESTAMP,
  until TIMESTAMP,
  offset BIGINT(11) NOT NULL,

  PRIMARY KEY(resumption_token_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table 'services'
-- -------------------------------------------------------------

DROP TABLE IF EXISTS services;
CREATE TABLE services
(
  service_id INT(11) NOT NULL AUTO_INCREMENT,
  service_name VARCHAR(63) NOT NULL,
  service_jar VARCHAR(255) NOT NULL,
  service_configuration TEXT,
  class_name VARCHAR(63),
  port INT(11),
  warnings INT(11) NOT NULL DEFAULT 0,
  errors INT(11) NOT NULL DEFAULT 0,
  input_record_count INT(11) NOT NULL DEFAULT 0,
  output_record_count INT(11) NOT NULL DEFAULT 0,
  last_log_reset DATETIME,
  log_file_name VARCHAR(255) NOT NULL,
  harvest_out_warnings INT(11) NOT NULL DEFAULT 0,
  harvest_out_errors INT(11) NOT NULL DEFAULT 0,
  harvest_out_records_available BIGINT(11) NOT NULL DEFAULT 0,
  harvest_out_records_harvested BIGINT(11) NOT NULL DEFAULT 0,
  harvest_out_last_log_reset DATETIME,
  harvest_out_log_file_name VARCHAR(255) NOT NULL,
  status VARCHAR(20),
  xccfg_file_name VARCHAR(255) NOT NULL,

  PRIMARY KEY(service_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table 'services_to_input_formats'
-- -------------------------------------------------------------

DROP TABLE IF EXISTS services_to_input_formats;
CREATE TABLE services_to_input_formats
(
  service_to_input_format_id INT(11) NOT NULL AUTO_INCREMENT,
  service_id INT(11) NOT NULL,
  format_id INT(11) NOT NULL,

  PRIMARY KEY(service_to_input_format_id),

  INDEX idx_services_to_input_formats_service_id(service_id),
  FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_services_to_input_formats_format_id(format_id),
  FOREIGN KEY (format_id) REFERENCES formats(format_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table 'services_to_output_formats'
-- -------------------------------------------------------------

DROP TABLE IF EXISTS services_to_output_formats;
CREATE TABLE services_to_output_formats
(
  service_to_output_format_id INT(11) NOT NULL AUTO_INCREMENT,
  service_id INT(11) NOT NULL,
  format_id INT(11) NOT NULL,

  PRIMARY KEY(service_to_output_format_id),

  INDEX idx_services_to_output_formats_service_id(service_id),
  FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_services_to_output_formats_format_id(format_id),
  FOREIGN KEY (format_id) REFERENCES formats(format_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for oai_identifier_for_services
-- -------------------------------------------------------------

DROP TABLE IF EXISTS oai_identifier_for_services;
CREATE TABLE oai_identifier_for_services
(
  oai_identifier_for_service_id INT(11) NOT NULL AUTO_INCREMENT,
  next_oai_id BIGINT(11) NOT NULL,
  service_id INT(11) NOT NULL,

  PRIMARY KEY(oai_identifier_for_service_id),

  INDEX idx_oai_identifier_for_services_service_id(service_id),
  FOREIGN KEY(service_id) REFERENCES services(service_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for error_codes
-- -------------------------------------------------------------

DROP TABLE IF EXISTS error_codes;
CREATE TABLE error_codes
(
  error_code_id INT(11) NOT NULL AUTO_INCREMENT,
  error_code VARCHAR(63) NOT NULL,
  error_description_file VARCHAR(511) NOT NULL,
  service_id INT(11) NOT NULL,

  PRIMARY KEY(error_code_id),

  INDEX idx_error_codes_service_id(service_id),
  FOREIGN KEY(service_id) REFERENCES services(service_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for xc_id_for_frbr_elements
-- -------------------------------------------------------------

DROP TABLE IF EXISTS xc_id_for_frbr_elements;
CREATE TABLE xc_id_for_frbr_elements
(
  xc_id_for_frbr_element_id INT(11) NOT NULL AUTO_INCREMENT,
  next_xc_id BIGINT(11) NOT NULL,
  element_id INT(11) NOT NULL, -- 1 = work, 2 = expression, 3 = manifestation, 4 = holdings, 5 = item

  PRIMARY KEY(xc_id_for_frbr_element_id),

  INDEX idx_xc_id_for_frbr_elements_element_id(element_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table 'processing_directives'
-- -------------------------------------------------------------

DROP TABLE IF EXISTS processing_directives;
CREATE TABLE processing_directives
(
  processing_directive_id INT(11) NOT NULL AUTO_INCREMENT,
  source_provider_id INT(11) NOT NULL,
  source_service_id INT(11) NOT NULL,
  service_id INT(11) NOT NULL,
  output_set_id BIGINT(11),
  maintain_source_sets BOOL,

  PRIMARY KEY(processing_directive_id),

  INDEX idx_processing_directives_source_provider_id(source_provider_id),
  INDEX idx_processing_directives_source_service_id(source_service_id),
  INDEX idx_processing_directives_service_id(service_id),
  FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table 'processing_directives_to_input_formats'
-- -------------------------------------------------------------

DROP TABLE IF EXISTS processing_directives_to_input_sets;
CREATE TABLE processing_directives_to_input_sets
(
  processing_directive_to_input_set_id INT(11) NOT NULL AUTO_INCREMENT,
  processing_directive_id INT(11) NOT NULL,
  set_id INT(11) NOT NULL,

  PRIMARY KEY(processing_directive_to_input_set_id),

  INDEX idx_pd_to_input_sets_processing_directive_id(processing_directive_id),
  FOREIGN KEY (processing_directive_id) REFERENCES processing_directives(processing_directive_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_services_to_input_sets_set_id(set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table 'processing_directives_to_input_formats'
-- -------------------------------------------------------------

DROP TABLE IF EXISTS processing_directives_to_input_formats;
CREATE TABLE processing_directives_to_input_formats
(
  processing_directive_to_input_format_id INT(11) NOT NULL AUTO_INCREMENT,
  processing_directive_id INT(11) NOT NULL,
  format_id INT(11) NOT NULL,

  PRIMARY KEY(processing_directive_to_input_format_id),

  INDEX idx_pd_to_input_formats_processing_directive_id(processing_directive_id),
  FOREIGN KEY (processing_directive_id) REFERENCES processing_directives(processing_directive_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_services_to_input_formats_format_id(format_id),
  FOREIGN KEY (format_id) REFERENCES formats(format_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for table groups
-- -------------------------------------------------------------

DROP TABLE IF EXISTS groups;
CREATE TABLE groups
(
  group_id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(127) NOT NULL,
  description VARCHAR(1023),  
  
  PRIMARY KEY(group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for users_to_groups
-- -------------------------------------------------------------

DROP TABLE IF EXISTS users_to_groups;
CREATE TABLE users_to_groups
(
  user_to_group_id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  group_id INT(11) NOT NULL,
  
  PRIMARY KEY(user_to_group_id),

  INDEX idx_users_to_groups_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_users_to_groups_group_id (group_id),
  FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for top_level_tabs
-- -------------------------------------------------------------

DROP TABLE IF EXISTS top_level_tabs;
CREATE TABLE top_level_tabs
(
  top_level_tab_id INT(11) NOT NULL AUTO_INCREMENT,
  tab_name VARCHAR(63) NOT NULL,
  tab_order INT(11) NOT NULL,
  
  PRIMARY KEY(top_level_tab_id)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for groups_to_top_level_tabs
-- -------------------------------------------------------------

DROP TABLE IF EXISTS groups_to_top_level_tabs;
CREATE TABLE groups_to_top_level_tabs
(
  group_to_top_level_tab_id INT(11) NOT NULL AUTO_INCREMENT,
  group_id INT(11) NOT NULL,
  top_level_tab_id INT(11) NOT NULL,
  
  PRIMARY KEY(group_to_top_level_tab_id),

  INDEX idx_groups_to_top_level_tabs_group_id (group_id),
  FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_group_to_top_level_tabs_TL_tab_id (top_level_tab_id),
  FOREIGN KEY (top_level_tab_id) REFERENCES top_level_tabs(top_level_tab_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for emailconfig
-- -------------------------------------------------------------

DROP TABLE IF EXISTS emailconfig;
CREATE TABLE emailconfig
(
  email_config_id INT(11) NOT NULL AUTO_INCREMENT,
  server_address VARCHAR(255) NOT NULL,
  port_number INT(11) NOT NULL,
  from_address VARCHAR(255) NOT NULL,
  password VARCHAR(255),
  encrypted_connection VARCHAR(31), 
  timeout INT(11),
  forgotten_password_link BOOL,
  
  PRIMARY KEY(email_config_id)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Insert information for the GUI
-- -------------------------------------------------------------

delete from top_level_tabs;
delete from servers;
delete from groups;
delete from groups_to_top_level_tabs;

insert into top_level_tabs values(1,'Repositories',1);
insert into top_level_tabs values(2,'Harvest',2);
insert into top_level_tabs values(3,'Services',3);
insert into top_level_tabs values(4,'Browse Records',4);
insert into top_level_tabs values(5,'Logs',5);
insert into top_level_tabs values(6,'Users/Groups',6);
insert into top_level_tabs values(7,'Configuration',7);
insert into top_level_tabs values(8,'Search Index',8);

insert into groups values(1,'Administrator','Administrator');
insert into groups values(2,'Librarian','Librarian');
insert into groups values(3,'XCPartner','XCPartner');
insert into groups values(4,'Suscriber','Suscriber');
insert into groups values(5,'Guest','Guest');

insert into groups_to_top_level_tabs values(1,1,1);
insert into groups_to_top_level_tabs values(2,1,2);
insert into groups_to_top_level_tabs values(3,1,3);
insert into groups_to_top_level_tabs values(4,1,4);
insert into groups_to_top_level_tabs values(5,1,5);
insert into groups_to_top_level_tabs values(6,1,6);
insert into groups_to_top_level_tabs values(7,1,7);
insert into groups_to_top_level_tabs values(8,1,8);
insert into groups_to_top_level_tabs values(9,2,1);
insert into groups_to_top_level_tabs values(10,2,2);
insert into groups_to_top_level_tabs values(11,2,3);
insert into groups_to_top_level_tabs values(12,2,4);
insert into groups_to_top_level_tabs values(13,3,5);
insert into groups_to_top_level_tabs values(14,3,6);
insert into groups_to_top_level_tabs values(15,3,7);
insert into groups_to_top_level_tabs values(16,3,8);
insert into groups_to_top_level_tabs values(17,4,4);
insert into groups_to_top_level_tabs values(18,4,8);
insert into groups_to_top_level_tabs values(19,4,5);
insert into groups_to_top_level_tabs values(20,5,4);

INSERT INTO servers (server_id, url, name, type, port, username_attribute, start_location, institution, forgot_password_url, forgot_password_label, show_forgot_password_link)
VALUES (NULL, 'Local', 'Local', '4', '0', 'Local', 'Local', 'University of Rochester', 'Local', 'Local', '0');

