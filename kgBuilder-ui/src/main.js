import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import axios from "axios";
import components from './components/index'

/**
 * 应用入口
 *
 * - 注册 ElementUI 与全局组件
 * - 注入 axios 到 Vue 原型
 * - 创建根实例并挂载到 `#app`
 */
Vue.prototype.$http = axios; //正确的使用
Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.use(components)
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
