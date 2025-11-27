# Neo4j-KGBuilder

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-green.svg)](https://spring.io/projects/spring-boot)
[![Neo4j](https://img.shields.io/badge/Neo4j-5.13.0-blue.svg)](https://neo4j.com/)
[![Vue](https://img.shields.io/badge/Vue.js-2.x-green.svg)](https://vuejs.org/)

## 简介 (Introduction)

**Neo4j-KGBuilder** 是一个基于 Neo4j 和 Spring Boot 的知识图谱构建与可视化工具。

最初是为了满足快速构建演示用知识图谱的需求而开发，逐渐演变为一个通用的轻量级工具。它支持节点和关系的增删改查、可视化展示、导入导出等功能，非常适合初学者学习知识图谱或用于小型项目的快速原型开发。

**GitHub**: [https://github.com/qingxuandaoming/](https://github.com/qingxuandaoming/)

**演示地址**: [http://kg.miaoleyan.com](http://kg.miaoleyan.com)

## 功能特性 (Features)

1.  **可视化操作**: 支持通过图形界面新增节点、添加连线，快速构建图谱。
2.  **样式定制**: 节点的颜色、大小等样式可自定义修改。
3.  **图谱编辑**: 支持对节点和关系进行编辑、删除操作。
4.  **导入导出**:
    *   支持导出图谱为图片。
    *   支持 CSV 格式的数据导入。
    *   支持导出为 CSV 文件。
    *   支持三元组导入（.xlsx, .xls, .csv）。
5.  **富文本支持**: 节点可添加图片和富文本描述。
6.  **多重关系**: 支持节点之间存在多种关系。
7.  **后续规划**:
    *   接入多种数据源。
    *   构建 ER 图。
    *   根据 ER 图自动生成图谱。

## 技术栈 (Technology Stack)

### 后端 (Backend)
*   **Java**: 17
*   **Framework**: Spring Boot 3.5.8
*   **Database**: Neo4j 5.13.0
*   **ORM**: MyBatis 3.0.3, MyBatis-Plus (PageHelper)
*   **Tools**: Lombok, Hutool, Apache POI, HanLP

### 前端 (Frontend)
*   **Framework**: Vue.js 2.x
*   **Visualization**: D3.js, G6
*   **UI Component**: Element UI

## 项目结构 (Project Structure)

```
Neo4j-KGBuilder/
├── docs/               # 文档及图片资源
│   └── images/         # 项目截图
├── kgBuilder-base/     # 基础模块（工具类、通用实体）
├── kgBuilder-meta/     # 元数据管理模块
├── kgBuilder-pro/      # 核心业务模块（API、服务实现）
├── kgBuilder-ui/       # 前端 Vue 项目
├── sql/                # SQL 脚本
│   └── kg_builder.sql  # 初始化 SQL
├── pom.xml             # Maven 父工程配置
└── README.md           # 项目说明文档
```

## 快速开始 (Getting Started)

### 前置要求 (Prerequisites)

1.  **JDK 17**: 确保已安装并配置好环境变量。
2.  **Neo4j 5.x**: 安装并启动 Neo4j 服务，确保开启外网访问（0.0.0.0）。
    *   参考: [Neo4j 安装教程](https://www.cnblogs.com/ljhdo/p/5521577.html)
3.  **Node.js**: 用于运行前端项目。
    *   参考: [Node.js 安装教程](https://blog.csdn.net/qq_46351233/article/details/120314928)

### 后端启动 (Backend Setup)

1.  **导入项目**: 使用 IntelliJ IDEA 导入项目根目录。
2.  **Maven 构建**: 右键根目录 -> Maven -> Reload Project，等待依赖下载完成。
3.  **配置文件**:
    *   找到 `kgBuilder-pro/src/main/resources/application.yml`。
    *   修改 Neo4j 连接配置（url, username, password）。
    *   修改 MySQL 连接配置（如果有用到 MySQL，导入 `sql/kg_builder.sql`）。
4.  **启动服务**:
    *   运行 `kgBuilder-pro/src/main/java/com/warmer/web/Application.java`。

### 前端启动 (Frontend Setup)

1.  进入前端目录:
    ```bash
    cd kgBuilder-ui
    ```
2.  安装依赖:
    ```bash
    npm install
    ```
3.  启动开发服务器:
    ```bash
    npm run serve
    ```
4.  构建发布:
    ```bash
    npm run build
    ```

### 访问应用 (Access)

启动成功后，访问: [http://localhost](http://localhost) (默认端口根据前端配置)

## 使用说明 (Usage)

### 图谱三元组导入

*   支持 `.xlsx`, `.xls`, `.csv` 格式。
*   文件编码必须为 **UTF-8 无 BOM** 格式。
*   数据格式: `节点-节点-关系`。
*   **注意**: 本地测试时，上传的文件路径需要确保 Neo4j 服务能够访问到（如果在同一台机器上通常没问题；如果是 Docker 部署或远程服务，需要挂载目录或使用对象存储如七牛云/HDFS）。

## 交流与反馈 (Contact)

![交流群1](docs/images/kgbuilder.jpg)
![交流群2](docs/images/kgbuilder2.jpg)

## 推荐资源 (Recommended)

*   **前端组件**: [AntV G6](https://g6.antv.vision/zh/examples/gallery)
*   **图数据库**: [Nebula Graph](https://docs.nebula-graph.com.cn/2.5.1/)

## 许可证 (License)

Apache License 2.0
