import request from "@/utils/request";
import BaseAPI from '@/utils/BaseAPI'

class datasourceApi extends BaseAPI{
  // 获取数据源
  getDatasource() {
    return request({
      url: "/datasource/getDataSource",
      method: "get"
    });
  }
  // 获取数据表
  getTableInfo(datasourceId) {
    return request({
      url: "/datasource/getDataTable?datasourceId=" + datasourceId,
      method: "get"
    });
  }
  // 获取数据列
  getTableColumn(tableId) {
    return request({
      url: "/datasource/getDataColumn?dataTableId=" + tableId,
      method: "get"
    });
  }
  //获取表及列
  getDataTableInfo(tableId) {
    return request({
      url: "/datasource/getDataTableInfo?dataTableId=" + tableId,
      method: "get"
    });
  }
  //获取预览数据
  getPreviewData(data) {
    return this.post("/datasource/getTableRecords",data,{
      headers: {
        'Content-Type': 'application/json'
      }});
    // return request({
    //   url: "/datasource/getTableRecords",
    //   method: "post",
    //   data: data
    // });
  }
  //保存数据源
  saveDatasource(data) {
    return this.post("/datasource/saveDataSource",data,{
      headers: {
        'Content-Type': 'application/json'
      }});
    // return request({
    //   url: "/datasource/saveDataSource",
    //   method: "post",
    //   data: data
    // });
  }
   //保存数据表
   saveDataTable(data) {
     return this.post("/datasource/saveDataTable",data,{
      headers: {
        'Content-Type': 'application/json'
      }});
    // return request({
    //   url: "/datasource/saveDataTable",
    //   method: "post",
    //   data: data
    // });
  }
    //获取数据表记录
    getDataRecord(data) {
      return this.post("/datasource/getTableRecords",data,{
        headers: {
          'Content-Type': 'application/json'
        }});
      // return request({
      //   url: "/datasource/getTableRecords",
      //   method: "post",
      //   data: data
      // });
    }
}
export default new datasourceApi();
