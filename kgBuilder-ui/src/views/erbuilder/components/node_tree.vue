<template>
  <div class="flow-menu" ref="tool">
    <div v-for="menu in menuList" :key="menu.id">
      <span class="ef-node-pmenu" @click="menuClick(menu)"
        ><i
          :class="{
            'el-icon-caret-bottom': menu.open,
            'el-icon-caret-right': !menu.open
          }"
        ></i
        >&nbsp;{{ menu.name }}</span
      >
      <ul v-show="menu.open" class="ef-node-menu-ul">
        <draggable
          @end="end"
          @start="move"
          v-model="menu.children"
          :options="draggableOptions"
        >
          <li
            v-for="subMenu in menu.children"
            class="ef-node-menu-li"
            :id="subMenu.id"
            :key="subMenu.id"
            :type="subMenu.type"
          >
            <i :class="subMenu.ico"></i> {{ subMenu.name }}
          </li>
        </draggable>
      </ul>
    </div>
  </div>
</template>
<script>
import draggable from "vuedraggable";
import { datasourceApi } from "@/api";
var mousePosition = {
  left: -1,
  top: -1
};

export default {
  data() {
    return {
      activeNames: "1",
      // draggable配置参数参考 https://www.cnblogs.com/weixin186/p/10108679.html
      draggableOptions: {
        preventOnFilter: false,
        sort: true,
        disabled: false,
        ghostClass: "tt",
        //

        scroll: true,
        animation: "300",
        // 不使用H5原生的配置
        forceFallback: true
        // 拖拽的时候样式
        // fallbackClass: 'flow-node-draggable'
      },
      // 默认打开的左侧菜单的id
      defaultOpeneds: ["1", "2"],
      menuList: [
        {
          id: "1",
          type: "group",
          name: "开始节点",
          ico: "el-icon-video-play",
          open: true,
          children: [
            {
              id: "11",
              type: "timer",
              name: "数据接入",
              ico: "el-icon-time",
              // 自定义覆盖样式
              style: {}
            },
            {
              id: "12",
              type: "task",
              name: "接口调用",
              ico: "el-icon-odometer",
              // 自定义覆盖样式
              style: {}
            }
          ]
        }
      ],
      datasourceList: [],
      nodeMenu: {
        table: {},
        columns: []
      }
    };
  },
  components: {
    draggable
  },
  created() {
    /**
     * 以下是为了解决在火狐浏览器上推拽时弹出tab页到搜索问题
     * @param event
     */
    if (this.isFirefox()) {
      document.body.ondrop = function(event) {
        // 解决火狐浏览器无法获取鼠标拖拽结束的坐标问题
        mousePosition.left = event.layerX;
        mousePosition.top = event.clientY - 50;
        event.preventDefault();
        event.stopPropagation();
      };
    }
    //加载数据源列表
    this.getDatasourceList();
  },
  methods: {
    menuClick(menu) {
      menu.open = !menu.open;
      if (menu.open && menu.children.length == 0) {
        this.getDataTableList(menu.id);
      }
    },
    //加载数据源
    getDatasourceList() {
      datasourceApi.getDatasource().then(response => {
        this.datasourceList = response.data;
        this.menuList = [];
        for (let i = 0; i < this.datasourceList.length; i++) {
          let data = this.datasourceList[i];
          let item = {
            id: data.datasourceId,
            type: "group",
            name: data.datasourceName,
            ico: "el-icon-video-play",
            open: false,
            children: []
          };
          this.menuList.push(item);
        }
      });
    },
    //加载数据表
    getDataTableList(sourceId) {
      datasourceApi.getTableInfo(sourceId).then(response => {
        for (let i = 0; i < this.menuList.length; i++) {
          if (this.menuList[i].id == sourceId) {
            this.menuList[i].children = [];
            for (let j = 0; j < response.data.length; j++) {
              let tableItem = response.data[j];
              let submitItem = {
                id: tableItem.dataTableId,
                type: "timer",
                name: tableItem.dataTableName,
                ico: "el-icon-menu",
                // 自定义覆盖样式
                style: {}
              };
              this.menuList[i].children.push(submitItem);
            }
          }
        }
      });
    },
    //获取数据表字段
    getDataTableInfo(tableId, evt) {
      datasourceApi.getDataTableInfo(tableId).then(response => {
        let columns = [];
        let tableItem = response.data;
        let item = {
          nodeKey: "table-" + tableItem.table.dataTableId,
          tableId: tableItem.table.dataTableId,
          type: "timer",
          nodeName: tableItem.table.dataTableName,
          alia: tableItem.table.dataTableAlia,
          sourceId: tableItem.table.datasourceId,
          startNode:0,
          ico: "el-icon-menu",
          // 自定义覆盖样式
          style: {},
          state: "success",
          viewOnly: 1 //0 不可拖拽
        };
        for (let i = 0; i < tableItem.column.length; i++) {
          let columnItem = tableItem.column[i];
          let data = {
            itemId: item.nodeKey + "-" + columnItem.dataColumnId,
            columnId: columnItem.dataColumnId,
            itemCode: columnItem.dataColumnName,
            itemName:
              columnItem.dataColumnName + "[" + columnItem.dataColumnAlia + "]",
            itemType: columnItem.dataColumnType,
            isPrimary: columnItem.isPrimary,
            ico: "el-icon-film"
          };
          columns.push(data);
        }
        this.nodeMenu.table = item;
        this.nodeMenu.columns = columns;
        this.$emit("addNode", evt, this.nodeMenu, mousePosition);
      });
    },
    // 拖拽开始时触发
    move(evt) {},
    // 拖拽结束时触发
    end(evt, e) {
      let tableId = evt.item.attributes.id.nodeValue;
      this.getDataTableInfo(tableId, evt);
    },
    // 是否是火狐浏览器
    isFirefox() {
      var userAgent = navigator.userAgent;
      if (userAgent.indexOf("Firefox") > -1) {
        return true;
      }
      return false;
    }
  }
};
</script>
<style>
/*节点菜单*/
.ef-node-pmenu {
  cursor: pointer;
  height: 32px;
  line-height: 32px;
  width: 255px;
  display: block;
  font-weight: bold;
  color: #4a4a4a;
  text-align: left;
  padding-left: 5px;
}

.ef-node-pmenu:hover {
  background-color: #e0e0e0;
}

.ef-node-menu-li {
  color: #565758;
  width: 180px;
  border: 1px dashed #e0e3e7;
  margin: 5px 0 5px 0;
  padding: 5px;
  border-radius: 5px;
  padding-left: 8px;
  text-align: left;
}

.ef-node-menu-li:hover {
  /* 设置移动样式*/
  cursor: move;
  background-color: #f0f7ff;
  border: 1px dashed #1879ff;
  border-left: 4px solid #1879ff;
  padding-left: 5px;
}

.ef-node-menu-ul {
  list-style: none;
  padding-left: 20px;
}
</style>
