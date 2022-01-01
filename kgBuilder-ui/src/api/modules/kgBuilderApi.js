import BaseAPI from '@/utils/BaseAPI'

class kgBuilderApi extends BaseAPI{
  // 获取图谱数据
  getKgData() {
    return this.get("/static/kgData.json");
  }
  saveData(data) {
    return this.post("/er/saveData",data,{
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
  }
  getDomainNode(domainId) {
    return this.get('/er/getDomainNode', {
      domainId
    })

  }
  getDomains(data) {
    return this.post("/getGraph",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    }
  );
  }
  createDomain(data) {
    return request({
      url: "/createDomain?domain=" + data.name + "&type=" + data.type,
      method: "get"
    });
  }
  getCypherResult(data) {
    return this.get("/getCypherResult",data);
  }
  getNodeContent(data) {
    return this.post("/getNodeContent",data);
  }
  getNodeImage(data) {
    return this.post("/getNodeImage",data);
  }
  getNodeDetail(data) {
    return this.post("/getNodeDetail",data);
  }
  saveNodeImage(data) {
    return this.post("/saveNodeImage",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  saveNodeContent(data) {
    return this.post("/saveNodeContent",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  getDomainGraph(data) {
    return this.post("/getDomainGraph",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  getRelationNodeCount(data) {
    return this.post("/getRelationNodeCount",data);
  }
  getMoreRelationNode(data) {
    return this.post("/getMoreRelationNode",data);
  }
  deleteDomain(data) {
    return this.post("/deleteDomain",data);
  }
  getRecommendGraph(data) {
    return this.post("/getRecommendGraph",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  createNode(data) {
    return this.post("/createNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  deleteNode(data) {
    return this.post("/deleteNode",data);
  }
  deleteLink(data) {
    return this.post("/deleteLink",data);
  }
  createLink(data) {
    return this.post("/createLink",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  updateLink(data) {
    return this.post("/updateLink",data);
  }
  updateNodeName(data) {
    return this.post("/updateNodeName",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  batchCreateNode(data) {
    return this.post("/batchCreateNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  batchCreateChildNode(data) {
    return this.post("/batchCreateChildNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  batchCreateSameNode(data) {
    return this.post("/batchCreateSameNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  exportGraph(data) {
    return this.post("/exportGraph",data);
  }
  updateCoordinateOfNode(data) {
    return this.post("/updateCoordinateOfNode",data,{
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

}
export default new kgBuilderApi();
