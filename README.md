## 初衷
这是一个知识图谱构建工具,最开始是对产品和领导为了做ppt临时要求配合做图谱展示的不厌其烦,做着做着就抽出一个目前看着还算通用的小工具，知识图谱可视化，仅供做知识图谱的小伙伴参考
## 技术栈
小工具是前台是基于vue + d3.js ,后台是springboot配合Neo4j.
## 演示
demo地址：[http://kg.miaoleyan.com](http://kg.miaoleyan.com)

## 实现的基本功能:
1. 新增节点,添加连线,快速添加节点和关系
2. 节点的颜色和大小可修改
3. 节点和关系的编辑,删除
4. 导出成图片
5. csv导入
6. 导出csv
7. 添加图片和富文本
8. 节点之间多个关系

## 后续优化:
1. 接入数据源
2. 构建er图
3. 根据er图生成图谱

## 运行与启动
### 安装jdk
可参考：[https://blog.csdn.net/qq_42003566/article/details/82629570](https://blog.csdn.net/qq_42003566/article/details/82629570)
### 安装Neo4j
可参考：[https://www.cnblogs.com/ljhdo/p/5521577.html](https://www.cnblogs.com/ljhdo/p/5521577.html),注意开放外网访问  0.0.0.0
### IDEA 导入项目 
导入成功后对着项目根目录，右键->maven->reimport，等待其执行完成，倘若下载jar包太慢，自己配置外部maven仓库[https://blog.csdn.net/liu_shi_jun/article/details/78733633](https://blog.csdn.net/liu_shi_jun/article/details/78733633)
以上配置在linux下配置自行百度
### 找到目录 src/main/resources  
修改application.yml,neo4配置url，password,改成自己的，同理修改mysql（mysql脚本在根目录下，knowledgegraph.sql）
### 打包发布  
在idea 右侧 有 maven project 工具栏，点击展开lifecycle-clean,然后install,等待完成后在控制台可以看见打包的目录，
例如：[INFO] Installing F:\git\Neo4j\kgmaker\target\kgmaker-0.0.1-SNAPSHOT.jar 复制jar包，
去windows  或者linux下 
切换到jar包目录执行jar包   
```
  java -jar xxx.jar   //即可启动
```
想部署到tomcat自行百度，springboot配置外部tomcat

启动前端
```
1.npm install // 安装依赖
2.npm run serve //启动
3.npm run build //发布
启动后访问http://localhost
```

### 访问路径
启动后访问[http://localhost](http://localhost) 

### 图谱三元组导入
支持,.xlsx,.xls,.csv，编码格式一定要是utf-8 无bom格式的，格式：节点-节点-关系，在本地测试时上传下载的文件要和neo4j在同一台电脑，当然如果能传到七牛或者hdfs上也是一样的，必须确认neo4j能访问到，否则load不成功
### 推荐前端组件
G6 [https://g6.antv.vision/zh/examples/gallery](https://g6.antv.vision/zh/examples/gallery)
### 推荐图数据库
Nebula [https://docs.nebula-graph.com.cn/2.5.1/](https://docs.nebula-graph.com.cn/2.5.1/)
## 交流
![](./kgbuilder.jpg)
![](./kgbuilder2.jpg)
