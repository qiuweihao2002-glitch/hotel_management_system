-- MySQL dump 10.13  Distrib 8.2.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: jiudianmanage
-- ------------------------------------------------------
-- Server version	8.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config` (
  `id` int NOT NULL,
  `managesalary` double(10,2) DEFAULT NULL,
  `staffsalary` double(10,2) DEFAULT NULL,
  `cleanerssalary` double(10,2) DEFAULT NULL,
  `manage` double(10,2) DEFAULT NULL,
  `staff` double(10,2) DEFAULT NULL,
  `cleaner` double(10,2) DEFAULT NULL,
  `totalmoney` double(10,2) DEFAULT NULL,
  `totalroom` double(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,19.00,15.00,10.00,8000.00,4500.00,4000.00,32774.00,8.00);
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `orderid` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `householdname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `ID` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `starttime` datetime DEFAULT NULL,
  `endtime` datetime DEFAULT NULL,
  `money` double DEFAULT NULL,
  `state` int DEFAULT NULL,
  `roomid` int DEFAULT NULL,
  `userid` int DEFAULT NULL,
  PRIMARY KEY (`orderid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,'张三','340123199001011234','2024-05-20 14:00:00','2024-05-22 12:00:00',316,2,202,3),(2,'李四','310109198505056789','2025-05-25 14:00:00','2025-05-28 12:00:00',474,1,103,4),(3,'赵六','340123199001011234','2024-11-21 14:00:00','2024-11-23 12:00:00',316,2,101,5),(4,'孙七','310109198505056789','2024-11-25 14:00:00','2024-11-28 12:00:00',714,1,201,6),(5,'李四','310109198505056789','2025-05-24 00:00:00','2025-05-27 00:00:00',474,2,103,3),(6,'王五','320123199506067890','2025-12-01 00:00:00','2025-12-03 00:00:00',316,1,102,3),(7,'裘伟豪','360102200209305355','2025-07-17 00:00:00','2025-08-10 00:00:00',10992,0,302,3),(8,'先王','11223344564987964646465','2025-11-20 00:00:00','2025-11-29 00:00:00',1242,0,1,6);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `roomid` int NOT NULL AUTO_INCREMENT,
  `local` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `money` double DEFAULT NULL,
  `state` int DEFAULT NULL,
  `type` int DEFAULT NULL,
  PRIMARY KEY (`roomid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,'1楼-101室',138,2,1),(2,'1楼-102室',138,2,1),(3,'1楼-104室',500,1,4),(4,'2楼-201室',238,2,2),(5,'2楼-202室',238,1,2),(6,'2楼-203室',238,0,2),(7,'2楼-204室',238,1,2),(8,'3楼-301室',458,0,3),(9,'3楼-302室',458,2,3),(10,'1楼-103室',500,3,4),(11,'4楼-401室',500,3,4),(12,'4楼-402室',138,3,1);
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `userid` int NOT NULL AUTO_INCREMENT COMMENT '用户ID（自增主键）',
  `useraccount` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `age` int DEFAULT NULL,
  `power` int DEFAULT NULL,
  `IDnumber` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `money` double DEFAULT NULL,
  `photoUrl` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `phonenumber` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`userid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','系统管理员',NULL,0,'ADMIN001',0,'File/ac44be52-6ecf-4d09-b811-2a341d84db41.jpg',NULL),(2,'manager1','123456','王经理',35,1,'MANAGER001',0,'File/b4499e0a-fc72-426e-badb-08a917833dda.jpg','13800138001'),(4,'cleaner1','123456','李四',45,3,'CLEANER001',0,NULL,'13700137001'),(5,'cleaner3','123456','赵六',30,3,'CLEANER002',0,'File/92767ec6-b3eb-4f70-8025-c0d9356c3c19.jpg','13500135001'),(6,'staff2','123456','孙七',28,2,'STAFF002',0,'File/148c4f0a-5f1e-4d56-b091-a29799d8ec2c.jpg','13600136001'),(7,'cleaner2','123456','王五',29,3,'CLEANER003',0,NULL,'13600136008'),(9,'cleaner5','123456','小张',36,3,'MPfufIby',0,NULL,'12365464648'),(10,'cleaner6','123456','小刘',33,2,'rHPoT68b',0,NULL,'12345647979'),(11,'cleaner7','123456','14',26,3,'rHP8bsdg',0,NULL,'12321654676'),(14,'staff4','123456','小吴',25,2,'TKBA1dEQ',4900,NULL,'14765464123'),(15,'staff6','123456','萨拉',29,2,'bMUvY6Hx',5100,NULL,'13156498797'),(16,'staff3','123456','小员工',19,2,'ZLuZNM37',0,NULL,'19179189328'),(17,'staff5','123456','小球',18,2,'z98CykOC',0,NULL,'11225465465'),(18,'staff7','123456','小小修',18,2,'z98CykOC',0,NULL,'12346546793'),(19,'xianiuma','123456',NULL,25,3,'votMhAxO',0,NULL,NULL),(20,'xianiuma2','123456',NULL,NULL,1,'EP1iDYC5',NULL,NULL,NULL),(21,'LIDANYANG','12346',NULL,NULL,2,'Xmxpykgi',NULL,NULL,NULL),(22,'LIDANYANG2','123456',NULL,NULL,2,'dTMN647L',NULL,NULL,NULL),(23,'xianiuma3','123456',NULL,NULL,2,'ztD8jI5r',NULL,NULL,NULL),(24,'xianiuma5','123456',NULL,NULL,1,'FqAne6LT',NULL,NULL,NULL),(25,'xianiuma6','123456',NULL,NULL,1,'Q1ChWDep',NULL,NULL,NULL),(26,'xiaoniuma10','123456',NULL,NULL,1,'UHZ1zQhj',NULL,NULL,NULL),(27,'LIDANYANG3','123456',NULL,NULL,1,'yC5rmlur',NULL,NULL,NULL),(28,'xianiuma12','123456',NULL,NULL,1,'08Vj0Md4',NULL,NULL,NULL),(30,'xiaoniuma14','123456',NULL,NULL,2,'VpFsPKLR',NULL,NULL,NULL),(32,'Niuma3','123456',NULL,NULL,2,'570xMsNx',NULL,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_backup`
--

DROP TABLE IF EXISTS `user_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_backup` (
  `userid` int NOT NULL,
  `useraccount` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `age` int DEFAULT NULL,
  `power` int DEFAULT NULL,
  `IDnumber` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `money` double DEFAULT NULL,
  `photoUrl` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `phonenumber` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`userid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_backup`
--

LOCK TABLES `user_backup` WRITE;
/*!40000 ALTER TABLE `user_backup` DISABLE KEYS */;
INSERT INTO `user_backup` VALUES (1,'admin','123456','系统管理员',NULL,0,'ADMIN001',0,'File/16cc2e06-80dd-4ac7-a899-f693fc847a5e.jpg',NULL),(2,'manager1','123456','王经理',35,1,'MANAGER001',0,NULL,'13800138001'),(3,'staff1','123456','张三',25,2,'STAFF001',0,'File/d0b33527-5667-4928-80f3-5536e9ed9f04.png','13900139001'),(4,'cleaner1','123456','李四',45,3,'CLEANER001',0,NULL,'13700137001'),(5,'cleaner3','123456','赵六',30,3,'CLEANER002',0,'File/92767ec6-b3eb-4f70-8025-c0d9356c3c19.jpg','13500135001'),(6,'staff2','123456','孙七',28,2,'STAFF002',0,NULL,'13600136001'),(7,'cleaner2','123456','王五',29,3,'CLEANER003',0,NULL,'13600136008');
/*!40000 ALTER TABLE `user_backup` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-03 11:24:04
