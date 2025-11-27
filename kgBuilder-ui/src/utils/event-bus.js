/**
 * 简易事件总线
 *
 * 提供订阅/发布/取消订阅/一次性订阅四种方法，供组件间通信使用。
 */
class EventPublic {
    constructor() {
      this.event = {}
    }
    /**
     * 订阅事件
     * @param {string} type 事件类型
     * @param {Function} callback 回调函数
     */
    $on(type, callback) {
      if (!this.event[type]) {
        this.event[type] = [callback]
      } else {
        this.event[type].push(callback)
      }
    }
    /**
     * 发布事件
     * @param {string} type 事件类型
     * @param {...any} args 参数列表
     */
    $emit(type, ...args) {
      if (!this.event[type]) {
        return
      }
      this.event[type].forEach(res => {
        res.apply(this, args)
      })
    }
    /**
     * 取消订阅
     * @param {string} type 事件类型
     * @param {Function} callback 订阅回调
     */
    $off(type, callback) {
      if (!this.event[type]) {
        return
      }
      this.event[type] = this.event[type].filter(res => {
        return res != callback
      })
    }
    /**
     * 执行一次订阅
     * @param {string} type 事件类型
     * @param {Function} callback 回调函数
     */
    $once(type, callback) {
      function f() {
        callback()
        this.$off(type, f)
      }
      this.$on(type, f)
    }
  }
  export const EventBus = new EventPublic()
  
