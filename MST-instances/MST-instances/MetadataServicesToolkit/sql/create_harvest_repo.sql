SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `repo_name` ;
USE `repo_name`;

-- -----------------------------------------------------
-- Table `repo_name`.`record_oai_ids`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `repo_name`.`record_oai_ids` (
  `record_id` BIGINT NOT NULL ,
  `oai_id` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`record_id`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
