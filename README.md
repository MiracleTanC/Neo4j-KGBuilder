# Neo4j-KGBuilder

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-green.svg)](https://spring.io/projects/spring-boot)
[![Neo4j](https://img.shields.io/badge/Neo4j-5.x-blue.svg)](https://neo4j.com/)
[![Vue](https://img.shields.io/badge/Vue.js-2.x-green.svg)](https://vuejs.org/)
[![JDK](https://img.shields.io/badge/JDK-17-orange.svg)](https://openjdk.org/projects/jdk/17/)

## 简介

**Neo4j-KGBuilder** 是一个基于 Neo4j 图数据库与 Spring Boot 的知识图谱构建与可视化工具。

从满足快速构建演示用知识图谱的需求出发，逐渐演变为一个通用的轻量级工具：图形化的节点/关系编辑、多种格式导入导出、外部数据源接入、ER 建模生成图谱、智能问答，开箱即用。适合初学者学习知识图谱，也可用于小型项目的快速原型开发。

- **GitHub**: <https://github.com/MiracleTanC/Neo4j-KGBuilder>

## 功能特性

- **可视化构建**: 画布直接新建节点（十字光标）、单击节点呼出环形菜单完成 编辑 / 展开 / 追加 / 连线 / 删除；双击修改节点名称。
- **样式与布局**: 节点颜色、大小可自定义；拖拽布局自动持久化坐标，刷新不乱。
- **批量操作**: 支持批量添加同级节点、批量创建下级节点及其关系。
- **多重关系**: 同一对节点之间支持多条不同名称的关系并存。
- **富文本附件**: 节点可关联图片与富文本描述，悬停展示详情。
- **导入导出**:
    - CSV 导入 / 导出（节点-节点-关系 三列格式）；
    - Excel 三元组导入（`.xlsx` / `.xls` / `.csv`）；
    - 图谱截图导出 PNG。
- **外部数据源**: 注册外部 MySQL 等数据源，浏览表和字段，勾选列后通过流程编排将关系表数据抽取成图谱。
- **ER 建模**: 内置 ER 图绘制工具，基于 ER 流程配置自动生成实体-关系图谱。
- **智能问答**: 输入自然语言问题，系统分词后检索匹配图谱节点作答（演示级实现）。
- **图谱检视**: 随时查看当前图谱的 JSON 数据结构。

## 技术栈

### 后端

| 组件 | 版本 / 说明 |
| --- | --- |
| Java | 17 |
| Spring Boot | 3.5.8 |
| Neo4j Driver | Neo4j **5.x**（已适配 elementId） |
| ORM | MyBatis + PageHelper |
| 工具库 | Lombok、Hutool、Apache POI、HanLP |

### 前端

| 组件 | 版本 / 说明 |
| --- | --- |
| Vue.js | 2.7（Vue CLI 5） |
| 可视化 | D3.js v5、AntV X6（ER）、JsPlumb |
| UI 组件 | Element UI |
| 状态/路由 | Vuex、Vue Router |

## 项目结构

```text
Neo4j-KGBuilder/
├── docs/               # 文档及图片资源
├── kgBuilder-base/     # 基础模块：Neo4jUtil、统一响应 R、分页等工具
├── kgBuilder-meta/     # 元数据管理模块：外部数据源注册与表结构浏览
├── kgBuilder-pro/      # 核心业务模块：图谱 API、导入流程、问答等
├── kgBuilder-ui/       # 前端 Vue 项目
├── sql/                # SQL 初始化脚本
│   └── kg_builder.sql
├── pom.xml             # Maven 父工程配置
└── README.md
```

架构说明：图数据全部存放在 Neo4j；SQL 库只保存记账信息（领域列表、分类树、节点详情/图片索引、反馈等）。所有图访问经 `kgBuilder-base` 的 `Neo4jUtil` 收口执行 Cypher 完成。

## 快速开始

### 环境要求

| 依赖 | 要求 | 备注 |
| --- | --- | --- |
| JDK | 17+ | 后端编译运行 |
| Maven | 3.6+ | 多模块构建 |
| Node.js | 14+（含 npm） | 前端开发构建 |
| Neo4j | **必须 5.x** | 本项目适配了 Neo4j 5 的 `elementId` 体系，不支持 3.x / 4.x |
| MySQL | 可选 | 不装也能跑：默认使用内置 H2 内存库；需要数据持久化时再接真实 MySQL |

### 启动后端

1. 克隆代码：

   ```bash
   git clone https://github.com/MiracleTanC/Neo4j-KGBuilder.git
   cd Neo4j-KGBuilder
   ```

2. 构建：

   ```bash
   mvn clean install -DskipTests
   ```

3. 配置 Neo4j 连接。默认连接 `bolt://localhost:7687`，账号 `neo4j`。推荐用环境变量注入你的实际密码：

   ```bash
   export NEO4J_URL=bolt://localhost:7687
   export NEO4J_USERNAME=neo4j
   export NEO4J_PASSWORD=你的密码
   ```

4. （可选）切换到真实 MySQL 持久化。项目内置两套数据源配置文件，切换只需激活 `mysql` profile：

   ```sql
   -- 第一步：在目标库执行一次建表脚本
   source sql/kg_builder.sql;
   ```

   ```bash
   # 第二步：以 mysql 配置启动
   export SPRING_PROFILES_ACTIVE=mysql

   # 第三步：注入数据库账号（默认占位为 root/你的密码，请务必覆盖）
   export DB_USERNAME=root
   export DB_PASSWORD=你的密码
   # 连接串与驱动可按需覆盖：
   # export DB_URL="jdbc:mysql://localhost:3306/kg?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
   # export DB_DRIVER=com.mysql.cj.jdbc.Driver
   ```

   > 注意：`mysql` 模式下 `SQL_INIT_MODE` 固定为 `never`（不重建表）；若误用 H2 默认模式的 `always` 启动策略连 MySQL，会导致每次启动清空重建全部数据。

5. 启动主类 `kgBuilder-pro/src/main/java/com/warmer/Application.java`（IDEA 直接运行），或打包后运行 jar。后端监听 **8081** 端口。

### 环境变量速查

| 变量 | 作用 | 默认值 |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | 数据源模式：`dev`=内置 H2 开箱即用；`mysql`=真实 MySQL 持久化 | `dev` |
| `NEO4J_URL` | Neo4j Bolt 地址 | `bolt://localhost:7687` |
| `NEO4J_USERNAME` | Neo4j 用户名 | `neo4j` |
| `NEO4J_PASSWORD` | Neo4j 密码 | `123456`（务必覆盖为自己的） |
| `DB_URL` | SQL 库 JDBC 连接串（仅 mysql 模式） | `jdbc:mysql://localhost:3306/kg…` |
| `DB_DRIVER` | JDBC 驱动类（仅 mysql 模式） | `com.mysql.cj.jdbc.Driver` |
| `DB_USERNAME` / `DB_PASSWORD` | SQL 库账号（mysql 模式必须覆盖） | 占位值 |
| `SQL_INIT_MODE` | 表初始化策略：`always`=按 schema.sql 重建；`never`=跳过 | dev 为 `always`，mysql 为 `never` |
| `VUE_APP_BACKEND` | 前端代理指向的后端地址 | `http://localhost:8081` |

> 文件上传目录由配置项 `neo4j.file.location` 指定（Windows 形如 `D:\\kgmanager\\csv\\`，Linux 形如 `/home/kgmanager/csv/`），该目录需保证 Neo4j 服务进程可读；同机部署通常无需处理，Docker/远程部署请挂载目录。

### 启动前端

```bash
cd kgBuilder-ui
npm install
npm run serve     # 开发模式，默认端口 80
npm run build     # 生产构建，产物在 dist/
```

开发模式下请求 `/kg-api/*` 会自动代理到后端 `http://localhost:8081`（可通过环境变量 `VUE_APP_BACKEND` 修改），因此前后端需同时在线。

### 访问应用

浏览器打开 <http://localhost:80> ，左侧选择或新建图谱即可开始。

## 使用指南

### 图谱基本操作

1. 左侧点击「新建图谱」输入名称创建领域；点击标签加载对应图谱。
2. 画布空白处右键唤出菜单，可将鼠标切为十字光标后在画布上点选位置创建单点。
3. 单击任意节点，会在其周围展开环形按钮组：
    - **编辑**：修改节点名称、颜色、大小；
    - **展开**：懒加载该节点的下级关联，逐步构建大图；
    - **追加**：为该节点批量追加子节点（单点 / 块状批量）；
    - **连线**：进入连线模式，再点选目标节点即建立关系（可自定义关系名）；
    - **删除**：删除该节点及其相关关系。
4. 双击节点可直接改名；拖拽节点的坐标变化会自动保存，刷新后保持布局。

### 数据导入

- 支持 `.xlsx` / `.xls` / `.csv` 三元组文件，列为 **节点-关系-节点** 顺序：

  ```text
  刘德华,出演,无间道
  刘德华,演唱,忘情水
  ```

- 文件编码必须是 **UTF-8 无 BOM**，否则中文会乱码；
- 上传目录需对 Neo4j 进程可见（见上文 `neo4j.file.location` 说明）；
- 已提供图谱导出为 CSV 与画布截图功能。

### 外部数据源生成图谱

适用于把已有业务库（如 MySQL）的关系数据转成图谱：

1. 「数据源管理」中登记外部数据库连接信息；
2. 浏览库中的表和字段，勾选参与建图的列并设置主键 / 主实体标识；
3. 进入流程设计器连线各节点组件、标注关系类型后执行；
4. 系统分页抽取源表数据，合并生成实体节点、属性节点与关系。

### ER 图建模

内置 ER 绘制工具（X6 + JsPlumb），可先定义实体与属性的结构视图，再基于 ER 流程配置一键生成对应的图谱结构。

### 智能问答

问答页面输入自然语言问题，系统使用 HanLP 分词后以关键词检索图谱节点返回命中结果（演示级实现，可作为接入 LLM 的前置示例扩展）。

## 常见问题 (FAQ)

- **为什么连不上 Neo4j / 提示认证失败？**
    - 确认 Neo4j 服务已启动且监听 7687（Bolt）端口；
    - Neo4j 5 默认要求首次登录修改密码，确认环境变量 `NEO4J_PASSWORD` 与实际一致；
    - 连续输错密码会触发 `AuthenticationRateLimit` 限流，等待片刻或重启 Neo4j 再试。

- **提示 Cypher 或 id 相关错误？**
    - 请确认 Neo4j 版本为 5.x，本项目已全面适配 `elementId()`，不支持旧版本。

- **导入的中文数据乱码？**
    - 文件需为 UTF-8 无 BOM 编码（Windows 下注意另存为时的编码选项）。

- **重启后之前建的图谱还在，但节点详情/图片丢了？**
    - 内置 H2 是内存库，重启即清空记账数据。生产使用请按上文步骤接入真实 MySQL 并设 `SQL_INIT_MODE=never`。

- **前端 80 端口被占用？**
    - 修改 `kgBuilder-ui/vue.config.js` 中 devServer 的端口配置，或以管理员权限运行。

## 文档索引

- 顶层说明：当前文件 `README.md`
- 文档索引与规范：`docs/README.md`
- 本机 Neo4j 安装启动指南：`docs/neo4j-startup-guide.md`
- 前端命令与说明：`kgBuilder-ui/README.md`

## 参与贡献

欢迎 Issue 和 PR！提交代码前请注意：

- Java 注释使用中文 Javadoc，JS/Vue 注释使用中文 JSDoc；
- 提交信息遵循 Conventional Commits，主题使用中文（如 `feat(问答系统): 新增智能问答功能模块`）；
- Markdown 文档遵循 `docs/README.md` 中的排版规范。

## 许可证

[Apache License 2.0](LICENSE)

## 交流反馈

![交流群1](docs/images/kgbuilder.jpg)
![交流群2](docs/images/kgbuilder2.jpg)
