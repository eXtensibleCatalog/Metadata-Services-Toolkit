SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `merged_records`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `merged_records` (
  `input_record_id` BIGINT NOT NULL ,
  `output_record_id` BIGINT NOT NULL ,
  PRIMARY KEY (`input_record_id`, `output_record_id`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `bibs2holdings_string_id`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `bibs2holdings_string_id` (
  `001` VARCHAR(255) NOT NULL ,
  `003` VARCHAR(255) NOT NULL ,
  `output_holding_id` BIGINT NOT NULL ,
  PRIMARY KEY (`001`, `003`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `bibs2holdings_numeric_id`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `bibs2holdings_numeric_id` (
  `001` INT NOT NULL ,
  `003` VARCHAR(255) NOT NULL ,
  `output_holding_id` BIGINT NOT NULL ,
  PRIMARY KEY (`001`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `merge_scores`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `merge_scores` (
  `output_record_id` BIGINT NOT NULL ,
  `score` TINYINT NOT NULL ,
  PRIMARY KEY (`output_record_id`) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `matchpoints_035_string_id`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `matchpoints_035_string_id` (
  `prefix` VARCHAR(255) NOT NULL ,
  `string_id` VARCHAR(255) NOT NULL ,
  `input_record_id` BIGINT NOT NULL )
ENGINE = InnoDB;

CREATE INDEX `PRIMARY` ON `matchpoints_035_string_id` (`input_record_id` ASC) ;


-- -----------------------------------------------------
-- Table `matchpoints_035_numeric_id`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `matchpoints_035_numeric_id` (
)
ENGINE = MyISAM;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
