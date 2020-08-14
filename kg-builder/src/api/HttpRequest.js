/*
 * @Description: http请求文件，axios拦截器等
 * @Author: chenfengtao
 * @Date: 2019-11-13 16:52:10
 * @LastEditors: chenfengtao
 * @LastEditTime: 2020-07-09 09:06:34
 */

import axios from "axios";
import { setToken, getToken } from "./token";
import { Message } from "element-ui";
import router from "@/router/index";

export default class HttpRequest {
  constructor(baseUrl = "") {
    this.baseUrl = baseUrl;
    // 存储请求队列
    this.queue = {};
  }

  getCommonConfig() {
    const config = {
      baseURL: this.baseUrl,
      // 请求超时时间
      timeout: 10 * 60 * 1000,
      headers: {
        "Content-Type": "application/x-www-form-urlencoded; charset=utf-8",
        // 'X-URL-PATH': location.pathname
        "X-Requested-With": "XMLHttpRequest"
      }
    };
    return config;
  }

  // 销毁请求实例
  destroy(url) {
    delete this.queue[url];
    if (!Object.keys(this.queue).length) {
    }
  }

  // 请求拦截
  interceptors(instance, url) {
    // 添加请求拦截器
    instance.interceptors.request.use(
      config => {
        // 这里可以添加全局loading...
        if (!Object.keys(this.queue).length) {
          //
        }
        this.queue[url] = true;
        // 请求发送前处理. 添加token
        //

        return config;
      },
      error => {
        // 请求错误处理
        return Promise.reject(error);
      }
    );

    // 添加响应拦截器
    instance.interceptors.response.use(res => {
      // 请求响应后销毁请求实例
      console.log(res);
    });
  }

  request(options) {
    const instance = axios.create();
    // 合并options
    let headers = Object.assign(
      this.getCommonConfig().headers,
      options.headers
    );
    options = Object.assign(this.getCommonConfig(), options);
    options.headers = headers;
    // 注册拦截器
    this.interceptors(instance, options.url);
    // 返回实例
    return instance(options);
  }
}
