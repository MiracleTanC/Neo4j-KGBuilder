# Neo4j Community 5.12.0 部署与启动指南（Windows）

本文记录在本项目本地环境（Windows）中部署和启动 `neo4j-community-5.12.0` 的完整流程，以及与后端联调的验证步骤。

## 环境要求

- 安装目录：`D:\neo4j-community-5.12.0`
- JDK：17（本机路径 `C:\work\jdk-17.0.6+10`）
- 默认端口：Bolt `7687`、HTTP `7474`

## 第一步：配置 JAVA_HOME

Neo4j 的 Windows 启动脚本依赖 `JAVA_HOME` 环境变量定位 JDK。

推荐永久设置：系统属性 → 高级 → 环境变量，新建系统变量：

```text
变量名：JAVA_HOME
变量值：C:\work\jdk-17.0.6+10
```

或在当前命令行窗口临时设置（仅对当前窗口生效）：

```cmd
set JAVA_HOME=C:\work\jdk-17.0.6+10
```

验证：

```cmd
echo %JAVA_HOME%
%JAVA_HOME%\bin\java.exe -version
```

## 第二步：设置或核对密码

后端配置文件 `kgBuilder-pro/src/main/resources/application-dev.yml` 中 `custom.neo4j.password` 的默认值为 `123456`。请确保数据库实际密码与后端使用的密码一致，两种做法任选其一：

**做法 A（推荐）：数据库密码保持现状，让后端通过环境变量匹配。**

例如本机实际密码为 `12345678`，则启动后端前设置：

```cmd
set NEO4J_PASSWORD=12345678
```

**做法 B：把数据库密码改成与配置一致的 `123456`（仅在首次启动前可用）。**

```cmd
cd /d D:\neo4j-community-5.12.0
bin\neo4j-admin.bat dbms set-initial-password 123456
```

注意：

- 做法 B 只在从未启动过服务器时可用（判断依据：`data\dbms` 目录不存在）。若已登录过，请在浏览器界面或用 Cypher 执行 `ALTER CURRENT USER SET PASSWORD FROM '旧密码' TO '新密码'` 修改。
- 切勿通过删除 `data\dbms` 目录重置密码，除非确认库内数据可丢弃。
- 后端应用若带着错误密码反复重连，会触发认证限流（详见常见问题表），因此首次接入前务必先核对密码。

## 第三步：启动服务

方式一（推荐调试用）：前台运行，日志直接输出到窗口，`Ctrl+C` 停止：

```cmd
bin\neo4j.bat console
```

方式二：后台运行（Windows 服务方式，切换目录后执行，停止用 `bin\neo4j.bat stop`）：

```cmd
bin\neo4j.bat start
bin\neo4j.bat status
bin\neo4j.bat stop
```

`conf\neo4j.conf` 中与本机环境相关的关键配置如下，均已配置好，无需改动：

```ini
server.default_listen_address=0.0.0.0            # 允许外部访问
server.directories.import=import                 # 导入目录
dbms.security.allow_csv_import_from_file_urls=true
```

## 第四步：验证数据库

打开 [http://localhost:7474](http://localhost:7474)，使用 `neo4j / 123456` 登录；或用命令行冒烟测试：

```cmd
bin\cypher-shell.bat -u neo4j -p 123456
```

进入交互界面后执行以下语句确认版本与索引状态，输入 `:exit` 退出：

```cypher
CALL dbms.components() YIELD name, versions RETURN name, versions;
SHOW DATABASES;
```

## 第五步：与本项目联调验证

1. 构建并启动后端：在仓库根目录执行 `mvn clean install -DskipTests`；若数据库密码不是 `123456`，先设置环境变量 `NEO4J_PASSWORD` 为实际密码，再运行 `kgBuilder-pro/src/main/java/com/warmer/Application.java`（端口 `8081`）。
2. 启动前端：`cd kgBuilder-ui && npm run serve`（开发代理 `/kg-api` → `http://localhost:8081`）。
3. 按下列清单逐项确认兼容改造生效：
   - 应用启动无异常，且 Neo4j 中已有的图标签自动出现在领域列表；
   - 上传一份「节点-节点-关系」格式的 CSV/XLSX 三元组文件，刷新图谱可看到节点与关系生成；
   - 浏览器登录 Neo4j 后执行 `SHOW INDEXES;`，能看到新语法创建的非旧式命名索引。

## 常见问题

| 现象 | 处理 |
| --- | --- |
| 报 `Neo.ClientError.Security.AuthenticationRateLimit`（认证限流） | 有客户端连续 3 次以上用错误凭证登录触发了临时锁定。先停止在用错误密码重连的应用（如后端、IDE 数据库工具），等待约十几秒让锁定期过期；把凭证改正后再连，计数会自动清零，无需重启数据库 |
| 启动脚本报找不到 JAVA_HOME | 按第一步设置环境变量后重新打开命令行窗口 |
| `set-initial-password` 报初始密码已设置 | 说明已经启动过一次，改用 `ALTER CURRENT USER SET PASSWORD` 方式修改 |
| 7474/7687 端口被占用 | 用 `netstat -ano \| findstr "7687"` 排查占用进程 |
| 后端连不上 Neo4j | 核对 `application-dev.yml` 与实际地址、账号、密码是否一致 |

> 本项目当前不依赖 APOC 插件；配置文件中的 `apoc.*` 白名单条目保留不影响运行。
