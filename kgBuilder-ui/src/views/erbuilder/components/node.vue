<template>
  <div
    ref="node"
    :style="nodeContainerStyle"
    :class="nodeContainerClass"
  >
    <!-- 最左侧的那条竖线 -->
    <div class="ef-node-left"></div>
    <!-- 节点类型的图标 -->
    <div class="ef-node-left-ico flow-node-drag">
      <i :class="nodeIcoClass"></i>
    </div>
    <!-- 节点名称 -->
    <div class="ef-node-text" :show-overflow-tooltip="true">
      <div class="table node">
        <div class="name">
          <span>{{ node.nodeName }}</span>
          <i class="node-ico-edit el-icon-edit-outline" @click="clickNode"></i>
          <i class="node-ico-delete el-icon-delete" @click="deleteNode"></i>
        </div>
        <div class="table-columns">
          <ul>
            <li
              class="flow-node-drag node"
              :show-overflow-tooltip="true"
              v-for="it in node.items"
              :id="it.itemId"
            >
              <span class="column-span">{{ it.itemName }}</span>
              <el-divider direction="vertical"></el-divider>
              <span class="column-span">{{ it.itemType }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <!-- 节点状态图标 -->
    <div class="ef-node-right-ico">
      <i
        class="el-icon-circle-check el-node-state-success"
        v-show="node.state === 'success'"
      ></i>
      <i
        class="el-icon-circle-close el-node-state-error"
        v-show="node.state === 'error'"
      ></i>
      <i
        class="el-icon-warning-outline el-node-state-warning"
        v-show="node.state === 'warning'"
      ></i>
      <i
        class="el-icon-loading el-node-state-running"
        v-show="node.state === 'running'"
      ></i>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    node: Object,
    activeElement: Object
  },
  data() {
    return {};
  },
  computed: {
    nodeContainerClass() {
      return {
        "ef-node-container": true,
        "ef-node-active":
          this.activeElement.type === "node"
            ? this.activeElement.nodeId === this.node.nodeKey
            : false
      };
    },
    // 节点容器样式
    nodeContainerStyle() {
      return {
        top: this.node.top,
        left: this.node.left
      };
    },
    nodeIcoClass() {
      var nodeIcoClass = {};
      nodeIcoClass[this.node.ico] = true;
      // 添加该class可以推拽连线出来，viewOnly 可以控制节点是否运行编辑
      nodeIcoClass["flow-node-drag"] = this.node.viewOnly>0;
      return nodeIcoClass;
    }
  },
  methods: {
    // 点击节点
    clickNode() {
      this.$emit("clickNode", this.node);
    },
    deleteNode(e) {
      this.$emit("deleteNode", this.node.nodeKey);
      e.stopPropagation(); //阻止冒泡
    }
  }
};
</script>
<style>
.table-columns ul {
  padding: 0px;
}
.table-columns li {
  list-style: none;
  border: 1px dashed lightblue;
  margin: 1px;
  z-index: 10;
  text-align: left;
  padding: 0 5px;
}
.column-span {
  margin: 0 5px;
}
.node-ico-edit {
  margin-right: 30px;
  line-height: 32px;
  position: absolute;
  right: 25px;
  color: #153df0;
  cursor: pointer;
}
.node-ico-delete {
  margin-right: 30px;
  line-height: 32px;
  position: absolute;
  right: 5px;
  color: #f56c6c;
  cursor: pointer;
}
/*节点的最外层容器*/
.ef-node-container {
  position: absolute;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  width: 230px;
  height: auto;
  border: 1px solid #e0e3e7;
  border-radius: 5px;
  background-color: #fff;
  z-index: -1;
}
.ef-node-container:hover {
  /* 设置移动样式*/
  cursor: move;
  background-color: #f0f7ff;
  /*box-shadow: #1879FF 0px 0px 12px 0px;*/
  background-color: #f0f7ff;
  border: 1px dashed #1879ff;
}

/*节点激活样式*/
.ef-node-active {
  background-color: #f0f7ff;
  /*box-shadow: #1879FF 0px 0px 12px 0px;*/
  background-color: #f0f7ff;
  border: 1px solid #1879ff;
}

/*节点左侧的竖线*/
.ef-node-left {
  width: 4px;
  background-color: #1879ff;
  border-radius: 4px 0 0 4px;
}

/*节点左侧的图标*/
.ef-node-left-ico {
  line-height: 32px;
  margin-left: 8px;
  display: block;
  position: absolute;
}

.ef-node-left-ico:hover {
  /* 设置拖拽的样式 */
  /* cursor: crosshair; */
}

/*节点显示的文字*/
.ef-node-text {
  color: #565758;
  font-size: 12px;
  line-height: 32px;
  margin-left: 8px;
  width: 210px;
  /* 设置超出宽度文本显示方式*/
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center;
}

/*节点右侧的图标*/
.ef-node-right-ico {
  line-height: 32px;
  position: absolute;
  right: 5px;
  color: #84cf65;
  cursor: default;
}

/*节点的几种状态样式*/
.el-node-state-success {
  line-height: 32px;
  position: absolute;
  right: 5px;
  color: #84cf65;
  cursor: default;
}

.el-node-state-error {
  line-height: 32px;
  position: absolute;
  right: 5px;
  color: #f56c6c;
  cursor: default;
}

.el-node-state-warning {
  line-height: 32px;
  position: absolute;
  right: 5px;
  color: #e6a23c;
  cursor: default;
}

.el-node-state-running {
  line-height: 32px;
  position: absolute;
  right: 5px;
  color: #84cf65;
  cursor: default;
}
</style>
