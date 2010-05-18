-- MySQL dump 10.13  Distrib 5.1.44, for Win32 (ia32)
--
-- Host: localhost    Database: MetadataServicesToolkit
-- ------------------------------------------------------
-- Server version	5.1.44-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `MetadataServicesToolkit`
--

-- drop DATABASE IF EXISTS `MetadataServicesToolkit`;
CREATE DATABASE /*!32312 IF NOT EXISTS*/ `MetadataServicesToolkit` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `MetadataServicesToolkit`;

--
-- Table structure for table `emailconfig`
--

DROP TABLE IF EXISTS `emailconfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emailconfig` (
  `email_config_id` int(11) NOT NULL AUTO_INCREMENT,
  `server_address` varchar(255) NOT NULL,
  `port_number` int(11) NOT NULL,
  `from_address` varchar(255) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `encrypted_connection` varchar(31) DEFAULT NULL,
  `timeout` int(11) DEFAULT NULL,
  `forgotten_password_link` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`email_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emailconfig`
--

LOCK TABLES `emailconfig` WRITE;
/*!40000 ALTER TABLE `emailconfig` DISABLE KEYS */;
/*!40000 ALTER TABLE `emailconfig` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `error_codes`
--

DROP TABLE IF EXISTS `error_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `error_codes` (
  `error_code_id` int(11) NOT NULL AUTO_INCREMENT,
  `error_code` varchar(63) NOT NULL,
  `error_description_file` varchar(511) NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`error_code_id`),
  KEY `idx_error_codes_service_id` (`service_id`),
  CONSTRAINT `error_codes_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `error_codes`
--

LOCK TABLES `error_codes` WRITE;
/*!40000 ALTER TABLE `error_codes` DISABLE KEYS */;
INSERT INTO `error_codes` VALUES (1,'100','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceErrors\\100.html',1),(2,'102','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceErrors\\102.html',1),(3,'103','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceErrors\\103.html',1),(4,'104','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceErrors\\104.html',1),(5,'105','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceErrors\\105.html',1),(6,'106','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceErrors\\106.html',1),(7,'107','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceErrors\\107.html',1),(8,'109','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceErrors\\109.html',1),(9,'100','MST-instances\\MetadataServicesToolkit\\services\\Transformation\\serviceErrors\\100.html',2),(10,'101','MST-instances\\MetadataServicesToolkit\\services\\Transformation\\serviceErrors\\101.html',2);
/*!40000 ALTER TABLE `error_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `formats`
--

DROP TABLE IF EXISTS `formats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formats` (
  `format_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(127) NOT NULL,
  `namespace` varchar(255) NOT NULL,
  `schema_location` varchar(255) NOT NULL,
  PRIMARY KEY (`format_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `formats`
--

LOCK TABLES `formats` WRITE;
/*!40000 ALTER TABLE `formats` DISABLE KEYS */;
INSERT INTO `formats` VALUES (1,'oai_dc','http://www.openarchives.org/OAI/2.0/oai_dc/','http://www.openarchives.org/OAI/2.0/oai_dc.xsd'),(2,'oai_marc','http://www.openarchives.org/OAI/1.1/oai_marc','http://www.openarchives.org/OAI/1.1/oai_marc.xsd'),(3,'marcxml','http://www.loc.gov/MARC21/slim','http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd'),(4,'mods','http://www.loc.gov/mods/v3','http://www.loc.gov/standards/mods/v3/mods-3-0.xsd'),(5,'html','http://www.w3.org/TR/REC-html40','http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd'),(6,'marc21','http://www.loc.gov/MARC21/slim','http://128.151.244.137:8080/OAIToolkit/schema/MARC21slim_custom.xsd'),(7,'xc','http://www.extensiblecatalog.info/Elements','http://www.extensiblecatalog.info/Elements');
/*!40000 ALTER TABLE `formats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `formats_to_providers`
--

DROP TABLE IF EXISTS `formats_to_providers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formats_to_providers` (
  `format_to_provider_id` int(11) NOT NULL AUTO_INCREMENT,
  `format_id` int(11) NOT NULL,
  `provider_id` int(11) NOT NULL,
  PRIMARY KEY (`format_to_provider_id`),
  KEY `idx_formats_to_providers_provider_id` (`provider_id`),
  KEY `idx_formats_to_providers_format_id` (`format_id`),
  CONSTRAINT `formats_to_providers_ibfk_1` FOREIGN KEY (`provider_id`) REFERENCES `providers` (`provider_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `formats_to_providers_ibfk_2` FOREIGN KEY (`format_id`) REFERENCES `formats` (`format_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8861 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `formats_to_providers`
--

LOCK TABLES `formats_to_providers` WRITE;
/*!40000 ALTER TABLE `formats_to_providers` DISABLE KEYS */;
INSERT INTO `formats_to_providers` VALUES (8611,1,1),(8612,2,1),(8613,3,1),(8614,4,1),(8615,5,1),(8851,1,2),(8852,2,2),(8853,3,2),(8854,4,2),(8855,5,2),(8856,1,3),(8857,2,3),(8858,3,3),(8859,4,3),(8860,5,3);
/*!40000 ALTER TABLE `formats_to_providers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` VALUES (1,'Administrator','Administrator');
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups_to_top_level_tabs`
--

DROP TABLE IF EXISTS `groups_to_top_level_tabs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups_to_top_level_tabs` (
  `group_to_top_level_tab_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `top_level_tab_id` int(11) NOT NULL,
  PRIMARY KEY (`group_to_top_level_tab_id`),
  KEY `idx_groups_to_top_level_tabs_group_id` (`group_id`),
  KEY `idx_group_to_top_level_tabs_TL_tab_id` (`top_level_tab_id`),
  CONSTRAINT `groups_to_top_level_tabs_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `groups_to_top_level_tabs_ibfk_2` FOREIGN KEY (`top_level_tab_id`) REFERENCES `top_level_tabs` (`top_level_tab_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups_to_top_level_tabs`
--

LOCK TABLES `groups_to_top_level_tabs` WRITE;
/*!40000 ALTER TABLE `groups_to_top_level_tabs` DISABLE KEYS */;
INSERT INTO `groups_to_top_level_tabs` VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,1,5),(6,1,6),(7,1,7),(8,1,8);
/*!40000 ALTER TABLE `groups_to_top_level_tabs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `harvest_schedule_steps`
--

DROP TABLE IF EXISTS `harvest_schedule_steps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `harvest_schedule_steps` (
  `harvest_schedule_step_id` int(11) NOT NULL AUTO_INCREMENT,
  `harvest_schedule_id` int(11) NOT NULL,
  `format_id` int(11) NOT NULL,
  `set_id` int(11) DEFAULT NULL,
  `last_ran` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`harvest_schedule_step_id`),
  KEY `idx_harvest_schedules_step_harvest_schedule_id` (`harvest_schedule_id`),
  KEY `idx_harvest_schedules_step_set_id` (`set_id`),
  KEY `idx_harvest_schedules_step_format_id` (`format_id`),
  CONSTRAINT `harvest_schedule_steps_ibfk_1` FOREIGN KEY (`harvest_schedule_id`) REFERENCES `harvest_schedules` (`harvest_schedule_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `harvest_schedule_steps_ibfk_2` FOREIGN KEY (`format_id`) REFERENCES `formats` (`format_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=282 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `harvest_schedule_steps`
--

LOCK TABLES `harvest_schedule_steps` WRITE;
/*!40000 ALTER TABLE `harvest_schedule_steps` DISABLE KEYS */;
INSERT INTO `harvest_schedule_steps` VALUES (282,3,3,0,NULL);
/*!40000 ALTER TABLE `harvest_schedule_steps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `harvest_schedules`
--

DROP TABLE IF EXISTS `harvest_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `harvest_schedules` (
  `harvest_schedule_id` int(11) NOT NULL AUTO_INCREMENT,
  `schedule_name` varchar(265) DEFAULT NULL,
  `recurrence` varchar(127) DEFAULT NULL,
  `provider_id` int(11) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `minute` int(11) DEFAULT NULL,
  `day_of_week` int(11) DEFAULT NULL,
  `hour` int(11) DEFAULT NULL,
  `notify_email` varchar(255) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `request` text,
  PRIMARY KEY (`harvest_schedule_id`),
  KEY `idx_harvest_schedules_hour` (`hour`),
  KEY `idx_harvest_schedules_minute` (`minute`),
  KEY `idx_harvest_schedules_day_of_week` (`day_of_week`),
  KEY `idx_harvest_schedules_start_date` (`start_date`),
  KEY `idx_harvest_schedules_end_date` (`end_date`),
  KEY `idx_harvest_schedules_provider_id` (`provider_id`),
  CONSTRAINT `harvest_schedules_ibfk_1` FOREIGN KEY (`provider_id`) REFERENCES `providers` (`provider_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `harvest_schedules`
--

LOCK TABLES `harvest_schedules` WRITE;
/*!40000 ALTER TABLE `harvest_schedules` DISABLE KEYS */;
INSERT INTO `harvest_schedules` VALUES (3,'==provider_name==','Daily',1,'2010-03-23 00:00:00',
	date_add(current_timestamp, interval 30 minute),0,0,0,'','NOT_RUNNING','');

/*!40000 ALTER TABLE `harvest_schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `harvests`
--

DROP TABLE IF EXISTS `harvests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `harvests` (
  `harvest_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NULL DEFAULT NULL,
  `request` text,
  `result` longtext,
  `harvest_schedule_id` int(11) NOT NULL,
  `provider_id` int(11) NOT NULL,
  PRIMARY KEY (`harvest_id`),
  KEY `idx_harvests_provider_id` (`provider_id`),
  CONSTRAINT `harvests_ibfk_1` FOREIGN KEY (`provider_id`) REFERENCES `providers` (`provider_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=712 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `harvests`
--

LOCK TABLES `harvests` WRITE;
/*!40000 ALTER TABLE `harvests` DISABLE KEYS */;
/*!40000 ALTER TABLE `harvests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `harvests_to_records`
--

DROP TABLE IF EXISTS `harvests_to_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `harvests_to_records` (
  `harvest_to_record_id` int(11) NOT NULL AUTO_INCREMENT,
  `harvest_id` int(11) NOT NULL,
  `record_id` bigint(11) NOT NULL,
  PRIMARY KEY (`harvest_to_record_id`),
  KEY `idx_harvests_to_records_harvest_id` (`harvest_id`),
  KEY `idx_harvests_to_records_record_id` (`record_id`),
  CONSTRAINT `harvests_to_records_ibfk_1` FOREIGN KEY (`harvest_id`) REFERENCES `harvests` (`harvest_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `harvests_to_records`
--

LOCK TABLES `harvests_to_records` WRITE;
/*!40000 ALTER TABLE `harvests_to_records` DISABLE KEYS */;
/*!40000 ALTER TABLE `harvests_to_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `held_marcxml_holding`
--

DROP TABLE IF EXISTS `held_marcxml_holding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `held_marcxml_holding` (
  `held_marcxml_holding_id` int(11) NOT NULL AUTO_INCREMENT,
  `marcxml_holding_oai_id` varchar(255) NOT NULL,
  `marcxml_holding_004_field` varchar(255) NOT NULL,
  PRIMARY KEY (`held_marcxml_holding_id`),
  KEY `idx_marcxml_holding_oai_id` (`marcxml_holding_oai_id`),
  KEY `idx_marcxml_holding_004_field` (`marcxml_holding_004_field`)
) ENGINE=InnoDB AUTO_INCREMENT=11699 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `held_marcxml_holding`
--

LOCK TABLES `held_marcxml_holding` WRITE;
/*!40000 ALTER TABLE `held_marcxml_holding` DISABLE KEYS */;
/*!40000 ALTER TABLE `held_marcxml_holding` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `holding_manifestation`
--

DROP TABLE IF EXISTS `holding_manifestation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `holding_manifestation` (
  `holding_manifestation_id` int(11) NOT NULL AUTO_INCREMENT,
  `xc_holding_oai_id` varchar(255) NOT NULL,
  `marcxml_holding_004_field` varchar(255) NOT NULL,
  `manifestation_oai_id` varchar(255) NOT NULL,
  PRIMARY KEY (`holding_manifestation_id`),
  KEY `idx_manifestation_oai_id` (`manifestation_oai_id`),
  KEY `idx_xc_holding_oai_id` (`xc_holding_oai_id`),
  KEY `idx_xc_marcxml_holding_004_field` (`marcxml_holding_004_field`)
) ENGINE=InnoDB AUTO_INCREMENT=1233 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `holding_manifestation`
--

LOCK TABLES `holding_manifestation` WRITE;
/*!40000 ALTER TABLE `holding_manifestation` DISABLE KEYS */;
/*!40000 ALTER TABLE `holding_manifestation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jobs`
--

DROP TABLE IF EXISTS `jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jobs` (
  `job_id` int(11) NOT NULL AUTO_INCREMENT,
  `service_id` int(11) DEFAULT NULL,
  `harvest_schedule_id` int(11) DEFAULT NULL,
  `processing_directive_id` int(11) DEFAULT NULL,
  `output_set_id` int(11) DEFAULT NULL,
  `job_order` int(11) DEFAULT NULL,
  `job_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=433 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jobs`
--

LOCK TABLES `jobs` WRITE;
/*!40000 ALTER TABLE `jobs` DISABLE KEYS */;
==begin_comment_run_harvest==
insert into jobs values (1, 0, 3, 0, 0, 1, 'REPOSITORY');
==end_comment_run_harvest==
==begin_comment_run_norm==
insert into jobs values (1, 99, 3, 9, 22, 1, 'SERVICE');
==end_comment_run_norm==
==begin_comment_run_trans==
insert into jobs values (1, 199, 0, 10, 22, 1, 'SERVICE');
==end_comment_run_trans==
/*!40000 ALTER TABLE `jobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logs` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `warnings` int(11) NOT NULL DEFAULT '0',
  `errors` int(11) NOT NULL DEFAULT '0',
  `last_log_reset` datetime DEFAULT NULL,
  `log_file_name` varchar(255) NOT NULL,
  `log_file_location` varchar(512) NOT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
INSERT INTO `logs` VALUES (1,8,0,NULL,'Repository Management','logs/general/repositoryManagement.txt'),(2,0,0,NULL,'User Management','logs/general/userManagement.txt'),(3,0,0,NULL,'Authentication Server Management','logs/general/authServerManagement.txt'),(4,0,0,NULL,'MySQL','logs/general/mysql.txt'),(5,0,1379,NULL,'Solr Index','logs/general/solr.txt'),(6,0,0,NULL,'Jobs Management','logs/general/jobs.txt'),(7,0,0,NULL,'Service Management','logs/general/serviceManagement.txt'),(8,0,0,NULL,'MST Configuration','logs/general/configuration.txt');
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marc_bibliographic_to_xc_manifestation`
--

DROP TABLE IF EXISTS `marc_bibliographic_to_xc_manifestation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marc_bibliographic_to_xc_manifestation` (
  `bibliographic_manifestation_id` int(11) NOT NULL AUTO_INCREMENT,
  `bibliographic_oai_id` varchar(255) NOT NULL,
  `manifestation_oai_id` varchar(255) NOT NULL,
  `bibliographic_001_field` varchar(255) NOT NULL,
  PRIMARY KEY (`bibliographic_manifestation_id`),
  KEY `idx_bibliographic_oai_id` (`bibliographic_oai_id`),
  KEY `idx_bibliographic_001_field` (`bibliographic_001_field`)
) ENGINE=InnoDB AUTO_INCREMENT=35951 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marc_bibliographic_to_xc_manifestation`
--

LOCK TABLES `marc_bibliographic_to_xc_manifestation` WRITE;
/*!40000 ALTER TABLE `marc_bibliographic_to_xc_manifestation` DISABLE KEYS */;
/*!40000 ALTER TABLE `marc_bibliographic_to_xc_manifestation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oai_identifier_for_services`
--

DROP TABLE IF EXISTS `oai_identifier_for_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oai_identifier_for_services` (
  `oai_identifier_for_service_id` int(11) NOT NULL AUTO_INCREMENT,
  `next_oai_id` bigint(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`oai_identifier_for_service_id`),
  KEY `idx_oai_identifier_for_services_service_id` (`service_id`),
  CONSTRAINT `oai_identifier_for_services_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oai_identifier_for_services`
--

LOCK TABLES `oai_identifier_for_services` WRITE;
/*!40000 ALTER TABLE `oai_identifier_for_services` DISABLE KEYS */;
INSERT INTO `oai_identifier_for_services` VALUES (1,85520,1),(2,118240,2);
/*!40000 ALTER TABLE `oai_identifier_for_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processing_directives`
--

DROP TABLE IF EXISTS `processing_directives`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processing_directives` (
  `processing_directive_id` int(11) NOT NULL AUTO_INCREMENT,
  `source_provider_id` int(11) NOT NULL,
  `source_service_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  `output_set_id` bigint(11) DEFAULT NULL,
  `maintain_source_sets` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`processing_directive_id`),
  KEY `idx_processing_directives_source_provider_id` (`source_provider_id`),
  KEY `idx_processing_directives_source_service_id` (`source_service_id`),
  KEY `idx_processing_directives_service_id` (`service_id`),
  CONSTRAINT `processing_directives_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processing_directives`
--

LOCK TABLES `processing_directives` WRITE;
/*!40000 ALTER TABLE `processing_directives` DISABLE KEYS */;
INSERT INTO `processing_directives` VALUES 
(9,1,0,==service_id==,22,0),
(10,0,==service_id==,==service_id_2==,30,0);
/*!40000 ALTER TABLE `processing_directives` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processing_directives_to_input_formats`
--

DROP TABLE IF EXISTS `processing_directives_to_input_formats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processing_directives_to_input_formats` (
  `processing_directive_to_input_format_id` int(11) NOT NULL AUTO_INCREMENT,
  `processing_directive_id` int(11) NOT NULL,
  `format_id` int(11) NOT NULL,
  PRIMARY KEY (`processing_directive_to_input_format_id`),
  KEY `idx_pd_to_input_formats_processing_directive_id` (`processing_directive_id`),
  KEY `idx_services_to_input_formats_format_id` (`format_id`),
  CONSTRAINT `processing_directives_to_input_formats_ibfk_1` FOREIGN KEY (`processing_directive_id`) REFERENCES `processing_directives` (`processing_directive_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `processing_directives_to_input_formats_ibfk_2` FOREIGN KEY (`format_id`) REFERENCES `formats` (`format_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processing_directives_to_input_formats`
--

LOCK TABLES `processing_directives_to_input_formats` WRITE;
/*!40000 ALTER TABLE `processing_directives_to_input_formats` DISABLE KEYS */;
INSERT INTO `processing_directives_to_input_formats` VALUES (16,9,3),(17,10,3);
/*!40000 ALTER TABLE `processing_directives_to_input_formats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processing_directives_to_input_sets`
--

DROP TABLE IF EXISTS `processing_directives_to_input_sets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processing_directives_to_input_sets` (
  `processing_directive_to_input_set_id` int(11) NOT NULL AUTO_INCREMENT,
  `processing_directive_id` int(11) NOT NULL,
  `set_id` int(11) NOT NULL,
  PRIMARY KEY (`processing_directive_to_input_set_id`),
  KEY `idx_pd_to_input_sets_processing_directive_id` (`processing_directive_id`),
  KEY `idx_services_to_input_sets_set_id` (`set_id`),
  CONSTRAINT `processing_directives_to_input_sets_ibfk_1` FOREIGN KEY (`processing_directive_id`) REFERENCES `processing_directives` (`processing_directive_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processing_directives_to_input_sets`
--

LOCK TABLES `processing_directives_to_input_sets` WRITE;
/*!40000 ALTER TABLE `processing_directives_to_input_sets` DISABLE KEYS */;
-- BDA
INSERT INTO `processing_directives_to_input_sets` VALUES 
( 1, 9, 1),( 2, 9, 2),( 3, 9, 3),( 4, 9, 4),( 5, 9, 5),( 6, 9, 6),( 7, 9, 7),( 8, 9, 8),( 9, 9, 9),(10, 9,10),
(11, 9,11),(12, 9,12),(13, 9,13),(14, 9,14),(15, 9,15),(16, 9,16),(17, 9,17),(18, 9,18),(19, 9,19),(20, 9,20),
(21, 9,21),(22, 9,22),(23, 9,23),(24, 9,24),(25, 9,25),(26, 9,26),(27, 9,27),(28, 9,28),(29, 9,29),(30, 9,30),
(31,10, 1),(32,10, 2),(33,10, 3),(34,10, 4),(35,10, 5),(36,10, 6),(37,10, 7),(38,10, 8),(39,10, 9),(40,10, 0),
(41,10,11),(42,10,12),(43,10,13),(44,10,14),(45,10,15),(46,10,16),(47,10,17),(48,10,18),(49,10,19),(50,10,10),
(51,10,21),(52,10,22),(53,10,23),(54,10,24),(55,10,25),(56,10,26),(57,10,27),(58,10,28),(59,10,29),(60,10,20);
/*!40000 ALTER TABLE `processing_directives_to_input_sets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `providers`
--

DROP TABLE IF EXISTS `providers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `providers` (
  `provider_id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `name` varchar(255) NOT NULL,
  `oai_provider_url` varchar(255) NOT NULL,
  `title` varchar(127) DEFAULT NULL,
  `creator` varchar(127) DEFAULT NULL,
  `subject` varchar(63) DEFAULT NULL,
  `description` text,
  `publisher` varchar(127) DEFAULT NULL,
  `contributors` varchar(511) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `type` varchar(11) DEFAULT NULL,
  `format` varchar(63) DEFAULT NULL,
  `identifier` int(11) DEFAULT NULL,
  `language` varchar(15) DEFAULT NULL,
  `relation` varchar(255) DEFAULT NULL,
  `coverage` varchar(255) DEFAULT NULL,
  `rights` varchar(255) DEFAULT NULL,
  `service` tinyint(1) NOT NULL DEFAULT '1',
  `next_list_sets_list_formats` datetime DEFAULT NULL,
  `protocol_version` varchar(7) DEFAULT NULL,
  `last_validated` datetime DEFAULT NULL,
  `identify` tinyint(1) NOT NULL DEFAULT '1',
  `listformats` tinyint(1) NOT NULL DEFAULT '1',
  `listsets` tinyint(1) NOT NULL DEFAULT '1',
  `warnings` int(11) NOT NULL DEFAULT '0',
  `errors` int(11) NOT NULL DEFAULT '0',
  `records_added` int(11) NOT NULL DEFAULT '0',
  `records_replaced` int(11) NOT NULL DEFAULT '0',
  `last_oai_request` varchar(511) DEFAULT NULL,
  `last_harvest_end_time` datetime DEFAULT NULL,
  `last_log_reset` datetime DEFAULT NULL,
  `log_file_name` varchar(355) NOT NULL,
  PRIMARY KEY (`provider_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `providers`
--

LOCK TABLES `providers` WRITE;
/*!40000 ALTER TABLE `providers` DISABLE KEYS */;
INSERT INTO `providers` VALUES (1,'2010-03-11 00:00:00','2010-03-30 13:36:34','==provider_name==','==provider_url==',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,-1,NULL,NULL,NULL,NULL,1,NULL,'2.0','2010-03-12 00:00:00',1,1,1,3,84,15400,8400,NULL,'2010-03-29 00:00:00',NULL,'MST-instances\\MetadataServicesToolkit\\logs\\harvestIn\\out.txt');
/*!40000 ALTER TABLE `providers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `record_types`
--

DROP TABLE IF EXISTS `record_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `record_types` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `processing_order` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `record_types`
--

LOCK TABLES `record_types` WRITE;
/*!40000 ALTER TABLE `record_types` DISABLE KEYS */;
INSERT INTO `record_types` VALUES (1,'MARC-Bib',1),(2,'MARC-Holding',2),(3,'XC-Work',3),(4,'XC-Expression',4),(5,'XC-Manifestation',5),(6,'XC-Holding',6);
/*!40000 ALTER TABLE `record_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `records`
--

==begin_comment_run_harvest==
DROP TABLE IF EXISTS `records`;
CREATE TABLE `records` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `service_id` int(11) DEFAULT NULL,
  `identifier_1` char(10) NOT NULL,
  `identifier_2` char(10) NOT NULL,
  `identifier_full` char(60) DEFAULT NULL,
  `process_complete` char(1)     default 'N',
  `datestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `setSpec` char(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `records_identifier_2_idx` (`identifier_2`),
  KEY `records_datestamp_idx` (`datestamp`),
  KEY `records_service_id_idx` (`service_id`),
  KEY `records_process_complete_idx` (`process_complete`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
==end_comment_run_harvest==
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `records`
--

LOCK TABLES `records` WRITE;
/*!40000 ALTER TABLE `records` DISABLE KEYS */;
/*!40000 ALTER TABLE `records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `records_xml`
--

==begin_comment_run_harvest==
DROP TABLE IF EXISTS `records_xml`;
CREATE TABLE `records_xml` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `xml` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
==end_comment_run_harvest==
/*!40101 SET character_set_client = @saved_cs_client */;

/* delete r, rx from records r, records_xml rx where r.id=rx.id and r.process_complete='N'; */
==begin_comment_run_norm==
delete r, rx from records r, records_xml rx where r.service_id===service_id== and r.id=rx.id;
update records set process_complete='N';
==end_comment_run_norm==

==begin_comment_run_trans==
delete r, rx from records r, records_xml rx where r.service_id===service_id_2== and r.id=rx.id;
update records set process_complete='N' where service_id===service_id==;
==end_comment_run_trans==

--
-- Dumping data for table `records_xml`
--

LOCK TABLES `records_xml` WRITE;
/*!40000 ALTER TABLE `records_xml` DISABLE KEYS */;
/*!40000 ALTER TABLE `records_xml` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resumption_tokens`
--

DROP TABLE IF EXISTS `resumption_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resumption_tokens` (
  `resumption_token_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `set_spec` varchar(255) DEFAULT NULL,
  `metadata_format` varchar(511) DEFAULT NULL,
  `starting_from` timestamp NULL DEFAULT NULL,
  `until` timestamp NULL DEFAULT NULL,
  `offset` bigint(11) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`resumption_token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resumption_tokens`
--

LOCK TABLES `resumption_tokens` WRITE;
/*!40000 ALTER TABLE `resumption_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `resumption_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servers`
--

DROP TABLE IF EXISTS `servers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `servers` (
  `server_id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `port` int(11) NOT NULL,
  `username_attribute` varchar(255) NOT NULL,
  `start_location` varchar(255) NOT NULL,
  `institution` varchar(255) DEFAULT NULL,
  `forgot_password_url` varchar(255) DEFAULT NULL,
  `forgot_password_label` varchar(255) DEFAULT NULL,
  `show_forgot_password_link` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servers`
--

LOCK TABLES `servers` WRITE;
/*!40000 ALTER TABLE `servers` DISABLE KEYS */;
INSERT INTO `servers` VALUES (1,'','Local',2,0,'','','University Name','','',0);
/*!40000 ALTER TABLE `servers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services` (
  `service_id` int(11) NOT NULL AUTO_INCREMENT,
  `service_name` varchar(255) NOT NULL,
  `class_name` varchar(155) DEFAULT NULL,
  `warnings` int(11) NOT NULL DEFAULT '0',
  `errors` int(11) NOT NULL DEFAULT '0',
  `input_record_count` int(11) NOT NULL DEFAULT '0',
  `output_record_count` int(11) NOT NULL DEFAULT '0',
  `last_log_reset` datetime DEFAULT NULL,
  `log_file_name` varchar(255) NOT NULL,
  `harvest_out_warnings` int(11) NOT NULL DEFAULT '0',
  `harvest_out_errors` int(11) NOT NULL DEFAULT '0',
  `harvest_out_records_available` bigint(11) NOT NULL DEFAULT '0',
  `harvest_out_records_harvested` bigint(11) NOT NULL DEFAULT '0',
  `harvest_out_last_log_reset` datetime DEFAULT NULL,
  `harvest_out_log_file_name` varchar(255) NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `version` varchar(10) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
/*
INSERT INTO `services` VALUES 
(1,'MARCNormalization','xc.mst.services.normalization.NormalizationService',0,35000,75520,39993,NULL,'logs\\service\\MARCNormalization Service.txt',0,0,39993,0,NULL,'logs\\harvestOut\\MARCNormalization Service.txt','NOT_RUNNING','0.2.8',0),
(2,'MARCToXCTransformation','xc.mst.services.transformation.TransformationService',0,1027,33196,60629,NULL,'logs\\service\\MARCToXCTransformation Service.txt',0,0,60629,0,NULL,'logs\\harvestOut\\MARCToXCTransformation Service.txt','NOT_RUNNING','0.1.9',0),
(99,'DBMARCNormalization','xc.mst.services.normalization.DBNormalizationService',0,35000,75520,39993,NULL,'logs\\service\\MARCNormalization Service.txt',0,0,39993,0,NULL,'logs\\harvestOut\\MARCNormalization Service.txt','NOT_RUNNING','0.2.8',0),
(199,'DBMARCToXCTransformation','xc.mst.services.transformation.DBTransformationService',0,1027,33196,60629,NULL,'logs\\service\\MARCToXCTransformation Service.txt',0,0,60629,0,NULL,'logs\\harvestOut\\MARCToXCTransformation Service.txt','NOT_RUNNING','0.1.9',0);
*/
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services_to_input_formats`
--

DROP TABLE IF EXISTS `services_to_input_formats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services_to_input_formats` (
  `service_to_input_format_id` int(11) NOT NULL AUTO_INCREMENT,
  `service_id` int(11) NOT NULL,
  `format_id` int(11) NOT NULL,
  PRIMARY KEY (`service_to_input_format_id`),
  KEY `idx_services_to_input_formats_service_id` (`service_id`),
  KEY `idx_services_to_input_formats_format_id` (`format_id`),
  CONSTRAINT `services_to_input_formats_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `services_to_input_formats_ibfk_2` FOREIGN KEY (`format_id`) REFERENCES `formats` (`format_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=540 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services_to_input_formats`
--

LOCK TABLES `services_to_input_formats` WRITE;
/*!40000 ALTER TABLE `services_to_input_formats` DISABLE KEYS */;
INSERT INTO `services_to_input_formats` VALUES 
(485,1,3),(486,1,6),(487,2,3),(488,2,6),(533,99,3),(534,99,6),(535,199,3),(536,199,6);
/*!40000 ALTER TABLE `services_to_input_formats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services_to_output_formats`
--

DROP TABLE IF EXISTS `services_to_output_formats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services_to_output_formats` (
  `service_to_output_format_id` int(11) NOT NULL AUTO_INCREMENT,
  `service_id` int(11) NOT NULL,
  `format_id` int(11) NOT NULL,
  PRIMARY KEY (`service_to_output_format_id`),
  KEY `idx_services_to_output_formats_service_id` (`service_id`),
  KEY `idx_services_to_output_formats_format_id` (`format_id`),
  CONSTRAINT `services_to_output_formats_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `services_to_output_formats_ibfk_2` FOREIGN KEY (`format_id`) REFERENCES `formats` (`format_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=267 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services_to_output_formats`
--

LOCK TABLES `services_to_output_formats` WRITE;
/*!40000 ALTER TABLE `services_to_output_formats` DISABLE KEYS */;
INSERT INTO `services_to_output_formats` VALUES (243,1,3),(244,2,7),(266,99,3);
/*!40000 ALTER TABLE `services_to_output_formats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services_to_output_sets`
--

DROP TABLE IF EXISTS `services_to_output_sets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services_to_output_sets` (
  `service_to_output_set_id` int(11) NOT NULL AUTO_INCREMENT,
  `service_id` int(11) NOT NULL,
  `set_id` int(11) NOT NULL,
  PRIMARY KEY (`service_to_output_set_id`),
  KEY `idx_services_to_output_sets_service_id` (`service_id`),
  KEY `idx_services_to_output_sets_set_id` (`set_id`),
  CONSTRAINT `services_to_output_sets_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `services_to_output_sets_ibfk_2` FOREIGN KEY (`set_id`) REFERENCES `sets` (`set_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=306 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services_to_output_sets`
--

LOCK TABLES `services_to_output_sets` WRITE;
/*!40000 ALTER TABLE `services_to_output_sets` DISABLE KEYS */;
INSERT INTO `services_to_output_sets` VALUES (302,1,9),(303,1,18),(304,2,14),(305,99,22);
/*!40000 ALTER TABLE `services_to_output_sets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sets`
--

DROP TABLE IF EXISTS `sets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sets` (
  `set_id` int(11) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(127) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `set_spec` varchar(127) NOT NULL,
  `is_provider_set` tinyint(1) DEFAULT NULL,
  `is_record_set` tinyint(1) DEFAULT NULL,
  `provider_id` int(11) NOT NULL,
  PRIMARY KEY (`set_id`),
  KEY `idx_sets_set_spec` (`set_spec`),
  KEY `idx_sets_provider_id` (`provider_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sets`
--

LOCK TABLES `sets` WRITE;
/*!40000 ALTER TABLE `sets` DISABLE KEYS */;
INSERT INTO `sets` VALUES 
(1,'MARCXML Bibliographic Records','A set of all MARCXML Bibliographic records in the repository.','MARCXMLbibliographic',0,1,0),
(2,'MARCXML Holdings Records','A set of all MARCXML holdings records in the repository.','MARCXMLholdings',0,1,0),
(3,'MARCXML Authority Records','A set of all MARCXML Authority records in the repository.','MARCXMLauthority',0,1,0),
(4,'Bibliographic records',NULL,'bib',1,0,1),
(5,'Authority records',NULL,'auth',1,0,1),
(6,'Holdings record',NULL,'hold',1,0,1),
(7,'Classification records',NULL,'class',1,0,1),
(8,'Community Information records',NULL,'comm',1,0,1),
(9,'norm_output_set_name',NULL,'norm_output_set_spec',0,0,0),
(10,'137',NULL,'137',0,1,1),
(11,'137:bib',NULL,'137:bib',0,1,1),
(12,'137:hold',NULL,'137:hold',0,1,1),
(13,'MARCXML Holding Records','A set of all MARCXML Holding records in the repository.','MARCXMLholding',0,1,0),
(14,'trans_output_set_name',NULL,'trans_output_set_spec',0,0,0),
(15,'132---40k',NULL,'132---40k',0,1,1),
(16,'132---40k:bib',NULL,'132---40k:bib',0,1,1),
(17,'132---40k:hold',NULL,'132---40k:hold',0,1,1),
(18,'132_40k_norm_output_set_name',NULL,'132_40k_norm_output_set_spec',0,0,0),
(19,'137---175',NULL,'137---175',0,1,1),
(20,'137---175:bib',NULL,'137---175:bib',0,1,1),
(21,'137---175:hold',NULL,'137---175:hold',0,1,1),
(22,'db_norm_service_set',NULL,'db_norm_service_spec',0,0,0),
(23,'132---1M',NULL,'132---1M',0,1,1),
(24,'132---1M:bib',NULL,'132---1M:bib',0,1,1),
(25,'132---1M:hold',NULL,'132---1M:hold',0,1,1),
(26,'137---6M',NULL,'137---6M',0,1,1),
(27,'137---6M:auth',NULL,'137---6M:auth',0,1,1),
(28,'137---6M:bib',NULL,'137---6M:bib',0,1,1),
(29,'137---6M:hold',NULL,'137---6M:hold',0,1,1),
(30,'db_trans_service_set',NULL,'db_trans_service_spec',0,0,0);
/*!40000 ALTER TABLE `sets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `top_level_tabs`
--

DROP TABLE IF EXISTS `top_level_tabs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `top_level_tabs` (
  `top_level_tab_id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_name` varchar(63) NOT NULL,
  `tab_order` int(11) NOT NULL,
  PRIMARY KEY (`top_level_tab_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `top_level_tabs`
--

LOCK TABLES `top_level_tabs` WRITE;
/*!40000 ALTER TABLE `top_level_tabs` DISABLE KEYS */;
INSERT INTO `top_level_tabs` VALUES (1,'Repositories',1),(2,'Harvest',2),(3,'Services',3),(4,'Processing Rules',4),(5,'Browse Records',5),(6,'Logs',6),(7,'Users/Groups',7),(8,'Configuration',8);
/*!40000 ALTER TABLE `top_level_tabs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(63) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `server_id` int(11) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `account_created` datetime NOT NULL,
  `failed_login_attempts` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `idx_users_server_id` (`server_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`server_id`) REFERENCES `servers` (`server_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','Metadata Services Toolkit','admin','L7I6GIFm7yfGo9cqko8U9jQBtQo=','MST_admin@mst.com',1,'2010-03-30 00:00:00','2008-10-20 00:00:00',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_to_groups`
--

DROP TABLE IF EXISTS `users_to_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users_to_groups` (
  `user_to_group_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`user_to_group_id`),
  KEY `idx_users_to_groups_user_id` (`user_id`),
  KEY `idx_users_to_groups_group_id` (`group_id`),
  CONSTRAINT `users_to_groups_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `users_to_groups_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_to_groups`
--

LOCK TABLES `users_to_groups` WRITE;
/*!40000 ALTER TABLE `users_to_groups` DISABLE KEYS */;
INSERT INTO `users_to_groups` VALUES (114,1,1);
/*!40000 ALTER TABLE `users_to_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xc_id_for_frbr_elements`
--

DROP TABLE IF EXISTS `xc_id_for_frbr_elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xc_id_for_frbr_elements` (
  `xc_id_for_frbr_element_id` int(11) NOT NULL AUTO_INCREMENT,
  `next_xc_id` bigint(11) NOT NULL,
  `element_id` int(11) NOT NULL,
  PRIMARY KEY (`xc_id_for_frbr_element_id`),
  KEY `idx_xc_id_for_frbr_elements_element_id` (`element_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xc_id_for_frbr_elements`
--

LOCK TABLES `xc_id_for_frbr_elements` WRITE;
/*!40000 ALTER TABLE `xc_id_for_frbr_elements` DISABLE KEYS */;
INSERT INTO `xc_id_for_frbr_elements` VALUES (1,465652,6);
/*!40000 ALTER TABLE `xc_id_for_frbr_elements` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-03-30  9:39:12
