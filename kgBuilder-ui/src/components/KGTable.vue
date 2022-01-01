<template>
  <div class="kg-table" ref="kgTable">
    <div
      ref="tableOperate"
      v-show="buttonGroup"
      class="table-operate button-group"
    >
      <slot name="button-group"></slot>
    </div>
    <div class="pagi-table" ref="pagiTable">
      <p
        class="notice mt10"
        v-show="
          multipleSelection.length > 0 && config.selection && !config.notShowNum
        "
      >

        已选择<span>{{ multipleSelection.length }}</span
        >项
      </p>
      <el-table
        ref="table"
        stripe
        size="medium"
        v-loading="loading"
        class="tableStyle"
        :data="pageObj.list"
        style="width: 100%"
        v-bind="tableBind"
        :row-key="config.rowKey || 'id'"
        @selection-change="onSelectionChange"
        @select="onSelectChange"
        @select-all="onSelectAllChange"
      >
        <el-table-column v-if="config.selection" type="selection" width="55">
        </el-table-column>

        <template v-for="item in columns">
          <!-- 自定义 header -->
          <el-table-column
            :key="item.prop"
            v-if="item.type === 'slotHeader'"
            v-bind="item"
            :show-overflow-tooltip="item.tooltip"
          >
            <template slot="header">
              <slot :name="'header' + item.prop">
                {{ item.label }}
              </slot>
            </template>
            <template slot-scope="scope">
              <span>{{ scope.row[item.prop] }}</span>
            </template>
          </el-table-column>
          <!-- slot渲染 -->
          <el-table-column
            :key="item.prop"
            v-else-if="item.type === 'slot'"
            :label="item.label"
            :prop="item.prop"
            :width="item.width"
          >
            <template slot-scope="scope">
              <slot :name="item.slotName" :row="scope.row"></slot>
            </template>
          </el-table-column>
          <!-- 正常渲染 -->
          <el-table-column
            v-else
            :key="item.prop"
            :label="item.label"
            :prop="item.prop"
            :align="item.align ? item.align : 'left'"
            :width="item.width"
            :show-overflow-tooltip="true"
          >
          </el-table-column>
        </template>

        <el-table-column
          v-if="operation && operation.label"
          :label="operation.label"
          :width="operation.width"
        >
          <template slot-scope="scope">
            <span v-if="operation.type !== 'link'">
              <template v-for="(item, index) in operation.options">
                <el-button
                  :key="'operation' + index"
                  :icon="item.icon"
                  :size="item.size"
                  :type="item.type"
                  :disabled="
                    item.disabled &&
                    scope.row[item.disabled.field] === item.disabled.value
                  "
                  @click="handleClick(item.method, scope.row)"
                  v-if="
                    !item.hide ||
                    !item.hide.value.includes(scope.row[item.hide.field])
                  "
                  ><svg-icon :icon="item.icon"></svg-icon
                  >{{ item.label }}</el-button
                >
              </template>
            </span>
            <span
              v-else-if="operation.doubleTitle && operation.type === 'link'"
            >
              <el-link
                class="btn-a"
                v-for="(item, index) in operation.options"
                :key="'operation' + index"
                v-bind="item"
                @click="handleClick(item.method, scope.row)"
              >
                <svg-icon :icon="item.icon"></svg-icon>
                {{
                  scope.row[item.flag] === item.realValue
                    ? item.values.real
                    : item.values.unde
                }}</el-link
              >
            </span>
            <span v-else>
              <el-link
                class="btn-a"
                v-for="(item, index) in operation.options"
                :key="'operation' + index"
                v-bind="item"
                @click="handleClick(item.method, scope.row)"
              >
                <svg-icon :icon="item.icon"></svg-icon>
                {{ item.label }}</el-link
              >
            </span>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        v-if="config.pagination"
        :total="pageObj.totalCount"
        layout="sizes, prev, pager, next, jumper"
        :page.sync="pageObj.currentPage"
        :totalPage="pageObj.totalPage"
        :limit.sync="pageObj.pageSize"
        @pagination="getPage"
      ></Pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: 'kgTable',
  props: {
    buttonGroup: {
      type: Boolean,
      default: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    columns: {
      type: Array,
      required: true
    },
    pageObj: {
      type: Object,
      required: true,
      default: () => ({
        currentPage: 1,
        pageSize: 15,
        list: [],
        totalCount: 0
      })
    },
    operation: {
      type: Object
    },
    config: {
      type: Object,
      default: () => ({
        pagination: true, // 是否显示分页
        selection: true, // 多选
        rowKey: 'id', // table row-key配置参数
        notUseMaxHeight: false
      })
    }
  },
  components: {},
  data() {
    return {
      multipleSelection: [],
      maxHeight: 0
    }
  },
  computed: {
    tableBind() {
      const obj = {}
      if (!this.config.notUseMaxHeight) {
        obj['max-height'] = this.maxHeight
      }
      return obj
    }
  },
  watch: {
    'pageObj.list': {
      deep: true,
      handler() {
        this.multipleSelection.length = 0
      }
    }
  },
  created() {},
  mounted() {
    this.$nextTick(function () {
      const operateHeight = this.$refs.tableOperate.clientHeight
      this.maxHeight =
        this.$refs.kgTable.clientHeight -
        108 -
        operateHeight -
        10 +
        (this.config.pagination ? 0 : 64) +
        'px'
    })
  },
  methods: {
    onSelectionChange(selection) {
      this.multipleSelection = selection
      this.$emit('selection-change', this.multipleSelection)
    },
    onSelectChange(selection, row) {
      this.$emit('select', selection, row)
    },
    onSelectAllChange(selection) {
      this.$emit('select-all', selection)
    },
    getPage({ page, limit }) {
      this.$emit('pagination', { page, limit })
    },
    handleClick(method, row) {
      this.$emit('handleClick', { method, row })
    },
    toggleRowSelection(row, selected) {
      this.$refs.table.toggleRowSelection(row, selected)
    }
  },
  beforeDestroy() {}
}
</script>

<style lang="less" scoped>
.kg-table {
  height: 100%;
  .btn-a {
    font-size: 14px;
    padding: 3px 10px;
    text-decoration: none;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    .icon {
      margin-right: 3px;
      vertical-align: -3px;
    }
    &:hover {
      color: #105aac;
      background-color: #f3f5f7;
      border-color: #d3deeb;
    }
    &:focus {
      color: #fff;
      background-color: #1264bf;
      border-color: #1264bf;
    }
  }
  /deep/ .el-link.is-underline:hover:after {
    border-bottom: none;
  }

}
</style>
