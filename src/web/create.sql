/*
SQLyog Ultimate - MySQL GUI v8.22 
MySQL - 5.1.53-log : Database - mycore4me
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mycore4me` /*!40100 DEFAULT CHARACTER SET utf8 */;

/*Table structure for table `user` */

CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `hash` char(40) NOT NULL COMMENT 'user_hash',
  `name` char(40) COMMENT 'user_name',
  `password` char(40) COMMENT 'password',
  `coresid` char(10) COMMENT 'core session id',
  `oauthtoken` char(255) COMMENT 'oauth token',

  PRIMARY KEY (`id`),
  UNIQUE KEY `hash_name` (`hash`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;