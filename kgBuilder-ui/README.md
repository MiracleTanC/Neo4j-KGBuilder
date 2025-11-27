# kg-builder

## Project setup
```bash
npm install
```

### Compiles and hot-reloads for development
```bash
npm run serve
```

### Compiles and minifies for production
```bash
npm run build
```

### Lints and fixes files
```bash
npm run lint
```

### Project structure

```
kgBuilder-ui/
├── public/            # 静态资源与示例数据
├── src/
│   ├── api/           # 接口模块 (BaseAPI, kgBuilderApi 等)
│   ├── components/    # 复用组件 (表格、图谱组件等)
│   ├── router/        # 路由配置
│   ├── store/         # Vuex 状态管理
│   ├── utils/         # 工具方法 (请求封装、错误码等)
│   └── views/         # 页面视图 (知识图谱、ER 构建等)
└── package.json
```

### 注释规范

- 使用 JSDoc 风格为组件与公共方法添加注释：

```js
/**
 * 获取推荐图谱
 * @param {Object} data 查询参数
 * @returns {Promise<any>} 接口返回 Promise
 */
kgBuilderApi.getRecommendGraph(data)
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
