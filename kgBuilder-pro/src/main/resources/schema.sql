DROP TABLE IF EXISTS kg_category;
CREATE TABLE kg_category (
  CategoryNodeId INT AUTO_INCREMENT PRIMARY KEY,
  CategoryNodeName VARCHAR(255),
  CategoryNodeCode VARCHAR(255),
  SystemCode VARCHAR(255),
  CategoryId BIGINT NOT NULL,
  ParentId INT,
  ParentCode VARCHAR(255),
  TreeLevel INT,
  IsLeaf INT DEFAULT 1,
  Status INT,
  FileUuid VARCHAR(255),
  FileName VARCHAR(255),
  CreateUser VARCHAR(64),
  CreateTime TIMESTAMP,
  UpdateUser VARCHAR(64),
  UpdateTime TIMESTAMP,
  Color VARCHAR(255)
);

DROP TABLE IF EXISTS kg_domain;
CREATE TABLE kg_domain (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  label VARCHAR(64),
  type INT,
  nodecount INT DEFAULT 0,
  shipcount INT NOT NULL,
  commend INT DEFAULT 0,
  status INT NOT NULL,
  createuser VARCHAR(255),
  createtime TIMESTAMP,
  modifyTime TIMESTAMP,
  modifyUser VARCHAR(255)
);

DROP TABLE IF EXISTS kg_feedback;
CREATE TABLE kg_feedback (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  `desc` TEXT,
  type INT,
  email VARCHAR(255),
  createTime TIMESTAMP
);

DROP TABLE IF EXISTS kg_graph_link;
CREATE TABLE kg_graph_link (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  `from` VARCHAR(100),
  `to` VARCHAR(100),
  label VARCHAR(100),
  domainId INT
);

DROP TABLE IF EXISTS kg_graph_node;
CREATE TABLE kg_graph_node (
  nodeId BIGINT AUTO_INCREMENT PRIMARY KEY,
  nodeKey VARCHAR(100),
  tableId INT,
  nodeName VARCHAR(100),
  type VARCHAR(100),
  `left` VARCHAR(50),
  top VARCHAR(50),
  ico VARCHAR(100),
  state VARCHAR(100),
  viewOnly INT,
  sourceId INT,
  domainId INT,
  startNode INT
);

DROP TABLE IF EXISTS kg_graph_node_map;
CREATE TABLE kg_graph_node_map (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  columnId INT,
  ico VARCHAR(100),
  isPrimary INT,
  itemId VARCHAR(100),
  itemCode VARCHAR(100) NOT NULL,
  isMainEntity INT DEFAULT 0,
  itemName VARCHAR(100),
  itemType VARCHAR(100),
  nodeId BIGINT,
  domainId INT
);

DROP TABLE IF EXISTS kg_nodedetail;
CREATE TABLE kg_nodedetail (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  DomainId INT,
  NodeId VARCHAR(64),
  Status INT DEFAULT 1,
  Content TEXT,
  CreateUser VARCHAR(255),
  CreateTime TIMESTAMP,
  ModifyUser VARCHAR(255),
  ModifyTime TIMESTAMP
);

DROP TABLE IF EXISTS kg_nodedetail_file;
CREATE TABLE kg_nodedetail_file (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  DomainId INT,
  NodeId VARCHAR(64),
  FileName VARCHAR(255),
  ImageType INT DEFAULT 0,
  Status INT DEFAULT 1,
  CreateUser VARCHAR(255),
  CreateTime TIMESTAMP,
  ModifyUser VARCHAR(255),
  ModifyTime TIMESTAMP
);

DROP TABLE IF EXISTS meta_data_column;
CREATE TABLE meta_data_column (
  DataColumnId INT AUTO_INCREMENT PRIMARY KEY,
  DataTableId INT,
  DataColumnCode VARCHAR(64),
  DataColumnName VARCHAR(64),
  DataColumnComment VARCHAR(64),
  DataColumnType VARCHAR(20),
  IsPrimary INT,
  Status INT DEFAULT 1,
  CreateUser VARCHAR(64),
  CreateTime TIMESTAMP,
  UpdateUser VARCHAR(64),
  UpdateTime TIMESTAMP
);

DROP TABLE IF EXISTS meta_data_source;
CREATE TABLE meta_data_source (
  DataSourceId INT AUTO_INCREMENT PRIMARY KEY,
  DbType VARCHAR(64),
  DriverName VARCHAR(255),
  IPAndPort VARCHAR(64),
  ConnectUrl VARCHAR(255),
  DbName VARCHAR(255),
  DbCode VARCHAR(255),
  DbUserName VARCHAR(255),
  DbPassWord VARCHAR(255),
  MaxPoolSize INT,
  Status INT DEFAULT 1,
  DatabaseCoding VARCHAR(64),
  Transcoding VARCHAR(64),
  CreateUser VARCHAR(64),
  CreateTime TIMESTAMP,
  UpdateUser VARCHAR(64),
  UpdateTime TIMESTAMP
);

DROP TABLE IF EXISTS meta_data_table;
CREATE TABLE meta_data_table (
  DataTableId INT AUTO_INCREMENT PRIMARY KEY,
  DatasourceId INT,
  DataTableCode VARCHAR(128),
  DataTableName VARCHAR(128),
  Status INT DEFAULT 1,
  CreateUser VARCHAR(64),
  CreateTime TIMESTAMP,
  UpdateUser VARCHAR(64),
  UpdateTime TIMESTAMP
);

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid VARCHAR(255),
  username VARCHAR(255),
  nickname VARCHAR(255),
  avatar VARCHAR(255),
  blog VARCHAR(255),
  company VARCHAR(255),
  location VARCHAR(255),
  email VARCHAR(255),
  remark VARCHAR(255),
  gender INT,
  source VARCHAR(255),
  createtime TIMESTAMP,
  updatetime TIMESTAMP,
  isadmin INT DEFAULT 0
);
