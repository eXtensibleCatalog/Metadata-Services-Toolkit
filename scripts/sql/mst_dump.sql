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

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `metadataservicestoolkit` /*!40100 DEFAULT CHARACTER SET utf8 */;

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
INSERT INTO `harvest_schedules` VALUES (3,'==provider_name==','Hourly',1,'2010-03-23 00:00:00',null,date_format(current_timestamp(),'%i')+1,0,-1,'','NOT_RUNNING','');

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
INSERT INTO `logs` VALUES (1,8,0,NULL,'Repository Management','MST-instances\\MetadataServicesToolkit\\logs/general/repositoryManagement.txt'),(2,0,0,NULL,'User Management','MST-instances\\MetadataServicesToolkit\\logs/general/userManagement.txt'),(3,0,0,NULL,'Authentication Server Management','MST-instances\\MetadataServicesToolkit\\logs/general/authServerManagement.txt'),(4,0,0,NULL,'MySQL','MST-instances\\MetadataServicesToolkit\\logs/general/mysql.txt'),(5,0,1379,NULL,'Solr Index','MST-instances\\MetadataServicesToolkit\\logs/general/solr.txt'),(6,0,0,NULL,'Jobs Management','MST-instances\\MetadataServicesToolkit\\logs/general/jobs.txt'),(7,0,0,NULL,'Service Management','MST-instances\\MetadataServicesToolkit\\logs/general/serviceManagement.txt'),(8,0,0,NULL,'MST Configuration','MST-instances\\MetadataServicesToolkit\\logs/general/configuration.txt');
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
INSERT INTO `processing_directives` VALUES (9,1,0,==service_id==,22,0);
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
INSERT INTO `processing_directives_to_input_formats` VALUES (16,9,3);
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
( 1,9, 1),( 2,9, 2),( 3,9, 3),( 4,9, 4),( 5,9, 5),( 6,9, 6),( 7,9, 7),( 8,9, 8),( 9,9, 9),(10,9,10),
(11,9,11),(12,9,12),(13,9,13),(14,9,14),(15,9,15),(16,9,16),(17,9,17),(18,9,18),(19,9,19),(20,9,20),
(21,9,21),(22,9,22);
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

DROP TABLE IF EXISTS `records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `records` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `service_id` int(11) DEFAULT NULL,
  `identifier_1` char(10) NOT NULL,
  `identifier_2` char(10) NOT NULL,
  `identifier_full` char(60) DEFAULT NULL,
  `datestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `setSpec` char(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `records_identifier_2_idx` (`identifier_2`),
  KEY `records_datestamp_idx` (`datestamp`),
  KEY `records_service_id_idx` (`service_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
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

DROP TABLE IF EXISTS `records_xml`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `records_xml` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `xml` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `service_jar` varchar(255) NOT NULL,
  `service_configuration` mediumtext,
  `class_name` varchar(155) DEFAULT NULL,
  `identifier` varchar(255) NOT NULL,
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
  `xccfg_file_name` varchar(255) NOT NULL,
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
INSERT INTO `services` VALUES (1,'MARCNormalization Service','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceJar\\NormalizationService.jar','#-----------------------------------------\n\n#-----------------------------------------\nENABLED STEPS\n#-----------------------------------------\n\n#*****************************************\n# Whether or not to run each of the normalization setps\n# A step will run if its value is 1\n# values can only be either 1 or 0.\n#*****************************************\n\n# 1 to remove the 003 if its value is \"OCoLC\"\nRemoveOCoLC003 = 0\n\n# 1 to create a field with a DCMI Type based on \n# the original record\'s Leader 06 value\nDCMIType06 = 1\n\n# 1to create a field with the MARC vocabulary for\n# the original record\'s Leader 06 value\nLeader06Vocab = 1\n\n# 1 to create a field with a term based on \n# the original record\'s Leader 06 value\n007Vocab06 = 1\n\n# 1 to create a field with a mode of issuance based on \n# the original record\'s Leader 07 value \nModeOfIssuance = 1\n\n# 1 to create a new 035 field with the\n# organization code in the record and the\n# record\'s control number \nMoveMARCOrgCode = 1\n\n# 1 if the MoveMARCOrgCode step should run regardless of the \n# 003 value, false if it should only run if the 003 value\n# is the Organization Code defined in the main configuration\n# file.  This value is ignored if the MoveMARCOrgCode step\n# is not configured to run.\nMoveMARCOrgCode_moveAll = 0\n\n# 1 to create a field with a DCMI Type based on \n# the original record\'s 007 offset 00  value \nDCMIType007 = 1\n\n# 1 to create a field with a term based on \n# the original record\'s 007 offset 00 value \n007Vocab = 1\n\n# 1 to create a field with the SMD vocabulary term based on \n# the original record\'s 007 offset 00 and offset 01 values \n007SMDVocab = 1\n\n# 1 to create a field with either \"Fiction\" or \"Non-Fiction\"\n# depending on the other fields in the record \nFictionOrNonfiction = 1\n\n# 1 to create a field with the date range specified\n# in the 008 field if 008 offset 6 is \'r\' \n008DateRange = 1\n\n# 1 to create a 502 field ith the value \"Thesis.\"\n# if there is not already a 502 field and the\n# leader 06 is \'1\' and 008 24-27 contains an \'m\'\n008Thesis = 1\n\nISBNCleanup = 1\n\n# 1 to create a field with a language code for \n# each unique language code found in the original \n# record\'s 008 or 041 fields. \nLanguageSplit = 1\n\n# 1 to create a field with a language term for \n# each language code created by the LanguageSplit\n# normalization step. \nLanguageTerm = 1\n\n# 1 to create an audience field for the\n# audience suggested by the 008 offset 22 \n# value. \n008Audience = 1\n\n# 1 to create a new 035 field with the\n# organization code in the MST configuration file\n# and the original record\'s control number \nSupplyMARCOrgCode = 1\n\n# Organization code \nOrganizationCode = NRU\n\n# 1 to cleanup common problems with OCoLC 035 fields. \nFix035 = 1\n\n# 1 to cleanup OCoLC 035 fields for $9. By default 0\nFix035Code9 = 0\n\n# 1 to remove leading zeros . \n035LeadingZero = 1\n\n# 1 to remove duplicate 035 fields. \nDedup035 = 1\n\n# 1 to set 100, 110, and 111 $4 fields to\n# \"Author\" if they are not currently set and\n# leader 06 is \'a\' \nRoleAuthor = 1\n\n# 1 to set 100, 110, and 111 $4 fields to\n# \"Composer\" if they are not currently set and\n# leader 06 is \'c\' \nRoleComposer = 1\n\n# 1 to copy the 245 field into the 240 field\n# if there is neither a 130, 240, or 243 field. \nUniformTitle = 1\n\n# 1 to change 655 $2 to \"NRUgenre\" and delete the 655 $5 subfield\n# for each 655 field with $2 = \"local\" and $5 = \"NRU\"\nNRUGenre = 0\n\n# 1 to copy 600, 610, 611, 630, and 650 fields and 6xx $x subfields into\n# new fields containing information on the topic \nTopicSplit = 1\n\n# 1 to copy 648 fields and 6xx $y subfields into\n# new fields containing information on the chronological subject \nChronSplit = 1\n\n# 1 to copy 651 fields and 6xx $z subfields into\n# new fields containing information on the geographic subject \nGeogSplit = 1\n\n# 1 to copy 655 fields and 6xx $v subfields into\n# new fields containing information on the genre \nGenreSplit = 1\n\n# 1 to remove duplicate DCMI type fields. \nDedupDCMIType = 1\n\n# 1 to remove duplicate 007 Vocab fields. \nDedup007Vocab = 1\n\n# 1 to replace location codes left by \n# ILS with the actual location. \nBibLocationName = 0\n\n# 1 to replace location codes left by \n# III with the actual location. \nIIILocationName = 0\n\n# 1 to remove 945 field if it\'s  \n# $5 does not match organization code \nremove945Field = 1\n\n# 1 to copy 600, 610, 611, 700, 710, 711, 800, 810, and 811 fields\n# containing a $t subfield into a 9xx field.  This will facilitate \n# authority matching in other XC services.\nSeperateName = 1\n\n# 1 to remove duplicate 959, 963, 965, 967, and 969 fields. \nDedup9XX = 1\n\n# 1 to create a 246 field without an initial article . \nTitleArticle = 1\n\n# 1 to replace location codes left by \n# ILS with the actual location. By default 0 \nHoldingsLocationName = 0\n\n# 1 to replace location codes by \n# location limit name. By default 0\nLocationLimitName = 0\n\n#-----------------------------------------\nFIELD 007 OFFSET 00 TO DCMI TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps 007 offset 00 values to the DCMI Type\n# they should be associated with.\n#\n# For example, The line \"a = image\" says that the DCMI \n# Type is \"image\" if the 007 offset 00 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for 007 offset 00\n# values not represented below, and unwanted mappings may be deleted\n#***************************************** \n\na = Image\ng = StillImage\nk = StillImage\nm = MovingImage\nq = Image\nr = StillImage\ns = Sound\nt = Text\nv = MovingImage\n\n#-----------------------------------------\nFIELD 007 OFFSET 00 TO FULL TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps 007 offset 00 values to the \n# Type they should be associated with.\n#\n# For example, The line \"a = map\" says that the\n# Type is \"map\" if the 007 offset 00 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for 007 offset 00\n# values not represented below, and unwanted mappings may be deleted\n# ***************************************** \n\na = Map\nc = Electronic resource\nd = Globe\nf = Tactile material\ng = Projected graphic\nh = Microform\nk = Nonprojected graphic\nm = Motion picture\no = Kit\nq = Notated music\nr = Remote-sensing image\ns = Sound recording\nt = Text\nv = Video recording\nz = Multiple\n\n#-----------------------------------------\nFIELD 007 OFFSET 00 TO SMD TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps 007 offset 00 and 01 values to the SMD type term\n# they should be associated with.\n#\n# For example, The line \"ad = atlas\" says that the SMD\n# Type is \"atlas\" if the 007 offset 00 is \"a\" and 007 offset \n# 00 is \"d\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for 007 values \n# not represented below, and unwanted mappings may be deleted\n#***************************************** \n \nad = Atlas\nag = Diagram\naj = Map\nak = Profile\naq = Model\nar = Remote-sensing image\nas = Section\n\nca = Tape cartridge\ncb = Chip cartridge\ncc = Computer optical disc cartridge\ncf = Tape cassette\nch = Tape reel\ncj = Magnetic disk\ncm = Magneto-optical disk\nco = Optical disk\ncr = Remote\n\nda = Celestial globe\ndb = Planetary or lunar globe\ndc = Terrestrial globe\nde = Earth moon globe\n\nfa = Moon\nfb = Braille\nfc = Combination\n\ngc = Filmstrip cartridge\ngd = Filmslip\ngf = Other type of filmstrip\ngo = Filmstrip roll\ngs = Slide\ngt = Transparency\n\nha = Aperture card\nhb = Microfilm cartridge\nhc = Microfilm cassette\nhd = Microfilm reel\nhe = Microfiche\nhf = Microfiche cassette\nhg = Micropaque\n\nkc = College\nkd = Drawing\nke = Painting\nkf = Photomechanical print\nkg = Photonegative\nkh = Photoprint\nki = Picture\nkj = Print\nkl = Technical drawing\nkn = Chart\nko = Flash card\n\nmc = Film cartridge\nmf = Film cassette\nmr = Film reel\n\nou = Kit\n\nqu = Notated music\n\nru = Remote-sensing image\n\nsd = Sound disk\nse = Cylinder\nsg = Sound cartridge\nsi = Sound-track film\nsq = Roll\nss = Sound cassette\nst = Sound-tape reel\nsw = Wire recording\n\nta = Regular print\ntb = Large print\ntc = Braille\ntd = Loose-leaf\n\nvc = Videocartridge\nvd = Videodisc\nvf = Videocassette\nvr = Videoreel\n\nzm = Multiple\n\n#-----------------------------------------\nFIELD 008 OFFSET 22 TO AUDIENCE\n#-----------------------------------------\n\n#*****************************************\n# This section maps 008 offset 22 values to the audience\n# they should be associated with.\n#\n# For example, The line \"a = Preschool\" says that the\n# audience is \"Preschool\" if the 008 offset 22 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for 008 offset 022\n# values not represented below, and unwanted mappings may be deleted\n#***************************************** \n\na = Preschool\nb = Primary\nc = Pre-adolescent\nd = Adolescent\ne = Adult\nf = Specialized\ng = General\nj = Juvenile\n\n#-----------------------------------------\nLANGUAGE CODE TO LANGUAGE\n#-----------------------------------------\n\n#*****************************************\n# This section maps language codes to the languages\n# they should be associated with.\n#\n# For example, The line \"aar = Afar\" says that the\n# language is \"Afar\" if the language code is \"aar\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for language codes\n# not represented below, and unwanted mappings may be deleted\n#***************************************** \n\naar = Afar\nabk = Abkhaz\nace = Achinese\nach = Acoli\nada = Adangme\nady = Adygei\nafa = Afroasiatic (Other)\nafh = Afrihili (Artificial language)\nafr = Afrikaans\nain = Ainu\najm = Aljamía\naka = Akan\nakk = Akkadian\nalb = Albanian\nale = Aleut\nalg = Algonquian (Other)\nalt = Altai\namh = Amharic\nang = English, Old (ca. 450-1100)\nanp = Angika\napa = Apache languages\nara = Arabic\narc = Aramaic\narg = Aragonese Spanish\narm = Armenian\narn = Mapuche\narp = Arapaho\nart = Artificial (Other)\narw = Arawak\nasm = Assamese\nast = Bable\nath = Athapascan (Other)\naus = Australian languages\nava = Avaric\nave = Avestan\nawa = Awadhi\naym = Aymara\naze = Azerbaijani\nbad = Banda languages\nbai = Bamileke languages\nbak = Bashkir\nbal = Baluchi\nbam = Bambara\nban = Balinese\nbaq = Basque\nbas = Basa\nbat = Baltic (Other)\nbej = Beja\nbel = Belarusian\nbem = Bemba\nben = Bengali\nber = Berber (Other)\nbho = Bhojpuri\nbih = Bihari\nbik = Bikol\nbin = Edo\nbis = Bislama\nbla = Siksika\nbnt = Bantu (Other)\nbos = Bosnian\nbra = Braj\nbre = Breton\nbtk = Batak\nbua = Buriat\nbug = Bugis\nbul = Bulgarian\nbur = Burmese\nbyn = Bilin\ncad = Caddo\ncai = Central American Indian (Other)\ncam = Khmer\ncar = Carib\ncat = Catalan\ncau = Caucasian (Other)\nceb = Cebuano\ncel = Celtic (Other)\ncha = Chamorro\nchb = Chibcha\nche = Chechen\nchg = Chagatai\nchi = Chinese\nchk = Chuukese\nchm = Mari\nchn = Chinook jargon\ncho = Choctaw\nchp = Chipewyan\nchr = Cherokee\nchu = Church Slavic\nchv = Chuvash\nchy = Cheyenne\ncmc = Chamic languages\ncop = Coptic\ncor = Cornish\ncos = Corsican\ncpe = Creoles and Pidgins, English-based (Other)\ncpf = Creoles and Pidgins, French-based (Other)\ncpp = Creoles and Pidgins, Portuguese-based (Other)\ncre = Cree\ncrh = Crimean Tatar\ncrp = Creoles and Pidgins (Other)\ncsb = Kashubian\ncus = Cushitic (Other)\ncze = Czech\ndak = Dakota\ndan = Danish\ndar = Dargwa\nday = Dayak\ndel = Delaware\nden = Slave\ndgr = Dogrib\ndin = Dinka\ndiv = Divehi\ndoi = Dogri\ndra = Dravidian (Other)\ndsb = Lower Sorbian\ndua = Duala\ndum = Dutch, Middle (ca. 1050-1350)\ndut = Dutch\ndyu = Dyula\ndzo = Dzongkha\nefi = Efik\negy = Egyptian\neka = Ekajuk\nelx = Elamite\neng = English\nenm = English, Middle (1100-1500)\nepo = Esperanto\nesk = Eskimo languages\nesp = Esperanto\nest = Estonian\neth = Ethiopic\newe = Ewe\newo = Ewondo\nfan = Fang\nfao = Faroese\nfar = Faroese\nfat = Fanti\nfij = Fijian\nfil = Filipino\nfin = Finnish\nfiu = Finno-Ugrian (Other)\nfon = Fon\nfre = French\nfri = Frisian\nfrm = French, Middle (ca. 1300-1600)\nfro = French, Old (ca. 842-1300)\nfrr = North Frisian\nfrs = East Frisian\nfry = Frisian\nful = Fula\nfur = Friulian\ngaa = Gã\ngae = Scottish Gaelix\ngag = Galician\ngal = Oromo\ngay = Gayo\ngba = Gbaya\ngem = Germanic (Other)\ngeo = Georgian\nger = German\ngez = Ethiopic\ngil = Gilbertese\ngla = Scottish Gaelic\ngle = Irish\nglg = Galician\nglv = Manx\ngmh = German, Middle High (ca. 1050-1500)\ngoh = German, Old High (ca. 750-1050)\ngon = Gondi\ngor = Gorontalo\ngot = Gothic\ngrb = Grebo\ngrc = Greek, Ancient (to 1453)\ngre = Greek, Modern (1453- )\ngrn = Guarani\ngsw = Swiss German\ngua = Guarani\nguj = Gujarati\ngwi = Gwich\'in\nhai = Haida\nhat = Haitian French Creole\nhau = Hausa\nhaw = Hawaiian\nheb = Hebrew\nher = Herero\nhil = Hiligaynon\nhim = Himachali\nhin = Hindi\nhit = Hittite\nhmn = Hmong\nhmo = Hiri Motu\nhsb = Upper Sorbian\nhun = Hungarian\nhup = Hupa\niba = Iban\nibo = Igbo\nice = Icelandic\nido = Ido\niii = Sichuan Yi\nijo = Ijo\niku = Inuktitut\nile = Interlingue\nilo = Iloko\nina = Interlingua (International Auxiliary Language Association)\ninc = Indic (Other)\nind = Indonesian\nine = Indo-European (Other)\ninh = Ingush\nint = Interlingua (International Auxiliary Language Association)\nipk = Inupiaq\nira = Iranian (Other)\niri = Irish\niro = Iroquoian (Other)\nita = Italian\njav = Javanese\njbo = Lojban (Artificial language)\njpn = Japanese\njpr = Judeo-Persian\njrb = Judeo-Arabic\nkaa = Kara-Kalpak\nkab = Kabyle\nkac = Kachin\nkal = Kalâtdlisut\nkam = Kamba\nkan = Kannada\nkar = Karen languages\nkas = Kashmiri\nkau = Kanuri\nkaw = Kawi\nkaz = Kazakh\nkbd = Kabardian\nkha = Khasi\nkhi = Khoisan (Other)\nkhm = Khmer\nkho = Khotanese\nkik = Kikuyu\nkin = Kinyarwanda\nkir = Kyrgyz\nkmb = Kimbundu\nkok = Konkani\nkom = Komi\nkon = Kongo\nkor = Korean\nkos = Kusaie\nkpe = Kpelle\nkrc = Karachay-Balkar\nkrl = Karelian\nkro = Kru (Other)\nkru = Kurukh\nkua = Kuanyama\nkum = Kumyk\nkur = Kurdish\nkus = Kusaie\nkut = Kootenai\nlad = Ladino\nlah = Lahnda\nlam = Lamba (Zambia and Congo)\nlan = Occitan (post 1500)\nlao = Lao\nlap = Sami\nlat = Latin\nlav = Latvian\nlez = Lezgian\nlim = Limburgish\nlin = Lingala\nlit = Lithuanian\nlol = Mongo-Nkundu\nloz = Lozi\nltz = Luxembourgish\nlua = Luba-Lulua\nlub = Luba-Katanga\nlug = Ganda\nlui = Luiseño\nlun = Lunda\nluo = Luo (Kenya and Tanzania)\nlus = Lushai\nmac = Macedonian\nmad = Madurese\nmag = Magahi\nmah = Marshallese\nmai = Maithili\nmak = Makasar\nmal = Malayalam\nman = Mandingo\nmao = Maori\nmap = Austronesian (Other)\nmar = Marathi\nmas = Masai\nmax = Manx\nmay = Malay\nmdf = Moksha\nmdr = Mandar\nmen = Mende\nmga = Irish, Middle (ca. 1100-1550)\nmic = Micmac\nmin = Minangkabau\nmis = Miscellaneous languages\nmkh = Mon-Khmer (Other)\nmla = Malagasy\nmlg = Malagasy\nmlt = Maltese\nmnc = Manchu\nmni = Manipuri\nmno = Manobo languages\nmoh = Mohawk\nmol = Moldavian\nmon = Mongolian\nmos = Mooré\nmun = Munda (Other)\nmus = Creek\nmwl = Mirandese\nmwr = Marwari\nmyn = Mayan languages\nmyv = Erzya\nnah = Nahuatl\nnai = North American Indian (Other)\nnap = Neapolitan Italian\nnau = Nauru\nnav = Navajo\nnbl = Ndebele (South Africa)\nnde = Ndebele (Zimbabwe)\nndo = Ndonga\nnds = Low German\nnep = Nepali\nnew = Newari\nnia = Nias\nnic = Niger-Kordofanian (Other)\nniu = Niuean\nnno = Norwegian (Nynorsk)\nnob = Norwegian (Bokmål)\nnog = Nogai\nnon = Old Norse\nnor = Norwegian\nnqo = N\'Ko\nnso = Northern Sotho\nnub = Nubian languages\nnwc = Newari, Old\nnya = Nyanja\nnym = Nyamwezi\nnyn = Nyankole\nnyo = Nyoro\nnzi = Nzima\noci = Occitan (post 1500)\noji = Ojibwa\nori = Oriya\norm = Oromo\nosa = Osage\noss = Ossetic\nota = Turkish, Ottoman\noto = Otomian languages\npaa = Papuan (Other)\npag = Pangasinan\npal = Pahlavi\npam = Pampanga\npan = Panjabi\npap = Papiamento\npau = Palauan\npeo = Old Persian (ca. 600-400 B.C.)\nper = Persian\nphi = Philippine (Other)\nphn = Phoenician\npli = Pali\npol = Polish\npon = Ponape\npor = Portuguese\npra = Prakrit languages\npro = Provençal (to 1500)\npus = Pushto\nque = Quechua\nraj = Rajasthani\nrap = Rapanui\nrar = Rarotongan\nroa = Romance (Other)\nroh = Raeto-Romance\nrom = Romani\nrum = Romanian\nrun = Rundi\nrup = Aromanian\nrus = Russian\nsad = Sandawe\nsag = Sango (Ubangi Creole)\nsah = Yakut\nsai = South American Indian (Other)\nsal = Salishan languages\nsam = Samaritan Aramaic\nsan = Sanskrit\nsao = Samoan\nsas = Sasak\nsat = Santali\nscc = Serbian\nscn = Sicilian Italian\nsco = Scots\nscr = Croatian\nsel = Selkup\nsem = Semitic (Other)\nsga = Irish, Old (to 1100)\nsgn = Sign languages\nshn = Shan\nsho = Shona\nsid = Sidamo\nsin = Sinhalese\nsio = Siouan (Other)\nsit = Sino-Tibetan (Other)\nsla = Slavic (Other)\nslo = Slovak\nslv = Slovenian\nsma = Southern Sami\nsme = Northern Sami\nsmi = Sami\nsmj = Lule Sami\nsmn = Inari Sami\nsmo = Samoan\nsms = Skolt Sami\nsna = Shona\nsnd = Sindhi\nsnh = Sinhalese\nsnk = Soninke\nsog = Sogdian\nsom = Somali\nson = Songhai\nsot = Sotho\nspa = Spanish\nsrd = Sardinian\nsrn = Sranan\nsrr = Serer\nssa = Nilo-Saharan (Other)\nsso = Sotho\nssw = Swazi\nsuk = Sukuma\nsun = Sundanese\nsus = Susu\nsux = Sumerian\nswa = Swahili\nswe = Swedish\nswz = Swazi\nsyc = Syriac\nsyr = Syriac, Modern\ntag = Tagalog\ntah = Tahitian\ntai = Tai (Other)\ntaj = Tajik\ntam = Tamil\ntar = Tatar\ntat = Tatar\ntel = Telugu\ntem = Temne\nter = Terena\ntet = Tetum\ntgk = Tajik\ntgl = Tagalog\ntha = Thai\ntib = Tibetan\ntig = Tigré\ntir = Tigrinya\ntiv = Tiv\ntkl = Tokelauan\ntlh = Klingon (Artificial language)\ntli = Tlingit\ntmh = Tamashek\ntog = Tonga (Nyasa)\nton = Tongan\ntpi = Tok Pisin\ntru = Truk\ntsi = Tsimshian\ntsn = Tswana\ntso = Tsonga\ntsw = Tswana\ntuk = Turkmen\ntum = Tumbuka\ntup = Tupi languages\ntur = Turkish\ntut = Altaic (Other)\ntvl = Tuvaluan\ntwi = Twi\ntyv = Tuvinian\nudm = Udmurt\nuga = Ugaritic\nuig = Uighur\nukr = Ukrainian\numb = Umbundu\nurd = Urdu\nuzb = Uzbek\nvai = Vai\nven = Venda\nvie = Vietnamese\nvol = Volapük\nvot = Votic\nwak = Wakashan languages\nwal = Wolayta\nwar = Waray\nwas = Washo\nwel = Welsh\nwen = Sorbian (Other)\nwln = Walloon\nwol = Wolof\nxal = Oirat\nxho = Xhosa\nyao = Yao (Africa)\nyap = Yapese\nyid = Yiddish\nyor = Yoruba\nypk = Yupik languages\nzap = Zapotec\nzbl = Blissymbolics\nzen = Zenaga\nzha = Zhuang\nznd = Zande languages\nzul = Zulu\nzun = Zuni\nzxx = No linguistic content\nzza = Zaza\n\n#-----------------------------------------\nLEADER 06 TO DCMI TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps leader 06 values to the DCMI Type\n# they should be associated with.\n#\n# For example, The line \"a = text\" says that the DCMI \n# Type is \"text if leader06 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for leader 06 \n# values not represented below, and unwanted mappings may be deleted\n#*****************************************\n\na = Text\nc = Image\nd = Image\ng = Image\ni = Sound\nj = Sound\nk = StillImage\nr = PhysicalObject\nt = Text\n\n#-----------------------------------------\nLEADER 06 TO FULL TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps leader 06 values to the \n# type they should be associated with.\n#\n# For example, The line \"a = text\" says that the\n# type is \"text\" if leader06 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for leader 06 \n# values not represented below, and unwanted mappings may be deleted\n#***************************************** \n  \na = Text\nc = Notated music\nd = Notated music\nh = Microform\ni = Sound recording\nj = Sound recording\nk = Nonprojected graphic\nm = Electronic resource\no = Kit\np = Multiple\nt = Text\n\n#-----------------------------------------\nLEADER 06 TO MARC VOCAB\n#-----------------------------------------\n\n#*****************************************\n# This section maps leader 06 values to the MARC \n# Vocabulary term they should be associated with.\n#\n# For example, The line \"a = Language material\" says that the MARC\n# Vocabulary term is \"Language material\" if leader06 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for leader 06 \n# values not represented below, and unwanted mappings may be deleted\n#***************************************** \n\na = Language material\nb = Archival and manuscripts control\nc = Notated music\nd = Manuscript notated music\ne = Cartographic material\nf = Manuscript cartographic material\ng = Projected medium\nh = Microform publications\ni = Nonmusical sound recording\nj = Musical sound recording\nk = Two-dimensional nonprojectable graphic\nm = Computer file\nn = Special instructional material\no = Kit\np = Mixed materials\nr = Three-dimensional artifact or naturally occurring object\nt = Manuscript language material\n\n#-----------------------------------------\nLEADER 07 TO MODE OF ISSUANCE\n#-----------------------------------------\n\n#*****************************************\n# This section maps leader 07 values to the mode of issuance\n# they should be associated with.\n#\n# For example, The line \"a = Monographic component part\" \n# says that the type is \"Monographic component part\" if \n# leader07 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for leader 07 \n# values not represented below, and unwanted mappings may be deleted\n# ***************************************** \n\na = Monographic component part\nb = Serial component part\nc = Collection\nd = Subunit\ni = Integrating resource\nm = Monograph/Item\ns = Serial\np = Pamphlet\n\n#-----------------------------------------\nLOCATION CODE TO LOCATION\n#-----------------------------------------\n\n#*****************************************\n# This section maps ILS location codes with the location\n# associated with them.  The location gives the institution the option to \n# provide a more complete description of an item’s location in the User Interface. \n#\n# For example, the line \"MainRef = Reference Desk, Main Library\" \n# says that the location is \"Reference Desk, Main Library\" \n# if the location code is \"MainRef\".\n#\n# A few sample codes and locations are included below.  These should be deleted \n# and replaced with the Institution’s actual location code mappings if this step is used.\n#\n# This step\'s default value is \"off\" because the XC Drupal Toolkit will also \n# provide this functionality.\n\n#*****************************************\n\nMainRef = Reference Desk, Main Library\nMainStk = Library Stacks\nOffStor = Offsite Storage; Inquire at Circulation Desk\n\n#-----------------------------------------\nLOCATION CODE TO LOCATION LIMIT NAME\n#-----------------------------------------\n\n#*****************************************\n# This section maps ILS location codes with the location limit name. \n# The location limit name is intended to be used as a facet value in a \n# facet for locations in a User Interface.\n#\n# For example, the line \"MainRef = Main Library\" \n# says that the location is \"Main Library\" if the location code is \"MainRef\"\n#\n# A few sample codes and locations are included below.  These should be deleted \n# and replaced with the Institution’s actual location code mappings if this step is used.\n#\n# This step\'s default value is \"off\" because the XC Drupal Toolkit will also \n# provide this functionality.\n# \n#*****************************************\n\nMainRef = Main Library\nMainStk = Main Library\nOffStor = Library Annex\n','xc.mst.services.normalization.NormalizationService','MARCNormalization',0,35000,75520,39993,NULL,'MST-instances\\MetadataServicesToolkit\\logs\\service\\MARCNormalization Service.txt',0,0,39993,0,NULL,'MST-instances\\MetadataServicesToolkit\\logs\\harvestOut\\MARCNormalization Service.txt','NOT_RUNNING','C:\\dev\\java\\tomcat_6.0\\MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceConfig\\DefaultNormalizationServiceConfig.xccfg','0.2.8',0),(2,'MARCToXCTransformation Service','MST-instances\\MetadataServicesToolkit\\services\\Transformation\\serviceJar\\TransformationService.jar','#-----------------------------------------\n#-----------------------------------------\n','xc.mst.services.transformation.TransformationService','MARCToXCTransformation',0,1027,33196,60629,NULL,'MST-instances\\MetadataServicesToolkit\\logs\\service\\MARCToXCTransformation Service.txt',0,0,60629,0,NULL,'MST-instances\\MetadataServicesToolkit\\logs\\harvestOut\\MARCToXCTransformation Service.txt','NOT_RUNNING','C:\\dev\\java\\tomcat_6.0\\MST-instances\\MetadataServicesToolkit\\services\\Transformation\\serviceConfig\\DefaultTransformationServiceConfig.xccfg','0.1.9',0),(99,'db norm service','MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceJar\\NormalizationService.jar','#-----------------------------------------\n\n#-----------------------------------------\nENABLED STEPS\n#-----------------------------------------\n\n#*****************************************\n# Whether or not to run each of the normalization setps\n# A step will run if its value is 1\n# values can only be either 1 or 0.\n#*****************************************\n\n# 1 to remove the 003 if its value is \"OCoLC\"\nRemoveOCoLC003 = 0\n\n# 1 to create a field with a DCMI Type based on \n# the original record\'s Leader 06 value\nDCMIType06 = 1\n\n# 1to create a field with the MARC vocabulary for\n# the original record\'s Leader 06 value\nLeader06Vocab = 1\n\n# 1 to create a field with a term based on \n# the original record\'s Leader 06 value\n007Vocab06 = 1\n\n# 1 to create a field with a mode of issuance based on \n# the original record\'s Leader 07 value \nModeOfIssuance = 1\n\n# 1 to create a new 035 field with the\n# organization code in the record and the\n# record\'s control number \nMoveMARCOrgCode = 1\n\n# 1 if the MoveMARCOrgCode step should run regardless of the \n# 003 value, false if it should only run if the 003 value\n# is the Organization Code defined in the main configuration\n# file.  This value is ignored if the MoveMARCOrgCode step\n# is not configured to run.\nMoveMARCOrgCode_moveAll = 0\n\n# 1 to create a field with a DCMI Type based on \n# the original record\'s 007 offset 00  value \nDCMIType007 = 1\n\n# 1 to create a field with a term based on \n# the original record\'s 007 offset 00 value \n007Vocab = 1\n\n# 1 to create a field with the SMD vocabulary term based on \n# the original record\'s 007 offset 00 and offset 01 values \n007SMDVocab = 1\n\n# 1 to create a field with either \"Fiction\" or \"Non-Fiction\"\n# depending on the other fields in the record \nFictionOrNonfiction = 1\n\n# 1 to create a field with the date range specified\n# in the 008 field if 008 offset 6 is \'r\' \n008DateRange = 1\n\n# 1 to create a 502 field ith the value \"Thesis.\"\n# if there is not already a 502 field and the\n# leader 06 is \'1\' and 008 24-27 contains an \'m\'\n008Thesis = 1\n\nISBNCleanup = 1\n\n# 1 to create a field with a language code for \n# each unique language code found in the original \n# record\'s 008 or 041 fields. \nLanguageSplit = 1\n\n# 1 to create a field with a language term for \n# each language code created by the LanguageSplit\n# normalization step. \nLanguageTerm = 1\n\n# 1 to create an audience field for the\n# audience suggested by the 008 offset 22 \n# value. \n008Audience = 1\n\n# 1 to create a new 035 field with the\n# organization code in the MST configuration file\n# and the original record\'s control number \nSupplyMARCOrgCode = 1\n\n# Organization code \nOrganizationCode = NRU\n\n# 1 to cleanup common problems with OCoLC 035 fields. \nFix035 = 1\n\n# 1 to cleanup OCoLC 035 fields for $9. By default 0\nFix035Code9 = 0\n\n# 1 to remove leading zeros . \n035LeadingZero = 1\n\n# 1 to remove duplicate 035 fields. \nDedup035 = 1\n\n# 1 to set 100, 110, and 111 $4 fields to\n# \"Author\" if they are not currently set and\n# leader 06 is \'a\' \nRoleAuthor = 1\n\n# 1 to set 100, 110, and 111 $4 fields to\n# \"Composer\" if they are not currently set and\n# leader 06 is \'c\' \nRoleComposer = 1\n\n# 1 to copy the 245 field into the 240 field\n# if there is neither a 130, 240, or 243 field. \nUniformTitle = 1\n\n# 1 to change 655 $2 to \"NRUgenre\" and delete the 655 $5 subfield\n# for each 655 field with $2 = \"local\" and $5 = \"NRU\"\nNRUGenre = 0\n\n# 1 to copy 600, 610, 611, 630, and 650 fields and 6xx $x subfields into\n# new fields containing information on the topic \nTopicSplit = 1\n\n# 1 to copy 648 fields and 6xx $y subfields into\n# new fields containing information on the chronological subject \nChronSplit = 1\n\n# 1 to copy 651 fields and 6xx $z subfields into\n# new fields containing information on the geographic subject \nGeogSplit = 1\n\n# 1 to copy 655 fields and 6xx $v subfields into\n# new fields containing information on the genre \nGenreSplit = 1\n\n# 1 to remove duplicate DCMI type fields. \nDedupDCMIType = 1\n\n# 1 to remove duplicate 007 Vocab fields. \nDedup007Vocab = 1\n\n# 1 to replace location codes left by \n# ILS with the actual location. \nBibLocationName = 0\n\n# 1 to replace location codes left by \n# III with the actual location. \nIIILocationName = 0\n\n# 1 to remove 945 field if it\'s  \n# $5 does not match organization code \nremove945Field = 1\n\n# 1 to copy 600, 610, 611, 700, 710, 711, 800, 810, and 811 fields\n# containing a $t subfield into a 9xx field.  This will facilitate \n# authority matching in other XC services.\nSeperateName = 1\n\n# 1 to remove duplicate 959, 963, 965, 967, and 969 fields. \nDedup9XX = 1\n\n# 1 to create a 246 field without an initial article . \nTitleArticle = 1\n\n# 1 to replace location codes left by \n# ILS with the actual location. By default 0 \nHoldingsLocationName = 0\n\n# 1 to replace location codes by \n# location limit name. By default 0\nLocationLimitName = 0\n\n#-----------------------------------------\nFIELD 007 OFFSET 00 TO DCMI TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps 007 offset 00 values to the DCMI Type\n# they should be associated with.\n#\n# For example, The line \"a = image\" says that the DCMI \n# Type is \"image\" if the 007 offset 00 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for 007 offset 00\n# values not represented below, and unwanted mappings may be deleted\n#***************************************** \n\na = Image\ng = StillImage\nk = StillImage\nm = MovingImage\nq = Image\nr = StillImage\ns = Sound\nt = Text\nv = MovingImage\n\n#-----------------------------------------\nFIELD 007 OFFSET 00 TO FULL TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps 007 offset 00 values to the \n# Type they should be associated with.\n#\n# For example, The line \"a = map\" says that the\n# Type is \"map\" if the 007 offset 00 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for 007 offset 00\n# values not represented below, and unwanted mappings may be deleted\n# ***************************************** \n\na = Map\nc = Electronic resource\nd = Globe\nf = Tactile material\ng = Projected graphic\nh = Microform\nk = Nonprojected graphic\nm = Motion picture\no = Kit\nq = Notated music\nr = Remote-sensing image\ns = Sound recording\nt = Text\nv = Video recording\nz = Multiple\n\n#-----------------------------------------\nFIELD 007 OFFSET 00 TO SMD TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps 007 offset 00 and 01 values to the SMD type term\n# they should be associated with.\n#\n# For example, The line \"ad = atlas\" says that the SMD\n# Type is \"atlas\" if the 007 offset 00 is \"a\" and 007 offset \n# 00 is \"d\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for 007 values \n# not represented below, and unwanted mappings may be deleted\n#***************************************** \n \nad = Atlas\nag = Diagram\naj = Map\nak = Profile\naq = Model\nar = Remote-sensing image\nas = Section\n\nca = Tape cartridge\ncb = Chip cartridge\ncc = Computer optical disc cartridge\ncf = Tape cassette\nch = Tape reel\ncj = Magnetic disk\ncm = Magneto-optical disk\nco = Optical disk\ncr = Remote\n\nda = Celestial globe\ndb = Planetary or lunar globe\ndc = Terrestrial globe\nde = Earth moon globe\n\nfa = Moon\nfb = Braille\nfc = Combination\n\ngc = Filmstrip cartridge\ngd = Filmslip\ngf = Other type of filmstrip\ngo = Filmstrip roll\ngs = Slide\ngt = Transparency\n\nha = Aperture card\nhb = Microfilm cartridge\nhc = Microfilm cassette\nhd = Microfilm reel\nhe = Microfiche\nhf = Microfiche cassette\nhg = Micropaque\n\nkc = College\nkd = Drawing\nke = Painting\nkf = Photomechanical print\nkg = Photonegative\nkh = Photoprint\nki = Picture\nkj = Print\nkl = Technical drawing\nkn = Chart\nko = Flash card\n\nmc = Film cartridge\nmf = Film cassette\nmr = Film reel\n\nou = Kit\n\nqu = Notated music\n\nru = Remote-sensing image\n\nsd = Sound disk\nse = Cylinder\nsg = Sound cartridge\nsi = Sound-track film\nsq = Roll\nss = Sound cassette\nst = Sound-tape reel\nsw = Wire recording\n\nta = Regular print\ntb = Large print\ntc = Braille\ntd = Loose-leaf\n\nvc = Videocartridge\nvd = Videodisc\nvf = Videocassette\nvr = Videoreel\n\nzm = Multiple\n\n#-----------------------------------------\nFIELD 008 OFFSET 22 TO AUDIENCE\n#-----------------------------------------\n\n#*****************************************\n# This section maps 008 offset 22 values to the audience\n# they should be associated with.\n#\n# For example, The line \"a = Preschool\" says that the\n# audience is \"Preschool\" if the 008 offset 22 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for 008 offset 022\n# values not represented below, and unwanted mappings may be deleted\n#***************************************** \n\na = Preschool\nb = Primary\nc = Pre-adolescent\nd = Adolescent\ne = Adult\nf = Specialized\ng = General\nj = Juvenile\n\n#-----------------------------------------\nLANGUAGE CODE TO LANGUAGE\n#-----------------------------------------\n\n#*****************************************\n# This section maps language codes to the languages\n# they should be associated with.\n#\n# For example, The line \"aar = Afar\" says that the\n# language is \"Afar\" if the language code is \"aar\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for language codes\n# not represented below, and unwanted mappings may be deleted\n#***************************************** \n\naar = Afar\nabk = Abkhaz\nace = Achinese\nach = Acoli\nada = Adangme\nady = Adygei\nafa = Afroasiatic (Other)\nafh = Afrihili (Artificial language)\nafr = Afrikaans\nain = Ainu\najm = Aljamía\naka = Akan\nakk = Akkadian\nalb = Albanian\nale = Aleut\nalg = Algonquian (Other)\nalt = Altai\namh = Amharic\nang = English, Old (ca. 450-1100)\nanp = Angika\napa = Apache languages\nara = Arabic\narc = Aramaic\narg = Aragonese Spanish\narm = Armenian\narn = Mapuche\narp = Arapaho\nart = Artificial (Other)\narw = Arawak\nasm = Assamese\nast = Bable\nath = Athapascan (Other)\naus = Australian languages\nava = Avaric\nave = Avestan\nawa = Awadhi\naym = Aymara\naze = Azerbaijani\nbad = Banda languages\nbai = Bamileke languages\nbak = Bashkir\nbal = Baluchi\nbam = Bambara\nban = Balinese\nbaq = Basque\nbas = Basa\nbat = Baltic (Other)\nbej = Beja\nbel = Belarusian\nbem = Bemba\nben = Bengali\nber = Berber (Other)\nbho = Bhojpuri\nbih = Bihari\nbik = Bikol\nbin = Edo\nbis = Bislama\nbla = Siksika\nbnt = Bantu (Other)\nbos = Bosnian\nbra = Braj\nbre = Breton\nbtk = Batak\nbua = Buriat\nbug = Bugis\nbul = Bulgarian\nbur = Burmese\nbyn = Bilin\ncad = Caddo\ncai = Central American Indian (Other)\ncam = Khmer\ncar = Carib\ncat = Catalan\ncau = Caucasian (Other)\nceb = Cebuano\ncel = Celtic (Other)\ncha = Chamorro\nchb = Chibcha\nche = Chechen\nchg = Chagatai\nchi = Chinese\nchk = Chuukese\nchm = Mari\nchn = Chinook jargon\ncho = Choctaw\nchp = Chipewyan\nchr = Cherokee\nchu = Church Slavic\nchv = Chuvash\nchy = Cheyenne\ncmc = Chamic languages\ncop = Coptic\ncor = Cornish\ncos = Corsican\ncpe = Creoles and Pidgins, English-based (Other)\ncpf = Creoles and Pidgins, French-based (Other)\ncpp = Creoles and Pidgins, Portuguese-based (Other)\ncre = Cree\ncrh = Crimean Tatar\ncrp = Creoles and Pidgins (Other)\ncsb = Kashubian\ncus = Cushitic (Other)\ncze = Czech\ndak = Dakota\ndan = Danish\ndar = Dargwa\nday = Dayak\ndel = Delaware\nden = Slave\ndgr = Dogrib\ndin = Dinka\ndiv = Divehi\ndoi = Dogri\ndra = Dravidian (Other)\ndsb = Lower Sorbian\ndua = Duala\ndum = Dutch, Middle (ca. 1050-1350)\ndut = Dutch\ndyu = Dyula\ndzo = Dzongkha\nefi = Efik\negy = Egyptian\neka = Ekajuk\nelx = Elamite\neng = English\nenm = English, Middle (1100-1500)\nepo = Esperanto\nesk = Eskimo languages\nesp = Esperanto\nest = Estonian\neth = Ethiopic\newe = Ewe\newo = Ewondo\nfan = Fang\nfao = Faroese\nfar = Faroese\nfat = Fanti\nfij = Fijian\nfil = Filipino\nfin = Finnish\nfiu = Finno-Ugrian (Other)\nfon = Fon\nfre = French\nfri = Frisian\nfrm = French, Middle (ca. 1300-1600)\nfro = French, Old (ca. 842-1300)\nfrr = North Frisian\nfrs = East Frisian\nfry = Frisian\nful = Fula\nfur = Friulian\ngaa = Gã\ngae = Scottish Gaelix\ngag = Galician\ngal = Oromo\ngay = Gayo\ngba = Gbaya\ngem = Germanic (Other)\ngeo = Georgian\nger = German\ngez = Ethiopic\ngil = Gilbertese\ngla = Scottish Gaelic\ngle = Irish\nglg = Galician\nglv = Manx\ngmh = German, Middle High (ca. 1050-1500)\ngoh = German, Old High (ca. 750-1050)\ngon = Gondi\ngor = Gorontalo\ngot = Gothic\ngrb = Grebo\ngrc = Greek, Ancient (to 1453)\ngre = Greek, Modern (1453- )\ngrn = Guarani\ngsw = Swiss German\ngua = Guarani\nguj = Gujarati\ngwi = Gwich\'in\nhai = Haida\nhat = Haitian French Creole\nhau = Hausa\nhaw = Hawaiian\nheb = Hebrew\nher = Herero\nhil = Hiligaynon\nhim = Himachali\nhin = Hindi\nhit = Hittite\nhmn = Hmong\nhmo = Hiri Motu\nhsb = Upper Sorbian\nhun = Hungarian\nhup = Hupa\niba = Iban\nibo = Igbo\nice = Icelandic\nido = Ido\niii = Sichuan Yi\nijo = Ijo\niku = Inuktitut\nile = Interlingue\nilo = Iloko\nina = Interlingua (International Auxiliary Language Association)\ninc = Indic (Other)\nind = Indonesian\nine = Indo-European (Other)\ninh = Ingush\nint = Interlingua (International Auxiliary Language Association)\nipk = Inupiaq\nira = Iranian (Other)\niri = Irish\niro = Iroquoian (Other)\nita = Italian\njav = Javanese\njbo = Lojban (Artificial language)\njpn = Japanese\njpr = Judeo-Persian\njrb = Judeo-Arabic\nkaa = Kara-Kalpak\nkab = Kabyle\nkac = Kachin\nkal = Kalâtdlisut\nkam = Kamba\nkan = Kannada\nkar = Karen languages\nkas = Kashmiri\nkau = Kanuri\nkaw = Kawi\nkaz = Kazakh\nkbd = Kabardian\nkha = Khasi\nkhi = Khoisan (Other)\nkhm = Khmer\nkho = Khotanese\nkik = Kikuyu\nkin = Kinyarwanda\nkir = Kyrgyz\nkmb = Kimbundu\nkok = Konkani\nkom = Komi\nkon = Kongo\nkor = Korean\nkos = Kusaie\nkpe = Kpelle\nkrc = Karachay-Balkar\nkrl = Karelian\nkro = Kru (Other)\nkru = Kurukh\nkua = Kuanyama\nkum = Kumyk\nkur = Kurdish\nkus = Kusaie\nkut = Kootenai\nlad = Ladino\nlah = Lahnda\nlam = Lamba (Zambia and Congo)\nlan = Occitan (post 1500)\nlao = Lao\nlap = Sami\nlat = Latin\nlav = Latvian\nlez = Lezgian\nlim = Limburgish\nlin = Lingala\nlit = Lithuanian\nlol = Mongo-Nkundu\nloz = Lozi\nltz = Luxembourgish\nlua = Luba-Lulua\nlub = Luba-Katanga\nlug = Ganda\nlui = Luiseño\nlun = Lunda\nluo = Luo (Kenya and Tanzania)\nlus = Lushai\nmac = Macedonian\nmad = Madurese\nmag = Magahi\nmah = Marshallese\nmai = Maithili\nmak = Makasar\nmal = Malayalam\nman = Mandingo\nmao = Maori\nmap = Austronesian (Other)\nmar = Marathi\nmas = Masai\nmax = Manx\nmay = Malay\nmdf = Moksha\nmdr = Mandar\nmen = Mende\nmga = Irish, Middle (ca. 1100-1550)\nmic = Micmac\nmin = Minangkabau\nmis = Miscellaneous languages\nmkh = Mon-Khmer (Other)\nmla = Malagasy\nmlg = Malagasy\nmlt = Maltese\nmnc = Manchu\nmni = Manipuri\nmno = Manobo languages\nmoh = Mohawk\nmol = Moldavian\nmon = Mongolian\nmos = Mooré\nmun = Munda (Other)\nmus = Creek\nmwl = Mirandese\nmwr = Marwari\nmyn = Mayan languages\nmyv = Erzya\nnah = Nahuatl\nnai = North American Indian (Other)\nnap = Neapolitan Italian\nnau = Nauru\nnav = Navajo\nnbl = Ndebele (South Africa)\nnde = Ndebele (Zimbabwe)\nndo = Ndonga\nnds = Low German\nnep = Nepali\nnew = Newari\nnia = Nias\nnic = Niger-Kordofanian (Other)\nniu = Niuean\nnno = Norwegian (Nynorsk)\nnob = Norwegian (Bokmål)\nnog = Nogai\nnon = Old Norse\nnor = Norwegian\nnqo = N\'Ko\nnso = Northern Sotho\nnub = Nubian languages\nnwc = Newari, Old\nnya = Nyanja\nnym = Nyamwezi\nnyn = Nyankole\nnyo = Nyoro\nnzi = Nzima\noci = Occitan (post 1500)\noji = Ojibwa\nori = Oriya\norm = Oromo\nosa = Osage\noss = Ossetic\nota = Turkish, Ottoman\noto = Otomian languages\npaa = Papuan (Other)\npag = Pangasinan\npal = Pahlavi\npam = Pampanga\npan = Panjabi\npap = Papiamento\npau = Palauan\npeo = Old Persian (ca. 600-400 B.C.)\nper = Persian\nphi = Philippine (Other)\nphn = Phoenician\npli = Pali\npol = Polish\npon = Ponape\npor = Portuguese\npra = Prakrit languages\npro = Provençal (to 1500)\npus = Pushto\nque = Quechua\nraj = Rajasthani\nrap = Rapanui\nrar = Rarotongan\nroa = Romance (Other)\nroh = Raeto-Romance\nrom = Romani\nrum = Romanian\nrun = Rundi\nrup = Aromanian\nrus = Russian\nsad = Sandawe\nsag = Sango (Ubangi Creole)\nsah = Yakut\nsai = South American Indian (Other)\nsal = Salishan languages\nsam = Samaritan Aramaic\nsan = Sanskrit\nsao = Samoan\nsas = Sasak\nsat = Santali\nscc = Serbian\nscn = Sicilian Italian\nsco = Scots\nscr = Croatian\nsel = Selkup\nsem = Semitic (Other)\nsga = Irish, Old (to 1100)\nsgn = Sign languages\nshn = Shan\nsho = Shona\nsid = Sidamo\nsin = Sinhalese\nsio = Siouan (Other)\nsit = Sino-Tibetan (Other)\nsla = Slavic (Other)\nslo = Slovak\nslv = Slovenian\nsma = Southern Sami\nsme = Northern Sami\nsmi = Sami\nsmj = Lule Sami\nsmn = Inari Sami\nsmo = Samoan\nsms = Skolt Sami\nsna = Shona\nsnd = Sindhi\nsnh = Sinhalese\nsnk = Soninke\nsog = Sogdian\nsom = Somali\nson = Songhai\nsot = Sotho\nspa = Spanish\nsrd = Sardinian\nsrn = Sranan\nsrr = Serer\nssa = Nilo-Saharan (Other)\nsso = Sotho\nssw = Swazi\nsuk = Sukuma\nsun = Sundanese\nsus = Susu\nsux = Sumerian\nswa = Swahili\nswe = Swedish\nswz = Swazi\nsyc = Syriac\nsyr = Syriac, Modern\ntag = Tagalog\ntah = Tahitian\ntai = Tai (Other)\ntaj = Tajik\ntam = Tamil\ntar = Tatar\ntat = Tatar\ntel = Telugu\ntem = Temne\nter = Terena\ntet = Tetum\ntgk = Tajik\ntgl = Tagalog\ntha = Thai\ntib = Tibetan\ntig = Tigré\ntir = Tigrinya\ntiv = Tiv\ntkl = Tokelauan\ntlh = Klingon (Artificial language)\ntli = Tlingit\ntmh = Tamashek\ntog = Tonga (Nyasa)\nton = Tongan\ntpi = Tok Pisin\ntru = Truk\ntsi = Tsimshian\ntsn = Tswana\ntso = Tsonga\ntsw = Tswana\ntuk = Turkmen\ntum = Tumbuka\ntup = Tupi languages\ntur = Turkish\ntut = Altaic (Other)\ntvl = Tuvaluan\ntwi = Twi\ntyv = Tuvinian\nudm = Udmurt\nuga = Ugaritic\nuig = Uighur\nukr = Ukrainian\numb = Umbundu\nurd = Urdu\nuzb = Uzbek\nvai = Vai\nven = Venda\nvie = Vietnamese\nvol = Volapük\nvot = Votic\nwak = Wakashan languages\nwal = Wolayta\nwar = Waray\nwas = Washo\nwel = Welsh\nwen = Sorbian (Other)\nwln = Walloon\nwol = Wolof\nxal = Oirat\nxho = Xhosa\nyao = Yao (Africa)\nyap = Yapese\nyid = Yiddish\nyor = Yoruba\nypk = Yupik languages\nzap = Zapotec\nzbl = Blissymbolics\nzen = Zenaga\nzha = Zhuang\nznd = Zande languages\nzul = Zulu\nzun = Zuni\nzxx = No linguistic content\nzza = Zaza\n\n#-----------------------------------------\nLEADER 06 TO DCMI TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps leader 06 values to the DCMI Type\n# they should be associated with.\n#\n# For example, The line \"a = text\" says that the DCMI \n# Type is \"text if leader06 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for leader 06 \n# values not represented below, and unwanted mappings may be deleted\n#*****************************************\n\na = Text\nc = Image\nd = Image\ng = Image\ni = Sound\nj = Sound\nk = StillImage\nr = PhysicalObject\nt = Text\n\n#-----------------------------------------\nLEADER 06 TO FULL TYPE\n#-----------------------------------------\n\n#*****************************************\n# This section maps leader 06 values to the \n# type they should be associated with.\n#\n# For example, The line \"a = text\" says that the\n# type is \"text\" if leader06 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for leader 06 \n# values not represented below, and unwanted mappings may be deleted\n#***************************************** \n  \na = Text\nc = Notated music\nd = Notated music\nh = Microform\ni = Sound recording\nj = Sound recording\nk = Nonprojected graphic\nm = Electronic resource\no = Kit\np = Multiple\nt = Text\n\n#-----------------------------------------\nLEADER 06 TO MARC VOCAB\n#-----------------------------------------\n\n#*****************************************\n# This section maps leader 06 values to the MARC \n# Vocabulary term they should be associated with.\n#\n# For example, The line \"a = Language material\" says that the MARC\n# Vocabulary term is \"Language material\" if leader06 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for leader 06 \n# values not represented below, and unwanted mappings may be deleted\n#***************************************** \n\na = Language material\nb = Archival and manuscripts control\nc = Notated music\nd = Manuscript notated music\ne = Cartographic material\nf = Manuscript cartographic material\ng = Projected medium\nh = Microform publications\ni = Nonmusical sound recording\nj = Musical sound recording\nk = Two-dimensional nonprojectable graphic\nm = Computer file\nn = Special instructional material\no = Kit\np = Mixed materials\nr = Three-dimensional artifact or naturally occurring object\nt = Manuscript language material\n\n#-----------------------------------------\nLEADER 07 TO MODE OF ISSUANCE\n#-----------------------------------------\n\n#*****************************************\n# This section maps leader 07 values to the mode of issuance\n# they should be associated with.\n#\n# For example, The line \"a = Monographic component part\" \n# says that the type is \"Monographic component part\" if \n# leader07 is \"a\"\n#\n# Any of the mappings may be changed based on your organization\'s\n# cataloging policies.  New properties may be added for leader 07 \n# values not represented below, and unwanted mappings may be deleted\n# ***************************************** \n\na = Monographic component part\nb = Serial component part\nc = Collection\nd = Subunit\ni = Integrating resource\nm = Monograph/Item\ns = Serial\np = Pamphlet\n\n#-----------------------------------------\nLOCATION CODE TO LOCATION\n#-----------------------------------------\n\n#*****************************************\n# This section maps ILS location codes with the location\n# associated with them.  The location gives the institution the option to \n# provide a more complete description of an item’s location in the User Interface. \n#\n# For example, the line \"MainRef = Reference Desk, Main Library\" \n# says that the location is \"Reference Desk, Main Library\" \n# if the location code is \"MainRef\".\n#\n# A few sample codes and locations are included below.  These should be deleted \n# and replaced with the Institution’s actual location code mappings if this step is used.\n#\n# This step\'s default value is \"off\" because the XC Drupal Toolkit will also \n# provide this functionality.\n\n#*****************************************\n\nMainRef = Reference Desk, Main Library\nMainStk = Library Stacks\nOffStor = Offsite Storage; Inquire at Circulation Desk\n\n#-----------------------------------------\nLOCATION CODE TO LOCATION LIMIT NAME\n#-----------------------------------------\n\n#*****************************************\n# This section maps ILS location codes with the location limit name. \n# The location limit name is intended to be used as a facet value in a \n# facet for locations in a User Interface.\n#\n# For example, the line \"MainRef = Main Library\" \n# says that the location is \"Main Library\" if the location code is \"MainRef\"\n#\n# A few sample codes and locations are included below.  These should be deleted \n# and replaced with the Institution’s actual location code mappings if this step is used.\n#\n# This step\'s default value is \"off\" because the XC Drupal Toolkit will also \n# provide this functionality.\n# \n#*****************************************\n\nMainRef = Main Library\nMainStk = Main Library\nOffStor = Library Annex\n','xc.mst.services.normalization.DBNormalizationService','DBMARCNormalization',0,35000,75520,39993,NULL,'MST-instances\\MetadataServicesToolkit\\logs\\service\\MARCNormalization Service.txt',0,0,39993,0,NULL,'MST-instances\\MetadataServicesToolkit\\logs\\harvestOut\\MARCNormalization Service.txt','NOT_RUNNING','C:\\dev\\java\\tomcat_6.0\\MST-instances\\MetadataServicesToolkit\\services\\Normalization\\serviceConfig\\DefaultNormalizationServiceConfig.xccfg','0.2.8',0);
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
) ENGINE=InnoDB AUTO_INCREMENT=535 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services_to_input_formats`
--

LOCK TABLES `services_to_input_formats` WRITE;
/*!40000 ALTER TABLE `services_to_input_formats` DISABLE KEYS */;
INSERT INTO `services_to_input_formats` VALUES (485,1,3),(486,1,6),(487,2,3),(488,2,6),(533,99,3),(534,99,6);
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
INSERT INTO `sets` VALUES (1,'MARCXML Bibliographic Records','A set of all MARCXML Bibliographic records in the repository.','MARCXMLbibliographic',0,1,0),(2,'MARCXML Holdings Records','A set of all MARCXML holdings records in the repository.','MARCXMLholdings',0,1,0),(3,'MARCXML Authority Records','A set of all MARCXML Authority records in the repository.','MARCXMLauthority',0,1,0),(4,'Bibliographic records',NULL,'bib',1,0,3),(5,'Authority records',NULL,'auth',1,0,3),(6,'Holdings record',NULL,'hold',1,0,3),(7,'Classification records',NULL,'class',1,0,3),(8,'Community Information records',NULL,'comm',1,0,3),(9,'norm_output_set_name',NULL,'norm_output_set_spec',0,0,0),(10,'137',NULL,'137',0,1,1),(11,'137:bib',NULL,'137:bib',0,1,1),(12,'137:hold',NULL,'137:hold',0,1,1),(13,'MARCXML Holding Records','A set of all MARCXML Holding records in the repository.','MARCXMLholding',0,1,0),(14,'trans_output_set_name',NULL,'trans_output_set_spec',0,0,0),(15,'132---40k',NULL,'132---40k',0,1,2),(16,'132---40k:bib',NULL,'132---40k:bib',0,1,2),(17,'132---40k:hold',NULL,'132---40k:hold',0,1,2),(18,'132_40k_norm_output_set_name',NULL,'132_40k_norm_output_set_spec',0,0,0),(19,'137---175',NULL,'137---175',0,1,1),(20,'137---175:bib',NULL,'137---175:bib',0,1,1),(21,'137---175:hold',NULL,'137---175:hold',0,1,1),(22,'db_norm_service_set',NULL,'db_norm_service_spec',0,0,0);
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
