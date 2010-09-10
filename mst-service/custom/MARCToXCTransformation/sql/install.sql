-- -------------------------------------------------------------
-- Table structure for Transformation service
-- -------------------------------------------------------------

-- -------------------------------------------------------------
-- Table structure for bibliographic_manifestation
-- -------------------------------------------------------------

DROP TABLE IF EXISTS marc_bibliographic_to_xc_manifestation;
CREATE TABLE marc_bibliographic_to_xc_manifestation
(
  bibliographic_manifestation_id INT(11) NOT NULL AUTO_INCREMENT,
  bibliographic_oai_id VARCHAR(255)  NOT NULL,
  manifestation_oai_id VARCHAR(255)  NOT NULL,
  bibliographic_001_field VARCHAR(255)  NOT NULL,
  PRIMARY KEY(bibliographic_manifestation_id),
  INDEX idx_bibliographic_oai_id (bibliographic_oai_id),
  INDEX idx_bibliographic_001_field (bibliographic_001_field)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -------------------------------------------------------------
-- Table structure for held_holdings
-- -------------------------------------------------------------

DROP TABLE IF EXISTS held_marcxml_holding;
CREATE TABLE held_marcxml_holding
(
  held_marcxml_holding_id INT(11) NOT NULL AUTO_INCREMENT,
  marcxml_holding_oai_id VARCHAR(255) NOT NULL,
  marcxml_holding_004_field VARCHAR(255) NOT NULL,
  PRIMARY KEY(held_marcxml_holding_id),
  INDEX idx_marcxml_holding_oai_id (marcxml_holding_oai_id),
  INDEX idx_marcxml_holding_004_field (marcxml_holding_004_field)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------------------------------------------
-- Table structure for holding_manifestation
-- -------------------------------------------------------------

DROP TABLE IF EXISTS holding_manifestation;
CREATE TABLE holding_manifestation
(
  holding_manifestation_id  INT(11) NOT NULL AUTO_INCREMENT,
  xc_holding_oai_id VARCHAR(255) NOT NULL,
  marcxml_holding_004_field VARCHAR(255) NOT NULL,
  manifestation_oai_id VARCHAR(255) NOT NULL,
  PRIMARY KEY(holding_manifestation_id),
  INDEX idx_manifestation_oai_id (manifestation_oai_id),
  INDEX idx_xc_holding_oai_id (xc_holding_oai_id),
  INDEX idx_xc_marcxml_holding_004_field (marcxml_holding_004_field)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ben's custom tables --
/* todo - tidy this up to match the maps in TransformationService */
create table marc_bibs_xc_manifestations (
	bib_man_id          int          not null      AUTO_INCREMENT,
	bib_001             char(50)     not null,
	record_id           BIGINT       not null,
	primary KEY (bib_man_id),
	KEY idx_marc_bibs_xc_manifestations_bib_001(bib_001),
	KEY idx_marc_bibs_xc_manifestations_bib_record_id(record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table links (
	link_id          int          not null      AUTO_INCREMENT,
	from_record_id   bigint       not null,
	to_record_id     bigint       not null,
	primary KEY (link_id),
	KEY idx_links_from_record_id(from_record_id),
	KEY idx_links_to_record_id(to_record_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
