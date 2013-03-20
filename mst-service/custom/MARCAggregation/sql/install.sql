-- SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
-- SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
-- SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

-- CREATE SCHEMA IF NOT EXISTS `dont_output_schema` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
-- USE `dont_output_schema` ;

-- -------------------------------------------------------------
-- Table structure for MARC Aggregation service
-- -------------------------------------------------------------

-- -----------------------------------------------------
-- Table `bib_records`
-- -----------------------------------------------------
DROP TABLE IF EXISTS bib_records;
CREATE  TABLE `bib_records` (
  `input_record_id` BIGINT NOT NULL ,
  `output_record_id` BIGINT NOT NULL ,

  INDEX idx_merged_output_id (output_record_id),
  INDEX idx_merged_input_id (input_record_id),

  PRIMARY KEY (`input_record_id`, `output_record_id`) )

ENGINE = MyISAM DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `merged_records`
-- -----------------------------------------------------
DROP TABLE IF EXISTS merged_records;
CREATE  TABLE `merged_records` (
  `input_record_id` BIGINT NOT NULL ,

  PRIMARY KEY (`input_record_id`) )

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
-- (Reference) Table `prefixes_035a` system control number prefix
-- -----------------------------------------------------
DROP TABLE IF EXISTS prefixes_035a;
CREATE  TABLE `prefixes_035a` (
  `prefix` VARCHAR(255)  NOT NULL ,
  `prefix_id` INT UNSIGNED NOT NULL ,

  PRIMARY KEY  (`prefix_id`),
  INDEX idx_mp_035a_prefix (prefix)

) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `matchpoints_035a` system control number
-- -----------------------------------------------------
DROP TABLE IF EXISTS matchpoints_035a;
CREATE  TABLE `matchpoints_035a` (
  `full_string` VARCHAR(255) NOT NULL ,
  `prefix_id` INT UNSIGNED NOT NULL ,
  `numeric_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,

  PRIMARY KEY  (`input_record_id`, `full_string`),

  -- may want to not do this and create it after 1st load create, ala repo
  INDEX idx_mp_035a_numeric_id (numeric_id),
  INDEX idx_mp_035a_prefix_id (prefix_id)

) ENGINE = InnoDB;
