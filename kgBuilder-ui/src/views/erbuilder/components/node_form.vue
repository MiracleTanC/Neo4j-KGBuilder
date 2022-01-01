<template>
  <el-drawer
    ref="drawer"
    title="编辑"
    :with-header="false"
    :visible.sync="drawer"
    :direction="direction"
    :append-to-body="true"
  >
    <div class="ef-node-form">
      <div class="ef-node-form-header">
        编辑
      </div>
      <div class="ef-node-form-body">
        <el-form
          :model="node"
          ref="dataForm"
          label-width="80px"
          v-show="type === 'node'"
        >
          <el-form-item label="类型">
            <el-input v-model="node.type" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="名称">
            <el-input v-model="node.nodeName"></el-input>
          </el-form-item>
          <el-form-item label="left坐标">
            <el-input v-model="node.left" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="top坐标">
            <el-input v-model="node.top" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="ico图标">
            <el-input v-model="node.ico"></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="node.state" placeholder="请选择">
              <el-option
                v-for="item in stateList"
                :key="item.state"
                :label="item.label"
                :value="item.state"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="字段">
            <el-checkbox
              :indeterminate="isIndeterminate"
              v-model="checkAll"
              @change="handleCheckAllChange"
              >全选
            </el-checkbox>
            <el-checkbox-group
              v-model="checkedColumns"
              @change="handlecheckedColumnsChange"
            >
              <kg-table
                :columns="insidetableColumns"
                :pageObj="pageObj"
                :config="tableConfig"
              >
                <template v-for="item in insidetableColumns">
                  <el-tooltip
                    :slot="'header' + item.prop"
                    class="item"
                    effect="dark"
                    :content="item.label"
                    placement="top"
                    :key="item.prop"
                  >
                    <el-checkbox :label="item.label">
                      {{ item.label }}
                    </el-checkbox>
                  </el-tooltip>
                </template>
              </kg-table>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-close" @click="drawer = false"
              >重置</el-button
            >
            <el-button type="primary" icon="el-icon-check" @click="save"
              >保存</el-button
            >
          </el-form-item>
        </el-form>

        <el-form
          :model="line"
          ref="dataForm"
          label-width="80px"
          v-show="type === 'line'"
        >
          <el-form-item label="条件">
            <el-input v-model="line.label"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-close" @click="drawer = false"
              >重置</el-button
            >
            <el-button type="primary" icon="el-icon-check" @click="saveLine"
              >保存</el-button
            >
          </el-form-item>
        </el-form>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import { cloneDeep } from "lodash";
import { datasourceApi } from "@/api";
import kgTable from "@/components/KGTable.vue";
export default {
  components: {
    kgTable
  },
  computed: {
    // 内部表格
    insidetableColumns() {
      const columns = [];
      this.columns &&
        this.columns.forEach(item => {
          columns.push({
            prop: item.dataColumnName,
            type: "slotHeader",
            tooltip: true,
            label: item.dataColumnName,
            minWidth: 120
          });
        });
      return columns;
    }
  },
  data() {
    return {
      visible: true,
      // node 或 line
      type: "node",
      checkModel: false,
      node: {},
      line: {},
      data: {},
      drawer: false,
      direction: "rtl",
      checkAll: false,
      isIndeterminate: true,
      columns: [],
      checkedColumns: [],
      stateList: [
        {
          state: "success",
          label: "成功"
        },
        {
          state: "warning",
          label: "警告"
        },
        {
          state: "error",
          label: "错误"
        },
        {
          state: "running",
          label: "运行中"
        }
      ],
      // 内部表格
      tableConfig: {
        pagination: false, // 是否显示分页
        selection: false, // 多选
        rowKey: "id", // table row-key配置参数
        notUseMaxHeight: true
      },
      pageObj: {
        currentPage: 1,
        pageSize: 15,
        list: [],
        totalCount: 0
      }
    };
  },
  methods: {
    handleCheckAllChange(val) {
      let arr = this.columns.map(function(col) {
        return col.dataColumnName;
      });
      this.checkedColumns = val ? arr : [];
      this.isIndeterminate = false;
    },
    handlecheckedColumnsChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.columns.length;
      this.isIndeterminate =
        checkedCount > 0 && checkedCount < this.columns.length;
      this.checkedColumns = value;
    },
    changeColumns() {
      this.data.nodeList.filter(node => {
        if (node.nodeKey === this.node.nodeKey) {
          node.nodeName = this.node.nodeName;
          node.left = this.node.left;
          node.top = this.node.top;
          node.ico = this.node.ico;
          node.state = this.node.state;
          let nodeItems = this.columns.filter(c => {
            if (this.checkedColumns.indexOf(c.dataColumnName) > -1) {
              return true;
            }
            return false;
          });
          node.items = [];
          for (var j = 0; j < nodeItems.length; j++) {
            let tr = {
              columnId: nodeItems[j].dataColumnId,
              ico: "el-icon-film",
              isPrimary: 0,
              itemId: node.id + "-" + nodeItems[j].dataColumnId,
              itemCode: nodeItems[j].dataColumnName,
              itemName: nodeItems[j].dataColumnName,
              itemType: nodeItems[j].dataColumnType
            };
            node.items.push(tr);
          }
        }
      });
    },
    /**
     * 表单修改
     * @param data
     * @param node
     */
    nodeInit(data, node) {
      this.drawer = true;
      this.type = "node";
      this.data = data;
      this.node = cloneDeep(node);
      let arr=node.items.map(function(col) {
        return col.itemCode;
      });
      this.checkedColumns = arr ? arr : [];
      let param = {
        dataSourceId: node.sourceId,
        dataTableName: node.nodeName,
        currentPage: 1,
        pageSize: 5
      };
      datasourceApi.getPreviewData(param).then(response => {
        if (response.code == 200) {
          this.pageObj.list = response.data.data;
          this.columns = response.data.heads;
        }
      });
    },
    lineInit(line) {
      this.drawer = true;
      this.type = "line";
      this.line = line;
    },
    // 修改连线
    saveLine() {
      this.drawer = false;
      this.$emit("setLineLabel", this.line.from, this.line.to, this.line.label);
    },
    save() {
      this.drawer = false;
      this.changeColumns();
    }
  }
};
</script>

<style>
.el-node-form-tag {
  position: absolute;
  top: 50%;
  margin-left: -15px;
  height: 40px;
  width: 15px;
  background-color: #fbfbfb;
  border: 1px solid rgb(220, 227, 232);
  border-right: none;
  z-index: 0;
}
/*node-form*/
.ef-node-form-header {
  height: 32px;
  border-top: 1px solid #dce3e8;
  border-bottom: 1px solid #dce3e8;
  background: #f1f3f4;
  color: #000;
  line-height: 32px;
  padding-left: 12px;
  font-size: 14px;
  text-align: left;
}

.ef-node-form-body {
  margin-top: 10px;
  padding-right: 10px;
  padding-bottom: 20px;
}
.ef-node-form-body {
  text-align: left;
}
</style>
