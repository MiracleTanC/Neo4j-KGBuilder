import BaseAPI from '@/utils/BaseAPI'

class kgBuilderApi extends BaseAPI{
  /**
   * 获取静态图谱数据
   * @returns {Promise}
   */
  getKgData() {
    return this.get("/static/kgData.json");
  }

  /**
   * 提交反馈
   * @param {Object} data - 反馈数据
   * @returns {Promise}
   */
  feedBack(data) {
    return this.post("/feedBack",data);
  }

  /**
   * 保存ER数据
   * @param {Object} data - ER数据
   * @returns {Promise}
   */
  saveData(data) {
    return this.post("/er/saveData",data,{
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
  }

  /**
   * 获取领域节点
   * @param {string|number} domainId - 领域ID
   * @returns {Promise}
   */
  getDomainNode(domainId) {
    return this.get('/er/getDomainNode', {
      domainId
    })

  }

  /**
   * 执行操作
   * @param {string|number} domainId - 领域ID
   * @returns {Promise}
   */
  execute(domainId) {
    return this.get('/er/execute', {
      domainId
    })

  }

  /**
   * 获取领域列表
   * @param {Object} data - 查询条件
   * @returns {Promise}
   */
  getDomains(data) {
    return this.post("/getGraph",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    }
  );
  }

  /**
   * 创建领域
   * @param {Object} data - 领域数据
   * @returns {Promise}
   */
  createDomain(data) {
    return this.get("/createDomain",data);
    // return request({
    //   url: "/createDomain?domain=" + data.domain + "&type=" + data.type,
    //   method: "get"
    // });
  }

  /**
   * 获取Cypher查询结果
   * @param {Object} data - 查询参数
   * @returns {Promise}
   */
  getCypherResult(data) {
    return this.get("/getCypherResult",data);
  }

  /**
   * 获取节点内容
   * @param {Object} data - 查询参数
   * @returns {Promise}
   */
  getNodeContent(data) {
    return this.post("/getNodeContent",data);
  }

  /**
   * 获取节点图片
   * @param {Object} data - 查询参数
   * @returns {Promise}
   */
  getNodeImage(data) {
    return this.post("/getNodeImage",data);
  }

  /**
   * 获取节点详情
   * @param {Object} data - 查询参数
   * @returns {Promise}
   */
  getNodeDetail(data) {
    return this.post("/getNodeDetail",data);
  }

  /**
   * 保存节点图片
   * @param {Object} data - 图片数据
   * @returns {Promise}
   */
  saveNodeImage(data) {
    return this.post("/saveNodeImage",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 保存节点内容
   * @param {Object} data - 内容数据
   * @returns {Promise}
   */
  saveNodeContent(data) {
    return this.post("/saveNodeContent",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 查询图谱结果
   * @param {Object} data - 查询条件
   * @returns {Promise}
   */
  getDomainGraph(data) {
    return this.post("/queryGraphResult",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  /**
   * 获取关联节点数量
   * @param {Object} data - 查询参数
   * @returns {Promise}
   */
  getRelationNodeCount(data) {
    return this.post("/getRelationNodeCount",data);
  }

  /**
   * 获取更多关联节点
   * @param {Object} data - 查询参数
   * @returns {Promise}
   */
  getMoreRelationNode(data) {
    return this.get("/getMoreRelationNode",data);
  }

  /**
   * 删除领域
   * @param {Object} data - 领域信息
   * @returns {Promise}
   */
  deleteDomain(data) {
    return this.post("/deleteDomain",data);
  }

  /**
   * 获取推荐图谱
   * @param {Object} data - 查询参数
   * @returns {Promise}
   */
  getRecommendGraph(data) {
    return this.post("/getRecommendGraph",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 创建节点
   * @param {Object} data - 节点数据
   * @returns {Promise}
   */
  createNode(data) {
    return this.post("/createNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 删除节点
   * @param {Object} data - 节点信息
   * @returns {Promise}
   */
  deleteNode(data) {
    return this.post("/deleteNode",data);
  }

  /**
   * 删除关系
   * @param {Object} data - 关系信息
   * @returns {Promise}
   */
  deleteLink(data) {
    return this.post("/deleteLink",data);
  }

  /**
   * 创建关系
   * @param {Object} data - 关系数据
   * @returns {Promise}
   */
  createLink(data) {
    return this.post("/createLink",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 更新关系
   * @param {Object} data - 关系数据
   * @returns {Promise}
   */
  updateLink(data) {
    return this.post("/updateLink",data);
  }

  /**
   * 更新节点名称
   * @param {Object} data - 节点数据
   * @returns {Promise}
   */
  updateNodeName(data) {
    return this.post("/updateNodeName",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 批量创建节点
   * @param {Object} data - 批量数据
   * @returns {Promise}
   */
  batchCreateNode(data) {
    return this.post("/batchCreateNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 批量创建子节点
   * @param {Object} data - 批量数据
   * @returns {Promise}
   */
  batchCreateChildNode(data) {
    return this.post("/batchCreateChildNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 批量创建同级节点
   * @param {Object} data - 批量数据
   * @returns {Promise}
   */
  batchCreateSameNode(data) {
    return this.post("/batchCreateSameNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * 导出图谱
   * @param {Object} data - 导出参数
   * @returns {Promise}
   */
  exportGraph(data) {
    return this.post("/exportGraph",data);
  }

  /**
   * 下载文件
   * @param {string} data - 文件标识
   * @returns {Promise}
   */
  download(data) {
    return this.get("/download/"+data,);
  }

  /**
   * 更新节点坐标
   * @param {Object} data - 坐标数据
   * @returns {Promise}
   */
  updateCoordinateOfNode(data) {
    return this.post("/updateCoordinateOfNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

}
export default new kgBuilderApi();
