SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
CREATE SCHEMA IF NOT EXISTS `repo_name` ;
USE `repo_name`;

-- -----------------------------------------------------
-- Table `repo_name`.`records`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`records` (
  `record_id` INT NOT NULL ,
  `oai_datestamp` DATETIME NULL DEFAULT NULL ,
  `type` CHAR(1) NULL DEFAULT NULL ,
  `status` CHAR(1) NULL DEFAULT NULL ,
  `prev_status` CHAR(1) NULL DEFAULT NULL ,
  `format_id` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`record_id`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `repo_name`.`record_updates`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`record_updates` (
  `record_id` INT NULL DEFAULT NULL ,
  `date_updated` DATETIME NULL DEFAULT NULL )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `repo_name`.`records_xml`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`records_xml` (
  `record_id` INT NOT NULL ,
  `xml` LONGTEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`record_id`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `repo_name`.`record_sets`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`record_sets` (
  `record_id` INT NULL DEFAULT NULL ,
  `set_id` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`record_id`, `set_id`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `repo_name`.`record_predecessors`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`record_predecessors` (
  `record_id` BIGINT NULL DEFAULT NULL ,
  `pred_record_id` BIGINT NULL DEFAULT NULL ,
  PRIMARY KEY (`record_id`, `pred_record_id`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `repo_name`.`properties`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`properties` (
  `prop_key` VARCHAR(255) NOT NULL ,
  `value` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`prop_key`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `repo_name`.`incoming_record_counts`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`incoming_record_counts` (
  `incoming_record_count_id` INT NOT NULL AUTO_INCREMENT ,
  `harvest_start_date` DATETIME NOT NULL ,
  `type_name` VARCHAR(35) NOT NULL ,
  `new_act_cnt` INT NOT NULL DEFAULT 0 ,
  `new_del_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_act_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_del_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_act_prev_act_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_act_prev_del_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_del_prev_act_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_del_prev_del_cnt` INT NOT NULL DEFAULT 0 ,
  `unexpected_error_cnt` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`incoming_record_count_id`) ,
  INDEX `idx_incoming_record_counts_type_name` (`type_name` ASC) ,
  UNIQUE INDEX `idx_incoming_record_counts_date_type` (`harvest_start_date` ASC, `type_name` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `repo_name`.`outgoing_record_counts`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`outgoing_record_counts` (
  `outgoing_record_count_id` INT NOT NULL AUTO_INCREMENT ,
  `harvest_start_date` DATETIME NOT NULL ,
  `type_name` VARCHAR(35) NOT NULL ,
  `new_act_cnt` INT NOT NULL DEFAULT 0 ,
  `new_held_cnt` INT NOT NULL DEFAULT 0 ,
  `new_del_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_act_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_held_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_del_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_act_prev_act_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_act_prev_held_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_act_prev_del_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_held_prev_act_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_held_prev_held_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_held_prev_del_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_del_prev_act_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_del_prev_held_cnt` INT NOT NULL DEFAULT 0 ,
  `upd_del_prev_del_cnt` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`outgoing_record_count_id`) ,
  INDEX `idx_outgoing_record_counts_type_name` (`type_name` ASC) ,
  UNIQUE INDEX `idx_outgoing_record_counts_date_type` (`harvest_start_date` ASC, `type_name` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
