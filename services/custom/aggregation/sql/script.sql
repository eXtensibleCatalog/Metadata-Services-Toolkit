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
  oai_id VARCHAR(255)  NOT NULL,
  oclc_value VARCHAR(255)  NULL,
  lccn_value VARCHAR(255)  NULL,
  isbn_value VARCHAR(255)  NULL,
  issn_value VARCHAR(255)  NULL,
  PRIMARY KEY(in_processed_identifiers_id),
  INDEX idx_oclc_value (oclc_value),
  INDEX idx_lccn_value (lccn_value),
  INDEX idx_isbn_value (isbn_value),
  INDEX idx_issn_value (issn_value),
  CONSTRAINT UNIQUE INDEX idx_oai_id (oai_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -------------------------------------------------------------
-- Table structure for output_record
-- -------------------------------------------------------------

DROP TABLE IF EXISTS output_record;
CREATE TABLE output_record
(
  output_record_id  INT(11) NOT NULL AUTO_INCREMENT,
  oai_id VARCHAR(255) NOT NULL,
  XML TEXT NOT NULL,
  updated boolean NOT NULL,
  PRIMARY KEY(output_record_id),
  CONSTRAINT UNIQUE INDEX idx_oai_id (oai_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -------------------------------------------------------------
-- Table structure for output_record_uplinks
-- -------------------------------------------------------------

DROP TABLE IF EXISTS output_record_uplinks;
CREATE TABLE output_record_uplinks
(
  output_record_uplinks_id  INT(11) NOT NULL AUTO_INCREMENT,
  output_record_id INT(11) NOT NULL,
  uplink_oai_id VARCHAR(255) NOT NULL,
  PRIMARY KEY(output_record_uplinks_id),
  FOREIGN KEY(output_record_id) REFERENCES output_record(output_record_id) ON DELETE CASCADE ON 
	UPDATE CASCADE,
  INDEX idx_uplink_oai_id (uplink_oai_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -------------------------------------------------------------
-- Table structure for predecessor_record
-- -------------------------------------------------------------

DROP TABLE IF EXISTS predecessor_record;
CREATE TABLE predecessor_record
(
  predecessor_record_id INT(11) NOT NULL AUTO_INCREMENT,
  predecessor_oai_id VARCHAR(255) NOT NULL,
  output_record_id INT(11) NOT NULL,
  PRIMARY KEY(predecessor_record_id),
  FOREIGN KEY(output_record_id) REFERENCES output_record(output_record_id) ON DELETE CASCADE ON 

UPDATE CASCADE,
  INDEX idx_predecessor_oai_id (predecessor_oai_id),
  INDEX idx_output_record_id (output_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- -------------------------------------------------------------
-- Table structure for held_records
-- -------------------------------------------------------------

DROP TABLE IF EXISTS held_records;
CREATE TABLE held_records
(
  held_records_id  INT(11) NOT NULL AUTO_INCREMENT,
  held_oai_id VARCHAR(255) NOT NULL,
  parent_oai_id VARCHAR(255) NOT NULL,
  PRIMARY KEY(held_records_id),
  INDEX idx_parent_oai_id (parent_oai_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;