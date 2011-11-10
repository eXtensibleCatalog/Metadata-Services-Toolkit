SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `dont_output_schema` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `dont_output_schema` ;

-- -----------------------------------------------------
-- Table `dont_output_schema`.`merged_records`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`merged_records` (
  `input_record_id` BIGINT NOT NULL ,
  `output_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`input_record_id`, `output_record_id`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`merge_scores`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`merge_scores` (
  `output_record_id` BIGINT NOT NULL ,
  `score` TINYINT NOT NULL ,
  PRIMARY KEY (`output_record_id`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`matchpoints_035`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`matchpoints_035` (
  `prefix` VARCHAR(255) NOT NULL ,
  `numeric_id` UNSIGNED INT NOT NULL ,
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL ,
  INDEX `PRIMARY` (`input_record_id` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`matchpoints_010a`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`matchpoints_010a` (
  `numeric_id` UNSIGNED INT NOT NULL ,
  `input_record_id` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`input_record_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`matchpoints_020a`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`matchpoints_020a` (
  `numeric_id` UNSIGNED INT NOT NULL ,
  `input_record_id` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`input_record_id`) )
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dont_output_schema`.`merged_035`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`merged_035` (
  `output_record_id` BIGINT NOT NULL ,
  `035a_string` VARCHAR(255) NULL ,
  `035a_numeric` INT NULL ,
  `prefix` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`035a_string`, `035a_numeric`, `prefix`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`merged_904`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`merged_904` (
  `holding_output_record_id` BIGINT NOT NULL ,
  `bib_output_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`holding_output_record_id`, `bib_output_record_id`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`bibs2holdings_string_id`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`bibs2holdings_string_id` (
  `001` VARCHAR(255) NOT NULL ,
  `003` VARCHAR(255) NOT NULL ,
  `output_holding_id` BIGINT NOT NULL ,
  PRIMARY KEY (`001`, `003`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`bibs2holdings_numeric_id`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`bibs2holdings_numeric_id` (
  `001` INT NOT NULL ,
  `003` VARCHAR(255) NOT NULL ,
  `output_holding_id` BIGINT NOT NULL ,
  PRIMARY KEY (`001`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`bibsProcessedLongId`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`bibsProcessedLongId` (
  `org_code` VARCHAR(255) NOT NULL ,
  `bib_001` BIGINT NOT NULL ,
  `record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`bib_001`, `record_id`) ,
  INDEX `bibsProcessedLongId_bib_001` (`bib_001` ASC) ,
  INDEX `bibsProcessedLongId_record_id` (`record_id` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`bibsProcessedStringId`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`bibsProcessedStringId` (
  `org_code` VARCHAR(255) NOT NULL ,
  `bib_001` VARCHAR(255) NOT NULL ,
  `record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`bib_001`, `record_id`) ,
  INDEX `bibsProcessedStringId_bib_001` (`bib_001` ASC) ,
  INDEX `bibsProcessedStringId_record_id` (`record_id` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`bibsYet2ArriveLongId`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`bibsYet2ArriveLongId` (
  `org_code` VARCHAR(255) NOT NULL ,
  `bib_001` BIGINT NOT NULL ,
  `record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`bib_001`, `record_id`) ,
  INDEX `bibsYet2ArriveLongId_bib_001` (`bib_001` ASC) ,
  INDEX `bibsYet2ArriveLongId_record_id` (`record_id` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`bibsYet2ArriveStringId`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`bibsYet2ArriveStringId` (
  `org_code` VARCHAR(255) NOT NULL ,
  `bib_001` VARCHAR(255) NOT NULL ,
  `record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`bib_001`, `record_id`) ,
  INDEX `bibsYet2ArriveStringId_bib_001` (`bib_001` ASC) ,
  INDEX `bibsYet2ArriveStringId_record_id` (`record_id` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dont_output_schema`.`held_holdings`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `dont_output_schema`.`held_holdings` (
  `org_code` VARCHAR(255) NOT NULL ,
  `held_holding_id` BIGINT NOT NULL ,
  `manifestation_id` BIGINT NOT NULL ,
  PRIMARY KEY (`held_holding_id`, `manifestation_id`) ,
  INDEX `idx_held_holdings_held_holding_id` (`held_holding_id` ASC) ,
  INDEX `idx_held_holdings_manifestation_id` (`manifestation_id` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
