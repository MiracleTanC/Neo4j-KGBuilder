import BaseApi from "../BaseApi";

class KgApi extends BaseApi {
  // 获取二维码
  getKgData() {
    return this.get("/static/kgData.json");
  }
}
export default new KgApi();
