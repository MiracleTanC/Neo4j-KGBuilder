/*
 * 动态注册全局组件
 */
const allComponents = require.context('./', true, /\.vue$/)

export default Vue => {
  allComponents.keys().forEach(item => {
    Vue.component(item.replace(/\.\//, '').replace(/\.vue$/, ''), allComponents(item).default)
  })
}
