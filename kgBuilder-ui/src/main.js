import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import axios from "axios";
import components from './components/index'

Vue.prototype.$http = axios; //正确的使用
Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.use(components)
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
