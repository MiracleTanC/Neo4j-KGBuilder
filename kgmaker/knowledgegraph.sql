/*
Navicat MySQL Data Transfer

Source Server         : 192.168.100.92
Source Server Version : 80012
Source Host           : 192.168.100.92:3306
Source Database       : bd

Target Server Type    : MYSQL
Target Server Version : 80012
File Encoding         : 65001

Date: 2018-12-05 13:34:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for knowledgegraphdomain
-- ----------------------------
DROP TABLE IF EXISTS `knowledgegraphdomain`;
CREATE TABLE `knowledgegraphdomain` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `nodecount` int(11) NOT NULL DEFAULT '0',
  `shipcount` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `createuser` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for knowledgenodedetail
-- ----------------------------
DROP TABLE IF EXISTS `knowledgenodedetail`;
CREATE TABLE `knowledgenodedetail` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '领域关系主键',
  `DomainId` int(11) DEFAULT NULL COMMENT '知识图谱领域主键',
  `NodeId` int(11) DEFAULT NULL COMMENT '关系定义主键',
  `Status` int(11) DEFAULT '1',
  `Content` text CHARACTER SET utf8 COLLATE utf8_general_ci,
  `CreateUser` varchar(255) DEFAULT NULL,
  `CreateTime` datetime DEFAULT NULL,
  `ModifyUser` varchar(255) DEFAULT NULL,
  `ModifyTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `domainid` (`DomainId`) USING BTREE,
  KEY `nodeid` (`NodeId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for knowledgenodedetailfile
-- ----------------------------
DROP TABLE IF EXISTS `knowledgenodedetailfile`;
CREATE TABLE `knowledgenodedetailfile` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '领域关系主键',
  `DomainId` int(11) DEFAULT NULL COMMENT '知识图谱领域主键',
  `NodeId` int(11) DEFAULT NULL COMMENT '关系定义主键',
  `FileName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '若是本地上传则为文件名称，若是网络链接则保存为链接',
  `ImageType` int(11) DEFAULT '0' COMMENT '0=本地上传,1=网络链接',
  `Status` int(11) DEFAULT '1',
  `CreateUser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `CreateTime` datetime DEFAULT NULL,
  `ModifyUser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `ModifyTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `domainid` (`DomainId`) USING BTREE,
  KEY `nodeid` (`NodeId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8;
