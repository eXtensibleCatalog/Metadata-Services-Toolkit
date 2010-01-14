-- -------------------------------------------------------------
-- Table structure for Aggregation service
-- -------------------------------------------------------------

-- -------------------------------------------------------------
-- Table structure for in_processed_identifier_values
-- -------------------------------------------------------------

DROP TABLE IF EXISTS in_processed_identifiers;
CREATE TABLE in_processed_identifiers
(
  in_processed_identifiers_id INT(11) NOT NULL AUTO_INCREMENT,
  oai_id TEXT  NOT NULL,
  oclc_value TEXT  NULL,
  lccn_value TEXT  NULL,
  isbn_value TEXT  NULL,
  issn_value TEXT  NULL,
  PRIMARY KEY(in_processed_identifiers_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -------------------------------------------------------------
-- Table structure for input_output_records
-- -------------------------------------------------------------

DROP TABLE IF EXISTS input_output_records;
CREATE TABLE input_output_records
(
  input_output_records_id INT(11) NOT NULL AUTO_INCREMENT,
  input_oai_id TEXT NOT NULL,
  output_oai_id TEXT NOT NULL,
  PRIMARY KEY(input_output_records_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for output_records
-- -------------------------------------------------------------

DROP TABLE IF EXISTS output_records;
CREATE TABLE output_records
(
  output_records_id  INT(11) NOT NULL AUTO_INCREMENT,
  oai_id TEXT NOT NULL,
  XML TEXT NOT NULL,
  updated boolean NOT NULL,
  PRIMARY KEY(output_records_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -------------------------------------------------------------
-- Table structure for held_records
-- -------------------------------------------------------------

DROP TABLE IF EXISTS held_records;
CREATE TABLE held_records
(
  held_records_id  INT(11) NOT NULL AUTO_INCREMENT,
  held_oai_id TEXT NOT NULL,
  waiting_for_parent_oai_id TEXT NOT NULL,
  PRIMARY KEY(held_records_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;