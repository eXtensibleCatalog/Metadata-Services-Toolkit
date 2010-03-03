-- -------------------------------------------------------------
-- Table structure for Aggregation service
-- -------------------------------------------------------------

-- -------------------------------------------------------------
-- Table structure for in_processed_identifiers
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
-- Table structure for output_record
-- -------------------------------------------------------------

DROP TABLE IF EXISTS output_record;
CREATE TABLE output_record
(
  output_record_id  INT(11) NOT NULL AUTO_INCREMENT,
  oai_id TEXT NOT NULL,
  XML TEXT NOT NULL,
  updated boolean NOT NULL,
  PRIMARY KEY(output_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -------------------------------------------------------------
-- Table structure for predecessor_record
-- -------------------------------------------------------------

DROP TABLE IF EXISTS predecessor_record;
CREATE TABLE predecessor_record
(
  predecessor_record_id INT(11) NOT NULL AUTO_INCREMENT,
  predecessor_oai_id TEXT NOT NULL,
  output_record_id INT(11) NOT NULL,
  PRIMARY KEY(predecessor_record_id),
  FOREIGN KEY(output_record_id) REFERENCES output_record(output_record_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- -------------------------------------------------------------
-- Table structure for held_records
-- -------------------------------------------------------------

DROP TABLE IF EXISTS held_records;
CREATE TABLE held_records
(
  held_records_id  INT(11) NOT NULL AUTO_INCREMENT,
  held_oai_id TEXT NOT NULL,
  parent_oai_id TEXT NOT NULL,
  PRIMARY KEY(held_records_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;