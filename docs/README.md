# 文档总览

本项目文档集中在 `docs/` 目录下，用于指导开发、部署、以及代码规范与注释风格。

## 目录结构

- `images/` 项目截图与示例图片
- `README.md` 文档索引与规范说明（当前文件）

## 快速导航

- 项目介绍与整体结构：参见根目录 `README.md`
- 前端开发指南：`kgBuilder-ui/README.md`
- 运行与调试：根目录与前端 README 的“快速开始”章节

## 注释与文档规范

- JavaScript/Vue 采用 JSDoc 风格，例如：

```js
/**
 * 获取领域节点
 * @param {string|number} domainId 领域 ID
 * @returns {Promise<any>} 接口返回 Promise
 */
function getDomainNode(domainId) {}
```

- Java 采用 Javadoc 风格，例如：

```java
/**
 * 保存或更新节点正文内容
 * @param params 包含 domainId、nodeId、content 的参数
 * @return 操作结果
 */
public R<String> saveNodeContent(Map<String, Object> params) {}
```

## Markdown 书写规范

- 所有代码块需标注语言（避免 MD040）。
- 列表前后保留空行，列表项使用统一标记（避免 MD032）。
- 避免出现连续的空行（避免 MD012）。

## 更新策略

- 文档与注释应随代码更新同步维护。
- 重要模块的公共方法需提供清晰的参数、返回值说明。

