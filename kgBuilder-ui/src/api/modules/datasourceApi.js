import request from "@/utils/request";
import BaseAPI from '@/utils/BaseAPI'

/**
 * 数据源 API 封装
 *
 * 提供获取数据源、数据表/列、预览数据以及保存相关配置的接口方法。
 */
class datasourceApi extends BaseAPI{
  /**
   * 获取数据源列表
   * @returns {Promise<any>}
   */
  getDatasource() {
    return request({
      url: "/datasource/getDataSource",
      method: "get"
    });
  }
  /**
   * 获取数据表列表
   * @param {string|number} datasourceId 数据源ID
   * @returns {Promise<any>}
   */
  getTableInfo(datasourceId) {
    return request({
      url: "/datasource/getDataTable?datasourceId=" + datasourceId,
      method: "get"
    });
  }
  /**
   * 获取数据列列表
   * @param {string|number} tableId 数据表ID
   * @returns {Promise<any>}
   */
  getTableColumn(tableId) {
    return request({
      url: "/datasource/getDataColumn?dataTableId=" + tableId,
      method: "get"
    });
  }
  /**
   * 获取数据表及其列信息
   * @param {string|number} tableId 数据表ID
   * @returns {Promise<any>}
   */
  getDataTableInfo(tableId) {
    return request({
      url: "/datasource/getDataTableInfo?dataTableId=" + tableId,
      method: "get"
    });
  }
  /**
   * 获取预览数据
   * @param {Object} data 查询条件
   * @returns {Promise<any>}
   */
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
  /**
   * 保存数据源配置
   * @param {Object} data 数据源配置
   * @returns {Promise<any>}
   */
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
   /**
    * 保存数据表配置
    * @param {Object} data 数据表配置
    * @returns {Promise<any>}
    */
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
    /**
     * 获取数据表记录
     * @param {Object} data 查询条件
     * @returns {Promise<any>}
     */
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
