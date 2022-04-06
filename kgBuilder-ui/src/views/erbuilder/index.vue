<template>
  <div
    v-if="easyFlowVisible"
    style="height: calc(100vh)"
    @contextmenu="hiddenLinkMenu"
    @click="hiddenLinkMenu"
  >
    <el-row>
      <el-col :span="24">
        <div class="ef-tooltar">
          <el-tag
            :key="tag.id"
            v-for="tag in domainList"
            @click="initERData(tag.id)"
            closable
            :disable-transitions="false"
            @close="deleteEr(tag.id)"
          >
            {{ tag.name }}
          </el-tag>
          <el-input
            class="input-new-tag"
            v-if="inputVisible"
            v-model="inputValue"
            ref="saveTagInput"
            size="small"
            @keyup.enter.native="createDomain"
            @blur="createDomain"
          >
          </el-input>
          <el-button
            v-else
            class="button-new-tag"
            size="small"
            @click="showAddDomain"
            >+ 添加领域</el-button
          >
        </div>
      </el-col>
      <!--顶部工具菜单-->
      <el-col :span="24">
        <div class="ef-tooltar">
          <el-link type="primary" :underline="false">
            当前领域 【{{ data.domainName }}】</el-link
          >
          <el-divider direction="vertical"></el-divider>
          <el-button
            type="text"
            @click="refreshData"
            icon="el-icon-refresh"
            size="large"
            >刷新</el-button
          >
          <el-divider direction="vertical"></el-divider>
          <el-button
            type="text"
            icon="el-icon-download"
            size="large"
            @click="downloadData"
          ></el-button>

          <div style="float: right; margin-right: 5px">
            <el-button
              type="primary"
              icon="el-icon-document"
              @click="saveERdata"
              size="mini"
              >保存</el-button
            >
            <el-button
              type="primary"
              icon="el-icon-document"
              @click="executeERdata"
              size="mini"
              >生成图谱</el-button
            >
            <el-button
              type="info"
              plain
              round
              icon="el-icon-document"
              @click="dataInfo"
              size="mini"
              >预览数据</el-button
            >

            <el-button
              type="info"
              plain
              round
              icon="el-icon-document"
              @click="openHelp"
              size="mini"
              >帮助</el-button
            >
          </div>
        </div>
      </el-col>
    </el-row>
    <div style="display: flex; height: calc(100% - 47px)">
      <div style="width: 230px; border-right: 1px solid #dce3e8">
        <node-menu @addNode="addNode" ref="nodeMenu"></node-menu>
      </div>
      <div id="efContainer" ref="efContainer" class="container" v-flowDrag>
        <template v-for="node in data.nodeList">
          <flow-node
            :key="node.nodeKey"
            :id="node.nodeKey"
            :node="node"
            :activeElement="activeElement"
            @clickNode="clickNode"
            @nodeRightMenu="nodeRightMenu"
            @deleteNode="deleteNode"
          >
          </flow-node>
        </template>
        <!-- 给画布一个默认的宽度和高度 -->
        <div style="position: absolute; top: 2000px; left: 2000px">&nbsp;</div>
      </div>
      <ul
        v-show="showLineMenu"
        :style="linkmenubarStyle"
        class="el-dropdown-menu el-popper linkmenubar"
      >
        <li class="el-dropdown-menu__item">
          <span class="pl-15 el-icon-reading" @click="editActiveElement"
            >编辑</span
          >
        </li>
        <li class="el-dropdown-menu__item">
          <span class="pl-15 el-icon-delete" @click="deleteElement">删除</span>
        </li>
      </ul>
      <!-- 右侧表单 -->
      <div>
        <flow-node-form
          ref="nodeForm"
          @setLineLabel="setLineLabel"
          @repaintEverything="repaintEverything"
        ></flow-node-form>
      </div>
    </div>
    <!-- 流程数据详情 -->
    <flow-info v-if="flowInfoVisible" ref="flowInfo" :data="data"></flow-info>
    <flow-help v-if="flowHelpVisible" ref="flowHelp"></flow-help>
  </div>
</template>

<script>
import { jsPlumb } from "jsplumb";
import lodash from "lodash";
import { easyFlowMixin } from "@/views/erbuilder/components/mixins";
import flowNode from "@/views/erbuilder/components/node";
import nodeMenu from "@/views/erbuilder/components/node_tree";
import FlowInfo from "@/views/erbuilder/components/node_info";
import FlowHelp from "@/views/erbuilder/components/help";
import FlowNodeForm from "@/views/erbuilder/components/node_form";
import { kgBuilderApi } from "@/api";
export default {
  name: "er",
  data() {
    return {
      // jsPlumb 实例
      jsPlumb: null,
      // 控制画布销毁
      easyFlowVisible: true,
      // 控制流程数据显示与隐藏
      flowInfoVisible: false,
      // 是否加载完毕标志位
      loadEasyFlowFinish: false,
      flowHelpVisible: false,
      // 数据
      data: {},
      // 激活的元素、可能是节点、可能是连线
      activeElement: {
        // 可选值 node 、line
        type: undefined,
        // 节点ID
        nodeId: undefined,
        // 连线ID
        sourceId: undefined,
        targetId: undefined,
        label: undefined
      },
      zoom: 0.5,
      showLineMenu: false,
      linkmenubar: {
        top: "-1000px",
        left: "-1000px"
      },
      domainQuery: {
        domainId: 0,
        type: 3,
        pageSize: 10,
        pageIndex: 1
      },
      inputVisible: false,
      inputValue: "",
      domainList: []
    };
  },
  computed: {
    linkmenubarStyle: function() {
      return {
        top: this.linkmenubar.top + "px",
        left: this.linkmenubar.left + "px"
      };
    }
  },
  // 一些基础配置移动该文件中
  mixins: [easyFlowMixin],
  components: {
    flowNode,
    nodeMenu,
    FlowInfo,
    FlowNodeForm,
    FlowHelp
  },
  directives: {
    flowDrag: {
      bind(el, binding, vnode, oldNode) {
        if (!binding) {
          return;
        }
        el.onmousedown = e => {
          if (e.button === 2) {
            // 右键不管
            return;
          }
          //  鼠标按下，计算当前原始距离可视区的高度
          let disX = e.clientX;
          let disY = e.clientY;
          el.style.cursor = "move";

          document.onmousemove = function(e) {
            // 移动时禁止默认事件
            e.preventDefault();
            const left = e.clientX - disX;
            disX = e.clientX;
            el.scrollLeft += -left;

            const top = e.clientY - disY;
            disY = e.clientY;
            el.scrollTop += -top;
          };

          document.onmouseup = function(e) {
            el.style.cursor = "auto";
            document.onmousemove = null;
            document.onmouseup = null;
          };
        };
      }
    }
  },
  mounted() {
    this.initDomain();
    this.jsPlumb = jsPlumb.getInstance();
  },
  methods: {
    showAddDomain() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    deleteEr(domainId) {
      this.$message.success("计划中");
    },
    initDomain() {
      let data = JSON.stringify(this.domainQuery);
      kgBuilderApi.getDomains(data).then(response => {
        if (response.code == 200) {
          this.domainList = response.data.nodeList;
        }
      });
    },
    createDomain() {
      let inputValue = this.inputValue;
      if (inputValue) {
        let data = {
          domain: inputValue,
          type: 3
        };
        kgBuilderApi.createDomain(data).then(response => {
          if (response.code == 200) {
            this.data.domainName = inputValue;
            this.data.domainId = response.data;
            this.domainList.push({ id: response.data, name: inputValue });
          }
        });
      }
      this.inputVisible = false;
      this.inputValue = "";
    },
    saveERdata() {
      let data = JSON.stringify(this.data);
      kgBuilderApi.saveData(data).then(response => {
        if (response.code == 200) {
          if (response.data) {
            this.$message.success("保存成功");
          } else {
            this.$message.error("暂时没有更多数据");
          }
        }
      });
    },
     executeERdata() {
      kgBuilderApi.execute(this.data.domainId).then(response => {
        if (response.code == 200) {
          if (response.data) {
            this.$message.success("保存成功");
          } else {
            this.$message.error("暂时没有更多数据");
          }
        }
      });
    },
    // 初始化数据
    initERData(domainId) {
      kgBuilderApi.getDomainNode(domainId).then(response => {
        if (response.code == 200) {
          if (response.data) {
            this.$nextTick(() => {
              // 默认加载流程A的数据、在这里可以根据具体的业务返回符合流程数据格式的数据即可
              this.dataReload(response.data);
            });
          } else {
            this.$message.error("暂时没有更多数据");
          }
        }
      });
    },
    hiddenLinkMenu(e) {
      //e.preventDefault();
      this.showLineMenu = false;
    },
    jsPlumbInit() {
      this.jsPlumb.ready(() => {
        // 导入默认配置
        this.jsPlumb.importDefaults(this.jsplumbSetting);
        // 会使整个jsPlumb立即重绘。
        this.jsPlumb.setSuspendDrawing(false, true);
        // 初始化节点
        this.loadEasyFlow();
        // 单点击了连接线, https://www.cnblogs.com/ysx215/p/7615677.html
        this.jsPlumb.bind("click", conn => {
          this.activeElement.type = "line";
          this.activeElement.sourceId = conn.sourceId;
          this.activeElement.targetId = conn.targetId;
          this.$refs.nodeForm.lineInit({
            from: conn.sourceId,
            to: conn.targetId,
            label: conn.getLabel()
          });
        });
        // 连线
        this.jsPlumb.bind("connection", evt => {
          let from = evt.source.id;
          let to = evt.target.id;
          if (this.loadEasyFlowFinish) {
            this.data.lineList.push({ from: from, to: to });
          }
        });

        // 删除连线回调
        this.jsPlumb.bind("connectionDetached", evt => {
          this.deleteLine(evt.sourceId, evt.targetId);
        });

        // 改变线的连接节点
        this.jsPlumb.bind("connectionMoved", evt => {
          this.changeLine(evt.originalSourceId, evt.originalTargetId);
        });

        // 连线右击
        this.jsPlumb.bind("contextmenu", (evt, e) => {
          let left = e.clientX;
          let top = e.clientY;
          this.showLineMenu = true;
          //console.log("contextmenu", evt);
          this.linkmenubar.top = top;
          this.linkmenubar.left = left;
          this.activeElement.type = "line";
          this.activeElement.sourceId = evt.sourceId;
          this.activeElement.targetId = evt.targetId;
          this.activeElement.label = evt.getLabel();
          e.preventDefault(); //禁止浏览器右键
          e.stopPropagation(); //阻止冒泡
        });
        // 连线
        this.jsPlumb.bind("beforeDrop", evt => {
          console.log(evt);
          let from = evt.sourceId;
          let to = evt.targetId;
          if (from === to) {
            this.$message.error("节点不支持连接自己");
            return false;
          }
          if (this.hasLine(from, to)) {
            this.$message.error("该关系已存在,不允许重复创建");
            return false;
          }
          if (this.hashOppositeLine(from, to)) {
            this.$message.error("不支持两个节点之间连线回环");
            return false;
          }

          if (this.isSameTableLine(from, to)) {
            //this.$message.error("同一个表之间无需连线");
            return false;
          }
          if (this.isMutilLine(from, to)) {
            this.$message.error("两个表之间只能存在一根连线");
            return false;
          }
          this.$message.success("连接成功");
          return true;
        });
        // beforeDetach
        this.jsPlumb.bind("beforeDetach", evt => {
          console.log("beforeDetach", evt);
        });

        this.jsPlumb.setContainer(this.$refs.efContainer);
      });
    },
    // 加载流程图
    loadEasyFlow() {
      // 初始化节点
      for (var i = 0; i < this.data.nodeList.length; i++) {
        let node = this.data.nodeList[i];
        if (node.viewOnly > 0) {
          this.jsPlumb.draggable(node.nodeKey, {
            containment: "parent",
            stop: function(el, e) {
              //debugger;
              let posArr = el.pos;
              node.left = posArr[0] + "px";
              node.top = posArr[1] + "px";
            }
          });
        }
        let nodeItems = node.items;
        for (var j = 0; j < nodeItems.length; j++) {
          // 设置源点，可以拖出线连接其他节点
          //let obj = lodash.merge(this.jsplumbSourceOptions, {});
          this.jsPlumb.makeSource(nodeItems[j].itemId);
          // // 设置目标点，其他源点拖出的线可以连接该节点
          this.jsPlumb.makeTarget(
            nodeItems[j].itemId,
            this.jsplumbSourceOptions
          );
        }
      }
      // 初始化连线
      for (var m = 0; m < this.data.lineList.length; m++) {
        let line = this.data.lineList[m];
        var connParam = {
          source: line.from,
          target: line.to,
          label: line.label ? line.label : "",
          connector: line.connector ? line.connector : "",
          anchors: line.anchors ? line.anchors : undefined,
          paintStyle: line.paintStyle ? line.paintStyle : undefined
        };
        this.jsPlumb.connect(connParam, this.jsplumbConnectOptions);
      }
      this.$nextTick(function() {
        this.loadEasyFlowFinish = true;
      });
    },
    // 设置连线条件
    setLineLabel(from, to, label) {
      var conn = this.jsPlumb.getConnections({
        source: from,
        target: to
      })[0];
      if (!label || label === "") {
        conn.removeClass("flowLabel");
        conn.addClass("emptyFlowLabel");
      } else {
        conn.addClass("flowLabel");
      }
      conn.setLabel({
        label: label
      });
      this.data.lineList.forEach(function(line) {
        if (line.from === from && line.to === to) {
          line.label = label;
        }
      });
    },
    //编辑激活的连线
    editActiveElement() {
      this.activeElement.type = "line";
      let sourceId = this.activeElement.sourceId;
      let targetId = this.activeElement.targetId;
      let label = this.activeElement.label;
      this.$refs.nodeForm.lineInit({
        from: sourceId,
        to: targetId,
        label: label
      });
    },
    // 删除激活的元素
    deleteElement() {
      if (this.activeElement.type === "node") {
        this.deleteNode(this.activeElement.nodeId);
      } else if (this.activeElement.type === "line") {
        this.$confirm("确定删除所点击的线吗?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            var conn = this.jsPlumb.getConnections({
              source: this.activeElement.sourceId,
              target: this.activeElement.targetId
            })[0];
            this.jsPlumb.deleteConnection(conn);
          })
          .catch(() => {});
      }
    },
    // 删除线
    deleteLine(from, to) {
      this.data.lineList = this.data.lineList.filter(function(line) {
        if (line.from === from && line.to === to) {
          return false;
        }
        return true;
      });
    },
    // 改变连线
    changeLine(oldFrom, oldTo) {
      this.deleteLine(oldFrom, oldTo);
    },
    /**
     * 拖拽结束后添加新的节点
     * @param evt
     * @param nodeMenu 被添加的节点对象
     * @param mousePosition 鼠标拖拽结束的坐标
     */
    addNode(evt, nodeMenu, mousePosition) {
      if (!this.data.domainId) {
        this.$message.warning("请先选择或者创建领域");
        return;
      }
      let screenX = evt.originalEvent.clientX;
      let screenY = evt.originalEvent.clientY;
      let efContainer = this.$refs.efContainer;
      let containerRect = efContainer.getBoundingClientRect();
      let left = screenX;
      let top = screenY;
      // 计算是否拖入到容器中
      if (
        left < containerRect.x ||
        left > containerRect.width + containerRect.x ||
        top < containerRect.y ||
        containerRect.y > containerRect.y + containerRect.height
      ) {
        this.$message.warning("请把节点拖入到中间画布里");
        return;
      }
      left = left - containerRect.x + efContainer.scrollLeft;
      top = top - containerRect.y + efContainer.scrollTop;
      // 居中
      left -= 85;
      top -= 16;
      var nodeId = nodeMenu.table.nodeKey;
      var nodeName = nodeMenu.table.nodeName;
      let nodeExists =
        _.findIndex(this.data.nodeList, function(o) {
          return o.id == nodeId;
        }) > -1;
      if (nodeExists) {
        this.$message.warning("当前表节点[" + nodeName + "]已经存在于画布中");
        return;
      }

      var node = {
        nodeKey: nodeId,
        nodeName: nodeName,
        type: "task",
        left: left + "px",
        top: top + "px",
        ico: nodeMenu.table.ico,
        state: "success",
        viewOnly: 1,
        startNode: 0,
        alia: nodeMenu.table.alia,
        sourceId: nodeMenu.table.sourceId,
        tableId: nodeMenu.table.tableId,
        items: nodeMenu.columns
      };
      /**
       * 这里可以进行业务判断、是否能够添加该节点
       */
      //console.log(this.data)
      this.data.nodeList.push(node);
      this.$nextTick(function() {
        let nodeItems = node.items;
        if (node.viewOnly > 0) {
          this.jsPlumb.draggable(node.nodeKey, {
            containment: "parent",
            stop: function(el) {
              // 拖拽节点结束后的对调
              //console.log("拖拽结束: ", el);
            }
          });
        }
        for (var j = 0; j < nodeItems.length; j++) {
          // 设置源点，可以拖出线连接其他节点
          //let obj = lodash.merge(this.jsplumbSourceOptions, {});
          this.jsPlumb.makeSource(nodeItems[j].itemId);
          // 设置目标点，其他源点拖出的线可以连接该节点
          this.jsPlumb.makeTarget(
            nodeItems[j].itemId,
            this.jsplumbTargetOptions
          );
        }
      });
    },
    /**
     * 删除节点
     * @param nodeId 被删除节点的ID
     */
    deleteNode(nodeId) {
      this.$confirm("确定要删除节点及有关的所有连线" + nodeId + "?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          /**
           * 这里需要进行业务判断，是否可以删除
           */
          this.data.nodeList = this.data.nodeList.filter(function(node) {
            if (node.nodeKey === nodeId) {
              // 伪删除，将节点隐藏，否则会导致位置错位
              // node.show = false
              return false;
            }
            return true;
          });
          //删除和该节点有关的所有连线
          let deleteArr = [];
          this.data.lineList = this.data.lineList.filter(function(line) {
            let fromArr = line.from.split("-");
            let fromTablePrex = fromArr[0] + "-" + fromArr[1];
            let toArr = line.to.split("-");
            let toTablePrex = toArr[0] + "-" + toArr[1];
            if (fromTablePrex === nodeId) {
              deleteArr.push({ sourceId: line.from, targetId: line.to });
              return false;
            }
            if (toTablePrex === nodeId) {
              deleteArr.push({ sourceId: line.to, targetId: line.from });
              return false;
            }
            return true;
          });
          this.$nextTick(function() {
            for (let i = 0; i < deleteArr.length; i++) {
              var conn = this.jsPlumb.getConnections({
                source: deleteArr[i].sourceId,
                target: deleteArr[i].targetId
              })[0];
              this.jsPlumb.deleteConnection(conn);
            }
            this.jsPlumb.removeAllEndpoints(nodeId);
          });
        })
        .catch(() => {});
      return true;
    },
    clickNode(node) {
      let nodeId = node.nodeKey;
      this.activeElement.type = "node";
      this.activeElement.nodeId = nodeId;
      this.$refs.nodeForm.nodeInit(this.data, node);
    },
    // 是否具有该线
    hasLine(from, to) {
      for (var i = 0; i < this.data.lineList.length; i++) {
        var line = this.data.lineList[i];
        if (line.from === from && line.to === to) {
          return true;
        }
      }
      return false;
    },
    // 是否含有相反的线
    hashOppositeLine(from, to) {
      return this.hasLine(to, from);
    },
    // 是否是同表之间列连线
    isSameTableLine(from, to) {
      let fromArr = from.split("-");
      let fromTablePrex = fromArr[0] + "-" + fromArr[1];
      let toArr = to.split("-");
      let toTablePrex = toArr[0] + "-" + toArr[1];
      if (fromTablePrex === toTablePrex) {
        return true;
      }
      return false;
    },
    // 两个表间只能连一根线
    isMutilLine(from, to) {
      let fromArr = from.split("-");
      let fromTablePrex = fromArr[0] + "-" + fromArr[1];
      let toArr = to.split("-");
      let toTablePrex = toArr[0] + "-" + toArr[1];
      let newLine = fromTablePrex + "_" + toTablePrex;
      let newLine2 = toTablePrex + "_" + fromTablePrex;
      for (var i = 0; i < this.data.lineList.length; i++) {
        var line = this.data.lineList[i];
        let existFromArr = line.from.split("-");
        let existFromTablePrex = existFromArr[0] + "-" + existFromArr[1];
        let existToArr = line.to.split("-");
        let existToTablePrex = existToArr[0] + "-" + existToArr[1];
        let oldLine = existFromTablePrex + "_" + existToTablePrex;
        let oldLine2 = existToTablePrex + "_" + existFromTablePrex;
        if (
          oldLine === newLine ||
          oldLine === newLine2 ||
          oldLine2 === newLine ||
          oldLine2 === newLine2
        ) {
          return true;
        }
      }
      return false;
    },
    nodeRightMenu(nodeId, evt) {
      this.menu.show = true;
      this.menu.curNodeId = nodeId;
      this.menu.left = evt.x + "px";
      this.menu.top = evt.y + "px";
    },
    repaintEverything() {
      this.jsPlumb.repaint();
    },
    // 流程数据信息
    dataInfo() {
      this.flowInfoVisible = true;
      this.$nextTick(function() {
        this.$refs.flowInfo.init();
      });
    },
    // 加载流程图
    dataReload(data) {
      this.easyFlowVisible = false;
      this.data.nodeList = [];
      this.data.lineList = [];
      this.$nextTick(() => {
        data = lodash.cloneDeep(data);
        this.easyFlowVisible = true;
        this.data = data;
        this.$nextTick(() => {
          this.jsPlumb = jsPlumb.getInstance();
          this.$nextTick(() => {
            this.jsPlumbInit();
          });
        });
      });
    },
    // 刷新数据
    refreshData() {
      //this.dataReload(getDataB());
    },
    zoomAdd() {
      if (this.zoom >= 1) {
        return;
      }
      this.zoom = this.zoom + 0.1;
      this.$refs.efContainer.style.transform = `scale(${this.zoom})`;
      this.jsPlumb.setZoom(this.zoom);
    },
    zoomSub() {
      if (this.zoom <= 0) {
        return;
      }
      this.zoom = this.zoom - 0.1;
      this.$refs.efContainer.style.transform = `scale(${this.zoom})`;
      this.jsPlumb.setZoom(this.zoom);
    },
    // 下载数据
    downloadData() {
      this.$confirm("确定要下载该流程数据吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          var datastr =
            "data:text/json;charset=utf-8," +
            encodeURIComponent(JSON.stringify(this.data, null, "\t"));
          var downloadAnchorNode = document.createElement("a");
          downloadAnchorNode.setAttribute("href", datastr);
          downloadAnchorNode.setAttribute("download", "data.json");
          downloadAnchorNode.click();
          downloadAnchorNode.remove();
          this.$message.success("正在下载中,请稍后...");
        })
        .catch(() => {});
    },
    openHelp() {
      this.flowHelpVisible = true;
      this.$nextTick(function() {
        this.$refs.flowHelp.init();
      });
    }
  }
};
</script>
<style>
/*画布容器*/
#efContainer {
  position: relative;
  overflow: scroll;
  flex: 1;
  z-index: 0;
}

/*顶部工具栏*/
.ef-tooltar {
  padding-left: 10px;
  box-sizing: border-box;
  height: 42px;
  line-height: 42px;
  z-index: 3;
  border-bottom: 1px solid #dadce0;
  text-align: left;
}

.jtk-overlay {
  cursor: pointer;
  color: #4a4a4a;
}

/* 连线中的label 样式*/
.jtk-overlay.flowLabel:not(.aLabel) {
  padding: 4px 10px;
  background-color: white;
  color: #565758 !important;
  border: 1px solid #e0e3e7;
  border-radius: 5px;
}
.ef-dot {
  background-color: #1879ff;
  border-radius: 10px;
}

.ef-dot-hover {
  background-color: red;
}

.ef-rectangle {
  background-color: #1879ff;
}

.ef-rectangle-hover {
  background-color: red;
}

.ef-drop-hover {
  border: 1px dashed #1879ff;
}
.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>
