import request from '@/utils/request'
import qs from 'qs'

/**
 * BaseAPI
 *
 * 提供统一的 `get` 与 `post` 方法封装，默认以 `application/x-www-form-urlencoded`
 * 发送表单数据；当 `headers.Content-Type` 包含 `application/json` 或
 * `multipart/form-data` 时，直接透传原始数据。
 */
export default class BaseAPI {
  /**
   * 发送 GET 请求
   * @param {string} url 请求地址
   * @param {Object} [params] 查询参数
   * @returns {Promise<any>} 请求 Promise
   */
  get (url, params) {
    return request({ url, method: 'GET', params })
  }

  /**
   * 发送 POST 请求
   * @param {string} url 请求地址
   * @param {Object|FormData} data 请求体
   * @param {Object} [config] 额外配置（含 headers）
   * @returns {Promise<any>} 请求 Promise
   */
  post (url, data, config) {
    let temp

    if (
      config &&
      config?.headers &&
      (config?.headers['Content-Type'].indexOf('application/json') !== -1 ||
        config?.headers['Content-Type'].indexOf('multipart/form-data') !== -1)
    ) {
      temp = data
    } else {
      temp = qs.stringify(data)
    }
    return request(Object.assign({ url, method: 'POST', data: temp }, config))
  }
}
