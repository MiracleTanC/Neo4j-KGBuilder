<template>
  <div class="mind-box">
    <!-- 左侧 -->
    <el-scrollbar class="mind-l">
      <div class="ml-m">
        <h2 class="ml-ht">数据源列表</h2>
        <div class="ml-a-box">
          <div class="block">
            <el-button
              @click="addWStatus = true"
              size="mini"
              type="primary"
              plain
              >新增</el-button
            >
            <el-aside
              v-if="dataSourceList.length > 0"

            >
              <el-menu :router="false">
                <el-submenu
                  :index="dIndex + ''"
                  :key="dIndex + ''"
                  v-for="(d, dIndex) in dataSourceList"
                  @click.native="clickDataSource(d, dIndex)"
                >
                  <template slot="title"
                    ><i class="el-icon-message"></i>{{ d.datasourceType
                    }}{{ d.datasourceAlia }}</template
                  >
                  <el-menu-item-group
                    v-if="d.tableList.length > 0"
                    v-for="(t, tIndex) in d.tableList"
                    :key="tIndex + ''"
                    @click.native="getFields(t.dataTableId)"
                  >
                    <!-- <template slot="title">分组一</template> -->
                    <el-menu-item :index="dIndex + '_' + tIndex"
                      >[{{ t.dataTableName }}]
                      <el-dropdown @command="getTableOperate" class="ds-down">
                        <span class="el-dropdown-link">...</span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item
                            :command="{ action: 'viewData', db: d, tb: t }"
                            >预览数据</el-dropdown-item
                          >
                          <el-dropdown-item
                            :command="{
                              action: 'rename',
                              t: t,
                              tIndex: tIndex,
                              dIndex: dIndex
                            }"
                            >重命名</el-dropdown-item
                          >
                          <el-dropdown-item
                            :command="{ action: 'delete', db: d, tb: t }"
                            >删除</el-dropdown-item
                          >
                        </el-dropdown-menu>
                      </el-dropdown>
                    </el-menu-item>
                  </el-menu-item-group>
                </el-submenu>
              </el-menu>
            </el-aside>
          </div>
        </div>
      </div>
    </el-scrollbar>
    <div class="mind-con">
      <!-- 头部 -->
      <div class="mind-top clearfix"></div>
      <el-scrollbar class="mind-cen">
        <el-dialog title="数据源编辑" :visible.sync="addWStatus">
          <el-form :model="form">
            <el-form-item label="数据库类型">
              <el-select
                v-model="form.dbType"
                placeholder="请选择数据库"
                @change="dbTypeChange"
              >
                <el-option
                  v-for="t in dataTypeList"
                  :key="t.type"
                  :label="t.type"
                  :value="t"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="ip及端口">
              <el-input v-model="form.ipAndPort"></el-input>
            </el-form-item>
            <el-form-item label="数据库别名">
              <el-input v-model="form.dbName"></el-input>
            </el-form-item>
            <el-form-item label="数据库名称">
              <el-input v-model="form.dbCode"></el-input>
            </el-form-item>
            <el-form-item label="用户名">
              <el-input v-model="form.dbUserName"></el-input>
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="form.dbPassWord"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <!--<el-button type="primary" @click="testDataSource">测试连接</el-button>-->
            <el-button type="primary" @click="addDataSource">确 定</el-button>
            <el-button @click="addWStatus = false">取 消</el-button>
          </div>
        </el-dialog>
        <el-dialog title="选择数据表" :visible.sync="selectTableStatus">
          <el-form :model="tableForm">
            <el-checkbox
              :indeterminate="isIndeterminate"
              v-model="checkAll"
              @change="handleCheckAllChange"
              >全选</el-checkbox
            >
            <div style="margin: 15px 0;"></div>
            <el-checkbox-group
              v-model="tableForm.dataTables"
              @change="handleCheckedChange"
            >
              <el-checkbox
                v-for="table in dataTableList"
                :label="table"
                :key="table"
                >{{ table }}</el-checkbox
              >
            </el-checkbox-group>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="saveDataTable">确 定</el-button>
            <el-button @click="selectTableStatus = false">取 消</el-button>
          </div>
        </el-dialog>
        <el-table
          v-show="dataFieldList.length > 0"
          :data="dataFieldList"
          stripe
          style="width: 100%"
        >
          <el-table-column prop="dataColumnName" label="列名" width="180">
          </el-table-column>
          <el-table-column prop="dataColumnAlia" label="列别名" width="180">
          </el-table-column>
          <el-table-column prop="dataColumnType" label="类型">
          </el-table-column>
          <el-table-column prop="isPrimary" label="是否主键">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.isPrimary == 1" type="success">是</el-tag>
              <el-tag v-else type="info">否</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div v-show="pageRecord.beanList.length > 0" class="ds-table-wrap">
          <div class="line-choose">
            <span>共 {{ pageRecord.totalCount }}条结果</span>
          </div>
          <el-table
            style="width: 100%"
            v-loading="listLoading"
            element-loading-text="给我一点时间"
            fit
            highlight-current-row
            :data="pageRecord.beanList"
            class="x-table"
          >
            <el-table-column
              width="200"
              :label="value"
              v-for="(value, key) in pageRecord.headerFields"
              :key="key"
            >
              <template slot-scope="scope">
                <el-popover
                  placement="top"
                  :title="value"
                  popper-class="table-item-popover"
                  trigger="hover"
                >
                  <div>{{ scope.row[value] }}</div>
                  <span slot="reference" class="table-item-span"
                    >{{ scope.row[value] }}
                  </span>
                </el-popover>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            @size-change="handleSizeChange"
            class="txtCenter"
            @current-change="handleCurrentChange"
            :current-page="pageRecord.currentPage"
            :page-sizes="[10, 20, 30]"
            :page-size="pageRecord.pageSize"
            layout=" sizes, prev, pager, next"
            :total="pageRecord.totalCount"
          ></el-pagination>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<script>
import { datasourceApi } from "@/api";
export default {
  name: "ds",
  components: {},
  data() {
    return {
      listLoading: false,
      isIndeterminate: true,
      checkAll: false,
      tableQueryForm: {
        dataSourceId: 0,
        dataTableName: "",
        currentPage: 1,
        pageSize: 10
      },
      form: {
        dbType: "mysql",
        driverName: "com.mysql.cj.jdbc.Driver",
        ipAndPort: "rm-uf649ipw0c2fdok97xo.mysql.rds.aliyuncs.com:3306",
        dbName: "zst",
        dbCode: "kg",
        dbUserName: "tan",
        dbPassWord: "Miracletan2021"
      },
      tableForm: {
        dataSourceId: 0,
        dataTables: []
      },
      addWStatus: false,
      selectTableStatus: false,
      checkAll: false,
      dataTypeList: [
        { type: "mysql", driver: "com.mysql.cj.jdbc.Driver" },
        { type: "postgresql", driver: "org.postgresql.Driver" },
        { type: "mariadb", driver: "org.mariadb.jdbc.Driver" },
        {
          type: "sqlserver",
          driver: "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        },
        { type: "oracle", driver: "oracle.jdbc.driver.OracleDriver" },
        { type: "hive", driver: "org.apache.hive.jdbc.HiveDriver" }
      ],
      dataSourceList: [],
      dataTableList: [],
      dataFieldList: [],
      pageRecord: {
        headerFields: [],
        beanList: [],
        currentPage: 1,
        pageSize: 10,
        totalCount: 0,
        totalPage: 0
      }
    };
  },
  created() {
    this.initDataSource();
  },
  methods: {
    handleCheckedChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.dataTableList.length;
      this.isIndeterminate =
        checkedCount > 0 && checkedCount < this.tableForm.dataTables.length;
    },
    handleCheckAllChange(val) {
      this.tableForm.dataTables = val ? this.dataTableList : [];
      this.isIndeterminate = false;
    },
    clickDataSource(item, index) {
      this.getTableList(item.datasourceId, index);
    },
    initDataSource() {
      let _this = this;
      datasourceApi.getDatasource().then(result => {
        if (result.code == 200) {
          let list = result.data;
          _this.dataSourceList = [];
          if (list) {
            for (let i = 0; i < list.length; i++) {
              let m = list[i];
              m.openState = 0;
              m.selected = 0;
              _this.dataSourceList.push(m);
            }
          }
        }
      });
    },
    testDataSource() {},
    saveDataTable() {
      let data = this.tableForm;
      let _this = this;
      datasourceApi.saveDataTable(JSON.stringify(data)).then(result => {
        if (result.code == 200) {
          _this.selectTableStatus = false;
          _this.initDataSource();
          _this.$message.success(result.msg);
        }
      });
    },
    getTableList(datasourceId, index) {
      let _this = this;
      _this.pageRecord.beanList = [];
      datasourceApi.getTableInfo(datasourceId).then(result => {
        if (result.code == 200) {
          let list = result.data;
          if (list) {
            for (let i = 0; i < _this.dataSourceList.length; i++) {
              if (i == index) {
                _this.dataSourceList[index].openState = !_this.dataSourceList[
                  index
                ].openState;
                _this.dataSourceList[index].selected = !_this.dataSourceList[
                  index
                ].selected;
                _this.dataSourceList[index].tableList = list;
              }
            }
          }
        }
      });
    },
    getFields(dataTableId) {
      let _this = this;
      _this.pageRecord.beanList = [];
      datasourceApi.getTableColumn(dataTableId).then(result => {
        if (result.code == 200) {
          _this.dataFieldList = result.data;
        }
      });
    },
    dbTypeChange(value) {
      this.form.driverName = value.driver;
      this.form.dbType = value.type;
    },
    addDataSource() {
      let data = this.form;
      let _this = this;
      datasourceApi.saveDatasource(data).then(result => {
        if (result.code == 200) {
          _this.addWStatus = false;
          _this.selectTableStatus = true;
          _this.dataTableList = result.data.tables;
          _this.tableForm.dataSourceId = result.data.sourceId;
          _this.initDataSource();
          _this.$message.success(result.msg);
        }
      });
    },
    deleteTable() {},
    getTableRecord() {
      let _this = this;
      let query = JSON.stringify(_this.tableQueryForm);
      _this.pageRecord.beanList = [];
      _this.pageRecord.headerFields = [];
      _this.listLoading = true;
      datasourceApi.getDataRecord(query).then(result => {
        if (result.code == 200) {
          _this.pageRecord.headerFields = result.data.heads;
          _this.pageRecord.beanList = result.data.data;
          _this.pageRecord.currentPage = result.data.pageIndex;
          _this.pageRecord.pageSize = result.data.pageSize;
          _this.pageRecord.totalCount = result.data.totalCount;
          _this.pageRecord.totalPage = result.data.totalPage;
          _this.listLoading = false;
        }
      });
    },
    getTableOperate(command) {
      var _this = this;
      _this.dataFieldList = [];
      if (command.action === "viewData") {
        _this.tableQueryForm.dataSourceId = command.db.datasourceId;
        _this.tableQueryForm.dataTableName = command.tb.dataTableName;
        _this.getTableRecord();
      }
      if (command.action === "rename") {
        _this.$message.error("暂不支持");
      }
      if (command.action === "delete") {
        _this.$message.error("暂不支持");
      }
    },
    handleCurrentChange(val) {
      this.pageRecord.currentPage = val;
      this.getTableRecord();
    },
    handleSizeChange(val) {
      this.pageRecord.currentPage = 1;
      this.pageRecord.pageSize = val;
      this.getTableRecord();
    }
  }
};
</script>
<style>
.ds-tree {
  border-bottom: solid 1px gray;
  padding: 10px;
}
.el-dropdown {
  float: right;
}
.el-pagination {
  text-align: center;
}
.el-table {
  padding: 35px;
}
.mind-box {
  height: calc(100vh - 85px);
  overflow: hidden;
}
.mind-l {
  width: 300px;
  float: left;
  background: #f7f9fc;
  height: 100%;
  border-right: 1px solid #d3e2ec;
  text-align: left;
}
.ml-ht {
  padding-top: 20px;
  line-height: 50px;
  font-size: 16px;
  font-weight: 400;
  text-align: center;
  color: #333;
  border-bottom: 1px solid #d3e2ec;
}
.ml-a-box {
  margin: 10px;
}
.ml-a {
  display: inline-block;
  min-width: 46px;
  line-height: 1;
  padding: 6px 8px 6px 8px;
  margin: 0 4px 5px 0;
  background: #fff;
  border: 1px solid #e3e3e3;
  box-sizing: border-box;
  transition: 0.3s;
}
.ml-a span {
  max-width: 190px;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: middle;
}
.ml-a-all {
  display: block;
  margin: 10px 10px 0;
  text-align: center;
}
.ml-a span:empty:before {
  content: "閺堫亜鎳￠崥锟�";
  color: #adadad;
}
.ml-a small {
  color: #999;
}
.ml-a:hover {
  background: #f4f4f4;
}
.ml-a.cur,
.ml-a.cur small {
  background: #156498;
  color: #fff;
}
.ml-btn-box {
  text-align: right;
  padding: 0 10px;
  margin-bottom: 20px;
}
.ml-btn {
  padding: 0 5px;
  color: #156498;
}
.mind-con {
  height: 100%;
  overflow: hidden;
  background: #fff;
  display: -webkit-flex;
  display: flex;
  flex-direction: column;
}
.mind-top {
  line-height: 70px;
  height: 70px;
  padding: 0 22px;
  border-bottom: 1px solid #ededed;
}
.mt-m {
  color: #666;
  margin-right: 30px;
}
.mt-m i {
  font-size: 18px;
  color: #333;
  font-weight: 700;
  font-style: normal;
}
.mb-con .search,
.mind-top .search {
  border: 1px solid #e2e2e2;
}
.svg-a-sm {
  font-size: 14px;
  color: #156498;
  margin-right: 30px;
}
.mind-cen {
  height: calc(100% - 70px);
}
.half-auto {
  height: 40%;
}
.mind-bottom {
  height: 490px;
  box-sizing: border-box;
  border-top: 1px solid #ededed;
}
.ss-d {
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
  border-radius: 50%;
  background: #dedede;
}
.sd {
  margin: 2px;
}
.sd-active {
  color: red !important;
}
.btn-line + .btn-line {
  margin-left: 10px;
}
.co {
  color: #ee8407 !important;
}
.a {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.fl {
  float: left;
}
.fr {
  float: right;
}
.tl {
  text-align: left;
}
</style>
