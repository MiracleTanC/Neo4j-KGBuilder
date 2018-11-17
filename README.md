Welcome to the Neo4j wiki!
## 初衷
这是一个知识图谱构建工具,最开始是对产品和领导为了做ppt临时要求配合做图谱展示的不厌其烦,做着做着就抽出一个目前看着还算通用的小工具
## 技术栈
小工具是前台是基于vue + d3.js ,后台是springboot配合Neo4j.
## 演示
实现的基本功能:
1. 新增节点,添加连线,快速添加节点和关系
2. 节点的颜色和大小可修改
3. 节点和关系的编辑,删除
4. 导出成图片
5. csv导入
6. 导出csv


![知识图谱构建小工具](http://file.miaoleyan.com/kg1.gif)
![知识图谱构建小工具](http://file.miaoleyan.com/kg2.gif)
![知识图谱构建小工具](http://file.miaoleyan.com/kg3.gif)
![知识图谱构建小工具](http://file.miaoleyan.com/kg4.gif)
![知识图谱构建小工具](http://file.miaoleyan.com/m99.gif)

## 后续优化:
1. ~~新建单节点,节点的位置能指定(或者和鼠标点下的位置一致) over 2018-11-16~~
2. ~~做出的图谱能够导出成图片 over 2018-11-16~~
3. ~~支持导入功能,同时支持导出关系和节点 over 2018-11-17~~
4. 输入一段文本,通过自然语言等手段,抽出实体和关系,编辑后可形成可视化的图谱
## 运行与启动
1. 需要jdk1.8,安装Neo4j,安装和配置自行百度
2. 启动后访问[http://localhost:8089/kg/index](http://localhost:8089/kg/index) 即可
