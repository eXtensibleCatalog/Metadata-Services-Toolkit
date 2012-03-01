-- SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
-- SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
-- SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

-- CREATE SCHEMA IF NOT EXISTS `dont_output_schema` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
-- USE `dont_output_schema` ;

-- -------------------------------------------------------------
-- Table structure for MARC Aggregation service
-- -------------------------------------------------------------

-- -----------------------------------------------------
-- Table `merged_records`
-- -----------------------------------------------------
CREATE  TABLE `merged_records` (
  `input_record_id` BIGINT NOT NULL ,
  `output_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`input_record_id`, `output_record_id`) )
ENGINE = MyISAM DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `merge_scores`
-- -----------------------------------------------------
DROP TABLE IF EXISTS merge_scores;
CREATE  TABLE `merge_scores` (
  `input_record_id` BIGINT NOT NULL ,
  `leaderByte17` char(1) NOT NULL ,
  `size` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`input_record_id`) )
ENGINE = MyISAM DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `matchpoints_010a` neither 010 nor $a can repeat,Lccn
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_010a;
CREATE  TABLE `matchpoints_010a` (
  `numeric_id` BIGINT NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,

  PRIMARY KEY (`input_record_id`),

  -- may want to not do this and create it after 1st load create, ala repo
  INDEX idx_mp_010a_numeric_id (numeric_id)

) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_020a` 020 can repeat, $a cannot,ISBN
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_020a;
CREATE  TABLE `matchpoints_020a` (
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,

  PRIMARY KEY (`input_record_id`, `string_id`),

  -- may want to not do this and create it after 1st load create, ala repo
  INDEX idx_mp_020a_string_id (string_id)

) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_022a` 022 can repeat, $a cannot,ISSN
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_022a;
CREATE  TABLE `matchpoints_022a` (
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,

  PRIMARY KEY (`input_record_id`, `string_id`),

  -- may want to not do this and create it after 1st load create, ala repo
  INDEX idx_mp_022a_string_id (string_id)

) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_024a` 024 can repeat, $a cannot
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_024a;
CREATE  TABLE `matchpoints_024a` (
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,

  PRIMARY KEY (`input_record_id`, `string_id`),

  -- may want to not do this and create it after 1st load create, ala repo
  INDEX idx_mp_024a_string_id (string_id)

) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_028a` 028 can repeat, $ab cannot
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_028a;
CREATE  TABLE `matchpoints_028a` (
  `string_ab_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`input_record_id`, `string_ab_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_130a` no repeating possible
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_130a;
CREATE  TABLE `matchpoints_130a` (
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`input_record_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_240a` no repeating possible
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_240a;
CREATE  TABLE `matchpoints_240a` (
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`input_record_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_245a` no repeating possible
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_245a;
CREATE  TABLE `matchpoints_245a` (
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`input_record_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_260abc` 260 can repeat, $abc can2!
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_260a;
CREATE  TABLE `matchpoints_260abc` (
  `string_abc_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`input_record_id`, `string_abc_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `matchpoints_035a` system control number
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_035a;
CREATE  TABLE `matchpoints_035a` (
  `full_string` VARCHAR(255) NOT NULL ,
--  `prefix` VARCHAR(255) NOT NULL ,
--  `numeric_id` INT UNSIGNED NOT NULL ,
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,

  PRIMARY KEY  (`input_record_id`, `string_id`),

  -- may want to not do this and create it after 1st load create, ala repo
  INDEX idx_mp_035a_string_id (string_id)

) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `merged_035`
-- -----------------------------------------------------
DROP TABLE IF EXISTS merged_035;
CREATE  TABLE `merged_035` (
  `output_record_id` BIGINT NOT NULL ,
  `035a_string` VARCHAR(255) NULL ,
  `035a_numeric` INT NULL ,
  `prefix` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`035a_string`, `035a_numeric`, `prefix`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `merged_904`
-- -----------------------------------------------------
DROP TABLE IF EXISTS merged_904;
CREATE  TABLE `merged_904` (
  `holding_output_record_id` BIGINT NOT NULL ,
  `bib_output_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`holding_output_record_id`, `bib_output_record_id`) )
ENGINE = MyISAM DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `bibs2holdings_string_id`
-- -----------------------------------------------------
CREATE  TABLE `bibs2holdings_string_id` (
  `001` VARCHAR(255) NOT NULL ,
  `003` VARCHAR(255) NOT NULL ,
  `output_holding_id` BIGINT NOT NULL ,
  PRIMARY KEY (`001`, `003`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bibs2holdings_numeric_id`
-- -----------------------------------------------------
CREATE  TABLE `bibs2holdings_numeric_id` (
  `001` INT NOT NULL ,
  `003` VARCHAR(255) NOT NULL ,
  `output_holding_id` BIGINT NOT NULL ,
  PRIMARY KEY (`001`) )
ENGINE = MyISAM DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table bibsProcessedLongId`
-- -----------------------------------------------------
CREATE  TABLE `bibsProcessedLongId` (
  `org_code` VARCHAR(255) NOT NULL ,
  `bib_001` BIGINT NOT NULL ,
  `record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`bib_001`, `record_id`) ,
  KEY `bibsProcessedLongId_bib_001` (`bib_001` ASC) ,
  KEY `bibsProcessedLongId_record_id` (`record_id` ASC) )
ENGINE = MyISAM DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table bibsProcessedStringId`
-- -----------------------------------------------------
CREATE  TABLE `bibsProcessedStringId` (
  `org_code` VARCHAR(255) NOT NULL ,
  `bib_001` VARCHAR(255) NOT NULL ,
  `record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`bib_001`, `record_id`) ,
  KEY `bibsProcessedStringId_bib_001` (`bib_001` ASC) ,
  KEY `bibsProcessedStringId_record_id` (`record_id` ASC) )
ENGINE = MyISAM DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `bibsYet2ArriveLongId`
-- -----------------------------------------------------
CREATE  TABLE `bibsYet2ArriveLongId` (
  `org_code` VARCHAR(255) NOT NULL ,
  `bib_001` BIGINT NOT NULL ,
  `record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`bib_001`, `record_id`) ,
  KEY `bibsYet2ArriveLongId_bib_001` (`bib_001` ASC) ,
  KEY `bibsYet2ArriveLongId_record_id` (`record_id` ASC) )
ENGINE = MyISAM DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `bibsYet2ArriveStringId`
-- -----------------------------------------------------
CREATE  TABLE `bibsYet2ArriveStringId` (
  `org_code` VARCHAR(255) NOT NULL ,
  `bib_001` VARCHAR(255) NOT NULL ,
  `record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`bib_001`, `record_id`) ,
  KEY `bibsYet2ArriveStringId_bib_001` (`bib_001` ASC) ,
  KEY `bibsYet2ArriveStringId_record_id` (`record_id` ASC) )
ENGINE = MyISAM DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `held_holdings`
-- -----------------------------------------------------
CREATE  TABLE `held_holdings` (
  `org_code` VARCHAR(255) NOT NULL ,
  `held_holding_id` BIGINT NOT NULL ,
  `manifestation_id` BIGINT NOT NULL ,
  PRIMARY KEY (`held_holding_id`, `manifestation_id`) ,
  KEY `idx_held_holdings_held_holding_id` (`held_holding_id` ASC) ,
  KEY `idx_held_holdings_manifestation_id` (`manifestation_id` ASC) )
ENGINE = MyISAM DEFAULT CHARACTER SET = utf8;


