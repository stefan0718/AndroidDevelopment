-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: bnenjoy
-- ------------------------------------------------------
-- Server version	5.7.17

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
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comments` (
  `place_id` varchar(100) NOT NULL,
  `comment` longtext NOT NULL,
  PRIMARY KEY (`place_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES ('4b05873bf964a520708622e3','admin: good\n\nadmin: delicious\n\n');
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_record`
--

DROP TABLE IF EXISTS `login_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `login_record` (
  `login_email` varchar(45) NOT NULL,
  `login_password` varchar(15) NOT NULL,
  `login_datetime` datetime NOT NULL,
  PRIMARY KEY (`login_datetime`),
  KEY `login_email_to_email_idx` (`login_email`),
  CONSTRAINT `login_email_to_email` FOREIGN KEY (`login_email`) REFERENCES `users` (`email`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_record`
--

LOCK TABLES `login_record` WRITE;
/*!40000 ALTER TABLE `login_record` DISABLE KEYS */;
INSERT INTO `login_record` VALUES ('stefan@gmail.com','stefan','2017-02-12 18:00:00'),('admin@admin.com','admin','2017-02-12 19:31:50'),('test@test.com','test','2017-02-12 19:46:50'),('stefan@gmail.com','stefan','2017-02-12 19:51:33'),('test@test.com','test','2017-02-12 19:52:46'),('test@test.com','test','2017-02-12 19:53:00'),('admin@admin.com','admin','2017-02-12 21:18:35'),('admin@admin.com','admin','2017-02-12 21:20:04'),('test@test.com','test','2017-02-12 21:29:27'),('stefan@gmail.com','stefan','2017-02-12 21:38:24'),('stefan@gmail.com','stefan','2017-02-12 21:50:46'),('admin@admin.com','admin','2017-02-12 21:55:03'),('stefan@gmail.com','stefan','2017-02-13 00:11:15'),('stefan@gmail.com','stefan','2017-02-13 00:36:12'),('stefan@gmail.com','stefan','2017-02-13 12:12:58'),('admin@admin.com','admin','2017-02-13 12:32:01'),('admin@admin.com','admin','2017-02-13 12:32:24'),('admin@admin.com','admin','2017-02-13 17:41:22'),('stefan@gmail.com','stefan','2017-02-13 18:25:34'),('admin@admin.com','admin','2017-02-13 18:39:17'),('test1@test.com','test','2017-02-13 18:54:30'),('festitch@hotmail.com','qqqq','2017-02-13 19:01:53'),('admin1@admin.com','admin','2017-02-13 19:37:30'),('admin@admin.com','admin','2017-02-20 20:40:36'),('admin@admin.com','admin','2017-02-20 20:53:47'),('stefan@gmail.com','stefan','2017-03-10 10:48:02'),('admin@admin.com','admin','2017-04-22 22:36:37'),('admin@admin.com','admin','2017-04-24 11:02:26'),('admin@admin.com','admin','2017-04-24 11:05:04'),('admin@admin.com','admin','2017-05-04 11:29:11'),('admin@admin.com','admin','2017-05-04 11:35:54'),('admin@admin.com','admin','2017-05-04 12:02:33'),('admin@admin.com','admin','2017-05-04 12:03:30'),('bart@gmail.com','bart','2017-05-04 12:22:40'),('admin@admin.com','admin','2017-05-05 10:50:48'),('admin@admin.com','admin','2017-05-05 15:11:46'),('admin@admin.com','admin','2017-05-05 15:14:02'),('admin@admin.com','admin','2017-05-11 14:04:15'),('stefan@gmail.com','stefan','2017-05-25 19:40:01'),('admin@admin.com','admin','2017-05-26 10:33:23'),('admin@admin.com','admin','2017-05-26 12:23:27');
/*!40000 ALTER TABLE `login_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `name` varchar(15) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(15) NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL,
  `signup_datetime` datetime DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`email`),
  KEY `email_index` (`email`),
  KEY `password_index` (`password`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='user account details';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('admin1','admin1@admin.com','admin',NULL,NULL,NULL,'2017-02-13 19:37:30',NULL),('admin','admin@admin.com','admin','Male','1990-01-01','64 Adelaide St, Brisbane City QLD 4000','2017-01-24 00:00:00',NULL),('Bart Simpson','bart@gmail.com','bart',NULL,NULL,NULL,'2017-05-04 12:22:40',NULL),('stitch','festitch@hotmail.com','qqqq',NULL,NULL,NULL,'2017-02-13 19:01:53',NULL),('stefan','stefan@gmail.com','stefan','Male','1993-01-31','47 Rennie St, Indooroopilly QLD 4068','2017-01-24 00:00:00',NULL),('test1','test1@test.com','test',NULL,NULL,NULL,'2017-02-13 18:54:30',NULL),('test','test@test.com','test','Female','1990-01-01','Building 49, The University of Queensland, Staff House Rd, St Lucia QLD 4072','2017-01-24 00:00:00',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-06-11 14:43:37
