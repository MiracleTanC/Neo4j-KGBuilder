/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : kg

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 08/05/2023 14:49:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for kg_category
-- ----------------------------
DROP TABLE IF EXISTS `kg_category`;
CREATE TABLE `kg_category`  (
  `CategoryNodeId` int(11) NOT NULL AUTO_INCREMENT,
  `CategoryNodeName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CategoryNodeCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `SystemCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CategoryId` bigint(20) NOT NULL,
  `ParentId` int(11) NULL DEFAULT NULL,
  `ParentCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `TreeLevel` int(11) NULL DEFAULT NULL,
  `IsLeaf` int(11) NOT NULL DEFAULT 1,
  `Status` int(11) NULL DEFAULT NULL,
  `FileUuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附件uuid',
  `FileName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '节点来源附件',
  `CreateUser` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CreateTime` datetime(0) NULL DEFAULT NULL,
  `UpdateUser` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `UpdateTime` datetime(0) NULL DEFAULT NULL,
  `Color` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`CategoryNodeId`) USING BTREE,
  INDEX `categoryId`(`CategoryId`) USING BTREE,
  INDEX `parentId`(`ParentId`) USING BTREE,
  INDEX `parentCode`(`ParentCode`) USING BTREE,
  INDEX `categoryCode`(`CategoryNodeCode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101534 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kg_domain
-- ----------------------------
DROP TABLE IF EXISTS `kg_domain`;
CREATE TABLE `kg_domain`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `label` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` int(11) NULL DEFAULT NULL COMMENT '0=手动创建，1=三元组导入，2=excel导入，3=er图构建',
  `nodecount` int(11) NOT NULL DEFAULT 0,
  `shipcount` int(11) NOT NULL,
  `commend` int(11) NULL DEFAULT 0 COMMENT '推荐',
  `status` int(11) NOT NULL,
  `createuser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createtime` datetime(0) NULL DEFAULT NULL,
  `modifyTime` datetime(0) NULL DEFAULT NULL,
  `modifyUser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kg_feedback
-- ----------------------------
DROP TABLE IF EXISTS `kg_feedback`;
CREATE TABLE `kg_feedback`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '反馈主题',
  `desc` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '描述',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kg_graph_link
-- ----------------------------
DROP TABLE IF EXISTS `kg_graph_link`;
CREATE TABLE `kg_graph_link`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `to` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `label` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `domainId` int(11) NULL DEFAULT NULL COMMENT '领域id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kg_graph_node
-- ----------------------------
DROP TABLE IF EXISTS `kg_graph_node`;
CREATE TABLE `kg_graph_node`  (
  `nodeId` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `nodeKey` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '节点唯一标识',
  `tableId` int(11) NULL DEFAULT NULL COMMENT '数据表id',
  `nodeName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '节点名称',
  `type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '节点类型',
  `left` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '节点左位置',
  `top` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '节点右位置',
  `ico` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '节点图标',
  `state` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '节点状态',
  `viewOnly` int(11) NULL DEFAULT NULL COMMENT '是否可以拖动，1=是，0=否',
  `sourceId` int(11) NULL DEFAULT NULL COMMENT '数据源id',
  `domainId` int(11) NULL DEFAULT NULL COMMENT '领域id',
  `startNode` int(11) NULL DEFAULT NULL COMMENT '是否是起点',
  PRIMARY KEY (`nodeId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kg_graph_node_map
-- ----------------------------
DROP TABLE IF EXISTS `kg_graph_node_map`;
CREATE TABLE `kg_graph_node_map`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `columnId` int(11) NULL DEFAULT NULL COMMENT '列id',
  `ico` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '列图标',
  `isPrimary` int(11) NULL DEFAULT NULL COMMENT '是否主键',
  `itemId` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据列key',
  `itemCode` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '原始数据列字段',
  `isMainEntity` int(11) NOT NULL DEFAULT 0 COMMENT '是否主实体',
  `itemName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '列别名',
  `itemType` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `nodeId` bigint(20) NULL DEFAULT NULL COMMENT '节点id',
  `domainId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 320 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kg_nodedetail
-- ----------------------------
DROP TABLE IF EXISTS `kg_nodedetail`;
CREATE TABLE `kg_nodedetail`  (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '领域关系主键',
  `DomainId` int(11) NULL DEFAULT NULL COMMENT '知识图谱领域主键',
  `NodeId` int(11) NULL DEFAULT NULL COMMENT '关系定义主键',
  `Status` int(11) NULL DEFAULT 1,
  `Content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `CreateUser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CreateTime` datetime(0) NULL DEFAULT NULL,
  `ModifyUser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ModifyTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `domainid`(`DomainId`) USING BTREE,
  INDEX `nodeid`(`NodeId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 135 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kg_nodedetail_file
-- ----------------------------
DROP TABLE IF EXISTS `kg_nodedetail_file`;
CREATE TABLE `kg_nodedetail_file`  (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '领域关系主键',
  `DomainId` int(11) NULL DEFAULT NULL COMMENT '知识图谱领域主键',
  `NodeId` int(11) NULL DEFAULT NULL COMMENT '关系定义主键',
  `FileName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '若是本地上传则为文件名称，若是网络链接则保存为链接',
  `ImageType` int(11) NULL DEFAULT 0 COMMENT '0=本地上传,1=网络链接',
  `Status` int(11) NULL DEFAULT 1,
  `CreateUser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `CreateTime` datetime(0) NULL DEFAULT NULL,
  `ModifyUser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ModifyTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `domainid`(`DomainId`) USING BTREE,
  INDEX `nodeid`(`NodeId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 187 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for meta_data_column
-- ----------------------------
DROP TABLE IF EXISTS `meta_data_column`;
CREATE TABLE `meta_data_column`  (
  `DataColumnId` int(11) NOT NULL AUTO_INCREMENT COMMENT '数据列id',
  `DataTableId` int(11) NULL DEFAULT NULL COMMENT '数据表id',
  `DataColumnCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据列名称',
  `DataColumnName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据列别名',
  `DataColumnComment` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据列描述',
  `DataColumnType` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据类型',
  `IsPrimary` int(11) NULL DEFAULT NULL COMMENT '是否主键',
  `Status` int(11) NULL DEFAULT 1 COMMENT '状态:0停用1启用',
  `CreateUser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `CreateTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `UpdateUser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修改人',
  `UpdateTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`DataColumnId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 231 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for meta_data_source
-- ----------------------------
DROP TABLE IF EXISTS `meta_data_source`;
CREATE TABLE `meta_data_source`  (
  `DataSourceId` int(11) NOT NULL AUTO_INCREMENT COMMENT '数据源主键',
  `DbType` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据源类型',
  `DriverName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `IPAndPort` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP及端口号',
  `ConnectUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'url',
  `DbName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据库别名',
  `DbCode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据库名称',
  `DbUserName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `DbPassWord` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `MaxPoolSize` int(11) NULL DEFAULT NULL COMMENT '最大连接数',
  `Status` int(11) NULL DEFAULT 1 COMMENT '状态 0禁用 1启用(默认)',
  `DatabaseCoding` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据库编码',
  `Transcoding` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转换编码',
  `CreateUser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `CreateTime` datetime(0) NULL DEFAULT NULL,
  `UpdateUser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `UpdateTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`DataSourceId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据源' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for meta_data_table
-- ----------------------------
DROP TABLE IF EXISTS `meta_data_table`;
CREATE TABLE `meta_data_table`  (
  `DataTableId` int(11) NOT NULL AUTO_INCREMENT COMMENT '数据表主键',
  `DatasourceId` int(11) NULL DEFAULT NULL COMMENT '数据源id',
  `DataTableCode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '表名',
  `DataTableName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '表别名',
  `Status` int(11) NULL DEFAULT 1 COMMENT '记录状态',
  `CreateUser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建用户',
  `CreateTime` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `UpdateUser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修改用户',
  `UpdateTime` timestamp(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`DataTableId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `blog` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `company` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gender` int(11) NULL DEFAULT NULL,
  `source` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createtime` datetime(0) NULL DEFAULT NULL,
  `updatetime` datetime(0) NULL DEFAULT NULL,
  `isadmin` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1123 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
