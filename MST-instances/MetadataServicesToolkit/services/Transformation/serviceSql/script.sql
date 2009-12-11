-- -------------------------------------------------------------
-- Table structure for Transformation service
-- -------------------------------------------------------------

-- -------------------------------------------------------------
-- Table structure for bibliographic_manifestation
-- -------------------------------------------------------------

DROP TABLE IF EXISTS bibliographic_manifestation;
CREATE TABLE bibliographic_manifestation
(
  bibliographic_manifestation_id INT(11) NOT NULL AUTO_INCREMENT,
  bibliographic_oai_id TEXT  NOT NULL,
  manifestation_oai_id TEXT  NOT NULL,
  bibliographic_001_field TEXT  NOT NULL,
  PRIMARY KEY(bibliographic_manifestation_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -------------------------------------------------------------
-- Table structure for held_holdings
-- -------------------------------------------------------------

DROP TABLE IF EXISTS held_marcxml_holding;
CREATE TABLE held_marcxml_holding
(
  held_marcxml_holding_id INT(11) NOT NULL AUTO_INCREMENT,
  marcxml_holding_oai_id TEXT NOT NULL,
  marcxml_holding_004_field TEXT NOT NULL,
  PRIMARY KEY(held_marcxml_holding_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for holding_manifestation
-- -------------------------------------------------------------

DROP TABLE IF EXISTS holding_manifestation;
CREATE TABLE holding_manifestation
(
  holding_manifestation_id  INT(11) NOT NULL AUTO_INCREMENT,
  xc_holding_oai_id TEXT NOT NULL,
  marcxml_holding_004_field TEXT NOT NULL,
  manifestation_oai_id TEXT NOT NULL,
  PRIMARY KEY(holding_manifestation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

