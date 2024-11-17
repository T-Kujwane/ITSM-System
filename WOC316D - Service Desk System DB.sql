DROP DATABASE IF EXISTS service_desk_system;
CREATE DATABASE  IF NOT EXISTS `service_desk_system` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `service_desk_system`;
-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: service_desk_system
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `priority`
--

DROP TABLE IF EXISTS `priority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `priority` (
  `priority_id` int NOT NULL AUTO_INCREMENT,
  `priority_name` varchar(50) NOT NULL,
  `sla_time` int NOT NULL COMMENT 'SLA in hours',
  PRIMARY KEY (`priority_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `priority`
--

LOCK TABLES `priority` WRITE;
/*!40000 ALTER TABLE `priority` DISABLE KEYS */;
INSERT INTO `priority` VALUES (1,'Low',48),(2,'Medium',24),(3,'High',8),(4,'Critical',2);
/*!40000 ALTER TABLE `priority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'end_user'),(2,'agent'),(3,'technician'),(4,'manager');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_priority`
--

DROP TABLE IF EXISTS `ticket_priority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_priority` (
  `entry_id` int NOT NULL AUTO_INCREMENT,
  `ticket_id` int NOT NULL,
  `priority_id` int NOT NULL,
  PRIMARY KEY (`entry_id`),
  KEY `ticket_id` (`ticket_id`),
  KEY `priority_id` (`priority_id`),
  CONSTRAINT `ticket_priority_ibfk_1` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  CONSTRAINT `ticket_priority_ibfk_2` FOREIGN KEY (`priority_id`) REFERENCES `priority` (`priority_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_priority`
--

LOCK TABLES `ticket_priority` WRITE;
/*!40000 ALTER TABLE `ticket_priority` DISABLE KEYS */;
INSERT INTO `ticket_priority` VALUES (1,1,3);
/*!40000 ALTER TABLE `ticket_priority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_updates`
--

DROP TABLE IF EXISTS `ticket_updates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_updates` (
  `update_id` int NOT NULL AUTO_INCREMENT,
  `ticket_id` int NOT NULL,
  `updated_by` int NOT NULL,
  `comment` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`update_id`),
  KEY `ticket_id` (`ticket_id`),
  KEY `updated_by` (`updated_by`),
  CONSTRAINT `ticket_updates_ibfk_1` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  CONSTRAINT `ticket_updates_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_updates`
--

LOCK TABLES `ticket_updates` WRITE;
/*!40000 ALTER TABLE `ticket_updates` DISABLE KEYS */;
/*!40000 ALTER TABLE `ticket_updates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `ticket_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `attachment` varchar(255) DEFAULT NULL,
  `status` enum('open','in_progress','escalated','resolved','closed') DEFAULT 'open',
  `created_by` int NOT NULL,
  `assigned_to` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ticket_id`),
  KEY `created_by` (`created_by`),
  KEY `assigned_to` (`assigned_to`),
  CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`assigned_to`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
INSERT INTO `tickets` VALUES (1,'Testing 0000000001','Testing 0000000001',NULL,'closed',1,NULL,'2024-11-17 21:46:23','2024-11-17 23:04:59');
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'manager','482c811da5d5b4bc6d497ffa98491e38','Manager','Developer.TK_Kujwane@outlook.com',4,'2024-11-17 21:46:06'),(2,'TDK','8d98f673df6c0828827a2a51bdbe5e1c','Thandeka Matampane','ThandekaBrad@gmail.com',3,'2024-11-17 21:46:06'),(3,'agent1','83a87fd756ab57199c0bb6d5e11168cb','Service Desk Agent 1','221078640@tut4life.ac.za',2,'2024-11-17 21:46:06'),(4,'agent2','b1a4a6b01cc297d4677c4ca6656e14d7','Service Desk Agent 2','KujwaneTK@outlook.com',2,'2024-11-17 21:46:06'),(5,'manager2','1fb9c14e934b825a62d15230cc0c2bd1','Manager 2','Student_KujwaneTK@outlook.com',4,'2024-11-17 21:46:06'),(6,'TK','c0d7b93e45580ee7f46d6c4ccf66a34c','Thato Keith Kujwane','KujwaneTK@outlook.com',3,'2024-11-17 21:46:06');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'service_desk_system'
--

--
-- Dumping routines for database 'service_desk_system'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-18  1:14:31
