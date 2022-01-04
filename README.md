## 初衷
这是一个知识图谱构建工具,最开始是对产品和领导为了做ppt临时要求配合做图谱展示的不厌其烦,做着做着就抽出一个目前看着还算通用的小工具，知识图谱可视化，仅供做知识图谱的小伙伴参考
## 技术栈
小工具是前台是基于vue + d3.js ,后台是springboot配合Neo4j.
## 演示
demo地址：[http://kg.miaoleyan.com](http://kg.miaoleyan.com)
## dev分支
dev分支是前后端分离版本，代码都进行了一些优化，可以去http://kg.miaoleyan.com 查看右上角帮助
实现的基本功能:
1. 新增节点,添加连线,快速添加节点和关系
2. 节点的颜色和大小可修改
3. 节点和关系的编辑,删除
4. 导出成图片
5. csv导入
6. 导出csv
7. 添加图片和富文本
8. 节点之间多个关系
9. 增加直接执行cypher功能


## 后续优化:
1. ~~新建单节点,节点的位置能指定(或者和鼠标点下的位置一致) over 2018-11-16~~
2. ~~做出的图谱能够导出成图片 over 2018-11-16~~
3. ~~支持导入功能,同时支持导出关系和节点 over 2018-11-17~~
4. 输入一段文本,通过自然语言等手段,抽出实体和关系,编辑后可形成可视化的图谱（暂不开放）
5. ~~按钮组不能随节点半径的变化而变化~~
6. ~~节点间多关系线和文字重叠~~
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
在idea 右侧 有 maven project 工具栏，点击展开lifecycle-clean,然后install,等待完成后在控制台可以看见打包的目录，例如：[INFO] Installing F:\git\Neo4j\kgmaker\target\kgmaker-0.0.1-SNAPSHOT.jar 复制jar包，去windows  或者linux下 切换到jar包目录执行  jar包   java -jar xxx.jar  即可启动，想部署到tomcat自行百度，springboot配置外部tomcat
### 访问路径
启动后访问[http://localhost](http://localhost) 

供前端小哥哥小姐姐参考的静态网页：打开文件夹，找到 /kgmaker/src/main/resources/templates/kg/demoforfont-end.html
### 特别说明
 **KgBuilder是纯前端项目，不和后端交互，kgmaker是后端项目，页面也在template下，和前者没有直接关系** 
### 图谱三元组导入
支持,.xlsx,.xls,.csv，编码格式一定要是utf-8 无bom格式的，格式：节点-节点-关系，在本地测试时上传下载的文件要和neo4j在同一台电脑，当然如果能传到七牛或者hdfs上也是一样的，必须确认neo4j能访问到，否则load不成功
### 本项目不再维护
可以进群和其他小伙伴交流
### 推荐前端组件
G6 [https://g6.antv.vision/zh/examples/gallery](https://g6.antv.vision/zh/examples/gallery)
### 推荐图数据库
Nebula [https://docs.nebula-graph.com.cn/2.5.1/](https://docs.nebula-graph.com.cn/2.5.1/)
## 交流
### 希望感兴趣的小伙伴能一起做些事情
![](https://img-blog.csdnimg.cn/20201003145558813.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3BveGlhb21lbmcxODc=,size_16,color_FFFFFF,t_70#pic_center)
